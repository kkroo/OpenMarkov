/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.elvira;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetReader;
import org.openmarkov.core.io.format.annotation.FormatType;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.ProductPotential;
import org.openmarkov.core.model.network.potential.SumPotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.canonical.ICIModelType;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.canonical.MaxPotential;
import org.openmarkov.core.model.network.potential.canonical.MinPotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;

/** Reads a probabilistic network in Elvira format and builds a 
 *  <code>ProbNet</code>
 *  @author marias */
@FormatType( name="ElviraParser", extension = "elv", description="Elvira", role="Reader")
public class ElviraParser implements ProbNetReader {

	// Attributes
	private ProbNet probNet;
	
	private ElviraScanner scanner;
	
	private String fileName;
	
	/** Store in this variable all the ICIPotentials. */
	private ArrayList<ICIPotential> iciPotentials;
	
	/** Store in this variable all the sub-potentials corresponding to 
	 *  ICIPotentials. The key = <code>String</code> is the 
	 *  <code>name-of-relation</code>.<p>
	 *  When the process of reading potentials finishes the 
	 *  <code>iciPotentials</code> are traversed to read all the sub-potentials
	 *  <code>name-of-relation</code>s and this variable is used to locate its 
	 *  sub-potentials. */
	private HashMap<String, TablePotential> subPotentials;

	private boolean continuousVariable;
	
	// Constructor
	/** @param scanner. <code>ElviraScanner</code>  */
	public ElviraParser(ElviraScanner scanner) {
		this.scanner = scanner;
		// Only for canonical models
		iciPotentials = new ArrayList<ICIPotential>();
		subPotentials = new HashMap<String, TablePotential>();
		fileName = null;
	}
	
	public ElviraParser() throws FileNotFoundException
	{
	    this(ElviraScanner.getUniqueInstance());
	}
	
	public ProbNetInfo loadProbNet(String fileName) throws ParserException {
		this.fileName = fileName;
		try {
			scanner.initializeScanner(fileName);
		} catch (FileNotFoundException e) {
			throw new ParserException("File: " + fileName +" not found.");
		}
		// Load probNet
		probNet = new ProbNet();
		getConstraints();
		ElviraToken token;
		try {
			token = getGeneralInfo();
			token = getNodes(token);
			if (token.getReservedWord() == ReservedWord.LINK) {
				token = getLinks(token);
			}
			getPotentials(token);
			ElviraUtil.swapNameAndTitle(probNet);
			
		} catch (IOException e) {
			throw new ParserException("Error reading general information in :" +
					fileName + ".");
		} catch (ProbNodeNotFoundException e) {
			throw new ParserException("Error reading:" + fileName + ":" + e.getMessage ());
		} catch (ConstraintViolationException e) {
			throw new ParserException("Constraint violation exception. " + 
					e.getLocalizedMessage());
		}
		
		addSubPotentials(); // Only for canonical models

		return new ProbNetInfo(probNet, null);
	}
	
	/** Reads the probNet type, creates the right compound constraint and
	 *  associate that constraint to <code>probNet</code> 
	 * @throws ParserException 
	 * @throws IOException */
	/** Reads the probNet type, creates the right compound constraint and
	 *  associate that constraint to <code>probNet</code> 
	 * @throws ParserException 
	 * @throws IOException */
	private void getConstraints() throws ParserException {
		ElviraToken token;
		try {
			token = scanner.getNextToken();
		} catch (IOException e) {
			throw new ParserException("Problem reading constraints.");
		}
		if (token.getTokenType() != TokenType.RESERVED) {
			throw new ParserException("No probNet type.");
		}
		try {
			if (token.getReservedWord() == ReservedWord.BNET) {
				probNet.setNetworkType(BayesianNetworkType.getUniqueInstance());
				probNet.setName(token.getStringValue1());
			} else if (token.getReservedWord() == ReservedWord.IDIAGRAM) {
				probNet.setNetworkType(
						InfluenceDiagramType.getUniqueInstance());
				probNet.setName(token.getStringValue1());
			} else if (token.getReservedWord() == ReservedWord.IDIAGRAMSV) {
				probNet.setNetworkType(
						InfluenceDiagramType.getUniqueInstance());
				probNet.setName(token.getStringValue1());
			} else {
				throw new ParserException("ProbNet type not recognized.");
			}
		} catch (ConstraintViolationException e) {
			throw new ParserException("ProbNet violates exception " + 
					e.toString());
		}
	}


