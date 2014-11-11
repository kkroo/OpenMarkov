/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.potential;

/** @author marias
 * @version 1.0 */
public enum PotentialType {
	
	UNIFORM(0, "Uniform"),
	TABLE(1, "Table"),
	TREE_ADD(2, "Tree/ADD"),
	CYCLE_LENGTH_SHIFT(3, "CycleLengthShift"),
	SAME_AS_PREVIOUS(4, "SameAsPrevious"),
	SUM(5,"Sum"),
	PRODUCT(6, "Product"),
	GTABLE(7, "Gtable"),
	MIN(8, "Min"),
	MAX(9,"Max"),
	COMBINATION_FUNCTION(10, "CombinationFunction"),
	ICIMODEL (11, "ICIModel"),
	LINEAR_COMBINATION(12, "LinearCombination"),
	PROBABILITY_DENSITY(13, "ProbabilityDensity"),
	STAT_FUNCTION(14, "StatFunction"),
	TUNING(15,"Tuning"),
	WEIBULL_HAZARD(16, "Hazard (Weibull)"),
    EXPONENTIAL_HAZARD(17, "Hazard (Exponential)"),
    LINEAR_REGRESSION(18, "Linear regression"),
	DELTA(19, "Delta"),
	EXPONENTIAL(20, "Exponential");
	// Add here more potential types (when available)
	
private int type;
	
	private String label;
	
	PotentialType(int type, String label) {
		this.type = type;
		this.label = label;
	}
	
	public String toString() {
		return label;
	}
	
	public int getType() {
		return type;
	}
	
	public static PotentialType getEnumMember(String auxLabel){
		for (PotentialType role:values()){
			String u = role.toString();
			if (u.equals(auxLabel)){
				return role;
			} 
		}
		return null;
		
	}
	
	;
}
