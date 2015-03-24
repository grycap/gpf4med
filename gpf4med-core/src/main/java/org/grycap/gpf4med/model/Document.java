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
 * TRENCADIS document.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Document {

	private String dateStart;
	private String dateEnd;
	private Integer idOntology;
	private String idReport;
	private String idTrencadisReport;
	private Container container;

	public Document() { }

	public String getDateStart() {
		return dateStart;
	}

	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Integer getIdOntology() {
		return idOntology;
	}

	public void setIdOntology(final Integer idOntology) {
		this.idOntology = checkNotNull(idOntology, "Uninitialized ontology");
	}

	public String getIdReport() {
		return idReport;
	}

	public void setIdReport(String idReport) {
		this.idReport = checkNotNull(idReport, "Uninitialized report identifier");
	}

	public String getIdTrencadisReport() {
		return idTrencadisReport;
	}

	public void setIdTrencadisReport(String idTrencadisReport) {
		this.idTrencadisReport = checkNotNull(idTrencadisReport, "Uninitialized TRENCADIS report identifier");
	}


	public Container getContainer() {
		return container;
	}

	public void setContainer(final Container container) {
		this.container = checkNotNull(container, "Uninitialized container");
	}	

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("DateTimeStart", dateStart)
				.add("DateTimeEnd", dateEnd)
				.add("idOntology", idOntology)
				.add("idReport", idReport)
				.add("idTRENCADISReport", idTrencadisReport)
				.add("container", container)
				.toString();
				
	}

}