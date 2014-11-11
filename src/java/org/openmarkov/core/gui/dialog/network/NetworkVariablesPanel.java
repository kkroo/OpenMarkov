/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.openmarkov.core.action.NetworkDefaultStatesEdit;
import org.openmarkov.core.action.VariableTypeConstraintEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.constraint.OnlyContinuousVariables;
import org.openmarkov.core.model.network.constraint.OnlyDiscreteVariables;
import org.openmarkov.core.model.network.constraint.PNConstraint;

/**
 * Panel to set the definition of the variables of a network. It will have a
 * variable type group selector with two check boxes, and a drop-down list with
 * the default values for the nodes
 * @author jlgozalo
 * @version 1.0 jlgozalo initial
 */
public class NetworkVariablesPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID       = -5183671164848473079L;
    /**
     * Label of the variables Type checkboxes
     */
    private JLabel            jLabelVariablesType    = null;
    /**
     * Label of the default states field.
     */
    private JLabel            jLabelDefaultStates    = null;
    /**
     * Combobox where the user can choose the default states.
     */
    private JComboBox<String> jComboBoxDefaultStates = null;
    /**
     * Specifies if the network whose additionalProperties are edited is new.
     */
    private boolean           newNetwork             = false;
    private ProbNet           probNet;
    private JComboBox<String> jComboBoxVariableType;
    /**
     * String database
     */
    protected StringDatabase  stringDatabase         = StringDatabase.getUniqueInstance ();

    /**
     * constructor without construction parameters
     */
    public NetworkVariablesPanel (ProbNet probNet)
    {
        this.probNet = probNet;
        this.newNetwork = probNet == null;
        setName ("NetworkVariablesPanel");
        initialize ();
        fill ();
    }

    /**
     * This method initialises this instance.
     * @param newNetwork true if the network to show is new, otherwise false
     * @wbp.parser.constructor
     */
    public NetworkVariablesPanel ()
    {
        this.newNetwork = true;
        setName ("NetworkVariablesPanel");
        initialize ();
        fill ();
    }

    /**
     * initialises the layout for this panel.
     */
    private void initialize ()
    {
        final GroupLayout groupLayout = new GroupLayout ((JComponent) this);
        groupLayout.setHorizontalGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addGap (26).addGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addComponent (getJLabelVariablesType (),
                                                                                                                                                                                                                                                                                  GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                  125,
                                                                                                                                                                                                                                                                                  GroupLayout.PREFERRED_SIZE).addPreferredGap (ComponentPlacement.RELATED).addComponent (getJComboBoxVariableType (),
                                                                                                                                                                                                                                                                                                                                                                         GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                         194,
                                                                                                                                                                                                                                                                                                                                                                         GroupLayout.PREFERRED_SIZE)).addGroup (groupLayout.createSequentialGroup ().addComponent (getJLabelDefaultStates (),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                   GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                   125,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                   GroupLayout.PREFERRED_SIZE).addPreferredGap (ComponentPlacement.RELATED).addComponent (getJComboBoxDefaultStates (),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          194,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          GroupLayout.PREFERRED_SIZE))).addContainerGap (17,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         Short.MAX_VALUE)));
        groupLayout.setVerticalGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addGap (11).addGroup (groupLayout.createParallelGroup (Alignment.BASELINE).addComponent (getJLabelVariablesType (),
                                                                                                                                                                                                                                  GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                  39,
                                                                                                                                                                                                                                  GroupLayout.PREFERRED_SIZE).addComponent (getJComboBoxVariableType (),
                                                                                                                                                                                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                            GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                            GroupLayout.PREFERRED_SIZE)).addGap (6).addGroup (groupLayout.createParallelGroup (Alignment.BASELINE).addComponent (getJLabelDefaultStates ()).addComponent (getJComboBoxDefaultStates (),
                                                                                                                                                                                                                                                                                                                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                          GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                          GroupLayout.PREFERRED_SIZE)).addContainerGap (224,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        Short.MAX_VALUE)));
        setLayout (groupLayout);
    }

    /**
     * This method initialises jLabelVariablesType
     * @return the Variables Type JLabel
     */
    private JLabel getJLabelVariablesType ()
    {
        if (jLabelVariablesType == null)
        {
            jLabelVariablesType = new JLabel ();
            jLabelVariablesType.setName ("jLabelVariablesType");
            jLabelVariablesType.setText ("a Label : ");
            jLabelVariablesType.setText (stringDatabase.getString ("NetworkVariablesPanel.jLabelVariablesType.Text"));
        }
        return jLabelVariablesType;
    }

    /**
     * This method initialises jLabelDefaultStates.
     * @return the Default States JLabel
     */
    private JLabel getJLabelDefaultStates ()
    {
        if (jLabelDefaultStates == null)
        {
            jLabelDefaultStates = new JLabel ();
            jLabelDefaultStates.setName ("jLabelDefaultStates");
            jLabelDefaultStates.setText ("a Label : ");
            jLabelDefaultStates.setText (stringDatabase.getString ("NetworkVariablesPanel.jLabelDefaultStates.Text"));
            jLabelDefaultStates.setLabelFor (jComboBoxDefaultStates);
        }
        return jLabelDefaultStates;
    }

    /**
     * This method initialises jComboBoxDefaultStates.
     * @return the Default States JCombo Box
     */
    private JComboBox<String> getJComboBoxDefaultStates ()
    {
        if (jComboBoxDefaultStates == null)
        {
            jComboBoxDefaultStates = new JComboBox<String> (GUIDefaultStates.getListStrings ());
            jComboBoxDefaultStates.setName ("jComboBoxDefaultStates");
            // jComboBoxDefaultStates.addItemListener(this);
        }
        return jComboBoxDefaultStates;
    }

    /**
     * This method initialises jComboBoxDefaultStates.
     * @return the Default States JCombo Box
     */
    private JComboBox<String> getJComboBoxVariableType ()
    {
        if (jComboBoxVariableType == null)
        {
            jComboBoxVariableType = new JComboBox<String> (getListOfTypes ());
            jComboBoxVariableType.setName ("jComboBoxVariableType");
        }
        return jComboBoxVariableType;
    }

    private String[] getListOfTypes ()
    {
        // TODO only discrete variable are enabled
        String[] types = {
                stringDatabase.getString ("NetworkVariablesPanel.ConstraintVariableType.Items."
                                          + "onlydiscrete"),
                stringDatabase.getString ("NetworkVariablesPanel.ConstraintVariableType."
                                          + "items.discreteandcontinuous")};
        return types;
    }

    /**
     * This method fills the content of the fields from a network Properties
     * (ProbNet object)
     */
    private void fill ()
    {
        if (!newNetwork)
        {
            State[] states = probNet.getDefaultStates ();
            // TODO modificar el indice a 2 cuando la creacion de variables
            // continuas este implementado
            int index = 1;
            for (PNConstraint constraint : probNet.getConstraints ())
            {
                if (constraint instanceof OnlyDiscreteVariables)
                {
                    index = 0;
                    break;
                }
                /*
                 * }else if (constraint instanceof OnlyContinuousVariables){
                 * index=1; break; }
                 */
            }
            jComboBoxVariableType.setSelectedIndex (index);
            jComboBoxDefaultStates.setSelectedIndex (DefaultStates.getIndex (states));
            jComboBoxVariableType.addActionListener (new ActionListener ()
                {
                    @Override
                    public void actionPerformed (ActionEvent arg0)
                    {
                        variableTypeChanged ();
                    }
                });
            jComboBoxDefaultStates.addActionListener (new ActionListener ()
                {
                    @Override
                    public void actionPerformed (ActionEvent arg0)
                    {
                        defaultStatesChanged ();
                    }
                });
        }
    }

    private void variableTypeChanged ()
    {
        VariableTypeConstraintEdit variableTypeCE = null;
        Object itemSelected = jComboBoxVariableType.getSelectedItem ();
        if (itemSelected != null
            && itemSelected.equals (stringDatabase.getString ("NetworkVariablesPanel.ConstraintVariableType."
                                                              + "Items.onlydiscrete")))
        {
            variableTypeCE = new VariableTypeConstraintEdit (probNet, new OnlyDiscreteVariables ());
        }
        else if (itemSelected != null
                 && itemSelected.equals (stringDatabase.getString ("NetworkVariablesPanel.ConstraintVariableType."
                                                                   + "Items.onlycontinuous")))
        {
            variableTypeCE = new VariableTypeConstraintEdit (probNet,
                                                             new OnlyContinuousVariables ());
        }
        if (variableTypeCE != null)
        {
            try
            {
                probNet.doEdit (variableTypeCE);
            }
            catch (ConstraintViolationException | CanNotDoEditException
                    | NonProjectablePotentialException | WrongCriterionException | DoEditException e)
            {
                e.printStackTrace ();
                JOptionPane.showMessageDialog (this,
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void defaultStatesChanged ()
    {
        // warning mpalacios relative function to options position.
        // Review "otros" option
        Object itemSelected = jComboBoxDefaultStates.getSelectedItem ();
        if (itemSelected != null)
        {
            NetworkDefaultStatesEdit networkDefaultStatesEdit = new NetworkDefaultStatesEdit (
                                                                                              probNet,
                                                                                              getDefaultStates ());
            try
            {
                probNet.doEdit (networkDefaultStatesEdit);
            }
            catch (ConstraintViolationException | CanNotDoEditException
                    | NonProjectablePotentialException | WrongCriterionException | DoEditException e)
            {
                e.printStackTrace ();
                JOptionPane.showMessageDialog (this,
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public State[] getDefaultStates ()
    {
        int i = 0;
        int selectedIndex = jComboBoxDefaultStates.getSelectedIndex ();
        String[] defaultStateNames = DefaultStates.getByIndex (selectedIndex);
        State[] defaultStates = new State[defaultStateNames.length];
        for (String str : defaultStateNames)
        {
            defaultStates[i] = new State (str);
            i++;
        }
        return defaultStates;
    }
}
