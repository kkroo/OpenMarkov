/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.database.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseReader;
import org.openmarkov.core.io.database.CaseDatabaseWriter;
import org.openmarkov.core.io.database.exception.UnableToOpenModelNet;
import org.openmarkov.core.io.database.plugin.CaseDatabaseFormat;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.type.BayesianNetworkType;


/** This class contains some routines to load a database from a '.csv' file 
 * (Comma Separated Values). The first line of the file has the names of
 * the variables.
 * @author manuel       
 * @author fjdiez               
 * @version 1.0
 * @since OpenMarkov 1.0 */
@CaseDatabaseFormat(extension = "csv", name = "CSV")
public class CSVDataBaseIO implements CaseDatabaseReader, CaseDatabaseWriter {

    /** If the value of this variable is 0 all the Strings are kept as reading,
     * if the value is 1, all the Strings are changed to lower case and if the
     * value is 2, all the Strings are changed to upper case.<p>
     * This variable is useful to avoid transcription problems when the data
     * source has been entered by persons. */
    public static int translateToLowerUpperCase = 0;
    
    
    /** Opens a database from a '.csv' file and creates a ProbNet
     * building the variables and states dinamically while reading, and 
     * returning the cases on the database.
     *
     * @param fileName <code>String</code> with the name of the 
     * file where the network is saved.
     * @return <code>CaseDatabase</code> with the cases in the database.
     * @throws IOException if the file does not exist or hte file format is not
     * correct.
     * @throws Exception if any other error occurs. */
    public CaseDatabase load (String fileName)
        throws IOException
    {
	    ProbNet probNet = new ProbNet ();
        // First row contains all the attributes names and the attributes number
        File file = new File (fileName);
        Scanner scanner = new Scanner (file);
        // first use a Scanner to get each line
        List<String> variablesNames;
        if (scanner.hasNextLine ())
        {
            variablesNames = getVariableNames (scanner.nextLine ());
        }
        else
        {
            scanner.close ();
            throw new IOException ("Bad format csv file: Empty file.");
        }
        int numColumns = variablesNames.size ();
        List<List<String>> variablesStatesNames = new ArrayList<>(numColumns);
        for (int i = 0; i < numColumns; i++)
        {
            variablesStatesNames.add(new ArrayList<String> ());
        }
        List<int[]> data = new ArrayList<int[]> (); // Read data
        List<Map<String, Integer>> variablesStates = new ArrayList<Map<String, Integer>> (); // Variables states
        for (int i = 0; i < numColumns; i++)
        {
            variablesStates.add (new HashMap<String, Integer> ());
        }
        int numRows = 0;
        while (scanner.hasNextLine ())
        {
            data.add (getDataLine (scanner.nextLine (), variablesStates, variablesStatesNames));
            numRows++;
        }
        scanner.close (); // ensure the underlying stream is always closed
        /*
         * Creation of the probNet. The probNet only contains variables but no
         * links.
         */
        probNet = getBayesNetVariables (fileName, variablesNames,
                                  variablesStatesNames);
        int[][] cases = new int[numRows][numColumns];
        for (int i = 0; i < numRows; i++)
        {
            int[] row = data.get (i);
            for (int j = 0; j < numColumns; j++)
            {
                cases[i][j] = row[j];
            }
        }
        return new CaseDatabase(probNet.getVariables (), cases);
    }

    /** This method writes a CSV database file
     * @param fileName <code>String</code> path to the resulting .csv file
     * @param probNet <code>ProbNet</code> contains the list of variables
     * @param cases <code>int[][]</code> examples to save in the database
     * @throws ProbNodeNotFoundException 
     * @throws UnableToOpenModelNet 
     * @throws java.lang.Exception */
    public void save (String fileName, CaseDatabase database)
        throws IOException
    {
        OutputStreamWriter writer = null;
        try
        {
            // Resource to write text file
            FileOutputStream fileOut = new FileOutputStream (fileName);
            writer = new OutputStreamWriter (fileOut);
            // Write the first line with the variables names
            List<Variable> variables = database.getVariables ();
            int numVariablesMinus1 = variables.size () - 1;
            for (int i = 0; i < numVariablesMinus1; i++)
            {
                writer.write (variables.get (i).getName () + ",");
            }
            writer.write (variables.get (numVariablesMinus1).getName ()
                          + "\r\n");
            for (int row = 0; row < database.getCases ().length; row++)
            {
                for (int column = 0; column < numVariablesMinus1; column++)
                {
                    Variable variable = variables.get (column);
                    writer.write (variable.getStateName (database.getCases ()[row][column])
                                  + ",");
                }
                Variable variable = variables.get (numVariablesMinus1);
                writer.write (variable.getStateName (database.getCases ()[row][numVariablesMinus1])
                              + "\r\n");
            }
        }
        catch (IOException io)
        {
            throw io;
        }
        finally
        {
            try
            {
                writer.close ();
            }
            catch (IOException e)
            {
                e.printStackTrace ();
            }
        }
    }

