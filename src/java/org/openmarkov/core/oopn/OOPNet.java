/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.oopn;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.action.CRemoveProbNodeEdit;
import org.openmarkov.core.action.PotentialChangeEdit;
import org.openmarkov.core.action.CompoundPNEdit;
import org.openmarkov.core.action.ICIPotentialEdit;
import org.openmarkov.core.action.InvertLinkEdit;
import org.openmarkov.core.action.NodeStateEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.action.RemoveNodeEdit;
import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.core.oopn.exception.InstanceAlreadyExistsException;

public class OOPNet extends ProbNet implements PNUndoableEditListener
{
    private LinkedHashMap<String, ProbNet> classes = new LinkedHashMap<String, ProbNet> ();
    private Map<String, Instance> instances        = new HashMap<String, Instance> ();
    private List<ReferenceLink>   referenceLinks  = new ArrayList<ReferenceLink> ();

    
    /**
     * Constructor for OOPNet.
     */
    public OOPNet ()
    {
        super ();
    }

    /**
     * Constructor for OOPNet.
     * @param networkType
     */
    public OOPNet (NetworkType networkType)
    {
        super (networkType);
    }
    
    /**
     * Constructor for OOPNet.
     * @param networkType
     */
    public OOPNet (ProbNet probNet)
    {
        super();
        try
        {
            setNetworkType (probNet.getNetworkType ());
        }
        catch (ConstraintViolationException e1)
        {
            e1.printStackTrace();
        } 
        // copy constraints
        int numConstraints = probNet.getConstraints ().size(); 
        for (int i = 1; i < numConstraints; i++)
        {
            try
            {
                addConstraint (probNet.getConstraints().get (i), false);
            }
            catch (ConstraintViolationException e)
            {
                // Unreachable code because constraints are not tested in copy
            }
        }
        List<ProbNode> probNodes = probNet.getProbNodes();
        // Adds variables and create corresponding nodes. Also add potentials
        for (ProbNode probNode : probNodes) {
            // Add variables and create corresponding nodes
            Variable variable = probNode.getVariable();
            ProbNode newProbNode = null;
            newProbNode = addProbNode (variable, probNode.getNodeType ());
            Node newNode = newProbNode.getNode();
            Node node = probNode.getNode();
            newNode.setCoordinateX(node.getCoordinateX());
            newNode.setCoordinateY(node.getCoordinateY());
            newProbNode.setPotentials(probNode.getPotentials());

            // TODO Hacer clon para probNode y quitar estas lineas
            newProbNode.setPurpose(probNode.getPurpose());
            newProbNode.setRelevance(probNode.getRelevance());
            newProbNode.setComment(probNode.getComment());
            newProbNode.setCanonicalParameters(probNode.isCanonicalParameters());
            newProbNode.additionalProperties = additionalProperties;
        }

        // Add links
        List<ProbNode> nodes = probNet.getProbNodes();
        for (ProbNode probNode1 : nodes) {
            Variable variable1 = probNode1.getVariable();
            ProbNode newNode1 = this.getProbNode(variable1);
            List<ProbNode> neighbors = getProbNodesOfNodes(probNode1.getNode().getNeighbors());
            for (ProbNode probNode2 : neighbors) {
                Variable variable2 = probNode2.getVariable();
                ProbNode newNode2 = this.getProbNode(variable2);
                if (probNode1.getNode().isSibling(probNode2.getNode())) {
                    if (!newNode1.getNode().isSibling(newNode2.getNode())) {
                        graph.addLink(newNode1.getNode(), newNode2.getNode(),
                                false);
                    }
                }
                if (probNode1.getNode().isChild(probNode2.getNode())) {
                    graph.addLink(newNode1.getNode(), newNode2.getNode(), true);
                }
            }
        }

        // copy listeners
        getPNESupport().setListeners(probNet.getPNESupport ().getListeners());
        
        // Copy additionalProperties
        Set<String> keys = probNet.additionalProperties.keySet();
        HashMap<String, String> copyProperties = new HashMap<String, String>();
        for (String key : keys) {
            copyProperties.put(key, probNet.additionalProperties.get(key));
        }
        additionalProperties = copyProperties;

        }
    

