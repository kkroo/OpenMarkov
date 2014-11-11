/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import org.openmarkov.core.action.UncertainValuesEdit;
import org.openmarkov.core.action.UncertainValuesRemoveEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NullListPotentialsException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.component.PotentialsTablePanelOperations;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.component.ValuesTableCellRenderer;
import org.openmarkov.core.gui.component.ValuesTableModel;
import org.openmarkov.core.gui.component.ValuesTableOptimalPolicyCellRenderer;
import org.openmarkov.core.gui.component.ValuesTableWithLinkRestrictionCellRenderer;
import org.openmarkov.core.gui.dialog.node.UncertainValuesDialog;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.menu.UncertaintyContextualMenu;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;

/**
 * This class implements a Table potential table with the following features:
 * <li>Its elements, except the first column, are modifiable.</li> <li>New
 * elements can be added, creating a new key row with empty data.</li> <li>The
 * key data (first column) consist of a key string following of the index of the
 * row and it is used for internal purposes only.</li> <li>The key data is
 * hidden.</li> <li>The information of a row (except the first column) can not
 * be taken up or down.</li> <li>The rows can not be removed.</li> <li>The first
 * editable row is the one that has the values of the potentials.</li> <li>The
 * rows between 0 and the first editable row are ocuppied by the values of the
 * states of the parents of the variable.</li> <li>The header of columns is
 * hidden.</li>
 * 
 * @author jlgozalo
 * @author myebra
 */
@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "Table")
public class TablePotentialPanel extends ProbabilityTablePanel {
    protected Logger      logger;
    /**
     * JTable where show the values.
     */
    protected ValuesTable valuesTable           = null;
    /**
     * Indicates if the data of the table is modifiable.
     */
    private boolean       modifiable;
    /**
     * Panel to scroll the table.
     */
    protected JScrollPane valuesTableScrollPane = null;
    protected ProbNode    probNode;
    protected boolean     hasLinkRestriction;

    /**
     * Constructor use by CPTablePanel
     * 
     * @param probNode
     */
    public TablePotentialPanel(ProbNode probNode) {
        super();
        this.probNode = probNode;
        modifiable = true;
        showValuesTable(true);
        setTableSpecificListeners();
        setData(probNode);
        setLayout(new BorderLayout());
        add(getValuesTableScrollPane(), BorderLayout.CENTER);
        repaint();
        // add(getCommentHTMLScrollPaneNodeDefinitionComment(),BorderLayout.SOUTH);
    }

    /**
     * Method to define the specific listeners in this table (not defined in the
     * common KeyTable hierarchy. This method creates the evidenceCase object
     * when the user do right click on the table.
     */
    private UncertaintyContextualMenu uncertaintyContextualMenu;

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
        valuesTable.resetModel();
        // valuesTable.setVariable(probNode.getPotentials().get( 0
        // ).getVariable( 0 ));
        valuesTable.setModel(getTableModel());
        valuesTable.initializeDataModified(false);
        ((ValuesTableModel) valuesTable.getModel()).setFirstEditableRow(firstEditableRow);
        valuesTable.setLastEditableRow(lastEditableRow);
        valuesTable.setShowingAllParameters(true);
        valuesTable.setNodeType(nodeType);
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
    public void setData(ProbNode probNode) {
        this.probNode = probNode;
        hasLinkRestriction = LinkRestrictionPotentialOperations.hasLinkRestriction(probNode);
        valuesTable.setData(probNode);
        Object[][] tableData = null;
        boolean[] uncertaintyInColumns = null;
        String[] newColumns = null;
        if (probNode.getPotentials() != null) {
            tableData = convertListPotentialsToTableFormat(probNode);
            newColumns = ValuesTable.getColumnsIdsSpreadSheetStyle(tableData[0].length);
            setFirstEditableRow(PotentialsTablePanelOperations.calculateFirstEditableRow(probNode.getPotentials(),
                    probNode));
            setLastEditableRow(PotentialsTablePanelOperations.calculateLastEditableRow(probNode.getPotentials(),
                    probNode));
            setData(tableData,
                    newColumns,
                    firstEditableRow,
                    lastEditableRow,
                    probNode.getNodeType());
            uncertaintyInColumns = getUncertaintyInColumns(probNode);
            setCellRenderers(uncertaintyInColumns);
            this.getTableModel().setNotEditablePositions(getNotEditablePositions(probNode));
            valuesTable.fitColumnsWidthToContent();
        } else {
            tableData = new Object[0][0];
            setFirstEditableRow(0);
            setData(tableData);
            setCellRenderers(uncertaintyInColumns);
        }
    }

