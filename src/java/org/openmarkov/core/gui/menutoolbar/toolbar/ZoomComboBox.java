/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.menutoolbar.toolbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;

/**
 * This class fills its combobox and listen to it to send action commands
 * defined in the class ActionCommands.
 * 
 * @author jmendoza
 */
public class ZoomComboBox extends JComboBox<String> implements ItemListener, KeyListener {
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID = 5380198895714343936L;
    /**
     * Prefixed elements of the combobox.
     */
    private static String[]   ZOOM_VALUES      = { "500%", "200%", "150%", "100%", "75%", "50%",
            "25%", "10%"                      };
    /**
     * Old value of the combobox
     */
    private String            oldValue;
    /**
     * Object that listen to the user's actions.
     */
    private ActionListener    listener;

    /**
     * Constructor that fills and initialize the combobox.
     * 
     * @param newListener
     *            object that listens to the zoom values.
     */
    public ZoomComboBox(ActionListener newListener) {
        super(ZOOM_VALUES);
        listener = newListener;
        initialize();
    }

    /**
     * This method initialises this instance.
     */
    private void initialize() {
        setEditable(true);
        setPreferredSize(new Dimension(70, 25));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setSelectedIndex(3); // 100% item
        oldValue = (String) getSelectedItem();
        addItemListener(this);
        addComponentKeyListener(this);
    }

    /**
     * Adds a new key listener to the component where edition is performed.
     * 
     * @param newListener
     *            new key listener.
     */
    private void addComponentKeyListener(KeyListener newListener) {
        Component componentEditor = getEditor().getEditorComponent();
        if (componentEditor != null) {
            componentEditor.addKeyListener(newListener);
        }
    }

    /**
     * Returns an integer that contains the value of the string. The string can
     * contain the symbol '%' at the end.
     * 
     * @param zoomString
     *            string that contains a zoom value.
     * @return the integer zoom value or null if the string has not a correct
     *         value.
     */
    private Integer getZoomValue(String zoomString) {
        Integer result;
        int length;
        String substring;
        String zString = zoomString.trim();
        int percentajePosition;
        length = zString.length();
        if (length == 0) {
            return null;
        }
        percentajePosition = zString.indexOf("%");
        if (percentajePosition >= 0) {
            if (percentajePosition == (length - 1)) {
                substring = zString.substring(0, percentajePosition);
            } else {
                substring = "";
            }
        } else {
            substring = zString;
        }
        try {
            result = new Integer(substring);
        } catch (NumberFormatException e) {
            result = null;
        }
        return result;
    }

    /**
     * Invoked when an item has been selected.
     * 
     * @param e
     *            event information.
     */
    public void itemStateChanged(ItemEvent e) {
        String newActionCommand;
        Integer zoomValue;
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (!e.getItem().equals(oldValue)) {
                zoomValue = getZoomValue((String) e.getItem());
                if ((zoomValue == null)
                        || (zoomValue.intValue() < 10)
                        || (zoomValue.intValue() > 500)) {
                    JOptionPane.showMessageDialog(getRootPane(),
                            StringDatabase.getUniqueInstance().getString("WrongZoomValue.Text.Label"),
                            StringDatabase.getUniqueInstance().getString("ErrorWindow.Title.Label"),
                            JOptionPane.ERROR_MESSAGE);
                    setSelectedItem(oldValue);
                } else {
                    newActionCommand = ActionCommands.getZoomActionCommandValue(zoomValue.doubleValue() / 100);
                    setSelectedItem(zoomValue + "%");
                    oldValue = (String) getSelectedItem();
                    listener.actionPerformed(new ActionEvent(this, 0, newActionCommand));
                }
                getRootPane().requestFocusInWindow();
            }
        }
    }

    /**
     * This method sets the value of the combobox.
     * 
     * @param value
     *            new value of zoom.
     */
    public void setZoom(double value) {
        setSelectedItem((int) Math.round(value * 100) + "%");
    }

    /**
     * Invoked when a key has been pressed.
     * 
     * @param e
     *            event information.
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            getEditor().setItem(oldValue);
            getRootPane().requestFocusInWindow();
        }
    }

    /**
     * Invoked when a key has been released. This method does nothing.
     * 
     * @param e
     *            event information.
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Invoked when a key has been typed. This method does nothing.
     * 
     * @param e
     *            event information.
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Enables the combo box so that items can be selected. When the combo box
     * is disabled, items cannot be selected, values cannot be typed into its
     * field and no elements are selected.
     * 
     * @param b
     *            true enables the combobox and false disables it.
     */
    @Override
    public void setEnabled(boolean b) {
        if (!b) {
            setSelectedIndex(-1);
        }
        super.setEnabled(b);
    }
}
