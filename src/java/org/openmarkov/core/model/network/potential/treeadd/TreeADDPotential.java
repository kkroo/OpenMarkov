/**
 * 
 */
package org.openmarkov.core.model.network.potential.treeadd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.operation.AuxiliaryOperations;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

/**
 * A TreeADDPotential is a type of Potential that implies several advantages
 * instead of using tables when the potential has a substructure that repeats
 * itself several times. Each TreeADDPotential is defined by a top variable and
 * its branches
 * 
 * @author myebra
 * 
 */
@RelationPotentialType(name = "Tree/ADD", family = "Tree")
public class TreeADDPotential extends Potential {

	/**
	 * topVariable represents the variable on the top of the tree, in other
	 * words the root variable
	 */
	private Variable topVariable;
	/**
	 * This List stores the branches created in the TreeADDPotential constructor
	 */
	private List<TreeADDBranch> branches = new ArrayList<TreeADDBranch>();

	/**
	 * label is incompatible with reference and reference is incompatible with
	 * potential This HashMap stores those potentials that have been labeled
	 * within the branches in a TreeADDPotential
	 */
	// private HashMap<String, Potential> potentialsLabeled;
	/**
	 * For role conditional
	 * 
	 * @param variables
	 * @param topVariable
	 * @param role
	 */
	public TreeADDPotential(List<Variable> variables, Variable topVariable, PotentialRole role) {
		super(variables, role);
		this.topVariable = topVariable;
		VariableType variableType = topVariable.getVariableType();
		List<Variable> potentialVariables;
		// if topVariable is finite states or discretized, it creates a branch
		// for each state
		if (variableType == VariableType.FINITE_STATES || variableType == VariableType.DISCRETIZED) {
			State[] states = topVariable.getStates();

			for (int i = states.length - 1; i >= 0; i--) {
				// if potential role of the treeADD is a conditional probability
				// it is assigned an uniform potential
				// to the conditioned variable which is always the first
				// variable of the arrayList of variables
				if (role == PotentialRole.CONDITIONAL_PROBABILITY) {
					Variable conditionedVariable = variables.get(0);
					potentialVariables = new ArrayList<Variable>();
					potentialVariables.add(conditionedVariable);
					UniformPotential potential = new UniformPotential(potentialVariables, role);
					List<State> branchStates = new ArrayList<State>();
					branchStates.add(states[i]);
					branches.add(new TreeADDBranch(branchStates, topVariable, potential, variables));
				}
			}
		}
		// if topVariable is numeric, it creates a branch whose thresholds are
		// the
		// same as those defined for the variable
		if (variableType == VariableType.NUMERIC) {
			PartitionedInterval interval = topVariable.getPartitionedInterval();
			Threshold minimum = new Threshold(interval.getMin(), !interval.isLeftClosed());
			Threshold maximum = new Threshold(interval.getMax(), interval.isRightClosed());
			potentialVariables = new ArrayList<Variable>();
			potentialVariables.add(variables.get(0));
			UniformPotential potential = new UniformPotential(potentialVariables, role);
			branches.add(new TreeADDBranch(minimum, maximum, topVariable, potential, variables));
		}
	}

	/**
	 * For role Utility
	 * 
	 * @param variables
	 * @param topVariable
	 * @param role
	 * @param utilityVariable
	 */
	public TreeADDPotential(List<Variable> variables, Variable topVariable, PotentialRole role,
			Variable utilityVariable) {
		super(variables, role, utilityVariable);
		// setUtilityVariable(utilityVariable);
		this.topVariable = topVariable;
		VariableType variableType = topVariable.getVariableType();
		List<Variable> potentialVariables;
		// if topVariable is finite states or discretized, it creates a branch
		// for each state
		if (variableType == VariableType.FINITE_STATES || variableType == VariableType.DISCRETIZED) {
			State[] states = topVariable.getStates();
			for (int i = 0; i < states.length; i++) {
				// if the role of the treeADD is utility, it assigns a uniform
				// potential
				if (role == PotentialRole.UTILITY) {
					potentialVariables = new ArrayList<Variable>();
					UniformPotential potential = new UniformPotential(potentialVariables, role,
							utilityVariable);
					// potential.setUtilityVariable(utilityVariable);
					List<State> branchStates = new ArrayList<State>();
					branchStates.add(states[i]);
					branches.add(new TreeADDBranch(branchStates, topVariable, potential, variables));
				}
			}
		}
		// if topVariable is numeric, it creates a branch whose thresholds are
		// the
		// same as those defined for the variable
		if (variableType == VariableType.NUMERIC) {
			PartitionedInterval interval = topVariable.getPartitionedInterval();
			Threshold minimum = new Threshold(interval.getMin(), !interval.isLeftClosed());
			Threshold maximum = new Threshold(interval.getMax(), interval.isRightClosed());
			potentialVariables = new ArrayList<Variable>();
			// it is an utility potential for sure so it is not necessary to add
			// variable 0 to potential variables
			UniformPotential potential = new UniformPotential(potentialVariables, role,
					utilityVariable);
			// potential.setUtilityVariable(utilityVariable);
			branches.add(new TreeADDBranch(minimum, maximum, topVariable, potential, variables));
		}
	}

