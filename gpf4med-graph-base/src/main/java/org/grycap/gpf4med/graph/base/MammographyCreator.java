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

import org.grycap.gpf4med.graph.base.model.LabelTypes;
import org.grycap.gpf4med.graph.base.model.RelTypes;
import org.grycap.gpf4med.data.GraphDatabaseHandler;
import org.grycap.gpf4med.model.BaseType;
import org.grycap.gpf4med.model.Code;
import org.grycap.gpf4med.model.ConceptName;
import org.grycap.gpf4med.model.Container;
import org.grycap.gpf4med.model.Document;
import org.grycap.gpf4med.model.DocumentTemplate;
import org.grycap.gpf4med.model.Num;
import org.grycap.gpf4med.model.Text;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads mammograms to the graph.
 * @author Erik Torres <ertorser@upv.es>
 */
public class MammographyCreator extends BaseDocumentCreator {

	private final static Logger LOGGER = LoggerFactory.getLogger(MammographyCreator.class);

	public static final String SIDE_PROPERTY = "side";
	public static final String CALCIFICATION_PROPERTY = "calcification";
	public static final String MORPHOLOGY_PROPERTY = "morphology";
	public static final String MARGIN_PROPERTY = "margin";
	public static final String LOCATION_PROPERTY = "location";
	public static final String DIMENSION1_PROPERTY = "dimension1";
	public static final String DIMENSION2_PROPERTY = "dimension2";
	public static final String DIMENSION3_PROPERTY = "dimension3";
	public static final String DISTANCE_TO_NIPPLE_PROPERTY = "distance_to_nipple";	

	public MammographyCreator() {
		super();
	}

	public final void create(final Document document, final DocumentTemplate template) {
		checkArgument(document != null, "Uninitialized document");
		checkArgument(template != null, "Uninitialized template");
		final GraphDatabaseService graphDb = GraphDatabaseHandler.INSTANCE.service();
		try (Transaction tx = graphDb.beginTx()) {			
			// create modality
			final Node modality = getOrCreateModality(tx, graphDb, template);
			// create study		
			final Node study = createRadiologicalStudy(tx, graphDb, document, modality);			
			// create children nodes
			if (document.getContainer() != null) {
				for (final BaseType item : document.getContainer().getChildren()) {
					if (item instanceof Text) {
						final Text text = (Text)item;
						if ("TRMM0017@TRENCADIS_MAMO".equals(text.getConceptNameValue().id())) {
							getOrCreateDICOMReference(tx, graphDb, text, study);
						}
					} else if (item instanceof Container) {
						final Container container = (Container)item;
						final String id = container.getConceptNameValue().id();
						if ("RID29896@RADLEX".equals(id)) {
							loadBreast(tx, graphDb, study, (Container)item, "right", template);
						} else if ("RID29897@RADLEX".equals(id)) {
							loadBreast(tx, graphDb, study, (Container)item, "left", template);
						} else {
							LOGGER.info("Ignoring unknown container, only breast are allowed at this position: " + id);
						}
					}				
				}
			}
			tx.success();
		} catch (Exception e) {
			LOGGER.warn("Failed to create mammography in the graph", e);
		}
	}

	private final void loadBreast(final Transaction tx, final GraphDatabaseService graphDb, final Node study,
			final Container container, final String side, final DocumentTemplate template) {
		checkArgument(container != null && container.getConceptNameValue() != null, 
				"Uninitialized or invalid container");
		final ConceptName conceptName = container.getConceptNameValue();
		final String id = conceptName.id();
		final Node breastNode = graphDb.createNode();
		breastNode.addLabel(LabelTypes.BREAST);
		breastNode.setProperty(ID_PROPERTY, id);
		breastNode.setProperty(SIDE_PROPERTY, side);
		study.createRelationshipTo(breastNode, RelTypes.INCLUDES);
		Node sizeNode = null;
		// load lesions and associated findings
		for (final BaseType item : container.getChildren()) {
			if (item instanceof Container) {
				final Container container2 = (Container)item;
				final String type = container2.getConceptNameValue().id();
				if (!"TRMM0011@TRENCADIS_MAMO".equals(type)) {
					final Node lesionNode = createLesion(tx, graphDb, container2, breastNode);
					for (final BaseType item2 : container2.getChildren()) {						
						final String idField = item2.getConceptNameValue().id();
						if (item2 instanceof Num && "RID5196@RADLEX".equals(idField)) {
							lesionNode.setProperty(CALCIFICATION_PROPERTY, ((Num)item2).getValue() == 1.0d);
						} else if (item2 instanceof Code && "RID28825@RADLEX".equals(idField)) {
							lesionNode.setProperty(MORPHOLOGY_PROPERTY, ((Code)item2).getValue().id());
						} else if (item2 instanceof Code && "RID5972@RADLEX".equals(idField)) {
							lesionNode.setProperty(MARGIN_PROPERTY, ((Code)item2).getValue().id());
						} else if (item2 instanceof Num && "RID29929@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29935@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29932@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29938@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29947@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29941@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29953@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29944@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29950@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29907@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "RID29918@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "TRMM0001@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "TRMM0003@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "TRMM0005@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "TRMM0007@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateLocation(tx, graphDb, (Num)item2, lesionNode, template);
						} else if (item2 instanceof Num && "372299002@SNOMED_CT".equals(idField)
								&& ((Num)item2).getValue() != null) {
							sizeNode = (sizeNode != null ? sizeNode : createSize(tx, graphDb, lesionNode));
							sizeNode.setProperty(DIMENSION1_PROPERTY, ((Num)item2).getValue());
						} else if (item2 instanceof Num && "372300005@SNOMED_CT".equals(idField)
								&& ((Num)item2).getValue() != null) {
							sizeNode = (sizeNode != null ? sizeNode : createSize(tx, graphDb, lesionNode));
							sizeNode.setProperty(DIMENSION2_PROPERTY, ((Num)item2).getValue());
						} else if (item2 instanceof Num && "372301009@SNOMED_CT".equals(idField)
								&& ((Num)item2).getValue() != null) {
							sizeNode = (sizeNode != null ? sizeNode : createSize(tx, graphDb, lesionNode));						
							sizeNode.setProperty(DIMENSION3_PROPERTY, ((Num)item2).getValue());
						} else if (item2 instanceof Num && "TRMM0015@TRENCADIS_MAMO".equals(idField)
								&& ((Num)item2).getValue() != null) {
							lesionNode.setProperty(DISTANCE_TO_NIPPLE_PROPERTY, ((Num)item2).getValue());
						} else if(item2 instanceof Code && "RID36027@RADLEX".equals(idField)) {
							getOrCreateBiRads(tx, graphDb, (Code)item2, lesionNode, template);
						}
					}
				} else {
					final Node otherNode = createOtherFindings(tx, graphDb, container2, breastNode);
					for (final BaseType item2 : container2.getChildren()) {						
						final String idField = item2.getConceptNameValue().id();
						if (item2 instanceof Num && "RID34272@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID1518@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID34263@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID5425@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID34270@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID34271@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "TRMM0016@TRENCADIS_MAMO".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID34318@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID34320@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID34319@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID28823@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID34383@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if (item2 instanceof Num && "RID34269@RADLEX".equals(idField)
								&& ((Num)item2).getValue() != null && ((Num)item2).getValue() == 1.0d) {
							getOrCreateFinding(tx, graphDb, (Num)item2, otherNode, template);
						} else if(item2 instanceof Code && "RID36027@RADLEX".equals(idField)) {
							getOrCreateBiRads(tx, graphDb, (Code)item2, otherNode, template);
						}
					}
				}							
			}
		}		
	}

}