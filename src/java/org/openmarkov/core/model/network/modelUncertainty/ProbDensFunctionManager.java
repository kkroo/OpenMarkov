/*
 * Copyright 2013 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.model.network.modelUncertainty;

import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;
import org.openmarkov.plugin.service.PluginLoaderIF;

public class ProbDensFunctionManager {

    private static ProbDensFunctionManager instance;
    private PluginLoaderIF                 pluginLoader;
    private Map<String, Class<?>>          probDensFunctions;

    /**
     * Constructor for ProbDensFunctionManager.
     */
    private ProbDensFunctionManager() {
        super();
        this.pluginLoader = new PluginLoader();
        this.probDensFunctions = new HashMap<String, Class<?>>();

        List<Class<?>> plugins = findAllProbDensFunctions();
        for (Class<?> plugin : plugins) {
            ProbDensFunctionType annotation = plugin.getAnnotation(ProbDensFunctionType.class);
            if (ProbDensFunction.class.isAssignableFrom(plugin)) {
                probDensFunctions.put(annotation.name(), plugin);
            } else {
                throw new AnnotationFormatError("ProbDensFunctionType annotation must be in a class that extends ProbDensFunction");
            }
        }
    }

    // Methods
    /**
     * Singleton pattern.
     * 
     * @return The unique instance.
     */
    public static ProbDensFunctionManager getUniqueInstance() {
        if (instance == null) {
            instance = new ProbDensFunctionManager();
        }
        return instance;
    }

    public List<String> getValidProbDensFunctions(boolean isChance) {
        List<String> validFunctions = new ArrayList<>();
        for (String functionName : probDensFunctions.keySet()) {
            Class<?> functionClass = probDensFunctions.get(functionName);
            ProbDensFunctionType annotation = functionClass.getAnnotation(ProbDensFunctionType.class);
            if ((annotation.isValidForNumeric() && !isChance)
                    || (annotation.isValidForProbabilities() && isChance)) {
                validFunctions.add(functionName);
            }
        }
        return validFunctions;
    }

    public String[] getParameters(String functionName) {
        Class<?> functionClass = probDensFunctions.get(functionName);
        ProbDensFunctionType annotation = functionClass.getAnnotation(ProbDensFunctionType.class);
        return annotation.parameters();
    }

    public ProbDensFunction newInstance(String functionName, double[] parameters) {
        Class<?> probDensFunctionClass = probDensFunctions.get(functionName);
        ProbDensFunction newInstance = null;
        try {
            newInstance = (ProbDensFunction) probDensFunctionClass.newInstance();
            newInstance.setParameters(parameters);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return newInstance;
    }

    private final List<Class<?>> findAllProbDensFunctions() {
        try {
            FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy(ProbDensFunctionType.class);
            return pluginLoader.loadAllPlugins(filter);
        } catch (Exception e) {
        }
        return null;
    }

}
