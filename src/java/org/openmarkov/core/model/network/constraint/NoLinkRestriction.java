package org.openmarkov.core.model.network.constraint;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint (name="NoLinkRestriction",defaultBehavior = ConstraintBehavior.YES)
public class NoLinkRestriction extends PNConstraint{

	@Override
	protected String getMessage() {
		return "";
	}

	@Override
	public boolean checkProbNet(ProbNet probNet) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkEdit(ProbNet probNet, PNEdit edit)
			throws NonProjectablePotentialException,
			WrongCriterionException {
		// TODO Auto-generated method stub
		return true;
	}

}
