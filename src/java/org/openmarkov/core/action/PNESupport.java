/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.action;

import java.util.List;
import java.util.Vector;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEditSupport;

import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.constraint.PNConstraint;

/**
 * This class is used over a <code>openmarkov.inference.ProbNet</code> where
 * changes can be undone and redone. One edition has two parts:
 * <ol>
 * <li>Inform the listeners and
 * <li>If the action is not vetoed (with a <code>Exception</code>) for a
 * listener it does the edition.
 * </ol>
 * 
 * @author marias
 */
public class PNESupport extends UndoableEditSupport {

    List<PNConstraint>           constraints;

    /**
     * If <code>true</code> stores editions in
     * <code>openmarkov.undo#UndoManager</code> for undo/redo.
     */
    protected boolean            withUndo;

    /**
     * List of undoable edits.
     * 
     * @see javax.swing.undo#UndoManager
     */
    protected UndoManagerSupport undoManagerSupport;

    private boolean              significantEdits = true;
    private boolean              openParenthesis  = false;
    private boolean              editsExecuted    = false;
    private int                  editCount;

    // Constructor
    /**
     * @param probNet
     *            <code>ProbNet</code>.
     * @param withUndo
     *            <code>boolean</code>
     */
    public PNESupport(boolean withUndo) {
        super();
        this.withUndo = withUndo;
        undoManagerSupport = new UndoManagerSupport();
    }

    // Methods
    public void setListeners(Vector<UndoableEditListener> listeners) {
        this.listeners = listeners;
    }

    public Vector<UndoableEditListener> getListeners() {
        return listeners;
    }

    /**
     * First part: Announce to the listeners than an edition can happen
     * 
     * @param edit
     *            <code>PNEdit</code>.
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     * @throws <code>ConstraintViolationException</code> in case of illegal
     *         <code>probNet</code> modification.
     * @throws <code>CanNotDoEditException</code> in case of illegal
     *         modifications in others listeners such as heuristics, GUI, ...
     */
    public void announceEdit(PNEdit edit)
            throws ConstraintViolationException, CanNotDoEditException,
            NonProjectablePotentialException, WrongCriterionException {
        UndoableEditEvent event = new UndoableEditEvent(this, edit);
        for (UndoableEditListener listener : listeners) {
            ((PNUndoableEditListener) listener).undoableEditWillHappen(event);
        }
    }

    /**
     * Second part: It does the edition and inform to the listeners
     * 
     * @param edit
     *            <code>PNEdit</code>.
     * @throws DoEditException
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     */
    public void doEdit(PNEdit edit)
            throws DoEditException, NonProjectablePotentialException, WrongCriterionException {
        // Inform the listeners that an edition will happen
        // May return an exception

        edit.doEdit();
        if (withUndo) {
            edit.setSignificant(significantEdits);
            editCount++;
            if (openParenthesis) {
                significantEdits = false;// from now, only no significant edits
                editsExecuted = true; // at least one edit was executed
            }

            undoManagerSupport.addEdit(edit);
        }
        postEdit(edit);// Inform the listeners that an edition has happened
    }

    /**
     * @see javax.swing.undo.UndoManager#canUndo()
     * @see javax.swing.undo.UndoManager#undo()
     */
    public void undo() {
        if (withUndo && undoManagerSupport.canUndo()) {

            UndoableEditEvent event = new UndoableEditEvent(this,
                    undoManagerSupport.editToBeUndone());
            undoManagerSupport.undo();
            for (UndoableEditListener listener : listeners) {
                ((PNUndoableEditListener) listener).undoEditHappened(event);
            }
        }
    }

    /**
     * @see javax.swing.undo.UndoManager#canRedo()
     * @see javax.swing.undo.UndoManager#redo()
     */
    public void redo() {
        if (withUndo && undoManagerSupport.canRedo()) {
            UndoableEditEvent event = new UndoableEditEvent(this,
                    undoManagerSupport.editToBeRedone());
            undoManagerSupport.redo();
            for (UndoableEditListener listener : listeners) {
                ((PNUndoableEditListener) listener).undoableEditHappened(event);
            }
        }
    }

    public UndoManagerSupport getUndoManager() {
        return undoManagerSupport;
    }

    public boolean getCanUndo() {
        return undoManagerSupport.canUndo();
    }

    public boolean getCanRedo() {
        return undoManagerSupport.canRedo();
    }

    /**
     * Add a <code>OpenParenthesisEdit</code> edit instance to
     * <code>undoManager</code> and increases the parenthesis deph.
     */
    public void openParenthesis() {
        if (withUndo) {
            openParenthesis = true;
            editCount = 0;
            editsExecuted = false;
        }
    }

    /**
     * Add a <code>CloseParenthesisEdit</code> edit instance to
     * <code>undoManager</code> and decreases the parenthesis deph.
     */
    public void closeParenthesis() {
        if (withUndo) {
            openParenthesis = false;
            significantEdits = true;
        }
    }

    /** @return withUndo <code>boolean</code>. */
    public boolean isWithUndo() {
        return withUndo;
    }

    /**
     * @param withUndo
     *            <code>boolean</code>.
     */
    public void setWithUndo(boolean withUndo) {
        this.withUndo = withUndo;
    }

    /** @return probNet <code>ProbNet</code>. */
    /*
     * public ProbNet getProbNet() { return (ProbNet)realSource; }
     */

    public String toString() {
        String out = "PNESupport. probNet: ";
        if (realSource == null) {
            out = out + "not defined.";
        } else {
            /*
             * try { String name = (String) ((ProbNet) realSource).getName(); if
             * (name != null) { out = out + name + '.'; } else { out = out +
             * "no name."; } } catch (Exception e) { logger.fatal (e); }
             */
        }
        if (listeners != null) {
            out = out + " Number of listeners: " + listeners.size() + '.';
        } else {
            out = out + " Number of listeners: 0.";
        }
        if (withUndo) {
            out = out + " With undo.";
        } else {
            out = out + " Without undo.";
        }
        return out;
    }

    public void undoAndDelete() {
        if (editsExecuted) {
            this.undo();
            undoManagerSupport.deleteEdits(editCount);
            UndoableEditEvent event = new UndoableEditEvent(this, null);
            for (UndoableEditListener listener : listeners) {
                ((PNUndoableEditListener) listener).undoEditHappened(event);
            }

        }

    }

}
