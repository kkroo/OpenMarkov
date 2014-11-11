/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import javax.swing.JScrollPane;

import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.model.network.ProbNode;

/**
 * This class extends from <code>TablePotentialPanel</code>, is a panel used by
 * <code>ICIOptionListenerAssistant</code> to show the complete parameters
 * table. It is similar to <code>TablePotentialPanel</code> with the peculiarity
 * that can not be edited cells
 * @author myebra
 */
@SuppressWarnings("serial")
public class CPTablePanel extends TablePotentialPanel
{
    /**
     * Indicates if the data of the table is modifiable.
     */
    private boolean modifiable;

    public CPTablePanel (ProbNode probNode)
    {
        super (probNode);
        modifiable = false;
    }

    /**
     * This method initializes valuesTable and defines that first two columns
     * are not selectable
     * @return a new values table.
     */
    @Override
    public ValuesTable getValuesTable ()
    {
        if (valuesTable == null)
        {
            valuesTable = new ValuesTable (probNode, getTableModel (), modifiable);
            valuesTable.setName ("PotentialsTablePanel.valuesTable");
        }
        return valuesTable;
    }

    /**
     * This method initializes valuesTableScrollPane.
     * @return a new values table scroll pane.
     */
    @Override
    public JScrollPane getValuesTableScrollPane ()
    {
        if (valuesTableScrollPane == null)
        {
            valuesTableScrollPane = new JScrollPane ();
            valuesTableScrollPane.setName ("CPTablePanel.valuesTableScrollPane");
            valuesTableScrollPane.setViewportView (getValuesTable ());
        }
        return valuesTableScrollPane;
    }

    @Override
    public void close ()
    {
        getValuesTable ().close ();
    }
}
