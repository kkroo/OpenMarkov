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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import org.openmarkov.core.model.network.ProbNode;



/**
 * This abstract class specifies the methods that all visual nodes have to
 * implement.
 * 
 * @author jmendoza 1.0
 * @author jlgozalo 1.1
 * @version 1.1 node always use name (not title)
 * 				minor changes to syntax due to warnings and version included
 * 				New size of the bubble
 * @version 1.2 asaez - The class is defined as abstract
 * 				Some new constants, attributes and methods are defined
 */
public abstract class VisualNode extends VisualElement {

	/**
	 * Font type Helvetica, bold, size 15.
	 */
	protected static final Font FONT_HELVETICA =
			new Font("Helvetica", Font.BOLD, 15);

	/**
	 * Font type Helvetica, normal, size 12.
	 */
	protected static final Font FONT_HELVETICA_SMALL =
			new Font("Helvetica", Font.PLAIN, 12);

	/**
	 * Default width of a node when it is contracted. It is the width that it
	 * has if the length of its name is shorter enough; otherwise, its width is
	 * adjusted to fit the length of the name.
	 */
	protected static final double DEFAULT_NODE_CONTRACTED_WIDTH = 40;
	
	/**
	 * Width of a node when it is expanded.
	 */
	protected static final double NODE_EXPANDED_WIDTH = 205;
	
	/**
	 * Vertical margin of a node when it is expanded.
	 */
	protected static final double NODE_EXPANDED_HEIGHT_MARGIN = 5;
	
	/**
	 * Space from the left border of the node to the text.
	 */
	protected static final double HORIZONTAL_SPACE_TO_TEXT = 15;

	/**
	 * Space from the top border of the node to the text.
	 */
	protected static final double VERTICAL_SPACE_TO_TEXT = 4;

	/**
	 * Object used to measure text in a specific font.
	 */
	private static FontMetrics fontMeter =
		new JPanel().getFontMetrics(FONT_HELVETICA);
	
	/**
	 * Visual Network to which this visual node is associated.
	 */
	protected VisualNetwork visualNetwork;

	/**
	 * Object that has the node information.
	 */
	protected ProbNode probNode;

	/**
	 * Object that manages the internal representation of the node when
	 * it is expanded.
	 */
	protected InnerBox innerBox;

	/**
	 * This variable determines if the node is going to be painted
	 * expanded (true) or contracted (false). 
	 */	
	protected boolean expanded;

	/**
	 * This variable indicates if the node has a pre-Resolution finding 
	 * established (true) or not (false). 
	 */	
	protected boolean preResolutionFinding;
	
	/**
	 * This variable indicates if the node has a post-Resolution finding 
	 * established (true) or not (false). 
	 */	
	protected boolean postResolutionFinding;
	//TODO Debería ser un array de booleanos, con un valor por cada caso de evidencia
	//     (esto no pasa en el caso del preResol.)
	
	/**
	 * This variable influences the width of the node.
	 */
	protected boolean byTitle = false;

	/**
	 * Value of the X coordinate in temporal position of the node.
	 */
	protected int temporalCoordinateX;

	/**
	 * Value of the Y coordinate in temporal position of the node.
	 */
	protected int temporalCoordinateY;
	
	public VisualNode(ProbNode node, VisualNetwork visualNetwork)
	{
	    this.probNode = node;
	    this.visualNetwork = visualNetwork;
	}

	/**
	 * Returns the height of the visual node. It's calculated depending on the
	 * font of the node and the text that appears in it.
	 * 
	 * @param text
	 *            text that appears in the visual node.
	 * @param g
	 *            graphics object where to paint the element.
	 * @return the height of the visual node.
	 */
	protected static double getHeight(String text, Graphics2D g) {

		return fontMeter.getStringBounds(text, g).getHeight();
	}

	/**
	 * Returns the width of the visual node. It's calculated depending on the
	 * font of the node and the text that appears in it.
	 * 
	 * @param text
	 *            text that appears in the visual node.
	 * @param g
	 *            graphics object where to paint the element.
	 * @return the height of the visual node.
	 */
	protected static double getWidth(String text, Graphics2D g) {

		return fontMeter.getStringBounds(text, g).getWidth();
	}

	/**
	 * Returns the real position of the node.
	 * 
	 * @return position of the node in the screen.
	 */
	@Override
	public Point2D.Double getPosition() {

		return new Point2D.Double(probNode.getNode().getCoordinateX(),
				probNode.getNode().getCoordinateY());
	}

	/**
	 * Sets the position of the node.
	 * 
	 * @param value
	 *            new position.
	 */
	public void setPosition(Point2D.Double value) {

		probNode.getNode().setCoordinateX((int) value.getX());
		probNode.getNode().setCoordinateY((int) value.getY());
	}
	
	/**
	 * Returns the temporal position of the node in the screen.
	 * 
	 * @return position of the node in the screen.
	 */
	public Point2D.Double getTemporalPosition() {

		return new Point2D.Double(temporalCoordinateX,
				temporalCoordinateY);
	}

	/**
	 * Sets the temporal position of the node.
	 * 
	 * @param value
	 *            new position.
	 */
	public void setTemporalPosition(Point2D.Double value) {

		temporalCoordinateX = (int) value.getX();
		temporalCoordinateY = (int) value.getY();
	}

