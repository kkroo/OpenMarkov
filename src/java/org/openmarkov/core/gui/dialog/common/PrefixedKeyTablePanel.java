/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.openmarkov.core.action.NodeStateEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Util;

/**
 * This class implements a key table with the following features:
 * <ul>
 * <li>Its elements, except the first column, are modifiable.</li>
 * <li>New elements can be added, creating a new key row with empty data.</li>
 * <li>The key data (first column) consist of a key string following of the
 * index of the row.</li>
 * <li>The information of a row (except the first column) can be taken up or
 * down.</li>
 * <li>The rows can be removed.</li>
 * </ul>
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @author myebra
 */
public class PrefixedKeyTablePanel extends KeyTablePanel implements TableModelListener {

    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID = 8550762264755243008L;

    /**
     * Key prefix.
     */
    private String            keyPrefix        = null;

    private ProbNode          probNode;

    private boolean           renameAction     = true;

    /*
     * this a default constructor with no construction parameters
     */
    public PrefixedKeyTablePanel() {

        keyPrefix = "";
        initialize();
        getValuesTable().getModel().addTableModelListener(this);
    }

    /**
     * This is the default constructor
     * 
     * @param newColumns
     *            array of texts that appear in the header of the columns.
     * @param noKeyData
     *            content of the cells except the first column.
     * @param newKeyPrefix
     *            prefix of the keys of each row that appear in the first
     *            column.
     */
    public PrefixedKeyTablePanel(String[] newColumns, Object[][] noKeyData, String newKeyPrefix,
            boolean firstColumnHidden) {// , ElementObservable notifier) {
        super(newColumns, new Object[0][0], true, true);// , notifier);
        keyPrefix = newKeyPrefix;
        initialize();
        getValuesTable().getModel().addTableModelListener(this);
        getValuesTable().setFirstColumnHidden(firstColumnHidden);
        setData(noKeyData);
    }

    public PrefixedKeyTablePanel(String[] newColumns, Object[][] noKeyData, String newKeyPrefix,
            boolean firstColumnHidden, ProbNode probNode) {
        super(newColumns, new Object[0][0], true, true);// , notifier);
        this.probNode = probNode;
        keyPrefix = newKeyPrefix;
        initialize();
        getValuesTable().setFirstColumnHidden(firstColumnHidden);
        setData(noKeyData);
    }

    /**
     * Sets a new table model with new data.
     * 
     * @param newData
     *            new data for the table without the key column.
     */
    @Override
    public void setData(Object[][] newData) {

        data = fillDataKeys(newData);
        tableModel = null;
        valuesTable.setModel(getTableModel());
        tableModel.addTableModelListener(this);

    }

    /**
     * This method takes a data object and creates a new column that content a
     * row key. This key begins with the key prefix following a number that
     * starts at 0.
     * 
     * @param oldData
     *            data to add a key column.
     * @return a data object with one more column that contains the keys.
     */
    private Object[][] fillDataKeys(Object[][] oldData) {

        Object[][] newData = null;
        int i1 = 0;
        int i2 = 0;
        int l1 = 0;
        int l2 = 0;

        l1 = oldData.length;
        if (l1 > 0) {
            l2 = oldData[0].length + 1;
            newData = new Object[l1][l2];
            for (i1 = 0; i1 < l1; i1++) {
                newData[i1][0] = getKeyString(i1);
                for (i2 = 1; i2 < l2; i2++) {
                    newData[i1][i2] = oldData[i1][i2 - 1];
                }
            }

            return newData;
        }

        return new Object[0][0];

    }

    /**
     * Returns a key represented by an index.
     * 
     * @param index
     *            index of the key which will be returned
     * @return the string that content the key.
     */
    private String getKeyString(int index) {

        return keyPrefix + index;

    }

