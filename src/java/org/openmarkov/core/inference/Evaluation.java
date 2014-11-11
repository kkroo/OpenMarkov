/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.inference;

import java.awt.Choice;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.openmarkov.core.action.PNESupport;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;

/** This class specifies basic operations to obtain probabilities and to set
 *   evidence and policies. 
 *   @author marias */
public abstract class Evaluation {

	// Attributes
	protected ArrayList<Potential> imposedPolicies;
	
	protected EvidenceCase evidence;
	
	/** This is a copy of the <code>ProbNet</code> received. */
	public ProbNet probNet;
	
	/** For undo/redo operations. */
	protected PNESupport pNESupport;	
	
	/** Set of constraints that the ProbNet must satisfy so that it can
	 * be evaluated by this algorithm. Its initial value must be null, instead 
	 * of an empty ArrayList<PNCconstraint>, so that
	 * the method getRequiredConstraints of its child classes detect when this
	 * property has not been initialized */
	protected ArrayList<PNConstraint> requiredConstraints;
	
	// Constructor
	public Evaluation(ProbNet probNet) 
	throws NotEvaluableNetworkException {
		this.probNet = probNet;
		for (PNConstraint constraint : getRequiredConstraints()) {
			if (!constraint.checkProbNet(probNet)) {
				// TODO mejorar el mensaje que devuelve
				throw new NotEvaluableNetworkException(constraint.toString());
			}
		}
	}
	
	/** This method must be overriden in the child classes */
	protected Collection<PNConstraint> getRequiredConstraints() {
		return new ArrayList<PNConstraint>();
	}

	//Methods
	/** @param policies. <code>ArrayList</code> of <code>Potential</code> */
	public void imposePolicies(ArrayList<Potential> policies) {
		imposedPolicies = policies;
	}
	
	/** @param evidence. <code>EvidenceCase</code> */
	public void setEvidence(EvidenceCase evidence) {
		this.evidence = evidence;
	}
	
	/** @return For each decision a potential in a <code>HashMap</code> with
	 *  key = <code>Variable</code> and value = <code>GTablePotential</code> of
	 *  <code>Choice</code>. The HashMap will be <code>null</code> if there are
	 *  no decisions. 
	 * @throws WrongCriterionException 
	 * @throws NonProjectablePotentialException */
	public HashMap<Variable, GTablePotential<Choice>> optimalStrategy() 
	throws WrongGraphStructureException, 
    ConstraintViolationException, CanNotDoEditException, DoEditException, 
    NonProjectablePotentialException, WrongCriterionException {
		return null;
	}
	
    /** @return maxExpectedUtility. <code>TablePotential</code> without
     *  variables, that has a table with one scalar;
     *  by default, its value is 0.0 
     * @throws WrongGraphStructureException 
     * @throws WrongCriterionException 
     * @throws NonProjectablePotentialException */
	public TablePotential getMaxExpectedUtility() 
	throws WrongGraphStructureException, 
	ConstraintViolationException, CanNotDoEditException,
	DoEditException, NonProjectablePotentialException, WrongCriterionException {
		return new TablePotential(
				null, PotentialRole.UTILITY, new double[]{0.0});
	}

	/** This method calculates the probabilities for all the variables in
	 *   this form: P(a|evidence), P(b|evidence) ...
	 * @param evidence <code>EvidenceCase</code>.
	 * @return A <code>HashMap</code> with key = a variable and value =
	 *   a potential 
	 * @throws CanNotDoEditException 
	 * @throws ConstraintViolationException 
	 * @throws DoEditException 
	 * @throws NotEvaluableNetworkException 
	 * @throws NonProjectablePotentialException 
	 * @throws WrongCriterionException */
	public abstract HashMap<Variable, Potential> individualProbabilities(
			EvidenceCase evidence) 
			throws NormalizeNullVectorException,
			DoEditException, ConstraintViolationException, 
			CanNotDoEditException, NotEvaluableNetworkException, 
			NonProjectablePotentialException, WrongCriterionException;
	
	/** This method calculates the probabilities of each variable of interest in
	 *   this form: P(a|evidence), P(b|evidence) ...
	 * @param variablesOfInterest <code>ArrayList</code> of 
	 *   <code>Variable</code>s.
	 * @param evidence <code>EvidenceCase</code>.
	 * @return A <code>HashMap</code> with key = a variable and value = 
	 *   a potential 
	 * @throws CanNotDoEditException 
	 * @throws ConstraintViolationException 
	 * @throws DoEditException 
	 * @throws NotEvaluableNetworkException 
	 * @throws WrongCriterionException */
	public abstract HashMap<Variable, Potential> individualProbabilities(
			ArrayList<Variable> variablesOfInterest, EvidenceCase evidence) 
			throws NormalizeNullVectorException,
			DoEditException, ConstraintViolationException,
			CanNotDoEditException, NotEvaluableNetworkException, 
			NonProjectablePotentialException, WrongCriterionException;;
	
	/** This method calculates the probabilities of the variables included in
	 *   the evidence case in the form: P(a,b,....x|evidence)
	 * @param variablesOfInterest <code>ArrayList</code> of
	 *   <code>Variable</code>s.
	 * @param evidence <code>EvidenceCase</code>.
	 * @param inferenceOptions TODO
	 * @return A <code>Potential</code> 
	 * @throws CanNotDoEditException 
	 * @throws ConstraintViolationException 
	 * @throws DoEditException 
	 * @throws NotEvaluableNetworkException 
	 * @throws NonProjectablePotentialException 
	 * @throws WrongCriterionException */
	public abstract Potential joinProbability(
			ArrayList<Variable> variablesOfInterest, EvidenceCase evidence, 
			InferenceOptions inferenceOptions) 
	throws NormalizeNullVectorException,
	DoEditException, ConstraintViolationException, 
	CanNotDoEditException, NotEvaluableNetworkException, 
	NonProjectablePotentialException, WrongCriterionException;

}
