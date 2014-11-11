/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.elvira;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WriterException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.core.io.format.annotation.FormatType;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.canonical.ICIModelType;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.NetworkType;

/** This class writes a <code>ProbNet</code> in elvira format. 
 * @author marias */
@FormatType( name="ElviraWriter", extension = "elv", description="Elvira", role="Writer")
public class ElviraWriter implements ProbNetWriter {

	// Attributes

	// @Override
	/**
	 * @param netName
	 *            = path + network name + extension.
	 * @param probNet
	 *            . <code>ProbNet</code> <code>String</code>
	 * @throws IOException
	 * @throws WriterException 
	 */
	public void writeProbNet(String netName, ProbNet probNet)
			throws WriterException {
		if (probNet.additionalProperties.get("hasElviraProperties") == null) {
			generateElviraProperties(probNet);
		}
		OutputStream writer;
		PrintWriter out;
		try {
			writer = new FileOutputStream(netName);
		} catch (IOException e) {
			throw new WriterException("Can not create file " + netName + ".");
		}
		out = new PrintWriter(new BufferedWriter (new OutputStreamWriter(writer, Charset.forName("windows-1252"))));
		ElviraUtil.swapNameAndTitle(probNet);
		writeElviraNetwork(out, probNet);
		ElviraUtil.swapNameAndTitle(probNet); // Restore previous version
		out.close();
	}

	/**
	 * This method writes the <code>BayesNet</code> in a file
	 * 
	 * @param out
	 *            <code>PrintWriter</code>
	 * @param infoNet
	 *            <code>InfoNet</code>
	 * @throws WriterException
	 */
	private void writeElviraNetwork(PrintWriter out, ProbNet probNet)
			throws WriterException {
		writeElviraPreamble(out, probNet);
		writeElviraNodes(out, probNet);
		writeElviraLinks(out, probNet);
		writeElviraRelations(out, probNet);
	}

	/** @param out. <code>PrintWriter</code>
	 * @param infoNet. <code>InfoNet</code>
	 * @throws WriterException */
	private void writeElviraPreamble(PrintWriter out,
				ProbNet probNet) throws WriterException {	
		// preamble comment
		out.println("//	   Network");
		out.println("//	   Elvira format");
		out.println();

		//Object object = infoNet.get("InfluenceDiagram");
		NetworkType networkType = probNet.getNetworkType();
		
		if (networkType instanceof InfluenceDiagramType){
			out.print("idiagram ");
		} else if (networkType instanceof BayesianNetworkType){
				out.print("bnet ");
			} else {
				throw new WriterException("Network type unknown: " + 
						"neither Bayesian or IDiagram.");
		}
		
		out.print('"');
		
		if (probNet.getName() != null) {
			out.println(probNet.getName() + '"' + " {");
		} else {
			out.println("NoNameNet" + '"' + " {");
		}
		out.println();

		// additionalProperties bnet comment
		out.println("//		 Network Properties");
		out.println();

		//kindofgraph = "...";
		Object objKindOfGraph = probNet.additionalProperties.get("KindOfGraph");
		
		
		probNet.getGraph().toString();
		if (objKindOfGraph != null) {
			String kindOfGraph = objKindOfGraph.toString();
			out.println("kindofgraph = " + '"' + kindOfGraph + '"' + ';');
		}
		

		// comment = "...";
		String comment = probNet.getComment();
		if (comment != null) {
			out.print("comment = ");
			out.print('"');
			out.print(comment);
			out.print('"');
			out.println(";");
		}

		// author = "...";
		/*String author = (String) infoNet.get("AuthorNet");
		if (author != null) {
			out.print("author = ");
			out.print('"');
			out.print(author);
			out.print('"');
			out.println(";");
		}

		// whochanged = "...";
		String whochanged = (String) infoNet.get("WhoChanged");
		if (whochanged != null) {
			out.print("whochanged = ");
			out.print('"');
			out.print(whochanged);
			out.print('"');
			out.println(";");
		}

		// whenchanged = "...";
		String whenchanged = (String) infoNet.get("WhenChanged");
		if (whenchanged != null) {
			out.print("whenchanged = ");
			out.print('"');
			out.print(whenchanged);
			out.print('"');
			out.println(";");
		}

		// visualprecision = "...";
		Object objVisualPrecision = infoNet.get("VisualPrecision");
		if (objVisualPrecision != null) {
			String visualPrecision = objVisualPrecision.toString();
			out.println("visualprecision = " + '"' + visualPrecision + '"'
					+ ';');
		}

		// version = ...;
		Object objVersion = infoNet.get("Version");
		if (objVersion != null) {
			String version = objVersion.toString();
			out.println("version = " + version + ';');
		}*/

		// node default states
		//Object objDefaultStates = infoNet.get("DefaultNodeStates");
		Object objDefaultStates = probNet.getDefaultStates();
		if (objDefaultStates != null) {
			State[] defaultStates = (State[]) objDefaultStates;
			out.print("default node states = (");
			for (int i = defaultStates.length - 1; i >= 1; i--) {
				out.print('"' + defaultStates[i].getName() + '"' + " , ");
			}
			out.println('"' + defaultStates[0].getName() + '"' + ");");
		}
		out.println();
	}

