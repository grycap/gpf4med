package org.grycap.gpf4med.concurrent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.grycap.gpf4med.concurrent.TaskUncaughtExceptionHandler.TASK_UNCAUGHT_EXCEPTION_HANDLER;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Runs tasks in a pool of threads that must be disposed as part of the application termination. Tasks
 * are submitted to the pool of threads for execution and a {@link ListenableFuture} is returned to the
 * caller.
 * @see <a href="https://code.google.com/p/guava-libraries/wiki/ListenableFutureExplained">ListenableFutureExplained</a>
 */
public enum TaskRunner implements Closeable {

	/**
	 * Singleton instance of {@link TaskRunner} that executes short-living tasks in a pool of threads.
	 */
	TASK_RUNNER;

	private final static Logger LOGGER = getLogger(TaskRunner.class);

	public static final String THREAD_NAME_PATTERN = "task-runner-%d";

	private static final int TIMEOUT_SECS = 20;

	/**
	 * Reuse the code of {@link java.util.concurrent.Executors#newCachedThreadPool(java.util.concurrent.ThreadFactory)}
	 * to create the thread pool, limiting the maximum number of threads in the pool to 128.
	 */
	private final ListeningExecutorService runner = listeningDecorator(new ThreadPoolExecutor(0, 128, 60l, SECONDS, new SynchronousQueue<Runnable>(), 
			new ThreadFactoryBuilder().setNameFormat(THREAD_NAME_PATTERN)
			.setDaemon(false)
			.setUncaughtExceptionHandler(TASK_UNCAUGHT_EXCEPTION_HANDLER)
			.build()));

	private AtomicBoolean shouldRun = new AtomicBoolean(false);

	private TaskRunner() { }

	/**
	 * Submits a new task for execution to the pool of threads managed by this class.
	 * @param task - task to be executed
	 * @return a {@link ListenableFuture} that the caller can use to track the execution of the
	 *         task and to register a callback function.
	 */
	public <T> ListenableFuture<T> submit(final Callable<T> task) {
		checkState(shouldRun.get(), "Task runner uninitialized");
		return runner.submit(task);
	}

	/**
	 * Executes a new task that supports cancellation and provides a unique identifier.
	 * @param task - task to be executed
	 */
	public <T> void execute(final CancellableTask<T> task) {
		checkState(shouldRun.get(), "Task runner uninitialized");
		checkArgument(task != null, "Uninitialized task");
		runner.execute(task.getTask());
	}

	public void preload() {
		final boolean previousStatus = shouldRun.getAndSet(true);
		if (!previousStatus) {
			LOGGER.info("Task runner initialized successfully");
		} else {
			LOGGER.info("Task runner was already initialized: status is left untouched");
		}
	}

	@Override
	public void close() throws IOException {
		shouldRun.set(false);
		try {
			if (!shutdownAndAwaitTermination(runner, TIMEOUT_SECS, SECONDS)) {
				runner.shutdownNow();
			}
		} catch (Exception e) {
			// force shutdown if current thread also interrupted, preserving interrupt status
			runner.shutdownNow();
			if (e instanceof InterruptedException) {
				currentThread().interrupt();
			}
		} finally {
			LOGGER.info("Task runner shutdown successfully");	
		}
	}

}