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
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Sets.filter;
import static org.grycap.gpf4med.cloud.util.CloudUtils.addLoginForCommandExecution;
import static org.grycap.gpf4med.cloud.util.CloudUtils.asCurrentUser;
import static org.grycap.gpf4med.cloud.util.CloudUtils.getStdout;
import static org.grycap.gpf4med.cloud.util.NodePredicate2.matchId;
import static org.jclouds.compute.options.TemplateOptions.Builder.inboundPorts;
import static org.jclouds.compute.options.TemplateOptions.Builder.userMetadata;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.byon.BYONApiMetadata;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;

/**
 * Handles the servers where the Gpf4Med study service is running. Note that the 
 * byon provider only supports the following functions of ComputeService:
 * <ul>
 * <li>listNodes</li>
 * <li>listNodesDetailsMatching</li>
 * <li>getNodeMetadata</li>
 * <li>runScriptOnNodesMatching</li>
 * </ul>
 * @see <a href="https://github.com/jclouds/jclouds/tree/master/apis/byon">Bring Your Own Nodes to the jclouds ComputeService</a>
 * @author Erik Torres <ertorser@upv.es>
 */
@Singleton
public class NodeManager {

	@Resource
	private Logger logger = Logger.NULL;

	private final ComputeService compute;

	@Inject
	public NodeManager(final ComputeService compute) {
		this.compute = compute;
	}

	public NodeMetadata createNode(final String group, final int port, final NodeConfiguration config) {
		checkArgument(StringUtils.isNotBlank(group), "Uninitialized or invalid group");
		checkArgument(port > 0 && port <= 65535, "Invalid port");
		checkArgument(config != null, "Uninitialized node configuration");
		checkState(nodesCanBeAcquired(), "Node acquisition is not supported by your compute service");

		// use default as base for template
		final Template defaultTemplate = compute.templateBuilder().build();
		final Template gpf4medTemplate = compute.templateBuilder().fromTemplate(defaultTemplate)
				.os64Bit(true) // needed by Neo4j
				.minCores(config.cores())
				.minRam(config.ramMb())
				.minDisk(config.diskSizeGb())
				.options(inboundPorts(22, port)) // open ports
				.options(userMetadata(ImmutableMap.<String, String> of("Name", group))) // additional tags
				.build();

		// customize the node
		final Statement bootstrap = newStatementList(AdminAccess.standard(), 
				InstallJDK.fromOpenJDK(), // install JDK
				addLoginForCommandExecution()); // create unprivileged user
		gpf4medTemplate.getOptions().runScript(bootstrap);

		// include any desired cloud-specific hook here
		if (gpf4medTemplate.getOptions() instanceof AWSEC2TemplateOptions) {
			gpf4medTemplate.getOptions().as(AWSEC2TemplateOptions.class).enableMonitoring();
		}

		// create node
		logger.info("Creating node type(%s) in group %s, opening ports 22, %s with admin user and JDK", 
				gpf4medTemplate.getHardware().getId(), group, port);
		NodeMetadata node = null;
		try {
			node = getOnlyElement(compute.createNodesInGroup(group, 1, gpf4medTemplate));
			checkState(node != null, "No node was acquired");
			logger.info(String.format("Available node(%s) os(%s) addresses%s group(%s)", node.getId(), 
					node.getOperatingSystem(), concat(node.getPrivateAddresses(), node.getPublicAddresses()), 
					group));
		} catch (RunNodesException e) {
			throw destroyBadNodesAndPropagate(e);
		}			
		return node;
	}

	public ExecResponse startDaemonOnNode(final InitScript daemon, final String nodeId) {
		checkArgument(StringUtils.isNotBlank(nodeId), "Uninitialized or invalid node Id");
		try {
			return getOnlyElement(compute.runScriptOnNodesMatching(matchId(nodeId), daemon, 
					asCurrentUser().blockOnComplete(false)).values());
		} catch (RunScriptOnNodesException e) {
			throw propagate(e);
		}
	}

	public Set<? extends NodeMetadata> listRunningNodesInGroup(final String group) {
		return filter(compute.listNodesDetailsMatching(all()), runningInGroup(group));
	}

	public @Nullable Set<? extends NodeMetadata> destroyNodesInGroup(final String group) {
		checkState(nodesCanBeAcquired(), "Node destruction is not supported by your compute service");		
		return compute.destroyNodesMatching(Predicates.<NodeMetadata> and(inGroup(group), not(TERMINATED)));
	}

	private RuntimeException destroyBadNodesAndPropagate(final RunNodesException e) {
		for (final Entry<? extends NodeMetadata, ? extends Throwable> nodeError : e.getNodeErrors().entrySet())
			compute.destroyNode(nodeError.getKey().getId());
		throw propagate(e);
	}

	@SuppressWarnings("unchecked")
	public Map<NodeMetadata, String> stdoutFromCommandOnGroup(final String command, final String group) {
		try {
			return transformValues((Map<NodeMetadata, ExecResponse>) compute.runScriptOnNodesMatching(
					runningInGroup(group), command, asCurrentUser().wrapInInitScript(false)), getStdout());
		} catch (RunScriptOnNodesException e) {
			throw propagate(e);
		}
	}

	public boolean nodesCanBeAcquired() {
		return !new BYONApiMetadata().getContext().isAssignableFrom(compute.getContext().getBackendType());
	}

	@Override
	public String toString() {
		return String.format("connection(%s)", compute.getContext().unwrap());				
	}	

	/* Additional, unused methods

	private Map<? extends NodeMetadata, ExecResponse> execScriptAsRoot(final NodeMetadata node, final String script,
			final @Nullable String nameTask) {
		checkArgument(node != null, "Uninitialized node");		
		checkArgument(StringUtils.isNotBlank(script), "Uninitialized or invalid script");
		try {
			final String nameTask2 = nameTask != null ? nameTask.replaceAll("\\..*", "") 
					: RandomStringUtils.random(8, true, true);
			logger.trace(String.format("Will execute command %s in the machine %s", script, node));
			return compute.runScriptOnNodesMatching(matchId(node.getId()), script,
					runAsRoot(true).nameTask(nameTask2));
		} catch (RunScriptOnNodesException e) {
			throw propagate(e);
		}
	}

	private Map<? extends NodeMetadata, ExecResponse> execScript(final NodeMetadata node, final String script,
			final @Nullable String nameTask) {
		checkArgument(node != null, "Uninitialized node");		
		checkArgument(StringUtils.isNotBlank(script), "Uninitialized or invalid script");
		try {
			final String nameTask2 = nameTask != null ? nameTask.replaceAll("\\..*", "") 
					: RandomStringUtils.random(8, true, true);
			logger.trace(String.format("Will execute command %s in the machine %s", script, node));
			return compute.runScriptOnNodesMatching(matchId(node.getId()), script,
					overrideLoginCredentials(currentUser()).runAsRoot(false).nameTask(nameTask2));
		} catch (RunScriptOnNodesException e) {
			throw propagate(e);
		}
	} */

}