	/**
	 * @param out
	 *            <code>PrintWriter</code>
	 * @param infoNet
	 *            <code>InfoNet</code>
	 */
	private void writeElviraNodes(PrintWriter out, ProbNet probNet) {
		// write coment
		out.println("// Variables");
		out.println();

		// write nodes
		List<ProbNode> probNodes = probNet.getProbNodes();
		for (ProbNode probNode : probNodes) {
			if (probNode.getVariable().getName().contains(" ")) {
				out.print("node \"" + probNode.getVariable().getName() + "\"(");
			} else {
				out.print("node " + probNode.getVariable().getName() + "(");
			}

			VariableType variableKind = (VariableType) probNode.getVariable().
			getVariableType();
			
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

			// write comment
			//hay dos tipos de comentarios para un nodo (de definición y de 
			//tablas de probabilidad) en OpenMarkov
			String comment = probNode.getComment();
			
			if (comment != null) {
				out.print("comment = ");
				out.print('"');
				out.print(comment);
				out.print('"');
				out.println(';');
			}

			// write kind of node
			NodeType nodeType = probNode.getNodeType();
			String nodeKindName = nodeType.name().toString();
			nodeKindName.toLowerCase();
			out.println("kind-of-node = " + nodeKindName.toLowerCase() + ";");

			// write kind of variable
			variableKind = probNode.getVariable().getVariableType();
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

			// write posX
			Integer coordinateX = (int) probNode.getNode().getCoordinateX();
			out.println("pos_x =" + coordinateX.toString() + ";");

			// write posY
			Integer coordinateY = (int) probNode.getNode().getCoordinateY();
			out.println("pos_y =" + coordinateY.toString() + ";");

			// write node relevance
			Double relevance = probNode.getRelevance();
			if ((relevance != null)
					&& (relevance.doubleValue() > Double.MIN_VALUE)) {
				out.println("relevance = " + relevance.toString() + ";");
			}

			// write purpose node
			String purpose = probNode.getPurpose();
			if (purpose != null) {
				out.print("purpose = ");
				out.print('"');
				out.print(purpose);
				out.print('"');
				out.println(';');
			}

			// write number of states
			//TODO revisar el uso de "UseDefaultStates"
			if (variableKind != VariableType.NUMERIC) {
				int numStates = probNode.getVariable().getNumStates();
				boolean defaultStates = false;
				if ((probNode.additionalProperties.get("UseDefaultStates") != null)
						&& Boolean.parseBoolean(probNode.additionalProperties
								.get("UseDefaultStates"))) {
					defaultStates = true;
					out.print("//");
				}
				out.println("num-states = " + numStates + ";");
				if (!defaultStates) { // print states
					State[] reverseOrderStates = 
						probNode.getVariable().getStates();
					
					State[] states = new State[numStates];
					for (int i = 0; i < numStates; i++) {
						states[i] = reverseOrderStates[numStates - i - 1];
					}

					out.print("states = (");
					int numStates_1 = states.length - 1;
					for (int i = 0; i < numStates_1; i++) {
						if (isInteger(states[i].getName())) {
							out.print(states[i].getName() + " ");
						} else {
							out.print('"' + states[i].getName() + '"' + " ");
						}
					}
					if (isInteger(states[numStates_1].getName())) {
						out.println(states[numStates_1].getName() + ");");
					} else {
						out.println('"' + states[numStates_1].getName() + '"' +
								");");
					}
				}
			} else {
				String min = probNode.additionalProperties.get("Min").toString();
				if (min != null) {
					out.println("min = " + min + ";");
				}
				String max = probNode.additionalProperties.get("Max").toString();
				if (max != null) {
					out.println("max = " + max + ";");
				}
				String precision = probNode.additionalProperties.get("Precision")
						.toString();
				if (precision != null) {
					out.println("precision = " + precision + ";");
				}
			}

			// end of node
			out.println('}');
			out.println();
		}

	}

