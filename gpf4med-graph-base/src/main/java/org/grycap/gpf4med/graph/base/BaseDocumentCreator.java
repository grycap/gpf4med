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

package org.grycap.gpf4med.graph.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.graph.base.model.LabelTypes;
import org.grycap.gpf4med.graph.base.model.RelTypes;
import org.grycap.gpf4med.model.BaseType;
import org.grycap.gpf4med.model.Code;
import org.grycap.gpf4med.model.ConceptName;
import org.grycap.gpf4med.model.Container;
import org.grycap.gpf4med.model.Date;
import org.grycap.gpf4med.model.Document;
import org.grycap.gpf4med.model.DocumentTemplate;
import org.grycap.gpf4med.model.Num;
import org.grycap.gpf4med.model.Text;
import org.grycap.gpf4med.util.TemplateUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.UniqueFactory;

/**
 * Base class that provides general methods for loading nodes in the graph.
 * @author Erik Torres <ertorser@upv.es>
 */
public abstract class BaseDocumentCreator {

	public static final String ID_PROPERTY = "id";
	public static final String TYPE_PROPERTY = "type";
	public static final String DESCRIPTION_PROPERTY = "description";
	public static final String ACQUISITION_DATE_PROPERTY = "acquisition_date";
	public static final String ID_REPORT_PROPERTY = "id_report";
	public static final String ID_PATIENT_PROPERTY = "id_patient";
	public static final String ID_LESION_TYPE_PROPERTY = "id_type";
	public static final String BI_RADS_CLASSIFICATION = "class";

	public static final String PATIENT_NODE = "Patient";
	public static final String MODALITY_NODE = "Modality";
	public static final String BI_RADS_NODE = "BI-RADS";
	public static final String TUMOUR_LOCATION_NODE = "Tumour Location";
	public static final String FINDING_NODE = "Finding";

	public BaseDocumentCreator() { }

	public Node getOrCreatePatient(final Transaction tx, final GraphDatabaseService graphDb, final Text patient) {
		checkArgument(patient != null && StringUtils.isNotBlank(patient.getValue()), 
				"Uninitialized or invalid patient");
		final ConceptName conceptName = patient.getConceptNameValue();
		final String id = conceptName.id();
		checkState(StringUtils.isNotBlank(id), "Patient concept name is invalid");
		final UniqueFactory<Node> uniqueFactory = new UniqueFactory.UniqueNodeFactory(graphDb, PATIENT_NODE) {
			@Override
			protected void initialize(final Node created, final Map<String, Object> properties) {
				created.setProperty(ID_PROPERTY, properties.get(ID_PROPERTY));	
			}
		};
		final Node node = uniqueFactory.getOrCreate(ID_PROPERTY, id);
		if (!node.hasLabel(LabelTypes.PATIENT)) {
			node.addLabel(LabelTypes.PATIENT);
		}
		return node;
	}

	public Node getOrCreateModality(final Transaction tx, final GraphDatabaseService graphDb, final DocumentTemplate template) {
		checkArgument(template != null && template.getContainerTemplate() != null
				&& template.getContainerTemplate().getConceptName() != null, 
				"Uninitialized or invalid template");
		final ConceptName conceptName = template.getContainerTemplate().getConceptName();
		final String id = conceptName.id();
		checkState(StringUtils.isNotBlank(id), "Template concept name is invalid");
		final UniqueFactory<Node> uniqueFactory = new UniqueFactory.UniqueNodeFactory(graphDb, MODALITY_NODE) {
			@Override
			protected void initialize(final Node created, final Map<String, Object> properties) {
				created.setProperty(ID_PROPERTY, properties.get(ID_PROPERTY));	
			}
		};
		final Node node = uniqueFactory.getOrCreate(ID_PROPERTY, id);
		if (!node.hasLabel(LabelTypes.MODALITY)) {
			node.addLabel(LabelTypes.MODALITY);
		}
		if (!node.hasProperty(TYPE_PROPERTY) && StringUtils.isNotBlank(conceptName.getCodeMeaning2())) {
			node.setProperty(TYPE_PROPERTY, conceptName.getCodeMeaning2());
		}
		return node;
	}

	public Node getOrCreateDICOMReference(final Transaction tx, final GraphDatabaseService graphDb, 
			final Text text, final Node parent) {
		checkArgument(text != null && text.getConceptNameValue() != null, "Uninitialized or invalid text");
		final ConceptName conceptName = text.getConceptNameValue();
		final String id = conceptName.id();
		checkState(StringUtils.isNotBlank(id), "Text concept name is invalid");
		final String ref = StringUtils.trimToNull(text.getValue());
		checkState(StringUtils.isNotBlank(ref), "uninitialized or invalid reference");
		final UniqueFactory<Node> uniqueFactory = new UniqueFactory.UniqueNodeFactory(graphDb, PATIENT_NODE) {
			@Override
			protected void initialize(final Node created, final Map<String, Object> properties) {
				created.setProperty(ID_PROPERTY, properties.get(ID_PROPERTY));	
			}
		};
		final Node node = uniqueFactory.getOrCreate(ID_PROPERTY, id + ":" + ref);
		if (!node.hasLabel(LabelTypes.DICOM_REF)) {
			node.addLabel(LabelTypes.DICOM_REF);
		}
		parent.createRelationshipTo(node, RelTypes.REFERS);
		return node;		
	}

