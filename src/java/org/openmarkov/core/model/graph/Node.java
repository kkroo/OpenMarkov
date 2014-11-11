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
import java.util.HashSet;
import java.util.List;

import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;


/** @author manuel
 * @author fjdiez
 * @see openmarkov.graphs.Link
 * @see openmarkov.graphs.Graph
 * @version 1.0
 * @since OpenMarkov 1.0
 * The general consistence conditions for <code>Node</code> class are:
 * <p><b>Links</b>:
 * All nodes defined in <code>parents</code> are also defined as links in the
 * <code>ArrayList links</code>, they are directed and the child is this node.
 * All nodes defined in <code>children</code> are also defined as links in the
 * <code>ArrayList links</code>, they are directed and the father is this node.
 * All nodes defined in <code>siblings</code> are also defined as links in the
 * <code>ArrayList links</code>, they are undirected and one of the members
 * is this node. */
public class Node {

	// Attributes
	/** The graph this node belongs to
	 * @frozen */
	protected Graph graph;

	/** An object represented by this node 
	 * @frozen */
	protected Object object;

	/** The parents of this node (directed links)*/
	protected List<Node> parents = new ArrayList<Node>();

	/** The children of this node (directed links)*/
	protected List<Node> children = new ArrayList<Node>();

	/**  The siblings of this node (undirected links)*/
	protected List<Node> siblings = new ArrayList<Node>();

	/** Explicit links defined to/from this node */
	protected List<Link> links = new ArrayList<Link>();
	
	/** The X coordinate of node */
	private double coordinateX = 100;
	
	/** The Y coordinate of node */
	private double coordinateY = 100;
	
	// Constructor
	/** @param graph <code>Graph</code>.
	 * @param object <code>Object</code> associated to this node. */
	public Node(Graph graph, Object object) {
		this.graph = graph;
		this.object = object;
		graph.uf_addNode(this);
	}

	// Methods
	/** @return <code>true</code> if node is child of <code>this</code> node. 
	 * @param node <code>Node</code>.
	 * @consultation */
	public boolean isChild(Node node) {
		return children.contains(node);
	}

	/** @return True if <code>node</code> is parent of <code>this</code> node 
	 * @param node <code>Node</code>.
	 * @consultation */
	public boolean isParent(Node node) {
		return parents.contains(node);
	}

	/** @return <code>true</code> if node is sibling of <code>this</code> node 
	 * @param node <code>Node</code>.
	 * @consultation */
	public boolean isSibling(Node node) {
		return siblings.contains(node);
	}

	/** @return <code>true</code> if <code>node</code> is child, parent or
	 * sibling of <code>this</code> node
	 * @param node <code>Node</code>
	 * @consultation */
	public boolean isNeighbor(Node node) {
		return (isChild(node) || isParent(node) || isSibling(node));
	}

	/** @return The <code>object</code> associated to this node
	 * @consultation */
	public Object getObject() {
		return object;
	}

	/**
	 * @param newObject
	 */
	public void setObject (Object newObject) {
		object= newObject;
	}
	
	/** @return Set of children, parents and siblings (excluding itself),
	 *  without duplications (to ensure this we copy all neighbors in a 
	 *  <code>HashSet</code> and then build and <code>ArrayList</code> of 
	 *  <code>Node</code>).
	 * @consultation */
	public List<Node> getNeighbors() {
		HashSet<Node> hashSetNodes = new HashSet<Node>(siblings);
		hashSetNodes.addAll(children);
		hashSetNodes.addAll(parents);
		ArrayList<Node> neighbors = new ArrayList<Node>(hashSetNodes);
		return neighbors;
	}

	/** @return graph <code>Graph</code>
	 * @consultation */
	public Graph getGraph() {
		return graph;
	}

	/** @return The explicit links for this node 
	 * 		(<code>ArrayList</code> of <code>Link</code>s).
	 * @precondition The graph has explicit links. Otherwise returns an empty
	 *  <code>ArrayList</code> and the node can have implicit links not 
	 *  reflected here
	 * @consultation */
	public List<Link> getLinks() {
		return new ArrayList<Link>(links);
	}

	/** @return <code>ArrayList</code> of <code>Node</code>s with the children
	 * of this node
	 * @consultation */
	public List<Node> getChildren() {
		return new ArrayList<Node>(children);
	}

	/** @return <code>ArrayList</code> of <code>Node</code>s with the parents of
	 * this node
	 * @consultation */
	public List<Node> getParents() {
		return new ArrayList<Node>(parents);
	}

	
	/** @return <code>List</code> of <code>Node</code>s with the siblings
	 * of this node
	 * @consultation */
	public List<Node> getSiblings() {
		return new ArrayList<Node>(siblings);
	}

	/** @consultation */
	public int getNumParents() {
		return parents.size();
	}

	/** @consultation */
	public int getNumChildren() {
		return children.size();
	}

	/** @consultation */
	public int getNumSiblings() {
		return siblings.size();
	}

	/** @consultation */
	public int getNumLinks() {
		return links.size();
	}

	/** Remove all the neighbor links.
	 * This method should be called only by 
	 * <code>openmarkov.Graph#removeNode</code> */
	void uf_removeAllLinks() {
		if (graph.useExplicitLinks()) {
			List<Link> listLinks = getLinks();
			if (listLinks.size() != 0) {
				for (Link link : listLinks) {
					graph.removeLink(link);
				}
			}
		} else {
			for (Node node : parents) {
				node.uf_removeChild(this);
			}
			parents.clear();
			for (Node node : children) {
				node.uf_removeParent(this);
			}
			children.clear();
			for (Node node : siblings) {
				node.uf_removeSibling(this);
			}
			siblings.clear();
		}
	}