	/**
	 * @param out
	 *            <code>PrintWriter</code>
	 * @param infoNet
	 *            <code>InfoNet</code>
	 */
	private void writeElviraLinks(PrintWriter out, ProbNet probNet) {
		// links comment
		out.println("//		 Links of the associated graph:");
		out.println();

		// links
		List<ProbNode> probNodes = probNet.getProbNodes();
		for (ProbNode parentProbNode : probNodes) {
			List<Node> children = parentProbNode.getNode().getChildren();
			for (Node child : children) {
				out.print("link ");
				if (parentProbNode.getVariable().getName().contains(" ")) {
					out.print("\"" + parentProbNode.getVariable().getName()	+ "\" ");
				} else
					out.print(parentProbNode.getVariable().getName() + " ");
				ProbNode childProbNode = (ProbNode) child.getObject();
				if (childProbNode.getVariable().getName().contains(" ")) {
					out.println("\"" + childProbNode.getVariable().getName() + "\";");
				} else
					out.println(childProbNode.getVariable().getName() + ";");
				out.println();
			}
		}
	}

	/**
	 * @param out
	 *            <code>PrintWriter</code>
	 * @param infoNet
	 *            <code>InfoNet</code>
	 * @throws WriterException 
	 */
	private void writeElviraRelations(PrintWriter out, ProbNet probNet)
			throws WriterException {
		// relations comment
		out.println("//		Network Relationships:");
		out.println();

		// relations
		List<Potential> potentials = probNet.getPotentials();
		for (Potential potential : potentials) {
			writeElviraTablePotential(out, potential);
		}
		out.println('}');
		out.println();
	}
	
	/** @param out. <code>PrintWriter</code>
	 * @param potential. <code>Potential</code>
	 * @throws WriterException */
	private void writeElviraTablePotential(PrintWriter out, Potential potential) 
			throws WriterException {
		writeCommonElviraPotentialPreamble(out, potential);
		TablePotential elviraPotential;
		if (potential.getClass() != TablePotential.class) {
			if (potential instanceof ICIPotential) {
				writeICIElviraPotentialPreamble(out, potential.getVariables());
				writeICIElviraPotentialBody(out, (ICIPotential)potential);
			} else {
				try {
					elviraPotential = potential.tableProject(null, null).get(0);
				} catch (NonProjectablePotentialException e) {
					throw new WriterException("Can not project potential type " + 
							potential.getClass().toString() + " to a TablePotential in " +
									"ElviraWriter.");
				} catch (WrongCriterionException e) {
					throw new WriterException("Exception writing potential type " + 
							potential.getClass().toString() + ": " + e.getMessage());
				}
				writeElviraTable(out, openMarkov2ElviraPotential(elviraPotential));
			}
		} else {
			writeElviraTable(out, openMarkov2ElviraPotential((TablePotential)potential));
		}		
	}
	
	/** @param out. <code>PrintWriter</code>
	 * @param variables. <code>ArrayList</code> of <code>Variable</code> */
	private void writeSubPotentialTrash(PrintWriter out, List<Variable> variables) {
		out.println("comment = \"new\";");
		writeICIElviraPotentialPreamble(out, variables);
	}

	/** @param out. <code>PrintWriter</code>
	 * @param potential. <code>Potential</code>	 */
	private void writeICIElviraPotentialPreamble(PrintWriter out, List<Variable> variables) {
		out.println("kind-of-relation = potential;");
		out.println("active=false;");
		out.print("name-of-relation = ");
		for (Variable variable : variables) {
			out.print(variable.getName());
		}
		if (variables.size() == 1) {
			out.print("Residual");
		}
		out.print(";");
		out.println("deterministic=false;");
	}
	
	/** @param out. <code>PrintWriter</code>
	 * @param potential. <code>Potential</code>	 */
	private void writeCommonElviraPotentialPreamble(PrintWriter out, Potential potential) {
		out.print("relation ");
		// The potentials in OpenMarkov are stored in the opposite in Elvira
		// The same method do the two conversions:
		// Elvira -> OpenMarkov and OpenMarkov -> Elvira
		if (potential.isUtility()) {
			writeUtilityVariable(out, potential.getUtilityVariable());
		}
		writeVariables(out, potential.getVariables());

		out.println('{');
	}
	
