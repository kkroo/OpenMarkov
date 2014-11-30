/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;
import org.openmarkov.core.gui.menutoolbar.common.ZoomMenuToolBar;
import org.openmarkov.core.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.core.gui.menutoolbar.menu.MainMenu;
import org.openmarkov.core.gui.menutoolbar.plugin.ToolbarManager;
import org.openmarkov.core.gui.menutoolbar.toolbar.EditionToolBar;
import org.openmarkov.core.gui.menutoolbar.toolbar.InferenceToolBar;
import org.openmarkov.core.gui.menutoolbar.toolbar.LookAheadToolBar;
import org.openmarkov.core.gui.menutoolbar.toolbar.StandardToolBar;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.gui.window.mdi.MDI;
import org.openmarkov.core.gui.window.message.MessageWindow;


/**
 * This is the main panel of the OpenMarkov interface. It contains all the menu
 * items, toolbars, listeners, etc. and manages all the network frames of
 * OepnMarkov.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Add getMainFrame()
 * @version 1.2 asaez	Added InferenceToolBar. Layout changed for having the
 * 						main and the secondary toolbar in the same line
 */
public class MainPanel extends JPanel {

	private static final long serialVersionUID = -7852474978327911654L;

	/**
	 * Object that manages the MultiDocument Interface.
	 */
	private MDI mdi = null;

	/**
	 * Main menu.
	 */
	private MainMenu mainMenu = null;

	/**
	 * Message window.
	 */
	private MessageWindow messageWindow = null;

	/**
	 * Panel that contains the toolbars.
	 */
	private JPanel toolBarPanel = null;

	/**
	 * Standard toolbar.
	 */
	private StandardToolBar standardToolBar = null;

	/**
	 * Edition toolbar.
	 */
	private EditionToolBar editionToolBar = null;

	/**
	 * Inference toolbar.
	 */
	private InferenceToolBar inferenceToolBar = null;
	
	/**
	 * Look Ahead toolbar 
	 */
	private LookAheadToolBar lookAheadToolBar = null;
	
	/**
	 * Object that supplies the contextual menus.
	 */
	private ContextualMenuFactory contextualMenuFactory = null;

	/**
	 * Object that assists this in the management of the menus and toolbars.
	 */
	private MainPanelMenuAssistant mainPanelMenuAssistant = null;

	/**
	 * Object that listens and manages the user's actions on the menus, contextual
	 * menus and toolbars. This object also listens and manages the mdi events.
	 */
	private MainPanelListenerAssistant mainPanelListenerAssistant = null;

	/**
	 * Object that allows to access some private methods for this.
	 */
	private static MainPanel mainPanel = null;
	
	/**
	 * The frame where this panel belongs to.
	 */
	private JFrame mainFrame = null;

	private ToolbarManager toolbarManager;

	/**
	 * Creates a new instance with a clear declared parent.
	 * 
	 * @param parentFrame
	 *            the parent Frame of the Main Panel
	 */
	public MainPanel(JFrame parentFrame) {

		mainPanel = this;
		mainPanel.setName("MainPanel");
		mainFrame = parentFrame;
		mainFrame.setName(parentFrame.getName());
        toolbarManager = new ToolbarManager (this);

        initialize();
	}

	/**
	 * Singleton pattern
	 * 
	 * @param parentFrame
	 *            is the parent Frame for this Main Panel
	 * @return mainPanel. <code>MainPanel</code>
	 */
	public static MainPanel getUniqueInstance(JFrame parentFrame) {

		if (mainPanel == null) {
			new MainPanel(parentFrame);
		}

		return mainPanel;

	}

	/**
	 * Singleton pattern
	 * 
	 * @return mainPanel. <code>MainPanel</code>
	 */
	public static MainPanel getUniqueInstance() {

		return getUniqueInstance(null);

	}

