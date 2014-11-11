/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
/**
 * <code>PurposeEdit</code> is a simple edit that allows modify
 * the node purpose property. 
 *    
 * @version 1.0 21/12/10
 * @author Miguel Palacios
 */
public class PrecisionEdit extends SimplePNEdit {
	/**
	 * The last purpose before the edition
	 */
	private Double lastPrecision;
	/**
	 * The new purpose after the edition
	 */
	private double newPrecision;
	/**
	 * The edited node
	 */
	private ProbNode probNode = null;
	/**
	 * Creates a new <code>PurposeEdit</code> with the node and its new purpose.
	 * @param probNode the edited node
	 * @param newPurpose the new purpose
	 */
	public PrecisionEdit (ProbNode probNode, double newPrecision){
		super(probNode.getProbNet());
		this.lastPrecision = probNode.getVariable().getPrecision();
		this.newPrecision = newPrecision;
		this.probNode = probNode;
	}	
	
	@Override
	public void doEdit() throws DoEditException {
		probNode.getVariable().setPrecision(newPrecision);
	}
	@Override
	public void undo() {
		super.undo();
		probNode.getVariable().setPrecision(lastPrecision);
	}
	/**
	 * Gets the new purpose after the edition
	 * @return the new purpose
	 */
	public Double getNewPrecision(){
		return newPrecision;
	}
	/**
	 * Gets the last purpose before the edition
	 * @return the last purpose
	 */
	public Double getLastPrecision(){
		return lastPrecision;
	}
}

