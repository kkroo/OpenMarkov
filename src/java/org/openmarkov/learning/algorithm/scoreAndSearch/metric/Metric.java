/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.algorithm.scoreAndSearch.metric;

import java.util.HashMap;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.BaseLinkEdit;
import org.openmarkov.core.action.InvertLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.learning.algorithm.scoreAndSearch.cache.Cache;
import org.openmarkov.learning.core.util.Util;

/** This abstract class defines the basic elements of a metric.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @author ibermejo
 * @version 1.1
 * @since OpenMarkov 1.0 */
public abstract class Metric implements PNUndoableEditListener{
	
    // Members
    
    /** Cache used to speed up the search of the best editions */
    protected Cache cache = null;
    
    /** Net we are generating editions for */
    protected ProbNet probNet;   
    
    /** Case database we are using for learning*/
    protected int[][] cases;      
    
    /** Current Score */
    protected double cachedScore;
    
    /** HashMap with the score of each node. (We do not want to
     * recalculate the score of all the nodes of the net every time
     * we make an edition)*/
    protected HashMap<String, Double> cachedNodeScores;     
    
    // Constructor
    /**
     */
    public Metric() {
    }
    
    //Methods
    public void init(ProbNet probNet, int[][] cases)
    {
        this.probNet = probNet;
        this.cases = cases;

        probNet.getPNESupport ().addUndoableEditListener (this);
    }
    
    /**
     * Scores the node based on a table potential with the absolute 
     * frequencies of the values of the child node given the parent nodes.
     * @param nodePotential
     * @return score for the node
     */
    public abstract double score(TablePotential nodePotential);

    /**
     * Scores the associated network. 
     */    
    public double getScore()
    {
        if(cache == null)
            initCache();
        return cachedScore;
    }

    /**
     * Scores the associated network given the edit.
     * @param edit
     * @return
     */
    public double getScore(PNEdit edit)
    {
        if(cache == null)
            initCache();
        return cache.getScore (edit);
    }
    
    /**
     * Scores the associated network given the edit.
     * @param edit
     * @return
     */
    protected double score(PNEdit edit)
    {
        double newScore = 0;
        Class<?> pNEditClass = edit.getClass ();
        if (pNEditClass == AddLinkEdit.class)
        {
            newScore = score ((AddLinkEdit) edit, false);
        }
        else if (pNEditClass == RemoveLinkEdit.class)
        {
            newScore = score ((RemoveLinkEdit) edit, false);
        }
        else
        {
            newScore = score ((InvertLinkEdit) edit, false);
        }
        return newScore;
    }    
    
    
    /**
     * Scores the associated network with the link given in the received 
     * edition added. We only have to recalculate the score 
     * of the destination node. If an undoable edit happened (that is, if
     * parameter change is true) we update the entropy and dimension of the
     * destination node and the net.
     * @param edition <code>AddLinkEdit</code> 
     * @param change <code>boolean</code> indicates whether the edition is 
     * definitive (UndoableEditHappend called this method) or not.
     * @return <code>double</code> score of the net with the given edition
     */
    protected double score(AddLinkEdit edition, boolean change) {
        
        ProbNode destinationNode = probNet.getProbNode(edition.getVariable2());
        ProbNode originNode = probNet.getProbNode(edition.getVariable1());
        double lastNodeScore;
        double newNodeScore;
        
        lastNodeScore = cachedNodeScores.get(destinationNode.getName());
        TablePotential absFrequencies = Util.getAbsoluteFreqExtraParent (probNet, cases, destinationNode, originNode);
        newNodeScore = scoreNode(destinationNode, absFrequencies, false);
        
        /*If change is true it's because we have to update the probNet values
         * and store the node dimension and entropy to avoid repeating the
         * calculations */
        if (change){
            cachedNodeScores.put (destinationNode.getName (),
                                  new Double (newNodeScore));
        }
        
        return newNodeScore - lastNodeScore;
    }

