/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.action;


import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;

/**
 * <code>NodePartitionedIntervalEdit</code> is a simple edit that allows to modify
 * the node partitioned interval. 
 *    
 * @version 1.0 21/12/10
 * @author Miguel Palacios
 */
public class NodePartitionedIntervalEdit extends SimplePNEdit{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The predefined increment for new intervals	
	 */
	private final int increment = 2;
	/**
	 * The state index that partitioned interval belongs to. 
	 */
	private int indexState;
	/**
	 * The node edited
	 */
	private ProbNode probNode = null;
	/**
	 * Action to do with the partitioned interval 
	 */
	private StateAction stateAction;
	/**
	 * The current partitioned interval
	 */
	private PartitionedInterval currentPartitionedInterval;
	
	/**
	 * A boolean that specify if the edition is in the lower limit 
	 * (value or symbol)
	 */	
	private boolean lower;
	/**
	 * The new limit value
	 */
	private double newValue;
	
	/**
	 * The last value contained in PartitionedInterval
	 */
	private double lastValue;
	
	/**
	 * Creates a new <code>PartiTionedIntervalEdit</code> to edit the limit 
	 * symbol of the interval
	 * @param probNode
	 * 	The node that contain the partionInterval object to be edited.
	 * @param stateAction
	 * 	The action to do in this edit.
	 * @param indexState
	 * 	The state index that partitionedInterval object belongs to. 
	 * @param lower
	 * 	A boolean that specify if the edition is in the lower symbol 
	 */	
	public NodePartitionedIntervalEdit ( ProbNode probNode,
				StateAction stateAction, int indexState, boolean lower){
			super(probNode.getProbNet());
			this.probNode = probNode;
			this.indexState = indexState;
			this.stateAction = stateAction;
			this.lower = lower;
			this.currentPartitionedInterval = probNode.getVariable().
				getPartitionedInterval();
			
			

	}
		/**
		 * Creates a new <code>NodePartiTionedIntervalEdit</code> to edit the 
		 * limit value of the interval
		 * @param probNode
		 * 	The node that contains the partionInterval object to be edited.
		 * @param stateAction
		 * 	The action to do in this edit.
		 * @param indexState
		 * 	The state index that partitionedInterval object belongs to. 
		 * @param newValue
		 * the new values of the limit
		 * @param lower
		 * 	A boolean that specify if the edition is in the lower value 
		 */	
		public NodePartitionedIntervalEdit ( ProbNode probNode,
				StateAction stateAction, int indexState, double newValue,
				boolean lower){
			
			this (probNode,stateAction, indexState, lower);
			this.newValue = newValue;
			if ( lower ){
				this.lastValue = probNode.getVariable().getPartitionedInterval().
				getLimit(indexState);
			}else{
				this.lastValue = probNode.getVariable().getPartitionedInterval().
				getLimit( indexState + 1 );
			}
			
		}
	
		
		@Override
		public void doEdit() throws DoEditException {
						
			switch (stateAction){
			case MODIFY_DELIMITER_INTERVAL:
				if (lower){
					currentPartitionedInterval.changeLimit(	indexState, 
							currentPartitionedInterval.getLimit( indexState ), 
							!currentPartitionedInterval.getBelongsToLeftSide( 
									indexState ));
				}else{
					currentPartitionedInterval.changeLimit(indexState + 1, 
							currentPartitionedInterval.getLimit(indexState + 1),
							!currentPartitionedInterval.getBelongsToLeftSide(
									indexState + 1));
				}
				/*probNode.getVariable().setPartitionedInterval(
						newPartitionedInterval );*/
				break;
			case MODIFY_VALUE_INTERVAL:
				if (lower)
					currentPartitionedInterval.changeLimit(indexState, newValue, 
					currentPartitionedInterval.getBelongsToLeftSide(
								indexState));
				else
					
					currentPartitionedInterval.changeLimit( indexState + 1, 
							newValue, currentPartitionedInterval.
							getBelongsToLeftSide( indexState + 1 ));
				break;
			
				
		}
			
			
	}
		
		
		@Override
		public void undo() {
			super.undo();
			switch (stateAction){
			case MODIFY_DELIMITER_INTERVAL:
				if (lower){
					currentPartitionedInterval.changeLimit(	indexState, 
							currentPartitionedInterval.getLimit( indexState ), 
							!currentPartitionedInterval.getBelongsToLeftSide( 
									indexState ));
				}else{
					currentPartitionedInterval.changeLimit(indexState + 1, 
							currentPartitionedInterval.getLimit(indexState + 1),
							!currentPartitionedInterval.getBelongsToLeftSide(
									indexState + 1));
				}
				/*probNode.getVariable().setPartitionedInterval(
						newPartitionedInterval );*/
				break;
			case MODIFY_VALUE_INTERVAL:
				if (lower)
					currentPartitionedInterval.changeLimit(indexState, lastValue, 
						currentPartitionedInterval.getBelongsToLeftSide(
								indexState));
				else
					currentPartitionedInterval.changeLimit( indexState + 1, 
							lastValue, currentPartitionedInterval.
							getBelongsToLeftSide( indexState + 1 ));
				break;
			
				
		}
			
		}
		
