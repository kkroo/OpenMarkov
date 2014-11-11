/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.modelUncertainty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * TablePotentialSampler generates samples of table potentials
 * @author manolo
 *
 */
public class TablePotentialSampler
{

    public TablePotentialSampler ()
    {
    }

    /**
     * @param simulationIndexVariable Variable indexing the number of
     *            simulation. The number of simulations performed is the number
     *            of states of this variable
     * @return A sampled potential table
     */
    public TablePotential sample (TablePotential inputTablePotential)
    {
        TablePotential sampledTablePotential = null;
        int inputTableSize;
        int[] indexesComplement = null;
        int[] indexesDirichlet = null;
        int[] indexesOther = null;
        ComplementFamily complementFamily = null;
        DirichletFamily dirFamily = null;
        FamilyDistribution otherFamily = null;
        List<Class<? extends ProbDensFunction>> functionTypes;
        functionTypes = new ArrayList<> ();
        functionTypes.add (ComplementFunction.class);
        functionTypes.add (DirichletFunction.class);
        List<UncertainValue> uncertainValues = null;
        double[] sampledConfigurationValues;
        int numStates;
        UncertainValue[] uTable = inputTablePotential.getUncertaintyTable ();
        double[] originalValues = inputTablePotential.getValues ();
        if (!(inputTablePotential.getUncertaintyTable () == null))
        {
            List<Variable> inputPotentialVariables = inputTablePotential.getVariables ();
            List<Variable> sampledPotentialVariables = new ArrayList<Variable> (inputPotentialVariables);
            sampledTablePotential = new TablePotential (sampledPotentialVariables,
                                                        inputTablePotential.getPotentialRole ());
            double[] sampledValues = sampledTablePotential.values;
            sampledTablePotential.setUncertaintyTable(inputTablePotential.getUncertaintyTable());
            if (!inputTablePotential.isUtility ())
            {// Probability potential
                numStates = inputPotentialVariables.get (0).getNumStates ();
            }
            else
            {// Utility potential
                numStates = 1;
            }
            sampledTablePotential.setUtilityVariable (inputTablePotential.getUtilityVariable ());
            sampledConfigurationValues = new double[numStates];
            // Number of configurations of the conditioning variables
            inputTableSize = inputTablePotential.getTableSize ();
            int numConfigurations = inputTableSize / numStates;
            boolean hasUncertainty;
            // iterates over the configurations
            for (int configurationIndex = 0; configurationIndex < numConfigurations; configurationIndex++)
            {
                int configurationBasePosition = numStates * configurationIndex;
                uncertainValues = getUncertainValuesChance (uTable, configurationBasePosition,
                                                            numStates);
                hasUncertainty = uncertainValues.get (0) != null;
                if (hasUncertainty)
                {
                    FamilyDistribution family = new FamilyDistribution (uncertainValues);
                    List<UncertainValue> familyList = family.family;
                    // calculates the indexes of the uncertain values for each
                    // group: Other, Dirichlet and Complement
                    indexesComplement = getIndexesUncertainValuesOfClass (familyList,
                                                                         ComplementFunction.class);
                    indexesDirichlet = getIndexesUncertainValuesOfClass (familyList,
                                                                        DirichletFunction.class);
                    indexesOther = getIndexesUncertainValuesNotOfClasses (familyList, functionTypes);
                    // Create the families of distributions
                    List<UncertainValue> complements = constructListFromIndexes (familyList,
                                                                                 indexesComplement);
                    List<UncertainValue> dirichlets = constructListFromIndexes (familyList,
                                                                                indexesDirichlet);
                    List<UncertainValue> others = constructListFromIndexes (familyList,
                                                                            indexesOther);
                    complementFamily = new ComplementFamily (complements);
                    dirFamily = new DirichletFamily (dirichlets);
                    otherFamily = new FamilyDistribution (others);
                    // Initialize the random seed and the random number
                    // generator in the Dirichlet family
                    // samples and places the results in the auxiliary
                    // vector 'sampledConfigurationValues'
                    sampledConfigurationValues = generateSample (otherFamily, dirFamily,
                                                                 complementFamily,
                                                                 indexesOther,
                                                                 indexesDirichlet,
                                                                 indexesComplement, numStates);
                    // copies the auxiliary them in the auxiliary vector
                    // 'sampledConfigurationValues'
                    for (int stateIndex = 0; stateIndex < numStates; stateIndex++)
                    {
                        sampledValues[configurationBasePosition + stateIndex] = sampledConfigurationValues[stateIndex];
                    }
                }
                else
                {
                    // takes the values from the original potential and places
                    // them in the auxiliary vector 'sampledConfigurationValues'
                    for (int stateIndex = 0; stateIndex < numStates; stateIndex++)
                    {
                        sampledValues[configurationBasePosition + stateIndex]  = originalValues[configurationBasePosition + stateIndex];
                    }
                }
            }
        }
        else
        {// There is no uncertainty for the input potential
            return inputTablePotential;
        }
        return sampledTablePotential;
    }

