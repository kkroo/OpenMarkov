
package org.openmarkov.core.gui.dialog.link;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.text.MessageFormat;

import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNode;

/**
 * This class implements the revelation arc dialog box for the edition of the
 * revelation arc properties.
 */
@SuppressWarnings("serial")
public class RevelationArcEditDialog extends OkCancelApplyUndoRedoHorizontalDialog
{
    private Link               link;
    /***
     * Jpanel showing the values table of the first node
     */
    private RevelationArcPanel revelationArcPanel;

    public RevelationArcEditDialog (Window owner, Link link)
    {
        super (owner);
        this.link = link;
        ((ProbNode) link.getNode1 ().getObject ()).getProbNet ().getPNESupport ().openParenthesis ();
        initialize ();
        setLocationRelativeTo (owner);
        setMinimumSize (new Dimension (750, 450));
        setResizable (true);
    }

    /**
     * This method configures the dialog box.
     */
    private void initialize ()
    {
        ProbNode node2 = (ProbNode) link.getNode2 ().getObject ();
        String title = "";
        if (link != null)
        {
            MessageFormat messageForm = new MessageFormat (
                                                           stringDatabase.getString ("RevelationArcDialog.Title.Label"));
            Object[] labelArgs = new Object[] {node2.getName ()};
            title = messageForm.format (labelArgs);
        }
        setTitle (title);
        configureComponentsPanel ();
        pack ();
    }

    /**
     * Sets up the panel where all components, except the buttons of the buttons
     * panel, will be appear.
     */
    private void configureComponentsPanel ()
    {
        getComponentsPanel ().setLayout (new BorderLayout (5, 5));
        getComponentsPanel ().add (getRevelationArcPanel (), BorderLayout.CENTER);
    }

    private RevelationArcPanel getRevelationArcPanel ()
    {
        if (this.revelationArcPanel == null)
        {
            this.revelationArcPanel = new RevelationArcPanel (link);
        }
        return revelationArcPanel;
    }

    /**
     * @return An integer indicating the button clicked by the user when closing
     *         this dialog
     */
    public int requestValues ()
    {
        revelationArcPanel.setFieldsFromProperties (link);
        setVisible (true);
        return selectedButton;
    }

    /**
     * This method carries out the actions when the user presses the OK button
     * before hiding the dialog.
     * @return true if all the fields are correct.
     */
    @Override
    protected boolean doOkClickBeforeHide ()
    {
        getRevelationArcPanel ().saveChanges ();
        ((ProbNode) link.getNode1 ().getObject ()).getProbNet ().getPNESupport ().closeParenthesis ();
        return true;
    }

    @Override
    protected void doCancelClickBeforeHide ()
    {
        ((ProbNode) link.getNode1 ().getObject ()).getProbNet ().getPNESupport ().closeParenthesis ();
    }
}
