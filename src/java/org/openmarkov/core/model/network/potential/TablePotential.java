/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.potential;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.modelUncertainty.TablePotentialSampler;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

/**
 * A <code>TablePotential</code> is a type of relation with a list of
 * probabilistic nodes. All variables will be discrete in this class.
 * <p>
 * Attributes <code>dimensions</code> and <code>offsets</code> only make sense
 * when the number of variables is greater than 0. Please be careful to check it
 * when necessary.
 * 
 * @author marias
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0
 */
@RelationPotentialType(name = "Table", family = "")
public class TablePotential extends Potential implements Comparable<TablePotential> {
    // Attributes
    /** Dimensions (number of states) of the variables. */
    protected int[]                  dimensions;
    /** Offsets of the variables in the table that represents this potential. */
    protected int[]                  offsets;
    /**
     * Table storing the numerical values of the potential. This attribute is
     * public for efficiency and volatile for efficiency in concurrent
     * operations.
     */
    public volatile double[]         values;
    /**
     * Table storing the values of the potential for the sensitivity analysis.
     * This attribute is public for efficiency and volatile for efficiency in
     * concurrent operations.
     */
    public volatile UncertainValue[] uncertainValues;
    /**
     * Indicates the first configuration. In a new potential it is 0. In a
     * projected potential it may be different from 0.
     */
    private int                      initialPosition = 0;
    /**
     * Indicates the number of configurations in this potentials. Note that this
     * number can be less than <code>table.length</code> when the
     * <code>TablePotential</code> is a projection.
     */
    protected int                    tableSize;
    /**
     * This object has a function that returns the available memory. Used in the
     * constructor before creating the <code>table</code>
     */
    protected static Runtime         runtime         = Runtime.getRuntime();

    // Constructors
    /**
     * @param variables
     *            . <code>List</code> of <code>Variable</code> used to build the
     *            <code>TablePotential</code>.
     * @param role
     *            . <code>PotentialRole</code>
     */
    public TablePotential(List<Variable> variables, PotentialRole role) {
        super(variables, role);
        // this.originalVariables = this.variables;
        if (numVariables != 0) {
            dimensions = TablePotential.calculateDimensions(variables);
            offsets = TablePotential.calculateOffsets(dimensions);
            tableSize = computeTableSize(variables);
            try {
                values = new double[tableSize];
            } catch (NegativeArraySizeException e) {
                throw new OutOfMemoryError();
            }
            setUniform(); // Initializes the table as an uniform potential
        } else {// In this case the potential is a constant
            tableSize = 1;
            values = new double[tableSize];
            offsets = new int[0];
        }
        type = PotentialType.TABLE;
    }

    /**
     * For role utility
     * 
     * @param variables
     * @param role
     */
    public TablePotential(List<Variable> variables, PotentialRole role, Variable utilityVariable) {
        this(variables, role);
        this.utilityVariable = utilityVariable;
    }

    /**
     * @param variables
     *            . <code>ArrayList</code> of <code>Variable</code>
     * @param role
     *            . <code>PotentialRole</code>
     * @param table
     *            . <code>double[]</code>
     * @argCondition All variables must be discrete.
     */
    public TablePotential(List<Variable> variables, PotentialRole role, double[] table) {
        this(variables, role);
        this.values = table;
    }

    /**
     * @param role
     *            . <code>PotentialRole</code>
     * @param variables
     *            . <code>ArrayList</code> of <code>Variable</code>
     * @argCondition All variables must be discrete.
     */
    public TablePotential(PotentialRole role, Variable... variables) {
        this(toList(variables), role);
    }

    /**
     * Internal constructor used to create a projected potential.
     * 
     * @param variables
     *            . <code>ArrayList</code> of <code>Variable</code>
     * @param role
     *            . <code>PotentialRole</code>
     * @param table
     *            . <code>double[]</code>
     * @param initialPosition
     *            First position in <code>table</code> (used in projected
     *            potentials).
     * @param offsets
     *            of variables. <code>int[]</code>
     * @param dimensions
     *            . Number of states of each variable. <code>int[]</code>
     */
    private TablePotential(List<Variable> variables, PotentialRole role, double[] table,
            int initialPosition, int[] offsets, int[] dimensions) {
        super(variables, role);
        // this.originalVariables = this.variables;
        this.values = table;
        this.initialPosition = initialPosition;
        this.offsets = offsets;
        this.dimensions = dimensions;
        tableSize = computeTableSize(variables);
        type = PotentialType.TABLE;
    }

    public TablePotential(TablePotential potential) {
        super(potential);
        this.initialPosition = potential.getInitialPosition();
        this.offsets = potential.getOffsets();
        this.dimensions = potential.getDimensions();
        tableSize = potential.tableSize;
        values = potential.values.clone();
        uncertainValues = potential.uncertainValues;
        type = PotentialType.TABLE;
    }

