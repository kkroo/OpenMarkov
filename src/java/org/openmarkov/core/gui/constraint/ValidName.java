/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.constraint;

import java.util.List;

import org.openmarkov.core.action.NodeNameEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.constraint.UtilConstraints;

/**
 * checks that the name field is filled and there isn't any node with the same
 * name.
 */
public class ValidName extends PNConstraint
{
    // Attributes.
    private String message;

    public boolean checkEdit (ProbNet probNet, PNEdit edit)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        List<PNEdit> edits = UtilConstraints.getSimpleEditsByType (edit, NodeNameEdit.class);
        for (PNEdit simpleEdit : edits)
        {
            String name = ((NodeNameEdit) simpleEdit).getNewName ();
            String currentName = ((NodeNameEdit) simpleEdit).getPreviousName ();
            // if ((name == null) || (name.contentEquals(""))) {
            if (!checkName (name, currentName, probNet))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * This method checks that the name field is filled and there isn't any node
     * with the same name.
     * @return true, if the name field isn't empty and there isn't any node with
     *         this name; otherwise, false.
     */
    public boolean checkName (String newName, String currentName, ProbNet probNet)
    {
        // boolean result = true;
        if ((newName == null) || newName.equals (""))
        {
            message = "NodeNameEmpty.Text.Label";
            return false;
        }
        else if (!currentName.equals (newName) && existNode (newName.toUpperCase (), probNet))
        {
            message = "ConstraintViolationException.ValidName.Exists";
            return false;
        }
        /*
         * if (!result) { jTextFieldNodeName.requestFocus(); return false; }
         */
        return true;
    }

    /**
     * This method checks if exists the specified node.
     * @param name name of the node to search.
     * @return true if the node exists; otherwise, false.
     */
    public boolean existNode (String name, ProbNet probNet)
    {
        try
        {
            probNet.getProbNode (name);
            return true;
        }
        catch (ProbNodeNotFoundException e)
        {
            return false;
        }
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
