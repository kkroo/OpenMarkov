package org.openmarkov.inference.variableElimination.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.AddPotentialEdit;
import org.openmarkov.core.action.CompoundPNEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.action.RemoveNodeEdit;
import org.openmarkov.core.action.RemovePotentialEdit;
import org.openmarkov.core.action.UsesVariable;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.PotentialOperationException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.operation.PotentialOperations;
import org.openmarkov.inference.variableElimination.VariableElimination;
import org.openmarkov.inference.variableElimination.VariableElimination.InferencePurpose;
import org.openmarkov.inference.variableElimination.VariableElimination.InferenceState;

@SuppressWarnings("serial")
public abstract class CRemoveNodeVEEdit extends CompoundPNEdit implements UsesVariable {

    protected Variable         variable;

    protected NodeType         nodeType;

    protected List<Node>       parents;

    protected List<Node>       children;

    protected List<Node>       siblings;

    protected InferenceState   inferenceState;

    protected InferencePurpose inferencePurpose;

    // protected VariableElimination varElimination;

    // protected TablePotential utilityTable;

    /**
     * Policy generated after eliminating a decision variable
     */
    protected TablePotential   policy;

    public TablePotential getPolicy() {
        return policy;
    }

    public void setPolicy(TablePotential policy) {
        this.policy = policy;
    }

    boolean                      isLastVariable;

    private TablePotential       globalUtility;

    private TablePotential       posteriorUtility;

    /**
     * List of constant potentials used in the inference. Probability constant
     * potentials are always removed and not included in this list. They are
     * useful to detect IncompatibleEvidenceException (constant 0.0) Utility
     * constant potential are stored in this list to simplify the management of
     * this kind of potentials instead of storing them in the
     * MarkovDecisionNetwork.
     */
    private List<TablePotential> constantPotentials;

    public CRemoveNodeVEEdit(ProbNet probNet, List<TablePotential> constantPotentials,
            InferencePurpose purpose, Variable variableToDelete,
            VariableElimination varElimination, boolean isLastVariable,
            InferenceState inferenceState) {
        super(probNet);
        this.variable = variableToDelete;
        this.nodeType = probNet.getProbNode(variable).getNodeType();
        this.isLastVariable = isLastVariable;
        this.inferenceState = inferenceState;
        inferencePurpose = purpose;
        this.constantPotentials = constantPotentials;
    }
    
    public TablePotential getPosteriorUtility() {
        return posteriorUtility;
    }

    public void setPosteriorUtility(TablePotential posteriorUtility) {
        this.posteriorUtility = posteriorUtility;
    }

    public TablePotential getGlobalUtility() {
        return globalUtility;
    }

    public void setGlobalUtility(TablePotential globalUtility) {
        this.globalUtility = globalUtility;
    }
    
    public void undo() {
        super.undo();
    }

    @Override
    /** @return variableToDelete <code>Variable</code> */
    public Variable getVariable() {
        return variable;
    }

