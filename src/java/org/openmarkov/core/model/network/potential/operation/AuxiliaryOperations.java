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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;


/** Auxiliary methods for <code>DiscretePotentialOperations</code> and 
 * <code>PotentialOperations</code> classes */
public class AuxiliaryOperations {

	// Constructor
	/**  Don't let anyone instantiate this class. */
	private AuxiliaryOperations() {
	}

	// Methods
	/** A proper potential is a potential that is not a constant; as consequence
	 * it has a not empty set of variables and a table with more than one value.
	 * @param potentials An <code>ArrayList</code> of 
	 *   <code>TablePotential</code>s
	 * @return An <code>ArrayList</code> of <code>TablePotential</code>s without
	 *   the constant potentials. */
    public static List<TablePotential> getProperPotentials (List<TablePotential> potentials)
    {
		List<TablePotential> properPotentials =
			new ArrayList<TablePotential>();
		for (TablePotential potential : potentials) {
			if (potential.values.length > 1) {
				properPotentials.add(potential);
			}
		}
		return properPotentials;
	}

	/** @param collection A generic <code>Collection</code>
	 * @param classObject Class object (i.e. <code>TablePotential.class</code>)
	 * @return <code>true</code> if all the objects in the parameter 
	 *   <code>collection</code> belongs to the parameter type 
	 *   <code>classObject</code>. */
	public static boolean checkObjectsCollectionType(
			Collection<?> collection, Class<?> classObject) {
		for (Object object : collection) {
			if (object.getClass() != classObject) {
				return false;
			}
		}
		return true;
	}
	
	/** @param collection A <code>Collection</code> of <code>Variable</code>
	 * @param variableType <code>VariableType</code>)
	 * @return <code>true</code> if all the variables in the parameter 
	 *  <code>collection</code> belongs to <code>variableType</code>. */
	public static boolean checkVariablesCollectionType(
			Collection<Variable> collection, VariableType variableType) {
		for (Variable variable : collection) {
			if (variable.getVariableType() != variableType) {
				return false;
			}
		}
		return true;		
	}

	/** @param potentials <code>Collection</code> of <code>Potential</code>s
	 * @return <code>ArrayList</code> of <code>Variable</code>s. */
	public static List<Variable> getUnionVariables(List<? extends Potential> potentials) {
		
		Set<Variable> variables = new HashSet<>();
		for (Potential potential : potentials) {
			variables.addAll(potential.getVariables());
		}
	    return new ArrayList<>(variables);
	}

	/** @param allVariables. <code>ArrayList</code> of <code>Variable</code>s
	 * @param variablesNames. <code>String[]</code>s
	 * @return an <code>ArrayList</code> of <code>Variable</code>s with the
	 *   variables in <code>allVariables</code> whose names are contained in 
	 *   <code>selectedVariables</code>, in the same order they are stored in 
	 *   <code>variablesNames</code> */
    public static List<Variable> getVariables (List<Variable> allVariables, String[] variablesNames)
    {
        List<Variable> variables = new ArrayList<Variable> ();
        for (String name : variablesNames)
        {
            for (Variable variable : allVariables)
            {
                if (name.contentEquals (variable.getName ()))
                {
                    variables.add (variable);
                }
            }
        }
        return variables;
    }
}
