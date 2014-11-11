/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.potential.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.openmarkov.core.exception.IllegalArgumentTypeException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.PotentialOperationException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.Choice;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * This class defines a set of common operations over discrete potentials (
 * <code>TablePotential</code>s) and discrete variables (<code>Variable</code>
 * s). The method are invoked from <code>PotentialOperations</code> after
 * checking that the parameters are discrete.
 * 
 * @author marias
 */
public final class DiscretePotentialOperations {

    /**
     * Round error used to compare two numbers. If they differ in less than
     * <code>maxRoundErrorAllowed</code> they will be considered equals.
     */
    public static double maxRoundErrorAllowed = 1E-5;

    /**
     * @param tablePotentials
     *            <code>ArrayList</code> of extends <code>Potential</code>.
     * @return A <code>TablePotential</code> as result.
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     */
    public static TablePotential multiply(List<TablePotential> tablePotentials) {
        return multiply(tablePotentials, true);
    }

    public static TablePotential multiply(List<TablePotential> tablePotentials, boolean reorder) {
        int numPotentials = tablePotentials.size();
        // Special cases: one or zero potentials
        if (numPotentials < 2) {
            if (numPotentials == 1) {
                return (TablePotential) tablePotentials.get(0);
            } else {
                return null;
            }
        }

        List<TablePotential> potentials = new ArrayList<>(tablePotentials);

        // Sort the potentials according to the table size
        if (reorder) {
            Collections.sort(potentials);
        }

        // Gets constant factor: The product of constant potentials
        double constantFactor = getConstantFactor(potentials);

        // get role
        PotentialRole role = getRole(potentials);

        potentials = AuxiliaryOperations.getProperPotentials(potentials);
        if (potentials.size() == 0) {
            TablePotential constantTablePotential = new TablePotential(null, role);
            constantTablePotential.values[0] = constantFactor;
            return constantTablePotential;
        }

        // Gets the union
        List<Variable> resultVariables = AuxiliaryOperations.getUnionVariables(potentials);

        int numVariables = resultVariables.size();

        // Gets the tables of each TablePotential
        numPotentials = potentials.size();
        double[][] tables = new double[numPotentials][];
        for (int i = 0; i < numPotentials; i++) {
            tables[i] = potentials.get(i).values;
        }

        // Gets dimension
        int[] resultDimension = TablePotential.calculateDimensions(resultVariables);

        // Gets offset accumulate
        int[][] offsetAccumulate = DiscretePotentialOperations.getAccumulatedOffsets(potentials,
                resultVariables);

        // Gets coordinate
        int[] resultCoordinate;
        if (numVariables != 0) {
            resultCoordinate = new int[numVariables];
        } else {
            resultCoordinate = new int[1];
            resultCoordinate[0] = 0;
        }

        // Position in each table potential
        int[] potentialsPositions = new int[numPotentials];
        for (int i = 0; i < numPotentials; i++) {
            potentialsPositions[i] = 0;
        }

        // Multiply
        int incrementedVariable = 0;
        double mulResult;
        int[] dimensions = TablePotential.calculateDimensions(resultVariables);
        int[] offsets = TablePotential.calculateOffsets(dimensions);
        int tableSize = 1; // If numVariables == 0 the potential is a constant
        if (numVariables > 0) {
            tableSize = dimensions[numVariables - 1] * offsets[numVariables - 1];
        }
        double[] resultValues = new double[tableSize];

        for (int resultPosition = 0; resultPosition < tableSize; resultPosition++) {
            mulResult = constantFactor;

            /*
             * increment the result coordinate and find out which variable is to
             * be incremented
             */
            for (int iVariable = 0; iVariable < resultCoordinate.length; iVariable++) {
                // try by incrementing the current variable (given by iVariable)
                resultCoordinate[iVariable]++;
                if (resultCoordinate[iVariable] != resultDimension[iVariable]) {
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
                resultCoordinate[iVariable] = 0;
            }

            // multiply
            for (int iPotential = 0; iPotential < numPotentials; iPotential++) {
                // multiply the numbers
                mulResult = mulResult * tables[iPotential][potentialsPositions[iPotential]];
                // update the current position in each potential table
                potentialsPositions[iPotential] += offsetAccumulate[iPotential][incrementedVariable];
            }

            resultValues[resultPosition] = mulResult;
        }
        return new TablePotential(resultVariables, role, resultValues);
    }

    /**
     * @param tablePotentials
     *            <code>List</code> of <code>TablePotential</code>s.
     */
    public static TablePotential sum(List<TablePotential> tablePotentials) {
        List<TablePotential> constantPotentials;
        if (tablePotentials.size() == 1) {
            return (TablePotential) tablePotentials.get(0);
        }

        List<TablePotential> potentials = new ArrayList<>(tablePotentials);

        // Leave out the constant potentials
        constantPotentials = new ArrayList<TablePotential>();
        for (int i = 0; i < tablePotentials.size(); i++) {
            Potential auxPotential = tablePotentials.get(i);
            if (auxPotential.getVariables().size() == 0) {
                potentials.remove(auxPotential);
                constantPotentials.add((TablePotential) auxPotential);
            }
        }

        // Calculate the sum of constant potentials
        double sumConstantPotentials = 0.0;
        int numConstantPotentials = constantPotentials.size();
        for (int i = 0; i < numConstantPotentials; i++) {
            sumConstantPotentials = sumConstantPotentials + constantPotentials.get(i).values[0];
        }

        int numPotentials = potentials.size();

        // Gets the union
        List<Variable> resultVariables = AuxiliaryOperations.getUnionVariables(potentials);
        int numVariables = resultVariables.size();

        // Gets the tables of each TablePotential
        double[][] tables = new double[numPotentials][];
        for (int i = 0; i < numPotentials; i++) {
            tables[i] = potentials.get(i).values;
        }

        // Gets dimensions
        int[] resultDimensions = TablePotential.calculateDimensions(resultVariables);

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

        // Sum
        int incrementedVariable = 0;
        int[] dimensions = (!resultVariables.isEmpty()) ? TablePotential.calculateDimensions(resultVariables)
                : new int[0];
        int[] offsets = (!resultVariables.isEmpty()) ? TablePotential.calculateOffsets(dimensions)
                : new int[0];
        int tableSize = 1; // If numVariables == 0 the potential is a constant
        if (numVariables > 0) {
            tableSize = dimensions[numVariables - 1] * offsets[numVariables - 1];
        }
        double[] resultValues = new double[tableSize];

        if (potentials.size() > 0) {
            double sum;
            for (int resultPosition = 0; resultPosition < tableSize; resultPosition++) {
                /*
                 * increment the result coordinate and find out which variable
                 * is to be incremented
                 */
                for (int iVariable = 0; iVariable < resultCoordinates.length; iVariable++) {
                    // try by incrementing the current variable (given by
                    // iVariable)
                    resultCoordinates[iVariable]++;
                    if (resultCoordinates[iVariable] != resultDimensions[iVariable]) {
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

                // sum
                sum = 0;
                for (int iPotential = 0; iPotential < numPotentials; iPotential++) {
                    // sum the numbers
                    sum = sum + tables[iPotential][potentialPositions[iPotential]];
                    // update the current position in each potential table
                    potentialPositions[iPotential] += accumulatedOffsets[iPotential][incrementedVariable];
                }                
                resultValues[resultPosition] = sum;
            }
        }
        // Sum constant potentials to the result
        if ((numConstantPotentials > 0) && (sumConstantPotentials != 0.0)) {
            int length = resultValues.length;
            for (int i = 0; i < length; i++) {
                resultValues[i] = resultValues[i] + sumConstantPotentials;
            }
        }
        return new TablePotential(resultVariables, getRole(tablePotentials), resultValues);
    }

    public static TablePotential sum(TablePotential... tablePotentials) {
        List<TablePotential> potentialList = new ArrayList<TablePotential>(tablePotentials.length);
        for (TablePotential potential : tablePotentials) {
            potentialList.add(potential);
        }
        return sum(potentialList);
    }

    private static PotentialRole getRole(List<? extends Potential> potentials) {
        boolean atLeastOneUtility = false;
        for (Potential potential : potentials) {
            atLeastOneUtility = atLeastOneUtility || potential.isUtility();
        }
        if (atLeastOneUtility) {
            return PotentialRole.UTILITY;
        }
        boolean atLeastOneJoinProbability = false;
        for (Potential potential : potentials) {
            atLeastOneJoinProbability = atLeastOneJoinProbability
                    || potential.getPotentialRole() == PotentialRole.JOINT_PROBABILITY;
        }
        if (atLeastOneJoinProbability) {
            return PotentialRole.JOINT_PROBABILITY;
        }
        return PotentialRole.CONDITIONAL_PROBABILITY;
    }

    /**
     * @param tablePotentials
     *            array to multiply
     * @param variablesToKeep
     *            The set of variables that will appear in the resulting
     *            potential
     * @param variablesToEliminate
     *            The set of variables eliminated by marginalization (in
     *            general, by summing out or maximizing)
     * @argCondition variablesToKeep and variablesToEliminate are a partition of
     *               the union of the variables of the potential
     * @return A <code>TablePotential</code> result of multiply and marginalize.
     */
    public static TablePotential multiplyAndMarginalize(List<TablePotential> tablePotentials,
            List<Variable> variablesToKeep,
            List<Variable> variablesToEliminate) {

        // Constant potentials are those that do not depend on any variables.
        // The product of all the constant potentials is the constant factor.
        double constantFactor = 1.0;
        // Non constant potentials are proper potentials.
        List<TablePotential> nonConstantPotentials = new ArrayList<TablePotential>();
        for (TablePotential potential : tablePotentials) {
            if (potential.getNumVariables() != 0) {
                nonConstantPotentials.add(potential);
            } else {
                constantFactor *= potential.values[potential.getInitialPosition()];
            }
        }

        int numNonConstantPotentials = nonConstantPotentials.size();

        if (numNonConstantPotentials == 0) {
            TablePotential resultingPotential = new TablePotential(variablesToKeep,
                    getRole(tablePotentials));
            resultingPotential.values[0] = constantFactor;
            return resultingPotential;
        }

        // variables in the resulting potential
        List<Variable> unionVariables = new ArrayList<Variable>(variablesToEliminate);
        unionVariables.addAll(variablesToKeep);
        int numUnionVariables = unionVariables.size();

        // current coordinate in the resulting potential
        int[] unionCoordinate = new int[numUnionVariables];
        int[] unionDimensions = TablePotential.calculateDimensions(unionVariables);

        // Defines some arrays for the proper potentials...
        double[][] tables = new double[numNonConstantPotentials][];
        int[] initialPositions = new int[numNonConstantPotentials];
        int[] currentPositions = new int[numNonConstantPotentials];
        int[][] accumulatedOffsets = new int[numNonConstantPotentials][];
        // ... and initializes them
        // TablePotential unionPotential = new
        // TablePotential(unionVariables,null);
        for (int i = 0; i < numNonConstantPotentials; i++) {
            TablePotential potential = nonConstantPotentials.get(i);
            tables[i] = potential.values;
            initialPositions[i] = potential.getInitialPosition();
            currentPositions[i] = initialPositions[i];
            accumulatedOffsets[i] = TablePotential.getAccumulatedOffsets(unionVariables, potential.getVariables());
        }

        // The result size is the product of the dimensions of the
        // variables to keep
        int resultSize = TablePotential.computeTableSize(variablesToKeep);
        double[] resultValues = new double[resultSize];
        // The elimination size is the product of the dimensions of the
        // variables to eliminate
        int eliminationSize = 1;
        for (Variable variable : variablesToEliminate) {
            eliminationSize *= variable.getNumStates();
        }

        // Auxiliary variables for the nested loops
        double multiplicationResult; // product of the table values
        double accumulator; // in general, the sum or the maximum
        int increasedVariable = 0; // when computing the next configuration

        // outer iterations correspond to the variables to keep
        for (int outerIteration = 0; outerIteration < resultSize; outerIteration++) {
            // Inner iterations correspond to the variables to eliminate
            // accumulator summarizes the result of all inner iterations

            // first inner iteration
            multiplicationResult = constantFactor;
            for (int i = 0; i < numNonConstantPotentials; i++) {
                // multiply the numbers
                multiplicationResult *= tables[i][currentPositions[i]];
            }
            accumulator = multiplicationResult;

            // next inner iterations
            for (int innerIteration = 1; innerIteration < eliminationSize; innerIteration++) {

                // find the next configuration and the index of the
                // increased variable
                for (int j = 0; j < unionCoordinate.length; j++) {
                    unionCoordinate[j]++;
                    if (unionCoordinate[j] < unionDimensions[j]) {
                        increasedVariable = j;
                        break;
                    }
                    unionCoordinate[j] = 0;
                }

                // update the positions of the potentials we are multiplying
                for (int i = 0; i < numNonConstantPotentials; i++) {
                    currentPositions[i] += accumulatedOffsets[i][increasedVariable];
                }

                // multiply the table values of the potentials
                multiplicationResult = constantFactor;
                for (int i = 0; i < numNonConstantPotentials; i++) {
                    multiplicationResult *= tables[i][currentPositions[i]];
                }

                // update the accumulator (for this inner iteration)
                accumulator += multiplicationResult;
                // accumulator =
                // operator.combine(accumulator,multiplicationResult);

            } // end of inner iteration

            // when eliminationSize == 0 there is a multiplication without
            // marginalization but we must find the next configuration
            if (outerIteration < resultSize - 1) {
                // find the next configuration and the index of the
                // increased variable
                for (int j = 0; j < unionCoordinate.length; j++) {
                    unionCoordinate[j]++;
                    if (unionCoordinate[j] < unionDimensions[j]) {
                        increasedVariable = j;
                        break;
                    }
                    unionCoordinate[j] = 0;
                }

                // update the positions of the potentials we are multiplying
                for (int i = 0; i < numNonConstantPotentials; i++) {
                    currentPositions[i] += accumulatedOffsets[i][increasedVariable];
                }
            }

            resultValues[outerIteration] = accumulator;

        } // end of outer iteration

        return new TablePotential(variablesToKeep, getRole(tablePotentials), resultValues);
    }

    /**
     * @param potentials
     *            potentials array to multiply
     * @param variablesOfInterest
     *            Set of variables that must be kept (although this set may
     *            contain some variables that are not in any potential)
     *            <code>potentials</code>
     * @return The multiplied potentials
     */
    public static TablePotential multiplyAndMarginalize(List<TablePotential> potentials,
            List<Variable> variablesOfInterest) {

        // Obtain parameters to invoke multiplyAndMarginalize
        // Union of the variables of the potential list
        List<Variable> unionVariables = AuxiliaryOperations.getUnionVariables(potentials);

        // Classify unionVariables in two possibles arrays
        List<Variable> variablesToKeep = new ArrayList<Variable>();
        List<Variable> variablesToEliminate = new ArrayList<Variable>();
        for (Variable variable : unionVariables) {
            if (variablesOfInterest.contains(variable)) {
                variablesToKeep.add(variable);
            } else {
                variablesToEliminate.add(variable);
            }
        }

        return DiscretePotentialOperations.multiplyAndMarginalize(potentials,
                variablesToKeep,
                variablesToEliminate);
    }

    /**
     * @param potentials
     *            <code>ArrayList</code> of <code>Potential</code>s to multiply.
     * @param variableToEliminate
     *            <code>Variable</code>.
     * @return result <code>Potential</code> multiplied without
     *         <code>variableToEliminate</code>
     */
    public static TablePotential multiplyAndMarginalize(List<TablePotential> potentials,
            Variable variableToEliminate) {
        List<Variable> variablesToKeep = AuxiliaryOperations.getUnionVariables(potentials);
        variablesToKeep.remove(variableToEliminate);
        return multiplyAndMarginalize(potentials, variablesToKeep, Arrays.asList(variableToEliminate));
    }

    /**
     * @param potential
     *            <code>Potential</code> to marginalize
     * @param variableToEliminate
     *            <code>Variable</code>
     * @return Marginalized potential
     */
    public static TablePotential marginalize(TablePotential potential, Variable variableToEliminate) {
        List<Variable> variablesToKeep = new ArrayList<Variable>(potential.getVariables());
        variablesToKeep.remove(variableToEliminate);
        List<Variable> variablesToEliminate = new ArrayList<Variable>();
        variablesToEliminate.add(variableToEliminate);
        List<TablePotential> potentials = new ArrayList<TablePotential>();
        potentials.add(potential);
        return multiplyAndMarginalize(potentials, variablesToKeep, variablesToEliminate);
    }

    /**
     * @param potential
     * @param variablesOfInterest
     */
    public static TablePotential marginalize(TablePotential potential,
            List<Variable> variablesOfInterest) {
        // Obtain parameters to invoke multiplyAndMarginalize
        // Union of the variables of the potential list
        List<Variable> variables = potential.getVariables();

        List<Variable> variablesToKeep = new ArrayList<Variable>();
        List<Variable> variablesToEliminate = new ArrayList<Variable>();

        for (Variable variable : variables) {
            if (variablesOfInterest.contains(variable)) {
                variablesToKeep.add(variable);
            } else {
                variablesToEliminate.add(variable);
            }
        }

        List<TablePotential> potentials = new ArrayList<TablePotential>();
        potentials.add(potential);

        return DiscretePotentialOperations.multiplyAndMarginalize(potentials,
                variablesToKeep,
                variablesToEliminate);
    }

    /**
     * @precondition variablesToKeep + variablesToEliminate =
     *               potential.getVariables()
     * @precondition variablesToKeep
     * @param potential
     *            that will be marginalized
     * @param variablesToKeep
     * @param variablesToEliminate
     * @throws PotentialOperationException
     */
    public static Potential marginalize(TablePotential potential,
            List<Variable> variablesToKeep,
            List<Variable> variablesToEliminate) {
        List<TablePotential> potentials = new ArrayList<TablePotential>();
        potentials.add(potential);
        return DiscretePotentialOperations.multiplyAndMarginalize(potentials,
                variablesToKeep,
                variablesToEliminate);
    }

    /**
     * @param potentials
     *            An array of ordered <code>TablePotential</code>s
     * @return constantFactor: The product of the constant potentials (the first
     *         <i>k</i> because the array is ordered by size)
     * @see org.openmarkov.core.model.network.potential.operation.AuxiliaryOperations#getProperPotentials(ArrayList)
     */
    public static double getConstantFactor(List<TablePotential> potentials) {
        double constantFactor = 1.0;
        for (TablePotential potential : potentials) {
            if (potential.values.length > 1) {
                continue;
            }
            constantFactor *= potential.values[0];
        }
        return constantFactor;
    }

    /**
     * Compute the accumulated offsets of a <code>Potential</code>s array with
     * the order imposed by <code>potentialResult</code>
     * 
     * @param potentials
     *            <code>ArrayList</code> of <code>Potential</code>s.
     * @param potentialResult
     *            <code>TablePotential</code>.
     * @return An array of arrays of integers (<code>int[][]</code>).
     */
    public static int[][] getAccumulatedOffsets(List<TablePotential> potentials,
            TablePotential potentialResult) {

        int numPotentials = potentials.size();
        int[][] accumulatedOffsets = new int[numPotentials][];

        for (int i = 0; i < numPotentials; i++) {
            TablePotential potential = potentials.get(i);
            accumulatedOffsets[i] = potentialResult.getAccumulatedOffsets(
            // potential.getOriginalVariables());
            potential.getVariables());
        }
        return accumulatedOffsets;
    }

    /**
     * Compute the accumulated offsets of a <code>Potential</code>s array with
     * the order imposed by <code>variables</code>
     * 
     * @param potentials
     *            <code>ArrayList</code> of <code>Potential</code>s.
     * @param variables
     * @return An array of arrays of integers (<code>int[][]</code>).
     */
    public static int[][] getAccumulatedOffsets(List<TablePotential> potentials,
            List<Variable> variables) {

        int numPotentials = potentials.size();
        int[][] accumulatedOffsets = new int[numPotentials][];

        for (int i = 0; i < numPotentials; i++) {
            TablePotential potential = potentials.get(i);
            accumulatedOffsets[i] = TablePotential.getAccumulatedOffsets(variables,
                    potential.getVariables());
        }
        return accumulatedOffsets;
    }

    /**
     * @param potentials
     * @param variablesToEliminate
     * @throws PotentialOperationException
     */
    public static Potential multiplyAndEliminate(List<TablePotential> potentials,
            List<Variable> variablesToEliminate) {

        // Obtain parameters to invoke multiplyAndMarginalize
        // Union of the variables of the potential list
        List<Variable> variablesToKeep = AuxiliaryOperations.getUnionVariables(potentials);
        variablesToKeep.removeAll(variablesToEliminate);

        return multiplyAndMarginalize(potentials, variablesToKeep, variablesToEliminate);
    }

    /**
     * @param potential
     *            a <code>TablePotential</code>
     * @return The <code>potential</code> normalized
     */
    public static TablePotential normalize(TablePotential potential)
            throws NormalizeNullVectorException {
        TablePotential tablePotential = (TablePotential) potential;
        // Check for null vectors
        int p = 0;
        for (p = 0; p < tablePotential.values.length; p++) {
            if (tablePotential.values[p] != 0.0) {
                break;
            }
        }
        if (p == tablePotential.values.length) {
            // All elements in tablePotential.table == 0
            throw new NormalizeNullVectorException("NormalizeNullVectorException: "
                    + "All elements in the TablePotential "
                    + tablePotential.getVariables()
                    + " table are equal to 0.0");
        }
        List<Variable> variables = tablePotential.getVariables();
        if ((variables != null) && (variables.size() > 0)) {
            if (potential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
                int numStates = variables.get(0).getNumStates();
                double normalizationFactor = 0.0;
                for (int i = 0; i < tablePotential.values.length; i += numStates) {
                    normalizationFactor = 0.0;
                    for (int j = 0; j < numStates; j++) {
                        normalizationFactor += tablePotential.values[i + j];
                    }
                    for (int j = 0; j < numStates; j++) {
                        tablePotential.values[i + j] /= normalizationFactor;
                    }
                }
            } else if (potential.getPotentialRole() == PotentialRole.JOINT_PROBABILITY) {
                double normalizationFactor = 0.0;
                for (int i = 0; i < tablePotential.values.length; i++) {
                    normalizationFactor += tablePotential.values[i];
                }
                for (int i = 0; i < tablePotential.values.length; i++) {
                    tablePotential.values[i] /= normalizationFactor;
                }
            }
        }
        return tablePotential;
    }

    /**
     * Divides two <code>TablePotential</code>s using the accumulated offsets
     * algorithm.
     * 
     * @argCondition numerator and denominator have the same domain (variables)
     * @param numerator
     *            <code>Potential</code>.
     * @param denominator
     *            <code>Potential</code>.
     * @return The quotient: A <code>TablePotential</code> with the union of the
     *         variables of numerator and denominator.
     */
    public static TablePotential divide(Potential numerator, Potential denominator) {
        // Get variables and create quotient potential.
        // Quotient potential variables = numerator potential variables union
        // denominator potential variables
        TablePotential tNumerator = (TablePotential) numerator;
        TablePotential tDenominator = (TablePotential) denominator;
        List<Variable> numeratorVariables = new ArrayList<Variable>(tNumerator.getVariables());
        List<Variable> denominatorVariables = new ArrayList<Variable>(tDenominator.getVariables());
        int numNumeratorVariables = numeratorVariables.size();
        int numDenominatorVariables = denominatorVariables.size();
        denominatorVariables.removeAll(numeratorVariables);
        numeratorVariables.addAll(denominatorVariables);
        List<Variable> quotientVariables = numeratorVariables;
        TablePotential quotient = new TablePotential(quotientVariables,
                PotentialRole.JOINT_PROBABILITY);
        if ((numNumeratorVariables == 0) || (numDenominatorVariables == 0)) {
            return divide(tNumerator,
                    tDenominator,
                    quotient,
                    numNumeratorVariables,
                    numDenominatorVariables);
        }

        int numVariables = quotient.getNumVariables();

        // Gets the tables of each TablePotential
        double[][] tables = new double[2][];
        tables[0] = tNumerator.values;
        tables[1] = tDenominator.values;

        // Gets dimension
        int[] quotientDimension = quotient.getDimensions();

        // Gets offset accumulate
        List<TablePotential> potentials = new ArrayList<TablePotential>();
        potentials.add(tNumerator);
        potentials.add(tDenominator);
        int[][] offsetAccumulate = DiscretePotentialOperations.getAccumulatedOffsets(potentials,
                quotient);

        // Gets coordinate
        int[] quotientCoordinate;
        if (numVariables != 0) {
            quotientCoordinate = new int[numVariables];
        } else {
            quotientCoordinate = new int[1];
            quotientCoordinate[0] = 0;
        }

        // Position in each table potential
        int[] potentialsPositions = new int[2];
        for (int i = 0; i < 2; i++) {
            potentialsPositions[i] = 0;
        }

        // Divide
        int incrementedVariable = 0;
        int[] dimension = quotient.getDimensions();
        int[] offset = quotient.getOffsets();
        int tamTable = 1; // If numVariables == 0 the potential is a constant
        if (numVariables > 0) {
            tamTable = dimension[numVariables - 1] * offset[numVariables - 1];
        }

        for (int quotientPosition = 0; quotientPosition < tamTable; quotientPosition++) {
            /*
             * increment the result coordinate and find out which variable is to
             * be incremented
             */
            for (int iVariable = 0; iVariable < quotientCoordinate.length; iVariable++) {
                // try by incrementing the current variable (given by iVariable)
                quotientCoordinate[iVariable]++;
                if (quotientCoordinate[iVariable] != quotientDimension[iVariable]) {
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
                quotientCoordinate[iVariable] = 0;
            }

            // divide
            if (tDenominator.values[potentialsPositions[1]] == 0.0) {
                quotient.values[quotientPosition] = 0.0;
            } else {
                quotient.values[quotientPosition] = tNumerator.values[potentialsPositions[0]]
                        / tDenominator.values[potentialsPositions[1]];
            }
            for (int iPotential = 0; iPotential < 2; iPotential++) {
                // update the current position in each potential table
                potentialsPositions[iPotential] += offsetAccumulate[iPotential][incrementedVariable];
            }
        }

        return quotient;
    }

    /**
     * Divide two potentials when one of them has any variable
     * 
     * @param numerator
     *            <code>TablePotential</code>
     * @param denominator
     *            <code>TablePotential</code>
     * @param quotient
     *            <code>TablePotential</code>
     * @param numNumeratorVariables
     *            <code>int</code>
     * @param numDenominatorVariables
     *            <code>int</code>
     * @return quotient The <code>TablePotential</code> received with its table.
     */
    private static TablePotential divide(TablePotential numerator,
            TablePotential denominator,
            TablePotential quotient,
            int numNumeratorVariables,
            int numDenominatorVariables) {
        if (numNumeratorVariables == 0) {
            int sizeTableDenominator = denominator.values.length;
            double dNumerator = numerator.values[0];
            for (int i = 0; i < sizeTableDenominator; i++) {
                quotient.values[i] = dNumerator / denominator.values[i];
            }
        } else {
            int sizeTableNumerator = numerator.values.length;
            double dDenominator = denominator.values[0];
            for (int i = 0; i < sizeTableNumerator; i++) {
                quotient.values[i] = numerator.values[i] / dDenominator;
            }
        }
        return quotient;
    }

    // TODO Eliminar este método si no es usado por otros
    /**
     * @param numerator
     *            <tt>Potential</tt>
     * @param denominator
     *            <tt>Potential</tt>
     * @throws <tt>IllegalArgumentTypeException</tt> if numerator of denominator
     *         are not <tt>TablePotential</tt>
     * @return The quotient
     */
    public static Potential dividePotentials(Potential numerator, Potential denominator)
            throws IllegalArgumentTypeException {
        // parameter correct type verification before calling right method
        if (!(numerator instanceof TablePotential) || !(denominator instanceof TablePotential)) {
            String errMsg = new String("");
            errMsg = errMsg
                    + "Unsupported operation: "
                    + "divide can only manage potentials of type TablePotential.\n";
            if (numerator == null) {
                errMsg = errMsg + "Numerator = null\n";
            } else {
                if (!(numerator instanceof TablePotential)) {
                    errMsg = errMsg + "Numerator class is " + numerator.getClass().getName() + "\n";
                }
            }
            if (denominator == null) {
                errMsg = errMsg + "Denominator = null\n";
            } else {
                if (!(denominator instanceof TablePotential)) {
                    errMsg = errMsg
                            + "Denominator class is "
                            + denominator.getClass().getName()
                            + "\n";
                }
            }
            throw new IllegalArgumentTypeException(errMsg);
        }

        return DiscretePotentialOperations.divide(numerator, denominator);
    }

    /**
     * @param tablePotentials
     *            <code>ArrayList</code> of <code>TablePotential</code>s.
     * @param fsVariablesToKeep
     *            <code>ArrayList</code> of <code>Variable</code>s.
     * @param fsVariableToMaximize
     *            <code>Variable</code>.
     * @return Two potentials: 1) a <code>Potential</code> resulting of
     *         multiplication and maximization of
     *         <code>variableToMaximize</code> and 2) a
     *         <code>GTablePotential</code> of <code>Choice</code> (same
     *         variables as preceding) with the value choosed for
     *         <code>variableToMaximize</code> in each configuration.
     */
    @SuppressWarnings("unchecked")
    public static Object[] multiplyAndMaximize(List<Potential> tablePotentials,
            List<Variable> fSVariablesToKeep,
            Variable fSVariableToMaximize) {
        List<TablePotential> potentials = (ArrayList<TablePotential>) ((Object) tablePotentials);
        List<Variable> variablesToKeep = (ArrayList<Variable>) ((Object) fSVariablesToKeep);

        PotentialRole role = getRole(tablePotentials);

        TablePotential resultingPotential = new TablePotential(variablesToKeep, role);

        GTablePotential<Choice> gResult = new GTablePotential<Choice>(variablesToKeep, role);
        int numStates = ((Variable) fSVariableToMaximize).getNumStates();
        int[] statesChoosed;
        Choice choice;

        // Constant potentials are those that do not depend on any variables.
        // The product of all the constant potentials is the constant factor.
        double constantFactor = 1.0;
        // Non constant potentials are proper potentials.
        List<TablePotential> properPotentials = new ArrayList<TablePotential>();
        for (Potential potential : potentials) {
            if (potential.getNumVariables() != 0) {
                properPotentials.add((TablePotential) potential);
            } else {
                constantFactor *= ((TablePotential) potential).values[((TablePotential) potential).getInitialPosition()];
            }
        }

        int numProperPotentials = properPotentials.size();

        if (numProperPotentials == 0) {
            resultingPotential.values[0] = constantFactor;
            return new Object[] { resultingPotential, gResult };
        }

        // variables in the resulting potential
        List<Variable> unionVariables = new ArrayList<Variable>();
        unionVariables.add((Variable) fSVariableToMaximize);
        unionVariables.addAll(variablesToKeep);
        int numUnionVariables = unionVariables.size();

        // current coordinate in the resulting potential
        int[] unionCoordinate = new int[numUnionVariables];
        int[] unionDimensions = TablePotential.calculateDimensions(unionVariables);

        // Defines some arrays for the proper potentials...
        double[][] tables = new double[numProperPotentials][];
        int[] initialPositions = new int[numProperPotentials];
        int[] currentPositions = new int[numProperPotentials];
        int[][] accumulatedOffsets = new int[numProperPotentials][];
        // ... and initializes them
        TablePotential unionPotential = new TablePotential(unionVariables, null);
        for (int i = 0; i < numProperPotentials; i++) {
            TablePotential potential = (TablePotential) properPotentials.get(i);
            tables[i] = potential.values;
            initialPositions[i] = potential.getInitialPosition();
            currentPositions[i] = initialPositions[i];
            accumulatedOffsets[i] = unionPotential
            // .getAccumulatedOffsets(potential.getOriginalVariables());
            .getAccumulatedOffsets(potential.getVariables());
        }

        // The result size is the product of the dimensions of the
        // variables to keeep
        int resultSize = resultingPotential.values.length;
        // The elimination size is the product of the dimensions of the
        // variables to eliminate
        int eliminationSize = 1;
        eliminationSize *= ((Variable) fSVariableToMaximize).getNumStates();

        // Auxiliary variables for the nested loops
        double multiplicationResult; // product of the table values
        double accumulator; // in general, the sum or the maximum
        int increasedVariable = 0; // when computing the next configuration

        // outer iterations correspond to the variables to keep
        for (int outerIteration = 0; outerIteration < resultSize; outerIteration++) {
            // Inner iterations correspond to the variables to eliminate
            // accumulator summarizes the result of all inner iterations

            // first inner iteration
            multiplicationResult = constantFactor;
            for (int i = 0; i < numProperPotentials; i++) {
                // multiply the numbers
                multiplicationResult *= tables[i][currentPositions[i]];
            }
            statesChoosed = new int[numStates];
            statesChoosed[0] = 0;
            choice = new Choice(fSVariableToMaximize, statesChoosed);
            accumulator = multiplicationResult;
            choice.setValue(0); // because in first iteration we have a maximum

            // next inner iterations
            for (int innerIteration = 1; innerIteration < eliminationSize; innerIteration++) {

                // find the next configuration and the index of the
                // increased variable
                for (int j = 0; j < unionCoordinate.length; j++) {
                    unionCoordinate[j]++;
                    if (unionCoordinate[j] < unionDimensions[j]) {
                        increasedVariable = j;
                        break;
                    }
                    unionCoordinate[j] = 0;
                }

                // update the positions of the potentials we are multiplying
                for (int i = 0; i < numProperPotentials; i++) {
                    currentPositions[i] += accumulatedOffsets[i][increasedVariable];
                }

                // multiply the table values of the potentials
                multiplicationResult = constantFactor;
                for (int i = 0; i < numProperPotentials; i++) {
                    multiplicationResult = multiplicationResult * tables[i][currentPositions[i]];
                }

                // update the accumulator (for this inner iteration)
                if (multiplicationResult > (accumulator + maxRoundErrorAllowed)) {
                    choice.setValue(innerIteration);
                    accumulator = multiplicationResult;
                } else {
                    if ((multiplicationResult < (accumulator + maxRoundErrorAllowed))
                            && (multiplicationResult >= (accumulator - maxRoundErrorAllowed))) {
                        choice.addValue(innerIteration);
                    }
                }
                // accumulator =
                // operator.combine(accumlator,multiplicationResult);

            } // end of inner iteration

            // when eliminationSize == 0 there is a multiplication without
            // maximization but we must find the next configuration
            if (outerIteration < resultSize - 1) {
                // find the next configuration and the index of the
                // increased variable
                for (int j = 0; j < unionCoordinate.length; j++) {
                    unionCoordinate[j]++;
                    if (unionCoordinate[j] < unionDimensions[j]) {
                        increasedVariable = j;
                        break;
                    }
                    unionCoordinate[j] = 0;
                }

                // update the positions of the potentials we are multiplying
                for (int i = 0; i < numProperPotentials; i++) {
                    currentPositions[i] += accumulatedOffsets[i][increasedVariable];
                }
            }

            resultingPotential.values[outerIteration] = accumulator;
            gResult.elementTable.add(choice);

        } // end of outer iteration

        Object[] resultPotentials = { resultingPotential, gResult };
        return resultPotentials;
    }

    /**
     * @param arrayListPotentials
     * @return true if there is utility potential in a list of potentials
     */
    public static boolean isThereAUtilityPotential(List<TablePotential> arrayListPotentials) {
        boolean isThere = false;
        for (int i = 0; (i < arrayListPotentials.size()) && !isThere; i++) {
            isThere = arrayListPotentials.get(i).getPotentialRole() == PotentialRole.UTILITY;
        }
        return isThere;
    }

    /**
     * @param tablePotentials
     *            <code>ArrayList</code> of <code>TablePotential</code>s.
     * @param fsVariablesToKeep
     *            <code>ArrayList</code> of <code>Variable</code>s.
     * @param fsVariableToMaximize
     *            <code>Variable</code>.
     * @return Two potentials: 1) a <code>Potential</code> resulting of
     *         multiplication and maximization of
     *         <code>variableToMaximize</code> and 2) a
     *         <code>TablePotential</code> with the mass probability 1.0
     *         uniformly distributed among the maximizing states of
     *         <code>variableToMaximize</code> in each configuration; this is
     *         typically a policy of a decision.
     */
    public static TablePotential[] multiplyAndMaximizeUniformly(List<TablePotential> tablePotentials,
            List<Variable> variablesToKeep,
            Variable variableToMaximize) {
        List<TablePotential> potentials = tablePotentials;

        PotentialRole roleResult = (isThereAUtilityPotential(tablePotentials)) ? PotentialRole.UTILITY
                : PotentialRole.CONDITIONAL_PROBABILITY;

        TablePotential resultingPotential = new TablePotential(variablesToKeep, roleResult);

        List<Variable> variablesPolicy = new ArrayList<Variable>();
        variablesPolicy.add(variableToMaximize);
        variablesPolicy.addAll(variablesToKeep);

        TablePotential policy = new TablePotential(variablesPolicy,
                PotentialRole.CONDITIONAL_PROBABILITY);

        // Constant potentials are those that do not depend on any variables.
        // The product of all the constant potentials is the constant factor.
        double constantFactor = 1.0;
        // Non constant potentials are proper potentials.
        List<TablePotential> properPotentials = new ArrayList<TablePotential>();
        for (Potential potential : potentials) {
            if (potential.getNumVariables() != 0) {
                properPotentials.add((TablePotential) potential);
            } else {
                constantFactor *= ((TablePotential) potential).values[((TablePotential) potential).getInitialPosition()];
            }
        }

        int numProperPotentials = properPotentials.size();

        if (numProperPotentials == 0) {
            resultingPotential.values[0] = constantFactor;
            return new TablePotential[] { resultingPotential, policy };
        }

        // variables in the resulting potential
        List<Variable> unionVariables = new ArrayList<Variable>();
        unionVariables.add((Variable) variableToMaximize);
        unionVariables.addAll(variablesToKeep);
        int numUnionVariables = unionVariables.size();

        // current coordinate in the resulting potential
        int[] unionCoordinate = new int[numUnionVariables];
        int[] unionDimensions = TablePotential.calculateDimensions(unionVariables);

        // Defines some arrays for the proper potentials...
        double[][] tables = new double[numProperPotentials][];
        int[] initialPositions = new int[numProperPotentials];
        int[] currentPositions = new int[numProperPotentials];
        int[][] accumulatedOffsets = new int[numProperPotentials][];
        // ... and initializes them
        TablePotential unionPotential = new TablePotential(unionVariables, null);
        for (int i = 0; i < numProperPotentials; i++) {
            TablePotential potential = (TablePotential) properPotentials.get(i);
            tables[i] = potential.values;
            initialPositions[i] = potential.getInitialPosition();
            currentPositions[i] = initialPositions[i];
            accumulatedOffsets[i] = unionPotential
            // .getAccumulatedOffsets(potential.getOriginalVariables());
            .getAccumulatedOffsets(potential.getVariables());
        }

        // The result size is the product of the dimensions of the
        // variables to keeep
        int resultSize = resultingPotential.values.length;
        // The elimination size is the product of the dimensions of the
        // variables to eliminate
        int eliminationSize = 1;
        eliminationSize *= ((Variable) variableToMaximize).getNumStates();

        // Auxiliary variables for the nested loops
        double multiplicationResult; // product of the table values
        double accumulator; // in general, the sum or the maximum
        int increasedVariable = 0; // when computing the next configuration

        List<Integer> statesTies;
        
        // outer iterations correspond to the variables to keep
        for (int outerIteration = 0; outerIteration < resultSize; outerIteration++) {
            // Inner iterations correspond to the variables to eliminate
            // accumulator summarizes the result of all inner iterations

            // first inner iteration
            multiplicationResult = constantFactor;
            for (int i = 0; i < numProperPotentials; i++) {
                // multiply the numbers
                multiplicationResult *= tables[i][currentPositions[i]];
            }
            statesTies = new ArrayList<Integer>();
            statesTies.add(0);
            accumulator = multiplicationResult;

            // next inner iterations
            for (int innerIteration = 1; innerIteration < eliminationSize; innerIteration++) {

                // find the next configuration and the index of the
                // increased variable
                for (int j = 0; j < unionCoordinate.length; j++) {
                    unionCoordinate[j]++;
                    if (unionCoordinate[j] < unionDimensions[j]) {
                        increasedVariable = j;
                        break;
                    }
                    unionCoordinate[j] = 0;
                }

                // update the positions of the potentials we are multiplying
                for (int i = 0; i < numProperPotentials; i++) {
                    currentPositions[i] += accumulatedOffsets[i][increasedVariable];
                }

                // multiply the table values of the potentials
                multiplicationResult = constantFactor;
                for (int i = 0; i < numProperPotentials; i++) {
                    multiplicationResult = multiplicationResult * tables[i][currentPositions[i]];
                }

                // update the accumulator (for this inner iteration)
                Double diffWithAccumulator = multiplicationResult - accumulator;
                if (diffWithAccumulator > maxRoundErrorAllowed) {
                    statesTies = new ArrayList<Integer>();
                    statesTies.add(innerIteration);
                    accumulator = multiplicationResult;
                } else {
                    if (Math.abs(diffWithAccumulator) < maxRoundErrorAllowed) {
                        statesTies.add(innerIteration);
                    }
                }
                // accumulator =
                // operator.combine(accumlator,multiplicationResult);

            } // end of inner iteration

            // when eliminationSize == 0 there is a multiplication without
            // maximization but we must find the next configuration
            if (outerIteration < resultSize - 1) {
                // find the next configuration and the index of the
                // increased variable
                for (int j = 0; j < unionCoordinate.length; j++) {
                    unionCoordinate[j]++;
                    if (unionCoordinate[j] < unionDimensions[j]) {
                        increasedVariable = j;
                        break;
                    }
                    unionCoordinate[j] = 0;
                }

                // update the positions of the potentials we are multiplying
                for (int i = 0; i < numProperPotentials; i++) {
                    currentPositions[i] += accumulatedOffsets[i][increasedVariable];
                }
            }

            resultingPotential.values[outerIteration] = accumulator;
            assignProbabilityUniformlyInTies(policy,
                    variableToMaximize.getNumStates(),
                    statesTies,
                    resultingPotential.getConfiguration(outerIteration));
            // .elementTable.add(choice);

        } // end of outer iteration

        TablePotential[] resultPotentials = { resultingPotential, policy };
        return resultPotentials;
    }

    private static void assignProbabilityUniformlyInTies(TablePotential tp,
            int numStatesVariable,
            List<Integer> statesTies,
            int[] policyDomainConfiguration) {
        Double probTies;

        int numStatesTies = statesTies.size();
        probTies = 1.0 / numStatesTies;

        int lenghtPolicyDomainConfiguration = policyDomainConfiguration.length;
        int[] tPConfiguration = new int[lenghtPolicyDomainConfiguration + 1];
        for (int j = 0; j < lenghtPolicyDomainConfiguration; j++) {
            tPConfiguration[j + 1] = policyDomainConfiguration[j];
        }
        // Assign probabilities to states in tie and the other ones
        for (int i = 0; i < numStatesVariable; i++) {
            tPConfiguration[0] = i;
            int posTPConfiguration = tp.getPosition(tPConfiguration);
            double iProb = (statesTies.contains(i)) ? probTies : 0.0;
            tp.values[posTPConfiguration] = iProb;
        }

    }

    /**
     * @param potentialsVariable
     *            <code>ArrayList</code> of <code>Potential</code>s to multiply.
     * @param variableToMaximize
     *            <code>Variable</code>.
     * @return Two potentials: 1) a <code>Potential</code> resulting of
     *         multiplication and maximization of
     *         <code>variableToMaximize</code> and 2) a
     *         <code>GTablePotential</code> of <code>Choice</code> (same
     *         variables as preceding) with the value chosen for
     *         <code>variableToMaximize</code> in each configuration.
     */
    public static Object[] multiplyAndMaximize(List<Potential> potentialsVariable,
            Variable variableToMaximize) {
        // Use a HashSet to add the variables to avoid adding one variable more
        // than one time
        HashSet<Variable> addedVariables = new HashSet<Variable>();
        for (Potential potential : potentialsVariable) {
            addedVariables.addAll(potential.getVariables());
        }
        List<Variable> variablesToKeep = new ArrayList<Variable>(addedVariables);
        variablesToKeep.remove(variableToMaximize);
        return multiplyAndMaximize(potentialsVariable, variablesToKeep, variableToMaximize);
    }

    /**
     * @param potentialsVariable
     *            <code>ArrayList</code> of <code>Potential</code>s.
     * @param fsVariableToMaximize
     *            <code>Variable</code>.
     * @return Two potentials: 1) a <code>Potential</code> resulting of
     *         multiplication and maximization of
     *         <code>variableToMaximize</code> and 2) a
     *         <code>TablePotential</code> with the mass probability 1.0
     *         uniformly distributed among the maximizing states of
     *         <code>variableToMaximize</code> in each configuration; this is
     *         typically a policy of a decision.
     */
    public static TablePotential[] multiplyAndMaximizeUniformly(List<TablePotential> potentialsVariable,
            Variable variableToMaximize) {
        // Use a HashSet to add the variables to avoid adding one variable more
        // than one time
        HashSet<Variable> addedVariables = new HashSet<Variable>();
        for (TablePotential potential : potentialsVariable) {
            addedVariables.addAll(potential.getVariables());
        }
        List<Variable> variablesToKeep = new ArrayList<Variable>(addedVariables);
        variablesToKeep.remove(variableToMaximize);
        return multiplyAndMaximizeUniformly(potentialsVariable, variablesToKeep, variableToMaximize);
    }

    /**
     * @param potential
     *            one <code>TablePotential</code>.
     * @param variableToMaximize
     *            <code>Variable</code>.
     * @return Two potentials: 1) a <code>Potential</code> resulting of
     *         multiplication and maximization of
     *         <code>variableToMaximize</code> and 2) a
     *         <code>GTablePotential</code> of <code>Choice</code> (same
     *         variables as preceding) with the value chosen for
     *         <code>variableToMaximize</code> in each configuration.
     */
    public static Object[] maximize(Potential potential, Variable variableToMaximize) {
        List<Potential> potentialsVariable = new ArrayList<Potential>();
        potentialsVariable.add(potential);
        List<Variable> variablesToKeep = new ArrayList<Variable>(potential.getVariables());
        variablesToKeep.remove(variableToMaximize);
        return multiplyAndMaximize(potentialsVariable, variablesToKeep, variableToMaximize);
    }

    /**
     * Copy the potential received to another potential with the same variables
     * but with the order received in <code>otherVariables</code>
     * 
     * @param potential
     *            <code>TablePotential</code>
     * @param orderVariables
     *            <code>ArrayList</code> of <code>Variable</code>
     * @return The <code>TablePotential</code> generated
     * @argCondition <code>otherVariables</code> are the same variables than the
     *               variables of <code>potential</code>
     */
    public static TablePotential reorder(TablePotential potential, List<Variable> orderVariables) {
        TablePotential copyPotential = new TablePotential(orderVariables,
                potential.getPotentialRole());
        int[] accOffsets = potential.getAccumulatedOffsets(orderVariables);
        int[] potentialPositions = new int[potential.getNumVariables()];
        int[] potentialDimensions = potential.getDimensions();
        double[] tablePotential = potential.values;
        double[] tableCopyPotential = copyPotential.values;
        UncertainValue[] uncertainValues = null;
        UncertainValue[] copyUncertainValues = null;
        if (potential.isUncertain()) {
            uncertainValues = potential.uncertainValues;
            copyPotential.uncertainValues = new UncertainValue[potential.uncertainValues.length];
            copyUncertainValues = copyPotential.uncertainValues;
        }

        int copyTablePosition = 0;
        int numVariables = orderVariables.size();
        int incrementedVariable, i;
        for (i = 0; i < tablePotential.length - 1; i++) {
            tableCopyPotential[copyTablePosition] = tablePotential[i];
            if (potential.isUncertain()) {
                copyUncertainValues[copyTablePosition] = uncertainValues[i];
            }

            for (incrementedVariable = 0; incrementedVariable < numVariables; incrementedVariable++) {
                potentialPositions[incrementedVariable]++;
                if (potentialPositions[incrementedVariable] == potentialDimensions[incrementedVariable]) {
                    potentialPositions[incrementedVariable] = 0;
                } else {
                    break;
                }
            }
            copyTablePosition += accOffsets[incrementedVariable];
        }
        tableCopyPotential[copyTablePosition] = tablePotential[i];
        if (potential.isUncertain()) {
            copyUncertainValues[copyTablePosition] = uncertainValues[i];
        }
        if (potential.isUtility()) {
            copyPotential.setUtilityVariable(potential.getUtilityVariable());
        }
        copyPotential.properties = potential.properties;
        return copyPotential;
    }

    /**
     * Copy the potential received to another potential with the same variables
     * but with changes in the order of states in one of the variables
     * 
     * @param potential
     *            <code>TablePotential</code>
     * @param variable
     *            <code>VariableList</code> whose order of states has changed
     * @param newOrder
     *            array of <code>State</code>s in the new order
     * @return The <code>TablePotential</code> generated
     * */
    public static TablePotential reorder(TablePotential potential,
            Variable variable,
            State[] newOrder) {
        TablePotential copyPotential = (TablePotential) potential.copy();
        double[] tablePotential = potential.values;
        double[] tableCopyPotential = copyPotential.values;
        UncertainValue[] uncertainValues = null;
        UncertainValue[] copyUncertainValues = null;
        int[] displacements = new int[newOrder.length];
        List<Variable> variables = copyPotential.getVariables();
        int variableIndex = variables.indexOf(variable);
        int offset = copyPotential.getOffsets()[variableIndex];
        State[] oldOrder = variable.getStates();
        for(int i=0; i<newOrder.length; ++i)
        {
            displacements[i] = -1;
            int j=0;
            boolean found = false;
            while(!found)
            {
                if(oldOrder[i] == newOrder[j])
                {
                    displacements[i] = j-i;
                    found = true;
                }
                ++j;
            }
        }
        
        if (potential.isUncertain()) {
            uncertainValues = potential.uncertainValues;
            copyPotential.uncertainValues = new UncertainValue[potential.uncertainValues.length];
            copyUncertainValues = copyPotential.uncertainValues;
        }

        for (int i = 0; i < tablePotential.length; i++) {
            int indexOfState = (i / offset) % variable.getNumStates();
            int newIndex = i + (displacements[indexOfState % variable.getNumStates()] * offset);
            tableCopyPotential[newIndex] = tablePotential[i];
            if (potential.isUncertain()) {
                copyUncertainValues[newIndex] = uncertainValues[i];
            }
        }
        if (potential.isUtility()) {
            copyPotential.setUtilityVariable(potential.getUtilityVariable());
        }
        copyPotential.properties = potential.properties;
        return copyPotential;
    }
}
