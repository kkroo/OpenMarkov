/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.localize;

import java.awt.Component;
import java.awt.Container;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.gui.component.LastRecentFilesMenuItem;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.menutoolbar.toolbar.ZoomComboBox;
import org.openmarkov.core.gui.window.mdi.MDIMenu;
import org.openmarkov.core.gui.window.message.NonEditableTextArea;

/**
 * This class creates new string resources with the recorded language.
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo adding the listeners for i18n support adding the
 *          get/set for the locale and setting internal StringResource variables
 *          for improving performance
 * @version 1.2. jlgozalo including ZoomComboBox component
 * @version 1.3. ibermejo challenge everything
 */
public class StringDatabase
{
    /**
     * Default language.
     */
    private static final String       DEFAULT_LANGUAGE = OpenMarkovPreferences.get (OpenMarkovPreferences.PREFERENCE_LANGUAGE,
                                                                                    OpenMarkovPreferences.OPENMARKOV_LANGUAGES,
                                                                                    System.getProperty ("user.language"));
    /**
     * Unique instance of this class.
     */
    private static StringDatabase     instance         = null;
    /**
     * Language to use.
     */
    private String                    language         = DEFAULT_LANGUAGE;
    /**
     * Locale to use
     */
    private Locale                    locale           = null;
    /**
     * Map containing all the bundles
     */
    private Map<String, StringBundle> bundles          = null;
    // Create the listener list
    private EventListenerList         listenerList     = null;

    /**
     * This constructor initializes the object with the language of the class.
     * Then creates all the resource bundles to check if the language is
     * available for all of them. If this language is not available for all, the
     * default one is used.
     */
    private StringDatabase ()
    {
        setLocale (new Locale (language));
        bundles = getAllBundles ();
        listenerList = new EventListenerList ();
        if(bundles.isEmpty())
        {
            setLanguage("en");
        }
    }

    /**
     * Returns the unique instance of this class. If the instance doesn't exist,
     * then a new instance is initialized.
     * @return the unique instance.
     */
    public static StringDatabase getUniqueInstance ()
    {
        if (instance == null)
        {
            instance = new StringDatabase ();
        }
        return instance;
    }

    /**
     * Sets the language to a new one.
     * @param newLanguage new language.
     */
    public void setLanguage (String newLanguage)
    {
        if (!newLanguage.equals (language))
        {
            language = (newLanguage.equals ("es")) ? "es" : "en";
            setLocale (getLocaleByLanguage (language));
            resetBundles ();
            fireLocaleChangeEvent (new LocaleChangeEvent (this, newLanguage));
            OpenMarkovPreferences.set (OpenMarkovPreferences.PREFERENCE_LANGUAGE, newLanguage,
                                       OpenMarkovPreferences.OPENMARKOV_LANGUAGES);
        }
    }

    private Locale getLocaleByLanguage (String language)
    {
        Locale locale = Locale.ENGLISH;
        if (language.equals (Locale.ENGLISH.getLanguage ()))
        {
            locale = Locale.ENGLISH;
        }
        else if (language.equals ("es"))
        {
            locale = new Locale ("es");
        }
        else
        {
            // System.out.println("LocaleChangeEvent failure for locale "
            // + locale.toString() + ": not defined");
            // System.out.println("Setting english as default locale...");
            locale = Locale.ENGLISH;
        }
        return locale;
    }

    /**
     * @return the language
     */
    public String getLanguage ()
    {
        return language;
    }

