/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.openmarkov.core.gui.graphic.FSVariableBox;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.model.network.Finding;

/**
 * Dialog box to add a finding in a node. The result of using this class is
 * equivalent to a double-click on a node's state when the working mode is
 * 'Inference mode'
 * @author asaez
 * @version 1.0
 */
public class NodeAddFindingDialog extends JDialog
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 5618641549380924577L;
    /**
     * Object where the finding will be set.
     */
    protected VisualNode      visualNode       = null;
    /**
     * Button group that holds the radio buttons that will be shown. There is a
     * radio button for each state of the node.
     */
    ButtonGroup               buttonGroup      = new ButtonGroup ();

    /**
     * This method initialises this instance.
     * @param owner window that owns this dialog.
     * @param visualNode the node to which this dialog is associated.
     * @param finding
     * @param g the graphics context in which to paint.
     * @param editorPanel the editor panel that called this dialog.
     */
    public NodeAddFindingDialog (Window owner,
                                 VisualNode visualNode,
                                 Finding finding,
                                 Graphics2D g,
                                 EditorPanel editorPanel)
    {
        this.visualNode = visualNode;
        JPanel principalPanel = new JPanel ();
        JPanel textPanel = new JPanel ();
        JPanel radioButtonsPanel = new JPanel ();
        JPanel buttonsPanel = new JPanel ();
        JButton okButton = new JButton (
                                        StringDatabase.getUniqueInstance ().getString ("NodeAddFindingDialog.jButtonOK.Label"));
        JButton cancelButton = new JButton (
                                            StringDatabase.getUniqueInstance ().getString ("NodeAddFindingDialog.jButtonCancel.Label"));
        if (visualNode.getInnerBox () instanceof FSVariableBox)
        {
            setTitle (StringDatabase.getUniqueInstance ().getString ("NodeAddFindingDialog.Title.Label"));
            this.getContentPane ().setLayout (new BorderLayout ());
            this.getContentPane ().add (principalPanel, BorderLayout.CENTER);
            principalPanel.setLayout (new BorderLayout ());
            textPanel.setLayout (new GridLayout (3, 1));
            textPanel.add (new JLabel (""));
            textPanel.add (new JLabel (visualNode.getProbNode ().getName (), SwingConstants.CENTER));
            textPanel.add (new JLabel (""));
            principalPanel.add (textPanel, BorderLayout.NORTH);
            FSVariableBox innerBox = (FSVariableBox) visualNode.getInnerBox ();
            int numStates = innerBox.getNumStates ();
            radioButtonsPanel.setLayout (new GridLayout (numStates, 1));
            for (int i = (numStates - 1); i >= 0; i--)
            {
                String stateName = innerBox.getVisualState (i).getStateName ();
                JRadioButton jRadioButton = new JRadioButton (stateName);
                if (finding != null)
                {
                    jRadioButton.setSelected (finding.getState ().equals (stateName));
                }
                radioButtonsPanel.add (jRadioButton);
                jRadioButton.setActionCommand (stateName);
                if (i == 0)
                {
                    jRadioButton.setSelected (true);
                }
                buttonGroup.add (jRadioButton);
            }
            principalPanel.add (radioButtonsPanel, BorderLayout.CENTER);
            buttonsPanel.add (okButton);
            buttonsPanel.add (cancelButton);
            principalPanel.add (buttonsPanel, BorderLayout.SOUTH);
            NodeAddFindingDialogListener nodeAddFindingDialogListener = new NodeAddFindingDialogListener (
                                                                                                          this,
                                                                                                          editorPanel);
            okButton.addActionListener (nodeAddFindingDialogListener);
            cancelButton.addActionListener (nodeAddFindingDialogListener);
            pack ();
            setMinimumSize (new Dimension (260, getHeight ()));
            int posX = owner.getX () + (owner.getWidth () - this.getWidth ()) / 2;
            int posY = owner.getY () + (owner.getHeight () - this.getHeight ()) / 2;
            this.setLocation (posX, posY);
            setModal (true);
        }
    }

    /**
     * This method returns the visual node to which this dialog is associated.
     * @return the visual node to which this dialog is associated.
     */
    public VisualNode getVisualNode ()
    {
        return visualNode;
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
