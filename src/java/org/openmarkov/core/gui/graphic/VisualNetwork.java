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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.InvertLinkEdit;
import org.openmarkov.core.action.PNESupport;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.util.MovedNodeInfo;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.oopn.Instance.ParameterArity;
import org.openmarkov.core.oopn.action.MarkAsInputEdit;
import org.openmarkov.learning.core.util.LearningEditProposal;
import org.openmarkov.learning.core.util.ScoreEditMotivation;
import org.openmarkov.learning.core.util.LearningEditMotivation;
import org.openmarkov.learning.core.util.LearningEditProposal;




/**
 * This class implements the visual representation of a network.
 * 
 * @author jmendoza 1.0
 * @author jlgozalo 1.1
 * @version 1.1 Javadoc tags corrected, variables initialized * 
 * @version 1.2 asaez - modified the constructor, the creation of
 *              visual nodes and the order of painting nodes
 */
public class VisualNetwork implements PNUndoableEditListener {

	
	/**
	 * Network whose visual representation is managed by this object.
	 */
	protected ProbNet probNet = null;
	
	/**
	 * This variable indicates if nodes must be drawn by title.
	 */
	// TODO este valor debe asignarse usando las preferencias de usuario de
	// visualización de redes
	protected boolean byTitle = false;
	
	protected List<VisualLink> proposedLinks = new ArrayList<VisualLink>();
	
	/**
	 * List of visual nodes.
	 */
	protected List<VisualNode> visualNodes = new ArrayList<VisualNode>();

	/**
	 * List of visual links.
	 */
	protected List<VisualLink> visualLinks = new ArrayList<VisualLink>();

	/**
	 * Set of selected nodes.
	 */
	protected Set<VisualNode> selectedNodes = new HashSet<VisualNode>();

	/**
	 * Set of selected links.
	 */
	protected Set<VisualLink> selectedLinks = new HashSet<VisualLink>();
	
    /**
     * This object represents the arrow that is painted when a new link is being
     * created.
     */
    protected VisualArrow newLink = null;

    /**
     * This object represents the source node of a new link.
     */
    protected VisualNode newLinkSource = null;
    
    /**
     * Rectangle used to select various nodes.
     */
    protected SelectionRectangle selection = null;    
    
    protected boolean isPropagationActive = true;
    
    protected int workingMode = NetworkPanel.EDITION_WORKING_MODE;
    
    /**
	 * Listener to the selection.
	 */
	protected Set<SelectionListener> selectionListeners =
		new HashSet<SelectionListener>();

	protected Graphics2D g2;

	private double maxMotivation = Double.POSITIVE_INFINITY;

	private double minMotivation = Double.NEGATIVE_INFINITY;
	private BufferedImage legendImage;

	//private LinkWrapper linkWrapper;
	/**
	 * Position of the mouse cursor when it is pressed.
	 */

	/** 
	 * Creates a new visual network.
	 * 
	 * @param probNet
	 *            object that has the information of the network.
	 * @param editorPanel
	 *            editor panel associated to this network.
	 */ 
	public VisualNetwork(ProbNet probNet) {
        
		this.probNet = probNet;
        this.probNet.getPNESupport().addUndoableEditListener(this);
		
		//network.addNetworkChangeListener(this);
		//changed by mpalacios
		constructVisualInfo();
		try {
        	URL url = getClass().getClassLoader().getResource("images/legend.png");
        	File img = new File(url.getPath());
        	legendImage = ImageIO.read(img);
        } catch (IOException e) {
            e.printStackTrace();
      }
	}

	/**
	 * Calculates the width and height of the panel according to the position of
	 * the left-most and bottom-most nodes.
	 * 
	 * @param g
	 *            graphics where the network is painted.
	 * @return an array that contains the lowest and highest X coordinate and
	 *         the lowest and highest Y coordinate.
	 */
	public double[] getNetworkBounds(Graphics2D g) {

		double[] networkBounds =
			{ Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE,
				Double.MIN_VALUE };
		Rectangle2D nodeBounds = null;
	

		for (VisualNode node : visualNodes) {
			nodeBounds = node.getShape(g).getBounds2D();
			networkBounds[0] = Math.min(nodeBounds.getMinX(), networkBounds[0]);
			networkBounds[1] = Math.max(nodeBounds.getMaxX(), networkBounds[1]);
			networkBounds[2] = Math.min(nodeBounds.getMinY(), networkBounds[2]);
			networkBounds[3] = Math.max(nodeBounds.getMaxY(), networkBounds[3]);
		}
		networkBounds[0] -= 2;
		networkBounds[1] += 2;
		networkBounds[2] -= 2;
		networkBounds[3] += 2;

		return networkBounds;

	}