    /**
     * Returns if an instance of a certain Potential type makes sense given the
     * variables and the potential role.
     * 
     * @param probNode
     *            . <code>ProbNode</code>
     * @param variables
     *            . <code>ArrayList</code> of <code>Variable</code>.
     * @param role
     *            . <code>PotentialRole</code>.
     */
    public static boolean validate(ProbNode probNode, List<Variable> variables, PotentialRole role) {
        boolean suitable = true;
        int i = 0;
        while (suitable && i < variables.size()) {
            suitable &= variables.get(i).getVariableType() == VariableType.FINITE_STATES
                    || variables.get(i).getVariableType() == VariableType.DISCRETIZED;
            ++i;
        }
        return suitable;
    }

    // Methods
    /**
     * @throws WrongCriterionException
     */
    public Potential removeVariable(Variable variable) {
        Potential newPotential = this;
        if (variables.contains(variable)) {
            Finding finding = new Finding(variable, 0);
            EvidenceCase evidenceCase = new EvidenceCase();
            try {
                evidenceCase.addFinding(finding);
                newPotential = tableProject(evidenceCase, null).get(0);
            } catch (InvalidStateException
                    | WrongCriterionException
                    | IncompatibleEvidenceException
                    | NonProjectablePotentialException e) {
                // Unreachable code
                e.printStackTrace();
            }
        } else {
            newPotential = this;
        }
        return newPotential;
    }

    /**
     * @param evidenceCase
     *            <code>EvidenceCase</code>
     * @return A <code>List</code> of <code>TablePotential</code>s containing
     *         only one element, which is a <code>ProjectedPotential</code>
     * @throws WrongCriterionException
     * @throws NoFindingException
     */
    public List<TablePotential> tableProject(EvidenceCase evidenceCase,
            InferenceOptions inferenceOptions,
            List<TablePotential> projectedPotentials)
            throws WrongCriterionException {
        // returned value
        boolean hasUncertainTable = (uncertainValues != null);
        List<TablePotential> newProjectedPotentials = new ArrayList<TablePotential>(1);
        List<Variable> unobservedVariables = new ArrayList<Variable>(variables);
        if (evidenceCase != null) {
            unobservedVariables.removeAll(evidenceCase.getVariables());
        }
        int numUnobservedVariables = unobservedVariables.size();
        TablePotential projectedPotential;
        if (numVariables == numUnobservedVariables) { // No projection.
            projectedPotential = this;
        } else {// Common part in constant potential and not constant potentials
            projectedPotential = new TablePotential(unobservedVariables, role);
            int length = projectedPotential.values.length;
            if (hasUncertainTable) {
                projectedPotential.setUncertaintyTable(new UncertainValue[length]);
            }
            // position (in this potential) of the first value
            // of the projected potential
            int firstPosition = 0;
            // auxiliary for the for loop
            int state;
            // iterate over the variables of this potential
            for (int i = 0; i < variables.size(); i++) {
                Variable variable = variables.get(i);
                if ((evidenceCase != null) && evidenceCase.contains(variable)) {
                    state = evidenceCase.getState(variable);
                    firstPosition += state * offsets[i];
                }
            }
            if (numUnobservedVariables == 0) {// Projection = constant potential
                projectedPotential.values[0] = values[firstPosition];
                if (hasUncertainTable) {
                    projectedPotential.uncertainValues[0] = uncertainValues[firstPosition];
                }
            } else { // Create projected potential
                     // Go trough this potential using accumulatedOffests
                int[] accumulatedOffsets = projectedPotential.getAccumulatedOffsets(variables);
                int numVariablesProjected = projectedPotential.getNumVariables();
                int[] projectedCoordinate = new int[numVariablesProjected];
                int[] projectedDimensions = new int[numVariablesProjected];
                for (int i = 0; i < numVariablesProjected; i++) {
                    projectedDimensions[i] = unobservedVariables.get(i).getNumStates();
                }
                // Copy configurations using the accumulated offsets algorithm
                for (int projectedPosition = 0; projectedPosition < length - 1; projectedPosition++) {
                    projectedPotential.values[projectedPosition] = values[firstPosition];
                    if (hasUncertainTable) {
                        projectedPotential.uncertainValues[projectedPosition] = uncertainValues[firstPosition];
                    }
                    // find the next configuration and the index of the
                    // increased variable
                    int increasedVariable = 0;
                    for (int j = 0; j < projectedCoordinate.length; j++) {
                        projectedCoordinate[j]++;
                        if (projectedCoordinate[j] < projectedDimensions[j]) {
                            increasedVariable = j;
                            break;
                        }
                        projectedCoordinate[j] = 0;
                    }
                    // update the positions of the potentials we are multiplying
                    firstPosition += accumulatedOffsets[increasedVariable];
                }
                int lastPositionProjected = length - 1;
                projectedPotential.values[lastPositionProjected] = values[firstPosition];
                if (hasUncertainTable) {
                    projectedPotential.uncertainValues[lastPositionProjected] = uncertainValues[firstPosition];
                }
            }
            // Common final part for constant and not constant potentials
            projectedPotential.setUtilityVariable(this.utilityVariable);
            projectedPotential.setUncertainTableToNullIfNullValues();
        }
        // discounts utilities
        if (role == PotentialRole.UTILITY
                && inferenceOptions != null
                && inferenceOptions.discountRate != 1.0
                && utilityVariable.isTemporal()) {
            int timeSlice = utilityVariable.getTimeSlice();
            double discount = Math.pow(inferenceOptions.discountRate, timeSlice);
            for (int i = 0; i < projectedPotential.values.length; i++) {
                projectedPotential.values[i] *= discount;
            }
        }
        // Cylindrical extension for utility potentials in the case of
        // multicriteria decision making
        if (role == PotentialRole.UTILITY && inferenceOptions != null) {
            Variable decisionCriteria = inferenceOptions.decisionCriteria;
            if (role == PotentialRole.UTILITY && decisionCriteria != null) {
                String criterion = utilityVariable.getDecisionCriteria().getString();
                List<TablePotential> potentials = new ArrayList<TablePotential>(2);
                potentials.add(projectedPotential);
                try {
                    potentials.add(decisionCriteria.deltaTablePotential(criterion));
                } catch (InvalidStateException e) {
                    throw new WrongCriterionException(utilityVariable, criterion, decisionCriteria);
                }
                projectedPotential = DiscretePotentialOperations.multiply(potentials);
            }
        }
        if (role == PotentialRole.UTILITY) {
            projectedPotential.setUtilityVariable(utilityVariable);
        }
        newProjectedPotentials.add(projectedPotential);
        return newProjectedPotentials;
    }

