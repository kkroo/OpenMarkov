/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmarkov.core.action.PotentialChangeEdit;
import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * <code>NodePotentialEdit</code> is a simple edit that allows to modify the
 * node's <code>Potential</code> values. It is implemented for TablePotential
 * Only
 * 
 * @version 1.0 21/12/10
 * @author mpalacios
 */
@SuppressWarnings("serial")
public class TablePotentialValueEdit extends SimplePNEdit {
    /**
     * The column of the table where is the potential
     */
    private int            col;
    /**
     * The row of the table where is the potential
     */
    private int            row;
    /**
     * The new value of the potential
     */
    private Double         newValue;
    /**
     * A list that store the edition order
     */
    private List<Integer>  priorityList;
    /**
     * The index of the value selected in the graphic table
     */
    private int            indexSelected;
    /**
     * Index of the value selected
     */
    private int            potentialSelected;
    /**
     * The potential
     */
    private TablePotential tablePotential;
    /**
     * Old table potential
     */
    private TablePotential oldTablePotential;
    /**
     * the increment to get the real position of the value modified
     */
    private int            increment;

    /**
     * the table potential
     */
    private double[]       newTable;
    private List<Variable> orderVariables        = new ArrayList<Variable>();
    private List<Variable> newOrderVariables     = new ArrayList<Variable>();
    private Object[][]     notEditablePostitions = new Object[0][0];
    private ProbNode       probNode;

    // Constructor
    /**
     * Creates a new <code>NodePotentialEdit</code> specifying the node to be
     * edited, the new value of the potential, the row and column where is the
     * value to be modified and a priority list for potentials updating.
     * 
     * @param probNode
     *            the node to be edited
     * @param newValue
     *            the new value
     * @param col
     *            the column in the edited table
     * @param row
     *            the row in the edited table
     * @param priorityList
     *            the priority lists for potentials update.
     * @param notEditablePositions
     *            two dimensional array with the information about editable
     *            positions.
     */
    public TablePotentialValueEdit(ProbNode probNode, ProbNet probNet,
            TablePotential tablePotential, Double newValue, int row, int col,
            List<Integer> priorityList, Object[][] notEditablePositions) {
        super(probNet);
        this.probNode = probNode;
        this.row = row;
        this.col = col;
        this.newValue = newValue;
        this.priorityList = priorityList;
        this.notEditablePostitions = notEditablePositions;
        this.indexSelected = probNode.getVariable().getNumStates()
                - (row - probNode.getNode().getNumParents() + 1);
        this.oldTablePotential = tablePotential;
        orderVariables = oldTablePotential.getVariables();
        // reorder the variables like appear in PotentialEditDialog
        int end = -1;
        if (orderVariables.size() > 0) {
            if (tablePotential.getPotentialRole() != PotentialRole.UTILITY) {
                newOrderVariables.add(orderVariables.get(0));
                end = 0;
            }
            for (int i = orderVariables.size() - 1; i > end; i--) {
                newOrderVariables.add(orderVariables.get(i));
            }
        }
        // Reorder the values table of TablePotential
        this.tablePotential = (TablePotential)tablePotential.copy();
        // values table reordered
        this.newTable = this.tablePotential.getValues();
        if (!(tablePotential.getDimensions() == null)) {
            this.increment = tablePotential.getDimensions()[0] * (col - 1);
        } else {
            increment = 0;
        }
        // The potentialSelected is the index in the values table reordered of
        // the value edited
        this.potentialSelected = Util.toPositionOnPotentialReordered(row,
                col,
                probNode.getVariable().getNumStates(),
                probNode.getNode().getNumParents());

    }

