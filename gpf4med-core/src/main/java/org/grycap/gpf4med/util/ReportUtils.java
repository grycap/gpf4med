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

package org.grycap.gpf4med.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.grycap.gpf4med.model.document.Document;
import org.grycap.gpf4med.model.document.Children;
import org.grycap.gpf4med.model.document.Code;
import org.grycap.gpf4med.model.document.ConceptName;
import org.grycap.gpf4med.model.document.Container;
import org.grycap.gpf4med.model.document.Date;
import org.grycap.gpf4med.model.document.Num;
import org.grycap.gpf4med.model.document.Text;
import org.grycap.gpf4med.model.document.Value;
import org.grycap.gpf4med.model.util.Id;

/**
 * Utilities to handle TRENCADIS reports.
 * @author Erik Torres <ertorser@upv.es>
 * @author Lorena Calabuig <locamo@inf.upv.es>
 */
public class ReportUtils {

	public @Nullable static String getMeaning(final ConceptName conceptName, final Document document) {
		checkArgument(conceptName != null, "Uninitialized or invalid concept name");
		final String id = Id.getId(conceptName);
		checkState(StringUtils.isNotBlank(id), "Concept name id is invalid");
		checkArgument(document != null, "Uninitialized or invalid report");
		final ConceptName found = find(conceptName, document.getCONTAINER());
		if (found != null) {
			return found.getCODEMEANING();				
		}
		return null;
	}

	private static @Nullable ConceptName find(final ConceptName conceptName, final Container item) {
		ConceptName found = null;
		// base case
		if (equalsIgnoreNoReferenceable(conceptName, item.getCONCEPTNAME())) {
			found = item.getCONCEPTNAME();
		}
		
		if (found == null && item.getCHILDREN() != null) {
			Children children = item.getCHILDREN();
			if (children.getCODE() != null) {
				for (final Code code : children.getCODE()) {
					if (equalsIgnoreNoReferenceable(conceptName, code.getCONCEPTNAME())) {
						found = code.getCONCEPTNAME();
						break;
					}
				}
			}
			if (children.getDATE() != null) {
				for (final Date date : children.getDATE()) {
					if (equalsIgnoreNoReferenceable(conceptName, date.getCONCEPTNAME())) {
						found = date.getCONCEPTNAME();
						break;
					}
				}			
			}
			if (children.getNUM() != null) {
				for (final Num num : children.getNUM()) {
					if (equalsIgnoreNoReferenceable(conceptName, num.getCONCEPTNAME())) {
						found = num.getCONCEPTNAME();
						break;
					}
				}
			}
			if (children.getTEXT() != null) {
				for (final Text text : children.getTEXT()) {
					if (equalsIgnoreNoReferenceable(conceptName, text.getCONCEPTNAME())) {
						found = text.getCONCEPTNAME();
						break;
					}
				}
			}
		}
		// recursive case
		if (found == null && item instanceof Container) {
			for (final Container item2 : ((Container)item).getCHILDREN().getCONTAINER()) {
				found = find(conceptName, item2);
				if (found != null) {
					break;
				}
			}
		}
		return found;
	}
	
	public static String getStringReport(Document report) {
		String result = "";
		Container container1 = report.getCONTAINER();
		result +=(printConceptName(container1.getCONCEPTNAME()));
		result +=("Children:\n");
		Children children1 = container1.getCHILDREN();
		for (final Text text1 : children1.getTEXT()) {
			result +=("\t" + printText(text1));
		}
		for (final Date date1 : children1.getDATE()) {
			result +=("\t" + printDate(date1));
		}
		for (final Container container2 : children1.getCONTAINER()) {
			result +=("\tContainer:\n");
			result +=("\t\t" + printConceptName(container2.getCONCEPTNAME()));
			result +=("\t\tChildren:\n");
			Children children2 = container2.getCHILDREN();
			for (final Text text2 : children2.getTEXT()) {
				result +=("\t\t\t" + printText(text2));
			}
			for (final Code code2 : children2.getCODE()) {
				result +=("\t\t\t" + printCode(code2));
			}
			for (final Num num2 : children2.getNUM()) {
				result +=("\t\t\t" + printNum(num2));
			}
			for (final Container container3 : children2.getCONTAINER()) {
				result +=("\t\t\tContainer:\n");
				result +=("\t\t\t\t" + printConceptName(container3.getCONCEPTNAME()));
				result +=("\t\t\t\tChildren:\n");
				Children children3 = container3.getCHILDREN();
				for (final Text text3 : children3.getTEXT()) {
					result +=("\t\t\t\t\t" + printText(text3));
				}
				for (final Code code3 : children3.getCODE()) {
					result +=("\t\t\t\t\t" + printCode(code3));
				}
				for (final Num num3 : children3.getNUM()) {
					result +=("\t\t\t\t\t" + printNum(num3));
				}
				for (final Container container4 : children3.getCONTAINER()) {
					result +=("\t\t\t\t\tContainer:\n");
					result +=("\t\t\t\t\t\t" + printConceptName(container4.getCONCEPTNAME()));
					result +=("\t\t\t\t\t\tChildren:\n");
					Children children4 = container4.getCHILDREN();
					for (final Text text4 : children4.getTEXT()) {
						result +=("\t\t\t\t\t\t\t" + printText(text4));
					}
					for (final Code code4 : children4.getCODE()) {
						result +=("\t\t\t\t\t\t\t" + printCode(code4));
					}
					for (final Num num4 : children4.getNUM()) {
						result +=("\t\t\t\t\t\t\t" + printNum(num4));
					}
				}
			}
		}
		return result;
	}
	
	public static boolean equalsIgnoreNoReferenceable(
			final ConceptName current, final ConceptName other) {
		if (other == null) {
			return false;
		}
		return Objects.equals(current.getCODEVALUE(), other.getCODEVALUE())
				&& Objects.equals(current.getCODESCHEMA(), other.getCODESCHEMA());
	}
	
	public static String getIdValue(Value value) {
		return value.getCODEVALUE() + "@" + value.getCODESCHEMA()
				+ " (" + value.getCODEMEANING() + ")";
	}
	
	public static String printConceptName(ConceptName conceptName) {
		return "ConceptName: " + Id.getId(conceptName)
				+ " (" + conceptName.getCODEMEANING() + ")\n";
	}
	
	public static String printNum(Num num) {
		return "Num: " + Id.getId(num.getCONCEPTNAME())
				+ " (" + num.getCONCEPTNAME().getCODEMEANING() + ")"
				+ " - Value " + "(" + num.getUNITMEASUREMENT().getCODEMEANING() + "): "
				+ num.getVALUE() + "\n";
	}
	
	public static String printCode(Code code) {
		return "Code: " + Id.getId(code.getCONCEPTNAME())
				+ " (" + code.getCONCEPTNAME().getCODEMEANING() + ")"
				+ " - Value: " + getIdValue(code.getVALUE()) + "\n";
	}
	
	public static String printText(Text text) {
		return "Text: " + Id.getId(text.getCONCEPTNAME())
				+ " (" + text.getCONCEPTNAME().getCODEMEANING()+ ")"
				+ " - Value: " + text.getVALUE() + "\n";
	}
	
	public static String printDate(Date date) {
		return "Date: " + Id.getId(date.getCONCEPTNAME())
				+ " (" + date.getCONCEPTNAME().getCODEMEANING() + ")"
				+ " - Value: " + date.getVALUE() + "\n";
	}
	
}