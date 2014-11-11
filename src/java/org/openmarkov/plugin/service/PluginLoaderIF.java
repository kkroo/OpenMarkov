/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.plugin.service;

import java.util.List;



/**
 * This interface is a contract for plugin loaders.
 * A plugin loader is responsible for find and load plugins. 
 * @(#)PluginsLoaderIF.java    1.0    15/09/2011 19:08:10
 * @author  jvelez
 * @version 1.0
 *
 * Development Environment        :  Eclipse
 * Name of the File               :  PluginsLoaderIF.java
 * Creation/Modification History  :
 *
 * jvelez     15/09/2011 19:08:10      Created.
 * Gigaesfera CO.
 * 
 */
public interface PluginLoaderIF
{	
	/**
	 * Returns a plugin from the system environment.  
	 * @param name The qualified name of the plugin class.
	 * @return a plugin from the system environment.
	 */
	public Class<?> loadPlugin (String name)
	throws PluginException;

	/**
	 * Returns all plugins from the system environment.  
	 * @param the plugins filter to select plugins.
	 * @return all plugins from the system environment.
	 */
	public List<Class<?>> loadAllPlugins (FilterIF filter)
	throws PluginException;
	
	/**
	 * Returns all plugins from the system environment.  
	 * @return all plugins from the system environment.
	 */
	public List<Class<?>> loadAllPlugins ()
	throws PluginException;
}
