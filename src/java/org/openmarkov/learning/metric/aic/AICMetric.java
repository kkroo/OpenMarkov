/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.metric.aic;

import java.util.HashMap;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.InvertLinkEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.annotation.MetricType;
import org.openmarkov.learning.metric.entropy.EntropyMetric;

/** This class implements the AIC metric.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
@MetricType(name = "AIC")
public class AICMetric extends EntropyMetric {
    
    //Members
    /** HashMap with the dimension of each node. (We do not want to
     * recalculate the dimension of all the nodes of the net every time
     * we make an edition)*/
    protected HashMap<String, Double> nodesDimensions;    
    
    //Constructor
    /**
     * After constructing the metric, we evaluate the given net.
     * @param probNet <code>ProbNet</code> to evaluate.
     * @param cases <code>double[][]</code> database cases.
     * @throws openmarkov.exceptions.NotEnoughMemoryException
     */
    public AICMetric() {
        super();
        nodesDimensions = new HashMap<String, Double>();
    }
    
    @Override
    public void init (ProbNet probNet, int[][] cases)
    {
        super.init (probNet, cases);
        calculateDimension ();
    }



    /**
     * Scores the associated network.
     * @return <code>double</code> score of the net 
     * @throws openmarkov.exceptions.NotEnoughMemoryException
     */
    @Override
    public double getScore() {
        return super.getScore() - calculateDimension();
    }
    
    /**
     * Scores the associated network with the link given in the received 
     * edition added. We only have to recalculate the entropy and dimension 
     * of the destination node. If an undoable edit happened (that is, if
     * parameter change is true) we update the entropy and dimension of the
     * destination node and the net.
     * @param edition <code>AddLinkEdit</code> 
     * @param change <code>boolean</code> indicates whether the edition is 
     * definitive (UndoableEditHappend called this method) or not.
     * @return <code>double</code> score of the net with the given edition
     * @throws openmarkov.exceptions.NotEnoughMemoryException
     */
    @Override
    protected double score(AddLinkEdit edition, boolean change) {
        
        ProbNode destinationNode = probNet.getProbNode(
                edition.getVariable2());
        /* dimension of the node without adding the link */
        double lastNodeDimension;
        double newNodeDimension;
        
        /* To calculate the dimension, we subtract the last dimension
         * of the destination node and sum the new dimension of this node*/
        lastNodeDimension = nodesDimensions.get(
                ((ProbNode) destinationNode).getName());
        
        newNodeDimension = ((edition.getVariable1().
            getNumStates()) * lastNodeDimension);
        
        /*If change is true it's because we have to update the probNet values
         * and store the node dimension and entropy to avoid repeating the
         * calculations */
        if (change == true){
            nodesDimensions.put(destinationNode.getName(), 
                    new Double(newNodeDimension));
        }
        
        return super.score(edition, change) - (newNodeDimension - lastNodeDimension);
    }
    
    /**
     * Scores the associated network with the link given in the received 
     * edition removed. We only have to recalculate the entropy and dimension 
     * of the destination node. If an undoable edit happened (that is, if
     * parameter change is true) we update the entropy and dimension of the
     * destination node and the net.
     * @param edition <code>AddLinkEdit</code> 
     * @param change <code>boolean</code> indicates whether the edition is 
     * definitive (UndoableEditHappend called this method) or not.
     * @return <code>double</code> score of the net with the given edition
     * @throws openmarkov.exceptions.NotEnoughMemoryException
     */
    @Override
    protected double score(RemoveLinkEdit edition, boolean change)  {
        
        ProbNode destinationNode = probNet.getProbNode(
                edition.getVariable2());
        /* dimension of the node without adding the link */
        double lastNodeDimension;
        double newNodeDimension;
        
        /* To calculate the dimension, we subtract the last dimension
         * of the destination node and sum the new dimension of this node*/
        lastNodeDimension = nodesDimensions.get(destinationNode.getName());
        newNodeDimension = lastNodeDimension / ((edition.
                getVariable1()).getNumStates());
        
        /*If change is true it's because we have to update the probNet values*/
        if (change){
            nodesDimensions.put (destinationNode.getName (),
                                 new Double (newNodeDimension));
        }
        
        return super.score(edition, change) - (newNodeDimension - lastNodeDimension);
    }
    
    /**
     * Scores the associated network with the link given in the received 
     * edition inverted. We have to recalculate the entropies and dimensions 
     * of the destinations nodes before and after the inversion. If an undoable 
     * edit happened (that is, if parameter change is true) we update the 
     * entropies and dimensions of the destinations node and the net.
     * @param edition <code>InvertLinkEdit</code> 
     * @param change <code>boolean</code> indicates whether the edition is 
     * definitive (UndoableEditHappend called this method) or not.
     * @return <code>double</code> score of the net with the given edition
     * @throws NotEnoughMemoryException
     */
    @Override
    protected double score(InvertLinkEdit edition, boolean change) {
        
        ProbNode initialDestinationNode = probNet.getProbNode (edition.getVariable2 ());
        ProbNode initialOriginNode = probNet.getProbNode(edition.getVariable1());
        /* dimension of the node without adding the link */
        double lastNodeDimension;
        double newNodeDimension;
        double result;

        lastNodeDimension = nodesDimensions.get (((ProbNode) initialDestinationNode).getName ());
        newNodeDimension = lastNodeDimension
                           / ((edition.getVariable1 ()).getNumStates ());
        
        /*If change is true it's because we have to update the probNet values*/
        if (change) {
            nodesDimensions.put(initialDestinationNode.getName(), 
                    new Double(newNodeDimension));
        }
        
        result = super.score (edition, change) - (newNodeDimension - lastNodeDimension);
        
        lastNodeDimension = nodesDimensions.get (((ProbNode) initialOriginNode).getName ());
        newNodeDimension = ((edition.getVariable1 ().getNumStates ()) * lastNodeDimension);
        
        /*If change is true it's because we have to update the probNet values
         * and store the node dimension and entropy to avoid repeating the
         * calculations */
        if (change) {
            nodesDimensions.put(initialOriginNode.getName(), 
                    new Double(newNodeDimension));
        }
        
        result -= newNodeDimension - lastNodeDimension;
        return result;
    }

    /**
     * Calculates the dimension of the net as the sum of the dimensions
     * of each node. It is only used the first time we score the net, so we make 
     * all the calculations.
     * @return double dimension of the net
     */
    protected double calculateDimension() {
        double newDimension = 0;
        
        for (ProbNode node : probNet.getProbNodes()) {
            newDimension += nodeDimension(node, true);
        }         
        return newDimension;
    }
    
    /**
     * Calculates the dimension of the given node as the product of
     * its number of states and the number of possible configurations
     * of its parents.
     * @param node <code>ProbNode</code> whose dimension we want to calculate.
     * @param change <code>boolean</code> indicates whether the edition is 
     * definitive (UndoableEditHappend called this method) or not.
     * @return double dimension of this node
     */
    protected double nodeDimension(ProbNode node, boolean change) {
        int numStates = node.getVariable().getNumStates(); 
        int parentsConfigurations = 1;  
        double nodeDimension = 0;

        if (node.getNode().getNumParents() == 0) {
            parentsConfigurations = 1;
        } else {
            try
            {
                for (Node parent : node.getNode().getParents()) {
                    parentsConfigurations *= ((ProbNode) parent.getObject ()).getVariable ().getNumStates ();
                }
            }catch(ClassCastException e)
            {
                e.printStackTrace ();
            }
        }

        nodeDimension = (numStates - 1) * parentsConfigurations;
        
        /* Store the dimension of the node to avoid repeating the calculations*/
        if (change) {
            nodesDimensions.put(node.getName(), new Double(nodeDimension));
        }
        
        return nodeDimension;
    }
}
