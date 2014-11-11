/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;

import org.openmarkov.core.action.PotentialChangeEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.DeltaPotential;
import org.openmarkov.core.model.network.potential.Potential;

@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "Delta")
public class DeltaPotentialPanel extends PotentialPanel {

    private JComboBox<String> stateComboBox;
    private JSpinner valueSpinner;
    private ProbNode probNode;
    
    public DeltaPotentialPanel(ProbNode probNode)
    {
        super();
        this.probNode = probNode;
        initComponents();
        
        setData(probNode);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel namelessPanel = new JPanel();
        namelessPanel.setBorder(new EtchedBorder());
        
        if(probNode.getVariable().getVariableType() == VariableType.NUMERIC)
        {
            SpinnerNumberModel model = new SpinnerNumberModel(0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.001); 
            valueSpinner = new JSpinner(model);
            valueSpinner.setPreferredSize(new Dimension(100, 20));
            JLabel valueLabel = new JLabel("Numeric value:");
            valueLabel.setLabelFor(valueSpinner);
            namelessPanel.add(valueLabel);
            namelessPanel.add(valueSpinner);
        }else
        {
            stateComboBox = new JComboBox<String>();
            stateComboBox.setPreferredSize(new Dimension(100, 20));
            JLabel stateLabel = new JLabel("State:");
            stateLabel.setLabelFor(stateComboBox);
            namelessPanel.add(stateLabel);
            namelessPanel.add(stateComboBox);
        }
        namelessPanel.setPreferredSize(new Dimension(200, 50));
        add(namelessPanel);
    }
    
    @Override
    public void setData(ProbNode probNode) {
        this.probNode = probNode;
        DeltaPotential oldPotential = null;
        if (!probNode.getPotentials().isEmpty()
                && probNode.getPotentials().get(0) instanceof DeltaPotential)
        {
            oldPotential = (DeltaPotential) probNode.getPotentials().get(0);
        }
        if(probNode.getVariable().getVariableType() == VariableType.NUMERIC)
        {
            double value = Double.NEGATIVE_INFINITY; 
            if(oldPotential != null)
            {
                value = oldPotential.getNumericValue();
            }else
            {
                value = probNode.getVariable().getPartitionedInterval().getMin();
            }
            valueSpinner.setValue(value);
        }else
        {
            stateComboBox.removeAllItems();
            for(State state : probNode.getVariable().getStates())
            {
                stateComboBox.addItem(state.getName());
            }
            
            if(oldPotential != null)
            {
                stateComboBox.setSelectedItem(oldPotential.getState().getName());
            }
        }
    }

    @Override
    public boolean saveChanges() {
        boolean result = super.saveChanges();
        ProbNet probNet = probNode.getProbNet();
        Potential oldPotential = probNode.getPotentials().get(0);
        Potential newPotential = null;
        if(probNode.getVariable().getVariableType() == VariableType.NUMERIC)
        {
            double numericValue = Double.parseDouble(valueSpinner.getValue().toString());
            PartitionedInterval domain = probNode.getVariable().getPartitionedInterval();
            if(numericValue <= domain.getMax() && numericValue >= domain.getMin())
            {
                newPotential = new DeltaPotential(oldPotential.getVariables(), oldPotential.getPotentialRole(), numericValue);
            }else
            {
                JOptionPane.showMessageDialog(null, "The value entered is not inside the domain of the variable.", 
                        "Invalid value", JOptionPane.ERROR_MESSAGE);
            }
        }else
        {
            int selectedIndex = stateComboBox.getSelectedIndex();
            State state = probNode.getVariable().getStates()[selectedIndex];
            newPotential = new DeltaPotential(oldPotential.getVariables(), oldPotential.getPotentialRole(), state);
        }
        newPotential.setComment(oldPotential.getComment());
        PotentialChangeEdit edit = new PotentialChangeEdit(probNet, oldPotential, newPotential);
        try {
            probNet.doEdit(edit);
        } catch (ConstraintViolationException
                | CanNotDoEditException
                | NonProjectablePotentialException
                | WrongCriterionException
                | DoEditException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() {
        // Do nothing
    }

}