    /**
     * @return the locale
     */
    public Locale getLocale ()
    {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale (Locale newLocale)
    {
        locale = newLocale;
    }

    /**
     * Returns a string resource linked to the file given as parameter. The
     * value of the 'language' variable is used. If it is null or empty, the
     * language of the system is taken into account. If the system's language
     * isn't available, the default language is English.
     * @param resourceFile file that contains the resource strings.
     * @return a resource bundle linked to the file.
     */
    public StringBundle getBundle (String resourceFile)
    {
        StringBundle stringBundle = null;
        XMLResourceBundle bundle = null;
        String file = "localize/" + resourceFile;
        try
        {
            bundle = (XMLResourceBundle) createXMLResourceBundle (file, locale);
        }
        catch (MissingResourceException e)
        {
            System.out.println ("WARNING: Resource bundle " + resourceFile
                                + " could not be found for locale '" + locale
                                + "'. English will be used instead");
            setLanguage ("en");
            try
            {
                bundle = (XMLResourceBundle) createXMLResourceBundle (file, locale);
            }
            catch (MissingResourceException e1)
            {
                throw new MissingResourceException ("Any of the " + resourceFile.toLowerCase ()
                                                    + " resource string files is missing",
                                                    StringDatabase.class.getName (),
                                                    getLocale ().getLanguage ());
            }
        }
        stringBundle = new StringBundle (bundle);
        return stringBundle;
    }

    public Map<String, StringBundle> getAllBundles ()
    {
        Map<String, StringBundle> bundleMap = new LinkedHashMap<> ();
        String localeSuffix = "_" + locale.getLanguage ();
        String classPath = System.getProperty ("java.class.path", ".");
        String[] classPathElements = classPath.split (File.pathSeparator);
        for (String element : classPathElements)
        {
            File classpathElement = new File (element);
            
            if(classpathElement.isDirectory ())
            {
                File localizeFolder =  new File (classpathElement.getAbsolutePath () + File.separator + "localize");
                if(localizeFolder.listFiles () != null)
                {
                    for (final File fileEntry : localizeFolder.listFiles ())
                    {
                        if (fileEntry.isFile ())
                        {
                            if (fileEntry.getName ().endsWith (".xml"))
                            {
                                String baseName = FilenameUtils.getBaseName (fileEntry.getName ());
                                if (baseName.endsWith (localeSuffix))
                                {
                                    baseName = baseName.substring (0,
                                                                   baseName.length ()
                                                                           - localeSuffix.length ());
                                    bundleMap.put (baseName, getBundle (baseName));
                                }
                            }
                        }
                    }
                }
            }else{ // it is a jar file
                ZipFile zipFile;
                try
                {
                    zipFile = new ZipFile (classpathElement.getAbsolutePath ());
                    Enumeration<? extends ZipEntry> zipEntryEn = (Enumeration<? extends ZipEntry>) zipFile.entries ();
                    while (zipEntryEn.hasMoreElements ())
                    {
                        ZipEntry aZipEntry = (ZipEntry) zipEntryEn.nextElement ();
                        if (aZipEntry.getName ().startsWith ("localize/") && aZipEntry.getName ().endsWith (".xml"))
                        {
                            String baseName = FilenameUtils.getBaseName (aZipEntry.getName ());
                            if (baseName.endsWith (localeSuffix))
                            {
                                baseName = baseName.substring (0,
                                                               baseName.length ()
                                                                       - localeSuffix.length ());
                                bundleMap.put (baseName, getBundle (baseName));
                            }
                        }
                    }
                    zipFile.close ();
                }
                catch (IOException e)
                {
                    e.printStackTrace ();
                }   
            }            
        }
        return bundleMap;
    }

    /**
     * @param file
     * @param locale
     * @return An instance of ResourceBundle considering that properties files
     *         are in XML format.
     */
    public ResourceBundle createXMLResourceBundle (String file, Locale locale)
    {
        ResourceBundle bundle;
        bundle = ResourceBundle.getBundle (file, locale, new ResourceBundle.Control ()
            {
                public java.util.List<String> getFormats (String baseName)
                {
                    if (baseName == null) throw new NullPointerException ();
                    return Arrays.asList ("xml");
                }

                public ResourceBundle newBundle (String baseName,
                                                 Locale locale,
                                                 String format,
                                                 ClassLoader loader,
                                                 boolean reload)
                    throws IllegalAccessException,
                    InstantiationException,
                    IOException
                {
                    if (baseName == null || locale == null || format == null || loader == null) throw new NullPointerException ();
                    ResourceBundle bundle = null;
                    if (format.equals ("xml"))
                    {
                        String bundleName = toBundleName (baseName, locale);
                        String resourceName = toResourceName (bundleName, format);
                        InputStream stream = null;
                        if (reload)
                        {
                            URL url = loader.getResource (resourceName);
                            if (url != null)
                            {
                                URLConnection connection = url.openConnection ();
                                if (connection != null)
                                {
                                    // Disable caches to get fresh data for
                                    // reloading.
                                    connection.setUseCaches (false);
                                    stream = connection.getInputStream ();
                                }
                            }
                        }
                        else
                        {
                            stream = loader.getResourceAsStream (resourceName);
                        }
                        if (stream != null)
                        {
                            BufferedInputStream bis = new BufferedInputStream (stream);
                            bundle = new XMLResourceBundle (bis);
                            bis.close ();
                        }
                    }
                    return bundle;
                }
            });
        return bundle;
    }

    // This methods allows classes to register for LocaleChangeEvent
    public void addLocaleChangeListener (LocaleChangeListener listener)
    {
        listenerList.add (LocaleChangeListener.class, listener);
    }

    // This methods allows classes to unregister for LocaleChangeEvent
    public void removeLocaleChangeListener (LocaleChangeListener listener)
    {
        listenerList.remove (LocaleChangeListener.class, listener);
    }

    /**
     * This private class is used to fire LocaleChangeEvent
     * @param evt - event to manage for locale change
     */
    protected void fireLocaleChangeEvent (LocaleChangeEvent evt)
    {
        Object[] listeners = listenerList.getListenerList ();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2)
        {
            if (listeners[i] == LocaleChangeListener.class)
            {
                ((LocaleChangeListener) listeners[i + 1]).processLocaleChange (evt);
            }
        }
    }

