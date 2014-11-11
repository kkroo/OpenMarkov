/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.dt;

import java.awt.BorderLayout;
import java.awt.Color;

import org.openmarkov.core.gui.window.mdi.FrameContentPanel;
import org.openmarkov.core.model.network.ProbNet;

@SuppressWarnings("serial")
public class DecisionTreeWindow extends FrameContentPanel
{
    private String  title   = null;
    private DecisionTreePanel decisionTreePanel = null;

    public DecisionTreeWindow (ProbNet probNet)
    {
        setLayout(new BorderLayout());
        title  = probNet.getName () + "- decision tree";
        decisionTreePanel = new DecisionTreePanel (probNet);
        add (decisionTreePanel, BorderLayout.CENTER);
        setBackground (Color.blue);
    }

    @Override
    public String getTitle ()
    {
        return title;
    }

    @Override
    public void close ()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public double getZoom ()
    {
        return decisionTreePanel.getZoom ();
    }

    @Override
    public void setZoom (double zoom)
    {
        decisionTreePanel.setZoom (zoom);
    }

}
