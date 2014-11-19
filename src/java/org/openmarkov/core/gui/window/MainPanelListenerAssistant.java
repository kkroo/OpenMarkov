/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.exception.CanNotWriteNetworkToFileException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NotRecognisedNetworkFileExtensionException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.gui.configuration.LastOpenFiles;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.costeffectiveness.CostEffectivenessAnalysis;
import org.openmarkov.core.gui.costeffectiveness.CostEffectivenessDialog;
import org.openmarkov.core.gui.costeffectiveness.CostEffectivenessProgressBar;
import org.openmarkov.core.gui.costeffectiveness.CostEffectivenessResultsDialog;
import org.openmarkov.core.gui.dialog.AboutBox;
import org.openmarkov.core.gui.dialog.HelpViewer;
import org.openmarkov.core.gui.dialog.LanguageDialog;
import org.openmarkov.core.gui.dialog.SelectZoomDialog;
import org.openmarkov.core.gui.dialog.configuration.PreferencesDialog;
import org.openmarkov.core.gui.dialog.io.DBReaderFileChooser;
import org.openmarkov.core.gui.dialog.io.FileChooser;
import org.openmarkov.core.gui.dialog.io.FileFilterBasic;
import org.openmarkov.core.gui.dialog.io.NetsIO;
import org.openmarkov.core.gui.dialog.io.NetworkFileChooser;
import org.openmarkov.core.gui.dialog.io.SaveOptions;
import org.openmarkov.core.gui.dialog.network.NetworkPropertiesDialog;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.plugin.ToolPluginManager;
import org.openmarkov.core.gui.util.PropertyNames;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.gui.window.dt.DecisionTreeWindow;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.gui.window.mdi.FrameContentPanel;
import org.openmarkov.core.gui.window.mdi.MDIListener;
import org.openmarkov.core.gui.window.message.MessageWindow;
import org.openmarkov.core.inference.MPADFactory;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.database.CaseDatabase;
import org.openmarkov.core.io.database.CaseDatabaseReader;
import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.oopn.Instance.ParameterArity;
import org.openmarkov.core.oopn.OOPNet;

/**
 * This class receives the main events of the application and helps the class
 * MainMenu to carry out this task.
 * 
 * @author jmendoza
 * @version 1.0 - jmendoza - initial version
 * @version 1.1 - jlgozalo - Modify/Add modifiers to methods and create
 *          zoomChangeValue variable
 * @version 1.2 - jlgozalo - Modify the OpenNetwork to set the name of the file
 *          to the Network and add AboutBox and Help actions
 * @version 1.3 - jlgozalo - Store the last open directory and file in the user
 *          Preferences
 * @version 1.4 - jlgozalo - remove calls to System.out and System.err replacing
 *          by calls to MessageWindow streams
 * @version 1.5 - asaez - Functionality added: Treatment of events related to -
 *          Explanation capabilities, - Management of working modes
 *          (edition/inference), - Expansion and contraction of nodes, -
 *          Introduction and elimination of evidence - Management of multiple
 *          evidence cases.
 */
