/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.window.edition.mode;

import java.awt.Cursor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmarkov.core.gui.loader.element.CursorLoader;
import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;

public class EditionModeManager
{
    private Map<String, EditionState> editionStates;
    private Map<String, Class<?>> editionModeClasses;
    private EditorPanel editorPanel;
    private ProbNet probNet;
    
    public EditionModeManager (EditorPanel editorPanel, ProbNet probNet)
    {
        editionStates = new HashMap<>();
        editionModeClasses = new HashMap<> ();
        this.editorPanel = editorPanel;
        this.probNet = probNet;
        for(Class<?> editionModeClass : findAllEditionStates ())
        {
            EditionState editionState = editionModeClass.getAnnotation (EditionState.class);
            editionStates.put(editionState.name (), editionState);
            editionModeClasses.put (editionState.name (), editionModeClass);
        }
    }

    
    public EditionMode getEditionMode (String editionMode)
    {
        EditionMode instance = null;
        if(editionModeClasses.containsKey (editionMode))
        {
            try
            {
                Constructor<?> constructor = null;
                constructor = editionModeClasses.get (editionMode).getConstructor (EditorPanel.class,
                                                                                   ProbNet.class);
                instance = (EditionMode) constructor.newInstance (editorPanel, probNet);
            }
            catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | SecurityException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace ();
            }
        }
        return instance;
    }
    
    public Collection<EditionState> getEditionStates()
    {
        return editionStates.values ();
    }

    /**
     * This method gets all the plugins with EditionState annotations
     * @return a list with the plugins detected with FormatType annotations.
     */
    private final  List<Class<?>> findAllEditionStates ()
    {
        PluginLoader pluginsLoader = new PluginLoader ();
        try
        {
            FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy (EditionState.class);
            return pluginsLoader.loadAllPlugins (filter);          
        }
        catch (Exception e) {}
        return null;
    }


    public Cursor getCursor (String newEditionModeName)
    {
        return CursorLoader.load (editionStates.get (newEditionModeName).cursor());
    }
    
    public Cursor getDefaultCursor ()
    {
        return CursorLoader.load (editionStates.get ("Edit.Mode.Selection").cursor());
    }    


    public EditionMode getDefaultEditionMode ()
    {
        return getEditionMode("Edit.Mode.Selection");
    }    
}
