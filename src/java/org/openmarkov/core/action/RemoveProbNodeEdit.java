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
public class RemoveProbNodeEdit extends SimplePNEdit implements UsesVariable {

		// Attributes
		/** Node associated to variable */
		private ProbNode probNode;
		
		private NodeType kindOfNode;
		
		protected Variable variable;
		
		private Logger logger;
		
		// Constructor
		/** @param probNet <code>ProbNet</code>
		 * @param variable <code>Variable</code> */
		public RemoveProbNodeEdit(ProbNet probNet, ProbNode probNode) {
			super(probNet);
			this.variable = probNode.getVariable();
			this.probNode = probNode;
			this.kindOfNode = null;
			this.logger = Logger.getLogger(RemoveProbNodeEdit.class);
			
		}

		// Methods
		@Override
		public void doEdit() throws DoEditException {
			if (probNode == null) {
					throw new DoEditException("Trying to access a null node");
			}
			
			probNet.removeProbNode(probNode);
			
		}
		
		public void undo() {
			super.undo();
			try {
				probNet.addProbNode(probNode);
			} catch (Exception e) {
				logger.fatal(e);
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
			StringBuffer buffer = new StringBuffer("RemoveNodeEdit: ");
			if (variable == null) {
				buffer.append("null");
			} else {
				buffer.append(variable.getName());
			}
			return buffer.toString();
		}

	

}
