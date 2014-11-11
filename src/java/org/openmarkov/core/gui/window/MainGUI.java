/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferencesKeys;
import org.openmarkov.core.gui.dialog.SplashScreenLoader;
import org.openmarkov.core.gui.loader.element.OpenMarkovLogoIcon;

/**
 * This class constructs the main GUI in a frame with a splash screen during the
 * loading and reading configuration from external preferences.
 * 
 * @author mendoza
 * @author jlgozalo
 * @version 0 mendoza - initial version
 * @version 1.0 jlgozalo - including splashscreen
 * @version 1.1 jlgozalo - externalizing thisFrame to handle AboutBox and others
 * @version 1.2 jlgozalo - including external preferences
 * @version 1.3 jlgozalo - replacing System.err with JOptionPane
 */
@SuppressWarnings("serial")
public class MainGUI extends JFrame {

    /**
     * Main panel of the GUI.
     */
    MainPanel mainPanel = null;

    /**
     * Launch the MainGUIInit runnable process
     * 
     * @wbp.parser.entryPoint
     */
    public MainGUI() {

        /**
         * Splash Screen panel
         */
        SplashScreenLoader splash = new SplashScreenLoader();

        configureUI();
        splash.splashScreenInit();

        splash.getSplash().setProgress("Loading OpenMarkov preferences", 0);
        doReadPreferences();
        splash.doingWork();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("OpenMarkov");
        setName("MainGUI");
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setSize(screenPortionSize(screenInsets));
        setLocation(screenInsets.left, screenInsets.top);
        splash.getSplash().setProgress("Loading Resources", 25);
        // TODO here will be the plug-in loaders in future
        setIconImage(OpenMarkovLogoIcon.getUniqueInstance().getOpenMarkovLogoIconImage16());
        splash.getSplash().setProgress("Loading Main Panel", 50);
        setContentPane(getMainPanel());
        splash.getSplash().setProgress("Completed", 100);
        // loading the application
        splash.splashScreenDestroy();
    }

    /**
     * This method sets and configures the UI manager.
     * 
     * @wbp.parser.entryPoint
     */
    private static void configureUI() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        /*
         * The next line is used to avoid that disabled menuitems are
         * highlighted.
         */

        UIManager.put("MenuItem.disabledAreNavigable", Boolean.FALSE);

    }

    /**
     * This method returns a dimension that represents the 3/4 size of the
     * screen.
     * 
     * @return new dimensions of the window.
     * @wbp.parser.entryPoint
     */
    private Dimension screenPortionSize(Insets screenInsets) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screen.width - screenInsets.right - screenInsets.left;
        int height = screen.height - screenInsets.top - screenInsets.bottom;
        return new Dimension(width, height);
    }

    /**
     * This method initialises mainPanel.
     * 
     * @return a new main panel.
     */
    public MainPanel getMainPanel() {

        if (mainPanel == null) {
            mainPanel = new MainPanel(this);
        }

        return mainPanel;

    }

    /**
     * Opens net from file
     * 
     * @param fileName
     */
    public void openNetwork(String fileName) {
        mainPanel.openNetwork(fileName);
    }

    /**
     * read the <code>OpenMarkovPreferences</code> configuration, and set the
     * LastConnection preference to current Time
     */
    private static void doReadPreferences() {

        final boolean initialised = OpenMarkovPreferences.getBoolean(OpenMarkovPreferencesKeys.INITIALIZED,
                OpenMarkovPreferences.OPENMARKOV_PREFERENCES,
                false);
        if (!initialised) {
            OpenMarkovPreferences.setDefaultPreferences();
        }
        OpenMarkovPreferences.set(OpenMarkovPreferencesKeys.LAST_CONNECTION,
                Double.toString(System.currentTimeMillis()),
                OpenMarkovPreferences.OPENMARKOV_PREFERENCES);
        OpenMarkovPreferences.set(OpenMarkovPreferencesKeys.LAST_USER_CONNECTED,
                System.getProperty("user.name"),
                OpenMarkovPreferences.OPENMARKOV_PREFERENCES);
    }

}
