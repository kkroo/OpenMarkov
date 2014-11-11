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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;

/**
 * This class is the visual representation of a decision node.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo - fix public and static methods, fix Double comparison
 * @version 1.2 asaez - add expanded representation
 */
public class VisualDecisionNode extends VisualNode {

    /**
     * Internal color of the visual node when there is no finding established.
     */
    private static final Color BACKGROUND_COLOR              = OpenMarkovPreferences.getColor(OpenMarkovPreferences.NODEDECISION_BACKGROUND_COLOR,
                                                                     OpenMarkovPreferences.OPENMARKOV_COLORS,
                                                                     new Color(207, 227, 253));

    /**
     * Internal color of the visual node when there is a preResolution finding
     * established.
     */
    private static final Color BACKGROUND_PRE_FINDING_COLOR  = Color.GRAY;                     // ...asaez...........

    /**
     * Internal color of the visual node when there is a postResolution finding
     * established.
     */
    private static final Color BACKGROUND_POST_FINDING_COLOR = Color.LIGHT_GRAY;

    /**
     * Internal color of the visual node when there is an imposed policy
     * established.
     */
    private static final Color BACKGROUND_POLICY_SET_COLOR   = new Color(112, 142, 184);

    /**
     * Color of lines and letters.
     */
    private static final Color FOREGROUND_COLOR              = OpenMarkovPreferences.getColor(OpenMarkovPreferences.NODEDECISION_FOREGROUND_COLOR,
                                                                     OpenMarkovPreferences.OPENMARKOV_COLORS,
                                                                     Color.BLACK);

    /**
     * Color of the letters
     */
    private static final Color TEXT_FOREGROUND_COLOR         = OpenMarkovPreferences.getColor(OpenMarkovPreferences.NODEDECISION_TEXT_COLOR,
                                                                     OpenMarkovPreferences.OPENMARKOV_COLORS,
                                                                     Color.BLACK);

    /**
     * This attribute indicates if the node has an imposed policy
     */
    private boolean            hasPolicy                     = false;

    /**
     * Creates a new visual node from a node.
     * 
     * @param node
     *            object that has the information of the node.
     * @param visualNetwork
     *            editor panel to which this visual node is associated.
     */
    public VisualDecisionNode(ProbNode node, VisualNetwork visualNetwork) {
        super(node, visualNetwork);
        expanded = false;
        if (probNode.getPolicyType() != PolicyType.OPTIMAL) {
            hasPolicy = true;
        }
        preResolutionFinding = false;
        postResolutionFinding = false;
        setTemporalPosition(new Point2D.Double(probNode.getNode().getCoordinateX(),
                probNode.getNode().getCoordinateY()));
        innerBox = new FSVariableBox(this);
        setHasPolicy(node.getPotentials().size() != 0);
    }

    /**
     * Returns a boolean indicating if the node has an imposed policy.
     * 
     * @return true if the node has imposed policy; false otherwise.
     */
    public boolean isHasPolicy() {
        return hasPolicy;
    }

    /**
     * Sets if the node has an imposed policy or not.
     * 
     * @param hasPolicy
     *            new value for the hasPolicy attribute.
     */
    public void setHasPolicy(boolean hasPolicy) {
        this.hasPolicy = hasPolicy;
    }

    /**
     * Returns the X-coordinate of the upper-left corner of the visual node.
     * 
     * @return the X-coordinate of the upper-left corner of the visual node.
     */
    public double getUpperLeftCornerX(Graphics2D g) {
        Point2D.Double[] points = getPoints(g);
        return points[0].getX();
    }

    /**
     * Returns the Y-coordinate of the upper-left corner of the visual node.
     * 
     * @return the Y-coordinate of the upper-left corner of the visual node.
     */
    public double getUpperLeftCornerY(Graphics2D g) {
        Point2D.Double[] points = getPoints(g);
        return points[0].getY();
    }