    /**
     * Invoked when the button 'add' is pressed.
     */
    @Override
    protected void actionPerformedAddValue() {
        // TODO warning esto afecta a la tabla de propiedades adicionales
        String option = JOptionPane.showInputDialog(this,
                stringDatabase.getString("AddState.Message"),
                stringDatabase.getString("AddState.Title"),
                JOptionPane.QUESTION_MESSAGE);

        if (option != null) {
            int newIndex = 0;

            newIndex = valuesTable.getRowCount();

            NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode,
                    StateAction.ADD,
                    newIndex,
                    option);

            try {
                probNode.getProbNet().doEdit(nodeStateEdit);
                renameAction = false;
                tableModel.insertRow(0, new Object[] { getKeyString(newIndex), option });
                valuesTable.getSelectionModel().setSelectionInterval(0, 0);
                renameAction = false;

            } catch (ConstraintViolationException
                    | CanNotDoEditException
                    | NonProjectablePotentialException
                    | WrongCriterionException
                    | DoEditException e) {
                JOptionPane.showMessageDialog(this,
                        stringDatabase.getString(e.getMessage()),
                        stringDatabase.getString(e.getMessage()),
                        JOptionPane.ERROR_MESSAGE);

                // jTextFieldNodeName.setText( this.nodeProperties.getName() );
                // jTextFieldNodeName.requestFocus();

            }

        }
    }

    /**
     * Invoked when the button 'remove' is pressed.
     */
    @Override
    protected void actionPerformedRemoveValue() {

        int selectedRow = valuesTable.getSelectedRow();
        int rowCount = 0;

        NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode,
                StateAction.REMOVE,
                selectedRow,
                "");

        try {
            probNode.getProbNet().doEdit(nodeStateEdit);

            cancelCellEditing();
            renameAction = false;
            tableModel.removeRow(selectedRow);
            rowCount = valuesTable.getRowCount();
            if (rowCount > 0) {
                if (selectedRow < rowCount) {
                    valuesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
                    while (selectedRow < rowCount) {
                        renameAction = false;
                        tableModel.setValueAt(getKeyString(selectedRow), selectedRow, 0);
                        selectedRow++;
                    }
                } else {
                    valuesTable.getSelectionModel().setSelectionInterval(selectedRow - 1,
                            selectedRow - 1);
                }
            }

            renameAction = false;
        } catch (ConstraintViolationException
                | CanNotDoEditException
                | NonProjectablePotentialException
                | WrongCriterionException
                | DoEditException e) {
            JOptionPane.showMessageDialog(this,
                    stringDatabase.getString(e.getMessage()),
                    stringDatabase.getString(e.getMessage()),
                    JOptionPane.ERROR_MESSAGE);

            // jTextFieldNodeName.setText( this.nodeProperties.getName() );
            // jTextFieldNodeName.requestFocus();
            // e.printStackTrace();
        }
    }

    /**
     * Invoked when the button 'up' is pressed.
     */
    @Override
    protected void actionPerformedUpValue() {

        int selectedRow = valuesTable.getSelectedRow();
        Object swap = null;

        NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, StateAction.UP, selectedRow, "");

        try {
            probNode.getProbNet().doEdit(nodeStateEdit);

            stopCellEditing();
            swap = valuesTable.getValueAt(selectedRow, 1);
            renameAction = false;
            valuesTable.setValueAt(valuesTable.getValueAt(selectedRow - 1, 1), selectedRow, 1);
            renameAction = false;
            valuesTable.setValueAt(swap, selectedRow - 1, 1);
            valuesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
            renameAction = false;

        } catch (ConstraintViolationException
                | CanNotDoEditException
                | NonProjectablePotentialException
                | WrongCriterionException
                | DoEditException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    stringDatabase.getString(e.getMessage()),
                    stringDatabase.getString(e.getMessage()),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Invoked when the button 'down' is pressed.
     */
    @Override
    protected void actionPerformedDownValue() {

        int selectedRow = valuesTable.getSelectedRow();
        Object swap = null;

        NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, StateAction.DOWN, selectedRow, "");

        try {
            probNode.getProbNet().doEdit(nodeStateEdit);

            stopCellEditing();
            swap = valuesTable.getValueAt(selectedRow, 1);
            renameAction = false;
            valuesTable.setValueAt(valuesTable.getValueAt(selectedRow + 1, 1), selectedRow, 1);
            renameAction = false;
            valuesTable.setValueAt(swap, selectedRow + 1, 1);
            valuesTable.getSelectionModel().setSelectionInterval(selectedRow + 1, selectedRow + 1);
            renameAction = false;

        } catch (ConstraintViolationException
                | CanNotDoEditException
                | NonProjectablePotentialException
                | WrongCriterionException
                | DoEditException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    stringDatabase.getString(e.getMessage()),
                    stringDatabase.getString(e.getMessage()),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns the content of the table except the first column. This column
     * contains the keys generated automatically by this class and is only used
     * to display it not to manage it.
     * 
     * @return the content of the table except the first column.
     */
    @Override
    public Object[][] getData() {

        Object[][] content = super.getData();
        Object[][] result = null;
        int rowCount = content.length;
        int columnCount = 0;
        int i = 0;
        int j = 0;

        if (rowCount > 0) {
            columnCount = content[0].length;
            result = new Object[rowCount][columnCount - 1];
            for (i = 0; i < rowCount; i++) {
                for (j = 1; j < columnCount; j++) {
                    result[i][j - 1] = content[i][j];
                }
            }
        } else {
            result = new Object[0][0];
        }

        return result;

    }

    public void tableChanged(TableModelEvent e) {
        int row = e.getLastRow();

        if (e.getType() != TableModelEvent.DELETE
                && e.getType() != TableModelEvent.INSERT
                && renameAction) {
            Object value = ((DefaultTableModel) e.getSource()).getValueAt(row,e.getColumn());
            String newName = value.toString() ;

            NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode,
                    StateAction.RENAME,
                    row,
                    newName);
            try {
                probNode.getProbNet().doEdit(nodeStateEdit);
            } catch (ConstraintViolationException e1) {
                JOptionPane.showMessageDialog(this,
                        stringDatabase.getString(e1.getMessage()),
                        stringDatabase.getString(e1.getMessage()),
                        JOptionPane.ERROR_MESSAGE);
                // valuesTable.getSelectionModel().setSelectionInterval(row,
                // e.getColumn());
                int i = Util.toPositionOnPotentialReordered(row,
                        e.getColumn(),
                        probNode.getVariable().getNumStates(),
                        probNode.getNode().getNumParents());
                valuesTable.setValueAt(probNode.getVariable().getStates()[i].getName(),
                        row,
                        e.getColumn());

            } catch (CanNotDoEditException
                    | DoEditException
                    | NonProjectablePotentialException
                    | WrongCriterionException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        stringDatabase.getString(e1.getMessage()),
                        stringDatabase.getString(e1.getMessage()),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        renameAction = true;
    }
}
