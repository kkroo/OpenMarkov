/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.localize;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

/**
 * This class is used to encapsulate the languages supported by OPENMARKOV
 * @author jlgozalo
 * @version 1.0
 */
public class Languages
{
    /**
     * Internal names of the different languages
     */
    private static List<String>   list           = null;
    /**
     * Internal short locale of the different languages
     */
    private static List<String>   shortLocale    = null;
    /**
     * String database
     */
    private static StringDatabase stringDatabase = StringDatabase.getUniqueInstance ();

    /**
     * This method adds all the languages supported by OPENMARKOV TODO read from
     * an external file configuration
     */
    private static void fillList ()
    {
        if (list == null)
        {
            list = new ArrayList<String> ();
            list.add ("Languages.English");
            list.add ("Languages.Spanish");
            shortLocale = new ArrayList<String> ();
            shortLocale.add ("Languages.English");
            shortLocale.add ("Languages.Spanish");
        }
    }

    /**
     * It retrieves the dependent-language string of the desired language. If
     * the language hasn't a dependent-language string (because this language
     * isn't a registered one), the returned string is the language itself.
     * @param element name of the language
     * @return a string that represents the language name in the actual language
     *         bundle
     */
    public static String getString (String element)
    {
        if (element.equals (""))
        {
            return element;
        }
        else
        {
            try
            {
                return stringDatabase.getString (element + ".LongName.Text.Label");
            }
            catch (MissingResourceException e)
            {
                return element;
            }
        }
    }

    /**
     * This method returns an array of strings, each one has the
     * dependent-language string of each purpose.
     * @return an array that contains a list of string that contains the
     *         different purposes.
     */
    public static String[] getStringList ()
    {
        String[] strings = null;
        int i = 0;
        int l = 0;
        if (list == null)
        {
            fillList ();
        }
        l = list.size ();
        strings = new String[l];
        for (i = 0; i < l; i++)
        {
            strings[i] = getString (list.get (i));
        }
        return strings;
    }

    /**
     * This method returns the language sited in the specified index in the
     * list. If the index is out of range (index < 0 || index > list size) the
     * null is returned.
     * @param index element of the list of language.
     * @return a string that contains the name of the specified language.
     */
    public static String getByIndex (int index)
    {
        if (list == null)
        {
            fillList ();
        }
        try
        {
            return list.get (index);
        }
        catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /**
     * This method returns the index in the list of the language passed as
     * parameter. If the parameter doesn't match any element of the list, then
     * the last index is returned.
     * @param element name of the language to search.
     * @return the index in the list of the language.
     */
    public static int getIndex (String element)
    {
        int index = list.indexOf (element);
        if (index == -1)
        {
            index = list.size () - 1;
        }
        return index;
    }

    /**
     * It retrieves the dependent-language short string of the desired language.
     * If the language hasn't a dependent-language string (because this language
     * isn't a registered one), the returned string is the language itself.
     * @param element name of the language
     * @return a short string that represents the language name in the actual
     *         language bundle
     */
    public static String getShortString (String element)
    {
        if (element.equals (""))
        {
            return element;
        }
        else
        {
            try
            {
                return stringDatabase.getString (element + ".ShortName.Text.Label");
            }
            catch (MissingResourceException e)
            {
                return element;
            }
        }
    }

    /**
     * This method returns the language sited in the specified index in the
     * short name list. If the index is out of range (index < 0 || index > list
     * size) the null is returned.
     * @param index element of the short name list of language.
     * @return a string that contains the short name of the specified language.
     */
    public static String getShortNameByIndex (int index)
    {
        if (shortLocale == null)
        {
            fillList ();
        }
        try
        {
            return stringDatabase.getString (shortLocale.get (index) + ".ShortName.Text.Label");
        }
        catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /**
     * This method prints an array of strings, each one has the
     * dependent-language string of each language
     * @return an array that contains a list of string that contains the
     *         different purposes.
     */
    public String toString ()
    {
        String strings = "";
        int i = 0;
        int l = 0;
        if (list == null)
        {
            fillList ();
        }
        l = list.size ();
        strings = "Languages = ";
        for (i = 0; i < l; i++)
        {
            strings = strings + "\n" + getString (list.get (i));
        }
        return strings + "\n";
    }
}