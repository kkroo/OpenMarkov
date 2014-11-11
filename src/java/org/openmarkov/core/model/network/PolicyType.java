/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network;

public enum PolicyType {
	OPTIMAL("Optimal"),
	DETERMINISTIC("Deterministic"),
	PROBABILISTIC("Probabilistic");
	
	String name;
	int type;
	
	PolicyType(String name) {
		this.type = this.ordinal();
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public int getType() {
		return type;
	}


}
