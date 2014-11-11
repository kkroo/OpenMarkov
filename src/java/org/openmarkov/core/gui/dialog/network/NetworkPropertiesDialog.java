/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.network;

import java.awt.Window;

import javax.help.BadIDException;
import javax.swing.JTabbedPane;

import org.openmarkov.core.gui.dialog.HelpViewer;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.util.PropertyNames;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.core.oopn.OOPNet;

/**
 * Dialog box to set the options of a network.
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.2 jlgozalo new Group layout and semantic errors fixed
 */
public class NetworkPropertiesDialog extends OkCancelHorizontalDialog
    implements
        PropertyNames
{
    private static final long           serialVersionUID            = -8734100506781534551L;
    private ProbNet                     probNet                     = null;
    /**
     * Panel to tab the different options.
     */
    private JTabbedPane                 tabbedPane                  = null;
    /**
     * Panel that contains the panel where definition fields are. It is used to
     * place the fields at the top of the panel.
     */
    private NetworkDefinitionPanel      networkDefinitionPanel      = null;
    /**
     * Panel that contains the panel where variables definition fields are. It
     * is used to place the fields at the top of the panel.
     */
    private NetworkVariablesPanel       networkVariablesPanel       = null;
    /**
     * Panel that contains the panel where a set of other additionalProperties
     * are. It is used to place the fields at the top of the panel.
     */
    private NetworkOtherPropertiesPanel networkOtherPropertiesPanel = null;
    /**
     * Advanced panel containing agents and decision criteria
     */
    private NetworkAdvancedPanel        networkAdvancedPanel;
    /**
     * Specifies if the network whose additionalProperties are edited is new.
     */
    private boolean                     newNetwork                  = false;
    /**
     * String database
     */
    protected StringDatabase            stringDatabase              = StringDatabase.getUniqueInstance ();

    /**
     * This method initializes this instance.
     * @param owner window that owns the dialog.
     * @param newElement if true, it indicates that a new network is being
     *            created; if false, an existing network is being modified.
     * @wbp.parser.constructor
     */
    public NetworkPropertiesDialog (Window owner)
    {
        super (owner);
        newNetwork = true;
        initialize ();
        setName ("NetworkPropertiesDialog");
        setLocationRelativeTo (owner);
    }

    /**
     * This method initializes this instance.
     * @param owner window that owns the dialog.
     * @param newElement if true, it indicates that a new network is being
     *            created; if false, an existing network is being modified.
     */
    public NetworkPropertiesDialog (Window owner, ProbNet probNet)
    {
        super (owner);
        if (probNet != null)
        {
            probNet.getPNESupport ().setWithUndo (true);
            probNet.getPNESupport ().openParenthesis ();
            this.probNet = probNet;
            newNetwork = false;
            initialize ();
            setName ("NetworkPropertiesDialog");
            setLocationRelativeTo (owner);
        }
        // SsetOnlineHelp("Network Properties Dialog");
    }

    /**
     * This method configures the dialog box.
     */
    private void initialize ()
    {
        String title = stringDatabase.getString ("NetworkPropertiesDialog.Title.Label");
        if (probNet != null)
        {
            title += ": " + probNet.getName ();
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
        getComponentsPanel ().add (getTabbedPane ());
    }

    /**
     * This method initialises tabbedPane.
     * @return a new tabbed pane.
     */
    private JTabbedPane getTabbedPane ()
    {
        if (tabbedPane == null)
        {
            tabbedPane = new JTabbedPane ();
            tabbedPane.addTab ("", null, getNetworkDefinitionPanel (), null);
            tabbedPane.setTitleAt (0,
                                   stringDatabase.getString ("NetworkPropertiesDialog.DefinitionTab.Label"));
            tabbedPane.addTab (stringDatabase.getString ("NetworkPropertiesDialog.VariablesTab.Label"),
                               null, getNetworkVariablesPanel (), null);
            if (!newNetwork)
            {
                tabbedPane.addTab (stringDatabase.getString ("NetworkPropertiesDialog.Advanced.Label"),
                                   null, getNetworkAdvancedPanel (), null);
                tabbedPane.addTab (stringDatabase.getString ("NetworkPropertiesDialog.OtherPropertiesTab.Label"),
                                   null, getNetworkOtherPropertiesPanel (), null);
            }
            tabbedPane.setName ("tabbedPane");
        }
        return tabbedPane;
    }

    /**
     * This method initialises networkAdvancedPanel.
     * @return a new definition panel.
     */
    NetworkAdvancedPanel getNetworkAdvancedPanel ()
    {
        if (networkAdvancedPanel == null)
        {
            networkAdvancedPanel = new NetworkAdvancedPanel (newNetwork, probNet);
            networkAdvancedPanel.setName ("networkAdvancedPanel");
        }
        return networkAdvancedPanel;
    }

    /**
     * This method initialises networkDefinitionPanel.
     * @return a new definition panel.
     */
    private NetworkDefinitionPanel getNetworkDefinitionPanel ()
    {
        if (networkDefinitionPanel == null)
        {
            networkDefinitionPanel = new NetworkDefinitionPanel (this, probNet);
            networkDefinitionPanel.setName ("networkDefinitionPanel");
        }
        return networkDefinitionPanel;
    }

    /**
     * This method initialises networkVariablesPanel.
     * @return a new variables definition panel.
     */
    private NetworkVariablesPanel getNetworkVariablesPanel ()
    {
        if (networkVariablesPanel == null)
        {
            networkVariablesPanel = new NetworkVariablesPanel (probNet);
            networkVariablesPanel.setName ("networkVariablesPanel");
        }
        return networkVariablesPanel;
    }

    /**
     * This method initialises networkOtherPropertiesPanel.
     * @return a new other additionalProperties panel.
     */
    private NetworkOtherPropertiesPanel getNetworkOtherPropertiesPanel ()
    {
        if (networkOtherPropertiesPanel == null)
        {
            networkOtherPropertiesPanel = new NetworkOtherPropertiesPanel (newNetwork);
            networkOtherPropertiesPanel.setName ("networkOtherPropertiesPanel");
        }
        return networkOtherPropertiesPanel;
    }

    /**
     * online help convenience method
     */
    @SuppressWarnings("unused")
    private void setOnlineHelp (String onlineSection)
    {
        /**
         * auxiliar help Viewer
         */
        HelpViewer helpViewer = null;
        helpViewer = HelpViewer.getUniqueInstance ();
        try
        {
            helpViewer.getHb ().enableHelpKey (this.getContentPane (), onlineSection,
                                               helpViewer.getHs ());
        }
        catch (BadIDException ex)
        {
            System.out.println ("WARNING >> " + ex.getMessage ());
            System.out.println (ex.getStackTrace ());
        }
        catch (Exception ex)
        {
            System.out.println ("WARNING >> " + ex.getMessage ());
            System.out.println (ex.getStackTrace ());
        }
    }

    /**
     * This method carries out the actions when the user press the Ok button
     * before hide the dialog.
     * @return true always
     */
    @Override
    protected boolean doOkClickBeforeHide ()
    {
        if (newNetwork)
        {
            // TODO Create probNet instance
            NetworkType networkType = getNetworkDefinitionPanel ().getNetworkType ();
            probNet = (getNetworkDefinitionPanel ().isObjectOriented ()) ? new OOPNet (networkType)
                                                                        : new ProbNet (networkType);
            probNet.setComment (getNetworkDefinitionPanel ().getNetworkComment ());
            probNet.setDefaultStates (getNetworkVariablesPanel ().getDefaultStates ());
        }
        else
        {
            probNet.getPNESupport ().closeParenthesis ();
        }
        return getNetworkDefinitionPanel ().checkName ();
    }

    // ESCA-JAVA0025: allows an empty method to override another one
    /**
     * This method carries out the actions when the user press the Cancel button
     * before hide the dialog.
     */
    @Override
    protected void doCancelClickBeforeHide ()
    {
        if (!newNetwork)
        {
            probNet.getPNESupport ().closeParenthesis ();
        }
    }

    /**
     * This method shows the dialog and requests the user the network
     * additionalProperties.
     * @param additionalProperties additionalProperties of the network.
     * @return OK_BUTTON if the user has pressed the 'Ok' button or
     *         CANCEL_BUTTON if the user has pressed the 'Cancel' button.
     */
    /**
     * This method shows the dialog and requests the user the network
     * additionalProperties.
     * @param additionalProperties additionalProperties of the network.
     * @return OK_BUTTON if the user has pressed the 'Ok' button or
     *         CANCEL_BUTTON if the user has pressed the 'Cancel' button.
     */
    public int showProperties ()
    {
        setVisible (true);
        return selectedButton;
    }

    /**
     * Returns the probNet.
     * @return the probNet.
     */
    public ProbNet getProbNet ()
    {
        return probNet;
    }
}