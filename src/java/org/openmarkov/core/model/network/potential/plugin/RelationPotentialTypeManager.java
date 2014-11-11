/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.model.network.potential.plugin;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;
import org.openmarkov.plugin.service.PluginLoaderIF;

public class RelationPotentialTypeManager
{

    private PluginLoaderIF pluginsLoader;
    private HashMap<String, Class<? extends Potential>> potentials;
    private HashMap<String, String> potentialFamilies;
    
    /**
     * Constructor for PotentialClassManager.
     */
    @SuppressWarnings("unchecked")
    public RelationPotentialTypeManager ()
    {
        super ();
        this.pluginsLoader = new PluginLoader ();
        potentials = new HashMap<String, Class<? extends Potential>> ();
        potentialFamilies = new HashMap<String, String> ();
        
        for (Class<?> plugin : findAllPotentials ())	
        {
            RelationPotentialType lAnnotation = plugin.getAnnotation (RelationPotentialType.class);
            if (Potential.class.isAssignableFrom (plugin))
            {
                potentials.put (lAnnotation.name (), (Class<? extends Potential>)plugin);
                potentialFamilies.put(lAnnotation.name (), lAnnotation.family ());
            }
            else
            {
                throw new AnnotationFormatError ("PotentialClass annotation must be in a class that extends Potential");
            }
        }  
    }
    /**
     * Returns a potential by name. 
     * @param name the potential's name.
     * @return a new Potential instance given the parameters.
     */
    public final Potential getByName (String name, List<Variable> variables, PotentialRole role)
    {
        Potential instance = null;
        try
        {
            Constructor<? extends Potential> constructor;
            
            try
            {
                constructor = potentials.get (name).getConstructor (List.class, PotentialRole.class);
                instance = (Potential) constructor.newInstance (variables, role);
            }catch (NoSuchMethodException e) {
                constructor = potentials.get (name).getConstructor (List.class);
                instance = constructor.newInstance (variables);
            }
        }catch (NoSuchMethodException e) {
            throw new InvalidParameterException ("A Potential subclass must have a constructor "
                    + "either that receives a list of variables or"
                    + " a list of variables and a potential role.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(instance == null)
            throw new InvalidParameterException();
        return instance;
    }
    /**
     * For utility potentials
     * @param name
     * @param variables
     * @param role
     * @param utilityVariable
     * @return
     */
    public final Potential getByName (String name, List<Variable> variables, PotentialRole role, Variable utilityVariable)
    {
        Potential instance = null;
        try
        {
            Constructor<? extends Potential> constructor;
            
            try
            {
                constructor = potentials.get (name).getConstructor (List.class, PotentialRole.class, Variable.class);
                instance = (Potential) constructor.newInstance (variables, role, utilityVariable);
            }catch (NoSuchMethodException e) {
                constructor = potentials.get (name).getConstructor (List.class);
                instance = constructor.newInstance (variables);
            }
        }catch (NoSuchMethodException e) {
            throw new InvalidParameterException ("A Potential subclass must have a constructor "
                    + "either that receives a list of variables or"
                    + " a list of variables a potential role and a utility variable.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(instance == null)
            throw new InvalidParameterException();
        return instance;
    }
    /**
     * Returns all potentials' names. 
     * @return a list of potentials' names.
     */
    public final  Set<String> getAllPotentialsNames ()
    {
        return potentials.keySet ();
    }
    
    /**
     * Returns all potentials' names applicable to the given variable list and potential role. 
     * @return a list of potentials' names.
     */
    public final  List<String> getFilteredPotentials (ProbNode probNode)
    {
        List<String> filteredPotentials = new ArrayList<String> ();
        
        for(String potentialName : potentials.keySet ())
        {
            Method validateMethod = null;
            try
            {
                validateMethod = potentials.get (potentialName).getMethod ("validate", ProbNode.class, List.class, PotentialRole.class);
                if((Boolean)validateMethod.invoke (null, probNode, probNode.getPotentials ().get (0).getVariables (), probNode.getPotentials ().get (0).getPotentialRole ()))
                {
                    filteredPotentials.add (potentialName);
                }                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return filteredPotentials;
    }      
    
    /**
     * Returns the family of the given potential type
     * @param name
     * @return the family of the given potential type
     */
    public String getPotentialsFamily(String name)
    {
        return potentialFamilies.get (name);
    }
  
    /**
     * Finds all learning algorithms. 
     * @return a list of learning algorithms.
     */
    private final  List<Class<?>> findAllPotentials ()
    {
        try
        {
            FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy (RelationPotentialType.class);
            return pluginsLoader.loadAllPlugins (filter);          
        }
        catch (Exception e) {}
        return null;
    }
   
}

