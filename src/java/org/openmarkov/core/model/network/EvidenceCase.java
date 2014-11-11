/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.potential.Potential;

/**
 * This class stores an <code>ArrayList</code> of <code>Findings</code> and can
 * search them with the name.
 * 
 * @author marias
 * @author fjdiez
 * @see org.openmarkov.core.model.network.Finding
 * @version 1.0
 * @since OpenMarkov 1.0
 */
public class EvidenceCase {

    // Attribute
    /**
     * List of findings <code>HashMap</code> of key=<code>String</code> and
     * value=<code>Finding</code>.
     */
    protected HashMap<Variable, Finding> findings;

    // Constructors
    /**
     * @param findings
     *            <code>HashMap</code> of key=<code>String</code> and value=
     *            <code>Finding</code>.
     */
    public EvidenceCase(HashMap<Variable, Finding> findings) {
        this.findings = findings;
    }

    /**
     * Constructor
     * 
     * @param findings
     */
    public EvidenceCase(List<Finding> findings) {
        this.findings = new HashMap<Variable, Finding>();
        for (Finding finding : findings) {
            this.findings.put(finding.getVariable(), finding);
        }
    }

    public EvidenceCase() {
        findings = new HashMap<Variable, Finding>();
    }

    /**
     * Copy constructor
     * 
     * @param evidenceCase
     */
    public EvidenceCase(EvidenceCase evidenceCase) {
        findings = new HashMap<Variable, Finding>(evidenceCase.findings);
    }

    // Methods
    /**
     * @return The state assigned to the variable. <code>int</code>.
     * @argCondition There is a finding for this variable in the evidence
     * @throws NoFindingException
     */
    public int getState(Variable variable) {
        return getFinding(variable).getStateIndex();
    }

    /**
     * @param variable
     *            <code>Variable</code>.
     * @argCondition There is a finding for this variable in the evidence
     * @return The value of a evidence for a continuous or hybrid variable if it
     *         exists: <code>double</code>.
     */
    public double getNumericalValue(Variable variable) {
        return getFinding(variable).getNumericalValue();
    }

    /**
     * @param finding
     *            . <code>Finding</code>.
     * @throws InvalidStateException
     * @throws IncompatibleEvidenceException
     */
	public void addFinding(Finding finding) throws InvalidStateException,
			IncompatibleEvidenceException {
		if (isCompatible(finding)) {
			if (!findings.containsKey(finding.getVariable())) {
				findings.put(finding.getVariable(), finding);
			}
		} else {
			throw new IncompatibleEvidenceException("Error trying to add " + "evidence: "
					+ finding.toString() + " having previously " + "evidence: "
					+ findings.get(finding.getVariable()));
		}
	}

    /**
     * @param finding
     *            . <code>Finding</code>.
     * @throws InvalidStateException
     * @throws IncompatibleEvidenceException
     */
    public void changeFinding(Finding finding)
            throws InvalidStateException, IncompatibleEvidenceException {
        findings.remove(finding.getVariable());
        addFinding(finding);
    }

    /**
     * @param findings
     *            . <code>Collection</code> of <code>Finding</code>s.
     * @throws InvalidStateException
     * @throws IncompatibleEvidenceException
     */
    public void addFindings(Collection<Finding> findings)
            throws InvalidStateException, IncompatibleEvidenceException {
        for (Finding finding : findings) {
            addFinding(finding);
        }
    }

    /**
     * @param finding
     *            <code>Finding</code>.
     * @throws InvalidStateException
     * @throws IncompatibleEvidenceException
     * @throws ProbNodeNotFoundException
     */
    public void addFinding(ProbNet probNet, String variableName, String stateName)
            throws ProbNodeNotFoundException, InvalidStateException, IncompatibleEvidenceException {
        Variable variable = probNet.getVariable(variableName);
        int stateIndex = variable.getStateIndex(stateName);
        addFinding(new Finding(variable, stateIndex));
    }