    /**
     * reset the StringResource to null
     */
    private void resetBundles ()
    {
        bundles.clear ();
        bundles = getAllBundles ();
    }

    /**
     * Method to change behaviours in a container using Java Reflection API. All
     * the different objects must comply with a strictly naming convention to
     * prevent string not to be updated properly. When the objects will be
     * created by programmers, the "name" property of the object must be set as
     * "ContainerOwner.ComponentVariableName" where the ContainerOwner is the
     * name of the container where the component belongs to. All other objects
     * must implement there own listeners.
     * @param c the container to be updated
     */
    public void allComponentsUpdateSetText (Container c)
    {
        String temp = "";
        Component[] listComponents = c.getComponents ();
        for (Component item : listComponents)
        {
            if (item instanceof JButton)
            {
                if (!((JButton) item).getText ().equals (""))
                {
                    temp = ((JButton) item).getName () + ".Text.Label";
                    ((JButton) item).setText (getString (temp));
                }
            }
            else if (item instanceof JDialog)
            {
                temp = ((JDialog) item).getName () + ".Title.Text";
                ((JDialog) item).setTitle (getString (temp));
                allComponentsUpdateSetText ((Container) item);
            }
            else if (item instanceof JFrame)
            {
                temp = ((JFrame) item).getName () + ".Title.Text";
                ((JFrame) item).setTitle (getString (temp));
                allComponentsUpdateSetText ((Container) item);
            }
            else if (item instanceof JLabel)
            {
                temp = ((JLabel) item).getName () + ".Text";
                ((JLabel) item).setText (getString (temp));
            }
            else if (item instanceof MDIMenu)
            {
                // doNothing
            }
            else if (item instanceof JMenu)
            {
                temp = ((JMenu) item).getName () + ".Label";
                ((JMenu) item).setText (getString (temp));
                temp = ((JMenu) item).getName () + ".Mnemonic";
                ((JMenu) item).setMnemonic (getString (temp).charAt (0));
                allComponentsUpdateSetText ((Container) item);
            }
            else if (item instanceof JMenuItem)
            {
                temp = ((JMenuItem) item).getName () + ".Label";
                ((JMenuItem) item).setText (getString (temp));
                temp = ((JMenuItem) item).getName () + ".Mnemonic";
                ((JMenuItem) item).setMnemonic (getString (temp).charAt (0));
            }
            else if (item instanceof NonEditableTextArea)
            {
                // doNothing
            }
            else if (item instanceof JPanel)
            {
                allComponentsUpdateSetText ((Container) item);
            }
            else if (item instanceof JTextArea)
            {
                temp = ((JTextArea) item).getName () + ".Text";
                ((JTextArea) item).setText (getString (temp));
            }
            else if (item instanceof JTextField)
            {
                temp = ((JTextField) item).getName () + ".Text";
                ((JTextField) item).setText (getString (temp));
            }
            else if (item instanceof ZoomComboBox)
            {
                temp = (String) ((ZoomComboBox) item).getSelectedItem ();
                ((ZoomComboBox) item).setSelectedItem (temp);
            }
            else if (item instanceof Container)
            {
                allComponentsUpdateSetText ((Container) item);
            }
            else
            {
                // do nothing for non registered objects as
                // those objects must implement the listener.
                // if required this method can be expanded
            }
        } // end-for
        if (c instanceof JMenu)
        {
            temp = ((JMenu) c).getName () + ".Label";
            ((JMenu) c).setText (getString (temp));
            // extract JMenuItems
            int itemCount = ((JMenu) c).getItemCount ();
            for (int i = 0; i < itemCount; i++)
            {
                Component item = ((JMenu) c).getItem (i);
                if (item instanceof JMenu)
                {
                    temp = ((JMenu) item).getName () + ".Label";
                    ((JMenu) item).setText (getString (temp));
                    temp = ((JMenu) item).getName () + ".Mnemonic";
                    ((JMenu) item).setMnemonic (getString (temp).charAt (0));
                    allComponentsUpdateSetText ((Container) item);
                }
                else if (item instanceof LastRecentFilesMenuItem)
                {
                    // do not change
                }
                else if (item instanceof JMenuItem)
                {
                    temp = ((JMenuItem) item).getName () + ".Label";
                    ((JMenuItem) item).setText (getString (temp));
                    temp = ((JMenuItem) item).getName () + ".Mnemonic";
                    ((JMenuItem) item).setMnemonic (getString (temp).charAt (0));
                }
                else
                {
                    // only JSeparators are entering here!!!!
                }
            }
        }
    }

