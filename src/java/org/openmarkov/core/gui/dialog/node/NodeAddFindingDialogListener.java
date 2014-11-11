/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.openmarkov.core.gui.graphic.FSVariableBox;
import org.openmarkov.core.gui.graphic.InnerBox;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.graphic.VisualState;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.window.edition.EditorPanel;

/**
 * Listener associated to NodeAddFindingDialog.
 * @author asaez
 * @version 1.0
 */
public class NodeAddFindingDialogListener
    implements
        ActionListener
{
    /**
     * The Dialog to which this listener is associated
     */
    NodeAddFindingDialog nodeAddFindingDialog = null;
    /**
     * The editor panel that called the associated dialog.
     */
    EditorPanel          editorPanel          = null;

    /**
     * constructor
     */
    public NodeAddFindingDialogListener (NodeAddFindingDialog nodeAddFindingDialog,
                                         EditorPanel editorPanel)
    {
        this.nodeAddFindingDialog = nodeAddFindingDialog;
        this.editorPanel = editorPanel;
    }

    /**
     * Invoked when an action occurs.
     * @param actionEvent event information.
     */
    public void actionPerformed (ActionEvent actionEvent)
    {
        String command = actionEvent.getActionCommand ();
        String selectedState = nodeAddFindingDialog.getButtonGroup ().getSelection ().getActionCommand ();
        if (command.equals (StringDatabase.getUniqueInstance ().getString ("NodeAddFindingDialog.jButtonOK.Label")))
        {
            VisualNode visualNode = nodeAddFindingDialog.getVisualNode ();
            InnerBox innerBox = visualNode.getInnerBox ();
            if (innerBox instanceof FSVariableBox)
            {
                FSVariableBox fsVariableBox = (FSVariableBox) innerBox;
                VisualState visualState = fsVariableBox.getVisualState (selectedState);
                editorPanel.setNewFinding (visualNode, visualState);
            }
        }
        else if (command.equals (StringDatabase.getUniqueInstance ().getString ("NodeAddFindingDialog.jButtonCancel.Label")))
        {
            // do nothing
        }
        nodeAddFindingDialog.setVisible (false);
    }
}
