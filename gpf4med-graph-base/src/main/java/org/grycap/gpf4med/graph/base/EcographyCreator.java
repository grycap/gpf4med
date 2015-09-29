/*
 * Copyright 2015 Institute for Molecular Imaging Instrumentation (I3M)
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

import org.grycap.gpf4med.data.GraphDatabaseHandler;
import org.grycap.gpf4med.graph.base.model.LabelTypes;
import org.grycap.gpf4med.graph.base.model.RelTypes;
import org.grycap.gpf4med.model.document.Children;
import org.grycap.gpf4med.model.document.Code;
import org.grycap.gpf4med.model.document.Container;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.model.document.Num;
import org.grycap.gpf4med.model.template.Template;
import org.grycap.gpf4med.model.util.Id;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads echographies to the graph.
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public class EcographyCreator extends BaseDocumentCreator {

	private final static Logger LOGGER = LoggerFactory.getLogger(EcographyCreator.class);

	public static final String SIDE_PROPERTY = "side";
	public static final String CALCIFICATION_PROPERTY = "calcification";
	public static final String SHAPE_PROPERTY = "shape";
	public static final String MARGIN_PROPERTY = "margin";
	public static final String NON_CIRCUMSCRIBED_MARGIN_PROPERTY = "non-circumscribed_margin";
	public static final String ECHO_PATTERN_PROPERTY = "echo_pattern";
	public static final String POSTERIOR_FEATURES_PROPERTY = "posterior_features";
	public static final String CALCIFICATION_INTRADUCTAL_PROPERTY = "intraductal_calcification";
	public static final String CALCIFICATIONS_IN_MASS_PROPERTY = "calcifications_in_mass";
	public static final String CALCIFICATIONS_OUT_MASS_PROPERTY = "calcifications_out_mass";
	public static final String ORIENTATION_PROPERTY = "orientation";
	public static final String DIMENSION1_PROPERTY = "dimension1";
	public static final String DIMENSION2_PROPERTY = "dimension2";
	public static final String DIMENSION3_PROPERTY = "dimension3";
	public static final String DISTANCE_TO_NIPPLE_PROPERTY = "distance_to_nipple";
	public static final String VASCULAR_ABNORMALITIES_PROPERTY = "vascular_abnormalities";
	public static final String SKIN_CHANGES_PROPERTY = "skin_changes";

	public EcographyCreator() {
		super();
	}
	
	public final void create(final Document document, final Template template) {
		checkArgument(document != null, "Uninitialized document");
		checkArgument(template != null, "Uninitialized template");
		final GraphDatabaseService graphDb = GraphDatabaseHandler.INSTANCE.service();
		try (Transaction tx = graphDb.beginTx()) {
			// Create modality
			final Node modality = getOrCreateModality(tx, graphDb, template);
			// create study		
			final Node study = createRadiologicalStudy(tx, graphDb, document, modality);			
			// create children nodes
			if (document.getCONTAINER() != null) {
				Children children = document.getCONTAINER().getCHILDREN();				
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
			LOGGER.warn("Failed to create ecography in the graph", e);
		}
	}
	
	private final void loadFindings(final Transaction tx, final GraphDatabaseService graphDb, final Node study,
			final Container container, final Template template) {
		
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");

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
					// Associated Features
					if ("TRMM0025@TRENCADIS_MAMO".equals(idField)) {
						for (final Container container3 : children2.getCONTAINER()) {
							final String idBreast = Id.getId(container3.getCONCEPTNAME());
							// Right breast
							if ("TRMM0058@TRENCADIS_MAMO".equals(idBreast)) {
								loadBreastFeatures(tx, graphDb, rightBreastNode, container3, template);
							}
							// Left breast
							else if ("TRMM0059@TRENCADIS_MAMO".equals(idBreast)) {
								loadBreastFeatures(tx, graphDb, leftBreastNode, container3, template);
							}
						}
					}
					// Special cases
					if ("TRMM0051@TRENCADIS_MAMO".equals(idField)) {
						for (final Container container3 : children2.getCONTAINER()) {
							final String idBreast = Id.getId(container3.getCONCEPTNAME());
							// Right breast
							if ("TRMM0064@TRENCADIS_MAMO".equals(idBreast)) {
								loadSpecialCases(tx, graphDb, rightBreastNode, container3, template);
							}
							// Left breast
							else if ("TRMM0065@TRENCADIS_MAMO".equals(idBreast)) {
								loadSpecialCases(tx, graphDb, leftBreastNode, container3, template);
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
		
		getOrCreateBreastComposition(tx, graphDb, container, study, template);
		
	}
	
	/** 
	 * Observations Section
	 *
	 */
	private final void loadBreastLesion(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final Node lesionNode = createLesion(tx, graphDb, container, breastNode, template);
		Node sizeNode = null;
		// Load lesions and associated findings
		Children item = container.getCHILDREN();
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				String idField = Id.getId(num.getCONCEPTNAME());
				// Existing associated calcifications
				if ("TRMM0033@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateLesionProperty(tx, graphDb, num, CALCIFICATION_PROPERTY, lesionNode, template);
				}
				// Intraductal Calcification
				else if ("TRMM0041@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateLesionProperty(tx, graphDb, num, CALCIFICATION_INTRADUCTAL_PROPERTY, lesionNode, template);
				}
				// Calcifications in mass
				else if ("RID34368@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateLesionProperty(tx, graphDb, num, CALCIFICATIONS_IN_MASS_PROPERTY, lesionNode, template);
				}
				// Calcifications out of mass
				else if ("RID36038@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateLesionProperty(tx, graphDb, num, CALCIFICATIONS_OUT_MASS_PROPERTY, lesionNode, template);
				}
			}
		}
		if (item.getCODE() != null) {
			for (final Code code : item.getCODE()) {
				final String idField = Id.getId(code.getCONCEPTNAME());
				// Shape
				if ("TRMM0008@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, SHAPE_PROPERTY, lesionNode, template);
				}
				// Margin
				else if ("TRMM0009@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, MARGIN_PROPERTY, lesionNode, template);
				}
				// Orientation 
				else if ("TRMM0035@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, ORIENTATION_PROPERTY, lesionNode, template);
				}
				// Non-Circumscribed Margin Type
				else if ("TRMM0036@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, NON_CIRCUMSCRIBED_MARGIN_PROPERTY, lesionNode, template);
				}
				// Echo pattern
				else if ("TRMM0037@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, ECHO_PATTERN_PROPERTY, lesionNode, template);
				}
				// Posterior Features
				else if ("TRMM0040@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, POSTERIOR_FEATURES_PROPERTY, lesionNode, template);
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
				// Size of Lesion
				if ("TRMM0024@TRENCADIS_MAMO".equals(idField)) {
					if (item2.getNUM() != null) {
						for (final Num num : item2.getNUM()) {
							final String idNum = Id.getId(num.getCONCEPTNAME());
							// Size (Craneo-Caudal)
							if ("TRMM0017@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) != null) {
								sizeNode = (sizeNode != null ? sizeNode : createSize(tx, graphDb, lesionNode));
								sizeNode.setProperty(DIMENSION1_PROPERTY, valueFromString(num.getVALUE()));
							}
							// Size(Latero-Lateral)
							else if ("TRMM0018@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) != null) {
								sizeNode = (sizeNode != null ? sizeNode : createSize(tx, graphDb, lesionNode));
								sizeNode.setProperty(DIMENSION2_PROPERTY, valueFromString(num.getVALUE()));
							}
							//Size (Antero-Posterior)
							else if ("TRMM0019@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) != null) {
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
							// Upper Outer Quadrant of Breast (CSE)
							if ("RID29928@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Lower Outer Quadrant of Breast (CIE)
							else if ("RID29934@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Upper Inner Quadrant of Breast (CSI)
							else if ("RID29931@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Lower Inner Quadrant of Breast (CII)
							else if ("RID29937@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Lateral Region of Breast (LIE)
							else if ("RID29946@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							} 
							// Superior Region of Breast (LIS)
							else if ("RID29940@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Subareolar Region of Breast (LIInf)
							else if ("RID29952@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Medial Region of Breast (LIInt)
							else if ("RID29943@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Central Region of Breast
							else if ("RID29949@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Nipple
							else if ("RID29902@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Areola
							else if ("RID29911@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Axillary Tail
							else if ("RID28643@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Axilla
							else if ("RID2470@RADLEX".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Inframammary Sulcus
							else if ("TRMM0015@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Intermammary Sulcus
							else if ("TRMM0016@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
								getOrCreateLocation(tx, graphDb, num, lesionNode, template);
							}
							// Distance from the Nipple
							else if ("TRMM0012@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) != null) {
								getOrCreateLesionProperty(tx, graphDb, num, DISTANCE_TO_NIPPLE_PROPERTY, lesionNode, template);
							}
						}
					}
				}
			}
		}
		
	}
	/** 
	 * Associated Features
	 * 
	 */
	private final void loadBreastFeatures(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");

		// load associated findings
		Children item = container.getCHILDREN();
		if (item.getCODE() != null) {
			for (final Code code : item.getCODE()) {
				final String idField = Id.getId(code.getCONCEPTNAME());
				// Skin Changes
				if ("TRMM0042@TRENCADIS_MAMO".equals(idField) && code.getVALUE() != null) {
					getOrCreateFinding(tx, graphDb, code, breastNode, template);
				}
			}
		}
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				final String idField = Id.getId(num.getCONCEPTNAME());
				// Architectural Distortion
				if ("RID34261@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Duct Changes
				else if ("RID34382@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Edema
				else if ("RID4865@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
			}
		}
	}
	
	/**
	 * Special cases
	 * 
	 */
	public final void loadSpecialCases(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		
		Children item = container.getCHILDREN();
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				final String idField = Id.getId(num.getCONCEPTNAME());
				// Simple Cyst
				if ("TRMM0057@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Postsurgical Fluid Collection
				else if ("TRMM0055@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Fat Necrosis
				else if ("TRMM0056@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Clustered Microcysts
				if ("RID34371@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Complicated Cysts
				else if ("RID34372@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Mass in or on Skin
				else if ("RID34373@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Foreign Body Including Implants
				else if ("RID5425@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Intramammary Lymph Node
				else if ("RID34263@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Axillary Adenopathy
				else if ("RID34272@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Foreign Body Including Implants
				else if ("RID5425@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Axillary Adenopathy
				else if ("RID34272@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
			}
		}
		if (item.getCODE() != null) {
			for (final Code code : item.getCODE()) {
				final String idField = Id.getId(code.getCONCEPTNAME());
				// Vascular Abnormalities
				if ("TRMM0052@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateFinding(tx, graphDb, code, breastNode, template);
				}
			}
		}
	}
	
}