/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.action;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialTypeManager;

@SuppressWarnings("serial")
public class SetPotentialEdit extends SimplePNEdit {
	// unused - private PotentialType lastPotentialType;
	private Potential lastPotential;
	private String newPotentialType;
	// private ICIModelType newICIModelType;
	private Variable variable;
	private Potential newPotential = null;
	private ProbNode probNode;

	/**
	 * Creates a new SetPotentialEdit object that sets the a new potential with
	 * the type specified for the probNode object.
	 * 
	 * @param probNode
	 *            The probNode that contains the potential to modify
	 * @param newPotentialType
	 *            The potential type of the new potential to be created
	 */
	public SetPotentialEdit(ProbNode probNode, String newPotentialType) {
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.variable = probNode.getVariable();
		//if (!(probNode.getNodeType() == NodeType.DECISION && probNode
			//	.getPolicyType() == PolicyType.OPTIMAL)) {
			lastPotential = probNode.getPotentials().get(0);
	//	}

		this.newPotentialType = newPotentialType;

	}

	/**
	 * SetPotentialEdit object that changes the last Potential with the
	 * potential specified for the probNode object.
	 * 
	 * @param probNode
	 *            The probNode that contains the potential to set.
	 * @param newPotentialType
	 *            The new potential object
	 */
	public SetPotentialEdit(ProbNode probNode, Potential potential) {
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.variable = probNode.getVariable();
		if (probNode.getPotentials().size() != 0) {// if probNode is a decision node it could not have a potential assigned yet
			lastPotential = probNode.getPotentials().get(0);
		}
		
		newPotential = potential;
	}

	// TODO al asignar un potencial tener en cuenta a los padres y a los
	// predecesores informativos que me los va a dar Manolo invocando a una
	// funcion
	@Override
	public void doEdit() throws DoEditException {
		List<Variable> variables = new ArrayList<Variable>();
		//ProbNode probNode = probNet.getProbNode(variable);
		PotentialRole role;
		// si es un nodo de decision y la politica es optima se asume un cambio
		// de politica optima a probabilista (de momento no se tiene en cuenta
		// la politica determinista)
	/*	if ((probNode.getNodeType() == NodeType.DECISION && probNode
				.getPolicyType() == PolicyType.OPTIMAL)) {// no tiene potencial
															// hay que crear uno
															// uniforme en
															// funcion de los
															// predecesores
															// informativos
			role = PotentialRole.POLICY;
			variables.add(variable);
			for (Node node : probNode.getNode().getParents()) {// cambiando el
																// getParents
																// por
																// predecesores
																// informativos,
																// quitar
																// el for y
																// llamar al
																// metodo de
																// Manolo que me
																// devuelve las
																// variables
				variables.add(((ProbNode) node.getObject()).getVariable());
			}
		} else {*/
			variables = lastPotential.getVariables();
			role = lastPotential.getPotentialRole();
	//	}
		List<Potential> potentials = new ArrayList<Potential>();
		if (newPotential == null) {
			RelationPotentialTypeManager relationTypeManager = new RelationPotentialTypeManager();
			if (lastPotential.isUtility()) {
				newPotential = relationTypeManager.getByName(newPotentialType,
						variables, role, lastPotential
						.getUtilityVariable());
			} else {
			newPotential = relationTypeManager.getByName(newPotentialType, variables, role);
			}

			// TODO Potential: SameAsPrevious without ProbNet
			// newPotential = new SameAsPrevious (probNet, variable);
		}

		if (!(probNode.getNodeType() == NodeType.DECISION && probNode
				.getPolicyType() == PolicyType.OPTIMAL)) {
		} else {
		//	probNet.getProbNode(variable).setPolicyType(PolicyType.PROBABILISTIC);
			probNode.setPolicyType(PolicyType.PROBABILISTIC);
		}

		potentials.add(newPotential);
		//probNet.getProbNode(variable).setPotentials(potentials);
		probNode.setPotentials(potentials);
		// update potential with link restriction
		if (newPotentialType == TablePotential.class.getAnnotation(
				RelationPotentialType.class).name() && probNode.getNodeType() != NodeType.DECISION ) {
			newPotential = (TablePotential) LinkRestrictionPotentialOperations
					.updatePotentialByLinkRestrictions(probNode.getNode());
			potentials = new ArrayList<Potential>();
			potentials.add(newPotential);
			probNode.setPotentials(potentials);
			//probNet.getProbNode(variable).setPotentials(potentials);
		}
	}

	public void undo() {
		super.undo();
		ProbNode probNode = probNet.getProbNode(variable);
		List<Potential> potentials = new ArrayList<Potential>();
		if (lastPotential != null) {
			potentials.add(lastPotential);
		} else if (probNode.getNodeType() == NodeType.DECISION) {
			probNode.setPolicyType(PolicyType.OPTIMAL);

		}
		probNode.setPotentials(potentials);
	}

	public Potential getNewPotential() {
		return newPotential;
	}

	public String getNewPotentialType() {
		return newPotentialType;
	}

	public ProbNode getProbNode() {
		return probNode;
	}

}