    public String getString (String key)
    {
        Iterator<StringBundle> bundleIterator = bundles.values ().iterator ();
        boolean found = false;
        String value = null;
        while (!found && bundleIterator.hasNext ())
        {
            StringBundle bundle = bundleIterator.next ();
            value = bundle.getString (key);
            found = value != null;
        }
        if (value == null)
        {
            value = ">>> " + key + " <<<";
        }
        return value;
    }

    public String getString (String bundle, String key)
    {
        String value = bundles.get (bundle).getString (key);
        if (value == null)
        {
            value = ">>> " + key + " <<<";
        }
        return value;
    }

    /**
     * This method returns the requested string resource, replacing each '~' by
     * an element of the array. The number of '~' replaced depends on the number
     * of elements of the array.
     * @param key the key of the desired string.
     * @param strings strings that will replace the '~'.
     * @return the string associated with the key. if the resource doesn't
     *         exist, then a special string is returned.
     */
    public String getFormattedString (String key, String... strings)
    {
        String result = "";
        String parameter = "";
        boolean flag = true;
        int i = 0;
        int l = 0;
        int index = 0;
        final String diacritic = "~";
        try
        {
            result = getString (key);
            if (strings != null)
            {
                l = strings.length;
                while (flag && (i < l))
                {
                    if ((index = result.indexOf (diacritic, index)) >= 0)
                    {
                        parameter = strings[i++];
                        if (parameter == null)
                        {
                            parameter = "";
                        }
                        result = result.substring (0, index)
                                 + result.substring (index).replaceFirst (diacritic, parameter);
                        index += parameter.length ();
                    }
                    else
                    {
                        flag = false;
                    }
                }
            }
        }
        catch (MissingResourceException e1)
        {
            result = ">>> " + key + " <<<";
        }
        return result;
    }
}