    private double[] generateSample (FamilyDistribution otherFamily,
                                     DirichletFamily dirFamily,
                                     ComplementFamily complementFamily,
                                     int[] indexesOther,
                                     int[] indexesDirichlet,
                                     int[] indexesComplement,
                                     int numStates)
    {
        Random randomGenerator = new XORShiftRandom();
        double[] sampleOther;
        double[] sampleDir;
        double massForComp;
        double[] sampledConfigurationValues = new double[numStates];
        // processes the uncertain values that can be sampled individually
        sampleOther = otherFamily.getSample (randomGenerator);
        placeInArray (sampledConfigurationValues, indexesOther, sampleOther);
        // processes Dirichlet
        sampleDir = dirFamily.getSample (randomGenerator);
        placeInArray (sampledConfigurationValues, indexesDirichlet, sampleDir);
        // Process complements
        massForComp = 1.0 - (Tools.sum (sampleOther));
        complementFamily.setProbMass (massForComp);
        double[] sampleComp = complementFamily.getSample ();
        placeInArray (sampledConfigurationValues, indexesComplement, sampleComp);
        return sampledConfigurationValues;
    }

    private List<UncertainValue> constructListFromIndexes (List<UncertainValue> arrayFamily,
                                                           int[] indComp)
    {
        List<UncertainValue> array = new ArrayList<UncertainValue> ();
        for (int i : indComp)
        {
            array.add (arrayFamily.get (i));
        }
        return array;
    }

    private static void placeInArray (double[] refValue, int[] indexes, double[] x)
    {
        for (int i = 0; i < indexes.length; i++)
        {
            refValue[indexes[i]] = x[i];
        }
    }

    private List<UncertainValue> getUncertainValuesChance (UncertainValue[] uTable,
                                                           int basePos,
                                                           int numStates)
    {
        List<UncertainValue> uv;
        uv = new ArrayList<UncertainValue> ();
        for (int i = 0; i < numStates; i++)
        {
            uv.add (uTable[basePos + i]);
        }
        return uv;
    }

    public static boolean hasUncertainValuesUtility (UncertainValue[] uTable, int basePosition)
    {
        return uTable[basePosition] != null;
    }

    /**
     * @param uncertainValues
     * @param types
     * @return
     */
    private static int[] getIndexesUncertainValuesOfClasses(List<UncertainValue> uncertainValues,
            List<Class<? extends ProbDensFunction>> types) {
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < uncertainValues.size(); i++) {
            UncertainValue uncertainValue = uncertainValues.get(i);
            ProbDensFunction probDensFunction = uncertainValue.getProbDensFunction();
            boolean isInTypes = false;
            for (int j = 0; (j < types.size()) && !isInTypes; j++) {
                isInTypes = types.get(j).isAssignableFrom(probDensFunction.getClass());
            }
            if (isInTypes) {
                indexes.add(i);
            }
        }
        int numIndexesOfTypes = indexes.size();
        int[] intIndexes = new int[numIndexesOfTypes];
        for (int i = 0; i < numIndexesOfTypes; i++) {
            intIndexes[i] = indexes.get(i);
        }
        return intIndexes;
    }
    
    /**
     * @param uncertainValues
     * @param types
     * @return
     */
    private static int[] getIndexesUncertainValuesNotOfClasses(List<UncertainValue> uncertainValues,
            List<Class<? extends ProbDensFunction>> types) {
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < uncertainValues.size(); i++) {
            UncertainValue uncertainValue = uncertainValues.get(i);
            ProbDensFunction probDensFunction = uncertainValue.getProbDensFunction();
            boolean isInTypes = false;
            for (int j = 0; (j < types.size()) && !isInTypes; j++) {
                isInTypes = types.get(j).isAssignableFrom(probDensFunction.getClass());
            }
            if (!isInTypes) {
                indexes.add(i);
            }
        }
        int numIndexesOfTypes = indexes.size();
        int[] intIndexes = new int[numIndexesOfTypes];
        for (int i = 0; i < numIndexesOfTypes; i++) {
            intIndexes[i] = indexes.get(i);
        }
        return intIndexes;
    }    

    public static int[] getIndexesUncertainValuesOfClass(List<UncertainValue> uncertainValues,
            Class<? extends ProbDensFunction> functionClass) {
        List<Class<? extends ProbDensFunction>> classes = new ArrayList<>();
        classes.add(functionClass);
        return getIndexesUncertainValuesOfClasses(uncertainValues, classes);
    }
}
