/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.core.preprocess.exception;

/**
 * Thrown when the minimum is under the left limit of the first interval, 
 * or the maximum is greater than the right limit of the last interval, 
 * @author Iñigo
 *
 */
@SuppressWarnings("serial")
public class WrongDiscretizationLimitException extends Exception {

}
