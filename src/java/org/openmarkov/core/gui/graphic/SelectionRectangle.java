/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.graphic;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.openmarkov.core.model.network.ProbNode;



/**
 * This class implements the square with various nodes are selected. It is
 * formed by a rectangle painted with a dash line.
 * 
 * @author jmendoza
 * @version 1.0
 */
public class SelectionRectangle {

	/**
	 * Used to paint dashed lines.
	 */
	private static final BasicStroke DASHED_STROKE =
		new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
			10.0f, new float[] { 3.0f }, 0.0f);

	/**
	 * Color of lines.
	 */
	private static final Color FOREGROUND_COLOR = Color.black;

	/**
	 * Shape of the selection. The dimensions of the shape can't be negative.
	 * This shape is only used in 'paint' and 'selectingNode' methods.
	 */
	private Rectangle2D.Double rectangleSelection =
		new Rectangle2D.Double(0, 0, 0, 0);

	/**
	 * X-coordinate of the rectangle selection.
	 */
	private double x = 0;

	/**
	 * Y-coordinate of the rectangle selection.
	 */
	private double y = 0;

	/**
	 * Width of the rectangle selection.
	 */
	private double w = 0;

	/**
	 * Height of the rectangle selection.
	 */
	private double h = 0;

	/**
	 * initialises a new selection.
	 * 
	 * @param start
	 *            the upper-left corner of the selection rectangle.
	 * @param newW
	 *            the width of the selection rectangle.
	 * @param newH
	 *            the height of the selection rectangle.
	 */
	public void initSelection(Point2D.Double start, double newW, double newH) {

		double[] dimensions = { start.getX(), start.getY(), newW, newH };

		x = start.getX();
		y = start.getY();
		w = newW;
		h = newH;
		dimensions = calculatePositiveDimensions(dimensions);
		rectangleSelection =
			new Rectangle2D.Double(dimensions[0], dimensions[1], dimensions[2],
				dimensions[3]);

	}

	/**
	 * This method makes selection square to have dimensions of 0.
	 */
	public void clearSelectionSquare() {

		setSize(0, 0);

	}

	/**
	 * Recalculates the dimensions of the selection rectangle because the width
	 * and/or the heigth can't be negative. If any of them are negative, the
	 * point must be recalculated and they must became positive.
	 * 
	 * @param dimensions
	 *            object that contains the dimensions.
	 * @return the new dimensions of the rectangle.
	 */
	private static double[] calculatePositiveDimensions(double[] dimensions) {

		if (dimensions[2] < 0) {
			dimensions[0] += dimensions[2];
			dimensions[2] = -dimensions[2];
		}
		if (dimensions[3] < 0) {
			dimensions[1] += dimensions[3];
			dimensions[3] = -dimensions[3];
		}

		return dimensions;

	}

	/**
	 * Returns the width of the rectangle selection.
	 * 
	 * @return the width of the rectangle selection.
	 */
	public double getWidth() {

		return w;

	}

	/**
	 * Returns the height of the rectangle selection.
	 * 
	 * @return the height of the rectangle selection.
	 */
	public double getHeight() {

		return h;

	}

	/**
	 * Sets the width and height of the selection rectangle.
	 * 
	 * @param newW
	 *            the width of the selection rectangle.
	 * @param newH
	 *            the height of the selection rectangle.
	 */
	public void setSize(double newW, double newH) {

		double[] dimensions = { x, y, newW, newH };

		w = newW;
		h = newH;
		calculatePositiveDimensions(dimensions);
		rectangleSelection.setRect(
			dimensions[0], dimensions[1], dimensions[2], dimensions[3]);

	}

	/**
	 * Paints the selection rectangle into the graphics object.
	 * 
	 * @param g
	 *            graphics object where paint the rectangle.
	 */
	public void paint(Graphics2D g) {

		Double aPoint = new Double(w);
		Double bPoint = new Double(h);
		g.setStroke(DASHED_STROKE);
		g.setPaint(FOREGROUND_COLOR);

		if (!(aPoint.equals(0) && bPoint.equals(0))) {
			g.draw(rectangleSelection);
		}

	}

	/**
	 * Tests if the center point of the node is inside the boundary of the
	 * rectangle selection.
	 * 
	 * @param node
	 *            node to be tested.
	 * @return true if the center of the node is contained into the rectangle;
	 *         otherwise, false.
	 */
	public boolean containsNode(VisualNode node) {

		ProbNode probNode = node.getProbNode();

		return rectangleSelection.contains(probNode.getNode().getCoordinateX(), 
				probNode.getNode().getCoordinateY());

	}

	/**
	 * Tests if the selection rectangle contains a certain rectangle.
	 * 
	 * @return true if the whole ractangle is contained into the rectangle;
	 *         otherwise, false.
	 */	
	public boolean containsRectangle(double x, double y, double width, double height) {
		return rectangleSelection.contains(x,y) && rectangleSelection.contains(x + width, y + height);
	}
}
