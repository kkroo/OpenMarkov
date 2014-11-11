/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.potential;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

@RelationPotentialType(name = "Hazard (Weibull)", family = "Regression")
public class WeibullHazardPotential extends RegressionPotential {

	protected static final String GAMMA = "Gamma";

	/**
	 * Time variable
	 */
	private Variable timeVariable = null;

	public WeibullHazardPotential(List<Variable> variables, PotentialRole role,
			String[] covariates, double[] coefficients) {
		super(variables, role, covariates, coefficients);
	}

	public WeibullHazardPotential(List<Variable> variables, PotentialRole role,
			String[] covariates, double[] coefficients, double[] covarianceMatrix) {
		super(variables, role, covariates, coefficients, covarianceMatrix);
	}

	public WeibullHazardPotential(List<Variable> variables, PotentialRole role,
			double[] coefficients, double[] covarianceMatrix) {
		super(variables, role, getDefaultCovariates(variables, role, getMandatoryCovariates()),
				coefficients, covarianceMatrix);
	}

	public WeibullHazardPotential(List<Variable> variables, PotentialRole role,
			String[] covariates, double[] coefficients, double[] uncertaintyMatrix,
			MatrixType matrixType) {
		super(variables, role, covariates, coefficients, uncertaintyMatrix, matrixType);
	}

	public WeibullHazardPotential(List<Variable> variables, PotentialRole role,
			double[] coefficients, double[] uncertaintyMatrix, MatrixType matrixType) {
		super(variables, role, getDefaultCovariates(variables, role, getMandatoryCovariates()),
				coefficients, uncertaintyMatrix, matrixType);
	}

	public WeibullHazardPotential(List<Variable> variables, PotentialRole role) {
		this(variables, role, getDefaultCovariates(variables, role, getMandatoryCovariates()),
				new double[variables.size() + 1]);
	}

	public WeibullHazardPotential(WeibullHazardPotential potential) {
		super(potential);
		timeVariable = potential.timeVariable;
	}

	/**
	 * Returns if an instance of a certain Potential type makes sense given the
	 * variables and the potential role.
	 * 
	 * @param probNode
	 *            . <code>ProbNode</code>
	 * @param variables
	 *            . <code>List</code> of <code>Variable</code>.
	 * @param role
	 *            . <code>PotentialRole</code>.
	 */
	public static boolean validate(ProbNode probNode, List<Variable> variables, PotentialRole role) {
		return variables.get(0).isTemporal()
				&& variables.get(0).getVariableType() == VariableType.FINITE_STATES
				&& variables.get(0).getNumStates() == 2;
	}

