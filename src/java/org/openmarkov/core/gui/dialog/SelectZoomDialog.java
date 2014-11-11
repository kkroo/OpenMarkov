/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.window.edition.Zoom;

/**
 * Dialog box used to select a zoom value for the network panel.
 * @author jmendoza
 * @version 1.0
 */
public class SelectZoomDialog extends OkCancelHorizontalDialog
{
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID    = 5524726919501736960L;
    /**
     * Radio button to select 500% of zoom.
     */
    private JRadioButton      zoom500             = null;
    /**
     * Radio button to select 200% of zoom.
     */
    private JRadioButton      zoom200             = null;
    /**
     * Radio button to select 150% of zoom.
     */
    private JRadioButton      zoom150             = null;
    /**
     * Radio button to select 100% of zoom.
     */
    private JRadioButton      zoom100             = null;
    /**
     * Radio button to select 75% of zoom.
     */
    private JRadioButton      zoom75              = null;
    /**
     * Radio button to select 50% of zoom.
     */
    private JRadioButton      zoom50              = null;
    /**
     * Radio button to select 25% of zoom.
     */
    private JRadioButton      zoom25              = null;
    /**
     * Radio button to select 10% of zoom.
     */
    private JRadioButton      zoom10              = null;
    /**
     * Panel used to contain zoom other's controls.
     */
    private JPanel            zoomOtherPanel      = null;
    /**
     * Radio button to select other zoom.
     */
    private JRadioButton      zoomOther           = null;
    /**
     * Spinner to select a zoom value.
     */
    private JSpinner          zoomSpinner         = null;
    /**
     * Object used to make autoexclusive the zoom values.
     */
    private ButtonGroup       groupZoom           = new ButtonGroup ();
    /**
     * Contents panel.
     */
    private JPanel            contentsPanel       = null;
    /**
     * Value of the zoom selected by the user.
     */
    private double            zoom;
    /**
     * Action listener used to enable or disable the spinner control.
     */
    private ChangeListener    radiobuttonListener = null;

    /**
     * initialises and configures the dialog box.
     * @param owner window that owns the dialog box.
     */
    public SelectZoomDialog (Window owner)
    {
        super (owner);
        initialize ();
        setLocationRelativeTo (owner);
    }

    /**
     * This method initialises this instance.
     */
    private void initialize ()
    {
        setSize (300, 300);
        setTitle (stringDatabase.getString ("SelectZoom.Title.Label"));
        configureComponentsPanel ();
        pack ();
    }

    /**
     * Sets up the panel where all components, except the buttons of the buttons
     * panel, will be appear.
     */
    private void configureComponentsPanel ()
    {
        getComponentsPanel ().setLayout (new BorderLayout ());
        getComponentsPanel ().add (getContentsPanel (), BorderLayout.CENTER);
    }

    /**
     * This method initialises contentsPanel.
     * @return a new contentsPanel.
     */
    private JPanel getContentsPanel ()
    {
        if (contentsPanel == null)
        {
            contentsPanel = new JPanel ();
            contentsPanel.setLayout (new GridLayout (0, 1, 0, 0));
            contentsPanel.setBorder (BorderFactory.createTitledBorder (null,
                                                                       stringDatabase.getString ("ZoomValues.Title.Label"),
                                                                       TitledBorder.DEFAULT_JUSTIFICATION,
                                                                       TitledBorder.DEFAULT_POSITION,
                                                                       null, new Color (51, 51, 51)));
            radiobuttonListener = new ChangeListener ()
                {
                    public void stateChanged (ChangeEvent e)
                    {
                        zoomSpinner.setEnabled (zoomOther.isSelected ());
                    }
                };
            contentsPanel.add (getZoom500 (), null);
            contentsPanel.add (getZoom200 (), null);
            contentsPanel.add (getZoom150 (), null);
            contentsPanel.add (getZoom100 (), null);
            contentsPanel.add (getZoom75 (), null);
            contentsPanel.add (getZoom50 (), null);
            contentsPanel.add (getZoom25 (), null);
            contentsPanel.add (getZoom10 (), null);
            contentsPanel.add (getZoomOtherPanel (), null);
        }
        return contentsPanel;
    }