	/** @param out. <code>PrintWriter</code>
	 * @param utilityVariable. <code>Variable</code> */
	private void writeUtilityVariable(PrintWriter out, Variable utilityVariable) {
		if (utilityVariable != null) {
			if (utilityVariable.getName().contains(" ")) {
				out.print("\"" + utilityVariable.getName() + "\" ");
			} else
				out.print(utilityVariable.getName() + " ");
		}
	}
	
	/** @param out. <code>PrintWriter</code>
	 * @param utilityVariable. <code>ArrayList</code> of <code>Variable</code> */
	private void writeVariables(PrintWriter out, List<Variable> variables) {
		int numVariables = variables.size();
		for (int i = 0; i < numVariables; i++) {
			if (variables.get(i).getName().contains(" ")) {
				out.print("\"" + variables.get(i).getName() + "\" ");
			} else
				out.print(variables.get(i).getName() + " ");
		}		
	}

	/** @param out. <code>PrintWriter</code>
	 * @param elviraPotential. <code>TablePotential</code>  */
	private void writeElviraTable(PrintWriter out, TablePotential elviraPotential) {
		HashMap<String, Object> infoPotential = elviraPotential.properties;
		if ((infoPotential != null) && (infoPotential.size() > 0)) {
			String comment = (String) infoPotential.get("comment");
			if ((comment != null) && (comment.length() > 0)) {
				out.println("comment = " + '"' + comment + '"' + ";");
			}
			String kindOfRelation = (String) infoPotential
					.get("kindrelation");
			if (kindOfRelation != null) {
				out.println("kind-of-relation = " + kindOfRelation + ";");
			}
			String deterministic = infoPotential.get("deterministic").toString();
			if (deterministic != null) {
				out.println("deterministic=" + deterministic + ";");
			}
		}

		// write table
		writeElviraTable(out, null, elviraPotential.values);
		out.println();
	}

	/** @param variables TODO
	 * @param out. <code>PrintWriter</code>
	 * @param values. <code>double[]</code>  */
	private void writeElviraTable(PrintWriter out, List<Variable> variables, double[] values) 
	{
		if (variables != null) {
			TablePotential openMarkovPotential = 
					new TablePotential(variables, PotentialRole.CONDITIONAL_PROBABILITY, values);
			TablePotential elviraPotential = openMarkov2ElviraPotential(openMarkovPotential);
			values = elviraPotential.values;
		}
		out.print("values = table(");
		for (int i = 0; i < values.length; i++) {
			out.print(values[i]);
			if (i < values.length - 1) {
				out.print(" ");
			}
			if (((i + 1) % 20) == 0) {
				out.println();
			}
		}
		out.println(" );");
		out.println('}');
	}

	/** @param out. <code>PrintWriter</code>
	 * @param potential. <code>ICIPotential</code>
	 * @throws <code>WriterException</code> */
	private void writeICIElviraPotentialBody(PrintWriter out, ICIPotential potential) 
			throws WriterException {
		out.println("values = function ");
		out.print("          ");
		ICIModelType modelType = potential.getModelType();
		switch (modelType) {
		case OR: out.print("Or"); break;
		case CAUSAL_MAX: out.print("CausalMax"); break;
		case GENERAL_MAX: out.print("GeneralizedMax"); break;
		case AND: out.print("And"); break;
		case CAUSAL_MIN: out.print("CausalMin"); break;
		case GENERAL_MIN: out.print("GeneralizedMin"); break;
		default: throw new WriterException("Trying to write an ICI model (" + 
				modelType.toString() + ") not supported by Elvira format");
		}
		out.print("(");
		List<Variable> potentialVariables = potential.getVariables();
		Variable conditionedVariable = potentialVariables.get(0);
		int numVariables = potentialVariables.size();
		for (int i = 1; i < numVariables; i++) {
			Variable conditioningVariable = potentialVariables.get(i);
			out.print(conditionedVariable.toString() + conditioningVariable.toString() + ",");
		}
		out.println(conditionedVariable.toString() + "Residual);");
		out.println();
		out.println("henrionVSdiez = \"Diez\";");
		out.println("}");
		out.println();
		
		// Write sub-potentials
		for (int i = 1; i < numVariables; i++) {
			Variable conditioningVariable = potentialVariables.get(i);
			ArrayList<Variable> subPotentialVariables = new ArrayList<Variable>(2);
			out.print("relation ");
			subPotentialVariables.add(conditionedVariable);
			subPotentialVariables.add(conditioningVariable);
			writeVariables(out, subPotentialVariables);
			out.println(" {");
			writeSubPotentialTrash(out, subPotentialVariables);
			double[] noisyParameters = potential.getNoisyParameters(conditioningVariable);
			writeElviraTable(out, subPotentialVariables, noisyParameters);
			out.println();
		}
		// Write residual potential
		ArrayList<Variable> residualVariable = new ArrayList<Variable>(1);
		residualVariable.add(conditionedVariable);
		out.print("relation ");
		writeVariables(out, residualVariable);
		out.println(" {");
		writeSubPotentialTrash(out, residualVariable);
		double[] leakyParameters = potential.getLeakyParameters();
		writeElviraTable(out, residualVariable, leakyParameters);
		out.println();
	}
	
