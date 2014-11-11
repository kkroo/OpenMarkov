/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.dbgenerator.gui;

import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.gui.dialog.io.DBWriterFileChooser;
import org.openmarkov.core.gui.dialog.io.FileFilterBasic;
import org.openmarkov.core.gui.dialog.io.NetsIO;
import org.openmarkov.core.gui.dialog.io.NetworkFileChooser;
import org.openmarkov.core.gui.loader.element.OpenMarkovLogoIcon;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.plugin.ToolPlugin;
import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseWriter;
import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.dbgenerator.DBGenerator;

/**
 * GUI to the DBGenerator option
 * @author fjdiez
 * @author ibermejo
 * @version 1.0
 * @since OpenMarkov 1.0
 */
@SuppressWarnings("serial")
@ToolPlugin(name = "DBGenerator", command = "Tools.DBGenerator")
public class DBGeneratorGUI extends javax.swing.JDialog
{
    private ProbNet             net;
    private String              netFilePath    = null;
    private String              fileName       = null;
    private CaseDatabaseWriter  databaseWriter = null;

    /**
     * Messages string resource.
     */
    private JFrame              parent;
    private CaseDatabaseManager caseDbManager;
    /**
     * String database
     */
    protected StringDatabase    stringDatabase = StringDatabase.getUniqueInstance ();

    /**
     * Constructor for DBGeneratorGUI.
     * @param parent
     */
    public DBGeneratorGUI (JFrame parent)
    {
        super (parent, true);
        this.parent = parent;
        initComponents ();
        caseDbManager = new CaseDatabaseManager ();
        setIconImage (OpenMarkovLogoIcon.getUniqueInstance ().getOpenMarkovLogoIconImage16 ());
        try
        {
            UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace ();
        }
        setDefaultCloseOperation (HIDE_ON_CLOSE);
        setLocationRelativeTo (null);
        netButtonGroup = new ButtonGroup ();
        netButtonGroup.add (fromFileRadioButton);
        netButtonGroup.add (fromOpenMarkovRadioButton);
        boolean isOpenNet = MainPanel.getUniqueInstance ().getMainPanelListenerAssistant ().getCurrentNetworkPanel () != null;
        if (isOpenNet)
        {
            net = MainPanel.getUniqueInstance ().getMainPanelListenerAssistant ().getCurrentNetworkPanel ().getProbNet ();
            generateButton.setEnabled (true);
        }
        fromOpenMarkovRadioButton.setEnabled (isOpenNet);
        fromOpenMarkovRadioButton.setSelected (isOpenNet);
        fromFileRadioButton.setSelected (!isOpenNet);
        loadNetButton.setEnabled (!isOpenNet);
        caseNumber.setValue (1000);
        setVisible (true);
    }