    private boolean[] getUncertaintyInColumns(ProbNode probNode) {
        int size = valuesTable.getColumnCount();
        boolean[] uncertaintyInColumns = new boolean[size - 1];

        if (probNode.getPotentials().size() > 0) {

            TablePotential tablePotential = (TablePotential) probNode.getPotentials().get(0);
            for (int i = 1; i < size; i++) {
                boolean hasUncertainty = false;
                try {
                    EvidenceCase configuration = getConfiguration(tablePotential, i);
                    hasUncertainty = tablePotential.hasUncertainty(configuration);
                } catch (InvalidStateException | IncompatibleEvidenceException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            stringDatabase.getString(e.getMessage()),
                            stringDatabase.getString(e.getMessage()),
                            JOptionPane.ERROR_MESSAGE);
                }
                uncertaintyInColumns[i - 1] = hasUncertainty;
            }
        }
        return uncertaintyInColumns;
    }

    /**
     * calculate the number of rows of the table based on the type of the node,
     * the number of parents and the number of states of the variable
     * 
     * @param additionalProperties
     *            - node additionalProperties
     * @return the number of rows of this Potentials Table
     */
    protected int howManyRows(ProbNode properties) {
        int numRows = 0;
        if (properties.getNode().getParents() != null) {
            numRows = properties.getNode().getParents().size();
        }
        if (properties.getNodeType() == NodeType.UTILITY) {
            numRows += 1;
        } else {
            if (properties.getVariable().getStates() != null) {
                numRows = numRows + properties.getVariable().getStates().length;
            }
        }
        return numRows;
    }

    /**
     * Set a blank data table
     * 
     * @param additionalProperties
     *            - to obtain the required number of rows and columns
     * @return the blank data table
     */
    private Object[][] setBlankTable(ProbNode properties) {
        Object[][] blankTable = null;
        int numRows = howManyRows(properties);
        int numColumns = ValuesTable.howManyColumns(properties);
        blankTable = new Object[numRows][numColumns];
        // TODO seria mas practico hacer un potential y luego ejecutar
        // el resto del metodo pero esto funciona
        for (int i = 0; i < properties.getVariable().getStates().length; i++) {
        }
        return blankTable;
    }

    /**
     * to retrieve the ListPotentials corresponding to the data in the table
     * 
     * @return
     */
    public ArrayList<Potential> getListPotentialsFromData() {
        ArrayList<Potential> result = null;
        result = convertTableFormatToListPotentials(valuesTable);
        // setListPotentials(result);
        return result;
    }

    private TablePotential getThisPotential(List<Potential> listPotentials) {
        TablePotential aPotential = null;
        try {
            aPotential = ((TablePotential) listPotentials.get(0));
        } catch (Exception ex) {
            // ExceptionsHandler.handleException(
            // ex, "no Potential.get(0) !!!", false );
            logger.error("no Potential.get(0) !!!");
        }
        return aPotential;
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
    protected Object[][] convertListPotentialsToTableFormat(ProbNode probNode) {
        Object[][] values = null;
        try {
            // mpal
            PotentialsTablePanelOperations.checkIfNoPotential(probNode.getPotentials());
            values = setValuesTableSize(values, probNode);
            values = setParentsNameInUpperLeftCornerArea(values, probNode);
            values = setParentsStatesInTopArea(values, probNode);
            values = setNodeStatesInLeftArea(values, probNode);
            values = setPotentialDataInCentreArea(values, probNode);
            if (probNode.getNodeType() != NodeType.UTILITY) {
                values = setVariableNameInLowerLeftCornerArea(values, probNode);
                values = setVariableStatesInBottomArea(values, probNode);
            }
            setPosition(setNumberOfPostions(probNode.getPotentials()));
        } catch (NullListPotentialsException ex) {
            values = setBlankTable(probNode);
        }
        return values;
    }

    /**
     * set values table size for the potential
     * 
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setValuesTableSize(Object[][] oldValues, ProbNode probNode) {
        Object[][] values = oldValues;
        int numRows = 0;
        int numColumns = 1; // at least, there is one column for the node names
        int row = PotentialsTablePanelOperations.calculateFirstEditableRow(probNode.getPotentials(),
                probNode);
        setBaseIndexForCoordinates(row);
        setFirstEditableRow(row);
        TablePotential tablePotential = getThisPotential(probNode.getPotentials());
        List<Variable> variablesBeforeReorder = tablePotential.getVariables();
        setVariables(variablesBeforeReorder);
        if (probNode.getNodeType() == NodeType.UTILITY) {
            setBaseIndexForCoordinates(row - 1);
            numRows = getVariables().size();
            setLastEditableRow(numRows - 1);
            // numRows++;
            if (tablePotential.getTableSize() == 0)
                numColumns++;
            else
                numColumns += tablePotential.getTableSize();
        } else {
            // number of states of the conditioned variable
            int numDimensions = tablePotential.getDimensions()[0];
            // parents + variableStates
            numRows = getVariables().size() - 1 + numDimensions;
            setLastEditableRow(numRows - 1);
            numRows = numRows + 1; // + 1 for variableValues (when used in show
                                   // as Values
            if (numDimensions == 0) {
                // do nothing??
            } else { // all table div by variable states
                numColumns = numColumns + (tablePotential.getTableSize() / numDimensions);
            }
        }
        // create the array of arrays
        values = new Object[numRows][numColumns];
        return values;
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
     * This methods fills the Upper Left corner of the table with the name of
     * the parents of the node
     * 
     * @param values
     *            - the table that is being modified
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setParentsNameInUpperLeftCornerArea(Object[][] oldValues, ProbNode probNode) {
        Object[][] values = oldValues;
        List<Variable> parents = new ArrayList<Variable>();
        for (Variable variable : getVariables()) {
            if (!variable.getName().equals(probNode.getName())) {
                parents.add(variable);
            }
        }
        if ((parents != null) && (parents.size() > 0)) {
            for (int i = 0; i < parents.size(); i++) {
                values[i][0] = parents.get(parents.size()-i-1);
            }
        }
        return values;
    }

    /**
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setParentsStatesInTopArea(Object[][] oldValues, ProbNode probNode) {
        Object[][] values = oldValues;
        int numColumns = (values.length == 0 ? 0 : values[0].length);
        TablePotential tablePotential = getThisPotential(probNode.getPotentials());
        List<Variable> variables = tablePotential.getVariables();
        int[] offsets = tablePotential.getOffsets();
        int numStates = probNode.getVariable().getNumStates();
        int numVariables = tablePotential.getNumVariables();
        int numParentVariables = (tablePotential.getUtilityVariable() != null) ? tablePotential.getNumVariables()
                : tablePotential.getNumVariables() - 1;
        for (int row = 0; row < numParentVariables; row++) {
            int variableIndex = numVariables - row - 1;
            int numRepetitions = offsets[variableIndex] / numStates;
            State[] states = variables.get(variableIndex).getStates();
            int column = 1;
            while (column < numColumns) {
                for (State state : states) {
                    for (int i = 0; i < numRepetitions; i++) {
                        values[row][column] = state.getName();
                        column++;
                    }
                }
            }
        }
        return values;
    }

    /**
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private int setNumberOfPostions(List<Potential> listPotentials) {
        int numPositions = 1;
        try {
            for (Variable variable : listPotentials.get(0).getVariables()) {
                numPositions = numPositions * variable.getNumStates();
            }
        } catch (NullPointerException exception) {
            numPositions = 0;
            logger.error("not enough memory");
        }
        setPosition(numPositions);
        return numPositions;
    }

    /**
     * this method sets the first row with the values of the states of the node
     * (if it is a node chance) or the name of the variable of the node (if it
     * is a utility node)
     * 
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setNodeStatesInLeftArea(Object[][] oldValues, ProbNode properties) {
        Object[][] values = oldValues;
        TablePotential tablePotential = (TablePotential) getThisPotential(properties.getPotentials());
        int row = getFirstEditableRow();
        if (properties.getNodeType() == NodeType.UTILITY) {
            values[row][0] = properties.getName();
        } else
        /* if (properties.getNodeType() == NodeType.CHANCE) */{
            // set first column values with the state names
            if (0 < tablePotential.getDimensions()[0]) {
                // int numOfTheState =
                // tablePotential.getVariable( 0 ).getNumStates() - 1;
                int length = values.length - 2;
                for (State state : tablePotential.getVariable(0).getStates()) {
                    values[length--][0] = state.getName();
                    // row++;
                    // numOfTheState--;
                }
            }
        }
        return values;
    }

    /**
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setPotentialDataInCentreArea(Object[][] oldValues, ProbNode properties) {
        Object[][] values = oldValues;
        int position = 0;
        int numColumns = (values.length == 0 ? 0 : values[0].length);
        TablePotential tablePotential = (TablePotential) getThisPotential(properties.getPotentials());
        // rounding initial values
        double[] initialValues = tablePotential.getValues();
        double[] roundedValues = new double[initialValues.length];
        int maxDecimals = 10;
        double epsilon;
        epsilon = Math.pow(10, -(maxDecimals + 2));
        for (int i = 0; i < initialValues.length; i++) {
            roundedValues[i] = Util.roundAndReduce(initialValues[i], epsilon, maxDecimals);
        }
        tablePotential.setValues(roundedValues);
        int cont = getLastEditableRow();
        for (int j = 1; j <= numColumns - 1; j++) {
            for (int i = cont; i >= getFirstEditableRow(); i--, position++) {
                double value = tablePotential.getValues()[position];
                values[i][j] = value;
            }
        }
        return values;
    }

    /****
     * Calculates the position on the dataTable for a state combination
     * 
     * @param stateIndices
     *            - indexes of the states
     * @return an array containing the row at the first position and the column
     *         at the second position.
     */
    private int[] getRowAndColumnForStateCombination(int[] stateIndices, TablePotential potential) {
        int numStates = probNode.getVariable().getNumStates();
        int position = potential.getPosition(stateIndices);
        int column = (position / numStates) + 1;
        int row = getLastEditableRow() - (position % numStates);
        return new int[] { row, column };
    }

    /****
     * Calculates the positions of the table which are not editable due to a
     * link restriction. If the position is not editable it contains the value
     * 1, otherwise it contains a null value.
     * @param probNode 
     * 
     * @return a two dimensional array with the size of the table containing the
     *         information about the editable positions.
     */
    private Object[][] getNotEditablePositions(ProbNode probNode) {
        Object[][] notEditablePositions = null;
        notEditablePositions = setValuesTableSize(notEditablePositions, probNode);
        if (probNode.getNodeType() == NodeType.CHANCE && hasLinkRestriction) {
            List<int[]> statesWithRestriction = LinkRestrictionPotentialOperations.getStateCombinationsWithLinkRestriction(probNode);
            TablePotential potential = (TablePotential) probNode.getPotentials().get(0);
            for (int[] state : statesWithRestriction) {
                // reorder the variables
                int[] reorderedState = new int[state.length];
                reorderedState[0] = state[0];
                for (int i = 1; i < state.length; i++) {
                    reorderedState[state.length - i] = state[i];
                }
                int[] position = getRowAndColumnForStateCombination(reorderedState, potential);
                int row = position[0];
                int column = position[1];
                notEditablePositions[row][column] = 1;
            }
        }
        
        boolean[] uncertaintyInColumns = getUncertaintyInColumns(probNode);
        for(int row=firstEditableRow; row< notEditablePositions.length; ++row)
        {
            for (int column = 1; column < notEditablePositions[0].length; ++column) {
                if(uncertaintyInColumns[column-1])
                {
                    notEditablePositions[row][column] = 1;
                }
            }
        }
        return notEditablePositions;
    }

    /**
     * In the lower left corner area, the last row is reserved in the model for
     * displaying the name of the variable
     * 
     * @param values
     *            - the table that is being modified
     * @param listPotentials
     *            - the list of potentials of the node
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setVariableNameInLowerLeftCornerArea(Object[][] oldValues,
            ProbNode properties) {
        Object[][] values = oldValues;
        values[getLastEditableRow() + 1][0] = properties.getName();
        return values;
    }

    /**
     * In a discretize table model that shows only values (not probabilities),
     * this area will store the name of the state that is required to display
     * 
     * @param values
     *            - the table that is being modified
     * @param additionalProperties
     *            - the additionalProperties of the node
     */
    private Object[][] setVariableStatesInBottomArea(Object[][] oldValues, ProbNode properties) {
        Object[][] values = oldValues;
        int numColumns = (values.length == 0 ? 0 : values[0].length);
        TablePotential tablePotential = (TablePotential) getThisPotential(properties.getPotentials());
        State[] states = tablePotential.getVariable(0).getStates();
        double max;
        for (int j = numColumns - 1; j >= 1; j--) {
            max = (Double) values[getFirstEditableRow()][j];
            values[getLastEditableRow() + 1][j] = states[0].getName();
            for (int i = getFirstEditableRow() + 1; i <= getLastEditableRow(); i++) {
                if (((Double) values[i][j]) > max) {
                    max = (Double) values[i][j];
                    values[getLastEditableRow() + 1][j] = states[i - getFirstEditableRow()].getName();
                }
            }
        }
        return values;
    }

    /**
     * Convert the table with the data in a List of Potentials to be saved
     * 
     * @param valuesTable
     *            - the table with the data
     * @return a list of Potentials
     */
    private ArrayList<Potential> convertTableFormatToListPotentials(ValuesTable valuesTable) {
        ArrayList<Potential> listPotentials = new ArrayList<Potential>();
        if (getPosition() >= 0) { // it is not a Decision node
            double[] table = new double[getPosition()];
            TablePotential tablePotential = null;
            int position = 0;
            for (int j = valuesTable.getColumnCount() - 1; j > 0; j--) {
                for (int i = valuesTable.getLastEditableRow() - 1; i >= getFirstEditableRow(); i--, position++) {
                    table[position] = (Double) valuesTable.getModel().getValueAt(i, j);
                }
            }
            tablePotential = new TablePotential(getVariables(),
                    PotentialRole.CONDITIONAL_PROBABILITY,
                    table);
            listPotentials.add(tablePotential);
        }
        return listPotentials;
    }

    /**
     * This method generates the evidenceCase based on the column selected on
     * the <code>valuesTable</code> object.
     * 
     * @param tablePotential
     *            The TablePotential object edited
     * @param col
     *            The column selected. Never is 0 , because the column 0 is the
     *            states column
     * @return An evidence case object
     * @throws InvalidStateException
     * @throws IncompatibleEvidenceException
     */
    private EvidenceCase getConfiguration(TablePotential tablePotential, int col)
            throws InvalidStateException, IncompatibleEvidenceException {
        Variable variable = null;
        EvidenceCase evidence = new EvidenceCase();
        // configuration of all variables
        if (tablePotential.getPotentialRole() == PotentialRole.UTILITY 
                && tablePotential.getUtilityVariable() != null) {
            variable = tablePotential.getUtilityVariable();
            variables = tablePotential.getVariables();
        } else { 
            variable = tablePotential.getVariable(0);
            variables = tablePotential.getVariables();
            variables.remove(0);
        }
        int[] parentsConfiguration = new int[variables.size()];
        // Gets the start position of a reordered potential
        int startPosition = Util.toPositionOnPotentialReordered(variable.getNumStates()
                + variables.size()
                - 1,
                col,
                variable.getNumStates(),
                variables.size());
        // gets the configuration selected
        int[] configuration = tablePotential.getConfiguration(startPosition);
        // back to the original order of variables configuration
        // first value of configuration matches the value of the first variable
        // in inverse order because the potential visualization is in inverse
        // order
        int j = 0;
        int end = 0;
        if (variable == tablePotential.getUtilityVariable()) {
            end = -1;
        }
        for (int i = configuration.length - 1; i > end; i--) {
            parentsConfiguration[j++] = configuration[i];
        }
        // Gets the evidence
        j = 0;
        Finding finding;
        for (Variable var : variables) {
            finding = new Finding(var, parentsConfiguration[j]);
            evidence.addFinding(finding);
            j++;
        }
        return evidence;
    }

    public EvidenceCase getEvidenceCaseFromSelectedColumn() {
        EvidenceCase evi = null;
        try {
            evi = getConfiguration((TablePotential) probNode.getPotentials().get(0), selectedColumn);
        } catch (InvalidStateException | IncompatibleEvidenceException e) {
            e.printStackTrace();
        }
        return evi;
    }

    /**
     * Creates and shows the UncertainValuesDialog object
     * 
     * @throws WrongCriterionException
     */
    public void showUncertaintyDialog()
            throws WrongCriterionException {
        // Generates the evidenceCase based on the column
        // selected on the JTable object
        evidenceCase = getEvidenceCaseFromSelectedColumn();
        UncertainValuesDialog uncertDialog = new UncertainValuesDialog(Utilities.getOwner(this),
                evidenceCase,
                (TablePotential) probNode.getPotentials().get(0));
        int button = uncertDialog.requestUncertainValues();
        if (button == UncertainValuesDialog.OK_BUTTON) {
            UncertainValuesEdit uncertEdit = new UncertainValuesEdit(probNode,
                    uncertDialog.getUncertainColumn(),
                    uncertDialog.getValuesColumn(),
                    uncertDialog.getPosBase(),
                    selectedColumn,
                    uncertDialog.isChanceVariable());
            try {
                probNode.getProbNet().doEdit(uncertEdit);
                if (selectedColumn > 0) {
                    ((ValuesTableCellRenderer) getValuesTable().getDefaultRenderer(Double.class)).setMark(selectedColumn - 1);
                    getValuesTable().repaint();
                    this.getTableModel().setNotEditablePositions(getNotEditablePositions(probNode));
                }
            } catch (ConstraintViolationException
                    | CanNotDoEditException
                    | NonProjectablePotentialException
                    | DoEditException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        stringDatabase.getString(e.getMessage()),
                        stringDatabase.getString(e.getMessage()),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method initializes valuesTable and defines that first two columns
     * are not selectable
     * 
     * @return a new values table.
     */
    public ValuesTable getValuesTable() {
        if (valuesTable == null) {
            valuesTable = new ValuesTable(probNode, getTableModel(), modifiable);
            valuesTable.setName("PotentialsTablePanel.valuesTable");
        }
        return valuesTable;
    }

    /**
     * This method initializes valuesTableScrollPane.
     * 
     * @return a new values table scroll pane.
     */
    protected JScrollPane getValuesTableScrollPane() {
        if (valuesTableScrollPane == null) {
            valuesTableScrollPane = new JScrollPane();
            valuesTableScrollPane.setName("TablePotentialPanel.valuesTableScrollPane");
            valuesTableScrollPane.setViewportView(getValuesTable());
        }
        return valuesTableScrollPane;
    }

    /**
     * special method to show/hide the values table
     */
    public void showValuesTable(final boolean visible) {
        getValuesTable().setVisible(visible);
    }

    /**
     * This method initializes tableModel.
     * 
     * @return a new tableModel.
     */
    protected ValuesTableModel getTableModel() {
        ValuesTableModel tableModel = null;
        if (valuesTable == null) {
            tableModel = new ValuesTableModel(data, columns, firstEditableRow);
        } else if (valuesTable.getTableModel() == null) {
            tableModel = new ValuesTableModel(data, columns, firstEditableRow);
        } else {
            tableModel = (ValuesTableModel) valuesTable.getModel();
        }
        return tableModel;
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * deterministic
     */
    public void setDeterministicModel() {
        valuesTable.setDeterministic(true);
        setShowAllParameters(true);
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * probabilistic
     */
    public void setProbabilisticModel() {
        valuesTable.setDeterministic(false);
        setShowAllParameters(true);
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * optimal (decision node)
     */
    public void setOptimalModel() {
        valuesTable.setShowingOptimal(true);
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * general (TablePotential)
     */
    public void setGeneralModel(int familyIndex) {
        valuesTable.setUsingGeneralPotential(familyIndex);
    }

    /**
     * This method handles the type of potential to be used for the model to be
     * canonical (ICIPotential)
     */
    public void setCanonicalModel(int familyIndex) {
        valuesTable.setUsingGeneralPotential(familyIndex);
    }

    /**
     * @param showAllParameters
     *            the showAllParameters to set
     */
    public void setShowAllParameters(boolean showAllParameters) {
        this.showAllParameters = showAllParameters;
        valuesTable.setShowingAllParameters(showAllParameters);
    }

    /**
     * @param showProbabilitiesValues
     *            the showProbabilitiesValues to set
     */
    public void setShowProbabilitiesValues(boolean showProbabilitiesValues) {
        this.showProbabilitiesValues = showProbabilitiesValues;
        valuesTable.setShowingProbabilitiesValues(showProbabilitiesValues);
    }

    /**
     * @param showTPCvalues
     *            the showTPCvalues to set
     */
    public void setShowTPCvalues(boolean showTPCvalues) {
        this.showTPCvalues = showTPCvalues;
        valuesTable.setShowingTPCvalues(showTPCvalues);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals(ActionCommands.UNCERTAINTY_ASSIGN)
                || actionCommand.equals(ActionCommands.UNCERTAINTY_EDIT)) {
            try {
                showUncertaintyDialog();
            } catch (WrongCriterionException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        stringDatabase.getString(e1.getMessage()),
                        stringDatabase.getString(e1.getMessage()),
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (actionCommand.equals(ActionCommands.UNCERTAINTY_REMOVE)) {
            try {
                removeUncertainty();
            } catch (WrongCriterionException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        stringDatabase.getString(e1.getMessage()),
                        stringDatabase.getString(e1.getMessage()),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Method for removing the uncertain values for a certain configuration
     * 
     * @throws WrongCriterionException
     */
    public void removeUncertainty()
            throws WrongCriterionException {
        evidenceCase = getEvidenceCaseFromSelectedColumn();
        UncertainValuesRemoveEdit uncertEdit = new UncertainValuesRemoveEdit(probNode, evidenceCase);
        try {
            probNode.getProbNet().doEdit(uncertEdit);
            if (selectedColumn > 0) {
                ((ValuesTableCellRenderer) getValuesTable().getDefaultRenderer(Double.class)).unMark(selectedColumn - 1);
                getValuesTable().repaint();
                this.getTableModel().setNotEditablePositions(getNotEditablePositions(probNode));                
            }
        } catch (ConstraintViolationException
                | CanNotDoEditException
                | NonProjectablePotentialException
                | DoEditException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    stringDatabase.getString(e.getMessage()),
                    stringDatabase.getString(e.getMessage()),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateContextualMenuOptions() {
        if (probNode.getPotentials().size() > 0
                && probNode.getPotentials().get(0) instanceof TablePotential) {
            TablePotential tablePotential = (TablePotential) probNode.getPotentials().get(0);
            boolean hasUncertainty = tablePotential.hasUncertainty(getEvidenceCaseFromSelectedColumn());
            if (hasUncertainty) {
                getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_ASSIGN.toString()).setEnabled(false);
                getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_EDIT.toString()).setEnabled(true);
                getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_REMOVE.toString()).setEnabled(true);
            } else {
                getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_ASSIGN.toString()).setEnabled(true);
                getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_EDIT.toString()).setEnabled(false);
                getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_REMOVE.toString()).setEnabled(false);
            }
        }
    }
    
    private void doubleClickEvent(MouseEvent evt)
    {
        if (probNode.getPotentials().size() > 0
                && probNode.getPotentials().get(0) instanceof TablePotential) {
            TablePotential tablePotential = (TablePotential) probNode.getPotentials().get(0);
            
            EvidenceCase configuration = null;
            int selectedColumn = valuesTable.columnAtPoint(evt.getPoint());
            try {
                configuration = getConfiguration((TablePotential) probNode.getPotentials().get(0), selectedColumn);
            } catch (InvalidStateException | IncompatibleEvidenceException e) {
                e.printStackTrace();
            }
            boolean hasUncertainty = tablePotential.hasUncertainty(configuration);
            if(hasUncertainty)
            {
                try
                {
                    showUncertaintyDialog ();
                }
                catch (WrongCriterionException e1)
                {
                    e1.printStackTrace ();
                    JOptionPane.showMessageDialog (this, stringDatabase.getString (e1.getMessage ()),
                                                   stringDatabase.getString (e1.getMessage ()),
                                                   JOptionPane.ERROR_MESSAGE);
                }
            }
        }              
    }

    /**
     * This method initializes uncertaintyContextualMenu.
     * 
     * @return the node contextual menu.
     */
    private UncertaintyContextualMenu getUncertaintyContextualMenu() {
        if (uncertaintyContextualMenu == null) {
            uncertaintyContextualMenu = new UncertaintyContextualMenu(this);
            uncertaintyContextualMenu.setName("uncertaintyContextualMenu");
        }
        return uncertaintyContextualMenu;
    }

    /**
     * set renders for the cells in the table. Only has to be called when set
     * data.
     * @param uncertaintyInColumns2 
     */
    protected void setCellRenderers(boolean[] uncertaintyInColumns) {
        int firstEditableRow = getFirstEditableRow();
        TableCellRenderer cellRenderer = null;
        if (probNode.getPotentials().size() > 0) {
            if (probNode.getNodeType() != NodeType.DECISION) {
                if (!hasLinkRestriction) {
                    cellRenderer = new ValuesTableCellRenderer(firstEditableRow, uncertaintyInColumns);
                } else {
                    cellRenderer = new ValuesTableWithLinkRestrictionCellRenderer(firstEditableRow,
                            uncertaintyInColumns);
                }
            } else { // probNode.getNodeType() == NodeType.DECISION)
                if (probNode.getPolicyType() == PolicyType.OPTIMAL
                        && (probNode.getPotentials().isEmpty() || !probNode.getPotentials().get(0).isUtility())) {
                    cellRenderer = new ValuesTableOptimalPolicyCellRenderer(firstEditableRow,
                            uncertaintyInColumns);
                } else {
                    cellRenderer = new ValuesTableCellRenderer(firstEditableRow, uncertaintyInColumns);
                }
            }
            valuesTable.setDefaultRenderer(Double.class, cellRenderer);
            valuesTable.setDefaultRenderer(String.class, cellRenderer);
        }

    }

    /**
     * Method to define the specific listeners in this table (not defined in the
     * common KeyTable hierarchy. This method creates the evidenceCase object
     * when the user do right click on the table.
     */
    protected void setTableSpecificListeners() {
        valuesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = valuesTable.rowAtPoint(e.getPoint());
                int col = valuesTable.columnAtPoint(e.getPoint());
                selectedColumn = col;
                if (SwingUtilities.isLeftMouseButton(e)) {
                    valuesTable.editCellAt(valuesTable.rowAtPoint(e.getPoint()),
                            valuesTable.columnAtPoint(e.getPoint()),
                            e);
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    if ((row > -1) && (col > 0)) {
                        if (getUncertaintyContextualMenu() != null) {
                            updateContextualMenuOptions();
                            getUncertaintyContextualMenu().show(valuesTable, e.getX(), e.getY());
                        }
                    }
                }  
            }
        });
        valuesTable.addMouseListener(new DoubleClickListener());
    }
    
    public class DoubleClickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2)
            {
                doubleClickEvent(e);
            }
        }
    }
                    

    @Override
    public void close() {
        getValuesTable().close();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        getValuesTable().setModifiable(!readOnly);
    }
}
