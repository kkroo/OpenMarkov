/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.io;

import java.util.List;

import org.openmarkov.core.exception.WriterException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;

public interface ProbNetWriter {

    /**
     * @param netName = path + network name + extension.
     * @param probNet. <code>ProbNet</code> <code>String</code>
     */
    public void writeProbNet (String netName, ProbNet probNet)
        throws WriterException;

    /**
     * @param netName = path + network name + extension.
     * @param probNet. <code>ProbNet</code> <code>String</code>
     */
    public void writeProbNet (String netName, ProbNet probNet, List<EvidenceCase> evidence)
        throws WriterException;	
}