	public TreeADDPotential(List<Variable> variables, PotentialRole role) {
		this(variables, (role == PotentialRole.UTILITY) ? variables.get(0) : variables.get(1), role);
	}

	public TreeADDPotential(List<Variable> variables, PotentialRole role, Variable utilityVariable) {
		this(variables, (role == PotentialRole.UTILITY) ? variables.get(0) : variables.get(1),
				role, utilityVariable);
	}

	/**
	 * Constructor for the parser
	 */
	public TreeADDPotential(List<Variable> variables, Variable topVariable, PotentialRole role,
			List<TreeADDBranch> branches) {
		super(variables, role);
		this.topVariable = topVariable;
		this.role = role;
		this.branches = branches;

		// Try to fill references in branches
		updateReferences(getLabeledBranches());
	}

	/**
	 * Copy constructor
	 * 
	 * @param treeADD
	 */
	public TreeADDPotential(TreeADDPotential treeADD) {
		super(treeADD);
		this.topVariable = treeADD.getRootVariable();
		List<TreeADDBranch> treeBranches = new ArrayList<>();
		for (int i = 0; i < treeADD.getBranches().size(); i++) {
			treeBranches.add(treeADD.getBranches().get(i).copy());
		}
		this.branches = treeBranches;
		updateReferences(getLabeledBranches());
	}

	@Override
	public PotentialType getPotentialType() {
		return PotentialType.TREE_ADD;
	}

	/**
	 * @param branch
	 */
	public void addBranch(TreeADDBranch branch) {
		branches.add(branch);
	}

	public List<TreeADDBranch> getBranches() {
		return branches;
	}

	public void setBranchAtIndex(int index, TreeADDBranch treeBranch) {
		this.branches.set(index, treeBranch);
	}

	public void setBranches(List<TreeADDBranch> branches) {
		this.branches = branches;
	}

	public Variable getRootVariable() {
		return topVariable;
	}

	public void setRootVariable(Variable variable) {
		this.topVariable = variable;
	}

	/**
	 * Adds variable to a treeADD potential
	 * 
	 */
	public Potential addVariable(Variable variable) {
		// return new UniformPotential(getVariables(), getPotentialRole());
		List<Variable> variables = getVariables();
		variables.add(variable);
		for (TreeADDBranch branch : getBranches()) {
			branch.setParentVariables(variables);
			branch.getPotential().addVariable(variable);
		}
		return this;
	}

	/**
	 * Removes variable from a treeADD potential
	 * 
	 */
	public Potential removeVariable(Variable variable) {
		List<Variable> newVariables = getVariables();
		newVariables.remove(variable);
		return new UniformPotential(newVariables, getPotentialRole());
	}

