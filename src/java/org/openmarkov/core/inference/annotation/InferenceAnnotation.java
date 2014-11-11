/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.inference.annotation;

/**
 * This class sets the labels for the annotations inference
 * @author mpalacios
 * @author myebra
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface InferenceAnnotation {
    /**
     * Gets the name of the class
     * @return The name of the inference algorithm
     */
    String name ();
}