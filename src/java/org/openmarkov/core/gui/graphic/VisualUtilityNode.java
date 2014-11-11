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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.model.network.ProbNode;

/**
 * This class is the visual representation of a utility node.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - fix public and static methods, fix Double comparison
 * @version 1.2 asaez - add expanded representation
 */
public class VisualUtilityNode extends VisualNode {

    /**
     * Internal color of the visual node.
     */
    private static final Color BACKGROUND_COLOR      = OpenMarkovPreferences.getColor(OpenMarkovPreferences.NODEUTILITY_BACKGROUND_COLOR,
                                                             OpenMarkovPreferences.OPENMARKOV_COLORS,
                                                             new Color(208, 230, 178));

    /**
     * Color of lines and letters.
     */
    private static final Color FOREGROUND_COLOR      = OpenMarkovPreferences.getColor(OpenMarkovPreferences.NODEUTILITY_FOREGROUND_COLOR,
                                                             OpenMarkovPreferences.OPENMARKOV_COLORS,
                                                             Color.BLACK);

    /**
     * Color of the letters
     */
    private static final Color TEXT_FOREGROUND_COLOR = OpenMarkovPreferences.getColor(OpenMarkovPreferences.NODEUTILITY_TEXT_COLOR,
                                                             OpenMarkovPreferences.OPENMARKOV_COLORS,
                                                             Color.BLACK);

    /**
     * Creates a new visual node from a node.
     * 
     * @param node
     *            object that has the information of the node.
     * @param panel
     *            editor panel to which this visual node is associated.
     */
    public VisualUtilityNode(ProbNode node, VisualNetwork visualNetwork) {
        super(node, visualNetwork);
        expanded = false;
        preResolutionFinding = false;
        postResolutionFinding = false;
        setTemporalPosition(new Point2D.Double(probNode.getNode().getCoordinateX(),
                probNode.getNode().getCoordinateY()));
        innerBox = new ExpectedValueBox(this);
    }

    /**
     * Returns the X-coordinate of the upper-left corner of the visual node.
     * 
     * @return the X-coordinate of the upper-left corner of the visual node.
     */
    public double getUpperLeftCornerX(Graphics2D g) {
        Point2D.Double[] points = getPoints(g);
        return points[1].getX();
    }

    /**
     * Returns the Y-coordinate of the upper-left corner of the visual node.
     * 
     * @return the Y-coordinate of the upper-left corner of the visual node.
     */
    public double getUpperLeftCornerY(Graphics2D g) {
        Point2D.Double[] points = getPoints(g);
        return points[1].getY();
    }

    /**
     * Returns the six points of the hexagon that limits the node. The order is:
     * first the most left point, then the left and right top points, then the
     * most right point and the right and left bottom points.
     * 
     * @param g
     *            graphic object where the node can be painted.
     * @return an array that contains the six (or four) points of the hexagon.
     */
    private Point2D.Double[] getPoints(Graphics2D g) {

        String text = getNodeString();
        double textHeight = getHeight(text, g);
        double textWidth = getWidth(text, g);
        double centerPosX = getTemporalPosition().getX();
        double centerPosY = getTemporalPosition().getY();

        Double hexagonWidth = 0.0;
        Double hexagonHeight = 0.0;
        double triangleWidth = 0.0;
        Point2D.Double[] points = null;

        if (isExpanded()) {
            hexagonHeight = innerBox.getInnerBoxHeight(g)
                    + textHeight
                    + 2
                    * VERTICAL_SPACE_TO_TEXT
                    + NODE_EXPANDED_HEIGHT_MARGIN
                    * 2;
            hexagonWidth = NODE_EXPANDED_WIDTH + 8.0;
        } else {
            hexagonHeight = textHeight + 2 * VERTICAL_SPACE_TO_TEXT;
            if (textWidth < textHeight) {
                hexagonWidth = DEFAULT_NODE_CONTRACTED_WIDTH;
            } else {
                hexagonWidth = textWidth + 2 * HORIZONTAL_SPACE_TO_TEXT;
            }
        }

        triangleWidth = 8.0;

        points = new Point2D.Double[6];
        points[0] = new Point2D.Double(centerPosX - (hexagonWidth / 2), centerPosY);
        points[3] = new Point2D.Double(centerPosX + (hexagonWidth / 2), centerPosY);
        points[1] = new Point2D.Double(points[0].getX() + triangleWidth, centerPosY
                - (hexagonHeight / 2));
        points[2] = new Point2D.Double(points[3].getX() - triangleWidth, points[1].getY());
        points[4] = new Point2D.Double(points[2].getX(), centerPosY + (hexagonHeight / 2));
        points[5] = new Point2D.Double(points[1].getX(), points[4].getY());

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
        int length = points.length;
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, length);

        polygon.moveTo(points[0].getX(), points[0].getY());
        for (int i = 1; i < length; i++) {
            polygon.lineTo(points[i].getX(), points[i].getY());
        }
        polygon.closePath();

        return polygon;

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
     * Paints the visual node into the graphics object as a hexagon.
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

        g.setPaint(BACKGROUND_COLOR);
        g.fill(shape);
        g.setPaint(FOREGROUND_COLOR);
        g.setStroke(getContourStroke());

        g.draw(shape);
        g.setFont(FONT_HELVETICA);
        g.setPaint(TEXT_FOREGROUND_COLOR);

        if (isExpanded()) {
            double interiorWitdh = points[2].getX() - points[1].getX();
            text = adjustText(text, interiorWitdh, 3, FONT_HELVETICA, g);
            textWidth = getWidth(text, g);
        }

        double textPosX = getTemporalPosition().getX() - (textWidth / 2);
        double textPosY = points[1].getY() + textHeight;

        g.drawString(text, (float) textPosX, (float) textPosY);

        if (isExpanded()) {
            innerBox.paint(g);
        }
    }
}
