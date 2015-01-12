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

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Graph statistics.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum Statistics {

	INSTANCE;

	private final AtomicInteger totalSubmitted = new AtomicInteger(0);
	private final AtomicInteger successDownloads = new AtomicInteger(0);
	private final AtomicInteger failedDownloads = new AtomicInteger(0);
	private final AtomicInteger successParses = new AtomicInteger(0);
	private final AtomicInteger failedParses = new AtomicInteger(0);
	private final AtomicInteger successGraphLoads = new AtomicInteger(0);
	private final AtomicInteger failedGraphLoads = new AtomicInteger(0);

	private Statistics() { }

	public void restart() {
		setTotalSubmitted(0);
		initSuccessDownloads();
		initFailedDownloads();
		initSuccessParses();
		initFailedParses();
		initSuccessGraphLoads();
		initFailedGraphLoads();
	}

	public int getTotalSubmitted() {
		return totalSubmitted.get();
	}

	public void setTotalSubmitted(final int value) {
		totalSubmitted.lazySet(value);
	}	

	public int getSuccessDownloads() {
		return successDownloads.get();
	}

	public void incSuccessDownloads(final int delta) {
		if (delta > 0) {
			successDownloads.addAndGet(delta);
		}
	}

	public void initSuccessDownloads() {
		successDownloads.lazySet(0);
	}

	public int getFailedDownloads() {
		return failedDownloads.get();
	}

	public void incFailedDownloads(final int delta) {
		if (delta > 0) {
			failedDownloads.addAndGet(delta);
		}
	}

	public void initFailedDownloads() {
		failedDownloads.lazySet(0);
	}

	public int getSuccessParses() {
		return successParses.get();
	}

	public void incSuccessParses(final int delta) {
		if (delta > 0) {
			successParses.addAndGet(delta);
		}
	}

	public void initSuccessParses() {
		successParses.lazySet(0);
	}

	public int getFailedParses() {
		return failedParses.get();
	}

	public void incFailedParses(final int delta) {
		if (delta > 0) {
			failedParses.addAndGet(delta);
		}
	}

	public void initFailedParses() {
		failedParses.lazySet(0);
	}

	public int getSuccessGraphLoads() {
		return successGraphLoads.get();
	}

	public void incSuccessGraphLoads(final int delta) {
		if (delta > 0) {
			successGraphLoads.addAndGet(delta);
		}
	}

	public void initSuccessGraphLoads() {
		successGraphLoads.lazySet(0);
	}

	public int getFailedGraphLoads() {
		return failedGraphLoads.get();
	}

	public void incFailedGraphLoads(final int delta) {
		if (delta > 0) {
			failedGraphLoads.addAndGet(delta);
		}
	}

	public void initFailedGraphLoads() {
		failedGraphLoads.lazySet(0);
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("totalSubmitted", totalSubmitted.get())
				.add("successDownloads", successDownloads.get())
				.add("failedDownloads", failedDownloads.get())
				.add("successParses", successParses.get())
				.add("failedParses", failedParses.get())
				.add("successGraphLoads", successGraphLoads.get())
				.add("failedGraphLoads", failedGraphLoads.get())				
				.toString();
	}

}