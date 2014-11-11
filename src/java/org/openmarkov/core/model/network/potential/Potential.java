/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.potential;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

/**
 * @author marias
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0
 */
public abstract class Potential
{
    // Constants
    /** Maximum size of a String used in toString() */
    protected static final int            STRING_MAX_LENGTH = 150;
    // Attributes
    /**
     * <code>List</code> of <code>Variable</code>s.
     * @frozen
     */
    protected List<Variable>       variables;
    /** @frozen */
    protected int                  numVariables;
    /**
     * Utility variable associated to the <code>ProbNode</code> that contains
     * this potential.
     */
    protected Variable             utilityVariable;
    /** @frozen */
    protected PotentialType        type;
    /** @frozen */
    protected PotentialRole        role;
    /**
     * This object contains all the information that the parser reads from disk
     * that does not have a direct connection with the attributes stored in the
     * <code>Potential</code> object.
     */
    public HashMap<String, Object> properties;
    protected String               comment         = "";

    // Constructor
    /**
     * @param variables. <code>ArrayList</code> of <code>Variable</code>.
     * @param role. <code>PotentialRole</code>
     */
    public Potential (List<Variable> variables, PotentialRole role)
    {
        numVariables = (variables != null)? variables.size () : 0;
        if(variables != null)
        {
            this.variables = new ArrayList<Variable> (variables);
        }else
        {
            this.variables = new ArrayList<Variable> ();
        }
        utilityVariable = null;
        properties = new HashMap<String, Object> ();
        this.role = role;
    }
    
    /**
     * @param variables <code>List</code> of <code>Variable</code>s.
     * @param role. <code>PotentialRole</code>
     * @param utility. <code>Variable</code>
     */
    public Potential (List<Variable> variables, PotentialRole role, Variable utility)
    {
        this(variables, role);
        utilityVariable = utility;
    }
    
    /**
     * Copy constructor for potential
     * @param potential
     */
    public Potential (Potential potential)
    {
        this(potential.getVariables(), potential.getPotentialRole());
        if(potential.getPotentialRole() == PotentialRole.UTILITY)
        {
            this.utilityVariable = potential.getUtilityVariable(); 
        }
        this.comment = potential.getComment();
    }      

    // Methods
    /**
     * Returns if an instance of a certain Potential type makes sense given the
     * variables and the potential role.
     * @param probNode. <code>ProbNode</code>
     * @param variables. <code>ArrayList</code> of <code>Variable</code>.
     * @param role. <code>PotentialRole</code>.
     */
    public static boolean validate (ProbNode probNode, List<Variable> variables, PotentialRole role)
    {
        // Default implementation: always return true
        return true;
    }

