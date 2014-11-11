/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.graphic;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * This abstract class specifies the methods that all inner boxes of the 
 * visual nodes have to implement.
 * 
 * @author asaez 
 * @version 1.0
 */
public abstract class InnerBox extends VisualElement {
	
	/**
	 * Font type Helvetica, plain, size 11.
	 */
	protected static final Font INNERBOX_FONT = new Font("Helvetica", Font.PLAIN, 11);
	
	/**
	 * Color of the Box.
	 */
	protected static final Color BACKGROUND_COLOR = Color.WHITE;
	
	/**
	 * Color of lines and letters.
	 */	
	protected static final Color FOREGROUND_COLOR = Color.BLACK;
	
	/**
	 * Internal margin around the Box.
	 */
	protected static final double INTERNAL_MARGIN = 4;
	
	/**
	 * Width of the Box.
	 */
	protected static final double BOX_WIDTH = 
		VisualNode.NODE_EXPANDED_WIDTH - (2 * INTERNAL_MARGIN) + 1;

	/**
	 * Indentation of states.
	 */
	protected static final double STATES_INDENT = 5;

	/**
	 * Vertical separation between states.
	 */
	protected static final double STATES_VERTICAL_SEPARATION = 12;
	
	/**
	 * Horizontal starting position of bars in Chance and Decision Nodes.
	 */
	protected static final double BAR_HORIZONTAL_POSITION = 52;
	
	/**
	 * Horizontal starting position of bars in Utility Nodes.
	 */
	protected static final double BAR_HORIZONTAL_POSITION_UTILITY = 32;
	
	/**
	 * Maximum length of the bar.
	 */
	protected static final double BAR_FULL_LENGTH = 100;
	
	/**
	 * Height of the bar.
	 */
	protected static final double BAR_HEIGHT = 5;
	
	/**
	 * Horizontal position for the value to be shown on the right 
	 * of the bar in Chance and Decision Nodes.
	 */
	protected static final double VALUE_HORIZONTAL_POSITION = 
			BAR_HORIZONTAL_POSITION + 
			BAR_FULL_LENGTH + STATES_INDENT;
	
	/**
	 * Horizontal position for the value to be shown on the right 
	 * of the bar in Utility Nodes.
	 */
	protected static final double VALUE_HORIZONTAL_POSITION_UTILITY = 
			BAR_HORIZONTAL_POSITION_UTILITY + 
			BAR_FULL_LENGTH + STATES_INDENT;

	/**
	 * Object used to measure text in a specific font.
	 */
	private static FontMetrics fontMeter = new JPanel().getFontMetrics(INNERBOX_FONT);
		
	/**
	 * The height of this InnerBox.
	 */
	protected double height;
	
	/**
	 * The VisualNode this InnerBox is associated to.
	 */
	protected VisualNode visualNode;
	
	/**
	 * Returns the visualNode associated with the innerBox.
	 * 
	 * @return visualNode associated with the innerBox.
	 */
	public VisualNode getVisualNode() {
		return visualNode;
	}
	
	/**
	 * Returns the height of the text used in the innerBox.
	 * 
	 * @param text
	 *            text that appears in the innerBox.
	 * @param g
	 *            graphics object where to paint the element.
	 * @return the height of the text used in the innerBox.
	 */
	protected static double getInnerBoxTextHeight(String text, Graphics2D g) {
		return fontMeter.getStringBounds(text, g).getHeight();
	}
	
	/**
	 * Returns the width of the text used in the innerBox.
	 * 
	 * @param text
	 *            text that appears in the innerBox.
	 * @param g
	 *            graphics object where to paint the element.
	 * @return the width of the text used in the innerBox.
	 */
	protected static double getInnerBoxTextWidth(String text, Graphics2D g) {
		return fontMeter.getStringBounds(text, g).getWidth();
	}
	
	/**
	 * Returns the height of the innerBox. It's calculated depending on the
	 * font, the number of states and the cases in memory
	 * 
	 * @return the height of the innerBox.
	 */
	public abstract double  getInnerBoxHeight(Graphics2D g);


	/**
	 * Returns the number of visual states of this inner box.
	 * 
	 * @return the number of visual states of this inner box.
	 */
	public abstract int getNumStates();

}
