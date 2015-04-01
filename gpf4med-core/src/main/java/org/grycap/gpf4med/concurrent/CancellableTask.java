package org.grycap.gpf4med.concurrent;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Range.closed;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Range;
import com.google.common.util.concurrent.ListenableFutureTask;

/**
 * Encloses a {@link ListenableFutureTask}, which allows the asynchronous execution of a task with cancellation support. In
 * addition, this class adds an identifier that can be used to store and access the task from a resource. Different instances
 * of this class can be grouped by assigning them the same parent identifier (field {@code puuid}). When the parent of an
 * instance is unknown, the default value {@code "00000000-0000-0000-0000-000000000000"} will be assigned to avoid {@code null}
 * values in the parent field.
 * @param <V> - The result type returned by this {@link CancellableTask}'s get methods.
 */
public class CancellableTask<V> {

	public static final Range<Double> PROGRESS_RANGE = closed(0.0d, 100.0d);
	public static final String DEFAULT_PUUID = "00000000-0000-0000-0000-000000000000";

	private final UUID uuid = randomUUID();
	private final UUID puuid;
	protected ListenableFutureTask<V> task;
	private double progress = PROGRESS_RANGE.lowerEndpoint();
	private String status;
	private boolean hasErrors = false;

	public CancellableTask() {
		this(null);
	}

	public CancellableTask(final UUID puuid) {
		this.puuid = puuid != null ? puuid : fromString(DEFAULT_PUUID);
	}

	public UUID getUuid() {
		return uuid;
	}

	public UUID getPuuid() {
		return puuid;
	}

	public @Nullable ListenableFutureTask<V> getTask() {
		return task;
	}

	public double getProgress() {
		double realProgress;
		if (task.isCancelled()) {
			realProgress = PROGRESS_RANGE.lowerEndpoint();
		} else if (task.isDone()) {
			realProgress = PROGRESS_RANGE.upperEndpoint();
		} else {
			realProgress = progress;
		}		
		return realProgress;
	}

	public void setProgress(final double progress) {
		checkArgument(PROGRESS_RANGE.contains(progress), "Progress is not in the valid range: " + PROGRESS_RANGE);
		this.progress = progress;
	}	

	public String getStatus() {
		return trimToEmpty(status);
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public boolean isDone() {
		return task.isDone();
	}	

	public boolean hasErrors() {
		return hasErrors;
	}

	public void setHasErrors(final boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("uuid", uuid)
				.add("puuid", puuid)
				.add("progress", progress)
				.add("status", status)
				.add("hasErrors", hasErrors)
				.toString();
	}

}