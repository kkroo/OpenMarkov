/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class implements a dialog box where the user can select various elements
 * from a table.
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo not showing the id column
 */
public class KeyListSelectionDialog extends OkCancelHorizontalDialog
{
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID         = 1059576863283664256L;
    /**
     * Elements of the table.
     */
    private Object[][]        data                     = null;
    /**
     * Columns of the table.
     */
    private String[]          columns                  = null;
    /**
     * Selected rows of the table.
     */
    private Object[][]        selectedRows             = null;
    /**
     * Panel that contains the scroll pane.
     */
    private JPanel            valuesTableScrollPane    = null;
    /**
     * Panel to scroll the table.
     */
    private JScrollPane       subValuesTableScrollPane = null;
    /**
     * Table where show the values.
     */
    private KeyTable          valuesTable              = null;
    /**
     * Model table.
     */
    private DefaultTableModel tableModel               = null;

    /**
     * Constructor that calls the superclass constructor and saves the objects.
     * @param owner window that owns this dialog box.
     * @param title title of the dialog box.
     * @param newData content of the rows of the table.
     * @param newColumns titles of the columns of the table.
     */
    public KeyListSelectionDialog (Window owner,
                                   String title,
                                   Object[][] newData,
                                   String[] newColumns)
    {
        super (owner);
        data = newData.clone ();
        columns = newColumns.clone ();
        initialize (title);
        setLocationRelativeTo (owner);
    }

    /**
     * This method configures the dialog box.
     * @param title title of the dialog box.
     */
    private void initialize (String title)
    {
        setTitle (title);
        configureComponentsPanel ();
        pack ();
    }

    /**
     * Sets up the panel where all components, except the buttons of the buttons
     * panel, will be appear.
     */
    private void configureComponentsPanel ()
    {
        getComponentsPanel ().add (getValuesTableScrollPane ());
    }

    /**
     * This method initialises valuesTableScrollPane.
     * @return a new panel that contains the scroll pane.
     */
    private JPanel getValuesTableScrollPane ()
    {
        if (valuesTableScrollPane == null)
        {
            GridBagConstraints gridBagConstraints = new GridBagConstraints ();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.ipadx = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new Insets (5, 5, 0, 5);
            valuesTableScrollPane = new JPanel ();
            valuesTableScrollPane.setLayout (new GridBagLayout ());
            valuesTableScrollPane.add (getSubValuesTableScrollPane (), gridBagConstraints);
        }
        return valuesTableScrollPane;
    }

    /**
     * This method initialises subValuesTableScrollPane.
     * @return a new values table scroll pane.
     */
    private JScrollPane getSubValuesTableScrollPane ()
    {
        if (subValuesTableScrollPane == null)
        {
            subValuesTableScrollPane = new JScrollPane ();
            subValuesTableScrollPane.setViewportView (getValuesTable ());
            subValuesTableScrollPane.setMinimumSize (new Dimension (240, 180));
            subValuesTableScrollPane.setMaximumSize (new Dimension (240, 180));
            valuesTableScrollPane.setPreferredSize (new Dimension (240, 180));
        }
        return subValuesTableScrollPane;
    }

    /**
     * This method initialises valuesTable.
     * @return a new values table.
     */
    private KeyTable getValuesTable ()
    {
        if (valuesTable == null)
        {
            valuesTable = new KeyTable (getTableModel (), false, true);
            valuesTable.getSelectionModel ().setSelectionMode (ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        return valuesTable;
    }

    /**
     * This method initialises tableModel.
     * @return a new tableModel.
     */
    private DefaultTableModel getTableModel ()
    {
        if (tableModel == null)
        {
            tableModel = new DefaultTableModel (data, columns);
        }
        return tableModel;
    }

    // ESCA-JAVA0025:
    /**
     * Cancel the operation.
     */
    @Override
    protected void doCancelClickBeforeHide ()
    {
    }

    /**
     * Checks if the user has selected any element.
     * @return true if the dialog box must be closed; otherwise, false.
     */
    @Override
    protected boolean doOkClickBeforeHide ()
    {
        fillSelectedRows ();
        if (selectedRows == null)
        {
            JOptionPane.showMessageDialog (this,
                                           StringDatabase.getUniqueInstance ().getString ("NoRowsSelected.Text.Label"),
                                           StringDatabase.getUniqueInstance ().getString ("ErrorWindow.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Returns the selected elements of the table.
     * @return an array that contains the selected elements of the table or null
     *         if nothing is selected.
     */
    public Object[][] getSelectedRows ()
    {
        return selectedRows.clone ();
    }

    /**
     * Fill the array of the selected rows.
     */
    private void fillSelectedRows ()
    {
        int selectedRowCount = valuesTable.getSelectedRowCount ();
        int index = 0;
        int[] selectedIndexes = null;
        if (selectedRowCount > 0)
        {
            selectedIndexes = valuesTable.getSelectedRows ();
            selectedRows = new Object[selectedRowCount][columns.length];
            for (index = 0; index < selectedRowCount; index++)
            {
                selectedRows[index] = data[selectedIndexes[index]];
            }
        }
        else
        {
            selectedRows = null;
        }
    }

    /**
     * This method shows the dialog and requests the user to select at least one
     * row.
     * @return OK_BUTTON if the user has pressed the 'Ok' button or CANCEL_BUTTON
     *         if the user has pressed the 'Cancel' button.
     */
    public int requestSelectRows ()
    {
        setVisible (true);
        return selectedButton;
    }
}