    /**
     * This method initialises zoom500.
     * @return a new radiobutton to select 500% of zoom.
     */
    private JRadioButton getZoom500 ()
    {
        if (zoom500 == null)
        {
            zoom500 = new JRadioButton ();
            zoom500.setText ("500%");
            zoom500.addChangeListener (radiobuttonListener);
            groupZoom.add (zoom500);
        }
        return zoom500;
    }

    /**
     * This method initialises zoom200.
     * @return a new radiobutton to select 200% of zoom.
     */
    private JRadioButton getZoom200 ()
    {
        if (zoom200 == null)
        {
            zoom200 = new JRadioButton ();
            zoom200.setText ("200%");
            zoom200.addChangeListener (radiobuttonListener);
            groupZoom.add (zoom200);
        }
        return zoom200;
    }

    /**
     * This method initialises zoom150.
     * @return a new radiobutton to select 500% of zoom.
     */
    private JRadioButton getZoom150 ()
    {
        if (zoom150 == null)
        {
            zoom150 = new JRadioButton ();
            zoom150.setText ("150%");
            zoom150.addChangeListener (radiobuttonListener);
            groupZoom.add (zoom150);
        }
        return zoom150;
    }

    /**
     * This method initialises zoom100.
     * @return a new radiobutton to select 100% of zoom.
     */
    private JRadioButton getZoom100 ()
    {
        if (zoom100 == null)
        {
            zoom100 = new JRadioButton ();
            zoom100.setText ("100%");
            zoom100.addChangeListener (radiobuttonListener);
            groupZoom.add (zoom100);
        }
        return zoom100;
    }

    /**
     * This method initialises zoom75.
     * @return a new radiobutton to select 75% of zoom.
     */
    private JRadioButton getZoom75 ()
    {
        if (zoom75 == null)
        {
            zoom75 = new JRadioButton ();
            zoom75.setText ("75%");
            zoom75.addChangeListener (radiobuttonListener);
            groupZoom.add (zoom75);
        }
        return zoom75;
    }

    /**
     * This method initialises zoom50.
     * @return a new radiobutton to select 50% of zoom.
     */
    private JRadioButton getZoom50 ()
    {
        if (zoom50 == null)
        {
            zoom50 = new JRadioButton ();
            zoom50.setText ("50%");
            zoom50.addChangeListener (radiobuttonListener);
            groupZoom.add (zoom50);
        }
        return zoom50;
    }

    /**
     * This method initialises zoom25.
     * @return a new radiobutton to select 25% of zoom.
     */
    private JRadioButton getZoom25 ()
    {
        if (zoom25 == null)
        {
            zoom25 = new JRadioButton ();
            zoom25.setText ("25%");
            zoom25.addChangeListener (radiobuttonListener);
            groupZoom.add (zoom25);
        }
        return zoom25;
    }

    /**
     * This method initialises zoom10.
     * @return a new radiobutton to select 10% of zoom.
     */
    private JRadioButton getZoom10 ()
    {
        if (zoom10 == null)
        {
            zoom10 = new JRadioButton ();
            zoom10.setText ("10%");
            zoom10.addChangeListener (radiobuttonListener);
            groupZoom.add (zoom10);
        }
        return zoom10;
    }

    /**
     * This method initialises zoomOtherPanel
     * @return javax.swing.JPanel
     */
    private JPanel getZoomOtherPanel ()
    {
        if (zoomOtherPanel == null)
        {
            zoomOtherPanel = new JPanel ();
            zoomOtherPanel.setLayout (new FlowLayout (FlowLayout.LEFT, 0, 0));
            zoomOtherPanel.add (getZoomOther ());
            zoomOtherPanel.add (getZoomSpinner ());
        }
        return zoomOtherPanel;
    }

