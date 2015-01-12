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

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.collect.Iterables.transform;
import static java.io.File.separator;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_HOME;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_INIT_SCRIPT;
import static org.grycap.gpf4med.cloud.Constants.GPF4MED_PORT;
import static org.grycap.gpf4med.cloud.util.CloudUtils.firstPublicAddressToHostAndPort;
import static org.jclouds.util.Maps2.transformKeys;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.io.FilenameUtils;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitScript;

import com.google.common.io.Closeables;
import com.google.common.net.HostAndPort;

/**
 * Handles the Gpf4Med study service.
 * @author Erik Torres <ertorser@upv.es>
 */
@Singleton
public class Gpf4MedServiceController implements Closeable {

	@Resource
	protected Logger logger = Logger.NULL;

	private final Closer closer;
	private final NodeManager nodeManager;
	private final Provider<InitScript> daemonFactory;
	private final String home;
	private final int port;

	@Inject
	public Gpf4MedServiceController(final Closer closer, final NodeManager nodeManager, 
			final Provider<InitScript> daemonFactory, final @Named(GPF4MED_HOME) String home, 
			final @Named(GPF4MED_PORT) int port) {
		this.closer = closer;
		this.nodeManager = nodeManager;
		this.daemonFactory = daemonFactory;
		this.home = home;
		this.port = port;
	}

	public HostAndPort add(final String group, final NodeConfiguration config) {
		return firstPublicAddressToHostAndPort(port).apply(createNodeWithGpf4Med(group, config));
	}

	public Iterable<HostAndPort> list(final String group) {
		return transformToHostAndPort(nodeManager.listRunningNodesInGroup(group));
	}

	public Map<HostAndPort, String> status(final String group) {
		return mapHostAndPortToStdoutForCommand(pathToInitScript(home) + " status", group);
	}

	public Map<HostAndPort, String> tail(final String group) {
		return mapHostAndPortToStdoutForCommand(pathToInitScript(home) + " tail", group);
	}

	public Iterable<HostAndPort> destroy(final String group) {
		return transformToHostAndPort(nodeManager.destroyNodesInGroup(group));
	}

	public Iterable<HostAndPort> transformToHostAndPort(final Set<? extends NodeMetadata> nodes) {
		return transform(nodes, firstPublicAddressToHostAndPort(port));
	}
	
	public boolean nodesCanBeAcquired() {
		return nodeManager.nodesCanBeAcquired();
	}

	private Map<HostAndPort, String> mapHostAndPortToStdoutForCommand(final String cmd, final String group) {
		return transformKeys(nodeManager.stdoutFromCommandOnGroup(cmd, group), firstPublicAddressToHostAndPort(port));
	}

	private NodeMetadata createNodeWithGpf4Med(final String group, final NodeConfiguration config) {
		// set the minimum memory to accommodate: Java maximum heap + overhead
		final NodeMetadata node = nodeManager.createNode(group, port, config.ramMb(config.ramMb() + 256));
		nodeManager.startDaemonOnNode(daemonFactory.get(), node.getId());
		return node;
	}

	private static String pathToInitScript(final String home) {
		return FilenameUtils.concat(home, "bin" + separator + GPF4MED_INIT_SCRIPT);
	}

	@Override
	public void close() throws IOException {
		Closeables.close(closer, true);
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("nodeManager", nodeManager)
				.add("port", port)
				.toString();
	}

}