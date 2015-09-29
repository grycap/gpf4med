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
 * Loads magnetic resonances to the graph.
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public class MagneticResonanceCreator extends BaseDocumentCreator {

	private final static Logger LOGGER = LoggerFactory.getLogger(MagneticResonanceCreator.class);

	public static final String SIDE_PROPERTY = "side";
	public static final String SHAPE_PROPERTY = "shape";
	public static final String MARGIN_PROPERTY = "margin";
	public static final String NON_CIRCUMSCRIBED_MARGIN_PROPERTY = "non-circumscribed_margin";
	public static final String DISTRIBUTION_PROPERTY = "distribution";
	public static final String DIMENSION1_PROPERTY = "dimension1";
	public static final String DIMENSION2_PROPERTY = "dimension2";
	public static final String DIMENSION3_PROPERTY = "dimension3";
	public static final String DISTANCE_TO_NIPPLE_PROPERTY = "distance_to_nipple";
	public static final String DISTANCE_TO_SKIN_PROPERTY = "distance_to_skin";
	public static final String DISTANCE_TO_THORAX_PROPERTY = "distance_to_thorax";
	public static final String SKIN_INVASION_PROPERTY = "skin_invasion";
	public static final String IMPLANT_RUPTURE_PROPERTY = "implant_rupture";
	public static final String IMPLANT_LOCATION_PROPERTY = "implant_location";
	public static final String IMPLANT_MATERIAL_PROPERTY = "implant_material";
	public static final String INTERNAL_ENHANCEMENT_CHARACTERISTICS_PROPERTY = "internal_enhancement_charac";
	public static final String INTERNAL_ENHANCEMENT_PATTERN_PROPERTY = "internal_enhancement_pattern";

	public MagneticResonanceCreator() {
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
						} else {
							LOGGER.info("Ignoring unknown container: " + id);
						}
					}
				}
			}
			tx.success();
		} catch (Exception e) {
			LOGGER.warn("Failed to create magnetic resonance in the graph", e);
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
					// No-enhancing Findings
					if ("TRMM0080@TRENCADIS_MAMO".equals(idField)) {
						for (final Container container3 : children2.getCONTAINER()) {
							final String idBreast = Id.getId(container3.getCONCEPTNAME());
							// Right breast
							if ("TRMM0081@TRENCADIS_MAMO".equals(idBreast)) {
								loadNoEnhancingFindings(tx, graphDb, rightBreastNode, container3, template);
							}
							// Left breast
							else if ("TRMM0082@TRENCADIS_MAMO".equals(idBreast)) {
								loadNoEnhancingFindings(tx, graphDb, leftBreastNode, container3, template);
							}
						}
					}
					// Fat Containing Lesions
					if ("TRMM0090@TRENCADIS_MAMO".equals(idField)) {
						for (final Container container3 : children2.getCONTAINER()) {
							final String idBreast = Id.getId(container3.getCONCEPTNAME());
							// Right breast
							if ("TRMM0091@TRENCADIS_MAMO".equals(idBreast)) {
								loadFatContainingLesions(tx, graphDb, rightBreastNode, container3, template);
							}
							// Left breast
							else if ("TRMM0092@TRENCADIS_MAMO".equals(idBreast)) {
								loadFatContainingLesions(tx, graphDb, leftBreastNode, container3, template);
							}
						}
					}
					// Implants
					if ("TRMM0099@TRENCADIS_MAMO".equals(idField)) {
						for (final Container container3 : children2.getCONTAINER()) {
							final String idBreast = Id.getId(container3.getCONCEPTNAME());
							// Right breast
							if ("TRMM00100@TRENCADIS_MAMO".equals(idBreast)) {
								loadImplants(tx, graphDb, rightBreastNode, container3, template);
							}
							// Left breast
							else if ("TRMM00101@TRENCADIS_MAMO".equals(idBreast)) {
								loadImplants(tx, graphDb, leftBreastNode, container3, template);
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
	
	/** 
	 * Observations Section
	 */
	private final void loadBreastLesion(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		final Node lesionNode = createLesion(tx, graphDb, container, breastNode, template);
		Node sizeNode = null;
		// Load lesions and associated findings
		Children item = container.getCHILDREN();
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
				// Non-Circumscribed Margin Type
				else if ("TRMM0036@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, NON_CIRCUMSCRIBED_MARGIN_PROPERTY, lesionNode, template);
				}
				// Internal Enhancement Characteristics
				else if ("TRMM0078@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, INTERNAL_ENHANCEMENT_CHARACTERISTICS_PROPERTY, lesionNode, template);
				}
				// Distribution Pattern
				else if ("RID5958@RADLEX".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, DISTRIBUTION_PROPERTY, lesionNode, template);
				}
				// Internal Enhancement Pattern
				else if ("RID5958@RADLEX".equals(idField)) {
					getOrCreateLesionProperty(tx, graphDb, code, INTERNAL_ENHANCEMENT_PATTERN_PROPERTY, lesionNode, template);
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
								lesionNode.setProperty(DISTANCE_TO_NIPPLE_PROPERTY, valueFromString(num.getVALUE()));
							}
							// Distance to Skin
							else if ("TRMM0097@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) != null) {
								lesionNode.setProperty(DISTANCE_TO_SKIN_PROPERTY, valueFromString(num.getVALUE()));
							}
							// Distance to Thorax Wall
							else if ("TRMM0098@TRENCADIS_MAMO".equals(idNum) && num.getVALUE() != null && valueFromString(num.getVALUE()) != null) {
								lesionNode.setProperty(DISTANCE_TO_THORAX_PROPERTY, valueFromString(num.getVALUE()));
							}
						}
					}
				}
			}
		}
		
	}
	/** 
	 * Associated Features
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
				// Skin Invasion
				if ("RID34318@RADLEX".equals(idField) && code.getVALUE() != null) {
					getOrCreateFinding(tx, graphDb, code, breastNode, template);
				}
			}
		}
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				final String idField = Id.getId(num.getCONCEPTNAME());
				// Nipple Invasion
				if ("TRMM0086@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Nipple Invasion
				else if ("TRMM0132@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Nipple Retraction
				else if ("RID34269@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Skin Retraction
				else if ("RID34383@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Skin Thickening
				else if ("RID34270@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Axillary Adenopathy
				else if ("RID34272@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Pectoralis Muscle Invasion
				else if ("RID34319@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Chest Wall Invasion
				else if ("RID34320@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
			}
		}
	}
	
	/**
	 * No-enhancing findings
	 * 
	 */
	public final void loadNoEnhancingFindings(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		
		Children item = container.getCHILDREN();
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				final String idField = Id.getId(num.getCONCEPTNAME());
				// Postoperative Collections (Hematoma/Seroma)
				if ("TRMM0083@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Post-therapy Skin Thickening and Trabecular Thickening
				else if ("TRMM0084@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Signal Void from Foreign bodies, clips, etc.
				else if ("TRMM0085@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Architectural Distorsion
				else if ("TRMM0132@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Ductal Pre-contrast High Ductal Signal
				if ("RID34315@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Cyst
				else if ("RID3890@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Non-mass-like Enhancement
				else if ("RID34342@RADLEX".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
			}
		}
	}
	
	/**
	 * Fat Containing Lesions
	 */
	public final void loadFatContainingLesions(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		
		Children item = container.getCHILDREN();
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				final String idField = Id.getId(num.getCONCEPTNAME());
				// Fat Necrosis
				if ("TRMM0094@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Hamartoma
				else if ("TRMM0095@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Postoperative Seroma/Hematoma with Fat
				else if ("TRMM0096@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
			}
		}
	}
	
	/**
	 * Implants
	 */
	public final void loadImplants(final Transaction tx, final GraphDatabaseService graphDb, final Node breastNode,
			final Container container, final Template template) {
		checkArgument(container != null && container.getCONCEPTNAME() != null, 
				"Uninitialized or invalid container");
		
		Children item = container.getCHILDREN();
		if (item.getNUM() != null) {
			for (final Num num : item.getNUM()) {
				final String idField = Id.getId(num.getCONCEPTNAME());
				// Abnormal Implant Contour
				if ("TRMM0110@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Radical Folds
				else if ("TRMM0112@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Subcapsular Line
				else if ("TRMM0113@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Keyhole Sign (Teardrop, noose)
				else if ("TRMM0114@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Linguine Sign
				else if ("TRMM0115@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Breast
				else if ("TRMM0118@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Lymph Nodes (Axil)
				else if ("TRMM0119@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Lymph Nodes (Internal Mammary Catena)
				else if ("TRMM0120@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Water Droplets
				else if ("TRMM0121@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
				// Peri-implant Fluid
				else if ("TRMM0122@TRENCADIS_MAMO".equals(idField) && num.getVALUE() != null && valueFromString(num.getVALUE()) == 1.0d) {
					getOrCreateFinding(tx, graphDb, num, breastNode, template);
				}
			}
		}
		if (item.getCODE() != null) {
			for (final Code code : item.getCODE()) {
				final String idField = Id.getId(code.getCONCEPTNAME());
				// Implant Material and Lumen Type
				if ("TRMM0102@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateFinding(tx, graphDb, code, breastNode, template);
				}
				// Implant Location
				if ("TRMM0107@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateFinding(tx, graphDb, code, breastNode, template);
				}
				// Type of Ruptured Silicone
				else if ("TRMM0133@TRENCADIS_MAMO".equals(idField)) {
					getOrCreateFinding(tx, graphDb, code, breastNode, template);
				}
			}
		}
	}	

}