/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.variableElimination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.inference.BasicOperations;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.inference.annotation.InferenceAnnotation;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.MarkovDecisionNetwork;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNetOperations;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.OnlyChanceNodes;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.MPADType;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.inference.heuristic.simpleElimination.SimpleElimination;
import org.openmarkov.inference.variableElimination.action.CRemoveChanceNodeVEEdit;
import org.openmarkov.inference.variableElimination.action.CRemoveDecisionNodeVEEdit;
import org.openmarkov.inference.variableElimination.action.CRemoveNodeVEEdit;

/**
 * Variable elimination algorithm for Bayesian networks and influence diagrams.
 * Propagation in influence diagrams allows two types of evidences:
 * pre-resolution (Ezawa's), and post-resolution (Luque and Díez').
 * 
 * @author mluque
 * @author fjdiez
 * @author marias
 */
@InferenceAnnotation(name = "VariableElimination")
public class VariableElimination extends InferenceAlgorithm {

	/**
	 * Indicates the state of the InferenceAlgorithm object. PRERESOLUTION
	 * indicates that the network has been edited and is prepared for any query.
	 * POSTRESOLUTION indicates that the policy of every decision in network has
	 * been calculated, and therefore queries about posterior probabilities and
	 * utilities are accepted.
	 * 
	 */
	public enum InferenceState {
		PRERESOLUTION, POSTRESOLUTION
	}

	private InferenceState inferenceState;

	/**
	 * This type is used when invoking method 'performInference'. Purpose of the
	 * inference: STRATEGY_AND_MEU indicates the inference performed is aimed to
	 * calculate the strategy and the global utility (maximum expected utility)
	 * POSTERIOR_PROB indicates that the inference's objective is calculating
	 * some posterior probability POSTERIOR_UTIL indicates that the inference's
	 * objective is calculating some posterior utility EXPECTED_UTIL indicates
	 * the inference performed is aimed to calculate the expected utilities of a
	 * policy
	 */
	public enum InferencePurpose {
		STRATEGY_AND_MEU, POSTERIOR_PROB, POSTERIOR_UTIL, EXPECTED_UTIL
	}

	// ** OUTPUTS OF THE RESOLUTION PHASE **//
	private HashMap<Variable, Potential> strategy;
	private TablePotential globalUtility;
	private List<TablePotential> utilityPotentials;

	/** Set of network types where the algorithm can be applied. */
	private List<NetworkType> networkTypesApplicable;

	/**
	 * Set of additional constraints that the ProbNet must satisfy in
	 * conjunction with the constraints typical of the network types. Its
	 * initial value must be null, instead of an empty ArrayList<PNCconstraint>,
	 * so that the method getRequiredConstraints of its child classes detect
	 * when this property has not been initialized
	 */
	private List<PNConstraint> additionalConstraints;

	/**
	 * @return A new <code>ArrayList</code> of <code>PNConstraint</code>.
	 */
	protected static List<PNConstraint> initializeAdditionalConstraints() {
		List<PNConstraint> constraints = new ArrayList<PNConstraint>();
		//constraints.add(new NoMixedParents());
		return constraints;
	}

	/**
	 * @return An <code>ArrayList</code> of <code>NetworkType</code> where the
	 *         algorithm can be applied: Bayesian networks and influence
	 *         diagrams.
	 */
	protected static List<NetworkType> initializeNetworkTypesApplicable() {
		return Arrays.asList(BayesianNetworkType.getUniqueInstance(),
				InfluenceDiagramType.getUniqueInstance(), MPADType.getUniqueInstance());
	}

	/**
	 * @return An <code>ArrayList</code> of <code>NetworkType</code> where the
	 *         algorithm can be applied: Bayesian networks and influence
	 *         diagrams.
	 */
	protected final List<NetworkType> getNetworkTypesApplicable() {

		if (networkTypesApplicable == null) {
			networkTypesApplicable = initializeNetworkTypesApplicable();
		}

		return networkTypesApplicable;
	}

	/**
	 * @return The additional constraints. If it is null then it is initialized.
	 */
	protected final Collection<PNConstraint> getAdditionalConstraints() {

		if (additionalConstraints == null) {
			additionalConstraints = initializeAdditionalConstraints();
		}

		return additionalConstraints;
	}

	@Override
	public boolean isEvaluable(ProbNet probNet) {
		boolean isApplicable;

		List<NetworkType> networkTypes = initializeNetworkTypesApplicable();

		isApplicable = false;
		// Check that there is a network type applicable equal to type of
		// probNet
		for (int i = 0; (i < networkTypes.size()) && !isApplicable; i++) {
			NetworkType auxType = networkTypes.get(i);
			isApplicable = (auxType == probNet.getNetworkType());
		}

		// Check that the probNet satisfies the specific constraints of the
		// algorithm
		if (isApplicable) {
			List<PNConstraint> additionalConstraints;
			additionalConstraints = initializeAdditionalConstraints();
			for (int i = 0; (i < additionalConstraints.size()) && isApplicable; i++) {
				isApplicable = additionalConstraints.get(i).checkProbNet(probNet);
			}
		}

		return isApplicable;
	}

	/**
	 * @param globalUtility
	 *            Sets the field 'globalUtility'
	 */
	private void setGlobalUtility(TablePotential globalUtility) {
		this.globalUtility = globalUtility;
	}

