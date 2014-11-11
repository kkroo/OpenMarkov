/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.graphic;


import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * This class is the visual representation of the inner box associated
 * to a VisualNode that represents an Expected Value variable
 * 
 * @author asaez 1.0
 */
public class ExpectedValueBox extends InnerBox {

	/**
	 * Font type Helvetica, plain, size 9.
	 */
	protected static final Font SCALE_FONT = new Font("Helvetica", Font.PLAIN, 9);
	
	/**
	 * Vertical separation between the line for expected value and
	 * the line for the scale.
	 */
	private static final double SCALE_VERTICAL_SEPARATION = 12;	
	
	/**
	 * Horizontal Offset for the position of the range values
	 * of the scale.
	 */
	private static final int SCALE_RANGE_HORIZONTAL_OFFSET = 8;

	/**
	 * Vertical Offset for the position of the range values
	 * of the scale.
	 */
	private static final int SCALE_RANGE_VERTICAL_OFFSET = 4;
	
	/**
	 * Minimum value that the expected value can take.
	 */
	private double minUtilityRange = Double.NEGATIVE_INFINITY;
	
	/**
	 * Maximum value that the expected value can take.
	 */
	private double maxUtilityRange = Double.POSITIVE_INFINITY;	
	
	/**
	 * This variable contains the visual state that is part
	 * of this inner box.
	 */
	VisualState visualState = null;

	/**
	 * Creates a new Expected Value Variable innerBox.
	 * 
	 * @param vNode
	 *            visualNode to which this Expected Value Variable innerBox is associated.
	 */
	public ExpectedValueBox(VisualNode vNode) {
		visualNode = vNode;
		visualState = new VisualState(visualNode, 0, "  EU");
	}
	
	/**
	 * Returns the minimum value that the expected value can take.
	 * 
	 * @return minimum value that the expected value can take.
	 */
	public double getMinUtilityRange() {
		return minUtilityRange;
	}
	
	/**
	 * Sets the minimum value that the expected value can take.
	 * 
	 * @param minUtilityRange
	 *            minimum value that the expected value can take.
	 */
	public void setMinUtilityRange(double minUtilityRange) {
		//minUtilityRange is currently formatted with 2 decimals
		this.minUtilityRange = (Math.rint(minUtilityRange*100))/100;
	}
	
	/**
	 * Returns the maximum value that the expected value can take.
	 * 
	 * @return maximum value that the expected value can take.
	 */
	public double getMaxUtilityRange() {
		return maxUtilityRange;
	}
	
	/**
	 * Sets the maximum value that the expected value can take.
	 * 
	 * @param maxUtilityRange
	 *            maximum value that the expected value can take.
	 */
	public void setMaxUtilityRange(double maxUtilityRange) {
		//maxUtilityRange is currently formatted with 2 decimals
		this.maxUtilityRange = (Math.rint(maxUtilityRange*100))/100;
	}
	
	/**
	 * This method recreates the visual state of the inner box.
	 *  
	 * @param numCases
	 *            Number of evidence cases in memory.
	 */
	public void recreateVisualState(int numCases) {
		visualState = new VisualState(visualNode, 0, "  EU", numCases);
	}

	/**
	 * Returns the visual state contained by this inner box.
	 * 
	 * @return visual state contained by this inner box.
	 */
	public VisualState getVisualState() {
		return visualState;
	}

	/**
	 * Sets the visual state contained by this inner box.
	 * 
	 * @param visualState
	 *            visual state contained by this inner box.
	 */
	public void setVisualState(VisualState visualState) {
		this.visualState = visualState;
	}
	
	/**
	 * Returns the number of visual states of this inner box.
	 * 
	 * @return the number of visual states of this inner box.
	 */
	public int getNumStates() {
		return 1;
	}
	
