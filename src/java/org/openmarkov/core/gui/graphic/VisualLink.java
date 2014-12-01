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
import java.awt.Shape;
import java.awt.geom.Point2D;

import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.model.graph.Link;

/**
 * This class is the visual representation of a link.
 * 
 * @author jmendoza
 * @version 1.0
 */
public class VisualLink extends VisualArrow {

    /**
     * Color of the border when the node is alwaysObserved.
     */
    private static final Color REVELATION_ARC_COLOR = OpenMarkovPreferences.getColor(OpenMarkovPreferences.REVELATION_ARC_VARIABLE,
                                                            OpenMarkovPreferences.OPENMARKOV_COLORS,
                                                            new Color(128, 0, 0));

    /**
     * Highest stroke width. Use "Independence" to get % of this max width
     */
    private static final float max_stroke_width = 25;
    
    /**
     * Object that has the information (included visual information) of the
     * destination node.
     */
    private VisualNode         destination          = null;

    /**
     * Object that has the information (included visual information) of the
     * source node.
     */
    private VisualNode         source               = null;

    /**
     * Object that has the link information.
     */
    private Link               link                 = null;

    /**
     * Creates a new visual link from a link.
     * 
     * @param newLink
     *            object that has the information of the link.
     * @param newSource
     *            source node.
     * @param newDestination
     *            destination node.
     */
    public VisualLink(Link newLink, VisualNode newSource, VisualNode newDestination) {
        super(newSource.getPosition(), newDestination.getPosition(), newLink.isDirected());

        link = newLink;
        source = newSource;
        destination = newDestination;
    }

    /**
     * Returns the source node of the link.
     * 
     * @return the source node of the link.
     */
    public VisualNode getSourceNode() {

        return source;

    }

    /**
     * Returns the destination node of the link.
     * 
     * @return the destination node of the link.
     */
    public VisualNode getDestinationNode() {

        return destination;

    }

    /**
     * Sets the destination node of the link.
     * 
     * @param node
     *            the destination node of the link.
     */
    public void setDestinationNode(VisualNode node) {
        destination = node;
    }

    /**
     * Returns the link associated with the visual link.
     * 
     * @return information of the link.
     */
    public Link getLink() {

        return link;

    }

    /**
     * Returns the shape of the arrow so that it can be selected with the mouse.
     * 
     * @return shape of the arrow.
     */
    @Override
    public Shape getShape(Graphics2D g) {
    	setWidthToSelect(link.getIndependence());
        setStartPoint(new Point2D.Double(source.getTemporalPosition().getX(),
                source.getTemporalPosition().getY()));
        setEndPoint(new Point2D.Double(destination.getTemporalPosition().getX(),
                destination.getTemporalPosition().getY()));
        
        return super.getShape(g);
    }

    /**
     * Paints the visual link into the graphics object.
     * 
     * @param g
     *            graphics object where paint the link.
     */
    @Override
    public void paint(Graphics2D g) {

        // Paint the final arrow when the user releases the button of the
        // mouse
        Segment line;

        try {
            line = new Segment(new Point2D.Double(source.getTemporalPosition().getX(),
                    source.getTemporalPosition().getY()),
                    new Point2D.Double(destination.getTemporalPosition().getX(),
                            destination.getTemporalPosition().getY()));
            //setStroke(new BasicStroke((float) ((link.getIndependence()/100)*max_stroke_width)));
        } catch (IllegalArgumentException e) {

            return;
        }
        
        
        if (link.hasRevealingConditions()) {
            setLinkColor(REVELATION_ARC_COLOR);
        } else {
            setLinkColor(Color.black);
        }

        //setStroke(new BasicStroke((float) ((link.getIndependence()/100)*max_stroke_width)));
        
        boolean hasAbsoluteLinkRestriction = link.hasTotalRestriction();
        setDoubleStriped(hasAbsoluteLinkRestriction);
        setSingleStriped(link.hasRestrictions() && !hasAbsoluteLinkRestriction);
        setStartPoint(source.getCutPoint(line, g));
        setEndPoint(destination.getCutPoint(line, g));

        setIndependence((float) link.getIndependence());
		//String foo = this.source.probNode.getName(); debug statement 
        super.paint(g);
        
    }

}
