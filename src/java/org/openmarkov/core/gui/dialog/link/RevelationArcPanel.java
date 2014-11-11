
package org.openmarkov.core.gui.dialog.link;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.gui.component.DiscretizeTablePanel;
import org.openmarkov.core.gui.component.RevelationArcDiscretizeTablePanel;
import org.openmarkov.core.gui.dialog.common.KeyTablePanel;
import org.openmarkov.core.gui.dialog.common.PrefixedKeyTablePanel;
import org.openmarkov.core.gui.dialog.common.SelectableKeyTablePanel;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.VariableType;

/*****
 * Graphic panel to display and set the revealing conditions for a link. The
 * conditions can be either states or intervals.
 * @author ckonig
 */
@SuppressWarnings("serial")
public class RevelationArcPanel extends JPanel
    implements
        ItemListener
{
    /***
     * Object where all the information will be saved
     */
    private Link                              link;
    /****
     * Variable Type of the revelation conditions
     */
    private VariableType                      variableType;
    /**
     * label for the table to show the values of the node
     */
    private JLabel                            jLabelValuesPanel = null;
    /**
     * table to show the states of the variable`s domain.
     */
    private SelectableKeyTablePanel           discreteNodeStatesTablePanel;
    /**
     * table to show intervals of the variable's domain.
     */
    private RevelationArcDiscretizeTablePanel discretizedNodeStatesTablePanel;
    /**
     * String database
     */
    protected StringDatabase                  stringDatabase    = StringDatabase.getUniqueInstance ();

    /****
     * Constructor - constructs a JPanel for displaying the revelation
     * conditions of a link
     * @param link
     */
    public RevelationArcPanel (Link link)
    {
        this.link = link;
        this.variableType = ((ProbNode) link.getNode1 ().getObject ()).getVariable ().getVariableType ();
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

    public void initialize ()
    {
        setPreferredSize (new Dimension (600, 375));
        this.setLayout (new BoxLayout (this, BoxLayout.PAGE_AXIS));
        this.add (Box.createRigidArea (new Dimension (5, 10)));
        JLabel label = this.getJLabelValuesPanel ();
        label.setAlignmentX (Component.CENTER_ALIGNMENT);
        this.add (label);
        this.add (Box.createRigidArea (new Dimension (5, 10)));
        JPanel centerPanel = new JPanel ();
        JPanel panel = this.getNodeStatesTablePanel ();
        centerPanel.add (panel);
        centerPanel.setAlignmentX (Component.CENTER_ALIGNMENT);
        this.add (centerPanel);
    }

    /**
     * @return
     */
    protected JLabel getJLabelValuesPanel ()
    {
        if (jLabelValuesPanel == null)
        {
            jLabelValuesPanel = new JLabel ();
            jLabelValuesPanel.setName ("jLabelValuesPanel");
            jLabelValuesPanel.setText ("a Label");
            ProbNode node1 = (ProbNode) link.getNode1 ().getObject ();
            ProbNode node2 = (ProbNode) link.getNode2 ().getObject ();
            MessageFormat messageForm = new MessageFormat (
                                                           stringDatabase.getString ("RevelationArcPanel.jLabelValuesPanel.Text"));
            Object[] labelArgs = new Object[] {node1.getName (), node2.getName ()};
            String msgText = messageForm.format (labelArgs);
            jLabelValuesPanel.setText (msgText);
        }
        return jLabelValuesPanel;
    }

    /**
     * This method initializes NodeValuesTable.
     * @return the PrefixedKeyTablePanel for the Node Values
     */
    protected KeyTablePanel getNodeStatesTablePanel ()
    {
        if (this.variableType != VariableType.NUMERIC)
        {
            return getNodeDiscreteStatesTablePanel ();
        }
        else
        {
            return getNodeDiscretizedStatesTablePanel ();
        }
    }

    /**
     * This method initializes NodeValuesTable.
     * @return the PrefixedKeyTablePanel for the Node Values
     */
    protected PrefixedKeyTablePanel getNodeDiscreteStatesTablePanel ()
    {
        if (discreteNodeStatesTablePanel == null)
        {
            String[] columnNames = {
                    stringDatabase.getString ("DiscreteValuesTablePanel.ValuesTable."
                                              + "Columns.Name.Text"),
                    "",
                    stringDatabase.getString ("DiscreteValuesTablePanel.ValuesTable."
                                              + "Columns.Value.Text")};
            discreteNodeStatesTablePanel = new SelectableKeyTablePanel (
                                                                        columnNames,
                                                                        new Object[][] {},
                                                                        stringDatabase.getString ("DiscreteValuesTablePanel.ValuesTable."
                                                                                                  + "Columns.Id.Prefix"),
                                                                        true, link);
        }
        discreteNodeStatesTablePanel.setBorder (new EmptyBorder (0, 0, 0, 0));
        return discreteNodeStatesTablePanel;
    }

    /**
     * This method initializes NodeValuesTable.
     * @return the DiscretizeTablePanel for the Node Values
     */
    protected DiscretizeTablePanel getNodeDiscretizedStatesTablePanel ()
    {
        if (discretizedNodeStatesTablePanel == null)
        {
            String[] columnNames = {
                    stringDatabase.getString ("DiscretizeTableModel.Columns." + "IntervalId.Text"),
                    stringDatabase.getString ("DiscretizeTableModel.Columns." + "IntervalName.Text"),
                    stringDatabase.getString ("DiscretizeTableModel.Columns."
                                              + "LowLimitSymbol.Text"),
                    stringDatabase.getString ("DiscretizeTableModel.Columns."
                                              + "LowLimitValue.Text"),
                    stringDatabase.getString ("DiscretizeTableModel.Columns."
                                              + "ValuesSeparator.Text"),
                    stringDatabase.getString ("DiscretizeTableModel.Columns."
                                              + "UpperLimitValue.Text"),
                    stringDatabase.getString ("DiscretizeTableModel.Columns."
                                              + "UpperLimitSymbol.Text")};
            discretizedNodeStatesTablePanel = new RevelationArcDiscretizeTablePanel (columnNames,
                                                                                     link);
            discretizedNodeStatesTablePanel.setBorder (new EmptyBorder (0, 0, 0, 0));
        }
        return discretizedNodeStatesTablePanel;
    }

    public void setFieldsFromProperties (Link link)
    {
        if (link != null)
        {
            if (this.variableType != VariableType.NUMERIC)
            {
                discreteNodeStatesTablePanel.setData (convertStringsToTableDiscreteFormat (link));
                discreteNodeStatesTablePanel.adjustColumnSize ();
            }
            else
            {
                discretizedNodeStatesTablePanel.setPartitionedInterval ();
            }
        }
    }

    /**
     * Convert an array of strings in an array of arrays of objects with the
     * same elements.
     * @param values array of strings.
     * @return an array of arrays of objects that has the same elements.
     */
    protected Object[][] convertStringsToTableDiscreteFormat (Link link)
    {
        ProbNode node = (ProbNode) link.getNode1 ().getObject ();
        State[] values = node.getVariable ().getStates ();
        List<State> revealingStates = link.getRevealingStates ();
        Object[][] data;
        int i, l;
        l = values.length;
        data = new Object[l][2];
        i = l - 1;
        for (State value : values)
        {
            data[i][0] = revealingStates.contains (value) ? true : false;
            data[i--][1] = value.getName ();
        }
        return data;
    }

    public void itemStateChanged (ItemEvent e)
    {
    }

    public void saveChanges ()
    {
    }
}
