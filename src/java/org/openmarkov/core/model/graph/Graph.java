/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;


/** This class implements the minimal set of methods for creating
 * a graph and inserting nodes and links.
 * <p>áñ
 * Links are represented implicitly by the lists of parents, children and 
 * siblings of each node. Links can be explicitly represented as objects of 
 * class <code>LabelledLink</code>.
 * Links are implicit until the method <code>makeLinksExplicit</code> is 
 * invoked.
 * Explicit links do not substitute implicit links. In fact, an explicit link 
 * implies the existence of an implicit link.
 * @author manuel
 * @author fjdiez
 * @since OpenMarkov 1.0
 * @version 1.0
 * @see openmarkov.graphs.Node
 * @see openmarkov.graphs.Link
 * @invariant Two different nodes can not represent the same object */
public class Graph {

	// Attributes
	private boolean explicitLinks = false;

	private List<Node> nodes;
	
	private Logger logger;

	// Constructor
	public Graph() {
		this.nodes = new ArrayList<Node>();
		this.logger = Logger.getLogger(Graph.class);
	}

	// Methods
	/** @return explicitLinks <code>boolean</code>. */
	public boolean useExplicitLinks() {
		return explicitLinks;
	}

	/** @return Number of nodes in the graph
	 * @consultation */
	public int getNumNodes() {
		return getNodes().size();
	}

	/** Inserts a link between <code>node1</code> and <code>node2</code>. 
	 * @argCondition <code>node1</code> and <code>node2</code> belongs to this 
	 *  <code>graph</code> 
	 * @param node1. <code>Node</code>
	 * @param node2. <code>Node</code>
	 * @param directed. <code>boolean</code> */
	public void addLink(Node node1, Node node2, boolean directed) {
		if (explicitLinks) {
			new Link(node1, node2, directed);
		} else {
			uf_addImplicitLink(node1, node2, directed);
		}
	}

	/** Removes a link between two nodes.
	 * @param node1 <code>Node</code>
	 * @param node2 <code>Node</code>
	 * @param directed <code>boolean</code> */
	public void removeLink(Node node1, Node node2, boolean directed) {
		if (explicitLinks) { // get the explicit link and remove it
			Link link = getLink(node1, node2, directed);
			if (link != null) {
				removeLink(link); // remove explicit and implicit link
			}
		} else { // remove the implicit link
			node1.getGraph().uf_removeImplicitLink(node1, node2, directed);
		}
	}

	/** Removes an explicit link.
	 * @precondition Links must be explicit.
	 * @param link <code>Link</code>. */
	public void removeLink(Link link) {
		Node node1 = link.getNode1();
		Node node2 = link.getNode2();
		boolean directed = link.isDirected();

		// remove the explicit link
		node1.uf_removeLink(link);
		node2.uf_removeLink(link);

		uf_removeImplicitLink(node1, node2, directed);
	}

	/** @param node1 <code>Node</code>
	 * @param node2 <code>Node</code>
	 * @param directed <code>boolean</code>
	 * @return The link between node1 and node2, if it exists, otherwise 
	 *         returns <code>null</code>
	 * @consultation */
	public Link getLink(Node node1, Node node2, boolean directed) {
		List<Link> linksNode1 = node1.getLinks();
		for (Link link : linksNode1) {
			if (directed) {
				if (link.isDirected() && link.getNode2() == node2) {
					return link;
				}
			} else {
				if (!link.isDirected() && link.contains(node2)) {
					return link;
				}
			}
		}
		return null;
	}

	/** Creates the explicit links (based on the implicit links).<p> When
	 *  <code>createLabelledLinks = true</code> create explicit links with label
	 *  = <code>null</code>. Otherwise, create unlabeled explicit links.
	 * @param createLabelledLinks. <code>boolean</code> */
	public void makeLinksExplicit(boolean createLabelledLinks) {
		if (!explicitLinks) {
			for (Node node1 : nodes) {
				List<Node> children = node1.getChildren();
				for (Node node2 : children) {
					try {
						if (createLabelledLinks) {
							new LabelledLink(node1, node2, true, null);
						} else {
							new Link(node1, node2, true);
						}
					} catch (Exception exception) { // Unreachable
						logger.fatal ("Unable to create an explicit link", exception);
					}
				}
				List<Node> siblings = node1.getSiblings();
				int auxNode1Index = nodes.indexOf(node1);
				for (Node node2 : siblings) {
					if (auxNode1Index > nodes.indexOf(node2)) {
						try {
							if (createLabelledLinks) {
								new LabelledLink(node1, node2, false, null);
							} else {
								new Link(node1, node2, false);
							}
						} catch (Exception exception) { // Unreachable
							logger.fatal ("Unable to create an explicit link", exception);
						}
					}
				}
			}
			this.explicitLinks = true;
		}
	}