    /**
     * @param classNet
     * @param instanceName
     * @param instanceNodes
     * @throws InstanceAlreadyExistsException
     */
    public void addInstance (Instance instance)
        throws InstanceAlreadyExistsException
    {
        if (instances.containsKey (instance.getName ()))
        {
            throw new InstanceAlreadyExistsException ();
        }
        else
        {
        	instance.getClassNet().getPNESupport().addUndoableEditListener(this);
            instances.put (instance.getName (), instance);
        }
    }

    /**
     * @return instance list
     */
    public Map<String, Instance> getInstances ()
    {
        return instances;
    }

    /**
     * Add an instance link
     * @param link
     */
    public void addReferenceLink (ReferenceLink link)
    {
        referenceLinks.add (link);
    }

    /**
     * @return the instanceLinks
     */
    public List<ReferenceLink> getReferenceLinks ()
    {
        return referenceLinks;
    }

    /**
     * Removes an instance Link
     * @param link
     */
    public void removeReferenceLink (ReferenceLink link)
    {
        referenceLinks.remove (link);
    }

    /**
     * Returns the equivalent node in <code>sourceInstance</code> to the
     * <code>probNode</code> in <code>destinationInstance</code>
     * @param sourceInstance
     * @param destInstance
     * @param probNode
     * @return
     */
    private ProbNode getEquivalentNode (Instance sourceInstance,
                                        Instance destInstance,
                                        ProbNode probNode)
    {
        ProbNode equivalentNode = null;
        int i = 0;
        String nodeName = probNode.getName ();
        nodeName = nodeName.replace (destInstance.getName () + ".", "");
        while (equivalentNode == null && i < sourceInstance.getNodes ().size ())
        {
            String equivalentNodeName = sourceInstance.getNodes ().get (i).getName ();
            equivalentNodeName = equivalentNodeName.replace (sourceInstance.getName () + ".", "");
            if (equivalentNodeName.equals (nodeName))
            {
                equivalentNode = sourceInstance.getNodes ().get (i);
            }
            ++i;
        }
        return equivalentNode;
    }

    /**
     * Unrolls instances and returns a plain probabilistic network
     * @return
     */
    public ProbNet getPlainProbNet ()
    {
        ProbNet probNet = copy ();
        probNet.getGraph ().makeLinksExplicit (false);
        for (Instance instance : getInstances().values())
        {
            for (Instance subInstance : instance.getSubInstances().values())
            {
	            if(subInstance.isInput())
	            {
	                List<ReferenceLink> linksToParameter = getLinksToParameter(subInstance);
		            for (ProbNode node : subInstance.getNodes ())
	            	{
	                    ProbNode formalNode = probNet.getProbNode (node.getVariable ());
		            	List<ProbNode> paramNodes = new ArrayList<>();
		            	for(ReferenceLink link: linksToParameter)
			            {
			            	InstanceReferenceLink instanceLink = (InstanceReferenceLink)link;        	
			            	ProbNode equivalentNode = getEquivalentNode (
			                		instanceLink.getSourceInstance (),
			                		instanceLink.getDestSubInstance (), node);
			                ProbNode paramNode = probNet.getProbNode (equivalentNode.getVariable ());
			                if (paramNode != null)
			                {
			                    paramNodes.add(paramNode);
			                }
			            }
		            	if(paramNodes.size () == 1)
		            	{
		                    replaceNode(probNet, formalNode, paramNodes.get (0));
		            	}else {//if (paramNodes.size () > 1){
                            replaceNodes(probNet, formalNode, paramNodes);
		            	}
	            	}
	            	// Remove formal parameter nodes
		            if(true)//linksToParameter.size () > 0)
		            {
    			        for (ProbNode node : subInstance.getNodes ())
    			        {
    			            probNet.removeProbNode (probNet.getProbNode (node.getVariable ()));
    	            	}
		            }
	            }
            }
        }
        
        for(ReferenceLink link : getReferenceLinks())
        {
    	    if(link instanceof NodeReferenceLink)
            {
            	NodeReferenceLink nodeLink = (NodeReferenceLink)link;
            	ProbNode sourceNode = probNet.getProbNode(nodeLink.getSourceNode().getVariable());
            	ProbNode destinationNode = probNet.getProbNode(nodeLink.getDestinationNode().getVariable());
    			replaceNode(probNet, destinationNode, sourceNode);
            	probNet.removeProbNode (destinationNode);
            }
        }
        
        return probNet;
    }

