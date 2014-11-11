/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.inference;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.GTablePotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

public class Strategy {

    Hashtable<Variable, Policy> strategy;

    public class Policy {

        GTablePotential<Choice> potential;

        @SuppressWarnings("unchecked")
        public Policy(Variable dec, TablePotential utilities) {
            this();
            potential = (GTablePotential<Choice>) DiscretePotentialOperations.maximize(utilities,
                    dec)[1];
        }

        public Policy() {
            // TODO Auto-generated constructor stub
        }

        public GTablePotential<Choice> getPotential() {

            return potential;
        }

        public List<Variable> getDomain() {
            return potential.getVariables();
        }

    }

    public List<Variable> getDomainOfPolicy(Variable varDecision) {
        return getPolicy(varDecision).getDomain();
    }

    public Policy getPolicy(Variable varDecision) {
        return strategy.get(varDecision);
    }

    /**
     * @param stratUtil
     *            constructs a strategy by maximizing over the utility tables
     */
    public Strategy(StrategyUtilities stratUtil) {
        this();
        Set<Variable> decisions = stratUtil.getUtilities().keySet();

        for (Variable dec : decisions) {
            setPolicy(dec, new Policy(dec, stratUtil.getUtilities(dec)));
        }

    }

    private void setPolicy(Variable dec, Policy policy) {
        strategy.put(dec, policy);

    }

    public Strategy() {
        strategy = new Hashtable<Variable, Policy>();
    }

}