	/**
	 * @param probNet
	 * @throws NotEvaluableNetworkException
	 *             Constructor
	 */
	public VariableElimination(ProbNet probNet) throws NotEvaluableNetworkException {
		super(probNet);
		this.probNet = probNet.copy();
		this.pNESupport = this.probNet.getPNESupport();
		initializeEvidences();
		setInferenceStateAndInitializeStructures(InferenceState.PRERESOLUTION);
		setConditioningVariables(new ArrayList<Variable>());

	}

	/**
	 * Initializes pre and post resolution evidences.
	 */
	private void initializeEvidences() {
		setPreResolutionEvidence(new EvidenceCase());
		setPostResolutionEvidence(new EvidenceCase());
	}

	/**
	 * @param state
	 *            Sets the inference state and initialize the structures if the
	 *            state is 'PRERESOLUTION'
	 */
	private void setInferenceStateAndInitializeStructures(InferenceState state) {

		inferenceState = state;
		switch (state) {
		case PRERESOLUTION:
			strategy = new HashMap<Variable, Potential>();
			globalUtility = null;
			break;
		default:
			break;
		}
	}

	@Override
	public Potential getOptimizedPolicy(Variable decisionVariable)
			throws IncompatibleEvidenceException, UnexpectedInferenceException {
		resolveNetworkIfPreResolutionState(InferencePurpose.STRATEGY_AND_MEU);
		return strategy.get(decisionVariable);
	}

	/**
	 * @param network
	 * @param utilityVariables
	 * @return An auxiliary Cooper policy network for computing the posterior
	 *         probabilities and utilities. If purpose!=POSTERIOR_UTIL and
	 *         'utilityVariables' contains a variable then its utility function
	 *         is kept in the diagram. If purpose!=POSTERIOR_UTIL and
	 *         'utilityVariables'==null then utility potentials are removed. If
	 *         purpose==POSTERIOR_UTIL then a sum of ordinary utility nodes are
	 *         kept.
	 * 
	 */
	private ProbNet constructAuxiliaryNetwork(ProbNet network, InferencePurpose purpose,
			List<Variable> infPred, List<Variable> utilityVariables) {
		ProbNet auxNet = null;
		boolean includeUtilities;
		Variable utilityVariable = null;

		if (hasOnlyChanceNodes(network)) {
			// In Bayesian networks we do nothing: use the original network
			auxNet = network;
		} else {
			if ((inferenceState == InferenceState.PRERESOLUTION)
					|| (purpose == InferencePurpose.EXPECTED_UTIL)) {
				includeUtilities = true;
			} else {// POSTRESOLUTION
				includeUtilities = utilityVariables != null  && !utilityVariables.isEmpty();
				if (includeUtilities && utilityVariables.size() == 1) {
					utilityVariable = utilityVariables.get(0);
				}
			}
			if (includeUtilities) {
				auxNet = BasicOperations.removeSuperValueNodes(network, null, false, true,
						utilityVariable);
			} else {
				auxNet = BasicOperations.removeUtilityNodes(network);
			}
			replaceDecisionsByChanceNodesWithPolicies(auxNet, infPred);
		}
		return auxNet;

	}

	/**
	 * @param auxNet
	 *            Replaces decision nodes in 'auxNet' by chance nodes by using
	 *            the corresponding policies. In PRERESOLUTION phase only
	 *            imposed policies are used. In POSTRESOLUTION phase both
	 *            imposed and calculated policies are used. Decision nodes in
	 *            'infPred' are not changed.
	 * @param infPred
	 */
	private void replaceDecisionsByChanceNodesWithPolicies(ProbNet auxNet, List<Variable> infPred) {
		List<Node> childrenOfDecision;
		// Change decision nodes by chance nodes whose probability potential
		// is given by the corresponding policy
		List<ProbNode> decisions = auxNet.getProbNodes(NodeType.DECISION);
		for (ProbNode decision : decisions) {
			Variable varDecision = decision.getVariable();

			if ((infPred == null) || (!infPred.contains(varDecision))) {

				Potential policy = (inferenceState == InferenceState.PRERESOLUTION) ? getImposedPolicy(varDecision)
						: getCurrentPolicy(varDecision);
				if (policy != null) {
					childrenOfDecision = auxNet.getProbNode(varDecision).getNode().getChildren();
					// Remove decision
					auxNet.removeProbNode(decision);
					// Create a chance node for the same variable
					auxNet.addProbNode(varDecision, NodeType.CHANCE);

					// Add the links to the children (chance) of decision node
					List<ProbNode> probNodesChildrenOfDecision = ProbNet
							.getProbNodesOfNodes(childrenOfDecision);
					for (ProbNode child : probNodesChildrenOfDecision) {
						NodeType type = child.getNodeType();
						// if (type == NodeType.CHANCE) {
						if ((type == NodeType.CHANCE) || (type == NodeType.UTILITY)) {
							try {
								auxNet.addLink(varDecision, child.getVariable(), true);
							} catch (NodeNotFoundException e) {
								e.printStackTrace();
							}
						}
					}

					// Incoming Links for the variable
					List<Variable> domainPolicy = policy.getVariables();
					domainPolicy.remove(varDecision);
					for (Variable varInDomain : domainPolicy) {
						try {
							auxNet.addLink(varInDomain, varDecision, true);
						} catch (NodeNotFoundException e) {
							e.printStackTrace();
						}
					}

					ProbNode probNodeDecision = auxNet.getProbNode(varDecision);
					List<Potential> potentials = probNodeDecision.getPotentials();
					if (potentials != null) {
						for (Potential auxPot : potentials) {
							probNodeDecision.removePotential(auxPot);
						}
					}

					// Potential probability for the variable
					auxNet.addPotential(policy);
				}
			}
		}
	}

