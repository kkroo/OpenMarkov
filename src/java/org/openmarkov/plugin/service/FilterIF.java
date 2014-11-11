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
 * This interface represents a contract for plugin filters.
 * A plugin filter is used to characterize a plugin by its internal structure
 * in terms of a set of constrains related to extended classes, implemented
 * interfaces and present annotations. Once defined the plugin can be used 
 * to validate whether a class fulfills that constrains.
 * @(#)FilterIF.java    1.0    15/09/2011 19:08:10
 * @author  jvelez
 * @version 1.0
 *
 * Development Environment        :  Eclipse
 * Name of the File               :  FilterIF.java
 * Creation/Modification History  :
 *
 * jvelez     15/09/2011 19:08:10      Created.
 * Gigaesfera CO.
 * 
 */
public interface FilterIF
{
    /**
     * Checks whether a class is a valid plugin.
     * @param aClass the class to validate.
     * @return true if the class is a valid plugin.
     */ 
	public boolean checkPlugin (Class<?> aClass);
}
