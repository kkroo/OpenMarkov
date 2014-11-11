
package org.openmarkov.core.gui.component;

import java.util.ListIterator;

import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.ICITablePotentialValueEdit;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;

@SuppressWarnings("serial")
public class ICIValuesTable extends ValuesTable
    implements
        PNUndoableEditListener
{
    /**
     * Define the last column of the table that was modified
     */
    private int lastCol = -1;

    public ICIValuesTable (ProbNode probNode, ValuesTableModel tableModel, final boolean modifiable)
    {
        super (probNode, tableModel, modifiable);
    }

    /**
     * check the value to modify in the table and sets
     */
    public void setValueAt (Object newValue, int row, int col)
    {
        Object oldValue = getValueAt (row, col);
        // TODO Verificar si la ubicación del siguiente código es
        // adecuada
        if (((Double) newValue).isNaN ())
        {
            newValue = oldValue;
            JOptionPane.showMessageDialog (this.getParent (), "Introduced value is not a number");
        }
        else if (((Double) newValue) < 0)
        {
            newValue = oldValue;
            JOptionPane.showMessageDialog (this.getParent (),
                                           "Introduced value can not be negative");
        }
        if (!oldValue.equals (newValue))
        {
            if (nodeType == NodeType.CHANCE || nodeType == NodeType.DECISION)
            {
                if (lastCol != col)
                {
                    priorityList.clear ();
                    lastCol = col;
                }
                ICITablePotentialValueEdit nodePotentialEdit = new ICITablePotentialValueEdit (
                                                                                               probNode,
                                                                                               (Double) newValue,
                                                                                               row,
                                                                                               col,
                                                                                               priorityList);
                try
                {
                    probNode.getProbNet ().doEdit (nodePotentialEdit);
                }
                catch (ConstraintViolationException | CanNotDoEditException
                        | NonProjectablePotentialException | WrongCriterionException
                        | DoEditException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace ();
                    JOptionPane.showMessageDialog (this,
                                                   StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                                   StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                                   JOptionPane.ERROR_MESSAGE);
                }
            }
        } // else it is not required to update values
    }

    /**
     * set the number of columns in the table for canonical models adding one
     * column per parent state and adding one more for the id column (hidden)
     * @param parents - parents of the variable
     * @return the number of columns in the table
     */
    public static int howManyCanonicalColumns (ProbNode properties)
    {
        int numColumns = 0;
        if (properties.getNode ().getParents () != null)
        {
            int aux = 1;// first column for child states
            for (Node parent : properties.getNode ().getParents ())
            {
                State[] parentStates = ((ProbNode) parent.getObject ()).getVariable ().getStates ();
                aux += parentStates.length;
            }
            numColumns = aux + 1; // last column for the leak potential
        }
        else
        {
            numColumns = 1;
        }
        // numColumns = FIRST_EDITABLE_COLUMN + numColumns;
        return numColumns;
    }

    public static int toPositionOnJtable (int index, int col, int numOfStates, int numOfParents)
    {
        return numOfParents - 1 + numOfStates + (numOfStates * (col - 1)) - index;
    }

    public void undoableEditHappened (UndoableEditEvent arg0)
    {
        int priorityListPosition = 0;
        UndoableEdit edit = arg0.getEdit ();
        if (edit instanceof ICITablePotentialValueEdit)
        {
            ICITablePotentialValueEdit iciEdit = (ICITablePotentialValueEdit) arg0.getEdit ();
            priorityList = iciEdit.getPriorityList ();
            if (!iciEdit.getLeakyFlag ())
            {// noisy parameters
                double[] noisyPotential = iciEdit.getNewNoisyValues ();
                ListIterator<Integer> listIterator = priorityList.listIterator ();
                while (listIterator.hasNext () == true)
                {
                    priorityListPosition = (Integer) listIterator.next ();
                    super.getModel ().setValueAt (noisyPotential[priorityListPosition],
                                                  iciEdit.getRowPosition (priorityListPosition),
                                                  iciEdit.getColumnPosition ());
                }
            }
            else
            {// leaky parametes
                double[] leakyPotential = iciEdit.getNewLeakyValues ();
                ListIterator<Integer> listIterator = priorityList.listIterator ();
                while (listIterator.hasNext () == true)
                {
                    priorityListPosition = (Integer) listIterator.next ();
                    super.getModel ().setValueAt (leakyPotential[priorityListPosition],
                                                  iciEdit.getRowPosition (priorityListPosition),
                                                  iciEdit.getColumnPosition ());
                }
            }
        }
    }

    public void undoableEditWillHappen (UndoableEditEvent event)
        throws ConstraintViolationException,
        CanNotDoEditException
    {
        // TODO Auto-generated method stub
    }

    public void undoEditHappened (UndoableEditEvent event)
    {
        int priorityListPosition = 0;
        UndoableEdit edit = event.getEdit ();
        if (edit instanceof ICITablePotentialValueEdit)
        {
            ICITablePotentialValueEdit iciEdit = (ICITablePotentialValueEdit) edit;
            priorityList = iciEdit.getPriorityList ();
            if (!iciEdit.getLeakyFlag ())
            {// noisy parameters
                double[] lastNoisyPotential = iciEdit.getLastNoisyValues ();
                ListIterator<Integer> listIterator = priorityList.listIterator ();
                while (listIterator.hasNext () == true)
                {
                    priorityListPosition = (Integer) listIterator.next ();
                    super.getModel ().setValueAt (lastNoisyPotential[priorityListPosition],
                                                  iciEdit.getRowPosition (priorityListPosition),
                                                  iciEdit.getColumnPosition ());
                }
            }
            else
            {// leaky parametes
                double[] lastLeakyPotential = iciEdit.getLastNoisyValues ();
                ListIterator<Integer> listIterator = priorityList.listIterator ();
                while (listIterator.hasNext () == true)
                {
                    priorityListPosition = (Integer) listIterator.next ();
                    super.getModel ().setValueAt (lastLeakyPotential[priorityListPosition],
                                                  iciEdit.getRowPosition (priorityListPosition),
                                                  iciEdit.getColumnPosition ());
                }
            }
            super.getModel ().setValueAt (iciEdit.getNewValue (), iciEdit.getRowPosition (),
                                          iciEdit.getColumnPosition ());
        }
    }
}
