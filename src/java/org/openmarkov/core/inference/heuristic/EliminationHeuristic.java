/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.inference.heuristic;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.UsesVariable;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

/** Here we define the skeleton (an abstract class) of a heuristic algorithm 
 * that eliminates variables from a <code>MarkovNet</code> 
 * @author  manuel
 * @author  fjdiez
 * @version  1.0
 * @since  OpenMarkov 1.0 */
public abstract class EliminationHeuristic implements PNUndoableEditListener {
	
	// Attributes
	/** A pointer to the received <code>Graph</code>. */
	protected ProbNet probNet;
	
	/** A set of nodes that points to variables that are nor query variables nor
	 * observed variables. */
	protected List<List<Variable>> variablesToEliminate;

    /** A set of nodes that points to variables that are nor query variables nor
     * observed variables. */
    protected List<List<ProbNode>> nodesToEliminate;
	
	/** <code>Variable</code> that the heuristic propose to eliminate. */
	protected Variable variableProposed;
	
	// Constructor
	/** Variables will be eliminated from the last array to the first.
	 * @param <code>probNet</code> it's a network that can contain decisions. <code>ProbNet</code>
	 * @param variablesToEliminate. <code>ArrayList</code> of <code>ArrayList</code> of <code>Variable</code> */
	public EliminationHeuristic(ProbNet probNet, 
			List<List<Variable>> variablesToEliminate) {
// TODO Revisar todas las heuristicas que suponian que trabajaban
// con una copia 
		this.probNet = probNet;
		this.variablesToEliminate = variablesToEliminate;
		this.nodesToEliminate = new ArrayList<>(variablesToEliminate.size());
		for(List<Variable> variables : variablesToEliminate)
		{
		    List<ProbNode> probNodes = new ArrayList<>(variables.size());
		    for(Variable variable : variables)
		    {
		        probNodes.add(probNet.getProbNode(variable));
		    }
		    this.nodesToEliminate.add(probNodes);
		}
		variableProposed = null;
	}

	// Methods
	/** @return The <code>ProbNode</code> that the heuristic suggest to 
	 * eliminate. */
	public abstract Variable getVariableToDelete();
	
	public void undoableEditHappened(UndoableEditEvent event) {
		Variable removedVariable = getEventVariable(event);

		if (removedVariable != null) {
            int listIndex = -1;
            for (int j = variablesToEliminate.size() - 1; j >= 0 && listIndex == -1; j--) {
                if ((variablesToEliminate.get(j) != null) && variablesToEliminate.get(j).size() > 0) {
                    listIndex = j;
                }
            }
			if (listIndex > -1){
			    int index = variablesToEliminate.get(listIndex).indexOf(removedVariable);
			    variablesToEliminate.get(listIndex).remove(removedVariable);
			    if(variablesToEliminate.get(listIndex).isEmpty())
			    {
			        variablesToEliminate.remove(listIndex);
			    }
                if (index > -1) {
                    nodesToEliminate.get(listIndex).remove(index);
                    if (nodesToEliminate.get(listIndex).isEmpty()) {
                        nodesToEliminate.remove(listIndex);
                    }
                }
			}
			//Two lines commented by mluque
			//probNet.removePotentials(toEliminateNode);
			//probNet.removeProbNode(toEliminateNode);
		}
	}
	
	/** @param event <code>UndoableEditEvent</code>
	 * @return probNode (<code>ProbNode</code>) in the heuristic 
	 *   <code>ProbNet</code> that will be removed */
	protected Variable getEventVariable(UndoableEditEvent event) {
		Variable variable = null;
		UndoableEdit pNEdit = event.getEdit();
		
		if (pNEdit instanceof UsesVariable) {
			variable = ((UsesVariable)pNEdit).getVariable();
		}
		return variable;
	}
    
    /** @return The class name */
    public String toString() {
        return this.getClass().getName();
    }

	public int getNumVariablesToEliminate() {
		return variablesToEliminate.size();
	}

}
