package org.openmarkov.learning.metric.bd;


import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.annotation.MetricType;
import org.openmarkov.learning.algorithm.scoreAndSearch.metric.util.MathUtils;
import org.openmarkov.learning.metric.bayesian.BayesianMetric;

/** This class implements the BD metric.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
@MetricType(name = "BD")
public class BDMetric extends BayesianMetric {
    
    //Constructor
    /**
     * Construct an instance of BDMetric
     * @param alpha <code>double</code> alpha parameter
     */
    public BDMetric(double alpha) {
        super(alpha);
    }
    
    @Override
    public double score (TablePotential tablePotential)
    {
        double nodeScore = 0;
        int numStates = tablePotential.getVariable (0).getNumStates ();
        double[] freq = tablePotential.getValues ();
        int position = 0;
        double n_ij;
        double n_ijk;
        int parentConfigurations = freq.length / numStates;
        
        
        while (position < freq.length) {
            n_ij = 0;
            double sumStates = 0;
            //k-th state of the node
            for (int k = 0; k < numStates; k++) {
                n_ijk = freq[position];
                n_ij += n_ijk;
                sumStates += (MathUtils.lnGamma((1.0 / (numStates * parentConfigurations)) + n_ijk));
                position++;
            }
            nodeScore += (MathUtils.lnGamma(1.0 / parentConfigurations)) 
                    - MathUtils.lnGamma(n_ij + (1.0 / parentConfigurations))
                    - numStates * MathUtils.lnGamma(1.0 / (numStates * parentConfigurations)) + sumStates; 
        }
        
        return nodeScore;
    }
    
//    @Override
//    /**
//     * Scores the given node with the new parent given. 
//     * @param node <code>ProbNode</code> 
//     * @param extraParent <code>ProbNode</code>
//     * @param change <code>boolean</code> indicates whether the edition is 
//     * definitive (UndoableEditHappend called this method) or not.
//     * @return <code>double</code> score of the node with the given parent
//     * @throws openmarkov.exceptions.NotEnoughMemoryException
//     */
//    protected double scoreNode (ProbNode node,
//                                ProbNode extraParent,
//                                boolean change)
//    {
//        int numStates = ((Variable) node.getVariable ()).getNumStates ();
//        int numParents = node.getNode ().getNumParents ();
//        int parentsConfigurations = 1;
//        double sumStates = 0;
//        double nodeScore = 0;
//        double n_ij;
//        double n_ijk;
//        int position = 0;
//        double[] freq = null;
//        int indexParent = 0;
//        if (extraParent != null) numParents++;
//        int[] indexParents = new int[numParents];
//        ArrayList<Variable> variables = new ArrayList<Variable> ();
//        variables.add ((Variable) node.getVariable ());
//        if ((numParents == 0) && (extraParent == null))
//        {
//            parentsConfigurations = 1;
//        }
//        else
//        {
//            if (extraParent != null)
//            {
//                indexParents[0] = probNet.getProbNodes ().indexOf (extraParent);
//                variables.add ((Variable) extraParent.getVariable ());
//                parentsConfigurations *= ((Variable) extraParent.getVariable ()).getNumStates ();
//                indexParent = 1;
//            }
//            for (ProbNode parent : ProbNet.getProbNodesOfNodes (node.getNode ().getParents ()))
//            {
//                variables.add ((Variable) parent.getVariable ());
//                indexParents[indexParent] = probNet.getProbNodes ().indexOf (parent);
//                parentsConfigurations *= ((Variable) parent.getVariable ()).getNumStates ();
//                indexParent++;
//            }
//        }
//        nodeScore = score (Util.getAbsoluteFreq (probNet, cases, node));
//        ;
//        if (change) cachedNodeScores.put (node.getName (),
//                                          new Double (nodeScore));
//        return nodeScore;
//    }
//    
//    @Override
//     /**
//     * Scores the given node without the removed parent given. 
//     * @param node <code>ProbNode</code> 
//     * @param removedParent <code>ProbNode</code>
//     * @param change <code>boolean</code> indicates wheter the edition is 
//     * definitive (UndoableEditHappend called this method) or not.
//     * @return <code>double</code> score of the node without the given parent
//     * @throws openmarkov.exceptions.NotEnoughMemoryException
//     */
//    protected double scoreNodeRemovingLink(ProbNode node, ProbNode removedParent, 
//            boolean change) throws NotEnoughMemoryException{
//        int numStates = ((Variable) node.getVariable()).getNumStates(); 
//        int numParents = node.getNode().getNumParents();
//        int[] indexParents = new int[numParents-1];
//        int parentsConfigurations = 1;  
//        double sumStates = 0;
//        double nodeScore = 0;
//        double n_ij;
//        double n_ijk;
//        int position = 0;	
//        double[] freq = null;
//        
//        ArrayList<Variable> variables = new ArrayList<Variable>(); 
//        variables.add(node.getVariable());
//            
//        if (numParents == 1) {
//            parentsConfigurations = 1;
//        } else {
//            int i = 0;
//            for (ProbNode parent : ProbNet.getProbNodesOfNodes(node.getNode().
//                    getParents())) {
//                if (!removedParent.getName().equals(parent.getName())) {
//                    indexParents[i] = probNet.getProbNodes().indexOf(parent);
//                    variables.add((Variable) parent.getVariable());
//                    parentsConfigurations *= ((Variable) 
//                            parent.getVariable()).getNumStates();
//                    i++;
//                }
//            }
//        }
//        
//        freq = Util.absolute (probNet, cases, node, parentsConfigurations, variables, indexParents, node.getVariable().getNumStates()).
//                getValues();
//        
//        //j-th configuration of the parents
//        for (int j = 0; j < parentsConfigurations; j++) {
//            n_ij = 0;
//            sumStates = 0;
//            //k-th state of the node
//            for (int k = 0; k < numStates; k++) {
//                n_ijk = freq[position];
//                n_ij += n_ijk;
//                sumStates += (MathUtils.lnGamma((1.0 / (numStates * parentsConfigurations)) + n_ijk));
//                position++;
//            }
//            nodeScore += (MathUtils.lnGamma(1.0 / parentsConfigurations))
//            		- MathUtils.lnGamma(n_ij + (1.0 / parentsConfigurations))
//            		- numStates * MathUtils.lnGamma(1.0 / (numStates * parentsConfigurations)) + sumStates;
//        }
//        
//        /* Store the entropy of the node to avoid repeating the calcularions */
//        if (change)
//            cachedNodeScores.put(node.getName(), new Double(nodeScore));
//        
//        return nodeScore;
//    }
}
