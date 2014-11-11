/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/


package org.openmarkov.io.database.elvira;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseReader;
import org.openmarkov.core.io.database.CaseDatabaseWriter;
import org.openmarkov.core.io.database.exception.UnableToCreateFile;
import org.openmarkov.core.io.database.exception.UnableToOpenDBException;
import org.openmarkov.core.io.database.exception.UnknownNetworkTypeException;
import org.openmarkov.core.io.database.plugin.CaseDatabaseFormat;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/** This class contains some routines to load a database from a '.dbc' file 
 * (the format used by Elvira). 
 * @author joliva
 * @author manuel       
 * @author fjdiez               
 * @version 1.0
 * @since OpenMarkov 1.0 */
@CaseDatabaseFormat(extension = "dbc", name = "ElviraDB")
public class ElviraDataBaseIO implements CaseDatabaseReader, CaseDatabaseWriter{
    
    private HashMap<String, Object> ioNet;
    
    /**
     * Opens a database file (in '.dbc' format) and creates the associated 
     * network 
     *
     * @param fileName <code>String</code> with the name of the 
     * file where the network is saved.
     * @return <code>int[][]</code> matrix with the cases in the database.
     * @throws Exception if the file does not exist or the file format is not 
     * correct.
     */
    public CaseDatabase load(String fileName) throws IOException {
    	ElviraDBParser parser;
    	FileInputStream fileStream = null;
    	HashMap <String,String> properties = new HashMap<String, String>();

    	try {
    	    fileStream = new FileInputStream(fileName);
    		parser = new ElviraDBParser(new ElviraDBLexer(fileStream));
    		ioNet = parser.database();                   
    		ProbNet probNet = (ProbNet) ioNet.get("ProbNet");
    		for (Entry<String, Object> property : ioNet.entrySet())
            	properties.put(property.getKey(), property.getValue().
            			toString());
            probNet.additionalProperties = properties;
    		return new CaseDatabase(probNet.getVariables (), parser.getCases());
    	} catch (RecognitionException e) {
			e.printStackTrace();
    		throw new IOException("RecognitionException open DBFile: " + 
    				fileName);
		} catch (TokenStreamException e) {
			e.printStackTrace();
    		throw new IOException("TokenStreamException in file: " + 
    				fileName);
		} finally {
    		if (fileStream != null) {
    			fileStream.close();
    		}
    	}
    }
    
    /** @param fileName We suppose that this <code>String</code> contains also
     *   the path
     * @param probNet <code>ProbNet</code>
     * @param cases <code>int[][]</code>
     * @throws UnableToCreateFile 
     * @throws UnableToOpenDBException */
    public void save(String fileName, CaseDatabase database)
            throws IOException{
            save(fileName, database.getCases (), database.getVariables ());
    }

    /** @param fileName <code>String</code>
     * @param infoNet <code>HashMap</code> 
     * @param cases <code>int[][]</code>
     * @throws UnableToOpenDBException 
     * @throws UnableToCreateFile */
    public void save (String fileName,
                      int[][] cases,
                      List<Variable> variables)
        throws IOException
    {
        FileWriter writer;
        PrintWriter out;
        writer = new FileWriter (fileName);
        out = new PrintWriter (writer);
        try
        {
            save (out, variables, cases);
            out.close ();
        }
        catch (UnknownNetworkTypeException e)
        {
            e.printStackTrace ();
        }
    }

    /**
     * This method writes the <code>BayesNet</code> in a file
     * @param out <code>PrintWriter</code>
     * @param probNet <code>ProbNet</code>
     */
    private void save (PrintWriter out, List<Variable> variables, int[][] cases)
        throws UnknownNetworkTypeException
    {
        writePreamble (out, new HashMap<String, String> (), cases.length);
        writeVariables (out, variables);
        writeRelation (out, cases);
    }

