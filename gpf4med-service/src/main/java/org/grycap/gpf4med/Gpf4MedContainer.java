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

package org.grycap.gpf4med;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.conf.LogManager;

/**
 * Gpf4Med container.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Gpf4MedContainer {

	public static final String VERSION_OPTION   = "version";
	public static final String HELP_OPTION      = "help";
	public static final String[] DAEMON_OPTION = { "d", "daemon" };
	public static final String[] CONFIG_OPTION  = { "c", "configuration" };

	public static final String DIR_PROP = "directory";

	public static final String CONTAINER_NAME = ConfigurationManager.GPF4MED_NAME + " service container";
	public static final String CONTAINER_VERSION = "1.0.0";

	public static void main(final String[] args) {
		// load logging bridges
		LogManager.INSTANCE.preload();

		// parse arguments		
		final Options options = new Options();
		CommandLine cmd = null;
		try {
			cmd = parseParameters(args, options);
		} catch (Exception e) {			
			System.err.println("Parsing options failed. Reason: " + e.getLocalizedMessage());
			System.exit(1);
		}

		if (cmd.getOptions() == null || cmd.hasOption(HELP_OPTION)) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Gpf4MedContainer.class.getSimpleName(), options, true);
			System.exit(0);
		}

		if (cmd.hasOption(VERSION_OPTION)) {
			System.out.println(CONTAINER_NAME + " - Version " + CONTAINER_VERSION);
			System.exit(0);
		}

		boolean daemon = false;
		if (cmd.hasOption(DAEMON_OPTION[0])) {
			daemon = true;
		}

		final Collection<URL> config = new ArrayList<URL>();
		if (cmd.hasOption(CONFIG_OPTION[0])) {
			try {
				final String configDir = cmd.getOptionValue(CONFIG_OPTION[0]);				
				if (StringUtils.isNotBlank(configDir)) {
					final Collection<File> configFiles = FileUtils.listFiles(new File(configDir), 
							new String[]{ "xml" }, false);
					for (final File file : configFiles) {
						config.add(file.toURI().toURL());
					}
				} else {
					throw new IllegalArgumentException("Parameter " + DIR_PROP + " is expected");
				}
			} catch (Exception e) {
				System.err.println("Configuration load failed with error: " + e.getLocalizedMessage());
				System.exit(1);
			}
		}

		// setup configuration URLs
		ConfigurationManager.INSTANCE.setup(config);

		// start service
		final Gpf4MedService service = SingletonService.INSTANCE.service();

		if (daemon) {
			try {
				Thread.currentThread().join();
			} catch (Exception e) { }			
		} else {
			System.out.println();
			System.out.println(String.format(Gpf4MedService.SERVICE_NAME 
					+ " started with WADL available at %sapplication.wadl\nHit enter to stop it...", 
					service.getBaseUri()));
			try {
				System.in.read();
			} catch (IOException ignore) { }
		}

		System.exit(0);
	}

	@SuppressWarnings("static-access")
	private static CommandLine parseParameters(final String[] args, final Options options) 
			throws ParseException {
		options.addOption(HELP_OPTION, false, "print this message and exit");
		options.addOption(VERSION_OPTION, false, "print the version information and exit");
		options.addOption(DAEMON_OPTION[0], DAEMON_OPTION[1], false, "run in daemon mode");		

		final Option configOption = OptionBuilder
				.withArgName(DIR_PROP)				
				.hasArg()
				.withDescription("load configuration from the specified directory")
				.withLongOpt(CONFIG_OPTION[1])
				.create(CONFIG_OPTION[0]);
		options.addOption(configOption);

		final CommandLineParser parser = new PosixParser();
		return parser.parse(options, args);
	}

}
