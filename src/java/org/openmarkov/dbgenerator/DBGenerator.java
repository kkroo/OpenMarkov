/*
* Copyright 2012 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.dbgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

public class DBGenerator {
	
	/**
	 * Generates a file containing a database of sampled cases
	 * @param outputPath
	 * @param numberOfCases
	 */
	public CaseDatabase generate(ProbNet probNet, int numberOfCases)
	{
		int[][] cases = new int[numberOfCases][probNet.getProbNodes().size()];
		Random randomGenerator = new Random();
		ArrayList<Integer> sortedNodeIndexes = sortNodesTopologically (probNet);
		for(int i=0; i <numberOfCases; ++i )
		{
			HashMap<Variable, Integer> sampledStateIndexes = new HashMap<Variable, Integer>();
			
			for(int j=0; j < sortedNodeIndexes.size(); ++j)
			{
				ProbNode node = probNet.getProbNodes().get(sortedNodeIndexes.get(j));
				int sampledIndex = node.getPotentials().get(0).sample(randomGenerator, sampledStateIndexes);
				sampledStateIndexes.put(node.getVariable(), sampledIndex);
				cases[i][sortedNodeIndexes.get(j)] = sampledIndex;
			}
		}
		return new CaseDatabase(probNet.getVariables (), cases);
	}
	
    /**
     * Uses the algorithm by Kahn (1962)
     * @param probNet
     * @param variablesToSort
     */
    public ArrayList<Integer> sortNodesTopologically (ProbNet probNet)
    {
        Graph graph = probNet.getGraph().copy ();
        ArrayList<Integer> sortedNodeIndexes = new ArrayList<Integer> (probNet.getProbNodes().size ());
        // Empty list that will contain the sorted elements
        Stack<Node> s = new Stack<Node> ();
        // Set of all nodes with no incoming edges
        ArrayList<Node> l = new ArrayList<Node> ();
        // Look for variables/nodes with no parents
        for(Node node: graph.getNodes ())
        {
            if(node.getParents ().size () == 0)
            {
                s.push (node);
            }
        }
        // while S is non-empty do
        while(!s.isEmpty ())
        {
            // remove a node n from S
            Node n = s.pop ();
            // insert n into L
            l.add (n);
            // for each node m with an edge e from n to m do
            for(Node m: n.getChildren ())
            {
                //remove edge e from the graph
                graph.removeLink (n, m, true);
                //if m has no other incoming edges then insert m into S
                if(m.getParents ().isEmpty ())
                {
                    s.push (m);
                }
            }
        }
        // fill sortedVariables list with filtering the l list with the list of variables to sort
        for(Node node: l)
        {
        	sortedNodeIndexes.add(probNet.getProbNodes().indexOf(node.getObject()));
        }        
        return sortedNodeIndexes;
    } 	
	

}
