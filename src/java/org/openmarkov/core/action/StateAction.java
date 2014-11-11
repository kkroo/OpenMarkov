/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import java.io.Serializable;

/**
 * Defines the different actions on the state an PartitionedInterval objects  
 * @author mpalacios
 * @version	1.0*/
public enum StateAction implements Serializable {
	ADD(0),
	REMOVE(1),
	RENAME(2),
    UP(3),
	DOWN(4),
	MODIFY_DELIMITER_INTERVAL(5),
	MODIFY_VALUE_INTERVAL(6);
	

	private final int value;

	StateAction(int value) {
		this.value = value;
	}

    public int value() { 
    	return value; 
    }
}
