/*
 * Copyright 2015 Institute for Molecular Imaging Instrumentation (I3M)
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

package org.grycap.gpf4med.akka;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.grycap.gpf4med.akka.AkkaApplication.GPF4MED_SHORTNAME;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.AbstractIdleService;

import javax.annotation.Nullable;

import org.grycap.gpf4med.akka.actors.TRENCADISActor;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.slf4j.Logger;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;

/**
 * Akka service.
 * @author Erik Torres <etserrano@gmail.com>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public class AkkaService extends AbstractIdleService {

	private final static Logger LOGGER = getLogger(AkkaService.class);	

	public static final String SERVICE_NAME = GPF4MED_SHORTNAME + " service";
	public static final String APP_NAME = GPF4MED_SHORTNAME;
	
	public static final Timeout TIMEOUT = new Timeout(Duration.create(2, TimeUnit.MINUTES));
	
	private final ActorSystem actorSystem;
	
	public enum Storage {
		TRENCADIS
	}

	public AkkaService() throws Exception {
		// Load configuration
		final Config config = loadConfig(ConfigurationManager.INSTANCE.getAkkaConfigFile().getAbsolutePath());
		// Create actor system
		actorSystem = ActorSystem.create(APP_NAME, config);
		
		LOGGER.info(SERVICE_NAME + " initialized successfully");

	}

	private Config loadConfig(final @Nullable String confname) {
		Config config = null;
		final String confname2 = trimToNull(confname);
		if (confname2 != null) {
			final ConfigParseOptions options = ConfigParseOptions.defaults().setAllowMissing(false);
			final Config customConfig = ConfigFactory.parseFileAnySyntax(new File(confname2), options);
			final Config regularConfig = ConfigFactory.load();
			final Config combined = customConfig.withFallback(regularConfig);
			config = ConfigFactory.load(combined);
		} else {
			config = ConfigFactory.load();
		}
		return config;
	}

	@Override
	protected void startUp() throws Exception {
		ActorRef dicomActor = actorSystem.actorOf(Props.create(TRENCADISActor.class), "trencadis_" + randomAlphanumeric(6));
	    Future<Object> future = Patterns.ask(dicomActor, Storage.TRENCADIS, TIMEOUT);
	    LOGGER.info(SERVICE_NAME + " started");
	    
	    try {
	    	Await.result(future, Duration.create(1, TimeUnit.MINUTES));
	    } catch (TimeoutException e) {
	    	actorSystem.shutdown();
	    }		
	}

	@Override
	protected void shutDown() throws Exception {
		LOGGER.info(SERVICE_NAME + " terminaded");
	}
}