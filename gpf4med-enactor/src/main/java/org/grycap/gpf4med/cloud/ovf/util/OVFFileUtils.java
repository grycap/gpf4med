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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;

import javax.annotation.Nullable;

import org.grycap.gpf4med.cloud.NodeConfiguration;
import org.grycap.gpf4med.cloud.ovf.MinimumHardware;
import org.grycap.gpf4med.cloud.ovf.MinimumHardwareBuilderFromResourceAllocations;
import org.grycap.gpf4med.cloud.ovf.MinimumVirtualHardwareHandler;
import org.grycap.gpf4med.cloud.ovf.wrapper.ProcessorListWrapper;
import org.grycap.gpf4med.cloud.ovf.wrapper.VolumeListWrapper;
import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.ovf.VirtualHardwareSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Utility class to deal with OVF file, such as files describing minimum hardware requirements.
 * @author Erik Torres <ertorser@upv.es>
 */
public final class OVFFileUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(OVFFileUtils.class);
	
	/**
	 * Compute the number of servers that are necessary to handle a set of reports
	 * with a given size.
	 * @param nodeConfiguration configuration that will be applied to the servers.
	 * @param numReports maximum number of reports that will be used by a single server.
	 * @param type resource type.
	 * @return the number of servers that are necessary to handle a set of reports of 
	 *         the specified size with the specified configuration.
	 */
	public static int numServers(final NodeConfiguration nodeConfiguration, final int numReports,
			final Class<?> type) {
		checkArgument(nodeConfiguration != null, "Uninitialized node configuration");
		checkArgument(type != null, "Uninitialized type");
		// load minimum hardware requirements
		final String ovfFilename = type.getSimpleName() + ".ovf";
		final MinimumHardware hardware = load(ovfFilename, type);
		checkState(hardware != null, "Cannot load minimum hardware requirements from your OVF file: " 
				+ ovfFilename);
		LOGGER.trace(" >> Hardware minimum requirement found: " + hardware.toString());		
		// compute the number of servers
		return HardwareMatcher.start()
				.consider(ProcessorListWrapper.wrap(hardware.getProcessors()), nodeConfiguration.cores())
				.consider(hardware.getRam(), nodeConfiguration.ramMb())
				.consider(VolumeListWrapper.wrap(hardware.getVolumes()), nodeConfiguration.diskSizeGb())
				.numServers(hardware.getReports(), numReports);		
	}

	/**
	 * Loads minimum hardware requirements from an OVF file.
	 * @param ovfFilename OVF filename.
	 * @param classLoader optional class loader. If not present, the current class loader
	 *        will be used to load the file.
	 * @return the minimum hardware requirements loaded from an OVF file.
	 */
	public static MinimumHardware load(final String ovfFilename, final @Nullable Class<?> type) {		
		final Injector injector = Guice.createInjector(new SaxParserModule() {
			public void configure() {
				super.configure();
			}
		});
		final ParseSax.Factory factory = injector.getInstance(ParseSax.Factory.class);
		final InputStream is = (type != null ? type : OVFFileUtils.class).getResourceAsStream("/" + ovfFilename);
		final VirtualHardwareSection virtualHardwareSection = factory.create(injector.getInstance(MinimumVirtualHardwareHandler.class)).parse(is);
		checkState(virtualHardwareSection != null, "Cannot find virtual hardware section in your OVF file: " + ovfFilename);
		if (LOGGER.isTraceEnabled()) {
			for (final ResourceAllocationSettingData item : virtualHardwareSection.getItems()) {
				LOGGER.trace(" >> RASD found: " + item.toString());
			}
		}
		final MinimumHardwareBuilderFromResourceAllocations builderFunction = injector.getInstance(MinimumHardwareBuilderFromResourceAllocations.class);
		return builderFunction.apply(virtualHardwareSection.getItems()).build();		
	}

}