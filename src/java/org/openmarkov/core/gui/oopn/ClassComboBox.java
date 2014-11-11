/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.oopn;


import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.gui.window.mdi.FrameContentPanel;
import org.openmarkov.core.gui.window.mdi.MDIListener;


/**
 * This class fills its combobox and listen to it to send action commands
 * defined in the class ActionCommands.
 * 
 * @author ibermejo
 */
public class ClassComboBox extends JComboBox<String> implements MDIListener {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 5380198895714343936L;

	/**
	 * Object that listen to the user's actions.
	 */
	private ActionListener listener;
	
	/**
	 *  Map of class names
	 */
	Map<String, String> classNames;
	
	String currentClassName;
	
	
	/**
	 * Constructor that fills and initialize the combobox.
	 * 
	 * @param newListener
	 *            object that listens to the zoom values.
	 */
	public ClassComboBox(ActionListener newListener) {

		super();
		listener = newListener;
		classNames = new HashMap<>();
		MainPanel mainPanel = MainPanel.getUniqueInstance();
		if(mainPanel.getMainPanelMenuAssistant().getCurrentNetworkPanel() != null)
		{
			currentClassName = getClassName(mainPanel.getMainPanelMenuAssistant().getCurrentNetworkPanel().getTitle());
		}
		for(JInternalFrame frame : mainPanel.getMdi().getFrames ())
		{
		    if(frame.getContentPane () instanceof NetworkPanel)
		    {
		        String className = FilenameUtils.getBaseName (frame.getTitle ());
		    	classNames.put(className.replace ("*", ""), frame.getTitle ());
		    }
		}
		initialize();
	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		setEditable(false);
		setPreferredSize(new Dimension(120, 25));
		setMaximumSize(getPreferredSize());
		setMinimumSize(getPreferredSize());
		updateComboBoxData(classNames, currentClassName);
		MainPanel.getUniqueInstance().getMdi().addFrameStateListener(this);
	}

//	/**
//	 * Invoked when an item has been selected.
//	 * 
//	 * @param e
//	 *            event information.
//	 */
//	public void selectedItemChanged(ItemEvent e) {
//		//TODO implement
//		listener.actionPerformed(new MenuActionEvent(this, 0, newActionCommand, this.selectedItemChanged());
//	}


	/**
	 * Enables the combo box so that items can be selected. When the combo box
	 * is disabled, items cannot be selected, values cannot be typed into its
	 * field and no elements are selected.
	 * 
	 * @param b
	 *            true enables the combobox and false disables it.
	 */
	@Override
	public void setEnabled(boolean b) {

		if (!b) {
			setSelectedIndex(-1);
		}
		super.setEnabled(b);
	}

	public void frameClosed(FrameContentPanel contentPanel) {
		// Remove from list
		classNames.remove(getClassName(contentPanel.getTitle()));
		updateComboBoxData(classNames, currentClassName);
	}
	
	public void frameSelected(FrameContentPanel contentPanel) {
		currentClassName = getClassName(contentPanel.getTitle());
		updateComboBoxData(classNames, currentClassName);
	}

    public void frameTitleChanged(FrameContentPanel contentPanel, String oldTitle, String newTitle) {
		String oldClassName = getClassName (oldTitle);
        String newClassName = getClassName (newTitle);
        if(oldClassName.equals(currentClassName))
		{
			currentClassName = newClassName;
		}
		classNames.remove(oldClassName);
        classNames.put(newClassName, newTitle);
		updateComboBoxData(classNames, currentClassName);
		this.setSelectedItem (newTitle);
	}

	public boolean frameClosing(FrameContentPanel contentPanel) {
		// Do nothing
		return true;
	}

	public void frameOpened(FrameContentPanel contentPanel) {
		//No need to add here as setTitle adds it before reaching here
		//this.addItem(contentPanel.getTitle());
	}
	
	private void updateComboBoxData(Map<String, String> classNames, String currentClassName)
	{
		List<String> showableClassNames = new ArrayList<>(classNames.keySet ());
		if(currentClassName != null)
		{
		    showableClassNames.remove(currentClassName);
		}
		updateComboBoxData(showableClassNames);
	}
	
	private void updateComboBoxData(List<String> classNames)
	{
		this.removeAllItems();
		for(String className : classNames)
		{
	        this.addItem(className);
		}
	}

    private String getClassName (String title)
    {
        return FilenameUtils.getBaseName (title).replace ("*", "");
    }

    public String getSelectedClassFrameTitle ()
    {
        return classNames.get (getSelectedItem ());
    }
}
