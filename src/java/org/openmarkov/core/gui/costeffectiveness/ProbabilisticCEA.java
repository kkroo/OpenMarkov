/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.costeffectiveness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

public class ProbabilisticCEA extends CostEffectivenessAnalysis implements Runnable{

    private int numSimulations;
    private List<TablePotential> ceaResults;
    private volatile int progress;

    public ProbabilisticCEA(ProbNet probNet, EvidenceCase evidence, double costDiscountRate,
            double effectivenessDiscountRate, int numSlices, int numSimulations,
            Map<Variable, Double> initialValues, TransitionTime transitionTime) {
        super(probNet, evidence, costDiscountRate, effectivenessDiscountRate, numSlices,
                initialValues, transitionTime);
        this.numSimulations = numSimulations;
    }
    
    public ProbabilisticCEA(ProbNet probNet, EvidenceCase evidence, double costDiscountRate,
            double effectivenessDiscountRate, int numSlices, int numSimulations,
            TransitionTime transitionTime) {
        this(probNet, evidence, costDiscountRate, effectivenessDiscountRate, numSlices, numSimulations, 
                new HashMap<Variable, Double>(), transitionTime);
    }    
    
    public void run()
    {
        this.ceaResults = runProbabilisticAnalysis(expandedNetwork, evidence,  transitionTime, numSimulations);
        reorderVariablesInPotentials(ceaResults);
        this.globalUtility = calculateMeanUtility(ceaResults);
        this.interventions = buildProbabilisticInterventions(ceaResults);
        this.frontierInterventions = calculateFrontierInterventions(interventions);        
    }    

    private void reorderVariablesInPotentials(List<TablePotential> ceaResults) {
		for(int i=0; i<ceaResults.size(); ++i)
		{
            TablePotential reorderedPotential = reorderVariables(ceaResults.get(i));
            ceaResults.set(i, reorderedPotential);
		}
	}

	private TablePotential calculateMeanUtility(List<TablePotential> ceaResults) {
        TablePotential globalUtility = new TablePotential(this.globalUtility.getVariables(), PotentialRole.UTILITY);
        double[] values = globalUtility.values;
        for(TablePotential simulationResult : ceaResults)
        {
            for(int i=0; i < values.length; ++i)
            {
                values[i] += simulationResult.values[i];
            }
        }
        for(int i=0; i < values.length; ++i)
        {
            values[i] /= ceaResults.size();
        }
        return globalUtility;
    }

    private List<Intervention> buildProbabilisticInterventions(List<TablePotential> results) {

        List<String> interventionNames = new ArrayList<>();
        List<List<Double>> costs = new ArrayList<>();
        List<List<Double>> effectivenesses = new ArrayList<>();

        // Gather intervention names
        TablePotential exampleResult = reorderVariables(results.get(0));
        List<Variable> decisions = exampleResult.getVariables();
        int[] offsets = exampleResult.getOffsets();
        for (int i = 0; i < exampleResult.values.length; i += 2) {
            StringBuffer description = new StringBuffer();
            for (int j = 1; j < decisions.size(); ++j) {
                String decisionName = decisions.get(j).getName();
                String stateName = decisions.get(j).getStateName(
                        (i / offsets[j]) % decisions.get(j).getNumStates());
                description.append(decisionName + " = " + stateName + "; ");
            }
            interventionNames.add((description.length()>0)?description.toString():"Baseline");
            costs.add(new ArrayList<Double>(results.size()));
            effectivenesses.add(new ArrayList<Double>(results.size()));
        }

        // Gather data
        for(TablePotential simulationResult : results)
        {
            // Gather cost-effectiveness data
            double[] values = simulationResult.values;
            for (int i = 0; i*2 < values.length; i++) {
                costs.get(i).add(values[i*2]);
                effectivenesses.get(i).add(values[i*2 + 1]);
            }
        }
        
        interventions.clear();
        for(int i=0; i < interventionNames.size(); ++i)
        {
            interventions.add(new ProbabilisticIntervention(interventionNames.get(i), costs.get(i), effectivenesses.get(i)));
        }
        return interventions;
    }

    public int getNumSimulations() {
        return numSimulations;
    }    
    
    public int getProgress() {
        return progress;
    }

	private List<TablePotential> runProbabilisticAnalysis(ProbNet expandedNetwork, EvidenceCase evidence, TransitionTime transitionTime, int numSimulations)
    {
        progress = 0;
        List<TablePotential> results = new ArrayList<>(numSimulations);
        for (int i = 0; i < numSimulations; ++i)
        {
            sampleProbNet(expandedNetwork);
            //applyDiscountToUncertainValues(expandedNetwork, costDiscount, effectivenessDiscount);
            TablePotential simulationResult = runAnalysis(expandedNetwork, evidence, transitionTime);
            results.add(simulationResult);
            progress = i * 100/numSimulations;
        }
        progress = 100;
        return results;
    }
    
    private void applyDiscountToUncertainValues(ProbNet expandedNetwork,
            double costDiscount, double effectivenessDiscount) {
        
        // apply discount rate for all temporal utility nodes in the expanded
        // network
        List<ProbNode> utilityExpandedNodes = expandedNetwork.getProbNodes(NodeType.UTILITY);
        for (ProbNode utilityProbNode : utilityExpandedNodes) {
            Variable utilityVariable = utilityProbNode.getVariable();

            if (utilityVariable.isTemporal()) {
                Potential potential = utilityProbNode.getPotentials().get(0);
                int timeSlice = utilityVariable.getTimeSlice();
                String decisionCriterion = utilityVariable.getDecisionCriteria().getString();
                double discount = decisionCriterion.equalsIgnoreCase("cost") ? costDiscount
                        : effectivenessDiscount;
                applyDiscountToUncertainPotential(potential, timeSlice, discount);
            }
        }
    }
    
