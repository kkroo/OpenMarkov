package org.openmarkov.learning.algorithm.pc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;

public class PCCache {
	
	public static final int ALREADY_DONE = -1;
	
	protected List<HashMap<Node, PCEditMotivation>> lookUpTable;
	
	/** number assigned to each node **/
	protected HashMap<Node, Integer> orderedNodes;

	protected int numNodes;

	public PCCache(ProbNet newNet)  {
        this.numNodes = newNet.getNumNodes();

        orderedNodes = new HashMap<Node,Integer>();
        lookUpTable = new ArrayList<HashMap<Node, PCEditMotivation>>();
        
        int i = 0;
        for (Node node : ProbNet.getNodesOfProbNodes(newNet.getProbNodes())){
            orderedNodes.put(node, i);
            lookUpTable.add(new HashMap<Node, PCEditMotivation>());
            i++;
        }
    }
	
	public void cacheScore(Node node1, Node node2, PCEditMotivation motivation) {
        
		if (orderedNodes.get (node1) < orderedNodes.get (node2))
		{
	        lookUpTable.get (orderedNodes.get (node1)).put (node2, motivation);
		}
		else
		{
			lookUpTable.get (orderedNodes.get (node2)).put (node1, motivation);
		}
	}  //put
	
	public PCEditMotivation getScore(Node node1, Node node2) {
		
		PCEditMotivation motivation;
		
		motivation = (orderedNodes.get (node1) < orderedNodes.get (node2)) 
				? lookUpTable.get(orderedNodes.get (node1)).get (node2) 
	        	: lookUpTable.get(orderedNodes.get (node2)).get (node1);			
		
		return motivation;
	} // get

	
	/** p-value for each pair of nodes and separation set**
	protected List<List<HashMap<List<Node>, Double>>>lookUpTable;
	
	/** number assigned to each node*
	protected HashMap<Node, Integer> orderedNodes;

	protected int numNodes;


	public PCCache(ProbNet newNet)  {
        this.numNodes = newNet.getNumNodes();

        orderedNodes = new HashMap<Node,Integer>();
        lookUpTable = new ArrayList<List<HashMap<List<Node>, Double>>>();
        
        int i = 0;
        for (Node node : ProbNet.getNodesOfProbNodes(newNet.getProbNodes())){
            orderedNodes.put(node, i);
            lookUpTable.add(new ArrayList<HashMap<List<Node>, Double>>());
            for (int j=0; j< newNet.getVariables().size(); ++j){
            	lookUpTable.get(i).add(new HashMap<List<Node>, Double>());
            }
            i++;
        }
    }

	public void cacheScore(Node node1, Node node2, List<Node> separationSet,
			double pValue) {
        
		if (orderedNodes.get(node1) < orderedNodes.get(node2))
		{
	        lookUpTable.get(orderedNodes.get(node1)).get(orderedNodes.get(node2)).
	        	put(separationSet, pValue);
		}
		else
		{
			lookUpTable.get(orderedNodes.get(node2)).get(orderedNodes.get(node1)).
        		put(separationSet, pValue);
		}
	}  //put

	public Double getScore(Node node1, Node node2, List<Node> separationSet) {
		
		if (orderedNodes.get(node1) < orderedNodes.get(node2))
		{
	        return lookUpTable.get(orderedNodes.get(node1)).get(orderedNodes.
	        		get(node2)).get(separationSet);
		}
		else
		{
			return lookUpTable.get(orderedNodes.get(node2)).get(orderedNodes.
					get(node1)).get(separationSet);
		}
	} // get
*/
}
