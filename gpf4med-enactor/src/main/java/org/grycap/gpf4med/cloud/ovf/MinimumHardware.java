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

package org.grycap.gpf4med.cloud.ovf;

import static com.google.common.base.MoreObjects.toStringHelper;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.compute.util.ComputeServiceUtils.getSpace;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.HardwareImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;

/**
 * Minimum hardware that supports the processing of a report set using a specific 
 * type of graph.
 * @author Erik Torres <ertorser@upv.es>
 */
public class MinimumHardware extends HardwareImpl {

	protected final int reports;

	public MinimumHardware(final String providerId, final String name, final String id,
			final Location location, final URI uri, final Map<String, String> userMetadata,
			final Set<String> tags, Iterable<? extends Processor> processors,
			final int ram, final Iterable<? extends Volume> volumes,
			final Predicate<Image> supportsImage, final String hypervisor,
			final int reports) {
		super(providerId, name, id, location, uri, userMetadata, tags, processors, ram,
				volumes, supportsImage, hypervisor);
		this.reports = reports;
	}

	/**
	 * Gets the maximum number of reports that can be processed with this hardware.
	 * @return the maximum number of reports that can be processed with this hardware.
	 */
	public int getReports() {
		return reports;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final ResourceMetadata<ComputeType> that) {
		if (that instanceof MinimumHardware) {
			final MinimumHardware thatHardware = MinimumHardware.class.cast(that);
			return ComparisonChain.start()
					.compare(getCores(this), getCores(thatHardware))
					.compare(getRam(), thatHardware.getRam())
					.compare(getSpace(this), getSpace(thatHardware))
					.compare(getReports(), thatHardware.getReports())
					.result();
		}
		return super.compareTo(that);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toStringHelper(this)
				.add("processors", getProcessors())
				.add("ram", getRam())
				.add("volumes", getVolumes())
				.add("reports", getReports())
				.toString();
	}

}