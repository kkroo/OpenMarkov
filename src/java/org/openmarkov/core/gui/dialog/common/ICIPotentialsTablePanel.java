/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.openmarkov.core.exception.NullListPotentialsException;
import org.openmarkov.core.gui.component.ICIValuesTable;
import org.openmarkov.core.gui.component.ICIValuesTableCellRenderer;
import org.openmarkov.core.gui.component.PotentialsTablePanelOperations;
import org.openmarkov.core.gui.component.ValuesTableModel;
import org.openmarkov.core.gui.dialog.node.ICIOptionsPanel;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;

/**
 * This class implements a ICI potential table with the following features: The
 * headers are characteristic for this specific potential type. two first rows:
 * The former for parent variables and the later for the parent states first
 * column: is reserved for the child variable name and states
 * 
 * @author jlgozalo
 * @author myebra
 * 
 */
@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "ICI")
public class ICIPotentialsTablePanel extends ProbabilityTablePanel {

    private ICIOptionsPanel iciOptionPanel;
    protected Logger        logger;
    /**
     * JTable where show the values.
     */
    private ICIValuesTable  iciValuesTable;

    private ProbNode        probNode;
    /**
     * Indicates if the data of the table is modifiable.
     */
    private boolean         modifiable;

    /**
     * Panel to scroll the table.
     */
    private JScrollPane     valuesTableScrollPane = null;

    public ICIPotentialsTablePanel(ProbNode probNode) {
        super();
        removeAll();
        this.probNode = probNode;
        modifiable = true;
        setLayout(new BorderLayout());
        add(getICIOptionPanel(), BorderLayout.NORTH);
        add(getValuesTableScrollPane(), BorderLayout.CENTER);
        showValuesTable(true);
        setData(probNode);
        repaint();
    }

    private ICIOptionsPanel getICIOptionPanel() {

        if (iciOptionPanel == null) {
            iciOptionPanel = new ICIOptionsPanel(probNode);
            boolean newNode = true;
            iciOptionPanel.setNewNode(newNode);
        }

        return iciOptionPanel;
    }

    public ProbNode getProbNode() {
        return probNode;
    }

    /**
     * Sets a new table model with new data.
     * 
     * @param newData
     *            new data for the table.
     */
    public void setData(Object[][] newData) {

        setData(newData, columns, 0, 0, NodeType.CHANCE);
    }

    /**
     * Sets a new table model with new data and new columns
     * 
     * @param newData
     *            new data for the table
     * @param newColumns
     *            new columns for the table
     */
    public void setData(Object[][] newData,
            String[] newColumns,
            int firstEditableRow,
            int lastEditableRow,
            NodeType nodeType) {

        showValuesTable(true);
        data = newData.clone();
        columns = newColumns.clone();
        this.firstEditableRow = firstEditableRow;
        this.lastEditableRow = lastEditableRow;
        iciValuesTable.resetModel();

        // valuesTable.setVariable(probNode.getPotentials().get( 0
        // ).getVariable( 0 ));

        iciValuesTable.setModel(getTableModel());
        iciValuesTable.initializeDataModified(false);
        ((ValuesTableModel) iciValuesTable.getModel()).setFirstEditableRow(firstEditableRow);
        iciValuesTable.setLastEditableRow(lastEditableRow);

        iciValuesTable.setNodeType(nodeType);

    }

    /**
     * Sets a new table model with new data and new columns based on three
     * items: <li>list of Potentials of the variable</li> <li>states of the
     * variable</li> <li>parents of the variable</li>
     * 
     * @param listPotentials
     *            - the list of potentials of the variable
     * @param variableName
     *            - name of the variable
     * @param variableStates
     *            - states of the variable
     * @param parents
     *            - parents of the variable
     */
    public void setData(ProbNode properties) {
        this.probNode = properties;
        iciValuesTable.setData(probNode);
        Object[][] tableData = null;
        String[] newColumns = null;
        if (properties.getPotentials() != null) {
            tableData = convertListPotentialsToCanonicalTableFormat(properties);
            newColumns = ICIValuesTable.getColumnsIdsSpreadSheetStyle(ICIValuesTable.howManyCanonicalColumns(properties));
            setFirstEditableRow(calculateFirstEditableRow(probNode.getPotentials()));
            setLastEditableRow(calculateLastEditableRow(probNode.getPotentials()));
            setData(tableData,
                    newColumns,
                    firstEditableRow,
                    lastEditableRow,
                    properties.getNodeType());

            setCellRenderers();
            getICIValuesTable().fitColumnsWidthToContent();

        } else {
            tableData = new Object[0][0];
            setFirstEditableRow(0);
            setData(tableData);
            // TODO setCellRenderes
            setCellRenderers();
        }
    }

    /**
     * calculate the last editable Row of the table, based upon:
     * <p>
     * <ul>
     * <li>number of parents for the node</li>
     * <li>type of the node (utility or other)</li>
     * </ul>
     * 
     * @param listPotentials
     *            - potentials for the variable
     * @param additionalProperties
     *            - additionalProperties for this variable
     */
    public static int calculateLastEditableRow(List<Potential> listPotentials) {
        int row = 0;
        if (listPotentials != null) {
            row = listPotentials.get(0).getVariables().get(0).getNumStates() + 1;
            // numStates of the child variable plus one empty cell plus a cell
            // for the variable´s name
        } else {
            row = 0;
        }
        return row + 1;
    }

