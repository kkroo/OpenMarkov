/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
/**
 * PreferencesDialog.java
 */

package org.openmarkov.core.gui.dialog.configuration;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * Class to help user to configure the system and user preferences in the
 * OPENMARKOV system
 * @author jlgozalo
 * @version 1.0 28 Aug 2009
 * @version 1.1 11 Mar 2010 fix import/export/reset errors
 */
public class PreferencesDialog extends JDialog
    implements
        ActionListener
{
    /**
     * generate serial id
     */
    private static final long  serialVersionUID  = -8957131079235183957L;
    /**
     * main display components
     */
    JTree                      jTreePreferences  = null;
    JTable                     jTableEdition     = null;
    /**
     * buttons for the dialog
     */
    JButton                    jButtonSave       = null;
    JButton                    jButtonCancel     = null;
    JButton                    jButtonExport     = null;
    JButton                    jButtonImport     = null;
    JButton                    jButtonReset      = null;
    /**
     * file chooser for export/import options
     */
    final private JFileChooser chooser           = new JFileChooser ();
    /**
     * constants for graphical drawing
     */
    private static final int   DIVIDER_LOCATION  = 250;
    private static final int   PREFERENCE_WIDTH  = 640;
    private static final int   PREFERENCE_HEIGHT = 480;
    private Logger             logger;
    /**
     * String database
     */
    protected StringDatabase   stringDatabase    = StringDatabase.getUniqueInstance ();

    /**
     * Creates PreferencesEditor dialog that show all System and User
     * preferences.
     * @param owner owner JFrame
     * @wbp.parser.constructor
     */
    public PreferencesDialog (JFrame owner)
    {
        this (owner, "OPENMARKOV User Preferences",
              OpenMarkovPreferences.OPENMARKOV_NODE_PREFERENCES, true/*
                                                                      * ,
                                                                      * OpenMarkovPreferences
                                                                      * .
                                                                      * OPENMARKOV_KERNEL_PREFERENCES
                                                                      * , false
                                                                      */);
    }

    /**
     * Creates PreferencesEditor dialog that show all System and User
     * preferences.
     * @param owner owner JFrame
     * @param title title of dialog
     */
    public PreferencesDialog (JFrame owner, String title)
    {
        this (owner, title, OpenMarkovPreferences.OPENMARKOV_NODE_PREFERENCES, true/*
                                                                                    * ,
                                                                                    * OpenMarkovPreferences
                                                                                    * .
                                                                                    * OPENMARKOV_KERNEL_PREFERENCES
                                                                                    * ,
                                                                                    * false
                                                                                    */);
    }

    /**
     * @param owner owner JFrame
     * @param title title of dialog
     * @param userObj the package to which this object belongs is used as the
     *            root-node of the User preferences tree (if userObj is null,
     *            then the rootnode of all user preferences will be used)
     * @boolean showUserPrefs if true, then show user preferences
     * @param systemObj the package to which this object belongs is used as the
     *            root-node of the System preferences tree (if systemObj is
     *            null, then the rootnode of all system preferences will be
     *            used)
     * @param showSystemPrefs if true, then show system preferences
     */
    public PreferencesDialog (JFrame owner, String title, Object userObj, boolean showUserPrefs/*
                                                                                                * ,
                                                                                                * Object
                                                                                                * systemObj
                                                                                                * ,
                                                                                                * boolean
                                                                                                * showSystemPrefs
                                                                                                */)
    {
        super (owner);
        setTitle (title);
        setChooser ();
        int width = PREFERENCE_WIDTH;
        int height = PREFERENCE_HEIGHT;
        int x = owner.getX () + (owner.getWidth () - width) / 2;
        int y = owner.getY () + (owner.getHeight () - height) / 2;
        this.setBounds (x, y, width, height);
        getContentPane ().setLayout (new BorderLayout (5, 5));
        createTree (userObj, showUserPrefs/* , systemObj, showSystemPrefs */);
        jTableEdition = new JTable ();
        createSplitPane ();
        createButtonPanel ();
        this.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible (true);
        this.logger = Logger.getLogger (PreferencesDialog.class);
    }

    /**
     * create the Tree where the Preferences will be displayed
     * @param userObj class to define the User Preferences
     * @param showUserPrefs true if User Preferences will be shown
     * @param systemObj class to define the System Preferences
     * @param showSystemPrefs true if System Preferences will be shown
     */
    private void createTree (Object userObj, boolean showUserPrefs/*
                                                                   * , Object
                                                                   * systemObj,
                                                                   * boolean
                                                                   * showSystemPrefs
                                                                   */)
    {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode ("Preferences");
        /*
         * if (showSystemPrefs) { rootNode.add(createSystemRootNode(systemObj));
         * //rootNode.add(createSystemNodeForPackage(systemObj)); }
         */
        if (showUserPrefs)
        {
            rootNode.add (createUserRootNode (userObj));
            // rootNode.add(createUserNodeForPackage(userObj));
        }
        DefaultTreeModel model = new DefaultTreeModel (rootNode);
        jTreePreferences = new JTree (model);
        TreeNode openmarkov = rootNode.getChildAt (0);
        // not shown color preferences in tree
        ((PreferenceTreeNode) openmarkov).removeChildAt (0);
        jTreePreferences.addTreeSelectionListener (new PrefTreeSelectionListener ());
    }

    private MutableTreeNode createSystemRootNode (Object obj)
    {
        try
        {
            PreferenceTreeNode systemRoot;
            if (obj == null)
            {
                systemRoot = new PreferenceTreeNode (Preferences.systemRoot ());
            }
            else
            {
                systemRoot = new PreferenceTreeNode (
                                                     Preferences.systemRoot ().node (obj.toString ()));
            }
            return systemRoot;
        }
        catch (BackingStoreException e)
        {
            e.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString ("No System Root Preferences!"
                                                                     + e.getMessage ()),
                                           stringDatabase.getString ("No System Root Preferences!"
                                                                     + e.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
            return new DefaultMutableTreeNode ("No System Root Preferences!");
        }
    }

    private MutableTreeNode createUserRootNode (Object obj)
    {
        try
        {
            PreferenceTreeNode userRoot;
            if (obj == null)
            {
                userRoot = new PreferenceTreeNode (Preferences.userRoot ());
            }
            else
            {
                userRoot = new PreferenceTreeNode (Preferences.userRoot ().node ((String) obj));
            }
            return userRoot;
        }
        catch (BackingStoreException e)
        {
            e.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString ("No User Root Preferences!"
                                                                     + e.getMessage ()),
                                           stringDatabase.getString ("No User Root Preferences!"
                                                                     + e.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
            return new DefaultMutableTreeNode ("No User Root Preferences!");
        }
    }

    private MutableTreeNode createSystemNodeForPackage (Object obj)
    {
        try
        {
            PreferenceTreeNode systemRoot;
            if (obj == null)
            {
                systemRoot = new PreferenceTreeNode (Preferences.systemRoot ());
            }
            else
            {
                systemRoot = new PreferenceTreeNode (
                                                     Preferences.systemNodeForPackage ((Class<?>) obj));
            }
            return systemRoot;
        }
        catch (BackingStoreException e)
        {
            e.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString ("No preferences for System Node for Package"
                                                                     + ((Class<?>) obj).getName ()
                                                                     + "!" + e.getMessage ()),
                                           stringDatabase.getString ("No preferences for System Node for Package"
                                                                     + ((Class<?>) obj).getName ()
                                                                     + "!" + e.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
            return new DefaultMutableTreeNode ("No preferences for System Node for Package"
                                               + ((Class<?>) obj).getName () + "!");
        }
    }

    private MutableTreeNode createUserNodeForPackage (Object obj)
    {
        try
        {
            PreferenceTreeNode userRoot;
            if (obj == null)
            {
                userRoot = new PreferenceTreeNode (Preferences.userRoot ());
            }
            else
            {
                userRoot = new PreferenceTreeNode (Preferences.userNodeForPackage ((Class<?>) obj));
            }
            return userRoot;
        }
        catch (BackingStoreException e)
        {
            e.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString ("No preferences for User Node for Package"
                                                                     + ((Class<?>) obj).getName ()
                                                                     + "!" + e.getMessage ()),
                                           stringDatabase.getString ("No preferences for User Node for Package"
                                                                     + ((Class<?>) obj).getName ()
                                                                     + "!" + e.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
            return new DefaultMutableTreeNode ("No preferences for User Node for Package"
                                               + ((Class<?>) obj).getName () + "!");
        }
    }

    private void createSplitPane ()
    {
        JSplitPane splitPane = new JSplitPane ();
        splitPane.setOrientation (JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable (true);
        splitPane.setLeftComponent (new JScrollPane (jTreePreferences));
        splitPane.setRightComponent (new JScrollPane (jTableEdition));
        splitPane.setDividerLocation (DIVIDER_LOCATION);
        getContentPane ().add (splitPane, BorderLayout.CENTER);
    }

    private void createButtonPanel ()
    {
        JPanel jPanelButtons = new JPanel (new FlowLayout ());
        jPanelButtons.add (getJButtonSave ());
        jPanelButtons.add (getJButtonReset ());
        jPanelButtons.add (getJButtonExport ());
        jPanelButtons.add (getJButtonImport ());
        jPanelButtons.add (getJButtonCancel ());
        getContentPane ().add (jPanelButtons, BorderLayout.SOUTH);
    }

    /**
     * @return
     */
    protected JButton getJButtonSave ()
    {
        if (jButtonSave == null)
        {
            jButtonSave = new JButton ();
            jButtonSave.setName ("jButtonSave");
            jButtonSave.setText ("Save");
            jButtonSave.addActionListener (this);
        }
        return jButtonSave;
    }

    /**
     * @return
     */
    protected JButton getJButtonCancel ()
    {
        if (jButtonCancel == null)
        {
            jButtonCancel = new JButton ();
            jButtonCancel.setName ("jButtonCancel");
            jButtonCancel.setText ("Cancel");
            jButtonCancel.addActionListener (this);
        }
        return jButtonCancel;
    }

    /**
     * @return
     */
    protected JButton getJButtonExport ()
    {
        if (jButtonExport == null)
        {
            jButtonExport = new JButton ();
            jButtonExport.setName ("jButtonExport");
            jButtonExport.setText ("Export preferences");
            jButtonExport.addActionListener (this);
        }
        return jButtonExport;
    }

    /**
     * @return
     */
    protected JButton getJButtonImport ()
    {
        if (jButtonImport == null)
        {
            jButtonImport = new JButton ();
            jButtonImport.setName ("jButtonImport");
            jButtonImport.setText ("Import preferences");
            jButtonImport.addActionListener (this);
        }
        return jButtonImport;
    }

    /**
     * @return
     */
    protected JButton getJButtonReset ()
    {
        if (jButtonReset == null)
        {
            jButtonReset = new JButton ();
            jButtonReset.setName ("jButtonReset");
            jButtonReset.setText ("Reset user preferences");
            jButtonReset.addActionListener (this);
        }
        return jButtonReset;
    }

    /**
     * Invoked when an action occurs.
     * @param e event information.
     */
    public void actionPerformed (ActionEvent e)
    {
        if (e.getSource ().equals (this.jButtonCancel))
        {
            actionPerformedCancel ();
        }
        else if (e.getSource ().equals (this.jButtonExport))
        {
            actionPerformedExport ();
        }
        else if (e.getSource ().equals (this.jButtonImport))
        {
            actionPerformedImport ();
        }
        else if (e.getSource ().equals (this.jButtonSave))
        {
            actionPerformedSave ();
        }
        else if (e.getSource ().equals (this.jButtonReset))
        {
            actionPerformedReset ();
        }
    }

    /**
     * execute the Cancel action by doing an undo operation in the system and
     * user preferences
     */
    protected void actionPerformedCancel ()
    {
        Preferences rootPreferences = Preferences.systemRoot ();
        PreferencesTableModel rootPrefTableModel = new PreferencesTableModel (rootPreferences);
        rootPrefTableModel.undo ();
        Preferences userPreferences = Preferences.userRoot ();
        PreferencesTableModel userPrefTableModel = new PreferencesTableModel (userPreferences);
        userPrefTableModel.undo ();
        this.setVisible (false);
        this.dispose ();
    }

    /**
     * execute the Save action by doing a sync in the system and user
     * preferences
     */
    protected void actionPerformedSave ()
    {
        Preferences rootPreferences = Preferences.systemRoot ();
        PreferencesTableModel rootPrefTableModel = new PreferencesTableModel (rootPreferences);
        rootPrefTableModel.syncSave ();
        Preferences userPreferences = Preferences.userRoot ();
        PreferencesTableModel userPrefTableModel = new PreferencesTableModel (userPreferences);
        userPrefTableModel.syncSave ();
        this.setVisible (false);
        this.dispose ();
    }

    /**
     * execute the Cancel action
     */
    protected void actionPerformedExport ()
    {
        Preferences root = Preferences.userRoot ();
        Preferences node = root.node (OpenMarkovPreferences.OPENMARKOV_NODE_PREFERENCES);
        System.out.println ("Export selected");
        if (chooser.showSaveDialog (PreferencesDialog.this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                OutputStream out = new FileOutputStream (chooser.getSelectedFile ());
                node.exportSubtree (out);
                out.close ();
            }
            catch (Exception e)
            {
                e.printStackTrace ();
                JOptionPane.showMessageDialog (this, stringDatabase.getString (e.getMessage ()),
                                               stringDatabase.getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * execute the Cancel action
     */
    protected void actionPerformedImport ()
    {
        System.out.println ("Import selected");
        if (chooser.showOpenDialog (PreferencesDialog.this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                InputStream in = new FileInputStream (chooser.getSelectedFile ());
                Preferences.importPreferences (in);
                in.close ();
                this.invalidate ();
                this.jTableEdition.repaint ();
                this.jTreePreferences.repaint ();
                this.repaint ();
            }
            catch (Exception e)
            {
                e.printStackTrace ();
                JOptionPane.showMessageDialog (this, stringDatabase.getString (e.getMessage ()),
                                               stringDatabase.getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * execute the Reset action by cleaning preferences in the user preferences
     */
    protected void actionPerformedReset ()
    {
        try
        {
            OpenMarkovPreferences.setDefaultPreferences ();
            this.jTableEdition.repaint ();
            this.jTreePreferences.repaint ();
            this.repaint ();
        }
        catch (Exception ex)
        {
            // ExceptionsHandler.handleException(
            // ex, "Error reseting Preferences", false );
            logger.error ("Error reseting Preferences");
        }
    }

    /**
     * set initial configuration for the chooser that shows ONLY .xml files
     */
    private void setChooser ()
    {
        chooser.setCurrentDirectory (new File ("."));
        // accept all files ending with .xml
        chooser.setFileFilter (new javax.swing.filechooser.FileFilter ()
            {
                public boolean accept (File f)
                {
                    return f.getName ().toLowerCase ().endsWith (".xml") || f.isDirectory ();
                }

                public String getDescription ()
                {
                    return "XML files";
                }
            });
    }
    /**
     * convenience internal class to handle Tree value changes
     * @author jlgozalo
     * @version 1.0 13 Sep 2009
     */
    class PrefTreeSelectionListener
        implements
            TreeSelectionListener
    {
        public void valueChanged (TreeSelectionEvent e)
        {
            try
            {
                PreferenceTreeNode node = (PreferenceTreeNode) e.getPath ().getLastPathComponent ();
                Preferences pref = node.getPrefObject ();
                jTableEdition.setModel (new PreferencesTableModel (pref));
            }
            catch (ClassCastException ce)
            {
                System.out.println ("Node not PrefTreeNode!");
                jTableEdition.setModel (new DefaultTableModel ());
            }
        }
    }
}
