/*
 * Copyright 2013 Institute for Molecular Imaging Instrumentation (I3M)
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by 
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 *   http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 * 
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 */

package org.grycap.gpf4med.cloud;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ContiguousSet.create;
import static com.google.common.collect.DiscreteDomain.integers;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Range.closed;
import static com.google.common.util.concurrent.Futures.successfulAsList;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.Collections.singleton;
import static java.util.Collections.synchronizedList;
import static java.util.Collections.synchronizedSet;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_CONNECTORS_URL;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_HOME;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_INSTALLER_URL;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_PORT;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_TEMPLATES_URL;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_VERSION;
import static org.grycap.gpf4med.cloud.DefaultNodeConfiguration.fromDefaults;
import static org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;
import static org.jclouds.openstack.keystone.v2_0.config.CredentialTypes.PASSWORD_CREDENTIALS;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.Closeable2;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.events.StatementOnNodeCompletion;
import org.jclouds.compute.events.StatementOnNodeFailure;
import org.jclouds.compute.events.StatementOnNodeSubmission;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Module;

/**
 * Cloud service that creates nodes in a Cloud provider and starts a Gpf4Med study
 * service on them. There are other actions that can be executed in the nodes, including 
 * listing the available services, tailing the log files and destroying a group of nodes.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum CloudService implements Closeable2 {

	INSTANCE;

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudService.class);	

	private static final long ADD_SERVERS_TIMEOUT_MINUTES = 15;

	public static final Map<String, ApiMetadata> ALL_APIS = uniqueIndex(Apis.viewableAs(ComputeServiceContext.class),
			Apis.idFunction());

	public static final Map<String, ProviderMetadata> ALL_PROVIDERS = uniqueIndex(Providers.viewableAs(ComputeServiceContext.class),
			Providers.idFunction());

	public static final Set<String> ALL_KEYS = ImmutableSet.copyOf(concat(ALL_PROVIDERS.keySet(), ALL_APIS.keySet()));

	/**
	 * Script execution timeout in minutes.
	 */
	public static final int SCRIPT_TIMEOUT_MINUTES = 20;

	private Set<String> groups = synchronizedSet(new HashSet<String>());
	private Gpf4MedServiceController dont_use = null;

	@Override
	public void setup(final @Nullable Collection<URL> urls) {
		this.dont_use = null;
	}

	@Override
	public void preload() {
		// lazy load
		controller();
	}

	@Override
	public void close() throws IOException {
		final Gpf4MedServiceController controller = controllerOrNull();
		if (controller != null) {
			for (final String group : groups) {
				releaseGroup(group);
			}
			controller.close();
		}
	}

	/**
	 * Starts new Gpf4Med study servers in the specified group, creating the group if necessary. The servers 
	 * will be created with the minimum requirements for cores, RAM and disk storage space as defined in: 
	 * {@link DefaultNodeConfiguration}.
	 * @param group the group to which the servers will be created.
	 * @param count number of servers to be created in the group.
	 */
	public void addServers(final String group, final int count) {
		addServers(group, count, null);
	}

	/**
	 * Starts new Gpf4Med study servers in the specified group, creating the group if necessary. The servers 
	 * will be created with the specified configuration.
	 * @param group the group to which the servers will be created.
	 * @param count number of servers to be created in the group.
	 * @param config optional configuration. If undefined, default configuration will be applied as defined 
	 *        in: {@link DefaultNodeConfiguration}.
	 */
	public void addServers(final String group, final int count, final @Nullable NodeConfiguration config) {
		checkArgument(StringUtils.isNotBlank(group), "Uninitialized or invalid group");
		checkArgument(count > 0, "Invalid number of servers");
		final NodeConfiguration config2 = (config != null ? config : fromDefaults());
		final Gpf4MedServiceController controller = controller();
		// quietly bypass node creation on providers that don't support it
		if (!controller.nodesCanBeAcquired()) {
			LOGGER.info("Bypassing unsupported node creation");
			return;
		}
		// create nodes asynchronously
		final ListeningExecutorService executorService = listeningDecorator(newCachedThreadPool());
		final List<ListenableFuture<HostAndPort>> futures = synchronizedList(new ArrayList<ListenableFuture<HostAndPort>>());		
		for (final UnmodifiableIterator<Integer> it = create(closed(1, count), integers()).iterator(); it.hasNext(); it.next()) {			
			final ListenableFuture<HostAndPort> future = executorService.submit(new Callable<HostAndPort>() {
				@Override
				public HostAndPort call() throws Exception {
					return controller.add(group, config2);
				}
			});
			futures.add(future);
		}
		final ListenableFuture<List<HostAndPort>> nodesFuture = successfulAsList(futures);
		List<HostAndPort> nodes = null;
		try {
			nodes = nodesFuture.get(MILLISECONDS.convert(ADD_SERVERS_TIMEOUT_MINUTES, MINUTES), MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			LOGGER.warn("Failed to acquire servers in group: " + group, e);
		}
		// create a mutable copy to remove null entries (failed nodes)
		if (nodes != null && !nodes.isEmpty()) {			
			nodes = new ArrayList<HostAndPort>(nodes);
			nodes.removeAll(singleton(null));
		}
		// register group
		if (nodes != null && !nodes.isEmpty()) {			
			groups.add(group);			
		}
		LOGGER.info(String.format("Server(s) acquired in the group %s (%s)", group, nodes));
	}

	/**
	 * Releases a group, destroying all nodes in the group.
	 * @param group the name of the group that will be released.
	 */
	public void releaseGroup(final String group) {
		checkArgument(StringUtils.isNotBlank(group), "Uninitialized or invalid group");
		final Gpf4MedServiceController controller = controller();
		// quietly bypass node destruction on providers that don't support it
		if (!controller.nodesCanBeAcquired()) {
			LOGGER.info("Bypassing unsupported node destruction");
			return;
		}
		// destroy nodes
		final Iterable<HostAndPort> destroyed = controller.destroy(group);
		groups.remove(group);
		LOGGER.info(String.format("Released group %s, destroyed servers %s", group, destroyed));
	}

	/**
	 * Lists the servers available to a group.
	 * @param group the name of the group.
	 * @return the servers available to a group.
	 */
	public @Nullable Iterable<HostAndPort> list(final String group) {
		checkArgument(StringUtils.isNotBlank(group), "Uninitialized or invalid group");
		return controller().list(group);
	}

	/**
	 * Lists the status of the servers available to a group.
	 * @param group the name of the group.
	 * @return
	 */
	public Map<HostAndPort, String> status(final String group) {
		checkArgument(StringUtils.isNotBlank(group), "Uninitialized or invalid group");
		return controller().status(group);
	}

	/**
	 * Lists the latest messages logged from the servers available to a group.
	 * @param group the name of the group.
	 * @return
	 */
	public Map<HostAndPort, String> tail(final String group) {
		checkArgument(StringUtils.isNotBlank(group), "Uninitialized or invalid group");
		return controller().tail(group);
	}

	private @Nullable Gpf4MedServiceController controllerOrNull() {
		return dont_use;
	}

	/**
	 * Lazy load compute service.
	 * @return specialized compute service.
	 */
	private Gpf4MedServiceController controller() {
		if (dont_use == null) {
			synchronized (CloudService.class) {
				if (dont_use == null) {
					dont_use = initComputeService();
				}
			}
		}
		return dont_use;
	}

	private static Gpf4MedServiceController initComputeService() {
		// get compute service configuration
		final String provider = StringUtils.trimToNull(ConfigurationManager.INSTANCE.getEnactorProvider());
		final String identity = ConfigurationManager.INSTANCE.getEnactorIdentity();
		final String credential = ConfigurationManager.INSTANCE.getEnactorCredential();
		final String version = ConfigurationManager.INSTANCE.getServerVersion();
		final URL installerUrl = ConfigurationManager.INSTANCE.getServerInstallerUrl();
		final File homeDir = ConfigurationManager.INSTANCE.getServerHome();		
		final URL templatesUrl = ConfigurationManager.INSTANCE.getTemplatesUrl();
		final URL connectorsUrl = ConfigurationManager.INSTANCE.getConnectorsUrl();
		String endpoint = null;
		checkState(StringUtils.isNotBlank(provider), "Uninitialized or invalid compute service cloud provider");
		checkState(StringUtils.isNotEmpty(identity), "Uninitialized or invalid compute service identity");
		checkState(StringUtils.isNotEmpty(credential), "Uninitialized or invalid compute service credential");		
		checkState(StringUtils.isNotEmpty(version), "Uninitialized or invalid container version");
		checkState(installerUrl != null, "Uninitialized container installer URL");
		checkState(homeDir != null, "Uninitialized container home directory");		
		checkState(templatesUrl != null, "Uninitialized DICOM-SR templates URL");
		checkState(connectorsUrl != null, "Uninitialized graph connectors URL");
		// check if the provider or API is present
		LOGGER.trace(String.format("Supported provider/API(s): %s", ALL_KEYS));
		checkState(contains(ALL_KEYS, provider), "Compute service provider/API %s not in supported list: %s", provider, ALL_KEYS);		
		// set properties
		final Properties properties = new Properties();
		properties.setProperty(GPF4MED_PORT, Integer.toString(ConfigurationManager.INSTANCE.getContainerPort()));		
		properties.setProperty(GPF4MED_INSTALLER_URL, installerUrl.toString());
		properties.setProperty(GPF4MED_VERSION, version);
		properties.setProperty(GPF4MED_HOME, homeDir.toString());
		properties.setProperty(GPF4MED_TEMPLATES_URL, templatesUrl.toString());
		properties.setProperty(GPF4MED_CONNECTORS_URL, connectorsUrl.toString());
		final long scriptTimeout = MILLISECONDS.convert(SCRIPT_TIMEOUT_MINUTES, MINUTES);
		properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
		// add provider/API's specific properties
		if ("aws-ec2".equals(provider)) {
			// only use images supplied by Amazon
			properties.setProperty(PROPERTY_EC2_AMI_QUERY, "owner-id=137112412989;state=available;image-type=machine");
			properties.setProperty(PROPERTY_EC2_CC_AMI_QUERY, "");
		} else if ("openstack-nova".equals(provider)) {
			// endpoint
			endpoint = ConfigurationManager.INSTANCE.getProperty("enactor.openstack-nova.endpoint", null);
			checkState(StringUtils.isNotBlank(endpoint), "Uninitialized or invalid OpenStack endpoint");
			// image Id
			String property = ConfigurationManager.INSTANCE.getProperty("enactor.openstack-nova.image-id", null);
			checkState(StringUtils.isNotBlank(property), "Uninitialized or invalid OpenStack image id");
			properties.setProperty("openstack-nova.image-id", property);
			// login user
			property  = ConfigurationManager.INSTANCE.getProperty("enactor.openstack-nova.login-user", null);
			checkState(StringUtils.isNotBlank(property), "Uninitialized or invalid OpenStack login user");
			properties.setProperty("openstack-nova.login-user", property);
			property  = ConfigurationManager.INSTANCE.getProperty("enactor.openstack-nova.endpoint", null);
			checkState(StringUtils.isNotBlank(property), "Uninitialized or invalid OpenStack endpoint");			
			properties.setProperty("openstack-nova.endpoint", property);
			// setup secure access
			properties.setProperty(PROPERTY_TRUST_ALL_CERTS, "true");
			properties.setProperty(CREDENTIAL_TYPE, PASSWORD_CREDENTIALS);
		} else if ("byon".equals(provider)) {
			// endpoint
			endpoint = StringUtils.trimToNull(ConfigurationManager.INSTANCE.getProperty("enactor.byon.endpoint", null));
			checkState(StringUtils.isNotBlank(endpoint), "Uninitialized or invalid bring your own node (byon) endpoint");
			properties.setProperty("byon.endpoint", (endpoint.startsWith("file:") ? endpoint : "file://" + endpoint));
		} else if ("stub".equals(provider)) {
			throw new UnsupportedOperationException("Stub provider is not supported");
		}
		// inject a SSH implementation
		final Iterable<Module> modules = ImmutableSet.<Module> of(
				new SshjSshClientModule(),
				new SLF4JLoggingModule(),
				new EnterpriseConfigurationModule(),
				new ConfigureGpf4MedDaemon());
		// create compute service
		ContextBuilder builder;
		if (StringUtils.isNotBlank(endpoint)) {
			builder = ContextBuilder.newBuilder(provider)
					.endpoint(endpoint.toString())
					.credentials(identity, credential)
					.modules(modules)
					.overrides(properties);
		} else {
			builder = ContextBuilder.newBuilder(provider)
					.credentials(identity, credential)
					.modules(modules)
					.overrides(properties);
		}
		LOGGER.info(String.format("Initializing compute service %s", builder.getApiMetadata()));			
		final ComputeServiceContext context = builder.buildView(ComputeServiceContext.class);
		context.utils().eventBus().register(ScriptLogger.INSTANCE);
		return context.utils().injector().getInstance(Gpf4MedServiceController.class);
	}

	/* Inner classes */

	public static enum ScriptLogger {

		INSTANCE;

		@Subscribe
		@AllowConcurrentEvents
		public void onStart(final StatementOnNodeSubmission event) {
			LOGGER.info("Running {} on node({})", event.getStatement(), event.getNode().getId());
			LOGGER.debug("Script for {} on node({})\n{}", new Object[] { 
					event.getStatement(), event.getNode().getId(), event.getStatement().render(OsFamily.UNIX) 
			});
		}

		@Subscribe
		@AllowConcurrentEvents
		public void onFailure(final StatementOnNodeFailure event) {
			LOGGER.error("Error running {} on node({}): {}", new Object[] { 
					event.getStatement(), event.getNode().getId(), event.getCause().getMessage() 
			}, event.getCause());
		}

		@Subscribe
		@AllowConcurrentEvents
		public void onSuccess(final StatementOnNodeCompletion event) {
			final ExecResponse response = event.getResponse();
			if (response.getExitStatus() != 0) {
				LOGGER.error("Error running {} on node({}): {}", new Object[] { 
						event.getStatement(), event.getNode().getId(), response 
				});
			} else {
				LOGGER.info("Success executing {} on node({}): {}", new Object[] { 
						event.getStatement(), event.getNode().getId(), response 
				});
			}
		}
	}

}