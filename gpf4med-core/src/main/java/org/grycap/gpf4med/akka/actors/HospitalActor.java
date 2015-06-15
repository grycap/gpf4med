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

import java.io.File;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.grycap.gpf4med.akka.AkkaApplication;
import org.grycap.gpf4med.akka.Progress;
import org.grycap.gpf4med.akka.actors.ReportDownloaderActor.ReportDownloaderMessage;
import org.grycap.gpf4med.akka.actors.TRENCADISActor.Work;
import org.grycap.gpf4med.util.AkkaException.CreateWorkerException;
import org.grycap.gpf4med.util.AkkaException.ReportDownloaderDataException;
import org.grycap.gpf4med.util.AkkaException.ReportDownloaderException;
import org.grycap.gpf4med.util.AkkaException.ReportDownloaderIOException;

import scala.concurrent.duration.Duration;
import trencadis.infrastructure.services.DICOMStorage.impl.wrapper.xmlOutputDownloadAllReportsID.DICOM_SR_ID;
import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;
import trencadis.middleware.operations.DICOMStorage.TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.event.LoggingAdapter;
import akka.japi.Function;


/**
 * This actor creates actors to download reports in order to
 * distribute work
 * 
 * @author Lorena Calabuig <locamo@inf.upv.es>
 *
 */
public class HospitalActor extends UntypedActor{
	
	private final LoggingAdapter LOGGER = getLogger(getContext().system(), this);
	
	private static final Duration TIMEOUT = Duration.create(1, TimeUnit.MINUTES);
	
	private static final int MIN_PARTITIONS = 6;
	private static int NUM_PARTITIONS = 1;
	
	private static final int MAX_PARTITION_SIZE = 50;
	private static int PARTITION = 30;
	
	private static Progress progress = null;
	private static int currentCount = 0;
	private static int WORKERS = 1;
	
	private ActorRef parent = null;
	
	// Stop HospitalActor children if a critical error occurs
	// Restart a given HospitalActor child if an error occurs
	// It retries during a given timeout; if it reaches that limit, it stop child automatically
	private static SupervisorStrategy strategy = new OneForOneStrategy(10,
			TIMEOUT, new Function<Throwable, Directive>(){
			@Override
				public Directive apply(Throwable t) {
					if (t instanceof ReportDownloaderDataException) {
						return restart();
					} else if (t instanceof ReportDownloaderIOException ||
							   t instanceof ReportDownloaderException) {
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
	
	public static Progress getProgress() {
		return progress;
	}
		
	@Override
	public void onReceive(Object message) throws CreateWorkerException, Exception {
		if (message instanceof HospitalMessage) {
			
			if (parent == null)
				parent = getSender();
			
			HospitalMessage hospitalMessage = (HospitalMessage) message;
			final String centerName = hospitalMessage.getDicomStorage().getCenterName().replaceAll(" ", "");
			progress = new Progress(centerName);
			File reportsDest = new File(AkkaApplication.INSTANCE.getDocumentsCacheDir(), centerName);
			reportsDest.mkdir();
			final BackEnd backend = new BackEnd(hospitalMessage.getDicomStorage().getBackend().toString());			
			
			Vector<DICOM_SR_ID> ids = hospitalMessage.getDicomStorage().getDICOM_DSR_IDS();
			// Determine the size of partition depending on number of ids
			PARTITION = calculateSizePartition(ids.size());
			// Determine the number of workers will used 
			WORKERS = calculateWorkers(ids);
			
			for (int i = 0; i < ids.size(); i += PARTITION) {
				createWorker(backend, reportsDest, ids, i, i+PARTITION);
			}
			
			
		} else if(message == Work.DONE) {
			currentCount += 1;		
			progress.setPercent((100 * currentCount) / WORKERS);
			System.out.println(progress.toString());
			if (currentCount == WORKERS) {
				parent.tell(Work.DONE, getSelf());
			}
		} else {
			unhandled(message);
			LOGGER.warning("Type of message not supported: " + message.getClass().getName());
		}
	}

	private void createWorker(BackEnd backend, File reportsDest,
			Vector<DICOM_SR_ID> ids, int beginIdx, int endIdx) throws CreateWorkerException {
		try {
			Vector<DICOM_SR_ID> sub_ids = null;
			if (endIdx > ids.size()) {
				sub_ids = new Vector<DICOM_SR_ID>(ids.subList(beginIdx, ids.size()));
			} else {
				sub_ids = new Vector<DICOM_SR_ID>(ids.subList(beginIdx, endIdx));
			}
			ReportDownloaderMessage workerMessage = new ReportDownloaderMessage(backend, sub_ids, reportsDest);
			getContext().actorOf(Props.create(ReportDownloaderActor.class),
					"worker_actor_" + randomAlphanumeric(6))
					.tell(workerMessage, getSelf());
		} catch (Exception e) {
			throw new CreateWorkerException("Can not create a worker");
		}
	}
	
	private void calculateNumberPartitions(int size) {
		if (size <= MAX_PARTITION_SIZE) {
			NUM_PARTITIONS = MIN_PARTITIONS;
		} else {
			NUM_PARTITIONS = MIN_PARTITIONS + (size / MAX_PARTITION_SIZE);
		}
	}
	
	private int calculateSizePartition(int size) {
		calculateNumberPartitions(size);
		return size / NUM_PARTITIONS;		
	}
	
	private int calculateWorkers(Vector<DICOM_SR_ID> ids) {
		if ((ids.size() % PARTITION) != 0) {
			return ids.size()/PARTITION + 1;
		} else
			return ids.size()/PARTITION;
	}
	
	/* Hospital Message class */
	
	public static class HospitalMessage {
		private TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE dicomStorage = null;
		
		public HospitalMessage(TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE dicomStorage) {
			this.dicomStorage = dicomStorage;
		}

		public TRENCADIS_RETRIEVE_IDS_FROM_DICOM_STORAGE getDicomStorage() {
			return dicomStorage;
		}

	}
	
}
