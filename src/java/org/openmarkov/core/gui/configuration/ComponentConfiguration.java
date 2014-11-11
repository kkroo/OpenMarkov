/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

import org.openmarkov.core.model.network.Util;



/** This class contains the OpenMarkov system variables and methods to change and 
 * recover those variables. */
@SuppressWarnings("serial")
public class ComponentConfiguration 
        implements Configuration, Serializable {

	// Attributes
	private HashMap<String, Object> componentVariables;

	/** @frozen */
	private String componentName;

	// Constructor
	/** @param componentName. <code>String</code> */
	public ComponentConfiguration(String componentName) {
		this.componentName = componentName;
		componentVariables = new HashMap<String, Object>();
	}
	
	// Methods
	public void generateDefaultConfiguration() {
    	String initialPath = System.getProperty("user.dir");
    	componentVariables.put("initialPath", initialPath + "\\");
    	
    	Properties properties = System.getProperties();
    	String osName = properties.getProperty("os.name");
    	if (osName.toLowerCase().contains("windows")) {
    		componentVariables.put("windows", new Boolean(true));
    	} else {
    		componentVariables.put("windows", new Boolean(false));
    	}
    	if (osName.toLowerCase().contains("linux") || 
    			osName.toLowerCase().contains("unix")) {
    		componentVariables.put("unix", new Boolean(true));    		
    	} else {
    		componentVariables.put("unix", new Boolean(false));
    	}

    	String netsPath = initialPath + "openmarkov\\nets\\";
    	String localizePath = "openmarkov\\io\\localize\\";
    	String ceNetTest = "tests\\openmarkov\\ce\\";
    	String netTest = "tests\\openmarkov\\nets\\";
    	String ioTest = "tests\\openmarkov\\nets\\";

    	if ((Boolean)componentVariables.get("unix") == true) {
    		netsPath = Util.windows2unixPath(netsPath);
    		localizePath = Util.windows2unixPath(localizePath);
    		ceNetTest = Util.windows2unixPath(ceNetTest);
    		netTest = Util.windows2unixPath(netTest);
    		ioTest = Util.windows2unixPath(ioTest);
    	}
    	
    	componentVariables.put("netsDirectory", netsPath);
    	componentVariables.put("localizeDirectory", localizePath);
    	componentVariables.put("ceTestDirectory", ceNetTest);
    	componentVariables.put("netsTestDirectory", netTest);
    	componentVariables.put("ioTestDirectory", ioTest);    	
	}
	
	public static OperatingSystem getOperatingSystem() {
		OperatingSystem operatingSystem = null;
    	Properties properties = System.getProperties();
    	String osName = properties.getProperty("os.name");
    	if (osName.toLowerCase().contains("windows")) {
    		operatingSystem = OperatingSystem.WINDOWS;
    	} else if (osName.toLowerCase().contains("linux")) { 
       		operatingSystem = OperatingSystem.LINUX;
    	} else {
    		operatingSystem = OperatingSystem.OTHER;
    	}
    	return operatingSystem;
	}

    public void setProperty(String name, Object value) {
    	componentVariables.put(name, value);
    }
    
    public Object getProperty(String name) {
    	return componentVariables.get(name);
    }
    
    public String getComponentName() {
    	return componentName;
	}
    
}
