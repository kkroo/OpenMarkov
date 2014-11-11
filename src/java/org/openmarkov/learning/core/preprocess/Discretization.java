/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/


package org.openmarkov.learning.core.preprocess;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.learning.core.preprocess.exception.WrongDiscretizationLimitException;

/** This class implements the routines to manage the discretization of the
 * variables.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @author ibermejo
 * @version 1.0
 * @since OpenMarkov 1.0 */
public class Discretization {

    public enum Option {
        NONE,
        EQUAL_FREQ,
        EQUAL_WIDTH,
        MODEL_NET;
    }

    public static Discretization.Option[] getOptions(){
        return Discretization.Option.values ();
    }
    
    /**
     * This function determines whether a variable is numeric or not
     * 
     * @param variable <code>Variable</code>
     * @return true if the variable is numeric
     */
    public static boolean isNumeric(Variable variable){

        State[] states = variable.getStates ();
        boolean hasMissingValues = false;
        for (int i = 0; i < states.length; i++)
        {
            try
            {
                if (!states[i].getName ().equals ("?"))
                {
                    Double.parseDouble (states[i].getName ());
                }else
                {
                    hasMissingValues = true;
                }
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        return states.length > 4 || (!hasMissingValues && states.length == 3);
    }

    /**
     * This function discretizes the database.
     *
     * @return <code>CaseDatabase</code> updated database
     */
    public static CaseDatabase process (CaseDatabase database,
                                    Map<String, Option> discretizeOptions,
                                    Map<String, Integer> numIntervalsPerVariable,
                                    ProbNet modelNet)
        throws InvalidStateException,
        ProbNodeNotFoundException,
        WrongDiscretizationLimitException
    {

        List<Variable> newVariables = new ArrayList<> (); 
                
        for (Variable variable : database.getVariables ())
        {
            Variable newVariable = variable;
            int numIntervals = numIntervalsPerVariable.get (variable.getName ());
            switch (discretizeOptions.get (variable.getName ()))
            {
                case EQUAL_WIDTH :
                    newVariable = discretizeEqualWidth (variable, numIntervals);
                    break;
                case EQUAL_FREQ :
                    newVariable = discretizeEqualFreq (variable, database, numIntervals);
                    break;
                case MODEL_NET :
                    newVariable = discretizeFromModelNet (variable, modelNet);
                    break;
                default :
                    newVariable = variable;
                    break;
            }
            newVariables.add (newVariable);
        }

        /* construct the new cases array */
        int[][] newCases = discretizeCases(database, newVariables, discretizeOptions);

        return new CaseDatabase (newVariables, newCases);
    }
    
    /**
     * This function discretizes the database.
     *
     * @return <code>CaseDatabase</code> updated database
     */
    public static CaseDatabase process (CaseDatabase database,
                                    Map<String, Option> discretizeOptions,
                                    Map<String, Integer> numIntervalsPerVariable)
        throws InvalidStateException,
        ProbNodeNotFoundException,
        WrongDiscretizationLimitException
    {
        return process (database, discretizeOptions, numIntervalsPerVariable, null);
    }    


    /**
     * This function discretizes the database.
     *
     * @return <code>CaseDatabase</code> updated database
     * @throws WrongDiscretizationLimitException 
     * @throws ProbNodeNotFoundException 
     * @throws InvalidStateException 
     */
    public static CaseDatabase process (CaseDatabase database,
                                        Discretization.Option discretizationOption,
                                        int numIntervals)
        throws InvalidStateException,
        ProbNodeNotFoundException,
        WrongDiscretizationLimitException
    {
        Map<String, Option> discretizeOptions = new HashMap<>();
        Map<String, Integer> numIntervalsPerVariable = new HashMap<>();
        
        for(Variable variable : database.getVariables ())
        {
            discretizeOptions.put (variable.getName (), discretizationOption);
            numIntervalsPerVariable.put (variable.getName (), numIntervals);
        }
        
        return process (database, discretizeOptions, numIntervalsPerVariable, null);
    }   
    
    /**
     * This function discretizes the database.
     *
     * @return <code>CaseDatabase</code> updated database
     * @throws WrongDiscretizationLimitException 
     * @throws ProbNodeNotFoundException 
     * @throws InvalidStateException 
     */
    public static CaseDatabase process (CaseDatabase database,
                                        ProbNet modelNet)
        throws InvalidStateException,
        ProbNodeNotFoundException,
        WrongDiscretizationLimitException
    {
        Map<String, Option> discretizeOptions = new HashMap<>();
        Map<String, Integer> numIntervalsPerVariable = new HashMap<>();
        
        for(Variable variable : database.getVariables ())
        {
            discretizeOptions.put (variable.getName(), Discretization.Option.MODEL_NET);
            numIntervalsPerVariable.put (variable.getName (), -1);
        }
        
        return process (database, discretizeOptions, numIntervalsPerVariable, modelNet);
    }    

    /**
     * This function makes the discretization of a variable taking the
     * information from a model net
     * @param oldVariable <code>Variable</code> variable to discretize
     * @param modelNet <code>ProbNet</code> net from which to tak the
     * information of the discretization
     * @param oldProbNet <code>ProbNet</code> original probNet
     * @throws java.lang.Exception
     */
    private static Variable discretizeFromModelNet (Variable oldVariable,
                                                ProbNet modelNet)
        throws ProbNodeNotFoundException
    {
        
        Variable newVariable = oldVariable;

        if (modelNet != null){
            Variable modelNetVariable = modelNet.getVariable(oldVariable.getName());
            
            boolean missingValuesInDB = false;
            try
            {
                oldVariable.getStateIndex ("?");
                missingValuesInDB = true;                
            }
            catch (InvalidStateException e)
            {
                // Do nothing
            }
            boolean missingValuesInModelNet = false;
            try
            {
                modelNetVariable.getStateIndex ("?");
                missingValuesInModelNet = true;                
            }
            catch (InvalidStateException e)
            {
                // Do nothing
            }
            State[] newStates = null;
            if(missingValuesInDB && !missingValuesInModelNet)
            {
                // Add "missing value" state
                newStates = new State[modelNetVariable.getNumStates () + 1];
                for(int i = 0; i < modelNetVariable.getNumStates (); ++i)
                {
                    newStates[i] = modelNetVariable.getStates ()[i];
                }
                newStates[newStates.length -1] = new State("?");
            }else
            {
                newStates = modelNetVariable.getStates ();
            }
            
            PartitionedInterval modelNetInterval = modelNetVariable.getPartitionedInterval();
            if(modelNetInterval != null)
            {
                double[] limits = modelNetInterval.getLimits();
                boolean[] belongsToLeftSide = modelNetInterval.getBelongsToLeftSide();
                
                newVariable = new Variable(oldVariable.getName(), newStates,
                		new PartitionedInterval(limits, belongsToLeftSide), 0.001);
            }else
            {
                newVariable = new Variable(oldVariable.getName(), newStates);
            }
        }
        
        return newVariable;
    }


    private static Variable discretizeEqualWidth (Variable variable,
                                             int numIntervals)
    {
        Variable newVariable = null;
        
        //Create a new discretized variable
        boolean containsMissingValues = false;
        try
        {
            variable.getStateIndex ("?");
            containsMissingValues = true;
        }
        catch (InvalidStateException e)
        {
            // Do nothing
        }
        
        int numStates = (containsMissingValues)? numIntervals +1 : numIntervals;
        State[] states = new State[numStates];
        boolean[] belongsToLeftSide = new boolean[numIntervals+1];
        double[] limits = new double[numIntervals + 1];
        double max = calculateVariableMax(variable);
        double min = calculateVariableMin(variable);
        double step = (max - min) / (double)numIntervals;
        for (int i = 0; i < numIntervals; i++)
        {
            states[i] = new State (new String ("(" + (min + (i * step)) + " , "
                                               + (min + ((i + 1) * step)) + "]"));
            belongsToLeftSide[i] = true;
            limits[i] = min + (i * step);
        }
        //Minimum and Maximum must be in the interval
        states[0].setName(states[0].getName().replace('(', '['));
        belongsToLeftSide[0] = false;
        belongsToLeftSide[numIntervals] = true;
        limits[numIntervals] = max;
        if(containsMissingValues)
        {
            states[numStates - 1] = new State("?");
        }
        newVariable = new Variable(variable.getName(), states,
                new PartitionedInterval(limits, belongsToLeftSide), 0.001);

        return newVariable;
    }

    /**
     * This function makes the discretization of a variable using equal
     * frequency intervals. If the distribution along the states is not
     * approximately uniform, the frequency of each interval could be really
     * different. For example, if we have three states with frequencies: 200, 3,
     * 4, making two intervals of "equal frequency" would lead to an interval
     * of frequency 200 and an interval of frequency 7.
     * @param variable <code>Variable</code> variable to discretize
     * @param oldProbNet <code>ProbNet</code> original probNet
     * @param cases <code>int[][]</code> database cases
     * @throws InvalidStateException 
     * @throws ProbNodeNotFoundException 
     * @throws java.lang.Exception
     */
    private static Variable discretizeEqualFreq (Variable variable, CaseDatabase database, int numIntervals)
        throws InvalidStateException,
        ProbNodeNotFoundException
    {
        Variable newVariable;
        State[] states = variable.getStates ();
        List<Double> intervalLimits = new ArrayList<Double> ();
        double accruedFreq = 0, stateFreq = 0, validCaseNum, intervalFreq;
        int stateIndex;
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en"));
        DecimalFormat decimalFormat = (DecimalFormat)nf;
        decimalFormat.applyPattern("###.########");
        String stateName;


        // Order the numerical states
        List<Double> orderedStates = new ArrayList<Double> ();
        for (int i = 0; i < states.length; i++){
            if(!states[i].getName ().equals ("?"))
                orderedStates.add(Double.parseDouble(states[i].getName()));
        }
        Collections.sort(orderedStates);
        
        int[] casesForVariable = database.getCases (variable);
        int[] histogram = new int[variable.getStates ().length];
        for(int i=0; i< casesForVariable.length; ++i)
        {
            ++histogram[casesForVariable[i]]; 
        }

        
        // number of cases with valid data, i.e. all minus the missing values
        validCaseNum = casesForVariable.length;
        try
        {
            validCaseNum = casesForVariable.length - histogram[variable.getStateIndex ("?")];
        }catch(InvalidStateException e)
        {
            // Do nothing;
        }
        
        //calculate approximate frequency of each interval
        intervalFreq = validCaseNum / (double) numIntervals;
        intervalLimits.add(Double.NEGATIVE_INFINITY);

        
        for (Double state : orderedStates){
            //check whether the state is integer or double
        	stateName = state.toString();
            try{
            	if (stateName.contains("E"))
            	{
            		//scientific notation
            		stateIndex = variable.getStateIndex(""+decimalFormat.format(state.doubleValue()));
            	}
            	else
            	{
            		stateIndex = variable.getStateIndex(""+state);
            	}
            }
            catch (Exception e){
                stateIndex = variable.getStateIndex(""+state.intValue());
            }
            stateFreq = histogram[stateIndex];
            if ((accruedFreq + stateFreq) >= intervalFreq){
                intervalLimits.add(state);
                accruedFreq = 0;
            }
            else
                accruedFreq += stateFreq;
        }
        intervalLimits.add(Double.POSITIVE_INFINITY);

        
        //Create a new discretized variable
        boolean containsMissingValues = false;
        try
        {
            variable.getStateIndex ("?");
            containsMissingValues = true;
        }
        catch (InvalidStateException e)
        {
            // Do nothing
        }        
        int numStates = (containsMissingValues) ? numIntervals + 1 : numIntervals;
        State[] newStates = new State[numStates];
        double[] limits = new double[numIntervals + 1];
        boolean[] belongsToLeftSide = new boolean[numIntervals + 1];
        for (int i = 0; i < numIntervals; i++)
        {
            newStates[i] = new State (new String ("(" + intervalLimits.get (i) + " , "
                                                  + intervalLimits.get (i + 1) + "]"));
            belongsToLeftSide[i] = true;
            limits[i] = intervalLimits.get (i);
        }
        limits[limits.length - 1] = intervalLimits.get (limits.length - 1);
        // open the last interval
        newStates[numIntervals - 1] = new State (newStates[numIntervals - 1].getName ().replace (']', ')'));
        // Minimum and Maximum must be in the interval
        belongsToLeftSide[0] = false;
        belongsToLeftSide[numIntervals] = true;
        
        if(containsMissingValues)
        {
            newStates[numStates - 1] = new State("?");
        }
        
        newVariable = new Variable (variable.getName (), newStates,
                                    new PartitionedInterval (limits, belongsToLeftSide), 0.001);

        return newVariable;
    }
    
    /**
     * This function updates the database cases to adapt them to the new
     * states of the discretized variables.
     * @param cases <code>int[][]</code> original database cases
     * @param oldProbNet <code>ProbNet</code> original probNet
     * @param discretizeOption <code>ArrayList</code> discretization option
     * selected for each variable.
    * @throws ProbNodeNotFoundException 
    * @throws WrongDiscretizationLimitException 
     * @throws InvalidStateException 
     */
   private static int[][] discretizeCases (CaseDatabase database, List<Variable> newVariables, Map<String, Option> discretizeOptions)
       throws ProbNodeNotFoundException,
       WrongDiscretizationLimitException, InvalidStateException
   {
       int[][] oldCases = database.getCases ();
       int[][] newCases = new int[oldCases.length][newVariables.size ()];
       
       for(int j = 0; j < database.getVariables ().size(); j++){
           Variable oldVariable = database.getVariables ().get (j);
           State[] oldStates = oldVariable.getStates ();
           int indexOfNewVariable = newVariables.indexOf (oldVariable);
           Variable newVariable = newVariables.get (indexOfNewVariable);
           PartitionedInterval partitionedInterval = newVariable.getPartitionedInterval();
           double[] newIntervals = (partitionedInterval != null)? partitionedInterval.getLimits() : null;
           boolean[] belongsToLeft = (partitionedInterval != null)? partitionedInterval.getBelongsToLeftSide() : null;
           boolean isNumeric = isNumeric (oldVariable);
           int missingValeStateIndex = -1;
           
           try
            {
                missingValeStateIndex = newVariable.getStateIndex ("?");
            }
            catch (InvalidStateException e)
            {
                // Do nothing
            }
           
           for(int i = 0; i < oldCases.length; i++){

               if (isNumeric){
                   switch(discretizeOptions.get(oldVariable.getName ())){
                       case NONE:
                           State state = oldVariable.getStates ()[oldCases[i][j]];
                           newCases[i][indexOfNewVariable] = newVariable.getStateIndex (state.getName ());
                           break;
                       default:
                           if(oldStates[oldCases[i][j]].getName ().equals ("?"))
                           {
                               newCases[i][j] = missingValeStateIndex;
                           }else
                           {
                                Double value = Double.parseDouble (oldStates[oldCases[i][j]].getName ());
                                // We search for the interval in which the value is contained
                                int k = 1;
                                boolean matched = false;
                                while (!matched && k < newIntervals.length)
                                {
                                    if (value <= newIntervals[k])
                                    {
                                        newCases[i][j] = (value < newIntervals[k] || belongsToLeft[k]) ? k - 1 : k;
                                        matched = true;
                                    }
                                    ++k;
                                }
                           }
                            break;
                   }
               }
               else {
                   State state = oldVariable.getStates ()[oldCases[i][j]];
                   newCases[i][indexOfNewVariable] = newVariable.getStateIndex (state.getName ());
               }
           }
       }
       
       return newCases;
   }    
   
   private static double calculateVariableMax (Variable variable)
   {
       double max = Double.NEGATIVE_INFINITY;
       
       for(State state : variable.getStates ())
       {
           if(!state.getName ().equals ("?") && max < Double.parseDouble (state.getName ()))
           {
               max = Double.parseDouble (state.getName ());
           }
       }
       
       return max;
   }
   
   private static double calculateVariableMin (Variable variable)
   {
       double min = Double.POSITIVE_INFINITY;
       
       for(State state : variable.getStates ())
       {
           if(!state.getName ().equals ("?") && min > Double.parseDouble (state.getName ()))
           {
               min = Double.parseDouble (state.getName ());
           }
       }
       
       return min;
   }

}