	/**
	 * This method constructs the lists of the visual nodes and visual links. It
	 * only creates visual information for the new nodes and links and delete
	 * the visual representation of the nodes and links that don't exist.
	 */
	protected void constructVisualInfo() {

		List<ProbNode> nodes = null;
		List<VisualNode> vNodesToDelete = new ArrayList<VisualNode>();
		List<VisualLink> vLinksToDelete = new ArrayList<VisualLink>();
		List<Link> links = null;
		ProbNode nodeToCheck = null;
		Link linkToCheck = null;
		VisualNode vNode1 = null;
		VisualNode vNode2 = null;
		int i = -1;
		int visualNodesCount = -1;

		
		nodes = probNet.getProbNodes();
		for (VisualNode vNode : visualNodes) {
			nodeToCheck = vNode.getProbNode();
			int index = nodes.indexOf(nodeToCheck);
			if ( index >=0 && vNode.getTemporalPosition().getX() == nodes.get(
					index).getNode().getCoordinateX() && vNode.
					getTemporalPosition().getX() == nodes.get( index ).getNode().
					getCoordinateX() ){
			//if (nodes.contains(nodeToCheck)  ) {
				//nodes.indexOf(o)
				nodes.remove(nodeToCheck);
			
			} else {
				vNodesToDelete.add(vNode);
			}
		}
		
		
		
		visualNodes.removeAll(vNodesToDelete);
		for (ProbNode node : nodes) {
			vNode1 = createVisualNode(node);
			visualNodes.add(vNode1);
			vNode1.setByTitle(byTitle);
			
		}
		
		//links = probNet.backupProbNet.getLinks();
		links = probNet.getGraph().getLinks();
		
		for (VisualLink vLink : visualLinks) {
			linkToCheck = vLink.getLink();
			if (links.contains(linkToCheck) && !containsNodeToDelete(linkToCheck, vNodesToDelete)) {
				links.remove(linkToCheck);
			} else {
				vLinksToDelete.add(vLink);
			}
		}
		visualLinks.removeAll(vLinksToDelete);
		visualNodesCount = visualNodes.size();
		for (Link link : links) {
			if (link.getLookAhead() == 0) {
				i = 0;
				vNode1 = null;
				vNode2 = null;
				while ((i < visualNodesCount) && ((vNode1 == null) || (vNode2 == null))) {
					if (vNode1 == null) {
						if (link.getNode1().equals(
							visualNodes.get(i).getProbNode().getNode())) {
							vNode1 = visualNodes.get(i);
						}
					}
					if (vNode2 == null) {
						if (link.getNode2().equals(
							visualNodes.get(i).getProbNode().getNode())) {
							vNode2 = visualNodes.get(i);
						}
					}
					i++;
				}
				if ((vNode1 != null) && (vNode2 != null)) {
					visualLinks.add(new VisualLink(link, vNode1, vNode2));
				}
			}
		}
		
	}

	/**
	 * Returns whether the link contains nodes to delete
	 * @param linkToCheck
	 * @param vNodesToDelete
	 * @return
	 */
	protected boolean containsNodeToDelete(Link linkToCheck, List<VisualNode> vNodesToDelete) {
		
		for (VisualNode vNode: vNodesToDelete)
		if (linkToCheck.contains(vNode.getProbNode().getNode()))
			return true;
		
			return false;
	}

	/**
	 * Changes the presentation mode of the text of the nodes.
	 * 
	 * @param value
	 *            new value of the presentation mode of the text of the nodes.
	 */
	public void setByTitle(boolean value) {

		if (byTitle != value) {
			byTitle = value;
		}
		for (VisualNode node : visualNodes) {
			node.setByTitle(value);
		}

	}

	/**
	 * Returns the presentation mode of the text of the nodes.
	 * 
	 * @return true if the title of the nodes is the name or false if it is the
	 *         name.
	 */
	public boolean getByTitle() {

		return byTitle;

	}

	/**
	 * Creates a new list of visual nodes reordering them following
	 * this criteria: 
	 * - first criteria: selection state -> the selected nodes are in
	 *   the first places of the array.
	 * - second criteria: relevance -> the higher the relevance 
	 *   the nearer to the start of the array.
	 * 
	 * @return a new ordered array (first, selected nodes, and last, 
	 * 			non selected nodes; each group is ordered in 
	 * 			descending relevance criteria).
	 */
	private ArrayList<VisualNode> reorderVisualNodes() {
		int selPos = 0;
		ArrayList<VisualNode> newList = new ArrayList<VisualNode>();
		ArrayList<VisualNode> nodesSelected = new ArrayList<VisualNode>();
		ArrayList<VisualNode> nodesUnselected = new ArrayList<VisualNode>();

		for (VisualNode node : visualNodes) {
			if (node.isSelected()) {
				nodesSelected.add(node);
			} else {
				nodesUnselected.add(node);
			}
		}
		
		int selected = nodesSelected.size();
		int counter1 = 0;
		while (counter1 < selected) {
			VisualNode candidate = null;
			double highestRelevance = -1;
			for (int i=0; i<nodesSelected.size(); i++) {
				double relevance = nodesSelected.get(i).getProbNode().getRelevance();
				if (relevance > highestRelevance) {
					highestRelevance = relevance;
					candidate = nodesSelected.get(i);
				}
			}
			newList.add(selPos, candidate);
			selPos++;
			nodesSelected.remove(candidate);
			counter1++;
		}
		
		int unselected = nodesUnselected.size();
		int counter2 = 0;
		while (counter2 < unselected) {
			VisualNode candidate = null;
			double highestRelevance = -1;
			for (int i=0; i<nodesUnselected.size(); i++) {
				double relevance = nodesUnselected.get(i).getProbNode().getRelevance();
				if (relevance > highestRelevance) {
					highestRelevance = relevance;
					candidate = nodesUnselected.get(i);
				}
			}
			newList.add(selPos, candidate);
			selPos++;
			nodesUnselected.remove(candidate);
			counter2++;
		}
		
		return newList;
	}

