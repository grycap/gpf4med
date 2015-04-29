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

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.grycap.gpf4med.data.GraphDatabaseHandler;
import org.grycap.gpf4med.graph.base.model.LabelTypes;
import org.grycap.gpf4med.graph.base.model.RelTypes;
import org.grycap.gpf4med.model.document.Children;
import org.grycap.gpf4med.model.document.Code;
import org.grycap.gpf4med.model.document.ConceptName;
import org.grycap.gpf4med.model.document.Container;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.model.document.Num;
import org.grycap.gpf4med.model.document.Text;
import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.model.util.Id;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads mammograms to the graph.
 * @author Erik Torres <ertorser@upv.es>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public class MammographyCreatorNewModel extends BaseDocumentCreator {

	private final static Logger LOGGER = LoggerFactory.getLogger(MammographyCreatorNewModel.class);

	public static final String SIDE_PROPERTY = "side";
	public static final String COMPOSITION_PROPERTY = "composition";
	public static final String CALCIFICATION_PROPERTY = "calcification";
	public static final String MORPHOLOGY_PROPERTY = "morphology";
	public static final String ORIENTATION_PROPERTY = "orientation";
	public static final String DISTRIBUTION_PROPERTY = "distribution";
	public static final String MARGIN_PROPERTY = "margin";
	public static final String MARGIN_TYPE_PROPERTY = "margin_type";
	public static final String DENSITY_PROPERTY = "density";
	public static final String LOCATION_PROPERTY = "location";
	public static final String ECHOGENIC_PATTERN_PROPERTY = "echogenic_pattern";
	public static final String DIMENSION1_PROPERTY = "dimension1";
	public static final String DIMENSION2_PROPERTY = "dimension2";
	public static final String DIMENSION3_PROPERTY = "dimension3";
	public static final String DISTANCE_TO_NIPPLE_PROPERTY = "distance_to_nipple";	

	public MammographyCreatorNewModel() {
		super();
	}
	
	// IT'S NECESSARY CHANGE THE IDs OF THE CODES (are not the same in the new reports)
	
	public final void create(final Document document, final Template template) {
		checkArgument(document != null, "Uninitialized document");
		checkArgument(template != null, "Uninitialized template");
		final GraphDatabaseService graphDb = GraphDatabaseHandler.INSTANCE.service();
		try (Transaction tx = graphDb.beginTx()) {			
			// create modality
			final Node modality = getOrCreateModality(tx, graphDb, template);
			// create study		
			final Node study = createRadiologicalStudy(tx, graphDb, document, modality);			
			// create children nodes
			if (document.getCONTAINER() != null) {
				Children children = document.getCONTAINER().getCHILDREN();
				if (children.getTEXT() != null) {
					for (final Text text : children.getTEXT()) {
						if ("TRMM0001@TRENCADIS_MAMO".equals(Id.getId(text.getCONCEPTNAME()))) {
							getOrCreateDICOMReference(tx, graphDb, text, study);
						}
					}
				}
				
				if (children.getCONTAINER() != null) {
					for (final Container container : children.getCONTAINER()) {
						final String id = Id.getId(container.getCONCEPTNAME());
						// Findings
						if ("RID28486@RADLEX".equals(id)) {
							loadFindings(tx, graphDb, study, container, template);
						}
						// Breast composition
						else if ("TRMM0027@TRENCADIS_MAMO".equals(id)) {
							loadBreastComposition(tx, graphDb, study, container, template);
						} else {
							LOGGER.info("Ignoring unknown container: " + id);
						}
					}
				}
			}
			tx.success();
		} catch (Exception e) {
			LOGGER.warn("Failed to create mammography in the graph", e);
		}
	}
	
	private final void loadFindings(final Transaction tx, final GraphDatabaseService graphDb, final Node study,
			final Container container, final Template template) {
		
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final ConceptName conceptName = container.getCONCEPTNAME();
		final String id = Id.getId(conceptName);
		
		final Node rightBreastNode = graphDb.createNode();
		rightBreastNode.addLabel(LabelTypes.BREAST);
		rightBreastNode.setProperty(SIDE_PROPERTY, "right");
		study.createRelationshipTo(rightBreastNode, RelTypes.INCLUDES);
		
		final Node leftBreastNode = graphDb.createNode();
		leftBreastNode.addLabel(LabelTypes.BREAST);
		leftBreastNode.setProperty(SIDE_PROPERTY, "left");
		study.createRelationshipTo(leftBreastNode, RelTypes.INCLUDES);

		if (container.getCHILDREN() != null) {
			Children children = container.getCHILDREN();
			if (children.getCONTAINER() != null) {
				for (final Container container2 : children.getCONTAINER()) {
					final String idField = Id.getId(container2.getCONCEPTNAME());
					Children children2 = container2.getCHILDREN();
					//Finding
					if ("TRMM0005@TRENCADIS_MAMO".equals(idField)) {
						for (final Container container3 : children2.getCONTAINER()) {
							final String idField2 = Id.getId(container3.getCONCEPTNAME());
							if ("TRMM0011@TRENCADIS_MAMO".equals(idField2)) {
								Children children3 = container3.getCHILDREN();
								for (final Code code : children3.getCODE()) {
									String idCode = Id.getId(code.getCONCEPTNAME());
									if ("RID5821@RADLEX".equals(idCode)) {
										String idLaterality = code.getVALUE().getCODEVALUE() + "@" + code.getVALUE().getCODESCHEMA();
										// Right breast
										if ("RID5825@RADLEX".equals(idLaterality)) {
											loadBreastLesion(tx, graphDb, rightBreastNode, container2, template);
										}
										// Left breast
										else if ("RID5824@RADLEX".equals(idLaterality)) {
											loadBreastLesion(tx, graphDb, leftBreastNode, container2, template);
										}
									}
								}
							}
						}
					}
					// Associated characteristics
					if ("TRMM0025@TRENCADIS_MAMO".equals(idField)) {
						for (final Container container3 : children2.getCONTAINER()) {
							final String idBreast = Id.getId(container3.getCONCEPTNAME());
							// Right breast
							if ("TRMM0058@TRENCADIS_MAMO".equals(idBreast)) {
								loadBreastCharacteristics(tx, graphDb, rightBreastNode, container3, template);
							}
							// Left breast
							else if ("TRMM0059@TRENCADIS_MAMO".equals(idBreast)) {
								loadBreastCharacteristics(tx, graphDb, leftBreastNode, container3, template);
							}
						}
					}
				}
			} else {
				LOGGER.info("Ignoring empty containers in: " + Id.getId(container.getCONCEPTNAME()));
			}
		} else {
			LOGGER.info("Ignoring empty children of: " + Id.getId(container.getCONCEPTNAME()));
		}

	}
	
	private final void loadBreastComposition(final Transaction tx, final GraphDatabaseService graphDb, final Node study,
			final Container container, final Template template) {
		
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final ConceptName conceptName = container.getCONCEPTNAME();
		final String id = Id.getId(conceptName);
		final Node breastCompositionNode = graphDb.createNode();
		breastCompositionNode.addLabel(LabelTypes.COMPOSITION);
		breastCompositionNode.setProperty(ID_PROPERTY, id);
		study.createRelationshipTo(breastCompositionNode, RelTypes.ARE);
		
		Children children = container.getCHILDREN();
		if (children != null) {
			for (final Code code : children.getCODE()) {
				final String idField = Id.getId(code.getCONCEPTNAME());
				if ("TRMM0028@TRENCADIS_MAMO".equals(idField)) {
					breastCompositionNode.setProperty(COMPOSITION_PROPERTY, code.getVALUE().getCODEMEANING());
				}
			}	
		}
		
	}
	
	/** 
	 * Hallazgos observados
	 *
	 */
	private final void loadBreastLesion(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final Node lesionNode = createLesion(tx, graphDb, container, breastNode);
		Node sizeNode = null;
		// Load lesions and associated findings
		Children item = container.getCHILDREN();
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				String idField = Id.getId(num.getCONCEPTNAME());
				// Calcificaciones en un nódulo
				if ("TRMM0033@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null) {
					lesionNode.setProperty(CALCIFICATION_PROPERTY, valueFromString(num.getVALUE()) == 1.0d);
				}
			}
		}
		if (item.getCODE() != null) {
			for (final Code code : item.getCODE()) {
				final String idField = Id.getId(code.getCONCEPTNAME());
				// Shape
				if ("TRMM0008@TRENCADIS_MAMO".equals(idField) || "TRMM0020@TRENCADIS_MAMO".equals(idField)) {
					lesionNode.setProperty(MORPHOLOGY_PROPERTY, code.getVALUE().getCODEMEANING());
				}
				// Margin
				else if ("TRMM0009@TRENCADIS_MAMO".equals(idField)) {
					lesionNode.setProperty(MARGIN_PROPERTY, code.getVALUE().getCODEMEANING());
				}
				// Density
				else if ("TRMM0010@TRENCADIS_MAMO".equals(idField)) {
					lesionNode.setProperty(DENSITY_PROPERTY, code.getVALUE().getCODEMEANING());
				}
				// Orientation
				else if ("TRMM0035@TRENCADIS_MAMO".equals(idField)) {
					lesionNode.setProperty(ORIENTATION_PROPERTY, code.getVALUE().getCODEMEANING());
				}
				// Margin Type <<<< 
				else if ("TRMM0036@TRENCADIS_MAMO".equals(idField)) {
					lesionNode.setProperty(MARGIN_TYPE_PROPERTY, code.getVALUE().getCODEMEANING());
				}
				// Echogenic pattern
				else if ("TRMM0037@TRENCADIS_MAMO".equals(idField) ) {
					lesionNode.setProperty(ECHOGENIC_PATTERN_PROPERTY, code.getVALUE().getCODEMEANING());
				}
				// Distribution
				else if ("RID5958@RADLEX".equals(idField) ) {
					lesionNode.setProperty(DISTRIBUTION_PROPERTY, code.getVALUE().getCODEMEANING());
				}
				// BI-RADS category
				else if ("RID36027@RADLEX".equals(idField)) {
					getOrCreateBiRads(tx, graphDb, code, lesionNode, template);
				}
			}
		}
		if (item.getCONTAINER() != null) {
			for (final Container container2 : item.getCONTAINER()) {
				final String idField = Id.getId(container2.getCONCEPTNAME());
				Children item2 = container2.getCHILDREN();
				// Lesion size
				if ("TRMM0024@TRENCADIS_MAMO".equals(idField)) {
					if (item2.getNUM() != null) {
						for (final Num num : item2.getNUM()) {
							final String idNum = Id.getId(num.getCONCEPTNAME());
							// Tamaño Lesion (Craneo-Caudal)
							if ("TRMM0017@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null) {
								sizeNode = (sizeNode != null ? sizeNode : createSize(tx, graphDb, lesionNode));
								sizeNode.setProperty(DIMENSION1_PROPERTY, valueFromString(num.getVALUE()));
							}
							// Tamaño Lesion (Latero-Lateral)
							else if ("TRMM0018@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null) {
								sizeNode = (sizeNode != null ? sizeNode : createSize(tx, graphDb, lesionNode));
								sizeNode.setProperty(DIMENSION2_PROPERTY, valueFromString(num.getVALUE()));
							}
							// Tamaño Lesion (Antero-Posterior)
							else if ("TRMM0019@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null) {
								sizeNode = (sizeNode != null ? sizeNode : createSize(tx, graphDb, lesionNode));						
								sizeNode.setProperty(DIMENSION3_PROPERTY, valueFromString(num.getVALUE()));
							}
						}
					}
				}
				// Lesion location
				else if ("TRMM0011@TRENCADIS_MAMO".equals(idField)) {
					if (item2.getNUM() != null) {
						for (final Num num : item2.getNUM()) {
							final String idNum = Id.getId(num.getCONCEPTNAME());
							// Cuadrante Supero-Externo (CSE)
							if ("RID29928@RADLEX".equals(idNum)
									&& num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Cuadrante Infero-Externo (CIE)
							else if ("RID29934@RADLEX".equals(idNum)
									&& num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Cuadrante Supero-Interno (CSI)
							else if ("RID29931@RADLEX".equals(idNum)
									&& num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Cuadrante Infero-Interno (CII)
							else if ("RID29937@RADLEX".equals(idNum)
									&& num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Línea Intercuadrantica Externa (LIE)
							else if ("RID29946@RADLEX".equals(idNum)
									&& num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							} 
							// Línea Intercuadrántica Superior (LIS)
							else if ("RID29940@RADLEX".equals(idNum)
									&& num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Línea Intercudrántica Inferior (LIInf)
							else if ("RID29952@RADLEX".equals(idNum)
									&& num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Línea Intercudrántica Interna (LIInt)
							else if ("RID29943@RADLEX".equals(idNum)
									&& num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Retroareolar
							else if ("RID29949@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Pezón
							else if ("RID29902@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Areola
							else if ("RID29911@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Prolongación axilar
							else if ("RID28643@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Axila
							else if ("RID2470@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Surco Inframamario
							else if ("TRMM0015@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Surco Intermamario
							else if ("TRMM0016@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Distancia desde el pezón
							else if ("TRMM0012@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								lesionNode.setProperty(DISTANCE_TO_NIPPLE_PROPERTY, num);
							}
						}
					}
				}
			}
		}
		
	}
	/** 
	 * Características asociadas
	 * 
	 */
	private final void loadBreastCharacteristics(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final ConceptName conceptName = container.getCONCEPTNAME();
		String id = Id.getId(conceptName);
		final Node propertiesNode = graphDb.createNode();
		propertiesNode.addLabel(LabelTypes.ASSOCIATED_PROPERTIES);
		//propertiesNode.setProperty(ID_PROPERTY, id);
		breastNode.createRelationshipTo(propertiesNode, RelTypes.HAS);

		// load lesions and associated findings
		Children item = container.getCHILDREN();
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				final String idField = Id.getId(num.getCONCEPTNAME());
				// Retraccion de la piel
				if ("RID34383@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, propertiesNode, template);
				}
				// Retraccion del pezón
				else if ("RID34269@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, propertiesNode, template);
				}
				// Engrosamiento de la piel
				else if ("RID34270@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, propertiesNode, template);
				}
				// Engrosamiento Trabecular
				else if ("RID34271@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, propertiesNode, template);
				}
				// Adenopatia Axilar
				else if ("RID34272@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, propertiesNode, template);
				}
			}
		}
	}	
	
	public @Nullable Double valueFromString(final String str) {
		return StringUtils.isNotBlank(str) ? Double.parseDouble(str) : 0.0d;
	}

}