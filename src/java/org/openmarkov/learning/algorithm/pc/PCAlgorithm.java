/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.algorithm.pc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.BaseLinkEdit;
import org.openmarkov.core.action.COrientLinksEdit;
import org.openmarkov.core.action.OrientLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.learning.algorithm.pc.independencetester.IndependenceTester;
import org.openmarkov.learning.algorithm.pc.util.PCEditMotivation;
import org.openmarkov.learning.algorithm.pc.util.PCCache;
import org.openmarkov.learning.core.algorithm.LearningAlgorithmType;
import org.openmarkov.learning.core.util.LearningEditMotivation;
import org.openmarkov.learning.core.util.LearningEditProposal;
import org.openmarkov.learning.core.util.ScoreEditMotivation;
import org.openmarkov.learning.core.util.StringEditMotivation;

@LearningAlgorithmType(name = "PC", supportsLatentVariables = false)
public class PCAlgorithm extends IndependenceRelationsAlgorithm implements PNUndoableEditListener{	

    /**
    * ProbNet that is being learned.
    */
   protected ProbNet probNet;
   
   protected PCCache cache;
   
   /**
    * History of last best edits returned.
    */
   protected Map<BaseLinkEdit, PCEditMotivation> lastRemovedEdits;
   protected Map<BaseLinkEdit, PCEditMotivation> lastOrientationEdits;
   protected List<COrientLinksEdit> lastCompoundOrientationEdits;
   

   /**
    * Separation sets used in the algorithm. Each of the elements of the
    * ArrayList represents the separation sets associated with each node 
    * by a HashMap. For example, if the separation set of the nodes X,Y is 
    * {W, Z}, and node X is the third node of the probNet, the arrayList, 
    * would have a HashMap on the third position with one entry: 
    * Y -> {W,Z}
    */
   protected List<HashMap<Node, List<Node>>> separationSets = null;
   
   /**
    * Case database we are learning upon
    */
   protected int[][] cases;
   
   protected IndependenceTester independenceTester;
   
   /**
    * Degree of accuracy of the independence test.
    */
   protected double degreeOfAccuracy;
   
   /**
    * These fields indicate the stage of the algorithm.
    */
   protected static final int INITIAL_PHASE = 0;
   protected static final int HEAD_TO_HEAD_ORIENTATION = 1;
   protected static final int REMAINING_LINKS_ORIENTATION = 2;
   protected static final int ORIENTATION_FINISHED = 3;
   protected static final int LEARNING_FINISHED = 4;
   
    public PCAlgorithm (ProbNet probNet,
                        CaseDatabase caseDatabase,
                        Double alpha,
                        IndependenceTester independenceTester,
                        Double degreeOfAccuracy)
    {
        super (probNet, caseDatabase, alpha);
        this.probNet = probNet;
        this.cases = caseDatabase.getCases ();
        this.independenceTester = independenceTester;
        this.degreeOfAccuracy = degreeOfAccuracy;
        this.probNet.getPNESupport ().addUndoableEditListener (this);
        cache = new PCCache (probNet);
        lastRemovedEdits = new HashMap<BaseLinkEdit, PCEditMotivation> ();
        lastOrientationEdits = new HashMap<BaseLinkEdit, PCEditMotivation> ();
        lastCompoundOrientationEdits = new ArrayList<COrientLinksEdit> ();
        separationSets = new ArrayList<HashMap<Node, List<Node>>> ();
        for (int i= 0; i < ProbNet.getNodesOfProbNodes (probNet.getProbNodes ()).size (); ++i){
            separationSets.add(new HashMap<Node,List<Node>> ());
        }        
    }

    /**
     * Method that returns the best edit in each step of the algorithm or null
     * if there are no more edits to consider (depending on the arguments it
     * receives).
     */
    @Override
    public LearningEditProposal getBestEdit (boolean onlyAllowedEdits,
                                     boolean onlyPositiveEdits)
    {
        resetHistory ();
        return getNextEdit (onlyAllowedEdits, onlyPositiveEdits);
    }

