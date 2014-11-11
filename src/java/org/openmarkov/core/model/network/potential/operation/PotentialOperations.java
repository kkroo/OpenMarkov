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
import java.util.List;

import org.openmarkov.core.exception.IllegalArgumentTypeException;
import org.openmarkov.core.exception.PotentialOperationException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;


/** The class <code>PotentialOperations</code> contains method for performing
  * basic operations in bayesian networks such as matrix multiplication,
  * marginalization, etc.
  * @author manuel
  * @author fjdiez
  * @version 1.0
  * @see openmarkov.networks.potentials.TablePotential
  * @since OpenMarkov 1.0 */
public class PotentialOperations {

//	public static long lines; // Only for test

	// Constructor
	/**  Don't let anyone instantiate this class. */
	private PotentialOperations() {
	}

	/** @param potential
	 * @param variablesOfInterest
	 * @throws PotentialOperationException */
	public static Potential marginalize(Potential potential,
	                                    List<Variable> variablesOfInterest) 
			throws PotentialOperationException {
	
		// Obtain parameters to invoke multiplyAndMarginalize
		// Union of the variables of the potential list
	    List<Variable> variables = potential.getVariables();
		
		// parameters correct type verification before calling right method
		if (!(potential instanceof TablePotential)) {
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
			   "marginalize can only manage potentials of type TablePotential");    						
		}
		if (!hasFiniteStates(variables)){
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
				"marginalize can only manage variables of type FSVariable");    						
		}
	
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
		potentials.add((TablePotential) potential);
	
		return DiscretePotentialOperations.multiplyAndMarginalize(
			potentials, variablesToKeep, variablesToEliminate);
	}

	/** @precondition variablesToKeep + variablesToEliminate =
	 * potential.getVariables()
	 * @precondition variablesToKeep
	 * @param potential that will be marginalized
	 * @param variablesToKeep
	 * @param variablesToEliminate
	 * @throws PotentialOperationException */
	public static Potential marginalize(
			Potential potential,
			List<Variable> variablesToKeep,
			List<Variable> variablesToEliminate) 
			throws PotentialOperationException {
	
		// params correct type verification before calling right method
		if (!(potential instanceof TablePotential)) {
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
			   "marginalize can only manage potentials of type TablePotential");    						
		}
		if (!hasFiniteStates(variablesToKeep) || !hasFiniteStates(variablesToEliminate)){
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
				"marginalize can only manage variables of type FSVariable");    						
		}
		
		List<TablePotential> potentials = new ArrayList<TablePotential>();
		potentials.add((TablePotential) potential);
	
		return DiscretePotentialOperations.multiplyAndMarginalize(
			potentials, variablesToKeep, variablesToEliminate);
	}

	/** @param potentials
	 * @param variablesToEliminate
	 * @throws PotentialOperationException */
    public static Potential multiplyAndEliminate (List<TablePotential> potentials,
                                                  List<Variable> variablesToEliminate)
        throws PotentialOperationException
    {	
        if (!hasFiniteStates (variablesToEliminate)) {
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
				"multiplyAndEliminate can only manage variables of type " + 
				"FSVariable");    						
		}		
		
		// Obtain parameters to invoke multiplyAndMarginalize
		// Union of the variables of the potential list
		List<Variable> variablesToKeep = AuxiliaryOperations.getUnionVariables(potentials);
		variablesToKeep.removeAll(variablesToEliminate);
	
		return DiscretePotentialOperations.multiplyAndMarginalize(
			potentials, variablesToKeep, variablesToEliminate);
	}

	/** @param potentials
	 * @param variableToEliminate
	 * @throws PotentialOperationException */
    public static Potential multiplyAndEliminate (List<TablePotential> potentials,
                                                  Variable variableToEliminate)
        throws PotentialOperationException
	{
		return multiplyAndEliminate(potentials, Arrays.asList(variableToEliminate));
	}

	/** @param potentials potentials array to multiply
	 * @return The multiplied potentials
	 * @throws PotentialOperationException */
	@SuppressWarnings("unchecked")
    public static Potential multiply(List<? extends Potential> potentials)
	        throws PotentialOperationException
	{	
    	// correct type verification of parameters before calling method.
		if (!AuxiliaryOperations.checkObjectsCollectionType(potentials, 
				TablePotential.class)) {
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
			   "newMultiply can only manage potentials of type TablePotential");
		}
		
		return DiscretePotentialOperations.multiply((List<TablePotential>) potentials);
	}

	/** @param potentials potentials array to multiply
	 * @param variablesOfInterest Set of variables that must be kept (although 
	 * this set may contain some variables that are not in any potential)
	 * <code>potentials</code>
	 * @return The multiplied potentials
	 * @throws PotentialOperationException */
    public static Potential multiplyAndMarginalize (List<TablePotential> potentials,
                                                    List<Variable> variablesOfInterest)
        throws PotentialOperationException
    {	
		// Obtain parameters to invoke multiplyAndMarginalize
		// Union of the variables of the potential list
		List<Variable> unionVariables = 
			AuxiliaryOperations.getUnionVariables(potentials);
	
		// params correct type verification before calling right method
		if (!AuxiliaryOperations.checkObjectsCollectionType(
				potentials,	TablePotential.class)) {
			throw new IllegalArgumentTypeException("Unsupported operation: " +
				"multiplyAndMarginalize can only manage potentials of type " +
				"TablePotential");    			
		}
		
		if (!hasFiniteStates (unionVariables)) {
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
				"multiplyAndMarginalize can only manage variables of type " +
				"FSVariable");    						
		}
	
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
		
		return DiscretePotentialOperations.multiplyAndMarginalize(
			potentials, variablesToKeep, variablesToEliminate);
	}

    /** Multiplies several potentials and maximizes the result removing
     *  variables that does not belong to <code>variablesOfInterest</code> 
     * @param potentials potentials array to multiply
     * @param variablesOfInterest Set of variables that must be kept (although 
     *  this set may contain some variables that are not in any potential)
     * <code>potentials</code>
     * @return The multiplied potentials
     * @throws PotentialOperationException */
    public static Object[] multiplyAndMaximize(List<Potential> potentials, List<Variable> variablesOfInterest) 
            throws PotentialOperationException {
    
        // Obtain parameters to invoke multiplyAndMarginalize
        // Union of the variables of the potential list
        List<Variable> unionVariables = 
        	AuxiliaryOperations.getUnionVariables(potentials);
    
        // params correct type verification before calling right method
        if (!AuxiliaryOperations
        		.checkObjectsCollectionType(potentials, TablePotential.class)) {
            throw new IllegalArgumentTypeException("Unsupported operation: " + 
                "multiplyAndMarginalize can only manage potentials of type " + 
                "TablePotential");              
        }
        
        if (!AuxiliaryOperations.checkVariablesCollectionType(
        		unionVariables, VariableType.FINITE_STATES)) {
            throw new IllegalArgumentTypeException("Unsupported operation: " + 
                "multiplyAndMarginalize can only manage variables of type " +
                "FSVariable");                          
        }
    
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
        
        return DiscretePotentialOperations.multiplyAndMaximize(potentials, variablesToKeep, variablesToEliminate.get(0));
    }

	/** @param potentials array to multiply
	 * @param variablesToKeep The set of variables that will appear in the 
	 * resulting potential
	 * @param variablesToEliminate The set of variables eliminated by 
	 * marginalization (in general, by summing out or maximizing)
	 * @argCondition variablesToKeep and variablesToEliminate are a partition of
	 * the union of the variables of the potentials
	 * @return result the multiplied potentials
	 * @throws PotentialOperationException */
    public static Potential multiplyAndMarginalize (List<TablePotential> potentials,
                                                    List<Variable> variablesToKeep,
                                                    List<Variable> variablesToEliminate)
        throws PotentialOperationException
    {
		// For test purposes only:
        /* Pruebas.numPotentialOperations++; */
    	
    	// params correct type verification before calling right method
    	if (!AuxiliaryOperations
    			.checkObjectsCollectionType(potentials, TablePotential.class)) {
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
			   "newMultiply can only manage potentials of type TablePotential");
    	}
    	if (!hasFiniteStates(variablesToKeep) || !hasFiniteStates(variablesToEliminate)){
			throw new IllegalArgumentTypeException("Unsupported operation: " + 
				"newMultiply can only manage variables of type FSVariable");
    	}
    	
        return DiscretePotentialOperations.multiplyAndMarginalize(potentials, 
        	variablesToKeep, variablesToEliminate);
	}
    
    /**
     * Gets a uniform <code>Potential</code> object for the variable specified.
     *
     * @param probNet the <code>probNet</code> object that contains the variable
     * @param variable  the <code>Variable</code> object.
     * @param auxNodeType the nodeType of the probNode that match the variable.
     * 
     * @return a new UniformPotential. 
     */
	public static Potential getUniformPotential(ProbNet probNet, 
			Variable variable, NodeType auxNodeType) {

	    List<Variable> variables = new ArrayList<Variable>();
		variables.add(variable);
		for (Node node:probNet.getProbNode(variable).getNode().getParents()){
			variables.add(((ProbNode)node.getObject()).getVariable());
		}
		PotentialRole role = PotentialRole.CONDITIONAL_PROBABILITY;
		if (auxNodeType == NodeType.UTILITY) {
			variables.remove(0);
			role = PotentialRole.UTILITY;
		}else if (auxNodeType == NodeType.DECISION) {
			role = PotentialRole.DECISION;
		}
		UniformPotential uniformPotential = new UniformPotential(variables, role);
		if ( auxNodeType == NodeType.UTILITY ){
			uniformPotential.setUtilityVariable(variable);
		}
		return uniformPotential;
	}
	
	private static boolean hasFiniteStates(List<Variable> variables)
	{
        boolean result = true;
        int i = 0;
        while (result && i < variables.size ())
        {
            result = variables.get (i).getVariableType () == VariableType.DISCRETIZED
                     || variables.get (i).getVariableType () == VariableType.FINITE_STATES;
            ++i;
        }
        return result;	        
	}

}
