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

package org.grycap.gpf4med.cloud.ovf.util;

import static com.google.common.collect.Lists.transform;

import org.grycap.gpf4med.cloud.ovf.wrapper.ProcessorListWrapper;
import org.grycap.gpf4med.cloud.ovf.wrapper.VolumeListWrapper;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

/**
 * Finds the number of servers configured in an specific manner that are necessary 
 * to handle a set of reports.
 * @author Erik Torres <ertorser@upv.es>
 */
public class HardwareMatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(HardwareMatcher.class);

	private int numServers;

	private HardwareMatcher() {
		numServers = 1;
	}

	public static HardwareMatcher start() {
		return new HardwareMatcher();
	}

	/**
	 * Computes the minimum number of servers that can handle a set of reports.
	 * @param maximum the maximum number of reports that can be handled with the ideal
	 *        configuration.
	 * @param size the actual size of the reports dataset.
	 * @return the minimum number of servers that can handle a set of reports.
	 */
	public int numServers(final int maximum, final int size) {
		return Math.max(1, size / (maximum / numServers));
	}

	/**
	 * Considers a requirement in the processing power (cores of 1 GHz each).
	 * @param processors requirements about processing power.
	 * @param cores nodes will be configured with this number of cores.
	 * @return a reference to this class.
	 */
	public HardwareMatcher consider(final ProcessorListWrapper processors, final double cores) {
		numServers = Math.max(numServers, (int) Math.max(1.0d, Math.ceil(Ordering.<Double> natural().max(transform(processors.asList(), new Function<Processor, Double>() {
			@Override
			public Double apply(final Processor item) {
				LOGGER.trace("Processing power requirement: " + item.getCores() + "/" + cores);
				return item.getCores() / cores;
			}
		})))));
		LOGGER.trace("Number of servers after considering processing power requirements: " + numServers);
		return this;
	}

	/**
	 * Considers a requirement in the disk size (in gigabytes).
	 * @param volumes requirements about disk size.
	 * @param size nodes will be configured with this disk size.
	 * @return a reference to this class.
	 */
	public HardwareMatcher consider(final VolumeListWrapper volumes, final double size) {
		numServers = Math.max(numServers, (int) Math.max(1.0d, Math.ceil(Ordering.<Double> natural().max(transform(volumes.asList(), new Function<Volume, Double>() {
			@Override
			public Double apply(final Volume item) {
				LOGGER.trace("Disk size requirement: " + item.getSize() + "/" + size);
				return item.getSize() / size;
			}			
		})))));
		LOGGER.trace("Number of servers after considering disk size requirements: " + numServers);
		return this;
	}

	/**
	 * Considers a quantifiable, finite requirement (e.g. RAM in megabytes).
	 * @param requirement the requirement.
	 * @param configuration nodes will use this configuration.
	 * @return a reference to this class.
	 */
	public HardwareMatcher consider(final int requirement, final int configuration) {
		LOGGER.trace("Integer requirement: " + requirement + "/" + configuration);
		numServers = Math.max(numServers, (int) Math.max(1.0d, Math.ceil(requirement / configuration)));
		LOGGER.trace("Number of servers after considering integer requirement: " + numServers);
		return this;
	}	

}