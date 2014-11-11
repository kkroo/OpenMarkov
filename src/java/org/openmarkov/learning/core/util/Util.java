/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.core.util;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * Calculates frequencies
 * @author Iñigo
 *
 */
public class Util
{
    /**
     * Calculate the absolute frequencies in the database of each of the
     * configurations of the given node and its parents.
     * @param probNode <code>ProbNode</code> whose frequencies we want to 
     * calculate.
     * @param parentsConfigurations product of the number of states of the
     * parents.
     * @param variables <code>ArrayList</code> formed by the variable associated
     * to the given node and the variables associated to its parents.
     * @param indexesOfParents <code>int[]</code> indexes of the parents in the
     * probNet list of nodes.
     * @param numValues number of states of the given node
     * @return <code>TablePotential</code> with the absolute frequencies in
     * the database of each of the configurations of the given node and its
     * parents.
     */
    private static TablePotential getAbsoluteFrequencies (ProbNet probNet,
                                           int[][] cases,
                                           ProbNode probNode,
                                           List<Variable> variables)
    {
        
        int parentsConfigurations = 1;
        int numValues = probNode.getVariable ().getNumStates ();
        // We miss the first one as it is the node itself, not one of its parents
        int[] indexesOfParents = new int[variables.size () -1];
        for (int i = 0; i < indexesOfParents.length; ++i)
        {
            indexesOfParents[i] = probNet.getProbNodes ().indexOf (probNet.getProbNode (variables.get (i + 1)));
            parentsConfigurations *= variables.get (i + 1).getNumStates ();
        }
        TablePotential absoluteFreqPotential = new TablePotential (
                                                        variables,
                                                        PotentialRole.CONDITIONAL_PROBABILITY);
        double[] absoluteFreqs = absoluteFreqPotential.getValues ();
        // Initialize the table
        for (int i = 0; i < parentsConfigurations * numValues; i++)
        {
            absoluteFreqs[i] = 0;
        }
        variables.remove (0);
        // Compute the absolute frequencies
        int iCPT;
        int iParent, iNode = probNet.getProbNodes ().indexOf (probNet.getProbNode (probNode.getVariable ()));
        List<ProbNode> nodes = probNet.getProbNodes (variables);
        for (int i = 0; i < cases.length; i++)
        {
            iCPT = 0;
            for (int j = 0; j < nodes.size (); ++j)
            {
                iParent = indexesOfParents[j];
                iCPT = iCPT * nodes.get (j).getVariable ().getNumStates ()
                       + cases[i][iParent];
            }
            absoluteFreqs[numValues * iCPT + cases[i][iNode]]++;
        }
        return absoluteFreqPotential;
    }

    
    /**
     * Calculate the absolute frequencies in the database of each of the
     * configurations of the given node and its parents and a given extra
     * parent.
     * @param node <code>ProbNode</code> whose frequencies we want to calculate.
     * @param extraParent <code>ProbNode</code>
     * @return <code>TablePotential</code> with the absolute frequencies in the
     *         database of each of the configurations of the given node and its
     *         parents and a given extra parent.
     */
    public static TablePotential getAbsoluteFreq (ProbNet probNet,
                                                  int[][] cases,
                                                  ProbNode node)
    {
        ArrayList<Variable> variables = new ArrayList<Variable> ();
        variables.add ((Variable) node.getVariable ());
        for (ProbNode parent : ProbNet.getProbNodesOfNodes (node.getNode ().getParents ()))
        {
            variables.add ((Variable) parent.getVariable ());
        }
        return getAbsoluteFrequencies (probNet, cases, node, variables);
    }    
    /**
     * Calculate the absolute frequencies in the database of each of the
     * configurations of the given node and its parents and a given extra
     * parent.
     * @param node <code>ProbNode</code> whose frequencies we want to calculate.
     * @param extraParent <code>ProbNode</code>
     * @return <code>TablePotential</code> with the absolute frequencies in the
     *         database of each of the configurations of the given node and its
     *         parents and a given extra parent.
     */
    public static TablePotential getAbsoluteFreqExtraParent (ProbNet probNet,
                                                                          int[][] cases,
                                                                          ProbNode node,
                                                                          ProbNode extraParent)
    {
        ArrayList<Variable> variables = new ArrayList<Variable> ();
        variables.add ((Variable) node.getVariable ());
        
        for (ProbNode parent : ProbNet.getProbNodesOfNodes (node.getNode ().getParents ()))
        {
            if(!variables.contains (parent.getVariable ()))
                variables.add (parent.getVariable ());
        }
        if ((extraParent != null) && (!variables.contains (extraParent.getVariable ())))
        {
            variables.add (extraParent.getVariable ());
        }
        return getAbsoluteFrequencies (probNet, cases, node, variables);
    }

    /**
     * Calculate the absolute frequencies in the database of each of the
     * configurations of the given node and its parents except one.
     * @param node <code>ProbNode</code> whose frequencies we want to calculate.
     * @param removedParent <code>ProbNode</code> that we do not want to include
     * in the calculations
     * @return <code>TablePotential</code> with the absolute frequencies in
     * the database of each of the configurations of the given node and its
     * parents except one.
     */
    public static TablePotential getAbsoluteFreqRemovingParent (ProbNet probNet,
                                                                            int[][] cases,
                                                                            ProbNode node,
                                                                            ProbNode removedParent)
    {
        List<Variable> variables = new ArrayList<Variable> ();
        variables.add (node.getVariable ());
        
        List<ProbNode> parents = ProbNet.getProbNodesOfNodes (node.getNode ().getParents ());
        for (ProbNode parent : parents)
        {
            if (parent.getVariable () != removedParent.getVariable ())
            {
                variables.add (parent.getVariable ());
            }
        }
        return getAbsoluteFrequencies (probNet, cases, node, variables);
    }
    
    
}
