/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This panel contains a table whose first column represents a key data.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Add support for i18N by having setName() property to
 *          all components Change attributes to protected to allow extension
 */
public class KeyTablePanel extends JPanel implements ActionListener, ListSelectionListener {

    /**
     * Static field for serializable class.
     */
    private static final long   serialVersionUID      = 6257314234781632512L;

    /**
     * Panel to scroll the table.
     */
    protected JScrollPane       valuesTableScrollPane = null;

    /**
     * Table where show the values.
     */
    protected KeyTable          valuesTable           = null;

    /**
     * Model table.
     */
    protected DefaultTableModel tableModel            = null;

    /**
     * Panel of buttons.
     */
    protected JPanel            buttonPanel           = null;

    /**
     * Name of the columns of the table.
     */
    protected String[]          columns               = null;

    /**
     * Data of the cells.
     */
    protected Object[][]        data                  = null;

    /**
     * This variable enables the buttons to reorder the elements of the table.
     */
    protected boolean           reorderable;

    /**
     * Indicates if the data of the table is modifiable.
     */
    private boolean             modifiable;
    
    /**
     * Indicates if the header is shown
     */
    private boolean             showHeader;
    

    /**
     * Button to bring one value up.
     */
    protected JButton           upValueButton         = null;

    /**
     * Button to bring one value down.
     */
    protected JButton           downValueButton       = null;

    /**
     * Button to add a new value.
     */
    protected JButton           addValueButton        = null;

    /**
     * Button to delete an existing value.
     */
    protected JButton           removeValueButton     = null;

    /**
     * Icon loader.
     */
    protected IconLoader        iconLoader            = null;

    /**
     * String Database
     */
    protected StringDatabase    stringDatabase        = StringDatabase.getUniqueInstance();

    /**
     * this is a default constructor with no construction parameters
     */
    public KeyTablePanel() {

        iconLoader = new IconLoader();
        reorderable = false;
        modifiable = false;
        showHeader = false;
    }

    /**
     * This is the default constructor
     * 
     * @param columns
     *            array of texts that appear in the header of the columns.
     * @param data
     *            content of the cells.
     * @param reorderable
     *            if true, the elements of the table can be reordered.
     * @param modifiable
     *            if true, the cells of the table (except the first) are
     *            modifiable.
     * @param notifier
     *            - ElementObservable notifier
     */
    public KeyTablePanel(String[] columns, Object[][] data, boolean reorderable,
            boolean modifiable, boolean showHeader) {

        iconLoader = new IconLoader();
        this.columns = columns.clone();
        this.data = data.clone();
        this.reorderable = reorderable;
        this.modifiable = modifiable;
        this.showHeader = showHeader;
    }
    
    public KeyTablePanel(String[] columns, Object[][] data, boolean reorderable,
            boolean modifiable) {
        this(columns, data, reorderable, modifiable, false);
    }

    /**
     * This method initializes this instance.
     */
    protected void initialize() {

        setBorder(new LineBorder(UIManager.getColor("Table.dropLineColor"), 1, false));

        final GroupLayout groupLayout = new GroupLayout((JComponent) this);
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(getValuesTableScrollPane(),
                GroupLayout.PREFERRED_SIZE,
                406,
                GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(getButtonPanel(),
                GroupLayout.DEFAULT_SIZE,
                74,
                Short.MAX_VALUE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addComponent(getValuesTableScrollPane(),
                GroupLayout.DEFAULT_SIZE, /* 274 */
                280,
                Short.MAX_VALUE).addGap(24, 24, 24)).addGroup(GroupLayout.Alignment.LEADING,
                groupLayout.createSequentialGroup().addComponent(getButtonPanel(),
                        GroupLayout.DEFAULT_SIZE, /* 262 */
                        270,
                        Short.MAX_VALUE).addContainerGap()));
        setLayout(groupLayout);
    }

    /**
     * This method initializes valuesTableScrollPane.
     * 
     * @return a new values table scroll pane.
     */
    protected JScrollPane getValuesTableScrollPane() {

        if (valuesTableScrollPane == null) {
            valuesTableScrollPane = new JScrollPane(getValuesTable());
            valuesTableScrollPane.setName("KeyTablePanel.valuesTableScrollPane");
        }
        return valuesTableScrollPane;
    }

    /**
     * This method initializes valuesTable.
     * 
     * @return a new values table.
     */
    public KeyTable getValuesTable() {

        if (valuesTable == null) {
            valuesTable = new KeyTable(getTableModel(), modifiable, true, showHeader);
            valuesTable.setName("KeyTablePanel.valuesTable");
            valuesTable.setListSelectionListener(this);
        }
        return valuesTable;
    }

    /**
     * This method initializes tableModel.
     * 
     * @return a new tableModel.
     */
    protected DefaultTableModel getTableModel() {

        if (tableModel == null) {
            tableModel = new DefaultTableModel(data, columns);

        }
        return tableModel;
    }

