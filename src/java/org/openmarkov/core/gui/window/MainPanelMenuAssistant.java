/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.ChangeNetworkTypeEdit;
import org.openmarkov.core.action.PNESupport;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.gui.graphic.VisualDecisionNode;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.graphic.VisualUtilityNode;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.MenuAssistant;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;
import org.openmarkov.core.gui.menutoolbar.common.ZoomMenuToolBar;
import org.openmarkov.core.gui.oopn.OOSelectionListener;
import org.openmarkov.core.gui.oopn.VisualInstance;
import org.openmarkov.core.gui.oopn.VisualReferenceLink;
import org.openmarkov.core.gui.window.dt.DecisionTreeWindow;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.gui.window.edition.Zoom;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.constraint.OnlyChanceNodes;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.MPADType;
import org.openmarkov.core.oopn.OOPNet;

/**
 * This class assists to the class MainPanel to manage the menus and toolbars.
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo modify setZoom method to use floating point comparison
 *          instead != method and add default statement for case sentences
 * @version 1.2 - asaez - Functionality added: Treatment of options related to -
 *          Explanation capabilities, - Management of working modes
 *          (edition/inference), - Expansion and contraction of nodes, -
 *          Introduction and elimination of evidence - Management of multiple
 *          evidence cases.
 */
