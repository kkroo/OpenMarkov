/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

/**
 * <code>NodeNameEdit</code> is a simple edit that allow modify the node
 * name.
 *   
 * @version 1.0 21/12/10
 * @author Miguel Palacios
 */
@SuppressWarnings("serial")
public class NodeNameEdit extends SimplePNEdit {
	/**
	 * Current node name
	 */
	private String previousName;
	/**
	 * New node name
	 */
	private String newName;
	/**
	 * The node edited
	 */
	private List<Variable> variables = null;
	/**
	 * Creates a new <code>NodeNameEdit</code> with the node and new name 
	 * specified.
	 * @param probNode the node that will be modified
	 * @param newName the new name of the node
	 */
	public NodeNameEdit (ProbNode probNode, String newName){
		super(probNode.getProbNet());
		variables = new ArrayList<Variable>();
		for(Variable variable : probNode.getProbNet().getVariables())
		{
			if(variable.getBaseName().equals(probNode.getVariable().getBaseName()))
			{
				variables.add(variable);
			}
		}
		this.previousName = probNode.getVariable().getBaseName();
		this.newName = newName;
	}	
	
	@Override
	public void doEdit() throws DoEditException {
		for(Variable variable : variables)
		{
			variable.setBaseName(newName);
		}
	}
	@Override
	public void undo() {
		super.undo();
		for(Variable variable : variables)
		{
			variable.setBaseName(previousName);
		}
	}
	/**
	 * Gets the new name of the node
	 * 
	 * @return
	 * 		the new name of the node
	 */
	public String getNewName(){
		return newName;
	}
	/**
	 * Gets the previous name of the node
	 * 
	 * @return
	 * 		the previous name of the node
	 */
	public String getPreviousName(){
		return previousName;
	}
}