    private void setUncertainTableToNullIfNullValues() {
        boolean allNull;
        if (uncertainValues != null) {
            allNull = true;
            for (int i = 0; i < uncertainValues.length && allNull; i++) {
                allNull = (uncertainValues[i] == null);
            }
            if (allNull) {
                uncertainValues = null;
            }
        }
    }

    /**
     * The accumulated offset represents the increment (positive or negative) in
     * the corresponding position of the table when a variable is incremented
     * given an ordering of the variables of other potential.
     * <p>
     * <big><b> Accumulated Offsets example
     * <p>
     * </b></big> We have two potentials: Potential <b>Y</b> (b, d, a, c) and
     * Potential <b>X</b> (a, b, c). All variables are binary for simplicity.
     * <p>
     * <p>
     * <table border="2">
     * <caption ALIGN="top"> </caption>
     * <tr>
     * <td><b><center>Y</center></b></td>
     * <td><b>pos<sub>y</sub>(Y)</b></td>
     * <td><b><center>Y<sup>X</sup></center></b></td>
     * <td><b>pos<sub>X</sub>(Y<sup>X</sup>)</b></td>
     * <td><b>varToIncr(Y)</b></td>
     * <td><b>accOffset</b></td>
     * </tr>
     * <tr>
     * </tr>
     * <td>[b<sub>0</sub>,d<sub>0</sub>,a<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>0</center></td>
     * <td>[a<sub>0</sub>,b<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>0</center></td>
     * <td><center>0(B)</center></td>
     * <td><center>+2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>1</sub>,d<sub>0</sub>,a<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>1</center></td>
     * <td>[a<sub>0</sub>,b<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>2</center></td>
     * <td><center>1(D)</center></td>
     * <td><center>-2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>0</sub>,d<sub>1</sub>,a<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>2</center></td>
     * <td>[a<sub>0</sub>,b<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>0</center></td>
     * <td><center>0(B)</center></td>
     * <td><center>+2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>1</sub>,d<sub>1</sub>,a<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>3</center></td>
     * <td>[a<sub>0</sub>,b<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>2</center></td>
     * <td><center>2(A)</center></td>
     * <td><center>-1</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>0</sub>,d<sub>0</sub>,a<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>4</center></td>
     * <td>[a<sub>1</sub>,b<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>1</center></td>
     * <td><center>0(B)</center></td>
     * <td><center>+2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>1</sub>,d<sub>0</sub>,a<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>5</center></td>
     * <td>[a<sub>1</sub>,b<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>3</center></td>
     * <td><center>1(D)</center></td>
     * <td><center>-2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>0</sub>,d<sub>1</sub>,a<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>6</center></td>
     * <td>[a<sub>1</sub>,b<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>1</center></td>
     * <td><center>0(B)</center></td>
     * <td><center>+2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>1</sub>,d<sub>1</sub>,a<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>7</center></td>
     * <td>[a<sub>1</sub>,b<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>3</center></td>
     * <td><center>3(C)</center></td>
     * <td><center>+1</center></td>
     * <tr>
     * </tr>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * </table>
     * <p>
     * The order is imposed by the variables of <b>this</b> potential (<b>Y</b>)
     * <p>
     * 
     * @param otherVariables
     *            <code>ArrayList</code> of <code>Variable</code>s of another
     *            potential (in this example: <b>Y<sup>X</sup></b> = [a, b, c])
     * @return The accumulated offsets in an array of integers. In this example
     *         Accumulated offsets returns: [+2,-2,-1,+1]. Size = this
     *         <code>TablePotential</code> number of variables.
     */
    public int[] getAccumulatedOffsets(List<Variable> otherVariables) {
        int otherSize = otherVariables.size();
        int thisSize = variables.size();
        int[] accOffsetXY = new int[thisSize];
        if (otherSize == 0) {
            return accOffsetXY; // Initialized to 0
        }
        int[] ordering = new int[thisSize];
        for (int i = 0; i < ordering.length; i++) {
            ordering[i] = otherVariables.indexOf(variables.get(i));
        }
        // offsets of otherVariables
        int[] offsetX = new int[otherSize];
        offsetX[0] = 1;
        for (int i = 1; i < offsetX.length; i++) {
            offsetX[i] = offsetX[i - 1] * otherVariables.get(i - 1).getNumStates();
        }
        int[] offsetXY = new int[thisSize];
        int ordering_0 = ordering[0];
        if (ordering_0 == -1) {
            offsetXY[0] = 0;
        } else {
            offsetXY[0] = offsetX[ordering_0];
        }
        accOffsetXY[0] = offsetXY[0];
        int ordering_j;
        for (int j = 1; j < accOffsetXY.length; j++) {
            ordering_j = ordering[j];
            if (ordering_j == -1) {
                offsetXY[j] = 0;
            } else {
                offsetXY[j] = offsetX[ordering_j];
            }
            int numStatesYj_1 = ((Variable) variables.get(j - 1)).getNumStates();
            accOffsetXY[j] = accOffsetXY[j - 1] + offsetXY[j] - (numStatesYj_1 * offsetXY[j - 1]);
        }
        return accOffsetXY;
    }

