/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.action.CompoundPNEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;

/** Utility methods for constraint package. */
public class UtilConstraints {

    /**
     * @param event <code>UndoableEditEvent</code>
     * @param typeEdit <code>Class</code>
     * @return An <code>ArrayList</code> of <code>PNEdit</code>s of type
     *         <code>typeEdit</code> that are contained in the
     *         <code>event</code> received (if there is any)
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     */
    public static List<PNEdit> getSimpleEditsByType (PNEdit edit,
                                                  Class<?> typeEdit)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        List<PNEdit> edits = new ArrayList<PNEdit> ();
        if (edit.getClass () == typeEdit)
        {
            edits.add ((PNEdit) edit);
        }
        else
        { // Check compound edits
            if (CompoundPNEdit.class.isInstance (edit))
            {
                Vector<UndoableEdit> simpleEdits = ((CompoundPNEdit) edit).getEdits ();
                for (UndoableEdit simpleEdit : simpleEdits)
                {
                    if (typeEdit.isInstance (simpleEdit))
                    {
                        edits.add ((PNEdit) simpleEdit);
                    }
                }
            }
        }
        return edits;
    }
	
}
