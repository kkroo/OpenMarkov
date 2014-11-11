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
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint (name = "OnlyDirectedLinks", defaultBehavior = ConstraintBehavior.YES)
public class OnlyDirectedLinks extends PNConstraint {

	private String explanation;

	@Override
	public boolean checkProbNet(ProbNet probNet) {
		List<Node> nodes = probNet.getGraph().getNodes();
		for (Node node : nodes) {
			if (node.getSiblings().size() != 0) {
				return false;
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
			if (!((AddLinkEdit) simpleEdit).isDirected()) {
				AddLinkEdit addLink = (AddLinkEdit)simpleEdit;
				explanation = new String(
					addLink.getVariable1() + " --- " + addLink.getVariable2());
				return false;
			}
		}
		return true;
	}

    @Override
    protected String getMessage ()
    {
        return explanation + ". Only directed links allowed";
    }

}
