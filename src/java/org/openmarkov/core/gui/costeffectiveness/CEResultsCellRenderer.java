/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.costeffectiveness;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class CEResultsCellRenderer extends DefaultTableCellRenderer
{
    private DecimalFormat formatter = new DecimalFormat ("0.###", new DecimalFormatSymbols(Locale.US));

    /**
     * Constructor for CEResultsCellRenderer.
     */
    public CEResultsCellRenderer ()
    {
        super ();
        setHorizontalAlignment (SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent (JTable table,
                                                    Object value,
                                                    boolean isSelected,
                                                    boolean hasFocus,
                                                    int row,
                                                    int column)
    {
        if (value instanceof Double)
        {
            if((Double)value <= 100.0 )
            {
                value = formatter.format ((Double) value);
            }else 
            {
                value = Math.round((Double)value);                
            }
        }
        if(column == 0)
        {
            setBackground (new Color (220, 220, 220));
        }else
        {
            setBackground (Color.WHITE);
        }
        setForeground (Color.BLACK);        
        return super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row,
                                                    column);
    }
}