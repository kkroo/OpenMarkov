/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;
import org.openmarkov.plugin.service.PluginLoaderIF;

public class ToolPluginManager
{
    private static ToolPluginManager instance = null;
    private PluginLoaderIF pluginsLoader; 
    private List<Class<?>> plugins;
    

    /**
     * Constructor for ToolsMenuManager.
     */
    private ToolPluginManager ()
    {
        super ();
        this.pluginsLoader = new PluginLoader ();
        this.plugins = findAllToolPlugins ();
    }    
    
    
    public static ToolPluginManager getInstance()
    {
        if(instance == null)
        {
            instance = new ToolPluginManager ();
        }
        return instance;
    }
   
  
    /**
     * Finds all learning tools menu items. 
     * @return a list of tools menu items.
     */
    private final  List<Class<?>> findAllToolPlugins ()
    {
        try
        {
            FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy (ToolPlugin.class);
            return pluginsLoader.loadAllPlugins (filter);          
        }
        catch (Exception e) {}
        return null;
    }

    public ArrayList<JMenuItem> getMenuItems ()
    {
        ArrayList<JMenuItem> menuItems = new ArrayList<JMenuItem> ();
        try
        {
            for (Class<?> plugin : plugins) {
                ToolPlugin lAnnotation = plugin.getAnnotation (ToolPlugin.class);
                JMenuItem menuItem = new LocalizedMenuItem (lAnnotation.name (),
                                                            lAnnotation.command ());
                menuItems.add (menuItem);
            }
        }
        catch (Exception e) {}
        
        return menuItems;
    }  
    
    public void processCommand(String command, JFrame parent) 
    {
        for(Class<?> plugin : plugins)
        {
            ToolPlugin lAnnotation = plugin.getAnnotation (ToolPlugin.class);
            if(lAnnotation.command ().equals (command))
            {
                JDialog dialog = null;
                try
                {
                    dialog = (JDialog) plugin.getConstructor (JFrame.class).newInstance (parent);
                    dialog.setEnabled (true);
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace ();
                }
            }
        }
    }
}
