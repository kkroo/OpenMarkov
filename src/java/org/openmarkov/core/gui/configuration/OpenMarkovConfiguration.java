/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;


@SuppressWarnings("serial")
/** Contains all the components configurations. */
public class OpenMarkovConfiguration implements DefaultConfiguration, Serializable {

	// Attributes
	/** Singleton pattern */ 
	private static OpenMarkovConfiguration openMarkovConfiguration;

	private final String configurationFileName = "OpenMarkov.conf";
	
	private HashMap<String, Configuration> configurations;
	
	private Logger logger;
	
	// Constructor
	/** Singleton pattern (private constructor).<p>
	 * Reads configuration from disk or generates default configuration. */
	private OpenMarkovConfiguration() {
		this.logger = Logger.getLogger(OpenMarkovConfiguration.class);
		readConfiguration();
		
	}
	
	// Methods
	/** Singleton pattern.
	 * @return <code>OpenMarkovConfiguration</code> */
	public static OpenMarkovConfiguration getUniqueInstance() {
		if (openMarkovConfiguration == null) {
			openMarkovConfiguration = new OpenMarkovConfiguration();
		}
		return openMarkovConfiguration;
	}
	
	/** Write configuration to disk in serialized format. */
	public void writeConfiguration() {
		try {
			FileOutputStream fos = new FileOutputStream(configurationFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(openMarkovConfiguration);
			oos.close();
		} catch (IOException e) {
			//ExceptionsHandler.handleException(e, "Can not write configuration", false);
			logger.info("Can no write configuraction");
		}
	}
	
	/** Generates a default configuration for each component. */
	public void generateDefaultConfiguration() {
		configurations = createConfigurationObjects();
	}
	
	/** @param name <code>String</code>.
	 * @return <code>ComponentConfiguration</code> if it exists, otherwise 
	 *  <code>null</code>. */
	public Configuration getComponentConfiguration(String name) {
		return configurations.get(name);
	}
	
	/** @param pluginName. <code>String</code>
	 * @param propertyName. <code>String</code>
	 * @return Property value or <code>null</code> if property does not exists.
	 *  <code>Object</code> */
	public static Object getProperty(String pluginName, String propertyName) {
		Configuration componentConfiguration = 
			getUniqueInstance().getComponentConfiguration(pluginName);
		if (componentConfiguration != null) {
			return componentConfiguration.getProperty(propertyName);
		}
		return null;
	}
	
	/** Creates configurations for each component.<p>
	 *  To extend this method for each component:<ol>
	 *  <li>Create a class that implements <code>ComponentConfiguration</code>.
	 *  <li>Create an object of that class.
	 *  <li>Put that object in the HashMap.
	 *  </ol>
	 * @return <code>HashMap</code> with key = <code>String</code> (component 
	 *  name) and value = an <code>object</code> that implements 
	 *  <code>ComponentConfiguration</code>. */
	private HashMap<String, Configuration> createConfigurationObjects() 
	{
		HashMap<String, Configuration> configurations;
		configurations = new HashMap<String, Configuration>();
		// Create a class that implements Configuration
		String kernelComponenteName = "kernel";
		ComponentConfiguration kernelConfiguration =  
			new ComponentConfiguration(kernelComponenteName); // component name
		// Put that object in the HashMap
    	configurations.put(kernelComponenteName, kernelConfiguration);		
    	return configurations;
	}

	private void readConfiguration() {
		HashMap<String, Configuration> configurationsCollection = 
			createConfigurationObjects();
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(configurationFileName);
			ois = new ObjectInputStream(fis);
			openMarkovConfiguration = (OpenMarkovConfiguration)ois.readObject();
			if (configurations == null) {
				generateDefaultConfiguration(configurationsCollection);
			} else {
				generateDefaultConfiguration(configurationsCollection, 
					configurations);
			}
		} catch (FileNotFoundException f) {
			generateDefaultConfiguration(configurationsCollection);
		} catch (IOException e) {
			logger.info(e);
		} catch (ClassNotFoundException e) {
			logger.info(e);
		} finally
		{
            try
            {
                ois.close ();
            }
            catch (IOException e)
            {
                logger.info(e);
            }
		}
	}

	/** @param configurationsCollection. <code>HashMap</code> with <code>key =
	 *  String</code> and <code>value = ComponentConfiguration</code> */
	private void generateDefaultConfiguration(
			HashMap<String, Configuration> configurationsCollection) {
		ArrayList<Configuration> configurationsArray = 
			new ArrayList<Configuration>(
			configurationsCollection.values());
		for(Configuration configuration : configurationsArray) {
			configuration.generateDefaultConfiguration();
		}
		configurations = configurationsCollection;
		writeConfiguration();
	}

	private void generateDefaultConfiguration(
			HashMap<String, Configuration> configurationsCollection, 
			HashMap<String, Configuration> configurationsReaded) {
		ArrayList<Configuration> configsCollectionArray = 
			new ArrayList<Configuration>(
			configurationsCollection.values());
		boolean newConfiguration = false;
		for(Configuration configuration : configsCollectionArray) {
			if (configurationsReaded.get(configuration) == null) {
				configuration.generateDefaultConfiguration();
				configurationsReaded.put(
					configuration.getComponentName(), configuration);
				newConfiguration = true;
			}
		}
		configurations = configurationsReaded;
		if (newConfiguration) {
			writeConfiguration();
		}
	}

}
