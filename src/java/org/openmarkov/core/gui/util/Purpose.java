/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.util;

import java.util.ArrayList;
import java.util.MissingResourceException;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class is used to encapsulate the purpose of the nodes and their
 * dependent-language strings.
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - fix initial values for fields
 */
public class Purpose
{
    /**
     * Internal names of the different purposes.
     */
    private static ArrayList<String> list = null;

    /**
     * This method adds all the purposes.
     */
    private static void fillList ()
    {
        if (list == null)
        {
            list = new ArrayList<String> ();
            list.add ("");
            list.add ("cost");
            list.add ("effectiveness");
            list.add ("treatment");
            list.add ("riskfactor");
            list.add ("symptom");
            list.add ("sign");
            list.add ("test");
            list.add ("diseaseanomaly");
            list.add ("auxiliary");
            list.add ("other");
        }
    }

    /**
     * It retrieves the dependent-language string of the desired purpose. If the
     * purpose hasn't a dependent-language string (because this purpose isn't a
     * registered one), the returned string is the purpose itself.
     * @param element name of the purpose.
     * @return a string that represents the purpose in the actual language.
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
                return StringDatabase.getUniqueInstance ().getString ("purpose." + element
                                                                              + ".Text");
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
    public static String[] getListStrings (boolean original)
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
        if (original)
        {
            list.toArray (strings);
        }
        else for (i = 0; i < l; i++)
        {
            strings[i] = getString (list.get (i));
        }
        return strings;
    }

    /**
     * This method returns the purpose sited in the specified index in the list.
     * If the index is out of range (index < 0 || index > list size) the null is
     * returned.
     * @param index element of the list of purposes.
     * @return a string that contains the name of the specified purpose.
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
     * This method returns the index in the list of the purpose passed as
     * parameter. If the parameter doesn't match any element of the list, then
     * the last index is returned.
     * @param element name of the purpose to search.
     * @return the index in the list of the purpose.
     */
    public static int getIndex (String element)
    {
        if (list == null)
        {
            fillList ();
        }
        int index = list.indexOf (element);
        if (index == -1)
        {
            index = list.size () - 1;
        }
        return index;
    }
}
