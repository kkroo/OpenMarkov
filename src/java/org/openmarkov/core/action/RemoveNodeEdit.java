/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import org.apache.log4j.Logger;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class RemoveNodeEdit extends SimplePNEdit implements UsesVariable {

	// Attributes
	/** Node associated to variable */
	private ProbNode probNode;
	
	private NodeType kindOfNode;
	
	protected Variable variable;
	
	private Logger logger;
	
	// Constructor
	/** @param probNet <code>ProbNet</code>
	 * @param variable <code>Variable</code> */
	public RemoveNodeEdit(ProbNet probNet, Variable variable) {
		super(probNet);
		this.variable = variable;
		probNode = null;
		kindOfNode = null;
		this.logger = Logger.getLogger(RemoveNodeEdit.class);
	}

	// Methods
	@Override
	public void doEdit() throws DoEditException {
		if (variable != null) {
			probNode = probNet.getProbNode(variable);
			if (probNode == null) {
				throw new DoEditException("Trying to access a null node");
			}
			kindOfNode = probNode.getNodeType();
			probNet.removeProbNode(probNode);
		} else {
			throw new DoEditException("Trying to access a null variable");
		}
	}
	
	public void undo() {
		super.undo();
		if (variable != null) {
			try {
				probNet.addProbNode(variable, kindOfNode);
			} catch (Exception e) {
				logger.fatal (e);
			}
		}
	}

	/** @return nodeType <code>NodeType</code> */
	public NodeType getNodeType() {
		return kindOfNode;
	}

	/** @return variable <code>Variable</code> */
	public Variable getVariable() {
		return variable;
	}

	public String toString() {
		return new String("RemoveNodeEdit: " + variable);
	}

}
