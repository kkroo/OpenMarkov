/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.configuration;

public enum OperatingSystem {

	WINDOWS(0, "Windows"),
	LINUX(1, "Linux"),
    OTHER(2, "Other");

	private int value;
	
	private String name;

	OperatingSystem(int value, String name) {
		this.value = value;
		this.name = name;
	}

    public int value() { 
    	return value; 
    }
    
    public String toString() {
    	return name;
    }
	
}