    /**
     * Method that returns the next best edit in each step of the algorithm 
     * or null if there are no more edits to consider (depending on the 
     * arguments it receives). 
     */
    @Override
    public LearningEditProposal getNextEdit (boolean onlyAllowedEdits,
                                     boolean onlyPositiveEdits)
    {
        
        LearningEditProposal bestEditProposal = getOptimalEdit (onlyAllowedEdits, onlyPositiveEdits);
        while(bestEditProposal != null && isBlocked (bestEditProposal.getEdit()))
        {
            bestEditProposal = getOptimalEdit (onlyAllowedEdits, onlyPositiveEdits);
        }
        return bestEditProposal;
    }
    
    /**
     * Method that returns the best edit in each step of the algorithm. It 
     * changes the phase of the algorithm in case it is necessary. If there
     * are no possible edits on any of the phases it returns null.
     */
    public LearningEditProposal getOptimalEdit ( boolean onlyAllowedEdits, 
    		boolean onlyPositiveEdits)
    {
        int adjacencySize = 0;
        double linkScore;
        LearningEditProposal bestEditProposal = null;
        List<Node> adjacencySubset;
        RemoveLinkEdit removeLinkEdit;
        PCEditMotivation motivation;
                
        try{
            while (maxOfAdjacencies () > adjacencySize){
                for (ProbNode nodeX : probNet.getProbNodes ())
                {
                    for (Node nodeY : nodeX.getNode ().getSiblings ())
                    {
                        adjacencySubset = new ArrayList<>(nodeX.getNode ().getNeighbors ());
                        adjacencySubset.remove (nodeY);
                        
                        for (List<Node> separationSet : subSetsOfSize (adjacencySubset, adjacencySize))
                        {
                            removeLinkEdit = new RemoveLinkEdit (probNet, 
                                    probNet.getProbNode (nodeX.getNode ()).getVariable (),
                                    probNet.getProbNode (nodeY).getVariable (), false);
                            if (!(alreadyConsidered (removeLinkEdit, separationSet, lastRemovedEdits)))
                            {
                            	motivation = cache.getScore (nodeX.getNode (), 
                                        nodeY); 
                                if ((motivation == null) || 
                                	((motivation.getScore() != PCCache.ALREADY_DONE) &&
                                	 (((!onlyPositiveEdits) && 
                                			 (motivation.getSeparationSet().size() > adjacencySize)) ||		
                                     (motivation.getSeparationSet().size() < adjacencySize))))
                                {
                                    linkScore = 1 - independenceTester.test (
                                            probNet, cases, nodeX.getNode (), nodeY, 
                                            separationSet);
                                    if ((motivation == null) ||
                                    		(!onlyPositiveEdits && (linkScore < degreeOfAccuracy)) ||
                                    		(linkScore > motivation.getScore()))
                                    {
	                                    cache.cacheScore(nodeX.getNode (), 
	                                            nodeY, new PCEditMotivation(linkScore, ProbNet.getProbNodesOfNodes(separationSet)));
                                    }
                                }
                            }
                        }
                    }
                }
                
                bestEditProposal = getOptimalEditFromCache(onlyAllowedEdits, onlyPositiveEdits);
                if (bestEditProposal != null)
                {
                	return bestEditProposal;
                }
                adjacencySize++;
            }
        } catch (ProbNodeNotFoundException e){
            Logger.getLogger (PCAlgorithm.class.getName ()).
                log(Level.WARN, null, e);
        }

        if (bestEditProposal == null){
            if (lastRemovedEdits.isEmpty ()){
                phase = HEAD_TO_HEAD_ORIENTATION;
                return getOrientationEdit (onlyAllowedEdits);
            }
        }
        phase = INITIAL_PHASE;
        return bestEditProposal;
    }
    