    /**
     * This method initialises zoomOther.
     * @return a new radiobutton to select other zoom.
     */
    private JRadioButton getZoomOther ()
    {
        if (zoomOther == null)
        {
            zoomOther = new JRadioButton ();
            zoomOther.setText (stringDatabase.getString ("ZoomOther.Text.Label"));
            zoomOther.setMnemonic (stringDatabase.getString ("ZoomOther.Text.Mnemonic").charAt (0));
            zoomOther.addChangeListener (radiobuttonListener);
            groupZoom.add (zoomOther);
        }
        return zoomOther;
    }

    /**
     * This method initialises zoomSpinner.
     * @return a new zoom spinner.
     */
    private JSpinner getZoomSpinner ()
    {
        if (zoomSpinner == null)
        {
            zoomSpinner = new JSpinner ();
            zoomSpinner.setModel (new SpinnerNumberModel (
                                                          (int) Math.round (Zoom.DEFAULT_VALUE * 100),
                                                          (int) Math.round (Zoom.MIN_VALUE * 100),
                                                          (int) Math.round (Zoom.MAX_VALUE * 100),
                                                          1));
        }
        return zoomSpinner;
    }

    // ESCA-JAVA0110: allows override
    /**
     * This method carries out the actions when the user press the Ok button
     * before hide the dialog.
     * @return true always and set the zoom to the selected value
     */
    @Override
    protected boolean doOkClickBeforeHide ()
    {
        if (zoom500.isSelected ())
        {
            zoom = 5;
        }
        else if (zoom200.isSelected ())
        {
            zoom = 2;
        }
        else if (zoom150.isSelected ())
        {
            zoom = 1.5;
        }
        else if (zoom100.isSelected ())
        {
            zoom = 1;
        }
        else if (zoom75.isSelected ())
        {
            zoom = 0.75;
        }
        else if (zoom50.isSelected ())
        {
            zoom = 0.5;
        }
        else if (zoom25.isSelected ())
        {
            zoom = 0.25;
        }
        else if (zoom10.isSelected ())
        {
            zoom = 0.1;
        }
        else
        {
            zoom = ((Integer) zoomSpinner.getValue ()).doubleValue () / 100;
        }
        return true;
    }

    /**
     * This method carries out the actions when the user press the Cancel button
     * before hide the dialog.
     */
    @Override
    protected void doCancelClickBeforeHide ()
    {
        zoom = 0;
    }

    /**
     * Checks the radiobutton corresponding to the zoom value.
     * @param value The value of the zoom to set
     */
    private void setZoom (double value)
    {
        Double aValue = new Double (value);
        zoom = value;
        if (aValue.equals (5))
        {
            zoom500.setSelected (true);
        }
        else if (aValue.equals (2))
        {
            zoom200.setSelected (true);
        }
        else if (aValue.equals (1.5))
        {
            zoom150.setSelected (true);
        }
        else if (aValue.equals (1))
        {
            zoom100.setSelected (true);
        }
        else if (aValue.equals (0.75))
        {
            zoom75.setSelected (true);
        }
        else if (aValue.equals (0.5))
        {
            zoom50.setSelected (true);
        }
        else if (aValue.equals (0.25))
        {
            zoom25.setSelected (true);
        }
        else if (aValue.equals (0.1))
        {
            zoom10.setSelected (true);
        }
        else
        {
            zoomOther.setSelected (true);
            zoomSpinner.setValue ((int) Math.round (value * 100));
        }
    }

    /**
     * Returns the zoom value selected by the user.
     * @return the zoom value selected by the user or 0 if the user cancels the
     *         dialog box.
     */
    public double getZoom ()
    {
        return zoom;
    }

    /**
     * This method shows the dialog and requests the user a new zoom value.
     * @param defaultZoom zoom that is selected as default.
     * @return OK_BUTTON if the user has pressed the 'Ok' button or
     *         CANCEL_BUTTON if the user has pressed the 'Cancel' button.
     */
    public int requestZoom (double defaultZoom)
    {
        setZoom (defaultZoom);
        setVisible (true);
        return selectedButton;
    }
}