	/** Gets some general information of the probNet and puts it in 
	 *  <code>probNet.properties</code>
	 * @return Next token corresponding to a node (end of general information).
	 *  <code>ElviraToken</code> 
	 * @throws ParserException */
	private ElviraToken getGeneralInfo() throws IOException, ParserException {
		
		ElviraToken token = scanner.getNextToken();
		ReservedWord reservedWord = token.getReservedWord(); 
		while (token.getReservedWord() != ReservedWord.NODE) {
			if (reservedWord == ReservedWord.KIND_OF_NODE) {
				probNet.additionalProperties.put("KindOfGraph", token.getStringValue1());
			} else if (reservedWord == ReservedWord.VISUALPRECISION) {
				probNet.additionalProperties.put("VisualPrecision", 
						Double.toString(token.getDoubleValue()));
			} else if (reservedWord == ReservedWord.VERSION) {
				probNet.additionalProperties.put(
						"Version", Double.toString(token.getDoubleValue()));
			} else if (reservedWord == ReservedWord.DEFAULT) {
                String[] reverseOrderStatesNames = token.getStringListValue();
                int numStates = reverseOrderStatesNames.length;
                State[] states = new State[numStates];
                for (int i = 0; i < numStates; i++) {
                    states[i] = new State(
                            reverseOrderStatesNames[numStates - i - 1]);
                }				
				probNet.setDefaultStates(states);
			} else if (reservedWord == ReservedWord.KIND_OF_GRAPH) {
				probNet.additionalProperties.put("KindOfGraph", token.getStringValue1());
			} else if (reservedWord == ReservedWord.TITLE) {
				probNet.additionalProperties.put("TitleNet", token.getStringValue1());
			} else if (reservedWord == ReservedWord.WHENCHANGED) {
				probNet.additionalProperties.put("WhenChanged", token.getStringValue1());
			} else if (reservedWord == ReservedWord.WHOCHANGED) {
				probNet.additionalProperties.put("WhoChanged", token.getStringValue1());
			}
			token = scanner.getNextToken();
			reservedWord = token.getReservedWord();
		}
		return token;
	}

	/** Reads nodes (variables)
	 * @param token. <code>ElviraToken</code> 
	 * @throws ParserException
	 * @throws IOException 
	 * @throws ConstraintViolationException */
	private ElviraToken getNodes(ElviraToken token) 
			throws IOException, ParserException, ConstraintViolationException {
		do {
			token = getNode(token);
		} while ((token.getReservedWord() != ReservedWord.LINK)  && 
				(token.getReservedWord() != ReservedWord.RELATION));
		return token;
	}

