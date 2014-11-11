/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.io.format.annotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmarkov.core.io.ProbNetReader;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;

/**
 * This class is the manager of the format annotations. Detects the plugins with FormatType 
 * annotations.
 * @see FormatType
 * @author mpalacios
 *
 */
public class FormatManager
{
	private static FormatManager instance = null;

	/**
	 * The Reader role
	 */
	private String roleReader = "Reader";
	
	/**
	 * The writer role
	 */
	private String roleWriter = "Writer";
	
	/**
	 * Reader classes
	 */
	private Map<String, Class<?>> readerClasses; 

	/**
     * Writer classes
     */
    private Map<String, Class<?>> writerClasses;
    
    /**
     * Reader instances
     */
    private Map<String, ProbNetReader> readerInstances; 
    
    /**
     * Writer instances
     */
    private Map<String, ProbNetWriter> writerInstances; 

    
	/**
	 * Gets a FormatManager instance
	 */
	private FormatManager ()
	{
		super ();
		this.readerClasses = new HashMap<> ();
        this.writerClasses = new HashMap<> ();
        this.readerInstances = new HashMap<> ();
        this.writerInstances = new HashMap<> ();
        
		for(Class<?> plugin : findAllFormatPlugins ())
		{
		    FormatType lAnnotation = plugin.getAnnotation (FormatType.class);
            if(lAnnotation.role().equals(roleReader))
            {
                readerClasses.put (lAnnotation.extension (), plugin);
            }
            if(lAnnotation.role().equals(roleWriter))
            {
                writerClasses.put (lAnnotation.extension (), plugin);
            }
		}
	}    


	public static FormatManager getInstance()
	{
		if(instance == null)
		{
			instance = new FormatManager ();
		}
		return instance;
	}


	/**
	 * This method gets all the plugins with FormatType annotations
	 * @return a list with the plugins detected with FormatType annotations.
	 */
	private final  List<Class<?>> findAllFormatPlugins ()
	{
	    PluginLoader pluginsLoader = new PluginLoader ();
		try
		{
			FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy (FormatType.class);
			return pluginsLoader.loadAllPlugins (filter);          
		}
		catch (Exception e) {}
		return null;
	}

	/**
	 * Gets the plugin with the "Writer" role and the extension 
	 * @param extension the extension required
	 * @return a probNetWriter object
	 */
	public ProbNetWriter getProbNetWriter (String extension)
	{
	    ProbNetWriter instance = null;
	    if(writerInstances.containsKey (extension))
	    {
	        instance = writerInstances.get(extension);
	    }else
	    {
	        if(writerClasses.containsKey (extension))
	        {
        		try
        		{
        		    instance = (ProbNetWriter) writerClasses.get (extension).newInstance ();
        		}
        		catch (Exception e) {}
	        }
	    }
		return instance;
	} 
	/**
	 * Gets the plugin with the "Reader" role and the extension 
	 * @param extension the extension required
	 * @return a probNetReader object
	 */
	public ProbNetReader getProbNetReader (String extension)
	{
	    ProbNetReader instance = null;
        if(readerInstances.containsKey (extension))
        {
            instance = readerInstances.get(extension);
        }else
        {
            if(readerClasses.containsKey (extension))
            {
                try
                {
                    instance = (ProbNetReader) readerClasses.get (extension).newInstance ();
                }
                catch (Exception e) {}
            }
        }
        return instance;
	} 


	/**
	 * Gets the all the writer plugins  
	 * @return all the writer plugins found
	 */

	public HashMap<String, String> getWriters()
	{
		HashMap<String, String> writers = new HashMap<String, String> ();
		for (String extension : writerClasses.keySet ()) {
			FormatType lAnnotation = writerClasses.get (extension).getAnnotation (FormatType.class);
			writers.put (lAnnotation.description(), lAnnotation.extension());
		}

		return writers;
	}
	
    /**
     * Gets the all the reader plugins  
     * @return all the reader plugins found
     */

    public HashMap<String, String> getReaders()
    {
        HashMap<String, String> writers = new HashMap<String, String> ();
        for (String extension : readerClasses.keySet ()) {
            FormatType lAnnotation = readerClasses.get (extension).getAnnotation (FormatType.class);
            writers.put (lAnnotation.description(), lAnnotation.extension());
        }

        return writers;
    }  	


}
