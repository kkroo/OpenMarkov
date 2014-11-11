/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * This class implements a table that has at least one column. The first column
 * is unmodifiable, has a prefixed width and the rest of cells are modifiable
 * clicking twice on them or they can't be modified. It depends of a parameter
 * of the constructor. All of the columns cannot be resized.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class KeyTable extends JTable {

    /**
     * Static field for serializable class.
     */
    private static final long     serialVersionUID           = 5072153109141850112L;

    /**
     * Width of the key column.
     */
    protected static final int    KEY_COLUMN_WIDTH           = 60;

    /**
     * Width of the key column.
     */
    protected static final int    ROW_HEIGHT                 = 20;

    /**
     * Selection Background color
     */
    protected static final Color  SELECTION_BACKGROUND_COLOR = new Color(211, 211, 211);
    /**
     * Selection Foreground color
     */
    protected static final Color  SELECTION_FOREGROUND_COLOR = Color.BLACK;

    /**
     * Background color
     */
    protected static final Color  BACKGROUND_COLOR           = new Color(230, 230, 250);

    /**
     * Indicates if the object is already created.
     */
    protected boolean             created                    = false;

    /**
     * Outer object that listens to the changes of the table selection.
     */
    private ListSelectionListener listener                   = null;

    /**
     * This variable says if the table can be modified. False by default
     */
    protected boolean             modifiable;

    /**
     * This variable says if the first column is hidden. Not Visible by default
     */
    private boolean               firstColumnHidden          = true;

    /**
     * This variable is used to set additionalProperties for the columns in the
     * table
     */
    protected TableColumn         column                     = null;
    /**
     * This variable is used to set additionalProperties for the header in the
     * table
     */
    protected JTableHeader        header                     = null;
    /**
     * This variable is used to display or not the column header. Visible by
     * default
     */
    private boolean               showColumnHeader           = true;

    /**
     * default constructor without construction parameters
     */
    public KeyTable() {
        created = true;
        modifiable = false;
        defaultConfiguration();
    }

    /**
     * Constructs a JTable that is initialized with dm as the data model, a
     * default column model, and a default selection model.
     * 
     * @param dm
     *            the data model for the table.
     * @param newModifiable
     *            specifies if the cells (except the first column) are
     *            modifiable.
     */
    public KeyTable(TableModel dm, boolean newModifiable, boolean firstColumnHidden) {
        this(dm, newModifiable, firstColumnHidden, true);
    }

    /**
     * Constructs a JTable that is initialized with dm as the data model, a
     * default column model, and a default selection model.
     * 
     * @param dm
     *            the data model for the table.
     * @param newModifiable
     *            specifies if the cells (except the first column) are
     *            modifiable.
     */
    public KeyTable(TableModel dm, boolean newModifiable, boolean firstColumnHidden,
            boolean showColumnHeader) {

        super(dm);

        created = true;
        modifiable = newModifiable;
        this.firstColumnHidden = firstColumnHidden;
        this.showColumnHeader = showColumnHeader;
        defaultConfiguration();
    }

    /**
     * Sets a new list selection listener.
     * 
     * @param newListener
     *            new list selection listener.
     */
    public void setListSelectionListener(ListSelectionListener newListener) {

        listener = newListener;
    }

    // ESCA-JAVA0126:
    /**
     * Sets the data model for this table to newModel and registers with it for
     * listener notifications from the new data model.
     * 
     * @param newDataModel
     *            the new data source for this table.
     * @throws IllegalArgumentException
     *             if newModel is null.
     */
    @Override
    public void setModel(TableModel newDataModel)
            throws IllegalArgumentException {

        super.setModel(newDataModel);
        if (created) {
            defaultConfiguration();
        }
    }

    /**
     * @return the modifiable
     */
    public boolean isModifiable() {

        return modifiable;
    }

    /**
     * @param modifiable
     *            the modifiable to set
     */
    public void setModifiable(boolean modifiable) {

        this.modifiable = modifiable;
    }

    /**
     * @return the firstColumnHidden
     */
    public boolean isFirstColumnHidden() {

        return firstColumnHidden;
    }

    /**
     * @param firstColumnHidden
     *            the firstColumnHidden to set
     */
    public void setFirstColumnHidden(boolean firstColumnHidden) {

        this.firstColumnHidden = firstColumnHidden;
    }

    /**
     * @return the showColumnHeader
     */
    public boolean isShowColumnHeader() {

        return showColumnHeader;
    }

    /**
     * @param showColumnHeader
     *            the showColumnHeader to set
     */
    public void setShowColumnHeader(boolean showColumnHeader) {

        this.showColumnHeader = showColumnHeader;
    }

    /**
     * This method configures the table to a default state.
     */
    protected void defaultConfiguration() {

        TableCellEditor editorCell = null;

        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setBackground(BACKGROUND_COLOR);// Component color
        setSelectionBackground(SELECTION_BACKGROUND_COLOR);// Color for cell
                                                           // renderers
        setSelectionForeground(SELECTION_FOREGROUND_COLOR);
        setRowHeight(ROW_HEIGHT);
        setShowGrid(true);
        setGridColor(Color.DARK_GRAY);
        setShowVerticalLines(false);
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (getColumnClass(0) != null) {
            if (modifiable) {
                editorCell = getDefaultEditor(getColumnClass(0));
                if (editorCell instanceof DefaultCellEditor) {
                    ((DefaultCellEditor) editorCell).setClickCountToStart(2);
                }
            }
            header = getTableHeader();
            header.setReorderingAllowed(false);
            header.setResizingAllowed(false);
            header.setVisible(this.showColumnHeader);
            if (this.showColumnHeader) {
                header.setVisible(this.showColumnHeader);
            } else {
                header.setPreferredSize(new Dimension(20, 0));
            }
            column = getColumnModel().getColumn(0);
            if (firstColumnHidden && (column != null)) {
                column.setMaxWidth(0);
                column.setMinWidth(0);
                column.setWidth(0);
            }
        }
    }

    /**
     * Returns an appropriate editor for the cell specified by row and column.
     * If the column is 0, returns null, else returns the default editor.
     * 
     * @param row
     *            the row of the cell to edit, where 0 is the first row.
     * @param column
     *            the column of the cell to edit, where 0 is the first column.
     * @return the editor for this cell.
     */
    @Override
    public TableCellEditor getCellEditor(int row, int column) {

        return (!modifiable || (column == 0)) ? null : super.getCellEditor(row, column);
    }

    /**
     * Invoked when the row selection changes.
     * 
     * @param e
     *            selection event information.
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        super.valueChanged(e);
        if (listener != null) {
            listener.valueChanged(e);
        }
    }

    public void setValueAt(Object newValue, int row, int col) {
        Object oldValue = getValueAt(row, col);
        if (!newValue.equals(oldValue)) {
            super.getModel().setValueAt(newValue, row, col);
        }
    }

}