    /**
     * Returns the four points of the rectangle that limits the node. First
     * point is top left corner. Second point is top right corner. Third point
     * is bottom right corner. And fourth one is bottom left corner.
     * 
     * @param g
     *            graphic object where the node can be painted.
     * @return an array that contains the four points of the rectangle.
     */
    private Point2D.Double[] getPoints(Graphics2D g) {

        String text = getNodeString();
        double textHeight = getHeight(text, g);
        double textWidth = getWidth(text, g);
        double rectangleWidth = 0.0;
        double rectangleHeight = 0.0;
        double left = 0.0;
        double right = 0.0;
        double top = 0.0;
        double bottom = 0.0;
        Point2D.Double[] points = new Point2D.Double[4];

        if (isExpanded()) {
            rectangleHeight = innerBox.getInnerBoxHeight(g)
                    + textHeight
                    + 2
                    * VERTICAL_SPACE_TO_TEXT
                    + NODE_EXPANDED_HEIGHT_MARGIN
                    * 2;
            rectangleWidth = NODE_EXPANDED_WIDTH;
        } else {
            rectangleHeight = textHeight + 2 * VERTICAL_SPACE_TO_TEXT;
            if (textWidth < textHeight) {
                rectangleWidth = DEFAULT_NODE_CONTRACTED_WIDTH;
            } else {
                rectangleWidth = textWidth + 2 * HORIZONTAL_SPACE_TO_TEXT;
            }
        }

        left = getTemporalPosition().getX() - rectangleWidth / 2;
        right = left + rectangleWidth;
        top = getTemporalPosition().getY() - rectangleHeight / 2;
        bottom = top + rectangleHeight;
        points[0] = new Point2D.Double(left, top);
        points[1] = new Point2D.Double(right, top);
        points[2] = new Point2D.Double(right, bottom);
        points[3] = new Point2D.Double(left, bottom);

        return points;

    }

    /**
     * Returns the shape of the node.
     * 
     * @return shape of the node.
     */
    @Override
    public Shape getShape(Graphics2D g) {

        Point2D.Double[] points = getPoints(g);
        double rectangleWidth = points[0].distance(points[1]);
        double rectangleHeight = points[1].distance(points[2]);

        double rectanglePosX = getTemporalPosition().getX() - rectangleWidth / 2;
        double rectanglePosY = getTemporalPosition().getY() - rectangleHeight / 2;

        return new Rectangle2D.Double(rectanglePosX, rectanglePosY, rectangleWidth, rectangleHeight);

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

        Point2D.Double[] points = getPoints(g);
        int length = points.length;
        Point2D.Double result = null;
        int index1 = 0;
        int index2 = 1;
        int iteration = 0;

        while ((result == null) && (iteration < length)) {
            result = segment.cutPoint(new Segment(points[index1], points[index2]));
            index1 = (index1 + 1) % length;
            index2 = (index2 + 1) % length;
            iteration++;
        }

        return result;

    }

    /**
     * Paints the visual node into the graphics object as a rectangle.
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
        Point2D.Double[] points = getPoints(g);

        if (preResolutionFinding) {
            g.setPaint(BACKGROUND_PRE_FINDING_COLOR);
        } else if (postResolutionFinding
                && (visualNetwork.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
            g.setPaint(BACKGROUND_POST_FINDING_COLOR);
        } else {
            if (hasPolicy) {
                g.setPaint(BACKGROUND_POLICY_SET_COLOR);
            } else {
                g.setPaint(BACKGROUND_COLOR);
            }
        }
        g.fill(shape);
        g.setPaint(FOREGROUND_COLOR);
        g.setStroke(getContourStroke());
        g.draw(shape);
        g.setFont(FONT_HELVETICA);
        g.setPaint(TEXT_FOREGROUND_COLOR);

        if (isExpanded()) {
            double rectangleWitdh = points[1].getX() - points[0].getX();
            text = adjustText(text, rectangleWitdh, 3, FONT_HELVETICA, g);
            textWidth = getWidth(text, g);
        }
        double textPosX = getTemporalPosition().getX() - (textWidth / 2);
        double textPosY = points[0].getY() + textHeight;

        g.drawString(text, (float) textPosX, (float) textPosY);

        if (isExpanded()) {
            innerBox.paint(g);
        }

    }
}
