/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.oopn;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.gui.graphic.Segment;
import org.openmarkov.core.gui.graphic.VisualElement;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.oopn.Instance;

/**
 * This class is the visual representation of a chance node.
 * 
 * @author ibermejo
 * @version 1.0
 */
public class VisualInstance extends VisualElement {

	protected static final BasicStroke OBSERVED_WIDE_STROKE = new BasicStroke(
			6.0f);

	/**
	 * Default internal color of the visual instance
	 * 
	 */
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	
	/**
	 * Internal color of the visual instance when it is marked as input.
	 */
	private static final Color BACKGROUND_COLOR_INPUT = new Color(0.8f, 0.8f, 0.8f);	

	/**
	 * Color of the letters
	 */
	private static final Color FOREGROUND_COLOR = Color.BLACK;

	/**
	 * Width of a the arc of the rounded rectangle.
	 */
	private static final double ARC_WIDTH = 20;

	/**
	 * Height of a the arc of the rounded rectangle.
	 */
	private static final double ARC_HEIGHT = 20;

	/**
	 * Horizontal margin for the bounding box
	 */
	protected static final double HORIZONTAL_MARGIN = 70;
	
	/**
	 * Vertical margin for the bounding box
	 */
	protected static final double VERTICAL_MARGIN = 35;	
	
	private  static final Font FONT_HELVETICA_BOLD = new Font("Helvetica", Font.BOLD, 15);	

	private  static final Font FONT_HELVETICA_PLAIN = new Font("Helvetica", Font.PLAIN, 15);	
	
    /**
     * Object used to measure text in a specific font.
     */
    private static FontMetrics fontMeter =
        new JPanel().getFontMetrics(FONT_HELVETICA_BOLD);	
	/**
	 * Embedded instance
	 */
	private Instance instance;
	
	/**
	 * Dimensions of the instance
	 */
	private double[] dimensions = new double[6];

	private double topCorner; 
	private double bottomCorner;
	private double leftCorner;
	private double rightCorner;
	
	private List<VisualNode> visualNodes = new ArrayList<>();

	private HashMap<String, VisualInstance> visualSubInstances = new HashMap<String, VisualInstance>();
	
	private boolean isExpanded = true;

	public VisualInstance(Instance instance, List<VisualNode> allVisualNodes, boolean isExpanded)
	{
		this.instance = instance;
		
		// Create visual subInstances
        for(Instance subInstance : instance.getSubInstances().values())
        {
            visualSubInstances.put(subInstance.getName(), new VisualInstance(subInstance, allVisualNodes, false));
        }
        setExpanded (isExpanded);		
		
		List<ProbNode> instanceNodes = new ArrayList<> (instance.getNodes());
        for(Instance subInstance : instance.getSubInstances().values())
        {
            instanceNodes.removeAll (subInstance.getNodes ());
        }
		
        for(ProbNode probNode: instanceNodes)
        {
            for(VisualNode visualNode: allVisualNodes)
            {
                if(probNode.equals(visualNode.getProbNode()))
                {
                    visualNodes.add(visualNode);
                }
            }
        }           
  
        // Calculate bounding box
        topCorner = Double.POSITIVE_INFINITY; 
        bottomCorner = 0.0;
        leftCorner = Double.POSITIVE_INFINITY;
        rightCorner = 0;
        
        for(VisualNode visualNode: visualNodes)
        {

            if(visualNode.getTemporalPosition ().getX () < leftCorner)
            {
                leftCorner = visualNode.getTemporalPosition ().getX ();
            }
            if(visualNode.getTemporalPosition ().getX () > rightCorner)
            {
                rightCorner = visualNode.getTemporalPosition ().getX ();
            }
            if(visualNode.getTemporalPosition ().getY () < topCorner)
            {
                topCorner = visualNode.getTemporalPosition ().getY();
            }
            if(visualNode.getTemporalPosition ().getY() > bottomCorner)
            {
                bottomCorner = visualNode.getTemporalPosition ().getY();
            }                   
        }
        
        for(VisualInstance visualInstance: visualSubInstances.values ())
        {
            if(visualInstance.getCoordinateX () < leftCorner)
            {
                leftCorner = visualInstance.getCoordinateX ();
            }
            if(visualInstance.getCoordinateX () > rightCorner)
            {
                rightCorner = visualInstance.getCoordinateX ();
            }
            if(visualInstance.getCoordinateY () < topCorner)
            {
                topCorner = visualInstance.getCoordinateY();
            }
            if(visualInstance.getCoordinateY () > bottomCorner)
            {
                bottomCorner = visualInstance.getCoordinateY ();
            }             
        }
        
        leftCorner -= 70;
        rightCorner += 90;
        topCorner -= 50;
        bottomCorner += 50;       
        
        dimensions[0] = (isExpanded)? leftCorner : (rightCorner + leftCorner) / 2;
        dimensions[1] = (isExpanded)? topCorner : (bottomCorner + topCorner) / 2;
        dimensions[2] = (isExpanded)? rightCorner - leftCorner : 25;
        dimensions[3] = (isExpanded)? bottomCorner - topCorner: 100;
        dimensions[4] = ARC_WIDTH;
        dimensions[5] = ARC_HEIGHT;          
		
	}
	