	/**
	 * This method initialises this instance, changing the default values and
	 * assigning the listeners of the window.
	 */
	private void initialize() {

		getMainPanelListenerAssistant();
		getMainMenu();
		getContextualMenuFactory();
		setLayout(new BorderLayout());
		setSize(new Dimension(500, 500));
		add(getToolBarPanel(), BorderLayout.NORTH);
		getMainPanelMenuAssistant();
		/*JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		splitPane.setDividerLocation(770);
		splitPane.setTopComponent(getMdi());
		splitPane.setBottomComponent(getPropertiesScrollPanel());
		
		splitPane.setOneTouchExpandable(true);*/
		
		add(getMdi(), BorderLayout.CENTER);
		//add(splitPane);//, BorderLayout.CENTER);
		//ClipboardManager.addClipboardListener(getMainPanelMenuAssistant());
		//add(getMessageWindow(), BorderLayout.SOUTH);

	}
	

	/**
	 * When this panel is added to a container, it tries to set the menubar of
	 * its top level ancestor that must be an instance of the classes JFrame or
	 * JApplet.
	 */
	@Override
	public void addNotify() {

		Component container = null;
		JFrame frame = null;

		super.addNotify();
		container = getTopLevelAncestor();
		if (container != null) {
			if (container instanceof JFrame) {
				frame = (JFrame) container;
				frame.setJMenuBar(getMainMenu());
				frame.addWindowListener(mainPanelListenerAssistant);
			} else if (container instanceof JApplet) {
				((JApplet) container).setJMenuBar(getMainMenu());
			}
		}

	}

	/**
	 * This method initialises messageWindow.
	 * 
	 * @return a new message window.
	 */
	public MessageWindow getMessageWindow() {

		if (messageWindow == null) {
			messageWindow = new MessageWindow(mainFrame);
			messageWindow.setVisible(true);
		}

		return messageWindow;

	}

	/**
	 * This method initialises mainMenu.
	 * 
	 * @return a new menubar.
	 */
	public MainMenu getMainMenu() {

		if (mainMenu == null) {
			mainMenu = new MainMenu(mainPanelListenerAssistant);
		}

		return mainMenu;

	}

	/**
	 * This method initialises contextualMenuFactory.
	 * 
	 * @return a new contextual menu factory.
	 */
	ContextualMenuFactory getContextualMenuFactory() {

		if (contextualMenuFactory == null) {
			contextualMenuFactory = new ContextualMenuFactory(mainPanelListenerAssistant);
		}

		return contextualMenuFactory;

	}

	/**
	 * This method initialises toolBarPanel.
	 * 
	 * @return a new toolbar panel.
	 */
	public JPanel getToolBarPanel() {

		if (toolBarPanel == null) {
			toolBarPanel = new JPanel();
				/* This way, the main toolbar and the secondary are in different lines 
				toolBarPanel.setLayout(new BoxLayout(getToolBarPanel(),
					BoxLayout.Y_AXIS));
				toolBarPanel.add(getStandardToolBar());
				toolBarPanel.add(getEditionToolBar());
				*/
			// This way, the main toolbar and the secondary are in the same line
			toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			toolBarPanel.add(getStandardToolBar());
			toolBarPanel.add(getEditionToolBar());
			toolBarPanel.add(getLookAheadToolBar());
		}

		return toolBarPanel;	

	}
	
	/**
	 * This method establishes the type of tool bar (Edition or Inference) to be
	 * set in the panel.
	 * 
	 * @param barType
	 *            new type of tool bar to be set in the panel
	 */
	protected void setToolBarPanel (int barType) {
		if (barType == NetworkPanel.EDITION_WORKING_MODE) {
			mainPanel.getToolBarPanel().remove(mainPanel.getInferenceToolBar());
			mainPanel.getToolBarPanel().add(mainPanel.getEditionToolBar(), 1);
		} else {
			mainPanel.getToolBarPanel().remove(mainPanel.getEditionToolBar());
			mainPanel.getInferenceToolBar().setExpansionThreshold(getMainPanelListenerAssistant().
					getCurrentNetworkPanel().getExpansionThreshold());
			mainPanel.getToolBarPanel().add(mainPanel.getInferenceToolBar(), 1);
		}
		mainPanel.initialize();
		
	}
	
