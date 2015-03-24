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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.model.BaseTemplate;
import org.grycap.gpf4med.model.CodeTemplate;
import org.grycap.gpf4med.model.ConceptName;
import org.grycap.gpf4med.model.ConceptName.CodeSchema;
import org.grycap.gpf4med.model.ConceptName.CodeValue;
import org.grycap.gpf4med.model.ContainerTemplate;
import org.grycap.gpf4med.model.DateTemplate;
import org.grycap.gpf4med.model.DocumentTemplate;
import org.grycap.gpf4med.model.MeasurementUnits;
import org.grycap.gpf4med.model.NumTemplate;
import org.grycap.gpf4med.model.Properties;
import org.grycap.gpf4med.model.Properties.Cardinality;
import org.grycap.gpf4med.model.Properties.ConditionType;
import org.grycap.gpf4med.model.Properties.ConditionTypeEnum;
import org.grycap.gpf4med.model.TextTemplate;
import org.grycap.gpf4med.util.DocumentUtils;
import org.grycap.gpf4med.xml.XmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TRENCADIS template loader. This class uses a StAX parser to load the template from
 * an XML document.
 * @author Erik Torres <ertorser@upv.es>
 */
public class TemplateLoader extends XmlParser<DocumentTemplate> implements TemplateLoaderIf {

	private final static Logger LOGGER = LoggerFactory.getLogger(TemplateLoader.class);

	public static TemplateLoader create(final File file) {
		return new TemplateLoader(file);
	}

	private TemplateLoader(final File file) {
		super(file);
	}

