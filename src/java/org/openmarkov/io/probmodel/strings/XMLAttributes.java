/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.probmodel.strings;

public enum XMLAttributes {
	BELONGS_TO("belongsTo"),
	DIRECTED("directed"),
	DISTRIBUTION("distribution"),
	FORMAT_VERSION("formatVersion"),
	FUNCTION("function"),
	LABEL("label"),
	NAME("name"),
	NUMERIC_VALUE("numericValue"),
	ORDER("order"),
	ROLE("role"),
	TYPE("type"),
	VALUE("value"),
	VAR1("var1"),
	VAR2("var2"),
	X("x"),
	Y("y"),
	REF("ref"),
	TIMESLICE("timeSlice"),
	//TODO OOBN start
	IS_INPUT("isInput");
	//TODO OOBN end
	
	private int type;
	
	private String name;
	
	XMLAttributes(String name) {
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