	/**
	 * Returns the shape of the innerBox.
	 * 
	 * @return shape of the innerBox.
	 */
	public Shape getShape(Graphics2D g) {
		double innerNodeHeight = getInnerBoxHeight(g);
		return new Rectangle2D.Double(
				visualNode.getUpperLeftCornerX(g),
				visualNode.getUpperLeftCornerY(g) + 
						visualNode.getTextHeight(g) + INTERNAL_MARGIN, 
				BOX_WIDTH, 
				innerNodeHeight
				);		
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
		
		visualState.paint(g);
		
		//draw the scale in the bottom part
		Double scaleXPostion = visualNode.getUpperLeftCornerX(g) + 
				INTERNAL_MARGIN + STATES_INDENT + BAR_HORIZONTAL_POSITION_UTILITY - 1;
		Double scaleYPostion = visualNode.getUpperLeftCornerY(g) +
				visualNode.getTextHeight(g) + INTERNAL_MARGIN + 
				STATES_VERTICAL_SEPARATION + SCALE_VERTICAL_SEPARATION +
				(BAR_HEIGHT*(visualState.getNumberOfValues()-1));		
		
		g.draw(new Line2D.Double(scaleXPostion,
				scaleYPostion, 
				scaleXPostion + BAR_FULL_LENGTH, 
				scaleYPostion)
				);
		g.draw(new Line2D.Double(scaleXPostion,
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion, 
				scaleYPostion + (BAR_HEIGHT/2))
				);
		g.draw(new Line2D.Double(scaleXPostion + (BAR_FULL_LENGTH/4), 
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion + (BAR_FULL_LENGTH/4), 
				scaleYPostion + (BAR_HEIGHT/2))
				);
		g.draw(new Line2D.Double(scaleXPostion + (BAR_FULL_LENGTH/2), 
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion + (BAR_FULL_LENGTH/2), 
				scaleYPostion + (BAR_HEIGHT/2))
				);
		g.draw(new Line2D.Double(scaleXPostion + (BAR_FULL_LENGTH*3/4), 
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion + (BAR_FULL_LENGTH*3/4), 
				scaleYPostion + (BAR_HEIGHT/2))
				);
		g.draw(new Line2D.Double(scaleXPostion + BAR_FULL_LENGTH, 
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion + BAR_FULL_LENGTH, 
				scaleYPostion + (BAR_HEIGHT/2))
				);

		g.setFont(SCALE_FONT);
		g.drawString("" + minUtilityRange,
				scaleXPostion.intValue() - SCALE_RANGE_HORIZONTAL_OFFSET,
				scaleYPostion.intValue() + g.getFont().getSize() + 
						SCALE_RANGE_VERTICAL_OFFSET
				);
		g.drawString("" + maxUtilityRange,			
				(int) (scaleXPostion.intValue()+ BAR_FULL_LENGTH) - 
						SCALE_RANGE_HORIZONTAL_OFFSET, 
				scaleYPostion.intValue() + g.getFont().getSize() + 
						SCALE_RANGE_VERTICAL_OFFSET
				);
		g.setFont(INNERBOX_FONT);
	}
	

	/**
	 * Returns the height of the innerBox.
	 * 
	 * @param g
	 *            graphics object.
	 * 
	 * @return the height of the innerBox.
	 */
	public double getInnerBoxHeight(Graphics2D g) {
		double innerBoxHeight = 0.0;
		if (visualNode.getVisualNetwork().isPropagationActive()) {	
			innerBoxHeight = INTERNAL_MARGIN*2 + 
				STATES_VERTICAL_SEPARATION + 
				SCALE_VERTICAL_SEPARATION +
				(visualState.getNumberOfValues()-1)*BAR_HEIGHT+
				BAR_HEIGHT/2 +
				SCALE_FONT.getSize();
		} else {			
			innerBoxHeight = INTERNAL_MARGIN*2 + 
				STATES_VERTICAL_SEPARATION + 
				SCALE_VERTICAL_SEPARATION +
				BAR_HEIGHT/2 +
				SCALE_FONT.getSize();
		}
		return innerBoxHeight;
	}

}