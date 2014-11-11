package org.openmarkov.core.gui.window.edition;

import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class ScrollableEditorPanel extends JScrollPane
{
    public ScrollableEditorPanel(EditorPanel editorPanel)
    {
        setViewportView (editorPanel);
        getVerticalScrollBar ().setUnitIncrement (25);
    }
}