	private void replaceNodes(ProbNet probNet, ProbNode formalNode,
			List<ProbNode> paramNodes) {
		// Update potentials
		HashMap<Potential, Potential> potentialsToReplace = new HashMap<Potential, Potential> ();
		for (Potential potential : probNet.getPotentials (formalNode.getVariable ()))
		{
	        if(potential instanceof ICIPotential)
	        {
			    ICIPotential potentialCopy;

				potentialCopy = (ICIPotential) potential.copy();
				double[] noisyParameters = potentialCopy
						.getNoisyParameters(formalNode.getVariable());
				if (noisyParameters != null) {
					potentialCopy = (ICIPotential) potentialCopy
							.removeVariable(formalNode.getVariable());
					for (ProbNode paramNode : paramNodes) {
						potentialCopy = (ICIPotential) potentialCopy
								.addVariable(paramNode.getVariable());
						potentialCopy.setNoisyParameters(
								paramNode.getVariable(), noisyParameters);
					}
					potentialsToReplace.put(potential, potentialCopy);
				}
	        }
		}
		for (ProbNode probNode : probNet.getProbNodes ())
		{
		    for (Potential potentialToReplace : potentialsToReplace.keySet ())
		    {
		        if (probNode.getPotentials ().contains (potentialToReplace))
		        {
		            List<Potential> potentials = probNode.getPotentials ();
		            potentials.remove (potentialToReplace);
		            potentials.add (potentialsToReplace.get (potentialToReplace));
		            probNode.setPotentials (potentials);
		        }
		    }
		}
		// Update Links
		// Replace links to children
		for (Node child : formalNode.getNode ().getChildren ())
		{
		    probNet.removeLink (formalNode, (ProbNode) child.getObject (), true);
		    for(ProbNode paramNode : paramNodes)
		    {
			    if (!child.isParent (paramNode.getNode ()))
			    {
			        probNet.addLink (paramNode, (ProbNode) child.getObject (), true);
			    }
		    }
		}
	}

	private List<ReferenceLink> getLinksToParameter(Instance instance) {
		List<ReferenceLink> links = new ArrayList<>();
		for(ReferenceLink link : getReferenceLinks())
		{
			if(link instanceof InstanceReferenceLink &&
					((InstanceReferenceLink)link).getDestSubInstance().equals(instance))
			{
				links.add(link);
			}
		}
		return links;
	}