    public VisualInstance (Instance instance, List<VisualNode> allVisualNodes)
    {
        this(instance, allVisualNodes, true);
    }

	@Override
	public Shape getShape(Graphics2D g) {

        if(!isExpanded)
        {   
            double textWidth = fontMeter.getStringBounds(toString(), g).getWidth ();
            double textHeight =  fontMeter.getStringBounds(toString(), g).getHeight();
            dimensions[0] = (rightCorner + leftCorner) / 2;
            dimensions[1] = (bottomCorner + topCorner) / 2;
            dimensions[2] =   textWidth + 20;
            dimensions[3] =  textHeight + 3;
        }else
        {
            dimensions[2] = rightCorner - leftCorner;
            dimensions[3] = bottomCorner - topCorner;
        }
        dimensions[4] = ARC_WIDTH;
        dimensions[5] = ARC_HEIGHT;  	    

		return new RoundRectangle2D.Double(dimensions[0], dimensions[1],
				dimensions[2], dimensions[3], dimensions[4], dimensions[5]);
	}

	@Override
	public void paint(Graphics2D g) {
		Shape shape = getShape(g);
		Color backgroundColor= BACKGROUND_COLOR;
		g.setPaint(backgroundColor);
		g.fill(shape);
		g.setPaint(FOREGROUND_COLOR);
		
		String text = adjustText(toString(), dimensions[2], 3, FONT_HELVETICA_BOLD, g);
		g.drawString(text, (float) dimensions[0] + 10.0f, (float) dimensions[1] + 15.0f);
		Stroke s = (isSelected())? WIDE_STROKE : NORMAL_STROKE;
		if(instance.isInput ()) s = (isSelected())? WIDE_DASHED_STROKE : NORMAL_DASHED_STROKE; 
		g.setStroke(s);
		g.draw(shape);
		
		if(isExpanded)
		{
    		for(VisualInstance subInstance : visualSubInstances.values())
    		{
    			subInstance.paint(g);
    		}
		}
	}

	/**
	 * Get instance name
	 * @return
	 */
	public String getName() {
		return instance.getName();
	}
	
	/**
	 * Returns the real position of the instance.
	 * 
	 * @return position of the instance in the screen.
	 */
	@Override
	public Point2D.Double getPosition() {

		return new Point2D.Double(dimensions[0], dimensions[1]);
	}
	
	/**
	 * Returns the center of the instance.
	 * 
	 * @return center of the node in the screen.
	 */
	public Point2D.Double getCenter() {
		return new Point2D.Double (dimensions[0] + dimensions[2]/2, dimensions[1] + dimensions[3]/2);
	}	
	
	public void move(double diffX, double diffY) {
		move(diffX, diffY, true);
	}

	private void move(double diffX, double diffY, boolean moveNodes) {
		dimensions[0] += diffX;
		dimensions[1] += diffY;
		
		leftCorner += diffX;
        rightCorner += diffX;
        topCorner += diffY;
        bottomCorner += diffY;

		for(VisualInstance subInstance : visualSubInstances.values())
		{
			subInstance.move(diffX, diffY, true);
		}
		if(moveNodes)
		{
			for(VisualNode visualNode : visualNodes)
			{
				visualNode.setTemporalPosition(new Point2D.Double(visualNode
						.getTemporalPosition().getX() + diffX, visualNode
						.getTemporalPosition().getY() + diffY));
			}		
		}
	}
	
	public double getCoordinateX()
	{
		return dimensions[0];
	}

	public double getCoordinateY()
	{
		return dimensions[1];
	}
	
	public double getWidth()
	{
		return dimensions[2];
	}
	
	public double getHeight()
	{
		return dimensions[3];
	}

	public boolean isInput() {
		return instance.isInput();
	}
	
	public void setInput(boolean b) {
		instance.setInput(b);
	}

	public Instance getInstance() {
		return instance;
	}

	public VisualInstance getSubInstance(String name) {
		return visualSubInstances.get(name);
	}

