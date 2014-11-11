/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.inference.likelihoodWeighting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceAnnotation;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNetOperations;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.XORShiftRandom;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.TuningNetworkType;

/**
 * Likelihood weighting algorithm for bayesian networks.
 * @author ibermejo
 * @author fjdiez
 * @version 1.0
 */
@InferenceAnnotation(name = "LikelihoodWeighting")
public class LikelihoodWeighting extends InferenceAlgorithm
{
    private static final int DEFAULT_SAMPLE_SIZE = 10000;
    
    private int sampleSize = DEFAULT_SAMPLE_SIZE;
    private double accumulatedWeight = 0.0;
    private int positiveSampleCount = 0;
    
    public LikelihoodWeighting (ProbNet probNet)
        throws NotEvaluableNetworkException
    {
        super (probNet);
    }

    @Override
    public boolean isEvaluable (ProbNet probNet)
    {
        return (probNet.getNetworkType ().equals (BayesianNetworkType.getUniqueInstance ()) || 
                probNet.getNetworkType ().equals (TuningNetworkType.getUniqueInstance ()));
    }
    
    public static void checkEvaluability(ProbNet probNet) throws NotEvaluableNetworkException {
        if (!probNet.getNetworkType ().equals (BayesianNetworkType.getUniqueInstance ()) && 
                !probNet.getNetworkType ().equals (TuningNetworkType.getUniqueInstance ()))
        {
            throw new NotEvaluableNetworkException("");
        }
    }    