	@Override
	public List<TablePotential> tableProject(EvidenceCase evidenceCase,
			InferenceOptions inferenceOptions, List<TablePotential> projectedPotentials)
			throws NonProjectablePotentialException, WrongCriterionException {
		TablePotential projected = null;
		if (topVariable.getVariableType() != VariableType.NUMERIC) {
			Map<TreeADDBranch, TablePotential> potentialsToBlend = new HashMap<>();
			List<TreeADDBranch> branches = this.getBranches();
			for (TreeADDBranch branch : branches) {
				Potential branchPotential = branch.getPotential();
				List<TablePotential> tablePotentials = branchPotential.tableProject(evidenceCase,
						inferenceOptions, projectedPotentials);
				potentialsToBlend.put(branch, tablePotentials.get(0));
			}
			projected = blendPotentials(topVariable, potentialsToBlend, evidenceCase);
		} else {
			// if there is no evidence for the numerical topVariable it is not
			// possible to project the tree
			if (evidenceCase == null || evidenceCase.getFinding(topVariable) == null) {
				throw new NonProjectablePotentialException(
						"It is not possible to project this tree " + this.toShortString()
								+ " because top variable " + topVariable.getName()
								+ " is numeric and has no evidence");
			}
			double topVariableValue = evidenceCase.getFinding(topVariable).getNumericalValue();
			List<TreeADDBranch> numericalBranches = getBranches();
			Potential potential = null;
			for (TreeADDBranch numericalBranch : numericalBranches) {
				double minLimit = numericalBranch.getLowerBound().getLimit();
				double maxlimit = numericalBranch.getUpperBound().getLimit();
				if (minLimit <= topVariableValue && topVariableValue <= maxlimit) {
					if (minLimit == topVariableValue) {
						if (numericalBranch.getLowerBound().belongsToLeft()) {
							continue;
						} else {
							potential = numericalBranch.getPotential();
							break;
						}
					} else if (maxlimit == topVariableValue) {
						if (numericalBranch.getUpperBound().belongsToLeft()) {
							potential = numericalBranch.getPotential();
							break;
						} else {
							continue;
						}
					} else { // minLimit < topVariableValue < maxLimit
						potential = numericalBranch.getPotential();
						break;
					}
				}
			}
			// if potential still null that means finding was not within the
			// numerical variable domain so
			if (potential == null) {
				throw new NonProjectablePotentialException(
						"It is not possible to project this tree, "
								+ "top variable value was not within the topVariable domain");
			}
			projected = potential.tableProject(evidenceCase, inferenceOptions).get(0);
		}
		// Make sure variables are in the correct order after applying the mask
		// there will be variables that disappear from the potential because of
		// evidence propagation
		/*
		 * if (role == PotentialRole.CONDITIONAL_PROBABILITY || role ==
		 * PotentialRole.UTILITY) { for (int i = 0; i < correctOrder.size ();
		 * i++) { if (!projected.contains (correctOrder.get (i))) {
		 * correctOrder.remove (i); } } if (role == PotentialRole.UTILITY) {
		 * projected = DiscretePotentialOperations.reorder (projected,
		 * correctOrder); } else { projected.setVariables (correctOrder); } }
		 */
		if (role == PotentialRole.UTILITY) {
			projected.setUtilityVariable(utilityVariable);
		}
		return Arrays.asList(projected);
	}

	/*
	 * private TablePotential getPotentialMask () { }
	 */
	@Override
	public void shift(ProbNet probNet, int timeDifference) throws ProbNodeNotFoundException {
		super.shift(probNet, timeDifference);
		List<Variable> copiedTreeVariables = new ArrayList<>();

		if (getRootVariable().isTemporal()) {
			setRootVariable(probNet.getShiftedVariable(getRootVariable(), timeDifference));
		}
		for (TreeADDBranch branch : getBranches()) {
			branch.setParentVariables(copiedTreeVariables);
			branch.setRootVariable(getRootVariable());
			if (!branch.isReference()) {
				Potential originalPotential = branch.getPotential();
				originalPotential.shift(probNet, timeDifference);
				branch.setPotential(originalPotential);
			}
		}
	}

	@Override
	public Potential copy() {
		return new TreeADDPotential(this);
	}

	/**
	 * Returns if an instance of a certain Potential type makes sense given the
	 * variables and the potential role
	 * 
	 * @param variables
	 * @param role
	 */
	public static boolean validate(ProbNode probNode, List<Variable> variables, PotentialRole role) {
		boolean validate = false;
		// node must have at least one parent node
		if (role == PotentialRole.UTILITY) {
			// in variables there is not utility variable
			if (variables.size() >= 1) {
				validate = true;
			}
		}
		if (role == PotentialRole.CONDITIONAL_PROBABILITY) {
			if (variables.size() >= 2) {
				validate = true;
			}
		}
		return validate;
	}

	@Override
	public boolean isUncertain() {
		// If at least one of the leaf potentials has uncertainty then returns
		// true
		boolean hasUncertainty = false;
		for (TreeADDBranch branch : getBranches()) {
			Potential branchPotential = branch.getPotential();
			hasUncertainty = branchPotential.isUncertain();
			if (hasUncertainty == true)
				break;
		}
		return hasUncertainty;
	}

	/**
	 * Generates a sampled potential
	 */
	@Override
	public Potential sample() {
		TreeADDPotential sampledTree = (TreeADDPotential) this.copy();
		for (TreeADDBranch branch : sampledTree.getBranches()) {
			branch.setPotential(branch.getPotential().sample());
		}
		return sampledTree;
	}

