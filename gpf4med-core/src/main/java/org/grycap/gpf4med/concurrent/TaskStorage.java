package org.grycap.gpf4med.concurrent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.common.util.concurrent.Monitor;

/**
 * Provides memory storage of tasks sent to the system for execution. These tasks can be accessed to track
 * their progress or to cancel them.
 */
public enum TaskStorage implements Closeable {

	/**
	 * Singleton instance of {@link TaskStorage} that provides a memory storage of tasks sent to the 
	 * system for execution.
	 */
	TASK_STORAGE;

	private final static Logger LOGGER = getLogger(TaskStorage.class);

	private final Monitor monitor = new Monitor();

	private final Map<UUID, CancellableTask<?>> map = newHashMap();

	private boolean isActive = false;

	private TaskStorage() { }

	public void add(final CancellableTask<?> task) {
		checkArgument(task != null && task.getUuid() != null, "Uninitialized or invalid task");
		monitor.enter();
		try {
			checkState(isActive, "Task storage uninitialized");
			map.put(task.getUuid(), task);
		} finally {
			monitor.leave();
		}
	}

	public @Nullable CancellableTask<?> remove(final UUID key) {
		monitor.enter();
		try {
			checkState(isActive, "Task storage uninitialized");
			return map.remove(key);
		} finally {
			monitor.leave();
		}
	}

	public @Nullable CancellableTask<?> get(final UUID key) {
		monitor.enter();
		try {
			checkState(isActive, "Task storage uninitialized");
			return map.get(key);		
		} finally {
			monitor.leave();
		}
	}
	
	public void preload() {
		monitor.enter();
		try {			
			if (!isActive) {
				isActive = true;
				LOGGER.info("Task storage initialized successfully");
			} else {
				LOGGER.info("Task storage was already initialized: status is left untouched");
			}
		} finally {
			monitor.leave();
		}
	}

	@Override
	public void close() throws IOException {
		monitor.enter();
		try {
			isActive = false;
			for (final Map.Entry<UUID, CancellableTask<?>> entry : map.entrySet()) {
				try {
					final CancellableTask<?> task = entry.getValue();
					LOGGER.info("Cancelling task: " + task);
					task.getTask().cancel(true);
				} catch (Exception ignored) { }
			}
		} finally {
			monitor.leave();
			LOGGER.info("Task storage shutdown successfully");
		}
	}

}