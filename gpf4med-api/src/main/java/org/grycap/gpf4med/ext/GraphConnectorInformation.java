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

package org.grycap.gpf4med.ext;

import static com.google.common.base.MoreObjects.toStringHelper;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

/**
 * Provides information about a specific connector.
 * @author Erik Torres <ertorser@upv.es>
 */
public class GraphConnectorInformation {

	// information from the interface
	private final String path;
	private final Class<?> restResourceDefinition;
	private final Class<?> restResourceImplementation;
	// information from the plugin
	private final String version;
	private final Optional<String> author;	
	private final Optional<String> packageName;
	private final Optional<String> description;

	public GraphConnectorInformation(final String path, final Class<?> restResourceDefinition, 
			final Class<?> restResourceImplementation, final String version, 
			final @Nullable String author, final @Nullable String packageName, final @Nullable String description) {
		this.path = path;
		this.restResourceDefinition = restResourceDefinition;
		this.restResourceImplementation = restResourceImplementation;
		this.version = version;
		this.author = Optional.fromNullable(author);		
		this.packageName = Optional.fromNullable(packageName);
		this.description = Optional.fromNullable(description);
	}

	public String getPath() {
		return path;
	}

	public Class<?> getRestResourceDefinition() {
		return restResourceDefinition;
	}

	public Class<?> getRestResourceImplementation() {
		return restResourceImplementation;
	}

	public String getVersion() {
		return version;
	}

	public @Nullable String getAuthor() {
		return author.orNull();
	}

	public @Nullable String getPackageName() {
		return packageName.orNull();
	}

	public @Nullable String getDescription() {
		return description.orNull();
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("path", path)
				.add("restResourceDefinition", restResourceDefinition)
				.add("restResourceImplementation", restResourceImplementation)
				.add("version", version)
				.add("author", author.orNull())
				.add("packageName", packageName.orNull())
				.add("description", description.orNull())				
				.toString();		
	}

}