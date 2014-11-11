/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.algorithm.scoreAndSearch.cache;

import java.util.HashMap;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.BaseLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;

/** This class implements a cache for the scores of the different possible
 * editions during the learning process following the approach of Weka.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
public class Cache{

	/** change in score due to adding an arc **/
	protected double [][] deltaScoreAdd;
	/** change in score due to deleting an arc **/
	protected double [][] deltaScoreRem;
	
	/** number assigned to each variable */
	protected HashMap<Variable, Integer> orderedVariables;

	protected int numNodes;


	public void flush(ProbNet newNet)  {
        this.numNodes = newNet.getNumNodes();

        orderedVariables = new HashMap<Variable,Integer>();
        deltaScoreAdd = new double [numNodes][numNodes];
        deltaScoreRem = new double [numNodes][numNodes];
        
        int i = 0;
        for (Variable var : newNet.getVariables()){
            orderedVariables.put(var, i);
            i++;
        }
    }

	/** Set the score of a given edition.
	 * @param PNEdit edition whose value is going to be put in the cache
	 * @param fValue value to put in cache
	 */
	public void cacheScore(PNEdit edit, double fValue) {
        Variable tail = ((BaseLinkEdit)edit).getVariable1 ();
        Variable head = ((BaseLinkEdit)edit).getVariable2 ();

        if (edit.getClass () == AddLinkEdit.class)
        {
            deltaScoreAdd[orderedVariables.get (tail)][orderedVariables.get (head)] = fValue;
        }
        else
        {
            deltaScoreRem[orderedVariables.get (tail)][orderedVariables.get (head)] = fValue;
        }
	} // put 

	/** Obtain the score of a given edition.
	 * @param PNEdit edition whose score we want to get.
	 * @return cache value
	 */
	public double getScore(PNEdit edit) {
	    Variable tail = ((BaseLinkEdit)edit).getVariable1 ();
        Variable head = ((BaseLinkEdit)edit).getVariable2 ();
	    
        if (edit.getClass () == AddLinkEdit.class)
        {
            return deltaScoreAdd[orderedVariables.get (tail)][orderedVariables.get (head)];
        }
        else if (edit.getClass () == RemoveLinkEdit.class)
        {
            return deltaScoreRem[orderedVariables.get (tail)][orderedVariables.get (head)];
        }
        else
        {
            return deltaScoreRem[orderedVariables.get (tail)][orderedVariables.get (head)]
                   + deltaScoreAdd[orderedVariables.get (head)][orderedVariables.get (tail)];
        }
	} // get

	
    /**
     * Returns score for adding a link between tail and head
     * @param tail
     * @param head
     * @return
     */
    public double getAddScore (ProbNet learnedNet, Variable tail, Variable head)
    {
        return deltaScoreAdd[orderedVariables.get (tail)][orderedVariables.get (head)];
    }
    
    /**
     * Returns score for removing a link between tail and head
     * @param tail
     * @param head
     * @return
     */
    public double getRemoveScore (ProbNet learnedNet, Variable tail, Variable head)
    {
        return deltaScoreRem[orderedVariables.get (tail)][orderedVariables.get (head)];
    }
    
}
