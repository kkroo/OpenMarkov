/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.action;

import java.util.Vector;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;

/**
 * A compound edit is a complex edition composed of several editions. This is an
 * abstract class.
 */
@SuppressWarnings("serial")
public abstract class CompoundPNEdit extends CompoundEdit
    implements
        PNEdit
{
    // Attribute
    protected ProbNet probNet;
    private boolean   generatedEdits;
    // All simple edits are significant
    private boolean   significant = true;

    // Constructor
    /** @param probNet <tt>ProbNet</tt> */
    public CompoundPNEdit (ProbNet probNet)
    {
        this.probNet = probNet;
        generatedEdits = false;
    }

    // Methods
    /**
     * Generate edits and does them
     * @throws DoEditException
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     */
    public void doEdit ()
        throws DoEditException,
        NonProjectablePotentialException,
        WrongCriterionException
    {
        if (!generatedEdits)
        {
            generateEdits ();
            generatedEdits = true;
        }
        for (UndoableEdit edit : edits)
        {
            ((PNEdit) edit).doEdit ();
        }
        super.end ();
    }

    public abstract void generateEdits ()
        throws NonProjectablePotentialException,
        WrongCriterionException;

    /**
     * @return <code>Vector</code> of <code>UndoableEdit</code>s
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     */
    public Vector<UndoableEdit> getEdits ()
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        if (!generatedEdits)
        {
            generateEdits ();
            generatedEdits = true;
        }
        return edits;
    }

    public void setSignificant (boolean significant)
    {
        this.significant = significant;
    }

    public boolean isSignificant ()
    {
        return significant;
    }

    @Override
    public ProbNet getProbNet ()
    {
        return probNet;
    }
}
