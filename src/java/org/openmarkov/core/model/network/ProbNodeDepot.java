/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;

/**
 * @author mluque It is the type of 'nodesHashMaps'. It contains a
 *         <code>LinkedHashMap</code> from <code>NodeType</code> to
 *         <code>NodesHashMapType</code>.
 */
public class ProbNodeDepot {
    /**
     * @author mluque Contains a <code>LinkedHashMap</code> from
     *         <code>Variable</code> to <code>ProbNode</code>.
     */
    private class NodesHashMap {
        LinkedHashMap<Variable, ProbNode> nodesHashMap;

        NodesHashMap() {
            nodesHashMap = new LinkedHashMap<Variable, ProbNode>();
        }

        public ProbNode get(Variable variable) {
            return nodesHashMap.get(variable);
        }

        public void put(Variable variable, ProbNode probNode) {
            nodesHashMap.put(variable, probNode);
        }

        public int size() {
            return nodesHashMap.size();
        }

        public Collection<ProbNode> values() {
            return nodesHashMap.values();
        }

        public void remove(Variable variable) {
            nodesHashMap.remove(variable);
        }
    }

    LinkedHashMap<NodeType, NodesHashMap> nodesHashMaps;

    ProbNodeDepot() {
        nodesHashMaps = new LinkedHashMap<NodeType, NodesHashMap>();
        // create a linkedHashMap for each type of nodes
        for (NodeType type : NodeType.values()) {
            nodesHashMaps.put(type, new NodesHashMap());
        }
    }

    public int getNumNodes() {
        int numNodes = 0;
        for (NodesHashMap hashMap : nodesHashMaps.values()) {
            numNodes = numNodes + hashMap.size();
        }
        return numNodes;
    }

    public int getNumNodes(NodeType nodeType) {
        return nodesHashMaps.get(nodeType).size();
    }

    public List<ProbNode> getProbNodes() {
        List<ProbNode> nodes = new ArrayList<ProbNode>(getNumNodes());
        for (NodesHashMap hashMap : nodesHashMaps.values()) {
            nodes.addAll(hashMap.values());
        }
        return nodes;
    }

    public List<Potential> getPotentialsByType(NodeType nodeType) {
        NodesHashMap nodesType = nodesHashMaps.get(nodeType);
        List<Potential> potentials = new ArrayList<Potential>();
        for (ProbNode node : nodesType.values()) {
            potentials.addAll(node.getPotentials());
        }
        return potentials;
    }

    /**
     * @return All the nodes of certain kind
     * @param nodeType
     * @consultation
     */
    public List<ProbNode> getProbNodes(NodeType nodeType) {
        return new ArrayList<ProbNode>(nodesHashMaps.get(nodeType).values());
    }

    public List<Potential> getPotentialsByRole(PotentialRole role) {
        List<Potential> potentials = new ArrayList<Potential>();
        for (NodesHashMap nodesHashMap : nodesHashMaps.values()) {
            for (ProbNode auxProbNode : nodesHashMap.values()) {
                for (Potential auxPot : auxProbNode.getPotentials()) {
                    if (auxPot.getPotentialRole() == role) {
                        potentials.add(auxPot);
                    }
                }
            }
        }
        return potentials;
    }

    public ProbNode getProbNode(NodeType nodeType, Variable variable) {
        return nodesHashMaps.get(nodeType).get(variable);
    }

    public ProbNode getProbNode(String nameOfVariable) {
        for (NodeType nodeType : NodeType.values()) {
            Collection<ProbNode> probNodes = nodesHashMaps.get(nodeType).values();
            for (ProbNode probNode : probNodes) {
                if (probNode.getVariable().getName().contentEquals(nameOfVariable)) {
                    return probNode;
                }
            }
        }
        return null;
    }

    public ProbNode getProbNode(Node node) {
        for (NodeType nodeType : NodeType.values()) {
            Collection<ProbNode> probNodes = nodesHashMaps.get(nodeType).values();
            for (ProbNode probNode : probNodes) {
                if (probNode.getNode().equals(node)) {
                    return probNode;
                }
            }
        }
        return null;
    }
    
    public ProbNode getProbNode (Variable variable)
    {
        ProbNode probNode = null;
        for (NodesHashMap nodes : nodesHashMaps.values ())
        {
            if ((probNode = nodes.get (variable)) != null)
            {
                break;
            }
        }
        return probNode;
    }    
    
    /**
     * @param nameOfVariable <code>String</code>
     * @param nodeType <code>NodeType</code>
     * @return The node with <code>nameOfVariable</code> and
     *         <code>kindOfNode</code> if exists otherwhise null
     * @throws ProbNodeNotFoundException
     */
    public ProbNode getProbNode (String nameOfVariable, NodeType nodeType)
    {
        for (ProbNode node : nodesHashMaps.get (nodeType).values ())
        {
            if (node.getVariable ().getName ().contentEquals (nameOfVariable))
            {
                return node;
            }
        }
        return null;
    }    

    public void addProbNode(Variable variable, ProbNode probNode) {
        nodesHashMaps.get(probNode.getNodeType()).put(variable, probNode);
    }

    public void removeProbNode(ProbNode probNode) {
        NodeType nodeKindValue = probNode.getNodeType ();
        Variable variable = probNode.getVariable ();
        NodesHashMap nodesMap = nodesHashMaps.get (nodeKindValue);
        nodesMap.remove (variable);
     }

    public int getNumPotentials() {
        int numPotentials = 0;
        for (NodesHashMap linkedHasMap : nodesHashMaps.values ())
        {
            for (ProbNode probNode : linkedHasMap.values ())
            {
                numPotentials += probNode.getNumPotentials ();
            }
        }
        return numPotentials;
    }
    
    
}