    @Override
    public HashMap<Variable, TablePotential> getProbsAndUtilities (List<Variable> variablesOfInterest)
        throws IncompatibleEvidenceException
    {
    	EvidenceCase evidence = getPostResolutionEvidence();
    	evidence.fuse(getPreResolutionEvidence(), true);
    	
        HashMap<Variable, TablePotential> probsAndUtilities;
        probsAndUtilities = new HashMap<Variable, TablePotential> ();

        if(probNet.getNetworkType ().equals (TuningNetworkType.getUniqueInstance ()))
        {
            setEvidenceInDecisionNodes(probNet, evidence);
        }
        
        // Build link restrictions list
        List<Potential> linkRestrictions = buildLinkRestrictionList(probNet);
        
        // List of variables to sample
        List<Variable> variablesToSample = getVariablesToSample (variablesOfInterest, evidence);

        // Order variables to sample ancestrally
        variablesToSample = ProbNetOperations.sortTopologically(probNet, variablesToSample);
        
        ArrayList<Potential> potentialsToSample = new ArrayList<Potential> ();
        for(Variable variable: variablesToSample)
        {
            potentialsToSample.addAll (probNet.getProbNode (variable).getPotentials ());
        }
        
        // Apply penalty for utility nodes without observed parents
        // TODO Remove this Hack ASAP
        if(probNet.additionalProperties.containsKey ("unobservedUtilityPenalty"))
        {
            int utilityPenalty = Integer.parseInt (probNet.additionalProperties.get ("unobservedUtilityPenalty").toString ());
            for(int i= 0; i < potentialsToSample.size (); ++i)
            {
                Potential potential = potentialsToSample.get (i);
                if(potential.getPotentialRole() == PotentialRole.UTILITY && !evidence.existsEvidence(potential.getVariables ()))
                {
                    if(potential instanceof TablePotential)
                    {
                        TablePotential potentialCopy = (TablePotential)potential.copy ();
                        double[] values = potentialCopy.values;
                        for(int j = 0; j < values.length; ++j)
                        {
                            values[j] /= utilityPenalty;
                        }
                        potentialsToSample.set (i, potentialCopy);
                    }
                }
            }       
        }
        
        // List of Variables of Evidence
        List<Variable> variablesOfEvidence = evidence.getVariables ();
        List<Potential> potentialsOfEvidence = new ArrayList<Potential> ();
        for(Variable variable: variablesOfEvidence)
        {
        	try
        	{
        		potentialsOfEvidence.addAll (probNet.getProbNode (variable).getPotentials ());
        	}catch(NullPointerException e)
        	{
        		throw new NullPointerException("Variable " + variable.getName() + " has no Potential");
        	}
        }

        List<int[]> parentIndexes = new ArrayList<int[]> ();
        for(Potential potential: potentialsOfEvidence)
        {
            int[] indexes = new int[potential.getVariables ().size ()-1];
            for(int j=1; j< potential.getVariables ().size(); ++j)
            {
                indexes[j-1] = variablesToSample.indexOf (potential.getVariable (j));
            }
            parentIndexes.add (indexes);
        }      
        
        HashMap<Variable, Integer> sampledStateIndexes = new HashMap<Variable, Integer> ();
        for(Variable variable:  evidence.getVariables ())
        {
            sampledStateIndexes.put (variable, evidence.getFinding (variable).getStateIndex ());
        }
        
        ArrayList<double[]> accumulatedProbabilities = new ArrayList<double[]> (variablesOfInterest.size ());
        for(Variable variable:  variablesOfInterest)
        {
            double[] accumulatedProbability = null;
            if(NodeType.UTILITY != probNet.getProbNode(variable).getNodeType())
            {
            	accumulatedProbability = new double[variable.getNumStates ()];
            }else
            {
            	accumulatedProbability = new double[1];
            }
            for(int i= 0; i < accumulatedProbability.length; ++i)
            {
                accumulatedProbability[i]=0.0;
            }
            accumulatedProbabilities.add (accumulatedProbability);
        }        

        // Loop through sampling until we reach sample limit
        Random randomGenerator = new XORShiftRandom();
        accumulatedWeight = 0.0;
        positiveSampleCount = 0;
        
        HashMap<Variable, Double> utilities = new HashMap<Variable, Double> ();
        for (int i = 0; i < sampleSize; ++i)
        {
            // Sample
            for(Potential potential: potentialsToSample)
            {
            	if(potential.getPotentialRole() != PotentialRole.UTILITY)
            	{
            		sampledStateIndexes.put (potential.getVariable (0),
                                         potential.sample (randomGenerator, sampledStateIndexes));
            	}else
            	{
            		utilities.put(potential.getUtilityVariable(), potential.getUtility(sampledStateIndexes, utilities));
            	}
            }
            // Weight sample according to likelihood
            double weight = 1.0;
            for(Potential potential: potentialsOfEvidence)
            {
                // Ignore potentials that belong to parentless nodes as they are constant across samples
                if(potential.getVariables ().size () > 1 || potential.getProbability (sampledStateIndexes) == 0)
                {
                    weight*= potential.getProbability (sampledStateIndexes);
                }
            }
            for(Potential potential: linkRestrictions)
            {
                weight*= potential.getProbability (sampledStateIndexes);
            }            
            for(int j = 0; j < variablesOfInterest.size (); j++)
            {
            	if(accumulatedProbabilities.get(j).length > 1)
            	{
            		accumulatedProbabilities.get (j)[sampledStateIndexes.get(variablesOfInterest.get (j))] += weight;
            	}else
            	{
            		accumulatedProbabilities.get (j)[0] += utilities.get(variablesOfInterest.get(j)) * weight; 
            	}
            }
            accumulatedWeight += weight;
            if(weight > 0)
                positiveSampleCount++;
        }
        try
        {
	        // Normalize
	        for (int i=0; i < variablesOfInterest.size (); ++i){
	        	if(accumulatedProbabilities.get(i).length > 1)
	        	{
		            TablePotential jointProbability = new TablePotential (PotentialRole.JOINT_PROBABILITY,
		                                                                  variablesOfInterest.get (i));
		            jointProbability.values = accumulatedProbabilities.get (i);
		            probsAndUtilities.put (variablesOfInterest.get (i),
		                                         DiscretePotentialOperations.normalize (jointProbability));
	        	}else
	        	{
	        		TablePotential utility = new TablePotential(new ArrayList<Variable>(),
							PotentialRole.UTILITY, variablesOfInterest.get (i));
	        		utility.values[0]= accumulatedProbabilities.get(i)[0] / accumulatedWeight;
	        		probsAndUtilities.put (variablesOfInterest.get (i), utility);
	        	}
	        }
        }catch(NormalizeNullVectorException e)
        {
        	throw new IncompatibleEvidenceException(e.getMessage());
        }      
        return probsAndUtilities;
    }

    @Override
    public HashMap<Variable, TablePotential> getProbsAndUtilities ()
        throws IncompatibleEvidenceException
    {
        List<Variable> variablesOfInterest = probNet.getVariables ();
        return getProbsAndUtilities(variablesOfInterest);
		
    }  

