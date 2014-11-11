/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;


/** An edition is one action defined over a Probabilistic Network. */
public interface PNEdit extends UndoableEdit {

    /**
     * Puts into effect the edition.
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     */
    public void doEdit ()
        throws DoEditException,
        NonProjectablePotentialException,
        WrongCriterionException;

    public void setSignificant (boolean significant);
    
    public ProbNet getProbNet();
	
	
}
