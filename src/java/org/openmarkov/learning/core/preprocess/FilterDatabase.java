/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.core.preprocess;

import java.util.List;

import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.network.Variable;

public class FilterDatabase
{
    public static CaseDatabase filter (CaseDatabase database, List<Variable> selectedVariables)
    {
        int[][] oldCases =  database.getCases ();
        int[][] newCases = new int[oldCases.length][selectedVariables.size ()];
        
        for(int j=0; j < selectedVariables.size (); ++j)
        {
            int indexOfVariable = database.getVariables ().indexOf (selectedVariables.get (j));
            for(int i=0; i < oldCases.length; ++i)
            {
                newCases[i][j] = oldCases[i][indexOfVariable];
            }
        }
        
        return new CaseDatabase (selectedVariables, newCases);
    }    
}
