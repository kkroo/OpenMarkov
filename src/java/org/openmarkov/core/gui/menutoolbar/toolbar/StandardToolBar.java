/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.menutoolbar.toolbar;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.ZoomMenuToolBar;
import org.openmarkov.core.gui.window.edition.NetworkPanel;

/**
 * This class implements the standard toolbar of the application.
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Added CloseNetworkButton to the toolbar
 * @version 1.2 20100408 jlgozalo Change the order of ZoomIn and ZoomOut buttons
 */
public class StandardToolBar extends ToolBarBasic
    implements
        ZoomMenuToolBar,
        MouseMotionListener
{
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID   = 6583864684969808896L;
    /**
     * Button to create a new network.
     */
    private JButton           newNetworkButton   = null;
    /**
     * Button to open a network.
     */
    private JButton           openNetworkButton  = null;
    /**
     * Button to open a network.
     */
    private JButton           saveNetworkButton  = null;
    /**
     * Button to open a network.
     */
    private JButton           closeNetworkButton = null;
    /**
     * Button to zoom in a network.
     */
    private JButton           zoomInButton       = null;
    /**
     * Button to zoom out a network.
     */
    private JButton           zoomOutButton      = null;
    /**
     * Combobox to select zoom values.
     */
    private ZoomComboBox      zoomComboBox       = null;
    /**
     * Button to switch to Inference mode.
     */
    private JToggleButton     workingModeButton  = null;
    /**
     * Button to open a decision tree panel.
     */
    private JToggleButton     decisionTreeButton = null;
    /**
     * Icon loader.
     */
    private IconLoader        iconLoader         = null;

    /**
     * This method initialises this instance.
     * @param newListener object that listens to the buttons events.
     */
    public StandardToolBar (ActionListener newListener)
    {
        super (newListener);
        initialize ();
    }

    /**
     * This method configures the toolbar.
     */
    private void initialize ()
    {
        iconLoader = new IconLoader ();
        add (getNewNetworkButton ());
        add (getOpenNetworkButton ());
        add (getSaveNetworkButton ());
        add (getCloseNetworkButton ());
        addSeparator ();
        add (getZoomOutButton ());
        add (getZoomComboBox ());
        add (getZoomInButton ());
        addSeparator ();
        add (getWorkingModeButton ());
        add (getDecisionTreeButton ());
        add (Box.createHorizontalGlue ());
    }

    /**
     * This method initialises newNetworkButton.
     * @return a new button.
     */
    private JButton getNewNetworkButton ()
    {
        if (newNetworkButton == null)
        {
            newNetworkButton = new JButton ();
            newNetworkButton.setIcon (iconLoader.load (IconLoader.ICON_NEW_ENABLED));
            newNetworkButton.setActionCommand (ActionCommands.NEW_NETWORK);
            newNetworkButton.setFocusable (false);
            newNetworkButton.setToolTipText (stringDatabase.getString (ActionCommands.NEW_NETWORK
                                                                       + STRING_TOOLTIP_SUFFIX));
            newNetworkButton.addActionListener (listener);
            newNetworkButton.addMouseMotionListener (this);
        }
        return newNetworkButton;
    }

    /**
     * This method initialises openNetworkButton.
     * @return a new button.
     */
    private JButton getOpenNetworkButton ()
    {
        if (openNetworkButton == null)
        {
            openNetworkButton = new JButton ();
            openNetworkButton.setIcon (iconLoader.load (IconLoader.ICON_OPEN_ENABLED));
            openNetworkButton.setActionCommand (ActionCommands.OPEN_NETWORK);
            openNetworkButton.setFocusable (false);
            openNetworkButton.setToolTipText (stringDatabase.getString (ActionCommands.OPEN_NETWORK
                                                                        + STRING_TOOLTIP_SUFFIX));
            openNetworkButton.addActionListener (listener);
            openNetworkButton.addMouseMotionListener (this);
        }
        return openNetworkButton;
    }

    /**
     * This method initialises saveNetworkButton.
     * @return a new button.
     */
    private JButton getSaveNetworkButton ()
    {
        if (saveNetworkButton == null)
        {
            saveNetworkButton = new JButton ();
            saveNetworkButton.setIcon (iconLoader.load (IconLoader.ICON_SAVE_ENABLED));
            saveNetworkButton.setActionCommand (ActionCommands.SAVE_NETWORK);
            saveNetworkButton.setFocusable (false);
            saveNetworkButton.setToolTipText (stringDatabase.getString (ActionCommands.SAVE_NETWORK
                                                                        + STRING_TOOLTIP_SUFFIX));
            saveNetworkButton.addActionListener (listener);
            saveNetworkButton.addMouseMotionListener (this);
        }
        return saveNetworkButton;
    }

    /**
     * This method initialises closeNetworkButton.
     * @return a new button.
     */
    private JButton getCloseNetworkButton ()
    {
        if (closeNetworkButton == null)
        {
            closeNetworkButton = new JButton ();
            closeNetworkButton.setIcon (iconLoader.load (IconLoader.ICON_CLOSE_ENABLED));
            closeNetworkButton.setActionCommand (ActionCommands.CLOSE_NETWORK);
            closeNetworkButton.setFocusable (false);
            closeNetworkButton.setToolTipText (stringDatabase.getString (ActionCommands.CLOSE_NETWORK
                                                                         + STRING_TOOLTIP_SUFFIX));
            closeNetworkButton.addActionListener (listener);
            closeNetworkButton.addMouseMotionListener (this);
        }
        return closeNetworkButton;
    }

    /**
     * This method initialises zoomInButton.
     * @return a new button.
     */
    private JButton getZoomInButton ()
    {
        if (zoomInButton == null)
        {
            zoomInButton = new JButton ();
            zoomInButton.setIcon (iconLoader.load (IconLoader.ICON_ZOOM_IN_ENABLED));
            zoomInButton.setActionCommand (ActionCommands.ZOOM_IN);
            zoomInButton.setFocusable (false);
            zoomInButton.setToolTipText (stringDatabase.getString (ActionCommands.ZOOM_IN
                                                                   + STRING_TOOLTIP_SUFFIX));
            zoomInButton.addActionListener (listener);
            zoomInButton.addMouseMotionListener (this);
        }
        return zoomInButton;
    }

    /**
     * This method initialises zoomOutButton.
     * @return a new button.
     */
    private JButton getZoomOutButton ()
    {
        if (zoomOutButton == null)
        {
            zoomOutButton = new JButton ();
            zoomOutButton.setIcon (iconLoader.load (IconLoader.ICON_ZOOM_OUT_ENABLED));
            zoomOutButton.setActionCommand (ActionCommands.ZOOM_OUT);
            zoomOutButton.setFocusable (false);
            zoomOutButton.setToolTipText (stringDatabase.getString (ActionCommands.ZOOM_OUT
                                                                    + STRING_TOOLTIP_SUFFIX));
            zoomOutButton.addActionListener (listener);
            zoomOutButton.addMouseMotionListener (this);
        }
        return zoomOutButton;
    }

    /**
     * This method initialises zoomComboBox.
     * @return a new zoom combobox.
     */
    private ZoomComboBox getZoomComboBox ()
    {
        if (zoomComboBox == null)
        {
            zoomComboBox = new ZoomComboBox (listener);
        }
        return zoomComboBox;
    }

    /**
     * This method sets the value of the combobox.
     * @param value new value of zoom.
     */
    public void setZoom (double value)
    {
        zoomComboBox.setZoom (value);
    }

    /**
     * This method initialises workingModeButton.
     * @return a working mode button.
     */
    private JToggleButton getWorkingModeButton ()
    {
        if (workingModeButton == null)
        {
            workingModeButton = new JToggleButton ();
            workingModeButton.setIcon (iconLoader.load (IconLoader.ICON_INFERENCE_MODE_ENABLED));
            workingModeButton.setFocusable (false);
            workingModeButton.setActionCommand (ActionCommands.CHANGE_WORKING_MODE);
            workingModeButton.setToolTipText (stringDatabase.getString (ActionCommands.CHANGE_WORKING_MODE
                                                                        + STRING_TOOLTIP_SUFFIX));
            workingModeButton.addActionListener (listener);
            workingModeButton.addMouseMotionListener (this);
        }
        return workingModeButton;
    }

    /**
     * This method initialises decisionTreeButton.
     * @return a new button.
     */
    public JToggleButton getDecisionTreeButton ()
    {
        if (decisionTreeButton == null)
        {
            decisionTreeButton = new JToggleButton ();
            decisionTreeButton.setIcon (iconLoader.load (IconLoader.ICON_DECISION_TREE));
            decisionTreeButton.setActionCommand (ActionCommands.DECISION_TREE);
            decisionTreeButton.setFocusable (false);
            decisionTreeButton.setToolTipText (stringDatabase.getString (ActionCommands.DECISION_TREE
                                                                         + STRING_TOOLTIP_SUFFIX));
            decisionTreeButton.addActionListener (listener);
            decisionTreeButton.addMouseMotionListener (this);
        }
        return decisionTreeButton;
    }

    /**
     * This method sets the button for switching between Edition/inference to
     * the pertinent value (pressed or not)
     * @param workingMode the working mode of the currently selected
     *            NetworkPanel. Depending on this value, the button will be set
     *            pressed or not.
     */
    public void changeWorkingModeButton (int workingMode)
    {
        if (workingMode == NetworkPanel.INFERENCE_WORKING_MODE)
        {
            workingModeButton.setSelected (true);
        }
        else
        {
            workingModeButton.setSelected (false);
        }
    }

    /**
     * Returns the component that corresponds to an action command.
     * @param actionCommand action command that identifies the component.
     * @return a components identified by the action command.
     */
    @Override
    protected JComponent getJComponentActionCommand (String actionCommand)
    {
        JComponent component = null;
        if (actionCommand.equals (ActionCommands.NEW_NETWORK))
        {
            component = newNetworkButton;
        }
        else if (actionCommand.equals (ActionCommands.OPEN_NETWORK))
        {
            component = openNetworkButton;
        }
        else if (actionCommand.equals (ActionCommands.SAVE_NETWORK))
        {
            component = saveNetworkButton;
        }
        else if (actionCommand.equals (ActionCommands.ZOOM_IN))
        {
            component = zoomInButton;
        }
        else if (actionCommand.equals (ActionCommands.ZOOM_OUT))
        {
            component = zoomOutButton;
        }
        else if (actionCommand.equals (ActionCommands.ZOOM_OTHER))
        {
            component = zoomComboBox;
        }
        else if (actionCommand.equals (ActionCommands.CHANGE_WORKING_MODE))
        {
            component = workingModeButton;
        }
        else if (actionCommand.equals (ActionCommands.DECISION_TREE))
        {
            component = decisionTreeButton;
        }
        return component;
    }

    @Override
    public void mouseDragged (MouseEvent e)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseMoved (MouseEvent e)
    {
        if (e.getSource ().equals (getNewNetworkButton ()))
        {
            getNewNetworkButton ().setToolTipText (stringDatabase.getString (ActionCommands.NEW_NETWORK
                                                                             + STRING_TOOLTIP_SUFFIX));
        }
        else if (e.getSource ().equals (getOpenNetworkButton ()))
        {
            getOpenNetworkButton ().setToolTipText (stringDatabase.getString (ActionCommands.OPEN_NETWORK
                                                                              + STRING_TOOLTIP_SUFFIX));
        }
        else if (e.getSource ().equals (getSaveNetworkButton ()))
        {
            getSaveNetworkButton ().setToolTipText (stringDatabase.getString (ActionCommands.SAVE_NETWORK
                                                                              + STRING_TOOLTIP_SUFFIX));
        }
        else if (e.getSource ().equals (getCloseNetworkButton ()))
        {
            getCloseNetworkButton ().setToolTipText (stringDatabase.getString (ActionCommands.CLOSE_NETWORK
                                                                               + STRING_TOOLTIP_SUFFIX));
        }
        else if (e.getSource ().equals (getZoomInButton ()))
        {
            getZoomInButton ().setToolTipText (stringDatabase.getString (ActionCommands.ZOOM_IN
                                                                         + STRING_TOOLTIP_SUFFIX));
        }
        else if (e.getSource ().equals (getZoomOutButton ()))
        {
            getZoomOutButton ().setToolTipText (stringDatabase.getString (ActionCommands.ZOOM_OUT
                                                                          + STRING_TOOLTIP_SUFFIX));
        }
        else if (e.getSource ().equals (getWorkingModeButton ()))
        {
            getWorkingModeButton ().setToolTipText (stringDatabase.getString (ActionCommands.CHANGE_WORKING_MODE
                                                                              + STRING_TOOLTIP_SUFFIX));
        }
    }
}
