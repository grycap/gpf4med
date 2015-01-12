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

package org.grycap.gpf4med.model.util;

/**
 * Available template.
 * @author Erik Torres <ertorser@upv.es>
 */
public class AvailableTemplate {

	private int idOntology;
	private String description;
	private String codeValue;
	private String codeSchema;
	private String codeMeaning;
	private String codeMeaning2;
	
	public AvailableTemplate() { }

	public int getIdOntology() {
		return idOntology;
	}

	public void setIdOntology(final int idOntology) {
		this.idOntology = idOntology;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(final String codeValue) {
		this.codeValue = codeValue;
	}

	public String getCodeSchema() {
		return codeSchema;
	}

	public void setCodeSchema(final String codeSchema) {
		this.codeSchema = codeSchema;
	}

	public String getCodeMeaning() {
		return codeMeaning;
	}

	public void setCodeMeaning(final String codeMeaning) {
		this.codeMeaning = codeMeaning;
	}

	public String getCodeMeaning2() {
		return codeMeaning2;
	}

	public void setCodeMeaning2(final String codeMeaning2) {
		this.codeMeaning2 = codeMeaning2;
	}	
	
}