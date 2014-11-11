/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.io.database;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.model.network.Variable;

public class CaseDatabase
{
    private List<Variable> variables;
    private int[][] cases;

    /**
     * Constructor for CaseDatabase.
     * @param probNet
     * @param cases
     */
    public CaseDatabase (List<Variable> variables, int[][] cases)
    {
        super ();
        this.variables = new ArrayList<> (variables);
        this.cases = cases;
    }
        
    public CaseDatabase (CaseDatabase database)
    {
        super();
        this.variables = new ArrayList<> (database.getVariables ());
        int[][] casesToCopy = database.getCases ();
        this.cases = new int[casesToCopy.length][variables.size ()];
        
        for(int i = 0; i< cases.length; ++i)
        {
            for(int j = 0; j < cases[i].length; ++i)
            {
                cases[i][j] = casesToCopy[i][j];
            }
        }
    }

    /**
     * Returns the cases.
     * @return the cases.
     */
    public int[][] getCases ()
    {
        return cases;
    }

    /**
     * Returns the variables.
     * @return the variables.
     */
    public List<Variable> getVariables ()
    {
        return variables;
    }
    
    /**
     * Returns the variable given the name
     * @param name
     * @return
     */
    public Variable getVariable (String name)
    {
        Variable variable = null;
        int i = 0;
        while(variable == null && i < variables.size ())
        {
            if(variables.get (i).getName ().equals (name))
            {
                variable = variables.get(i);
            }
            ++i;
        }
        return variable;
    }
    
    /**
     * Returns the cases for a given variable.
     * @return the cases for a given variable.
     */
    public int[] getCases (Variable variable)
    {
        int[] casesOfVariable = null;
        int indexOfVariable = variables.indexOf (variable);
        
        if(indexOfVariable != -1)
        {
            casesOfVariable = new int[cases.length];
            for(int i= 0; i < cases.length; ++i)
            {
                casesOfVariable [i] = cases[i][indexOfVariable];
            }
        }
        
        return casesOfVariable;
    }    

}
