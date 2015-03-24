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
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FileUtils;
import org.grycap.gpf4med.conf.ConfigurationManager;
import org.grycap.gpf4med.model.BaseType;
import org.grycap.gpf4med.model.Code;
import org.grycap.gpf4med.model.ConceptName;
import org.grycap.gpf4med.model.ConceptName.CodeSchema;
import org.grycap.gpf4med.model.ConceptName.CodeValue;
import org.grycap.gpf4med.model.Container;
import org.grycap.gpf4med.model.Date;
import org.grycap.gpf4med.model.Document;
import org.grycap.gpf4med.model.MeasurementUnits;
import org.grycap.gpf4med.model.Num;
import org.grycap.gpf4med.model.Text;
import org.grycap.gpf4med.model.util.Value;
import org.grycap.gpf4med.security.FileEncryptionProvider;
import org.grycap.gpf4med.util.MimeUtils;
import org.grycap.gpf4med.xml.XmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TRENCADIS report loader. This class uses a StAX parser to load the report from
 * an XML document.
 * @author Erik Torres <ertorser@upv.es>
 */
public class DocumentLoader extends XmlParser<Document> implements DocumentLoaderIf {

	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentLoader.class);

	public static DocumentLoader create(final File file) {
		return new DocumentLoader(file);
	}

	private DocumentLoader(final File file) {
		super(file);
	}

	@Override
	public Document load() throws IOException {
		checkArgument(file != null && file.canRead(), "Uninitialized or invalid file");
		File clearFile = file;		
		final Document document = new Document();
		try {			
			// get input from encrypted file			
			if (!MimeUtils.isTextFile(file) && ConfigurationManager.INSTANCE.getEncryptLocalStorage()) {
				clearFile = new File(file.getPath() + ".tmp");
				final FileEncryptionProvider encryptionProvider = ConfigurationManager.INSTANCE.getFileEncryptionProvider();
				encryptionProvider.decrypt(new FileInputStream(file), new FileOutputStream(clearFile));
			}
			final String filename = clearFile.getCanonicalPath();
			LOGGER.trace("Loading document from file: " + filename);
			// setup input factory
			final XMLInputFactory xmlif = XMLInputFactory.newInstance();
			xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
			xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
			xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
			// setup reader
			final XMLStreamReader xmlr = xmlif.createXMLStreamReader(filename, new FileInputStream(clearFile));
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
						stack.push(document);
						for (int i = 0 ; i < xmlr.getAttributeCount() ; i++) {
							if ("IDOntology".equals(xmlr.getAttributeName(i).toString())) {
								document.setIdOntology(Integer.parseInt(xmlr.getAttributeValue(i)));
							} else if ("IDReport".equals(xmlr.getAttributeName(i).toString())) {
								document.setIdReport(xmlr.getAttributeValue(i));
							} else if ("IDTRENCADISReport".equals(xmlr.getAttributeName(i).toString())) {
								document.setIdTrencadisReport(xmlr.getAttributeValue(i));
							} else if ("DateTimeEnd".equals(xmlr.getAttributeName(i).toString())) {
								document.setDateEnd(xmlr.getAttributeValue(i));
							} else if ("DateTimeStart".equals(xmlr.getAttributeName(i).toString())) {
								document.setDateStart(xmlr.getAttributeValue(i));
							}
						}
					} else if ("CONTAINER".equals(name)) {
						stack.push(new Container());
					} else if ("CONCEPT_NAME".equals(name)) {
						stack.push(new ConceptName());
					} else if ("CODE_VALUE".equals(name)) {
						stack.push(new CodeValue());
					} else if ("CODE_SCHEMA".equals(name)) {
						stack.push(CodeSchema.UNKNOWN);
					} else if ("CODE_MEANING".equals(name)) {
						// skip
					} else if ("CODE_MEANING2".equals(name)) {
						// skip
					} else if ("UNIT_MEASUREMENT".equals(name)) {
						final MeasurementUnits measurementUnits = new MeasurementUnits();
						measurementUnits.setConceptName(new ConceptName());
						stack.push(measurementUnits);
					} else if ("DATE".equals(name)) {
						stack.push(new Date());
					} else if ("TEXT".equals(name)) {
						stack.push(new Text());
					} else if ("NUM".equals(name)) {
						stack.push(new Num());					
					} else if ("CODE".equals(name)) {
						stack.push(new Code());
					} else if ("VALUE".equals(name)) {
						final Value value = new Value();
						value.setConceptName(new ConceptName());
						stack.push(value);
					} else if ("CHILDREN".equals(name)) {
						parent = stack.peek();
						checkState(parent instanceof Container, "Unsupported parent type found: " + parent.getClass().getCanonicalName() 
								+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
					} else {
						throw new IllegalStateException("Unsupported field found: " + name + ", event " + getEventTypeString(eventType));
					}
					break;
				case XMLEvent.END_ELEMENT:
					name = xmlr.getName().toString();
					if ("DICOM_SR".equals(name)) {
						element = stack.pop();
						checkState(stack.isEmpty() && element instanceof Document, "Malformed XML, event " + getEventTypeString(eventType));												
					} else if ("CONTAINER".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						if (parent instanceof Document) {
							((Document)parent).setContainer((Container)element);
						} else if (parent instanceof Container) {
							((Container)parent).getChildren().add((Container)element);						
						} else {
							throw new IllegalStateException("Unsupported parent type found: " + parent.getClass().getCanonicalName() 
									+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
						}
					} else if ("CONCEPT_NAME".equals(name)) {
						element = stack.pop();
						parent = stack.peek();						
						((BaseType)parent).setConceptNameValue((ConceptName)element);
					} else if ("CODE_VALUE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();						
						if (parent instanceof ConceptName) {
							((ConceptName)parent).setCodeValue((CodeValue)element);
						} else if (parent instanceof MeasurementUnits) {
							((MeasurementUnits)parent).getConceptName().setCodeValue((CodeValue)element);
						} else if (parent instanceof Value) {
							((Value)parent).getConceptName().setCodeValue((CodeValue)element);
						} else {
							throw new IllegalStateException("Unsupported parent type found: " + parent.getClass().getCanonicalName() 
									+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
						}
					} else if ("CODE_SCHEMA".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						if (parent instanceof ConceptName) {
							((ConceptName)parent).setCodeSchema((CodeSchema)element);
						} else if (parent instanceof MeasurementUnits) {
							((MeasurementUnits)parent).getConceptName().setCodeSchema((CodeSchema)element);
						} else if (parent instanceof Value) {
							((Value)parent).getConceptName().setCodeSchema((CodeSchema)element);
						} else {
							throw new IllegalStateException("Unsupported parent type found: " + parent.getClass().getCanonicalName() 
									+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
						}
					} else if ("CODE_MEANING".equals(name)) {
						// skip
					} else if ("CODE_MEANING2".equals(name)) {
						// skip
					} else if ("UNIT_MEASUREMENT".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((BaseType)parent).setMeasurementUnits((MeasurementUnits)element);										
					} else if ("DATE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Container)parent).getChildren().add((Date)element);
					} else if ("TEXT".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Container)parent).getChildren().add((Text)element);
					} else if ("NUM".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Container)parent).getChildren().add((Num)element);				
					} else if ("CODE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						((Container)parent).getChildren().add((Code)element);
					} else if ("VALUE".equals(name)) {
						element = stack.pop();
						parent = stack.peek();
						if (parent instanceof Date) {
							((Date)parent).setValue(Date.valueFromString(((Value)element).getValue()));
						} else if (parent instanceof Text) {
							((Text)parent).setValue(((Value)element).getValue());
						} else if (parent instanceof Num) {
							((Num)parent).setValue(Num.valueFromString(((Value)element).getValue()));
						} else if (parent instanceof Code) {
							((Code)parent).setValue(((Value)element).getConceptName());
						} else {
							throw new IllegalStateException("Unsupported parent type found: " + parent.getClass().getCanonicalName() 
									+ ", while trying to process: " + name + ", event " + getEventTypeString(eventType));
						}
					} else if ("CHILDREN".equals(name)) {
						parent = stack.peek();
						checkState(parent instanceof Container, "Unsupported parent type found: " + parent.getClass().getCanonicalName() 
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
					} else if (element instanceof Value) {
						((Value)element).setValue(xmlr.getText());
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
			throw new IOException("Failed to load document", e);
		} finally {
			if (clearFile != file) {
				FileUtils.deleteQuietly(clearFile);
			}
		}
		return document;
	}

}