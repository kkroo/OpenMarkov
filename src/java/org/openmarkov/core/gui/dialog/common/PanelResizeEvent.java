package org.openmarkov.core.gui.dialog.common;

import java.awt.Dimension;

import javax.swing.JPanel;

public class PanelResizeEvent {
    private JPanel source;
    private Dimension newDimension;
    
    public PanelResizeEvent(JPanel source, Dimension newDimension) {
        super();
        this.source = source;
        this.newDimension = newDimension;
    }

    public JPanel getSource() {
        return source;
    }

    public Dimension getNewDimension() {
        return newDimension;
    }
    
}