	/**
	 * repaints all nodes and links
	 * pbs experiment
	 */
	public void repaint() {
		paintNodes(g2);
		paintLinks(g2);
		//paint(g2);
	}
	
	/**
	 * Paints the nodes. The nodes are painted in reverse order of its
	 * position in the array. It means that the selected nodes are
	 * always shown at first plane; and the nodes with higher relevance
	 * are shown ahead of those with lower if they have the same selection
	 * state
	 * 
	 * @param g
	 *            the graphics context in which to paint.
	 */
	protected void paintNodes(Graphics2D g) {
		//what if selectedLinks also not null
//		if (selectedNodes != null) {
//			//make the selected node green
//			//make all the other nodes grey?
//			for (VisualNode n : visualNodes) {
//				if (n.isSelected()) {
//					
//				} else {
//					
//				}
//				//make them green
//			}
//			visualNodes = reorderVisualNodes();
//			
//		}else{
			visualNodes = reorderVisualNodes();
			for (int i = (visualNodes.size() - 1); i >= 0; i--) {
				if(visualNodes.get(i).isVisible ())
				{
				    visualNodes.get(i).paint(g);
				}
			}
		//}
	}
	
	/*
	 * for Edit View, when nodes are selected, should only be called by paintLinks
	 */
	private void paintLinksWhenNodeSelected(Graphics2D g) {
		for (VisualNode n : selectedNodes){
			// iterate through all the rest of the node and find what's the best move between them
			Collection<LearningEditProposal> proposals = n.probNode.getProposedEdits();
			for (LearningEditProposal proposal : proposals) {
				PNEdit proposedEdit = proposal.getEdit();
				LearningEditMotivation proposedEditMotivation = proposal.getMotivation();
				if (proposedEdit instanceof AddLinkEdit) {
					AddLinkEdit newEdit = (AddLinkEdit) proposedEdit;
					VisualNode source = null;
					VisualNode destination = null;
					for (VisualNode node : visualNodes) {
						if (newEdit.node1.equals(node.probNode)) {
							source = node;
						}
						if (newEdit.node2.equals(node.probNode	)) {
							destination = node;
						}
					}
					Link l = new Link(newEdit.node1.node, newEdit.node2.node, true);
					l.setLookAhead(1);
					VisualLink tempLink = new VisualLink(l, source, destination);
					tempLink.paint(g);
					probNet.removeLink(newEdit.node1, newEdit.node2, true);
				} else if (proposedEdit instanceof RemoveLinkEdit) {
					RemoveLinkEdit newEdit = (RemoveLinkEdit) proposedEdit;
					VisualNode source = null;
					VisualNode destination = null;
					for (VisualNode node : visualNodes) {
						if (newEdit.node1.equals(node.probNode)) {
							source = node;
						}
						if (newEdit.node2.equals(node.probNode	)) {
							destination = node;
						}
					}
					Link l = new Link(newEdit.node1.node, newEdit.node2.node, true);
					l.setLookAhead(2);
					VisualLink tempLink = new VisualLink(l, source, destination);
					tempLink.paint(g);
					probNet.removeLink(newEdit.node1, newEdit.node2, true);
				} else {
					InvertLinkEdit newEdit = (InvertLinkEdit) proposedEdit;
					VisualNode source = null;
					VisualNode destination = null;
					for (VisualNode node : visualNodes) {
						if (newEdit.node1.equals(node.probNode)) {
							source = node;
						}
						if (newEdit.node2.equals(node.probNode	)) {
							destination = node;
						}
					}
					Link l = new Link(newEdit.node1.node, newEdit.node2.node, true);
					l.setLookAhead(3);
					VisualLink tempLink = new VisualLink(l, source, destination);
					tempLink.paint(g);
					probNet.removeLink(newEdit.node1, newEdit.node2, true);
				}
			}
		}
		for (VisualLink visualLink : visualLinks) {
			VisualNode source = visualLink.getSourceNode();
			VisualNode destination = visualLink.getDestinationNode();
			//selectednodes does not contian source or destination
			if (!selectedNodes.contains(source) && !selectedNodes.contains(destination)) {
				visualLink.paintGrayLink(g);
			}
		}
	}
	
	/**
	 * Look-ahead mode, should only be called by paintLinks
	 */
	
	private void paintLinksWhenLookahead(Graphics2D g) {
		for (VisualLink visualLink : visualLinks) {
			visualLink.paint(g);
		}
		
	}

	/**
	 * Paints the links.
	 * 
	 * @param g
	 *            the graphics context in which to paint.
	 */
	protected void paintLinks(Graphics2D g) {
		if (selectedNodes.size() != 0) {
			paintLinksWhenNodeSelected(g);
		}
		 else {
			
			for (VisualLink visualLink : visualLinks) {
//				if (probNet.getLookAheadButton() == true) {
//					visualLink.getLink().setLookAhead(4);
//				}
				visualLink.paint(g);
			}
//			probNet.setLookAheadButton(false);
		}

	}

