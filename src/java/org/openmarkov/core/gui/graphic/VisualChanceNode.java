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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.PriorityQueue;

import javax.swing.JPanel;

import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.learning.core.util.LearningEditMotivation;
import org.openmarkov.learning.core.util.LearningEditProposal;
import org.openmarkov.learning.core.util.ScoreEditMotivation;

/**
 * This class is the visual representation of a chance node.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - fix public and static methods, fix Double comparison
 * @version 1.2 asaez - add expanded representation
 */
public class VisualChanceNode extends VisualNode {

	protected static final BasicStroke OBSERVED_WIDE_STROKE = new BasicStroke(
			6.0f);
	protected static final BasicStroke OBSERVED_NORMAL_STROKE = new BasicStroke(
			3.0f);

	/**
	 * Internal color of the visual node when there is no finding established.
	 */
	private static final Color BACKGROUND_COLOR = OpenMarkovPreferences
			.getColor(OpenMarkovPreferences.NODECHANCE_BACKGROUND_COLOR,
					OpenMarkovPreferences.OPENMARKOV_COLORS, new Color(251,
							249, 153));

	/**
	 * Internal color of the visual node when there is a preResolution 
	 * finding established.
	 */
	private static final Color BACKGROUND_PRE_RESOLUTION_FINDING_COLOR = 
			Color.GRAY; //...asaez...........

	/**
	 * Internal color of the visual node when there is a postResolution 
	 * finding established.
	 */
	private static final Color BACKGROUND_POST_RESOLUTION_FINDING_COLOR = 
			Color.LIGHT_GRAY;

	/**
	 * Color of lines and letters.
	 */
	private static final Color FOREGROUND_COLOR = OpenMarkovPreferences
			.getColor(OpenMarkovPreferences.NODECHANCE_FOREGROUND_COLOR,
					OpenMarkovPreferences.OPENMARKOV_COLORS, Color.BLACK);

	/**
	 * Color of the border when the node is alwaysObserved.
	 */
	private static final Color ALWAYS_OBSERVED_COLOR = OpenMarkovPreferences
			.getColor(OpenMarkovPreferences.ALWAYS_OBSERVED_VARIABLE,
					OpenMarkovPreferences.OPENMARKOV_COLORS, new Color(128,0,0));

	/**
	 * Color of the letters
	 */
	private static final Color TEXT_FOREGROUND_COLOR = OpenMarkovPreferences
			.getColor(OpenMarkovPreferences.NODECHANCE_TEXT_COLOR,
					OpenMarkovPreferences.OPENMARKOV_COLORS, Color.BLACK);

	/**
	 * Width of a the arc of the rounded rectangle.
	 */
	private static final double ARC_WIDTH = 20;

	/**
	 * Height of a the arc of the rounded rectangle.
	 */
	private static final double ARC_HEIGHT = 20;

	/**
	 * Creates a new visual node from a node.
	 * 
	 * @param node
	 *            object that has the information of the node.
	 * @param visualNetwork
	 *            editor panel to which this visual node is associated.
	 */
	public VisualChanceNode(ProbNode node, VisualNetwork visualNetwork) {
		super(node, visualNetwork);
		expanded = false;
		preResolutionFinding = false;
		postResolutionFinding = false;
		setTemporalPosition(new Point2D.Double(probNode.getNode()
				.getCoordinateX(), probNode.getNode().getCoordinateY()));
		innerBox = new FSVariableBox(this);
	}

