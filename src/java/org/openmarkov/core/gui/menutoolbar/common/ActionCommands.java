/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.menutoolbar.common;


/**
 * This class defines the constants used to identify the actions invoked by the
 * user.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Add help menus (previously to change to dynamic version),
 *    ficheros usados recientemente y cambios de lenguaje
 */
public class ActionCommands {

	/**
	 * Action invoked when the user wants to create a new network.
	 */
	public static final String NEW_NETWORK = "NewNetwork";

	/**
	 * Action invoked when the user wants to open a network.
	 */
	public static final String OPEN_NETWORK = "OpenNetwork";

	/**
	 * Action invoked when the user wants to open a network.
	 */
	public static final String OPEN_LAST_1_FILE ="OpenLastRecentNetwork1";
	/**
	 * Action invoked when the user wants to open a network.
	 */
	public static final String OPEN_LAST_2_FILE ="OpenLastRecentNetwork2";
	/**
	 * Action invoked when the user wants to open a network.
	 */
	public static final String OPEN_LAST_3_FILE ="OpenLastRecentNetwork3";
	/**
	 * Action invoked when the user wants to open a network.
	 */
	public static final String OPEN_LAST_4_FILE ="OpenLastRecentNetwork4";
	/**
	 * Action invoked when the user wants to open a network.
	 */
	public static final String OPEN_LAST_5_FILE ="OpenLastRecentNetwork5";

	/**
	 * Action invoked when the user wants to save a network.
	 */
	public static final String SAVE_NETWORK = "SaveNetwork";
	
	/**
	 * Action invoked when the user wants to save and open newly the same network.
	 */
	public static final String SAVE_OPEN_NETWORK = "SaveOpenNetwork";

	/**
	 * Action invoked when the user wants to save a network as another one.
	 */
	public static final String SAVEAS_NETWORK = "SaveAsNetwork";

	/**
	 * Action invoked when the user wants to close a network.
	 */
	public static final String CLOSE_NETWORK = "CloseNetwork";

    /**
     * Action invoked when the user wants to load evidence.
     */
    public static final String LOAD_EVIDENCE = "LoadEvidence";

    /**
     * Action invoked when the user wants to save evidence.
     */
    public static final String SAVE_EVIDENCE = "SaveEvidence";

    /**
	 * Action invoked when the user wants to edit the additionalProperties of a network.
	 */
	public static final String NETWORK_PROPERTIES = "NetworkProperties";

	/**
	 * Action invoked when the user wants to exit the application.
	 */
	public static final String EXIT_APPLICATION = "ExitApplication";

	/**
	 * Action invoked when the user wants to set the nodes to paint its text by
	 * title.
	 */
	public static final String BYTITLE_NODES = "ByTitleNodes";

	/**
	 * Action invoked when the user wants to set the nodes to paint its text by
	 * name.
	 */
	public static final String BYNAME_NODES = "ByNameNodes";

	/**
	 * Represents all the actions related to the nodes viewing.
	 */
	public static final String NODES = "Nodes";

	/**
	 * Represents all the actions related to the variable zoom options.
	 */
	public static final String ZOOM = "Zoom";

	/**
	 * Action invoked when the user wants to view the message window.
	 */
	public static final String MESSAGE_WINDOW = "MessageWindow";

	/**
	 * Used only to guarantee that all zoom actions commands begin the same.
	 */
	private static final String ZOOM_PREFIX = "Zoom_";

	/**
	 * Action invoked when the user wants to select all the objects.
	 */
	public static final String SELECT_ALL = "SelectAll";

    /**
     * Prefix used for edition mode changes
     */
    public static final String EDITION_MODE_PREFIX = "Edit.Mode";
	
	/**
	 * Action invoked when the user wants to activate the selection option.
	 */
	public static final String OBJECT_SELECTION = "Edit.Mode.Selection";

	/**
	 * Action invoked when the user wants to activate the chance node creation
	 * option.
	 */
	public static final String CHANCE_CREATION = "Edit.Mode.Chance";

	/**
	 * Action invoked when the user wants to activate the decision node creation
	 * option.
	 */
	public static final String DECISION_CREATION = "Edit.Mode.Decision";

	/**
	 * Action invoked when the user wants to activate the utility node creation
	 * option.
	 */
	public static final String UTILITY_CREATION = "Edit.Mode.Utility";

