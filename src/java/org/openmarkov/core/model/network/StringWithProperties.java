package org.openmarkov.core.model.network;

/** @author marias */
public class StringWithProperties {

	// Attributes
	public String string;
	
	private AdditionalProperties additionalProperties;
	
	// Constructor
	/** @param string. <code>String</code> */
	public StringWithProperties(String string) {
		this.string = string;
		additionalProperties = new AdditionalProperties();
	}
	
	// Methods
	/** @return The object stored with <code>key</code> or <code>null</code> if
	 * it does not exists. <code>Object</code> */
	public Object get(String key) {
		return additionalProperties.get(string);
	}
	
	/** @param key. <code>String</code>
	 * @param value. <code>Object</code> */
	public void put(String key, String value) {
		additionalProperties.put(key, value);
	}
	
	/** @param properties. <code>AdditionalProperties</code> */
	public void put(AdditionalProperties properties) {
		additionalProperties = properties;
	}
	
	/** @param key. <code>String</code>
     * @return The object stored with <code>key</code> or <code>null</code> if
	 * it does not exists. <code>Object</code> */
	public Object remove(String key) {
		return additionalProperties.remove(key);
	}
	
	public String getString(){
		return string;
	}
	
	public AdditionalProperties getAdditionalProperties () {
		return additionalProperties;
	}

	public StringWithProperties copy() {
		StringWithProperties copiedStringWithProperties = new StringWithProperties(string);
		copiedStringWithProperties.put(additionalProperties);
		return copiedStringWithProperties;
	}
}