    /**
     * @param finding
     *            <code>Finding</code>.
     * @throws IncompatibleEvidenceException
     * @throws ProbNodeNotFoundException
     */
    public void addFinding(ProbNet probNet, String variableName, double value)
            throws ProbNodeNotFoundException, InvalidStateException, IncompatibleEvidenceException {
        Variable variable = probNet.getVariable(variableName);
        Finding finding = new Finding(variable, value);
        addFinding(finding);
    }

    /**
     * @param variable
     *            <code>Variable</code>.
     * @throws NoFindingException
     */
    public void removeFinding(Variable variable)
            throws NoFindingException {
        Finding finding = getFinding(variable);
        if (finding == null) {
            throw new NoFindingException(variable);
        }
        findings.remove(finding.getVariable());
    }

    /**
     * @param variableName
     *            <code>String</code>.
     * @throws NoFindingException
     */
    public void removeFinding(String variableName)
            throws NoFindingException {
        ArrayList<Variable> findingsVariables = new ArrayList<Variable>(findings.keySet());
        int i = 0, numVariables = findingsVariables.size();
        Variable variable = null;
        do {
            variable = findingsVariables.get(i++);
        } while (i < numVariables && !variable.getName().contentEquals(variableName));
        if (variable == null) {
            throw new NoFindingException(variableName);
        } else {
            findings.remove(variable);
        }
    }

    /**
     * @return The set of variables associated to the set of findings in the
     *         same order: <code>ArrayList</code> of <code>Variable</code>.
     */
    public List<Variable> getVariables() {
        return new ArrayList<Variable>(findings.keySet());
    }

    /**
     * @param variable
     *            <code>String</code>.
     * @return finding <code>Finding</code>.
     * @argCondition There is a finding for this variable in the evidence
     */
    public Finding getFinding(Variable variable) {
        return findings.get(variable);
    }

    /** @return findings: <code>ArrayList</code> of <code>Finding</code>s. */
    public List<Finding> getFindings() {
        return new ArrayList<Finding>(findings.values());
    }

    /**
     * Returns true if the evidence case contains a finding for this variable.
     * 
     * @return <code>boolean</code>.
     * @param variable
     *            . <code>Variable</code>
     */
    public boolean contains(Variable variable) {
        return findings.containsKey(variable);
    }

