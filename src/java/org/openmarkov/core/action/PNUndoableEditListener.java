/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;


public interface PNUndoableEditListener extends UndoableEditListener {

	/** An undoable edit will happen 
	 * @throws WrongCriterionException 
	 * @throws NonProjectablePotentialException */
    public void undoableEditWillHappen(UndoableEditEvent event) 
    throws ConstraintViolationException, CanNotDoEditException, 
    NonProjectablePotentialException, 
    WrongCriterionException;
    
    public void undoEditHappened(UndoableEditEvent event);
    
}
