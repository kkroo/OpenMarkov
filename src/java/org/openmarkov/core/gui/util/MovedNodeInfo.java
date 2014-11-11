/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.util;


import java.awt.geom.Point2D;

import org.openmarkov.core.model.network.ProbNode;



/**
 * This class contains the information of a node that has been moved. This class
 * only serves to contain the information for undo and redo.
 * 
 * @author jmendoza
 */
public class MovedNodeInfo {

	/**
	 * Node whose position must be undone and redone.
	 */
	private ProbNode nodeWrapper = null;

	/**
	 * Original position of the node before it has been moved.
	 */
	private Point2D.Double diffPosition = null;

	/**
	 * Constructor. Only saves the information.
	 * 
	 * @param newNodeWrapper
	 *            node moved.
	 * @param newDiffPosition
	 *            difference with the original position.
	 */
	public MovedNodeInfo(ProbNode newNodeWrapper,
							Point2D.Double newDiffPosition) {

		nodeWrapper = newNodeWrapper;
		diffPosition = newDiffPosition;
	}

	/**
	 * Returns the node that has been moved.
	 * 
	 * @return the node that has been moved.
	 */
	public ProbNode getProbNode() {

		return nodeWrapper;
	}

	/**
	 * Returns the difference of position of the node.
	 * 
	 * @return two values that represents the difference of position of the
	 *         node.
	 */
	public Point2D.Double getDiffPosition() {

		return diffPosition;
	}

	/**
	 * Sets the value of the differente of position.
	 * 
	 * @param newDiffPosition
	 *            new difference of position.
	 */
	public void setDiffPosition(Point2D.Double newDiffPosition) {

		diffPosition = newDiffPosition;
	}
}