		/**
		 * Gets the node edited
		 * @return the node edited
		 */		
		public ProbNode getProbNode(){
			return probNode;
		}
		/**
		 * Gets the action realized.
		 * @see <code>StateAction</code>
		 * @return
		 * the action realized.
		 */
		public StateAction getStateAction(){
			return stateAction;
		}
		
		/**
		 * This method add a new default subInterval, in the current 
		 * PartitionedInterval object, is used when a new state has been created.
		 * @return
		 * 	The PartitionedInterval object with a new default subInterval
		 */		
		
		private PartitionedInterval getNewPartitionedInterval(){
			double limits [] = currentPartitionedInterval.getLimits();
			double newLimits [] = new double [limits.length + 1];
			boolean belongsToLeftSide [] = currentPartitionedInterval.
				getBelongsToLeftSide();
			boolean newBelongsToLeftSide [] = new boolean [limits.length + 1];
			for (int i = 0; i < limits.length; i++){
				newLimits[ i ] = limits[ i ];
				newBelongsToLeftSide [ i ] = belongsToLeftSide [ i ];
			}
			newLimits[ limits.length ] = currentPartitionedInterval.getMax() + increment; 
			newBelongsToLeftSide [ limits.length ] = false;
			return 	new PartitionedInterval(newLimits, newBelongsToLeftSide);
		}
		
		/**
		 * This method gets the new row data when a new state is 
		 * inserted/deleted in a discretized variable.
		 * @param stateAction the action carried out in the edit.
		 * @return The row data of the new state
		 */
		public Object[] getNewRowOfData(StateAction stateAction){
			String firstSymbol = null;
			String secondSymbol = null;
			double limits [] = null;
			boolean belongsToLeftSide [];
			if (stateAction == StateAction.ADD){
				limits = probNode.getVariable().getPartitionedInterval().
					getLimits();
				belongsToLeftSide = probNode.getVariable().
					getPartitionedInterval().getBelongsToLeftSide();
			
				firstSymbol = (belongsToLeftSide[limits.length-2]? "(" : "[");
				secondSymbol = (belongsToLeftSide[limits.length-1]? "]" : ")");
			} else if (stateAction == StateAction.REMOVE){
				limits = probNode.getVariable().getPartitionedInterval().
					getLimits();
				belongsToLeftSide = probNode.getVariable().
					getPartitionedInterval().getBelongsToLeftSide();
		
				firstSymbol = (belongsToLeftSide[indexState]? "(" : "[");
				secondSymbol = (belongsToLeftSide[indexState + 1]? "]" : ")");
						
			}
			return new Object[] {"", GUIDefaultStates.getString(probNode.getVariable().
	 				getStates()[indexState].toString()) , firstSymbol, 
	 				limits[ indexState], "," , limits[ indexState + 1], 
	 				secondSymbol };
			
		}
		
		/**
		 * Gets the boolean lower identifier
		 * @return a boolean lower identifier
		 */
		public boolean getLower() {
			// TODO Auto-generated method stub
			return lower;
		}
		/**
		 * Gets the new value of the limit modified
		 * @return the new value of the limit modified
		 */
		public Double getNewValue() {
			// TODO Auto-generated method stub
			return newValue;
		}
		/**
		 * Gets the index of the state modified
		 * @return the index of the state modified
		 */
		public Integer getIndexState() {
			// TODO Auto-generated method stub
			return indexState;
		}
}