	/** Generate a <code>HashMap</code> with the <code>probNet</code> 
	 * additionalProperties to write in a elvira format file.
	 * @param probNet. <code>ProbNet</code> */
	private void generateElviraProperties(ProbNet probNet) {
		HashMap<Object, Object> elviraNetworkProperties = new HashMap<Object, Object>();
		elviraNetworkProperties.put("ProbNet", probNet);
		elviraNetworkProperties.put("Name", probNet.getName());
		elviraNetworkProperties.put("DefaulNodeStates", probNet.getDefaultStates());
		
		@SuppressWarnings("rawtypes")
		Class networkTypeClass = probNet.getNetworkType().getClass();
		if (networkTypeClass == BayesianNetworkType.class) {
			elviraNetworkProperties.put("BayesNet", probNet);
		} else if (networkTypeClass == InfluenceDiagramType.class) {
			elviraNetworkProperties.put("InfluenceDiagram", probNet);
		}
		

		// Nodes additionalProperties
		List<ProbNode> probNodes = probNet.getProbNodes();
		for (ProbNode probNode : probNodes) {
			// sets the known probNode additionalProperties
			Map<String, String> infoNode = probNode.additionalProperties;
			//Variable fsVariable = (Variable) probNode.getVariable();
			//String[] states = fsVariable.getStates();
			ArrayList<String> statesNames = new ArrayList<String>();
			State[] states = probNode.getVariable().getStates();
			for (int i = 0; i < states.length; i++) {
				statesNames.add(states[i].getName());
			}
			ElviraUtil.putPropertyArray(infoNode, "NodeStates", statesNames);
			NodeType nodeType = probNode.getNodeType();
			infoNode.put("NodeType", nodeType.toString());
			if (nodeType == NodeType.UTILITY) {
				infoNode.put("TypeOfVariable", VariableType.NUMERIC.toString());
			} else {
				infoNode.put("TypeOfVariable", 
						VariableType.FINITE_STATES.toString());
			}
		}
	}

	/**
	 * @param string
	 *            with an integer or something else.
	 * @return <code>true</code> if <code>string</code> contains an integer.
	 */
	private boolean isInteger(String string) {
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

	/**
	 * This method is used to translate openmarkov potentials to elvira potentials.
	 * This method modify <code>elviraPotential</code> table.
	 * 
	 * @return A <code>TablePotential</code> with the same variables but in
	 *         Elvira order: Conditioned variable the last one and the first
	 *         configuration equals to (yes, yes...yes), increasing first the
	 *         right-most variable: Conf. 0 = (yes, yes...yes) -> Conf. 1 =
	 *         (yes, yes...no), etc.
	 * @param openMarkovPotential
	 *            A <code>TablePotential</code>
	 * @throws <code>NotEnoughtMemoryException</code>
	 */
	private static TablePotential openMarkov2ElviraPotential(TablePotential openMarkovPotential)
	{
	    List<Variable> potentialVariables = openMarkovPotential.getVariables();
		int numVariables = potentialVariables.size();
		List<Variable> elviraVariables = new ArrayList<Variable>(numVariables);
		for (int i = 0; i < numVariables; i++) {
			elviraVariables.add(potentialVariables.get(numVariables - i - 1));
		}

		TablePotential elviraPotential = DiscretePotentialOperations.reorder(
				openMarkovPotential, elviraVariables);

		// Invert potential values
		double[] table = elviraPotential.values;
		double aux;
		int sizePotential = table.length, halfPotential = sizePotential / 2;
		for (int i = 0; i < halfPotential; i++) {
			aux = table[i];
			table[i] = table[sizePotential - i - 1];
			table[sizePotential - i - 1] = aux;
		}

		return elviraPotential;
	}

	/**
	 * Ignores evidence, as evidence is stores in another file in Elvira
	 */
    public void writeProbNet (String netName, ProbNet probNet, List<EvidenceCase> evidence)
        throws WriterException
    {
        writeProbNet(netName, probNet);
    }

}
