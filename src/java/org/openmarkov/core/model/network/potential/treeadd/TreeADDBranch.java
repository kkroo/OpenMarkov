
package org.openmarkov.core.model.network.potential.treeadd;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;

/**
 * TreeADDBranch represents branch of a treeADD. If the top variable of the
 * treeADD is numeric a branch is defined by two thresholds: a minimum and a
 * maximum limit If top variable is finite states, then each branch is defined
 * by its states In both cases branches have a potential assigned,. If the
 * branch is a leaf, it potential could be any kind of potential except a
 * TreeADDPotential
 * @author myebra
 */
public class TreeADDBranch
{
    /**
     * Each TreeADDBranch has an associated potential 
     */
    private Potential      potential = null;
    /**
     * If the topVariable of the tree is a finite states or a discretized
     * variable each branch has an associated state.
     */
    private List<State>    states;
    private List<Variable> parentVariables;
    /**
     * If the topVariable of the tree is a continuous variable it is defined in
     * a continuous interval which has two thresholds.
     */
    private Threshold      lowerBound;
    private Threshold      upperBound;
    private Variable       rootVariable;
    /**
     * A branch can be labeled, labels are used to reference potential from
     * other branches when that potential has more than one parents
     */
    private String label;
    /**
     * A branch can reference a potential from other branch that has been
     * labeled
     */
    private String reference = null;
    private TreeADDBranch referencedBranch = null;
    
    /**
     * Constructor for discretized and finite states variables
     * @param branchStates
     * @param potential
     * @param topVariable
     * @param parentVariables
     */
    public TreeADDBranch(List<State> branchStates, Variable topVariable, Potential potential,
            List<Variable> parentVariables)    {
        this.states = branchStates;
        this.potential = potential;
        this.rootVariable = topVariable;
        this.parentVariables = parentVariables;
    }

    /**
     * Constructor for numeric variables
     * @param lowerThreshold
     * @param upperThreshold
     * @param topVariable
     * @param potential
     * @param parentVariables
     */
    public TreeADDBranch (Threshold lowerBound,
                          Threshold upperBound,
                          Variable topVariable,
                          Potential potential,
                          List<Variable> parentVariables)
    {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.potential = potential;
        this.rootVariable = topVariable;
        this.parentVariables = parentVariables;
    }

    /**
     * Constructor for discretized and finite states variables with reference
     * @param branchStates
     * @param topVariable
     * @param reference
     * @param parentVariables
     */
    public TreeADDBranch (List<State> branchStates,
                          Variable topVariable,
                          String reference,
                          List<Variable> parentVariables)
    {
        this.states = branchStates;
        this.reference = reference;
        this.rootVariable = topVariable;
        this.parentVariables = parentVariables;
    }

    /**
     * Constructor for numeric variables with reference
     * @param thresholdMin
     * @param thresholdMax
     * @param potential
     * @param topVariable
     * @param parentVariables
     */
    public TreeADDBranch (Threshold lowerBound,
                          Threshold upperBound,
                          Variable topVariable,
                          String reference,
                          List<Variable> parentVariables)
    {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.reference = reference;
        this.rootVariable = topVariable;
        this.parentVariables = parentVariables;
    }
    
    public TreeADDBranch copy ()
    {
        TreeADDBranch branch = null;
        if(potential != null)
        {
            if (this.rootVariable.getVariableType() == VariableType.FINITE_STATES
                    || this.rootVariable.getVariableType() == VariableType.DISCRETIZED) {
                branch = new TreeADDBranch(new ArrayList<>(getBranchStates()),
                        this.getRootVariable(),
                        this.getPotential().copy(),
                        this.getParentVariables());

            } else if (this.rootVariable.getVariableType() == VariableType.NUMERIC) {
                branch = new TreeADDBranch(this.getLowerBound().copy(),
                        this.getUpperBound().copy(),
                        this.getRootVariable(),
                        this.getPotential().copy(),
                        this.getParentVariables());
            }
            if(label != null)
            {
                branch.setLabel(label);
            }
        }else if(reference != null)
        {
            if (this.rootVariable.getVariableType () == VariableType.FINITE_STATES
                    || this.rootVariable.getVariableType () == VariableType.DISCRETIZED)
           {            
                branch = new TreeADDBranch(new ArrayList<>(getBranchStates()),
                        this.getRootVariable(),
                        this.reference,
                        this.getParentVariables());
            }
            else if (this.rootVariable.getVariableType () == VariableType.NUMERIC)
            {
                branch = new TreeADDBranch(this.getLowerBound().copy(),
                        this.getUpperBound().copy(),
                        this.getRootVariable(),
                        this.reference,
                        this.getParentVariables());
           }
            if(referencedBranch != null)
            {
                branch.setReferencedBranch(referencedBranch);
            }
        }
        return branch;
    }    
    
    public List<Variable> getAddableVariables() {
        List<Variable> addableVariables = new ArrayList<>();
        List<Variable> potentialVariables = potential.getVariables();
        for (Variable variable : parentVariables) {
            if (!variable.equals(rootVariable)
                    && !potentialVariables.contains(variable)
                    && !variable.equals(potential.getConditionedVariable())) {
                addableVariables.add(variable);
            }
        }
        return addableVariables;
    }
    
    public void setLowerBound (Threshold lowerBound)
    {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound (Threshold upperBound)
    {
        this.upperBound = upperBound;
    }

    public List<State> getBranchStates ()
    {
        return this.states;
    }

    public List<Variable> getParentVariables ()
    {
        return this.parentVariables;
    }

    public void setParentVariables (List<Variable> parentVariables)
    {
        this.parentVariables = parentVariables;
    }

    public Variable getRootVariable ()
    {
        return this.rootVariable;
    }

    public Potential getPotential ()
    {
        return (potential != null || referencedBranch == null) ? potential
                : referencedBranch.getPotential();
    }

    public void setPotential (Potential potential)
    {
        this.potential = potential;
    }

    public void setRootVariable (Variable topVariable)
    {
        this.rootVariable = topVariable;
    }

    public void setStates (List<State> states)
    {
        this.states = states;
    }

    public Threshold getLowerBound ()
    {
        return lowerBound;
    }

    public Threshold getUpperBound ()
    {
        return upperBound;
    }
    
    public boolean isInsideInterval(double numericValue)
    {
        return (lowerBound == null || lowerBound.isBelow(numericValue))
                && (upperBound == null || upperBound.isAbove(numericValue));
    }

    public String getLabel() {
        return this.label;
    }    

    public void setLabel(String label) {
        this.label = label;
    }    
    
    public boolean isLabeled()
    {
        return this.label != null;
    }    

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReference() {
        return this.reference;
    }
    
    public boolean isReference()
    {
        return this.reference != null;
    }

    @Override
    public String toString ()
    {
        StringBuilder builder = new StringBuilder ();
        builder.append ("TreeADDBranch [potential=");
        builder.append (potential);
        builder.append (", states=");
        builder.append (states);
        builder.append (", parentVariables=");
        builder.append (parentVariables);
        builder.append (", thresholdMin=");
        builder.append (lowerBound);
        builder.append (", thresholdMax=");
        builder.append (upperBound);
        builder.append (", topVariable=");
        builder.append (rootVariable);
        builder.append ("]");
        return builder.toString ();
    }

    public void setReferencedBranch(TreeADDBranch treeADDBranch) {
        this.reference = treeADDBranch.getLabel();
        this.referencedBranch = treeADDBranch;
        this.potential = null;
    }
    
}
