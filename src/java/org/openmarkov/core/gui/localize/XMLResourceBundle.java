/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.localize;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * @author mluque
 * ResourceBundle based in XML properties files.
 *
 */
public class XMLResourceBundle extends ResourceBundle {
	private XMLProperties props;
    public XMLResourceBundle(InputStream stream) throws IOException {
        props = new XMLProperties();
        //props.loadFromXML(stream);
        props.load(stream);
    }
    protected Object handleGetObject(String key) {
        return props.getProperty(key);
    }
   
	@Override
	public Enumeration<String> getKeys() {
		return (Enumeration<String>) props.keySet();
	}
}
