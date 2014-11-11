/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.component;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;

import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;

/**
 * This class is used for painting and coloring the ICItable and the headers
 * @author jlgozalo
 * @version 1.0 15/08/2009
 */
@SuppressWarnings("serial")
public class ICIValuesTableCellRenderer extends ValuesTableCellRenderer
{
    private List<Variable> variables;
    private int[]          numColumnsParents;
    private int            acummulativeColumns[];

    public ICIValuesTableCellRenderer (int firstEditableRow,
                                       boolean[] uncertaintyInColumns,
                                       ICIPotential iciPotential)
    {
        super (firstEditableRow, uncertaintyInColumns);
        this.variables = iciPotential.getVariables ();
        this.numColumnsParents = new int[variables.size ()];
        for (int i = 1; i < variables.size (); ++i)
        {
            numColumnsParents[i - 1] = variables.get (i).getNumStates ();
        }
        numColumnsParents[variables.size () - 1] = 1;
        acummulativeColumns = new int[variables.size ()];
        acummulativeColumns[0] = numColumnsParents[0];
        for (int i = 1; i < numColumnsParents.length; ++i)
        {
            acummulativeColumns[i] = numColumnsParents[i] + acummulativeColumns[i - 1];
        }
    }

    @Override
    protected void setCellColors (JTable table,
                                  Object value,
                                  boolean isSelected,
                                  boolean hasFocus,
                                  int row,
                                  int column)
    {
        if ((column < ValuesTable.FIRST_EDITABLE_COLUMN) & (row < firstEditableRow))
        { // PARENTS CELLS
            // set alternate colors
            // column = 0 row = 0 o 1
            switch (row % 3)
            {
                case 0 :
                    setBackground (new Color (220, 220, 220));
                    setForeground (TABLE_HEADER_TEXT_COLOR_1);
                    break;
                case 1 :
                    setBackground (new Color (220, 220, 220));
                    setForeground (TABLE_HEADER_TEXT_COLOR_2);
                    break;
                case 2 :
                    setBackground (new Color (220, 220, 220));
                    setForeground (TABLE_HEADER_TEXT_COLOR_3);
                    break;
                default :
                    break;
            }
        }
        // NEW
        if ((column < ValuesTable.FIRST_EDITABLE_COLUMN) & (row >= firstEditableRow))
        { // NODE STATES CELLS
            // column 0 child states
            // setBackground( Color.LIGHT_GRAY );
            setBackground (new Color (220, 220, 220));
            setForeground (Color.BLACK);
        }
        if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN) & (row < firstEditableRow))
        { // headers cells
            if (row == 0)
            {// FIRST ROW
                for (int i = 0; i < acummulativeColumns.length; i++)
                {
                    if (i == 0)
                    {
                        if (column <= acummulativeColumns[i])
                        {
                            setBackground (new Color (220, 220, 220));
                            setForeground (new Color (128, 0, 64));
                            break;
                        }
                    }
                    else if (acummulativeColumns[i - 1] < column
                             && column <= acummulativeColumns[i])
                    {
                        if (i % 2 == 0)
                        {
                            setBackground (new Color (220, 220, 220));
                            setForeground (new Color (128, 0, 64));
                        }
                        else
                        {
                            setBackground (new Color (220, 220, 220));
                            setForeground (Color.BLUE.darker ());
                        }
                        break;
                    }
                }
            }
            if (row == 1)
            {// SECOND ROW
                // setBackground( new Color(220,220,220));
                if (column % 2 == 0)
                {
                    setBackground (new Color (220, 220, 220));
                    setForeground (new Color (128, 0, 64));
                }
                else
                {
                    setBackground (new Color (220, 220, 220));
                    setForeground (Color.BLUE.darker ());
                }
            }
        }
        if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN) && firstEditableRow >= 0
            && (row >= firstEditableRow))
        {
            setBackground (Color.WHITE);
            setForeground (Color.BLACK);
            if (hasFocus)
            {
                if (table.isCellEditable (row, column))
                {
                    setForeground (Color.BLUE);
                    setBackground (Color.YELLOW);
                }
            }
        }
    }
}