	/**
	 * Action invoked when the user wants to activate the link creation option.
	 */
	public static final String LINK_CREATION = "Edit.Mode.Link";
	
    /**
     * Action invoked when the user wants to mark an object as input parameter.
     */
    public static final String INSTANCE_CREATION = "Edit.Mode.Instance";        

    //TODO OOPN start
    /**
	 * Action invoked when the user wants to mark an object as input parameter.
	 */
	public static final String MARK_AS_INPUT = "MarkAsInput";		

    /**
     * Action invoked when the user wants to edit the class of the instance selected
     */
    public static final String EDIT_CLASS = "EditClass";       

    /**
     * Action invoked when the user wants to set the arity of the selected parameter
     */
    public static final String SET_ARITY = "Arity";       

    /**
     * Action invoked when the user wants to set the arity of the selected parameter to one
     */
    public static final String SET_ARITY_ONE = "Arity.One";
    
    /**
     * Action invoked when the user wants to set the arity of the selected parameter to one
     */
    public static final String SET_ARITY_MANY = "Arity.Many";
    //TODO OOPN end

	/**
	 * Action invoked when the user wants to change the working mode by
	 * pressing the button in the standard tool bar
	 * (switching from Edition to Inference mode or vice versa).
	 */
	public static final String CHANGE_WORKING_MODE = "ChangeWorkingMode";
	
	/**
	 * Action invoked when the user wants to change to inference mode
	 * using the option in the Edit menu
	 */
	public static final String CHANGE_TO_INFERENCE_MODE = "ChangeToInferenceMode";
	
	/**
	 * Action invoked when the user wants to change to edition mode
	 * using the option in the Inference menu
	 */
	public static final String CHANGE_TO_EDITION_MODE = "ChangeToEditionMode";

	/**
	 * Action invoked when the user wants to change the Expansion Threshold.
	 */
	public static final String SET_NEW_EXPANSION_THRESHOLD = "SetNewExpansionThreshold";
	
	/**
	 * Action invoked when the user wants to create a new evidence case.
	 */
	public static final String CREATE_NEW_EVIDENCE_CASE = "CreateNewEvidenceCase";
	
	/**
	 * Action invoked when the user wants to go to the first evidence case.
	 */
	public static final String GO_TO_FIRST_EVIDENCE_CASE = "GoToFirstEvidenceCase";
	
	/**
	 * Action invoked when the user wants to go to the previous evidence case.
	 */
	public static final String GO_TO_PREVIOUS_EVIDENCE_CASE = "GoToPreviousEvidenceCase";
	
	/**
	 * Action invoked when the user wants to go to the next evidence case.
	 */
	public static final String GO_TO_NEXT_EVIDENCE_CASE = "GoToNextEvidenceCase";
	
	/**
	 * Action invoked when the user wants to go to the last evidence case.
	 */
	public static final String GO_TO_LAST_EVIDENCE_CASE = "GoToLastEvidenceCase";

	/**
	 * Action invoked when the user wants to clear out all evidence cases.
	 */
	public static final String CLEAR_OUT_ALL_EVIDENCE_CASES = "ClearOutAllEvidenceCases";
	
	/**
	 * Action invoked when the user wants to propagate inference.
	 */
	public static final String PROPAGATE_EVIDENCE = "PropagateEvidence";
	
	/**
	 * Action invoked when the user wants to undo an operation.
	 */
	public static final String UNDO = "Undo";

	/**
	 * Action invoked when the user wants to redo an operation.
	 */
	public static final String REDO = "Redo";

	/**
	 * Action invoked when the user wants to show the additionalProperties of a node.
	 */
	public static final String NODE_PROPERTIES = "NodeProperties";
	
	/**
	 * Action invoked when the user wants to show the table of a node.
	 */
	public static final String EDIT_POTENTIAL = "NodePotential";
		
	/** 
	 * Action invoked when the user wants to impose a policy in a decision node.
	 */
	public static final String DECISION_IMPOSE_POLICY = "ImposePolicy";
	
	/** 
	 * Action invoked when the user wants to modify the policy of a decision node.
	 */
	public static final String DECISION_EDIT_POLICY = "EditPolicy";
	
