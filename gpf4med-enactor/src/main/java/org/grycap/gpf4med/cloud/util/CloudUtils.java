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

package org.grycap.gpf4med.cloud.util;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.compute.options.TemplateOptions.Builder.runAsRoot;

import java.io.File;
import java.io.IOException;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.statements.login.UserAdd;

import com.google.common.base.Function;
import com.google.common.io.Files;
import com.google.common.net.HostAndPort;

/**
 * Utilities to handle common actions on Cloud nodes.
 * @author Adrian Cole
 * <br>
 * Modifications by Erik Torres:
 * <ul>
 * <li>Adaptation to the standards of the Gpf4Med project.</li>
 * </ul>
 */
public final class CloudUtils {

	/**
	 * Gets the standard output part from the response of a node after a command execution.
	 * @return the standard output part of a command execution.
	 */
	public static Function<ExecResponse, String> getStdout() {
		return new Function<ExecResponse, String>() {
			@Override
			public String apply(final ExecResponse input) {
				return input.getOutput();
			}
		};
	}

	/**
	 * Gets the first public address where a node listen for incoming requests in the
	 * specified network port.
	 * @param port the network port where the node listens for connections.
	 * @return the first public address found for the specified port.
	 */
	public static Function<NodeMetadata, HostAndPort> firstPublicAddressToHostAndPort(final int port) {
		return new Function<NodeMetadata, HostAndPort>() {
			@Override
			public HostAndPort apply(final NodeMetadata input) {
				return HostAndPort.fromParts(get(input.getPublicAddresses(), 0), port);
			}
			@Override
			public String toString() {
				return "firstPublicAddressToHostAndPort(" + port + ")";
			}
		};
	}

	/**
	 * Configures an options template for executing operations on the nodes using the
	 * login credentials of the current user.
	 * @return an options template configured for executing operations on the nodes 
	 *         using the login credentials of the current user.
	 */
	public static TemplateOptions asCurrentUser() {
		return runAsRoot(false).overrideLoginCredentials(currentUser());
	}

	/**
	 * Creates a statement that will add an unprivileged user to a machine that could
	 * be latter used for executing commands on the machine using RSA SSH authentication.
	 * @return
	 */
	public static UserAdd addLoginForCommandExecution() {
		final String publicKeyFile = System.getProperty("user.home") + "/.ssh/id_rsa.pub";
		final String publicKey;
		try {
			publicKey = Files.toString(new File(publicKeyFile), UTF_8);
		} catch (IOException e) {
			throw propagate(e);
		}
		assert publicKey.startsWith("ssh-rsa ") : "invalid public key:\n" + publicKey;
		return UserAdd.builder().login(System.getProperty("user.name")).authorizeRSAPublicKey(publicKey).build();
	}
	
	/**
	 * Loads the RSA SSH private key of the current user from the default location.
	 * @return the login credentials of the current user.
	 */
	public static LoginCredentials currentUser() {
		final String privateKeyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
		final String privateKey;
		try {
			privateKey = Files.toString(new File(privateKeyFile), UTF_8);
		} catch (IOException e) {
			throw propagate(e);
		}
		assert privateKey.startsWith("-----BEGIN RSA PRIVATE KEY-----") : "invalid key:\n" + privateKey;
		return LoginCredentials.builder().user(System.getProperty("user.name")).privateKey(privateKey).build();
	}

}