	/**
	 * @param decision
	 * @return the policy existing for the decision. It can be (1) an imposed
	 *         policy, or (2) a policy calculated when the network was
	 *         evaluated.
	 */
	private Potential getCurrentPolicy(Variable decision) {
		Potential policy = getImposedPolicy(decision);
		if (policy == null) {
			policy = strategy.get(decision);
		}
		return policy;
	}

	/**
	 * @param network
	 * @return True if the network has only chance nodes.
	 */
	private static boolean hasOnlyChanceNodes(ProbNet network) {
		return network.hasConstraint(OnlyChanceNodes.class);
	}

	/**
	 * Creates an heuristic associated to <code>network</code>
	 * 
	 * @param markovNetworkInference
	 *            <code>MarkovDecisionNetwork</code>
	 * @param queryVariables
	 * @param conditioningVariables
	 * @return <code>EliminationHeuristic</code>
	 */
	private EliminationHeuristic factoryHeuristic(ProbNet markovNetworkInference,
			List<Variable> queryVariables, List<Variable> evidenceVariables,
			List<Variable> conditioningVariables, PartialOrder partialOrder) {
		List<List<Variable>> projectedOrderVariables = partialOrder.projectPartialOrder(
				queryVariables, evidenceVariables, conditioningVariables);

		EliminationHeuristic heuristic = new SimpleElimination(markovNetworkInference,
				projectedOrderVariables);
		return heuristic;
	}

	/**
	 * @throws IncompatibleEvidenceException
	 *             Resolves the network: Computes the optimized policies and the
	 *             global utility. The new state is POSTRESOLUTION.
	 * @throws UnexpectedInferenceException
	 */
	private void resolveNetwork() throws WrongCriterionException, IncompatibleEvidenceException,
			UnexpectedInferenceException {

		resolveNetwork(InferencePurpose.STRATEGY_AND_MEU);

	}

	/**
	 * @throws IncompatibleEvidenceException
	 *             Resolves the network: Computes the optimized policies and the
	 *             global utility. The new state is POSTRESOLUTION.
	 * @throws UnexpectedInferenceException
	 */
	private void resolveNetwork(InferencePurpose purpose) throws WrongCriterionException,
			IncompatibleEvidenceException, UnexpectedInferenceException {

		ProbNet reducedProbNet = null;
		reducedProbNet = constructAuxiliaryNetwork(probNet, null, null, null);
		TablePotential auxGlobalUtility = performInference(reducedProbNet, purpose,
				new ArrayList<Variable>(), new EvidenceCase(getPreResolutionEvidence()));
		if (purpose == InferencePurpose.STRATEGY_AND_MEU) {
			globalUtility = auxGlobalUtility;
		}

		inferenceState = InferenceState.POSTRESOLUTION;

	}

