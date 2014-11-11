package org.openmarkov.inference.variableElimination.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.inference.variableElimination.VariableElimination;
import org.openmarkov.inference.variableElimination.VariableElimination.InferencePurpose;
import org.openmarkov.inference.variableElimination.VariableElimination.InferenceState;

@SuppressWarnings("serial")
/** Removes a decision node in a variable elimination algorithm.
 * This Edit class is valid for Bayesian networks and influence diagrams.
 * */
public class CRemoveDecisionNodeVEEdit extends CRemoveNodeVEEdit {

	public CRemoveDecisionNodeVEEdit(ProbNet probNet, List<TablePotential> constantPotentials,
			InferencePurpose purpose, Variable variableToDelete,
			VariableElimination varElimination, boolean isLastVariable,
			InferenceState inferenceState) {
		super(probNet, constantPotentials, purpose, variableToDelete, varElimination,
				isLastVariable, inferenceState);
	}

	public CRemoveDecisionNodeVEEdit(ProbNet probNet, List<TablePotential> constantPotentials,
			InferencePurpose purpose, Variable variableToDelete,
			VariableElimination varElimination, InferenceState inferenceState) {
		this(probNet, constantPotentials, purpose, variableToDelete, varElimination, false,
				inferenceState);

	}

	@Override
	protected List<TablePotential> marginalizeVariableFromPotentials(
			TablePotential probabilityPotential, List<TablePotential> utilityPotentials) {

		List<TablePotential> potentialsToMarginalize = new ArrayList<>();
		if (!utilityPotentials.isEmpty()) {
			potentialsToMarginalize.add(DiscretePotentialOperations.sum(utilityPotentials));
		}
		if (probabilityPotential != null) {
			potentialsToMarginalize.add(probabilityPotential);
		}
		TablePotential[] newPotentials = DiscretePotentialOperations.multiplyAndMaximizeUniformly(
				potentialsToMarginalize, variable);
		if (DiscretePotentialOperations.isThereAUtilityPotential(utilityPotentials)) {
			// Save the policy
			policy = newPotentials[1];
		}
		if (utilityPotentials.size() == 1) {
			newPotentials[0].setUtilityVariable(utilityPotentials.get(0).getUtilityVariable());
		}
		return Arrays.asList(newPotentials[0]);
	}

}