	@Override
	public DocumentTemplate load() throws IOException {		
		checkArgument(file != null, "Uninitialized or invalid file");
		final DocumentTemplate template = new DocumentTemplate();
		try {
			final String filename = file.getCanonicalPath();
			LOGGER.trace("Loading template from file: " + filename);
			// setup input factory
			final XMLInputFactory xmlif = XMLInputFactory.newInstance();
			xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
			xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,Boolean.FALSE);
			xmlif.setProperty(XMLInputFactory.IS_COALESCING , Boolean.FALSE);
			// setup reader
			final XMLStreamReader xmlr = xmlif.createXMLStreamReader(filename, new FileInputStream(file));
			int eventType = xmlr.getEventType();			
			// read the document
			String name = null;
			Object element, parent = null;
			while (xmlr.hasNext()) {
				eventType = xmlr.next();
				switch (eventType) {
				case XMLEvent.START_ELEMENT:
					name = xmlr.getName().toString();
					if ("DICOM_SR".equals(name)) {
						stack.push(template);
						for (int i = 0 ; i < xmlr.getAttributeCount() ; i++) {
							if ("IDOntology".equals(xmlr.getAttributeName(i).toString())) {
								template.setIdOntology(Integer.parseInt(xmlr.getAttributeValue(i)));
							} else if ("Description".equals(xmlr.getAttributeName(i).toString())) {
								template.setDescription(xmlr.getAttributeValue(i));
							}
						}
					} else if ("CONTAINER".equals(name)) {
						stack.push(new ContainerTemplate());
					} else if ("CONCEPT_NAME".equals(name)) {
						stack.push(new ConceptName());
					} else if ("CODE_VALUE".equals(name)) {
						stack.push(new CodeValue());
					} else if ("CODE_SCHEMA".equals(name)) {
						stack.push(CodeSchema.UNKNOWN);
					} else if ("CODE_MEANING".equals(name)) {
						stack.push("");
					} else if ("CODE_MEANING2".equals(name)) {
						stack.push("");
					} else if ("PROPERTIES".equals(name)) {
						stack.push(new Properties());
					} else if ("CARDINALITY".equals(name)) {
						final Cardinality cardinality = new Cardinality();
						stack.push(cardinality);
						for (int i = 0 ; i < xmlr.getAttributeCount() ; i++) {
							if ("min".equals(xmlr.getAttributeName(i).toString())) {
								cardinality.setMin(Integer.parseInt(xmlr.getAttributeValue(i)));
							} else if ("max".equals(xmlr.getAttributeName(i).toString())) {
								cardinality.setMax(Integer.parseInt(xmlr.getAttributeValue(i)));
							}
						}
					} else if ("CONDITION_TYPE".equals(name)) {
						final ConditionType conditionType = new ConditionType();
						stack.push(conditionType);
						for (int i = 0 ; i < xmlr.getAttributeCount() ; i++) {
							if ("type".equals(xmlr.getAttributeName(i).toString())) {
								conditionType.setConditionType(ConditionTypeEnum.fromValue(xmlr.getAttributeValue(i)));
							} else if ("xpath_expresion".equals(xmlr.getAttributeName(i).toString())) {
								conditionType.setXpathExpression(xmlr.getAttributeValue(i));
							}
						}
					} else if ("INTEGRITY_RESTRICTIONS".equals(name)) {
						stack.push("");
					} else if ("DEFAULT_VALUE".equals(name)) {
						String defaultValue = null;
						for (int i = 0 ; i < xmlr.getAttributeCount() ; i++) {
							if ("value".equals(xmlr.getAttributeName(i).toString())) {
								defaultValue = xmlr.getAttributeValue(i);
							}
						}
						stack.push(StringUtils.trimToEmpty(defaultValue));
					} else if ("UNIT_MEASUREMENT".equals(name)) {
						stack.push(new MeasurementUnits());
					} else if ("DEFAULT_CODE_VALUE".equals(name)) {
						final ConceptName defaultConceptName = new ConceptName();
						stack.push(defaultConceptName);
						for (int i = 0 ; i < xmlr.getAttributeCount() ; i++) {
							if ("code_schema".equals(xmlr.getAttributeName(i).toString())) {
								defaultConceptName.setCodeSchema(CodeSchema.fromValue(xmlr.getAttributeValue(i)));
							} else if ("code_value".equals(xmlr.getAttributeName(i).toString())) {
								final CodeValue codeValue = new CodeValue();
								codeValue.setValue(xmlr.getAttributeValue(i));
								defaultConceptName.setCodeValue(codeValue);
							} else if ("code_meaning".equals(xmlr.getAttributeName(i).toString())) {
								defaultConceptName.setCodeMeaning(xmlr.getAttributeValue(i));
							} else if ("code_meaning2".equals(xmlr.getAttributeName(i).toString())) {
								defaultConceptName.setCodeMeaning2(xmlr.getAttributeValue(i));
							}
						}
					} else if ("CODE_VALUES".equals(name)) {
						parent = stack.peek();
						checkState(parent instanceof Properties, "Unsupported parent type found: " + parent.getClass().getCanonicalName() 
								+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
					} else if ("DATE".equals(name)) {
						stack.push(new DateTemplate());
					} else if ("TEXT".equals(name)) {
						stack.push(new TextTemplate());
					} else if ("NUM".equals(name)) {
						stack.push(new NumTemplate());					
					} else if ("CODE".equals(name)) {
						stack.push(new CodeTemplate());
					} else if ("CHILDREN".equals(name)) {
						parent = stack.peek();
						checkState(parent instanceof ContainerTemplate, "Unsupported parent type found: " + parent.getClass().getCanonicalName() 
								+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
					} else {
						throw new IllegalStateException("Unsupported field found: " + name + ", event " + getEventTypeString(eventType));
					}
					break;
				case XMLEvent.END_ELEMENT:
					name = xmlr.getName().toString();
					if ("DICOM_SR".equals(name)) {
						element = stack.pop();
						checkState(stack.isEmpty() && element instanceof DocumentTemplate, "Malformed XML, event " + getEventTypeString(eventType));												
					} else if ("CONTAINER".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						if (parent instanceof DocumentTemplate) {
							((DocumentTemplate)parent).setContainerTemplate((ContainerTemplate)element);
						} else if (parent instanceof ContainerTemplate) {
							((ContainerTemplate)parent).getChildrenTemplate().add((ContainerTemplate)element);						
						} else {
							throw new IllegalStateException("Unsupported parent type found: " + parent.getClass().getCanonicalName() 
									+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
						}
					} else if ("CONCEPT_NAME".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						if (parent instanceof Properties) {
							if (((Properties)parent).getCodeValues() == null) {
								((Properties)parent).setCodeValues(new ArrayList<ConceptName>());
							}
							((Properties)parent).getCodeValues().add((ConceptName)element);
						} else if (parent instanceof MeasurementUnits) {
							((MeasurementUnits)parent).setConceptName((ConceptName)element);
						} else {
							((BaseTemplate)parent).setConceptName((ConceptName)element);
						}
					} else if ("CODE_VALUE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((ConceptName)parent).setCodeValue((CodeValue)element);
					} else if ("CODE_SCHEMA".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((ConceptName)parent).setCodeSchema((CodeSchema)element);
					} else if ("CODE_MEANING".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((ConceptName)parent).setCodeMeaning((String)element);
					} else if ("CODE_MEANING2".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((ConceptName)parent).setCodeMeaning2((String)element);
					} else if ("PROPERTIES".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						if (parent instanceof DocumentTemplate) {
							((DocumentTemplate)parent).getContainerTemplate().setProperties((Properties)element);
						} else if (parent instanceof ContainerTemplate) {
							((ContainerTemplate)parent).setProperties((Properties)element);
						} else if (parent instanceof TextTemplate) {
							((TextTemplate)parent).setProperties((Properties)element);
						} else if (parent instanceof NumTemplate) {
							((NumTemplate)parent).setProperties((Properties)element);
						} else if (parent instanceof DateTemplate) {
							((DateTemplate)parent).setProperties((Properties)element);						
						} else if (parent instanceof CodeTemplate) {
							((CodeTemplate)parent).setProperties((Properties)element);
						} else {
							throw new IllegalStateException("Unsupported parent type found: " + parent.getClass().getCanonicalName() 
									+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
						}
					} else if ("CARDINALITY".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Properties)parent).setCardinality((Cardinality)element);
					} else if ("CONDITION_TYPE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Properties)parent).setConditionType((ConditionType)element);
					} else if ("INTEGRITY_RESTRICTIONS".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Properties)parent).setIntegrityRestriction(StringUtils.trimToNull((String)element));
					} else if ("DEFAULT_VALUE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Properties)parent).setDefaultValue(StringUtils.trimToNull((String)element));
					} else if ("UNIT_MEASUREMENT".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Properties)parent).setMeasurementUnits((MeasurementUnits)element);
					} else if ("DEFAULT_CODE_VALUE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						if (!DocumentUtils.isEmpty((ConceptName)element)) {
							((Properties)parent).setDefaultConceptName((ConceptName)element);
						}
					} else if ("CODE_VALUES".equals(name)) {
						parent = stack.peek();
						checkState(parent instanceof Properties, "Unsupported parent type found: " + parent.getClass().getCanonicalName() 
								+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
					} else if ("DATE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((ContainerTemplate)parent).getChildrenTemplate().add((DateTemplate)element);
					} else if ("TEXT".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((ContainerTemplate)parent).getChildrenTemplate().add((TextTemplate)element);
					} else if ("NUM".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((ContainerTemplate)parent).getChildrenTemplate().add((NumTemplate)element);				
					} else if ("CODE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((ContainerTemplate)parent).getChildrenTemplate().add((CodeTemplate)element);
					} else if ("CHILDREN".equals(name)) {
						parent = stack.peek();
						checkState(parent instanceof ContainerTemplate, "Unsupported parent type found: " + parent.getClass().getCanonicalName() 
								+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
					} else {
						throw new IllegalStateException("Unsupported field found: " + name + ", event " + getEventTypeString(eventType));
					}
					break;
				case XMLEvent.CHARACTERS:
					element = stack.pop();
					if (element instanceof CodeValue) {
						((CodeValue)element).setValue(xmlr.getText());
					} else if (element instanceof CodeSchema) {
						element = CodeSchema.fromValue(xmlr.getText());
					} else if (element instanceof String) {
						element = xmlr.getText();
					}
					stack.push(element);					
					break;
				case XMLEvent.END_DOCUMENT:
					checkState(stack.isEmpty(), "Malformed XML, event " + getEventTypeString(eventType));
					break;
				default:
					LOGGER.trace("Ignored event: " + getEventTypeString(eventType));
					break;
				}
			}
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			throw new IOException("Failed to load template", e);
		}
		return template;
	}	

}