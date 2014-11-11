package org.openmarkov.core.oopn;

import org.openmarkov.core.model.network.ProbNode;

public class NodeReferenceLink extends ReferenceLink{

	private ProbNode sourceNode;
	private ProbNode destinationNode;

	public NodeReferenceLink(ProbNode sourceNode, ProbNode destinationNode) {
		this.sourceNode = sourceNode;
		this.destinationNode = destinationNode;
	}

	public ProbNode getSourceNode() {
		return sourceNode;
	}

	public ProbNode getDestinationNode() {
		return destinationNode;
	}

}