    private ProbNet loadNet (String filePath)
    {
        ProbNet probNet = null;
        if ((netFilePath != null) && (!netFilePath.equals ("")))
        {
            if (!isSupportedNetFormat (fileName))
            {
                JOptionPane.showMessageDialog (null,
                                               stringDatabase.getString ("DBGenerator.IncorrectFileFormat"),
                                               stringDatabase.getString ("ErrorWindow.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                try
                {
                    probNet = NetsIO.openNetworkFile (filePath).getProbNet ();
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog (null,
                                                   stringDatabase.getString ("DBGenerator.UnableToLoadNet"),
                                                   stringDatabase.getString ("ErrorWindow.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace ();
                }
            }
        }
        return probNet;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents ()
    {
        caseDBFileChooser = new DBWriterFileChooser ();
        netFileChooser = new NetworkFileChooser ();
        netButtonGroup = new javax.swing.ButtonGroup ();
        jPanel8 = new javax.swing.JPanel ();
        fromFileRadioButton = new javax.swing.JRadioButton ();
        fromOpenMarkovRadioButton = new javax.swing.JRadioButton ();
        jScrollPane5 = new javax.swing.JScrollPane ();
        netFilePathTextPane = new javax.swing.JTextPane ();
        loadNetButton = new javax.swing.JButton ();
        generateButton = new javax.swing.JButton ();
        cancelButton = new javax.swing.JButton ();
        jPanel2 = new javax.swing.JPanel ();
        caseNumber = new javax.swing.JSpinner ();
        netFileChooser.setCursor (new java.awt.Cursor (java.awt.Cursor.DEFAULT_CURSOR));
        setDefaultCloseOperation (javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle ("OpenMarkov - Evaluation");
        jPanel8.setBorder (javax.swing.BorderFactory.createTitledBorder ("Choose net"));
        fromFileRadioButton.setText ("Open from file");
        fromFileRadioButton.addActionListener (new java.awt.event.ActionListener ()
            {
                public void actionPerformed (java.awt.event.ActionEvent evt)
                {
                    fromFileRadioButtonActionPerformed (evt);
                }
            });
        fromOpenMarkovRadioButton.setText ("Use open net");
        fromOpenMarkovRadioButton.addActionListener (new java.awt.event.ActionListener ()
            {
                public void actionPerformed (java.awt.event.ActionEvent evt)
                {
                    fromOpenMarkovRadioButtonActionPerformed (evt);
                }
            });
        netFilePathTextPane.setEditable (false);
        netFilePathTextPane.setEnabled (false);
        jScrollPane5.setViewportView (netFilePathTextPane);
        loadNetButton.setText ("Open");
        loadNetButton.setEnabled (false);
        loadNetButton.addActionListener (new java.awt.event.ActionListener ()
            {
                public void actionPerformed (java.awt.event.ActionEvent evt)
                {
                    loadNetButtonActionPerformed (evt);
                }
            });
        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout (
                                                                                             jPanel8);
        jPanel8.setLayout (jPanel8Layout);
        jPanel8Layout.setHorizontalGroup (jPanel8Layout.createParallelGroup (org.jdesktop.layout.GroupLayout.LEADING).add (jPanel8Layout.createSequentialGroup ().addContainerGap ().add (jPanel8Layout.createParallelGroup (org.jdesktop.layout.GroupLayout.LEADING).add (jPanel8Layout.createSequentialGroup ().add (fromFileRadioButton).add (18,
                                                                                                                                                                                                                                                                                                                                                 18,
                                                                                                                                                                                                                                                                                                                                                 18).add (loadNetButton,
                                                                                                                                                                                                                                                                                                                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                          88,
                                                                                                                                                                                                                                                                                                                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap (org.jdesktop.layout.LayoutStyle.UNRELATED).add (jScrollPane5,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           297,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add (fromOpenMarkovRadioButton)).addContainerGap (org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              Short.MAX_VALUE)));
        jPanel8Layout.setVerticalGroup (jPanel8Layout.createParallelGroup (org.jdesktop.layout.GroupLayout.LEADING).add (jPanel8Layout.createSequentialGroup ().addContainerGap ().add (fromOpenMarkovRadioButton).addPreferredGap (org.jdesktop.layout.LayoutStyle.RELATED).add (jPanel8Layout.createParallelGroup (org.jdesktop.layout.GroupLayout.LEADING).add (jScrollPane5,
                                                                                                                                                                                                                                                                                                                                                                   org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                   22,
                                                                                                                                                                                                                                                                                                                                                                   org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add (jPanel8Layout.createParallelGroup (org.jdesktop.layout.GroupLayout.BASELINE).add (fromFileRadioButton).add (loadNetButton))).addContainerGap (org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      Short.MAX_VALUE)));
        generateButton.setText ("Generate");
        generateButton.setEnabled (false);
        generateButton.addActionListener (new java.awt.event.ActionListener ()
            {
                public void actionPerformed (java.awt.event.ActionEvent evt)
                {
                    generateButtonActionPerformed (evt);
                }
            });
        cancelButton.setText ("Cancel");
        cancelButton.setPreferredSize (new java.awt.Dimension (99, 23));
        cancelButton.addActionListener (new java.awt.event.ActionListener ()
            {
                public void actionPerformed (java.awt.event.ActionEvent evt)
                {
                    cancelButtonActionPerformed (evt);
                }
            });
        jPanel2.setBorder (javax.swing.BorderFactory.createTitledBorder ("Number of cases"));
        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout (
                                                                                             jPanel2);
        jPanel2.setLayout (jPanel2Layout);
        jPanel2Layout.setHorizontalGroup (jPanel2Layout.createParallelGroup (org.jdesktop.layout.GroupLayout.LEADING).add (jPanel2Layout.createSequentialGroup ().add (218,
                                                                                                                                                                       218,
                                                                                                                                                                       218).add (caseNumber,
                                                                                                                                                                                 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                 84,
                                                                                                                                                                                 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap (org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                  Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup (jPanel2Layout.createParallelGroup (org.jdesktop.layout.GroupLayout.LEADING).add (jPanel2Layout.createSequentialGroup ().addContainerGap ().add (caseNumber,
                                                                                                                                                                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap (15,
                                                                                                                                                                                                                                                         Short.MAX_VALUE)));
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout (
                                                                                      getContentPane ());
        getContentPane ().setLayout (layout);
        layout.setHorizontalGroup (layout.createParallelGroup (org.jdesktop.layout.GroupLayout.LEADING).add (layout.createSequentialGroup ().addContainerGap ().add (layout.createParallelGroup (org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                                                                                                                 false).add (org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                                                                                                             jPanel2,
                                                                                                                                                                                                             org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                             org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                             Short.MAX_VALUE).add (org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                                                                                                                                   jPanel8,
                                                                                                                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                   Short.MAX_VALUE)).addContainerGap ()).add (layout.createSequentialGroup ().add (182,
                                                                                                                                                                                                                                                                                                                   182,
                                                                                                                                                                                                                                                                                                                   182).add (generateButton).add (18,
                                                                                                                                                                                                                                                                                                                                                  18,
                                                                                                                                                                                                                                                                                                                                                  18).add (cancelButton,
                                                                                                                                                                                                                                                                                                                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                           90,
                                                                                                                                                                                                                                                                                                                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add (0,
                                                                                                                                                                                                                                                                                                                                                                                                                0,
                                                                                                                                                                                                                                                                                                                                                                                                                Short.MAX_VALUE)));
        layout.linkSize (new java.awt.Component[] {cancelButton, generateButton},
                         org.jdesktop.layout.GroupLayout.HORIZONTAL);
        layout.setVerticalGroup (layout.createParallelGroup (org.jdesktop.layout.GroupLayout.LEADING).add (layout.createSequentialGroup ().addContainerGap ().add (jPanel8,
                                                                                                                                                                   org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                   org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap (org.jdesktop.layout.LayoutStyle.RELATED).add (jPanel2,
                                                                                                                                                                                                                                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap (org.jdesktop.layout.LayoutStyle.RELATED).add (layout.createParallelGroup (org.jdesktop.layout.GroupLayout.BASELINE).add (generateButton).add (cancelButton,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap (org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   Short.MAX_VALUE)));
        jPanel8.getAccessibleContext ().setAccessibleName ("Select net");
        pack ();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * It asks the user to choose a file by means of a open-file dialog box.
     * @return complete path of the file, or null if the user selects cancel.
     */
    private String requestNetworkFileToOpen ()
    {
        netFileChooser.setDialogTitle (stringDatabase.getString ("OpenNetwork.Title.Label"));
        String filePath = null;
        if (netFileChooser.showOpenDialog (this.parent) == JFileChooser.APPROVE_OPTION)
        {
            filePath = netFileChooser.getSelectedFile ().getAbsolutePath ();
            fileName = netFileChooser.getSelectedFile ().getName ();
            netFilePathTextPane.setText (filePath);
        }
        return filePath;
    }

    private void cancelButtonActionPerformed (java.awt.event.ActionEvent evt)
    {// GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible (false);
    }// GEN-LAST:event_cancelButtonActionPerformed

    private void generateButtonActionPerformed (java.awt.event.ActionEvent evt)
    {// GEN-FIRST:event_EvaluateButtonActionPerformed
        DBGenerator dbGenerator = new DBGenerator ();
        CaseDatabase database = dbGenerator.generate (net, ((Integer) caseNumber.getValue ()));
        String databasePath = null;
        try
        {
            if (caseDBFileChooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION)
            {
                String filename = caseDBFileChooser.getSelectedFile ().getName ();
                if (!caseDBFileChooser.getFileFilter ().accept (caseDBFileChooser.getSelectedFile ()))
                {
                    filename = caseDBFileChooser.getSelectedFile ().getName ()
                               + "."
                               + ((FileFilterBasic) caseDBFileChooser.getFileFilter ()).getFilterExtension ();
                }
                databaseWriter = caseDbManager.getWriter (FilenameUtils.getExtension (filename));
                if (databaseWriter == null)
                {
                    JOptionPane.showMessageDialog (null,
                                                   stringDatabase.getString ("DBGenerator.IncorrectCaseDatabaseFileFormat"),
                                                   stringDatabase.getString ("ErrorWindow.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    generateButton.setEnabled (net != null);
                    databasePath = caseDBFileChooser.getSelectedFile ().getParent () + "\\"
                                   + filename;
                }
                databaseWriter.save (databasePath, database);
                JOptionPane.showMessageDialog (null,
                                               stringDatabase.getString ("DBGenerator.Finished"),
                                               stringDatabase.getString ("DBGenerator.Title"),
                                               JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog (null, stringDatabase.getString ("DBGenerator.Error"),
                                           stringDatabase.getString ("ErrorWindow.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
            e.printStackTrace ();
        }
        this.setVisible (false);
    }// GEN-LAST:event_EvaluateButtonActionPerformed

    private void fromOpenMarkovRadioButtonActionPerformed (java.awt.event.ActionEvent evt)
    {// GEN-FIRST:event_fromOpenMarkovRadioButtonActionPerformed
        if (fromOpenMarkovRadioButton.isSelected ())
        {
            NetworkPanel networkPanel = MainPanel.getUniqueInstance ().getMainPanelListenerAssistant ().getCurrentNetworkPanel ();
            net = networkPanel.getProbNet ();
            generateButton.setEnabled (net != null);
        }
    }// GEN-LAST:event_fromOpenMarkovRadioButtonActionPerformed

    private void loadNetButtonActionPerformed (java.awt.event.ActionEvent evt)
    {// GEN-FIRST:event_loadModelNetButtonActionPerformed
        netFilePath = requestNetworkFileToOpen ();
        if (netFilePath != null)
        {
            net = loadNet (netFilePath);
            generateButton.setEnabled (net != null);
        }
    }// GEN-LAST:event_loadModelNetButtonActionPerformed

    private void fromFileRadioButtonActionPerformed (java.awt.event.ActionEvent evt)
    {// GEN-FIRST:event_fromFileRadioButton1ActionPerformed
        if (netFilePath == null)
        {
            netFilePath = requestNetworkFileToOpen ();
            if (netFilePath != null)
            {
                net = loadNet (netFilePath);
            }
        }
        if (netFilePath != null)
        {
            loadNetButton.setEnabled (fromFileRadioButton.isSelected ());
            generateButton.setEnabled (net != null);
        }
    }// GEN-LAST:event_fromFileRadioButton1ActionPerformed

    /**
     * @param path
     * @return whether a file format is supported or not
     */
    private static boolean isSupportedNetFormat (String path)
    {
        return (FilenameUtils.getExtension (path).toLowerCase ().equals ("elv")
                || FilenameUtils.getExtension (path).toLowerCase ().equals ("xml") || FilenameUtils.getExtension (path).toLowerCase ().equals ("pgmx"));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton      cancelButton;
    private static javax.swing.JFileChooser caseDBFileChooser;
    private javax.swing.JSpinner            caseNumber;
    private static javax.swing.JRadioButton fromFileRadioButton;
    private static javax.swing.JRadioButton fromOpenMarkovRadioButton;
    private static javax.swing.JButton      generateButton;
    private javax.swing.JPanel              jPanel2;
    private javax.swing.JPanel              jPanel8;
    private javax.swing.JScrollPane         jScrollPane5;
    private static javax.swing.JButton      loadNetButton;
    private javax.swing.ButtonGroup         netButtonGroup;
    private static NetworkFileChooser       netFileChooser;
    private static javax.swing.JTextPane    netFilePathTextPane;
    // End of variables declaration//GEN-END:variables
}