	public Map<String, TreeADDBranch> getLabeledBranches() {
		Map<String, TreeADDBranch> labeledBranches = new HashMap<>();
		Stack<TreeADDPotential> subtrees = new Stack<>();
		subtrees.push(this);
		while (!subtrees.isEmpty()) {
			TreeADDPotential treeADD = subtrees.pop();
			for (TreeADDBranch branch : treeADD.getBranches()) {
				if (branch.getLabel() != null) {
					labeledBranches.put(branch.getLabel(), branch);
				}
				if (branch.getPotential() != null
						&& branch.getPotential() instanceof TreeADDPotential) {
					subtrees.push((TreeADDPotential) branch.getPotential());
				}
			}
		}
		return labeledBranches;
	}

	/**
	 * 
	 * @param labeledBranches
	 */
	public void updateReferences(Map<String, TreeADDBranch> labeledBranches) {
		Stack<TreeADDPotential> subtrees = new Stack<>();
		if (!labeledBranches.isEmpty()) {
			subtrees.push(this);
			while (!subtrees.isEmpty()) {
				TreeADDPotential treeADD = subtrees.pop();
				for (TreeADDBranch branch : treeADD.getBranches()) {
					if (branch.getReference() != null
							&& labeledBranches.containsKey(branch.getReference())) {
						branch.setReferencedBranch(labeledBranches.get(branch.getReference()));
					}
					if (branch.getPotential() instanceof TreeADDPotential) {
						subtrees.push((TreeADDPotential) branch.getPotential());
					}
				}
			}
		}
	}

	@Override
	public void setUtilityVariable(Variable utilityVariable) {
		super.setUtilityVariable(utilityVariable);
		for (TreeADDBranch branch : branches) {
			if (branch.getPotential() != null && branch.getPotential().getUtilityVariable() == null) {
				branch.getPotential().setUtilityVariable(utilityVariable);
			}
		}
	}

	@Override
	public Collection<Finding> getInducedFindings(EvidenceCase evidenceCase, double cycleLength)
			throws IncompatibleEvidenceException, WrongCriterionException {
		List<Finding> newFindings = new ArrayList<>();
		for (TreeADDBranch branch : branches) {
			if (evidenceCase.contains(topVariable)) {
				boolean isInduced = false;
				Finding finding = evidenceCase.getFinding(topVariable);
				if (topVariable.getVariableType() == VariableType.NUMERIC) {
					isInduced = branch.isInsideInterval(finding.getNumericalValue());
				} else {
					int i = 0;
					List<State> branchStates = branch.getBranchStates();
					while (i < branchStates.size() && !isInduced) {
						isInduced = finding.getState().equals(branchStates.get(i++).getName());
					}
				}
				if (isInduced) {
					newFindings.addAll(branch.getPotential().getInducedFindings(evidenceCase,
							cycleLength));
				}
			}
		}
		return newFindings;
	}