public class MainPanelListenerAssistant extends WindowAdapter implements ActionListener,
        MDIListener, PropertyNames {
    /**
     * Main panel which this object helps.
     */
    private MainPanel           mainPanel       = null;
    /**
     * last open files instance
     */
    private LastOpenFiles       lastOpenFiles   = new LastOpenFiles();
    /**
     * Messages string resource.
     */
    // private StringResource stringResource;
    private List<NetworkPanel>  networkPanels;
    /**
     * Counter incremented each time a network frame is created.
     */
    private static int          frameIndex      = 1;
    /**
     * Value for the Zoom increment/decrement
     */
    private static final double zoomChangeValue = 0.2;

    private StringDatabase      stringDatabase  = null;

    /**
     * Constructor that save the references to the objects that this class
     * needs.
     * 
     * @param mainPanel
     *            - main panel which this listener helps.
     */
    public MainPanelListenerAssistant(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        this.mainPanel.setName(mainPanel.getName());
        // stringResource = StringResourceLoader.getUniqueInstance()
        // .getBundleMessages();
        this.networkPanels = new ArrayList<NetworkPanel>();
        this.stringDatabase = StringDatabase.getUniqueInstance();
    }

    /**
     * Invoked when a window is in the process of being closed.
     * 
     * @param e
     *            - event information.
     */
    @Override
    public void windowClosing(WindowEvent e) {
        closeApplication();
    }

    /**
     * This method listens to the user actions on the main menu.
     * 
     * @param e
     *            menu event information.
     */
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals(ActionCommands.NEW_NETWORK)) {
            createNewNetwork();
        } else if (actionCommand.equals(ActionCommands.OPEN_NETWORK)) {
            openNetwork();
        } else if (actionCommand.equals(ActionCommands.OPEN_LAST_1_FILE)) {
            openNetwork(lastOpenFiles.getFileNameAt(1));
        } else if (actionCommand.equals(ActionCommands.OPEN_LAST_2_FILE)) {
            openNetwork(lastOpenFiles.getFileNameAt(2));
        } else if (actionCommand.equals(ActionCommands.OPEN_LAST_3_FILE)) {
            openNetwork(lastOpenFiles.getFileNameAt(3));
        } else if (actionCommand.equals(ActionCommands.OPEN_LAST_4_FILE)) {
            openNetwork(lastOpenFiles.getFileNameAt(4));
        } else if (actionCommand.equals(ActionCommands.OPEN_LAST_5_FILE)) {
            openNetwork(lastOpenFiles.getFileNameAt(5));
        } else if (actionCommand.equals(ActionCommands.SAVE_NETWORK)) {
            saveNetwork(getCurrentNetworkPanel());
        } else if (actionCommand.equals(ActionCommands.SAVE_OPEN_NETWORK)) {
            saveOpenNetwork(getCurrentNetworkPanel());
        } else if (actionCommand.equals(ActionCommands.SAVEAS_NETWORK)) {
            saveNetworkAs(getCurrentNetworkPanel());
        } else if (actionCommand.equals(ActionCommands.CLOSE_NETWORK)) {
            closeCurrentNetwork();
        } else if (actionCommand.equals(ActionCommands.LOAD_EVIDENCE)) {
            loadEvidence(getCurrentNetworkPanel());
        } else if (actionCommand.equals(ActionCommands.SAVE_EVIDENCE)) {
            saveEvidence(getCurrentNetworkPanel());
        } else if (actionCommand.equals(ActionCommands.NETWORK_PROPERTIES)) {
            getCurrentNetworkPanel().changeNetworkProperties();
        } else if (actionCommand.equals(ActionCommands.EXPAND_NETWORK)) {
            expandNetwork(getCurrentNetworkPanel().getProbNet(),
                    getCurrentNetworkPanel().getEditorPanel().getPreResolutionEvidence());
        } else if (actionCommand.equals(ActionCommands.EXPAND_NETWORK_CE)) {
            expandNetworkCE(getCurrentNetworkPanel().getProbNet(),
                    getCurrentNetworkPanel().getEditorPanel().getPreResolutionEvidence());
        } else if (actionCommand.equals(ActionCommands.EXIT_APPLICATION)) {
            closeApplication();
        } else if (actionCommand.equals(ActionCommands.CLIPBOARD_COPY)) {
            getCurrentNetworkPanel().exportToClipboard(false);
            mainPanel.getMainPanelMenuAssistant().setOptionEnabled(ActionCommands.CLIPBOARD_PASTE,
                    true);
        } else if (actionCommand.equals(ActionCommands.CLIPBOARD_CUT)) {
            getCurrentNetworkPanel().exportToClipboard(true);
            mainPanel.getMainPanelMenuAssistant().setOptionEnabled(ActionCommands.CLIPBOARD_PASTE,
                    true);
        } else if (actionCommand.equals(ActionCommands.CLIPBOARD_PASTE)) {
            getCurrentNetworkPanel().pasteFromClipboard();
        } else if (actionCommand.equals(ActionCommands.UNDO)) {
            undo();
        } else if (actionCommand.equals(ActionCommands.REDO)) {
            redo();
        } else if (actionCommand.equals(ActionCommands.SELECT_ALL)) {
            getCurrentNetworkPanel().selectAllObjects();
        } else if (actionCommand.equals(ActionCommands.OBJECT_REMOVAL)) {
            getCurrentNetworkPanel().removeSelectedObjects();
        } else if (actionCommand.startsWith(ActionCommands.EDITION_MODE_PREFIX)) {
            activateEditionMode(actionCommand);
        } else if (actionCommand.equals(ActionCommands.CHANGE_WORKING_MODE)) {
            setNewWorkingMode();
        } else if (actionCommand.equals(ActionCommands.CHANGE_TO_INFERENCE_MODE)) {
            setNewWorkingMode();
        } else if (actionCommand.equals(ActionCommands.CHANGE_TO_EDITION_MODE)) {
            setNewWorkingMode();
        } else if (actionCommand.equals(ActionCommands.SET_NEW_EXPANSION_THRESHOLD)) {
            setNewExpansionThreshold((Double) e.getSource());
        } else if (actionCommand.equals(ActionCommands.CREATE_NEW_EVIDENCE_CASE)) {
            evidenceCasesNavigationOption("CREATE_NEW_EVIDENCE_CASE");
        } else if (actionCommand.equals(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE)) {
            evidenceCasesNavigationOption("GO_TO_FIRST_EVIDENCE_CASE");
        } else if (actionCommand.equals(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE)) {
            evidenceCasesNavigationOption("GO_TO_PREVIOUS_EVIDENCE_CASE");
        } else if (actionCommand.equals(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE)) {
            evidenceCasesNavigationOption("GO_TO_NEXT_EVIDENCE_CASE");
        } else if (actionCommand.equals(ActionCommands.GO_TO_LAST_EVIDENCE_CASE)) {
            evidenceCasesNavigationOption("GO_TO_LAST_EVIDENCE_CASE");
        } else if (actionCommand.equals(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES)) {
            evidenceCasesNavigationOption("CLEAR_OUT_ALL_EVIDENCE_CASES");
        } else if (actionCommand.equals(ActionCommands.PROPAGATE_EVIDENCE)) {
            getCurrentNetworkPanel().propagateEvidence(mainPanel.getMainPanelMenuAssistant());
        } else if (actionCommand.equals(ActionCommands.NODE_PROPERTIES)) {
            getCurrentNetworkPanel().changeNodeProperties();
        } else if (actionCommand.equals(ActionCommands.EDIT_POTENTIAL)) {
            getCurrentNetworkPanel().changePotential();
        } else if (actionCommand.equals(ActionCommands.DECISION_IMPOSE_POLICY)) {
            getCurrentNetworkPanel().imposePolicyInNode();
        } else if (actionCommand.equals(ActionCommands.DECISION_EDIT_POLICY)) {
            getCurrentNetworkPanel().editNodePolicy();
        } else if (actionCommand.equals(ActionCommands.DECISION_REMOVE_POLICY)) {
            getCurrentNetworkPanel().removePolicyFromNode();
        } else if (actionCommand.equals(ActionCommands.DECISION_SHOW_EXPECTED_UTILITY)) {
            getCurrentNetworkPanel().showExpectedUtilityOfNode();
        } else if (actionCommand.equals(ActionCommands.DECISION_SHOW_OPTIMAL_POLICY)) {
            getCurrentNetworkPanel().showOptimalPolicyOfNode();
        } else if (actionCommand.equals(ActionCommands.NODE_EXPANSION)) {
            getCurrentNetworkPanel().expandNode();
        } else if (actionCommand.equals(ActionCommands.NODE_CONTRACTION)) {
            getCurrentNetworkPanel().contractNode();
        } else if (actionCommand.equals(ActionCommands.NODE_ADD_FINDING)) {
            getCurrentNetworkPanel().addFinding();
        } else if (actionCommand.equals(ActionCommands.NODE_REMOVE_FINDING)) {
            getCurrentNetworkPanel().removeFinding();
        } else if (actionCommand.equals(ActionCommands.NODE_REMOVE_ALL_FINDINGS)) {
            getCurrentNetworkPanel().removeAllFindings();
        } else if (actionCommand.equals(ActionCommands.BYTITLE_NODES)) {
            activateByTitle(true);
        } else if (actionCommand.equals(ActionCommands.BYNAME_NODES)) {
            activateByTitle(false);
        } else if (actionCommand.startsWith(ActionCommands.VIEW_TOOLBARS)) {
            MainPanel.getUniqueInstance().getToolbarManager().addToolbar(actionCommand.replace(ActionCommands.VIEW_TOOLBARS
                    + ".",
                    ""));
        } else if (actionCommand.equals(ActionCommands.ZOOM_IN)) {
            incrementZoom(getCurrentPanel());
        } else if (actionCommand.equals(ActionCommands.ZOOM_OUT)) {
            decrementZoom(getCurrentPanel());
        } else if (actionCommand.equals(ActionCommands.ZOOM_OTHER)) {
            setZoom(true, getCurrentPanel(), 0);
        } else if (ActionCommands.isZoomActionCommand(actionCommand)) {
            setZoom(false,
                    getCurrentPanel(),
                    ActionCommands.getValueZoomActionCommand(actionCommand));
        } else if (actionCommand.equals(ActionCommands.MESSAGE_WINDOW)) {
            showMessageWindow();
        } else if (actionCommand.equals(ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC)) {
            // Deterministic
            showCostEffectivenessDialog(getCurrentNetworkPanel().getProbNet(),
                    getCurrentNetworkPanel().getEditorPanel().getPreResolutionEvidence(),
                    false);
        } else if (actionCommand.equals(ActionCommands.SENSITIVITY_ANALYSIS)) {
            showCostEffectivenessDialog(getCurrentNetworkPanel().getProbNet(),
                    getCurrentNetworkPanel().getEditorPanel().getPreResolutionEvidence(),
                    true);
        } else if (actionCommand.equals(ActionCommands.CONFIGURATION)) {
            showUserConfigurationDialog();
        } else if (actionCommand.equals(ActionCommands.INFERENCE_OPTIONS)) {
            setInferenceOptions();
        } else if (actionCommand.equals(ActionCommands.HELP_CHANGE_LANGUAGE)) {
            showLanguageChangeDialog();
        } else if (actionCommand.equals(ActionCommands.HELP_HELP)) {
            showHelp();
        } else if (actionCommand.equals(ActionCommands.HELP_ABOUT)) {
            showAbout();
        } else if (actionCommand.equals(ActionCommands.LINK_RESTRICTION_ENABLE_PROPERTIES)) {
            this.getCurrentNetworkPanel().enableLinkRestriction();
        } else if (actionCommand.equals(ActionCommands.LINK_RESTRICTION_EDIT_PROPERTIES)) {
            this.getCurrentNetworkPanel().enableLinkRestriction();
        } else if (actionCommand.equals(ActionCommands.LINK_RESTRICTION_DISABLE_PROPERTIES)) {
            this.getCurrentNetworkPanel().disableLinkRestriction();
        } else if (actionCommand.equals(ActionCommands.LINK_REVELATIONARC_PROPERTIES)) {
            this.getCurrentNetworkPanel().enableRevelationArc();
            // TODO OOPN start
        } else if (actionCommand.equals(ActionCommands.TEMPORAL_EVOLUTION_ACTION)) {
            this.getCurrentNetworkPanel().temporalEvolution();
        } else if (actionCommand.equals(ActionCommands.MARK_AS_INPUT)) {
            this.getCurrentNetworkPanel().markSelectedAsInput();
        } else if (actionCommand.equals(ActionCommands.EDIT_CLASS)) {
            this.getCurrentNetworkPanel().editClass();
        } else if (actionCommand.equals(ActionCommands.SET_ARITY_ONE)) {
            this.getCurrentNetworkPanel().setParameterArity(ParameterArity.ONE);
        } else if (actionCommand.equals(ActionCommands.SET_ARITY_MANY)) {
            this.getCurrentNetworkPanel().setParameterArity(ParameterArity.MANY);
            // TODO OOPN end
        } else if (actionCommand.equals(ActionCommands.DECISION_TREE)) {
            toggleDecisionTree(this.getCurrentNetworkPanel().getProbNet());
        } else if (actionCommand.equals(ActionCommands.NEXT_SLICE_NODE)) {
            this.getCurrentNetworkPanel().createNextSliceNode();
        } else {
            ToolPluginManager.getInstance().processCommand(actionCommand, mainPanel.getMainFrame());
        }
    }

    /**
     * Create a Java Help viewer
     * 
     * @return helpViewer a window to display help
     */
    private static HelpViewer showHelp() {
        return HelpViewer.getUniqueInstance();
    }

    /**
     * Create a Frame for a Change Language dialog
     * 
     * @return a change language dialog to allow language change
     */
    private void showLanguageChangeDialog() {
        LanguageDialog.getUniqueInstance(mainPanel.getMainFrame()).setVisible(true);
    }

    /**
     * Create a Frame for the User Configuration dialog
     * 
     * @return a UserConfiguration dialog
     */
    private PreferencesDialog showUserConfigurationDialog() {
        return new PreferencesDialog(mainPanel.getMainFrame());
    }

    /**
     * Create a Frame for About information
     * 
     * @return aboutBox the AboutBox dialog
     */
    private AboutBox showAbout() {
        return new AboutBox(mainPanel.getMainFrame());
    }

    /**
     * Create an instance of <code>openmarkov.learning.gui.NewLearningGUI</code>
     * 
     * @return LearningGUI
     */
    /*
     * private LearningGUI learning() { //return
     * LearningGUI.getUniqueInstance(mainPanel.getMainFrame()); }
     */
    /**
     * Returns the current network panel of the current frame.
     * 
     * @return the current network panel.
     */
    public NetworkPanel getCurrentNetworkPanel() {
        return mainPanel.getMainPanelMenuAssistant().getCurrentNetworkPanel();
    }

    /**
     * Returns the current network panel of the current frame.
     * 
     * @return the current network panel.
     */
    public FrameContentPanel getCurrentPanel() {
        return mainPanel.getMdi().getCurrentPanel();
    }

    /**
     * Returns a value indicating if the network can be closed. If the network
     * has not been saved, this method offers to the users the possibility of
     * save it. If the user answers 'yes', the network is saved and can be
     * closed. If the user answers 'no', the network isn't saved and can be
     * closed. If the user answers 'cancel', the network can't be closed.
     * 
     * @param networkPanel
     *            network panel to be checked.
     * @return true, if the network can be closed; otherwise, false.
     */
    private boolean networkCanBeClosed(NetworkPanel networkPanel) {
        int response = 0;
        boolean canClose = true;
        if (networkPanel.getModified()) {
            String title = stringDatabase.getFormattedString("NetworkNotSaved.Title.Label",
                    networkPanel.getTitle());
            String message = stringDatabase.getFormattedString("NetworkNotSaved.Text.Label",
                    networkPanel.getTitle());
            response = JOptionPane.showConfirmDialog(Utilities.getOwner(mainPanel),
                    message,
                    title,
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            switch (response) {
            case JOptionPane.YES_OPTION: {
                canClose = saveNetwork(networkPanel);
                break;
            }
            case JOptionPane.NO_OPTION: {
                canClose = true;
                break;
            }
            default: {
                return false;
            }
            }
        }
        if (canClose) {
            networkPanels.remove(networkPanel);
        }
        return canClose;
    }

    /**
     * This method executes when a network frame is going to be closed.
     * 
     * @param contentPanel
     *            content panel of the frame that is trying to be closed.
     * @return true, if the frame that contents the panel can be closed;
     *         otherwise, false.
     */
    public boolean frameClosing(FrameContentPanel contentPanel) {
        contentPanel.close();
        if (NetworkPanel.class.isAssignableFrom(contentPanel.getClass())) {
            return networkCanBeClosed((NetworkPanel) contentPanel);
        } else {
            return true;
        }
    }

    /**
     * This method executes when a frame has been closed.
     * 
     * @param contentPanel
     *            content panel of the frame that has been closed.
     */
    public void frameClosed(FrameContentPanel contentPanel) {
        if (networkPanels.size() == 0) {
            mainPanel.setToolBarPanel(NetworkPanel.EDITION_WORKING_MODE);
            mainPanel.getMainPanelMenuAssistant().updateOptionsAllNetworkClosed();
        }
    }

    /**
     * This method executes when a network frame has been selected.
     * 
     * @param contentPanel
     *            content panel of the frame that has been selected.
     */
    public void frameSelected(FrameContentPanel contentPanel) {
        if (contentPanel instanceof NetworkPanel) {
            mainPanel.getMainPanelMenuAssistant().updateOptionsNetworkDependent((NetworkPanel) contentPanel);
            mainPanel.getInferenceToolBar().setCurrentEvidenceCaseName(getCurrentNetworkPanel().getCurrentCase());
            mainPanel.getMainPanelMenuAssistant().updateOptionsWindowSelected(true);
        } else if (contentPanel instanceof MessageWindow) {
            mainPanel.getMainPanelMenuAssistant().updateOptionsWindowSelected(false);
        } else if (contentPanel instanceof DecisionTreeWindow) {
            mainPanel.getMainPanelMenuAssistant().updateOptionsDecisionTree((DecisionTreeWindow) contentPanel);
        }
    }

    /**
     * Saves a network in a file and makes the rest of actions in the
     * environment (menus, messages, etc.).
     * 
     * @param networkPanel
     *            network panel which contains the network to be saved.
     * @param fileName
     *            file where save the network.
     * @param saveOptions
     * @return true if the network could be saved; otherwise, false.
     */
    private boolean saveNetworkActions(NetworkPanel networkPanel,
            String fileName,
            SaveOptions saveOptions) {
        boolean result = false;
        mainPanel.getMessageWindow().getNormalMessageStream().println(stringDatabase.getString("SavingNetwork.Text.Label")
                + " "
                + fileName);
        try {
            if (saveOptions != null && saveOptions.isSavePlainNetwork()) {
                networkPanel.showPlainNetwork();
            }
            if (saveOptions != null
                    && saveOptions.isSaveClassesInFile()
                    && networkPanel.getProbNet() instanceof OOPNet) {
                ((OOPNet) networkPanel.getProbNet()).fillClassList();
            }
            NetsIO.saveNetworkFile(networkPanel.getProbNet(),
                    networkPanel.getEditorPanel().getEvidence(),
                    fileName);
            // networkPanel.getNetwork().backupProbNet.saveToFile( fileName );
            networkPanel.setModified(false);
            networkPanel.setNetworkFile(fileName);
            mainPanel.getMainPanelMenuAssistant().updateOptionsNetworkSaved();
            lastOpenFiles.setLastFileName(fileName);
            OpenMarkovPreferences.set(OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
                    getDirectoryFileName(fileName),
                    OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
            mainPanel.getMessageWindow().getNormalMessageStream().println(stringDatabase.getString("NetworkSaved.Text.Label"));
            mainPanel.getMainMenu().rechargeLastOpenFiles();
            result = true;
        } catch (NotRecognisedNetworkFileExtensionException e) {
            JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                    stringDatabase.getString("CanNotRecognisedFileExtension.Text.Label"),
                    stringDatabase.getString("ErrorWindow.Title.Label"),
                    JOptionPane.ERROR_MESSAGE);
        } catch (CanNotWriteNetworkToFileException e) {
            JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                    stringDatabase.getString("ErrorSavingNetwork.Text.Label"),
                    stringDatabase.getString("ErrorWindow.Title.Label"),
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                    stringDatabase.getString("Generic I/O error"),
                    stringDatabase.getString("ErrorWindow.Title.Label"),
                    JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    private boolean saveNetworkActions(NetworkPanel networkPanel, String fileName) {
        return saveNetworkActions(networkPanel, fileName, null);
    }

    /**
     * Save a network. First it requests the file in which save the network and
     * then saves the network.
     * 
     * @param networkPanel
     *            network panel that contains the network to be saved.
     * @return true if the network has been saved; otherwise, false.
     */
    private boolean saveNetwork(NetworkPanel networkPanel) {
        String fileName = networkPanel.getNetworkFile();
        return (fileName != null) ? saveNetworkActions(networkPanel, fileName)
                : saveNetworkAs(networkPanel);
    }

    /**
     * Save a network. First it requests the file in which save the network and
     * then saves the network.
     * 
     * @param networkPanel
     *            network panel that contains the network to be saved.
     * @return true if the network has been saved; otherwise, false.
     */
    private void saveOpenNetwork(NetworkPanel networkPanel) {
        String fileName = networkPanel.getNetworkFile();
        if (fileName != null) {
            try {
                File inFile = new File(fileName);
                String newFileName = toBakExtension(networkPanel.getNetworkFile());
                File outFile = new File(newFileName);
                FileInputStream in = new FileInputStream(inFile);
                FileOutputStream out = new FileOutputStream(outFile);
                int c;
                while ((c = in.read()) != -1)
                    out.write(c);
                in.close();
                out.close();
            } catch (IOException e) {
                mainPanel.getMessageWindow().getNormalMessageStream().println(stringDatabase.getString("NetworkBackupError.Text.Label"));
            }
        }
        mainPanel.getMessageWindow().getNormalMessageStream().println(stringDatabase.getString("NetworkBackup.Text.Label"));
        saveNetwork(networkPanel);
        fileName = networkPanel.getNetworkFile();
        closeCurrentNetwork();
        openNetwork(fileName);
    }

    private String toBakExtension(String nameFile) {
        String newName;
        int index = nameFile.lastIndexOf(".");
        if (index > 0) {
            newName = nameFile.substring(0, index);
        } else
            newName = nameFile;
        return newName + ".bak";
    }

    /**
     * Save a network in a different file. First it requests the file in which
     * save the network and then saves the network.
     * 
     * @param networkPanel
     *            network panel that contains the network to be saved.
     * @return true if the network has been saved; otherwise, false.
     */
    private boolean saveNetworkAs(NetworkPanel networkPanel) {
        String fileName = networkPanel.getNetworkFile();
        fileName = requestNetworkFileToSave((fileName != null) ? fileName
                : networkPanel.getProbNet().getName());
        SaveOptions saveOptions = null;
        if (fileName != null) {
            networkPanel.setNetworkFile(fileName);
            networkPanel.getProbNet().setName(new File(fileName).getName());
            MainPanel mainPanel = MainPanel.getUniqueInstance();
            saveOptions = new SaveOptions(null, networkPanel.getProbNet(), true);
            if (saveOptions.isWorthShowing()) {
                saveOptions.setLocation(mainPanel.getLocation().x
                        + (mainPanel.getWidth() - saveOptions.getWidth())
                        / 2,
                        mainPanel.getLocation().y
                                + (mainPanel.getHeight() - saveOptions.getHeight())
                                / 2);
                saveOptions.setVisible(true);
            }
        }
        return (fileName != null) ? saveNetworkActions(networkPanel, fileName, saveOptions) : false;
    }

    /**
     * It asks the user to choose a file by means of a save-file dialog box.
     * 
     * @param suggestedFileName
     *            name of the file where the net can be saved as default.
     * @return complete path of the file, or null if the user selects cancel.
     */
    private String requestNetworkFileToSave(String suggestedFileName) {
        NetworkFileChooser fileChooser = new NetworkFileChooser(false);
        String title = stringDatabase.getString("SaveNetwork.Title.Label");
        fileChooser.setDialogTitle(title);
        fileChooser.setSelectedFile(new File(suggestedFileName));
        String filename = null;
        if (fileChooser.showSaveDialog(Utilities.getOwner(mainPanel)) == JFileChooser.APPROVE_OPTION) {
            filename = fileChooser.getSelectedFile().getAbsolutePath();
            String chosenFilterExtension = ((FileFilterBasic) fileChooser.getFileFilter()).getFilterExtension();
            if (!filename.toLowerCase().endsWith("." + chosenFilterExtension.toLowerCase())) {
                filename += "." + chosenFilterExtension.toLowerCase();
            }
        }
        return filename;
    }

    /**
     * Creates a new network in the workspace. First, it requests the
     * additionalProperties of the new network and, if the user accepts the
     * dialog box, a new network is created.
     * 
     * @wbp.parser.entryPoint
     */
    private void createNewNetwork() {
        NetworkPropertiesDialog dialogProperties = new NetworkPropertiesDialog(Utilities.getOwner(mainPanel));
        if (dialogProperties.showProperties() == NetworkPropertiesDialog.OK_BUTTON) {
            ProbNet probNet = dialogProperties.getProbNet();
            String networkName = new String(stringDatabase.getString("InternalFrame.Title.Label")
                    + " "
                    + frameIndex);
            probNet.setName(networkName);
            probNet.getPNESupport().setWithUndo(true);
            networkPanels.add(createNewFrame(probNet));
            frameIndex++;
            // mainPanelMenuAssistant is added as listener to probNet
            // for menus updated purposes.
            probNet.getPNESupport().addUndoableEditListener(mainPanel.getMainPanelMenuAssistant());
        }
    }

    /**
     * Creates a new frame in the workspace, suppling the network to be painted
     * into the frame.
     * 
     * @param network
     *            network to be painted into the frame
     * @return the network panel that is created.
     */
    public NetworkPanel createNewFrame(ProbNet probNet, CaseDatabase cases) {
        NetworkPanel networkPanel = null;
        try {
        	if (cases != null)
        		networkPanel = new NetworkPanel(probNet, cases, mainPanel);
        	else
        		networkPanel = new NetworkPanel(probNet, mainPanel);
        	
            mainPanel.getMdi().createNewFrame(networkPanel);
            networkPanel.setContextualMenuFactory(mainPanel.getContextualMenuFactory());
            // networkPanel.addEditionListener( mainPanel
            // .getMainPanelMenuAssistant() );
            networkPanel.addSelectionListener(mainPanel.getMainPanelMenuAssistant());
            mainPanel.getMainPanelMenuAssistant().updateOptionsNewNetworkOpen();
            mainPanel.getMainPanelMenuAssistant().updateOptionsNetworkDependent(networkPanel);
            // mainPanel.getMainPanelMenuAssistant().updateNetworkAgents(networkPanel);
            mainPanel.getInferenceToolBar().setCurrentEvidenceCaseName(getCurrentNetworkPanel().getCurrentCase());
        } catch (UnsupportedOperationException e) {
            JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                    e.getMessage(),
                    stringDatabase.getString("ErrorWindow.Title.Label"),
                    JOptionPane.ERROR_MESSAGE);
        }
        return networkPanel;
    }
    
	public NetworkPanel createNewFrame(ProbNet probNet) {
		// TODO Auto-generated method stub
		return createNewFrame(probNet, null);
	}

    /**
     * Open a network.
     */
    private void openNetwork() {
        openNetwork("");
    }

    /**
     * Open a existing network in a new network frame. If it is not a recently
     * closed network (registered in the menu), it requests the file which
     * contains the network and then opens a new network frame.
     * 
     * @param fileName
     *            - for the network
     */
    public void openNetwork(String fileName) {
        if (fileName.equals("")) {
            fileName = requestNetworkFileToOpen();
        }
        ProbNet netReadFromFile = null;
        NetworkPanel networkPanel = null;
        if (fileName != null) {
            try {
                mainPanel.getMessageWindow().getNormalMessageStream().println(stringDatabase.getString("LoadingNetwork.Text.Label")
                        + " "
                        + fileName);
                ProbNetInfo probNetInfo = NetsIO.openNetworkFile(fileName);
                netReadFromFile = probNetInfo.getProbNet();
                netReadFromFile.getPNESupport().addUndoableEditListener(mainPanel.getMainPanelMenuAssistant());
                netReadFromFile.getPNESupport().setWithUndo(true);
                netReadFromFile.setName(new File(fileName).getName());
                networkPanel = createNewFrame(netReadFromFile);
                networkPanel.setNetworkFile(fileName);
                List<EvidenceCase> evidence = probNetInfo.getEvidence();
                if (evidence != null && !evidence.isEmpty()) {
                    EvidenceCase preResolutionEvidence = evidence.get(0);
                    evidence.remove(0);
                    networkPanel.getEditorPanel().setEvidence(preResolutionEvidence, evidence);
                }
                networkPanels.add(networkPanel);
                lastOpenFiles.setLastFileName(fileName);
                if (getDirectoryFileName(fileName) != null) {
                    OpenMarkovPreferences.set(OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
                            getDirectoryFileName(fileName),
                            OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
                }
                mainPanel.getMessageWindow().getNormalMessageStream().println(stringDatabase.getString("NetworkLoaded.Text.Label"));
                mainPanel.getMainMenu().rechargeLastOpenFiles();
            } catch (Exception e) {
                mainPanel.getMessageWindow().getErrorMessageStream().println(e.getMessage());
                JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                        stringDatabase.getString("ErrorLoadingNetwork.Text.Label"),
                        stringDatabase.getString("ErrorWindow.Title.Label"),
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public void openNetwork(ProbNet probNet) {
        NetworkPanel newNetworkPanel = createNewFrame(probNet);
        networkPanels.add(newNetworkPanel);
    }

    /**
     * It asks the user to choose a file by means of a open-file dialog box.
     * 
     * @return complete path of the file, or null if the user selects cancel.
     */
    private String requestNetworkFileToOpen() {
        NetworkFileChooser fileChooser = new NetworkFileChooser();
        fileChooser.setDialogTitle(stringDatabase.getString("OpenNetwork.Title.Label"));
        String fileName = null;
        if (fileChooser.showOpenDialog(Utilities.getOwner(mainPanel)) == JFileChooser.APPROVE_OPTION) {
            fileName = fileChooser.getSelectedFile().getAbsolutePath();
        }
        return fileName;
    }

    /**
     * Closes the current network frame.
     * 
     * @return true if the network has been closed; otherwise, false.
     */
    private boolean closeCurrentNetwork() {
        boolean canClose = true;
        if (getCurrentNetworkPanel() != null) {
            canClose = networkCanBeClosed(getCurrentNetworkPanel());
            if (canClose) {
                mainPanel.getMdi().closeCurrentFrame();
                if (networkPanels.size() == 0) {
                    mainPanel.setToolBarPanel(NetworkPanel.EDITION_WORKING_MODE);
                    mainPanel.getMainPanelMenuAssistant().updateOptionsAllNetworkClosed();
                }
            }
        }
        return canClose;
    }

    /**
     * Process that executes when the user is trying to close the application.
     */
    private void closeApplication() {
        boolean allClosed = true;
        while (allClosed && networkPanels.size() > 0) {
            allClosed = closeCurrentNetwork();
        }
        if (allClosed) {
            System.exit(0);
        }
    }

    /**
     * Creates an expanded network from current network
     * */
    private void expandNetwork(ProbNet probNet, EvidenceCase preResolutionEvidence) {
        CostEffectivenessDialog costEffectivenessDialog = new CostEffectivenessDialog(Utilities.getOwner(mainPanel));
        if (costEffectivenessDialog.requestData() == CostEffectivenessDialog.OK_BUTTON) {
            int numSlices = costEffectivenessDialog.getNumSlices();
            MPADFactory expandedNetFactory = new MPADFactory(probNet, numSlices);
            ProbNet expandedNetwork = expandedNetFactory.getExtendedNetwork();
            String fileName = probNet.getName() + "_expanded";
            expandedNetwork.setName(fileName);
            NetworkPanel networkPanel = createNewFrame(expandedNetwork);
            networkPanel.setNetworkFile(fileName);
            networkPanel.getEditorPanel().setEvidence(preResolutionEvidence,
                    new ArrayList<EvidenceCase>());
            networkPanels.add(networkPanel);
        }
    }

    /**
     * expand the network like it would be done in CE analysis to show it in the
     * GUI
     */
    private void expandNetworkCE(ProbNet probNet, EvidenceCase preResolutionEvidence) {
        CostEffectivenessDialog costEffectivenessDialog = new CostEffectivenessDialog(Utilities.getOwner(mainPanel),
                probNet,
                false,
                false);
        if (costEffectivenessDialog.requestData() == CostEffectivenessDialog.OK_BUTTON) {
            EvidenceCase evidence = new EvidenceCase(preResolutionEvidence);
            int numSlices;

            numSlices = costEffectivenessDialog.getNumSlices();
            List<ProbNode> temporalNodes = CostEffectivenessAnalysis.getShiftingTemporalNodes(probNet);
            if (!temporalNodes.isEmpty()) {
                for (ProbNode timeDependentNode : temporalNodes) {
                    Variable timeDependentVariable = timeDependentNode.getVariable();
                    Finding finding = new Finding(timeDependentVariable,
                            costEffectivenessDialog.getInitialValues().get(timeDependentVariable));
                    try {
                        evidence.addFinding(finding);
                    } catch (InvalidStateException | IncompatibleEvidenceException e) {
                        e.printStackTrace();
                    }
                }
            }

            double costDiscountRate = costEffectivenessDialog.getCostDiscount();
            double effectivenessDiscountRate = costEffectivenessDialog.getEffectivenessDiscount();
            double maxX = 0.0;
            for (ProbNode probNode : probNet.getProbNodes()) {
                if (probNode.getNode().getCoordinateX() > maxX) {
                    maxX = probNode.getNode().getCoordinateX();
                }
            }
            MPADFactory expandedNetFactory = new MPADFactory(probNet, numSlices);
            ProbNet expandedNetwork = expandedNetFactory.getExtendedNetwork();
            expandedNetwork = CostEffectivenessAnalysis.adaptMPADforCE(expandedNetwork,
                    numSlices,
                    evidence);
            CostEffectivenessAnalysis.translateMonthlyUtilities(expandedNetwork);
            // TODO apply changes for transitions at cycle start, end or half cycle
            CostEffectivenessAnalysis.applyDiscountToUtilityNodes(expandedNetwork,
                    costDiscountRate,
                    effectivenessDiscountRate);
//            for (ProbNode probNode : expandedNetwork.getProbNodes()) {
//                probNode.samplePotentials();
//            }
            String fileName = probNet.getName() + "_expandedCE";
            expandedNetwork.setName(fileName);
            NetworkPanel networkPanel = createNewFrame(expandedNetwork);
            networkPanel.setNetworkFile(fileName);
            networkPanel.getEditorPanel().setEvidence(evidence, new ArrayList<EvidenceCase>());
            networkPanels.add(networkPanel);
        }
    }

    /**
     * This method saves the evidence of the current network to a file
     * 
     * @param currentNetworkPanel
     */
    private void saveEvidence(NetworkPanel currentNetworkPanel) {
        // TODO Implement
        List<EvidenceCase> evidence = currentNetworkPanel.getEditorPanel().getEvidence();
        evidence.add(0, currentNetworkPanel.getEditorPanel().getPreResolutionEvidence());
        JFileChooser fileChooser = new JFileChooser();
        File currentDirectory = new File(OpenMarkovPreferences.get(OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
                OpenMarkovPreferences.OPENMARKOV_DIRECTORIES,
                "."));
        fileChooser.setCurrentDirectory(currentDirectory);
        String suggestedFileName = currentNetworkPanel.getTitle().replaceFirst("^*", "");
        fileChooser.setSelectedFile(new File(suggestedFileName));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(Utilities.getOwner(mainPanel)) == JFileChooser.APPROVE_OPTION) {
            // save the selected file
            System.out.println("Save evidence file "
                    + fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * This method tries to load evidence into the current network
     * 
     * @param currentNetworkPanel
     */
    private void loadEvidence(NetworkPanel currentNetworkPanel) {
        FileChooser evidenceFileChooser = new DBReaderFileChooser();
        evidenceFileChooser.setDialogTitle(stringDatabase.getString("LoadEvidence.Title.Label"));
        // Set last used evidence format as default
        String lastFileFilter = OpenMarkovPreferences.get(OpenMarkovPreferences.LAST_LOADED_EVIDENCE_FORMAT,
                OpenMarkovPreferences.OPENMARKOV_FORMATS,
                "xls");
        evidenceFileChooser.setFileFilter(lastFileFilter);
        if ((evidenceFileChooser.showOpenDialog(Utilities.getOwner(mainPanel)) == JFileChooser.APPROVE_OPTION)) {
            // load the selected file
            System.out.println("Load evidence file "
                    + evidenceFileChooser.getSelectedFile().getAbsolutePath());
            CaseDatabaseManager caseDbManager = new CaseDatabaseManager();
            CaseDatabaseReader caseDbReader = caseDbManager.getReader(FilenameUtils.getExtension(evidenceFileChooser.getSelectedFile().getName()));
            ProbNet currentNet = currentNetworkPanel.getProbNet();
            try {
                CaseDatabase caseDatabase = caseDbReader.load(evidenceFileChooser.getSelectedFile().getAbsolutePath());
                List<Variable> variables = caseDatabase.getVariables();
                int[][] cases = caseDatabase.getCases();
                for (int i = 0; i < cases.length; ++i) {
                    EvidenceCase newEvidenceCase = new EvidenceCase();
                    for (int j = 0; j < cases[i].length; ++j) {
                        Variable variable = null;
                        try {
                            // Ignore missing values
                            if (!variables.get(j).getStateName(cases[i][j]).isEmpty()
                                    && !variables.get(j).getStateName(cases[i][j]).equals("?")) {
                                variable = currentNet.getVariable(variables.get(j).getName());
                                try {
                                    newEvidenceCase.addFinding(new Finding(variable,
                                            variable.getStateIndex(variables.get(j).getStateName(cases[i][j]))));
                                } catch (InvalidStateException e) {
                                    JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                                            stringDatabase.getString("LoadEvidence.Error.InvalidState.Text")
                                                    + e.getMessage(),
                                            stringDatabase.getString("ErrorWindow.Title.Label"),
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (ProbNodeNotFoundException e) {
                            JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                                    stringDatabase.getString("LoadEvidence.Error.UnknownVariable.Text")
                                            + ": "
                                            + variables.get(j).getName(),
                                    stringDatabase.getString("ErrorWindow.Title.Label"),
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (IncompatibleEvidenceException e) {
                            JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                                    stringDatabase.getString("LoadEvidence.Error.IncompatibleEvidence.Text"),
                                    stringDatabase.getString("ErrorWindow.Title.Label"),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    currentNetworkPanel.getEditorPanel().addNewEvidenceCase(newEvidenceCase);
                }
                // save format extension in preferences
                OpenMarkovPreferences.set(OpenMarkovPreferences.LAST_LOADED_EVIDENCE_FORMAT,
                        ((FileFilterBasic) evidenceFileChooser.getFileFilter()).getFilterExtension(),
                        OpenMarkovPreferences.OPENMARKOV_FORMATS);
                OpenMarkovPreferences.set(OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
                        getDirectoryFileName(evidenceFileChooser.getSelectedFile().getAbsolutePath()),
                        OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                        stringDatabase.getString("LoadEvidence.Error.Text"),
                        stringDatabase.getString("ErrorWindow.Title.Label"),
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * This method undoes the last operation on the actual network.
     */
    private void undo() {
        try {
            undoRedo(true);
        } catch (CannotUndoException e) {
            JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                    stringDatabase.getString("CannotUndo.Text.Label"),
                    stringDatabase.getString("ErrorWindow.Title.Label"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method re-does the last undone operation on the actual network.
     */
    private void redo() {
        try {
            undoRedo(false);
        } catch (CannotRedoException e) {
            JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                    stringDatabase.getString("CannotRedo.Text.Label"),
                    stringDatabase.getString("ErrorWindow.Title.Label"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method undoes or re-does an operation on the actual network.
     * 
     * @param undoOperation
     *            - if true, an undo must be performed; if false, a redo will be
     *            performed.
     * @throws CannotUndoException
     *             - if undo can't be performed.
     * @throws CannotRedoException
     *             - if redo can't be performed.
     */
    private void undoRedo(boolean undoOperation)
            throws CannotUndoException, CannotRedoException {
        NetworkPanel networkPanel = null;
        networkPanel = getCurrentNetworkPanel();
        if (undoOperation) {
            networkPanel.undo();
            networkPanel.repaint();
        } else {
            networkPanel.redo();
            networkPanel.repaint();
        }
    }

    /**
     * This method activates an edition option for the current network.
     * 
     * @param newState
     *            new edition state to set.
     */
    private void activateEditionMode(String newEditionMode) {
        NetworkPanel networkPanel = null;
        networkPanel = getCurrentNetworkPanel();
        networkPanel.setEditionMode(newEditionMode);
        mainPanel.getMainPanelMenuAssistant().setEditionOption(newEditionMode,
                networkPanel.isThereDataStored());
    }

    /**
     * This method establishes the network working mode (edition or inference).
     * 
     * @param newWorkingMode
     *            new working mode to set in the network.
     */
    private void setNewWorkingMode() {
        int currentWorkingMode = getCurrentNetworkPanel().getWorkingMode();
        int newWorkingMode;
        if (currentWorkingMode == NetworkPanel.EDITION_WORKING_MODE) {
            newWorkingMode = NetworkPanel.INFERENCE_WORKING_MODE;
        } else {
            newWorkingMode = NetworkPanel.EDITION_WORKING_MODE;
        }
        mainPanel.setToolBarPanel(newWorkingMode);
        mainPanel.changeWorkingModeButton(newWorkingMode);
        if (getNetworkPanels().size() > 0) {
            getCurrentNetworkPanel().setWorkingMode(newWorkingMode);
        }
        getCurrentNetworkPanel().setSelectedAllObjects(false);
        mainPanel.getMainPanelMenuAssistant().updateOptionsNetworkDependent(getCurrentNetworkPanel());
        if (newWorkingMode == NetworkPanel.INFERENCE_WORKING_MODE) {
            getCurrentNetworkPanel().updateIndividualProbabilities();
            mainPanel.getInferenceToolBar().setCurrentEvidenceCaseName(getCurrentNetworkPanel().getCurrentCase());
        } else {
            // getCurrentNetworkPanel().removeAllFindings(); //Suppressed the
            // elimination of findings on returning to Edition Mode
            if (getCurrentNetworkPanel().getInferenceAlgorithm() != null) {
                getCurrentNetworkPanel().setInferenceAlgorithm(null);
            }
        }
        getCurrentNetworkPanel().updateNodesExpansionState(newWorkingMode);
    }

    /**
     * This method establishes the new expansion threshold of the network.
     * 
     * @param newValue
     *            new value for expansion threshold
     */
    private void setNewExpansionThreshold(Double newValue) {
        getCurrentNetworkPanel().setExpansionThreshold(newValue);
        getCurrentNetworkPanel().setSelectedAllNodes(false);
        mainPanel.getMainPanelMenuAssistant().updateOptionsNewWorkingMode(NetworkPanel.INFERENCE_WORKING_MODE,
                getCurrentNetworkPanel());
        getCurrentNetworkPanel().updateNodesExpansionState(NetworkPanel.INFERENCE_WORKING_MODE);
    }

    /**
     * This method responds to the navigation among the evidence cases option
     * selected by the user.
     * 
     * @param command
     *            the Action Command corresponding to the selected option
     */
    private void evidenceCasesNavigationOption(String command) {
        if (command.equals("CREATE_NEW_EVIDENCE_CASE")) {
            getCurrentNetworkPanel().createNewEvidenceCase();
        } else if (command.equals("GO_TO_FIRST_EVIDENCE_CASE")) {
            getCurrentNetworkPanel().goToFirstEvidenceCase();
        } else if (command.equals("GO_TO_PREVIOUS_EVIDENCE_CASE")) {
            getCurrentNetworkPanel().goToPreviousEvidenceCase();
        } else if (command.equals("GO_TO_NEXT_EVIDENCE_CASE")) {
            getCurrentNetworkPanel().goToNextEvidenceCase();
        } else if (command.equals("GO_TO_LAST_EVIDENCE_CASE")) {
            getCurrentNetworkPanel().goToLastEvidenceCase();
        } else if (command.equals("CLEAR_OUT_ALL_EVIDENCE_CASES")) {
            getCurrentNetworkPanel().clearOutAllEvidenceCases();
        }
        mainPanel.getMainPanelMenuAssistant().updateOptionsEvidenceCasesNavigation(getCurrentNetworkPanel());
        mainPanel.getMainPanelMenuAssistant().updateOptionsPropagationTypeDependent(getCurrentNetworkPanel());
    }

    /**
     * This method sets the inference options.
     */
    private void setInferenceOptions() {
        getCurrentNetworkPanel().setInferenceOptions();
        mainPanel.getMainPanelMenuAssistant().updatePropagateEvidenceButton();
    }

    /**
     * Sets the mode of painting the nodes.
     * 
     * @param byTitle
     *            if true, then the texts that appear into the nodes will be
     *            their titles; if false, these texts will be their name.
     */
    private void activateByTitle(boolean byTitle) {
        NetworkPanel actualNetwork = null;
        actualNetwork = getCurrentNetworkPanel();
        if (actualNetwork.getByTitle() != byTitle) {
            actualNetwork.setByTitle(byTitle);
            mainPanel.getMainPanelMenuAssistant().setByTitle(byTitle);
        }
    }

    /**
     * This method restores (if minimized) and shows the message window.
     */
    private void showMessageWindow() {
        if (!mainPanel.getMessageWindow().isVisible()) {
            mainPanel.getMdi().createNewFrame(mainPanel.getMessageWindow(), false);
            mainPanel.getMessageWindow().setVisible(true);
        } else {
            mainPanel.getMdi().selectFrame(mainPanel.getMessageWindow());
        }
    }

    /**
     * This method increments the zoom of the current panel.
     * 
     * @param frameContentPanel
     *            network whose zoom will be changed.
     */
    private void incrementZoom(FrameContentPanel frameContentPanel) {
        setZoom(false, frameContentPanel, frameContentPanel.getZoom() + zoomChangeValue);
    }

    /**
     * This method decrements the zoom of the current panel.
     * 
     * @param frameContentPanel
     *            network whose zoom will be changed.
     */
    private void decrementZoom(FrameContentPanel frameContentPanel) {
        setZoom(false, frameContentPanel, frameContentPanel.getZoom() - zoomChangeValue);
    }

    /**
     * Sets the zoom of the current panel and updates the menu and the toolbar.
     * 
     * @param dialogBox
     *            if true, the parameter 'value' is ignored and this value is
     *            requested to user.
     * @param frameContentPanel
     *            network whose zoom will be changed.
     * @param value
     *            new zoom value.
     */
    private void setZoom(boolean dialogBox, FrameContentPanel frameContentPanel, double value) {
        double newZoom = 0.0;
        if (dialogBox) {
            requestZoomToUser(Utilities.getOwner(mainPanel), frameContentPanel);
        } else {
            frameContentPanel.setZoom(value);
        }
        newZoom = frameContentPanel.getZoom();
        mainPanel.getMainPanelMenuAssistant().setZoom(newZoom);
    }

    /**
     * This method requests to the user a new value of zoom for the actual
     * network.
     * 
     * @param owner
     *            window that owns the dialog box.
     */
    public void requestZoomToUser(Window owner, FrameContentPanel frameContentPanel) {
        SelectZoomDialog dialogZoom = new SelectZoomDialog(owner);
        if (dialogZoom.requestZoom(frameContentPanel.getZoom()) == SelectZoomDialog.OK_BUTTON) {
            frameContentPanel.setZoom(dialogZoom.getZoom());
        }
    }

    /**
     * commodity method to provide the path directory for the network file name
     * 
     * @param fileName
     *            - name of the file to obtain the short name
     * @return the directory of the file
     */
    private static String getDirectoryFileName(String fileName) {
        return (new File(fileName)).getAbsolutePath();
    }

    /**
     * Returns current list of opened network panels
     * 
     * @return current list of opened network panels
     */
    public List<NetworkPanel> getNetworkPanels() {
        return networkPanels;
    }

    public void frameTitleChanged(FrameContentPanel contentPanel, String oldName, String newName) {
        // TODO Auto-generated method stub
    }

    public void frameOpened(FrameContentPanel contentPanel) {
        // TODO Auto-generated method stub
    }

    private void toggleDecisionTree(ProbNet probNet) {
        if (mainPanel.getStandardToolBar().getDecisionTreeButton().isSelected()) {
            try {
                DecisionTreeWindow decisionTree = new DecisionTreeWindow(probNet);
                mainPanel.getMdi().createNewFrame(decisionTree);
                mainPanel.getMainPanelMenuAssistant().updateOptionsDecisionTree(decisionTree);
            } catch (OutOfMemoryError e) {
                mainPanel.getStandardToolBar().getDecisionTreeButton().setSelected(false);
                JOptionPane.showMessageDialog(Utilities.getOwner(mainPanel),
                        stringDatabase.getString("ExceptionNotEnoughMemory.Text.Label"),
                        stringDatabase.getString("ExceptionNotEnoughMemory.Title.Label"),
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            mainPanel.getMdi().closeCurrentFrame();
        }
    }

    private void showCostEffectivenessDialog(ProbNet probNet,
            EvidenceCase evidence,
            boolean sensitivityAnalysis) {
        CostEffectivenessDialog costEffectivenessDialog = new CostEffectivenessDialog(Utilities.getOwner(mainPanel),
                probNet,
                sensitivityAnalysis,
                false);

        if (costEffectivenessDialog.requestData() == CostEffectivenessDialog.OK_BUTTON) {
            CostEffectivenessAnalysis costEffectivenessAnalysis = null;
            if (sensitivityAnalysis) {
                CostEffectivenessProgressBar ceProgressBar = new CostEffectivenessProgressBar(Utilities.getOwner(mainPanel), probNet, evidence, costEffectivenessDialog);
                ceProgressBar.setVisible(true);
            } else {
                costEffectivenessAnalysis = new CostEffectivenessAnalysis(probNet,
                        evidence,
                        costEffectivenessDialog.getCostDiscount(),
                        costEffectivenessDialog.getEffectivenessDiscount(),
                        costEffectivenessDialog.getNumSlices(),
                        costEffectivenessDialog.getInitialValues(),
                        costEffectivenessDialog.getTransitionTime());
                    JDialog ceaResultsDialog = new CostEffectivenessResultsDialog(Utilities.getOwner(mainPanel),
                            costEffectivenessAnalysis);
                    ceaResultsDialog.setVisible(true);
            }
        }
    }

}
