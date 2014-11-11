/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.model.network.type;

import org.openmarkov.core.model.network.constraint.ConstraintBehavior;
import org.openmarkov.core.model.network.constraint.NoLinkRestriction;
import org.openmarkov.core.model.network.constraint.NoRevelationArc;
import org.openmarkov.core.model.network.type.plugin.ProbNetType;

@ProbNetType(name="DAN")
public class DecisionAnalysisNetworkType extends NetworkType {
	private static DecisionAnalysisNetworkType instance = null;

	// Constructor
	private DecisionAnalysisNetworkType() {
		super();
		overrideConstraintBehavior(NoRevelationArc.class, ConstraintBehavior.NO);
		overrideConstraintBehavior(NoLinkRestriction.class,
				ConstraintBehavior.NO);
	}

	// Methods
	public static DecisionAnalysisNetworkType getUniqueInstance() {
		if (instance == null) {
			instance = new DecisionAnalysisNetworkType();
		}
		return instance;
	}

	/** @return String "DecisionAnalysisNetwork". */
	public String toString() {
		return "DAN";
	}

}