    public static void applyDiscountToUncertainPotential(Potential potential, int timeSlice, double discount) {
        double discountRate = 1.0 / (Math.pow((1.0 + (discount / 100.0)), timeSlice));
        if(potential instanceof TablePotential)
        {
            TablePotential tablePotential = ((TablePotential)potential);
            double[] potentialValues = tablePotential.getValues();
            if(tablePotential.getUncertaintyTable() != null)
            {
                UncertainValue[] uncertaintyTable = tablePotential.getUncertaintyTable();
                for(int j=0; j < uncertaintyTable.length; ++j)
                {
                    if(uncertaintyTable[j] != null)
                    {
                        potentialValues[j] = potentialValues[j] * discountRate;
                    }
                }
            }
        }else if (potential instanceof TreeADDPotential)
        {
            TreeADDPotential treeADD = (TreeADDPotential)potential; 
            for(TreeADDBranch branch : treeADD.getBranches())
            {
                applyDiscountToUncertainPotential(branch.getPotential(), timeSlice, discount);
            }
        }
    }
    public Map<Integer, double[]> calculateCEAC(int maxRatio)
    {
        if(ceaResults == null)
        {
            run();
        }
        
        Map<Integer, double[]> results = new LinkedHashMap<>();
        int numInterventions = interventions.size();
        int numSimulations = ((ProbabilisticIntervention)interventions.get(0)).getNumSimulations();
        for(int i=0; i<=1000; ++i)
        {
            int ratio = maxRatio * i / 1000; 
            // calculate CE probability for ratio
            double[] ceProbabilities = new double[numInterventions];
            // Initialize with zeros
            for(int k=0; k< numInterventions; ++k)
            {
                ceProbabilities[k] = 0;
            }               
            for(int j=0; j< numSimulations; ++j)
            {
                double maxNetBenefit = Double.NEGATIVE_INFINITY;
                int maxBenefitInterventionIndex = -1; 
                for(int k=0; k< numInterventions; ++k)
                {
                    ProbabilisticIntervention intervention = (ProbabilisticIntervention)interventions.get(k);
                    double netBenefit = ratio * intervention.getEffectivenesses().get(j) - intervention.getCosts().get(j);
                    if(netBenefit > maxNetBenefit)
                    {
                        maxNetBenefit = netBenefit;
                        maxBenefitInterventionIndex = k;
                    }
                }
                for(int k=0; k< numInterventions; ++k)
                {
                    ceProbabilities[k] += (maxBenefitInterventionIndex == k)? 1 : 0;
                }                
            }
            for(int k=0; k< numInterventions; ++k)
            {
                ceProbabilities[k] /= numSimulations;
            }                
            results.put(ratio, ceProbabilities);
        }
        return results;
    }
    
    public Map<Integer, Double> calculateEVPI(int maxRatio,
            int patientsPerAnnum, int lifetime, double discountRate)    {
        Map<Integer, Double> results = new LinkedHashMap<>();
        
        if(ceaResults == null)
        {
            run();
        }
        
        // Calculate effective population
        int effectivePopulation = 0;
        for(int i=0; i<lifetime; ++i)
        {
            effectivePopulation += patientsPerAnnum / Math.pow(1 + discountRate, i);
        }
        int numInterventions = interventions.size();
        int numSimulations = ((ProbabilisticIntervention)interventions.get(0)).getNumSimulations();
        double[][] netBenefits = new double[numInterventions][numSimulations];
        // Max benefit in each simulation
        double[] maxNetBenefits = new double[numSimulations];
        // Average net benefits for each intervention
        double[] avgNetBenefits = new double[numInterventions];

        for(int i=0; i<=1000; ++i)
        {
            // Calculate netBenefits and maxBenefit
            int ratio = maxRatio * i / 1000; 
            for(int j=0; j< numSimulations; ++j)
            {
                maxNetBenefits[j] = Double.NEGATIVE_INFINITY;
                for(int k=0; k< numInterventions; ++k)
                {
                    ProbabilisticIntervention intervention = (ProbabilisticIntervention)interventions.get(k);
                    double netBenefit = ratio * intervention.getEffectivenesses().get(j) - intervention.getCosts().get(j);
                    netBenefits[k][j] = netBenefit;
                    if(netBenefit > maxNetBenefits[j])
                    {
                        maxNetBenefits[j] = netBenefit;
                    }
                }
            }
            // Calculate average net benefit for each intervention
            for(int k=0; k< numInterventions; ++k)
            {
                avgNetBenefits[k] = 0;
                for(int j=0; j< numSimulations; ++j)
                {                
                    avgNetBenefits[k] += netBenefits[k][j];
                }
                avgNetBenefits[k] /= numSimulations;
            }
            // Calculate the maximum average net benefit across interventions
            double maxAverage = Double.NEGATIVE_INFINITY;
            for(int k=0; k< numInterventions; ++k)
            {
                if(maxAverage < avgNetBenefits[k])
                {
                    maxAverage = avgNetBenefits[k];
                }
            }
            // Calculate the average of maximum net benefits for each simulation
            double averageMax = 0;
            for(int j=0; j< numSimulations; ++j)
            {                
                averageMax += maxNetBenefits[j];
            }    
            averageMax /= numSimulations;
            double popEVPI = effectivePopulation * (averageMax - maxAverage);
            results.put(ratio, popEVPI);
        }
        return results;
    }    

    /**
     * @param simulationIndexVariable
     *            . <code>Variable</code>
     */
    private void sampleProbNet(ProbNet probNet) {
        for (ProbNode probNode : probNet.getProbNodes()) {
            probNode.samplePotentials();
        }
    }
}
