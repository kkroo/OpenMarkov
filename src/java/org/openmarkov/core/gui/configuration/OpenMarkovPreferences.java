/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
/**
 * 
 */

package org.openmarkov.core.gui.configuration;

import java.awt.Color;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * Convenience class to encapsulate the Preferences for OpenMarkov project
 * @author jlgozalo
 * @version 1.0 13 Sep 2009
 * @version 1.1 30 Oct 2009 - adding kernel and languages subsets. - adding
 *          getInt/setInt methods - adding interface implementation (constants)
 */
public class OpenMarkovPreferences
    implements
        OpenMarkovPreferencesKeys
{
    /**
     * the package nodes in the Preferences
     */
    public static final String    OPENMARKOV_NODE_PREFERENCES = "OPENMARKOV";
    /**
     * the package system in the Preferences
     */
    // public static final String OPENMARKOV_SYSTEM_PREFERENCES =
    // "Openmarkovkernel";
    /**
     * the preferences
     */
    /*
     * public static Preferences OPENMARKOV_KERNEL_PREFERENCES =
     * Preferences.systemRoot().node( OPENMARKOV_SYSTEM_PREFERENCES );
     */
    public static Preferences     OPENMARKOV_PREFERENCES      = Preferences.userRoot ().node (OPENMARKOV_NODE_PREFERENCES);
    public static Preferences     OPENMARKOV_DIRECTORIES      = OPENMARKOV_PREFERENCES.node ("directories");
    public static Preferences     OPENMARKOV_POSITIONS        = OPENMARKOV_PREFERENCES.node ("positions");
    public static Preferences     OPENMARKOV_COLORS           = OPENMARKOV_PREFERENCES.node ("colors");
    public static Preferences     OPENMARKOV_LANGUAGES        = OPENMARKOV_PREFERENCES.node ("languages");
    public static Preferences     OPENMARKOV_FORMATS          = OPENMARKOV_PREFERENCES.node ("formats");

    private static StringDatabase stringDatabase;

    /**
     * constructor.
     */
    private OpenMarkovPreferences ()
    {
        stringDatabase = StringDatabase.getUniqueInstance ();
    }

    /**
     * get a string <code>Preference</code> with a specific key
     * @param key - the key to get the preference
     * @param defaultValue - a default value to set if no key is found
     * @return the string value of the preference
     */
    public static String get (String key, Preferences preferences, String defaultValue)
    {
        String result = "";
        try
        {
            result = preferences.get (key, defaultValue);
        }
        catch (NullPointerException ex)
        {
            System.out.println ("wrong access to " + key);
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "wrong access to " + key),
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "wrong access to " + key),
                                           JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            System.out.println ("unexpected exception accesing key" + key);
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "unexpected exception accesing key"
                                                                     + key),
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "unexpected exception accesing key"
                                                                     + key),
                                           JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * get a boolean <code>Preference</code> with a specific key
     * @param key - the key to get the preference
     * @param preferences - the preferences node to look for
     * @param defaultBoolean - a default value to set if no key is found
     * @return the boolean value of the preference
     */
    public static boolean getBoolean (String key, Preferences preferences, boolean defaultBoolean)
    {
        boolean result = false;
        try
        {
            result = preferences.getBoolean (key, defaultBoolean);
        }
        catch (NullPointerException ex)
        {
            System.out.println ("wrong access to " + key);
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "wrong access to " + key),
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "wrong access to " + key),
                                           JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            System.out.println ("unexpected exception accesing key" + key);
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "unexpected exception accesing key"
                                                                     + key),
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "unexpected exception accesing key"
                                                                     + key),
                                           JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * get an integer <code>Preference</code> with a specific key
     * @param key - the key to get the preference
     * @param preferences - the preferences node to look for
     * @param defaultInteger - a default value to set if no key is found
     * @return the integer value of the preference
     */
    public static int getInteger (String key, Preferences preferences, int defaultInteger)
    {
        int result = 0;
        try
        {
            result = preferences.getInt (key, defaultInteger);
        }
        catch (NullPointerException ex)
        {
            System.out.println ("wrong access to " + key);
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "wrong access to " + key),
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "wrong access to " + key),
                                           JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            System.out.println ("unexpected exception accesing key" + key);
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "unexpected exception accesing key"
                                                                     + key),
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "unexpected exception accesing key"
                                                                     + key),
                                           JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * get a Color <code>Preference</code> with a specific key
     * @param key - the key to get the preference
     * @param preferences - the preferences node to look for
     * @param defaultColor - a default value to set if no key is found
     * @return the Color value of the preference
     */
    public static Color getColor (String key, Preferences preferences, Color defaultColor)
    {
        Color result = defaultColor;
        int redParam = 0;
        int greenParam = 0;
        int blueParam = 0;
        try
        {
            Preferences child = preferences.node (key);
            redParam = child.getInt ("RED", defaultColor.getRed ());
            greenParam = child.getInt ("GREEN", defaultColor.getGreen ());
            blueParam = child.getInt ("BLUE", defaultColor.getBlue ());
            result = new Color (redParam, greenParam, blueParam);
        }
        catch (NullPointerException ex)
        {
            System.out.println ("wrong access to " + key);
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "wrong access to " + key),
                                           stringDatabase.getString (ex.getMessage ()
                                                                     + "wrong access to " + key),
                                           JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            System.out.println ("unexpected exception accesing key" + key);
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null,
                                           stringDatabase.getString ("unexpected exception accesing key"
                                                                     + key + ex.getMessage ()),
                                           stringDatabase.getString ("unexpected exception accesing key"
                                                                     + key + ex.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * set a string <code>Preference</code> with a specific key
     * @param key - the key to access the preference
     * @param value - the string value to set the preference
     * @param preferences - the preference node to look for
     */
    public static void set (String key, String value, Preferences preferences)
    {
        try
        {
            preferences.put (key, value);
            preferences.sync ();
        }
        catch (BackingStoreException ex)
        {
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null, stringDatabase.getString (ex.getMessage ()),
                                           stringDatabase.getString (ex.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * set a boolean <code>Preference</code> with a specific key
     * @param key - the key to access the preference
     * @param value - the boolean value to set the preference
     * @param preferences - the preference node to look for
     */
    public static void setBoolean (String key, boolean value, Preferences preferences)
    {
        try
        {
            preferences.putBoolean (key, value);
            preferences.sync ();
        }
        catch (BackingStoreException ex)
        {
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null, stringDatabase.getString (ex.getMessage ()),
                                           stringDatabase.getString (ex.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * set a integer <code>Preference</code> with a specific key
     * @param key - the key to access the preference
     * @param value - the int value to set the preference
     * @param preferences - the preference node to look for
     */
    public static void setInteger (String key, int value, Preferences preferences)
    {
        try
        {
            preferences.putInt (key, value);
            preferences.sync ();
        }
        catch (BackingStoreException ex)
        {
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null, stringDatabase.getString (ex.getMessage ()),
                                           stringDatabase.getString (ex.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * set a Color <code>Preference</code> with a specific key
     * @param key - the key to access the preference
     * @param value - the int value to set the preference
     * @param preferences - the preference node to look for
     */
    public static void setColor (String key, Color value, Preferences preferences)
    {
        try
        {
            Preferences child = preferences.node (key);
            child.putInt ("RED", value.getRed ());
            child.putInt ("GREEN", value.getGreen ());
            child.putInt ("BLUE", value.getBlue ());
            child.sync ();
            preferences.sync ();
        }
        catch (BackingStoreException ex)
        {
            ex.printStackTrace ();
            JOptionPane.showMessageDialog (null, stringDatabase.getString (ex.getMessage ()),
                                           stringDatabase.getString (ex.getMessage ()),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * set the default preferences for OpenMarkov
     */
    public static void setDefaultPreferences ()
    {
        System.out.println ("Initializing OpenMarkov Default Preferences");
        // OPENMARKOV KERNEL PREFERENCES
        // OPENMARKOV DIRECTORIES PREFERENCES
        setDefaultDirectories ();
        // OPENMARKOV POSITIONS (for dialogs and windows) PREFERENCES
        setDefaultDimensions ();
        // OPENMARKOV COLORS PREFERENCES
        setDefaultColors ();
        // OPENMARKOV OTHER PREFERENCES
        set (PREFERENCE_LANGUAGE, System.getProperty ("user.language"), OPENMARKOV_LANGUAGES);
        // set the default initial flag
        setBoolean (INITIALIZED, true, OPENMARKOV_PREFERENCES);
    }

    /**
     * set default directories
     */
    public static void setDefaultDirectories ()
    {
        set (LAST_OPEN_DIRECTORY, "", OPENMARKOV_DIRECTORIES);
        set (LAST_OPEN_FILE_1, "", OPENMARKOV_DIRECTORIES);
        set (LAST_OPEN_FILE_2, "", OPENMARKOV_DIRECTORIES);
        set (LAST_OPEN_FILE_3, "", OPENMARKOV_DIRECTORIES);
        set (LAST_OPEN_FILE_4, "", OPENMARKOV_DIRECTORIES);
        set (LAST_OPEN_FILE_5, "", OPENMARKOV_DIRECTORIES);
        // set( STRING_RESOURCES_PATH, "openmarkov/gui/localize/",
        // OPENMARKOV_LANGUAGES );
        set (STRING_LANGUAGES_PATH, "localize", OPENMARKOV_LANGUAGES);
    }

    /**
     * set default dimensions
     */
    public static void setDefaultDimensions ()
    {
        setInteger (X_OPENMARKOV_MAIN_FRAME, 0, OPENMARKOV_POSITIONS);
        setInteger (Y_OPENMARKOV_MAIN_FRAME, 0, OPENMARKOV_POSITIONS);
        setInteger (X_OPEMARKOV_HELP_DIMENSION, 640, OPENMARKOV_POSITIONS);
        setInteger (Y_OPENMARKOV_HELP_DIMENSION, 480, OPENMARKOV_POSITIONS);
    }

    /**
     * set default color preferences
     */
    public static void setDefaultColors ()
    {
        // OPENMARKOV COLORS PREFERENCEs
        setColor (OpenMarkovPreferences.NODECHANCE_BACKGROUND_COLOR, // def
                  new Color (251, 249, 153), // cream color before it was -->
                                             // //new Color( 235, 245, 35 ),
                                             // //one type of yellow
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.NODECHANCE_FOREGROUND_COLOR, // def
                  Color.BLACK, // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.NODECHANCE_TEXT_COLOR, // def
                  Color.BLACK, // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.NODEDECISION_BACKGROUND_COLOR, // def
                  new Color (207, 227, 253), // light blue color before it was
                                             // --> //new Color( 25, 255, 255 ),
                                             // // gray color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.NODEDECISION_FOREGROUND_COLOR, // def
                  Color.BLACK, // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.NODEDECISION_TEXT_COLOR, // def
                  Color.BLACK, // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.NODEUTILITY_BACKGROUND_COLOR, // def
                  new Color (208, 230, 178), // light green color before it was
                                             // --> //new Color( 0, 125, 0 ),
                                             // //green color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.NODEUTILITY_FOREGROUND_COLOR, // def
                  Color.BLACK, // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.NODEUTILITY_TEXT_COLOR, // def
                  new Color (0, 0, 0), // black color before it was --> //new
                                       // Color( 230, 230, 230 ), //one dark
                                       // green color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_HEADER_TEXT_COLOR_1, // def
                  new Color (0, 0, 0), // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_HEADER_TEXT_COLOR_2, // def
                  new Color (0, 0, 0), // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_HEADER_TEXT_COLOR_3, // def
                  new Color (0, 0, 0), // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_HEADER_TEXT_BACKGROUND_COLOR_1, // def
                  new Color (150, 150, 150), // another light light gray color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_HEADER_TEXT_BACKGROUND_COLOR_2, // def
                  new Color (170, 170, 170), // another light gray color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_FIRST_COLUMN_FOREGROUND_COLOR, // def
                  new Color (0, 0, 0), // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_FIRST_COLUMN_BACKGROUND_COLOR, // def
                  new Color (192, 192, 192), // light gray color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_CELLS_FOREGROUND_COLOR, // def
                  new Color (0, 0, 0), // black color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.TABLE_CELLS_BACKGROUND_COLOR, // def
                  new Color (255, 255, 255), // white color
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.REVELATION_ARC_VARIABLE, // def
                  new Color (128, 0, 0), // dark red
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor (OpenMarkovPreferences.ALWAYS_OBSERVED_VARIABLE, // def
                  new Color (128, 0, 0), // dark red
                  OpenMarkovPreferences.OPENMARKOV_COLORS);
    }
}
