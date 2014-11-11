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

/** @author marias */
public enum ReservedWord implements Serializable {

	ASSIGNMENT,
	REMARK,
	LEFTP,
	RIGHTP,
	CONDITIONED,
	LEFTSB,
	RIGHTSB,
	LEFTCB,
	RIGHTCB,
	COMMA,
	ACTIVE,
	AND, 
	AUTHOR,
	BNET,
	CAUSAL_MAX,
	CAUSAL_MIN,
	CHANCE,
	COMMENT,
	CONTINUOUS,
	DECISION,
	DEFAULT,
	DETERMINISTIC,
	FALSE,
	FINITE,
	FINITE_STATES,
	FUNCTION,
	GENERALIZED_MAX,
	GENERALIZED_TABLE,
	HENRIONVSDIEZ,
	IDIAGRAM,
	IDIAGRAMSV,
	KIND_OF_NODE,
	KIND_OF_GRAPH,
	KIND_OF_RELATION,
	LINK,
	MAX,
	MIN,
	NAME,
	NAME_OF_RELATION,
	NODE,
	NUM_STATES,
	OR,
	POSX,
	POSY,
	POTENTIAL,
	PRECISION,
	PRODUCT,
	PURPOSE,
	RELATION,
	RELEVANCE,
	STATES,
	SUM,
	SUPERVALUE,
	TABLE,
	TITLE,
	TRUE,
	TYPE_OF_VARIABLE,
	UNIT,
	UTILITY,
	UTILITYCOMBINATION,
	VALUES,
	VARIABLE,
	VERSION,
	VISUALPRECISION,
	WHENCHANGED,
	WHOCHANGED;

	private final int value;

	ReservedWord() {
		this.value = this.ordinal();
	}

    public int value() { 
    	return value; 
    }

}
