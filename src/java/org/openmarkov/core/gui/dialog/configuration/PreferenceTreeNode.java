/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
/**
 * 
 */

package org.openmarkov.core.gui.dialog.configuration;

import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * PreferenceTreeNode is defining the node behaviours in a Preferences Tree
 * @author jlgozalo
 * @version 1.0 28 Aug 2009
 */
@SuppressWarnings("serial")
public class PreferenceTreeNode extends DefaultMutableTreeNode
{
    Preferences pref;
    String      nodeName;
    String[]    childrenNames;

    public PreferenceTreeNode (Preferences pref)
        throws BackingStoreException
    {
        this.pref = pref;
        childrenNames = pref.childrenNames ();
    }

    public Preferences getPrefObject ()
    {
        return pref;
    }

    public boolean isLeaf ()
    {
        return ((childrenNames == null) || (childrenNames.length == 0));
    }

    public int getChildCount ()
    {
        return childrenNames.length;
    }

    /**
     * Removes child at index @param childIndex Used to hide a child in
     * displayed tree
     * @author myebra
     * @param childIndex
     */
    public void removeChildAt (int childIndex)
    {
        if (childIndex < childrenNames.length)
        {
            ArrayList<String> newChildrenNames = new ArrayList<String> ();
            for (int i = 0; i < childrenNames.length; i++)
            {
                if (i != childIndex)
                {
                    newChildrenNames.add (childrenNames[i]);
                }
            }
            String[] newChildrenNames2 = new String[childrenNames.length - 1];
            for (int i = 0; i < newChildrenNames.size (); i++)
            {
                newChildrenNames2[i] = newChildrenNames.get (i);
            }
            this.childrenNames = newChildrenNames2;
        }
    }

    public TreeNode getChildAt (int childIndex)
    {
        if (childIndex < childrenNames.length)
        {
            try
            {
                PreferenceTreeNode child = new PreferenceTreeNode (
                                                                   pref.node (childrenNames[childIndex]));
                return child;
            }
            catch (BackingStoreException e)
            {
                e.printStackTrace ();
                JOptionPane.showMessageDialog (null,
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
                return new DefaultMutableTreeNode ("Problem Child!");
            }
        }
        return null;
    }

    public String toString ()
    {
        String name = pref.name ();
        if ((name == null) || ("".equals (name)))
        { // if root node
            name = "System Preferences";
            if (pref.isUserNode ()) name = "User Preferences";
        }
        return name;
    }
}
