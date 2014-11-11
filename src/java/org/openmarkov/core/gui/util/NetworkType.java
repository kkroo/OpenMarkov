/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.util;


import java.util.HashSet;
import org.openmarkov.core.model.network.NodeType;



/**
 * This enum class encapsulates the types of a network.
 * 
 * @author jmendoza
 */
public enum NetworkType {
	/**
	 * Bayesian net.
	 */
	BAYESIAN_NET(0),

	/**
	 * Influence diagram.
	 */
	INFLUENCE_DIAGRAM(1),

	/**
	 * Markov net.
	 */
	MARKOV_NET(2),

	/**
	 * Ghain graph.
	 */
	CHAIN_GRAPH(3),
	
	/**
	 * Simple Markov Model.
	 */
	SIMPLE_MARKOV_MODEL(4),
	
	/**
	 * Simple Markov Model.
	 */
	MARKOV_DECISION_PROCESS(5),
	
	/**
	 * Simple Markov Model.
	 */
	POMDP(6),
	
	
	/****
	 * Decision Analysis Network
	 */
	
	DAN(7),
	
	/****
	 * LIMID
	 */
	
	LIMID(8),
	/****
	 * 
	 * Dynamic Bayesian Network
	 * */
	
	DYN_BAYESIAN_NET(9),
	
	/****
	 * 
	 * Dynamic LIMID
	 * 
	 */
	DYN_LIMID(10),
	
	/****
	 * 
	 * Dec-pomdp
	 * 
	 */
	DEC_POMDP(11),
	
	/****
	 * 
	 * OOPN
	 * 
	 */
	OOPN(12),
	
    /****
     * 
     * TUNING
     * 
     */
    TUNING(13);

	
	

	/**
	 * Types of node that can be created into the network.
	 */
	private HashSet<NodeType> nodeTypes = new HashSet<NodeType>();

	/**
	 * Constructor that saves the information about the nodes that can be
	 * created into the network.
	 * 
	 * @param type
	 *            new type of network.
	 * @throws IllegalArgumentException
	 *             if the type is not valid.
	 */
	private NetworkType(int type) throws IllegalArgumentException {

		switch (type) {
		case 0: {
			nodeTypes.add(NodeType.CHANCE);
			break;
		}
		case 4:
		case 5:
		case 6:
		case 7:
		case 1: {
			nodeTypes.add(NodeType.CHANCE);
			nodeTypes.add(NodeType.DECISION);
			nodeTypes.add(NodeType.UTILITY);
			break;
		}
		case 2:
		case 3:
		case 8:
		case 9:
		case 10:
        case 11:
        case 12:
		case 13:{
			break;
		}
		default: {
			throw new IllegalArgumentException();
		}
		}
	}

	/**
	 * Returns types of node that can be created into this type of network.
	 * 
	 * @return a list of types of node that can be created into this type of
	 *         network.
	 */
	public HashSet<NodeType> getNodeTypes() {

		return new HashSet<NodeType>(nodeTypes);
	}
}
