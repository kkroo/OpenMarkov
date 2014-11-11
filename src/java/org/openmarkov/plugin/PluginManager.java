/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.plugin;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.plugin.service.PluginManagerIF;


/**
 * This class is an implementation for PluginsManager interface.
 * @(#)PluginsManager.java    1.0    15/09/2011 19:08:10
 * @author  jvelez
 * @version 1.0
 *
 * Development Environment        :  Eclipse
 * Name of the File               :  PluginsManager.java
 * Creation/Modification History  :
 *
 * jvelez     15/09/2011 19:08:10      Created.
 * Gigaesfera CO.
 * 
 */
public class PluginManager
	implements PluginManagerIF
{
	private List<Class<?>> plugins; 
	
	/**
	 * Constructor for PluginsManager.
	 */
	public PluginManager () 
	{
		super ();
		this.plugins = new ArrayList<Class<?>> (); 
	}
	
	/**
	 * Adds a new plugin to the manager.  
	 * @param plugin the plugin class to add. 
	 */
	public void addPlugin (Class<?> plugin)
	{
		plugins.add (plugin);
	}

	/**
	 * Removes a plugin from the manager.  
	 * @param plugin the plugin class to remove. 
	 */
	public void removePlugin (Class<?> plugin)
	{
		plugins.remove (plugin);
	}
	
	/**
	 * Removes all plugins from the manager.
	 */
	public void clearPlugins ()
	{
		plugins.clear ();
	}
	
	/**
	 * Indicates whether a plugin is contained. 
	 * @param plugin The plugin class to find.
	 * @return true if the plugin is contained. 
	 */
	public boolean containsPlugin (Class<?> plugin)
	{
		return plugins.contains (plugin);
	}
	
	/**
     * Returns the hashCode.
     * @return the hashCode.
     */
    @Override
    public int hashCode ()
    {
        return 31 * plugins.hashCode();
    }

    /**
     * Indicates whether the other object is equals to this one.
     * @return true if the other object is equals to this one.
     */  
    @Override
    public boolean equals (Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (other instanceof PluginManager) 
        {
        	PluginManager aPluginsManager = (PluginManager) other;
            return plugins.equals(aPluginsManager.plugins);           
        }
        return false;
    }
    
    /**
    * Returns the String representing this object.
    * @return the String representing this object.
    */
    @Override
    public String toString () 
    {
        StringBuffer strBuffer = new StringBuffer ();
        strBuffer.append ("[PluginsManager] - {");
        strBuffer.append ("plugins = ");
        strBuffer.append (plugins);
        strBuffer.append ("}");
        
        return strBuffer.toString ();            
    }
}