    /**
     * @param evidenceCase. <code>EvidenceCase</code>
     * @return The conditional probability table of this potential given the
     *         evidence
     * @throws WrongCriterionException
     * @throws NonProjectablePotentialException
     */
    public TablePotential getCPT (EvidenceCase evidenceCase)
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        List<TablePotential> potentials = tableProject (evidenceCase, null);
        HashSet<Variable> variablesToEliminate = new HashSet<Variable> ();
        // Fill it with variables appearing in all potentials except this
        for (TablePotential tablePotential : potentials)
        {
            variablesToEliminate.addAll (tablePotential.getVariables ());
        }
        variablesToEliminate.removeAll (variables);
        return DiscretePotentialOperations.multiplyAndMarginalize (potentials,
                                                                   variables,
                                                                   new ArrayList<Variable> (
                                                                                            variablesToEliminate));
    }

    /**
     * The conditional probability table given by this potential
     * @return <code>TablePotential</code>
     * @throws NonProjectablePotentialException
     * @throws WrongCriterionException
     */
    public TablePotential getCPT ()
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        return getCPT (new EvidenceCase ());
    }

    /**
     * Modifies the frozen variable role. This method exists to avoid some
     * problems with legacy code in DiscretePotentialOperations class and it
     * does not be used except in very special cases.
     * @param role. <code>PotentialRole</code>
     */
    public void setPotentialRole (PotentialRole role)
    {
        this.role = role;
    }

    /**
     * Checks if all the variables belongs to the type received. The utility
     * variable is not considered.
     * @param type. <code>VariableType</code>
     * @return <code>boolean</code>
     */
    protected boolean noNumericVariables ()
    {
        if (variables != null)
        {
            for (Variable variable : variables)
            {
                if (variable.getVariableType () == VariableType.NUMERIC)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @consultation
     * @return A <code>List</code> of <code>Variable</code>s
     */
    public List<Variable> getVariables ()
    {
        return new ArrayList<Variable> (variables);
    }

    /**
     * @consultation
     * @return The variable in the place <code>position</code>
     */
    public Variable getVariable (int position)
    {
        return variables.get (position);
    }

    public void replaceVariable (Variable variableToReplace, Variable variable)
    {
        if (!variableToReplace.equals (utilityVariable))
        {
            replaceVariable (variables.indexOf (variableToReplace), variable);
        }
        else
        {
            utilityVariable = variable;
        }
    }

    public void replaceVariable (int position, Variable variable)
    {
        variables.remove (position);
        variables.add (position, variable);
    }

    /**
     * @return <code>true</code> if contains the received <code>Variable</code>.
     * @param variable <code>Variable</code>
     */
    public boolean contains (Variable variable)
    {
        return variables.contains (variable);
    }

    // TODO documentar
    /**
     * @param evidenceCase <code>EvidenceCase</code>
     * @param inferenceOptions
     * @param projectedPotentials <code>List</code> of already projected potentials 
     * @throws WrongCriterionException
     * @throws NoFindingException
     */
    public abstract List<TablePotential> tableProject(EvidenceCase evidenceCase,
            InferenceOptions inferenceOptions,
            List<TablePotential> projectedPotentials)
            throws NonProjectablePotentialException, WrongCriterionException;
    
    public List<TablePotential> tableProject(EvidenceCase evidenceCase,
            InferenceOptions inferenceOptions)
            throws NonProjectablePotentialException, WrongCriterionException {
        return tableProject(evidenceCase, inferenceOptions, new ArrayList<TablePotential>());
    }

    /** @return isUtility <code>boolean</code> */
    public boolean isUtility ()
    {
        return role == PotentialRole.UTILITY;
    }

    /** @return number of variables: <code>int</code> */
    public int getNumVariables ()
    {
        return variables.size();
    }

    public Variable getConditionedVariable ()
    {
        Variable conditionedVariable = null;
        switch (role)
        {
        case UTILITY:
            conditionedVariable = (utilityVariable != null)? utilityVariable : variables.get (0);
            break;
        default:
            conditionedVariable = variables.isEmpty()? null : variables.get (0);
            break;
        }
        return conditionedVariable;
    }
    
    /** @return utilityVariable. <code>Variable</code> */
    public Variable getUtilityVariable ()
    {
        return utilityVariable;
    }
    
    /** @param utilityVariable. <code>Variable</code> */
    public void setUtilityVariable (Variable utilityVariable)
    {
        this.utilityVariable = utilityVariable;
        if (utilityVariable != null)
        {
            role = PotentialRole.UTILITY;
        }
    }
    
    /** @return <code>PotentialType</code> */
    public PotentialType getPotentialType ()
    {
        return type;
    }

    /**
     * Generates new <code>Finding</code>s generated by an
     * <code>EvidenceCase</code>. In principle this method does not generate any
     * new finding, but it is overridden in some of its subclasses.
     * @param evidenceCase. <code>EvidenceCase</code>
     * @return <code>Collection</code> of <code>Finding</code>s
     * @throws IncompatibleEvidenceException
     * @throws WrongCriterionException
     */
    public Collection<Finding> getInducedFindings (EvidenceCase evidenceCase, double cycleLength)
        throws IncompatibleEvidenceException,
        WrongCriterionException
    {
        return new ArrayList<Finding> ();
    }

    /** @return role. <code>PotentialRole</code> */
    public PotentialRole getPotentialRole ()
    {
        return role;
    }

    /** @param comment. <code>String</code> */
    public void setComment (String comment)
    {
        this.comment = comment;
    }

    /** @return comment. <code>String</code> */
    public String getComment ()
    {
        return comment;
    }

    /**
     * Subclasses of Potential must override this
     * method.
     * @return <code>Potential</code>
     * @param timeDifference. <code>int</code>
     * @param probNet This parameter is necessary because the shifted variables
     *            are taken from the network. <code>ProbNet</code>
     * @throws ProbNodeNotFoundException
     */
    public void shift(ProbNet probNet, int timeDifference) throws ProbNodeNotFoundException {
        setVariables(getShiftedVariables(probNet, timeDifference));
        if (isUtility() && getUtilityVariable().isTemporal()) {
            setUtilityVariable(probNet.getShiftedVariable(getUtilityVariable(),
                    timeDifference));
        }
    }

    /**
     * Creates links between the variables of a potential
     * @argCondition The role of the potential must be utility of conditional
     *               probability
     * @argCondition The network must contain all the variables of the potential
     */
    public void createDirectedLinks (ProbNet probNet)
    {
        Variable childVariable;
        int firstParentIndex;
        if (isUtility ())
        {
            childVariable = utilityVariable;
            firstParentIndex = 0;
        }
        else
        {
            childVariable = variables.get (0);
            firstParentIndex = 1;
        }
        for (int parentIndex = firstParentIndex; parentIndex < variables.size (); parentIndex++)
        {
            try
            {
                probNet.addLink (variables.get (parentIndex), childVariable, true);
            }
            catch (NodeNotFoundException e)
            {
                // Unreachable code
            }
        }
    }

    /**
     * Returns a list with the same variables as this potential, including the
     * utility variable but shifted in time as indicated by timeDifference
     * @throws ProbNodeNotFoundException 
     * @argCondition The network must contain the shifted variables.
     */
    public List<Variable> getShiftedVariables(ProbNet probNet, int timeDifference)
            throws ProbNodeNotFoundException    {
        List<Variable> shiftedVariables = new ArrayList<Variable> ();
        // also shift variables within the tree
        for (Variable variable : getVariables ())
        {
            if (variable.isTemporal ())
            {
                shiftedVariables.add (probNet.getShiftedVariable (variable, timeDifference));
            }
            else
            {
                shiftedVariables.add (variable);
            }
        }
        return shiftedVariables;
    }

    /** Overrides <code>toString</code> method. Mainly for test purposes */
    public String toString ()
    {
        return toShortString ();
    }

    public String toShortString ()
    {
        StringBuffer buffer = new StringBuffer ();
        if (numVariables == 0)
        { // Constant potential
            switch (role)
            {
                case UTILITY :
                    buffer.append (utilityVariable == null? "unspecified" : utilityVariable.getName ());
                    break;
                case CONDITIONAL_PROBABILITY :
                    break;
                case JOINT_PROBABILITY :
                    break;
                default :
                    break;
            }
        }
        else
        {
            switch (role)
            {
                case CONDITIONAL_PROBABILITY :
                    buffer.append ("P(" + variables.get (0));
                    if (numVariables > 1)
                    {
                        buffer.append (" | ");
                        printVariables (buffer, 1);
                    }
                    buffer.append (")");
                    break;
                case UTILITY :
                    buffer.append ("U(" + utilityVariable);
                    if (numVariables > 0)
                    {
                        buffer.append (" | ");
                    }
                    printVariables (buffer, 0);
                    buffer.append (")");
                    break;
                case JOINT_PROBABILITY :
                    buffer.append ("P(");
                    printVariables (buffer, 0);
                    buffer.append (")");
                    break;
                default :
                    buffer.append (numVariables + " Variables: ");
                    if (numVariables > 0)
                    {
                        buffer.append (variables.get (0).getName ());
                        for (int i = 1; i < numVariables - 1; i++)
                        {
                            buffer.append (", " + variables.get (i).getName ());
                        }
                        if (numVariables > 1)
                        {
                            buffer.append (", " + variables.get (numVariables - 1).getName ());
                        }
                    }
            }
        }
        return buffer.toString ();
    }

    /**
     * Prints in buffer the variables and in case of TablePotential the
     * configurations
     */
    private StringBuffer printVariables (StringBuffer buffer, int firstVariable)
    {
        // Print variables
        for (int i = firstVariable; i < numVariables - 1; i++)
        {
            buffer.append (variables.get (i) + ", ");
        }
        buffer.append (variables.get (numVariables - 1));
        return buffer;
    }

    public String treeADDString ()
    {
        return toString ();
    }

    /**
     * @returns a sampled potential. By default, itself, i.e., not sampled.
     */
    public Potential sample ()
    {
        return this; // By default
    }

    @Override
    public boolean equals (Object arg0)
    {
        if (arg0.getClass ().equals (this.getClass ()))
        {
            Potential potential = (Potential) arg0;
            return variables.equals (potential.variables)
                   && type == potential.type
                   && role == potential.role;
        }
        else
        {
            return false;
        }
    }

    public int sample (Random randomGenerator, Map<Variable, Integer> sampledParents)
    {
        return Integer.MAX_VALUE;
    }

    /**
     * Return a copy instance of the potential
     * @return potential copy
     */
    public abstract Potential copy ();

    /**
     * Return true if potential has uncertainty values
     * @return whether the potential has uncertainty or not
     */
    public abstract boolean isUncertain ();

    /**
     * Adds variable to a potential implemented in each child class
     */
    public Potential addVariable (Variable variable)
    {
        return null;
    }

    /**
     * Removes variable to a potential implemented in each child class
     */
    public Potential removeVariable (Variable variable)
    {
        return null;
    }

    public double getProbability (HashMap<Variable, Integer> sampledStateIndexes)
    {
        return 0;
    }

    public double getUtility (HashMap<Variable, Integer> sampledStateIndexes,
                              HashMap<Variable, Double> utilities)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    protected static List<Variable> toList (Variable[] variables)
    {
        List<Variable> variablesArrayList = new ArrayList<Variable> ();
        for (Variable variable : variables)
        {
            variablesArrayList.add (variable);
        }
        return variablesArrayList;
    }

    public void setVariables (List<Variable> variables)
    {
        this.variables = variables;
        this.numVariables = variables.size();
    }

    public double getProbability (EvidenceCase evidenceCase)
    {
        HashMap<Variable, Integer> configuration = new HashMap<> ();
        for (Finding finding : evidenceCase.getFindings ())
        {
            configuration.put (finding.getVariable (), finding.getStateIndex ());
        }
        return getProbability (configuration);
    }

	public void replaceNumericVariable(Variable convertedParentVariable) {
		int varIndex = -1;
		for(int i=0; i<variables.size();++i)
		{
			if(variables.get(i).getName().equals(convertedParentVariable.getName()))
			{
				varIndex = i;
			}
		}
		if(varIndex != -1)
		{
			variables.set(varIndex, convertedParentVariable);
		}
	}

    protected static TablePotential findPotentialByVariable(Variable variable, List<TablePotential> potentials) {
        int i=0;
        TablePotential potential = null;
        while(i<potentials.size() && potential==null)
        {
            if(variable.equals(potentials.get(i).getConditionedVariable()))
            {
                potential = potentials.get(i);
            }
            ++i;
        }
        return potential;
    }	
}