    public static int calculateFirstEditableRow(List<Potential> listPotentials) {
        int row = 0;
        if (listPotentials != null) {

            row = 2; // In a canonical table there are always two rows: one for
                     // parent´s names
                     // and another for parent´s states
        } else {
            row = 0;
        }

        return row;
    }

    /**
     * calculate the number of rows of the canonical table based on the type of
     * the node, the number of parents and the number of states of the variable
     * for canonical models
     * 
     * @param additionalProperties
     *            - node additionalProperties
     * @return the number of rows of this Potentials Table
     */
    protected int howManyCanonicalRows(ProbNode properties) {

        int numRows = 2;// The first two rows are first for parent´s name and
                        // second one for parent´s states

        if (properties.getVariable().getStates() != null) {// there is a row for
                                                           // each child state
            numRows = numRows + properties.getVariable().getStates().length;
        }

        return numRows;
    }

    /**
     * Set a blank data table for canonical models
     * 
     * @param additionalProperties
     *            - to obtain the required number of rows and columns
     * @return the blank data table
     */
    private Object[][] setBlankCanonicalTable(ProbNode properties) {

        Object[][] blankTable = null;
        int numRows = howManyCanonicalRows(properties);
        int numColumns = ICIValuesTable.howManyCanonicalColumns(properties);
        blankTable = new Object[numRows][numColumns];
        for (int i = 0; i < properties.getVariable().getStates().length; i++) {
        }

        return blankTable;
    }

    /**
     * Retrieves ICIPotential
     * 
     * @param listPotentials
     * @return
     */
    private ICIPotential getThisICIPotential(List<Potential> listPotentials) {

        ICIPotential aPotential = null;
        try {
            aPotential = ((ICIPotential) listPotentials.get(0));
        } catch (Exception ex) {
            // ExceptionsHandler.handleException(
            // ex, "no Potential.get(0) !!!", false );
            logger.error("no Potential.get(0) !!!");
        }

        return aPotential;
    }

    /**
     * Calculates number of positions in a canonical table Number of canonical
     * table positions: sum of the product of each parent variable states by the
     * child variable states (conditioned)
     * 
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private int getNumberOfPostions(List<Potential> listPotentials) {
        int numPositions = 0;
        int numParentStates;
        try {
            List<Variable> variables = listPotentials.get(0).getVariables();
            int numChildStates = variables.get(0).getNumStates();
            for (int i = 1; i < variables.size(); i++) {
                numParentStates = variables.get(i).getNumStates();
                numPositions += numParentStates * numChildStates;
            }
            numPositions += numChildStates; // for the leak column
        } catch (NullPointerException exception) {
            numPositions = 0;
            // ExceptionsHandler.handleException(
            // exception, "not enougth memory", false );
            logger.error("not enougth memory");
        }
        setPosition(numPositions);
        return numPositions;
    }

    /**
     * Prepare the table data from the <code>Potential</code>s and States.
     * <p>
     * If the Potential is null, then the information is taken from the
     * <code>NodeProperties</code>
     * 
     * @param listPotentials
     *            - potentials of the table
     * @param states
     *            - states of the variable of this node
     * @param parents
     *            - <code>NodeWrapper</code> list of the parents
     * @return the table data to be set
     */
    protected Object[][] convertListPotentialsToCanonicalTableFormat(ProbNode properties) {
        Object[][] values = null;
        try {

            PotentialsTablePanelOperations.checkIfNoPotential(properties.getPotentials());
            values = setCanonicalTableSize(values, properties);
            values = setCanonicalTable(values, properties);

            setPosition(getNumberOfPostions(properties.getPotentials()));

        } catch (NullListPotentialsException ex) {
            values = setBlankCanonicalTable(properties);
        }
        return values;
    }

    /**
     * set values table size for the potential of the canonical model
     * 
     * @param values
     *            - the table that is being modified
     * 
     * @param additionalProperties
     *            - the additionalProperties of the node
     */

    private Object[][] setCanonicalTableSize(Object[][] oldValues, ProbNode properties) {
        Object[][] values = oldValues;
        int numRows = 0;
        int numColumns = 2; // at least, there is one column for leak potential
                            // and the first one with child states and name
        // first editable row in a canonical table is always the third one
        // first one for the parent´s names and second one for parent´s states
        int row = 2;

        setBaseIndexForCoordinates(row);
        setFirstEditableRow(row);

        ICIPotential iciPotential = getThisICIPotential(properties.getPotentials());
        List<Variable> variables = iciPotential.getVariables();

        setVariables(variables);

        numRows = getVariables().get(0).getNumStates() + row;

        setLastEditableRow(numRows - 1);

        for (int i = 1; i < variables.size(); i++) {
            numColumns += variables.get(i).getNumStates();
        }

        // create the array of arrays
        values = new Object[numRows][numColumns];
        return values;
    }

