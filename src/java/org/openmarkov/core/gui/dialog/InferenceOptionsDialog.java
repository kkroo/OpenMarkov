/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.menutoolbar.toolbar.InferenceToolBar;
import org.openmarkov.core.gui.window.edition.EditorPanel;

/**
 * Dialog box to set the inference options
 * @author asaez
 * @version 1.0
 */
public class InferenceOptionsDialog extends JDialog
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1912979194800110113L;
    /**
     * Button group that holds the radio buttons that will be shown. There is a
     * radio button for each state of the node.
     */
    ButtonGroup               buttonGroup      = new ButtonGroup ();
    /**
     * String database
     */
    protected StringDatabase  stringDatabase   = StringDatabase.getUniqueInstance ();

    /**
     * This method initialises this instance.
     * @param owner window that owns this dialog.
     * @param editorPanel the editor panel that called this dialog.
     */
    public InferenceOptionsDialog (Window owner,
                                   EditorPanel editorPanel,
                                   InferenceToolBar inferenceToolBar)
    {
        JPanel principalPanel = new JPanel ();
        JPanel textPanel = new JPanel ();
        JPanel radioButtonsPanel = new JPanel ();
        JPanel buttonsPanel = new JPanel ();
        JButton okButton = new JButton (
                                        stringDatabase.getString ("OptionsInferenceDialog.jButtonOK.Label"));
        JButton cancelButton = new JButton (
                                            stringDatabase.getString ("OptionsInferenceDialog.jButtonCancel.Label"));
        setTitle (stringDatabase.getString ("OptionsInferenceDialog.Title.Label"));
        this.getContentPane ().setLayout (new BorderLayout ());
        setLocationRelativeTo (owner);
        this.getContentPane ().add (principalPanel, BorderLayout.CENTER);
        principalPanel.setLayout (new BorderLayout ());
        textPanel.setLayout (new GridLayout (2, 1));
        textPanel.add (new JLabel (
                                   "\n"
                                           + stringDatabase.getString ("OptionsInferenceDialog.Text.Label"),
                                   SwingConstants.CENTER));
        principalPanel.add (textPanel, BorderLayout.NORTH);
        radioButtonsPanel.setLayout (new GridLayout (2, 1));
        JRadioButton jRadioButton1 = new JRadioButton (
                                                       stringDatabase.getString ("OptionsInferenceDialog.optionAuto.Label"));
        jRadioButton1.setActionCommand (stringDatabase.getString ("OptionsInferenceDialog.optionAuto.Label"));
        JRadioButton jRadioButton2 = new JRadioButton (
                                                       stringDatabase.getString ("OptionsInferenceDialog.optionManual.Label"));
        jRadioButton2.setActionCommand (stringDatabase.getString ("OptionsInferenceDialog.optionManual.Label"));
        radioButtonsPanel.add (jRadioButton1);
        radioButtonsPanel.add (jRadioButton2);
        jRadioButton1.setSelected (true);
        buttonGroup.add (jRadioButton1);
        buttonGroup.add (jRadioButton2);
        principalPanel.add (radioButtonsPanel, BorderLayout.CENTER);
        buttonsPanel.add (okButton);
        buttonsPanel.add (cancelButton);
        principalPanel.add (buttonsPanel, BorderLayout.SOUTH);
        InferenceOptionsDialogListener optionsInferenceDialogListener = new InferenceOptionsDialogListener (
                                                                                                            this,
                                                                                                            editorPanel,
                                                                                                            inferenceToolBar);
        okButton.addActionListener (optionsInferenceDialogListener);
        cancelButton.addActionListener (optionsInferenceDialogListener);
        pack ();
        setMinimumSize (new Dimension (300, getHeight ()));
        setModal (true);
    }

    /**
     * This method returns the button group contained by this dialog.
     * @return the button group contained by this dialog.
     */
    public ButtonGroup getButtonGroup ()
    {
        return buttonGroup;
    }
}
