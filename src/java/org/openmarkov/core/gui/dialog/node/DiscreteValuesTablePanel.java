/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.gui.dialog.common.PrefixedKeyTablePanel;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.ProbNode;

/**
 * Panel to set the values of a node.
 * @author jlgozalo
 * @version 1.0 jlgozalo
 */
public class DiscreteValuesTablePanel extends JPanel
    implements
        ItemListener
{
    /**
     * serial uid
     */
    private static final long     serialVersionUID                      = 1047978130482205148L;
    /**
     * String database
     */
    protected StringDatabase      stringDatabase                        = StringDatabase.getUniqueInstance ();
    /**
     * Object where all information will be saved.
     */
    private ProbNode              nodeProperties                        = null;
    /**
     * Specifies if the node whose additionalProperties are edited is new.
     */
    private boolean               newNode                               = false;
    /**
     * label for the values comboBox for the states of the node
     */
    private JLabel                jLabelStatesValues;
    /**
     * combo box to select the values for the states of the node
     */
    private JComboBox<String>             jComboBoxStatesValues;
    /**
     * label for the table to show the values of the node
     */
    private JLabel                jLabelValuesPanel                     = null;
    /**
     * table to show the other additionalProperties
     */
    private PrefixedKeyTablePanel prefixedKeyTablePanelNodeStatesValues = null;
    /**
     * The Node Values Comment Label
     */
    private JTextArea             jTextAreaLabelNodeValuesComment;
    /**
     * The Node Values Comment Scroll Panel box
     */
    // private CommentHTMLScrollPane commentHTMLScrollPaneNodeValuesComment =
    // null;
    /*
     * boolean to set if the panel has valid data or not
     */
    private boolean               validDataInPanel                      = true;

    /**
     * constructor without construction parameters
     */
    public DiscreteValuesTablePanel ()
    {
        this (true);// ,new ElementObservable());
    }

    /**
     * constructor
     */
    /*
     * public DiscreteValuesTablePanel(ElementObservable notifier) {
     * this(true,notifier); }
     */
    /**
     * This method initialises this instance.
     * @param newNode true if the node is a new node; otherwise false
     */
    public DiscreteValuesTablePanel (final boolean newNode)
    {// , ElementObservable notifier) {
        setName ("DiscreteValuesTablePanel");
        this.newNode = newNode;
        // this.notifier = notifier;
        try
        {
            initialize ();
        }
        catch (Throwable e)
        {
            e.printStackTrace ();
            JOptionPane.showMessageDialog (this, stringDatabase.getString (e.getMessage ()),
                                           stringDatabase.getString (e.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @return true if the Panel has valid data (verified); false otherwise
     */
    public boolean isValidDataInPanel ()
    {
        return validDataInPanel;
    }

    /**
     * @param validDataInPanel to be set
     */
    public void setValidDataInPanel (boolean validDataInPanel)
    {
        this.validDataInPanel = validDataInPanel;
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
     * <p>
     * <code>Initialize</code>
     * <p>
     * initialize the layout for this panel
     */
    private void initialize ()
        throws Exception
    {
        setPreferredSize (new Dimension (700, 375));
        final GroupLayout groupLayout = new GroupLayout ((JComponent) this);
        groupLayout.setHorizontalGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.TRAILING).addGroup (groupLayout.createSequentialGroup ().addGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.TRAILING,
                                                                                                                                                                                                                                                                                                                                               false).addComponent (getJLabelStatesValues (),
                                                                                                                                                                                                                                                                                                                                                                    GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                                                                                                                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                    Short.MAX_VALUE).addComponent (getJLabelValuesPanel (),
                                                                                                                                                                                                                                                                                                                                                                                                   GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                                                                                                                                                                                                                   GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                   54,
                                                                                                                                                                                                                                                                                                                                                                                                   Short.MAX_VALUE)).addPreferredGap (LayoutStyle.ComponentPlacement.RELATED).addGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addComponent (getJComboBoxStatesValues (),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     0,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     431,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     Short.MAX_VALUE).addGap (179,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              179,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              179)).addComponent (getNodeValuesTablePanel (),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  610,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  Short.MAX_VALUE))))));
        groupLayout.setVerticalGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.BASELINE).addComponent (getJLabelStatesValues ()).addComponent (getJComboBoxStatesValues (),
                                                                                                                                                                                                                                                                                                         GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                         23,
                                                                                                                                                                                                                                                                                                         GroupLayout.PREFERRED_SIZE)).addPreferredGap (LayoutStyle.ComponentPlacement.RELATED).addGroup (groupLayout.createParallelGroup (GroupLayout.Alignment.LEADING).addComponent (getJLabelValuesPanel ()).addComponent (getNodeValuesTablePanel (),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              208,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              GroupLayout.PREFERRED_SIZE)).addPreferredGap (LayoutStyle.ComponentPlacement.RELATED).addContainerGap (26,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     Short.MAX_VALUE)));
        setLayout (groupLayout);
    }

    /**
     * @return
     */
    protected JLabel getJLabelStatesValues ()
    {
        if (jLabelStatesValues == null)
        {
            jLabelStatesValues = new JLabel ();
            jLabelStatesValues.setName ("jLabelStatesValues");
            jLabelStatesValues.setText ("a Label");
            jLabelStatesValues.setText (stringDatabase.getString ("DiscreteValuesTablePanel.jLabelStatesValues.Text"));
        }
        return jLabelStatesValues;
    }

    /**
     * This method initialises NodeValuesTable.
     * @return the PrefixedKeyTablePanel for the Node Values
     */
    protected PrefixedKeyTablePanel getNodeValuesTablePanel ()
    {
        if (prefixedKeyTablePanelNodeStatesValues == null)
        {
            String[] columnNames = {
                    stringDatabase.getString ("DiscreteValuesTablePanel.ValuesTable.Columns.Name.Text"),
                    stringDatabase.getString ("DiscreteValuesTablePanel.ValuesTable.Columns.Value.Text")};
            prefixedKeyTablePanelNodeStatesValues = new PrefixedKeyTablePanel (
                                                                               columnNames,
                                                                               new Object[][] {},
                                                                               stringDatabase.getString ("DiscreteValuesTablePanel.ValuesTable.Columns.Id.Prefix"),
                                                                               true);// ,
                                                                                     // notifier);
        }
        prefixedKeyTablePanelNodeStatesValues.setBorder (new EmptyBorder (0, 0, 0, 0));
        return prefixedKeyTablePanelNodeStatesValues;
    }

    /**
     * @return JComboBox for the set of values a state can take
     */
    protected JComboBox<String> getJComboBoxStatesValues ()
    {
        if (jComboBoxStatesValues == null)
        {
            jComboBoxStatesValues = new JComboBox<String> (GUIDefaultStates.getListStrings ());
            jComboBoxStatesValues.setName ("jComboBoxStatesValues");
            jComboBoxStatesValues.addItemListener (this);
        }
        return jComboBoxStatesValues;
    }

    /**
     * @return JLabel for the states panel
     */
    protected JLabel getJLabelValuesPanel ()
    {
        if (jLabelValuesPanel == null)
        {
            jLabelValuesPanel = new JLabel ();
            jLabelValuesPanel.setName ("jLabelValuesPanel");
            jLabelValuesPanel.setText ("a Label");
            jLabelValuesPanel.setText (stringDatabase.getString ("DiscreteValuesTablePanel.jLabelValuesPanel.Text"));
        }
        return jLabelValuesPanel;
    }

    /**
     * This method initialises jLabelNodeValuesComment
     * @return a new label for the comment
     */
    protected JTextArea getJTextAreaLabelNodeValuesComment ()
    {
        if (jTextAreaLabelNodeValuesComment == null)
        {
            jTextAreaLabelNodeValuesComment = new JTextArea ();
            jTextAreaLabelNodeValuesComment.setLineWrap (true);
            jTextAreaLabelNodeValuesComment.setOpaque (false);
            jTextAreaLabelNodeValuesComment.setName ("jTextAreaLabelNetworkValuesComment");
            jTextAreaLabelNodeValuesComment.setFocusable (false);
            jTextAreaLabelNodeValuesComment.setEditable (false);
            jTextAreaLabelNodeValuesComment.setFont (getJLabelStatesValues ().getFont ());
            jTextAreaLabelNodeValuesComment.setText ("an Extended Label");
            jTextAreaLabelNodeValuesComment.setText (stringDatabase.getString ("DiscreteValuesTablePanel.jTextAreaLabelNodeValuesComment.Text"));
        }
        return jTextAreaLabelNodeValuesComment;
    }

    /**
     * Invoked when an item has been selected.
     * @param e event information.
     */
    public void itemStateChanged (ItemEvent e)
    {
        subItemStateChanged (e);
    }

    /**
     * Invoked when an item of the type of states of the node has been selected.
     * @param e event information.
     */
    protected void subItemStateChanged (ItemEvent e)
    {
        String[] states;
        if (e.getItemSelectable ().equals (jComboBoxStatesValues))
        {
            if (e.getStateChange () == ItemEvent.SELECTED)
            {
                states = DefaultStates.getByIndex (jComboBoxStatesValues.getSelectedIndex ());
                translateStates (states);
                prefixedKeyTablePanelNodeStatesValues.setData (convertStringsToTableFormat (states));
            }
        }
    };

    /**
     * Translate an array of states into their depending-language strings.
     * @param states states to translate.
     */
    protected void translateStates (String[] states)
    {
        int i, l = states.length;
        for (i = 0; i < l; i++)
        {
            states[i] = GUIDefaultStates.getString (states[i]);
        }
    }

    /**
     * Convert an array of strings in an array of arrays of objects with the
     * same elements.
     * @param values array of strings.
     * @return an array of arrays of objects that has the same elements.
     */
    protected Object[][] convertStringsToTableFormat (String[] values)
    {
        Object[][] data;
        int i, l;
        l = values.length;
        data = new Object[l][1];
        for (i = 0; i < l; i++)
        {
            data[i][0] = values[i];
        }
        return data;
    }

    /**
     * Convert an array of arrays of objects in an array of strings with the
     * same elements.
     * @param values array of arrays of objects.
     * @return array of strings that has the same elements.
     */
    protected String[] convertTableFormatToStrings (Object[][] values)
    {
        String[] data;
        int i, l;
        l = values.length;
        data = new String[l];
        for (i = 0; i < l; i++)
        {
            data[i] = (String) values[i][0];
        }
        return data;
    }

    /**
     * This method checks the states table, ensuring that there aren't
     * duplicated states and empty states.
     * @return true if all the states are defined and appears only once.
     */
    public boolean checkStates ()
    {
        Object[][] data;
        int i, l;
        HashSet<Object> statesSet = new HashSet<Object> ();
        prefixedKeyTablePanelNodeStatesValues.stopCellEditing ();
        data = prefixedKeyTablePanelNodeStatesValues.getData ();
        i = 0;
        l = data.length;
        if (l == 0)
        {
            JOptionPane.showMessageDialog (this,
                                           stringDatabase.getString ("EmptyStateList.Text.Label"),
                                           stringDatabase.getString ("EmptyStateList.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
            this.setValidDataInPanel (false);
            return false;
        }
        while (this.isValidDataInPanel () && (i < l))
        {
            if ((data[i][0] == null) || data[i][0].equals (""))
            {
                JOptionPane.showMessageDialog (this,
                                               stringDatabase.getString ("EmptyState.Text.Label"),
                                               stringDatabase.getString ("EmptyState.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
                this.setValidDataInPanel (false);
                return false;
            }
            else if (!statesSet.add (data[i][0]))
            {
                JOptionPane.showMessageDialog (this,
                                               stringDatabase.getString ("DuplicatedState.Text.Label"),
                                               stringDatabase.getString ("DuplicatedState.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
                this.setValidDataInPanel (false);
                return false;
            }
            i++;
        }
        this.setValidDataInPanel (true);
        return true;
    }
}