	private TablePotential blendPotentials(Variable topVariable,
			Map<TreeADDBranch, TablePotential> branchPotentials, EvidenceCase evidence) {
		List<TablePotential> potentials = new ArrayList<>();
		// branchStateIndex contains in it's i-th position the index of the
		// potential in potentials that is relevant for topVariable's i-th state
		int[] branchStateIndex = new int[topVariable.getNumStates()];
		for (TreeADDBranch branch : branchPotentials.keySet()) {
			potentials.add(branchPotentials.get(branch));
			for (State branchState : branch.getBranchStates()) {
				branchStateIndex[topVariable.getStateIndex(branchState)] = potentials.size() - 1;
			}
		}

		int numPotentials = potentials.size();

		// Gets the union
		List<Variable> resultVariables = AuxiliaryOperations.getUnionVariables(potentials);
		
		// Add top variable to resulting potential's variable list
		int topVariableIndex = resultVariables.indexOf(topVariable);
		int topVariableEvidenceStateIndex = -1;
		if(evidence == null || !evidence.contains(topVariable))
		{
			if(topVariableIndex == -1)
			{
				topVariableIndex = resultVariables.indexOf(getConditionedVariable())+1;
				resultVariables.add(topVariableIndex, topVariable);
			}
		}else
		{
			topVariableEvidenceStateIndex = evidence.getFinding(topVariable).getStateIndex();
		}
		
		TablePotential resultPotential = new TablePotential(resultVariables, potentials.get(0).getPotentialRole());
		// Number of variables
		int numVariables = resultVariables.size();

		// Gets the tables of each TablePotential
		double[][] tables = new double[numPotentials][];
		for (int i = 0; i < numPotentials; i++) {
			tables[i] = potentials.get(i).values;
		}
		
		// Gets the uncertain tables of each TablePotential
		boolean containsUncertainty = false;
		UncertainValue[][] uncertaintyTables = new UncertainValue[numPotentials][];
		for (int i = 0; i < numPotentials; i++) {
			uncertaintyTables[i] = potentials.get(i).uncertainValues;
			containsUncertainty |= uncertaintyTables[i] !=null;
		}
		if(containsUncertainty)
		{
			resultPotential.uncertainValues = new UncertainValue[resultPotential.getTableSize()];
		}

		// Gets dimensions
		int[] resultDimensions = resultPotential.getDimensions();

		// Gets accumulated offsets
		int[][] accumulatedOffsets = DiscretePotentialOperations.getAccumulatedOffsets(potentials,
				resultVariables);

		// Gets coordinate
		int[] resultCoordinates;
		if (numVariables != 0) {
			resultCoordinates = new int[numVariables];
		} else {
			resultCoordinates = new int[1];
			resultCoordinates[0] = 0;
		}

		// Position in each table potential
		int[] potentialPositions = new int[numPotentials];
		for (int i = 0; i < numPotentials; i++) {
			potentialPositions[i] = 0;
		}

		int incrementedVariable = 0;
		int tableSize = resultPotential.getTableSize();
		double[] resultValues = resultPotential.values;
		UncertainValue[] uncertainValues = resultPotential.uncertainValues;
		int topVariableStateIndex = (topVariableEvidenceStateIndex != -1)? topVariableEvidenceStateIndex : resultCoordinates[topVariableIndex];
		int potentialIndex = branchStateIndex[topVariableStateIndex];

		if (potentials.size() > 0) {
			for (int resultPosition = 0; resultPosition < tableSize; resultPosition++) {
				/*
				 * increment the result coordinate and find out which variable
				 * is to be incremented
				 */
				for (int iVariable = 0; iVariable < resultCoordinates.length; iVariable++) {
					// try by incrementing the current variable (given by
					// iVariable)
					resultCoordinates[iVariable]++;
					if (resultDimensions == null ||
							resultCoordinates[iVariable] != resultDimensions[iVariable]) {
						// we have incremented the right variable
						incrementedVariable = iVariable;
						// do not increment other variables;
						break;
					}
					/*
					 * this variable could not be incremented; we set it to 0 in
					 * resultCoordinate (the next iteration of the for-loop will
					 * increment the next variable)
					 */
					resultCoordinates[iVariable] = 0;
				}

				// Find out which is the relevant potential for this state of the root variable
				
				// Copy the value of the relevant potential onto the result potential
				resultValues[resultPosition] =  tables[potentialIndex][potentialPositions[potentialIndex]];
				if(uncertaintyTables[potentialIndex]!=null)
				{
					uncertainValues[resultPosition] = uncertaintyTables[potentialIndex][potentialPositions[potentialIndex]];
				}
				
				for (int iPotential = 0; iPotential < numPotentials; iPotential++) {
					// update the current position in each potential table
					if(accumulatedOffsets[iPotential].length>0)
					{
						potentialPositions[iPotential] += accumulatedOffsets[iPotential][incrementedVariable];
					}
				}
				topVariableStateIndex = (topVariableEvidenceStateIndex != -1)? topVariableEvidenceStateIndex : resultCoordinates[topVariableIndex];
				potentialIndex = branchStateIndex[topVariableStateIndex];
				
			}
		}
		return resultPotential;
	}

	@Override
	public void replaceNumericVariable(Variable convertedParentVariable) {
		super.replaceNumericVariable(convertedParentVariable);
		
		if(topVariable.getName().equals(convertedParentVariable.getName()))
		{
			State[] states = convertedParentVariable.getStates();
			double[] stateValues = new double[states.length];
			for(int i=0; i< states.length; ++i)
			{
				stateValues[i] = Double.parseDouble(states[i].getName());
			}			
			for(TreeADDBranch branch: branches)
			{
				List<State> branchStates = new ArrayList<>();
				for(int i=0; i< stateValues.length; ++i)
				{
					if(branch.isInsideInterval(stateValues[i]))
					{
						branchStates.add(states[i]);
					}
				}
				branch.setStates(branchStates);
				branch.setRootVariable(convertedParentVariable);
			}
			topVariable = convertedParentVariable;
		}		
		for(TreeADDBranch branch: branches)
		{
			branch.getPotential().replaceNumericVariable(convertedParentVariable);
		}
	}
	
	

}