	/**
	 * This method has been designed to be used in both states: PRERESOLUTION
	 * and POSTRESOLUTION. It implements the basic variable elimination scheme.
	 * In PRESOLUTION state it computes the optimized policies and the global
	 * utility. In POSTRESOLUTION state it calculates the posterior
	 * probabilities and utilities.
	 * 
	 * @param network
	 * @throws WrongCriterionException
	 */
	@SuppressWarnings("unchecked")
	private TablePotential performInference(ProbNet network, InferencePurpose purpose,
			List<Variable> queryVariables, EvidenceCase evidence)
			throws UnexpectedInferenceException, IncompatibleEvidenceException {
		ProbNet prunedProbNet = null;
		List<TablePotential> projectedTablePotentials;
		TablePotential posteriorProbOrUtil;
		TablePotential utilityFromElimination = null;
		EliminationHeuristic heuristic;
		boolean isLastVariable;
		List<TablePotential> constantPotentials;
		List<Variable> queryVariablesForPrune;
		List<Variable> informationalPredecessors = null;

		if (purpose == InferencePurpose.EXPECTED_UTIL) {
			informationalPredecessors = getInformationalPredecessors(network, queryVariables.get(0));
		}

		if (inferenceState == InferenceState.PRERESOLUTION) {
			prunedProbNet = network;
		} else {// POSTRESOLUTION:
			List<Variable> utilityVariables = (purpose == InferencePurpose.POSTERIOR_UTIL) ? queryVariables : null;

			network = constructAuxiliaryNetwork(network, purpose, informationalPredecessors, utilityVariables);
			// TODO Study how to prune the influence diagram when we want to
			// calculate a posterior utility of a utility node.

			queryVariablesForPrune = (purpose != InferencePurpose.EXPECTED_UTIL) ? queryVariables
					: null;

			prunedProbNet = (purpose == InferencePurpose.POSTERIOR_PROB) ? ProbNetOperations
					.getPruned(network, queryVariablesForPrune, evidence) : network;
		}

		removePotentialsWithPrunedVariables(prunedProbNet);

		// ERROR HERE with potentials of prunedProbNet

		// builds a MarkovNet with the TablePotentials obtained when projecting
		// the potentials of the ProbNet according to the evidence
		// potentials have been projected while pruning
		posteriorProbOrUtil = null;
		if (purpose == InferencePurpose.POSTERIOR_PROB) {
			removeUniformPotentials(prunedProbNet);
		}
		try {
			projectedTablePotentials = prunedProbNet.tableProjectPotentials(evidence);
		} catch (NonProjectablePotentialException | WrongCriterionException e1) {
			throw new IncompatibleEvidenceException("Unexpected inference exception :"
					+ e1.getMessage());
		}

		constantPotentials = removeConstantPotentials(projectedTablePotentials, purpose);
		ProbNet markovNetwork = new MarkovDecisionNetwork(network, projectedTablePotentials);
		List<Variable> variablesToEliminate = prunedProbNet.getChanceAndDecisionVariables();
		if (purpose == InferencePurpose.EXPECTED_UTIL) {
			Variable decisionVariable = queryVariables.get(0);
			variablesToEliminate.removeAll(informationalPredecessors);
			variablesToEliminate.remove(decisionVariable);
		} else {
			variablesToEliminate.removeAll(queryVariables);
		}
		variablesToEliminate.removeAll(evidence.getVariables());
		List<Variable> conditioningVariables = getConditioningVariables();
		variablesToEliminate.removeAll(conditioningVariables);
		PartialOrder partialOrder = null;
		try {
			partialOrder = new PartialOrder(prunedProbNet);
		} catch (WrongGraphStructureException e) {
			e.printStackTrace();
		}
		((MarkovDecisionNetwork) markovNetwork).setPartialOrder(partialOrder);
		heuristic = factoryHeuristic(markovNetwork, queryVariables,
				evidence.getVariables(), conditioningVariables, partialOrder);

		// register the created heuristic as listener
		markovNetwork.getPNESupport().addUndoableEditListener(heuristic);
		Variable variableToDelete = heuristic.getVariableToDelete();
		List<Variable> chanceAndDecisionVariables = markovNetwork
				.getChanceAndDecisionVariables();
		int numVariablesToEliminate = chanceAndDecisionVariables.size();
		if (purpose == InferencePurpose.POSTERIOR_PROB) {
			numVariablesToEliminate = numVariablesToEliminate - queryVariables.size();
		} else if (purpose == InferencePurpose.EXPECTED_UTIL) {
			numVariablesToEliminate = variablesToEliminate.size();
		}
		
		if (purpose == InferencePurpose.STRATEGY_AND_MEU) {
			strategy = new HashMap<Variable, Potential>();
		}

		// Eliminate variables one by one
		int numVariablesEliminated = 0;
		while ((variableToDelete != null) && (numVariablesEliminated < numVariablesToEliminate)) {
			isLastVariable = (numVariablesEliminated + 1 == numVariablesToEliminate);
			utilityFromElimination = eliminateVariable(variableToDelete, purpose, isLastVariable,
					markovNetwork, constantPotentials);
			++numVariablesEliminated;
			variableToDelete = heuristic.getVariableToDelete();
		}

		// removes the heuristic from pNESupport
		markovNetwork.getPNESupport().removeUndoableEditListener(heuristic);

		if ((inferenceState == InferenceState.POSTRESOLUTION)
				&& (purpose != InferencePurpose.EXPECTED_UTIL)) {
			List<? extends Potential> remainingPotentials = markovNetwork.getPotentials();
			List<Variable> emptyList = new ArrayList<Variable>();
			TablePotential multipliedPotential = (TablePotential) DiscretePotentialOperations
					.multiplyAndEliminate((List<TablePotential>) remainingPotentials, emptyList);

			if (purpose == InferencePurpose.POSTERIOR_PROB) {
				try {
					multipliedPotential.setPotentialRole(PotentialRole.JOINT_PROBABILITY);
					posteriorProbOrUtil = DiscretePotentialOperations.normalize(multipliedPotential);
				} catch (NormalizeNullVectorException e) {
					throw new IncompatibleEvidenceException("Incompatible Evidence");
				}
			}
		}

		switch (purpose) {
		case POSTERIOR_UTIL:
		case STRATEGY_AND_MEU:
		case EXPECTED_UTIL:
			utilityPotentials = new ArrayList<>();
			for (Potential pot : markovNetwork.getPotentialsByRole(PotentialRole.UTILITY)) {
				utilityPotentials.add((TablePotential) pot);
			}

			if (utilityFromElimination != null) {
				utilityPotentials.add(utilityFromElimination);
			}
			utilityPotentials.addAll(getUtilityPotentials(constantPotentials));
			posteriorProbOrUtil = DiscretePotentialOperations.sum(utilityPotentials);
			if (purpose == InferencePurpose.EXPECTED_UTIL) {
				posteriorProbOrUtil = convertToTablePotentialDecisionFirst(posteriorProbOrUtil,
						queryVariables.get(0));
			}
			if (purpose == InferencePurpose.STRATEGY_AND_MEU) {
				completeStrategyWithUniformPoliciesIfNecessary();
			}
			break;
		default:
			break;
		}

		return posteriorProbOrUtil;
	}

	/**
	 * @param posteriorProbOrUtil
	 * @param variable
	 * @return A new potential equivalent to the input potential, where the
	 *         'decision' appears in the first position
	 */
	private TablePotential convertToTablePotentialDecisionFirst(TablePotential potential,
			Variable decision) {
		TablePotential newPotential = potential;

		List<Variable> variables = potential.getVariables();
		if (variables.indexOf(decision) != 0) {
			List<Variable> reorderedVariables = new ArrayList<>(variables);
			Collections.swap(reorderedVariables, 0, variables.indexOf(decision));
			newPotential = DiscretePotentialOperations.reorder(potential, reorderedVariables);
		}
		return newPotential;
	}

