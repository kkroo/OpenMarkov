/*
* Copyright 2013 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.learning.core.exception;

import java.util.List;

import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class LatentVariablesException extends Exception
{
    List<Variable> latentVariables;
    
    public LatentVariablesException(List<Variable> latentVariables)
    {
        this.latentVariables = latentVariables;
    }

    /**
     * Returns the latentVariables.
     * @return the latentVariables.
     */
    public List<Variable> getLatentVariables ()
    {
        return latentVariables;
    }
}
