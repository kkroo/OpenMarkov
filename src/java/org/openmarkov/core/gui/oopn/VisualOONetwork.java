/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.oopn;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.graphic.SelectionListener;
import org.openmarkov.core.gui.graphic.SelectionRectangle;
import org.openmarkov.core.gui.graphic.VisualArrow;
import org.openmarkov.core.gui.graphic.VisualElement;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNetwork;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.oopn.Instance;
import org.openmarkov.core.oopn.Instance.ParameterArity;
import org.openmarkov.core.oopn.InstanceReferenceLink;
import org.openmarkov.core.oopn.NodeReferenceLink;
import org.openmarkov.core.oopn.OOPNet;
import org.openmarkov.core.oopn.ReferenceLink;
import org.openmarkov.core.oopn.action.AddReferenceLinkEdit;
import org.openmarkov.core.oopn.action.ChangeParameterArityEdit;
import org.openmarkov.core.oopn.action.MarkAsInputEdit;

public class VisualOONetwork extends VisualNetwork
{
    /**
     * HashMap of visual instances.
     */
    private Map<String, VisualInstance> visualInstances;

    /**
     * List of visual reference links.
     */
    private List<VisualReferenceLink> visualReferenceLinks;

    /**
     * List of visual contracted node links.
     */
    private List<VisualContractedNodeLink> visualContractedNodeLinks;
    
    
    /**
     * Set of selected instances.
     */
    private Set<VisualInstance> selectedInstances = new HashSet<VisualInstance>();  

    /**
     * Set of selected parameter links.
     */
    private Set<VisualReferenceLink> selectedReferenceLinks = new HashSet<VisualReferenceLink>();  

    private VisualInstance newInstanceLinkSource;    

    public VisualOONetwork (OOPNet probNet)
    {
        super (probNet);
        selectedInstances = new HashSet<> ();
        selectionListeners = new HashSet<> ();
    }

    /**
     * Constructs visual elements
     */
    @Override
    protected void constructVisualInfo ()
    {
        super.constructVisualInfo ();
       
        if(probNet instanceof OOPNet && getWorkingMode () == NetworkPanel.EDITION_WORKING_MODE)
        {
            // construct visual instances
            if(visualInstances == null)
            {
                visualInstances = new HashMap<> ();
            }
            visualInstances.clear();
            for(String instanceName : ((OOPNet)probNet).getInstances().keySet())
            {
                visualInstances.put(instanceName, new VisualInstance(((OOPNet)probNet).getInstances().get(instanceName), visualNodes));
            }
            
            // construct visual parameter links
            if(visualReferenceLinks == null)
            {
                visualReferenceLinks = new ArrayList<> ();
            }
            visualReferenceLinks.clear();
            for(ReferenceLink link : ((OOPNet)probNet).getReferenceLinks())
            {
                if(link instanceof InstanceReferenceLink)
                {
                	InstanceReferenceLink instanceLink = (InstanceReferenceLink)link;             	
                	VisualInstance sourceVisualInstance = visualInstances.get(instanceLink.getSourceInstance().getName());
                	VisualInstance destVisualInstance = visualInstances.get(instanceLink.getDestInstance().getName()).getSubInstance(instanceLink.getDestSubInstance().getName());
                	visualReferenceLinks.add(new VisualReferenceLink(link, sourceVisualInstance, destVisualInstance));
                }else if(link instanceof NodeReferenceLink)
                {
                	NodeReferenceLink nodeLink = (NodeReferenceLink)link;
                	VisualNode sourceNode = getVisualNode(nodeLink.getSourceNode());
                	VisualNode destinationNode = getVisualNode(nodeLink.getDestinationNode());
                	visualReferenceLinks.add(new VisualReferenceLink(link, sourceNode, destinationNode));
                }
            } 
            
            HashMap<VisualNode, VisualInstance> contractedNodes = getContractedNodes(visualInstances.values ());
            // Do not paint nodes of contracted instances
            visualNodes.removeAll (contractedNodes.keySet ());
            // Do not paint links to nodes of contracted instances, paint contracted node links instead
            if(visualContractedNodeLinks == null)
            {
                visualContractedNodeLinks = new ArrayList<> ();
            }
            visualContractedNodeLinks.clear();
            
            Collection<VisualLink> linksToRemove = new HashSet<> ();
            for(VisualLink visualLink : visualLinks)
            {
                if(contractedNodes.containsKey (visualLink.getSourceNode ()))
                {
                    linksToRemove.add (visualLink);
                    if(!contractedNodes.containsKey (visualLink.getDestinationNode ()))
                    {
                        visualContractedNodeLinks.add (new VisualContractedNodeLink (contractedNodes.get(visualLink.getSourceNode ()), 
                                                                                 visualLink.getDestinationNode ()));
                    }
                }
            }
            visualLinks.removeAll (linksToRemove);
        }
    }
    
