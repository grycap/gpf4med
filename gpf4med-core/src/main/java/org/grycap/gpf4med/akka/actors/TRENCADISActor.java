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

package org.grycap.gpf4med.akka.actors;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.event.Logging.getLogger;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.grycap.gpf4med.akka.AkkaService.Storage;
import org.grycap.gpf4med.akka.Progress;
import org.grycap.gpf4med.akka.actors.HospitalActor.HospitalMessage;
import org.grycap.gpf4med.util.AkkaException.CreateWorkerException;
import org.grycap.gpf4med.util.AkkaException.DicomStorageException;
import org.grycap.gpf4med.util.TRENCADISUtils;

import scala.concurrent.duration.Duration;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.event.LoggingAdapter;
import akka.japi.Function;

/**
 * Este actor recibe el objeto TRENCADIS
 * @author locamo
 *
 */
public class TRENCADISActor extends UntypedActor{
	
	protected final LoggingAdapter LOGGER = getLogger(getContext().system(), this);
	
	private static final Duration TIMEOUT = Duration.create(1, TimeUnit.MINUTES);
	
	private static Progress progress = new Progress(TRENCADISActor.class.getSimpleName());
	private static int currentCount = 0;
	private static int HOSPITALS = 1;
	
	// Stop TRENCADISActor children if the service is unavailable
	// It retries 10 times during a given timeout; if it reaches that limit, it stop child automatically
	private SupervisorStrategy strategy = new OneForOneStrategy(10, TIMEOUT,
			new Function<Throwable, Directive>() {
		@Override
		public Directive apply(final Throwable t) {			
			if (t instanceof CreateWorkerException) {
				return restart();
			} else if (t instanceof Exception) {
				return stop();
			} else {
				return escalate();
			}
		}
	});
	
	@Override
	public void preStart() throws Exception {
		LOGGER.debug("Actor created");
	};
	
	@Override
	public void postStop() throws Exception {
		super.postStop();
		LOGGER.debug("Actor stopped");
	}
	
	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Storage.TRENCADIS) {
			Vector<TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE> dicomStorages = TRENCADISUtils.INSTANCE.getDicomStorage();
			if (dicomStorages != null) {
				HOSPITALS = dicomStorages.size();
				for (TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE dicomStorage : dicomStorages) {
					HospitalMessage hospitalMessage = new HospitalMessage(dicomStorage);
					getContext().actorOf(Props.create(HospitalActor.class),
							"hospital_actor_" + randomAlphanumeric(6))
							.tell(hospitalMessage, getSelf());
				}
			} else {
				throw new DicomStorageException("Can not get reports from TRENCADIS middleware");
			}			
		} else if(message == Work.DONE) {
			currentCount += 1;
			progress.setPercent(100 * (currentCount / HOSPITALS));
			if (currentCount == HOSPITALS)
				getContext().system().shutdown();
		} else {
			unhandled(message);
			LOGGER.warning("Type of message not supported: " + message.getClass().getName());
		}		
	}
	
	public static Progress getProgress() {
		return progress;
	}
		
	public enum Work {
		DONE
	}
	
}