public class MainPanelMenuAssistant extends MenuAssistant
    implements
        OOSelectionListener,
        PNUndoableEditListener
{
    /**
     * Composed action command that contains all the save and close actions
     * (except save).
     */
    public static final String[] FILING_ACTION_COMMANDS    = {ActionCommands.SAVE_OPEN_NETWORK,
            ActionCommands.SAVEAS_NETWORK, ActionCommands.CLOSE_NETWORK,
            ActionCommands.LOAD_EVIDENCE, ActionCommands.SAVE_EVIDENCE,
            ActionCommands.NETWORK_PROPERTIES              };
    /**
     * Composed action command that contains all the edition actions (except
     * undo and redo).
     */
    public static final String[] EDITING_ACTION_COMMANDS   = {ActionCommands.OBJECT_SELECTION,
            ActionCommands.CHANCE_CREATION, ActionCommands.DECISION_CREATION,
            ActionCommands.UTILITY_CREATION, ActionCommands.LINK_CREATION, /*
                                                                            * //TODO
                                                                            * OOPN
                                                                            */
            ActionCommands.INSTANCE_CREATION               };
    /**
     * Composed action command that contains inference actions.
     */
    public static final String[] INFERENCE_ACTION_COMMANDS = {
            ActionCommands.CREATE_NEW_EVIDENCE_CASE, ActionCommands.GO_TO_FIRST_EVIDENCE_CASE,
            ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE, ActionCommands.GO_TO_NEXT_EVIDENCE_CASE,
            ActionCommands.GO_TO_LAST_EVIDENCE_CASE, ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES,
            ActionCommands.PROPAGATE_EVIDENCE              };
    /**
     * Composed action command that contains all the viewing actions (except
     * view message window).
     */
    public static final String[] VIEWING_ACTION_COMMANDS   = {ActionCommands.ZOOM,
            ActionCommands.ZOOM_IN, ActionCommands.ZOOM_OUT, ActionCommands.ZOOM_OTHER,
            ActionCommands.NODES                           };
    /**
     * Menus and toolbar that manage zoom.
     */
    private ZoomMenuToolBar[]    zoomMenus                 = null;
    /**
     * MainPanel from which this object depends.
     */
    private MainPanel            mainPanel                 = null;
    /**
     * networkPanel that is currently selected.
     */
    private NetworkPanel         currentNetworkPanel       = null;
    /**
     * String database
     */
    protected StringDatabase     stringDatabase            = StringDatabase.getUniqueInstance ();

    /**
     * Constructor that registers the arrays of menus.
     * @param newBasicMenus array of basic menus and toolbars.
     * @param newZoomMenus array of zoom menus and toolbars.
     * @param mainPanel MainPanel that creates this MainPanelMenuAssistant.
     */
    public MainPanelMenuAssistant (MenuToolBarBasic[] newBasicMenus,
                                   ZoomMenuToolBar[] newZoomMenus,
                                   MainPanel mainPanel)
    {
        super (newBasicMenus);
        ZoomMenuToolBar[] menus = newZoomMenus;
        if (menus == null)
        {
            menus = new ZoomMenuToolBar[0];
        }
        zoomMenus = menus;
        this.mainPanel = mainPanel;
    }

    /**
     * Sets the zoom value on the menus and toolbars.
     * @param value new zoom value.
     */
    public void setZoom (double value)
    {
        for (ZoomMenuToolBar menu : zoomMenus)
        {
            menu.setZoom (value);
        }
        Double dd = new Double (value);
        if (dd.equals (Zoom.MIN_VALUE))
        {
            setOptionEnabled (ActionCommands.ZOOM_OUT, false);
        }
        else
        {
            setOptionEnabled (ActionCommands.ZOOM_OUT, true);
        }
        if (dd.equals (Zoom.MAX_VALUE))
        {
            setOptionEnabled (ActionCommands.ZOOM_IN, false);
        }
        else
        {
            setOptionEnabled (ActionCommands.ZOOM_IN, true);
        }
    }

    /**
     * Enables the menu items and toolbar buttons when all networks are closed.
     */
    public void updateOptionsAllNetworkClosed ()
    {
        setOptionEnabled (FILING_ACTION_COMMANDS, false);
        setOptionEnabled (ActionCommands.SAVE_NETWORK, false);
        setOptionEnabled (EDITING_ACTION_COMMANDS, false);
        setOptionEnabled (INFERENCE_ACTION_COMMANDS, false);
        setOptionEnabled (ActionCommands.SELECT_ALL, false);
        setOptionEnabled (ActionCommands.CHANGE_WORKING_MODE, false);
        setOptionEnabled (ActionCommands.CHANGE_TO_INFERENCE_MODE, false);
        setOptionEnabled (ActionCommands.CHANGE_TO_EDITION_MODE, false);
        setOptionEnabled (ActionCommands.NODE_EXPANSION, false);
        setOptionEnabled (ActionCommands.NODE_CONTRACTION, false);
        setOptionEnabled (ActionCommands.NODE_ADD_FINDING, false);
        setOptionEnabled (ActionCommands.NODE_REMOVE_FINDING, false);
        setOptionEnabled (ActionCommands.NODE_REMOVE_ALL_FINDINGS, false);
        addOptionText (ActionCommands.UNDO, null);
        addOptionText (ActionCommands.REDO, null);
        setOptionEnabled (ActionCommands.UNDO, false);
        setOptionEnabled (ActionCommands.REDO, false);
        setOptionEnabled (ActionCommands.CLIPBOARD_CUT, false);
        setOptionEnabled (ActionCommands.CLIPBOARD_COPY, false);
        setOptionEnabled (ActionCommands.CLIPBOARD_PASTE, false);
        setOptionEnabled (ActionCommands.OBJECT_REMOVAL, false);
        setOptionEnabled (ActionCommands.NODE_PROPERTIES, false);
        setOptionEnabled (ActionCommands.EDIT_POTENTIAL, false);
        setOptionEnabled (ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, false);
        setOptionEnabled (ActionCommands.SENSITIVITY_ANALYSIS, false);
        setOptionEnabled (ActionCommands.LINK_PROPERTIES, false);
        setOptionEnabled (VIEWING_ACTION_COMMANDS, false);
        setOptionEnabled (ActionCommands.INFERENCE_OPTIONS, false);
        setOptionEnabled (ActionCommands.DECISION_IMPOSE_POLICY, false);
        setOptionEnabled (ActionCommands.DECISION_EDIT_POLICY, false);
        setOptionEnabled (ActionCommands.DECISION_REMOVE_POLICY, false);
        setOptionEnabled (ActionCommands.DECISION_SHOW_EXPECTED_UTILITY, false);
        setOptionEnabled (ActionCommands.DECISION_SHOW_OPTIMAL_POLICY, false);
        setOptionEnabled (ActionCommands.TEMPORAL_EVOLUTION_ACTION, false);
        setOptionEnabled (ActionCommands.EXPAND_NETWORK, false);
        setOptionEnabled (ActionCommands.DECISION_TREE, false);
        setOptionEnabled (ActionCommands.NEXT_SLICE_NODE, false);
    }

    /**
     * Disables the menu items and toolbar buttons when any network is opened.
     */
    public void updateOptionsNewNetworkOpen ()
    {
        int workingMode = NetworkPanel.EDITION_WORKING_MODE;
        if (!(currentNetworkPanel == null))
        {
            workingMode = currentNetworkPanel.getWorkingMode ();
            boolean enable = currentNetworkPanel.getProbNet ().getNetworkType () instanceof InfluenceDiagramType
                             || currentNetworkPanel.getProbNet ().getNetworkType () instanceof MPADType;
            setOptionEnabled (ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, enable);
            setOptionEnabled (ActionCommands.SENSITIVITY_ANALYSIS, enable);
        }
        setOptionEnabled (FILING_ACTION_COMMANDS, true);
        if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
        {
            setOptionEnabled (EDITING_ACTION_COMMANDS, true);
            setOptionEnabled (INFERENCE_ACTION_COMMANDS, false);
        }
        setOptionEnabled (VIEWING_ACTION_COMMANDS, true);
        setOptionEnabled (ActionCommands.CHANGE_WORKING_MODE, true);
        setOptionEnabled (ActionCommands.INFERENCE_OPTIONS, true);
        setOptionEnabled (ActionCommands.TEMPORAL_EVOLUTION_ACTION, false);
        setOptionEnabled (ActionCommands.EXPAND_NETWORK, false);
        setOptionEnabled (ActionCommands.NEXT_SLICE_NODE, false);
    }

    /**
     * Activates the corresponding options when a network has been modified.
     * @param undoManager network panel undo manager.
     */
    /*
     * public void updateOptionsNetworkModified(UndoManagerInfo undoManager) {
     * updateUndoRedo(undoManager); //changed by mpalacios updateUndoRedo(true,
     * true); setOptionEnabled(ActionCommands.SAVE_NETWORK, true); }
     */
    /**
     * Activates the corresponding options when a network has been modified.
     * @param undoManager network panel undo manager.
     */
    public void updateOptionsNetworkModified (boolean canUndo, boolean canRedo)
    {
        // updateUndoRedo(undoManager);
        // changed by mpalacios
        updateUndoRedo (canUndo, canRedo);
        setOptionEnabled (ActionCommands.SAVE_NETWORK, true);
    }

    /**
     * Activates the corresponding options when a network has been saved.
     */
    public void updateOptionsNetworkSaved ()
    {
        setOptionEnabled (ActionCommands.SAVE_NETWORK, false);
    }

    /**
     * Activates the options byTitle or byName.
     * @param byTitleActive if true, the option 'byTitle' will be activated; if
     *            false, the option 'byName' will be activated.
     */
    public void setByTitle (boolean byTitleActive)
    {
        if (byTitleActive)
        {
            setOptionSelected (ActionCommands.BYTITLE_NODES, true);
        }
        else
        {
            setOptionSelected (ActionCommands.BYNAME_NODES, true);
        }
    }

    /**
     * It is called when a network has been modified if new network do not have
     * OnlyOneAgentConstraints that means it is multiagent, so network is
     * initialized with two arbitrary agents
     * @param networkPanel
     */
    public void updateNetworkAgents (NetworkPanel networkPanel)
    {
        if (currentNetworkPanel.getProbNet ().isMultiagent ())
        {
            ArrayList<StringWithProperties> agents = new ArrayList<StringWithProperties> ();
            agents.add (new StringWithProperties (stringDatabase.getString ("Network.Agent1")));
            agents.add (new StringWithProperties (stringDatabase.getString ("Network.Agent2")));
            currentNetworkPanel.getProbNet ().setAgents (agents);
        } /*
           * else if (!currentNetworkPanel.getProbNet().isMultiagent()) { if
           * (currentNetworkPanel.getProbNet().getAgents() != null) {
           * currentNetworkPanel.getProbNet().setAgents(null); } for (ProbNode
           * probNode : currentNetworkPanel.getProbNet().getProbNodes()) {
           * probNode.getVariable().setAgent(null); } }
           */
    }

    /**
     * Activates the options on the menus and toolbars that depend on the
     * network.
     * @param networkPanel information of the network panel.
     */
    public void updateOptionsNetworkDependent (NetworkPanel networkPanel)
    {
        currentNetworkPanel = networkPanel;
        int workingMode = NetworkPanel.EDITION_WORKING_MODE;
        if (!(currentNetworkPanel == null))
        {
            workingMode = currentNetworkPanel.getWorkingMode ();
        }
        if (networkPanel.getByTitle ())
        {
            setOptionSelected (ActionCommands.BYTITLE_NODES, true);
        }
        else
        {
            setOptionSelected (ActionCommands.BYNAME_NODES, true);
        }
        setOptionEnabled (ActionCommands.CHANGE_WORKING_MODE, true);
        setOptionEnabled (ActionCommands.CHANGE_TO_INFERENCE_MODE, true);
        setOptionEnabled (ActionCommands.CHANGE_TO_EDITION_MODE, true);
        setOptionEnabled (ActionCommands.OBJECT_SELECTION, false);
        setOptionEnabled (ActionCommands.CHANCE_CREATION, false);
        setOptionEnabled (ActionCommands.DECISION_CREATION, false);
        setOptionEnabled (ActionCommands.UTILITY_CREATION, false);
        setOptionEnabled (ActionCommands.LINK_CREATION, false);
        setOptionEnabled (ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, false);
        setOptionEnabled (ActionCommands.SENSITIVITY_ANALYSIS, false);
        setOptionEnabled (ActionCommands.CHANGE_TO_INFERENCE_MODE, false);
        setOptionEnabled (ActionCommands.CHANGE_TO_EDITION_MODE, false);
        setOptionEnabled (ActionCommands.DECISION_TREE, false);
        if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
        {
            setOptionEnabled (ActionCommands.OBJECT_SELECTION, true);
            setOptionEnabled (ActionCommands.CHANCE_CREATION, true);
            setOptionEnabled (ActionCommands.LINK_CREATION, true);
            setOptionEnabled (ActionCommands.CHANGE_TO_INFERENCE_MODE, true);
            setOptionEnabled (INFERENCE_ACTION_COMMANDS, false);
            if (!networkPanel.getProbNet ().hasConstraint (OnlyChanceNodes.class))
            {
                setOptionEnabled (ActionCommands.DECISION_CREATION, true);
                setOptionEnabled (ActionCommands.UTILITY_CREATION, true);
                setOptionEnabled (ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, false);
                setOptionEnabled (ActionCommands.SENSITIVITY_ANALYSIS, false);
                setOptionEnabled (ActionCommands.DECISION_TREE, true);
            }
            if (networkPanel.getProbNet ().getNetworkType () instanceof MPADType
                || networkPanel.getProbNet ().getNetworkType () instanceof InfluenceDiagramType)
            {
                setOptionEnabled (ActionCommands.EXPAND_NETWORK, false);
                setOptionEnabled (ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, true);
                setOptionEnabled (ActionCommands.SENSITIVITY_ANALYSIS, true);
            }
        }
        else
        {
            setOptionEnabled (ActionCommands.CHANGE_TO_EDITION_MODE, true);
            setOptionEnabled (ActionCommands.CREATE_NEW_EVIDENCE_CASE, true);
            updateOptionsEvidenceCasesNavigation (networkPanel);
            if (networkPanel.isPropagationActive ())
            {
                setOptionEnabled (ActionCommands.PROPAGATE_EVIDENCE, false);
            }
            else
            {
                setOptionEnabled (ActionCommands.PROPAGATE_EVIDENCE, true);
            }
            if (!networkPanel.getProbNet ().hasConstraint (OnlyChanceNodes.class))
            {
                setOptionEnabled (ActionCommands.DECISION_TREE, true);
            }
        }
        updateOptionsFindingsDependent (networkPanel);
        updatePropagateEvidenceButton ();
        mainPanel.changeWorkingModeButton (workingMode);
        mainPanel.getStandardToolBar ().getDecisionTreeButton ().setSelected (false);
        /*
         * for (NodeType type : networkPanel.getNetwork().getNetworkType()
         * .getNodeTypes()) { switch (type) { case CHANCE: {
         * setOptionEnabled(ActionCommands.CHANCE_CREATION, true); break; } case
         * DECISION: { setOptionEnabled(ActionCommands.DECISION_CREATION, true);
         * break; } case UTILITY: {
         * setOptionEnabled(ActionCommands.UTILITY_CREATION, true); break; }
         * default: { setOptionEnabled(ActionCommands.CHANCE_CREATION, true);
         * break; } } }
         */
        setOptionEnabled (ActionCommands.SAVE_NETWORK, networkPanel.getModified ());
        objectsSelected (networkPanel.getSelectedNodes (), networkPanel.getSelectedLinks ());
        setZoom (networkPanel.getZoom ());
        /*
         * updateUndoRedo(networkPanel.getUndoManager().canUndo(),
         * networkPanel.getUndoManager().canUndo());
         */
        updateUndoRedo (networkPanel.getProbNet ().getPNESupport ().getCanUndo (),
                        networkPanel.getProbNet ().getPNESupport ().getCanRedo ());
        // updateUndoRedo(networkPanel.getUndoManager());
        mainPanel.setToolBarPanel (networkPanel.getWorkingMode ());
        // OOPN start
        setOptionEnabled (ActionCommands.INSTANCE_CREATION,
                          networkPanel.getProbNet () instanceof OOPNet);
        // OOPN end
    }

    /**
     * Enables or disables the undo and redo operations in the menubar and in
     * the toolbar, according to the state of undo and redo of the network.
     * @param undoManager undo manager.
     */
    /*
     * private void updateUndoRedo(UndoManagerInfo undoManager) { if
     * (undoManager.canUndo()) { setOptionEnabled(ActionCommands.UNDO, true);
     * addOptionText(ActionCommands.UNDO, undoManager
     * .getUndoPresentationName()); } else {
     * setOptionEnabled(ActionCommands.UNDO, false);
     * addOptionText(ActionCommands.UNDO, null); } if (undoManager.canRedo()) {
     * setOptionEnabled(ActionCommands.REDO, true);
     * addOptionText(ActionCommands.REDO, undoManager
     * .getRedoPresentationName()); } else {
     * setOptionEnabled(ActionCommands.REDO, false);
     * addOptionText(ActionCommands.REDO, null); } }
     */
    /**
     * Enables or disables the undo and redo operations in the menubar and in
     * the toolbar, according to the state of undo and redo of the network.
     * @param undoManager undo manager.
     */
    private void updateUndoRedo (boolean canUndo, boolean canRedo)
    {
        if (canUndo)
        {
            setOptionEnabled (ActionCommands.UNDO, true);
            addOptionText (ActionCommands.UNDO, "Deshacer");
        }
        else
        {
            setOptionEnabled (ActionCommands.UNDO, false);
            addOptionText (ActionCommands.UNDO, null);
        }
        if (canRedo)
        {
            setOptionEnabled (ActionCommands.REDO, true);
            addOptionText (ActionCommands.REDO, "Rehacer");
        }
        else
        {
            setOptionEnabled (ActionCommands.REDO, false);
            addOptionText (ActionCommands.REDO, null);
        }
    }

    /**
     * Activates the options on the menus and toolbars that depend on the
     * working mode established on the network (edition or inference)
     * @param workingMode the working mode (edition or inference).
     * @param networkPanel information of the network panel.
     */
    public void updateOptionsNewWorkingMode (int workingMode, NetworkPanel networkPanel)
    {
        if (workingMode == NetworkPanel.INFERENCE_WORKING_MODE)
        {
            setOptionEnabled (EDITING_ACTION_COMMANDS, false);
            setOptionEnabled (ActionCommands.UNDO, false);
            setOptionEnabled (ActionCommands.REDO, false);
            setOptionEnabled (ActionCommands.CLIPBOARD_CUT, false);
            setOptionEnabled (ActionCommands.CLIPBOARD_COPY, false);
            setOptionEnabled (ActionCommands.CLIPBOARD_PASTE, false);
            setOptionEnabled (ActionCommands.OBJECT_REMOVAL, false);
            setOptionEnabled (ActionCommands.LINK_PROPERTIES, false);
            setOptionEnabled (ActionCommands.CHANGE_TO_INFERENCE_MODE, false);
            setOptionEnabled (ActionCommands.CHANGE_TO_EDITION_MODE, true);
            setOptionEnabled (ActionCommands.CREATE_NEW_EVIDENCE_CASE, true);
            updateOptionsEvidenceCasesNavigation (networkPanel);
            if (networkPanel.isPropagationActive ())
            {
                setOptionEnabled (ActionCommands.PROPAGATE_EVIDENCE, false);
            }
            else
            {
                setOptionEnabled (ActionCommands.PROPAGATE_EVIDENCE, true);
            }
        }
        else if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
        {
            setOptionEnabled (EDITING_ACTION_COMMANDS, true);
            setOptionEnabled (INFERENCE_ACTION_COMMANDS, false);
            setOptionEnabled (ActionCommands.CHANGE_TO_INFERENCE_MODE, true);
            setOptionEnabled (ActionCommands.CHANGE_TO_EDITION_MODE, false);
        }
        objectsSelected (networkPanel.getSelectedNodes (), networkPanel.getSelectedLinks ());
    }

    /**
     * Activates the menu items and toolbar buttons for navigate among the set
     * of evidence cases.
     * @param networkPanel information of the network panel.
     */
    public void updateOptionsEvidenceCasesNavigation (NetworkPanel networkPanel)
    {
        if (networkPanel.getNumberOfCases () > 1)
        {
            setOptionEnabled (ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES, true);
            if (networkPanel.getCurrentCase () > 0)
            {
                setOptionEnabled (ActionCommands.GO_TO_FIRST_EVIDENCE_CASE, true);
                setOptionEnabled (ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE, true);
            }
            else
            {
                setOptionEnabled (ActionCommands.GO_TO_FIRST_EVIDENCE_CASE, false);
                setOptionEnabled (ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE, false);
            }
            if (networkPanel.getCurrentCase () < (networkPanel.getNumberOfCases () - 1))
            {
                setOptionEnabled (ActionCommands.GO_TO_NEXT_EVIDENCE_CASE, true);
                setOptionEnabled (ActionCommands.GO_TO_LAST_EVIDENCE_CASE, true);
            }
            else
            {
                setOptionEnabled (ActionCommands.GO_TO_NEXT_EVIDENCE_CASE, false);
                setOptionEnabled (ActionCommands.GO_TO_LAST_EVIDENCE_CASE, false);
            }
        }
        else
        {
            setOptionEnabled (ActionCommands.GO_TO_FIRST_EVIDENCE_CASE, false);
            setOptionEnabled (ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE, false);
            setOptionEnabled (ActionCommands.GO_TO_NEXT_EVIDENCE_CASE, false);
            setOptionEnabled (ActionCommands.GO_TO_LAST_EVIDENCE_CASE, false);
            setOptionEnabled (ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES,
                              networkPanel.areThereFindingsInCase ());
        }
        updateOptionsFindingsDependent (networkPanel);
    }

    /**
     * Activates the options on the menus and toolbars that depend on the
     * propagation type established on the network (automatic or manual).
     * @param networkPanel information of the network panel.
     */
    public void updateOptionsPropagationTypeDependent (NetworkPanel networkPanel)
    {
        if (networkPanel.isPropagationActive ())
        {
            setOptionEnabled (ActionCommands.PROPAGATE_EVIDENCE, false);
        }
        else
        {
            if (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE)
            {
                setOptionEnabled (ActionCommands.PROPAGATE_EVIDENCE, true);
            }
        }
    }

    /**
     * Activates the options on the menus and toolbars that depend on the
     * existence of findings in the current evidence case.
     * @param networkPanel information of the network panel.
     */
    public void updateOptionsFindingsDependent (NetworkPanel networkPanel)
    {
        setOptionEnabled (ActionCommands.NODE_REMOVE_ALL_FINDINGS,
                          networkPanel.areThereFindingsInCase ());
        if (networkPanel.getNumberOfCases () == 1)
        {
            setOptionEnabled (ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES,
                              networkPanel.areThereFindingsInCase ());
        }
    }

    /**
     * Activates an edition option on the menus and toolbars according to the
     * edition state.
     * @param value actual edition state.
     * @param canPaste if the state is SELECTION, this parameter says if there
     *            is data in the clipboard.
     */
    public void setEditionOption (String newEditionMode, boolean canPaste)
    {
        setOptionSelected (newEditionMode, true);
        setOptionEnabled (ActionCommands.SELECT_ALL, true);
        setOptionEnabled (ActionCommands.CLIPBOARD_PASTE, canPaste);
    }

    /**
     * This method activates o desactivates some options depending on the
     * numbers of nodes or links selected or the expanded state of the specific
     * nodes selected
     * @param nodes number of selected nodes.
     * @param links number of selected links.
     * @param arrayOfNodes an array with the selected nodes.
     */
    public void objectsSelected (List<VisualNode> selectedNodes, List<VisualLink> selectedLinks)
    {
        boolean canCut = false;
        boolean canCopy = false;
        boolean canRemove = false;
        boolean canNodeProperties = false;
        boolean canNodeTable = false;
        boolean canLinkProperties = false;
        boolean canExpand = false;
        boolean canContract = false;
        boolean canAddFinding = false;
        boolean canRemoveFinding = false;
        boolean canLog = false;
        boolean canImposePolicy = false;
        boolean canEditPolicy = false;
        boolean canRemovePolicy = false;
        boolean canShowExpectedUtility = false;
        boolean canShowOptimalPolicy = false;
        boolean canTemporalEvolution = false;
        boolean canCreateNextSliceNode = false;
        int workingMode = NetworkPanel.EDITION_WORKING_MODE;
        if (!(currentNetworkPanel == null))
        {
            workingMode = currentNetworkPanel.getWorkingMode ();
        }
        if (selectedNodes.size () > 0)
        {
            canCopy = true;
            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
            {
                canRemove = true;
                canCut = true;
            }
            if (selectedLinks.size () <= 0)
            {
                // if we are in Inference Mode, options about expansion and
                // contraction must be activated
                if (workingMode == NetworkPanel.INFERENCE_WORKING_MODE)
                {
                    if (selectedNodes.size () > 0)
                    {
                        VisualNode visualNode = null;
                        for (int i = 0; i < selectedNodes.size (); i++)
                        {
                            visualNode = selectedNodes.get (i);
                            // if at least one selected node is expanded,
                            // 'contract node(s)' option must be active
                            if (visualNode.isExpanded ())
                            {
                                canContract = true;
                            }
                            // if at least one selected node is contracted,
                            // 'expand node(s)' option must be active
                            if (!(visualNode.isExpanded ()))
                            {
                                canExpand = true;
                            }
                        }
                    }
                }
                // if at least one selected node has a post-Resolution finding,
                // 'remove finding' option must be active
                VisualNode vNode = null;
                for (int i = 0; i < selectedNodes.size (); i++)
                {
                    vNode = selectedNodes.get (i);
                    if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                    {
                        if (vNode.isPreResolutionFinding ())
                        {
                            canRemoveFinding = true;
                        }
                        else
                        {
                            canRemoveFinding = false;
                        }
                    }
                    else
                    {
                        if (vNode.isPostResolutionFinding ())
                        {
                            canRemoveFinding = true;
                        }
                        else
                        {
                            canRemoveFinding = false;
                        }
                    }
                }
                if (selectedNodes.size () == 1)
                {
                    canNodeProperties = true;
                    VisualNode visualNode = selectedNodes.get (0);
                    if (visualNode.getProbNode ().getVariable ().isTemporal ())
                    {
                        canLog = true;
                        canTemporalEvolution = true;
                        canCreateNextSliceNode = !visualNode.getProbNode ().getProbNet().containsShiftedVariable(visualNode.getProbNode ().getVariable (), 1);
                    }
                    String label = null;
                    switch (visualNode.getProbNode ().getNodeType ())
                    {
                        case CHANCE :
                            canNodeTable = true;
                            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                            {
                                label = stringDatabase.getString ("Edit.NodePotential.Label");
                            }
                            else
                            {
                                label = stringDatabase.getString ("Edit.ViewNodePotential.Label");
                            }
                            break;
                        case UTILITY :
                            canNodeTable = true;
                            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                            {
                                label = stringDatabase.getString ("Edit.Utility.Label");
                            }
                            else
                            {
                                label = stringDatabase.getString ("Edit.ViewUtility.Label");
                            }
                            break;
                        case DECISION :
                            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                            {
                                label = stringDatabase.getString ("Edit.NodePotential.Label");
                                if (((VisualDecisionNode) visualNode).isHasPolicy ())
                                {
                                    canEditPolicy = true;
                                    canRemovePolicy = true;
                                }
                                else
                                {
                                    canImposePolicy = true;
                                }
                            }
                            else
                            {
                                label = stringDatabase.getString ("Edit.ViewNodePotential.Label");
                                if (true)
                                { // ...asaez...if network compiled...currently
                                  // not needed
                                  // ...because if not compiled, those options
                                  // are not shown.
                                    canShowExpectedUtility = true;
                                    canShowOptimalPolicy = true;
                                }
                            }
                            break;
                    }
                    setText (ActionCommands.EDIT_POTENTIAL, label);
                    canAddFinding = !visualNode.hasAnyFinding ()
                                    || (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                                    || (workingMode == NetworkPanel.INFERENCE_WORKING_MODE && visualNode.isPostResolutionFinding ());
                    canAddFinding &= !(visualNode instanceof VisualUtilityNode);
                    boolean addOrChange = (workingMode == NetworkPanel.EDITION_WORKING_MODE && !visualNode.isPreResolutionFinding ())
                                          || (workingMode == NetworkPanel.INFERENCE_WORKING_MODE && !visualNode.isPostResolutionFinding ());
                    setText (ActionCommands.NODE_ADD_FINDING,
                             stringDatabase.getString ((addOrChange) ? "Inference.AddFinding.Label"
                                                                    : "Inference.ChangeFinding.Label"));
                }
            }
        }
        else
        {
            if (selectedLinks.size () > 0)
            {
                if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                {
                    canRemove = true;
                }
                if (selectedLinks.size () == 1)
                {
                    if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                    {
                        canLinkProperties = true;
                    }
                }
            }
        }
        setOptionEnabled (ActionCommands.CLIPBOARD_CUT, canCut);
        setOptionEnabled (ActionCommands.CLIPBOARD_COPY, canCopy);
        setOptionEnabled (ActionCommands.OBJECT_REMOVAL, canRemove);
        setOptionEnabled (ActionCommands.NODE_PROPERTIES, canNodeProperties);
        setOptionEnabled (ActionCommands.EDIT_POTENTIAL, canNodeTable);
        setOptionEnabled (ActionCommands.LINK_PROPERTIES, canLinkProperties);
        setOptionEnabled (ActionCommands.NODE_EXPANSION, canExpand);
        setOptionEnabled (ActionCommands.NODE_CONTRACTION, canContract);
        setOptionEnabled (ActionCommands.NODE_ADD_FINDING, canAddFinding);
        setOptionEnabled (ActionCommands.NODE_REMOVE_FINDING, canRemoveFinding);
        setOptionEnabled (ActionCommands.LOG, canLog);
        setOptionEnabled (ActionCommands.DECISION_IMPOSE_POLICY, canImposePolicy);
        setOptionEnabled (ActionCommands.DECISION_EDIT_POLICY, canEditPolicy);
        setOptionEnabled (ActionCommands.DECISION_REMOVE_POLICY, canRemovePolicy);
        setOptionEnabled (ActionCommands.DECISION_SHOW_EXPECTED_UTILITY, canShowExpectedUtility);
        setOptionEnabled (ActionCommands.DECISION_SHOW_OPTIMAL_POLICY, canShowOptimalPolicy);
        setOptionEnabled (ActionCommands.TEMPORAL_EVOLUTION_ACTION, canTemporalEvolution);
        setOptionEnabled (ActionCommands.NEXT_SLICE_NODE, canCreateNextSliceNode);
    }

    // TODO OOPN start
    /**
     * This method activates o desactivates some options depending on the
     * numbers of nodes or links selected or the expanded state of the specific
     * nodes selected
     * @param nodes number of selected nodes.
     * @param links number of selected links.
     * @param arrayOfNodes an array with the selected nodes.
     */
    public void objectsSelected (List<VisualNode> selectedNodes,
                                 List<VisualLink> selectedLinks,
                                 List<VisualInstance> selectedInstances,
                                 List<VisualReferenceLink> selectedReferenceLinks)
    {
        boolean canCut = false;
        boolean canCopy = false;
        boolean canRemove = false;
        boolean canNodeProperties = false;
        boolean canNodeTable = false;
        boolean canLinkProperties = false;
        boolean canExpand = false;
        boolean canContract = false;
        boolean canAddFinding = false;
        boolean canRemoveFinding = false;
        boolean canLog = false;
        boolean canImposePolicy = false;
        boolean canEditPolicy = false;
        boolean canRemovePolicy = false;
        boolean canShowExpectedUtility = false;
        boolean canShowOptimalPolicy = false;
        boolean canTemporalEvolution = false;
        boolean canCreateNextSliceNode = false;
        int workingMode = NetworkPanel.EDITION_WORKING_MODE;
        if (!(currentNetworkPanel == null))
        {
            workingMode = currentNetworkPanel.getWorkingMode ();
        }
        if (selectedInstances.size () > 0)
        {
            canCopy = true;
            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
            {
                canRemove = true;
                canCut = true;
            }
            boolean isInstanceInput = true;
            for (VisualInstance instance : selectedInstances)
            {
                isInstanceInput &= instance.isInput ();
            }
            setOptionSelected (ActionCommands.MARK_AS_INPUT, isInstanceInput);
        }
        if (selectedNodes.size () > 0)
        {
            canCopy = true;
            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
            {
                canRemove = true;
                canCut = true;
            }
            if (selectedLinks.size () <= 0)
            {
                // if we are in Inference Mode, options about expansion and
                // contraction must be activated
                if (workingMode == NetworkPanel.INFERENCE_WORKING_MODE)
                {
                    if (selectedNodes.size () > 0)
                    {
                        VisualNode visualNode = null;
                        for (int i = 0; i < selectedNodes.size (); i++)
                        {
                            visualNode = selectedNodes.get (i);
                            // if at least one selected node is expanded,
                            // 'contract node(s)' option must be active
                            if (visualNode.isExpanded ())
                            {
                                canContract = true;
                            }
                            // if at least one selected node is contracted,
                            // 'expand node(s)' option must be active
                            if (!(visualNode.isExpanded ()))
                            {
                                canExpand = true;
                            }
                        }
                    }
                }
                // if at least one selected node has a post-Resolution finding,
                // 'remove finding' option must be active
                VisualNode vNode = null;
                for (int i = 0; i < selectedNodes.size (); i++)
                {
                    vNode = selectedNodes.get (i);
                    if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                    {
                        if (vNode.isPreResolutionFinding ())
                        {
                            canRemoveFinding = true;
                        }
                        else
                        {
                            canRemoveFinding = false;
                        }
                    }
                    else
                    {
                        if (vNode.isPostResolutionFinding ())
                        {
                            canRemoveFinding = true;
                        }
                        else
                        {
                            canRemoveFinding = false;
                        }
                    }
                }
                if (selectedNodes.size () == 1)
                {
                    canNodeProperties = true;
                    VisualNode visualNode = selectedNodes.get (0);
                    if (visualNode.getProbNode ().getVariable ().isTemporal ())
                    {
                        canLog = true;
                        canTemporalEvolution = true;
                        canCreateNextSliceNode = !visualNode.getProbNode ().getProbNet().containsShiftedVariable(visualNode.getProbNode ().getVariable (), 1);
                    }
                    String label = null;
                    switch (visualNode.getProbNode ().getNodeType ())
                    {
                        case CHANCE :
                            canNodeTable = true;
                            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                            {
                                label = stringDatabase.getString ("Edit.NodePotential.Label");
                            }
                            else
                            {
                                label = stringDatabase.getString ("Edit.ViewNodePotential.Label");
                            }
                            break;
                        case UTILITY :
                            canNodeTable = true;
                            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                            {
                                label = stringDatabase.getString ("Edit.Utility.Label");
                            }
                            else
                            {
                                label = stringDatabase.getString ("Edit.ViewUtility.Label");
                            }
                            break;
                        case DECISION :
                            if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                            {
                                label = stringDatabase.getString ("Edit.NodePotential.Label");
                                if (((VisualDecisionNode) visualNode).isHasPolicy ())
                                {
                                    canEditPolicy = true;
                                    canRemovePolicy = true;
                                }
                                else
                                {
                                    canImposePolicy = true;
                                }
                            }
                            else
                            {
                                label = stringDatabase.getString ("Edit.ViewNodePotential.Label");
                                if (true)
                                { // ...asaez...if network compiled...currently
                                  // not needed
                                  // ...because if not compiled, those options
                                  // are not shown.
                                    canShowExpectedUtility = true;
                                    canShowOptimalPolicy = true;
                                }
                            }
                            break;
                    }
                    setText (ActionCommands.EDIT_POTENTIAL, label);
                    canAddFinding = !visualNode.hasAnyFinding ()
                                    || (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                                    || (workingMode == NetworkPanel.INFERENCE_WORKING_MODE && visualNode.isPostResolutionFinding ());
                    canAddFinding &= !(visualNode instanceof VisualUtilityNode);
                    boolean addOrChange = (workingMode == NetworkPanel.EDITION_WORKING_MODE && !visualNode.isPreResolutionFinding ())
                                          || (workingMode == NetworkPanel.INFERENCE_WORKING_MODE && !visualNode.isPostResolutionFinding ());
                    setText (ActionCommands.NODE_ADD_FINDING,
                             stringDatabase.getString ((addOrChange) ? "Inference.AddFinding.Label"
                                                                    : "Inference.ChangeFinding.Label"));
                }
            }
        }
        else
        {
            if (selectedLinks.size () > 0 || selectedReferenceLinks.size () > 0)
            {
                if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                {
                    canRemove = true;
                }
                if (selectedLinks.size () == 1)
                {
                    if (workingMode == NetworkPanel.EDITION_WORKING_MODE)
                    {
                        canLinkProperties = true;
                    }
                }
            }
        }
        setOptionEnabled (ActionCommands.CLIPBOARD_CUT, canCut);
        setOptionEnabled (ActionCommands.CLIPBOARD_COPY, canCopy);
        setOptionEnabled (ActionCommands.OBJECT_REMOVAL, canRemove);
        setOptionEnabled (ActionCommands.NODE_PROPERTIES, canNodeProperties);
        setOptionEnabled (ActionCommands.EDIT_POTENTIAL, canNodeTable);
        setOptionEnabled (ActionCommands.LINK_PROPERTIES, canLinkProperties);
        setOptionEnabled (ActionCommands.NODE_EXPANSION, canExpand);
        setOptionEnabled (ActionCommands.NODE_CONTRACTION, canContract);
        setOptionEnabled (ActionCommands.NODE_ADD_FINDING, canAddFinding);
        setOptionEnabled (ActionCommands.NODE_REMOVE_FINDING, canRemoveFinding);
        setOptionEnabled (ActionCommands.LOG, canLog);
        setOptionEnabled (ActionCommands.DECISION_IMPOSE_POLICY, canImposePolicy);
        setOptionEnabled (ActionCommands.DECISION_EDIT_POLICY, canEditPolicy);
        setOptionEnabled (ActionCommands.DECISION_REMOVE_POLICY, canRemovePolicy);
        setOptionEnabled (ActionCommands.DECISION_SHOW_EXPECTED_UTILITY, canShowExpectedUtility);
        setOptionEnabled (ActionCommands.DECISION_SHOW_OPTIMAL_POLICY, canShowOptimalPolicy);
        setOptionEnabled (ActionCommands.TEMPORAL_EVOLUTION_ACTION, canTemporalEvolution);
        setOptionEnabled (ActionCommands.NEXT_SLICE_NODE, canCreateNextSliceNode);
    }

    // TODO OOPN end
    /**
     * This method indicates that some information has been put into the
     * clipboard.
     */
    public void dataStoredClipboard ()
    {
        setOptionEnabled (ActionCommands.CLIPBOARD_PASTE, true);
    }

    /**
     * This method indicates that there isn't valid information in the
     * clipboard.
     */
    public void invalidDataClipboard ()
    {
        setOptionEnabled (ActionCommands.CLIPBOARD_PASTE, false);
    }

    /**
     * This method notifies to the listener that an edition action has occurred
     * on a network panel.
     * @param undoManager undo manager object limited in functionality.
     */
    /*
     * public void editionPerformed(UndoManagerInfo undoManager) {
     * updateOptionsNetworkModified(undoManager); }
     */
    public void undoableEditHappened (UndoableEditEvent e)
    {
        ProbNet probNet = currentNetworkPanel.getProbNet ();
        // update menu options and network agents when network type has been
        // modified
        if (e.getEdit () instanceof ChangeNetworkTypeEdit)
        {
            updateOptionsNetworkDependent (currentNetworkPanel);
            // updateNetworkAgents(currentNetworkPanel);
        }
        updateOptionsNetworkModified (probNet.getPNESupport ().getCanUndo (),
                                      probNet.getPNESupport ().getCanRedo ());
        /*
         * updateOptionsNetworkModified(((ProbNet)e.getSource()).getPNESupport().
         * getCanUndo(), ((ProbNet)e.getSource()).getPNESupport().getCanRedo());
         */
    }

    public void undoableEditWillHappen (UndoableEditEvent event)
        throws ConstraintViolationException,
        CanNotDoEditException
    {
        // TODO Auto-generated method stub
    }

    public void undoEditHappened (UndoableEditEvent event)
    {
        updateOptionsNetworkModified (((PNESupport) event.getSource ()).getCanUndo (),
                                      ((PNESupport) event.getSource ()).getCanRedo ());
        /*
         * updateOptionsNetworkModified(((PNESupport)event.getSource()).getCanUndo
         * (), ((PNESupport)event.getSource()).getCanRedo());
         */
    }

    public NetworkPanel getCurrentNetworkPanel ()
    {
        return currentNetworkPanel;
    }

    /**
     * Enables or disables options on 'File' menu depending on the type of
     * window selected.
     * @param value indicates if options should be enabled or disabled.
     */
    public void updateOptionsWindowSelected (boolean value)
    {
        setOptionEnabled (ActionCommands.SAVE_OPEN_NETWORK, value);
        setOptionEnabled (ActionCommands.SAVEAS_NETWORK, value);
        setOptionEnabled (ActionCommands.NETWORK_PROPERTIES, value);
        setOptionEnabled (ActionCommands.CLOSE_NETWORK, value);
        setOptionEnabled (ActionCommands.LOAD_EVIDENCE, value);
        setOptionEnabled (ActionCommands.SAVE_EVIDENCE, value);
    }

    /**
     * Shows or hides 'Propagate evidence' option from menu and toolbar.
     * @param value indicates if options should be enabled or disabled.
     */
    public void updatePropagateEvidenceButton ()
    {
        if (getCurrentNetworkPanel ().isAutomaticPropagation ())
        {
            mainPanel.getInferenceToolBar ().removePropagateNowButton ();
            mainPanel.getMainMenu ().removePropagateNowItem ();
        }
        else
        {
            mainPanel.getInferenceToolBar ().addPropagateNowButton ();
            mainPanel.getMainMenu ().addPropagateNowItem ();
        }
        updateOptionsEvidenceCasesNavigation (getCurrentNetworkPanel ());
        updateOptionsPropagationTypeDependent (getCurrentNetworkPanel ());
    }

    public void updateOptionsDecisionTree (DecisionTreeWindow decisionTreeWindow)
    {
        setOptionEnabled (EDITING_ACTION_COMMANDS, false);
        setOptionEnabled (INFERENCE_ACTION_COMMANDS, false);
        // setOptionEnabled(VIEWING_ACTION_COMMANDS, false);
        setOptionEnabled (ActionCommands.SAVE_NETWORK, false);
        setOptionEnabled (ActionCommands.INFERENCE_OPTIONS, false);
        setOptionEnabled (ActionCommands.CHANGE_WORKING_MODE, false);
        setOptionEnabled (ActionCommands.CHANGE_TO_INFERENCE_MODE, false);
        setOptionEnabled (ActionCommands.CHANGE_TO_EDITION_MODE, false);
        mainPanel.getStandardToolBar ().getDecisionTreeButton ().setSelected (true);
        setZoom (decisionTreeWindow.getZoom ());
    }
}
