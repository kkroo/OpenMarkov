/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.constraint;

import java.util.List;

import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;

@Constraint(name = "OnlyTemporalVariables", defaultBehavior = ConstraintBehavior.NO)
public class OnlyTemporalVariables extends PNConstraint
{

    @Override
    public boolean checkProbNet (ProbNet probNet)
    {
        List<Variable> variables = probNet.getVariables ();
        for (Variable variable : variables)
        {
            if (!variable.isTemporal ())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkEdit (ProbNet probNet, PNEdit edit)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        List<PNEdit> edits = UtilConstraints.getSimpleEditsByType (edit, AddProbNodeEdit.class);
        for (PNEdit simpleEdit : edits)
        {
            Variable variable = ((AddProbNodeEdit) simpleEdit).getVariable ();
            if (!variable.isTemporal ())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getMessage ()
    {
        return "all variables must be temporal.";
    }
}
