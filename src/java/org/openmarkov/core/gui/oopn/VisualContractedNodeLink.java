package org.openmarkov.core.gui.oopn;

import java.awt.Graphics2D;
import java.awt.Shape;

import org.openmarkov.core.gui.graphic.Segment;
import org.openmarkov.core.gui.graphic.VisualArrow;
import org.openmarkov.core.gui.graphic.VisualNode;

public class VisualContractedNodeLink extends VisualArrow
{
    VisualInstance sourceInstance = null;
    VisualNode destNode = null;

    public VisualContractedNodeLink (VisualInstance sourceInstance, VisualNode destNode)
    {
        super (sourceInstance.getPosition(), destNode.getPosition());
        this.sourceInstance = sourceInstance;
        this.destNode = destNode;
    }
    
    /**
     * Returns the shape of the arrow so that it can be selected with the mouse.
     * 
     * @return shape of the arrow.
     */
    @Override
    public Shape getShape(Graphics2D g) {

        setStartPoint(sourceInstance.getCutPoint(new Segment(sourceInstance.getCenter(), destNode.getCenter()), g));
        setEndPoint(destNode.getCutPoint(new Segment(destNode.getCenter(), sourceInstance.getCenter()), g));
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
        
        setStartPoint(sourceInstance.getCutPoint(new Segment(sourceInstance.getCenter(), destNode.getCenter()), g));
        setEndPoint(destNode.getCutPoint(new Segment(destNode.getCenter(), sourceInstance.getCenter()), g));
        
        super.paint(g);
    }   

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(sourceInstance.toString());
        sb.append(" |--> ");
        sb.append(destNode.toString());

        return sb.toString();
    }    
}
