/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.inference.heuristic.canoAndMoral;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.swing.event.UndoableEditEvent;

import org.apache.log4j.Logger;
import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.UtilMarkovNet;
import org.openmarkov.core.model.network.Variable;

/** Implements the heuristic triangulation algorithm defined by <cite>Andres 
 *  Cano and Serafin Moral</cite> in <cite>Heuristic Algorithms for the 
 *  Triangulation of Graphs</cite>
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
public class CanoMoralElimination extends EliminationHeuristic {

	// Attributes
	/** The <code>Graph</code> triangulated. */
	private ProbNet markovNet;
	
	private List<ProbNode> nodesToEliminate;
	
	private Logger logger;
	
    // Constructor
	/** @param probNet <code>ProbNet</code>.
	 * @param setsOfVariablesToEliminate <code>ArrayList</code> of 
	 *  <code>Variable</code>  */
	public CanoMoralElimination(ProbNet probNet, 
			List<List<Variable>> setsOfVariablesToEliminate) {
		super(probNet, setsOfVariablesToEliminate);
		// Creates an undirected to be graph used for triangulation
		markovNet= UtilMarkovNet.getMarkovNet(probNet);
		nodesToEliminate = new ArrayList<ProbNode>();
		for (List<Variable> array : setsOfVariablesToEliminate) {
			for (Variable variable : array) {
				nodesToEliminate.add(markovNet.getProbNode(variable));
			}
		}
		
		logger = Logger.getLogger(CanoMoralElimination.class);
	}
	
	// Methods
	/** Applies the algorithm
	 * @return A triangulated undirected graph. <code>Graph</code>.  */
	public Graph getTriangulatedGraph() {
		List<ProbNode> deletionSequence = getDeletionSequence();
		triangulate(deletionSequence);
		return markovNet.getGraph();
	}
	
	/** It does nothing.
	 * @param event <code>UndoableEditEvent</code>. */
	public void undoableEditWillHappen(UndoableEditEvent event) {
	}
	
	/** @param event (<code>UndoableEditEvent</code>) that contains the edit
	 *   with the <code>variableToDelete</code>. */
	public void undoableEditHappened(UndoableEditEvent event) {
		Variable variableToDelete =getEventVariable(event);
		if (variableToDelete != null) {
			ProbNode nodeToDelete = markovNet.getProbNode(variableToDelete);
			// Create a clique with the siblings of this node
			List<Node> siblings = nodeToDelete.getNode().getSiblings();
			createClique(siblings);

			// Eliminate the node and its links
			markovNet.getGraph().removeLinks(nodeToDelete.getNode());
			markovNet.removeProbNode(nodeToDelete);
			nodesToEliminate.remove(nodeToDelete);
		}
	}

    /**
     * Triangulate the graph given the deletion sequence
     * @param deletionSequence <code>ArrayList</code> of <code>Node</code>s.
     */
    public void triangulate (List<ProbNode> deletionSequence)
    {
        // Creates an array of booleans for each node in the deletion sequence
        List<ProbNode> nodeList = markovNet.getProbNodes ();
        int nodeListSize = nodeList.size ();
        HashMap<Integer, Node> eliminated = new HashMap<Integer, Node> (nodeListSize);
        for (ProbNode toDelete : deletionSequence)
        {
            // Collect the neighbors nodes to each node in the deletion sequence
            List<Node> notDeletedNeighbors = collectNeighbors (eliminated, toDelete.getNode ());
            // Adds links between the collected filtered nodes
            createClique (notDeletedNeighbors);
        }
    }
	
	@Override
	/** @return Next <code>Variable</code> to delete. */
	public Variable getVariableToDelete() {
		ProbNode toDeleteNode = null;
		if (nodesToEliminate != null && nodesToEliminate.size() > 0) {
			toDeleteNode = nodesToEliminate.get(0);
		}
		// Heuristic algorithm from Andres Cano and Serafin Moral
		double H6 = Double.MAX_VALUE, aux; 
		for (ProbNode node : nodesToEliminate) {
	        double createdCliqueSize = createdCliqueSize(node);
	        double sumCliqueSizes = sumCliqueSizes(node);
			aux = createdCliqueSize / sumCliqueSizes;
			if (aux < H6) {
				H6 = aux;
				toDeleteNode = (ProbNode)node;
			}
		}
		if (toDeleteNode != null) {
			return toDeleteNode.getVariable();
		}
		else return null;
	}

	/** Collect the neighbors nodes of a node that belogns to the deletion 
	 *  sequence excluding the nodes of the eliminated array. 
     * @param eliminated (<code>HashMap</code> with key = <code>Integer</code>
     *  and value = <code>Node</code>)
	 * @param nodeToDelete <code>Node</code>.
     * @return Not deleted neighbors (an <code>ArrayList</code> of 
     *  <code>node</code>s) */
	public List<Node> collectNeighbors(HashMap<Integer, Node> eliminated, 
		    Node nodeToDelete) {
    	List<Node> neighbors = nodeToDelete.getNeighbors();
    	eliminated.put(nodeToDelete.hashCode(), nodeToDelete);//Delete this node
    	List<Node> notDeletedNeighbors = new ArrayList<Node>();
    	for (Node node:neighbors) { // Filter the collected nodes
    		if (!eliminated.containsValue(node)) {
    			notDeletedNeighbors.add(node);
    		}
    	}
		return notDeletedNeighbors;
	}
	
	/** Creates undirected links between each pair of nodes in the collection 
	 *  received if them does not exists.
	 * @param nodes <code>Collection</code> of <code>? extends Node</code>s. */
	public void createClique(Collection<? extends Node> nodes) {
    	for (Node node1 : nodes) {
        	for (Node node2 : nodes) {
        		if ((node1 != node2) && (!node1.isSibling(node2))) {
        			try { // Adds an undirected link
            			new Link(node1, node2, false); 
        			} catch (Exception exception) {
        				logger.fatal(exception);
        			}
        		}
        	}        		
    	}		
	}

	/** @return An ordered list of nodes in an <code>ArrayList</code>  */
	private List<ProbNode> getDeletionSequence() {
	    List<ProbNode> nodesUndirected = markovNet.getProbNodes();
	    int numNodes = nodesUndirected.size();
	    int[] ordering = new int[numNodes];
	    List<ProbNode> deletionSequence = new ArrayList<ProbNode>(numNodes);
	    
	    List<Variable> variablesUndirected = new ArrayList<Variable>(numNodes);
	    for (ProbNode node : nodesUndirected) {
	        variablesUndirected.add(node.getVariable());
	    }
	    
	    // Builds the deletion sequence
	    for (int i = 0; i < numNodes; i++) {
	    	ProbNode toDeleteNode = 
	    		markovNet.getProbNode(getVariableToDelete());
	    	
	        // Store the node
	        ordering[i] = variablesUndirected.indexOf(
	            toDeleteNode.getVariable());
	        
	        // Create a clique with the siblings of this node
	        List<Node> siblings = toDeleteNode.getNode().getSiblings();
	        createClique(siblings);
	        
	        // Eliminate the node and its links
			markovNet.getGraph().removeLinks(toDeleteNode.getNode());
	    }
	    markovNet = UtilMarkovNet.getMarkovNet(probNet);
	    
	    // Builds the pointers array to the deletion sequence from the ordering
	    nodesUndirected = markovNet.getProbNodes();
	    for (int i = 0; i < numNodes; i++) {
	        deletionSequence.add(nodesUndirected.get(ordering[i]));
	    }
	    return deletionSequence;
	}

	/** Calculates the heuristic metric <i>S(i)</i>: size of the clique created
	 *   by deleting the <i>i</i> node
	 * @return The heuristic metric <i>S(i)</i>. <code>long</code>.
	 * @param toDeleteNode. <code>ProbNode</code> */
	private long createdCliqueSize(ProbNode toDeleteNode) {
		List<Node> neighbors = toDeleteNode.getNode().getNeighbors();
		return cliqueSize(neighbors);
	}
	
	/** @param nodes is a generic <code>Collection</code> 
	 * of <code>Node</code>s.
	 * @return A <code>long</code> integer. Clique size = the product of the 
	 * cardinalities of the variables stored in the nodes. */
	public static long cliqueSize(Collection<Node> nodes) {
		int result = 1;
		for (Node node : nodes) {
			ProbNode probNode = (ProbNode)node.getObject();
			result *= probNode.getVariable().getNumStates();
		}
		return result;
	}
	
	public void undoEditHappened(UndoableEditEvent event) {
	}
	
	public String toString() {
		return nodesToEliminate.toString();
	}
	
	/** Calculates the heuristic metric <i>C(i)</i>: sum of clique sizes in the 
	 *   subgraph of <i>X<sub>i</sub></i> and its adjacent nodes.
	 * @param center. <code>ProbNode</code> */
	private long sumCliqueSizes(ProbNode centerProbNode) {
		// Initialize
		long sumCliqueSizes = 0;
		HashMap<Variable, Set<Variable>> subGraph = getSubGraph(centerProbNode);
		Stack<HashSet<Variable>> expandableCliques = 
				getInitialExpandableCliques(centerProbNode.getVariable(), subGraph);
		HashSet<HashSet<Variable>> maximalCliques = new HashSet<HashSet<Variable>>(); 
		
		while (!expandableCliques.isEmpty()) {
			HashSet<Variable> clique = expandableCliques.pop();
			Set<Variable> notInCliqueVariables = new HashSet<Variable>(subGraph.keySet());
			notInCliqueVariables.removeAll(clique);
			boolean maximalClique = true;
			for (Variable notInCliqueVariable : notInCliqueVariables) {
				boolean isNeighbor = isNeighbor(notInCliqueVariable, clique, subGraph);
				maximalClique &= !isNeighbor;
				if (isNeighbor) {
					HashSet<Variable> newClique = new HashSet<Variable>(clique);
					newClique.add(notInCliqueVariable);
					expandableCliques = addExpandableClique(expandableCliques, newClique);
				}
			}
			if (maximalClique) {
				maximalCliques.add(clique);
			}
		}

		for (HashSet<Variable> clique : maximalCliques) {
			sumCliqueSizes += cliqueVariablesSize(clique);
		}
		return sumCliqueSizes;
	}

	/** Add <code>newClique</code> to <code>expandableCliques</code>, 
	 * removing from <code>expandableCliques</code> the cliques whose variables are included in <code>newClique</code>.
	 * @param expandableCliques. <code>Stack<code> of <code>HashSet</code> of <code>Variable</code>
	 * @param newClique. <code>HashSet</code> of <code>Variable</code> */
	private Stack<HashSet<Variable>> addExpandableClique(Stack<HashSet<Variable>> expandableCliques,
			HashSet<Variable> newClique) {
		Stack<HashSet<Variable>> newExpandableCliques = new Stack<HashSet<Variable>>();
		for (HashSet<Variable> clique : expandableCliques) {
			if (!newClique.containsAll(clique)) {
				newExpandableCliques.add(clique);
			}
		}
		newExpandableCliques.add(newClique);
		return newExpandableCliques;
	}

	/** Look if a variable is neighbor of all the nodes in the clique.
	 * @param variable. <code>Variable</code>
	 * @param clique. <code>HashSet</code> of <code>Variable</code>
	 * @param subGraph. Contains the neighbors of each node. 
	 * @return is neighbor. <code>boolean</code>.	 */
	private boolean isNeighbor(Variable variable,
			HashSet<Variable> clique, HashMap<Variable, Set<Variable>> subGraph) {
		boolean isNeighborg = true;
		for (Iterator<Variable> i = clique.iterator(); isNeighborg && i.hasNext(); ) {
			Variable cliqueVariable = i.next();
			isNeighborg &= subGraph.get(cliqueVariable).contains(variable);
		}
		return isNeighborg;
	}

	/** Create a structure that contains the neighborgs of each variable in a subgraph.
	 * @param centerProbNode. <code>ProbNode</code>
	 * @return <code>HashMap</code> with key = <code>Variable</code> 
	 *  of <code>Set</code> of <code>Variable</code> */
	private HashMap<Variable, Set<Variable>> getSubGraph(ProbNode centerProbNode) {
		Node centerNode = centerProbNode.getNode();
		Set<Node> subGraphNodes = new HashSet<Node>(centerNode.getNeighbors());
		subGraphNodes.add(centerNode);
		HashMap<Variable, Set<Variable>> subGraph = new HashMap<Variable, Set<Variable>>();
		for (Node subGraphNode : subGraphNodes) {
			// A intersection B = B - (B - A)
			HashSet<Node> neighborsMinusSubGraphNeighbors = 
					new HashSet<Node>(subGraphNode.getNeighbors());
			neighborsMinusSubGraphNeighbors.removeAll(subGraphNodes);
			HashSet<Node> intersectionNeighborsNodes = 
					new HashSet<Node>(subGraphNode.getNeighbors());
			intersectionNeighborsNodes.removeAll(neighborsMinusSubGraphNeighbors);
			// Get variables from intersection
			HashSet<Variable> intersectionVariableNeighbors = new HashSet<Variable>();
			for (Node intersectionNeighborNode : intersectionNeighborsNodes) {
				intersectionVariableNeighbors.add(
						((ProbNode)intersectionNeighborNode.getObject()).getVariable());
			}
			Variable variable = ((ProbNode)subGraphNode.getObject()).getVariable();
			subGraph.put(variable, intersectionVariableNeighbors);
		}
		return subGraph;
	}

	/** Each initial expandable clique have two variables: the center variable and each neighbor of that variable.
	 * @param variable. <code>Variable</code>
	 * @param subGraph
	 * @return A collection (Stack) of cliques (Sets). <code>Stack<code> of <code>HashSet</code> of <code>Variable</code> */
	private Stack<HashSet<Variable>> getInitialExpandableCliques(Variable variable,
			HashMap<Variable, Set<Variable>> subGraph) {
		Stack<HashSet<Variable>> expandableCliques = new Stack<HashSet<Variable>>();
		Set<Variable> neighborsCenter = subGraph.get(variable);
		for (Variable neighborVariable : neighborsCenter) {
			HashSet<Variable> expandableClique = new HashSet<Variable>();
			expandableClique.add(variable);
			expandableClique.add(neighborVariable);
			expandableCliques.add(expandableClique);
		}
		return expandableCliques;
	}

	/**
	 * @param clique. <code>Collection</code> of <code>Variable</code>s
	 * @return <code>long</code> 
	 */
	private long cliqueVariablesSize(Collection<Variable> clique) {
		long cliqueSize = 1;
		for (Variable variable : clique) {
			cliqueSize *= variable.getNumStates();
		}
		return cliqueSize;
	}

}
