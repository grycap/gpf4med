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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.predicates.ImagePredicates.any;

import java.util.List;

import org.jclouds.compute.domain.ComputeMetadataBuilder;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Minimum hardware builder.
 * @author Erik Torres <ertorser@upv.es>
 */
public class MinimumHardwareBuilder extends ComputeMetadataBuilder {

	protected List<Processor> processors = Lists.newArrayList();
	protected int ram;
	protected List<Volume> volumes = Lists.newArrayList();
	protected Predicate<Image> supportsImage = any();
	protected String hypervisor;
	protected int reports;

	public MinimumHardwareBuilder() {
		super(ComputeType.HARDWARE);
	}

	public MinimumHardwareBuilder processor(final Processor processor) {
		this.processors.add(checkNotNull(processor, "processor"));
		return this;
	}

	public MinimumHardwareBuilder processors(final Iterable<Processor> processors) {
		this.processors = ImmutableList.copyOf(checkNotNull(processors, "processors"));
		return this;
	}

	public MinimumHardwareBuilder ram(final int ram) {
		this.ram = ram;
		return this;
	}

	public MinimumHardwareBuilder volume(final Volume volume) {
		this.volumes.add(checkNotNull(volume, "volume"));
		return this;
	}

	public MinimumHardwareBuilder volumes(final Iterable<Volume> volumes) {
		this.volumes = ImmutableList.copyOf(checkNotNull(volumes, "volumes"));
		return this;
	}

	public MinimumHardwareBuilder reports(final int reports) {
		this.reports = reports;
		return this;
	}
	
	@Override
	public MinimumHardware build() {
		return new MinimumHardware(providerId, name, id, location, uri, userMetadata, 
				tags, processors, ram, volumes, supportsImage, hypervisor, reports);
	}

}