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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.annotation.Nullable;

import org.grycap.gpf4med.event.EventBusHandler;
import org.grycap.gpf4med.event.FileEnqueuedEvent;

import com.google.common.util.concurrent.Monitor;

/**
 * Stores the files that will be parsed.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum FileQueue {

	INSTANCE;

	private final Monitor monitor = new Monitor();

	private final Queue<File> queue = new LinkedList<File>();

	public void add(final File file) {
		checkArgument(file != null, "Uninitialized file");
		monitor.enter();
		try {
			queue.add(file);
			EventBusHandler.INSTANCE.post(new FileEnqueuedEvent());
			Statistics.INSTANCE.incSuccessDownloads(1);
		} finally {
			monitor.leave();
		}
	}

	public void failed() {
		failed(1);
	}

	public void failed(final int count) {
		checkArgument(count > 0, "Invalid count");
		monitor.enter();
		try {
			Statistics.INSTANCE.incFailedDownloads(count);
		} finally {
			monitor.leave();
		}
	}

	public @Nullable File remove() {
		monitor.enter();
		try {
			return queue.remove();
		} catch (NoSuchElementException e) {
			return null;
		} finally {
			monitor.leave();
		}
	}

	public @Nullable File element() {
		monitor.enter();
		try {
			return queue.element();
		} catch (NoSuchElementException e) {
			return null;
		} finally {
			monitor.leave();
		}
	}

	public final void restart() {
		monitor.enter();
		try {
			queue.clear();
			Statistics.INSTANCE.initSuccessDownloads();
			Statistics.INSTANCE.initFailedDownloads();
		} finally {
			monitor.leave();
		}
	}

}