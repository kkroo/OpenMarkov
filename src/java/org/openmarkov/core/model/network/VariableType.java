/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network;

import java.io.Serializable;

/** @author manuel */
public enum VariableType implements Serializable {
	FINITE_STATES(0, "finiteStates"),
	NUMERIC(1, "numeric"),
    DISCRETIZED(2, "discretized");

	private int value;
	
	private String name;

	VariableType(int value, String name) {
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
