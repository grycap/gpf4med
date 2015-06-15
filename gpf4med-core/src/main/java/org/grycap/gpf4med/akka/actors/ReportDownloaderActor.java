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

import static akka.event.Logging.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.grycap.gpf4med.akka.actors.TRENCADISActor.Work;
import org.grycap.gpf4med.util.AkkaException.ReportDownloaderDataException;
import org.grycap.gpf4med.util.AkkaException.ReportDownloaderException;
import org.grycap.gpf4med.util.AkkaException.ReportDownloaderIOException;
import org.grycap.gpf4med.util.TRENCADISUtils;

import trencadis.infrastructure.services.DICOMStorage.impl.wrapper.xmlOutputDownloadAllReportsID.DICOM_SR_ID;
import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;
import akka.actor.UntypedActor;
import akka.event.LoggingAdapter;


public class ReportDownloaderActor extends UntypedActor {

	protected final LoggingAdapter LOGGER = getLogger(getContext().system(), this);
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		LOGGER.debug("Actor created");
	};
	
	@Override
	public void postStop() throws Exception {
		super.postStop();
		LOGGER.debug("Actor stopped");
	}
	
	@Override
	public void preRestart(Throwable reason, scala.Option<Object> message) throws Exception {
		super.preRestart(reason, message);
		LOGGER.debug("Actor restarted");		
	};
	
	@Override
	public void onReceive(Object message) throws ReportDownloaderIOException, ReportDownloaderException, ReportDownloaderDataException {
		if (message instanceof ReportDownloaderMessage) {
			ReportDownloaderMessage reports = (ReportDownloaderMessage) message;
			
			String ids = vectorToString(reports.getIds());
			try {
				// Download a set of reports				
				boolean success = TRENCADISUtils.INSTANCE.downloadReports(reports.getBackend(),
						ids, reports.getDest().getAbsolutePath());
				
				// Fault-Tolerance
				if (!success) {
					throw new ReportDownloaderDataException("Can not download data of reports with IDs: " + ids);
				} else {
					getSender().tell(Work.DONE, getSelf());
				}
				
			} catch (IOException e1) {
				throw new ReportDownloaderIOException("Can not write the reports in file");
			} catch (Exception e2) {
				throw new ReportDownloaderException("Can not get report from backend storage");
			}			
		} else {
			unhandled(message);
			LOGGER.warning("Type of message not supported: " + message.getClass().getName());
		}
		
	}
	
	private String vectorToString(Vector<DICOM_SR_ID> ids) {
		String retval = "";
		for (DICOM_SR_ID id : ids) {
			retval += id.getValue() + ",";
		}
		return retval.substring(0, retval.length() - 1);
	}
	
	/* Report Downloader Message class */
		
	public static class ReportDownloaderMessage {
		
		private BackEnd backend = null; 
		private Vector<DICOM_SR_ID> ids = null;
		private File dest = null;
		
		public ReportDownloaderMessage(BackEnd backend,
				Vector<DICOM_SR_ID> ids, File reportsDest) {

			this.backend = backend;
			this.ids = ids;
			this.dest = reportsDest;
		}

		public BackEnd getBackend() {
			return backend;
		}

		public void setBackend(BackEnd backend) {
			this.backend = backend;
		}

		public Vector<DICOM_SR_ID> getIds() {
			return ids;
		}

		public void setIds(Vector<DICOM_SR_ID> ids) {
			this.ids = ids;
		}

		public File getDest() {
			return dest;
		}

		public void setDest(File dest) {
			this.dest = dest;
		}
		
	}
	
}
