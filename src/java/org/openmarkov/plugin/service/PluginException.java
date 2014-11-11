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
 * This class represents a throwable exception related to plugins.
 * @(#)PluginExeption.java    1.0    15/09/2011		19:08:10
 * @author  jvelez
 * @version 1.0
 *
 * Development Environment        :  Eclipse
 * Name of the File               :  PluginExeption.java
 * Creation/Modification History  :
 *
 * jvelez      15/09/2011 19:08:10      Created.
 * Gigaesfera  CO.
 *
 */

public class PluginException extends Exception 
{
	 private static final long serialVersionUID = -9088696846391248107L;

	    /**
	     * Constructor for PluginException.
	     */
	    public PluginException ()
	    {
	        super ();
	    }

	    /**
	     * Constructor for PluginException.
	     * @param message The message.
	     * @param cause The cause.
	     */
	    public PluginException (String message, Throwable cause)
	    {
	        super (message, cause);
	    }

	    /**
	     * Constructor for PluginException.
	     * @param message The message.
	     */
	    public PluginException (String message)
	    {
	        super (message);
	    }

	    /**
	     * Constructor for PluginException.
	     * @param cause The cause.
	     */
	    public PluginException (Throwable cause)
	    {
	        super (cause);
	    }    
}
