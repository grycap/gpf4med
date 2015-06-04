package org.grycap.gpf4med.util;

import java.io.IOException;

public class AkkaException {
	
	public static class DicomStorageException extends NoSuchFieldException {
		
		private static final long serialVersionUID = 1L;
	      
		public DicomStorageException(String msg) {
	    	  super(msg);
		}
	}
	
	public static class CreateWorkerException extends InterruptedException {
		
		private static final long serialVersionUID = 1L;
	      
		public CreateWorkerException(String msg) {
	    	  super(msg);
		}
	}
	
	public static class ReportDownloaderException extends Exception {
		
		private static final long serialVersionUID = 1L;
	      
		public ReportDownloaderException(String msg) {
	    	  super(msg);
		}
	}
	
	public static class ReportDownloaderIOException extends IOException {
		
		private static final long serialVersionUID = 1L;
	      
		public ReportDownloaderIOException(String msg) {
	    	  super(msg);
		}
	}
	
	public static class ReportDownloaderDataException extends Exception {
		private static final long serialVersionUID = 1L;
	      
		public ReportDownloaderDataException(String msg) {
	    	  super(msg);
		}
	}
	
	public static class ServiceUnavailable extends RuntimeException {
		
		private static final long serialVersionUID = 1L;
		
		public ServiceUnavailable(String msg) {
	    	  super(msg);
	    }
	}
	
}