    public LearningEditProposal getOptimalEditFromCache (
    		boolean onlyAllowedEdits, 
    		boolean onlyPositiveEdits) throws ProbNodeNotFoundException
    {
    	int indexNodeX = 0;
	    LearningEditProposal bestEditProposal = null;
	    PCEditMotivation motivation, bestMotivation = null;
	    RemoveLinkEdit removeLinkEdit;
         
    	for (ProbNode nodeX : probNet.getProbNodes ())
        {
            indexNodeX = probNet.getProbNodes ().indexOf (nodeX);
            for (Node nodeY : nodeX.getNode ().getSiblings ())
            {	
            	removeLinkEdit = new RemoveLinkEdit (probNet, 
                        probNet.getProbNode (nodeX.getNode ()).getVariable (),
                        probNet.getProbNode (nodeY).getVariable (), false);
            	motivation = cache.getScore(nodeX.getNode(), nodeY);
				if(( motivation != null) &&
						(motivation.getScore() != PCCache.ALREADY_DONE) &&
						(motivation.compareTo(bestMotivation) > 0) &&
				        (!onlyPositiveEdits || motivation.getScore() > degreeOfAccuracy)  &&
				        !isBlocked (removeLinkEdit) && 
				        !isBlocked (inverseEdit (removeLinkEdit)) &&
				        !alreadyConsidered (removeLinkEdit, 
				        		ProbNet.getNodesOfProbNodes(motivation.getSeparationSet()), 
				        		lastRemovedEdits) &&
				        (!onlyAllowedEdits || isAllowed (removeLinkEdit)))
				{
					bestMotivation = motivation;
				    bestEditProposal = new LearningEditProposal (removeLinkEdit,
				            bestMotivation);
				    separationSets.get (indexNodeX).put (nodeY, ProbNet.
				    		getNodesOfProbNodes(bestMotivation.getSeparationSet())); 
				}
            }
        }
    	if (bestEditProposal != null)
    	{
    		lastRemovedEdits.put ((RemoveLinkEdit) bestEditProposal.getEdit(), 
    				(PCEditMotivation) bestEditProposal.getMotivation());
    	}
    	return bestEditProposal;
    }
    
    /**
     * Returns the <code>PCEditProposal</code> with the 
     * <code>DirectLinkEdit</code> depending on which stage is the algorithm.
     * If the "head to head" orientations have not been done, then, the
     * DirectLinkEdit contains these edits. Else, it contains the remaining
     * orientations. 
     * @return
     */
    public LearningEditProposal getOrientationEdit(boolean onlyAllowedEdits){
        LearningEditProposal bestEdit = null;
            
        try {
            bestEdit = orientHeadToHeadLinks (onlyAllowedEdits);
        
            if (bestEdit == null){
                if (lastCompoundOrientationEdits.isEmpty ()){
                    phase = REMAINING_LINKS_ORIENTATION;
                    return orientRemainingLinks (onlyAllowedEdits);
                }
            }
        } catch (ProbNodeNotFoundException | NonProjectablePotentialException
                | WrongCriterionException e) {
            e.printStackTrace ();
        } 

        return bestEdit;
    }
    
    /**
     * This method returns the maximum number of neighbors of a node in 
     * the probNet that is being learned.
     */
    private int maxOfAdjacencies (){
        int max, adjacents;
      
        max = 0;
        for (ProbNode node : probNet.getProbNodes ()) {
            adjacents = node.getNode ().getNeighbors ().size ();
        
            if (adjacents > max)
                max = adjacents;
        }
    
        return max;
    }
    