	/**
	 * @param probNet
	 * @param node
	 * @param formalNode
	 * @param paramNode
	 */
	private void replaceNode(ProbNet probNet, ProbNode formalNode, ProbNode paramNode) {
		// Update potentials
		HashMap<Potential, Potential> potentialsToReplace = new HashMap<Potential, Potential> ();
		for (Potential potential : probNet.getPotentials (formalNode.getVariable ()))
		{
		    Potential potentialCopy = potential.copy();
			potentialCopy.replaceVariable(formalNode.getVariable(),
					paramNode.getVariable());
			potentialsToReplace.put(potential, potentialCopy);
		}
		for (ProbNode probNode : probNet.getProbNodes ())
		{
		    for (Potential potentialToReplace : potentialsToReplace.keySet ())
		    {
		        if (probNode.getPotentials ().contains (potentialToReplace))
		        {
		            List<Potential> potentials = probNode.getPotentials ();
		            potentials.remove (potentialToReplace);
		            potentials.add (potentialsToReplace.get (potentialToReplace));
		            probNode.setPotentials (potentials);
		        }
		    }
		}
		// Update Links
		// Replace links to children
		for (Node child : formalNode.getNode ().getChildren ())
		{
		    probNet.removeLink (formalNode, (ProbNode) child.getObject (), true);
		    if (!child.isParent (paramNode.getNode ()))
		    {
		        probNet.addLink (paramNode, (ProbNode) child.getObject (), true);
		    }
		}
		// Replace links from parents
		for (Node parent : formalNode.getNode ().getParents ())
		{
		    probNet.removeLink ((ProbNode) parent.getObject (), formalNode, true);
		    if (!paramNode.getNode ().isParent (parent))
		    {
		        probNet.addLink ((ProbNode) parent.getObject (), paramNode, true);
		    }
		}
		// Replace links between siblings
		for (Node sibling : formalNode.getNode ().getSiblings ())
		{
		    probNet.removeLink ((ProbNode) sibling.getObject (), formalNode, false);
		    probNet.addLink ((ProbNode) sibling.getObject (), paramNode, false);
		}
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		if(e.getEdit() instanceof PNEdit)
		{
			PNEdit edit = (PNEdit)e.getEdit();
			List<PNEdit> simpleEdits = new ArrayList<>();
			if(edit instanceof CompoundPNEdit)
			{
				try {
					for(UndoableEdit undoableEdit : ((CompoundPNEdit)edit).getEdits())
					{
						simpleEdits.add((PNEdit) undoableEdit);
					}
				} catch (NonProjectablePotentialException
						| WrongCriterionException e1) {
					e1.printStackTrace();
				}
			}else{
				simpleEdits.add((PNEdit) edit);
			}
				
			ProbNet classNet = edit.getProbNet();
			PNEdit newEdit = null;

			for(Instance instance : instances.values())
			{
				if(instance.getClassNet().equals(classNet))
				{
					String instanceName = instance.getName();
					for (PNEdit simpleEdit : simpleEdits)
					{
						if(simpleEdit instanceof AddLinkEdit)
						{
							AddLinkEdit addLinkEdit = (AddLinkEdit)simpleEdit;
							Variable variable1;
							try {
								variable1 = getVariable(instanceName + "." + addLinkEdit.getVariable1().getName());
								Variable variable2 = getVariable(instanceName + "." + addLinkEdit.getVariable2().getName());
								newEdit = new AddLinkEdit(this, variable1, variable2, addLinkEdit.isDirected());
							} catch (ProbNodeNotFoundException e1) {
								e1.printStackTrace();
							}
						}else if(simpleEdit instanceof RemoveLinkEdit)
						{
							RemoveLinkEdit removeLinkEdit = (RemoveLinkEdit)simpleEdit;
							Variable variable1;
							try {
								variable1 = getVariable(instanceName + "." + removeLinkEdit.getVariable1().getName());
								Variable variable2 = getVariable(instanceName + "." + removeLinkEdit.getVariable2().getName());
								newEdit = new RemoveLinkEdit(this, variable1, variable2, removeLinkEdit.isDirected());
							} catch (ProbNodeNotFoundException e1) {
								e1.printStackTrace();
							}
							
						}else if(simpleEdit instanceof InvertLinkEdit)
						{
							InvertLinkEdit invertLinkEdit = (InvertLinkEdit)simpleEdit;
							Variable variable1;
							try {
								variable1 = getVariable(instanceName + "." + invertLinkEdit.getVariable1().getName());
								Variable variable2 = getVariable(instanceName + "." + invertLinkEdit.getVariable2().getName());
								newEdit = new InvertLinkEdit(this, variable1, variable2, invertLinkEdit.isDirected());
							} catch (ProbNodeNotFoundException e1) {
								e1.printStackTrace();
							}
						} else if(simpleEdit instanceof AddProbNodeEdit)
						{
							AddProbNodeEdit addProbNodeEdit = (AddProbNodeEdit)simpleEdit;
							
							Variable newVariable = new Variable(addProbNodeEdit.getVariable());
							newVariable.setName(instanceName + "." + newVariable.getName());
							
							//determine position of new node inside instance, using a node as reference
							Point2D.Double position = new Point2D.Double();
							if(!classNet.getProbNodes().isEmpty())
							{
								
								ProbNode referenceNode = classNet.getProbNodes().get(0);
								ProbNode referenceInstanceNode = null;
								try {
									referenceInstanceNode = getProbNode(instanceName + "." + referenceNode.getVariable().getName());
								} catch (ProbNodeNotFoundException e1) {
									e1.printStackTrace();
								}
								double x = addProbNodeEdit.getCursorPosition().getX() 
										- referenceNode.getNode().getCoordinateX() 
										+ referenceInstanceNode.getNode().getCoordinateX();
								double y = addProbNodeEdit.getCursorPosition().getY() 
										- referenceNode.getNode().getCoordinateY() 
										+ referenceInstanceNode.getNode().getCoordinateY();
								position = new Point2D.Double(x, y);
							}
							newEdit = new AddProbNodeEdit(this,
									newVariable,
									addProbNodeEdit.getNodeType(),
									position);
						} else if(simpleEdit instanceof RemoveNodeEdit)
						{
							RemoveNodeEdit removeNodeEdit = (RemoveNodeEdit)simpleEdit;
							newEdit = new RemoveNodeEdit(this, removeNodeEdit.getVariable());
						} else if(simpleEdit instanceof CRemoveProbNodeEdit)
						{
							CRemoveProbNodeEdit removeNodeEdit = (CRemoveProbNodeEdit)simpleEdit;
							ProbNode nodeToRemove = null;
							try {
								nodeToRemove = getProbNode(instanceName + "." + removeNodeEdit.getVariable().getName());
							} catch (ProbNodeNotFoundException e1) {
								e1.printStackTrace();
							}
							newEdit = new CRemoveProbNodeEdit(this, nodeToRemove);
						} else if(simpleEdit instanceof NodeStateEdit)
						{
							NodeStateEdit nodeStateEdit = (NodeStateEdit)simpleEdit;
							ProbNode nodeInInstance = null;
							try {
								nodeInInstance = getProbNode(instanceName + "." + nodeStateEdit.getProbNode().getName());
							} catch (ProbNodeNotFoundException e1) {
								e1.printStackTrace();
							}
							newEdit = new NodeStateEdit(nodeInInstance,
									nodeStateEdit.getStateAction(),
									nodeStateEdit.getIndexState(),
									nodeStateEdit.getNewStateName());
						} else if(simpleEdit instanceof SetPotentialEdit)
						{
							try {
								SetPotentialEdit setPotentialEdit = (SetPotentialEdit)simpleEdit;
								ProbNode probNode = getProbNode(instanceName + "." + setPotentialEdit.getProbNode().getName());
								if(setPotentialEdit.getNewPotential() != null)
								{
									Potential newPotential = setPotentialEdit.getNewPotential().copy();
									for(Variable variable : setPotentialEdit.getNewPotential().getVariables())
									{
										newPotential.replaceVariable(variable, getVariable(instanceName + "." + variable.getName()));
									}
				                    if(newPotential.isUtility())
				                    {
				                        Variable utilityVariable = newPotential.getUtilityVariable();
			                            newPotential.replaceVariable (utilityVariable, getVariable(instanceName + "." + utilityVariable.getName()));
				                    }									
									newEdit = new SetPotentialEdit(probNode, newPotential);
								}else
								{
									newEdit = new SetPotentialEdit(probNode, setPotentialEdit.getNewPotentialType());
								}
							} catch (ProbNodeNotFoundException e1) {
								e1.printStackTrace();
							}
							
						} else if(simpleEdit instanceof PotentialChangeEdit)
						{
							try {
								PotentialChangeEdit changePotentialEdit = (PotentialChangeEdit)simpleEdit;
								
								// Find oldPotential in instance
								Potential oldPotential = findEquivalentPotentialInInstance(instanceName, changePotentialEdit.getOldPotential());
								
								// Copy newPotential with the variables in the instance
								Potential newPotential = changePotentialEdit.getNewPotential().copy();
								for(Variable variable : changePotentialEdit.getNewPotential().getVariables())
								{
									newPotential.replaceVariable(variable, getVariable(instanceName + "." + variable.getName()));
								}
                                if(newPotential.isUtility())
                                {
                                    Variable utilityVariable = newPotential.getUtilityVariable();
                                    newPotential.replaceVariable (utilityVariable, getVariable(instanceName + "." + utilityVariable.getName()));
                                }                                   
								
								newEdit = new PotentialChangeEdit(this, oldPotential, newPotential);
							} catch (ProbNodeNotFoundException e1) {
								e1.printStackTrace();
							}
						}else if(simpleEdit instanceof ICIPotentialEdit)
						{
							ICIPotentialEdit iciPotentialEdit = (ICIPotentialEdit)simpleEdit;
							// Find oldPotential in instance
							ICIPotential potential = (ICIPotential)findEquivalentPotentialInInstance(instanceName, iciPotentialEdit.getPotential());
							if(iciPotentialEdit.isNoisyParameter())
							{
								Variable variable = null;
								try {
									variable = getVariable(instanceName + "." + iciPotentialEdit.getVariable().getName());
									newEdit = new ICIPotentialEdit(this, potential, variable, iciPotentialEdit.getNoisyParameters());
								} catch (ProbNodeNotFoundException e1) {
									e1.printStackTrace();
								}
							}else
							{
								newEdit = new ICIPotentialEdit(this, potential, iciPotentialEdit.getLeakyParameters());
							}
						}
						
						if(newEdit != null)
						{
							try {
								doEdit(newEdit);
							} catch (ConstraintViolationException
									| CanNotDoEditException | NonProjectablePotentialException
									| WrongCriterionException | DoEditException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		}
		
	}

	@Override
	public void undoableEditWillHappen(UndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException,
			NonProjectablePotentialException,
			WrongCriterionException {
		// Do nothing
		
	}

	@Override
	public void undoEditHappened(UndoableEditEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private Potential findEquivalentPotentialInInstance(String instanceName, Potential potential)
	{
		Potential oldPotential = null;
		List<Variable> instanceVariables = new ArrayList<>();
		try {
			for(Variable variable : potential.getVariables())
			{
				instanceVariables.add(getVariable(instanceName + "." + variable.getName()));
			}
			oldPotential = findPotentialByVariables(instanceVariables);
		} catch (ProbNodeNotFoundException e) {
			e.printStackTrace();
		}
		return oldPotential;
	}
	
	private Potential findPotentialByVariables(List<Variable> variables)
	{
		int i = 0;
		List<Potential> potentials = getPotentials();
		Potential potential = null;
		
		while(i<potentials.size() && potential == null)
		{
			boolean match = potentials.get(i).getVariables().size() == variables.size();
			int j = 0;
			while(match && j< variables.size())
			{
				match &= potentials.get(i).getVariables().contains(variables.get(j));
				++j;
			}
			if(match)
			{
				potential = potentials.get(i);
			}
			++i;
		}
		return potential;
	}

    /**
     * Returns the classes.
     * @return the classes.
     */
    public LinkedHashMap<String, ProbNet> getClasses ()
    {
        return classes;
    }

    /**
     * Sets the classes.
     * @param classes the classes to set.
     */
    public void setClasses (LinkedHashMap<String, ProbNet> classes)
    {
        this.classes = classes;
    }

    public void fillClassList ()
    {
        this.classes = getClassList ();
    }
    
    protected LinkedHashMap<String, ProbNet> getClassList ()
    {
        LinkedHashMap<String, ProbNet> classes = new LinkedHashMap<> ();
        for(Instance instance : getInstances().values ())
        {
            if(instance.getClassNet () instanceof OOPNet)
            {
                classes.putAll (((OOPNet)instance.getClassNet ()).getClassList());
            }
            if(!classes.containsKey (instance.getClassNet ().getName ()))
            {
                classes.put (instance.getClassNet ().getName (), instance.getClassNet ());
            }
        }
        
        return classes;
    }
    
	
}
