/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import org.openmarkov.core.gui.dialog.common.PrefixedOtherPropertiesTablePanel;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.ProbNode;

/**
 * Panel to set other additionalProperties in the node not managed by OpenMarkov
 * directly. It will have a scroll table with two visible columns and a third
 * column to store the object type. Also, a "+" and "-" buttons to manage the
 * insert and delete additionalProperties in the right side and the "Accept" and
 * "Cancel" buttons on the bottom, and a HTML comment text field
 * @author jlgozalo
 * @version 1.0 jlgozalo initial
 */
public class NodeOtherPropsTablePanel extends JPanel
    implements
        ItemListener
{
    /**
     * serial uid
     */
    private static final long                 serialVersionUID           = 1047978130482205148L;
    /**
     * label for the table to show the other additionalProperties
     */
    private JLabel                            jLabelOtherPropertiesTable = null;
    /**
     * table to show the other additionalProperties
     */
    private PrefixedOtherPropertiesTablePanel otherPropertiesTablePanel  = null;
    /**
     * Object where all information will be saved.
     */
    private ProbNode                          nodeProperties             = null;
    /**
     * Specifies if the node whose additionalProperties are edited is new.
     */
    private boolean                           newNode                    = false;
    /**
     * String database
     */
    protected StringDatabase                  stringDatabase             = StringDatabase.getUniqueInstance ();

    /**
     * constructor without construction parameters
     */
    public NodeOtherPropsTablePanel ()
    {
        this (true);
    }

    /**
     * This method initialises this instance.
     * @param newNode true if the node is a new node; otherwise false
     */
    public NodeOtherPropsTablePanel (final boolean newNode)
    {
        this.newNode = newNode;
        setName ("NodeOtherPropsTablePanel");
        try
        {
            initialize ();
        }
        catch (Throwable e)
        {
            e.printStackTrace ();
            JOptionPane.showMessageDialog (null, stringDatabase.getString (e.getMessage ()),
                                           stringDatabase.getString (e.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get the node Properties in this panel
     * @return the nodeProperties
     */
    public ProbNode getNodeProperties ()
    {
        return nodeProperties;
    }

    /**
     * Set the node additionalProperties in this panel with the provided ones
     * @param nodeProperties the nodeProperties to set
     */
    public void setNodeProperties (final ProbNode nodeProperties)
    {
        this.nodeProperties = nodeProperties;
    }

    /**
     * @return the newNode
     */
    public boolean isNewNode ()
    {
        return newNode;
    }

    /**
     * @param newNode the newNode to set
     */
    public void setNewNode (boolean newNode)
    {
        this.newNode = newNode;
    }

    /**
     * init the layout for this panel. Firstly, the horizontal layout will be
     * set with one row (parallel group) in the first row the table with
     * additionalProperties and the Add/Delete buttons and to force the buttons
     * to be aligned, we will do: - sequential group (gap, table, gap,
     * buttonsParalellGroup) Then, the vertical layout will be set with the two
     * columns (parallel group) in the first column, the table in the second
     * column, the Add/Delete buttons
     */
    private void initialize ()
    {
        setPreferredSize (new Dimension (700, 375));
        final GroupLayout groupLayout = new GroupLayout ((JComponent) this);
        groupLayout.setHorizontalGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addComponent (getJLabelOtherPropertiesTable (),
                                                                                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                        Short.MAX_VALUE).addPreferredGap (LayoutStyle.ComponentPlacement.RELATED).addComponent (getOtherPropertiesTablePanel (),
                                                                                                                                                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                GroupLayout.PREFERRED_SIZE).addGap (61,
                                                                                                                                                                                                                                                                                                                    61,
                                                                                                                                                                                                                                                                                                                    61)));
        groupLayout.setVerticalGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.TRAILING).addComponent (getOtherPropertiesTablePanel (),
                                                                                                                                                                                                                                                                 GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                                                                                 GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                 274,
                                                                                                                                                                                                                                                                 Short.MAX_VALUE).addComponent (getJLabelOtherPropertiesTable (),
                                                                                                                                                                                                                                                                                                GroupLayout.Alignment.LEADING)).addContainerGap (89,
                                                                                                                                                                                                                                                                                                                                                 Short.MAX_VALUE)));
        setLayout (groupLayout);
    }

    /**
     * This method initialises jLabelOtherPropertiesTable
     * @return the JLabel for the Other Properties Table
     */
    private JLabel getJLabelOtherPropertiesTable ()
    {
        if (jLabelOtherPropertiesTable == null)
        {
            jLabelOtherPropertiesTable = new JLabel ();
            jLabelOtherPropertiesTable.setName ("jLabelOtherPropertiesTable");
            jLabelOtherPropertiesTable.setVerticalTextPosition (SwingConstants.TOP);
            jLabelOtherPropertiesTable.setVerticalAlignment (SwingConstants.TOP);
            jLabelOtherPropertiesTable.setText ("a Label :");
            jLabelOtherPropertiesTable.setText (stringDatabase.getString ("NodeOtherPropertiesPanel.jLabelOtherPropertiesTable.Text"));
        }
        return jLabelOtherPropertiesTable;
    }

    /**
     * This method initialises otherPropertiesTable.
     * @return the PrefixedKeyTablePanel for the Other additionalProperties of
     *         the network
     */
    private PrefixedOtherPropertiesTablePanel getOtherPropertiesTablePanel ()
    {
        if (otherPropertiesTablePanel == null)
        {
            String columnNames[] = new String[] {
                    stringDatabase.getString ("NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyIdColumn.Label"),
                    stringDatabase.getString ("NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyNameColumn.Label"),
                    stringDatabase.getString ("NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyValueColumn.Label"),
            // dialogStringResource
            // .getString("NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyTypeColumn.Label")
            };
            // ,
            otherPropertiesTablePanel = new PrefixedOtherPropertiesTablePanel (
                                                                               columnNames,
                                                                               new Object[][] {},
                                                                               stringDatabase.getString ("NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyIdColumn.Prefix"),
                                                                               true);
        }
        return otherPropertiesTablePanel;
    }

    /**
     * Invoked when an item has been selected.
     * @param e event information.
     */
    public void itemStateChanged (ItemEvent e)
    {
    }

    /**
     * This method fills the content of the fields from a NodeProperties object.
     * @param additionalProperties object from where load the information.
     */
    public void setFieldsFromProperties (ProbNode properties)
    {
        // getOtherPropertiesTablePanel()
        // .setData( additionalProperties.getOtherProperties() );
    }
}