    @SuppressWarnings("unchecked")
    public void generateEdits() {
        ProbNode probNode = probNet.getProbNode(variable);
        Node node = probNode.getNode();

        // gets neighbors of this node
        parents = node.getParents();
        children = node.getChildren();
        siblings = node.getSiblings();

        List<? extends Potential> probPotentialsVariable = probNet.getProbPotentials(variable);
        List<? extends Potential> utilityPotentialsVariable = probNet.getUtilityPotentials(variable);

        // ... multiply and eliminate the variable
        PotentialsAfterElimination newPotentials = multiplyAndEliminatePotentials(inferencePurpose,
                (List<TablePotential>) probPotentialsVariable,
                (List<TablePotential>) utilityPotentialsVariable);

        List<TablePotential>  potentialsContainingVariable = new ArrayList<TablePotential>((List<TablePotential>) probPotentialsVariable);
        for (Potential auxPot : utilityPotentialsVariable) {
            potentialsContainingVariable.add((TablePotential) auxPot);
        }

        for (Potential potential : potentialsContainingVariable) {
            edits.add(new RemovePotentialEdit(probNet, potential));
        }

        // add a link between the siblings of the removed node
        for (Node node1 : siblings) {
            for (Node node2 : siblings) {
                if ((node1 != node2) && (!node1.isSibling(node2))) {
                    addEdit(new AddLinkEdit(probNet,
                            ((ProbNode) node1.getObject()).getVariable(),
                            ((ProbNode) node2.getObject()).getVariable(),
                            false,
                            false));
                }
            }
        }

        // remove links between probNode and its parents, children and siblings
        for (Node parent : parents) {
            Variable parentVariable = ((ProbNode) parent.getObject()).getVariable();
            addEdit(new RemoveLinkEdit(probNet, parentVariable, variable, true, false));
        }

        for (Node child : children) {
            Variable childVariable = ((ProbNode) child.getObject()).getVariable();
            addEdit(new RemoveLinkEdit(probNet, variable, childVariable, true, false));
        }

        for (Node sibling : siblings) {
            Variable siblingVariable = ((ProbNode) sibling.getObject()).getVariable();
            addEdit(new RemoveLinkEdit(probNet, siblingVariable, variable, false, false));
        }

        // add edit to remove the variable
        addEdit(new RemoveNodeEdit(probNet, variable));

        // TODO Study if Compound edits have to catch the
        // IncompatibleEvidenceException
        try {
            newPotentials.removeConstantPotentials();
        } catch (IncompatibleEvidenceException e) {
            e.printStackTrace();
        }

        boolean addNewPotentials;
        addNewPotentials = false;
        if (inferenceState == InferenceState.PRERESOLUTION) {
            if (!isLastVariable) {
                // add edit to add the new potentials
                addNewPotentials = true;
            } else {
                // globalUtility = newPotentials.utilityPotential;
            }
        } else {// POSTRESOLUTION
            if ((inferencePurpose == InferencePurpose.POSTERIOR_PROB)
                    || (inferencePurpose == InferencePurpose.EXPECTED_UTIL)
                    || (!isLastVariable)) {
                addNewPotentials = true;
            } else {
                posteriorUtility = DiscretePotentialOperations.sum(newPotentials.getUtilityPotentials());
            }
        }

        if (addNewPotentials) {
            // add edit to add the new potentials
            for (Potential newPotential : newPotentials.getListOfProbAndUtilityPotentials()) {
                edits.add(new AddPotentialEdit(probNet, newPotential));
            }
        }

    }

    /**
     * 
     * @param purpose
     * @param probPotentialsVariable
     * @param utilityPotentialsVariable
     * @return
     */
    protected PotentialsAfterElimination multiplyAndEliminatePotentials(InferencePurpose purpose,
            List<TablePotential> probPotentialsVariable,
            List<TablePotential> utilityPotentialsVariable) {
        PotentialsAfterElimination newPotentials = new PotentialsAfterElimination();
        boolean isNullNewMarginalizedProbPotential; 
        boolean utilitiesDependOnVariable = !utilityPotentialsVariable.isEmpty();
        boolean isUnityNewMarginalizedProbPotential = true;
        TablePotential newMarginalizedProbPotential = null;
        TablePotential jointProbability = null;

        if (purpose == InferencePurpose.POSTERIOR_PROB) {
            // Optimize for the case of Bayesian networks
            // ... multiply and eliminate the variable in one step
            try {
                newMarginalizedProbPotential = (TablePotential) PotentialOperations.multiplyAndEliminate(probPotentialsVariable,
                        variable);
            } catch (PotentialOperationException e) {
                e.printStackTrace();
            }
            isNullNewMarginalizedProbPotential = false;
        } else {
            // computes the new probability potential in two steps
            jointProbability = DiscretePotentialOperations.multiply(probPotentialsVariable);

            isNullNewMarginalizedProbPotential = (jointProbability == null);

            if (!isNullNewMarginalizedProbPotential) {
                newMarginalizedProbPotential = marginalizeVariableFromPotential(jointProbability);
            }
        }

        // Add the new probability potential
        if (!isNullNewMarginalizedProbPotential) {
            isUnityNewMarginalizedProbPotential = isUnityProbabilityPotential((TablePotential) newMarginalizedProbPotential);
            if (!isUnityNewMarginalizedProbPotential) {
                newPotentials.setProbabilityPotential((TablePotential) newMarginalizedProbPotential);
            }
        }

        if (utilitiesDependOnVariable) {
        	TablePotential normalizedProbPotential = null;
            if (!isNullNewMarginalizedProbPotential) {
                // Normalize the potential for the computation with utility
                // potential
            	normalizedProbPotential = !isUnityNewMarginalizedProbPotential ? (TablePotential) DiscretePotentialOperations.divide(jointProbability,
                        newMarginalizedProbPotential)
                        : jointProbability;
            }
            List<TablePotential> newUtilityPotentials = marginalizeVariableFromPotentials(normalizedProbPotential, utilityPotentialsVariable);
        	newPotentials.setUtilityPotentials(newUtilityPotentials);
        }
 
        return newPotentials;
    }

