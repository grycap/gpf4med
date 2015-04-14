package org.grycap.gpf4med.threads;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.util.concurrent.ListenableFutureTask.create;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.grycap.gpf4med.concurrent.TaskRunner.TASK_RUNNER;
import static org.grycap.gpf4med.concurrent.TaskScheduler.TASK_SCHEDULER;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.grycap.gpf4med.concurrent.CancellableTask;
import org.slf4j.Logger;

import trencadis.infrastructure.services.dicomstorage.backend.BackEnd;

import com.google.common.util.concurrent.ListenableScheduledFuture;

public class ImportReportsGroupTask extends CancellableTask<Integer> {

	private static final Logger LOGGER = getLogger(ImportReportsGroupTask.class);

	private final List<ImportReportsTask> tasks = newArrayList();

	private final AtomicBoolean submitted = new AtomicBoolean(false);
	private static final Lock mutex = new ReentrantLock();
	
	public void addTask(final BackEnd backend, final String centerName,
			final String credentials, final List<String> ids, final int partition, final File destDir) {
		mutex.lock();
		try {
			checkState(!submitted.get(), "Task already submitted");
			
			tasks.add(new ImportReportsTask(getUuid(), backend, centerName, credentials, ids, partition, destDir));
		} finally {
			mutex.unlock();
		}
	}

	@Override
	public boolean isDone() {
		mutex.lock();
		try {
			return submitted.get() ? super.isDone() : false;		
		} finally {
			mutex.unlock();
		}
	}

	@Override
	public double getProgress() {
		mutex.lock();
		try {
			return submitted.get() ? super.getProgress() : PROGRESS_RANGE.lowerEndpoint();
		} finally {
			mutex.unlock();
		}
	}

	public void sumbitAll() {
		mutex.lock();
		try {
			checkState(!tasks.isEmpty(), "No tasks found");
			this.task = create(importReportsGroupTask(this));
			TASK_RUNNER.execute(this);
			submitted.set(true);					
		} finally {			
			mutex.unlock();
		}
	}

	public void tick() {
		mutex.lock();
		try {
			double realProgress = 0.0d;
			boolean realDone = true;
			for (final ImportReportsTask task : tasks) {
				realProgress += task.getProgress();
				realDone = realDone && task.isDone();
			}
			if (PROGRESS_RANGE.upperEndpoint().equals(realProgress) && !realDone) {
				realProgress--;
			}			
			setProgress(realProgress / tasks.size());
		} finally {			
			mutex.unlock();
		}
	}

	private Callable<Integer> importReportsGroupTask(final ImportReportsGroupTask task) {
		return new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				setStatus("Downloading reports with " + tasks.size() + " workers...");
				for (final ImportReportsTask task : tasks) {
					TASK_RUNNER.execute(task);
				}
				do {
					try {
						final ListenableScheduledFuture<?> future = TASK_SCHEDULER.schedule(checkTaskProgress(task), 2l, SECONDS);
						future.get();
					} catch (Exception e) {
						LOGGER.error("Failed to get task status", e);
					}
				} while (!PROGRESS_RANGE.upperEndpoint().equals(task.getProgress()));
				return tasks.size();
			}
		};
	}

	private static Runnable checkTaskProgress(final ImportReportsGroupTask task) {
		return new Runnable() {
			@Override
			public void run() {
				task.tick();
			}
		};
	}

}