	@Override
	public List<TablePotential> tableProject(EvidenceCase evidenceCase,
			InferenceOptions inferenceOptions, double[] coefficients, String[] covariates)
			throws NonProjectablePotentialException, WrongCriterionException {
		Variable conditionedVariable = getConditionedVariable();
		// Fill arrays numericValues and evidencelessVariables
		List<Variable> evidencelessVariables = new ArrayList<>();
		List<Integer> evidencelessVariablesIndex = new ArrayList<>();
		Map<String, String> variableValues = new HashMap<>();

		int gammaIndex = -1;
		int constantIndex = -1;
		for (int i = 0; i < covariates.length; ++i) {
			if (covariates[i].equals(GAMMA)) {
				gammaIndex = i;
			} else if (covariates[i].equals(CONSTANT)) {
				constantIndex = i;
			}
		}

		for (int i = 1; i < variables.size(); ++i) {
			Variable variable = variables.get(i);
			if (!variables.get(i).equals(timeVariable)) {
				if (evidenceCase == null || !evidenceCase.contains(variable)) {
					if (variable.getVariableType() == VariableType.NUMERIC) {
						throw new NonProjectablePotentialException(
								"Can not project potential with numeric variable "
										+ variable.getName());
					}
					evidencelessVariables.add(variable);
					evidencelessVariablesIndex.add(i - 1);
					variableValues.put(variable.getName(), "0.0");
				} else {
					double numericValue = 0;
					if (variable.getVariableType() == VariableType.NUMERIC) {
						numericValue = evidenceCase.getFinding(variable).getNumericalValue();
					} else {
						int index = evidenceCase.getFinding(variable).getStateIndex();
						numericValue = index;
						try {
							numericValue = Double
									.parseDouble(variable.getStates()[index].getName());
						} catch (NumberFormatException e) {
							// ignore
						}
					}
					variableValues.put(variable.getName(), String.valueOf(numericValue));
				}
			}
		}

		int numConfigurations = 1;
		for (Variable evidencelessVariable : evidencelessVariables) {
			numConfigurations *= evidencelessVariable.getNumStates();
		}

		List<Variable> projectedPotentialVariables = new ArrayList<>(evidencelessVariables);
		projectedPotentialVariables.add(0, variables.get(0));
		if (timeVariable != null && timeVariable.getVariableType() != VariableType.NUMERIC
				&& !evidenceCase.contains(timeVariable)) {
			projectedPotentialVariables.add(timeVariable);
		}
		TablePotential projectedPotential = new TablePotential(projectedPotentialVariables, role);
		int[] offsets = projectedPotential.getOffsets();
		int[] dimensions = projectedPotential.getDimensions();

		double[] ts = null;
		if (timeVariable != null && timeVariable.getVariableType() != VariableType.NUMERIC) {
			ts = new double[timeVariable.getNumStates()];
			double timeDifference = conditionedVariable.getTimeSlice()
					- timeVariable.getTimeSlice();
			for (int i = 0; i < ts.length; ++i) {
				ts[i] = Double.parseDouble(timeVariable.getStates()[i].getName()) + timeDifference;
			}
		} else {
			ts = new double[1];
			double t = conditionedVariable.getTimeSlice();
			if (timeVariable != null) {
				if (!evidenceCase.contains(timeVariable)) {
					throw new NonProjectablePotentialException(
							"Can not project potential without evidence on timeVariable "
									+ timeVariable.getName());
				}
				double timeDifference = conditionedVariable.getTimeSlice()
						- timeVariable.getTimeSlice();
				t = evidenceCase.getFinding(timeVariable).getNumericalValue() + timeDifference;
			}
			ts[0] = t;
		}
		double shape = Math.exp(coefficients[gammaIndex]);
		Evaluator evaluator = new Evaluator();
		for (int timeVariableState = 0; timeVariableState < ts.length; ++timeVariableState) {
			double t = ts[timeVariableState];
			for (int i = 0; i < numConfigurations; i++) {
				int configBaseIndex = (i + timeVariableState * numConfigurations) * 2;
				// Set the values of variables without evidence
				for (int j = 1; j < projectedPotentialVariables.size(); ++j) {
					int index = (configBaseIndex / offsets[j]) % dimensions[j];
					double value = index;
					try {
						value = Double
								.parseDouble(projectedPotentialVariables.get(j).getStates()[index]
										.getName());
					} catch (NumberFormatException e) {
						// ignore
					}
					variableValues.put(projectedPotentialVariables.get(j).getName(),
							String.valueOf(value));
				}
				evaluator.setVariables(variableValues);
				double lambda = coefficients[constantIndex];
				for (int j = 0; j < coefficients.length; ++j) {
					double covariateValue = 0.0;
					if (j != gammaIndex && j != constantIndex) {
						try {
							covariateValue = Double.parseDouble(evaluator.evaluate(covariates[j]));
						} catch (NumberFormatException | EvaluationException e) {
							e.printStackTrace();
						}
						lambda += covariateValue * coefficients[j];
					}
				}
				lambda = Math.exp(lambda);
				double diff = Math.pow(t - 1, shape) - Math.pow(t, shape);
				double probability = 1 - Math.exp(lambda * diff);
				// p
				projectedPotential.values[configBaseIndex + 1] = probability;
				// Complement (1-p)
				projectedPotential.values[configBaseIndex] = 1 - probability;
			}
		}

		return Arrays.asList(projectedPotential);
	}

	@Override
	public Potential copy() {
		return new WeibullHazardPotential(this);
	}

	public Variable getTimeVariable() {
		return timeVariable;
	}

	public void setTimeVariable(Variable timeVariable) {
		this.timeVariable = timeVariable;
	}

	public static String[] getMandatoryCovariates() {
		return new String[] { GAMMA, CONSTANT };
	}

	@Override
	public String toString() {
		return super.toString() + " = Hazard (Weibull)";
	}

	@Override
	public void shift(ProbNet probNet, int timeDifference) throws ProbNodeNotFoundException {
		super.shift(probNet, timeDifference);
		if (timeVariable != null) {
			timeVariable = probNet.getShiftedVariable(timeVariable, timeDifference);
		}
	}

	@Override
	public void replaceNumericVariable(Variable convertedParentVariable) {
		super.replaceNumericVariable(convertedParentVariable);
		if (timeVariable != null
				&& convertedParentVariable.getName().equals(timeVariable.getName())) {
			setTimeVariable(convertedParentVariable);
		}
	}

}
