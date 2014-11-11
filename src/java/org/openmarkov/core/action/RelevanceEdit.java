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

/**
 * <code>RelevanceEdit</code> is a simple edit that allows modify
 * the node relevance property. 
 *    
 * @version 1.0 21/12/10
 * @author Miguel Palacios
 */
@SuppressWarnings("serial")
public class RelevanceEdit extends SimplePNEdit {
	/**
	 * The last relevance before the edition
	 */
	private double lastRelevance;
	/**
	 * The new relevance after the edition
	 */
	private double newRelevance;
	/**
	 * The edited node
	 */
	private ProbNode probNode = null;
	/**
	 * Creates a new <code>RelevanceEdit</code> with the node and new relevance
	 * specified.
	 * @param probNode the node that will be edited
	 * @param newRelevance the new relevance
	 */
	public RelevanceEdit (ProbNode probNode, double newRelevance){
		super(probNode.getProbNet());
		this.lastRelevance = probNode.getRelevance();
		this.newRelevance = newRelevance;
		this.probNode = probNode;
	}	
	
	@Override
	public void doEdit() throws DoEditException {
		probNode.setRelevance(newRelevance);
	}
	@Override
	public void undo() {
		super.undo();
		probNode.setRelevance(lastRelevance);
	}
	/**
	 * Gets the new relevance after the edition
	 * @return the new relevance
	 */
	public double getNewRelevance(){
		return newRelevance;
	}
	/**
	 * Gets the new relevance before the edition
	 * @return the last relevance
	 */
	public double getLastRelevance(){
		return lastRelevance;
	}
}