    private HashMap<VisualNode, VisualInstance> getContractedNodes (Collection<VisualInstance> visualInstances)
    {
        HashMap<VisualNode, VisualInstance> contractedNodes = new HashMap<> ();
        
        for(VisualInstance visualInstance : visualInstances)
        {
            if(!visualInstance.isExpanded ())
            {
                for(VisualNode visualNode : visualInstance.getVisualNodes ())
                {
                    contractedNodes.put (visualNode, visualInstance);
                }
            }
            contractedNodes.putAll (getContractedNodes (visualInstance.getSubInstances ()));
        }
        return contractedNodes;
    }

    private VisualNode getVisualNode(ProbNode sourceNode) {
		VisualNode visualNode = null;
    	int i = 0;
		while (visualNode == null && i < visualNodes.size())
		{
			if(sourceNode.equals(visualNodes.get(i).getProbNode()))
			{
				visualNode = visualNodes.get(i);
			}
			++i;
		}
		return visualNode;
	}


	/**
     * Paints the instances.
     * 
     * @param g
     *            the graphics context in which to paint.
     */
    protected void paintInstances(Graphics2D g) {

        for (VisualInstance visualInstance : visualInstances.values()) {
            visualInstance.paint(g);
        }

    }
    
    /**
     * Paints the reference links.
     * 
     * @param g
     *            the graphics context in which to paint.
     */
    protected void paintReferenceLinks(Graphics2D g) {

        for (VisualReferenceLink visualReferenceLink : visualReferenceLinks) {
            visualReferenceLink.paint(g);
        }

    }   
    
    /**
     * Paints the links for contracted nodes.
     * 
     * @param g
     *            the graphics context in which to paint.
     */
    protected void paintContractedNodeLinks(Graphics2D g) {

        for (VisualContractedNodeLink visualContractedNodeLink : visualContractedNodeLinks) {
            visualContractedNodeLink.paint(g);
        }

    }      
    
    /**
     * Overwrited 'paint' method to avoid to call it explicitly.
     * 
     * @param g
     *            the graphics context in which to paint.
     */
    @Override
    public void paint(Graphics2D g) {
        paintInstances(g);
        paintReferenceLinks(g);
        paintContractedNodeLinks(g);
        super.paint (g);
    }
    