	private List<Variable> getInformationalPredecessors(ProbNet network, Variable variable) {
		List<Variable> informationalPredecessors = new ArrayList<>();
		ProbNode decisionNode = network.getProbNode(variable);
		
		List<ProbNode> predecessorDecisions = new ArrayList<>();
		for (ProbNode candidateDecisionNode : network.getProbNodes(NodeType.DECISION)) {
			if (network.existsPath(candidateDecisionNode, decisionNode, true)) {
				predecessorDecisions.add(candidateDecisionNode);
			}
		}
		informationalPredecessors.addAll(ProbNet.getVariables(predecessorDecisions));
		
		for (ProbNode candidateNode : network.getProbNodes(NodeType.CHANCE)) {
			boolean isInformationalPredecessor = decisionNode.isParent(candidateNode);
			int i = 0;
			while(i < predecessorDecisions.size() && !isInformationalPredecessor) {
				isInformationalPredecessor = predecessorDecisions.get(i).isParent(candidateNode);
				++i;
			}
			if (isInformationalPredecessor) {
				informationalPredecessors.add(candidateNode.getVariable());
			}
		}
		return informationalPredecessors;
	}

	/**
	 * @param variableToDelete
	 * @param markovNetworkInference
	 */
	@SuppressWarnings("unused")
	private boolean stopVariableElimination(Variable variableToDelete,
			List<Variable> queryVariables, InferencePurpose purpose) {
		// return ((variableToDelete== null)||
		// ((queryVariables!=null)&&(queryVariables.size()>0)&&(queryVariables.get(0)==variableToDelete)&&(purpose==InferencePurpose.EXPECTED_UTIL)));
		return (variableToDelete == null);
	}

	/**
	 * Every decision not belonging to conditioning variables and with null
	 * policy needs a uniform policy to be assigned.
	 */
	private void completeStrategyWithUniformPoliciesIfNecessary() {

		List<Variable> decisionVariables = probNet.getVariables(NodeType.DECISION);
		List<Variable> conditioningVariables = this.getConditioningVariables();
		for (Variable decisionVariable : decisionVariables) {
			if ((getCurrentPolicy(decisionVariable) == null) && (!conditioningVariables.contains(decisionVariable))) {
				List<Variable> variables = new ArrayList<Variable>();
				variables.add(decisionVariable);
				List<Node> parents = probNet.getProbNode(decisionVariable).getNode().getParents(); 
				for (Node parentNode : parents) {
					try {
						variables.add(probNet.getProbNode(parentNode).getVariable());
					} catch (ProbNodeNotFoundException e) {
						e.printStackTrace();
					}
				}
				strategy.put(decisionVariable, new TablePotential(variables,
						PotentialRole.CONDITIONAL_PROBABILITY));
			}
		}
	}

	/**
	 * @param prunedProbNet
	 *            Eliminates Uniform potentials, because they are unnecessary
	 *            for the inference
	 */
	private void removeUniformPotentials(ProbNet prunedProbNet) {
		for (ProbNode node : prunedProbNet.getProbNodes()) {
			for (Potential potential : node.getPotentials()) {
				if (potential.getPotentialType() == PotentialType.UNIFORM
						&& potential.getPotentialRole() != PotentialRole.UTILITY) {
					node.removePotential(potential);
				}
			}
		}
	}

	/**
	 * @param potentials
	 * @return The list of utility potentials from a list of potentials
	 */
	private List<TablePotential> getUtilityPotentials(List<TablePotential> potentials) {
		List<TablePotential> utilityPotentials = new ArrayList<TablePotential>();
		for (TablePotential potential : potentials) {
			if (potential.getPotentialRole() == PotentialRole.UTILITY) {
				utilityPotentials.add(potential);
			}
		}
		return utilityPotentials;
	}

	/**
	 * Removes those potentials that contain a variable that does not appear in the
	 * network after being pruned.
	 * 
	 * @param prunedProbNet
	 */
	private void removePotentialsWithPrunedVariables(ProbNet prunedProbNet) {

		List<Potential> potentials = prunedProbNet.getPotentials();

		for (Potential potential : potentials) {
			List<Variable> potentialVariables = potential.getVariables();
			boolean removePotential = false;
			for (int j = 0; j < potentialVariables.size() && !removePotential; j++) {
				removePotential = !prunedProbNet.containsVariable(potentialVariables.get(j));
			}
			if (removePotential) {
				prunedProbNet.removePotential(potential);
			}
		}

	}