    /**
     * @return <code>boolean</code>.
     * @param variables
     *            . <code>ArrayList</code> of <code>Variable</code>s.
     * @throws NoFindingException
     */
    public boolean existsEvidence(List<Variable> variables) {
        for (Variable variable : variables) {
            if (findings.get(variable) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Keeps the nodes of the received <code>probNet</code> that has not
     * received evidence.
     * 
     * @param probNet
     *            <code>ProbNet</code>.
     * @return An <code>ArrayList</code> of <code>ProbNode</code>s.
     */
    public List<ProbNode> getRemainingNodes(ProbNet probNet) {
        List<ProbNode> probNetNodes = probNet.getProbNodes();
        List<ProbNode> remainingNodes = new ArrayList<ProbNode>();
        for (ProbNode probNode : probNetNodes) {
            if (!contains(probNode.getVariable())) {
                remainingNodes.add(probNode);
            }
        }
        return remainingNodes;
    }

    /**
     * @return <code>true</code> if there are no findings. <code>boolean</code>
     */
    public boolean isEmpty() {
        return findings.isEmpty();
    }

    /**
     * Overrides <code>toString</code> method. Mainly for test purposes. It
     * writes the name of the variables and the findings.
     */
    public String toString() {
        String string = new String("[");
        Collection<Finding> findingsCollection = findings.values();
        for (Finding finding : findingsCollection) {
            if (string.compareTo("[") != 0) {
                string = string + ", ";
            }
            string = string + finding.toString();
        }
        string = string + "]\n";
        return string;
    }

    /**
     * Extends an evidence case by taking into account that the deterministic
     * potentials of a <code>ProbNet</code> may induce new findings
     * 
     * @throws InvalidStateException
     * @throws WrongCriterionException
     */
    public void extendEvidence(ProbNet probNet, double cycleLength)
            throws IncompatibleEvidenceException, InvalidStateException, WrongCriterionException {
        for(Potential potential: probNet.getPotentials())
        {
            List<Finding> newFindings = (List<Finding>) potential.getInducedFindings(this,
                    cycleLength);
            for (Finding newFinding : newFindings) {
                findings.put(newFinding.getVariable(), newFinding);
            }
        }
        Queue<Finding> pendingFindings = new LinkedList<Finding>(findings.values());
        while (!pendingFindings.isEmpty()) {
            Finding oldFinding = pendingFindings.poll();
            Variable oldVariable = oldFinding.getVariable();
            List<Potential> potentials = probNet.getPotentials(oldVariable);
            for (Potential potential : potentials) {
                List<Finding> newFindings = (List<Finding>) potential.getInducedFindings(this,
                        cycleLength);
                for (Finding newFinding : newFindings) {
                    if(!findings.containsKey(newFinding.getVariable()))
                    {
                        findings.put(newFinding.getVariable(), newFinding);
                        pendingFindings.add(newFinding);
                    }
                }
            }
        }
    }

	/**
	 * Ensures that the <code>newFinding</code> is not inconsistent with the
	 * actual evidence.
	 * 
	 * @param newFinding
	 *            . <code>Finding</code>
	 * @return <code>boolean</code>
	 * @throws InvalidStateException
	 */
	public boolean isCompatible(Finding newFinding) throws InvalidStateException {
		Variable variable = newFinding.getVariable();
		Finding existingFinding = findings.get(variable);
		if (existingFinding == null) {
			return true;
		} else {
			VariableType variableType = variable.getVariableType();
			switch (variableType) {
			case FINITE_STATES:
				return newFinding.stateIndex == existingFinding.stateIndex;
			case NUMERIC:
				return newFinding.numericalValue == existingFinding.numericalValue;
			case DISCRETIZED:
				return (newFinding.stateIndex == existingFinding.stateIndex)
						|| (newFinding.numericalValue == existingFinding.numericalValue);
			}
		}
		return true;
	}

    public EvidenceCase shiftEvidenceBackwards(int timeDifference, ProbNet probNet) {
        EvidenceCase shiftedEvidence = new EvidenceCase();
        try {
            for (Finding finding : findings.values()) {
                Variable findingVariable = finding.getVariable();
                // generate shifted finding
                if (findingVariable.isTemporal()) {
                    if (probNet.containsShiftedVariable(findingVariable, -timeDifference)) {
                        Variable shiftedVariable = probNet.getShiftedVariable(findingVariable,
                                -timeDifference);
                        Finding shiftedFinding = new Finding(shiftedVariable, finding.stateIndex);
                        shiftedFinding.numericalValue = finding.numericalValue;
                        shiftedEvidence.addFinding(shiftedFinding);
                    }
                } else {
                    // add non-temporal findings
                    shiftedEvidence.addFinding(finding);
                }
            }
        } catch (Exception e) {
            // Unreachable code
            throw new Error("shifted finding");
        }
        return shiftedEvidence;
    }

    /**
     * @return The number of findings in the evidence case
     */
    public int getNumberOfFindings() {
        int num;
        if (findings == null) {
            num = 0;
        } else {
            num = findings.size();
        }
        return num;
    }

    /**
     * Fuse this EvidenceCase with the input parameter one
     * 
     * @param evidenceCaseToFuse
     * @param overwrite
     *            if true the findings in the parameter will overwrite those in
     *            this EvidenceCase
     * @throws IncompatibleEvidenceException
     */
    public void fuse(EvidenceCase evidenceCaseToFuse, boolean overwrite)
            throws IncompatibleEvidenceException {
        if (evidenceCaseToFuse != null) {
            for (Finding finding : evidenceCaseToFuse.getFindings()) {
                try {
                    if (this.contains(finding.getVariable())) {
                        if (overwrite) {
                            changeFinding(finding);
                        }
                    } else {
                        this.addFinding(finding);
                    }
                } catch (InvalidStateException ignore) {
                }
            }
        }
    }

}