/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.constraint;

import java.util.Iterator;
import java.util.List;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;


/** This constraint ensures that exists at least one cost potential and all of 
 *  them are children of a chance or a decision node. */
@Constraint (name = "AtLeastOneCostPotential", defaultBehavior = ConstraintBehavior.OPTIONAL)
public class AtLeastOneCostPotential extends PNConstraint {

	@Override
	/** This method has no sense because this constraint is only used to check
	 * the whole <code>ProbNet</code> before execute the algorithm. */
	public boolean checkEdit(ProbNet probNet, PNEdit edit) {
		return false;
	}

	@Override
	public boolean checkProbNet(ProbNet probNet) {
	    List<ProbNode> costNodes = probNet.getProbNodes(NodeType.COST);
		if (costNodes.size() == 0) {
			return false;
		}
		for (ProbNode costNode : costNodes) {
		    List<ProbNode> parents = 
				ProbNet.getProbNodesOfNodes(costNode.getNode().getParents());
			Iterator<ProbNode> i = parents.iterator();
			boolean chanceOrDecision = false;
			ProbNode parent;
			do {
				parent = i.next();
				NodeType nodeType = parent.getNodeType();
				chanceOrDecision = nodeType == NodeType.CHANCE || 
					nodeType == NodeType.DECISION;
			} while (!chanceOrDecision && i.hasNext());
			if (!chanceOrDecision) {
				return false;
			}
		}
		return true;
	}

    @Override
    protected String getMessage ()
    {
     // TODO Auto-generated method stub
        return "";
    }	

}