	@SuppressWarnings("restriction")
    @Override
	public TablePotential getGlobalUtility() throws IncompatibleEvidenceException {
        return null;
	}
	
    @Override
	public TablePotential getJointProbability(List<Variable> variables)
			throws IncompatibleEvidenceException {
        
        int[] dimensions = TablePotential.calculateDimensions (variables);
        int[] offsets = TablePotential.calculateOffsets (dimensions);
	    
	    
	    EvidenceCase evidence = getPostResolutionEvidence();
        evidence.fuse(getPreResolutionEvidence(), true);
        
        // Tuning Networks: Set evidence in all decision nodes
        if(probNet.getNetworkType ().equals (TuningNetworkType.getUniqueInstance ()))
        {
            setEvidenceInDecisionNodes(probNet, evidence);
        }
        
        // Build link restrictions list
        List<Potential> linkRestrictions = buildLinkRestrictionList(probNet);
        
        // List of variables to sample
        List<Variable> variablesToSample = getVariablesToSample (probNet.getVariables (NodeType.CHANCE), evidence); 

        // Order variables to sample ancestrally
        variablesToSample = ProbNetOperations.sortTopologically(probNet, variablesToSample);
        
        List<Potential> potentialsToSample = new ArrayList<Potential> ();
        for(Variable variable: variablesToSample)
        {
            potentialsToSample.addAll (probNet.getProbNode (variable).getPotentials ());
        }
        
        // List of Variables of Evidence
        List<Variable> variablesOfEvidence = evidence.getVariables ();
        List<Potential> potentialsOfEvidence = new ArrayList<Potential> ();
        for(Variable variable: variablesOfEvidence)
        {
            try
            {
                potentialsOfEvidence.addAll (probNet.getProbNode (variable).getPotentials ());
            }catch(NullPointerException e)
            {
                throw new NullPointerException("Variable " + variable.getName() + " has no Potential");
            }
        }

        List<int[]> parentIndexes = new ArrayList<int[]> ();
        for(Potential potential: potentialsOfEvidence)
        {
            int[] indexes = new int[potential.getVariables ().size ()-1];
            for(int j=1; j< potential.getVariables ().size(); ++j)
            {
                indexes[j-1] = variablesToSample.indexOf (potential.getVariable (j));
            }
            parentIndexes.add (indexes);
        }      
        
        HashMap<Variable, Integer> sampledStateIndexes = new HashMap<Variable, Integer> ();
        for(Variable variable:  evidence.getVariables ())
        {
            sampledStateIndexes.put (variable, evidence.getFinding (variable).getStateIndex ());
        }
        
        TablePotential jointProbability = new TablePotential (variables, PotentialRole.JOINT_PROBABILITY);
        accumulatedWeight = 0.0;
        positiveSampleCount = 0;
        
        // Loop through sampling until we reach sample limit
        Random randomGenerator = new Random ();
        for (int i = 0; i < sampleSize; ++i)
        {
            // Sample
            for(Potential potential: potentialsToSample)
            {
                sampledStateIndexes.put (potential.getVariable (0),
                                     potential.sample (randomGenerator, sampledStateIndexes));
            }
            // Weight sample according to likelihood
            double weight = 1.0;
            for(Potential potential: potentialsOfEvidence)
            {
                // Ignore potentials that belong to parentless nodes as they are constant across samples
                if(potential.getVariables ().size () > 1 || potential.getProbability (sampledStateIndexes) == 0)
                {
                    weight*= potential.getProbability (sampledStateIndexes);
                }
            }
            for(Potential potential: linkRestrictions)
            {
                weight*= potential.getProbability (sampledStateIndexes);
            }
            
            jointProbability.values[getSampleIndex(variables, offsets, sampledStateIndexes)] += weight;

            accumulatedWeight += weight;
            if(weight > 0)
                positiveSampleCount++;
            
        }
        try
        {
            // Normalize
            DiscretePotentialOperations.normalize (jointProbability);
        }catch(NormalizeNullVectorException e)
        {
            throw new IncompatibleEvidenceException(e.getMessage());
        }      
        return jointProbability;
   }
    