    /**
     * This method initializes buttonPanel.
     * 
     * @return a new button panel.
     */
    protected JPanel getButtonPanel() {

        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setName("KeyTablePanel.buttonPanel");
            final GroupLayout groupLayout = new GroupLayout((JComponent) buttonPanel);
            groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(getAddValueButton(),
                    GroupLayout.DEFAULT_SIZE,
                    62,
                    Short.MAX_VALUE).addComponent(getDownValueButton(),
                    GroupLayout.Alignment.LEADING,
                    GroupLayout.DEFAULT_SIZE,
                    62,
                    Short.MAX_VALUE).addComponent(getUpValueButton(),
                    GroupLayout.Alignment.LEADING,
                    GroupLayout.DEFAULT_SIZE,
                    62,
                    Short.MAX_VALUE).addComponent(getRemoveValueButton(),
                    GroupLayout.Alignment.LEADING,
                    GroupLayout.DEFAULT_SIZE,
                    62,
                    Short.MAX_VALUE)).addContainerGap()));
            groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addComponent(getAddValueButton()).addGap(5,
                    5,
                    5).addComponent(getRemoveValueButton()).addGap(5, 5, 5).addComponent(getUpValueButton()).addGap(5,
                    5,
                    5).addComponent(getDownValueButton()).addGap(88, 88, 88)));
            buttonPanel.setLayout(groupLayout);
        }
        return buttonPanel;
    }

    /**
     * This method initializes upValueButton.
     * 
     * @return a new up value button.
     */
    protected JButton getUpValueButton() {

        if (upValueButton == null) {
            upValueButton = new JButton();
            upValueButton.setName("KeyTablePanel.upValueButton");
            upValueButton.setText(stringDatabase.getString("Up.Text.Label"));
            upValueButton.setMnemonic(stringDatabase.getString("Up.Text.Mnemonic").charAt(0));
            upValueButton.setIcon(iconLoader.load(IconLoader.ICON_ARROW_UP_ENABLED));
            upValueButton.setVisible(reorderable);
            upValueButton.setEnabled(false);
            upValueButton.addActionListener(this);
        }
        return upValueButton;
    }

    /**
     * This method initializes downValueButton.
     * 
     * @return a new down value button.
     */
    protected JButton getDownValueButton() {

        if (downValueButton == null) {
            downValueButton = new JButton();
            downValueButton.setName("KeyTablePanel.downValueButton");
            downValueButton.setText(stringDatabase.getString("Down.Text.Label"));
            downValueButton.setMnemonic(stringDatabase.getString("Down.Text.Mnemonic").charAt(0));
            downValueButton.setIcon(iconLoader.load(IconLoader.ICON_ARROW_DOWN_ENABLED));
            downValueButton.setVisible(reorderable);
            downValueButton.setEnabled(false);
            downValueButton.addActionListener(this);
        }
        return downValueButton;
    }

    /**
     * This method initializes addValueButton.
     * 
     * @return a new add value button.
     */
    protected JButton getAddValueButton() {

        if (addValueButton == null) {
            addValueButton = new JButton();
            addValueButton.setName("KeyTablePanel.addValueButton");
            addValueButton.setText(stringDatabase.getString("Add.Text.Label"));
            addValueButton.setMnemonic(stringDatabase.getString("Add.Text.Mnemonic").charAt(0));
            addValueButton.setIcon(iconLoader.load(IconLoader.ICON_PLUS_ENABLED));
            addValueButton.addActionListener(this);
        }
        return addValueButton;
    }

    /**
     * Enables or disabled the AddValue button.
     * 
     * @param enabled
     *            if true, it will be enabled; otherwise, disabled.
     */
    public void setEnabledAddValue(boolean enabled) {
        addValueButton.setEnabled(enabled);
    }

    public void setEnabledRemoveValue(boolean enabled) {
        removeValueButton.setEnabled(enabled);
    }

    public void setEnabledDownValue(boolean enabled) {
        downValueButton.setEnabled(enabled);
    }

    public void setEnabledUpValue(boolean enabled) {
        upValueButton.setEnabled(enabled);
    }

    public void setVisibleAddValue(boolean visible) {
        addValueButton.setVisible(visible);
    }

    public void setVisibleRemoveValue(boolean visible) {
        removeValueButton.setVisible(visible);
    }

    public void setVisibleDownValue(boolean visible) {
        downValueButton.setVisible(visible);
    }

    public void setVisibleUpValue(boolean visible) {
        upValueButton.setVisible(visible);
    }

    /**
     * This method initializes removeValueButton.
     * 
     * @return a new delete value button.
     */
    protected JButton getRemoveValueButton() {

        if (removeValueButton == null) {
            removeValueButton = new JButton();
            removeValueButton.setName("KeyTablePanel.removeValueButton");
            removeValueButton.setText(stringDatabase.getString("Delete.Text.Label"));
            removeValueButton.setMnemonic(stringDatabase.getString("Delete.Text.Mnemonic").charAt(0));
            removeValueButton.setIcon(iconLoader.load(IconLoader.ICON_MINUS_ENABLED));
            removeValueButton.setEnabled(false);
            removeValueButton.addActionListener(this);
        }
        return removeValueButton;
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            event information.
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(addValueButton)) {
            actionPerformedAddValue();
        } else if (e.getSource().equals(removeValueButton)) {
            actionPerformedRemoveValue();
        } else if (e.getSource().equals(upValueButton)) {
            actionPerformedUpValue();
        } else if (e.getSource().equals(downValueButton)) {
            actionPerformedDownValue();
        }
    }

    /**
     * Invoked when the button 'add' is pressed.
     */
    protected void actionPerformedAddValue() {

    };

    /**
     * Invoked when the button 'remove' is pressed.
     */
    protected void actionPerformedRemoveValue() {
        int selectedRowIndex = valuesTable.getSelectedRow();
        tableModel.removeRow(selectedRowIndex);
    };

    /**
     * Invoked when the button 'up' is pressed.
     */
    protected void actionPerformedUpValue() {
        int selectedRowIndex = valuesTable.getSelectedRow();
        tableModel.moveRow(selectedRowIndex, selectedRowIndex, selectedRowIndex-1);
        valuesTable.setRowSelectionInterval(selectedRowIndex-1, selectedRowIndex-1);
    };

    /**
     * Invoked when the button 'down' is pressed.
     */
    protected void actionPerformedDownValue() {
        int selectedRowIndex = valuesTable.getSelectedRow();
        tableModel.moveRow(selectedRowIndex, selectedRowIndex, selectedRowIndex+1);
        valuesTable.setRowSelectionInterval(selectedRowIndex+1, selectedRowIndex+1);
    };

    /**
     * Invoked when the row selection changes.
     * 
     * @param e
     *            selection event information.
     */
    public void valueChanged(ListSelectionEvent e) {

        int index = valuesTable.getSelectedRow();
        int rowCount = valuesTable.getRowCount();
        if ((rowCount == 0) || (index == -1)) {
            removeValueButton.setEnabled(false);
            upValueButton.setEnabled(false);
            downValueButton.setEnabled(false);
        } else {
            removeValueButton.setEnabled(true);
            if (index == 0) {
                upValueButton.setEnabled(false);
                if (index == (rowCount - 1)) {
                    downValueButton.setEnabled(false);
                } else {
                    downValueButton.setEnabled(true);
                }
            } else if (index == (valuesTable.getRowCount() - 1)) {
                downValueButton.setEnabled(false);
                if (index == 0) {
                    upValueButton.setEnabled(false);
                } else {
                    upValueButton.setEnabled(true);
                }
                upValueButton.setEnabled(true);
            } else {
                upValueButton.setEnabled(true);
                downValueButton.setEnabled(true);
            }
        }
        if (rowCount <= 2) {
            removeValueButton.setEnabled(false);
        } else {
            removeValueButton.setEnabled(true);
        }
    }

    /**
     * Cancels the editing in any cell of the table, avoiding its new value to
     * be recorded.
     */
    public void cancelCellEditing() {

        TableCellEditor currentEditor = valuesTable.getCellEditor();

        if (currentEditor != null) {
            currentEditor.cancelCellEditing();
        }
    }

    /**
     * Stops the editing in any cell of the table, recording the new value.
     */
    public void stopCellEditing() {

        TableCellEditor currentEditor = valuesTable.getCellEditor();

        if (currentEditor != null) {
            currentEditor.stopCellEditing();
        }
    }

    /**
     * Returns the content of the table.
     * 
     * @return the content of the table.
     */
    @SuppressWarnings("rawtypes")
    public Object[][] getData() {

        DefaultTableModel model = (DefaultTableModel) valuesTable.getModel();
        int columnCount = model.getColumnCount();
        int rowCount = model.getRowCount();
        int i = 0;
        int j = 0;
        Object[][] datatmp = new Object[rowCount][columnCount];
        Vector vectorData = model.getDataVector();
        Vector vectorRow = null;

        for (i = 0; i < rowCount; i++) {
            vectorRow = (Vector) vectorData.get(i);
            for (j = 0; j < columnCount; j++) {
                datatmp[i][j] = vectorRow.get(j);
            }
        }
        return datatmp;
    }

    // ESCA-JAVA0173:
    /**
     * Sets a new table model with new data.
     * 
     * @param newData
     *            new data for the table.
     */
    public void setData(Object[][] newData) {
        tableModel.setDataVector(newData, columns);
    }

}
