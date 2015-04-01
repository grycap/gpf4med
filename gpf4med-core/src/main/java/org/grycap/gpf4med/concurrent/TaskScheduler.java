package org.grycap.gpf4med.concurrent;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static org.grycap.gpf4med.concurrent.TaskUncaughtExceptionHandler.TASK_UNCAUGHT_EXCEPTION_HANDLER;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Schedules tasks to run periodically. Tasks are scheduled with a pool of threads for periodic execution 
 * and a {@link ListenableScheduledFuture} is returned to the caller.
 */
public enum TaskScheduler implements Closeable {

	TASK_SCHEDULER;

	private final static Logger LOGGER = getLogger(TaskScheduler.class);

	public static final String THREAD_NAME_PATTERN = "task-scheduler-%d";
	private static final int MIN_NUM_THREADS = 2;

	private static final int TIMEOUT_SECS = 20;

	private final ListeningScheduledExecutorService scheduler;

	private AtomicBoolean shouldRun = new AtomicBoolean(false);

	private TaskScheduler() { 
		final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(
				Math.max(MIN_NUM_THREADS, Runtime.getRuntime().availableProcessors()), 
				new ThreadFactoryBuilder()
				.setNameFormat(THREAD_NAME_PATTERN)
				.setDaemon(false)
				.setUncaughtExceptionHandler(TASK_UNCAUGHT_EXCEPTION_HANDLER)
				.build());
		scheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
		scheduler = listeningDecorator(scheduledThreadPoolExecutor);
	}

	/**
	 * See description of {@link java.util.concurrent.ScheduledExecutorService#schedule(Runnable, long, TimeUnit)}
	 */
	public ListenableScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
		checkState(shouldRun.get(), "Task scheduler uninitialized");
		return scheduler.schedule(command, delay, unit);
	}

	/**
	 * See description of {@link java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 */
	public ListenableScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
		checkState(shouldRun.get(), "Task scheduler uninitialized");
		return scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	/**
	 * See description of {@link java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)}
	 */
	public ListenableScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay, final TimeUnit unit) {
		checkState(shouldRun.get(), "Task scheduler uninitialized");
		return scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

	public void preload() {
		final boolean previousStatus = shouldRun.getAndSet(true);
		if (!previousStatus) {
			LOGGER.info("Task scheduler initialized successfully");
		} else {
			LOGGER.info("Task scheduler was already initialized: status is left untouched");
		}		
	}

	@Override
	public void close() throws IOException {
		shouldRun.set(false);
		try {
			if (!shutdownAndAwaitTermination(scheduler, TIMEOUT_SECS, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (Exception e) {
			// force shutdown if current thread also interrupted, preserving interrupt status
			scheduler.shutdownNow();
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
		} finally {
			LOGGER.info("Task scheduler shutdown successfully");	
		}
	}

}