    /**
     * Get accumulated offsets of a projected potential.
     * 
     * @param otherVariables
     *            . Actual set of variables in a projected potential.
     *            <code>ArrayList</code> of <code>Variable</code>
     * @param originalVariables
     *            . Complete set of variables in a projected potential.
     *            <code>ArrayList</code> of <code>Variable</code>
     * @return The accumulated offsets in an array of integers.
     * @argCondigion otherVariables is contained in originalVariables.
     * @argCondigion otherVariables and originalVariables have the same order.
     */
    public int[] getProjectedAccumulatedOffsets(List<Variable> otherVariables,
            List<Variable> originalVariables) {
        if (otherVariables == originalVariables) { // Not projected potential
            return getAccumulatedOffsets(otherVariables);
        }
        int[] originalAccOffsets = getAccumulatedOffsets(originalVariables);
        int[] accOffsets = new int[otherVariables.size()];
        int j = 0;
        for (int i = 0; i < originalVariables.size(); i++) {
            Variable variable = originalVariables.get(i);
            if (otherVariables.contains(variable)) {
                accOffsets[j++] = originalAccOffsets[i];
            }
        }
        return accOffsets;
    }

    /**
     * A configuration is a set of integers that represents a position in the
     * table.
     * <p>
     * <strong>Example</strong>: A potential <strong>T</strong> with two binary
     * variables: <strong>a</strong> and <strong>b</strong> has this possible
     * configurations and position in the table:
     * <p>
     * <TABLE BORDER=1 ALIGN=CENTER>
     * <tr>
     * <td><strong>a b position</strong></td>
     * </tr>
     * <tr>
     * <td>0 0 0</td>
     * </tr>
     * <tr>
     * <td>1 0 1</td>
     * </tr>
     * <tr>
     * <td>0 1 2</td>
     * </tr>
     * <tr>
     * <td>1 1 3</td>
     * </tr>
     * </TABLE>
     * <p>
     * <p>
     * 
     * @return The position in the table of the value corresponding to the given
     *         coordinates of the variables.
     *         <p>
     *         In the above example <code>T.getPosition([0,1])</code> will
     *         return: <strong>2</strong>
     * @argCondition coordinates.length = numVariables
     * @argCondition coordinates[i] >= 0 and coordinates[i] < dimensions[i].
     */
    public int getPosition(int[] coordinates) {
        int position = 0;
        for (int i = 0; i < numVariables; i++) {
            position += offsets[i] * coordinates[i];
        }
        return position;
    }

