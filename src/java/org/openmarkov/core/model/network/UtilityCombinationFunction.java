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

/** Codifies existing super-value node types using this numbers:
  * <ol start="0">
  * <li>SUM
  * <li>PRODUCT
  * </ol> 
  * @author manuel */
public enum UtilityCombinationFunction implements Serializable {
	SUM(0),
	PRODUCT(1);

	/** Existing types: SUM(0), PRODUCT(1) */
	private final int type;
	
	/** @param <code>type</code> An integer: SUM(0), PRODUCT(1), ... 
	 * @argCondition value >= 0 and 
	 * value < UtilityCombinationFunction.values().length */
	UtilityCombinationFunction(int type) {
    	this.type = type; 
    }

    /** @Cconsultation 
     * @return type. <code>int</code> */
    public int type() { 
    	return type; 
    }
    
    /** @param utilityCombinationFunction
     * @return utilityCombinationFunction.value. <code>int</code> */
    public int type(
    		UtilityCombinationFunction utilityCombinationFunction) {
    	return utilityCombinationFunction.type();
    }

}