    private static boolean isUnityProbabilityPotential(TablePotential pot) {
        return ((pot == null) || ((pot.getVariables().size() == 0) && (pot.values[0] == 1.0)));
    }


    private TablePotential marginalizeVariableFromPotential(TablePotential potential) {
        return marginalizeVariableFromPotentials(potential, new ArrayList<TablePotential>()).get(0);
    }

    protected abstract List<TablePotential> marginalizeVariableFromPotentials(TablePotential probabilityPotential, List<TablePotential> utilityPotentials);

    /**
     * Adds <code>utilityPotentials</code> received.
     * 
     * @param utilityPotentials
     *            <code>List</code> of <code>Potential</code>s.
     * @return utilityPotential <code>Potential</code>.
     */
    protected TablePotential calculateUtilityPotential(List<TablePotential> utilityPotentials) {
        return utilityPotentials.isEmpty()? null : DiscretePotentialOperations.sum(utilityPotentials);
    }
    
    /**
     * @author manolo This class contains the potentials generated by the
     *         elimination of a variable.
     * 
     */
    private class PotentialsAfterElimination {
        private TablePotential probabilityPotential;
        private List<TablePotential> utilityPotentials = new ArrayList<>();

        public TablePotential getProbabilityPotential() {
            return probabilityPotential;
        }

        public void setProbabilityPotential(TablePotential newMarginalizedProbPotential) {
            this.probabilityPotential = newMarginalizedProbPotential;
        }

        public List<TablePotential> getUtilityPotentials() {
            return utilityPotentials;
        }

        public void setUtilityPotentials(List<TablePotential> utilityPotential) {
            this.utilityPotentials = utilityPotential;
        }

        public List<TablePotential> getListOfProbAndUtilityPotentials() {
            List<TablePotential> list = new ArrayList<TablePotential>();
            if (probabilityPotential != null) {
                list.add(probabilityPotential);
            }
            if (utilityPotentials != null) {
                list.addAll(utilityPotentials);
            }
            return list;
        }

        /**
         * Removes constant potentials (only have one value in attribute values)
         * 
         * @throws IncompatibleEvidenceException
         */
        public void removeConstantPotentials()
                throws IncompatibleEvidenceException {

        	// Probability potential
            if (!VariableElimination.checkIfIncludeInMarkovDecisionNetwork(probabilityPotential)) {
                probabilityPotential = null;
            }

            List<TablePotential> newUtilityPotentialList = new ArrayList<>();
            for(TablePotential utilityPotential : utilityPotentials)
            {
	            // Utility potential
	            if (VariableElimination.checkIfAddToConstantPotentials(utilityPotential)) {
	                constantPotentials.add(utilityPotential);
	            }
	            if (VariableElimination.checkIfIncludeInMarkovDecisionNetwork(utilityPotential)) {
	            	newUtilityPotentialList.add(utilityPotential);
	            }
            }
            utilityPotentials = newUtilityPotentialList;
        }

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("Probability potential= ");
			sb.append(probabilityPotential == null?  "null" : probabilityPotential.toShortString());
			sb.append("; Utility potentials= ");
			for(TablePotential utilityPotential : utilityPotentials)
            {
				sb.append(utilityPotential.toShortString());
            }
			return  sb.toString();
		}
    }    

}
