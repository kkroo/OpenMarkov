/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.oopn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;

public class Instance {

    public enum ParameterArity {
        ONE, MANY;

        public static ParameterArity parseArity(String name) {
            ParameterArity arity = null;
            if (name.equals(ONE.toString())) {
                arity = ParameterArity.ONE;
            } else if (name.equals(MANY.toString())) {
                arity = ParameterArity.MANY;
            }
            return arity;
        }
    }

    private String                    name;
    private ProbNet                   classNet;
    private boolean                   isInput;
    private ParameterArity            arity;
    private List<ProbNode>            instanceNodes;
    private HashMap<String, Instance> subInstances;

    /**
     * Constructor
     * 
     * @param name
     * @param classNet
     * @param instanceNodes
     * @param isInput
     */
    public Instance(String name, ProbNet classNet, List<ProbNode> instanceNodes, boolean isInput) {
        super();
        this.name = name;
        this.classNet = classNet;
        this.instanceNodes = instanceNodes;
        this.subInstances = new HashMap<String, Instance>();
        this.isInput = isInput;
        this.arity = ParameterArity.ONE;

        if (classNet instanceof OOPNet) {
            for (String subInstanceName : ((OOPNet) classNet).getInstances().keySet()) {
                Instance originalSubinstance = ((OOPNet) classNet).getInstances().get(subInstanceName);

                ArrayList<ProbNode> subInstanceNodes = new ArrayList<ProbNode>();
                for (ProbNode originalSubinstanceNode : originalSubinstance.getNodes()) {
                    String subinstanceNodeName = name + "." + originalSubinstanceNode.getName();
                    int i = 0;
                    boolean found = false;
                    while (!found && i < instanceNodes.size()) {
                        found = instanceNodes.get(i).getName().equals(subinstanceNodeName);
                        if (!found) {
                            ++i;
                        }
                    }
                    subInstanceNodes.add(instanceNodes.get(i));
                }
                this.subInstances.put(name + "." + subInstanceName, new Instance(name
                        + "."
                        + subInstanceName,
                        originalSubinstance.getClassNet(),
                        subInstanceNodes,
                        originalSubinstance.isInput));
            }
        }
    }

    /**
     * Constructor
     * 
     * @param name
     * @param classNet
     * @param instanceNodes
     */
    public Instance(String name, ProbNet classNet, ArrayList<ProbNode> instanceNodes) {
        this(name, classNet, instanceNodes, false);
    }

    /**
     * @return the isInput
     */
    public boolean isInput() {
        return isInput;
    }

    /**
     * @param isInput
     *            the isInput to set
     */
    public void setInput(boolean isInput) {
        this.isInput = isInput;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the classNet
     */
    public ProbNet getClassNet() {
        return classNet;
    }

    /**
     * @return the instanceNodes
     */
    public List<ProbNode> getNodes() {
        return instanceNodes;
    }

    /**
     * 
     * @return sub instances
     */
    public HashMap<String, Instance> getSubInstances() {
        return subInstances;
    }

    public ParameterArity getArity() {
        return arity;
    }

    public void setArity(ParameterArity arity) {
        this.arity = arity;
    }

}