    /**
     * 
     * @param oldValues
     * @param probNode
     * @return
     */
    private Object[][] setCanonicalTable(Object[][] oldValues, ProbNode probNode) {

        Object[][] values = oldValues;
        ICIPotential iciPotential = (ICIPotential) getThisICIPotential(probNode.getPotentials());
        List<Variable> variables = iciPotential.getVariables();
        int lastRow = values.length - 1;
        int lastColumn = values[0].length - 1;

        // First column - conditioned variable
        Variable conditionedVariable = variables.get(0);
        values[0][0] = "";
        values[1][0] = conditionedVariable.getName();
        State[] childStates = conditionedVariable.getStates();
        for (int i = 0; i < childStates.length; ++i) {
            values[lastRow - i][0] = childStates[i].getName();
        }

        int columnOffset = 1;
        for (int i = 1; i < variables.size(); ++i) {

            // Header
            Variable variable = variables.get(i);
            State[] states = variable.getStates();
            for (int j = 0; j < states.length; ++j) {
                values[0][j + columnOffset] = variable.getName();
                values[1][j + columnOffset] = states[j].getName();
            }
            // Values
            double[] noisyParameters = iciPotential.getNoisyParameters(variable);
            int numStates = conditionedVariable.getNumStates();
            for (int k = 0; k < noisyParameters.length; ++k) {

                values[lastRow - k % numStates][columnOffset + k / numStates] = noisyParameters[k];
            }

            columnOffset += variable.getNumStates();
        }

        // Leaky parent
        // Header

        values[0][lastColumn] = "Leak";
        values[1][lastColumn] = "--";
        double[] leakyParameters = iciPotential.getLeakyParameters();
        for (int i = 0; i < leakyParameters.length; ++i) {
            values[lastRow - i][lastColumn] = leakyParameters[i];
        }

        return values;
    }

    /**
     * set renders for the cells in the table. Only has to be called when set
     * data.
     */
    protected void setCellRenderers() {
        int size = iciValuesTable.getColumnCount();// returns number of columns
                                                   // in the column model
        boolean[] editableColumns = new boolean[size - 1];

        for (int i = 1; i < size; i++) {
            editableColumns[i - 1] = false;// Uncertainty values false for
                                           // canonical models
        }

        iciValuesTable.setDefaultRenderer(Double.class,
                new ICIValuesTableCellRenderer(getFirstEditableRow(),
                        editableColumns,
                        (ICIPotential) getThisICIPotential(probNode.getPotentials())));
        iciValuesTable.setDefaultRenderer(String.class,
                new ICIValuesTableCellRenderer(getFirstEditableRow(),
                        editableColumns,
                        (ICIPotential) getThisICIPotential(probNode.getPotentials())));

    }

    @Override
    public void close() {
        getICIValuesTable().close();
    }

    private void setVariables(List<Variable> variables) {
        // TODO update this statement, when constructor of this class with
        // potential as parameter is implemented
        if (probNode != null && probNode.getNodeType() == NodeType.UTILITY) {
            this.variables = new ArrayList<Variable>();
            this.variables.add(probNode.getVariable());
            for (Variable variable : variables)
                this.variables.add(variable);
        } else

            this.variables = variables;

    }

    /**
     * This method initializes valuesTableScrollPane.
     * 
     * @return a new values table scroll pane.
     */
    public JScrollPane getValuesTableScrollPane() {

        if (valuesTableScrollPane == null) {
            valuesTableScrollPane = new JScrollPane();
            valuesTableScrollPane.setName("ICIPotentialsTablePanel.valuesTableScrollPane");
            valuesTableScrollPane.setViewportView(getICIValuesTable());

        }
        return valuesTableScrollPane;
    }

    /**
     * This method initializes ICIvaluesTable and defines that first two columns
     * are not selectable
     * 
     * @return a new values table.
     */
    public ICIValuesTable getICIValuesTable() {

        if (iciValuesTable == null) {
            iciValuesTable = new ICIValuesTable(probNode, getTableModel(), modifiable);
            // iciValuesTable.setAutoResizeMode(JTable.);
            // calcColumnWidths(iciValuesTable);
            iciValuesTable.setName("PotentialsTablePanel.valuesTable");
        }
        return iciValuesTable;
    }

    /**
     * This method initializes tableModel. Uses the same table model as
     * CPTTablePanel
     * 
     * @return a new tableModel.
     */
    protected ValuesTableModel getTableModel() {

        ValuesTableModel tableModel = null;
        if (iciValuesTable == null) {
            tableModel = new ValuesTableModel(data, columns, firstEditableRow);
        } else if (iciValuesTable.getTableModel() == null) {
            tableModel = new ValuesTableModel(data, columns, firstEditableRow);
        } else {
            tableModel = (ValuesTableModel) iciValuesTable.getModel();
        }
        return tableModel;
    }

    /**
     * special method to show/hide the values table
     */
    public void showValuesTable(final boolean visible) {

        getICIValuesTable().setVisible(visible);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        getICIValuesTable().setModifiable(!readOnly);
    }

}