	/** 
	 * Action invoked when the user wants to remove a policy from a decision node.
	 */
	public static final String DECISION_REMOVE_POLICY = "RemovePolicy";
	
	/**
	 * Action invoked when the user wants to show the expected utility of a decision node.
	 */
	public static final String DECISION_SHOW_EXPECTED_UTILITY = "ShowExpectedUtility";
	
	/**
	 * Action invoked when the user wants to show the optimal policy of a decision node.
	 */
	public static final String DECISION_SHOW_OPTIMAL_POLICY = "ShowOptimalPolicy"; 	
	
    /**
     * Action invoked when the user wants to show the optimal policy of a decision node.
     */
    public static final String DECISION_TREE = "DecisionTree";  

	/**
	 * Action invoked for testing
	 */
	public static final String TEST = "Test";

	/**
	 * Action invoked when the user wants to expand a node.
	 */
	public static final String NODE_EXPANSION = "NodeExpansion";
	
	/**
	 * Action invoked when the user wants to contract a node.
	 */
	public static final String NODE_CONTRACTION = "NodeContraction";
	
	/**
	 * Action invoked when the user wants to add a finding to a node.
	 */
	public static final String NODE_ADD_FINDING = "NodeAddFinding";	

	/**
	 * Action invoked when the user wants to remove a finding from a node.
	 */
	public static final String NODE_REMOVE_FINDING = "NodeRemoveFinding";
	
	/**
	 * Action invoked when the user wants to remove all the finding of the
	 * current evidence case
	 */
	public static final String NODE_REMOVE_ALL_FINDINGS = "NodeRemoveAllFindings"; 
	
	/**
	 * Action invoked when the user wants to show the additionalProperties of a link.
	 */
	public static final String LINK_PROPERTIES = "LinkProperties";

	/****
	 * Action invoked when the user wants to enable the linkRestrictions of a link.
	 */
	
	public static final String LINK_RESTRICTION_ENABLE_PROPERTIES = "LinkRestrictionEnableProperties";
	
	
	/****
	 * Action invoked when the user wants to disable the linkRestrictions of a link.
	 */
	
	public static final String LINK_RESTRICTION_DISABLE_PROPERTIES = "LinkRestrictionDisableProperties";
	
	/****
	 * Action invoked when the user wants to disable the linkRestrictions of a link.
	 */
	
	public static final String LINK_RESTRICTION_EDIT_PROPERTIES = "LinkRestrictionEditProperties";
	
	
	/****
	 * Action invoked when the user wants to show the revlationArc conditions of a link,
	 */
	
	public static final String LINK_REVELATIONARC_PROPERTIES = "RevelationArcProperties";
	
    /**
     * Action invoked when the user wants to view a toolbar
     */
    public static final String VIEW_TOOLBARS = "View.Toolbars";

    /**
	 * Action invoked when the user wants to change the zoom of the panel to
	 * another value.
	 */
	public static final String ZOOM_OTHER = "ZoomOther";
    
	/**
	 * Action invoked when the user wants to increment the zoom of the panel.
	 */
	public static final String ZOOM_IN = "ZoomIn";

	/**
	 * Action invoked when the user wants to decrement the zoom of the panel.
	 */
	public static final String ZOOM_OUT = "ZoomOut";

	/**
	 * Action invoked when the user wants to cut to clipboard.
	 */
	public static final String CLIPBOARD_CUT = "ClipboardCut";

	/**
	 * Action invoked when the user wants to copy to clipboard.
	 */
	public static final String CLIPBOARD_COPY = "ClipboardCopy";

	/**
	 * Action invoked when the user wants to paste from clipboard.
	 */
	public static final String CLIPBOARD_PASTE = "ClipboardPaste";

	/**
	 * Action invoked when the user wants to remove an object.
	 */
	public static final String OBJECT_REMOVAL = "ObjectRemoval";

	/**
	 * Action invoked when the user wants to learn a network.
	 */
	public static final String LEARNING = "Tools.Learning";
	
