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
import java.awt.Stroke;
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

	private static final int NON_LOOKAHEAD = 0;
	private static final int LOOKAHEAD_ADD = 1;
	private static final int LOOKAHEAD_DELETE = 2;
	private static final int LOOKAHEAD_INVERT = 3;
	private static final int LOOKAHEAD_BUTTON = 4;


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
		} else if (link.getLookAhead() != NON_LOOKAHEAD) {
			if (link.getLookAhead() == LOOKAHEAD_ADD) {
				setLinkColor(Color.green);
			} else if (link.getLookAhead() == LOOKAHEAD_DELETE) {
				setLinkColor(Color.red);
			} else if(link.getLookAhead() == LOOKAHEAD_INVERT) {
				setLinkColor(Color.blue);
			} else if (link.getLookAhead() == LOOKAHEAD_BUTTON || this.getLookAheadState() == LookAheadState.LOOKAHEAD_NEW_ADD) {
				setLinkColor(Color.gray);
			}
		} else {
			setLinkColor(Color.black);
		}	


		boolean hasAbsoluteLinkRestriction = link.hasTotalRestriction();
		setDoubleStriped(hasAbsoluteLinkRestriction);
		setSingleStriped(link.hasRestrictions() && !hasAbsoluteLinkRestriction);
		setStartPoint(source.getCutPoint(line, g));

		setEndPoint(destination.getCutPoint(line, g));

		setIndependence((float) link.getIndependence());
		
		

		
//		Point2D.Double newEndPoint = fixEndPointWithEdgeWidth(this.endPoint);
//		setEndPoint(newEndPoint);
		//String foo = this.source.probNode.getName(); debug statement 
		super.paint(g);

	}
	
	protected Stroke getStroke() {
		if (getLookAheadState() == LookAheadState.NORMAL && link.getLookAhead() == VisualLink.NON_LOOKAHEAD) {
            setStroke(INDEPENDENCE);
        } else {
        	setStroke(2.0);
        }
		return this.stroke;
	}

	private Point2D.Double fixEndPointWithEdgeWidth(Point2D.Double original) {
		Double x = original.getX();
		Double y = original.getY();
		Double indep = (double) INDEPENDENCE / 8;
		//depending on direction?
		int direction = getDirection();
		if (direction == 1) {
			original.setLocation(x + indep, y + indep);
		} else if (direction == 2) {
			original.setLocation(x - indep, y + indep);
		} else if (direction == 3) {
			original.setLocation(x + indep, y - indep);
		} else {
			original.setLocation(x - indep, y - indep);
		}
		return original;
	}

	private int getDirection() {
		VisualNode source = getSourceNode();
		VisualNode destination = getDestinationNode();
		double x1 = source.getPosition().getX();
		double y1 = source.getPosition().getY();
		double x2 = destination.getPosition().getX();
		double y2 = destination.getPosition().getY();
		if (x2 <= x1 && y2 <= y1) {
			return 1;
		} else if (x2 >= x1 && y2 <= y1) {
			return 2;
		} else if (x2 <= x1 && y2 >= y1) {
			return 3;
		} else {
			return 4;
		}
	}

	/**
	 * Paint existing links to light gray for edit view
	 */
	public void paintGrayLink(Graphics2D g){
		Segment line;

		try {
			line = new Segment(new Point2D.Double(source.getTemporalPosition().getX(),
					source.getTemporalPosition().getY()),
					new Point2D.Double(destination.getTemporalPosition().getX(),
							destination.getTemporalPosition().getY()));
		} catch (IllegalArgumentException e) {

			return;
		}
		setLinkColor(Color.GRAY);

		boolean hasAbsoluteLinkRestriction = link.hasTotalRestriction();
		setDoubleStriped(hasAbsoluteLinkRestriction);
		setSingleStriped(link.hasRestrictions() && !hasAbsoluteLinkRestriction);
		setStartPoint(source.getCutPoint(line, g));
		setEndPoint(destination.getCutPoint(line, g));

		super.paint(g);
	}

}
