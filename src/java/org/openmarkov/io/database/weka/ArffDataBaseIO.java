/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/


package org.openmarkov.io.database.weka;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseReader;
import org.openmarkov.core.io.database.CaseDatabaseWriter;
import org.openmarkov.core.io.database.plugin.CaseDatabaseFormat;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/** This class contains some routines to load a database from a '.arff' file 
 * (the format used by Weka). 
 * @author joliva
 * @author manuel       
 * @author fjdiez               
 * @version 1.0
 * @since OpenMarkov 1.0 */
@CaseDatabaseFormat(extension = "arff", name = "WekaDB")
public class ArffDataBaseIO implements CaseDatabaseReader, CaseDatabaseWriter {
    
    private HashMap<String, Object> ioNet;
    
    /**
     * Opens a database file (in '.arff' format) and creates the associated 
     * network 
     *
     * @param filename <code>String</code> with the name of the 
     * file where the network is saved.
     * @return <code>int[][]</code> matrix with the cases in the database.
     * @throws IOException 
     * @throws InvalidStateException 
     * @throws Exception if the file does not exist or the file format is not 
     * correct.
     */
    public CaseDatabase load(String filename) throws IOException {
        
        ArffParser parser;
        FileInputStream fileStream = null;
        HashMap<String, String> properties = new HashMap<String, String> ();
        try
        {
            fileStream = new FileInputStream (filename);
            parser = new ArffParser (new ArffLexer (fileStream));
            ioNet = parser.relation ();
            ProbNet probNet = (ProbNet) ioNet.get ("ProbNet");
            for (Entry<String, Object> property : ioNet.entrySet ())
            {
                properties.put (property.getKey (),
                                property.getValue ().toString ());
            }
            probNet.additionalProperties = properties;
            return new CaseDatabase (probNet.getVariables (), parser.getCases());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace ();
            throw new IOException ("File " + filename + " not found.");
        }
        catch (RecognitionException e)
        {
            e.printStackTrace ();
            throw new IOException ("RecognitionException in " + filename + ".");
        }
        catch (TokenStreamException e)
        {
            e.printStackTrace ();
            throw new IOException ("TokenStreamException in " + filename + ".");
        }
        finally
        {
            if (fileStream != null)
            {
                fileStream.close ();
            }
        }
    }
    
    /** This method writes an Arff database file
     * @param filename <code>String</code> path to the resulting .arff file
     * @param probNet <code>ProbNet</code> contains the list of variables
     * @param cases <code>int[][]</code> examples to save in the database
     * @throws java.lang.Exception
     */
    public void save (String filename, CaseDatabase database)
        throws IOException
    {
        FileOutputStream stream = new FileOutputStream (filename);
        OutputStreamWriter output = new OutputStreamWriter (stream);
        State[] states;
        boolean numeric;
        /* Relation name */
        String relationName = filename;
        output.write ("\n@RELATION \"" + relationName + "\"\n");
        /* Attributes and values */
        for (Variable variable : database.getVariables ())
        {
            String nodeName = variable.getName ();
            if (nodeName.contains (" ")) output.write ("\n@ATTRIBUTE \""
                                                       + nodeName + "\" ");
            else output.write ("\n@ATTRIBUTE " + nodeName + " ");
            states = variable.getStates ();
            /*
             * Before printing the states of the node, we have to know if the
             * node is numeric or not.
             */
            numeric = true;
            for (int i = 0; i < states.length; i++)
            {
                try
                {
                    if (!states[i].getName ().equals ("?"))
                    {
                        Integer.parseInt (states[i].getName ());
                    }
                }
                catch (NumberFormatException e)
                {
                    numeric = false;
                }
            }
            if (numeric)
            {
                output.write ("numeric {");
            }
            else
            {
                output.write ("{");
            }
            for (int i = 0; i < states.length; i++)
            {
                if (states[i].getName ().equals ("?"))
                {
                    if (i == 0)
                    {
                        continue;
                    }
                    else if (i != states.length - 1)
                    {
                        output.write (",");
                    }
                }
                else
                {
                    if (states[i].getName ().contains (" "))
                    {
                        output.write ("\"" + states[i].getName () + "\"");
                    }
                    else
                    {
                        output.write (states[i].getName ());
                    }
                    if ((i != states.length - 1)
                        && (!states[i + 1].getName ().equals ("?")))
                    {
                        output.write (",");
                    }
                }
            }
            output.write ("}\n");
        }
        /*
         * Data. If any of the attributes is "String", then we have to get the
         * correct index.
         */
        output.write ("\n@DATA\n");
        List<Variable> variables = database.getVariables ();
        int[][] cases = database.getCases ();
        for (int i = 0; i < cases.length; i++)
        {
            for (int j = 0; j < cases[i].length; j++)
            {
                states = variables.get (j).getStates ();
                if (states[cases[i][j]].getName ().contains (" "))
                {
                    output.write ("\"" + states[cases[i][j]].getName () + "\"");
                }
                else
                {
                    output.write (states[cases[i][j]].getName ());
                }
                if (j != cases[i].length - 1)
                {
                    output.write (",");
                }
            }
            output.write ("\n");
        }
        output.close ();
    }
}
