/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.algorithm.scoreAndSearch.metric.annotation;

import java.lang.annotation.AnnotationFormatError;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openmarkov.learning.algorithm.scoreAndSearch.metric.Metric;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;
import org.openmarkov.plugin.service.PluginLoaderIF;

public class MetricManager
{
    private PluginLoaderIF pluginsLoader; 
    private HashMap<String, Class<? extends Metric>> metrics; 
    
    /**
     * Constructor for MetricManager.
     */
    @SuppressWarnings("unchecked")
    public MetricManager ()
    {
        super ();
        this.pluginsLoader = new PluginLoader ();
        
        metrics = new HashMap<String, Class<? extends Metric>> ();
        
        for (Class<?> plugin : findAllMetrics ())
        {
            MetricType lAnnotation = plugin.getAnnotation (MetricType.class);
            if (Metric.class.isAssignableFrom (plugin))
            {
                metrics.put (lAnnotation.name (), (Class<? extends Metric>)plugin);
            }
            else
            {
                throw new AnnotationFormatError ("Constraint annotation must be in a class that extends Metric");
            }
        }  
        
    }

    /**
     * Finds a learning algorithm by name. 
     * @param name the algorithm name.
     * @return a learning algorithm.
     */
    public final Class<? extends Metric> getMetricByName (String name)
    {
        return metrics.get (name);
    }
    
    
    /**
     * Returns the names of all metrics. 
     * @return the names of all metrics.
     */
    public final Set<String> getAllMetricNames ()
    {
        return metrics.keySet ();
    }    
    
  
    /**
     * Finds all metrics. 
     * @return a list of metrics.
     */
    private final  List<Class<?>> findAllMetrics ()
    {
        try
        {
            FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy (MetricType.class);
            return pluginsLoader.loadAllPlugins (filter);          
        }
        catch (Exception e) {}
        return null;
    }
   
}

