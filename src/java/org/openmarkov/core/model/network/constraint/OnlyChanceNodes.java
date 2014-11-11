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

import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;


@Constraint (name = "OnlyChanceNodes", defaultBehavior = ConstraintBehavior.NO)
public class OnlyChanceNodes extends PNConstraint {

	@Override
	public boolean checkProbNet(ProbNet probNet) {
	    List<ProbNode> probNodes = probNet.getProbNodes();
		for (ProbNode probNode : probNodes) {
			if (probNode.getNodeType() != NodeType.CHANCE) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkEdit(ProbNet probNet, PNEdit edit)
	throws NonProjectablePotentialException, 
	WrongCriterionException {
	    List<PNEdit> edits = 
			UtilConstraints.getSimpleEditsByType(edit, AddProbNodeEdit.class);
		for (PNEdit simpleEdit : edits) {
			if (((AddProbNodeEdit)simpleEdit).getNodeType () != NodeType.CHANCE) {
				return false;
			}
		}
		return true;
	}

    @Override
    protected String getMessage ()
    {
        return "only chance nodes allowed";
    }

}
