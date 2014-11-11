/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
/**
 * OpenMarkov - NodeDiscretizeValuesTablePanelListener.java
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import org.openmarkov.core.action.PrecisionEdit;
import org.openmarkov.core.action.UnitEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.PartitionedIntervalEdit;
import org.openmarkov.core.gui.component.DiscretizeTablePanel;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.VariableType;

/**
 * Auxiliary class to handle events for the primary class
 * NodeDiscretizeValuesTablePanel
 * @author jlgozalo
 * @version 1.0 5 Feb 2010
 */
public class NodeDomainValuesTablePanelListener
    implements
        ActionListener,
        ItemListener,
        FocusListener,
        PropertyChangeListener
{
    /**
     * the panel to handle the events
     */
    private NodeDomainValuesTablePanel panel;
    private int                        previousMonotony = -1;
    private static int                 DOWN             = 0;
    private static int                 UP               = 1;

    /**
     * Constructor
     * @param panel - the panel to handle the events
     */
    public NodeDomainValuesTablePanelListener (NodeDomainValuesTablePanel panel)
    {
        this.panel = panel;
    }

    /**
     * @return the panel
     */
    private NodeDomainValuesTablePanel getPanel ()
    {
        return panel;
    }

    public void actionPerformed (ActionEvent event)
    {
        // TODO do something
    }

    // button initially selected down
    public void itemStateChanged (ItemEvent e)
    {
        getPanel ().getDiscretizedStatesPanel ().getNegativeInfinityButton ().setVisible (false);
        getPanel ().getDiscretizedStatesPanel ().getPositiveInfinityButton ().setVisible (false);
        getPanel ().getDiscretizedStatesPanel ().getNegativeInfinityButton ().setEnabled (false);
        getPanel ().getDiscretizedStatesPanel ().getPositiveInfinityButton ().setEnabled (false);
        if (e.getItem ().equals (getPanel ().getJRadioButtonIncreasing ()))
        {
            itemStateChangedUp (e);
        }
        if (e.getItem ().equals (getPanel ().getJRadioButtonDecreasing ()))
        {
            itemStateChangedDown (e);
        }
    }

    private void itemStateChangedUp (ItemEvent e)
    {
        if (e.getStateChange () == ItemEvent.DESELECTED)
        {
            previousMonotony = UP; // deselected up
            getPanel ().getDiscretizedStatesPanel ().setUpMonotony (false);
        }
        else if (e.getStateChange () == ItemEvent.SELECTED)
        {
            if (previousMonotony == UP)
            { // UP --> UP
              // do nothing
                getPanel ().getDiscretizedStatesPanel ().setUpMonotony (true);
            }
            else if (previousMonotony == DOWN)
            { // DOWN --> UP
                DiscretizeTablePanel panel = getPanel ().getDiscretizedStatesPanel ();
                panel.setUpMonotony (true);
                Object[][] data = panel.getData ();
                Object[][] intermediateRows = new Object[data.length][data[0].length - 2];
                for (int i = 0; i < data.length; i++)
                {// for each row
                    for (int j = 2; j < data[0].length; j++)
                    {
                        intermediateRows[i][j - 2] = data[i][j];
                    }
                }
                for (int i = 0; i < intermediateRows.length; i++)
                {
                    for (int j = 0; j < intermediateRows[0].length; j++)
                    {
                        data[i][j + 2] = intermediateRows[intermediateRows.length - 1 - i][j];
                    }
                }
                Object[][] newData = new Object[data.length][data[0].length - 1];
                for (int i = 0; i < data.length; i++)
                {
                    for (int j = 1; j < data[0].length; j++)
                    {
                        newData[i][j - 1] = data[i][j];
                    }
                }
                panel.setData (newData); // set data fill the first key column
            }
        }
    }

    private void itemStateChangedDown (ItemEvent e)
    {
        if (e.getStateChange () == ItemEvent.DESELECTED)
        {
            previousMonotony = DOWN;// deselected down
            getPanel ().getDiscretizedStatesPanel ().setUpMonotony (true);
        }
        else if (e.getStateChange () == ItemEvent.SELECTED)
        {
            if (previousMonotony == UP)
            { // UP --> DOWN
                DiscretizeTablePanel panel = getPanel ().getDiscretizedStatesPanel ();
                panel.setUpMonotony (false);
                Object[][] data = panel.getData ();
                Object[][] intermediateRows = new Object[data.length][data[0].length - 2];
                for (int i = 0; i < data.length; i++)
                {// for each row
                    for (int j = 2; j < data[0].length; j++)
                    {
                        intermediateRows[i][j - 2] = data[i][j];
                    }
                }
                for (int i = 0; i < intermediateRows.length; i++)
                {
                    for (int j = 0; j < intermediateRows[0].length; j++)
                    {
                        data[i][j + 2] = intermediateRows[intermediateRows.length - 1 - i][j];
                    }
                }
                Object[][] newData = new Object[data.length][data[0].length - 1];
                for (int i = 0; i < data.length; i++)
                {
                    for (int j = 1; j < data[0].length; j++)
                    {
                        newData[i][j - 1] = data[i][j];
                    }
                }
                panel.setData (newData); // set data fill the first key column
            }
            else if (previousMonotony == DOWN)
            { // DOWN --> DOWN
              // do nothing
                getPanel ().getDiscretizedStatesPanel ().setUpMonotony (false);
            }
        }
    }

    /**
     * Invoked when an item of the type of states of the node has been selected.
     * @param e event information.
     */
    protected void subItemStateChanged (ItemEvent e)
    {
        if (e.getItemSelectable ().equals (getPanel ().getJComboBoxStatesValues ()))
        {
            if (e.getStateChange () == ItemEvent.SELECTED)
            {
                // getPanel().getNodeDiscretizedValuesTablePanel().setNewDataInTable(
                // getPanel().getJComboBoxStatesValues().getSelectedIndex() );
            }
        }
    }

    public void focusGained (FocusEvent e)
    {
        if (e.getSource ().equals (getPanel ().getJFormattedTextFieldPrecision ()))
        {
            System.out.println ("precision focus gained");
            getPanel ().getJFormattedTextFieldPrecision ().selectAll ();
        }
        else if (e.getSource ().equals (getPanel ().getJTextFieldUnit ()))
        {
            getPanel ().getJTextFieldUnit ().selectAll ();
        }
    }

    public void focusLost (FocusEvent evt)
    {
        if (evt.getSource ().equals (getPanel ().getJFormattedTextFieldPrecision ()))
        {
            PrecisionEdit precisionEdit = new PrecisionEdit (
                                                             getPanel ().getProbNode (),
                                                             ((Double) getPanel ().getJFormattedTextFieldPrecision ().getValue ()).doubleValue ());
            try
            {
                getPanel ().getProbNode ().getProbNet ().doEdit (precisionEdit);
            }
            catch (ConstraintViolationException | CanNotDoEditException
                    | NonProjectablePotentialException | WrongCriterionException | DoEditException e)
            {
                e.printStackTrace ();
                JOptionPane.showMessageDialog (null,
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
            System.out.println ("precision set to "
                                + ((Double) getPanel ().getJFormattedTextFieldPrecision ().getValue ()).toString ());
        }
        else if (evt.getSource ().equals (getPanel ().getJTextFieldUnit ()))
        {
            UnitEdit unitEdit = new UnitEdit (getPanel ().getProbNode (),
                                              getPanel ().getJTextFieldUnit ().getText ());
            try
            {
                getPanel ().getProbNode ().getProbNet ().doEdit (unitEdit);
                getPanel ().getJTextFieldUnit ().setText (getPanel ().getJTextFieldUnit ().getText ());
            }
            catch (DoEditException | ConstraintViolationException | CanNotDoEditException
                    | NonProjectablePotentialException | WrongCriterionException e)
            {
                e.printStackTrace ();
            }
        }
    }

    public void propertyChange (PropertyChangeEvent evt)
    {
        if (evt.getSource ().equals (getPanel ().getJFormattedTextFieldPrecision ()))
        {
            PrecisionEdit precisionEdit = new PrecisionEdit (
                                                             panel.getProbNode (),
                                                             (Double) getPanel ().getJFormattedTextFieldPrecision ().getValue ());
            try
            {
                getPanel ().getProbNode ().getProbNet ().doEdit (precisionEdit);
            }
            catch (ConstraintViolationException | CanNotDoEditException
                    | NonProjectablePotentialException | WrongCriterionException | DoEditException e)
            {
                e.printStackTrace ();
                JOptionPane.showMessageDialog (null,
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
            NumberFormat nf = NumberFormat.getNumberInstance ();
            nf.setGroupingUsed (false); // don't group by threes
            if (getPanel ().getProbNode ().getVariable ().getVariableType () == VariableType.DISCRETIZED
                || getPanel ().getProbNode ().getVariable ().getVariableType () == VariableType.NUMERIC)
            {
                double precision = (Double) getPanel ().getJFormattedTextFieldPrecision ().getValue ();
                double[] limits = getPanel ().getProbNode ().getVariable ().getPartitionedInterval ().getLimits ();
                boolean[] belongs = getPanel ().getProbNode ().getVariable ().getPartitionedInterval ().getBelongsToLeftSide ();
                for (int i = 0; i < limits.length; i++)
                {
                    if (limits[i] != Double.POSITIVE_INFINITY
                        && limits[i] != Double.NEGATIVE_INFINITY)
                    {
                        double newLimit = Util.roundWithPrecision (limits[i],
                                                                          Double.toString (precision));
                        if (limits[i] != newLimit)
                        {
                            limits[i] = newLimit;
                            int j = i;
                            while (j + 1 <= limits.length - 1 && limits[j] >= limits[j + 1])
                            {
                                if (belongs[j] == false && belongs[j + 1] == true)
                                {
                                    limits[j + 1] = limits[j];
                                }
                                else
                                {
                                    if (j + 1 == limits.length - 1)
                                    {
                                        limits[j + 1] = Double.POSITIVE_INFINITY;
                                        break;
                                    }
                                    else limits[j + 1] = limits[j] + precision;
                                }
                                j++;
                            }
                            // previous limits
                            int k = i;
                            while (k - 1 >= 0 && limits[k] <= limits[k - 1])
                            {
                                if (belongs[k] == true && belongs[k - 1] == false)
                                {
                                    limits[k - 1] = limits[k];
                                }
                                else
                                {
                                    if (k - 1 == 0)
                                    {
                                        limits[k - 1] = Double.NEGATIVE_INFINITY;
                                        break;
                                    }
                                    else limits[k - 1] = limits[k] - precision;
                                }
                                k--;
                            }
                        }
                        else
                        {
                            limits[i] = newLimit;
                        }
                    }
                }
                for (int m = 0; m < limits.length; m++)
                {
                    if (limits[m] != Double.POSITIVE_INFINITY
                        && limits[m] != Double.NEGATIVE_INFINITY)
                    {
                        limits[m] = Util.roundWithPrecision (limits[m],
                                                                    Double.toString (precision));
                    }
                }
                PartitionedInterval newPartitionedInterval = new PartitionedInterval (limits,
                                                                                      belongs);
                PartitionedIntervalEdit partitionedIntervalEdit = new PartitionedIntervalEdit (
                                                                                               getPanel ().getProbNode (),
                                                                                               newPartitionedInterval);
                try
                {
                    getPanel ().getProbNode ().getProbNet ().doEdit (partitionedIntervalEdit);
                }
                catch (DoEditException | ConstraintViolationException | CanNotDoEditException
                        | NonProjectablePotentialException | WrongCriterionException e)
                {
                    e.printStackTrace ();
                }
                PartitionedInterval newPartitionInterval = getPanel ().getProbNode ().getVariable ().getPartitionedInterval ();
                State[] states = getPanel ().getProbNode ().getVariable ().getStates ();
                getPanel ().getDiscretizedStatesPanel ().setDataFromPartitionedInterval (newPartitionInterval,
                                                                                                  states);
            }
        }
        else if (evt.getSource ().equals (getPanel ().getJTextFieldUnit ()))
        {
            UnitEdit unitEdit = new UnitEdit (getPanel ().getProbNode (),
                                              getPanel ().getJTextFieldUnit ().getText ());
            try
            {
                getPanel ().getProbNode ().getProbNet ().doEdit (unitEdit);
            }
            catch (DoEditException | ConstraintViolationException | CanNotDoEditException
                    | NonProjectablePotentialException | WrongCriterionException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            }
        }
    }
}