    /**
     * The accumulated offset represents the increment (positive or negative) in
     * the corresponding position of the table when a variable is incremented
     * given an ordering of the variables of other potential.
     * <p>
     * <big><b> Accumulated Offsets example
     * <p>
     * </b></big> We have two potentials: Potential <b>Y</b> (b, d, a, c) and
     * Potential <b>X</b> (a, b, c). All variables are binary for simplicity.
     * <p>
     * <p>
     * <table border="2">
     * <caption ALIGN="top"> </caption>
     * <tr>
     * <td><b><center>Y</center></b></td>
     * <td><b>pos<sub>y</sub>(Y)</b></td>
     * <td><b><center>Y<sup>X</sup></center></b></td>
     * <td><b>pos<sub>X</sub>(Y<sup>X</sup>)</b></td>
     * <td><b>varToIncr(Y)</b></td>
     * <td><b>accOffset</b></td>
     * </tr>
     * <tr>
     * </tr>
     * <td>[b<sub>0</sub>,d<sub>0</sub>,a<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>0</center></td>
     * <td>[a<sub>0</sub>,b<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>0</center></td>
     * <td><center>0(B)</center></td>
     * <td><center>+2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>1</sub>,d<sub>0</sub>,a<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>1</center></td>
     * <td>[a<sub>0</sub>,b<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>2</center></td>
     * <td><center>1(D)</center></td>
     * <td><center>-2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>0</sub>,d<sub>1</sub>,a<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>2</center></td>
     * <td>[a<sub>0</sub>,b<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>0</center></td>
     * <td><center>0(B)</center></td>
     * <td><center>+2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>1</sub>,d<sub>1</sub>,a<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>3</center></td>
     * <td>[a<sub>0</sub>,b<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>2</center></td>
     * <td><center>2(A)</center></td>
     * <td><center>-1</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>0</sub>,d<sub>0</sub>,a<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>4</center></td>
     * <td>[a<sub>1</sub>,b<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>1</center></td>
     * <td><center>0(B)</center></td>
     * <td><center>+2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>1</sub>,d<sub>0</sub>,a<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>5</center></td>
     * <td>[a<sub>1</sub>,b<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>3</center></td>
     * <td><center>1(D)</center></td>
     * <td><center>-2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>0</sub>,d<sub>1</sub>,a<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>6</center></td>
     * <td>[a<sub>1</sub>,b<sub>0</sub>,c<sub>0</sub>]</td>
     * <td><center>1</center></td>
     * <td><center>0(B)</center></td>
     * <td><center>+2</center></td>
     * <tr>
     * </tr>
     * <td>[b<sub>1</sub>,d<sub>1</sub>,a<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>7</center></td>
     * <td>[a<sub>1</sub>,b<sub>1</sub>,c<sub>0</sub>]</td>
     * <td><center>3</center></td>
     * <td><center>3(C)</center></td>
     * <td><center>+1</center></td>
     * <tr>
     * </tr>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * <td><center>...</center></td>
     * </table>
     * <p>
     * The order is imposed by the variables of <b>this</b> potential (<b>Y</b>)
     * <p>
     * 
     * @param otherVariables
     *            <code>ArrayList</code> of <code>Variable</code>s of another
     *            potential (in this example: <b>Y<sup>X</sup></b> = [a, b, c])
     * @return The accumulated offsets in an array of integers. In this example
     *         Accumulated offsets returns: [+2,-2,-1,+1]. Size = this
     *         <code>TablePotential</code> number of variables.
     */
    public static int[] getAccumulatedOffsets(List<Variable> variables,
            List<Variable> otherVariables) {
        int otherSize = otherVariables.size();
        int thisSize = variables.size();
        int[] accOffsetXY = new int[thisSize];
        if (otherSize == 0) {
            return accOffsetXY; // Initialized to 0
        }
        int[] ordering = new int[thisSize];
        for (int i = 0; i < ordering.length; i++) {
            ordering[i] = otherVariables.indexOf(variables.get(i));
        }
        // offsets of otherVariables
        int[] offsetX = new int[otherSize];
        offsetX[0] = 1;
        for (int i = 1; i < offsetX.length; i++) {
            offsetX[i] = offsetX[i - 1] * otherVariables.get(i - 1).getNumStates();
        }
        int[] offsetXY = new int[thisSize];
        int ordering_0 = ordering[0];
        if (ordering_0 == -1) {
            offsetXY[0] = 0;
        } else {
            offsetXY[0] = offsetX[ordering_0];
        }
        accOffsetXY[0] = offsetXY[0];
        int ordering_j;
        for (int j = 1; j < accOffsetXY.length; j++) {
            ordering_j = ordering[j];
            if (ordering_j == -1) {
                offsetXY[j] = 0;
            } else {
                offsetXY[j] = offsetX[ordering_j];
            }
            int numStatesYj_1 = ((Variable) variables.get(j - 1)).getNumStates();
            accOffsetXY[j] = accOffsetXY[j - 1] + offsetXY[j] - (numStatesYj_1 * offsetXY[j - 1]);
        }
        return accOffsetXY;
    }

    /**
     * This method is similar to getPosition(int []), but the input argument is
     * a configuration of variables which are not necessarily in the same order
     * that the variables in the potential
     * 
     * @param potential
     * @param configuration
     */
    private int getPosition(EvidenceCase configuration) {
        int[] coordinates;
        int sizeCoordinates;
        int pos;
        boolean isChanceVariable;
        int sizeEvi = configuration.getFindings().size();
        isChanceVariable = !(this.isUtility());
        sizeCoordinates = sizeEvi + (isChanceVariable ? 1 : 0);
        coordinates = new int[sizeCoordinates];
        List<Variable> varsTable = this.getVariables();
        int startLoop;
        if (isChanceVariable) {
            coordinates[0] = 0;
            startLoop = 1;
        } else {
            startLoop = 0;
        }
        for (int i = startLoop; i < sizeCoordinates; i++) {
            coordinates[i] = configuration.getFinding(varsTable.get(i)).getStateIndex();
        }
        pos = getPosition(coordinates);
        return pos;
    }