    /**
     * Returns the instance in the given position if there is one, null otherwise
     * @param position
     * @param g
     * @return
     */
    public VisualInstance getInstanceInPosition (java.awt.geom.Point2D.Double position,
                                                  Graphics2D g)
    {
        VisualInstance instance = null;
        VisualInstance instanceFound = null;
        Iterator<VisualInstance> iterator = visualInstances.values ().iterator ();
        while ((instanceFound == null) && iterator.hasNext ())
        {
            instance = iterator.next ();
            if (instance.pointInsideShape (position, g))
            {
                instanceFound = instance;
            }
        }
        return instanceFound;
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
    @Override
    public VisualElement getElementInPosition(Point2D.Double position,
                                                Graphics2D g) {

        VisualElement elementSelected = null;

        if ((elementSelected = super.getElementInPosition (position, g)) == null) {
        	if ((elementSelected = getReferenceLinkInPosition (position, g)) == null) {
        		elementSelected = getInstanceInPosition(position, g);
        	}
        }

        return elementSelected;

    }
    
    public VisualElement getReferenceLinkInPosition(Double position,
			Graphics2D g) {
		VisualReferenceLink linkFound = null;
		int i = 0;
		int length = visualReferenceLinks.size();

		while ((linkFound == null) && (i < length)) {
			if (visualReferenceLinks.get(i).pointInsideShape(position, g)) {
				linkFound = visualReferenceLinks.get(i);
			}
			++i;
		}

		return linkFound;
	}


	/**
     * Sets the selection state of an element.
     * 
     * @param element
     *            element to be selected/deselected.
     * @param selected
     *            new selection state.
     */
    @Override
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
            } else if (element instanceof VisualInstance){
                if (selected) {
                    selectedInstances.add((VisualInstance) element);
                } else {
                    selectedInstances.remove(element);
                }
            } else if (element instanceof VisualReferenceLink){
                if (selected) {
                    selectedReferenceLinks.add((VisualReferenceLink) element);
                } else {
                	selectedReferenceLinks.remove(element);
                }
            }
            notifyObjectsSelected();
            element.setSelected(selected);
        }
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
            if(listener instanceof OOSelectionListener)
            {
                ((OOSelectionListener)listener).objectsSelected(
                                         getSelectedNodes(), getSelectedLinks(), getSelectedInstances(), getSelectedReferenceLinks());
            }
        }
    }    
    
    /**
     * Sets the selection state of an instance.
     * 
     * @param node
     *            node to be selected/deselected.
     * @param selected
     *            new selection state.
     */
    public void setSelectedInstance(VisualInstance instance, boolean selected) {

        setSelectedElement(instance, selected);
    }
    
    /**
     * Sets the selection state of a node identified by its name.
     * 
     * @param name
     *            name of the node to be selected/deselected.
     * @param selected
     *            new selection state.
     */
    public void setSelectedInstance(String name, boolean selected) {

        boolean found = false;
        int i = 0;

        while (!found && i < visualInstances.size()) {
            if (visualInstances.get(i).getName().equals(name)) {
                setSelectedElement(visualInstances.get(i), selected);
                found = true;
            } else {
                i++;
            }
        }
    }    
    
    /**
     * Selects/deselects all instances.
     * 
     * @param selected
     *            new selection state.
     */
    public void setSelectedAllInstances(boolean selected) {

        for (VisualInstance instance : visualInstances.values()) {
            setSelectedElement(instance, selected);
        }

        if(!selected)
        {
            selectedInstances.clear();
        }
    }
    
    /**
     * Selects/deselects all parameter links
     * @param selected
     */
    private void setSelectedAllReferenceLinks(boolean selected) {
        for (VisualReferenceLink link : visualReferenceLinks){
            setSelectedElement(link, selected);
        }

        if(!selected)
        {
            selectedReferenceLinks.clear();
        }
	}    
    
    /**
     * Selects all nodes and links.
     * 
     * @param selected
     *            new selection state.
     */
    @Override
    public void setSelectedAllObjects(boolean selected) {

        super.setSelectedAllObjects (selected);
        setSelectedAllInstances(selected);
        setSelectedAllReferenceLinks(selected);
    }

	/**
     * Move the selected nodes an amount in both axis.
     * 
     * @param diffX
     *            X-axis movement.
     * @param diffY
     *            Y-axis movement.
     */
    public void moveSelectedInstances(double diffX, double diffY) {

        for (VisualInstance instance : visualInstances.values()) {
            if (instance.isSelected()) {
                instance.move(diffX, diffY);
                if(g2!=null)
                {
                    instance.paint((Graphics2D) g2);
                }
            }
        }
    }   
    
    /**
     * Selects the nodes and links that are inside the selection rectangle and deselects
     * the ones that are outside.
     * 
     * @param selection
     *            object that manages the selection.
     */
    @Override
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
        
        for (VisualInstance instance : visualInstances.values()) {
            if (selection.containsRectangle (instance.getCoordinateX (),
                                             instance.getCoordinateY (), 
                                             instance.getWidth (),
                                             instance.getHeight ()))
            {
                setSelectedElement (instance, true);
                for(VisualNode node: instance.getVisualNodes())
                {
                	setSelectedElement(node, false);
                	selectedVisualNodes.remove(node);
                }
            }
        }       
    }
    
    /**
     * Returns the list of nodes belonging to the selected instances
     * 
     * @return
     */
    public ArrayList<VisualNode> getVisualNodesOfSelectedInstances() {
        ArrayList<VisualNode> visualNodes = new ArrayList<VisualNode>();

        for (VisualInstance instance : visualInstances.values()) {
            if (instance.isSelected()) {
                visualNodes.addAll(instance.getVisualNodes(true));
            }
        }
        return visualNodes;
    }    
    
    /**
     * This method returns a list containing the selected instances.
     * 
     * @return a list containing the selected instances.
     */
    public ArrayList<VisualInstance> getSelectedInstances() {

        return new ArrayList<VisualInstance>(selectedInstances);

    }  
    
    /**
     * This method returns a list containing the selected parameter links.
     * 
     * @return a list containing the selected parameter links.
     */
    public ArrayList<VisualReferenceLink> getSelectedReferenceLinks() {

        return new ArrayList<VisualReferenceLink>(selectedReferenceLinks);
    }          
    
    public Map<String, VisualInstance> getVisualInstances() {
		return visualInstances;
	}

	public List<VisualReferenceLink> getVisualReferenceLinks() {
		return visualReferenceLinks;
	}

	/**
     * 
     */
    @Override
    public void addToSelection (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualNode node = null;
        VisualLink link = null;
        VisualInstance instance = null;
        
        if ((instance = getInstanceInPosition (cursorPosition, g)) != null)
        {
            setSelectedInstance (instance, !instance.isSelected ());
        }
        else if ((node = whatNodeInPosition (cursorPosition, g)) != null)
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
     * @return element in the position given, null if none
     */
    @Override
    public VisualElement selectElementInPosition (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualElement selectedElement = null;
        if ((selectedElement = getReferenceLinkInPosition (cursorPosition, g)) != null)
        {
            setSelectedAllObjects (false);
            setSelectedElement(selectedElement, true);
        } else if ((selectedElement = getInstanceInPosition (cursorPosition, g)) != null)
        {
            setSelectedAllObjects (false);
            setSelectedElement(selectedElement, true);
        }
        else
        {
            selectedElement = super.selectElementInPosition (cursorPosition, g);
        }
        return selectedElement;
    }
    
    /**
     * Starts link creation
     * @param cursorPosition
     * @param g
     */
    @Override
    public void startLinkCreation (java.awt.geom.Point2D.Double cursorPosition, Graphics2D g)
    {
        VisualInstance instance = null;
        
        VisualNode node = null;
        
        if ((node = whatNodeInPosition (cursorPosition, g)) != null)
        {
            newLink = new VisualArrow (node.getPosition (), cursorPosition);
            newLinkSource = node;
        }else if ((instance = getInstanceInPosition (cursorPosition, g)) != null)
        {
            newLink = new VisualArrow (new Point2D.Double (instance.getCenter ().getX (),
                                                           instance.getCenter ().getY ()),
                                       cursorPosition);
            newInstanceLinkSource = instance;
        }
    }    
    
    /**
     * Move the selected elements an amount in both axis.
     * @param diffX X-axis movement.
     * @param diffY Y-axis movement.
     */
    public void moveSelectedElements (double diffX, double diffY)
    {
        moveSelectedNodes (diffX, diffY);
        moveSelectedInstances (diffX, diffY);
    }  
    
    /**
     * Fills the array of information of the selected nodes and their actual
     * state.
     * 
     * @return list where are the moved nodes information.
     */
    public List<VisualNode> fillVisualNodesSelected() {

        List<VisualNode> movedNodes = super.fillVisualNodesSelected ();

        movedNodes.addAll (getVisualNodesOfSelectedInstances());
        return movedNodes;

    }    
    
    public void markSelectedInstancesAsInput() {
        for (VisualInstance instance : getSelectedInstances()) {
            instance.setInput(!instance.isInput());
        }
    }

    @Override
    public PNEdit finishLinkCreation (java.awt.geom.Point2D.Double point, Graphics2D g)
    {
        PNEdit linkEdit = null;
        VisualInstance newInstanceLinkDestination = null;
        VisualNode newLinkDestination = null;
        
        if ((newInstanceLinkDestination = getInstanceInPosition(point, g)) != null
                && newInstanceLinkSource != null) {
            newLink = null;
            VisualInstance inputParameter = newInstanceLinkDestination
                    .getParameterInPosition(point, g);
            if (inputParameter != null
                    && inputParameter
                            .getInstance()
                            .getClassNet()
                            .getName()
                            .compareToIgnoreCase (newInstanceLinkSource.getInstance()
                                    .getClassNet().getName()) == 0) {
                linkEdit = new AddReferenceLinkEdit(probNet,
                        newInstanceLinkSource.getInstance(),
                        newInstanceLinkDestination.getInstance(),
                        inputParameter.getInstance());
            }
        } else if((newLinkDestination = whatNodeInPosition (point, g)) != null
                && newLinkSource != null)
        {
        	if(newLinkDestination.getProbNode().isInput() &&
        			isEquivalentVariable(newLinkDestination.getProbNode().getVariable(), newLinkSource.getProbNode().getVariable()))
        	{
        		newLink = null;
        		linkEdit = new AddReferenceLinkEdit(probNet, newLinkSource.getProbNode(), newLinkDestination.getProbNode());
        	}else
        	{
        		linkEdit = super.finishLinkCreation (point, g);
        	}
        }else
        {
            newLink = null;
            newLinkSource = null;
        }
        return linkEdit;
    }

    /**
     * Checks whether two variables are similar: in case of FS variables, if they have the same states
     * @param variable
     * @param otherVariable
     * @return
     */
    private boolean isEquivalentVariable(Variable variable, Variable otherVariable) {
    	boolean isEquivalent = true;
    	
    	switch(variable.getVariableType())
    	{
	    	case DISCRETIZED:
	    	case FINITE_STATES:
	    		isEquivalent &= otherVariable.getVariableType() == VariableType.DISCRETIZED 
	    			|| otherVariable.getVariableType() == VariableType.FINITE_STATES;
	    		isEquivalent &= variable.getStates().length == otherVariable.getStates().length;
	    		int i=0;
	    		while(isEquivalent && i< variable.getStates().length)
	    		{
	    			isEquivalent &= variable.getStates()[i].equals(variable.getStates()[i]);
	    			++i;
	    		}
	    		break;
	    	case NUMERIC:
	    		isEquivalent &= otherVariable.getVariableType() == VariableType.NUMERIC;
	    		break;
    	}
		return isEquivalent;
	}


	@Override
    public void markSelectedAsInput ()
    {
        super.markSelectedAsInput ();
        
        for(VisualInstance visualInstance : getSelectedInstances ())
        {
        	MarkAsInputEdit markAsInputEdit = new MarkAsInputEdit(probNet, 
        											!visualInstance.getInstance().isInput (), 
        											visualInstance.getInstance());
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
        Instance selectedInstance = ((VisualInstance)selectedInstances.toArray ()[0]).getInstance ();
        Container openedFrame = MainPanel.getUniqueInstance ().getMdi ().getFrameByTitle (selectedInstance.getClassNet ().getName ());
        if(openedFrame !=  null)
        {
            MainPanel.getUniqueInstance ().getMdi ().selectFrame ((JPanel)openedFrame);
        }else
        {
            MainPanel.getUniqueInstance ().getMainPanelListenerAssistant ().openNetwork (selectedInstance.getClassNet ());
        }
        
    }

    @Override
    public void setWorkingMode (int workingMode)
    {
        super.setWorkingMode (workingMode);
        if(workingMode == NetworkPanel.EDITION_WORKING_MODE)
        {
            constructVisualInfo ();
        }else
        {
            visualInstances.clear ();
            visualReferenceLinks.clear ();
            visualContractedNodeLinks.clear ();
        }
    }    
    
    @Override
	public void setParameterArity(ParameterArity arity) {
    	for(VisualInstance visualInstance : getSelectedInstances())
    	{
    		ChangeParameterArityEdit changeParameterArityEdit = 
    				new ChangeParameterArityEdit(probNet, visualInstance.getInstance(), arity);
    		try {
				probNet.doEdit(changeParameterArityEdit);
			} catch (ConstraintViolationException
					| CanNotDoEditException | NonProjectablePotentialException
					| WrongCriterionException | DoEditException e) {
				e.printStackTrace();
			}
    	}
	}    

	public void undoableEditHappened(UndoableEditEvent e) {
		super.undoableEditHappened(e);
	}
		

	public void undoEditHappened(UndoableEditEvent event) {
		super.undoEditHappened(event);
	}

    protected void clean ()
    {
        super.clean ();
        visualInstances.clear ();
        visualReferenceLinks.clear ();
        visualContractedNodeLinks.clear ();
        selectedInstances.clear ();
        selectedReferenceLinks.clear ();
        newInstanceLinkSource = null;
    }    
   
}
