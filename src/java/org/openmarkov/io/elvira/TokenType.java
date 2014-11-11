/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.elvira;

import java.io.Serializable;

/** Types of tokens:
 * <ol>
 * <li>IDENT
 * <li>RESERVED 
 * <li>INTEGER
 * <li>DOUBLE
 * </ol>
 *  @author marias */
public enum TokenType implements Serializable {
	IDENTIFIER(0),
	RESERVED(1),
    INTEGER(2),
    DOUBLE(3);

	private final int value;

	TokenType(int value) {
		this.value = value;
	}

    public int value() { 
    	return value; 
    }

}