	public Node createRadiologicalStudy(final Transaction tx, final GraphDatabaseService graphDb, final Document document,
			final Node modality) {
		checkArgument(document != null && document.getContainer() != null
				&& document.getContainer().getConceptNameValue() != null, 
				"Uninitialized or invalid document");
		final ConceptName conceptName = document.getContainer().getConceptNameValue();
		final String id = conceptName.id();
		checkState(StringUtils.isNotBlank(id), "Document concept name is invalid");
		final Node studyNode = graphDb.createNode();
		studyNode.addLabel(LabelTypes.RADIOLOGICAL_STUDY);
		studyNode.setProperty(ID_PROPERTY, id);
		studyNode.createRelationshipTo(modality, RelTypes.IS);
		modality.createRelationshipTo(studyNode, RelTypes.HAS);
		final Text identifier = getStudyId(document);
		if (identifier != null) {
			studyNode.setProperty(ID_REPORT_PROPERTY, identifier.getValue());
		}
		final Text patient = getPatient(document);
		if (patient != null) {
			final Node patientNode = getOrCreatePatient(tx, graphDb, patient);
			patientNode.setProperty(ID_PATIENT_PROPERTY, patient.getValue());
			final Relationship relationship = patientNode.createRelationshipTo(studyNode, RelTypes.HAS);
			final Date date = getDate(document);
			if (date != null) {
				relationship.setProperty(ACQUISITION_DATE_PROPERTY, Date.stringFromValue(date.getValue(), Locale.ENGLISH));
			}
			studyNode.createRelationshipTo(patientNode, RelTypes.FROM);
		}
		return studyNode;
	}

	public Node createLesion(final Transaction tx, final GraphDatabaseService graphDb, final Container container, final Node parent) {
		checkArgument(container != null && container.getConceptNameValue() != null, 
				"Uninitialized or invalid container");
		final ConceptName conceptName = container.getConceptNameValue();
		final String type = conceptName.id();
		checkState(StringUtils.isNotBlank(type), "Container concept name is invalid");
		String id = null;
		for (final BaseType item : container.getChildren()) {
			if (item instanceof Text) {
				final Text tmp = (Text)item;				
				if (tmp.getConceptNameValue() != null && "118522005@SNOMED_CT".equals(tmp.getConceptNameValue().id())) {
					id = tmp.getValue();
					break;
				}
			}
		}		
		checkState(StringUtils.isNotBlank(id), "Uninitialized or invalid lesion id");
		final Node lesionNode = graphDb.createNode();
		lesionNode.addLabel(LabelTypes.LESION);
		lesionNode.setProperty(ID_PROPERTY, id);
		lesionNode.setProperty(ID_LESION_TYPE_PROPERTY, type);
		parent.createRelationshipTo(lesionNode, RelTypes.PRESENTS);
		return lesionNode;
	}

	public Node createOtherFindings(final Transaction tx, final GraphDatabaseService graphDb, final Container container, final Node parent) {
		checkArgument(container != null && container.getConceptNameValue() != null, 
				"Uninitialized or invalid container");
		final Node otherNode = graphDb.createNode();
		otherNode.addLabel(LabelTypes.OTHER_FINDINGS);
		parent.createRelationshipTo(otherNode, RelTypes.PRESENTS);
		return otherNode;
	}

	public Node getOrCreateBiRads(final Transaction tx, final GraphDatabaseService graphDb, 
			final Code code, final Node parent, final DocumentTemplate template) {
		checkArgument(code != null && code.getValue() != null, "Uninitialized or invalid code");
		final ConceptName conceptName = code.getValue();		
		final String id = conceptName.id();
		checkState(StringUtils.isNotBlank(id), "BI-RADS value is invalid");
		final UniqueFactory<Node> uniqueFactory = new UniqueFactory.UniqueNodeFactory(graphDb, BI_RADS_NODE) {
			@Override
			protected void initialize(final Node created, final Map<String, Object> properties) {
				created.setProperty(ID_PROPERTY, properties.get(ID_PROPERTY));	
			}
		};
		final Node node = uniqueFactory.getOrCreate(ID_PROPERTY, id);
		if (!node.hasLabel(LabelTypes.BI_RADS)) {
			node.addLabel(LabelTypes.BI_RADS);
		}
		if (!node.hasProperty(BI_RADS_CLASSIFICATION)) {
			final String classification = TemplateUtils.getMeaning(conceptName, template, null);
			if (StringUtils.isNotBlank(classification)) {
				node.setProperty(BI_RADS_CLASSIFICATION, classification);
			}
		}
		parent.createRelationshipTo(node, RelTypes.IS_CLASSIFIED);
		return node;
	}

