/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network.constraint;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;

public interface Checkable {

   /** @param probNet. <code>ProbNet</code>
    * @return <code>true</code> if the <code>probNet</code> fulfills the 
    * condition. */
   public boolean checkProbNet(ProbNet probNet);
   
   /** Make sure all editions of the event fulfill the condition.
    * @param probNet. <code>ProbNet</code>
    * @param edit <code>PNEdit</code>
    * @return <code>true</code> if the <code>ProbNet</code> will fulfill certain
    *  condition after applying the <code>edit</code> in a 
    *  <code>ProbNet</code> that previously fulfilled the constraint. 
    * @throws WrongCriterionException 
    * @throws NonProjectablePotentialException */
   public boolean checkEdit(ProbNet probNet, PNEdit edit) 
   throws NonProjectablePotentialException, 
   WrongCriterionException;
   
}