	/**
	 * Overwrited 'paint' method to avoid to call it explicitly.
	 * 
	 * @param g
	 *            the graphics context in which to paint.
	 */
	public void paint(Graphics2D g) {
        this.g2 = g ;
		paintLinks(g);
		paintNodes(g);
        if (newLink != null)
        {
            newLink.paint (g);
        }
        if(selection != null)
        {
            selection.paint (g);
        }
        if (selectedNodes.size() > 0) {
        	g.drawImage(legendImage, 800, 300, null);
        }
     }

	/**
	 * Checks if is there a node in a position. You must specify if the node
	 * must be selected or not.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if there is a (selected or not) node in the position, returns it,
	 *         else, returns null.
	 */
	public VisualNode whatNodeInPosition(Point2D.Double position, Graphics2D g) {

		VisualNode node = null;
		VisualNode nodeFound = null;
		int index = 0, length = visualNodes.size();

		while ((nodeFound == null) && (index < length)) {
			node = visualNodes.get(index++);
			if (node.pointInsideShape(position, g)) {
				nodeFound = node;
			}
		}

		return nodeFound;

	}
	
	/**
	 * Checks if is there a inner box in a position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if there is a inner box in the position, returns it,
	 *         else, returns null.
	 */
	public InnerBox whatInnerBoxInPosition(Point2D.Double position, Graphics2D g) {

		InnerBox innerBox = null;
		InnerBox innerBoxFound = null;
		VisualNode node = null;
		int index = 0;
		int nodesLength = visualNodes.size();
		while ((innerBoxFound == null) && (index < nodesLength)) {
			node = visualNodes.get(index++);
			if (node.pointInsideShape(position, g)) {
				innerBox = node.getInnerBox();
				if (innerBox.pointInsideShape(position, g)) {
					innerBoxFound = innerBox;
				}
			}
		}
		return innerBoxFound;
	}
	
	/**
	 * Checks if is there a visual state in a position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if there is a visual state in the position, returns it,
	 *         else, returns null.
	 */
	public VisualState whatStateInPosition(Point2D.Double position, Graphics2D g) {

		VisualState state = null;
		VisualState stateFound = null;
		VisualNode node = null;
		int index = 0;
		int nodesLength = visualNodes.size();
		while ((stateFound == null) && (index < nodesLength)) {
			node = visualNodes.get(index++);
			if (node.pointInsideShape(position, g)) {
				if (node.getInnerBox() instanceof FSVariableBox) {
					int numStates = ((FSVariableBox)node.getInnerBox()).getNumStates();
					for (int i=0; i<numStates; i++) {
						state = ((FSVariableBox)node.getInnerBox()).getVisualState(i);
						if (state.pointInsideShape(position, g)) {
							stateFound = state;
						}
					}
				}
			}
		}
		return stateFound;
	}

	/**
	 * Checks if is there a link in a position. You must specify if the link
	 * must be selected or not.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if there is a (selected or not) link in the position, returns it,
	 *         else, returns null.
	 */
	public VisualLink whatLinkInPosition(Point2D.Double position, Graphics2D g) {

		VisualLink link = null;
		VisualLink linkFound = null;
		int index = 0;
		int length = visualLinks.size();

		while ((linkFound == null) && (index < length)) {
			link = visualLinks.get(index++);
			if (link.pointInsideShape(position, g)) {
				linkFound = link;
			}
		}

		return linkFound;

	}

	/**
	 * Checks if is there any selected element in a position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if a selected element is there in the position, returns the
	 *         element, else returns null.
	 */
    public VisualElement getElementInPosition (Point2D.Double position, Graphics2D g)
    {
        VisualElement elementSelected = null;
        if ((elementSelected = whatNodeInPosition (position, g)) == null)
        {
            elementSelected = whatLinkInPosition (position, g);
        }
        return elementSelected;
    }

	/**
	 * Sets the selection state of an element.
	 * 
	 * @param element
	 *            element to be selected/deselected.
	 * @param selected
	 *            new selection state.
	 */
	protected void setSelectedElement(VisualElement element, boolean selected) {

		if (selected != element.isSelected()) {
			if (element instanceof VisualNode) {
				if (selected) {
					selectedNodes.add((VisualNode) element);
				} else {
					selectedNodes.remove(element);
				}
			} else if (element instanceof VisualLink){
				if (selected) {
					selectedLinks.add((VisualLink) element);
				} else {
					selectedLinks.remove(element);
				}
			} 
			notifyObjectsSelected();
			element.setSelected(selected);
		}

	}

	/**
	 * Sets the selection state of a node.
	 * 
	 * @param node
	 *            node to be selected/deselected.
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedNode(VisualNode node, boolean selected) {

		setSelectedElement(node, selected);

	}
	

    /**
     * Sets the selection state of a node identified by its name.
     * 
     * @param name
     *            name of the node to be selected/deselected.
     * @param selected
     *            new selection state.
     */
    public void setSelectedNode(String name, boolean selected) {

        boolean found = false;
        int i = 0, l = visualNodes.size();

        while (!found && (i < l)) {
            if (visualNodes.get(i).getProbNode().getName().equals(name)) {
                setSelectedElement(visualNodes.get(i), selected);
                found = true;
            } else {
                i++;
            }
        }

    }	

