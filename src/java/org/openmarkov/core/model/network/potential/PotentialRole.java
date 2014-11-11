/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * 
 */
package org.openmarkov.core.model.network.potential;

/** @author marias
 * @version 1.0 */
public enum PotentialRole {

	CONDITIONAL_PROBABILITY(0, "conditionalProbability"),
	DECISION(1, "decision"),
	JOINT_PROBABILITY(2, "joinProbability"),
	POLICY(3, "policy"),
	UTILITY(4, "utility"),
	LINK_RESTRICTION(5,"linkRestriction"),
	UNSPECIFIED(6,"unspecified");
	
	private int type;
	
	private String label;
	
	PotentialRole(int type, String label) {
		this.type = type;
		this.label = label;
	}
	
	public String toString() {
		return label;
	}
	
	public int getType() {
		return type;
	}
	
	public static PotentialRole getEnumMember(String auxLabel){
		for (PotentialRole role:values()){
			String u = role.toString();
			if (u.equals(auxLabel)){
				return role;
			} 
		}
		return null;
		
	}


}
