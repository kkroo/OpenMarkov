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
public class PurposeEdit extends SimplePNEdit {
	/**
	 * The last purpose before the edition
	 */
	private String lastPurpose;
	/**
	 * The new purpose after the edition
	 */
	private String newPurpose;
	/**
	 * The edited node
	 */
	private ProbNode probNode = null;
	/**
	 * Creates a new <code>PurposeEdit</code> with the node and its new purpose.
	 * @param probNode the edited node
	 * @param newPurpose the new purpose
	 */
	public PurposeEdit (ProbNode probNode, String newPurpose){
		super(probNode.getProbNet());
		this.lastPurpose = probNode.getPurpose();
		this.newPurpose = newPurpose;
		this.probNode = probNode;
	}	
	
	@Override
	public void doEdit() throws DoEditException {
		probNode.setPurpose(newPurpose);
	}
	@Override
	public void undo() {
		super.undo();
		probNode.setPurpose(lastPurpose);
	}
	/**
	 * Gets the new purpose after the edition
	 * @return the new purpose
	 */
	public String getNewPurpose(){
		return newPurpose;
	}
	/**
	 * Gets the last purpose before the edition
	 * @return the last purpose
	 */
	public String getLastPurpose(){
		return lastPurpose;
	}
}

