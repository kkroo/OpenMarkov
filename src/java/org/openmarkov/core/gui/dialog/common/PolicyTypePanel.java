/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.openmarkov.core.action.RemovePolicyEdit;
import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.gui.dialog.node.PotentialEditDialog;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

@SuppressWarnings("serial")
public class PolicyTypePanel extends JPanel
    implements
        ItemListener
{
    private ButtonGroup         buttonGroupRelationType;
    /**
     * Option to set as probabilistic the potential
     */
    private JRadioButton        jRadioButtonProbabilisticType;
    /**
     * Option to set as deterministic the potential
     */
    private JRadioButton        jRadioButtonDeterministicType;
    /**
     * Option to set as Optimal the potential
     */
    private JRadioButton        jRadioButtonOptimal;
    /**
     * The node edited
     */
    private ProbNode            probNode;
    /**
     * The previous policy
     */
    private PolicyType          previousPolicy;
    /**
     * Parent dialog
     */
    private PotentialEditDialog parent;
    /**
     * String database
     */
    protected StringDatabase    stringDatabase = StringDatabase.getUniqueInstance ();

    /**
     * Constructor for PolicyTypePanel.
     * @param parent
     * @param probNode
     */
    // TODO Reduce two only two types: optimal and imposed
    public PolicyTypePanel (PotentialEditDialog parent, ProbNode probNode)
    {
        this.parent = parent;
        this.probNode = probNode;
        setBorder (new LineBorder (UIManager.getColor ("List.dropLineColor"), 1, false));
        setLayout (new FlowLayout ());
        // jPanelRelationTableType.setSize( 294, 29 );
        // jPanelRelationTableType.setName( "jPanelRadioRelationTableType" );
        add (getJRadioButtonOptimalType ());
        add (getJRadioButtonProbabilisticType ());
        add (getJRadioButtonDeterministicType ());
        getButtonGroupRelationType ();
    }

    /**
     * @return the button for the Optimal model (when decision node)
     */
    protected JRadioButton getJRadioButtonOptimalType ()
    {
        if (jRadioButtonOptimal == null)
        {
            jRadioButtonOptimal = new JRadioButton ();
            jRadioButtonOptimal.setMargin (new Insets (0, 0, 0, 0));
            jRadioButtonOptimal.setName ("jRadioButtonOptimal");
            jRadioButtonOptimal.setText ("New JRadioBut");
            jRadioButtonOptimal.setText (stringDatabase.getString ("NodeProbsValuesTablePanel."
                                                                   + "jRadioButtonOptimal.Text"));
            jRadioButtonOptimal.setSelected (true);
            jRadioButtonOptimal.setEnabled (false);
            jRadioButtonOptimal.addItemListener (this);
        }
        return jRadioButtonOptimal;
    }

    /**
     * @return the button for Probabilistic model
     */
    protected JRadioButton getJRadioButtonProbabilisticType ()
    {
        if (jRadioButtonProbabilisticType == null)
        {
            jRadioButtonProbabilisticType = new JRadioButton ();
            jRadioButtonProbabilisticType.setMargin (new Insets (0, 0, 0, 0));
            jRadioButtonProbabilisticType.setName ("jRadioButtonProbabilisticType");
            jRadioButtonProbabilisticType.setText ("New JRadioBut");
            jRadioButtonProbabilisticType.setText (stringDatabase.getString ("NodeProbsValuesTablePanel."
                                                                             + "jRadioButtonProbabilisticType.Text"));
            jRadioButtonProbabilisticType.addItemListener (this);
            jRadioButtonProbabilisticType.setEnabled (false);
        }
        return jRadioButtonProbabilisticType;
    }

    /**
     * @return the button for the Deterministic Model
     */
    protected JRadioButton getJRadioButtonDeterministicType ()
    {
        if (jRadioButtonDeterministicType == null)
        {
            jRadioButtonDeterministicType = new JRadioButton ();
            jRadioButtonDeterministicType.setMargin (new Insets (0, 0, 0, 0));
            jRadioButtonDeterministicType.setName ("jRadioButtonDeterministicType");
            jRadioButtonDeterministicType.setText ("New JRadioBut");
            jRadioButtonDeterministicType.setText (stringDatabase.getString ("NodeProbsValuesTablePanel."
                                                                             + "jRadioButtonDeterministicType.Text"));
            jRadioButtonDeterministicType.addItemListener (this);
            jRadioButtonDeterministicType.setEnabled (false);
        }
        return jRadioButtonDeterministicType;
    }

    /**
     * initialize the button group Probabilistic Or Deterministic Or Optimal
     */
    private void getButtonGroupRelationType ()
    {
        buttonGroupRelationType = new ButtonGroup ();
        buttonGroupRelationType.add (getJRadioButtonProbabilisticType ());
        buttonGroupRelationType.add (getJRadioButtonDeterministicType ());
        buttonGroupRelationType.add (getJRadioButtonOptimalType ());
    }

    public void itemStateChanged (ItemEvent e)
    {
        if (e.getItem ().equals (getJRadioButtonProbabilisticType ()))
        {
            itemStateChangedProbabilisticType (e);
        }
        if (e.getItem ().equals (getJRadioButtonDeterministicType ()))
        {
            itemStateChangedDeterministicType (e);
        }
        if (e.getItem ().equals (getJRadioButtonOptimalType ()))
        {
            itemStateChangedOptimalType (e);
        }
    }

    private void itemStateChangedDeterministicType (ItemEvent e)
    {
        /*
         * if (e.getStateChange() == ItemEvent.DESELECTED){ //optionDeselected =
         * comboBox.getSelectedIndex(); previousPolicy = Policy.PROBABILISTIC;
         * }else if (e.getStateChange() == ItemEvent.SELECTED ){ if (
         * getPotentialPanel() instanceof TablePotentialPanel){
         * ((TablePotentialPanel)getPotentialPanel()).
         * hideElementsWhenIsDecisionNodeOrUniformPotential(); } }
         */
    }

    private void itemStateChangedOptimalType (ItemEvent e)
    {
        if (e.getStateChange () == ItemEvent.DESELECTED)
        {
            // optionDeselected = comboBox.getSelectedIndex();
            previousPolicy = PolicyType.OPTIMAL;
        }
        else if (e.getStateChange () == ItemEvent.SELECTED)
        {
            parent.setEnabledPotentialTypeCombobox (true);
            if (previousPolicy == PolicyType.PROBABILISTIC)
            {
                RemovePolicyEdit removePolicyEdit = null;
                removePolicyEdit = new RemovePolicyEdit (probNode);
                try
                {
                    probNode.getProbNet ().doEdit (removePolicyEdit);
                }
                catch (ConstraintViolationException e1)
                {
                    JOptionPane.showMessageDialog (this,
                                                   stringDatabase.getString (e1.getMessage ()),
                                                   stringDatabase.getString ("ConstraintViolationException"),
                                                   JOptionPane.ERROR_MESSAGE);
                    // getJComboBoxRelationType().requestFocus();
                    parent.revertPotentialTypeChange ();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace ();
                }
            }
        }
    }

    private void itemStateChangedProbabilisticType (ItemEvent e)
    {
        if (e.getStateChange () == ItemEvent.DESELECTED)
        {
            // optionDeselected = comboBox.getSelectedIndex();
            previousPolicy = PolicyType.PROBABILISTIC;
        }
        else if (e.getStateChange () == ItemEvent.SELECTED)
        {
            parent.setEnabledPotentialTypeCombobox (true);
            if (previousPolicy == PolicyType.OPTIMAL)
            {
                SetPotentialEdit setPotentialEdit = null;
                setPotentialEdit = new SetPotentialEdit (
                                                         probNode,
                                                         TablePotential.class.getAnnotation (RelationPotentialType.class).name ());
                try
                {
                    probNode.getProbNet ().doEdit (setPotentialEdit);
                }
                catch (ConstraintViolationException e1)
                {
                    JOptionPane.showMessageDialog (this,
                                                   stringDatabase.getString (e1.getMessage ()),
                                                   stringDatabase.getString ("ConstraintViolationException"),
                                                   JOptionPane.ERROR_MESSAGE);
                    // getJComboBoxRelationType().requestFocus();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace ();
                }
            }
        }
    }

    /**
     * Shows and activated the options related to decision policy
     * @param show
     */
    public void setEnabledDecisionOptions (boolean show)
    {
        getJRadioButtonOptimalType ().removeItemListener (this);
        getJRadioButtonDeterministicType ().removeItemListener (this);
        getJRadioButtonProbabilisticType ().removeItemListener (this);
        if (show)
        {
            /*
             * getJRadioButtonOptimalType().setSelected(false);
             * getJRadioButtonDeterministicType().setSelected(false);
             * getJRadioButtonProbabilisticType().setSelected(false);
             */
            switch (probNode.getPolicyType ())
            {
                case OPTIMAL :
                    getJRadioButtonOptimalType ().setSelected (true);
                    break;
                case DETERMINISTIC :
                    getJRadioButtonDeterministicType ().setSelected (true);
                    break;
                case PROBABILISTIC :
                    getJRadioButtonProbabilisticType ().setSelected (true);
                    break;
            }
            getJRadioButtonOptimalType ().addItemListener (this);
            getJRadioButtonDeterministicType ().addItemListener (this);
            getJRadioButtonProbabilisticType ().addItemListener (this);
            getJRadioButtonOptimalType ().setEnabled (true);
            getJRadioButtonDeterministicType ().setEnabled (false);
            getJRadioButtonProbabilisticType ().setEnabled (true);
        }
        else
        {
            getJRadioButtonOptimalType ().setEnabled (false);
            getJRadioButtonDeterministicType ().setEnabled (false);
            getJRadioButtonProbabilisticType ().setEnabled (false);
        }
    }
}