    /**
     * Scores the associated network with the link given in the received 
     * edition removed. We only have to recalculate the score 
     * of the destination node. If an undoable edit happened (that is, if
     * parameter change is true) we update the entropy and dimension of the
     * destination node and the net.
     * @param edition <code>AddLinkEdit</code> 
     * @param change <code>boolean</code> indicates wheter the edition is 
     * definitive (UndoableEditHappend called this method) or not.
     * @return <code>double</code> score of the net with the given edition
     */
    protected double score(RemoveLinkEdit edition, boolean change) {
        ProbNode destinationNode = probNet.getProbNode(
                edition.getVariable2());
        ProbNode originNode = probNet.getProbNode(edition.getVariable1());
        double lastNodeScore;
        double newNodeScore;
        
        lastNodeScore = cachedNodeScores.get(destinationNode.getName());
        TablePotential absFrequencies = Util.getAbsoluteFreqRemovingParent (probNet, cases, destinationNode, originNode);
        newNodeScore = scoreNode(destinationNode, absFrequencies, false);
        
        /*If change is true it's because we have to update the probNet values
         * and store the node dimension and entropy to avoid repeating the
         * calculations */
        if (change){
            cachedNodeScores.put(destinationNode.getName(), 
                    new Double(newNodeScore));
        }
        
        return newNodeScore - lastNodeScore;
    }
    
    /**
     * Scores the associated network with the link given in the received 
     * edition inverted. We have to recalculate the scores 
     * of the destination nodes before and after the inversion. If an undoable 
     * edit happened (that is, if parameter change is true) we update the 
     * entropy and dimension of the destination node and the net.
     * @param edition <code>AddLinkEdit</code> 
     * @param change <code>boolean</code> indicates whether the edition is 
     * definitive (UndoableEditHappend called this method) or not.
     * @return <code>double</code> score of the net with the given edition
     */
    protected double score(InvertLinkEdit edition, boolean change) {
        ProbNode initialDestinationNode = probNet.getProbNode (edition.getVariable2 ());
        ProbNode initialOriginNode = probNet.getProbNode (edition.getVariable1 ());
        double lastNodeScore;
        double newNodeScore;
        double result;
        lastNodeScore = cachedNodeScores.get (initialDestinationNode.getName ());
        TablePotential absFrequencies = Util.getAbsoluteFreqRemovingParent (probNet, cases, initialDestinationNode, initialOriginNode);
        newNodeScore = scoreNode (initialDestinationNode,
                                              absFrequencies, false);
        /*
         * If change is true it's because we have to update the probNet values
         * and store the node dimension and entropy to avoid repeating the
         * calculations
         */
        if (change)
        {
            cachedNodeScores.put (initialDestinationNode.getName (),
                                  new Double (newNodeScore));
        }
        result = newNodeScore - lastNodeScore;
        lastNodeScore = cachedNodeScores.get (((ProbNode) initialOriginNode).getName ());
        absFrequencies = Util.getAbsoluteFreqExtraParent (probNet, cases, initialOriginNode, initialDestinationNode);
        newNodeScore = scoreNode (initialOriginNode, absFrequencies, false);
        /*
         * If change is true it's because we have to update the probNet values
         * and store the node dimension and entropy to avoid repeating the
         * calculations
         */
        if (change)
        {
            cachedNodeScores.put (initialOriginNode.getName (),
                                  new Double (newNodeScore));
        }
        result += (newNodeScore - lastNodeScore);
        return result;
    }
    
    
    /**
     * Scores the given node with the new parent given.
     * @param node <code>ProbNode</code>
     * @param extraParent <code>ProbNode</code>
     * @param change <code>boolean</code> indicates whether the edition is
     *            definitive (UndoableEditHappend called this method) or not.
     * @return <code>double</code> score of the node with the given parent
     */
    protected double scoreNode (ProbNode node,
                                TablePotential absFrequencies,
                                boolean change)
    {
        double nodeScore =  score(absFrequencies);

        /* Store the entropy of the node to avoid repeating the calculations */
        if (change)
        {
            cachedNodeScores.put (node.getName (), new Double (nodeScore));
        }
        return nodeScore;
    }
    
    /** An undoable edit will happen.
     * @param event <code>UndoableEditEvent</code> that will happen
     */
    public void undoableEditWillHappen(UndoableEditEvent event){
        if (cache == null)
        {
            initCache ();
        }        
    }
    
    /** 
     * An undoable edit happened. We have to update the copy of the net and
     * score this new net.
     * @param event <code>UndoableEditEvent</code> that happened
     */
    public void undoableEditHappened(UndoableEditEvent event) {
        if(BaseLinkEdit.class.isAssignableFrom (event.getEdit().getClass ()))
        {
            updateCache((BaseLinkEdit)event.getEdit ());
        }
    }

