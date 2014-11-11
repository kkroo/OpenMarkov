/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.gui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openmarkov.learning.core.algorithm.LearningAlgorithm;

@Retention(RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface AlgorithmConfiguration {
    Class<? extends LearningAlgorithm> algorithm();
}
