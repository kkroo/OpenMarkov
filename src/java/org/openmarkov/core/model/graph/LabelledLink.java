/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.graph;

/** @author fjdiez
 * @author manuel
 * @version 1.0
 * @since OpenMarkov 1.0
 * @see openmarkov.graphs.Link */
public class LabelledLink extends Link {

	// Attributes
	/** In labelled graphs labels are used to distinguish among links.
	 * In unlabelled graphs, it can be used for other purposes. */
	protected Object label;

	// Constructor
	/** Creates a labelled link and sets the cross references in the nodes.
	 * @param node1 <code>Node</code>
	 * @param node2 <code>Node</code>
	 * @param directed <code>boolean</code>
	 * @param label <code>Object</code> */
	public LabelledLink(Node node1, Node node2, boolean directed, Object label) 
	{
		super(node1, node2, directed);
		this.label = label;
	}

	// Methods
	/** Gets the label value
	 * @consultation  
	 * @return label <code>Object</code> */
	public Object getLabel() {
		return label;
	}

	/** Sets the label value
	 * @param label <code>Object</code> */
	public void setLabel(Object label) {
		this.label = label;
	}

}
