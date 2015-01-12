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

package org.grycap.gpf4med.model.util;

/**
 * Statistics abut the objects loaded in the current graph.
 * @author Erik Torres <ertorser@upv.es>
 */
public class GraphStatistics {

	private int totalSubmitted;
	private int successDownloads;
	private int failedDownloads;
	private int successParses;
	private int failedParses;
	private int successGraphLoads;
	private int failedGraphLoads;
	
	public GraphStatistics() { }

	public int getTotalSubmitted() {
		return totalSubmitted;
	}

	public void setTotalSubmitted(final int totalSubmitted) {
		this.totalSubmitted = totalSubmitted;
	}

	public int getSuccessDownloads() {
		return successDownloads;
	}

	public void setSuccessDownloads(final int successDownloads) {
		this.successDownloads = successDownloads;
	}

	public int getFailedDownloads() {
		return failedDownloads;
	}

	public void setFailedDownloads(final int failedDownloads) {
		this.failedDownloads = failedDownloads;
	}

	public int getSuccessParses() {
		return successParses;
	}

	public void setSuccessParses(final int successParses) {
		this.successParses = successParses;
	}

	public int getFailedParses() {
		return failedParses;
	}

	public void setFailedParses(final int failedParses) {
		this.failedParses = failedParses;
	}

	public int getSuccessGraphLoads() {
		return successGraphLoads;
	}

	public void setSuccessGraphLoads(final int successGraphLoads) {
		this.successGraphLoads = successGraphLoads;
	}

	public int getFailedGraphLoads() {
		return failedGraphLoads;
	}

	public void setFailedGraphLoads(final int failedGraphLoads) {
		this.failedGraphLoads = failedGraphLoads;
	}	
	
}