    public Map<Variable, TablePotential> getFamilyJointProbabilities()
            throws IncompatibleEvidenceException {
        
        Map<Variable, TablePotential> jointProbabilities = new HashMap<> ();
        
        EvidenceCase evidence = getPostResolutionEvidence();
        evidence.fuse(getPreResolutionEvidence(), true);
        
        // Tuning Networks: Set evidence in all decision nodes
        if(probNet.getNetworkType ().equals (TuningNetworkType.getUniqueInstance ()))
        {        
            setEvidenceInDecisionNodes(probNet, evidence);
        }
        
        // Build link restrictions list
        List<Potential> linkRestrictions = buildLinkRestrictionList(probNet);
        
        // List of variables to sample
        List<Variable> variablesToSample = getVariablesToSample (probNet.getVariables (NodeType.CHANCE), evidence);
        
        // Order variables to sample ancestrally
        variablesToSample = ProbNetOperations.sortTopologically(probNet, variablesToSample);
        
        List<Potential> potentialsToSample = new ArrayList<Potential> ();
        for(Variable variable: variablesToSample)
        {
            potentialsToSample.addAll (probNet.getProbNode (variable).getPotentials ());
        }
        
        // List of Variables of Evidence
        List<Variable> variablesOfEvidence = evidence.getVariables ();
        List<Potential> potentialsOfEvidence = new ArrayList<Potential> ();
        for(Variable variable: variablesOfEvidence)
        {
            try
            {
                potentialsOfEvidence.addAll (probNet.getProbNode (variable).getPotentials ());
            }catch(NullPointerException e)
            {
                throw new NullPointerException("Variable " + variable.getName() + " has no Potential");
            }
        }

        List<int[]> parentIndexes = new ArrayList<int[]> ();
        for(Potential potential: potentialsOfEvidence)
        {
            int[] indexes = new int[potential.getVariables ().size ()-1];
            for(int j=1; j< potential.getVariables ().size(); ++j)
            {
                indexes[j-1] = variablesToSample.indexOf (potential.getVariable (j));
            }
            parentIndexes.add (indexes);
        }      
        
        HashMap<Variable, Integer> sampledStateIndexes = new HashMap<Variable, Integer> ();
        for(Variable variable:  evidence.getVariables ())
        {
            sampledStateIndexes.put (variable, evidence.getFinding (variable).getStateIndex ());
        }
        
        Map<Potential, int[]> potentialOffsets = new HashMap<>();
        for(Potential potential: potentialsToSample)
        {
            TablePotential accruedProbability = new TablePotential (potential.getVariables (), PotentialRole.JOINT_PROBABILITY);
            for(int i=0; i < accruedProbability.values.length; ++i)
            {
                accruedProbability.values[i] = 0.0;
            }
            jointProbabilities.put (potential.getVariable (0), accruedProbability);
            int[] dimensions = TablePotential.calculateDimensions (potential.getVariables());
            int[] offsets = TablePotential.calculateOffsets (dimensions);
            
            potentialOffsets.put (potential, offsets);
        }        
        
        // Loop through sampling until we reach sample limit
        accumulatedWeight = 0.0;
        positiveSampleCount = 0;
        Random randomGenerator = new Random ();

        for (int i = 0; i < sampleSize; ++i)
        {
            // Sample
            for(Potential potential: potentialsToSample)
            {
                sampledStateIndexes.put (potential.getVariable (0),
                                     potential.sample (randomGenerator, sampledStateIndexes));
            }
            // Weight sample according to likelihood
            double weight = 1.0;
            for(Potential potential: potentialsOfEvidence)
            {
                // Ignore potentials that belong to parentless nodes as they are constant across samples
                if(potential.getVariables ().size () > 1 || potential.getProbability (sampledStateIndexes) == 0)
                {
                    weight*= potential.getProbability (sampledStateIndexes);
                }
            }
            for(Potential potential: linkRestrictions)
            {
                weight*= potential.getProbability (sampledStateIndexes);
            }
            
            for(Potential potential: potentialsToSample)
            {
                List<Variable> variables =  potential.getVariables();
                int[] offsets = potentialOffsets.get (potential);
                jointProbabilities.get (potential.getVariable (0)).values[getSampleIndex(variables, offsets, sampledStateIndexes)] += weight;
            }
            accumulatedWeight+=weight;
            if(weight > 0)
                positiveSampleCount++;
        }
        // Construct list of evidence potentials
        Map<Variable, TablePotential> evidencePotentials = new HashMap<> ();
        for(Finding finding : evidence.getFindings ())
        {
            Variable evidenceVariable = finding.getVariable ();
            TablePotential evidencePotential = new TablePotential (PotentialRole.JOINT_PROBABILITY, evidenceVariable);
            
            for(int i=0; i < evidenceVariable.getNumStates (); ++i)
            {
                evidencePotential.values[i] = (finding.getStateIndex () == i)? 1.0 : 0.0;
            }
            evidencePotentials.put(evidenceVariable, evidencePotential);
        }
        
        for(Potential potential: potentialsOfEvidence)
        {
            ArrayList<TablePotential> evidencePotentialList = new ArrayList<> ();
            evidencePotentialList.add ((TablePotential) potential);
            for(Variable evidenceVariable : potential.getVariables ())
            {
                if(evidencePotentials.containsKey (evidenceVariable))
                {
                    evidencePotentialList.add (evidencePotentials.get (evidenceVariable));
                }
            }
            TablePotential evidencePotential = DiscretePotentialOperations.multiply (evidencePotentialList);
            jointProbabilities.put (potential.getVariable (0), evidencePotential);
        }
        
        try
        {
            // Normalize
            for(TablePotential jointProbability: jointProbabilities.values ())
            {
                jointProbability.setPotentialRole (PotentialRole.JOINT_PROBABILITY);
                DiscretePotentialOperations.normalize (jointProbability);
            }        
        }catch(NormalizeNullVectorException e)
        {
            throw new IncompatibleEvidenceException(e.getMessage());
        }
        
        return jointProbabilities;
   }    

