
package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * @author maryebra
 */
@SuppressWarnings("serial")
public class StandardDomainPanel extends JPanel
    implements
        ItemListener
{
    /**
     * String database
     */
    protected StringDatabase        stringDatabase = StringDatabase.getUniqueInstance ();
    private ButtonGroup             buttonGroup    = new ButtonGroup ();
    private ArrayList<JRadioButton> radioButtons   = new ArrayList<JRadioButton> ();

    public StandardDomainPanel ()
    {
        initialize ();
        repaint ();
    }

    public void initialize ()
    {
        setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
        // ButtonGroup buttonGroup = new ButtonGroup();
        String[] defaultStates = {stringDatabase.getString ("defaultStates.absentPresent.Text"),
                stringDatabase.getString ("defaultStates.noYes.Text"),
                stringDatabase.getString ("defaultStates.negativePositive.Text"),
                stringDatabase.getString ("defaultStates.absentMildModerateSevere.Text"),
                stringDatabase.getString ("defaultStates.lowMediumhigh.Text")};
        // String [] defaultStates = GUIDefaultStates.getListStrings();
        // String [] defaultStates2 =
        // GUIDefaultStates.getStringsLanguageDependent(GUIDefaultStates.getListStrings());
        for (String defaultState : defaultStates)
        {
            JRadioButton radioButton = new JRadioButton (defaultState);
            // radioButton.addItemListener(this);
            radioButtons.add (radioButton);
            buttonGroup.add (radioButton);
            add (radioButton, BorderLayout.CENTER);
        }
    }

    @Override
    public void itemStateChanged (ItemEvent e)
    {
        //String states = ((JRadioButton) e.getItem ()).getName ();
    }

    public ButtonGroup getButtonGroup ()
    {
        return buttonGroup;
    }

    public ArrayList<JRadioButton> getRadioButtons ()
    {
        return radioButtons;
    }
}
