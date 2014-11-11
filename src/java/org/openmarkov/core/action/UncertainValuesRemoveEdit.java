/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.action;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * <code>UncertainValuesRemoveEdit</code> is an edit that allow us to removes the uncertain values
 * column for a certain configuration.
 * If all the values in the uncertain values are null after the removal then the uncertain value is set to null.
 * @version 1 23/06/11
 * @author mluque
 * 
 */

@SuppressWarnings("serial")
public class UncertainValuesRemoveEdit extends SimplePNEdit {
	
	private List<UncertainValue> oldUncertainColumn;

	private int basePosition;

	private ProbNode probNode;
	
	private boolean wasNullOldUncertainColumn;


	/**
	 * Creates a new <code>AddProbNodeEdit</code> with the network where the new
	 * new node will be added and basic information about it. 
	 * @param probNet the <code>ProbNet</code> where the new node will be added.
	 * @param newNodeName the name of the new node
	 * @param nodeType The new node type.
	 * @param cursorposition the position (coordinates X,Y) of the node.
	 *            
	 */
	public UncertainValuesRemoveEdit(ProbNode probNode, EvidenceCase 
			configuration){
		super(probNode.getProbNet());
		
		this.probNode = probNode;
		
		TablePotential potential = getPotential();
		
		TablePotential auxProjected = null;
		try {
			auxProjected = potential.tableProject(configuration, null).get(0);
		} catch (WrongCriterionException | NonProjectablePotentialException e) {
		
			e.printStackTrace();
		}
		
		UncertainValue[] auxUncertainTable = auxProjected.getUncertaintyTable();
		
		wasNullOldUncertainColumn = !hasUncertainValues(auxUncertainTable);
				
		UncertainValue[] oldUncertainColumnArray = (wasNullOldUncertainColumn?null:auxUncertainTable);
		
		oldUncertainColumn = new ArrayList<UncertainValue>();
		for (UncertainValue aux:oldUncertainColumnArray){
			oldUncertainColumn.add(aux);
		}
				
		this.basePosition = potential.getBasePosition(configuration);
				
	}
	
	
	private TablePotential getPotential(){
		return (TablePotential)(probNode.getPotentials().get(0));
	}

	
	
	public Variable getVariable(){
		return probNode.getVariable();
	}
	
	@Override
	public void doEdit() {
		
		TablePotential potential = getPotential();
		
		//Remove the column of uncertain values
		if (!wasNullOldUncertainColumn){
			UncertainValuesEdit.placeUncertainColumn(potential,null,getVariable(),basePosition);
		}
		//If all the elements are null then the uncertain value object is set to null
		if (!hasUncertainValues(potential.getUncertaintyTable())){
			potential.setUncertaintyTable(null);
		}
		        
	}

	

	public void undo() {
		super.undo();
		
		TablePotential potential = getPotential();
		
		UncertainValue[] table = potential.getUncertaintyTable();
		//Create uncertain values table if it is null
		if (table==null){
			table = new UncertainValue[potential.getTableSize()];
		}
		//Restore the elements of the uncertain column
		UncertainValuesEdit.placeUncertainColumn(potential, oldUncertainColumn, getVariable(),basePosition);
	}
	
	private boolean hasUncertainValues(UncertainValue[] auxUncertainTable) {
		boolean hasUncertainValues;
		if ((auxUncertainTable==null)||(auxUncertainTable.length==0)){
			hasUncertainValues = false;
		}
		else{
			hasUncertainValues = false;
			for (int i=0;(i<auxUncertainTable.length)&&!hasUncertainValues;i++){
				hasUncertainValues = (auxUncertainTable[i]!=null);
			}
		}
		return hasUncertainValues;
	}	

}