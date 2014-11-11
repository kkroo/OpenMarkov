/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.graphic;


import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;

/**
 * This class is the visual representation of the inner box associated
 * to a VisualNode that represents a Finite States variable
 * 
 * @author asaez 
 * @version 1.0
 */
public class FSVariableBox extends InnerBox {

	/**
	 * This variable contains a list of all the visual states that are part
	 * of this inner box.
	 */
	public HashMap<Integer, VisualState> visualStates = null;
	
	/**
	 * Creates a new Finite States Variable innerBox.
	 * 
	 * @param vNode
	 *            visualNode to which this Finite States Variable innerBox is associated.
	 */
	public FSVariableBox(VisualNode vNode) {
		visualNode = vNode;
		visualStates = new HashMap<Integer, VisualState>();
		createVisualStates();
	}
	
	/**
	 * This method creates a visual state for each state of the variable.
	 * Each visual state will have only a value
	 */
	protected void createVisualStates() {
		ProbNode probNode = visualNode.getProbNode();
		Variable variable = probNode.getVariable();
		State[] states = variable.getStates();
		for (int i=0; i<states.length; i++) {
			VisualState visualState = new VisualState(visualNode, i, states[i].getName());
			this.visualStates.put(i, visualState);
		}
	}
	
	/**
	 * This method creates a visual state for each state of the variable.
	 * 
	 * @param numValues
	 *            Number of values that has to be each visual state.
	 */
	private void createVisualStates(int numValues) {
		ProbNode probNode = visualNode.getProbNode();
		Variable variable = probNode.getVariable();
		State[] states = variable.getStates();
		for (int i=0; i<states.length; i++) {
			VisualState visualState =
					new VisualState(visualNode, i, states[i].getName(), numValues);
			this.visualStates.put(i, visualState);
		}
	}
	
	/**
	 * This method recreates the visual states of the inner box.
	 *  
	 * @param numCases
	 *            Number of evidence cases in memory.
	 */
	public void recreateVisualStates(int numCases) {
		visualStates.clear();
		createVisualStates(numCases);
	}
		
	/**
	 * Returns the visual state that occupies the given position.
	 * 
	 * @param numPosition
	 *            The position of the state to be returned.
	 * 
	 * @return the visual state that occupies the given position.
	 */
	public VisualState getVisualState(Integer numPosition) {
		return visualStates.get(numPosition);
	}
	
	/**
	 * Returns the visual state with the given name.
	 * 
	 * @param name
	 *            The name of the state to be returned.
	 * 
	 * @return the visual state with the given name.
	 */
	public VisualState getVisualState(String name) {
		VisualState visualState = null;
		for (int i=0; i<visualStates.size(); i++) {
			if (visualStates.get(i).getStateName().equals(name)) {
				visualState = visualStates.get(i);
			}
		}
		return visualState;
	}
	
	/**
	 * Returns the number of visual states of this inner box.
	 * 
	 * @return the number of visual states of this inner box.
	 */
	public int getNumStates() {
		return visualStates.size();
	}
	
	/**
	 * Returns the shape of the innerBox.
	 * 
	 * @param g
	 *            graphics object.
	 * 
	 * @return shape of the innerBox.
	 */
	public Shape getShape(Graphics2D g) {
		double innerNodeHeight = getInnerBoxHeight(g);
		return new Rectangle2D.Double(visualNode.getUpperLeftCornerX(g) + INTERNAL_MARGIN, 
				visualNode.getUpperLeftCornerY(g) + visualNode.getTextHeight(g) + INTERNAL_MARGIN, 
				BOX_WIDTH, 
				innerNodeHeight);
	}
		
	/**
	 * Paints the inner part of the visual node into the graphics object.
	 * 
	 * @param g
	 *            graphics object where paint the node.
	 */
	public void paint(Graphics2D g) {
		Shape shape = getShape(g);
		g.setPaint(BACKGROUND_COLOR);
		g.fill(shape);
		g.setStroke(NORMAL_STROKE);
		g.setPaint(FOREGROUND_COLOR);
		g.draw(shape);	
		g.setFont(INNERBOX_FONT);
		for (int i= 0; i<visualStates.size(); i++) {
			visualStates.get(i).paint(g);
		}
	}	

	/**
	 * Returns the height of the innerBox. It's calculated depending on the
	 * font, the number of states and the cases in memory
	 * 
	 * @param g
	 *            graphics object.
	 * 
	 * @return the height of the innerBox.
	 */
	public double getInnerBoxHeight(Graphics2D g) {
		double innerBoxHeight = 0.0;
		int numEstados = visualStates.size();
		if (visualNode.getVisualNetwork().isPropagationActive()) {	
			innerBoxHeight = INTERNAL_MARGIN + 
				(STATES_VERTICAL_SEPARATION*(numEstados)) + 
				((getVisualState(0).getNumberOfValues()-1)*BAR_HEIGHT*numEstados);
		} else {			
			innerBoxHeight = INTERNAL_MARGIN + 
				(STATES_VERTICAL_SEPARATION*(numEstados));	
		}
		return innerBoxHeight;
	}
	
}