    /**
     * It returns the first position in the table of the consecutive cells where
     * all the values corresponding to a certain configuration are stored. It
     * assumes that configuration is a complete instantiation of the parents of
     * the variable associated to the table.
     * 
     * @param configuration
     * @return
     */
    public int getBasePosition(EvidenceCase configuration) {
        int[] coordinates;
        int sizeCoordinates;
        int pos;
        boolean isChanceVariable;
        isChanceVariable = !(this.isUtility());
        int sizeEvi = configuration.getFindings().size();
        sizeCoordinates = sizeEvi + (isChanceVariable ? 1 : 0);
        coordinates = new int[sizeCoordinates];
        List<Variable> varsTable = this.getVariables();
        int startLoop;
        if (isChanceVariable) {
            coordinates[0] = 0;
            startLoop = 1;
        } else {
            startLoop = 0;
        }
        for (int i = startLoop; i < sizeCoordinates; i++) {
            coordinates[i] = configuration.getFinding(varsTable.get(i)).getStateIndex();
        }
        pos = this.getPosition(coordinates);
        return pos;
    }

    /**
     * @param position
     *            in the table. <code>int</code>
     * @return The configuration corresponding to <code>position</code>
     *         <code>double</code>
     */
    public int[] getConfiguration(int position) {
        int[] coordinate = new int[offsets.length];
        for (int i = offsets.length - 1; i >= 0; i--) {
            coordinate[i] = position / offsets[i];
            position -= coordinate[i] * offsets[i];
        }
        return coordinate;
    }

    /**
     * Given a set of variables and a set of corresponding states indices, gets
     * the corresponding value in the table.
     * 
     * @argCondition All the variables in this potentials are included into the
     *               received variables.
     * @param variables
     *            . <code>ArrayList</code> of <code>Variable</code>
     * @param stateIndices
     *            . <code>int[]</code>
     * @return <code>double</code>
     */
    public double getValue(List<Variable> variables, int[] statesIndices) {
        int position = 0;
        for (int i = 0; i < variables.size(); i++) {
            Variable variable = variables.get(i);
            int indexVariable = this.variables.indexOf(variable);
            if (indexVariable != -1) {
                position += offsets[indexVariable] * statesIndices[i];
            }
        }
        return values[position];
    }

    /**
     * Given a set an EvidenceCase, gets the corresponding value in the table.
     * 
     * @argCondition All the variables in this potentials are included into the
     *               variables field of the evidence case (configuration).
     * @param configuration
     *            . <code>EvidenceCase</code>
     * @return <code>double</code>
     */
    public double getValue(EvidenceCase configuration) {
        int[] states;
        List<Variable> variables;
        int size;
        variables = configuration.getVariables();
        size = variables.size();
        states = new int[size];
        List<Finding> findings = configuration.getFindings();
        for (int i = 0; i < size; i++) {
            states[i] = findings.get(i).getStateIndex();
        }
        return getValue(variables, states);
    }

    /*******
     * Assigns a value at the table for the combination of a set of variables
     * and the corresponding state indices.
     * 
     * @param variables
     *            . <code>ArrayList</code> of <code>Variable</code>
     * @param statesIndexes
     *            . <code>int[]</code>
     * @param value
     */
    public void setValue(List<Variable> variables, int[] statesIndexes, double value) {
        int position = 0;
        for (int i = 0; i < variables.size(); i++) {
            Variable variable = variables.get(i);
            int indexVariable = this.variables.indexOf(variable);
            if (indexVariable != -1) {
                position += offsets[indexVariable] * statesIndexes[i];
            }
        }
        values[position] = value;
    }

    /**
     * @return <code>int[]</code>: The offsets of the variables in the table of
     *         values.
     * @consultation
     */
    public int[] getOffsets() {
        return offsets;
    }

    /**
     * @return <code>double[]</code>: Table containing the values of the
     *         potential.
     * @consultation
     */
    public double[] getValues() {
        return values;
    }

    /**
     * The dimensions of the new table have to be same that the current table
     * 
     * @return <code>double[]</code>: Table containing the values of the
     *         potential.
     * @consultation
     */
    public void setValues(double[] table) {
        this.values = table;
    }

    /**
     * Uncertain Table
     * 
     * @return
     */
    public UncertainValue[] getUncertaintyTable() {
        return uncertainValues;
    }

    /**
     * @consultation
     * @return dimensions of the variables in an array of <code>int[]</code>.
     */
    public int[] getDimensions() {
        return dimensions;
    }

    /**
     * This method is <code>static</code> because sometimes it can be used
     * without creating the <code>TablePotential</code>; for instance, to
     * estimate the amount of memory that would be necessary to actually create
     * the PotentialTable.
     * 
     * @param fsVariables
     *            <code>ArrayList</code> of <code>Variable</code>s.
     * @return array of <code>int[]</code> with the dimension of each variable.
     */
    public static int[] calculateDimensions(List<Variable> fsVariables) {
        int numVariables = 0;
        if (fsVariables != null) {
            numVariables = fsVariables.size();
        }
        int[] dimensions = new int[numVariables];
        for (int i = 0; i < numVariables; i++) {
            dimensions[i] = fsVariables.get(i).getNumStates();
        }
        return dimensions;
    }

