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

package org.grycap.gpf4med;

import static org.grycap.gpf4med.cloud.DefaultNodeConfiguration.fromDefaults;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.grycap.gpf4med.cloud.NodeConfiguration;
import org.grycap.gpf4med.cloud.ovf.MinimumHardware;
import org.grycap.gpf4med.cloud.ovf.util.HardwareMatcher;
import org.grycap.gpf4med.cloud.ovf.util.OVFFileUtils;
import org.grycap.gpf4med.cloud.ovf.wrapper.ProcessorListWrapper;
import org.grycap.gpf4med.cloud.ovf.wrapper.VolumeListWrapper;
import org.junit.Test;

/**
 * Tests OVF file format parsing and handling.
 * @author Erik Torres <ertorser@upv.es>
 */
public class OVFTest {

	@Test
	public void test() {
		System.out.println("OVFTest.test()");
		try {			
			// test OVF file loading and parsing			
			final MinimumHardware hardware = OVFFileUtils.load("gpf4med-ovf.xml", getClass());
			assertThat("hardware is not null", hardware, notNullValue());
			// test hardware matching
			final NodeConfiguration nodeConfiguration = fromDefaults();
			int numReports = 1000;
			int numServers = HardwareMatcher.start()
					.consider(ProcessorListWrapper.wrap(hardware.getProcessors()), nodeConfiguration.cores())
					.consider(hardware.getRam(), nodeConfiguration.ramMb())
					.consider(VolumeListWrapper.wrap(hardware.getVolumes()), nodeConfiguration.diskSizeGb())
					.numServers(hardware.getReports(), numReports);
			assertThat("hardware matching coincides, combination: CPU=1/2, RAM=512/2048, Disk=20/8, Reports=1000/1000", numServers, equalTo(3));
			numReports = 10000;
			numServers = HardwareMatcher.start()
					.consider(ProcessorListWrapper.wrap(hardware.getProcessors()), nodeConfiguration.cores())
					.consider(hardware.getRam(), nodeConfiguration.ramMb())
					.consider(VolumeListWrapper.wrap(hardware.getVolumes()), nodeConfiguration.diskSizeGb())
					.numServers(hardware.getReports(), numReports);
			assertThat("hardware matching coincides, combination: CPU=1/2, RAM=512/2048, Disk=20/8, Reports=10000/1000", numServers, equalTo(30));
			numReports = 1000;
			nodeConfiguration.diskSizeGb(20.0d);
			numServers = HardwareMatcher.start()
					.consider(ProcessorListWrapper.wrap(hardware.getProcessors()), nodeConfiguration.cores())
					.consider(hardware.getRam(), nodeConfiguration.ramMb())
					.consider(VolumeListWrapper.wrap(hardware.getVolumes()), nodeConfiguration.diskSizeGb())
					.numServers(hardware.getReports(), numReports);
			assertThat("hardware matching coincides, combination: CPU=1/2, RAM=512/2048, Disk=20/20, Reports=1000/1000", numServers, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			fail("OVFTest.test() failed: " + e.getMessage());
		} finally {			
			System.out.println("OVFTest.test() has finished");
		}
	}

}