	public Point2D.Double getCutPoint(Segment segment, Graphics2D g) {
		
		double radius = dimensions[4] / 2;
		double left = dimensions[0];
		double top = dimensions[1];
		double rectangleWidth = dimensions[2];
		double rectangleHeight = dimensions[3];
		double right = left + rectangleWidth;
		double bottom = top + rectangleHeight;
		Point2D.Double topLeftH = new Point2D.Double(left + radius, top);
		Point2D.Double topRightH = new Point2D.Double(right - radius, top);
		Point2D.Double topRightV = new Point2D.Double(right, top + radius);
		Point2D.Double bottomRightV = new Point2D.Double(right, bottom - radius);
		Point2D.Double bottomRightH = new Point2D.Double(right - radius, bottom);
		Point2D.Double bottomLeftH = new Point2D.Double(left + radius, bottom);
		Point2D.Double bottomLeftV = new Point2D.Double(left, bottom - radius);
		Point2D.Double topLeftV = new Point2D.Double(left, top + radius);
		Point2D.Double circleTLCenter = new Point2D.Double(left + radius, top + radius);
		Point2D.Double circleTRCenter = new Point2D.Double(right - radius, top + radius);
		Point2D.Double circleBLCenter = new Point2D.Double(left + radius, bottom - radius);
		Point2D.Double circleBRCenter = new Point2D.Double(right - radius, bottom - radius);
		Point2D.Double point;
		Point2D.Double[] points;

		// try to find the cut point in the upper horizontal segment of the
		// round rectangle
		point = segment.cutPoint(new Segment(topLeftH, topRightH));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the right vertical segment of the round
		// rectangle
		point = segment.cutPoint(new Segment(topRightV, bottomRightV));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the lower horizontal segment of the
		// round rectangle
		point = segment.cutPoint(new Segment(bottomRightH, bottomLeftH));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the left vertical segment of the round
		// rectangle
		point = segment.cutPoint(new Segment(bottomLeftV, topLeftV));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the upper left corner of the round
		// rectangle
		points = segment.cutPoint(circleTLCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() < circleTLCenter.getX())
						&& (points[i].getY() < circleTLCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the upper right corner of the round
		// rectangle
		points = segment.cutPoint(circleTRCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() > circleTRCenter.getX())
						&& (points[i].getY() < circleTRCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the lower right corner of the round
		// rectangle
		points = segment.cutPoint(circleBRCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() > circleBRCenter.getX())
						&& (points[i].getY() > circleBRCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the lower left corner of the round
		// rectangle
		points = segment.cutPoint(circleBLCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() < circleBLCenter.getX())
						&& (points[i].getY() > circleBLCenter.getY())) {
					return points[i];
				}
			}
		}

		return point;
	}
	
	/**
	 * Returns list of visual nodes
	 * @return
	 */
	public List<VisualNode> getVisualNodes()
	{
	    return visualNodes;
	}

    /**
     * Returns list of visual nodes
     * @return
     */
    public List<VisualNode> getVisualNodes(boolean recursive)
    {
        List<VisualNode> visualNodes = new ArrayList<VisualNode>(this.visualNodes);
        if(recursive)
        {
            for(VisualInstance visualSubInstance : visualSubInstances.values ())
            {
                visualNodes.addAll (visualSubInstance.getVisualNodes (recursive));
            }
        }
        return visualNodes;
    }	

	/**
	 * Returns the parameter in the position given (if any)
	 * @param position
	 * @param g
	 * @return
	 */
	public VisualInstance getParameterInPosition(Point2D.Double position, Graphics2D g) {
		VisualInstance instance = null;
		VisualInstance instanceFound = null;

		Iterator<VisualInstance> iterator = visualSubInstances.values().iterator();
		while ((instanceFound == null) && iterator.hasNext()) {
			instance = iterator.next();
			if (instance.isInput() && instance.pointInsideShape(position, g)) {
				instanceFound = instance;
			}
		}
		return instanceFound;
	}

    /**
     * Returns the isExpanded.
     * @return the isExpanded.
     */
    public boolean isExpanded ()
    {
        return isExpanded;
    }

    /**
     * Sets the isExpanded.
     * @param isExpanded the isExpanded to set.
     */
    public void setExpanded (boolean isExpanded)
    {
//        dimensions[0] = (isExpanded)? leftCorner : (leftCorner + rightCorner) / 2;
//        dimensions[1] = (isExpanded)? topCorner : (topCorner + bottomCorner) / 2;
//
////        dimensions[2] = rightCorner - leftCorner;
////        dimensions[3] = bottomCorner - topCorner;
//        dimensions[2] = (isExpanded)? rightCorner - leftCorner : 20;
//        dimensions[3] = (isExpanded)? bottomCorner - topCorner : 100;
//        dimensions[4] = ARC_WIDTH;
//        dimensions[5] = ARC_HEIGHT;           

        this.isExpanded = isExpanded;
        
    }
    
    @Override
    public String toString()
    {
    	return FilenameUtils.getBaseName (instance.getClassNet().getName()) + " " + instance.getName();
    }

    public Collection<VisualInstance> getSubInstances ()
    {
        return visualSubInstances.values ();
    }
}
