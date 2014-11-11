/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.constraint;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;


/** A constraint is a condition that a model must fulfill.<p>
  * This class implements <code>PNUndoableEditListener</code> because like
  * that all the classes that implement this interface will be able to receive 
  * the same messages than <code>UndoableEditListener</code> and they will be 
  * able to be referenced with same identifier. */
public abstract class PNConstraint implements PNUndoableEditListener, Checkable {
	
    @Override
    public void undoableEditHappened (UndoableEditEvent e)
    {
        // Do nothing
    }

    /** Given a <code>probNet</code> that complies with this constraint, this
     * method checks that after the application of the <code>edit</code> 
     * contained in the <code>event</code> received, the 
     * <code>probNet</code> continues complying with this constraint. 
     * @param event <code>UndoableEditEvent</code>
     * @throws CanNotDoEditException 
     * @throws ConstraintViolationException 
      * @throws WrongCriterionException 
     * @throws NonProjectablePotentialException */
    @Override
    public void undoableEditWillHappen (UndoableEditEvent event)
        throws ConstraintViolationException,
        CanNotDoEditException,
        NonProjectablePotentialException,
        WrongCriterionException
    {
        PNEdit edit = (PNEdit) event.getEdit ();
        if (!checkEdit(edit.getProbNet (), edit)) {
            throw new ConstraintViolationException (getMessage ());
        }
        
    }

    protected abstract String getMessage ();

    @Override
    public void undoEditHappened (UndoableEditEvent event)
    {
        // Do nothing
    }

    /** @param probNet. <code>ProbNet</code>
	 * @return <code>true</code> if the <code>probNet</code> fulfills the 
	 * constraint. */
	public abstract boolean checkProbNet(ProbNet probNet);
	
	/** Make sure all editions of the event do not violate restrictions.
	 * @param probNet. <code>ProbNet</code>
	 * @param edit <code>PNEdit</code>
     * @return <code>true</code> if the <code>ProbNet</code> will fulfill the
	 *  constraint after applying the <code>event</code> in a 
	 *  <code>ProbNet</code> that previously fulfilled the constraint. 
	 * @throws WrongCriterionException 
	 * @throws NonProjectablePotentialException */
	public abstract boolean checkEdit(ProbNet probNet, PNEdit edit) 
	throws NonProjectablePotentialException, 
	WrongCriterionException;
	
	@Override
    public String toString() {
        return this.getClass().getName();
    }

    @Override
    public boolean equals (Object paramObject)
    {
        return (paramObject.getClass () == this.getClass ());
    }

    @Override
    public int hashCode ()
    {
        int hashCode = 17 + this.getClass ().hashCode ();
        return hashCode;
    }    
    
	
	
}
