/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.potential;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;

/** Potential associated to supervalue node to indicate that the utility is a
 * product of the utilities of its parents.
 * @author marias
 * @author mkpalacio
 * @version 1.0 */
@RelationPotentialType(name="Product", family="Utility")
public class ProductPotential extends Potential {

	// Constructor
	/**
	 * @param variables
	 * @param parentsProbNodes
	 * @param role
	 */
	public ProductPotential(List<Variable> variables, PotentialRole role) {
		super(variables, role);
		type = PotentialType.PRODUCT;
	}
	
    public ProductPotential(ProductPotential potential) {
        super(potential);
    }	
    
	// Methods
    /** Returns if an instance of a certain Potential type makes sense given 
     * the variables and the potential role.
     * @param probNode. <code>ProbNode</code> 
     * @param variables. <code>ArrayList</code> of <code>Variable</code>.
     * @param role. <code>PotentialRole</code>. */
	public static boolean validate(ProbNode probNode, List<Variable> variables, 
			PotentialRole role) {
		boolean suitable = (role == PotentialRole.CONDITIONAL_PROBABILITY
				|| role == PotentialRole.POLICY) && variables.get(0).getVariableType() == VariableType.NUMERIC;
				
        return suitable || role == PotentialRole.UTILITY;
    }        
    

	// Methods
	@Override
	/** @return If none of the potential variables are included in the 
	 * <code>evidenceCase</code> variables returns itself, in other case, 
	 * returns a uniform potential with the potential variables minus the 
	 * <code>evidenceCase</code> variables.
	 * @param evidenceCase. <code>evidenceCase</code> */
	public List<TablePotential> tableProject(EvidenceCase evidenceCase,
			InferenceOptions inferenceOptions,
            List<TablePotential> projectedPotentials)
	throws NonProjectablePotentialException, WrongCriterionException {
		List<Variable> parentVariables = new ArrayList<>(variables);
		parentVariables.remove(getConditionedVariable());
		List<TablePotential> parentPotentials = new ArrayList<>();
		for(Variable parentVariable : parentVariables)
		{
			parentPotentials.add(findPotentialByVariable(parentVariable, projectedPotentials));
		}
		TablePotential productPotential = DiscretePotentialOperations.multiply(parentPotentials);
		productPotential.utilityVariable = utilityVariable;
		return Arrays.asList(productPotential);	}

    @Override
    public Potential copy ()
    {
        return new ProductPotential(this);
    }

	@Override
	public boolean isUncertain() {
		return false;
	}	

}

