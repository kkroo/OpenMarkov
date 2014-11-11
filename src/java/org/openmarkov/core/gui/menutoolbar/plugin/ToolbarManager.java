package org.openmarkov.core.gui.menutoolbar.plugin;

import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmarkov.core.gui.menutoolbar.toolbar.ToolBarBasic;
import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;

public class ToolbarManager
{
    private MainPanel mainPanel;
    private Map<String, Class<?>> toolbarClasses;
    private List<String> activeToolbars = new ArrayList<> ();
    
    public ToolbarManager(MainPanel mainPanel)
    {
        toolbarClasses = new HashMap<String, Class<?>>();
        this.mainPanel = mainPanel;
        
        for(Class<?> toolbarClass : findAllToolbars ())
        {
            Toolbar toolbar = toolbarClass.getAnnotation (Toolbar.class);
            toolbarClasses.put (toolbar.name (), toolbarClass);
        }    
    }


    public Set<String> getToolbarNames()
    {
        return toolbarClasses.keySet ();
    }
    
    public void addToolbar(String name)
    {
        ToolBarBasic instance = null;

        if(!activeToolbars.contains (name))
        {
            if(toolbarClasses.containsKey (name))
            {
                try
                {
                    Constructor<?> constructor = toolbarClasses.get (name).getConstructor (ActionListener.class);
                    instance = (ToolBarBasic) constructor.newInstance (mainPanel.getMainPanelListenerAssistant ());
                }
                catch (NoSuchMethodException | SecurityException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
            mainPanel.getToolBarPanel ().add (instance);
        }
        activeToolbars.add (name);
    }
    
    /**
     * This method gets all the plugins with Toolbar annotations
     * @return a list with the plugins detected with Toolbar annotations.
     */
    private final  List<Class<?>> findAllToolbars ()
    {
        PluginLoader pluginsLoader = new PluginLoader ();
        try
        {
            FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy (Toolbar.class);
            return pluginsLoader.loadAllPlugins (filter);          
        }
        catch (Exception e) {}
        return null;
    }    
}
