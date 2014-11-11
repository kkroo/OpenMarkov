/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.constraint;

import java.util.List;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.NodeStateEdit;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.constraint.UtilConstraints;

/**
 * Checks that the state field is filled and there isn't any node with the same
 * name.
 */
public class ValidState extends PNConstraint
{
    // Attributes.
    private String message;

    public boolean checkEdit (ProbNet probNet, PNEdit edit)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        List<PNEdit> edits = UtilConstraints.getSimpleEditsByType (edit, NodeStateEdit.class);
        for (PNEdit simpleEdit : edits)
        {
            State state = ((NodeStateEdit) simpleEdit).getNewState ();
            State currentState = ((NodeStateEdit) simpleEdit).getLastState ();
            ProbNode probNode = ((NodeStateEdit) simpleEdit).getProbNode ();
            StateAction stateAction = ((NodeStateEdit) simpleEdit).getStateAction ();
            // if ((name == null) || (name.contentEquals(""))) {
            if (!checkState (state.getName (), currentState.getName (), probNode, stateAction))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * This method checks that the state field is filled and there isn't any
     * node with the same name.
     * @return true, if the state field isn't empty and there isn't any node with
     *         this name; otherwise, false.
     */
    public boolean checkState (String newState,
                               String currentState,
                               ProbNode probNode,
                               StateAction stateAction)
    {
        switch (stateAction)
        {
            case RENAME :
            case ADD :
                if ((newState == null) || newState.equals (""))
                {
                    message = "NodeStateEmpty.Text.Label";
                    return false;
                }
                else if (existState (newState, probNode))
                {
                    message = "DuplicatedState.Text.Label";
                    return false;
                }
                break;
            case REMOVE :
        }
        return true;
    }

    /**
     * This method checks if exists the state specified.
     * @param name name of the node to search.
     * @return true if the state exists; otherwise, false.
     */
    public boolean existState (String state, ProbNode probNode)
    {
        for (State states : probNode.getVariable ().getStates ())
        {
            if (states.getName ().toUpperCase ().equals (state.toUpperCase ()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean checkProbNet (ProbNet probNet)
    {
        List<Variable> variables = probNet.getVariables ();
        for (Variable variable : variables)
        {
            String name = variable.getName ();
            if ((name == null) || (name.contentEquals ("")))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getMessage ()
    {
        return message;
    }
}