	/**
	 * Returns the visual measurements of the node.
	 * 
	 * @param g
	 *            graphics object where to paint the element.
	 * @return an array of six elements that contains the center of the node
	 *         (elements 0 and 1), the width and height of the node (elements 2
	 *         and 3) and the width and height of the rounded corner (elements 4
	 *         and 5).
	 */
	private double[] getNodeDimensions(Graphics2D g) {

		double[] dimensions = new double[6];
		String text = getNodeString();
		double textHeight = getHeight(text, g);
		double textWidth = getWidth(text, g);
		double width;
		double height;
		if (isExpanded()) {
			height = innerBox.getInnerBoxHeight(g) + textHeight + 2
					* VERTICAL_SPACE_TO_TEXT + NODE_EXPANDED_HEIGHT_MARGIN * 2;
			width = NODE_EXPANDED_WIDTH;
		} else {
			height = textHeight + 2 * VERTICAL_SPACE_TO_TEXT;
			if (textWidth < textHeight) {
				width = DEFAULT_NODE_CONTRACTED_WIDTH;
			} else {
				width = textWidth + 2 * HORIZONTAL_SPACE_TO_TEXT;
			}
		}
		double bestMotivation = probNode.getBestEditMotivation();
		if ( bestMotivation != 0){
			String motivationText = "Proposed edits: " + probNode.getProposedEdits().size() + " (+" + String.format("%.2f", bestMotivation) + ")";		
			height += getHeight(motivationText, g);
			width = Math.max(getWidth(motivationText, g), width);
		}

		// for visualization purposes the position is temporal
		dimensions[0] = getTemporalPosition().getX() - width / 2;
		dimensions[1] = getTemporalPosition().getY() - height / 2;

		dimensions[2] = width;
		dimensions[3] = height;
		dimensions[4] = ARC_WIDTH;
		dimensions[5] = ARC_HEIGHT;

		return dimensions;

	}

	/**
	 * Returns the X-coordinate of the upper-left corner of the visual node.
	 * 
	 * @return the X-coordinate of the upper-left corner of the visual node.
	 */
	public double getUpperLeftCornerX(Graphics2D g) {
		double[] dims = getNodeDimensions(g);
		return dims[0];
	}

	/**
	 * Returns the Y-coordinate of the upper-left corner of the visual node.
	 * 
	 * @return the Y-coordinate of the upper-left corner of the visual node.
	 */
	public double getUpperLeftCornerY(Graphics2D g) {
		double[] dims = getNodeDimensions(g);
		return dims[1];
	}
	
	/**
	 * Returns the shape of the node.
	 * 
	 * @return shape of the node.
	 */
	@Override
	public Shape getShape(Graphics2D g) {

		double dimensions[] = getNodeDimensions(g);

		return new RoundRectangle2D.Double(dimensions[0], dimensions[1],
				dimensions[2], dimensions[3], dimensions[4], dimensions[5]);

	}

	/**
	 * Returns the point where the segment cuts with the border of the node.
	 * 
	 * @param segment
	 *            segment that cuts the border of the node.
	 * @return the point where the segments cuts the border or null if it
	 *         doesn't.
	 */
	@Override
	public Point2D.Double getCutPoint(Segment segment, Graphics2D g) {

		double dimensions[] = getNodeDimensions(g);
		double radius = dimensions[4] / 2;
		double rectangleWidth = dimensions[2] - dimensions[4];
		double rectangleHeight = dimensions[3] - dimensions[5];
		Point2D.Double point1 = new Point2D.Double(dimensions[0] + radius,
				dimensions[1]);
		Point2D.Double point2 = new Point2D.Double(point1.getX()
				+ rectangleWidth, point1.getY());
		Point2D.Double point3 = new Point2D.Double(point2.getX() + radius,
				point2.getY() + radius);
		Point2D.Double point4 = new Point2D.Double(point3.getX(), point3.getY()
				+ rectangleHeight);
		Point2D.Double point5 = new Point2D.Double(point2.getX(), point4.getY()
				+ radius);
		Point2D.Double point6 = new Point2D.Double(point1.getX(), point5.getY());
		Point2D.Double point7 = new Point2D.Double(dimensions[0], point4.getY());
		Point2D.Double point8 = new Point2D.Double(dimensions[0], point3.getY());
		Point2D.Double circleULCenter = new Point2D.Double(point1.getX(),
				point8.getY());
		Point2D.Double circleURCenter = new Point2D.Double(point2.getX(),
				point3.getY());
		Point2D.Double circleDLCenter = new Point2D.Double(point6.getX(),
				point7.getY());
		Point2D.Double circleDRCenter = new Point2D.Double(point5.getX(),
				point4.getY());
		Point2D.Double point;
		Point2D.Double[] points;

		// try to find the cut point in the upper horizontal segment of the
		// round rectangle
		point = segment.cutPoint(new Segment(point1, point2));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the right vertical segment of the round
		// rectangle
		point = segment.cutPoint(new Segment(point3, point4));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the lower horizontal segment of the
		// round rectangle
		point = segment.cutPoint(new Segment(point5, point6));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the left vertical segment of the round
		// rectangle
		point = segment.cutPoint(new Segment(point7, point8));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the upper left corner of the round
		// rectangle
		points = segment.cutPoint(circleULCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() < circleULCenter.getX())
						&& (points[i].getY() < circleULCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the upper right corner of the round
		// rectangle
		points = segment.cutPoint(circleURCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() > circleURCenter.getX())
						&& (points[i].getY() < circleURCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the lower right corner of the round
		// rectangle
		points = segment.cutPoint(circleDRCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() > circleDRCenter.getX())
						&& (points[i].getY() > circleDRCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the lower left corner of the round
		// rectangle
		points = segment.cutPoint(circleDLCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() < circleDLCenter.getX())
						&& (points[i].getY() > circleDLCenter.getY())) {
					return points[i];
				}
			}
		}

		return point;
	}