	/** @param token. <code>ElviraToken</code>
	 * @return token. <code>ElviraToken</code> 
	 * @throws ParserException 
	 * @throws IOException 
	 * @throws ConstraintViolationException */
	private ElviraToken getNode(ElviraToken token) 
			throws IOException, ParserException, ConstraintViolationException {
		ProbNode probNode = null;
		String variableName = token.getStringValue1();
		NodeType nodeType = NodeType.CHANCE;
		Variable variable = null;
		HashMap<String, String> infoNode = new HashMap<String, String>();
		int numStates = -1;
		do {
			token = scanner.getNextToken();
			ReservedWord reservedWord = token.getReservedWord();
			if (reservedWord == ReservedWord.TITLE) {
				token = scanner.getNextToken();
				infoNode.put("Title", token.getIdentifierString());

			} else if (reservedWord == ReservedWord.KIND_OF_NODE) {
				token = scanner.getNextToken();
				reservedWord = token.getReservedWord();
				if (reservedWord == ReservedWord.CHANCE) {
					nodeType = NodeType.CHANCE;
				} else if (reservedWord == ReservedWord.DECISION) {
					nodeType = NodeType.DECISION;
				} else {
					nodeType = NodeType.UTILITY;
				}
			} else if (reservedWord == ReservedWord.TYPE_OF_VARIABLE) {
				token = scanner.getNextToken(); // FINITE_STATES or CONTINUOUS
				if (token.isReservedWord()) {
					if (token.getReservedWord() == ReservedWord.CONTINUOUS) {
						continuousVariable = true;
						nodeType = NodeType.UTILITY;
					} else {
						continuousVariable = false;
					}
				}
			} else if (reservedWord == ReservedWord.POSX) {
				infoNode.put("CoordinateX", 
						Integer.toString(token.getIntegerValue()));
				//probNet.getPr
			} else if (reservedWord == ReservedWord.POSY) {
				infoNode.put("CoordinateY", 
						Integer.toString(token.getIntegerValue()));
			} else if (reservedWord == ReservedWord.PRECISION) {
				infoNode.put("Precision", 
						new Double(token.getIntegerValue()).toString());
			} else if (reservedWord == ReservedWord.MIN) {
				infoNode.put("Min", Double.toString(token.getDoubleValue()));
			} else if (reservedWord == ReservedWord.MAX) {
				infoNode.put("Max", Double.toString(token.getDoubleValue()));
			} else if (reservedWord == ReservedWord.RELEVANCE) {
				infoNode.put("Relevance",
						Double.toString(token.getDoubleValue()));
			} else if (reservedWord == ReservedWord.PURPOSE) {
				infoNode.put("Purpose", token.getStringValue1());
	        } else if (reservedWord == ReservedWord.NUM_STATES) {
	            numStates =  token.getIntegerValue ();
	        } else if (reservedWord == ReservedWord.STATES) {
				String[] reverseOrderStatesNames = token.getStringListValue();
				if(numStates < 0)
				{
				    numStates = reverseOrderStatesNames.length;
				}else if(numStates != reverseOrderStatesNames.length){
                    throw new ParserException ("Wrong number of states in node " + variableName
                                               + ": expected " + numStates + ", found "
                                               + reverseOrderStatesNames.length);
				}
				State[] statesNames = new State[numStates];
				for (int i = 0; i < numStates; i++) {
					statesNames[i] = new State(
							reverseOrderStatesNames[numStates - i - 1]);
			}
				variable = new Variable(variableName, statesNames);
				probNode =	probNet.addProbNode(variable, nodeType);
				//probNode.properties = infoNode;
				
			} else if (reservedWord == ReservedWord.COMMENT) {
				infoNode.put("Comment", token.getStringValue1());
			}
		} while ((token.getReservedWord() != ReservedWord.NODE) && 
				(token.getReservedWord() != ReservedWord.LINK) && 
				(token.getReservedWord() != ReservedWord.RELATION));
		if (variable == null) {
			if (continuousVariable) {
				Double min = Double.parseDouble(infoNode.get("Min"));
				Double max = Double.parseDouble(infoNode.get("Max"));
				Double precision = 
					Double.parseDouble(infoNode.get("Precision"));
				if ((min == null) || (max == null) || (precision == null)) {
					throw new ParserException("Missing information in "
							+ "definition of continuos variable " + variableName
							+ " in line " +	scanner.lineno());
				}
				variable = new	Variable(variableName, true, min, max, true, precision);
				probNode =	probNet.addProbNode(variable, nodeType);
				
			} else { // default states
				State[] statesNames = probNet.getDefaultStates();
				variable = new Variable(variableName, statesNames);
				probNode =	probNet.addProbNode(variable, nodeType);
			}
		}
		int coordX = (infoNode.containsKey ("CoordinateX"))? Integer.parseInt(infoNode.get("CoordinateX")) : 150;
        int coordY = (infoNode.containsKey ("CoordinateY"))? Integer.parseInt(infoNode.get("CoordinateY")) : 50;
		probNode.getNode().setCoordinateX(coordX);
		probNode.getNode().setCoordinateY(coordY);
		
		if (infoNode.get("Comment") != null)
			probNode.setComment((String)infoNode.get("Comment"));
		if (infoNode.get("Purpose") != null)
			probNode.setPurpose((String)infoNode.get("Purpose"));
		if (infoNode.get("Relevance") != null)
			probNode.setRelevance(
					Double.parseDouble(infoNode.get("Relevance")));
		if (infoNode.get("Min") != null)
			probNode.additionalProperties.put("Min",infoNode.get("Min"));
		if (infoNode.get("Max") != null)
			probNode.additionalProperties.put("Max",infoNode.get("Max"));
		if (infoNode.get("Precision") != null)
			probNode.additionalProperties.put("Precision",infoNode.get("Precision"));
		if (infoNode.get("Title") != null) {
			probNode.additionalProperties.put("Title", infoNode.get("Title"));
		}
		return token;
	}