	/**
	 * Sets the selection state of a link.
	 * 
	 * @param link
	 *            link to be selected/deselected.
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedLink(VisualLink link, boolean selected) {

		setSelectedElement(link, selected);

	}
	
    /**
     * Sets the selection state of a link.
     * 
     * @param link
     *            link to be selected/deselected.
     * @param selected
     *            new selection state.
     */
    public void setSelectedLink(Link link, boolean selected) {
        int i=0;
        VisualLink visualLink = null;
        while(visualLink == null && i<visualLinks.size ())
        {
            if( visualLinks.get (i).getLink ().equals (link))
            {
                visualLink = visualLinks.get (i);
            }
            ++i;
        }
        if(visualLink!=null)
        {
            setSelectedElement(visualLink, selected);
        }
    }	
    
	/**
	 * Selects all nodes.
	 * 
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedAllNodes(boolean selected) {

		for (VisualNode node : visualNodes) {
			setSelectedElement(node, selected);
		}
		
		if(!selected)
		{
			selectedNodes.clear();
		}

	}

	/**
	 * Selects all links.
	 * 
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedAllLinks(boolean selected) {

		for (VisualLink link : visualLinks) {
			setSelectedElement(link, selected);
		}
		
		if(!selected)
		{
			selectedLinks.clear();
		}		

	}
	
	/**
	 * Selects all nodes and links.
	 * 
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedAllObjects(boolean selected) {

		setSelectedAllNodes(selected);
		setSelectedAllLinks(selected);
	}

	/**
	 * Move some nodes an amount in both axis. The parameter 'selected'
	 * indicates if the nodes must be selected or it doesn't mind.
	 * 
	 * @param diffX
	 *            X-axis movement.
	 * @param diffY
	 *            Y-axis movement.
	 * @param selected
	 *            if true, only the selected nodes are moved; if false, all
	 *            nodes are moved.
	 */
	private void moveNodes(double diffX, double diffY, boolean selected) {

		//ProbNode nodeWrapper = null;

		for (VisualNode node : visualNodes) {
			//nodeWrapper = node.getProbNode();
			if (!selected || (node.isSelected())) {
				
				
				/*MoveNodeEdit moveNodeEdit = new MoveNodeEdit(node.getProbNode(), 
						node.getProbNode().getNode().getCoordinateX() + 
						diffX, node.getProbNode().getNode().getCoordinateY() + diffY);
				
				try {
					probNet.getPNESupport().announceEdit(moveNodeEdit);
					probNet.getPNESupport().doEdit(moveNodeEdit);
				} catch (ConstraintViolationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				
				//nodeWrapper.getNode().coordinateX =+ diffX;
				//nodeWrapper.getNode().coordinateY =+ diffY;
				//node.setTemporalPosition(diffX, diffY);
				node.setTemporalPosition(new Point2D.Double(node.
						getTemporalPosition().getX() + diffX,
						node.getTemporalPosition().getY() + diffY ));
				if(g2!=null)
				{
				    node.paint((Graphics2D) g2);
				}
				
				//constructVisualInfo();
			}
		}

	}

    /**
     * Move the selected elements an amount in both axis.
     * @param diffX X-axis movement.
     * @param diffY Y-axis movement.
     */
    public void moveSelectedElements (double diffX, double diffY)
    {
        moveSelectedNodes(diffX, diffY);
    }	
	
	/**
	 * Move the selected nodes an amount in both axis.
	 * 
	 * @param diffX
	 *            X-axis movement.
	 * @param diffY
	 *            Y-axis movement.
	 */
	protected void moveSelectedNodes(double diffX, double diffY) {

		moveNodes(diffX, diffY, true);

	}

	/**
	 * Move all the nodes an amount in both axis.
	 * 
	 * @param diffX
	 *            X-axis movement.
	 * @param diffY
	 *            Y-axis movement.
	 */
	protected void moveAllNodes(double diffX, double diffY) {

		moveNodes(diffX, diffY, false);

	}
	
	/**
	 * Selects the nodes and links that are inside the selection rectangle and deselects
	 * the ones that are outside.
	 * 
	 * @param selection
	 *            object that manages the selection.
	 */
	public void selectElementsInsideSelection(SelectionRectangle selection) {

	    setSelectedAllNodes (false);
	    setSelectedAllLinks (false);
	    // Select nodes
	    ArrayList<VisualNode> selectedVisualNodes = new ArrayList<VisualNode> (); 
		for (VisualNode node : visualNodes) {
		    if(selection.containsNode(node)){
		        setSelectedElement(node, true);
		        selectedVisualNodes.add (node);
		    }
		}
		// Select links
		for(VisualLink selectedLink: getLinksOfNodes (selectedVisualNodes, true))
		{
		    setSelectedElement(selectedLink, true);
		}
	}

	/**
	 * Fills the array of information of the selected nodes and their actual
	 * state.
	 * 
	 * @return list where are the moved nodes information.
	 */
	public List<MovedNodeInfo> fillActualNodesMovedInfo() {

		List<MovedNodeInfo> movedNodes = new ArrayList<MovedNodeInfo>();

		for (VisualNode node : visualNodes) {
			if (node.isSelected()) {
				movedNodes.add(new MovedNodeInfo(node.getProbNode(), node
					.getPosition()));
			}
		}

		return movedNodes;

	}

	/**
	 * Fills the array of information of the selected nodes and the differences
	 * of their state.
	 * 
	 * @param movedNodes
	 *            list where is saved the moved nodes information.
	 */
    public void fillDifferencesNodesMovedInfo(List<MovedNodeInfo> movedNodes) {

		ProbNode probNodeAux = null;

		for (MovedNodeInfo movedNode : movedNodes) {
			probNodeAux = movedNode.getProbNode();
			movedNode.setDiffPosition(new Point2D.Double(
					probNodeAux.getNode().getCoordinateX()
				- movedNode.getDiffPosition().getX(), 
				probNodeAux.getNode().getCoordinateY() 
				- movedNode.getDiffPosition().getY()));
		}

	}
	
	/**
	 * Fills the array of information of the selected nodes and their actual
	 * state.
	 * 
	 * @return list where are the moved nodes information.
	 */
	public List<VisualNode> fillVisualNodesSelected() {

		List<VisualNode> movedNodes = new ArrayList<>();

		for (VisualNode node : visualNodes) {
			if (node.isSelected()) {
				movedNodes.add(node);
			}
		}

		return movedNodes;

	}
	
	/**
	 * This method returns a list containing all the nodes in the network.
	 * 
	 * @return a list containing all the nodes in the network.
	 */
	public List<VisualNode> getAllNodes() {

		return visualNodes;

	}

    /**
     * This method returns a list that contains all the links that leave of or
     * arrive in one node of the list of nodes passed as parameter.
     * @param nodes list of nodes whose links are returned.
     * @param onlyBothEnds returns only those links whose two ends are selected
     * @return a list of links related to the nodes.
     */
    public List<VisualLink> getLinksOfNodes (List<VisualNode> nodes, boolean onlyBothEnds)
    {
        ArrayList<VisualLink> links = new ArrayList<VisualLink> ();
        int i, l = nodes.size ();
        boolean found = false;
        boolean foundSource = false;
        boolean foundDestination = false;
        for (VisualLink visualLink : visualLinks)
        {
            found = false;
            foundSource = false;
            foundDestination = false;
            i = 0;
            while (!found && (i < l))
            {
                foundSource |= visualLink.getSourceNode ().equals (nodes.get (i));
                foundDestination |= visualLink.getDestinationNode ().equals (nodes.get (i));
                found = (onlyBothEnds)? foundSource && foundDestination : foundSource || foundDestination;  
                if (found)
                {
                    links.add (visualLink);
                }
                else
                {
                    i++;
                }
            }
        }
        return links;
    }
	
    /**
     * This method returns a list that contains all the links that leave of or
     * arrive in one node of the list of nodes passed as parameter.
     * @param nodes list of nodes whose links are returned.
     * @return a list of links related to the nodes.
     */
    public List<VisualLink> getLinksOfNodes (List<VisualNode> nodes)
    {
        return getLinksOfNodes (nodes, false);
    }

	/**
	 * Sets a new selection listener.
	 * 
	 * @param listener
	 *            listener to be set.
	 */
	public void addSelectionListener(SelectionListener listener) {

		selectionListeners.add(listener);

	}

	/**
	 * This method returns a list containing the selected nodes.
	 * 
	 * @return a list containing the selected nodes.
	 */
	public List<VisualNode> getSelectedNodes() {

		return new ArrayList<VisualNode>(selectedNodes);

	}

	/**
	 * This method returns a list containing the selected links.
	 * 
	 * @return a list containing the selected links.
	 */
	public List<VisualLink> getSelectedLinks() {

		return new ArrayList<VisualLink>(selectedLinks);

	}
	
	/**
	 * Returns the number of selected nodes.
	 * 
	 * @return number of selected nodes.
	 */
	public int getSelectedNodesNumber() {

		return selectedNodes.size();

	}

	/**
	 * Returns the number of selected links.
	 * 
	 * @return number of selected links.
	 */
	public int getSelectedLinksNumber() {

		return selectedLinks.size();

	}

	/**
	 * Notifies to the registered selection listener how many nodes and links
	 * are selected, and which are the especific selected nodes. 
	 * Also notifies this situation to the menu assistant.
	 */
	protected void notifyObjectsSelected() {

		for (SelectionListener listener : selectionListeners) {
			listener.objectsSelected(
				getSelectedNodes(), getSelectedLinks());
		}
	}

	/**
	 * Returns the network that is painted by this object.
	 * 
	 * @return network which is painted.
	 */
	public ProbNet getNetwork() {

		return probNet;

	}
	public PNESupport getpNESupport() {
//review method
		return probNet.getPNESupport();

	}

	
	public void undoableEditHappened(UndoableEditEvent e) {
			
		
		//if (edit instanceof AddVariableEdit){
		/*if (edit instanceof AddProbNodeEdit){
			
			
			String name=((AddVariableEdit)edit).getVariable().getName();
			ProbNode newProbNode;
			try {
				newProbNode = pNESupport.getProbNet().getProbNode(name);
				nodeWrapper =
				createNewNonamedNode(newProbNode, cursorPosition);
			} catch (ProbNodeNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				//JOptionPane.showMessageDialog(
					//	Utilities.getOwner(this), e2.getMessage(), stringResource
						//	.getString("ErrorWindow.Title.Label"),
						//JOptionPane.ERROR_MESSAGE);
					}
			
			}else if (edit instanceof AddLinkEdit){
			
				}*/
	
			constructVisualInfo();
		
	}

		
	public void undoableEditWillHappen(UndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException {
		// TODO Auto-generated method stub
		
	}

	/*public void setLinkWrapper(ProbNode sourceNode,
		ProbNode destinationNode) {
		//LinkWrapper link = null;
		NodeType sourceNodeType = null;
		NodeType destinationNodeType = null;
		ProbNode sourceProbNode = null;
		ProbNode destinationProbNode = null;
		ArrayList<ProbNode> probNodes = probNet.getProbNodes();

		sourceNodeType = sourceNode.getNodeType();
		destinationNodeType = destinationNode.getNodeType();
		sourceProbNode = sourceNode;
		destinationProbNode = destinationNode;
		//nodes exists
		if (!probNodes.contains(sourceProbNode)
			|| !probNodes.contains(destinationProbNode)) {
			//throw new Exception(stringResource
				//.getString("LinkedNodesNotExist.Text.Label"));
			//nodes different
		} else if (sourceProbNode.equals(destinationProbNode)) {
			//throw new Exception(stringResource
				//.getString("LinkNotAllowed.Text.Label"));
			//node utility partner
		} else if ((sourceNodeType == NodeType.UTILITY)
			&& (destinationNodeType != NodeType.UTILITY)) {
			//throw new Exception(stringResource
				//.getString("LinkNotAllowed.Text.Label"));
		}
		//link don´t exists
		if (probNet.getGraph().getLink(
			sourceProbNode.getNode(), destinationProbNode.getNode(), true) != null) {
			//throw new Exception(stringResource.getString(
				//"LinkExists.Text.Label", sourceNode.getName(), destinationNode
					//.getName()));
		}
		//inverse link exists
		if (probNet.getGraph().getLink(
			destinationProbNode.getNode(), sourceProbNode.getNode(), true) != null) {
			//throw new Exception(stringResource.getString(
				//"InverseLinkExists.Text.Label", sourceNode.getName(),
				//destinationNode.getName()));
		}
		
		/*try {
			this.getGraph().addLink(
				sourceProbNode.getNode(), destinationProbNode.getNode(), true);
		} catch (Exception e) {
			throw new Exception(stringResource.getString(
				"LinkMakesCycle.Text.Label", sourceNode.getName(),
				destinationNode.getName()));
		}*/
		/*linkWrapper =
			new LinkWrapper(probNet.getGraph().getLink(
				sourceProbNode.getNode(), destinationProbNode.getNode(), true),
				sourceNode, destinationNode);
	
	}*/
	
		
	/**
	 * Returns different types of visual nodes according to the supplied node.
	 * 
	 * @param node
	 *            node whose visual representation is going to be returned.
	 * @return the visual representation of the node.
	 */
	protected VisualNode createVisualNode(ProbNode node) {

		switch (node.getNodeType()) {
			case CHANCE: {
				return new VisualChanceNode(node, this);
			}
			case DECISION: {
			    return new VisualDecisionNode(node, this);
			}
			case UTILITY: {
				return new VisualUtilityNode(node, this);
			}
			default: {
				return null;
			}
		}

	}

	
	public void undoEditHappened(UndoableEditEvent event) {
		//Object p=event.getSource();
		//ProbNet p2=(ProbNet)p;
		//if (edit instanceof AddVariableEdit){
		/*if (edit instanceof AddProbNodeEdit){
			
			
			String name=((AddVariableEdit)edit).getVariable().getName();
			ProbNode newProbNode;
			try {
				newProbNode = pNESupport.getProbNet().getProbNode(name);
				nodeWrapper =
				createNewNonamedNode(newProbNode, cursorPosition);
			} catch (ProbNodeNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				//JOptionPane.showMessageDialog(
					//	Utilities.getOwner(this), e2.getMessage(), stringResource
						//	.getString("ErrorWindow.Title.Label"),
						//JOptionPane.ERROR_MESSAGE);
					}
			
			}else if (edit instanceof AddLinkEdit){
			
				}*/
	
			constructVisualInfo();
		
	}
	
	public void setProbNet(ProbNet probNet) {
		if(!this.probNet.equals(probNet))
		{
			this.probNet = probNet;
			setSelectedAllObjects(false);
			clean();
			constructVisualInfo();
		}
	}

	protected void clean ()
    {
        visualNodes.clear ();
        visualLinks.clear ();
        selectedNodes.clear ();
        selectedLinks.clear ();
        newLink = null;
        newLinkSource = null;
    }

    /**
	 * Adds whatever is in that position to the selection
	 * @param cursorPosition
	 * @param g
	 */
    public void addToSelection (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualNode node = null;
        VisualLink link = null;
        
        if ((node = whatNodeInPosition (cursorPosition, g)) != null)
        {
            setSelectedNode (node, !node.isSelected ());
        }
        else if ((link = whatLinkInPosition (cursorPosition, g)) != null)
        {
            setSelectedLink (link, !link.isSelected ());
        }
    }

    /**
     * Cleans selection and sets it to whatever is in the cursorPosition
     * @param cursorPosition
     * @param g
     * @return true if there is an element in the position
     */
    public VisualElement selectElementInPosition (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualNode node = null;
        VisualLink link = null;
        VisualElement selectedElement = null;
        if ((node = whatNodeInPosition (cursorPosition, g)) != null)
        {
            if (!node.isSelected ())
            {
                setSelectedAllObjects (false);
                setSelectedNode (node, true);
            }
            selectedElement = node;
        }
        else if ((link = whatLinkInPosition (cursorPosition, g)) != null)
        {
            if (!link.isSelected ())
            {
                setSelectedAllObjects (false);
                setSelectedLink (link, true);
            }
            selectedElement = link;
        }
        else
        {
            setSelectedAllObjects (false);
        } 
        return selectedElement;
    }

    /**
     * Starts link creation
     * @param cursorPosition
     * @param g
     */
    public void startLinkCreation (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualNode node = null;
        
        if ((node = whatNodeInPosition (cursorPosition, g)) != null)
        {
            newLink = new VisualArrow (node.getPosition (), cursorPosition);
            newLinkSource = node;
        }  
    }

    public void updateLinkCreation (java.awt.geom.Point2D.Double position)
    {
        if(newLink!=null)
        {
            newLink.setEndPoint(position);
        }
    }

    /**
     * Finishes link creation and returns edit for the new link
     * @param point
     * @param g
     * @return
     */
    public PNEdit finishLinkCreation (java.awt.geom.Point2D.Double point, Graphics2D g)
    {
        PNEdit linkEdit = null;
        if (newLink != null)
        {
            newLink = null;
            VisualNode newLinkDestination = null;
            if(newLinkSource != null)
            {
                if ((newLinkDestination = whatNodeInPosition (point, g)) != null)
                {
                    if (!newLinkSource.equals (newLinkDestination))
                    {
                        try {
                            linkEdit = new AddLinkEdit(probNet,
                                    probNet.getVariable(newLinkSource
                                            .getProbNode().getName()),
                                    probNet.getVariable(newLinkDestination
                                            .getProbNode().getName()), true);
                        } catch (ProbNodeNotFoundException e1) {/* Cannot happen */
                        }
                    }
                }
                newLinkSource = null;
            }
        }
        return linkEdit;
    }

    public void startSelectionRectangle (Point2D.Double position)
    {
        selection = new SelectionRectangle ();
        selection.initSelection(position, 0, 0);        
    }

    public void finishSelectionRectangle (Point2D.Double position)
    {
        selection.clearSelectionSquare();
    }

    public void updateSelectionRectangle (double diffX, double diffY)
    {
        selection.setSize(selection.getWidth() + diffX,
                          selection.getHeight() + diffY);
        selectElementsInsideSelection(selection);
    }
    
    /**
     * Returns the isPropagationActive.
     * @return the isPropagationActive.
     */
    public boolean isPropagationActive ()
    {
        return isPropagationActive;
    }

    /**
     * Sets the isPropagationActive.
     * @param isPropagationActive the isPropagationActive to set.
     */
    public void setPropagationActive (boolean isPropagationActive)
    {
        this.isPropagationActive = isPropagationActive;
    }

    public void setWorkingMode (int workingMode)
    {
        this.workingMode = workingMode;
    }  
    public int getWorkingMode ()
    {
        return workingMode;
    }

    //TODO OOPN start
    public void markSelectedAsInput ()
    {
        for(VisualNode visualNode : getSelectedNodes ())
        {
        	MarkAsInputEdit markAsInputEdit = new MarkAsInputEdit(probNet, !visualNode.getProbNode ().isInput (), visualNode.getProbNode ());
        	try {
				probNet.doEdit(markAsInputEdit);
			} catch (ConstraintViolationException
					| CanNotDoEditException | NonProjectablePotentialException
					| WrongCriterionException | DoEditException e) {
				e.printStackTrace();
			}
        }
    }

    public void editClass ()
    {
        // TODO Auto-generated method stub
    }

	public void setParameterArity(ParameterArity arity) {
		// TODO Auto-generated method stub
	}
    //TODO OOPN end
    
    public void selectElement (VisualElement selectedElement)
    {
        setSelectedAllObjects (false);
        setSelectedElement (selectedElement, true);
    }
    
    public double getMinMotivation()
    {
    	if (minMotivation == Double.NEGATIVE_INFINITY){
    		computeMotivationExtrema();
    	}
    	return minMotivation;
    }
    
    public double getMaxMotivation()
    {
    	if (maxMotivation == Double.POSITIVE_INFINITY){
    		computeMotivationExtrema();
    	}
    	return maxMotivation;
    }
    
    public void computeMotivationExtrema(){
		maxMotivation =  Double.NEGATIVE_INFINITY;
		minMotivation = Double.POSITIVE_INFINITY;
		double bestEditMotivationScore;
		for (ProbNode node : getNetwork().getProbNodes()){
			PriorityQueue<LearningEditProposal> edits = (PriorityQueue<LearningEditProposal>) node.getProposedEdits();
			if (edits == null || edits.isEmpty())
				bestEditMotivationScore = 0;
			else {
				ScoreEditMotivation bestEditMotivation = (ScoreEditMotivation) edits.peek().getMotivation();
				bestEditMotivationScore = bestEditMotivation.getScore();
			}
			maxMotivation = (maxMotivation > bestEditMotivationScore) ? maxMotivation : bestEditMotivationScore;
			minMotivation = (minMotivation < bestEditMotivationScore) ? minMotivation : bestEditMotivationScore;		
		}
    }
    
    public void resetMotivationExtrema() {
    	maxMotivation = Double.POSITIVE_INFINITY;
    	minMotivation = Double.NEGATIVE_INFINITY;
    }
}