    /**
     * Returns a list of the subsets of size n of the given set   
     * @param set <code>ArrayList</code> of 
     * <code>Node</code> from which extract the subsets.
     * @param subSetsSize size of the subsets.
     * @return <code>ArrayList</code> of <code>ArrayList</code> of 
     * <code>Node</code>. Each <code>ArrayList</code> of <code>Node</code> 
     * is one of the subsets of size n.
     */
    public List<List<Node>> subSetsOfSize (List<Node> set, int subSetsSize) {

        List<List<Node>> subSets = new ArrayList<List<Node>> ();
        List<Node> subSet = new ArrayList<Node> ();
        int indexSubSet[];
        boolean found = true;

        indexSubSet = new int[subSetsSize];
        
        //Add the empty set
        if (subSetsSize == 0){
            subSets.add(new ArrayList<Node> ());
        }

        if ((subSetsSize > 0) & (subSetsSize <= set.size ())) {
            for (int i = 0 ; i < subSetsSize ; i++) {
                indexSubSet[i] = i;
                subSet.add(set.get (i));
            }
            subSets.add (subSet);

            if (subSetsSize < set.size ()) {
                while (found) {
                    found = false;

                    for (int i = subSetsSize-1 ; i >= 0 ; i--){
                        if (indexSubSet[i] < (set.size () + (i - subSetsSize))) {
                            indexSubSet[i] = indexSubSet[i] + 1;

                            if (i < (subSetsSize-1)) {
                                for (int j = i+1 ; j <subSetsSize ; j++){
                                    indexSubSet[j] = indexSubSet[j-1] + 1;
                                }
                            }

                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        subSet = new ArrayList<Node>();
                        for (int k = 0 ; k < subSetsSize ; k++){
                            subSet.add (set.get (indexSubSet[k]));
                        }

                        subSets.add (subSet);
                    }
                }
            }   
        }

        return subSets;
    }
    
    /** 
     * Given a RemoveLinkEdit, this method returns the same link with the inverse
     * direction. For example, if the parameter edit is a RemoveLinkEdit A->B,
     * it returns the RemoveLinkEdit B->A
     */
    public RemoveLinkEdit inverseEdit(RemoveLinkEdit edit)
    {
        return new RemoveLinkEdit(probNet, edit.getVariable2(),
                edit.getVariable1(), false);
    }
    
    
    public boolean alreadyConsidered (BaseLinkEdit edit, List<Node> separationSet, Map<BaseLinkEdit, PCEditMotivation> list)
    {
    	BaseLinkEdit inverseEdit = new RemoveLinkEdit (probNet, edit.getVariable2 (), edit.getVariable1 (), edit.isDirected ());

        for (BaseLinkEdit bestEdit : list.keySet())
        {
            if (bestEdit.equals(edit) || bestEdit.equals(inverseEdit))
                //if ((separationSet == null) || (ProbNet.getNodesOfProbNodes(list.get(bestEdit).getSeparationSet()).equals(separationSet)))
                    return true; 
        }
        return false;
    }
    
    public boolean alreadyConsidered (OrientLinkEdit edit1, OrientLinkEdit edit2)
    {
        boolean result = false;
        
        for (COrientLinksEdit compoundDirectLinkEdit : lastCompoundOrientationEdits){
            try {
                result |= ((edit1.compareTo((OrientLinkEdit)compoundDirectLinkEdit.getEdits().get(0)) == 0)
                        && (edit2.compareTo((OrientLinkEdit)compoundDirectLinkEdit.getEdits().get(1)) == 0));
                result |= ((edit1.compareTo((OrientLinkEdit)compoundDirectLinkEdit.getEdits().get(1)) == 0)
                        && (edit2.compareTo((OrientLinkEdit)compoundDirectLinkEdit.getEdits().get(0)) == 0));
            } catch (NonProjectablePotentialException | WrongCriterionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Method to compute the first stage of the orientation. For each
     * uncoupled meeting X - Y - Z if Y does not pertain to the separation
     * set of X and Z, we should orient X -> Y <- Z.
     */
    private LearningEditProposal orientHeadToHeadLinks(boolean onlyAllowedEdits) 
            throws ProbNodeNotFoundException{

        List<Node> neighborhoodX, neighborhoodY, separationSetXZ;
        int indexNodeX;
        COrientLinksEdit compoundDirectLinkEdit = null;
        OrientLinkEdit orientLinkEdit1, orientLinkEdit2;
        StringEditMotivation motivation;
        
        for (ProbNode probNodeX : probNet.getProbNodes ()){
            indexNodeX = probNet.getProbNodes ().indexOf (probNodeX);
            neighborhoodX = probNodeX.getNode ().getSiblings ();
            for (Node nodeY : neighborhoodX){
                neighborhoodY = nodeY.getSiblings ();
                neighborhoodY.remove(probNodeX.getNode ());
    
                for (Node nodeZ : neighborhoodY){
                    //Adjacent nodeX and nodeZ?
                    if (!probNodeX.getNode ().getNeighbors ().contains (nodeZ)){
                        //does Y pertain to the separation set of X and Z?
                        separationSetXZ = separationSets.get (indexNodeX).
                            get (nodeZ);
                        if ((separationSetXZ != null) && !(separationSetXZ.contains (nodeY)) || 
                                (separationSetXZ == null)){
                            //Then orient X->Y<-Z
                            orientLinkEdit1 = new OrientLinkEdit (probNet,
                                    probNodeX.getVariable (), probNet.
                                    getProbNode (nodeY).getVariable (), true);
                            orientLinkEdit2 = new OrientLinkEdit(probNet,
                                    probNet.getProbNode (nodeZ).getVariable (),
                                    probNet.getProbNode (nodeY).getVariable (),
                                    true); 
                            compoundDirectLinkEdit = new COrientLinksEdit (
                                    probNet, new Vector<UndoableEdit> ());
                            compoundDirectLinkEdit.addEdit (orientLinkEdit1);
                            compoundDirectLinkEdit.addEdit (orientLinkEdit2);
                            if (!alreadyConsidered (orientLinkEdit1,orientLinkEdit2) &&
                                    !isBlocked (compoundDirectLinkEdit) &&
                                    (!onlyAllowedEdits || (isOrientationAllowed (orientLinkEdit1) &&
                                    isOrientationAllowed (orientLinkEdit2))))
                            {
                                if (separationSetXZ == null)
                                    separationSetXZ = new ArrayList<Node> ();
                                motivation = new StringEditMotivation ("Sep. set (" + probNodeX.getName() + ", " + probNet.getProbNode(nodeZ).getName() + ") does not contain variable: " + probNet.getProbNode (nodeY).getName());
                                lastCompoundOrientationEdits.add (compoundDirectLinkEdit);
                                return new LearningEditProposal (compoundDirectLinkEdit, motivation);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Method to compute the final stage of the algorithm. The basic idea is
     * that no new head-to-head links are created and that the DAG condition is
     * preserved.
     * @throws WrongCriterionException 
     * @throws NonProjectablePotentialException 
     */
    private LearningEditProposal orientRemainingLinks(boolean onlyAllowedEdits) throws ProbNodeNotFoundException, NonProjectablePotentialException, WrongCriterionException{
        boolean change=true, change2=true, oriented, skip;
        Node nodeX, nodeZ;
        List<Node> siblingsNodeZ;
        OrientLinkEdit orientLinkEdit = null;

        while (change2){
            change2 = false;
            while (change){
                change = false;
                for (Link link : probNet.getGraph ().getLinks ()){
                    nodeX = link.getNode1 ();
                    nodeZ = link.getNode2 ();
                    if(link.isDirected ()){   // X-->Z
                        for (Node nodeY : nodeZ.getSiblings ()){
                            orientLinkEdit = new OrientLinkEdit(probNet,
                                    probNet.getProbNode (nodeZ).getVariable (),
                                    probNet.getProbNode (nodeY).getVariable (),
                                    true);
                            if(!nodeY.getNeighbors ().contains (nodeX) &&
                                    !alreadyConsidered (orientLinkEdit, (ArrayList<Node>)null, lastOrientationEdits) &&
                                    !isBlocked (orientLinkEdit) &&
                                    (!onlyAllowedEdits || isOrientationAllowed (orientLinkEdit))){ 
                                lastOrientationEdits.put (orientLinkEdit, null);
                                return (new LearningEditProposal (orientLinkEdit, new StringEditMotivation("Do not create cycles")));
                            }
                        }
                    }
                    else{ // X -- Z Non-oriented link
                        oriented = false;
                        orientLinkEdit = new OrientLinkEdit (probNet,
                                probNet.getProbNode (nodeX).getVariable (),
                                probNet.getProbNode (nodeZ).getVariable (),
                                true);
                        if(probNet.getGraph ().
                                existsPath(nodeX, nodeZ, true) &&
                                !alreadyConsidered (orientLinkEdit, (ArrayList<Node>)null, lastOrientationEdits) &&
                                !isBlocked (orientLinkEdit) &&
                                (!onlyAllowedEdits || isOrientationAllowed (orientLinkEdit))){
                            change = true;
                            oriented = true;
                            lastOrientationEdits.put (orientLinkEdit, null);
                            return (new LearningEditProposal (orientLinkEdit, new StringEditMotivation("Do not create cycles")));
                        }
                        orientLinkEdit = new OrientLinkEdit (probNet,
                                probNet.getProbNode (nodeZ).getVariable (),
                                probNet.getProbNode (nodeX).getVariable (),
                                true);
                        if((probNet.getGraph ().
                                existsPath (nodeZ, nodeX, true)) && (!oriented) &&
                                !alreadyConsidered (orientLinkEdit, (ArrayList<Node>)null, lastOrientationEdits) &&
                                !isBlocked (orientLinkEdit) &&
                                (!onlyAllowedEdits || isOrientationAllowed (orientLinkEdit))){
                            change = true;
                            oriented = true;
                            lastOrientationEdits.put (orientLinkEdit, null);
                            return (new LearningEditProposal (orientLinkEdit, new StringEditMotivation("Do not create cycles")));
                        }
                        if (!oriented){
                            siblingsNodeZ = nodeZ.getSiblings ();
                            siblingsNodeZ.remove (nodeX);
                            for (Node nodeY : siblingsNodeZ){
                                if (!nodeY.getNeighbors ().contains (nodeX)){
                                    for (Node nodeW : siblingsNodeZ){
                                        if (!nodeY.equals (nodeW)){
                                            skip = false;
                                            if (!nodeX.getChildren ().contains (nodeW)){
                                                skip = true;
                                            }
                                            if(probNet.getGraph ().getLink (nodeZ, nodeY, true) != null){
                                                skip = true;
                                            }
                                            if (!skip){
                                                orientLinkEdit = new OrientLinkEdit (probNet,
                                                        probNet.getProbNode (nodeZ).getVariable (),
                                                        probNet.getProbNode (nodeW).getVariable (),
                                                        true);
                                                if (nodeY.getChildren ().contains (nodeW) &&
                                                        !alreadyConsidered (orientLinkEdit, (ArrayList<Node>)null, lastOrientationEdits) &&
                                                        !isBlocked (orientLinkEdit) &&
                                                        (!onlyAllowedEdits || isOrientationAllowed (orientLinkEdit))){
                                                    change = true;
                                                    skip=true;
                                                    lastOrientationEdits.put (orientLinkEdit, null);
                                                    return (new LearningEditProposal (orientLinkEdit, new StringEditMotivation("Do not create cycles")));
                                                }
                                            }
                                            if (!skip){
                                                orientLinkEdit = new OrientLinkEdit (probNet,
                                                        probNet.getProbNode (nodeZ).getVariable (),
                                                        probNet.getProbNode (nodeY).getVariable (),
                                                        true);
                                                if (nodeW.getChildren ().contains (nodeY) &&
                                                        !alreadyConsidered (orientLinkEdit, (ArrayList<Node>)null, lastOrientationEdits) &&
                                                        !isBlocked (orientLinkEdit) &&
                                                        (!onlyAllowedEdits || isOrientationAllowed (orientLinkEdit))){
                                                    change = true;
                                                    skip=true;
                                                    lastOrientationEdits.put (orientLinkEdit, null);
                                                    return (new LearningEditProposal (orientLinkEdit, new StringEditMotivation("Do not create cycles")));
                                                }
                                            }       
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            for (Link link : probNet.getGraph ().getLinks ()){
                nodeX = link.getNode1 ();
                nodeZ = link.getNode2 ();
                if(!link.isDirected ()) {   // X--Z
                    orientLinkEdit = new OrientLinkEdit (probNet,
                            probNet.getProbNode (nodeX).getVariable (),
                            probNet.getProbNode (nodeZ).getVariable (),
                            true);
                    if(!probNet.getGraph ().existsPath (nodeZ, nodeX, true) &&
                            !alreadyConsidered (orientLinkEdit, (ArrayList<Node>)null, lastOrientationEdits) &&
                            !isBlocked (orientLinkEdit) &&
                            (!onlyAllowedEdits || isOrientationAllowed (orientLinkEdit))){
                        change2=true;
                        lastOrientationEdits.put (orientLinkEdit, null);
                        return (new LearningEditProposal (orientLinkEdit, new StringEditMotivation("Do not create cycles")));
                    }
                    else 
                    {
                        orientLinkEdit = new OrientLinkEdit (probNet,
                                probNet.getProbNode (nodeZ).getVariable (),
                                probNet.getProbNode (nodeX).getVariable (),
                                true);
                        if (!isBlocked (orientLinkEdit) &&
                            (!onlyAllowedEdits || isOrientationAllowed (orientLinkEdit)) &&
                        	(!alreadyConsidered (orientLinkEdit, (ArrayList<Node>)null, lastOrientationEdits)))
                        {
                            change2=true;
                            lastOrientationEdits.put (orientLinkEdit, null);
                            return (new LearningEditProposal (orientLinkEdit, new StringEditMotivation("Do not create cycles")));
                        }
                    }
                }
            }
            orientLinkEdit = null;
        }
        if ((orientLinkEdit == null) && (lastOrientationEdits.isEmpty ())){
            phase = ORIENTATION_FINISHED;
        }
        return null;
    }
    
    private boolean isOrientationAllowed (OrientLinkEdit orientLinkEdit)
    {
        Node sourceNode = probNet.getProbNode (orientLinkEdit.getVariable1 ()).getNode();
        Node destinationNode = probNet.getProbNode (orientLinkEdit.getVariable2 ()).getNode();
        return (!probNet.getGraph ().existsPath (destinationNode, sourceNode, true) &&
                isAllowed(orientLinkEdit));
    }
    
    public void undoableEditWillHappen(UndoableEditEvent event)
                    throws ConstraintViolationException, CanNotDoEditException {
    }

    public void undoEditHappened(UndoableEditEvent event) {
        UndoableEdit edit = event.getEdit ();
        ProbNode nodeX, nodeY;
        int indexNodeX;
        double linkScore;
        
        try {
	        if(edit.getClass () == RemoveLinkEdit.class){
	            phase = INITIAL_PHASE;
	            RemoveLinkEdit removeLinkEdit = (RemoveLinkEdit) edit;
	            nodeX = probNet.getProbNode (removeLinkEdit.getVariable1 ());
	            nodeY = probNet.getProbNode (removeLinkEdit.getVariable2 ());
	            indexNodeX = probNet.getProbNodes ().indexOf (nodeX);
				linkScore = 1 - independenceTester.test (
				        probNet, cases, nodeX.getNode (), nodeY.getNode(), 
				        separationSets.get (indexNodeX).
								get(nodeY.getNode()));
				
	            cache.cacheScore(nodeX.getNode(), nodeY.getNode(), 
	            		new PCEditMotivation(linkScore, 
	            		ProbNet.getProbNodesOfNodes(separationSets.get (indexNodeX).
	            				get(nodeY.getNode()))));
	        }
	        else if(edit.getClass () == AddLinkEdit.class){
	            AddLinkEdit addLinkEdit = (AddLinkEdit) edit;
	            nodeX = probNet.getProbNode (addLinkEdit.getVariable1 ());
	            nodeY = probNet.getProbNode (addLinkEdit.getVariable2 ());
	            probNet.removeLink (nodeX, nodeY, false);
	            phase = INITIAL_PHASE;
	        }           
	        else if(edit.getClass () == COrientLinksEdit.class){
	            phase = INITIAL_PHASE;
	        }
	        else if(edit.getClass () == OrientLinkEdit.class){
	            phase = HEAD_TO_HEAD_ORIENTATION;
	        }
	        resetHistory ();
        } catch (ProbNodeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void undoableEditHappened(UndoableEditEvent event) {
        
        UndoableEdit edit = event.getEdit ();
        ProbNode nodeX, nodeY;
        int indexNodeX;

        if(edit.getClass () == RemoveLinkEdit.class)
        {
            RemoveLinkEdit removeLinkEdit = (RemoveLinkEdit) edit;
            nodeX = probNet.getProbNode (removeLinkEdit.getVariable1 ());
            nodeY = probNet.getProbNode (removeLinkEdit.getVariable2 ());
            indexNodeX = probNet.getProbNodes ().indexOf (nodeX);
            if(!lastRemovedEdits.containsKey (removeLinkEdit))
            {
                separationSets.get (indexNodeX).put (nodeY.getNode(), 
                        new ArrayList<Node> ());
            }
            cache.cacheScore(nodeX.getNode(), nodeY.getNode(), 
            		new PCEditMotivation(PCCache.ALREADY_DONE, 
            		ProbNet.getProbNodesOfNodes(separationSets.get (indexNodeX).
            				get(nodeY.getNode()))));
        }
        //An AddLinkEdit can only be done by the user. Just undirect the link
        if(edit.getClass() == AddLinkEdit.class)
        {
            AddLinkEdit addLinkEdit = (AddLinkEdit) edit;
            nodeX = probNet.getProbNode (addLinkEdit.getVariable1 ());
            nodeY = probNet.getProbNode (addLinkEdit.getVariable2 ());
            probNet.removeLink(nodeX, nodeY, true);
            probNet.addLink(nodeX, nodeY, false);
            phase = INITIAL_PHASE;
        }
        else if(edit.getClass() == COrientLinksEdit.class){
            
        }
        resetHistory();
    }
    

    public LearningEditMotivation getMotivation (PNEdit edit)
    {
        ProbNode nodeX, nodeY, nodeZ;
        int indexNodeX;
        List<Node> separationSet = null;
        LearningEditMotivation motivation = null;
        double linkScore = 0;
        
        if(edit.getClass() == RemoveLinkEdit.class)
        {
            RemoveLinkEdit removeLinkEdit = (RemoveLinkEdit) edit;
            nodeX = probNet.getProbNode (removeLinkEdit.getVariable1 ());
            nodeY = probNet.getProbNode (removeLinkEdit.getVariable2 ());
            indexNodeX = probNet.getProbNodes ().indexOf (nodeX);
            separationSet = separationSets.get (indexNodeX).get(nodeY.getNode());
            
            motivation = cache.getScore (nodeX.getNode(), 
                    nodeY.getNode());
            
        }
        else if(edit.getClass() == COrientLinksEdit.class)
        {
            try {
                COrientLinksEdit compoundDirectLinkEdit = (COrientLinksEdit) edit;
                nodeX = probNet.getProbNode (((OrientLinkEdit)compoundDirectLinkEdit.getEdits().get(0)).getVariable1 ());
                nodeZ = probNet.getProbNode (((OrientLinkEdit)compoundDirectLinkEdit.getEdits().get(0)).getVariable2 ());
                nodeY = probNet.getProbNode (((OrientLinkEdit)compoundDirectLinkEdit.getEdits().get(1)).getVariable1 ());
                indexNodeX = probNet.getProbNodes ().indexOf (nodeX);
                separationSet = separationSets.get (indexNodeX).get(nodeY.getNode());
                motivation = new StringEditMotivation ("Sep. set (" + nodeX.getName() + ", " + nodeY.getName() + ") does not contain variable: " + nodeZ.getName());
            } catch (NonProjectablePotentialException
                    | WrongCriterionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(edit.getClass() == OrientLinkEdit.class)
        {
            motivation = new StringEditMotivation("Do not create cycles");
        }
        return motivation;
    }
    
    @Override
    public boolean isLastPhase ()
    {
        return (phase >= REMAINING_LINKS_ORIENTATION);
    }
    
    protected void resetHistory ()
    {
        phase = INITIAL_PHASE;
        lastRemovedEdits.clear ();
        lastOrientationEdits.clear();
        lastCompoundOrientationEdits.clear ();
    }

	@Override
	public Collection<LearningEditProposal> getProposedEditsForVariable(
			ProbNet learnedNet, Variable head, boolean onlyAllowedEdits,
			boolean onlyPositiveEdits) {
		// TODO 
		return null;
	}
    
}