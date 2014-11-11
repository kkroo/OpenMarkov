
package org.openmarkov.core.gui.dialog.node;

import java.util.ArrayList;

import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.KeyTablePanel;
import org.openmarkov.core.gui.dialog.network.AdvancedPropertiesTableModel;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;

@SuppressWarnings("serial")
public class ReorderVariablesPanel extends KeyTablePanel
    implements
        TableModelListener,
        PNUndoableEditListener
{
    private ProbNode                     probNode;
    private String                       keyPrefix;
    private AdvancedPropertiesTableModel netWorkAgentstableModel;
    private Object                       dataTable[][];
    private ArrayList<Variable>          newVariables;
    private ArrayList<PNEdit>            edits = new ArrayList<PNEdit> ();

    public ReorderVariablesPanel (String[] newColumns, ProbNode probNode)
    {
        this (newColumns, new Object[0][0], "a");
        this.probNode = probNode;
    }

    public ReorderVariablesPanel (String[] newColumns, Object[][] noKeyData, String newKeyPrefix)
    {
        super (newColumns, new Object[0][0], true, true);
        keyPrefix = newKeyPrefix;
        initialize ();
        getAddValueButton ().setVisible (false);
        getRemoveValueButton ().setVisible (false);
        setData (noKeyData);
        defineTableLookAndFeel (); // define specific listeners
        // defineTableSpecificListeners();
        // getTableModel().addTableModelListener(this);
    }

    /**
     * Sets a new table model with new data.
     * @param newData new data for the table without the key column.
     */
    @Override
    public void setData (Object[][] newData)
    {
        if (newData != null)
        {
            // dataTable = newData;
            data = fillDataKeys (newData);
            // tableModel = new DefaultTableModel(data, columns);
            netWorkAgentstableModel = new AdvancedPropertiesTableModel (data, columns);
            // valuesTable.setModel(tableModel);
            valuesTable.setModifiable (false);
            valuesTable.setModel (netWorkAgentstableModel);
            valuesTable.getModel ().addTableModelListener (this);
            this.defineTableLookAndFeel ();
        }
    }

    protected void defineTableLookAndFeel ()
    {
        // center the data in all columns
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer ();
        tcr.setHorizontalAlignment (SwingConstants.LEFT);
        DefaultTableCellRenderer statesRender = new DefaultTableCellRenderer ();
        statesRender.setHorizontalAlignment (SwingConstants.LEFT);
        int maxColumn = valuesTable.getColumnModel ().getColumnCount ();
        for (int i = 1; i < maxColumn; i++)
        {
            TableColumn aColumn = valuesTable.getColumnModel ().getColumn (i);
            aColumn.setCellRenderer (tcr);
            valuesTable.getTableHeader ().getColumnModel ().getColumn (i).setCellRenderer (tcr);
        }
    }

    /**
     * This method takes a data object and creates a new column that content a
     * row key. This key begins with the key prefix following a number that
     * starts at 0.
     * @param oldData data to add a key column.
     * @return a data object with one more column that contains the keys.
     */
    private Object[][] fillDataKeys (Object[][] oldData)
    {
        Object[][] newData = null;
        int i1 = 0; // aux int
        int i2 = 0; // aux int
        int l1 = 0; // num of rows
        int l2 = 0; // num of columns
        l1 = oldData.length;
        if (l1 > 0)
        {
            l2 = oldData[0].length + 1;
            newData = new Object[l1][l2];
            for (i1 = 0; i1 < l1; i1++)
            {
                newData[i1][0] = getKeyString (i1);
                for (i2 = 1; i2 < l2; i2++)
                {
                    newData[i1][i2] = oldData[i1][i2 - 1];
                }
            }
            return newData;
        }
        return new Object[0][0];
    }

    /**
     * Returns a key represented by an index.
     * @param index index of the key which will be returned
     * @return the string that content the key.
     */
    private String getKeyString (int index)
    {
        return keyPrefix + index;
    }

    public void setDataTable (Object[][] dataTable)
    {
        this.dataTable = dataTable;
    }

    @Override
    protected void actionPerformedUpValue ()
    {
        int selectedRow = valuesTable.getSelectedRow ();
        Object swap = null;
        swap = dataTable[selectedRow][0];
        dataTable[selectedRow][0] = dataTable[selectedRow - 1][0];
        dataTable[selectedRow - 1][0] = swap;
        setData (dataTable);
        valuesTable.getSelectionModel ().setSelectionInterval (selectedRow - 1, selectedRow - 1);
        for (int i = 0; i < valuesTable.getRowCount (); i++)
        {
            dataTable[i][0] = valuesTable.getValueAt (i, 1);
        }
        ArrayList<Variable> newVariablesDown = new ArrayList<Variable> ();
        for (int i = 0; i < dataTable.length; i++)
        {
            for (int j = 0; j < probNode.getPotentials ().get (0).getVariables ().size (); j++)
            {
                if ((String) dataTable[i][0] == probNode.getPotentials ().get (0).getVariables ().get (j).getName ())
                {
                    newVariablesDown.add (probNode.getPotentials ().get (0).getVariables ().get (j));
                }
            }
        }
        newVariables = new ArrayList<> ();
        if (probNode.getPotentials ().get (0).getPotentialRole () == PotentialRole.CONDITIONAL_PROBABILITY)
        {
            newVariables.add (probNode.getPotentials ().get (0).getVariables ().get (0));
            newVariables.addAll (newVariablesDown);
        }
        else if (probNode.getPotentials ().get (0).getPotentialRole () == PotentialRole.UTILITY)
        {
            newVariables.addAll (newVariablesDown);
        }
    }

    @Override
    protected void actionPerformedDownValue ()
    {
        int selectedRow = valuesTable.getSelectedRow ();
        Object swap = null;
        swap = dataTable[selectedRow][0];
        dataTable[selectedRow][0] = dataTable[selectedRow + 1][0];
        dataTable[selectedRow + 1][0] = swap;
        setData (dataTable);
        valuesTable.getSelectionModel ().setSelectionInterval (selectedRow + 1, selectedRow + 1);
        for (int i = 0; i < valuesTable.getRowCount (); i++)
        {
            dataTable[i][0] = valuesTable.getValueAt (i, 1);
        }
        ArrayList<Variable> newVariablesDown = new ArrayList<Variable> ();
        for (int i = 0; i < dataTable.length; i++)
        {
            for (int j = 0; j < probNode.getPotentials ().get (0).getVariables ().size (); j++)
            {
                if ((String) dataTable[i][0] == probNode.getPotentials ().get (0).getVariables ().get (j).getName ())
                {
                    newVariablesDown.add (probNode.getPotentials ().get (0).getVariables ().get (j));
                }
            }
        }
        if (probNode.getPotentials ().get (0).getPotentialRole () == PotentialRole.CONDITIONAL_PROBABILITY)
        {
            newVariables.add (probNode.getPotentials ().get (0).getVariables ().get (0));
            newVariables.addAll (newVariablesDown);
        }
        else if (probNode.getPotentials ().get (0).getPotentialRole () == PotentialRole.UTILITY)
        {
            newVariables.addAll (newVariablesDown);
        }
    }

    public ArrayList<Variable> getVariables ()
    {
        return this.newVariables;
    }

    /**
     * @return
     */
    public ArrayList<PNEdit> getEdits ()
    {
        return edits;
    }

    @Override
    public void undoableEditHappened (UndoableEditEvent arg0)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void undoableEditWillHappen (UndoableEditEvent event)
        throws ConstraintViolationException,
        CanNotDoEditException,
        NonProjectablePotentialException,
        WrongCriterionException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void undoEditHappened (UndoableEditEvent event)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void tableChanged (TableModelEvent arg0)
    {
        // TODO Auto-generated method stub
    }
}
