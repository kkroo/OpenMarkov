/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.network;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import org.openmarkov.core.gui.dialog.common.PrefixedOtherPropertiesTablePanel;
import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * Panel to set other additionalProperties in the network not managed by
 * OpenMarkov directly. It will have a scroll table with two visible columns and
 * a third column to store the object type. Also, a "+" and "-" buttons to
 * manage the insert and delete additionalProperties in the right side and the
 * "Accept" and "Cancel" buttons on the bottom, and a HTML comment text field
 * @author jlgozalo
 * @version 1.0 jlgozalo initial
 */
public class NetworkOtherPropertiesPanel extends JPanel
{
    /**
     * 
     */
    private static final long                 serialVersionUID           = 2760077067652885395L;
    /**
     * label for the table to show the other additionalProperties
     */
    private JLabel                            jLabelOtherPropertiesTable = null;
    /**
     * table to show the other additionalProperties
     */
    private PrefixedOtherPropertiesTablePanel otherPropertiesTablePanel  = null;

    private StringDatabase stringDatabase = StringDatabase.getUniqueInstance (); 
    
    /**
     * constructor without construction parameters
     */
    public NetworkOtherPropertiesPanel ()
    {
        this (true);
    }

    /**
     * This method initialises this instance.
     * @param newNetwork is true if the network is first created
     */
    public NetworkOtherPropertiesPanel (final boolean newNetwork)
    {
        this.setName ("NetworkOtherPropertiesPanel");
        initialize ();
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
        final GroupLayout groupLayout = new GroupLayout ((JComponent) this);
        groupLayout.setHorizontalGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addComponent (getJLabelOtherPropertiesTable (),
                                                                                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                        Short.MAX_VALUE).addPreferredGap (LayoutStyle.ComponentPlacement.RELATED).addComponent (getOtherPropertiesTablePanel (),
                                                                                                                                                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                Short.MAX_VALUE).addContainerGap ()));
        groupLayout.setVerticalGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.TRAILING,
                                                                                                                                                                                                                   false).addComponent (getJLabelOtherPropertiesTable (),
                                                                                                                                                                                                                                        GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                        Short.MAX_VALUE).addComponent (getOtherPropertiesTablePanel (),
                                                                                                                                                                                                                                                                       GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                                                                                       GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                       274,
                                                                                                                                                                                                                                                                       Short.MAX_VALUE)).addContainerGap (GroupLayout.DEFAULT_SIZE,
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
            jLabelOtherPropertiesTable.setText (stringDatabase.getString ("NetworkOtherPropertiesPanel.jLabelOtherPropertiesTable.Text"));
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
                    stringDatabase.getString ("NetworkOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyIdColumn.Label"),
                    stringDatabase.getString ("NetworkOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyNameColumn.Label"),
                    stringDatabase.getString ("NetworkOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyValueColumn.Label"),
            // dialogStringResource
            // .getString("NetworkOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyTypeColumn.Label")
            };
            otherPropertiesTablePanel = new PrefixedOtherPropertiesTablePanel (
                                                                               columnNames,
                                                                               new Object[][] {},
                                                                               stringDatabase.getString ("NetworkOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyIdColumn.Prefix"),
                                                                               true);// ,
                                                                                     // notifier);
                                                                                     // //
                                                                                     // true
                                                                                     // =>
                                                                                     // not
                                                                                     // show
                                                                                     // the
                                                                                     // id
                                                                                     // column
        }
        return otherPropertiesTablePanel;
    }
}
