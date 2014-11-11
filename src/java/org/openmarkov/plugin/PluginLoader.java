/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.plugin;

import static org.openmarkov.plugin.Filter.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.openmarkov.plugin.service.FilterIF;
import org.openmarkov.plugin.service.PluginException;
import org.openmarkov.plugin.service.PluginLoaderIF;


/**
 * This class is an implementation of  PluginsLoaderIF interface.
 * @(#)PluginLoader.java    1.0    15/09/2011 19:08:10
 * @author  jvelez
 * @version 1.0
 *
 * Development Environment        :  Eclipse
 * Name of the File               :  PluginLoader.java
 * Creation/Modification History  :
 *
 * jvelez     15/09/2011 19:08:10      Created.
 * Gigaesfera CO.
 * 
 */
public class PluginLoader 
	implements PluginLoaderIF
{
	private ClassLoader classLoader;
	
	/**
	 * Constructor for PluginsLoader.
	 */
	public PluginLoader ()
	{
		super ();
		this.classLoader = ClassLoader.getSystemClassLoader ();
	}
		
	/**
	 * Returns a plugin from the system environment.  
	 * @param name The qualified name of the plugin class.
	 * @return a plugin from the system environment.
	 */
	public Class<?> loadPlugin (String name) 
	throws PluginException
	{
		try 
		{
			Class<?> aClass = (Class<?>) classLoader.loadClass(name);
			return aClass;
		} 
		catch (Exception e) 
		{
			throw new PluginException ("Unable to load plugin [" + name + "]", e);
		}
	}

	/**
	 * Returns all plugins from the system environment.  
	 * @param the plugins filter to select plugins.
	 * @return all plugins from the system environment.
	 */
	public List<Class<?>> loadAllPlugins (FilterIF filter)
	throws PluginException
	{ 
		List<Class<?>> classes   = new ArrayList<Class<?>> ();
		String         classPath = System.getProperty ("java.class.path", ".");
		String[]       classPathElements = classPath.split (File.pathSeparator);
		for (String element : classPathElements) {
			List<String> aResources = getResources (element); 
		    for (String aResource : aResources) {
		        if(aResource.startsWith ("org.openmarkov"))
		        {
        	    	try {
        	    		Class<?> aClass = (Class<?>) classLoader.loadClass(aResource);
        	    		if (filter.checkPlugin(aClass))
        	    			classes.add (aClass);	    			
        	    	}
        	    	catch (Exception e) {}
		        }
		    }
		}
		return classes;
	}
	
	/**
	 * Returns all plugins from the system environment.  
	 * @return all plugins from the system environment.
	 */
	public List<Class<?>> loadAllPlugins ()
	throws PluginException
	{ 
		FilterIF filter = filter ().end ();
		return  loadAllPlugins (filter);
	}
	
	/**
	 * Returns all resources matching with a pattern type.
	 * @param classpath the path where searches starts. 
	 * @return a list of resource names.
	 */
	 private List<String> getResources (String classpath)
	 {
		 List<String> resources = new ArrayList<String> ();
		 File aFile = new File (classpath);
		 if (aFile.isDirectory ()) {
			 List<String> aResources = getResourcesFromDirectory (aFile, aFile);
			 resources.addAll (aResources);
		 }
		 else {
			 List<String> aResources = getResourcesFromJar (aFile);
			 resources.addAll (aResources);
	 	 }
		return resources;
	 }
	 
	 /**
	  * Returns all resources matching with a pattern type from a jar file
	  * @param file the jar file.
	  * @param pattern the resource pattern.
	  * @return a list of resource names.
	  */
	 @SuppressWarnings("unchecked")
	 private List<String> getResourcesFromJar (File file)
	 {
		 List<String> resources = new ArrayList<String> ();
		 try
		 {
			 ZipFile zipFile = new ZipFile (file);
			 Enumeration<ZipEntry> zipEntryEn = (Enumeration<ZipEntry>) zipFile.entries ();
			 while (zipEntryEn.hasMoreElements ()) {
			    ZipEntry aZipEntry = (ZipEntry) zipEntryEn.nextElement ();
			    String   aFileName = aZipEntry.getName ();
			    if(aFileName.endsWith (".class")) {
			    	String aClassName = aFileName.substring(0, aFileName.length() - 6);
			    	aClassName = aClassName.replace('/', '.');
			    	resources.add (aClassName);
			    }
			 }
			 zipFile.close ();
		 } catch (Exception e) {}
		 return resources;
	 }
	 
	 /**
	  * Returns all resources matching with a pattern type from a directory.
	  * @param directory the directory.
	  * @param classpath the classpath.
	  * @return a list of resource names.
	  */
	 private List<String> getResourcesFromDirectory (File directory, File classpath) 
	 {
		 List<String> resources = new ArrayList<String> ();
		 File[] files = directory.listFiles ();
		 for (File aFile : files) {
		    if(aFile.isDirectory ()) {
		    	 List<String> aResources = getResourcesFromDirectory (aFile, classpath);
				 resources.addAll (aResources);
		    } 
		    else {
		    	try {
		    		String fileName = aFile.getCanonicalPath ();
		    		String pathName = classpath.getCanonicalPath ();
		    		if (fileName.endsWith (".class")) {
		    			String aClassName = fileName.substring(pathName.length() + 1, fileName.length() - 6);
		    			aClassName = aClassName.replace(File.separatorChar, '.');
		    			resources.add(aClassName);
		    		}		            	
		    	} catch (Exception e) {}        
		    }
		 }
		 return resources;
	 }
}
