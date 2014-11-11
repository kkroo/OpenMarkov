/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.configuration;

public interface Configuration extends DefaultConfiguration {

	/** @return Component name. <code>String</code>*/
	public String getComponentName();
	
	/** @param name <code>String</code>. Property name.
	 * @return An <code>Object</code> whose name = <code>name</code>. */
	public Object getProperty(String name);
	
	/** Creates or modifies a property.
	 * @param name <code>String</code>. Property name.
	 * @param value <code>Object</code>. */
	public void setProperty(String name, Object value);
	
}
