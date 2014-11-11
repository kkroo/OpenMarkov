/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.io.format.annotation;

/**
 * This class sets the labels for the annotations format
 * @author mpalacios
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface FormatType {
    /**
     * Gets the name of the class
     * @return The name of the format
     */
    String name ();
    /**
     * Gets the file extension that write/read the class
     * @return The format extension
     */
    String extension ();
    
    /**
     * Gets the file description that writes/reads the class. 
     * It will be used as a string id for the file description to be shown in the GUI.
     * The string id will be built using the following pattern: <code>"FileExtension." + description + ".Description"</code>
     * @return The format description
     */
    String description ();
    
    /**
     * Gets the role of the class.
     * @return the role of the class. "Writer" if implements the ProbNetWriter interface, "Reader" if implements the ProbNetReader interface.
     */
    String role ();
}
