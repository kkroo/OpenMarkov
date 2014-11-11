/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network;

import java.util.HashMap;
import java.util.Set;

/** This class gives the possibility to store new attributes in several classes.
 * <p>
 * All those attributes are accessed with a key (<code>String</code>) and the
 * value is an <code>Object</code>. */
public class AdditionalProperties {

	// Attributes
	protected HashMap<String, String> information;
	
	// Constructor
	public AdditionalProperties() {
		information = new HashMap<String, String>();
	}
	
	// Methods
	/** @return The object stored with <code>key</code> or <code>null</code> if
	 * it does not exists. */
	public Object get(String key) {
		return information.get(key);
	}
	
	/** @param key. <code>String</code>
	 * @param value. <code>Object</code> */
	public void put(String key, String value) {
		information.put(key, value);
	}
	
	/** @return The object stored with <code>key</code> or <code>null</code> if
	 * it does not exists. */
	public Object remove(String key) {
		return information.remove(key);
	}
	
	public int size() {
		return information.size();
	}
	
	/** @return The set of keys. <code>Set</code> of <code>String</code> */
	public Set<String> getKeySet() {
		return information.keySet();
	}
	
	/** @return The object stored with <code>key</code> or <code>null</code> if
	 * it does not exists.
	 * @precondition The object must be an <code>Integer</code> */
	/*public Integer getIntegerValue(String key) {
		return (Integer)information.get(key);
	}*/
	
}