	private int getSampleIndex (List<Variable> variables, int[] offsets,
                                HashMap<Variable, Integer> sampledStateIndexes)
    {
        int position = 0;
        for (int i = 0; i < variables.size (); i++)
        {
            Variable variable = variables.get (i);
            int indexVariable = variables.indexOf (variable);
            if (indexVariable != -1)
            {
                position += offsets[indexVariable] * sampledStateIndexes.get (variable);
            }
        }
        return position;
    }

    /**
	 * @return the sampleSize
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * @param sampleSize the sampleSize to set
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	private void setEvidenceInDecisionNodes(ProbNet probNet, EvidenceCase evidence) throws IncompatibleEvidenceException
	{

        for (Variable variable : probNet.getVariables (NodeType.DECISION))
        {
            if (!evidence.contains (variable))
            {
                try
                {
                    evidence.addFinding (new Finding (variable, 1));
                }
                catch (InvalidStateException e)
                {
                    e.printStackTrace ();
                }
            }
        }
	}
	
	private List<Potential> buildLinkRestrictionList(ProbNet probNet)
	{
        List<Potential> linkRestrictions = new ArrayList<> ();
        for(Link link:  probNet.getGraph ().getLinks ())
        {
            if(link.hasRestrictions ())
            {
                linkRestrictions.add (link.getRestrictionsPotential ());
            }
        }
        return linkRestrictions;
	}
	
	private List<Variable> getVariablesToSample(List<Variable> variablesOfInterest, EvidenceCase evidence)
	{
        List<Variable> variablesToSample = new ArrayList<Variable>();

        for(Variable variable :  variablesOfInterest)
        {
            if(!evidence.contains (variable))
            {
                variablesToSample.add (variable);
            }
        }
        
        return variablesToSample;
	}
	
    /**
     * Returns the accruedWeight.
     * @return the accruedWeight.
     */
    public double getAccumulatedWeight ()
    {
        return accumulatedWeight;
    }	
    
    /**
     * Returns the accruedWeight.
     * @return the accruedWeight.
     */
    public double getPositiveSampleRatio ()
    {
        return positiveSampleCount/(double)sampleSize;
    }

	@Override
	public Potential getOptimizedPolicy(Variable decisionVariable)
			throws IncompatibleEvidenceException, UnexpectedInferenceException {
            return null;
	}

	@Override
	public Potential getExpectedUtilities(Variable decisionVariable)
			throws IncompatibleEvidenceException, UnexpectedInferenceException {
            return null;
	}       
}
