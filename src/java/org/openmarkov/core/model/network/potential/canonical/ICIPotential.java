/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.potential.canonical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

public abstract class ICIPotential extends Potential {

	/* Model type may be OR, causal MAX, AND, etc. */
	protected ICIModelType modelType;
	
	/* ICI family will be MAX (which includes OR, causal MAX...), MIN, etc. */
	protected ICIFamily family;
	
    /**
     * List of Z variables we are going to use in the canonical model
     */
    private HashMap<Variable, Variable> zVariables;
    
	/**
	 * Noisy parameters for the canonical model
	 * The leak parameter is in the last position
	 */
	private double[][] noisyParameters;
	
    /**
     * Leak parameters for the canonical model
     */
    private double[] leakyParameters;   
    
    private Variable leakyVariable = null;
    
	// Constructor
	/** @param variables. <code>ArrayList</code> of <code>Variable</code>
	 * @param model. <code>ICIModel</code> */
	public ICIPotential(ICIModelType modelType, List<Variable> variables) {
		// In principle, role will be "conditional probability"
		// and the first variable will be the conditioned variable
		super(variables, PotentialRole.CONDITIONAL_PROBABILITY);
		Variable conditionedVariable = getConditionedVariable();		
		this.modelType = modelType;
		this.family = modelType.getFamily();
		this.noisyParameters = getDefaultNoisyParameters();
		this.leakyParameters = getDefaultLeakyParameters (conditionedVariable.getNumStates ());
        zVariables = new HashMap<Variable, Variable> ();
        for(int i=1; i<variables.size (); ++i)
        {
            zVariables.put (variables.get (i), createZVariable (variables.get (i), conditionedVariable));
        }
        leakyVariable = new Variable (conditionedVariable.getName () + "-leaky", conditionedVariable.getStates ());
        
	}

    public ICIPotential(ICIPotential potential) {
        super(potential);
        this.modelType = potential.modelType;
        this.family = modelType.getFamily();
        this.noisyParameters = getDefaultNoisyParameters();
        Variable conditionedVariable = getConditionedVariable();        
        this.leakyParameters = getDefaultLeakyParameters (conditionedVariable.getNumStates ());
        zVariables = new HashMap<Variable, Variable> ();
        for(int i=1; i<variables.size (); ++i)
        {
            zVariables.put (variables.get (i), createZVariable (variables.get (i), conditionedVariable));
        }
        leakyVariable = new Variable (conditionedVariable.getName () + "-leaky", conditionedVariable.getStates ());
        for(int i=1; i<variables.size (); ++i)
        {
            setNoisyParameters(variables.get (i), potential.getNoisyParameters(variables.get (i)).clone ());
        }
        setLeakyParameters(potential.getLeakyParameters().clone ());
    }

    public double[][] getDefaultNoisyParameters()
    {
        double[][] noisyParameters = new double[variables.size()-1][];
       
        for (int i = 1; i < variables.size (); ++i)
        {
            Variable parent = variables.get (i); 
            noisyParameters[i-1] = initializeNoisyParameters(variables.get (0), parent);
        }
        return noisyParameters;
    }
	
    /**
     * Returns if an instance of a certain Potential type makes sense given the variables and the potential role 
     * @param variables
     * @param role
     */
    public static boolean validate (ProbNode probNode, List<Variable> variables, PotentialRole role)
    {
        return variables.size () > 1;
    }   
    /**
     * Initializes noisy parameters values
     * @param parent
     * @return
     */
    public double [] initializeNoisyParameters(Variable conditionedVariable, Variable parent) {
    	double[] probabilities = new double[conditionedVariable.getNumStates () * parent.getNumStates ()];
    	for (int j = 0; j < parent.getNumStates (); ++j)
        {
            for (int k = 0; k < conditionedVariable.getNumStates (); ++k)
            {
                probabilities[j * conditionedVariable.getNumStates () + k] = (k == j) ? 1.0 : 0.0;
            }
        }
    	return probabilities;
    }
    
	public abstract double[] getDefaultLeakyParameters(int numStates);
	