	/**
	 * Returns the string that must appear into the node. The variable 'byTitle'
	 * influences this string. If 'byTitle' is true and the node hasn't a title,
	 * then the name is used as title.
	 * 
	 * @return the string that must appear into the node.
	 */
	protected String getNodeString() {

		return probNode.getName();
	}


	/**
	 * Returns the node associated with the visual node.
	 * 
	 * @return information of the node.
	 */
	public ProbNode getProbNode() {
		return probNode;
	}

	/**
	 * Returns the InnerBox associated with the visual node.
	 * 
	 * @return innerBox associated with the visual node.
	 */
	public InnerBox getInnerBox() {
		return innerBox;
	}
	
	/**
	 * Sets the inner box associated to the node.
	 * 
	 * @param innerBox
	 *            new inner box associated to the node.
	 */
	public void setInnerBox(InnerBox innerBox) {
		this.innerBox = innerBox;
	}
	

	/**
	 * Returns true if the string of the node must be the title; otherwise,
	 * false.
	 * 
	 * @return true if the node will show the title; otherwise, false.
	 */
	public boolean getByTitle() {

		return byTitle;
	}

	/**
	 * Changes the type of the text (name or title) that appears inside the
	 * node.
	 * 
	 * @param newByTitle
	 *            true if the title of the node will be shown; false if the name
	 *            will be shown.
	 */
	public void setByTitle(boolean newByTitle) {

		byTitle = newByTitle;
	}

	/**
	 * Returns true if the node will be painted expanded; false if contracted.
	 * 
	 * @return true if the node will be painted expanded.
	 */	
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Establishes the way in which the node will be painted: expanded if true; 
	 * contracted if false.
	 * 
	 * @param expanded
	 *            true if the node has to be represented expanded; false if contracted.
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	/**
	 * Returns true if the node has a pre-Resolution finding established.
	 * 
	 * @return true if the node has a pre-Resolution finding established.
	 */	
	public boolean isPreResolutionFinding() {
		return this.preResolutionFinding;
	}

	/**
	 * Sets if the node has a pre-Resolution finding established or not.
	 * 
	 * @param findingInNode
	 *            true if the node has a pre-Resolution finding established.
	 */
	public void setPreResolutionFinding(boolean findingInNode) {
		this.preResolutionFinding = findingInNode;
	}
	
	/**
	 * Returns true if the node has a post-Resolution finding established.
	 * 
	 * @return true if the node has a post-Resolution finding established.
	 */	
	public boolean isPostResolutionFinding() {
		return this.postResolutionFinding;
	}

	/**
	 * Sets if the node has a post-Resolution finding established or not.
	 * 
	 * @param findingInNode
	 *            true if the node has a post-Resolution finding established.
	 */
	public void setPostResolutionFinding(boolean findingInNode) {
		this.postResolutionFinding = findingInNode;
	} 
	
	/**
	 * Returns true if the node has a finding established (pre or post-Resolution).
	 * 
	 * @return true if the node has a finding established (pre or post-Resolution).
	 */	
	public boolean hasAnyFinding() {
		return this.preResolutionFinding || this.postResolutionFinding;
	}
	
	// ESCA-JAVA0173: allows unused arguments
	/**
	 * Returns the point where the segment cuts with the border of the node.
	 * 
	 * @param segment
	 *            segment that cuts the border of the node.
	 * @param g
	 *            graphic object where the node can be painted.
	 * @return the point where the segments cuts the border or null if it
	 *         doesn't.
	 */
	public abstract Point2D.Double getCutPoint(Segment segment, Graphics2D g);
	
	/**
	 * Returns the X-coordinate of the upper-left corner of the visual node.
	 * 
	 * @return the X-coordinate of the upper-left corner of the visual node.
	 */
	public abstract double getUpperLeftCornerX(Graphics2D g);
	
	/**
	 * Returns the Y-coordinate of the upper-left corner of the visual node.
	 * 
	 * @return the Y-coordinate of the upper-left corner of the visual node.
	 */
	public abstract double getUpperLeftCornerY(Graphics2D g);
	
    /**
     * Returns the text's height of node's name.
     * 
     * @return the text's height of node's name.
     */
    public double getTextHeight(Graphics2D g) {
        return getHeight(getNodeString(), g);
    }

    /**
     * Returns the visualNetwork.
     * @return the visualNetwork.
     */
    public VisualNetwork getVisualNetwork ()
    {
        return visualNetwork;
    }
    
    /**
     * Returns stroke to be used for the contour
     * @return
     */
    protected Stroke getContourStroke()
    {
    	Stroke s = null;
    	if (probNode.isInput ()) {
		    s = (isSelected())? WIDE_DASHED_STROKE : NORMAL_DASHED_STROKE;
		} else {
            s = (isSelected())? WIDE_STROKE : NORMAL_STROKE;
		}    	
    	return s;
    }
    
    @Override
    public Point2D.Double getCenter()
    {
    	return getTemporalPosition();
    }
    
    @Override
    public String toString()
    {
    	StringBuilder sb = new StringBuilder();
    	sb.append(probNode.getName());
    	sb.append(" - ");
    	sb.append(getPosition());
    	return sb.toString();
    }

}