    /** @param out <code>PrintWriter</code>
     * @param infoNet <code>HashMap</code> 
     * @throws UnknownNetworkTypeException */
    private void writePreamble (PrintWriter out,
                                HashMap<String, String> infoNet,
                                int numCases) throws UnknownNetworkTypeException
    {
            // preamble comment
            out.println("//	   Network");
            out.println("//	   Elvira format");
            out.println();

            // bnet or influence diagram
//            @SuppressWarnings("unused")
//            Object object = infoNet.get("BayesNet");
//            if (object != null) {
                    out.print("data-base ");
//            } else {
//                    throw new UnknownNetworkTypeException ();
//            }
            out.print('"'); 
            String name = infoNet.get("Name");
            if (name != null) {
                out.println(name + '"' + " {");
            } else {
                    out.println("NoNameNet" + '"' + " {");
            }
            out.println();

            out.println("number-of-cases = " + numCases + ";");

            // additionalProperties bnet comment
            out.println("//		 Network Properties");
            out.println();

            // kindofgraph = "...";
            String objKindOfGraph = infoNet.get("KindOfGraph");
            if (objKindOfGraph != null) {
                    out.println("kindofgraph = " + '"' + objKindOfGraph + '"' + 
                            ';');
            }

            // title = "...";
            String title = infoNet.get("TitleNet");
            if (title != null) {
                    out.print("title = ");
                    out.print('"');
                    out.print(title); 
                    out.print('"');
                    out.println(";");
            }

            // comment = "...";
            String comment = infoNet.get("CommentNet");
            if (comment != null) {
                    out.print("comment = ");
                    out.print('"');
                    out.print(comment);
                    out.print('"');
                    out.println(";");
            }

            // author = "...";
            String author = infoNet.get("AuthorNet");
            if (author != null) {
                    out.print("author = ");
                    out.print('"');
                    out.print(author);
                    out.print('"');
                    out.println(";");
            }

            // whochanged = "...";
            String whochanged = infoNet.get("WhoChanged");
            if (whochanged != null) {
                    out.print("whochanged = ");
                    out.print('"');
                    out.print(whochanged);
                    out.print('"');
                    out.println(";");
            }

            // whenchanged = "...";
            String whenchanged = infoNet.get("WhenChanged");
            if (whenchanged != null) {
                    out.print("whenchanged = ");
                    out.print('"');
                    out.print(whenchanged);
                    out.print('"');
                    out.println(";");
            }

            // visualprecision = "...";
            String objVisualPrecision = infoNet.get("VisualPrecision");
            if (objVisualPrecision != null) {
                    out.println(
                            "visualprecision = " + '"' + objVisualPrecision 
                            + '"' + ';');
            }

            // version = ...;
            String objVersion = infoNet.get("Version");
            if (objVersion != null) {
                    out.println("version = " + objVersion + ';');
            }

            // node default states
            Object objDefaultStates = infoNet.get("DefaultNodeStates");
            if (objDefaultStates != null) {
                    String[] defaultStates = (String[])objDefaultStates;
                    out.print("default node states = (");
                    for (int i = 0; i < defaultStates.length - 1; i++) {
                            out.print('"' + defaultStates[i] + '"' + " , ");
                    }
                    out.println(
                            '"' + defaultStates[defaultStates.length - 1] + '"' 
                            + ");");
            }
            out.println();
    }

    /** @param out <code>PrintWriter</code>
	 * @param infoNet <code>InfoNet</code> */
    private static void writeVariables (PrintWriter out, List<Variable> variables)
    {
		// write coment
		out.println("// Variables");
		out.println();

		// write variables
		for (Variable variable: variables) {
            if(variable.getName().contains(" ")){
                out.print("node \"" + variable.getName() + "\"(");
            }
            else {
            	out.print("node " + variable.getName() + "(");
            }

			VariableType variableKind = variable.getVariableType();
			switch (variableKind) {
			case FINITE_STATES: {
				out.print("finite-states");
				break;
			}
			case NUMERIC: {
				out.print("continuous");
				break;
			}
			case DISCRETIZED: {
				out.print("hybrid");
				break;
			}
			}

			out.println(") {");

			// write kind of node
			out.println("kind-of-node = chance;");

			// write kind of variable
			variableKind = variable.getVariableType();
			out.print("type-of-variable = ");
			switch (variableKind) {
			case FINITE_STATES: {
				out.print("finite-states");
				break;
			}
			case NUMERIC: {
				out.print("continuous");
				break;
			}
			case DISCRETIZED: {
				out.print("hybrid");
				break;
			}
			}
			out.println(';');

			// end of node
			out.println('}');
			out.println();
		}

	}

    /** @param out <code>PrintWriter</code> 
     * @param cases <code>int[][]</code> database cases */
    private static void writeRelation(PrintWriter out, int[][] cases) {

            out.println();

            out.println("relation  {");
            out.println("memory = true;");
            out.println("cases = (");

            for (int i = 0; i < cases.length; i++) {
                if (cases[i]!=null){
                    out.print("[");
                    for(int j = 0; j < cases[i].length-1; j++)
                        out.print(cases[i][j] + ",");
                    out.println(cases[i][cases[i].length-1] + "]");    
                }
            }
            out.println(");");
            out.println('}');
            out.println('}');
            out.println();
    }

    /** @param string with an integer or something else.
	 * @return <code>true</code> if <code>string</code> contains an integer. */
	private static boolean isInteger(String string) {
		try {
			int integer = Integer.parseInt(string);
			int numDigits = 0;
			do {
				integer = integer / 10;
				numDigits++;
			} while (integer > 0);
			if (numDigits != string.length()) {
				return false;
			}
		} catch (NumberFormatException n) {
			return false;
		}
		return true;
	}

}
