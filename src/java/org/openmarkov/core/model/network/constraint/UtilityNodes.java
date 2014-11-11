/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.constraint;

import java.util.List;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.constraint.annotation.Constraint;
import org.openmarkov.core.model.network.potential.Potential;


@Constraint (name = "UtilityNodes", defaultBehavior = ConstraintBehavior.OPTIONAL)
public class UtilityNodes extends PNConstraint {
	
	@Override
	public boolean checkEdit(ProbNet probNet, PNEdit edit) {
		return true;
	}

	@Override
	public boolean checkProbNet(ProbNet probNet) {
	    List<ProbNode> utilityNodes = 
			probNet.getProbNodes(NodeType.UTILITY);
		int numUtilityNodes = utilityNodes.size();
		if (numUtilityNodes == 0) {
			return false;
		} else { // check same number of utility nodes and utility potentials
		    List<Potential> potentials = probNet.getPotentials();
			int numUtilityPontentials = 0;
			for (Potential potential : potentials) {
				if (potential.isUtility()) {
					numUtilityPontentials++;
				}
			}
			return (numUtilityPontentials == numUtilityNodes);
		}
	}

    @Override
    protected String getMessage ()
    {
        // TODO Auto-generated method stub
        return "";
    }

}
