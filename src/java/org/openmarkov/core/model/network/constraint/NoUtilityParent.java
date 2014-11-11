/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.constraint;

import java.util.List;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint (name = "NoUtilityParent", defaultBehavior = ConstraintBehavior.YES)
public class NoUtilityParent extends PNConstraint  {

    @Override
    public boolean checkProbNet (ProbNet probNet)
    {
        List<ProbNode> utilityNodes = probNet.getProbNodes (NodeType.UTILITY);
        for (ProbNode utilNode : utilityNodes)
        {
            List<Node> children = utilNode.getNode ().getChildren ();
            for (Node child : children)
            {
                ProbNode probChild = (ProbNode) child.getObject ();
                if (probChild.getNodeType () != NodeType.UTILITY)
                {
                    return false;
                }
            }
        }
        return true;
    }

	@Override
	public boolean checkEdit(ProbNet probNet, PNEdit edit)
			throws NonProjectablePotentialException,
			WrongCriterionException {
		
        List<PNEdit> edits = UtilConstraints.getSimpleEditsByType (edit, AddLinkEdit.class);
		
		for (PNEdit simpleEdit : edits) {
			if (((AddLinkEdit)simpleEdit).isDirected()) { 
				Variable variable1 = ((AddLinkEdit)simpleEdit).getVariable1(); 
				ProbNode node1 = probNet.getProbNode(variable1);
				if(node1.getNodeType()== NodeType.UTILITY)
				{
					Variable variable2 = ((AddLinkEdit)simpleEdit).getVariable2(); 
					ProbNode node2 = probNet.getProbNode(variable2);
					if(node2.getNodeType()!= NodeType.UTILITY)
					{
					    return false;
					}
				}
			}
		}
		return true;
	}

    @Override
    protected String getMessage ()
    {
        return "Utility nodes only can have utility children";
    }


}
