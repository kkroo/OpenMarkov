/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JOptionPane;

import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Util;

public class NodePropertiesDialogListenerAssistant
    implements
        ActionListener,
        ItemListener
{
    private NodePropertiesDialog dialog          = null;
    private NodeDefinitionPanel  definitionPanel = null;
    private ProbNode             properties      = null;

    /**
     * constructor
     */
    public NodePropertiesDialogListenerAssistant (NodePropertiesDialog dialog)
    {
        this.dialog = dialog;
        definitionPanel = (NodeDefinitionPanel) dialog.getNodeDefinitionPanel ();
        properties = dialog.getNodeProperties ();
    }

    /**
     * @return the dialog
     */
    protected NodePropertiesDialog getDialog ()
    {
        return dialog;
    }

    /**
     * @param dialog the dialog to set
     */
    protected void setDialog (NodePropertiesDialog dialog)
    {
        this.dialog = dialog;
    }

    /**
     * Invoked when an action occurs.
     * @param e event information.
     */
    public void actionPerformed (ActionEvent e)
    {
        if (e.getSource ().equals (definitionPanel.getJTextFieldNodeName ()))
        {
            actionPerformedNodeNameChangeValue ();
        }
    }

    /**
     * Invoked when the button 'add' is pressed.
     */
    protected void actionPerformedNodeNameChangeValue ()
    {
        if (checkName ())
        {
            // announceEdit
            // if ok, change also the name of the NodePropertiesDialog
        }
        else
        {
            definitionPanel.getJTextFieldNodeName ().setText (properties.getName ());
        }
    }

    /**
     * Invoked when an item has been selected.
     * @param e event information.
     */
    public void itemStateChanged (ItemEvent e)
    {
    }

    /**
     * This method checks that the name field is filled and there isn't any node
     * with the same name.
     * @return true, if the name field isn't empty and there isn't any node with
     *         this name; otherwise, false.
     */
    public boolean checkName ()
    {
        String name = definitionPanel.getJTextFieldNodeName ().getText ();
        boolean result = true;
        if ((name == null) || name.equals (""))
        {
            JOptionPane.showMessageDialog (definitionPanel,
                                           StringDatabase.getUniqueInstance ().getString ("NodeNameEmpty.Text.Label"),
                                           StringDatabase.getUniqueInstance ().getString ("NodeNameEmpty.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
            result = false;
        }
        else if (!properties.getName ().equals (name)
                 && Util.existNode (properties.getProbNet (), name))
        {
            JOptionPane.showMessageDialog (definitionPanel,
                                           StringDatabase.getUniqueInstance ().getString ("DuplicatedNode.Text.Label"),
                                           StringDatabase.getUniqueInstance ().getString ("DuplicatedNode.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
            result = false;
        }
        if (!result)
        {
            definitionPanel.getJTextFieldNodeName ().requestFocus ();
            return false;
        }
        return true;
    }

    /**
     * This method checks that the purpose field is filled if this field is
     * enabled.
     * @return true, if the purpose field isn't empty; otherwise, false.
     */
    public boolean checkPurpose ()
    {
        return true;
    }
}
