/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.menutoolbar.toolbar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;

/**
 * This class fills its comboBox and listen to it to send action commands
 * defined in the class ActionCommands.
 * 
 * @author asaez
 * @version 1.0
 */
public class ExpansionThresholdComboBox extends JComboBox implements ItemListener {

    /**
     * Static field for serializable class.
     */
    private static final long    serialVersionUID           = 5380008890004340036L;

    /**
     * Elements of the comboBox.
     */
    private static double[]      EXPANSION_THRESHOLD_VALUES = { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

    /**
     * Data model for the comboBox.
     */
    private DefaultComboBoxModel comboModel                 = null;

    /**
     * Current value of the expansion threshold.
     */
    private double               currentValue;

    /**
     * Object that listen to the user's actions.
     */
    private ActionListener       listener;

    /**
     * Constructor that fills and initialize the comboBox.
     * 
     * @param newListener
     *            object that listens for changes in threshold values.
     */
    public ExpansionThresholdComboBox(ActionListener newListener) {
        super();
        fillModel();
        setModel(comboModel);
        listener = newListener;
        initialize();
    }

    private void fillModel() {
        comboModel = new DefaultComboBoxModel();
        for (int i = 0; i < EXPANSION_THRESHOLD_VALUES.length; i++) {
            comboModel.addElement(EXPANSION_THRESHOLD_VALUES[i]);
        }
    }

    /**
     * This method initialises this instance.
     */
    private void initialize() {
        setEditable(false);
        setPreferredSize(new Dimension(60, 25));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setExpansionThreshold(5.0);
        addItemListener(this);
    }

    /**
     * Invoked when an item has been selected.
     * 
     * @param e
     *            event information.
     */
    public void itemStateChanged(ItemEvent e) {
        double newValue = (Double) e.getItem();
        if (!(newValue == currentValue)) {
            setExpansionThreshold(newValue);
            listener.actionPerformed(new ActionEvent(newValue,
                    0,
                    ActionCommands.SET_NEW_EXPANSION_THRESHOLD));
        }
    }

    /**
     * This method sets the value of the comboBox.
     * 
     * @param value
     *            new value for the Expansion Threshold.
     */
    public void setExpansionThreshold(double value) {
        if ((value >= 0.0) && (value <= 10.0)) {
            comboModel.setSelectedItem(Math.floor(value));
            currentValue = (Double) comboModel.getSelectedItem();
        }
    }

    /**
     * This method returns the current expansion threshold value.
     * 
     * @return the current expansion threshold value.
     */
    public double getExpansionThreshold() {
        return currentValue;
    }

}
