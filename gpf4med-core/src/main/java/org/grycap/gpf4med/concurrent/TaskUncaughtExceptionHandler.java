package org.grycap.gpf4med.concurrent;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;

/**
 * Handles possible uncaught {@link RuntimeException} thrown by threaded tasks. When a thread terminates 
 * abnormally class simply writes a message to the logging system, which will not hang the JVM.
 */
public enum TaskUncaughtExceptionHandler implements UncaughtExceptionHandler {

	TASK_UNCAUGHT_EXCEPTION_HANDLER;
	
	private final static Logger LOGGER = getLogger(TaskUncaughtExceptionHandler.class);
	
	@Override
	public void uncaughtException(final Thread thread, final Throwable cause) {
		LOGGER.error("Thread " + thread.getName() + " terminates abnormally", cause);
	}

}