    /**
     * This method is <code>static</code> because sometimes can be used outside
     * of a <code>TablePotential</code>.
     * 
     * @param dimensions
     *            of variables. Array of <code>int[]</code>.
     * @return array of <code>int[]</code> with the offset of each variable.
     */
    public static int[] calculateOffsets(int[] dimensions) {
        int[] offsets;
        int numVariables = dimensions.length;
        offsets = new int[numVariables];
        offsets[0] = 1;
        for (int i = 1; i < numVariables; i++) {
            offsets[i] = dimensions[i - 1] * offsets[i - 1];
        }
        return offsets;
    }

    /**
     * @return <code>initialPosition int</code>.
     * @consultation
     */
    public int getInitialPosition() {
        return initialPosition;
    }

    /**
     * Compares two <code>TablePotential</code>s using <code>tableSize</code> as
     * a criterion.
     * 
     * @param tablePotential
     *            <code>Object</code>.
     * @return <code>int</code>:
     *         <p>
     *         <0 if <code>this</code> table size is minor than the received
     *         potential
     *         <p>=
     *         0 if tables size is equal
     *         <p>>
     *         0 if <code>this</code> table size is greater than the table size
     *         of received potential.
     */
    public int compareTo(TablePotential other) {
        return this.tableSize - other.tableSize;
    }

    /**
     * @param configuration
     * @return true if and only if the potential contains uncertainty values for
     *         a certain configuration
     */
    public boolean hasUncertainty(EvidenceCase configuration) {
        boolean hasUncertainty;
        if (uncertainValues == null) {
            hasUncertainty = false;
        } else {
            int positionConfiguration;
            positionConfiguration = getPosition(configuration);
            hasUncertainty = uncertainValues[positionConfiguration] != null;
        }
        return hasUncertainty;
    }

    /**
     * @return <code>List</code> of <code>Variable</code>s.
     * @consultation
     */
    public List<Variable> getVariables() {
        return (variables != null) ? new ArrayList<Variable>(variables) : variables;
    }

    /** @return tableSize <code>int</code> */
    public int getTableSize() {
        return tableSize;
    }

    // TODO revisar para que no use tableProject(...)
    public Collection<Finding> getInducedFindings(EvidenceCase evidenceCase, double cycleLength)
            throws IncompatibleEvidenceException, WrongCriterionException {
        Collection<Finding> inducedFindings = new ArrayList<Finding>();
        if (role == PotentialRole.CONDITIONAL_PROBABILITY || role == PotentialRole.POLICY) {
            // Iterates over the list of parents. If some parent is not in the
            // evidence case, it is not possible to induce a new Finding
            for (int i = 1; i < variables.size(); i++) {
                if (!evidenceCase.contains(variables.get(i))) {
                    // returnS the empty list
                    return inducedFindings;
                }
            }
            // Checks if the projected potentials are deterministic
            try {
                TablePotential projectedPotential = tableProject(evidenceCase, null).get(0);
                if ((projectedPotential.getNumVariables() == 1)
                        && (projectedPotential.getPotentialType() == PotentialType.TABLE)) {
                    double[] table = ((TablePotential) projectedPotential).values;
                    int zeros = 0;
                    int position = 0;
                    for (int i = 0; i < table.length; i++) {
                        if (table[i] == 0.0) {
                            zeros++;
                        } else {
                            position = i;
                        }
                    }
                    if (zeros == (table.length - 1)) {// new finding
                        inducedFindings.add(new Finding(projectedPotential.getVariable(0), position));
                    }

                }
            } catch (NonProjectablePotentialException e) {
                e.printStackTrace();
            }
        }
        return inducedFindings;
    }

