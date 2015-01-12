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

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;

import javax.inject.Singleton;

import org.jclouds.cim.CIMPredicates;
import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;

import com.google.common.base.Function;

/**
 * Use the resource allocation data that to compose the minimum hardware settings.
 * @author Erik Torres <ertorser@upv.es>
 */
@Singleton
public class MinimumHardwareBuilderFromResourceAllocations implements
Function<Iterable<? extends ResourceAllocationSettingData>, MinimumHardwareBuilder> {

	@Override
	public MinimumHardwareBuilder apply(final Iterable<? extends ResourceAllocationSettingData> from) {
		final MinimumHardwareBuilder builder = new MinimumHardwareBuilder();

		builder.id("minimum");
		
		builder.volumes(transform(filter(from, CIMPredicates.resourceTypeIn(ResourceType.LOGICAL_DISK)),
				new Function<ResourceAllocationSettingData, Volume>() {
			@Override
			public Volume apply(ResourceAllocationSettingData from) {
				return MinimumHardwareBuilderFromResourceAllocations.this.apply(from);
			}
		}));

		builder.ram((int) find(from, CIMPredicates.resourceTypeIn(ResourceType.MEMORY)).getVirtualQuantity().longValue());

		builder.processors(transform(filter(from, CIMPredicates.resourceTypeIn(ResourceType.PROCESSOR)),
				new Function<ResourceAllocationSettingData, Processor>() {
			@Override
			public Processor apply(final ResourceAllocationSettingData rasd) {
				return new Processor(rasd.getVirtualQuantity(), 1);
			}
		}));
		
		builder.reports((int) find(from, CIMPredicates.resourceTypeIn(ResourceType.OTHER)).getVirtualQuantity().longValue());
		
		return builder;		
	}

	public Volume apply(final ResourceAllocationSettingData from) {
		return new VolumeImpl("disk", Volume.Type.LOCAL, from.getVirtualQuantity() == null ? null
				: (float) from.getVirtualQuantity(), null, false, false);
	}

}