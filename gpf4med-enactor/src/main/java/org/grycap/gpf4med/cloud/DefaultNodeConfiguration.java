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

package org.grycap.gpf4med.cloud;

/**
 * Immutable class that contains the default node configuration.
 * @author Erik Torres <ertorser@upv.es>
 */
public final class DefaultNodeConfiguration implements NodeConfiguration {

	/**
	 * Minimum number of CPU cores: 2.
	 */
	public static final double MIN_CORES = 2.0d;

	/**
	 * Minimum RAM in megabytes: 2048.
	 */
	public static final int MIN_RAM_MEGABYTES = 2048;

	/**
	 * Minimum disk space in gigabytes: 8.
	 */
	public static final double MIN_DISK_GIGABYTES = 8.0d;

	@Override
	public double cores() {
		return MIN_CORES;
	}

	@Override
	public int ramMb() {
		return MIN_RAM_MEGABYTES;
	}

	@Override
	public double diskSizeGb() {
		return MIN_DISK_GIGABYTES;
	}

	@Override
	public NodeConfiguration cores(final double cores) {
		throw new UnsupportedOperationException("Updating default values is not supported");
	}

	@Override
	public NodeConfiguration ramMb(final int ramMb) {
		throw new UnsupportedOperationException("Updating default values is not supported");
	}

	@Override
	public NodeConfiguration diskSizeGb(double diskSizeGb) {
		throw new UnsupportedOperationException("Updating default values is not supported");
	}

	/**
	 * Creates an instance of {@link NodeConfiguration} from the default values that can
	 * be updated with values above defaults (values below defaults are ignored).
	 * @return a modifiable version of the default node configuration.
	 */
	public static NodeConfiguration fromDefaults() {
		return new NodeConfiguration() {
			private double cores = MIN_CORES;
			private int ram = MIN_RAM_MEGABYTES;
			private double disk = MIN_DISK_GIGABYTES;
			@Override
			public double cores() {
				return cores;
			}
			@Override
			public int ramMb() {
				return ram;
			}
			@Override
			public double diskSizeGb() {
				return disk;
			}
			@Override
			public NodeConfiguration cores(final double cores) {
				this.cores = Math.max(cores, MIN_CORES);
				return this;
			}
			@Override
			public NodeConfiguration ramMb(final int ramMb) {
				this.ram = Math.max(ramMb, MIN_RAM_MEGABYTES);
				return this;
			}
			@Override
			public NodeConfiguration diskSizeGb(final double diskSizeGb) {
				this.disk = Math.max(diskSizeGb, MIN_DISK_GIGABYTES);
				return this;
			}
		};
	}

}