    /** This method writes a CSV database file
     * @param fileName <code>String</code> path to the resulting .csv file
     * @param probNet <code>ProbNet</code> contains the list of variables
     * @param cases <code>ArrayList</code> of <code>int[]</code> examples to 
     *  save in the database
     * @throws java.lang.Exception */
	public void save(String fileName, ProbNet probNet,
            ArrayList<int[]> cases) throws Exception {
    	int numCases = cases.size();
    	int[][] newCases = new int[numCases][];
    	for (int i = 0; i < newCases.length; i++) {
    		newCases[i] = cases.get(i);
    	}
    	save(fileName, new CaseDatabase (probNet.getVariables (), newCases));
    }
    
    /** Reads the first line of a 'csv' file where is stored the variable list. 
     * @param firstLine. <code>String</code>
     * @return Variables list. <code>ArrayList</code> of <code>String</code> */
    private ArrayList<String> getVariableNames (String firstLine)
        throws IOException
    {
        ArrayList<String> variableNames = new ArrayList<String> ();
        // Use a second Scanner to parse the content of each line
        Scanner scanner = new Scanner (firstLine);
        scanner.useDelimiter (",|;");
        while (scanner.hasNext ())
        {
            variableNames.add (scanner.next ());
        }
        if (variableNames.size () == 0)
        {
            scanner.close ();
            throw new IOException ("Empty of invalid first line in csv file.");
        }
        scanner.close ();
        return variableNames;
    }
    
    /** Reads a data line of a 'csv' file. 
     * @param variablesStatesNames 
     * @param line. Data to be parsed. <code>String</code>
     * @param variablesStates. Current states of variables that can be updated
     *  if appears new ones. <code>ArrayList</code> of <code>HashSet</code> of
     *  <code>String</code>.
     * @return A data line, each cell contains the state number readed. 
     *  <code>Integer[]</code>. */
    private int[] getDataLine (String line,
                               List<Map<String, Integer>> variablesStates,
                               List<List<String>> variablesStatesNames)
    {
        int[] statesLines = new int[variablesStates.size ()];
        Scanner scanner = new Scanner (line);
        scanner.useDelimiter (",|;");
        int numVariable = 0;
        while (scanner.hasNext ())
        {
            String stateVariable = scanner.next ();
            switch (translateToLowerUpperCase)
            {
                case 1 :
                    stateVariable = stateVariable.toLowerCase ();
                    break;
                case 2 :
                    stateVariable = stateVariable.toUpperCase ();
                    break;
                default :
                    break;
            }
            Map<String, Integer> variableStates = variablesStates.get (numVariable);
            Integer stateNumber = variableStates.get (stateVariable);
            if (stateNumber == null)
            {
                stateNumber = variableStates.size ();
                variableStates.put (stateVariable, stateNumber);
                variablesStatesNames.get (numVariable).add (stateVariable);
            }
            statesLines[numVariable] = stateNumber;
            numVariable++;
        }
        scanner.close ();
        return statesLines;
    }
    
    /** Returns a bayesian network with a list of variables: 
     *  <code>ProbNet</code> with a
     *  <code>openmarkov.networks.constraints.compound.BNConstraint</code>. */
    public static ProbNet getBayesNetVariables(String fileName, 
            List<String> variablesNames,
            List<List<String>> variablesStatesNames) {
        ProbNet probNet = new ProbNet(BayesianNetworkType.getUniqueInstance ());
        HashMap<String, String> ioNet = new HashMap<String, String>();
        ioNet.put("Name", fileName);
        for (int i = 0; i < variablesNames.size (); ++i){ 
            String variableName = variablesNames.get (i);
            List<String> variableStateNames = variablesStatesNames.get (i);
            Map<String, String> infoNode = new HashMap<String, String>();
            State[] states = new State[variableStateNames.size()];
            
            for (int j=0; j <variableStateNames.size (); ++j){
                states[j] = new State(variableStateNames.get (j));
            }
            infoNode.put("Title", variableName);
            infoNode.put("CoordinateX", "0");
            infoNode.put("CoordinateY", "0");
            infoNode.put("UseDefaultStates", "false");
            Variable variable = new Variable(variableName, states);
            ProbNode probNode = probNet.addProbNode(variable, NodeType.CHANCE);
            probNode.additionalProperties = infoNode;
            infoNode.put("Name", variableName);
        }
        probNet.additionalProperties = ioNet;

        return probNet;
    }    

}