	/** @prerequisite The graph has explicit links
	 * @return A list of explicit links directed to this node
	 * @consultation */
	List<Link> parentLinks() {
		List<Link> parentLinks = new ArrayList<Link>();

		for (Link link : links) {
			if (link.isDirected()) {
				if (this == link.getNode2()) {
					parentLinks.add(link);
				}
			}
		}
		return parentLinks;
	}

	/** @prerequisite The graph has explicit links 
	 * @return A vector with a list of undirected links associated with this 
	 * node
	 * @consultation */
	List<Link> siblingLinks() {
		List<Link> siblingLinks = new ArrayList<Link>();

		for (Link link : links) {
			if (!link.isDirected()) {
				if (this == link.getNode1() || this == link.getNode2()) {
					siblingLinks.add(link);
				}
			}
		}

		return siblingLinks;
	}

	/** @prerequisite The graph has explicit links.
	 * @return <code>ArrayList</code> of directed <code>Link</code>s with this
	 * node as father.
	 * @consultation */
	List<Link> childLinks() {
		List<Link> childLinks = new ArrayList<Link>();

		for (Link auxLink : links) {
			if (auxLink.isDirected()) {
				if (this == auxLink.getNode1()) {
					childLinks.add(auxLink);
				}
			}
		}
		return childLinks;
	}

	/** Adds an explicit link to the list of neighbor links of this node
	 * This method should be called only by the <code>addLink</code>
	 * method in the class <code>Graph</code>.
	 * @param link <code>Link</code> */
	void addLink(Link link) {
		links.add(link);
	}

	/** This method should be called only by the <code>addLink</code>
	 * method in the class <code>Graph</code>.
	 * @param node <code>Node</code>. */
	void uf_addParent(Node node) {
		parents.add(node);
	}

	/** This method should be called only by the <code>removeLink</code>
	 * method in the class Graph
	 * @param node <code>Node</code>. */
	void uf_removeParent(Node node) {
		parents.remove(node);
	}

	/** This method should be called only by the <code>addLink</code>
	 * method in the class Graph
	 * @param node <code>Node</code>. */
	void uf_addChild(Node node) {
		children.add(node);
	}

	/** This method should be called only by the <code>removeLink</code>
	 * method in the class Graph
	 * @param node <code>Node</code>. */
	void uf_removeChild(Node node) {
		children.remove(node);
	}

	/** This method should be called only by the <code>addLink</code>
	 * method in the class Graph
	 * @param node <code>Node</code>. */
	void uf_addSibling(Node node) {
		siblings.add(node);
	}

	/** This method should be called only by the <code>removeLink</code>
	 * method in the class Graph
	 * @param node <code>Node</code>. */
	void uf_removeSibling(Node node) {
		siblings.remove(node);
	}

	/** Adds an explicit link to the list of neighbor links of this node
	 * This method should be called only by the <code>addLink</code>
	 * method in the class <code>Graph</code>.
	 * @param link <code>Link</code>.*/
	void uf_addLink(Link link) {
		links.add(link);
	}

	/** Removes the link from the list of explicit links
	 * This method should be calle only by <code>Graph.removeLink</code>.
	 * @param link <code>Link</code>. */
	void uf_removeLink(Link link) {
		links.remove(link);
	}

	/** @return String */
	// Object in node: X. n1 parents, n2 children, n3 siblings.
	//   parents: [p1, p2,...,pn]
	//   children: [c1, c2,...,cn]
	//   siblings: [s1, s2,...,sn]
	public String toString() {
		int numParents = getNumParents();
		int numChildren = getNumChildren();
		int numSiblings = getNumSiblings();
		int numNeighbors = numParents + numChildren + numSiblings;
		StringBuffer buffer = 
			new StringBuffer("Number of neighbors: " + numNeighbors + "\n");
		if (numParents > 0) {
			if (numParents == 1) {
				buffer.append(numParents + " parent");
			} else {
				buffer.append(numParents + " parents");
			}
			if ((numChildren + numSiblings) > 0) {
				buffer.append(", ");
			}
		}
		if (numChildren > 0) {
			if (numChildren == 1) {
				buffer.append(numChildren + " child");
			} else {
				buffer.append(numChildren + " children");
			}
			if (numSiblings > 0) {
				buffer.append(", ");
			}
		}
		if (numSiblings > 0) {
			if (numSiblings == 1) {
				buffer.append(numSiblings + " sibling");
			} else {
				buffer.append(numSiblings + " siblings");
			}
		}
		buffer.append("\n");
		if (object != null) {
			buffer.append("Object stored: " + 
					object.getClass().getSimpleName());
			if (object.getClass() == ProbNode.class) {
				Variable variable = ((ProbNode)object).getVariable();
				if (variable != null) {
					buffer.append("(" + variable.getName() + ")");
				} else {
					buffer.append(" No variable");
				}
			}
			if (object.getClass() == Variable.class) {
				buffer.append("(" + ((Variable)object).getName() + ")");
			}
		} else {
			buffer.append("No object");
		}
		buffer.append("\n");
		return buffer.toString();
	}

	/**
	 * @param coordinateX the coordinateX to set
	 */
	public void setCoordinateX(double coordinateX) {
		this.coordinateX = coordinateX;
	}

	/**
	 * @return the coordinateX
	 */
	public double getCoordinateX() {
		return coordinateX;
	}

	/**
	 * @param coordinateY the coordinateY to set
	 */
	public void setCoordinateY(double coordinateY) {
		this.coordinateY = coordinateY;
	}

	/**
	 * @return the coordinateY
	 */
	public double getCoordinateY() {
		return coordinateY;
	}
}