	/** Reads and creates links between variables.
	 * @param token. <code>ElviraToken</code>
	 * @return token. <code>ElviraToken</code> 
	 * @throws IOException 
	 * @throws ParserException 
	 * @throws ProbNodeNotFoundException */
	private ElviraToken getLinks(ElviraToken token) 
			throws IOException, ParserException, ProbNodeNotFoundException {
		do {
			String variable1Name = token.getStringValue1();
			String variable2Name = token.getStringValue2();
			ProbNode probNode1 = probNet.getProbNode(variable1Name);
			ProbNode probNode2 = probNet.getProbNode(variable2Name);
			if ((probNode1 == null) || (probNode2 == null)) {
				String msg = new String("");
				if (probNode1 == null) {
					msg = msg + "Variable " + variable1Name + 
					        " does not exists on probNet";
				}
				if (probNode2 == null) {
					if (probNode1 == null) {
						msg = msg + " and v";
					} else {
						msg = msg + "V";
					}
					msg = msg + "ariable " + variable2Name + 
					        " does not exists on probNet";
				}
				msg = msg + " adding link: " + variable1Name + "->" + 
						variable2Name + " at line" + scanner.lineno();
				throw new ParserException(msg);
			}
			probNet.getGraph().addLink(
					probNode1.getNode(), probNode2.getNode(), true);
			token = scanner.getNextToken();
		} while (token.getReservedWord() == ReservedWord.LINK);
		return token;
	}

	/** Reads potentials information and create potentials.
	 * @param token. <code>ElviraToken</code>
	 * @return token. <code>ElviraToken</code> 
	 * @throws ParserException 
	 * @throws IOException 
	 * @throws ProbNodeNotFoundException */
	private void getPotentials(ElviraToken token) 
			throws IOException, ParserException, 
					ProbNodeNotFoundException {
		do {
			// Gets relation variables
			String[] variablesListNames = token.getStringListValue();
			List<Variable> variables = new ArrayList<Variable>();
			for (String variableName : variablesListNames) {
				variables.add(probNet.getVariable(variableName));
			}
			Potential potential = getPotential(variables);
			if (potential != null) { // null->sub-potential of a canonical model
				probNet.addPotential(potential);
			}
			token = scanner.getNextToken();
		} while (token.getReservedWord() != ReservedWord.RIGHTCB);
	}

