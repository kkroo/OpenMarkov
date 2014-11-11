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

import org.apache.log4j.Logger;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.PotentialOperations;

/** Removes a node performing this steps:<ol>
 * <li>Collect all potentials with this node variable
 * <li>Multiply and eliminates the variable
 * <li>Removes the collected potentials
 * <li>Adds to the <code>probNet</code> the new potential
 * <li>Adds links between the node siblings
 * <li>Remove links between the node and its children, parents and siblings
 * <li>Removes the node
 * </ol> */
@SuppressWarnings("serial")
public class CompoundRemoveNodeEdit extends CompoundPNEdit {

	// Attributes
	protected Variable variable;
	
	protected NodeType nodeType;
	
	protected List<Node> parents;

	protected List<Node> children;

	protected List<Node> siblings;
	
	protected List<Potential> marginalizedPotentials;

	protected List<Potential> allPotentials;

	private Logger logger;
	
	// Constructor
	/** @param probNet </code>ProbNet</code>
	 * @param variable <code>Variable</code> */
	public CompoundRemoveNodeEdit(ProbNet probNet, Variable variable) {
		super(probNet);
		this.variable = variable;
		this.nodeType = probNet.getProbNode(variable).getNodeType();
		this.logger = Logger.getLogger(CompoundPNEdit.class);
	}

	public void generateEdits() {
		ProbNode probNode = probNet.getProbNode(variable);

		// gets neighbors of this node
		parents = probNode.getNode().getParents();
		children = probNode.getNode().getChildren();
		siblings = probNode.getNode().getSiblings();
		
		// collect potentials of this node ...
		List<TablePotential> potentialsVariable = new ArrayList<>();
		
		for (Potential pot:probNet.extractPotentials(variable)){
			potentialsVariable.add((TablePotential)pot);
		}
		
		Potential newPotential = null;
		try {
			// ... multiply and eliminate the variable
			newPotential = PotentialOperations.multiplyAndEliminate(
				potentialsVariable, variable);
		} catch (Exception e) {
			logger.fatal (e);
		}
		
		List<Variable> variablesNewPotential = newPotential.getVariables();
		if (variablesNewPotential != null && variablesNewPotential.size() > 0) {
			edits.add(new AddPotentialEdit(probNet, newPotential));
		}
		for (Potential potential : potentialsVariable) {
			edits.add(new RemovePotentialEdit(probNet, potential));
		}

		// add a link between the siblings of the removed node
		for (Node node1 : siblings) {
			for (Node node2 : siblings) {
				if ((node1 != node2) && (!node1.isSibling(node2))) {
					addEdit(new AddLinkEdit(probNet, 
						((Variable)node1.getObject()), 
						((Variable)node2.getObject()), false));
				}
			}
		}
		
		// remove links between probNode and its parents, children and siblings
		for (Node parent : parents) {
			addEdit(new RemoveLinkEdit(probNet, 
				((ProbNode)parent.getObject()).getVariable(), 
				probNode.getVariable(), true));
		}
		for (Node child : children) {
			addEdit(new RemoveLinkEdit(probNet,	probNode.getVariable(),
				((ProbNode)child.getObject()).getVariable(), true));
		}
		for (Node sibling : siblings) {
			addEdit(new RemoveLinkEdit(probNet, 
				((ProbNode)sibling.getObject()).getVariable(), 
				probNode.getVariable(), false));
		}

		// generate edit related to remove the variable
		addEdit(new RemoveNodeEdit(probNet, variable));
	}
	
	public void undo() {
		super.undo();
	}

	/** @return variable <code>Variable</code> */
	public Variable getVariable() {
		return variable;
	}

	/** @return <code>String</code> */
	public String toString() {
		return new String("CompoundRemoveNodeEdit: " +	variable);
	}

}
