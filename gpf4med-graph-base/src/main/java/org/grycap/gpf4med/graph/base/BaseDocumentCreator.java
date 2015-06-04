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
import org.grycap.gpf4med.model.document.Children;
import org.grycap.gpf4med.model.document.Code;
import org.grycap.gpf4med.model.document.ConceptName;
import org.grycap.gpf4med.model.document.Container;
import org.grycap.gpf4med.model.document.Date;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.model.document.Num;
import org.grycap.gpf4med.model.document.Text;
import org.grycap.gpf4med.model.document.Value;
import org.grycap.gpf4med.model.template.ConceptNameTemplate;
import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.model.util.DateUtils;
import org.grycap.gpf4med.model.util.Id;
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
	public static final String COMPOSITION_PROPERTY = "composition";

	public static final String PATIENT_NODE = "Patient";
	public static final String MODALITY_NODE = "Modality";
	public static final String BI_RADS_NODE = "BI-RADS";
	public static final String TUMOUR_LOCATION_NODE = "Tumour Location";
	public static final String FINDING_NODE = "Finding";

	public BaseDocumentCreator() { }

	public Node getOrCreatePatient(final Transaction tx, final GraphDatabaseService graphDb, final Text patient) {
		checkArgument(patient != null && StringUtils.isNotBlank(patient.getVALUE()), 
				"Uninitialized or invalid patient");
		final ConceptName conceptName = patient.getCONCEPTNAME();
		final String id = Id.getId(conceptName);
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

	public Node getOrCreateModality(final Transaction tx, final GraphDatabaseService graphDb, final Template template) {
		checkArgument(template != null && template.getCONTAINER() != null
				&& template.getCONTAINER().getCONCEPTNAME() != null, 
				"Uninitialized or invalid template");
		final ConceptNameTemplate conceptName = template.getCONTAINER().getCONCEPTNAME();
		final String id = Id.getId(conceptName);
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
		if (!node.hasProperty(TYPE_PROPERTY) && StringUtils.isNotBlank(conceptName.getCODEMEANING2())) {
			node.setProperty(TYPE_PROPERTY, conceptName.getCODEMEANING2());
		}
		return node;
	}

	public Node getOrCreateDICOMReference(final Transaction tx, final GraphDatabaseService graphDb, 
			final Text text, final Node parent) {
		checkArgument(text != null && text.getCONCEPTNAME() != null, "Uninitialized or invalid text");
		final ConceptName conceptName = text.getCONCEPTNAME();
		final String id = Id.getId(conceptName);
		checkState(StringUtils.isNotBlank(id), "Text concept name is invalid");
		final String ref = StringUtils.trimToNull(text.getVALUE());
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

	public Node createRadiologicalStudy(final Transaction tx, final GraphDatabaseService graphDb,
			final Document document, final Node modality) {
		checkArgument(document != null && document.getCONTAINER() != null
				&& document.getCONTAINER().getCONCEPTNAME() != null, 
				"Uninitialized or invalid document");
		final ConceptName conceptName = document.getCONTAINER().getCONCEPTNAME();
		final String id = Id.getId(conceptName);
		checkState(StringUtils.isNotBlank(id), "Document concept name is invalid");
		final Node studyNode = graphDb.createNode();
		studyNode.addLabel(LabelTypes.RADIOLOGICAL_STUDY);
		studyNode.setProperty(ID_PROPERTY, id);
		studyNode.createRelationshipTo(modality, RelTypes.IS);
		modality.createRelationshipTo(studyNode, RelTypes.HAS);
		final Text identifier = getStudyId(document);
		if (identifier != null) {
			studyNode.setProperty(ID_REPORT_PROPERTY, identifier.getVALUE());
		}
		final Text patient = getPatient(document);
		if (patient != null) {
			final Node patientNode = getOrCreatePatient(tx, graphDb, patient);
			patientNode.setProperty(ID_PATIENT_PROPERTY, patient.getVALUE());
			final Relationship relationship = patientNode.createRelationshipTo(studyNode, RelTypes.HAS);
			final Date date = getDate(document);
			if (date != null) {
				relationship.setProperty(ACQUISITION_DATE_PROPERTY, DateUtils.formattedString(date.getVALUE(), Locale.ENGLISH));
			}
			studyNode.createRelationshipTo(patientNode, RelTypes.FROM);
		}
		return studyNode;
	}
	
	public Node createBreastComposition(final Transaction tx, final GraphDatabaseService graphDb, final Container container, final Node parent, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final Node breastCompositionNode = graphDb.createNode();
		breastCompositionNode.addLabel(LabelTypes.COMPOSITION);
		
		parent.createRelationshipTo(breastCompositionNode, RelTypes.ARE);

		Children children = container.getCHILDREN();
		if (children != null) {
			for (final Code code : children.getCODE()) {
				final String idField = Id.getId(code.getCONCEPTNAME());
				if ("TRMM0028@TRENCADIS_MAMO".equals(idField)) {
					 
					ConceptNameTemplate conceptNameTemplate = new ConceptNameTemplate().withCODEVALUE(code.getVALUE().getCODEVALUE())
							   														   .withCODESCHEMA(code.getVALUE().getCODESCHEMA());
					final String composition = TemplateUtils.getMeaning(conceptNameTemplate, template, null);
					final String idComposition = Id.getId(conceptNameTemplate);
					breastCompositionNode.setProperty(ID_PROPERTY, idComposition);
					breastCompositionNode.setProperty(COMPOSITION_PROPERTY, composition);
				}
			}	
		}
		return breastCompositionNode;
	}

	public Node createLesion(final Transaction tx, final GraphDatabaseService graphDb, final Container container, final Node parent) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		String type = null;
		String id = null;
		Children item = container.getCHILDREN();
		if (item.getTEXT() != null) {
			for (final Text text : item.getTEXT()) {
				final Text tmp = text;
				if (tmp.getCONCEPTNAME() != null && "TRMM0006@TRENCADIS_MAMO".equals(Id.getId(tmp.getCONCEPTNAME()))) {
					id = tmp.getVALUE();
					break;
				}
			}
		}
		if (item.getCODE() != null) {
			for (final Code code : item.getCODE()) {
				final String idCode = Id.getId(code.getCONCEPTNAME());
				if ("TRMM0007@TRENCADIS_MAMO".equals(idCode)){
					type = code.getVALUE().getCODEVALUE() + "@" + code.getVALUE().getCODESCHEMA();
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

	public Node createOtherFindings(final Transaction tx, final GraphDatabaseService graphDb,
			final Container container, final Node parent) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final Node otherNode = graphDb.createNode();
		otherNode.addLabel(LabelTypes.OTHER_FINDINGS);
		parent.createRelationshipTo(otherNode, RelTypes.PRESENTS);
		return otherNode;
	}

	public Node getOrCreateBiRads(final Transaction tx, final GraphDatabaseService graphDb, 
			final Code code, final Node parent, final Template template) {
		checkArgument(code != null && code.getVALUE() != null, "Uninitialized or invalid code");
		final Value value = code.getVALUE();
		final ConceptName conceptName = new ConceptName().withCODEVALUE(value.getCODEVALUE()).withCODESCHEMA(value.getCODESCHEMA());
		final String id = Id.getId(conceptName);
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
			ConceptNameTemplate conceptNameTemplate = new ConceptNameTemplate().withCODEVALUE(conceptName.getCODEVALUE())
																			   .withCODESCHEMA(conceptName.getCODESCHEMA());
			final String classification = TemplateUtils.getMeaning(conceptNameTemplate, template, null);
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
			final Template template) {
		checkArgument(num != null && num.getCONCEPTNAME() != null, "Uninitialized or invalid numeric field");
		final ConceptName conceptName = num.getCONCEPTNAME();		
		final String id = Id.getId(conceptName);
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
			ConceptNameTemplate conceptNameTemplate = new ConceptNameTemplate().withCODEVALUE(conceptName.getCODEVALUE())
																			   .withCODESCHEMA(conceptName.getCODESCHEMA())
																			   .withCODEMEANING(conceptName.getCODEMEANING())
																			   .withCODEMEANING2(conceptName.getCODEMEANING2());
			final String meaning = TemplateUtils.getMeaning(conceptNameTemplate, template, null);
			if (StringUtils.isNotBlank(meaning)) {
				node.setProperty(DESCRIPTION_PROPERTY, StringUtils.abbreviate(meaning, 20));
			}
		}
		parent.createRelationshipTo(node, RelTypes.LOCATED_IN);
		return node;
	}
	
	public Node getOrCreateFinding(final Transaction tx, final GraphDatabaseService graphDb, final Num num, final Node parent, 
			final Template template) {
		checkArgument(num != null && num.getCONCEPTNAME() != null, "Uninitialized or invalid numeric field");
		final ConceptName conceptName = num.getCONCEPTNAME();		
		final String id = Id.getId(conceptName);
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
			ConceptNameTemplate conceptNameTemplate = new ConceptNameTemplate().withCODEVALUE(conceptName.getCODEVALUE())
																			   .withCODESCHEMA(conceptName.getCODESCHEMA())
																			   .withCODEMEANING(conceptName.getCODEMEANING())
																			   .withCODEMEANING2(conceptName.getCODEMEANING2());
			final String meaning = TemplateUtils.getMeaning(conceptNameTemplate, template, null);
			if (StringUtils.isNotBlank(meaning)) {
				node.setProperty(DESCRIPTION_PROPERTY, StringUtils.abbreviate(meaning, 20));
			}
		}
		parent.createRelationshipTo(node, RelTypes.PRESENTS);
		return node;
	}
	
	public Node createComposition(final Transaction tx, final GraphDatabaseService graphDb,
			final Container container, final Node parent) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final Node otherNode = graphDb.createNode();
		otherNode.addLabel(LabelTypes.OTHER_FINDINGS);
		parent.createRelationshipTo(otherNode, RelTypes.PRESENTS);
		return otherNode;
	}

	public @Nullable Date getDate(final Document document) {		
		checkArgument(document != null &&  document.getCONTAINER() != null, 
				"Uninitialized or invalid document");
		Date date = null;
		for (final Date item : document.getCONTAINER().getCHILDREN().getDATE()) {
			if (item instanceof Date) {
				final Date tmp = (Date)item;
				if ("TRMM0002@TRENCADIS_MAMO".equals(Id.getId(tmp.getCONCEPTNAME()))) {
					date = tmp;
					break;
				}				
			}
		}
		return date;
	}

	public @Nullable Text getStudyId(final Document document) {
		checkArgument(document != null &&  document.getCONTAINER() != null, 
				"Uninitialized or invalid document");
		Text text = null;
		for (final Text item : document.getCONTAINER().getCHILDREN().getTEXT()) {
			if (item instanceof Text) {
				final Text tmp = (Text)item;
				if ("TRMM0001@TRENCADIS_MAMO".equals(Id.getId(tmp.getCONCEPTNAME()))) {
					text = tmp;
					break;
				}				
			}
		}
		return text;
	}

	public @Nullable Text getPatient(final Document document) {
		checkArgument(document != null &&  document.getCONTAINER() != null, 
				"Uninitialized or invalid document");
		Text text = null;
		for (final Text item : document.getCONTAINER().getCHILDREN().getTEXT()) {
			if (item instanceof Text) {
				final Text tmp = (Text)item;
				if ("TRMM0003@TRENCADIS_MAMO".equals(Id.getId(tmp.getCONCEPTNAME()))) {
					text = tmp;
					break;
				}				
			}
		}
		return text;
	}
	
	public @Nullable Double valueFromString(final String str) {
		return StringUtils.isNotBlank(str) ? Double.parseDouble(str) : null;
	}
	
	public void setPropertyNode(final Node node, final String property, final Code code, final Template template) {
		final ConceptNameTemplate conceptNameTemplate = new ConceptNameTemplate().withCODEVALUE(code.getVALUE().getCODEVALUE())
																				 .withCODESCHEMA(code.getVALUE().getCODESCHEMA());
		final String meaning = TemplateUtils.getMeaning(conceptNameTemplate, template, null);
		node.setProperty(property, meaning);
	}

}