	private TablePotential eliminateVariable(Variable variableToDelete, InferencePurpose purpose,
			boolean isLastVariable, ProbNet markovNetworkInference,
			List<TablePotential> constantPotentials) throws IncompatibleEvidenceException {

		CRemoveNodeVEEdit edit = null;
		NodeType nodeType = markovNetworkInference.getProbNode(variableToDelete).getNodeType();

		switch (nodeType) {
		case CHANCE:
			edit = new CRemoveChanceNodeVEEdit(markovNetworkInference, constantPotentials, purpose,
					variableToDelete, this, isLastVariable, inferenceState);
			break;
		case DECISION:
			edit = new CRemoveDecisionNodeVEEdit(markovNetworkInference, constantPotentials,
					purpose, variableToDelete, this, isLastVariable, inferenceState);
			break;
		default:
			break;
		}

		// pNESupport.announceEdit (edit);

		try {
			markovNetworkInference.getPNESupport().doEdit(edit);
		} catch (DoEditException | NonProjectablePotentialException | WrongCriterionException e) {
			e.printStackTrace();
		}

		if (nodeType == NodeType.DECISION) {
			TablePotential policy = edit.getPolicy();
			if (policy != null) {
				strategy.put(variableToDelete, policy);
			}
		}
		TablePotential posteriorUtil = null;
		if (purpose != InferencePurpose.EXPECTED_UTIL) {
			storeGlobalUtilityIfNecessary(isLastVariable, edit);
			posteriorUtil = getPosteriorUtilityIfNecessary(isLastVariable, edit, purpose);
		}

		return posteriorUtil;

	}

	private TablePotential getPosteriorUtilityIfNecessary(boolean isLastVariable,
			CRemoveNodeVEEdit edit, InferencePurpose purpose) {
		TablePotential posteriorUtility = null;
		if (isLastVariable && purpose == InferencePurpose.POSTERIOR_UTIL) {
			posteriorUtility = edit.getPosteriorUtility();
		}
		return posteriorUtility;

	}

	private void storeGlobalUtilityIfNecessary(boolean isLastVariable, CRemoveNodeVEEdit edit) {
		if (isLastVariable) {
			setGlobalUtility(edit.getGlobalUtility());
		}
	}

	@Override
	public TablePotential getGlobalUtility() throws IncompatibleEvidenceException,
			UnexpectedInferenceException {

		resolveNetworkIfPreResolutionState(InferencePurpose.STRATEGY_AND_MEU);

		return globalUtility;
	}