	public Node createSize(final Transaction tx, final GraphDatabaseService graphDb, final Node parent) {
		final Node node = graphDb.createNode();
		node.addLabel(LabelTypes.TUMOUR_SIZE);
		parent.createRelationshipTo(node, RelTypes.IS);
		return node;
	}

	public Node getOrCreateLocation(final Transaction tx, final GraphDatabaseService graphDb, final Num num, final Node parent, 
			final DocumentTemplate template) {
		checkArgument(num != null && num.getConceptNameValue() != null, "Uninitialized or invalid numeric field");
		final ConceptName conceptName = num.getConceptNameValue();		
		final String id = conceptName.id();
		checkState(StringUtils.isNotBlank(id), "Tumour location id is invalid");
		final UniqueFactory<Node> uniqueFactory = new UniqueFactory.UniqueNodeFactory(graphDb, TUMOUR_LOCATION_NODE) {
			@Override
			protected void initialize(final Node created, final Map<String, Object> properties) {
				created.setProperty(ID_PROPERTY, properties.get(ID_PROPERTY));	
			}
		};
		final Node node = uniqueFactory.getOrCreate(ID_PROPERTY, id);
		if (!node.hasLabel(LabelTypes.TUMOUR_LOCATION)) {
			node.addLabel(LabelTypes.TUMOUR_LOCATION);
		}
		if (!node.hasProperty(DESCRIPTION_PROPERTY)) {
			final String meaning = TemplateUtils.getMeaning(conceptName, template, null);
			if (StringUtils.isNotBlank(meaning)) {
				node.setProperty(DESCRIPTION_PROPERTY, StringUtils.abbreviate(meaning, 20));
			}
		}
		parent.createRelationshipTo(node, RelTypes.LOCATED_IN);
		return node;
	}
	
	public Node getOrCreateFinding(final Transaction tx, final GraphDatabaseService graphDb, final Num num, final Node parent, 
			final DocumentTemplate template) {
		checkArgument(num != null && num.getConceptNameValue() != null, "Uninitialized or invalid numeric field");
		final ConceptName conceptName = num.getConceptNameValue();		
		final String id = conceptName.id();
		checkState(StringUtils.isNotBlank(id), "Finding id is invalid");
		final UniqueFactory<Node> uniqueFactory = new UniqueFactory.UniqueNodeFactory(graphDb, FINDING_NODE) {
			@Override
			protected void initialize(final Node created, final Map<String, Object> properties) {
				created.setProperty(ID_PROPERTY, properties.get(ID_PROPERTY));	
			}
		};
		final Node node = uniqueFactory.getOrCreate(ID_PROPERTY, id);
		if (!node.hasLabel(LabelTypes.FINDING)) {
			node.addLabel(LabelTypes.FINDING);
		}
		if (!node.hasProperty(DESCRIPTION_PROPERTY)) {
			final String meaning = TemplateUtils.getMeaning(conceptName, template, null);
			if (StringUtils.isNotBlank(meaning)) {
				node.setProperty(DESCRIPTION_PROPERTY, StringUtils.abbreviate(meaning, 20));
			}
		}
		parent.createRelationshipTo(node, RelTypes.PRESENTS);
		return node;
	}

	public @Nullable Date getDate(final Document document) {		
		checkArgument(document != null &&  document.getContainer() != null, 
				"Uninitialized or invalid document");
		Date date = null;
		for (final BaseType item : document.getContainer().getChildren()) {
			if (item instanceof Date) {
				final Date tmp = (Date)item;
				if ("399651003@SNOMED_CT".equals(tmp.getConceptNameValue().id())) {
					date = tmp;
					break;
				}				
			}
		}
		return date;
	}

	public @Nullable Text getStudyId(final Document document) {
		checkArgument(document != null &&  document.getContainer() != null, 
				"Uninitialized or invalid document");
		Text text = null;
		for (final BaseType item : document.getContainer().getChildren()) {
			if (item instanceof Text) {
				final Text tmp = (Text)item;
				if ("118522005@SNOMED_CT".equals(tmp.getConceptNameValue().id())) {
					text = tmp;
					break;
				}				
			}
		}
		return text;
	}

	public @Nullable Text getPatient(final Document document) {
		checkArgument(document != null &&  document.getContainer() != null, 
				"Uninitialized or invalid document");
		Text text = null;
		for (final BaseType item : document.getContainer().getChildren()) {
			if (item instanceof Text) {
				final Text tmp = (Text)item;
				if ("RID13159@RADLEX".equals(tmp.getConceptNameValue().id())) {
					text = tmp;
					break;
				}				
			}
		}
		return text;
	}

}