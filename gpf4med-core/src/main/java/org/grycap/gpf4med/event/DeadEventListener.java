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

package org.grycap.gpf4med.event;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

/**
 * Listener waiting for events that were posted but not delivered to anyone.
 * @author Erik Torres <ertorser@upv.es>
 */
public enum DeadEventListener {

	INSTANCE;

	private final static Logger LOGGER = LoggerFactory.getLogger(DeadEventListener.class);

	@Subscribe
	public void listen(final DeadEvent deadEvent) {
		LOGGER.warn("Undelivered event with no subscribers was caught: [type='"
				+ getEventType(deadEvent) + "', source='" + getSource(deadEvent) + "']");
	}

	private String getEventType(final @Nullable DeadEvent deadEvent) {
		return (deadEvent != null && deadEvent.getEvent() != null
				? deadEvent.getEvent().getClass().getCanonicalName() : "(not available)");
	}

	private String getSource(final @Nullable DeadEvent deadEvent) {
		return (deadEvent != null && deadEvent.getSource() != null
				? deadEvent.getSource().getClass().getCanonicalName() : "(not available)");
	}

}