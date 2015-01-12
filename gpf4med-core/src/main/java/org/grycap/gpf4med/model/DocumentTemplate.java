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

package org.grycap.gpf4med.model;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Optional;

/**
 * TRENCADIS document template.
 * @author Erik Torres <ertorser@upv.es>
 */
public class DocumentTemplate {

	private Integer idOntology;
	private Optional<String> description = Optional.absent();
	private ContainerTemplate containerTemplate;

	public DocumentTemplate() { }

	public Integer getIdOntology() {
		return idOntology;
	}

	public void setIdOntology(final Integer idOntology) {
		this.idOntology = checkNotNull(idOntology, "Uninitialized ontology");
	}

	public @Nullable String getDescription() {
		return description.orNull();
	}

	public void setDescription(final @Nullable String description) {
		this.description = Optional.fromNullable(description);
	}

	public ContainerTemplate getContainerTemplate() {
		return containerTemplate;
	}	

	public void setContainerTemplate(final ContainerTemplate containerTemplate) {
		this.containerTemplate = checkNotNull(containerTemplate, "Uninitialized container");
	}	

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("idOntology", idOntology)
				.add("description", "\"" + StringUtils.trimToEmpty(description.orNull()) + "\"")
				.add("containerTemplate", containerTemplate)
				.toString();
	}
	
}