    @Override
    /** @throws exception <code>Exception</code> */
    public void doEdit()
            throws DoEditException {
        if (oldTablePotential.getPotentialRole() != PotentialRole.UTILITY) {
            if (priorityList.isEmpty()) {
                // User is editing a new column of potentials //node
                priorityList = getPriorityListInitialization();
            } else {
                // the user is editing a the same column of potentials that last
                // time
                priorityList.remove(new Integer(potentialSelected));
                priorityList.add(potentialSelected);
            }
            Iterator<Integer> listIterator = priorityList.listIterator();
            Double sum = 0.0;
            Double rest = 0.0;
            int position = 0;
            int maxDecimals = 10;
            double epsilon;
            epsilon = Math.pow(10, -(maxDecimals + 2));
            newTable[potentialSelected] = Util.roundAndReduce(newValue, epsilon, maxDecimals);
            while (listIterator.hasNext()) {
                position = (Integer) listIterator.next();
                if (isEditablePosition(position)) {
                    sum = Util.roundAndReduce(sum + newTable[position], epsilon, maxDecimals);
                }
                // sum += newTable[pos];
            }
            rest = Math.abs(Util.roundAndReduce(1 - sum, epsilon, maxDecimals));
            // rest = Math.abs( 1 - sum );
            if (sum > 1.0) {
                listIterator = priorityList.listIterator();
                while (listIterator.hasNext() && rest != 0) {
                    position = (Integer) listIterator.next();
                    if (this.isEditablePosition(position)) {
                        rest = Util.roundAndReduce(rest - newTable[position], epsilon, maxDecimals);
                        // rest = rest - newTable[pos];
                        if (rest < 0) {// it is because the value of the table
                                       // is bigger than the rest
                                       // and now there's nothing left to reach
                                       // one
                            newTable[position] = Math.abs(Util.roundAndReduce(rest,
                                    epsilon,
                                    maxDecimals));
                            break;
                        } else
                            newTable[position] = 0.0;
                    }
                }
            } else {// =< 1
                boolean updated = false;
                listIterator = priorityList.listIterator();
                while (listIterator.hasNext() && !updated) {
                    position = (Integer) listIterator.next();
                    if (this.isEditablePosition(position)) {
                        newTable[position] = Util.roundAndReduce(newTable[position] + rest,
                                epsilon,
                                maxDecimals);
                        updated = true;
                    }
                }
            }
        } else {
            newTable[potentialSelected] = newValue;
        }
        PotentialChangeEdit changePotentialEdit = new PotentialChangeEdit(probNet,
                oldTablePotential,
                tablePotential);
        try {
            probNet.doEdit(changePotentialEdit);
        } catch (ConstraintViolationException
                | CanNotDoEditException
                | NonProjectablePotentialException
                | WrongCriterionException e) {
            e.printStackTrace();
            throw new DoEditException(e);
        }
    }

    /**
     * Gets the table-potential of the node
     * 
     * @return variable1 <code>Variable</code>
     */
    public TablePotential getPotential() {
        return tablePotential;
    }

    /**
     * Gets the priority list initialization
     * 
     * @return the priority list initialized with the the value edited in the
     *         last place of the list
     */
    private List<Integer> getPriorityListInitialization() {
        for (int i = 0; i < probNode.getVariable().getNumStates(); i++) {
            if (i != indexSelected)
                priorityList.add(i + increment);
        }
        priorityList.add(indexSelected + increment);
        return priorityList;
    }

    /**
     * Gets the priority list
     * 
     * @return the priority list
     */
    public List<Integer> getPriorityList() {
        return priorityList;
    }

    /*
     * private double roundingDouble(double number) { double positions =
     * Math.pow( 10, (double) decimalPositions ); return Math.round( number *
     * positions ) / positions; }
     */
    /**
     * Gets the row position associated to value edited if priorityList exists
     * 
     * @param position
     *            position of the value in the array of values
     * @return the position in the table
     */
    public int getRowPosition(int position) {
        return Util.toPositionOnJtable(position,
                col,
                probNode.getVariable().getNumStates(),
                probNode.getNode().getNumParents());
    }

    /**
     * Gets the row position associated to value edited if priorityList no
     * exists
     * 
     * @param position
     *            position of the value in the array of values
     * @return the position in the table
     */
    public int getRowPosition() {
        return row;
    }

    /**
     * Gets the column where the value is edited
     * 
     * @return the column edited
     */
    public int getColumnPosition() {
        return col;
    }

    /***
     * Checks if the position corresponds to an editable cell
     * 
     * @param position
     * @return
     */
    private boolean isEditablePosition(int position) {
        boolean editable = false;
        int row = getRowPosition(position);
        if (this.notEditablePostitions.length > row && this.notEditablePostitions[0].length > col) {
            if (this.notEditablePostitions[row][col] == null) {
                editable = true;
            }
        } else {
            editable = true;
        }
        return editable;
    }
}
