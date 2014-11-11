/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmarkov.learning.gui;

import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.learning.core.algorithm.LearningAlgorithm;


/**
 * This abstract class represents the dialog that shows the user the options
 * and parameters of each learning algorithm.
 * @author joliva
 * @author ibermejo
 */
@SuppressWarnings("serial")
public abstract class AlgorithmParametersDialog extends javax.swing.JDialog{
    
    /**
     * String database 
     */
    protected StringDatabase stringDatabase = StringDatabase.getUniqueInstance ();
    
    /**
     * Dialog that shows the user the options and parameters of each learning 
     * algorithm.
     * @param parent
     * @param modal
     */
    public AlgorithmParametersDialog(java.awt.Frame parent, boolean modal){
        super(parent, modal);
    }
    
    public abstract String getDescription();
    
    public abstract LearningAlgorithm getInstance(ProbNet probNet, CaseDatabase database);    
    
}
