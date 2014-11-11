/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.window.edition;

import java.util.ArrayList;

import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNode;

/**
 * 
 * @author ibermejo
 *
 */
public class SelectedContent
{
    /**
     * Copied nodes
     */
    private ArrayList<ProbNode> nodes;
    
    /**
     * Copied links
     */
    private ArrayList<Link> links;
    
    /**
     * 
     * Constructor for ClipboardContent.
     * @param nodes
     * @param links
     */
    
    public SelectedContent(ArrayList<ProbNode> nodes, ArrayList<Link> links)
    {
        this.nodes = nodes;
        this.links = links;
    }
    
    /**
     * 
     * Copy constructor for ClipboardContent.
     * @param nodes
     * @param links
     */
    public SelectedContent(SelectedContent content) 
    {
        this.nodes = new ArrayList<ProbNode> (content.getNodes ());
        this.links = new ArrayList<Link> (content.getLinks ());
    }    
    
    /**
     * Returns nodes in the clipboard
     * @return nodes in the clipboard
     */
    public ArrayList<ProbNode> getNodes()
    {
        return this.nodes;
    }

    /**
     * Returns links in the clipboard
     * @return links in the clipboard
     */
    public ArrayList<Link> getLinks()
    {
        return this.links;
    }

    /**
     * returns whether the object is empty
     * @return
     */
    public boolean isEmpty ()
    {
        return this.nodes.size () == 0 && this.links.size () == 0;
    }
    
}