	private Color getBackgroundColor() {
		if (preResolutionFinding) {
			return BACKGROUND_PRE_RESOLUTION_FINDING_COLOR;
		} else if (postResolutionFinding && 
				(visualNetwork.getWorkingMode() == 
					NetworkPanel.INFERENCE_WORKING_MODE)) {
			return BACKGROUND_POST_RESOLUTION_FINDING_COLOR;
		} else if ( !visualNetwork.getSelectedNodes().isEmpty() ) {
			return BACKGROUND_COLOR;
		} else {
			double maxMotivation =  visualNetwork.getMaxMotivation();
			double minMotivation = visualNetwork.getMinMotivation();
			double bestEditMotivation = probNode.getBestEditMotivation();
			double relativeMotivation = ( bestEditMotivation - minMotivation + 0.01 ) / (maxMotivation - minMotivation + 0.01);
			int alpha = (int) (255 * relativeMotivation);
			Color color = new Color(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), BACKGROUND_COLOR.getBlue(), alpha);
			return color;
		}
	}
	/**
	 * Paints the visual node into the graphics object as a rounded rectangle.
	 * 
	 * @param g
	 *            graphics object where paint the node.
	 */
	@Override
	public void paint(Graphics2D g) {

		String text = getNodeString();
		double textHeight = getHeight(text, g);
		double textWidth = getWidth(text, g);
		Shape shape = getShape(g);
		double[] dimensions = getNodeDimensions(g);

		g.setPaint(getBackgroundColor());
		g.fill(shape);
		g.setPaint(FOREGROUND_COLOR);

		if (probNode.isAlwaysObserved()) {
			g.setPaint(ALWAYS_OBSERVED_COLOR);
			g.setStroke((isSelected())? OBSERVED_WIDE_STROKE : OBSERVED_NORMAL_STROKE);
		} else if (probNode.isInput ()) {
		    g.setStroke((isSelected())? WIDE_DASHED_STROKE : NORMAL_DASHED_STROKE);
		} else {
            g.setStroke((isSelected())? WIDE_STROKE : NORMAL_STROKE);
		}

		g.draw(shape);
		g.setFont(FONT_HELVETICA);
		g.setPaint(TEXT_FOREGROUND_COLOR);

		if (isExpanded()) {
			text = adjustText(text, dimensions[2], 3, FONT_HELVETICA, g);
			textWidth = getWidth(text, g);
		}
		double textPosX = getTemporalPosition().getX() - (textWidth / 2);
		double textPosY = getTemporalPosition().getY() - (dimensions[3] / 2)
				+ (textHeight);

		g.drawString(text, (float) textPosX, (float) textPosY);
		
		double bestMotivation = probNode.getBestEditMotivation();
		if (bestMotivation != 0) {
			String motivationText = "Proposed edits: " + probNode.getProposedEdits().size() + " (+" + String.format("%.2f", bestMotivation) + ")";			motivationText = adjustText(motivationText, dimensions[2], 3, FONT_HELVETICA_SMALL, g);
			FontMetrics fontMeter = new JPanel().getFontMetrics(FONT_HELVETICA_SMALL);
			textWidth = fontMeter.getStringBounds(motivationText, g).getWidth();
			textPosX = getTemporalPosition().getX() - (textWidth / 2);
			textPosY += textHeight;
			g.drawString(motivationText, (float) textPosX, (float) textPosY);
		}
		

		if (isExpanded()) {
			innerBox.paint(g);
		}

	}

}
