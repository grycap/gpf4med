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

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.Objects;

/**
 * Group wrapper.
 * @author Erik Torres <ertorser@upv.es>
 */
public class Group {

	/**
	 * Default group name.
	 */
	public final static String DEFAULT_GROUP = "gpf4med";

	private String group;

	public Group() { }

	public Group(final String group) {		
		setGroup(group);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(final String group) {
		this.group = group;
	}

	@Override
	public int hashCode() {		
		return Objects.hashCode(group);
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("group", group)
				.toString();
	}

	public static Group fromDefaultGroup() {
		return new Group(DEFAULT_GROUP);
	}

}