	/**
	 * Resolves the network if the inference algorithm is in the PRERESOLUTION
	 * state
	 * 
	 * @throws IncompatibleEvidenceException
	 * @throws UnexpectedInferenceException
	 */
	private void resolveNetworkIfPreResolutionState(InferencePurpose purpose)
			throws IncompatibleEvidenceException, UnexpectedInferenceException {
		if (inferenceState == InferenceState.PRERESOLUTION) {
			try {
				resolveNetwork(purpose);
			} catch (WrongCriterionException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public HashMap<Variable, TablePotential> getProbsAndUtilities()
			throws IncompatibleEvidenceException, UnexpectedInferenceException {

		List<Variable> variablesOfInterest = probNet.getVariables();
		return getProbsAndUtilitiesAfterFirstPrune(probNet, variablesOfInterest);
	}

	@Override
	public HashMap<Variable, TablePotential> getProbsAndUtilities(List<Variable> variablesOfInterest)
			throws IncompatibleEvidenceException, UnexpectedInferenceException {
		ProbNet prunedNet;

		List<Variable> unobservedVariablesOfInterest = new ArrayList<Variable>(variablesOfInterest);
		unobservedVariablesOfInterest.removeAll(getPostResolutionEvidence().getVariables());
		// Prune the nodes that are barren for all the queries
		if (hasOnlyChanceNodes(probNet)) {
			EvidenceCase evidence = joinPreAndPostResolutionEvidence();
			prunedNet = ProbNetOperations.getPruned(probNet, unobservedVariablesOfInterest,
					evidence);
		} else {
			prunedNet = probNet;
		}

		return getProbsAndUtilitiesAfterFirstPrune(prunedNet, variablesOfInterest);
	}

	/**
	 * resolves the network if it has some decision whose policy has to be
	 * calculated.
	 * 
	 * @throws IncompatibleEvidenceException
	 * @throws UnexpectedInferenceException
	 * 
	 */
	private void resolveNetworkIfThereAreDecisionsWithoutPolicy() throws WrongCriterionException,
			IncompatibleEvidenceException, UnexpectedInferenceException {

		boolean thereAreDecWithoutPolicy = false;
		List<Variable> decisions = probNet.getVariables(NodeType.DECISION);

		for (int i = 0; (i < decisions.size()) && !thereAreDecWithoutPolicy; i++) {
			thereAreDecWithoutPolicy = !hasImposedPolicy(decisions.get(i));
		}

		if (thereAreDecWithoutPolicy) {
			resolveNetwork();
		}
	}

	@Override
	public TablePotential getJointProbability(List<Variable> interestVariables)
			throws IncompatibleEvidenceException, UnexpectedInferenceException {

		EvidenceCase evidence = joinPreAndPostResolutionEvidence();

		// Calculate intersections of interest and evidence variable sets
		List<Variable> interestVariablesWithEvidence = new ArrayList<Variable>(interestVariables);
		interestVariablesWithEvidence.retainAll(evidence.getVariables());
		List<Variable> evidencelessInterestVariables = new ArrayList<>(interestVariables);
		evidencelessInterestVariables.removeAll(evidence.getVariables());

		TablePotential jointProbability = getJointProbabilityOrUtility(probNet, InferencePurpose.POSTERIOR_PROB,
				evidencelessInterestVariables, evidence);
		List<TablePotential> potentialsToMultiply = new ArrayList<>();
		potentialsToMultiply.add(jointProbability);
		for (Variable intersectionVariable : interestVariablesWithEvidence) {
			potentialsToMultiply.add(constructEvidenceTablePotential(intersectionVariable, evidence));
		}

		return DiscretePotentialOperations.multiply(potentialsToMultiply);
	}

	/**
	 * @param net
	 * @param interestVariables
	 * @return The individual probabilities for each interest variable. It
	 *         assumes that the network has been pruned if the interest
	 *         variables are not all the variables in the network
	 * @throws IncompatibleEvidenceException
	 * @throws UnexpectedInferenceException
	 */
	private HashMap<Variable, TablePotential> getProbsAndUtilitiesAfterFirstPrune(
			ProbNet net, List<Variable> interestVariables) throws IncompatibleEvidenceException,
			UnexpectedInferenceException {
		HashMap<Variable, TablePotential> individualProbabilities = new HashMap<>();

		EvidenceCase evidence = joinPreAndPostResolutionEvidence();

		// Commented this line out, as it is very expensive and if evidence is
		// not compatible we will find it out anyway while performing inference
		// checkIfEvidenceIsCompatible(net, evidence);
		
		//List<Variable> terminalUtilityVariables = BasicOperations.getTerminalUtilityVariables(net);
		//terminalUtilityVariables.retainAll(interestVariables);
		
		performInference(net, InferencePurpose.POSTERIOR_UTIL, interestVariables, evidence);
		for (TablePotential utilityPotential : utilityPotentials) {
			if(utilityPotential.getUtilityVariable()!= null && interestVariables.contains(utilityPotential.getUtilityVariable()))
			{
				individualProbabilities.put(utilityPotential.getUtilityVariable(), utilityPotential);
			}
		}
		interestVariables.removeAll(individualProbabilities.keySet());
		
		for (Variable variable : interestVariables) {
			individualProbabilities.put(variable,
					getIndividualProbabilityOrUtility(net, variable, evidence));
		}

		return individualProbabilities;
	}

	private EvidenceCase joinPreAndPostResolutionEvidence() throws IncompatibleEvidenceException {
		EvidenceCase evidence = new EvidenceCase(getPreResolutionEvidence());
		try {
			evidence.addFindings(getPostResolutionEvidence().getFindings());
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
		return evidence;
	}

	/**
	 * Checks if the evidence is compatible with the network (structure and
	 * potentials)
	 * 
	 * @param net
	 * @param evidence
	 * @throws IncompatibleEvidenceException
	 * @throws UnexpectedInferenceException
	 */
	@SuppressWarnings("unused")
	private void checkIfEvidenceIsCompatible(ProbNet net, EvidenceCase evidence)
			throws IncompatibleEvidenceException, UnexpectedInferenceException {
		if(evidence.getFindings().size() <
				net.getChanceAndDecisionVariables().size())
		{
			TablePotential jointProbability = getJointProbabilityOrUtility(net, InferencePurpose.POSTERIOR_PROB,
					evidence.getVariables(), new EvidenceCase());
			if (jointProbability.getValue(evidence) <= 0.0) {
				throw new IncompatibleEvidenceException(
						"The probability of the introduced evidence is 0.0");
			}
		}
	}

	/**
	 * It is invoked from getIndividualProbabilities when a prune, common to all
	 * the variables of interest, has been perform. This method performs, for
	 * each variable of interest, a second pruning. The probabilities of the
	 * variables with evidence are also returned by the method. These
	 * probabilities can be set from the beginning with value 1.0 for the state
	 * of the evidence and 0.0 for the other states
	 * 
	 * @param evidence
	 * @param variablesOfInterest
	 *            <code>ArrayList</code> of <code>Variable</code>.
	 * @param evidence
	 *            <code>EvidenceCase</code>.
	 * @return the probabilities or each variable in a <code>HashMap</code> with
	 *         <code>key = String</code> (variable name) and
	 *         <code>value = Potential</code> identified by its name.
	 * @throws NormalizeNullVectorException
	 * @throws UnexpectedInferenceException
	 * @throws IncompatibleEvidenceException
	 */
	protected TablePotential getIndividualProbabilityOrUtility(ProbNet net, Variable variable,
			EvidenceCase evidence) throws UnexpectedInferenceException,
			IncompatibleEvidenceException {
		
		TablePotential result;
		if (evidence.contains(variable)) {
			result = constructEvidenceTablePotential(variable, evidence);
		} else {
			NodeType nodeType = net.getProbNode(variable).getNodeType();
			InferencePurpose purpose = (nodeType == NodeType.UTILITY) ? InferencePurpose.POSTERIOR_UTIL
					: InferencePurpose.POSTERIOR_PROB;
			result = getJointProbabilityOrUtility(net, purpose, Arrays.asList(variable), evidence);
		}
		return result;
	}

	/**
	 * @param variable
	 * @param evidence
	 * @return A PotentialTable with 1.0 in the evidence state and 0.0 in the
	 *         rest.
	 */
	private TablePotential constructEvidenceTablePotential(Variable variable, EvidenceCase evidence) {
		TablePotential individualProbability = new TablePotential(Arrays.asList(variable),
				PotentialRole.CONDITIONAL_PROBABILITY);
		int numStates = variable.getNumStates();
		for (int i = 0; i < numStates; i++) {
			individualProbability.values[i] = (i != evidence.getState(variable)) ? 0.0 : 1.0;
		}
		return individualProbability;
	}

	/**
	 * Prunes the <code>net</code> (which would probably has been pruned before
	 * in the case of computation of marginal probabilities). After that, it
	 * projects the evidence in the pruned net and eliminates the variables that
	 * does not belong to <code>queryVariables</code> or to the
	 * <code>evidence</code> variables. When this finishes the method multiplies
	 * the remaining potentials and returns them.
	 * 
	 * @param purpose
	 * @param queryVariables
	 *            <code>List</code> of <code>Variable</code>.
	 * @param evidence
	 *            <code>EvidenceCase</code>.
	 * @return One potential with the <code>variablesOfInterest</code>
	 *         probability table.
	 * @throws UnexpectedInferenceException
	 * @throws IncompatibleEvidenceException
	 */
	protected TablePotential getJointProbabilityOrUtility(ProbNet net, InferencePurpose purpose,
			List<Variable> queryVariables, EvidenceCase evidence)
			throws UnexpectedInferenceException, IncompatibleEvidenceException {

		if (inferenceState == InferenceState.PRERESOLUTION) {
			try {
				resolveNetworkIfThereAreDecisionsWithoutPolicy();
			} catch (WrongCriterionException e) {
				e.printStackTrace();
			}
			setInferenceStateAndInitializeStructures(InferenceState.POSTRESOLUTION);
		}

		return performInference(net, purpose, queryVariables, evidence);
	}

	/**
	 * Removes constant potentials (only have one value in attribute values)
	 * 
	 * @param projectedTablePotentials
	 * @param purpose
	 * @return A list of constant potentials removed from
	 *         projectedTablePotentials
	 * @throws IncompatibleEvidenceException
	 */
	private List<TablePotential> removeConstantPotentials(
			Collection<TablePotential> projectedTablePotentials, InferencePurpose purpose)
			throws IncompatibleEvidenceException {
		boolean addToConstantPotentials;
		boolean includeInMDN;
		List<TablePotential> constantPotentials = new ArrayList<TablePotential>();
		List<TablePotential> toRemove = new ArrayList<TablePotential>();
		for (TablePotential potential : projectedTablePotentials) {
			addToConstantPotentials = checkIfAddToConstantPotentials(potential);
			includeInMDN = checkIfIncludeInMarkovDecisionNetwork(potential);
			if (addToConstantPotentials) {
				constantPotentials.add(potential);
			}
			if (!includeInMDN) {
				toRemove.add(potential);
			}
		}
		for (TablePotential potential : toRemove) {
			projectedTablePotentials.remove(potential);
		}
		return constantPotentials;
	}

	/**
	 * @param potential
	 * @return True if and only if the potential has to be added to the list of
	 *         constant potentials. It also detects 0 in probability potentials
	 *         and then throws IncompatibleEvidenceException
	 * @throws IncompatibleEvidenceException
	 */
	public static boolean checkIfAddToConstantPotentials(TablePotential potential)
			throws IncompatibleEvidenceException {

		if (potential != null 
				&& potential.getPotentialRole() != PotentialRole.UTILITY 
				&& potential.getVariables().size() == 0 
				&& potential.values[0] == 0.0) {
					throw new IncompatibleEvidenceException("Incompatible Evidence");
		}
		return potential != null && potential.getVariables().size() == 0;
	}

	/**
	 * @param potential
	 * @return True if and only if the potential has to be added to the list of
	 *         constant potentials. It also detects 0 in probability potentials
	 *         and then throws IncompatibleEvidenceException
	 * @throws IncompatibleEvidenceException
	 */
	public static boolean checkIfIncludeInMarkovDecisionNetwork(TablePotential potential) {

		return ((potential != null) && (potential.getVariables().size() > 0));

	}

	public static void checkEvaluability(ProbNet probNet) throws NotEvaluableNetworkException {
		boolean isApplicable;

		List<NetworkType> networkTypes = initializeNetworkTypesApplicable();

		isApplicable = false;
		NetworkType networkType = probNet.getNetworkType();
		// Check that there is a network type applicable equal to type of
		// probNet
		for (int i = 0; (i < networkTypes.size()) && !isApplicable; i++) {
			isApplicable = networkType == networkTypes.get(i);
		}

		if (!isApplicable) {
			throw new NotEvaluableNetworkException("Network type " + networkType.toString()
					+ "is not evaluable.");
		} else {
			// Check that the probNet satisfies the specific constraints of the
			// algorithm
			List<PNConstraint> additionalConstraints = initializeAdditionalConstraints();
			for (int i = 0; (i < additionalConstraints.size()) && isApplicable; i++) {
				PNConstraint pnConstraint = additionalConstraints.get(i);
				if (!pnConstraint.checkProbNet(probNet)) {
					throw new NotEvaluableNetworkException("Constraint " + pnConstraint.toString()
							+ " is not satisfied by the network.");
				}
			}
		}

	}

	@Override
	public Potential getExpectedUtilities(Variable decisionVariable)
			throws IncompatibleEvidenceException, UnexpectedInferenceException {

		ProbNet reducedProbNet = null;
		// reducedProbNet = constructAuxiliaryNetwork(probNet);
		reducedProbNet = probNet;
		List<Variable> queryVariables = new ArrayList<Variable>();
		queryVariables.add(decisionVariable);
		TablePotential auxGlobalUtility = performInference(reducedProbNet,
				InferencePurpose.EXPECTED_UTIL, queryVariables, new EvidenceCase(
						getPreResolutionEvidence()));

		return auxGlobalUtility;
	}

}