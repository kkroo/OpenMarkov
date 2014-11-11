/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.probmodel.strings;

public enum XMLCompoundConstraints {
	BAYESIAN_NETWORK(0, "BayesianNetwork"),
	DINAMIC_BAYESIAN_NETWORK(1, "DinamicBayesianNetwork"),
	INFLUENCE_DIAGRAM(2, "InfluenceDiagram"),
	MDP(3, "MDP"),
	POMDP(4, "POMDP");
	
	private int type;
	
	private String name;
	
	XMLCompoundConstraints(int type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public int getType() {
		return type;
	}

}
