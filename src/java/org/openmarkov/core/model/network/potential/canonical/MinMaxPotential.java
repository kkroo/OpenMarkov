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
import java.util.List;

import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;


public abstract class MinMaxPotential extends ICIPotential {

	// Constants
	/** Constant defined to manipulate sub-potential variables */
	protected final int CONDITIONED_VAR_POSITION = 0;
	
	/** Constant defined to manipulate sub-potential variables */
	protected final int CONDITIONING_VAR_POSITION = 1;
	
	// Attributes
	/** The pseudoVariable is used in the factorization
	 * of the noisy MAX/MIN proposed by D&iacute;ez and 
	 * Gal&aacute;n (2003).
	 * @frozen */
	protected Variable pseudoVariable;
	
	// Constructor
    public MinMaxPotential (ICIModelType model, List<Variable> variables)
    {
        // In principle, role will be "conditional probability"
        super (model, variables);
        Variable conditionedVariable = getConditionedVariable();
        String pseudoVariableName = "pseudo-" + conditionedVariable.getName ();
        // TODO Check no other variable exists with the same name
        pseudoVariable = new Variable (pseudoVariableName, conditionedVariable.getNumStates ());
	}
        
	public MinMaxPotential(MinMaxPotential potential) {
	    super(potential);
        Variable conditionedVariable = getConditionedVariable();
        String pseudoVariableName = "pseudo-" + conditionedVariable.getName ();
        // TODO Check no other variable exists with the same name
        pseudoVariable = new Variable (pseudoVariableName, conditionedVariable.getNumStates ());
    }

    // Methods
	/** @return Delta<sub>Y</sub> potential. <code>TablePotential</code> */
	protected abstract TablePotential getDeltaPotential();
	
	/** @return C<sub>y</sub><sup>x<sub>i</sub></sup> potential. 
	 *  <code>TablePotential</code> */
    protected abstract TablePotential getAccruedPotential (TablePotential potential);

	public List<TablePotential> getAccruedPotentials(List<TablePotential> subpotentials) {
		List<TablePotential> accruedPotentials = 
			new ArrayList<TablePotential>(subpotentials.size());
		for (TablePotential subpotential : subpotentials) {
			accruedPotentials.add(getAccruedPotential(subpotential));
		}
		return accruedPotentials;
	}
	
	/** @return Given a model in witch A->D and B->D, this method returns:
	 *  delta<sub>D,D'</sub>, C<sub>D'</sub><sup>A</sup>, 
	 *  C<sub>D'</sub><sup>B</sup>, C<sub>D</sub><sup>*</sup>.
	 *  <code>ArrayList</code> of <code>TablePotential</code>  */
    public List<TablePotential> getTablePotentials ()
    {
        List<TablePotential> iCIPotentials = new ArrayList<TablePotential> ();
        iCIPotentials.add (getDeltaPotential ());
        // subPotentials must be of sub-type TablePotential
        for (TablePotential potential : buildSubpotentialList())
        {
            iCIPotentials.add (getAccruedPotential (potential));
        }
        return iCIPotentials;
	}
	
	/** @return The accrued potentials plus the Delta potential, 
	 * all of them projected onto the evidence
	 * @throws WrongCriterionException 	
	 * 
	 */
    @Override
	public List<TablePotential> tableProject(EvidenceCase evidence, 
			InferenceOptions inferenceOptions,
            List<TablePotential> projectedPotentials) 
			throws WrongCriterionException {
        List<TablePotential> potentials = new ArrayList<TablePotential> ();
		for (TablePotential subPotential : buildSubpotentialList()) {
			potentials.addAll(subPotential.tableProject(evidence, null, projectedPotentials));
		}
		List<TablePotential> singleElementPotentialList = new ArrayList<TablePotential> ();
		singleElementPotentialList.add (DiscretePotentialOperations.multiplyAndMarginalize (potentials, variables));
		return singleElementPotentialList;
	}
	
	/**	@return The conditional probability table given by this potential
	 * 
	 */
	public TablePotential getCPT() {
		List<Variable> variablesToEliminate = Arrays.asList(pseudoVariable);
        return DiscretePotentialOperations.multiplyAndMarginalize (buildSubpotentialList(), variables,
                                                                   variablesToEliminate);
	}

	public Variable getPseudoVariable() {
		return pseudoVariable;
	}
	
    /**
     * There will be a potential for each link, plus the leak potential 
     * @return <code>ArrayList</code> of <code>TablePotential</code>. 
     * */
    protected List<TablePotential> buildSubpotentialList() {
        List<TablePotential> subpotentials = new ArrayList<TablePotential> ();

        //Noisy parents
        for(int i=1; i<variables.size(); ++i)
        {
            List<Variable> linkVariables = new ArrayList<Variable> ();
            linkVariables.add(variables.get (0)); // conditioned variable
            linkVariables.add(variables.get (i)); // parent i
            
            subpotentials.add (new TablePotential(linkVariables, PotentialRole.CONDITIONAL_PROBABILITY, getNoisyParameters(variables.get (i))));
        }

        // Leak parent
        if(getLeakyParameters() != null)
        {
            List<Variable> leakVariables = new ArrayList<Variable> ();
            leakVariables.add(variables.get(0)); // conditioned variable
            subpotentials.add (new TablePotential(leakVariables, PotentialRole.CONDITIONAL_PROBABILITY, getLeakyParameters()));
        }
        
        List<TablePotential> accruedPotentials = getAccruedPotentials(subpotentials);
        accruedPotentials.add(getDeltaPotential());
            
         return accruedPotentials;
    }	
	
	
    @Override
    public TablePotential getFFunctionPotential ()
    {
        // We won't be using it
        return null;
    }	
}
