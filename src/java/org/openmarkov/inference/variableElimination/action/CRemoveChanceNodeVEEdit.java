package org.openmarkov.inference.variableElimination.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.inference.variableElimination.VariableElimination;
import org.openmarkov.inference.variableElimination.VariableElimination.InferencePurpose;
import org.openmarkov.inference.variableElimination.VariableElimination.InferenceState;



@SuppressWarnings("serial")
/** Removes a chance node in a variable elimination algorithm.
 * This Edit class is valid for Bayesian networks and influence diagrams.
 * */
public class CRemoveChanceNodeVEEdit extends CRemoveNodeVEEdit {
	
	
	public CRemoveChanceNodeVEEdit(ProbNet probNet,List<TablePotential> constantPotentials,InferencePurpose purpose, 
			Variable variableToDelete, VariableElimination varElimination,boolean isLastVariable,InferenceState inferenceState) {
		super(probNet,constantPotentials,purpose,variableToDelete,varElimination,isLastVariable,inferenceState);
	
	}

	
	public CRemoveChanceNodeVEEdit(ProbNet probNet,List<TablePotential> constantPotentials,InferencePurpose purpose, 
			Variable variableToDelete, VariableElimination varElimination,InferenceState inferenceState) {
		this(probNet,constantPotentials,purpose,variableToDelete,varElimination,false,inferenceState);
	
	}


	/** @return <code>String</code> */
	public String toString() {
		return new String("CRemoveChanceNodeVEEdit: " +	variable);
	}

	@Override
    protected List<TablePotential> marginalizeVariableFromPotentials(TablePotential probabilityPotential, List<TablePotential> utilityPotentials) {

	    List<TablePotential> marginalizedPotentials = null;
	    if(probabilityPotential != null)
	    {
	    	marginalizedPotentials = new ArrayList<>();
	    	if(!utilityPotentials.isEmpty())
	    	{
			    for(TablePotential utilityPotential: utilityPotentials)
			    {
			    	List<TablePotential> potentials = Arrays.asList(probabilityPotential, utilityPotential);
		            TablePotential marginalizedPotential = DiscretePotentialOperations.multiplyAndMarginalize(
		            		potentials, variable);
		            marginalizedPotential.setUtilityVariable(utilityPotential.getUtilityVariable());
		            marginalizedPotentials.add(marginalizedPotential);
			    }
	    	}else
	    	{
	            TablePotential marginalizedPotential = DiscretePotentialOperations.multiplyAndMarginalize(
	            		Arrays.asList(probabilityPotential), variable);
	            marginalizedPotentials.add(marginalizedPotential);
	    	}
	    }else
	    {
	    	marginalizedPotentials = new ArrayList<>(utilityPotentials);
	    }

        return marginalizedPotentials;
    }

	

}