    public void undoEditHappened(UndoableEditEvent event) {
        if(BaseLinkEdit.class.isAssignableFrom (event.getEdit().getClass ()))
        {
            updateCache(((BaseLinkEdit)event.getEdit()).getUndoEdit ());
        }
    }    
    
    /**
     * Method to check whether the link from var1 to var2 is a fixed link.
     * @param var1 origin variable
     * @param var2 destination variable
     * @return true if the link is fixed, otherwise false.
     */
    private boolean isFixedLink (ProbNet probNet, Variable var1, Variable var2){
        return (cache.getRemoveScore (probNet, var1, var2) == Double.NEGATIVE_INFINITY);
    }      
    
    /**
     * Fills cache with data    
     */
    private void initCache()
    {
        this.cache = new Cache();
        cachedNodeScores = new HashMap<String, Double>();
        cache.flush (probNet);
        
        cachedScore = 0;
        for (ProbNode node : probNet.getProbNodes()){
            cachedScore += scoreNode(node, Util.getAbsoluteFreqExtraParent (probNet, cases, node, null), true);
        }          
        PNEdit edit;
        for (Variable tail : probNet.getVariables ())
        {
            for (Variable head : probNet.getVariables ())
            {
                if ((!tail.equals (head)) && (!isFixedLink (probNet, tail, head)))
                {
                    if (!probNet.getProbNode (head).isParent (probNet.getProbNode (tail)))
                        edit = new AddLinkEdit (probNet, tail, head, true);
                    else
                        edit = new RemoveLinkEdit (probNet, tail, head, true);
                    cache.cacheScore (edit, score (edit));
                }
            }
        }        
    }
    
    /**
     * Method to update the cache after doing an edition to the learnedNet.
     * @param bestEdition <code>PNEdit</code> last edition done to the 
     * learnedNet.
     */
    private void updateCache (BaseLinkEdit edit)
    {
        if (cache == null)
        {
            initCache ();
        }
        else
        {
            Class<?> editClass = edit.getClass ();
            try
            {
                if (editClass == AddLinkEdit.class)
                {
                    cachedScore = score ((AddLinkEdit) edit, true);
                }
                else if (editClass == RemoveLinkEdit.class)
                {
                    cachedScore = score ((RemoveLinkEdit) edit, true);
                }
                else if (editClass == InvertLinkEdit.class)
                {
                    cachedScore = score ((InvertLinkEdit) edit, true);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace ();
            }
        }
        PNEdit updatedEdit;
        /* Obtain the destination node of the given edition */
        Variable head = edit.getVariable2 ();
        /*
         * Score all the links that have as destination the destination node of
         * the bestEdition
         */
        for (Variable tail : probNet.getVariables ())
        {
            if ((!tail.equals (head)) && (!isFixedLink (probNet, tail, head)))
            {
                if (!probNet.getProbNode (head).isParent (probNet.getProbNode (tail)))
                {
                    updatedEdit = new AddLinkEdit (probNet, tail, head, true);
                }
                else
                {
                    updatedEdit = new RemoveLinkEdit (probNet, tail, head, true);
                }
                cache.cacheScore (updatedEdit,
                                  score (updatedEdit));
            }
        }

        /*
         * If we have a link inversion, we have to update the entries of the
         * cache of both origin and destination node of the original links
         */
        if (edit.getClass () == InvertLinkEdit.class)
        {
            Variable head2 = edit.getVariable1 ();
            for (Variable tail : probNet.getVariables ())
            {
                if ((!tail.equals (head2))
                    && (!isFixedLink (probNet, tail, head2)))
                {
                    if (!probNet.getProbNode (head2).getNode ().isParent (probNet.getProbNode (tail).getNode ()))
                    {
                        updatedEdit = new AddLinkEdit (probNet, tail, head2,
                                                       true);
                    }
                    else
                    {
                        updatedEdit = new RemoveLinkEdit (probNet, tail, head2,
                                                          true);
                    }
                    cache.cacheScore (updatedEdit,
                                      score (updatedEdit));
                }
            }
        }
    }
 
}