	/**
	 * This method sets the button for switching between Edition/inference to
	 * the pertinent value (pressed or not)
	 * 
	 * @param workingMode
	 *            the working mode of the currently selected NetworkPanel. 
	 *            Depending on this value, the button will be set pressed or not.
	 */
	public void changeWorkingModeButton(int workingMode){
		getStandardToolBar().changeWorkingModeButton(workingMode);		
	}

	/**
	 * This method initialises mainMDI.
	 * 
	 * @return a new MDI panel.
	 */
	public MDI getMdi() {

		if (mdi == null) {
			mdi = new MDI(mainMenu.getMenuMDI());
			mdi.addFrameStateListener(mainPanelListenerAssistant);
			mdi.setPreferredSize(new Dimension(400,600));
			mdi.createNewFrame(getMessageWindow(), false);
		}

		return mdi;

	}

	/**
	 * This method initialises standardToolBar.
	 * 
	 * @return a new standard toolbar.
	 */
	public StandardToolBar getStandardToolBar() {

		if (standardToolBar == null) {
			standardToolBar = new StandardToolBar(mainPanelListenerAssistant);
		}

		return standardToolBar;

	}

	/**
	 * This method initialises editionToolBar.
	 * 
	 * @return a new edition toolbar.
	 */
	public EditionToolBar getEditionToolBar() {

		if (editionToolBar == null) {
			editionToolBar = new EditionToolBar(mainPanelListenerAssistant);
		}

		return editionToolBar;

	}
	
	/**
	 * This method initialized lookaheadToolBar
	 */
	public LookAheadToolBar getLookAheadToolBar() {
		
		if (lookAheadToolBar == null) {
			lookAheadToolBar = new LookAheadToolBar(mainPanelListenerAssistant); 
		}
		
		return lookAheadToolBar;
	}
	
	/**
	 * This method initialises and returns the inferenceToolBar.
	 * 
	 * @return a new inference toolbar.
	 */
	public InferenceToolBar getInferenceToolBar() {
		if (inferenceToolBar == null) {
			inferenceToolBar = new InferenceToolBar(mainPanelListenerAssistant);
		}
		return inferenceToolBar;
	}


	/**
	 * This method initialises menuAssistant.
	 * 
	 * @return a new menu assistant.
	 */
	public MainPanelMenuAssistant getMainPanelMenuAssistant() {

		if (mainPanelMenuAssistant == null) {
			mainPanelMenuAssistant =
				new MainPanelMenuAssistant(new MenuToolBarBasic[] { mainMenu,
					standardToolBar, editionToolBar, getInferenceToolBar(), contextualMenuFactory },
					new ZoomMenuToolBar[] { mainMenu, standardToolBar }, this);
			mainPanelMenuAssistant.updateOptionsAllNetworkClosed();
		}

		return mainPanelMenuAssistant;

	}

	/**
	 * This method initialises mainPanelListenerAssistant.
	 * 
	 * @return a new main panel listener assistant.
	 */
	public MainPanelListenerAssistant getMainPanelListenerAssistant() {

		if (mainPanelListenerAssistant == null) {
			mainPanelListenerAssistant = new MainPanelListenerAssistant(this.mainPanel);
		}

		return mainPanelListenerAssistant;

	}

	/**
	 * @return the mainFrame
	 */
	public JFrame getMainFrame() {

		return mainFrame;
	}
	
	/**
	 * Opens a prob net
	 * @param fileName
	 */
	public void openNetwork(String fileName)
	{
	    getMainPanelListenerAssistant().openNetwork (fileName);
	}

	/**
	 * Returns instance of toolbarManager
	 * @return
	 */
    public ToolbarManager getToolbarManager ()
    {
        return toolbarManager;
    }
}