    /** Initialize the table as a uniform potential. */
    public void setUniform() {
        int numVariables;
        boolean setValue = false;
        Double value = 0.0;
        if (variables != null) {
            numVariables = variables.size();
            if ((numVariables > 0)
                    && noNumericVariables()
                    && ((role == PotentialRole.CONDITIONAL_PROBABILITY)
                            || (role == PotentialRole.POLICY)
                            || (role == PotentialRole.JOINT_PROBABILITY)
                            || (role == PotentialRole.UTILITY) || (role == PotentialRole.LINK_RESTRICTION))) {
                setValue = true;
                value = 0.0;
                switch (role) {
                case CONDITIONAL_PROBABILITY:
                    value = 1.0 / new Double(variables.get(0).getNumStates());
                    break;
                case POLICY:
                case JOINT_PROBABILITY:
                    value = 1.0;
                    for (Variable variable : variables) {
                        value *= variable.getNumStates();
                    }
                    value = 1 / value;
                    break;
                case LINK_RESTRICTION:
                    value = 1.0;
                    break;
                default:
                    // Do nothing
                    break;
                } // When role = UTILITY -> value = 0.0 (default)
                for (int i = 0; i < values.length; i++) {
                    values[i] = value;
                }
            } else if (numVariables == 0) {
                setValue = true;
                if (role == PotentialRole.JOINT_PROBABILITY) {
                    value = 1.0;
                } else {
                    value = 0.0;
                }
            }
            if (setValue) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = value;
                }
            }
        }
    }

    /** Overrides <code>toString</code> method. Mainly for test purposes */
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));
        // writes variables names
        StringBuffer buffer = new StringBuffer(super.toString());
        // Print configurations
        int valuesPosition = 0;
        if (buffer.length() < STRING_MAX_LENGTH) {
            if (variables.size() > 0) {
                buffer.append(" = {");
            } else {
                buffer.append((role == PotentialRole.UTILITY) ? " = " : " ");
            }
        }
        while ((buffer.length() < STRING_MAX_LENGTH) && (valuesPosition < values.length)) {
            buffer.append(formatter.format(values[valuesPosition++]));
            if ((valuesPosition < values.length) && (buffer.length() < (STRING_MAX_LENGTH - 2))) {
                buffer.append(", ");
            }
        }
        if (values.length != 1) {
            if (valuesPosition != values.length || variables.size() == 0) {
                buffer.append("...");
            }
            buffer.append("}");
        }
        return buffer.toString();
    }

    public String treeADDString() {
        if (role == PotentialRole.CONDITIONAL_PROBABILITY && numVariables == 1) {
            Variable firstVariable = variables.get(0);
            for (int i = 0; i < firstVariable.getNumStates(); i++) {
                if (values[i] == 1) {
                    return firstVariable.getName() + " = " + firstVariable.getStateName(i);
                }
            }
        }
        return toString();
    }

    /**
     * Calculates <code>tableSize</code> = product of dimensions of variables.
     * In projected potentials <code>tableSize</code> can be distinct that
     * <code>table.length</code>.
     */
    public static int computeTableSize(List<Variable> variables) {
        int tableSize = 1;
        for (Variable variable : variables) {
            tableSize *= variable.getNumStates();
        }
        return tableSize;
    }

    /**
     * @param uncertainTable
     */
    public void setUncertaintyTable(UncertainValue[] uncertainTable) {
        this.uncertainValues = uncertainTable;
    }

    /**
     * @param uncertainTable
     * @return true if the uncertain values are correct
     */
    public static boolean checkUncertainTable(List<UncertainValue> uncertainTable) {
        return true;
    }

    /**
     * Generates a sampled potential
     */
    public Potential sample() {
        Potential sampledPotential = this;
        if (uncertainValues != null) {
            TablePotentialSampler samplePotentialTable = new TablePotentialSampler();
            sampledPotential = samplePotentialTable.sample(this);
        }
        return sampledPotential;
    }

    @Override
    public boolean equals(Object arg0) {
        boolean isEqual = super.equals(arg0) && arg0 instanceof TablePotential;
        if (isEqual) {
            double[] otherValues = ((TablePotential) arg0).getValues();
            if (values.length == otherValues.length) {
                for (int i = 0; i < values.length; i++) {
                    isEqual &= values[i] == otherValues[i];
                }
            } else {
                isEqual = false;
            }
        }
        return isEqual;
    }

    @Override
    public Potential copy() {
        return new TablePotential(this);
    }

    @Override
    public int sample(Random randomGenerator, Map<Variable, Integer> sampledParents) {
        int index = 0;
        int sampleIndex = 0;
        // find index of first position for the given configuration
        for (int i = 1; i < variables.size(); ++i) {
            index += sampledParents.get(variables.get(i)) * offsets[i];
        }
        double random = randomGenerator.nextDouble();
        double accumulatedProbability = values[index + sampleIndex];
        while (random > accumulatedProbability
        // Make sure we don't go out of bounds even if the sum of probabilities
        // is smaller than one.
                && sampleIndex < variables.get(0).getNumStates() - 1) {
            ++sampleIndex;
            accumulatedProbability += values[index + sampleIndex];
        }
        return sampleIndex;
    }

    @Override
    public double getProbability(HashMap<Variable, Integer> sampledStateIndexes) {
        int index = 0;
        // find index of first position for the given configuration
        for (int i = 0; i < variables.size(); ++i) {
            index += sampledStateIndexes.get(variables.get(i)) * offsets[i];
        }
        return values[index];
    }

    public double getUtility(HashMap<Variable, Integer> sampledStateIndexes,
            HashMap<Variable, Double> utilities) {
        return getProbability(sampledStateIndexes);
    }

    @Override
    public Potential addVariable(Variable newVariable) {
        // creates the new potential
        List<Variable> newVariables = new ArrayList<Variable>(variables);
        newVariables.add(newVariable);
        TablePotential newPotential = new TablePotential(newVariables, role);
        newPotential.setUtilityVariable(utilityVariable);
        // assigns the values of the new potential
        int newVariableNumStates = newVariable.getNumStates();
        for (int i = 0; i < newVariableNumStates; i++) {
            for (int j = 0; j < values.length; j++) {
                newPotential.values[j + i * values.length] = values[j];
                // newPotential.uncertainValues[j + i * values.length] =
                // uncertainValues[j];
            }
        }
        return newPotential;
    }

    @Override
    public boolean isUncertain() {
        return (this.uncertainValues != null) ? true : false;
    }
}