	// Methods
	/**
	 * Returns the f function potential
	 * @return TablePotential containing the f function
	 */
	public abstract TablePotential getFFunctionPotential ();

    /** @param evidenceCase. <code>EvidenceCase</code>
     * @return <code>ArrayList</code> of <code>Potential</code>*/
    // TODO This is the actual valid tableProject that should be used once the
    // bug in projectEvidence (assuming tableProject always returns a
    // one-element list of potentials) is solved
    public List<TablePotential> internalTableProject (EvidenceCase evidenceCase,
                                                   InferenceOptions inferenceOptions)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        List<TablePotential> projectedPotentials = new ArrayList<TablePotential> ();
        for (TablePotential subPotential : getSubpotentials ())
        {
            projectedPotentials.add (subPotential.tableProject (evidenceCase, null).get (0));
        }
        return projectedPotentials;
    }
    
    @Override
    /** @param evidenceCase. <code>EvidenceCase</code>
     * @return <code>ArrayList</code> of <code>Potential</code>*/
    public List<TablePotential> tableProject (EvidenceCase evidenceCase,
                                                   InferenceOptions inferenceOptions,
                                                   List<TablePotential> projectedPotentials)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        List<TablePotential> potentials = internalTableProject (evidenceCase, inferenceOptions);
        HashSet<Variable> variablesToEliminate = new HashSet<Variable> ();
        // Fill it with variables appearing in all potentials except this
        for (TablePotential tablePotential : potentials)
        {
            variablesToEliminate.addAll (tablePotential.getVariables ());
        }
        variablesToEliminate.removeAll (variables);
        List<TablePotential> singleElementPotentialList = new ArrayList<TablePotential> ();
        
        List<Variable> allVariables = new ArrayList<Variable>(variables);
        allVariables.addAll (variablesToEliminate);
        while (allVariables.size () > variables.size ())
        {
            Variable variableToEliminate = allVariables.get (allVariables.size () - 1);
            allVariables.remove (allVariables.size () - 1);
            List<TablePotential> relatedPotentials = new ArrayList<TablePotential> ();
            int i = 0;
            while (i < potentials.size ())
            {
                if (potentials.get (i).getVariables ().contains (variableToEliminate))
                {
                    // remove potentials related to the deleted variable
                    relatedPotentials.add (potentials.get (i));
                    potentials.remove (i);
                }
                else
                {
                    ++i;
                }
            }
            //add resulting potential
            potentials.add (0, DiscretePotentialOperations.multiplyAndMarginalize (relatedPotentials,
                                                                                allVariables));
        }
        singleElementPotentialList.add (DiscretePotentialOperations.multiplyAndMarginalize (potentials,
                                                                                            variables));
        return singleElementPotentialList;
    }    

    public double[] getNoisyParameters(Variable variable)
    {
        return noisyParameters[variables.indexOf(variable)-1];
    }	
	/**
	 * Sets the noisy parameters, i.e. <i>P(z<sub>i</sub>|x<sub>i</sub>)</i>
	 * @param parent parent variable (<i>X<sub>i</sub></i>) whose noisy parameters we want to set 
	 * @param parameters the noisy parameters. The length of the array must be the multiplication of the parent's and child's state number
	 */
	public void setNoisyParameters(Variable parent, double[] parameters)
	{
	    if(parameters.length != variables.get (0).getNumStates () * parent.getNumStates ())
	    {
            throw new IllegalArgumentException (
                                                "The length of the array must be the multiplication"
                                                        + " of the parent's and child's state number "
                                                        + variables.get (0).getNumStates ()
                                                        * parent.getNumStates () + " and is "
                                                        + parameters.length);
	    }
        if (!getVariables ().contains (parent))
        {
            throw new IllegalArgumentException("There is no variable " + parent + " in this ICI family.");
        }
        
        noisyParameters[variables.indexOf(parent)-1]= parameters;
	}
	
	/**
	 * There will be a potential for each link, plus the leak potential and the f function
	 * @return <code>ArrayList</code> of <code>TablePotential</code>. 
	 * */
	public List<TablePotential> getSubpotentials() {
	    List<TablePotential> subpotentials = new ArrayList<TablePotential> ();

	    // F function
	    subpotentials.add (getFFunctionPotential ());
	    
        //Noisy potentials
	    subpotentials.addAll (getNoisyPotentials ());

        // Leak potential
	    TablePotential leakyPotential = getLeakyPotential ();
	    if(leakyPotential != null)
	    {
	        subpotentials.add (leakyPotential);
	    }
	    
	     return subpotentials;
	}
	
    /**
     * There will be a potential for each link, plus the leak potential 
     * @return <code>ArrayList</code> of <code>TablePotential</code>. 
     * */
    public List<TablePotential> getNoisyPotentials() {
        List<TablePotential> noisyPotentials = new ArrayList<> ();

        //Noisy parents
        for(Variable parent: zVariables.keySet ())
        {
            List<Variable> linkVariables = Arrays.asList(zVariables.get (parent), parent);
            noisyPotentials.add (new TablePotential(linkVariables, PotentialRole.CONDITIONAL_PROBABILITY, noisyParameters[variables.indexOf(parent)-1]));
        }

         return noisyPotentials;
    }	
	
	/** @return Leak potential. <code>TablePotential</code> */
	public double[] getLeakyParameters() {
		return leakyParameters;
	}
	
	public TablePotential getLeakyPotential()
	{
	    TablePotential leakyPotential = null;
        if(this.leakyParameters != null)
        {
            ArrayList<Variable> leakVariables = new ArrayList<Variable> ();
            leakVariables.add(leakyVariable); // conditioned variable
            leakyPotential = new TablePotential(leakVariables, PotentialRole.CONDITIONAL_PROBABILITY, leakyParameters);
        }
        return leakyPotential;
	}
	
	/**
	 * Sets Leak parameters
	 * @param leakyParameters
	 */
    public void setLeakyParameters(double[] leakyParameters) {
        if(leakyParameters.length != variables.get (0).getNumStates () )
        {
            throw new IllegalArgumentException (
                                                "The length of the array must be the conditioned variable's state number "
                                                        + variables.get (0).getNumStates ()
                                                        + " and is " + leakyParameters.length);
        }
        
        this.leakyParameters = leakyParameters;
    }
    
    /**
     * Returns leaky variable
     * @return
     */
    protected Variable getLeakyVariable()
    {
        return this.leakyVariable;
    }
    
    /**
     * 
     * @return collection of Z variables
     */
    protected Collection<Variable> getAuxiliaryVariables()
    {
        return zVariables.values ();
    }    

	/** @return model. <code>ICIModel</code> */
	public ICIModelType getModelType() {
		return modelType;
	}

	/** @return model. <code>ICIModel</code> */
	public ICIFamily getFamily() {
		return modelType.getFamily();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(super.toString());
		buffer.append("\nFamily: " + family + ". Model: " + modelType);
		buffer.append("\nNumber of variables: " +  variables.size());
		buffer.append("\nVariables: ");
        buffer.append ("[");
        for (int i = 0; i < variables.size () - 1; i++)
        {
            buffer.append (variables.get (i) + ", ");
        }
        buffer.append (variables.get (variables.size () - 1) + "] ");
		buffer.append("\n");
		return buffer.toString();
	}
	
    @Override
    public boolean equals (Object arg0)
    {
		boolean isEqual = super.equals(arg0) && arg0 instanceof ICIPotential;
		if (isEqual) {
			ICIPotential otherPotential = (ICIPotential) arg0;
			if (isEqual) {
				for (int j = 1; j < variables.size(); ++j) {
					double[] values = getNoisyParameters(variables.get(j));
					Variable otherVariable = null;
					int k = 0;
					while (otherVariable == null
							&& k < otherPotential.variables.size()) {
						otherVariable = (otherPotential.variables.get(k)
								.getName().equals((variables.get(j).getName()))) ? otherPotential.variables
								.get(k) : null;
						++k;
					}
					double[] otherValues = otherPotential
							.getNoisyParameters(otherVariable);
					if (values.length == otherValues.length) {
						for (int i = 0; i < values.length; i++) {
							isEqual &= values[i] == otherValues[i];
						}
					} else {
						isEqual = false;
					}
				}

				double[] values = getLeakyParameters();
				double[] otherValues = otherPotential.getLeakyParameters();
				if (values.length == otherValues.length) {
					for (int i = 0; i < values.length; i++) {
						isEqual &= values[i] == otherValues[i];
					}
				} else {
					isEqual = false;
				}
			}
		}
		return isEqual;
    }

    @Override
    public void replaceVariable (int position, Variable variable)
    {
        Variable oldVariable = variables.get (position);
        variables.remove(position);
        variables.add (position, variable);
        
        // if position == 0, it is the conditioned variable, not a noisy one
        if(position > 0)
        {
	        zVariables.remove (oldVariable);
	        zVariables.put (variable, createZVariable(variables.get (0), variable));
        }
        
    }
    
    /**
     * Creates analogous Z variable for the parent variable
     * @param parent
     * @param child
     * @return
     */
    private Variable createZVariable(Variable parent, Variable child)
    {
        return new Variable ("z_" + parent.getName () + "_" + child.getName(),
                             child.getStates ());
    }
    
    @Override
    public int sample (Random randomGenerator, Map<Variable, Integer> sampledParents)
    {
        int[] iciSampledStates = new int[noisyParameters.length+1];
        int childNumStates = variables.get (0).getNumStates ();

        // Sample noisy 
        for(int i=1; i < variables.size(); ++i)
        {
            double[] probabilities = noisyParameters[i-1];
            int index = childNumStates * sampledParents.get (variables.get(i));
            int sampleIndex = 0;
            double randomPick = randomGenerator.nextDouble ();
            double accumulatedProbability = probabilities[index + sampleIndex];
            while(accumulatedProbability < randomPick)
            {
                ++sampleIndex;
                accumulatedProbability +=probabilities[index + sampleIndex];
            }
            iciSampledStates[i-1] = sampleIndex;
        }
        
        // Sample leaky
        int sampleIndex = 0;
        double randomPick = randomGenerator.nextDouble ();
        double accumulatedProbability = leakyParameters[sampleIndex];
        while(accumulatedProbability < randomPick)
        {
            ++sampleIndex;
            accumulatedProbability += leakyParameters[sampleIndex];
        }
        iciSampledStates[iciSampledStates.length-1] = sampleIndex;        
        // Sample child
        return computeFFunction(iciSampledStates);
    }         
    
    protected abstract int computeFFunction (int[] iciSampledStates);

    @Override    
    public double getProbability (HashMap<Variable, Integer> sampledStateIndexes)
    {
        int sampledState = sample(new Random (), sampledStateIndexes);
        
        return (sampledStateIndexes.get (variables.get (0)) == sampledState)? 1.0 : 0.0;
    }

    public void setNoisyPotentials (List<TablePotential> noisyPotentials)
    {
        for(int i=0; i <noisyPotentials.size(); ++i)
        {
            TablePotential noisyPotential = noisyPotentials.get(i);
            noisyParameters[variables.indexOf(noisyPotential.getVariable (0))-1] = noisyPotential.values;
        }
    }

    public TablePotential expand ()
    {
        TablePotential expandedPotential = getFFunctionPotential ();
        
        // Marginalize out noisy variables
        for(TablePotential noisyPotential: getNoisyPotentials ())
        {
            List<TablePotential> potentials = Arrays.asList(expandedPotential, noisyPotential);
            expandedPotential = (TablePotential)DiscretePotentialOperations.multiplyAndMarginalize (potentials, noisyPotential.getVariable (0));
        }
        
        // Marginalize out leaky variable
        List<TablePotential> potentials = Arrays.asList(expandedPotential, getLeakyPotential ());
        expandedPotential = (TablePotential)DiscretePotentialOperations.multiplyAndMarginalize (potentials, getLeakyVariable());
        
        return expandedPotential;
    }

}