	/**
	 * Action invoked when the user wants to obtain the optimal interventions.
	 */
	public static final String COST_EFFECTIVENESS_DETERMINISTIC = "Tools.CostEffectivenessDeterministic";
	/**
	 * Action invoked when the user wants to expands the network.
	 */
	public static final String EXPAND_NETWORK = "CostEffectiveness.ExpandNetwork";
	/**
	 * Action invoked when the user wants to expands the network for CE analysis.
	 */
	public static final String EXPAND_NETWORK_CE = "CostEffectiveness.ExpandNetworkCE";
	/**
	 * Action invoked when the user wants to obtain the optimal interventions.
	 */
	public static final String SENSITIVITY_ANALYSIS = "Tools.SensitivityAnalysis";

	/**
	 * Action invoked when the user wants to configure OPENMARKOV options
	 */
	public static final String CONFIGURATION = "Tools.Configuration";
	
	/**
	 * Action invoked when the user wants to set the inference options.
	 */
	public static final String INFERENCE_OPTIONS = "InferenceOptions";
	
	/**
	 * Action invoked when the user wants to change the language
	 */
	public static final String HELP_CHANGE_LANGUAGE = "Help.ChangeLanguage";
	/**
	 * Action invoked when the user wants to open the help.
	 */
	public static final String HELP_HELP = "Help.Help";

	/**
	 * Action invoked when the user wants to open the "About..."
	 */
	public static final String HELP_ABOUT = "Help.About";
	
	
	/**
	 * Action invoked when the user wants to assign uncertainty to potential
	 */
	public static final String UNCERTAINTY_ASSIGN = "Uncertainty.Assign";
	
	/**
	 * Action invoked when the user wants to edit the uncertainty of potential
	 */
	public static final String UNCERTAINTY_EDIT = "Uncertainty.Edit";
	
	/**
	 * Action invoked when the user wants to remove uncertainty on potential
	 */
	public static final String UNCERTAINTY_REMOVE = "Uncertainty.Remove";

	/**
	 * Action invoked when the user wants to log temporal evolution
	 */
	public static final String LOG = "Log";

	/**
	 * Action invoked when the user selects another class to instantiate
	 */
	public static final String CHANGE_ACTIVE_CLASS = "PRM.ChangeActiveClass";	
	
	/**
	 * Action invoked when the user selects temporal evolution menu item
	 */
	public static final String TEMPORAL_EVOLUTION_ACTION = "Temporal.Evolution";
	
	/**
     * Action invoked when the user selects temporal evolution menu item
     */
    public static final String NEXT_SLICE_NODE = "Edit.NextSliceNode";    
    
    /**
     * Action invoked when the user want to see lookahead graph
     */
    public static final String LOOK_AHEAD ="LookAhead";
    
    /**
     * Action invoked when the user want to reset lookahead graph
     */
    
    public static final String LOOK_AHEAD_RESET = "LookAheadReset";
    
    /**
     * Action invoked when the user want to apply edits in the lookahead graph
     */
    
    public static final String LOOK_AHEAD_APPLY_EDIT = "LookAheadApplyEdit";
    		
    		

	/**
	 * Checks if the action command corresponds to a zoom action command.
	 * 
	 * @param actionCommand
	 *            action command.
	 * @return true if the action command corresponds to a zoom action command;
	 *         otherwise, false.
	 */
	public static boolean isZoomActionCommand(String actionCommand) {

		int lengthZoomPrefix = ZOOM_PREFIX.length();

		if (actionCommand.length() < (lengthZoomPrefix + 1)) {

			return false;
		} else if (actionCommand.substring(0, lengthZoomPrefix).equals(
			ZOOM_PREFIX)) {
			try {
				new Integer(actionCommand.substring(lengthZoomPrefix));
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Returns the zoom value of a zoom action command.
	 * 
	 * @param actionCommand
	 *            action command.
	 * @return the value of zoom of the action command or 0 if the action
	 *         command isn't a zoom menu item.
	 */
	public static double getValueZoomActionCommand(String actionCommand) {

		int lengthZoomPrefix = ZOOM_PREFIX.length();

		return (isZoomActionCommand(actionCommand)) ? (new Double(actionCommand
			.substring(lengthZoomPrefix)).doubleValue() / 100) : 0;

	}

	/**
	 * Returns the action command associated with the specified zoom value.
	 * 
	 * @param zoom
	 *            value of the zoom.
	 * @return a string that represents an action command associated with the
	 *         zoom value.
	 */
	public static String getZoomActionCommandValue(double zoom) {

		return ZOOM_PREFIX + (int) Math.round(zoom * 100);

	}
}