	/** Removes implicit and explicit links to <code>node</code> from the 
	 *  neighbors of <code>node</code>.
	 * @param node <code>Node</code> */
	public void removeLinks(Node node) {

		List<Node> neighbors = node.getNeighbors();
		for (Node auxNode : neighbors) {
			if (auxNode.isChild(node)) {
				auxNode.uf_removeChild(node);
			}
			if (auxNode.isParent(node)) {
				auxNode.uf_removeParent(node);
			}
			if (auxNode.isSibling(node)) {
				auxNode.uf_removeSibling(node);
			}
		}
		if (explicitLinks) {
			List<Link> linksNode = node.getLinks();
			for (Link link : linksNode) {
				removeLink(link);
			}
		}

	}

	/** Create nodes and its links and associate objects to them. 
	 *  Do not copy objects.
	 * @return <code>Graph</code>. */
	public Graph copy() {
		Graph copied = new Graph();
		ArrayList<Node> nodesCopied = new ArrayList<Node>();

		// Create structure for fast nodes localization
		int numNodes = nodes.size();
		HashMap<Node, Integer> nodesPosition = new HashMap<Node, Integer>(
				numNodes * 2);
		int i = 0;
		for (Node node : nodes) {
			nodesPosition.put(node, i++);
			nodesCopied.add(new Node(copied, node.getObject())); // Insert nodes
		}

		if (!explicitLinks) { // Copy implicit links
			for (Node node : nodes) {
				List<Node> children = node.getChildren(); // Directed links
				i = nodesPosition.get(node);
				Node copied_i = nodesCopied.get(i);
				for (Node child : children) {
					int j = nodesPosition.get(child);
					Node copied_j = nodesCopied.get(j);
					copied.addLink(copied_i, copied_j, true);
				}
				List<Node> siblings = node.getSiblings();//Undirected links
				for (Node sibling : siblings) {
					int j = nodesPosition.get(sibling);
					Node copied_j = nodesCopied.get(j);
					if (!copied_i.isSibling(copied_j)) {//Do not add links twice
						copied.addLink(copied_i, copied_j, false);
					}
				}
			}
		} else { // Copy explicit links
			for (Node node : nodes) {
				List<Link> links = node.getLinks();
				for (Link link : links) {
					Node node1 = link.getNode1();
					Node node2 = link.getNode2();
					// Gets corresponding nodes of copied graph
					Node node1c = nodesCopied.get(nodesPosition.get(node1));
					Node node2c = nodesCopied.get(nodesPosition.get(node2));
					boolean directed = link.isDirected();
					if ((node1 == node) // do not add a link twice 
							&& (link.getClass() == LabelledLink.class)) {
						Object label = ((LabelledLink) link).getLabel();
						new LabelledLink(node1c, node2c, directed, label);
					} else {
						Link newLink = new Link(node1c, node2c, directed);
						newLink.setRestrictionsPotential (link.getRestrictionsPotential ());
                        newLink.setRevealingStates (link.getRevealingStates ());
					}
				}
			}
		}
		return copied;
	}

	/** @return A clone of the list of nodes (<code>List</code> of 
	 * <code>Node</code>). */
	public List<Node> getNodes() {
		return new ArrayList<Node>(nodes);
	}

	/** @return The <code>Graph</code> explicit links. */
	public List<Link> getLinks() {
		if (!explicitLinks) {
			makeLinksExplicit(false);
		}
		List<Link> links = new ArrayList<Link>();
		List<Link> auxLinks; // the links of each node
		for (Node node : nodes) {
			auxLinks = node.getLinks();
			for (Link link : auxLinks) {
				if (link.getNode1() == node) {
					links.add(link);
				}
			}
		}
		return links;
	}

	/** @param node <code>Node</code>
	 * @param nodeList <code>Collection</code> of <code>Node</code>
	 * @return <code>true</code> if <code>node</code> has at least a neighbor 
	 * other than those in <code>nodeList</code> */
	public boolean hasNeighborsOutside(Node node,
			Collection<? extends Node> nodeList) {
		boolean hasNeighborsOutside = false;
		boolean neighborIsInList; // aux for the for-loop
		for (Node neighbor : node.getNeighbors()) {
			neighborIsInList = false;
			for (Node cliqueNode : nodeList) {
				if (neighbor == cliqueNode) {
					neighborIsInList = true;
					break;
				}
			}
			if (!neighborIsInList) {
				hasNeighborsOutside = true;
				break;
			}
		}
		return hasNeighborsOutside;
	}

