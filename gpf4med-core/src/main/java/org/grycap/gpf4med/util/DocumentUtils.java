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

import org.grycap.gpf4med.model.ConceptName;

/**
 * Utilities to handle TRENCADIS documents.
 * @author Erik Torres <ertorser@upv.es>
 *
 */
public final class DocumentUtils {

	public static boolean isEmpty(final ConceptName conceptName) {
		boolean isEmpty = true;
		if (conceptName != null) {
			final boolean tmp[] = { conceptName.getCodeSchema() == null,
					conceptName.getCodeValue() == null,
					conceptName.getCodeMeaning() == null,
					conceptName.getCodeMeaning2() == null };
			for (int i = 0; i < tmp.length && isEmpty; i++) {
				isEmpty = isEmpty && tmp[i];
			}
		}
		return isEmpty;
	}

}