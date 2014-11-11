/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.component;

import javax.swing.table.DefaultTableModel;

/**
 * ValuesTableModel defines the basic behavior of the Table Model
 * @author jlgozalo
 * @version 1.0 7 Jul 2009
 */
public class ValuesTableModel extends DefaultTableModel
{
    /**
     * calculated serial ID
     */
    private static final long serialVersionUID = 7010730473355625101L;
    /**
     * first editable row. By default, all rows are editable (first 0)
     */
    int                       firstEditableRow = 0;
    /***
     * List of additional not editable positions
     */
    private Object[][]        notEditablePositions;

    /**
     * constructor
     */
    public ValuesTableModel ()
    {
        super ();
    }

    /**
     * constructor
     */
    public ValuesTableModel (Object[][] data, String[] columns, int firstEditableRow)
    {
        super (data, columns);
        this.firstEditableRow = firstEditableRow;
        this.notEditablePositions = new Object[0][0];
    }

    /**
     * This method determines the default renderer/editor for each cell. First
     * column is a String class type and the others are double type.
     */
    public Class<?> getColumnClass (int c)
    {
        return (c == 0) ? String.class : Double.class;
    }

    /**
     * This method determines if the cell is editable or not, considering : <li>
     * all rows in the header are not editable</li> <li>column with the name of
     * the parents and the values are not editable</li>
     */
    public boolean isCellEditable (int row, int col)
    {
        if (row < firstEditableRow)
        {
            return false;
        }
        if (col < ValuesTable.FIRST_EDITABLE_COLUMN)
        {
            // states names are not editable
            return false;
        }
        if (notEditablePositions.length > row && notEditablePositions[0].length > col)
        {
            if ((notEditablePositions[row][col]) != null)
            {
                return false;
            }
        }
        return true; // all other cells are editable
    }

    /**
     * @return the firstEditableRow
     */
    public int getFirstEditableRow ()
    {
        return firstEditableRow;
    }

    /**
     * @param firstEditableRow the firstEditableRow to set
     */
    public void setFirstEditableRow (int firstEditableRow)
    {
        this.firstEditableRow = firstEditableRow;
    }

    public Object[][] getNotEditablePositions ()
    {
        return notEditablePositions.clone ();
    }

    public void setNotEditablePositions (Object[][] notEditablePositions)
    {
        this.notEditablePositions = notEditablePositions;
    }
}