	/** Reads and create one potential 
 	 * @param variables. <code>ArrayList</code> of <code>Variable</code>
	 * @return potential. <code>Potential</code> 
	 * @throws ParserException 
	 * @throws IOException  */
	private Potential getPotential(List<Variable> variables) 
			throws IOException, ParserException {
		boolean isUtilityPotential = false;
		Variable utilityVariable = null;
		if (variables.get(0).getVariableType() == VariableType.NUMERIC) {
			isUtilityPotential = true;
			utilityVariable = variables.get(0); 
		}
		Potential potential = null;
		HashMap<String, Object> properties = new HashMap<String, Object>();
		ElviraToken token = scanner.getNextToken();
		ReservedWord reservedWord = token.getReservedWord();
		do {
			if (reservedWord == ReservedWord.COMMENT) {
				properties.put("comment", token.getStringValue1());
			} else if (reservedWord == ReservedWord.KIND_OF_RELATION) {
				properties.put("kindrelation", token.getStringValue1());
			} else if (reservedWord == ReservedWord.ACTIVE) {
				properties.put("active", token.getBooleanValue());
			} else if (reservedWord == ReservedWord.NAME) {
				properties.put("name", token.getStringValue1());
			} else if (reservedWord == ReservedWord.DETERMINISTIC) {
				properties.put("deterministic", token.getBooleanValue());
			} else if (reservedWord == ReservedWord.HENRIONVSDIEZ) {
				properties.put("henrionVSdiez", token.getStringValue1());
			} else if (reservedWord == ReservedWord.NAME_OF_RELATION) {
				properties.put("nameOfRelation", token.getStringValue1());
			} else if (reservedWord == ReservedWord.FUNCTION) {//Canonical model
				token = scanner.getNextToken(); // Type of canonical model
				reservedWord = token.getReservedWord();
				ICIModelType model;
				if (reservedWord == ReservedWord.OR) {
					model = ICIModelType.OR;
					potential = new MaxPotential(model, variables);
				} else if (reservedWord == ReservedWord.CAUSAL_MAX) {
					model = ICIModelType.CAUSAL_MAX;
					potential = new MaxPotential(model, variables);
				} else if (reservedWord == ReservedWord.GENERALIZED_MAX) {
					model = ICIModelType.GENERAL_MAX;
					potential = new MaxPotential(model, variables);
				} else if (reservedWord == ReservedWord.AND) {
					model = ICIModelType.AND;
					potential = new MinPotential(model, variables);
				} else if (reservedWord == ReservedWord.CAUSAL_MIN) {
					model = ICIModelType.CAUSAL_MIN;
					potential = new MinPotential(model, variables);
				} else if (reservedWord == ReservedWord.PRODUCT) {
				    potential = new ProductPotential(variables.subList (1, variables.size ()), PotentialRole.UTILITY);
				    potential.setUtilityVariable (variables.get (0));
				} else if (reservedWord == ReservedWord.SUM) {
                    potential = new SumPotential(variables.subList (1, variables.size ()), PotentialRole.UTILITY);
                    potential.setUtilityVariable (variables.get (0));
				} else {
					model = ICIModelType.GENERAL_MIN;
					potential = new MinPotential(model, variables);
				}
				if (reservedWord != ReservedWord.PRODUCT && 
						reservedWord != ReservedWord.SUM){
					potential.properties = properties;
					properties.put("Relations", token.getStringListValue());
					iciPotentials.add((ICIPotential)potential);
				}
			} else if ((reservedWord == ReservedWord.TABLE) || 
					(reservedWord == ReservedWord.GENERALIZED_TABLE)) {
				PotentialRole role = PotentialRole.CONDITIONAL_PROBABILITY;
				if (isUtilityPotential) {
					variables.remove(utilityVariable);
					role = PotentialRole.UTILITY;
				}
				variables = elvira2OpenMarkovVariables(variables);
				TablePotential tablePotential = 
					new TablePotential(variables, role);
				tablePotential.values = token.getDoublesTableValue();
				tablePotential = elvira2OpenMarkovPotential(tablePotential);
				if (isUtilityPotential) {
					tablePotential.setUtilityVariable(utilityVariable);
				}
				tablePotential.properties = properties;
				// If it is part of a canonical model adds it to the last
				// ICIPotential
				String nameOfRelation = (String)
					tablePotential.properties.get("nameOfRelation");
				potential = tablePotential;
				if (nameOfRelation != null) {// Canonical model sub-potential
					// Add a sub-potential only if there is no other previously
					// with the same name.
					// This is impossible to happen if the writer is well done.
					if (subPotentials.get(nameOfRelation) == null) {
						subPotentials.put(nameOfRelation, tablePotential);
					}
					potential = null; // Do not add yet this potential
				}
			}
			token = scanner.getNextToken();
			reservedWord = token.getReservedWord();
		} while (token.getReservedWord() != ReservedWord.RIGHTCB);
		return potential;
	}
	
