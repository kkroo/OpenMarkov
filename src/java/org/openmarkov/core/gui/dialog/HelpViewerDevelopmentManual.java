/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

//import openmarkov.exceptions.ExceptionsHandler;
import org.apache.log4j.Logger;

/**
 * Help Viewer using standard JavaHelp
 * 
 * @author jlgozalo
 * @version 1.0 15/04/2010 jlgozalo
 */
public class HelpViewerDevelopmentManual extends javax.swing.JFrame {

    /**
	 * 
	 */
    private static final long                  serialVersionUID        = 332731030432142803L;
    /**
     * HelpViewer unique instance. Used in singleton pattern.
     */
    private static HelpViewerDevelopmentManual helpViewer              = null;
    /**
     * the Helpset used to display the help
     */
    private HelpSet                            theHelpSet              = null;
    /**
     * the HelpBroker used to display the help
     */
    private HelpBroker                         theHelpBroker           = null;
    /**
     * X Dimension of the Help Viewer
     */
    private static final int                   HELP_VIEWER_X_DIMENSION = 640;
    /**
     * Y Dimension of the Help Viewer
     */
    private static final int                   HELP_VIEWER_Y_DIMENSION = 480;

    /**
     * private constructor
     */
    private HelpViewerDevelopmentManual() {
        this.theHelpSet = myHelpSet();
        this.theHelpBroker = myHelpBroker();
    }

    /**
     * @return HelpViewer unique instance (singleton pattern)
     */
    public static HelpViewerDevelopmentManual getUniqueInstance() {
        if (helpViewer == null) {
            helpViewer = new HelpViewerDevelopmentManual();
            helpViewer.setJMenuBar(helpMenuBar());
            helpViewer.setTitle("OpenMarkov Development Online Help");
            helpViewer.setSize(640, 480);
        }
        return helpViewer;

    }

    public static HelpSet myHelpSet() {
        String aHelpSet = "helpset.hs";
        Logger auxLogger = Logger.getLogger(HelpSet.class);
        HelpSet theHelpSet = null;
        try {
            URL hsURL = HelpSet.findHelpSet(HelpViewerDevelopmentManual.class.getClassLoader(),
                    aHelpSet);
            URL realHsURL = hsURL;
            theHelpSet = new HelpSet(null, realHsURL);
        } catch (HelpSetException ex) {
            auxLogger.info(ex.getMessage() + " Helpset " + aHelpSet + " not found");
            // ExceptionsHandler.handleException( ex, "Helpset "+aHelpSet +
            // " not found", false );
            theHelpSet = null;
        }
        return theHelpSet;

    }

    /**
     * Open HelpSet and send a message to Log in case it is not possible
     * 
     * @return myHelpBroker to display the Help System
     */
    public HelpBroker myHelpBroker() {

        HelpBroker aHelpBroker = null;

        if (theHelpSet == null) {
            System.out.println("theHelpSet is null!!!");
            aHelpBroker = null;
        } else {
            // create HelpBroker from HelpSet
            aHelpBroker = theHelpSet.createHelpBroker();
            aHelpBroker.setSize(new Dimension(HELP_VIEWER_X_DIMENSION, HELP_VIEWER_Y_DIMENSION));

        }
        return aHelpBroker;
    }

    private static JMenuBar helpMenuBar() {
        JMenuBar theMenuBar = new JMenuBar();
        theMenuBar.add(helpMenu());
        return theMenuBar;
    }

    private static JMenu helpMenu() {
        JMenu theMenu = new JMenu();
        theMenu.add(helpMenuItem());
        return theMenu;
    }

    private static JMenuItem helpMenuItem() {
        JMenuItem theMenuItem = new JMenuItem("Help");
        theMenuItem.setText("Help");
        ActionListener helper = new CSH.DisplayHelpFromSource(helpViewer.getHb());
        theMenuItem.addActionListener(helper);

        return theMenuItem;
    }

    /**
     * @return the helpset
     */
    public HelpSet getHs() {

        return theHelpSet;
    }

    /**
     * @param aHelpSet
     *            the helpset to set
     */
    public void setHs(HelpSet aHelpSet) {

        this.theHelpSet = aHelpSet;
    }

    /**
     * @return the helpBroker
     */
    public HelpBroker getHb() {

        return theHelpBroker;
    }

    /**
     * @param aHelpBroker
     *            the helpbroker to set
     */
    public void setHb(HelpBroker aHelpBroker) {

        this.theHelpBroker = aHelpBroker;
    }

    /**
     * HelpViewerDevelopmentManual main class
     * 
     * @param args
     */
    public static void main(String[] args) {
        // JFrame helpFrame = new JFrame("ayuda");
        // helpFrame.setVisible(true);
        HelpViewerDevelopmentManual.getUniqueInstance().setVisible(true);

    }
}
