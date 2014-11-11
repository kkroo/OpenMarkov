/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JOptionPane;

import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.gui.dialog.common.PotentialPanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanelPlugin;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "Tree/ADD")
public class TreeADDPanel extends PotentialPanel
{
    /**
     * The builder object of Tree - ADDs
     */
    private TreeADDEditorPanel treeADDController;
    /**
     * The node edited
     */
    private ProbNode           probNode;

    public TreeADDPanel (ProbNode probNode)
    {
        super ();
        setData (probNode);
    }

    public boolean saveChanges ()
    {
        SetPotentialEdit setPotentialEdit = new SetPotentialEdit (
                                                                  probNode,
                                                                  treeADDController.getTreePotential ());
        try
        {
            probNode.getProbNet ().doEdit (setPotentialEdit);
        }
        catch (ConstraintViolationException e1)
        {
            JOptionPane.showMessageDialog (this,
                                           e1.getMessage (),
                                           StringDatabase.getUniqueInstance ().getString ("ConstraintViolationException"),
                                           JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
        return true;
    }

    @Override
    public void setData (ProbNode probNode)
    {
        setLayout (new BorderLayout ());
        this.probNode = probNode;
        treeADDController = new TreeADDEditorPanel (new TreeADDCellRenderer (probNode.getProbNet ()),
                                                    probNode);
        removeAll ();
        // treeADDController.setMaximumSize(new Dimension(10, 10));
        // treeADDController.setPreferredSize(new Dimension(10, 10));
        add (treeADDController, BorderLayout.CENTER);
        setName ("nodeTreeADDPotentialPanel");
        setBackground (Color.blue);
        // nodeADDPotentialPanel.setNewNode(newNode);
        // nodeADDPotentialPanel.setNodeProperties(probNode);
    }

    @Override
    public void close ()
    {
        // TODO Auto-generated method stub
    }
}
