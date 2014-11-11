/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.constraint;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint (name = "NoClosedPath", defaultBehavior = ConstraintBehavior.OPTIONAL)
public class NoClosedPath extends PNConstraint {

    private  NoLoops noLoopsConstraint;
    private  NoCycle noCycleConstraint;
    
	public NoClosedPath() {
	    noLoopsConstraint= new NoLoops();
	    noCycleConstraint= new NoCycle();
	}
	

    @Override
    public boolean checkProbNet(ProbNet probNet) {
    	
    	return (noLoopsConstraint.checkProbNet(probNet)&& noCycleConstraint.checkProbNet(probNet));
    	
    }

    @Override
    public boolean checkEdit (ProbNet probNet, PNEdit edit)
        throws NonProjectablePotentialException,
        WrongCriterionException{
    	return (noLoopsConstraint.checkEdit(probNet,edit)&& noCycleConstraint.checkEdit(probNet,edit));
    	
    }

    @Override
    protected String getMessage ()
    {
       
        return "no closed path allowed.";
    }
}
