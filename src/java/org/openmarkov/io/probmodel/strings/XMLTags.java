/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.probmodel.strings;

import java.io.Serializable;

public enum XMLTags implements Serializable {
	ADDITIONAL_CONSTRAINTS("AdditionalConstraints"),
	ADDITIONAL_PROPERTIES("AdditionalProperties"),
	AGENTS("Agents"),
	AGENT("Agent"),
	ALWAYS_OBSERVED("AlwaysObserved"),
	ARGUMENT("Argument"),
	BRANCH("Branch"),
	CHOLESKY_DECOMPOSITION("CholeskyDecomposition"),
	COEFFICIENTS("Coefficients"),
	COVARIANCE_MATRIX("CovarianceMatrix"),
    COVARIATE("Covariate"),
	COVARIATES("Covariates"),
	COMMENT("Comment"),
	COORDINATES("Coordinates"),
	COORDINATES_SHIFT("CoordinatesShift"),
	CONSTRAINT("Constraint"),
	CONSTRAINTS("Constraints"),
	CRITERION("Criterion"),
	CYCLE_LENGTH("CycleLength"),
	DECISION_CRITERIA("DecisionCriteria"),
	DISCOUNT_RATE("DiscountRate"),
	EVIDENCE("Evidence"),
	EVIDENCE_CASE("EvidenceCase"),	
	HORIZON("Horizon"),
	INFERENCE_OPTIONS("InferenceOptions"),
	INTERVAL("Interval"),
	LABEL("Label"),
	LANGUAGE("Language"),
	LINK("Link"),
	LINKS("Links"),
    NUMERIC_VALUE("NumericValue"),
	NUMERIC_VARIABLES("NumericVariables"),
    MODEL("Model"),
	OPEN_MARKOV_XML("OpenMarkov"),
	POLICIES("Policies"),
	POLICY("Policy"),
	POTENTIAL("Potential"),
	POTENTIALS("Potentials"),
	PRECISION("Precision"),
	PROB_NET("ProbNet"),
	PROPERTY("Property"),
	REFERENCE("Reference"),
	STATE("State"),
	STATES("States"),
    STATE_INDEX("StateIndex"),
	STRATEGY("Strategy"),
	SUBPOTENTIALS("Subpotentials"),
	THRESHOLD("Threshold"),
	THRESHOLDS("Thresholds"),
	TIME_UNIT("TimeUnit"),
	TIME_VARIABLE("TimeVariable"),
	TOP_VARIABLE("TopVariable"),
	UNIT("Unit"),
	UTILITY_VARIABLE("UtilityVariable"),
	VALUE("Value"),
	VALUES("Values"),
	VARIABLE("Variable"),
	VARIABLES("Variables"),
	PURPOSE("Purpose"),
	RELEVANCE("Relevance"),
	REVELATION_CONDITIONS("RevelationCondition"),
	BRANCHES("Branches"),
	UNCERTAIN_VALUES("UncertainValues"),
	// TODO OOPN start
    CLASS("Class"),
    CLASSES("Classes"),
    INSTANCE("Instance"),
    REFERENCE_LINK("ReferenceLink"),
    REFERENCE_LINKS("ReferenceLinks"),
    INSTANCE_NODE("InstanceNode"),
    INSTANCE_NODES("Nodes"),
    INSTANCES("Instances"),
    OOPN("OON")	
    // TODO OOPN end
	;
	
	private int type;
	
	private String name;
	
	XMLTags(String name) {
		this.name = name;
		this.type = this.ordinal();
	}
	
	public String toString() {
		return name;
	}
	
	public int getType() {
		return type;
	}
	
}
