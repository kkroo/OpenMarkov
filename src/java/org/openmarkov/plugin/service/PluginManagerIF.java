/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.plugin.service;

/**
 * This interface is a contract for plugin managers.
 * A plugin manager manages a set of plugins. 
 * @(#)PluginsManagerIF.java    1.0    15/09/2011 19:08:10
 * @author  jvelez
 * @version 1.0
 *
 * Development Environment        :  Eclipse
 * Name of the File               :  PluginsManagerIF.java
 * Creation/Modification History  :
 *
 * jvelez     15/09/2011 19:08:10      Created.
 * Gigaesfera CO.
 * 
 */
public interface PluginManagerIF
{
	/**
	 * Adds a new plugin to the manager.  
	 * @param plugin the plugin class to add. 
	 */
	public void addPlugin (Class<?> plugin);

	/**
	 * Removes a plugin from the manager.  
	 * @param plugin the plugin class to remove. 
	 */
	public void removePlugin (Class<?> plugin);
	
	/**
	 * Removes all plugins from the manager.
	 */
	public void clearPlugins ();
	
	/**
	 * Indicates whether a plugin is contained. 
	 * @param plugin The plugin class to find.
	 * @return true if the plugin is contained. 
	 */
	public boolean containsPlugin (Class<?> plugin);
}
