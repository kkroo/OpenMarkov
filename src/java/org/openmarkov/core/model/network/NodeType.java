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

/** Codifies existing node types using this numbers:
  * <ol start="0">
  * <li>CHANCE
  * <li>DECISION
  * <li>UTILITY
  * <li>SV_SUM
  * <li>SV_PRODUCT
  * <li>COST
  * <li>EFFECTIVENESS
  * <li>CE (Cost-Effectiveness)
  * </ol>
  * @author manuel
  * @author fjdiez */
public enum NodeType implements Serializable {
	CHANCE(0, "chance"),
	DECISION(1, "decision"), 
	UTILITY(2, "utility"),
	SV_SUM(3, "svSum"),
	SV_PRODUCT(4, "svProduct"),
	COST(5, "cost"),
	EFFECTIVENESS(6, "effectiveness"),
	CE(7, "ce");//  (Cost-Effectiveness)
	
	/** Existing types: CHANCE(0), DECISION(1), UTILITY(2), ... */
	private int type;
	
	private String name;

	/** @param <code>type</code> An integer: CHANCE(0), DECISION(1), ... 
	 * @argCondition value >= 0 and value < NodeType.values().length */
    NodeType(int type, String name) {
    	this.type = type;
    	this.name = name;
    }

    /** @return <code>type</code> */
    public int type() { 
    	return type; 
    }
    
    /** @param nodeType
     * @return nodeType.value. <code>int</code> */
    public static int type(NodeType nodeType) {
    	return nodeType.type();
    }
    
    public String toString() {
    	return name;
    }
    
}