	/** @return <code>true</code> if it exists a path between node1 and node2
	 * with a criterion to go from a node to another.
	 * @precondition <code>node1</code> and <code>node2</code> belongs to this
	 * graph. Otherwise this method always returns <code>false</code>.
	 * @param node1 <code>Node</code>. 
	 * @param node2 <code>Node</code>. 
	 * @param directed <code>boolean</code>. If this parameter is true, this 
	 * method returns <code>true</code> only if there is a directed path; 
	 * otherwise, this method returns <code>true</code> if there is any path.*/
	public boolean existsPath(Node node1, Node node2, boolean directed) {
		if ((node1 == null) || (node2 == null)) {
			return false;
		}
		if (node1 == node2) {
			return true;
		}
		List<Node> nodeList = getNodes();
		int numNodes = nodeList.size();
		boolean[] markedNodes = new boolean[numNodes];
		Stack<Node> nodesToExpand = new Stack<Node>();

		for (int i = 0; i < numNodes; i++) {
			markedNodes[i] = false;
		}

		// Mark node1 and put it in the list of nodes to be expanded
		nodesToExpand.push(node1);
		markedNodes[nodeList.indexOf(node1)] = true;

		List<Node> nodes;
		List<Node> neighbors = new ArrayList<Node>();
		while (!nodesToExpand.empty()) {
			Node expandableNode = nodesToExpand.pop(); // the top of the stack
			nodes = expandableNode.getChildren();
			for (Node nodeChildren : nodes) {
				neighbors.add(nodeChildren);
			}
			if (!directed) {
				nodes = expandableNode.getParents();
				for (Node nodeParent : nodes) {
					neighbors.add(nodeParent);
				}
				nodes = expandableNode.getSiblings();
				for (Node nodeSibling : nodes) {
					neighbors.add(nodeSibling);
				}
			}
			if (neighbors.indexOf(node2) != -1) {
				return true; // node2 is in a path from node1
			}

			for (Node neighborNode : neighbors) {
				if (!markedNodes[nodeList.indexOf(neighborNode)]) {
					nodesToExpand.push(neighborNode);
					markedNodes[nodeList.indexOf(neighborNode)] = true;
				}
			}
		}

		return false;
	}

	/** Adds an undirected link between each pair of nodes in
	 *    <code>nodeList</code> if it did not exist.
	 * @param nodeList <code>ArrayList</code> of <code>? extends Node</code>.
	 * @precondition All nodes in <code>nodeList</code> belongs to
	 *   <code>this</code>. */
	public void marry(Collection<Node> nodeList) {
		int size = nodeList.size();
		Node[] nodes = nodeList.toArray(new Node[size]);
		for (int i = 0; i < size - 1; i++) {
			Node node_i = nodes[i];
			for (int j = i + 1; j < size; j++) {
				Node node_j = nodes[j];
				if (!(node_i.isSibling(node_j))) {
					addLink(node_i, node_j, false);
				}
			}
		}
	}

	/** @param node <code>Node</code> */
	public void removeNode(Node node) {
		node.uf_removeAllLinks();
		nodes.remove(node);
	}

	/** Adds an implicit link by setting cross references between the two nodes.
	 * @param link <code>Link</code>. */
	void uf_addImplicitLink(Link link) {
		uf_addImplicitLink(link.getNode1(), link.getNode2(), link.isDirected());
	}

	/** Adds an implicit link by setting cross references between the two nodes.
	 * @param node1 <code>Node</code>
	 * @param node2 <code>Node</code>
	 * @param directed <code>boolean</code>
	 * @argCondition Both nodes must belong to the same graph. */
	void uf_addImplicitLink(Node node1, Node node2, boolean directed) {
		if (directed) {
			if (!node1.isChild(node2)) {
				node1.uf_addChild(node2);
			}
			if (!node2.isParent(node1)) {
				node2.uf_addParent(node1);
			}
		} else {
			if (!node1.isSibling(node2)) {
				node1.uf_addSibling(node2);
			}
			if (!node2.isSibling(node1)) {
				node2.uf_addSibling(node1);
			}
		}
	}

	/** Removes an implicit link by deleting cross references between the two
	 *  nodes.
	 * @param link <code>Link</code> */
	void uf_removeImplicitLink(Link link) {
		uf_removeImplicitLink(link.getNode1(), link.getNode2(), link
				.isDirected());
	}

	/** Removes an implicit link by deleting cross references between the two 
	 *  nodes.
	 * @param node1 <code>Node</code>
	 * @param node2 <code>Node</code>
	 * @param directed <code>boolean</code>
	 * @argCondition The two nodes must belong to the same graph */
	void uf_removeImplicitLink(Node node1, Node node2, boolean directed) {
		if (directed) {
			node1.uf_removeChild(node2);
			node2.uf_removeParent(node1);
		} else {
			node1.uf_removeSibling(node2);
			node2.uf_removeSibling(node1);
		}
	}

	/** @param node <code>Node</code> */
	public void uf_addNode(Node node) {
		nodes.add(node);
	}

	/** @return A <code>String</code> with:
	 * <ol>
	 * <li>Number of nodes.
	 * <li>List of nodes. For each node calls <code>node.toString()</code>.
	 * </ol> */
	public String toString() {
		StringBuffer buffer = 
			new StringBuffer("Number of nodes: " + nodes.size() + "\n");
		for (Node node : nodes) {
			buffer.append(node.toString() + "\n");
		}
		return buffer.toString();
	}

}