	/** This method is used to translate elvira potentials to openmarkov potentials.
	 * @return A <code>TablePotential</code> with the same variables but
	 * in OpenMarkov order: First the conditioned variable and the first 
	 * configuration equals to (no, no...no), increasing first the left-most 
	 * variable: Conf. 0 = (no, no...no) -> Conf. 1 = (yes, no...no), etc. 
	 * @param elviraPotential A <code>TablePotential</code>
	 * @throws <code>NotEnoughtMemoryException</code> */
	static TablePotential elvira2OpenMarkovPotential(TablePotential elviraPotential) {
		List<Variable> elviraVariables = elviraPotential.getVariables();
		List<Variable> auxVariables = elvira2OpenMarkovVariables(elviraVariables);
		
		// Invert potential values
		double[] table = elviraPotential.values;
		double aux;
		int sizePotential = table.length, halfPotential = sizePotential / 2;
		for (int i = 0; i < halfPotential; i++) {
			aux = table[i];
			table[i] = table[sizePotential - i - 1];
			table[sizePotential - i - 1] = aux;
		}

	    // Creation of openmarkov potential reordering configurations
	    TablePotential openMarkovPotential = DiscretePotentialOperations.reorder(
			elviraPotential, auxVariables);
		return openMarkovPotential;
	}
	
	/** This method is used to translate ICI elvira potentials to ICI openmarkov 
	 *  potentials.
	 * @return A <code>ICIPotential</code> with the same variables but
	 *  in OpenMarkov order: First the conditioned variable 
	 * @param elviraPotential A <code>ICIPotential</code>
	 * @throws <code>NotEnoughtMemoryException</code> */
	static ICIPotential elvira2ICIOpenMarkovPotential(ICIPotential elviraPotential)
	{
		List<Variable> elviraVariables = elviraPotential.getVariables();
		List<Variable> auxVariables = new ArrayList<Variable>();
		int numVariables = elviraVariables.size();
		
		// Change variables order. Conditioned variable is the last one.
		for (int i = numVariables - 1; i >= 0; i--) {
			auxVariables.add(elviraVariables.get(i));
		}
		
		ICIModelType model = elviraPotential.getModelType();
		
	    // Creation of ICI OpenMarkov potential
		ICIPotential openMarkovPotential = null;
		if ((model == ICIModelType.OR) || (model == ICIModelType.CAUSAL_MAX) || 
				(model == ICIModelType.GENERAL_MAX)) {
			openMarkovPotential = new MaxPotential(model, auxVariables);
		} else {
			openMarkovPotential = new MinPotential(model, auxVariables);
		}
	    openMarkovPotential.properties = elviraPotential.properties;
		return openMarkovPotential;
	}
	
	static List<Variable> elvira2OpenMarkovVariables(List<Variable> elviraVariables) {
		int numVariables = elviraVariables.size();	
		List<Variable> auxVariables = new ArrayList<Variable>();
		// Change variables order. Conditioned variable is the last one.
		for (int i = numVariables - 1; i >= 0; i--) {
			auxVariables.add(elviraVariables.get(i));
		}
		return auxVariables;
	}

	/** Adds the sub-potentials stored in <code>subPotentials</code> to 
	 *  <code>ICIPotentials</code>.
	 * @throws ParserException if remains one or more sub-potential or there are
	 *  some missing relation. */
	private void addSubPotentials() throws ParserException {
		for (ICIPotential potential : iciPotentials) {
			String[] relations = 
				(String[])potential.properties.get("Relations");
			for (String relation : relations) {
				TablePotential subPotential = subPotentials.get(relation);
				if (subPotential == null) {
					throw new ParserException("Sub-potential " + relation + 
							" does not exist.");
				} else {
				    if(subPotential.getVariables ().size () > 1)
				    {
				        potential.setNoisyParameters ( subPotential.getVariable (1), //parent
				                                       subPotential.values);
				    }else
				    {
                        potential.setLeakyParameters ( subPotential.values);
				    }
                    subPotentials.remove(relation);
				    
				}
			}
		}
		if (subPotentials.size() > 0) {
			throw new ParserException("There are " + subPotentials.size() + 
					" sub-potentials not linked no an ICIPotential");
		}
	}
	
	public String toString() {
		String parser = new String();
		if (fileName != null) {
			parser = parser + "File: " + fileName;
		} else {
			parser = parser + "No file";
		}
		return parser;
	}

}
