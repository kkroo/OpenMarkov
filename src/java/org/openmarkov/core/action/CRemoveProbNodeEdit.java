/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import java.util.List;

import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;


/** <code>CRemoveProbNodeEdit</code> is an compound edit that removes a node 
 * performing this steps:<ol>
 * <ol> 
 * <li>Remove links between the node and its children
 * <li> Remove links between the node and its children
 * <li> Removes the node
 * </ol> */
@SuppressWarnings("serial")
public class CRemoveProbNodeEdit extends CompoundPNEdit{ //implements UsesVariable{

	// Attributes
	
	protected ProbNode probNode;
	
	protected NodeType nodeType;
	
	protected List<Node> parents;

	protected List<Node> children;

	protected List<Node> siblings;
	
	protected List<Potential> marginalizedPotentials;

	protected List<Potential> allPotentials;
	
	protected ProbNet probNet;
	
	// Constructor
	/** @param probNet </code>ProbNet</code>
	 * @param variable <code>Variable</code> */
	public CRemoveProbNodeEdit(ProbNet probNet, ProbNode probNode) {
		super(probNet);
		this.probNet = probNet;
		this.probNode = probNode;
		this.nodeType = probNode.getNodeType();
	}

	public void generateEdits() {
		// gets neighbors of this node
		parents = probNode.getNode().getParents();
		children = probNode.getNode().getChildren();
		
		for (Node parent : parents) {
			String name = (String) ((ProbNode)(parent.getObject())).getName();
			try {
				addEdit(new RemoveLinkEdit(probNode.getProbNet(),probNet.getVariable(name), probNet.getVariable(probNode.getName()), true));
			} catch (ProbNodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (Node child : children) {
			try {
				addEdit(new RemoveLinkEdit(probNode.getProbNet(), probNet.getVariable(probNode.getName()), probNet.getVariable(((ProbNode)child.
						getObject()).getName()), true));
			} catch (ProbNodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// add edit to remove the variable
		addEdit(new RemoveProbNodeEdit(probNet, probNode));
		
		// add edit to add the new potential
		//edits.add(new AddPotentialEdit(probNet, newPotential));
	}
	
	public void undo() {
		super.undo();
	}

	/** @return variable <code>Variable</code> */
	public Variable getVariable() {
		return probNode.getVariable();
	}

	/** @return <code>String</code> */
	public String toString() {
		return new String("CompoundRemoveNodeEdit: " +	probNode.getName());
	}

}