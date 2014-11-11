/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.edition;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.help.UnsupportedOperationException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.gui.action.PasteEdit;
import org.openmarkov.core.gui.action.RemoveSelectedEdit;
import org.openmarkov.core.gui.costeffectiveness.TraceTemporalEvolutionDialog;
import org.openmarkov.core.gui.dialog.InferenceOptionsDialog;
import org.openmarkov.core.gui.dialog.link.LinkRestrictionEditDialog;
import org.openmarkov.core.gui.dialog.link.RevelationArcEditDialog;
import org.openmarkov.core.gui.dialog.network.NetworkPropertiesDialog;
import org.openmarkov.core.gui.dialog.node.CommonNodePropertiesDialog;
import org.openmarkov.core.gui.dialog.node.NodeAddFindingDialog;
import org.openmarkov.core.gui.dialog.node.NodePropertiesDialog;
import org.openmarkov.core.gui.dialog.node.PotentialEditDialog;
import org.openmarkov.core.gui.graphic.ExpectedValueBox;
import org.openmarkov.core.gui.graphic.FSVariableBox;
import org.openmarkov.core.gui.graphic.InnerBox;
import org.openmarkov.core.gui.graphic.SelectionListener;
import org.openmarkov.core.gui.graphic.VisualDecisionNode;
import org.openmarkov.core.gui.graphic.VisualElement;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNetwork;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.graphic.VisualState;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.menutoolbar.menu.ContextualMenu;
import org.openmarkov.core.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.gui.window.MainPanelMenuAssistant;
import org.openmarkov.core.gui.window.edition.mode.EditionMode;
import org.openmarkov.core.gui.window.edition.mode.EditionModeManager;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceManager;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.oopn.Instance.ParameterArity;

/**
 * This class implements the behaviour of a panel where a network will be
 * edited.
 * @author jmendoza
 * @author jlgozalo
 * @version 1.1 - jlgozalo - all variables initialized and eliminate the call to
 *          NodePropertiesDialogFactory (to use directly call to
 *          CommonNodePropertiesDialog method.
 * @version 1.3 - asaez - Functionality added: - Explanation capabilities, -
 *          Management of working modes (edition/inference), - Expansion and
 *          contraction of nodes, - Introduction and elimination of evidence -
 *          Management of multiple evidence cases.
 */
public class EditorPanel extends JPanel
    implements
        MouseListener,
        MouseMotionListener
{
    protected ProbNet                        probNet;
    /**
     * Static field for serializable class.
     */
    private static final long                serialVersionUID                 = 2789011585460326400L;
    /**
     * Object to convert coordinates of the screen to the panel and vice versa.
     */
    protected Zoom                           zoom;
    /**
     * Maximum width of the panel.
     */
    private double                           maxWidth                         = Toolkit.getDefaultToolkit ().getScreenSize ().getWidth () * 20;
    /**
     * Maximum height of the panel.
     */
    private double                           maxHeight                        = Toolkit.getDefaultToolkit ().getScreenSize ().getHeight () * 20;
    /**
     * Constant that indicates the value of the Expansion Threshold by default.
     */
    // This should be in a future a configuration option that should be read on
    // start
    private static final int                 DEFAULT_THRESHOLD_VALUE          = 5;
    /**
     * Current edition mode.
     */
    private EditionMode                      editionMode                      = null;
    /**
     * This variable indicates which is the expansion threshold of the network
     */
    private double                           currentExpansionThreshold        = DEFAULT_THRESHOLD_VALUE;
    /**
     * Network panel associated to this editor panel
     */
    private NetworkPanel                     networkPanel                     = null;
    /**
     * Pre resolution evidence
     */
    private EvidenceCase                     preResolutionEvidence;
    /**
     * Array of Evidence cases treated for this editor panel
     */
    private List<EvidenceCase>               postResolutionEvidence;
    /**
     * Each position of this array indicates if the corresponding evidence case
     * is currently compiled (if true) or not (if false)
     */
    private List<Boolean>                    evidenceCasesCompilationState;
    /**
     * Minimum value of the range of each utility node.
     */
    private HashMap<Variable, Double>        minUtilityRange;
    /**
     * Maximum value of the range of each utility node.
     */
    private HashMap<Variable, Double>        maxUtilityRange;
    /**
     * This variable indicates which is the evidence case that is currently
     * being treated
     */
    private int                              currentCase;
    /**
     * Inference manager
     */
    private InferenceManager                 inferenceManager                 = null;
    /**
     * Inference algorithm used to evaluate this network
     */
    private InferenceAlgorithm               inferenceAlgorithm               = null;
    /**
     * This variable indicates if the propagation mode is automatic or manual.
     */
    private boolean                          automaticPropagation;
    /**
     * This variable indicates if propagation should be done right now (if being
     * in Inference Mode).
     */
    private boolean                          propagationActive;
    /**
     * This variable indicates if it has been a change in the properties or in
     * the potential values in some node.
     */
    private boolean                          networkChanged                   = true;
    /**
     * Visual representation of the network
     */
    protected VisualNetwork                  visualNetwork                    = null;
    /**
     * Position of the mouse cursor when it is pressed.
     */
    protected Point2D.Double                 cursorPosition                   = new Point2D.Double ();
    /**
     * Listener that listen to the changes of size.
     */
    private HashSet<EditorPanelSizeListener> sizeListeners                    = new HashSet<EditorPanelSizeListener> ();
    /**
     * Object that creates the contextual menus.
     */
    private ContextualMenuFactory            contextualMenuFactory            = null;
    /**
     * Object that assists this panel in the operations with the clipboard.
     */
    private EditorPanelClipboardAssistant    clipboardAssistant               = null;
    /**
     * String database
     */
    protected StringDatabase                 stringDatabase                   = StringDatabase.getUniqueInstance ();
    private EditionModeManager               editionModeManager;
    /**
     * Object Dialog for potentials edition
     */
    PotentialEditDialog                      potentialsDialog                 = null;
    /****
     * Dialog for link restriction edition
     */
    LinkRestrictionEditDialog                linkRestrictionDialog            = null;
    /***
     * Dialog for revelation arc edition
     */
    RevelationArcEditDialog                  revelationArcDialog              = null;
    private boolean                          approximateInferenceWarningGiven = false;
    private boolean                          canBeExpanded                    = false;

    /**
     * Constructor that creates the instance.
     * @param networkPanel network that will be edited.
     */
    public EditorPanel (NetworkPanel networkPanel, VisualNetwork visualNetwork)
    {
        zoom = new Zoom ();
        // super();
        this.networkPanel = networkPanel;
        this.probNet = networkPanel.getProbNet ();
        this.visualNetwork = visualNetwork;
        automaticPropagation = true;
        propagationActive = true;
        preResolutionEvidence = new EvidenceCase ();
        postResolutionEvidence = new ArrayList<EvidenceCase> (1);
        currentCase = 0;
        EvidenceCase evidenceCase = new EvidenceCase ();
        postResolutionEvidence.add (currentCase, evidenceCase);
        evidenceCasesCompilationState = new ArrayList<Boolean> (1);
        evidenceCasesCompilationState.add (currentCase, false);
        minUtilityRange = new HashMap<Variable, Double> ();
        maxUtilityRange = new HashMap<Variable, Double> ();
        initialize ();
        inferenceManager = new InferenceManager ();
        editionModeManager = new EditionModeManager (this, probNet);
        editionMode = editionModeManager.getDefaultEditionMode ();
    }

    /**
     * This method initializes this instance.
     */
    private void initialize ()
    {
        addMouseListener (this);
        addMouseMotionListener (this);
        this.setBackground (Color.white);
        adjustPanelDimension ();
        clipboardAssistant = new EditorPanelClipboardAssistant ();
    }

    /**
     * Changes the presentation mode of the text of the nodes.
     * @param value new value of the presentation mode of the text of the nodes.
     */
    public void setByTitle (boolean value)
    {
        visualNetwork.setByTitle (value);
        adjustPanelDimension ();
        repaint ();
    }

    /**
     * Returns the presentation mode of the text of the nodes.
     * @return true if the title of the nodes is the name or false if it is the
     *         name.
     */
    public boolean getByTitle ()
    {
        return visualNetwork.getByTitle ();
    }

    /**
     * Overwrite 'paint' method to avoid to call it explicitly.
     * @param g the graphics context in which to paint.
     */
    @Override
    public void paint (Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;
        super.paint (g);
        g2D.scale (zoom.getZoom (), zoom.getZoom ());
        visualNetwork.paint (g2D);
    }

    /**
     * Returns the edition mode.
     * @return edition mode.
     */
    public EditionMode getEditionMode ()
    {
        return editionMode;
    }

    /**
     * Changes the state of the edition and carries out the necessary actions in
     * each case.
     * @param newState new edition state.
     */
    public void setEditionMode (String newEditionModeName)
    {
        EditionMode newEditionMode = editionModeManager.getEditionMode (newEditionModeName);
        if (!editionMode.equals (newEditionMode))
        {
            setCursor (editionModeManager.getCursor (newEditionModeName));
            visualNetwork.setSelectedAllObjects (false);
            editionMode = newEditionMode;
            repaint ();
        }
    }

    /**
     * Selects all nodes and links.
     */
    public void selectAllObjects ()
    {
        visualNetwork.setSelectedAllObjects (true);
        repaint ();
    }

    /**
     * Notifies to the registered size listener (if any) that the panel's size
     * has changed.
     * @param incrLeft increase for the left side.
     * @param incrTop increase overhead.
     * @param incrRight increase for the right side.
     * @param incrBottom increase for below.
     */
    @SuppressWarnings("unused")
    private void notifySizeChanged (double incrLeft,
                                    double incrTop,
                                    double incrRight,
                                    double incrBottom)
    {
        for (EditorPanelSizeListener listener : sizeListeners)
        {
            listener.sizeChanged (incrLeft, incrTop, incrRight, incrBottom);
        }
    }

    /**
     * Invoked when a mouse button has been clicked (pressed and released) on
     * the component.
     * @param e mouse event information.
     */
    public void mouseClicked (MouseEvent e)
    {
    }

    /**
     * Invoked when a mouse button has been pressed on the component.
     * @param e mouse event information.
     */
    public void mousePressed (MouseEvent e)
    {
        Graphics2D g = (Graphics2D) getGraphics ();
        cursorPosition.setLocation (zoom.screenToPanel (e.getX ()), zoom.screenToPanel (e.getY ()));
        // Specific functionality depending on the edition mode;
        editionMode.mousePressed (e, cursorPosition, g);
        // Generic functionality regardless of the edition mode
        VisualNode node = null;
        VisualLink link = null;
        if (SwingUtilities.isLeftMouseButton (e))
        {
            if (e.getClickCount () == 2)
            {
                if (Utilities.noMouseModifiers (e))
                {
                    if (networkPanel.getWorkingMode () == NetworkPanel.EDITION_WORKING_MODE)
                    {
                        // If we are in Edition Mode a double click must open
                        // the corresponding properties dialog (for node, link
                        // or network)
                        if ((node = visualNetwork.whatNodeInPosition (cursorPosition, g)) != null)
                        {
                            changeNodeProperties (node);
                        }
                        else if ((link = visualNetwork.whatLinkInPosition (cursorPosition, g)) != null)
                        {
                            changeLinkProperties (link);
                        }
                        else
                        {
                            changeNetworkProperties ();
                        }
                    }
                    else
                    {
                        // If we are in Inference Mode a double click inside a
                        // visual state of a node without pre-resolution finding
                        // must introduce evidence in that node.
                        // If the double click is inside a node but outside its
                        // inner box (in its 'expanded external shape'), its
                        // properties dialog should be open
                        if (visualNetwork.whatStateInPosition (cursorPosition, g) != null)
                        {
                            VisualNode visualNode = visualNetwork.whatNodeInPosition (cursorPosition,
                                                                                      g);
                            if (visualNode.isPreResolutionFinding ())
                            {
                                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                               "This node has a Pre-Resolution Finding that cannot be modified in Inference Mode.",
                                                               stringDatabase.getString ("ErrorWindow.Title.Label"),
                                                               JOptionPane.ERROR_MESSAGE);
                                // TODO //...asaez...Internacionalizar la
                                // sentencia
                            }
                            else
                            {
                                VisualState visualState = visualNetwork.whatStateInPosition (cursorPosition,
                                                                                             g);
                                setNewFinding (visualNode, visualState);
                            }
                        }
                        else
                        {
                            if ((visualNetwork.whatNodeInPosition (cursorPosition, g) != null)
                                && (visualNetwork.whatInnerBoxInPosition (cursorPosition, g) == null))
                            {
                                changeNodeProperties ();
                            }
                        }
                    }
                }
            }
            else if (e.isAltDown ())
            {
                if ((node = visualNetwork.whatNodeInPosition (cursorPosition, g)) != null)
                {
                    if (!node.isSelected ())
                    {
                        visualNetwork.setSelectedAllObjects (false);
                        visualNetwork.setSelectedNode (node, true);
                    }
                    showPotentialDialog (networkPanel.getWorkingMode () != NetworkPanel.EDITION_WORKING_MODE);
                }
            }
        }
        else if (SwingUtilities.isRightMouseButton (e))
        {
            showContextualMenu (e, g);
        }
        repaint ();
    }

    /**
     * Shows contextual menu
     * @param e MouseEvent
     * @param g Graphics2D
     */
    private void showContextualMenu (MouseEvent e, Graphics2D g)
    {
        VisualElement selectedElement = visualNetwork.getElementInPosition (cursorPosition, g);
        ContextualMenu contextualMenu = null;
        if (selectedElement != null)
        {
            contextualMenu = getContextualMenu (selectedElement, this);
            visualNetwork.selectElement (selectedElement);
        }
        else
        {
            canBeExpanded = probNet.thereAreTemporalNodes ();
            contextualMenu = contextualMenuFactory.getNetworkContextualMenu (canBeExpanded);
        }
        contextualMenu.show (this, e.getX (), e.getY ());
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged.
     * @param e mouse event information.
     */
    public void mouseDragged (MouseEvent e)
    {
        Graphics2D g = (Graphics2D) getGraphics ();
        Point2D.Double point = new Point2D.Double (zoom.screenToPanel (e.getX ()),
                                                   zoom.screenToPanel (e.getY ()));
        double diffX = point.getX () - cursorPosition.getX ();
        double diffY = point.getY () - cursorPosition.getY ();
        cursorPosition.setLocation (point);
        editionMode.mouseDragged (e, point, diffX, diffY, g);
    }

    /**
     * Invoked when a mouse button has been released on the component.
     * @param e mouse event information.
     */
    public void mouseReleased (MouseEvent e)
    {
        Graphics2D g = (Graphics2D) getGraphics ();
        Point2D.Double position = new Point2D.Double (zoom.screenToPanel (e.getX ()),
                                                      zoom.screenToPanel (e.getY ()));
        editionMode.mouseReleased (e, position, g);
    }

    /**
     * Invoked when the mouse button enters the component.
     * @param e mouse event information.
     */
    public void mouseEntered (MouseEvent e)
    {
    }

    /**
     * Invoked when the mouse button exits the component.
     * @param e mouse event information.
     */
    public void mouseExited (MouseEvent e)
    {
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no
     * buttons have been pushed.
     * @param e mouse event information.
     */
    public void mouseMoved (MouseEvent e)
    {
    }

    /**
     * This method allows to an object to be registered as size listener.
     * @param l size listener.
     */
    public void addEditorPanelSizeListener (EditorPanelSizeListener l)
    {
        sizeListeners.add (l);
    }

    /**
     * Return the maximum height of the panel till now.
     * @return maximum height of the panel till now.
     */
    double getMaxHeight ()
    {
        return zoom.panelToScreen (maxHeight);
    }

    /**
     * Return the maximum width of the panel till now.
     * @return maximum width of the panel till now.
     */
    double getMaxWidth ()
    {
        return zoom.panelToScreen (maxWidth);
    }

    /**
     * Changes the value of the zoom.
     * @param value new zoom.
     */
    public void setZoom (double value)
    {
        Dimension newDimension = null;
        Double dd = new Double (zoom.getZoom ());
        Double dd1 = new Double (value);
        if (dd.compareTo (dd1) != 0)
        { // jlgozalo. 24/08 fix condition to !=
            zoom.setZoom (value);
            newDimension = new Dimension ((int) Math.round (getMaxWidth ()),
                                          (int) Math.round (getMaxHeight ()));
            setPreferredSize (newDimension);
            setSize (newDimension);
            adjustPanelDimension ();
            repaint ();
        }
    }

    /**
     * Returns the value of the zoom.
     * @return actual value of zoom.
     */
    public double getZoom ()
    {
        return zoom.getZoom ();
    }

    /**
     * This method performs a undo or redo operation.
     * @param undoOperation if true, an undo must be performed; if false, a redo
     *            will be performed.
     * @throws CannotUndoException if undo can't be performed.
     * @throws CannotRedoException if redo can't be performed.
     */
    private void undoRedo (boolean undoOperation)
        throws CannotUndoException,
        CannotRedoException
    {
        visualNetwork.setSelectedAllObjects (false);
        if (undoOperation)
        {
            probNet.getPNESupport ().undo ();
            // undoManager.undo();
        }
        else
        {
            // undoManager.redo();
            probNet.getPNESupport ().redo ();
        }
        adjustPanelDimension ();
        repaint ();
    }

    /**
     * This method performs a undo operation.
     * @throws CannotUndoException if undo can't be performed.
     */
    public void undo ()
        throws CannotUndoException
    {
        undoRedo (true);
    }

    /**
     * This method performs a redo operation.
     * @throws CannotRedoException if redo can't be performed.
     */
    public void redo ()
        throws CannotRedoException
    {
        undoRedo (false);
    }

    /**
     * Sets a new selection listener.
     * @param listener listener to be set.
     */
    public void addSelectionListener (SelectionListener listener)
    {
        visualNetwork.addSelectionListener (listener);
    }

    /**
     * Sets a new contextual menu factory.
     * @param newContextualMenuFactory contextual menu factory to be set.
     */
    public void setContextualMenuFactory (ContextualMenuFactory newContextualMenuFactory)
    {
        contextualMenuFactory = newContextualMenuFactory;
    }

    /**
     * Retrieves the contextual menu that corresponds to the selectedElement.
     * @return the contextual menu corresponding the the parameter.
     */
    private ContextualMenu getContextualMenu (VisualElement selectedElement, EditorPanel panel)
    {
        return (contextualMenuFactory != null) ? contextualMenuFactory.getContextualMenu (selectedElement,
                                                                                          panel)
                                              : null;
    }

    /**
     * Returns the number of selected nodes.
     * @return number of selected nodes.
     */
    public int getSelectedNodesNumber ()
    {
        return visualNetwork.getSelectedNodesNumber ();
    }

    /**
     * Returns the number of selected links.
     * @return number of selected links.
     */
    public int getSelectedLinksNumber ()
    {
        return visualNetwork.getSelectedLinksNumber ();
    }

    /**
     * Returns a list containing the selected nodes.
     * @return a list containing the selected nodes.
     */
    public List<VisualNode> getSelectedNodes ()
    {
        return visualNetwork.getSelectedNodes ();
    }

    /**
     * Returns a list containing the selected links.
     * @return a list containing the selected links.
     */
    public List<VisualLink> getSelectedLinks ()
    {
        return visualNetwork.getSelectedLinks ();
    }

    /**
     * Selects or deselects all nodes of the network.
     * @param selected new selection state.
     */
    public void setSelectedAllNodes (boolean selected)
    {
        visualNetwork.setSelectedAllNodes (selected);
    }

    /**
     * Selects or deselects all objects of the network.
     * @param selected new selection state.
     */
    public void setSelectedAllObjects (boolean selected)
    {
        visualNetwork.setSelectedAllObjects (selected);
    }

    /**
     * This method shows a dialog box with the additionalProperties of a node.
     * If some property has changed, insert a new undo point into the network
     * undo manager.
     * @param selectedNode
     */
    public void changeNodeProperties (VisualNode selectedNode)
    {
        if (requestNodePropertiesToUser2 (Utilities.getOwner (this), selectedNode.getProbNode (),
                                          false))
        {
            adjustPanelDimension ();
            repaint ();
            if (selectedNode.getInnerBox () instanceof FSVariableBox)
            {
                ((FSVariableBox) selectedNode.getInnerBox ()).recreateVisualStates (postResolutionEvidence.size ());
            }
            networkChanged = true;
            removeNodeEvidenceInAllCases (selectedNode.getProbNode ());
        }
        else probNet.getPNESupport ().undoAndDelete ();
    }

    public void changeNodeProperties ()
    {
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes ();
        if (selectedNodes.size () == 1)
        {
            changeNodeProperties (selectedNodes.get (0));
        }
    }

    /**
     * 
     */
    public void showPotentialDialog (boolean readOnly)
    {
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes ();
        ProbNode probNode = selectedNodes.get (0).getProbNode ();
        /*
         * Potential oldPotential = probNode.getPotentials().get(0);
         * PotentialEditDialog dialog = new PotentialEditDialog(owner,
         * oldPotential, newElement); Potential newPotential =
         * dialog.getNewPotential(); if ( newPotential != null ) { new edit =
         * new ChangeNodePotentialEdit(newPotential);//sets the potential in the
         * probNode pNESupport.doedit //probnet PNESuport, inside panels
         * PNESupports will be owned by the edit dialog adjustPanelDimension();
         * repaint(); networkChanged = true; }
         */
        if (requestPotentialValues (Utilities.getOwner (this), probNode, false, readOnly))
        {
            // if the user has selected the ok button when closing the dialog
            adjustPanelDimension ();
            repaint ();
            networkChanged = true;
            removeNodeEvidenceInAllCases (probNode);
        }
        else
        {
            probNet.getPNESupport ().undoAndDelete ();
        }
    }

    /**
     * This method requests to the user the additionalProperties of a node.
     * @param owner owner window that shows the dialog box.
     * @param probNode object that contains the additionalProperties of the node
     *            and where changes will be saved.
     * @param newNode specifies if the node whose additionalProperties are going
     *            to be edited is new.
     * @return true, if the user save the changes on probNode; otherwise, false.
     */
    private boolean requestNodePropertiesToUser2 (Window owner, ProbNode probNode, boolean newNode)
    {
        NodePropertiesDialog nodePropertiesDialog = new CommonNodePropertiesDialog (owner,
                                                                                    probNode,
                                                                                    newNode);
        return (nodePropertiesDialog.requestProperties () == NodePropertiesDialog.OK_BUTTON);
    }

    private boolean requestPotentialValues (Window owner,
                                            ProbNode probNode,
                                            boolean newNode,
                                            boolean readOnly)
    {
        potentialsDialog = new PotentialEditDialog (owner, probNode, newNode, readOnly);
        return (potentialsDialog.requestValues ()// to know if the user has
                                                 // selected the ok button when
                                                 // closing the dialog
        == NodePropertiesDialog.OK_BUTTON);
    }

    /**
     * This method requests to the user the link restriction properties of a
     * link.
     * @param owner owner window that shows the dialog box.
     * @param link object that contains the link restriction properties of the
     *            link and where changes will be saved.
     * @return true, if the user save the changes on probNode; otherwise, false.
     */
    private boolean requestLinkRestrictionValues (Window owner, Link link)
    {
        linkRestrictionDialog = new LinkRestrictionEditDialog (owner, link);
        return (linkRestrictionDialog.requestValues () == NodePropertiesDialog.OK_BUTTON);
    }

    /**
     * This method requests to the user the revelation arc properties of a link.
     * @param owner owner window that shows the dialog box.
     * @param link object that contains the revelation arc properties of the
     *            link and where changes will be saved.
     * @return true, if the user save the changes on probNode; otherwise, false.
     */
    private boolean requestRevelationArcValues (Window owner, Link link)
    {
        revelationArcDialog = new RevelationArcEditDialog (owner, link);
        return (revelationArcDialog.requestValues () == NodePropertiesDialog.OK_BUTTON);
    }

    // private boolean requestCostEffectiveness(Window owner,
    // String suffixTypeAnalysis, boolean isProbabilistic) {
    // costEffectivenessDialog = new CostEffectivenessDialog(owner);
    // costEffectivenessDialog.showSimulationsNumberElements(isProbabilistic);
    // return (costEffectivenessDialog.requestData(probNet.getName(),
    // suffixTypeAnalysis) == CostEffectivenessDialog.OK_BUTTON);
    // }
    /**
     * This method shows a dialog box with the additionalProperties of a link.
     * If some property has changed, insert a new undo point into the network
     * undo manager.
     * @param link
     */
    public void changeLinkProperties (VisualLink link)
    {
        /*
         * This method must be implemented to activate the possibility of
         * editing the additionalProperties of a link in future versions.
         */
    }

    /**
     * This method shows a dialog box with the additionalProperties of the
     * network. If some property has changed, insert a new undo point into the
     * network undo manager.
     */
    public void changeNetworkProperties ()
    {
        // TODO be careful with local pNESupport and extern pNESupport
        if (!requestNetworkProperties (Utilities.getOwner (this), probNet))
        {
            probNet.getPNESupport ().undoAndDelete ();
        }
    }

    /**
     * This method requests to the user the additionalProperties of a network.
     * @param owner window that owns the dialog box.
     * @param additionalProperties object that contains the additionalProperties
     *            of the network and where changes will be saved, if the user
     *            accepts the changes.
     * @param newNetwork specifies if the network whose additionalProperties are
     *            going to be edited is new.
     * @return true, if the user has made changes on the additionalProperties;
     *         otherwise, false.
     */
    public static boolean requestNetworkProperties (Window owner, ProbNet probNet)
    {
        NetworkPropertiesDialog dialogProperties = new NetworkPropertiesDialog (owner, probNet);
        return (dialogProperties.showProperties () == NetworkPropertiesDialog.OK_BUTTON);
    }

    /**
     * This method copies the selected nodes to the clipboard.
     * @param cut if true, the nodes copied to the clipboard are also removed.
     */
    public void exportToClipboard (boolean cut)
    {
        if (clipboardAssistant == null)
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                           stringDatabase.getString ("ClipboardNotSet.Text.Label"),
                                           stringDatabase.getString ("ErrorWindow.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            ArrayList<ProbNode> selectedNodes = new ArrayList<ProbNode> ();
            for (VisualNode visualNode : visualNetwork.getSelectedNodes ())
            {
                selectedNodes.add (visualNode.getProbNode ());
            }
            ArrayList<Link> selectedLinks = new ArrayList<Link> ();
            for (VisualLink visualLink : visualNetwork.getSelectedLinks ())
            {
                selectedLinks.add (visualLink.getLink ());
            }
            SelectedContent copiedContent = new SelectedContent (selectedNodes, selectedLinks);
            if (!copiedContent.isEmpty ())
            {
                clipboardAssistant.copyToClipboard (copiedContent);
                if (cut)
                {
                    removeSelectedObjects ();
                }
            }
        }
    }

    /**
     * This method imports the content from the clipboard and creates it in the
     * network.
     */
    public void pasteFromClipboard ()
    {
        if (clipboardAssistant == null)
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                           stringDatabase.getString ("ClipboardNotSet.Text.Label"),
                                           stringDatabase.getString ("ErrorWindow.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            if (clipboardAssistant.isThereDataStored ())
            {
                visualNetwork.setSelectedAllObjects (false);
                SelectedContent clipboardContent = clipboardAssistant.paste ();
                PasteEdit pasteEdit = new PasteEdit (visualNetwork, clipboardContent);
                try
                {
                    probNet.doEdit (pasteEdit);
                    // Set the nodes and links we just pasted as selected
                    SelectedContent pastedContent = pasteEdit.getPastedContent ();
                    for (ProbNode node : pastedContent.getNodes ())
                    {
                        visualNetwork.setSelectedNode (node.getName (), true);
                    }
                    for (Link link : pastedContent.getLinks ())
                    {
                        visualNetwork.setSelectedLink (link, true);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace ();
                    JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                   stringDatabase.getString ("CannotPasteAllNodes.Text.Label"),
                                                   stringDatabase.getString ("ErrorWindow.Title.Label"),
                                                   JOptionPane.WARNING_MESSAGE);
                }
                adjustPanelDimension ();
                repaint ();
            }
        }
    }

    /**
     * This method says if there is data stored in the clipboard.
     * @return true if there is data stored in the clipboard; otherwise, false.
     */
    public boolean isThereDataStored ()
    {
        return (clipboardAssistant != null) ? clipboardAssistant.isThereDataStored () : false;
    }

    /**
     * This method imposes a policy in a decision node.
     */
    public void imposePolicyInNode ()
    {
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            if (node.getProbNode ().getNodeType () == NodeType.DECISION)
            {
                ProbNode probNode = node.getProbNode ();
                // TODO manage other kind of policy types from the interface
                probNode.setPolicyType (PolicyType.OPTIMAL);
                List<Variable> variables = new ArrayList<Variable> ();
                // it is added first conditioned variable
                variables.add (node.getProbNode ().getVariable ());
                List<ProbNode> probNodes = probNode.getProbNet ().getProbNodes ();
                for (ProbNode possibleParent : probNodes)
                {
                    if (probNode.isParent (possibleParent))
                    {
                        variables.add (possibleParent.getVariable ());
                    }
                }
                UniformPotential policy = new UniformPotential (
                                                                variables,
                                                                PotentialRole.POLICY);
                List<Potential> policies = new ArrayList<Potential> ();
                policies.add (policy);
                probNode.setPotentials (policies);
                PotentialEditDialog imposePolicyDialog = new PotentialEditDialog (
                                                                                  Utilities.getOwner (this),
                                                                                  probNode, false);
                imposePolicyDialog.setTitle ("ImposePolicydialog.Title.Label");
                if (imposePolicyDialog.requestValues () == NodePropertiesDialog.OK_BUTTON)
                {
                    // change its color
                    ((VisualDecisionNode) node).setHasPolicy (true);
                    networkChanged = true;
                }
                else
                { // if user cancels policy imposition then no potential is
                  // restored to the probnode
                    List<Potential> noPolicy = new ArrayList<Potential> ();
                    probNode.setPotentials (noPolicy);
                }
            }
        }
        setSelectedAllNodes (false);
        repaint ();
    }

    /**
     * This method edits an imposed policy of a decision node.
     */
    public void editNodePolicy ()
    {
        System.out.println ("Pulsada la opción 'Editar Política'"); // ...Borrar
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            if (node.getProbNode ().getNodeType () == NodeType.DECISION)
            {
                ProbNode probNode = node.getProbNode ();
                // TODO manage other kind of policy types from the interface
                // probNode.setPolicyType(PolicyType.OPTIMAL);
                // Potential imposedPolicy = probNode.getPotentials ().get (0);
                PotentialEditDialog imposePolicyDialog = new PotentialEditDialog (
                                                                                  Utilities.getOwner (this),
                                                                                  probNode, false);
                if (imposePolicyDialog.requestValues () == NodePropertiesDialog.OK_BUTTON)
                {
                    // change it colour
                    ((VisualDecisionNode) node).setHasPolicy (true);
                    networkChanged = true;
                }
            }
        }
        setSelectedAllNodes (false);
        repaint ();
    }

    /**
     * This method removes an imposed policy from a decision node.
     */
    public void removePolicyFromNode ()
    {
        System.out.println ("Pulsada la opción 'Eliminar Política'"); // ...Borrar
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            if (node.getProbNode ().getNodeType () == NodeType.DECISION)
            {
                ProbNode probNode = node.getProbNode ();
                ArrayList<Potential> noPolicy = new ArrayList<> ();
                probNode.setPotentials (noPolicy);
                ((VisualDecisionNode) node).setHasPolicy (false);
            }
        }
        networkChanged = true;
        setSelectedAllNodes (false);
        repaint ();
    }

    /**
     * This method shows the expected utility of a decision node.
     */
    public void showExpectedUtilityOfNode ()
    {
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            ProbNode probNode = node.getProbNode ();
            try
            {
                // Potential expectedUtility = null;// =
                // inferenceAlgorithm.getExpectedtedUtility(node.getProbNode().getVariable());
                Potential expectedUtility;
                expectedUtility = inferenceAlgorithm.getExpectedUtilities (probNode.getVariable ());
                ProbNode dummyNode = new ProbNode (new ProbNet (), probNode.getVariable (),
                                                   probNode.getNodeType ());
                dummyNode.setPotential (expectedUtility);
                PotentialEditDialog expectedUtilityDialog = new PotentialEditDialog (
                                                                                     Utilities.getOwner (this),
                                                                                     dummyNode,
                                                                                     false, true);
                expectedUtilityDialog.setTitle ("ExpectedUtilityDialog.Title.Label");
                expectedUtilityDialog.requestValues ();
            }
            catch (IncompatibleEvidenceException | UnexpectedInferenceException e)
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               "ERROR\n" + e.getMessage (), e.getMessage (),
                                               JOptionPane.ERROR_MESSAGE);
                e.printStackTrace ();
            }
        }
        networkChanged = false;
        setSelectedAllNodes (false);
        repaint ();
        setSelectedAllNodes (false);
        repaint ();
    }

    /**
     * This method shows the optimal policy for a decision node.
     */
    public void showOptimalPolicyOfNode ()
    {
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            ProbNet dummyProbNet = new ProbNet ();
            ProbNode dummy = null;
            try
            {
                // Potential optimalPolicy =
                // inferenceAlgorithm.getOptimizedPolicies().get(node.getProbNode().getVariable());
                Potential optimalPolicy = inferenceAlgorithm.getOptimizedPolicy (node.getProbNode ().getVariable ());
                dummyProbNet.addPotential (optimalPolicy);
                Variable conditionedVariable = optimalPolicy.getVariable (0);
                dummy = dummyProbNet.getProbNode (conditionedVariable);
                dummy.setNodeType (NodeType.DECISION);
                dummy.setPolicyType (PolicyType.OPTIMAL);
                for (Variable variable : optimalPolicy.getVariables ())
                {
                    if (variable.equals (conditionedVariable))
                    {
                        continue;
                    }
                    try
                    {
                        dummyProbNet.addLink (variable, conditionedVariable, true);
                    }
                    catch (NodeNotFoundException e)
                    {
                        throw new RuntimeException ("Node not found: " + e.getMessage ());
                    }
                }
                PotentialEditDialog optimalPolicyDialog = new PotentialEditDialog (
                                                                                   Utilities.getOwner (this),
                                                                                   dummy, false,
                                                                                   true);
                optimalPolicyDialog.setTitle ("OptimalPolicyDialog.Title.Label");
                optimalPolicyDialog.requestValues ();
            }
            catch (IncompatibleEvidenceException | UnexpectedInferenceException e)
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               "ERROR\n" + e.getMessage (), e.getMessage (),
                                               JOptionPane.ERROR_MESSAGE);
                e.printStackTrace ();
            }
        }
        networkChanged = false;
        setSelectedAllNodes (false);
        repaint ();
        setSelectedAllNodes (false);
        repaint ();
    }

    /**
     * This method expands a node.
     */
    public void expandNode ()
    {
        VisualNode visualNode = null;
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes ();
        if (selectedNodes.size () > 0)
        {
            for (int i = 0; i < selectedNodes.size (); i++)
            {
                visualNode = selectedNodes.get (i);
                if (!(visualNode.isExpanded ()))
                {
                    visualNode.setExpanded (true);
                    visualNetwork.setSelectedNode (visualNode, false);
                }
                repaint ();
            }
        }
    }

    /**
     * This method contracts a node.
     */
    public void contractNode ()
    {
        VisualNode visualNode = null;
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes ();
        if (selectedNodes.size () > 0)
        {
            for (int i = 0; i < selectedNodes.size (); i++)
            {
                visualNode = selectedNodes.get (i);
                if (visualNode.isExpanded ())
                {
                    visualNode.setExpanded (false);
                    visualNetwork.setSelectedNode (visualNode, false);
                }
                repaint ();
            }
        }
    }

    /**
     * This method adds a finding in a node.
     */
    public void addFinding ()
    {
        setPropagationActive (isAutomaticPropagation ());
        Graphics2D g = (Graphics2D) getGraphics ();
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            EvidenceCase currentEvidence = (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE) ? getCurrentEvidenceCase ()
                                                                                                                  : preResolutionEvidence;
            Finding finding = currentEvidence.getFinding (node.getProbNode ().getVariable ());
            NodeAddFindingDialog nodeAddFinding = new NodeAddFindingDialog (
                                                                            Utilities.getOwner (this),
                                                                            node, finding, g, this);
            nodeAddFinding.setVisible (true);
        }
        repaint ();
        setSelectedAllNodes (false);
        networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsFindingsDependent (networkPanel);
    }

    /**
     * This method removes findings from selected nodes.
     */
    public void removeFinding ()
    {
        setPropagationActive (isAutomaticPropagation ());
        VisualNode node = null;
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes ();
        for (int i = 0; i < selectedNodes.size (); i++)
        {
            node = selectedNodes.get (i);
            Variable variable = node.getProbNode ().getVariable ();
            try
            {
                if (networkPanel.getWorkingMode () == NetworkPanel.EDITION_WORKING_MODE)
                {
                    if (node.isPreResolutionFinding ()
                        && preResolutionEvidence.getFinding (variable) != null)
                    {
                        preResolutionEvidence.removeFinding (variable);
                        node.setPreResolutionFinding (false);
                    }
                }
                else
                {
                    if (node.isPreResolutionFinding ())
                    {
                        JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                       "This node has a Pre-Resolution Finding that cannot be modified in Inference Mode.",
                                                       stringDatabase.getString ("ErrorWindow.Title.Label"),
                                                       JOptionPane.ERROR_MESSAGE);
                        // TODO //...asaez...Internacionalizar la sentencia
                    }
                    else if (node.isPostResolutionFinding ()
                             && postResolutionEvidence.get (currentCase).getFinding (variable) != null)
                    {
                        postResolutionEvidence.get (currentCase).removeFinding (variable);
                        node.setPostResolutionFinding (false);
                    }
                }
            }
            catch (NoFindingException exc)
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               "ERROR\n"
                                                       + stringDatabase.getString ("ExceptionNoFinding.Text.Label")
                                                       + "\n\n" + exc.getMessage (),
                                               stringDatabase.getString ("ExceptionNoFinding.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
        if ((propagationActive)
            && (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE))
        {
            if (doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
        }
        networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsFindingsDependent (networkPanel);
        setSelectedAllNodes (false);
        repaint ();
    }

    /**
     * This method returns the current Evidence Case.
     * @return the current Evidence Case.
     */
    public EvidenceCase getCurrentEvidenceCase ()
    {
        return postResolutionEvidence.get (currentCase);
    }

    /**
     * This method returns the Evidence Case.
     * @param caseNumber the number of the case to be returned.
     * @return the selected Evidence Case.
     */
    public EvidenceCase getEvidenceCase (int caseNumber)
    {
        return postResolutionEvidence.get (caseNumber);
    }

    /**
     * This method returns list of evidence cases
     * @return the list of Evidence Cases.
     */
    public ArrayList<EvidenceCase> getEvidence ()
    {
        ArrayList<EvidenceCase> evidence = new ArrayList<EvidenceCase> ();
        for (EvidenceCase postResolutionEvidenceCase : postResolutionEvidence)
        {
            if (!postResolutionEvidenceCase.isEmpty ())
            {
                evidence.add (postResolutionEvidenceCase);
            }
        }
        if (!evidence.isEmpty () || !preResolutionEvidence.isEmpty ())
        {
            evidence.add (0, preResolutionEvidence);
        }
        return evidence;
    }

    /**
     * This method returns the number of the Evidence Case that is currently
     * selected
     * @return the number of the current Evidence Case.
     */
    public int getCurrentCase ()
    {
        return currentCase;
    }

    public EvidenceCase getPreResolutionEvidence ()
    {
        return preResolutionEvidence;
    }

    /**
     * This method sets which is the current evidence case.
     * @param currentCase new value for the current evidence case.
     */
    public void setCurrentCase (int currentCase)
    {
        this.currentCase = currentCase;
    }

    /**
     * This method returns the number of Evidence Cases that the ArrayList is
     * currently holding .
     * @return the number of Evidence Cases in the ArrayList.
     */
    public int getNumberOfCases ()
    {
        return postResolutionEvidence.size ();
    }

    /**
     * This method returns a boolean indicating if the case number passed as
     * parameter is currently compiled.
     * @param caseNumber number of the evidence case.
     * @return the compilation state of the case.
     */
    public boolean getEvidenceCasesCompilationState (int caseNumber)
    {
        return evidenceCasesCompilationState.get (caseNumber);
    }

    /**
     * This method sets which is the compilation state of the case.
     * @param caseNumber number of the evidence case to be set.
     * @param value true if compiled; false otherwise.
     */
    public void setEvidenceCasesCompilationState (int caseNumber, boolean value)
    {
        this.evidenceCasesCompilationState.set (caseNumber, value);
    }

    /**
     * This method sets the list of evidence cases
     * @param owner window that owns the dialog box.
     */
    public void setEvidence (EvidenceCase preResolutionEvidence,
                             List<EvidenceCase> postResolutionInference)
    {
        this.postResolutionEvidence = (postResolutionInference == null) ? new ArrayList<EvidenceCase> ()
                                                                       : postResolutionInference;
        this.preResolutionEvidence = (preResolutionEvidence == null) ? new EvidenceCase ()
                                                                    : preResolutionEvidence;
        if (postResolutionEvidence.isEmpty ())
        {
            this.postResolutionEvidence.add (new EvidenceCase ());
        }
        currentCase = this.postResolutionEvidence.size () - 1;
        // Update visual info on evidence
        for (VisualNode node : visualNetwork.getAllNodes ())
        {
            node.setPostResolutionFinding (false);
        }
        for (EvidenceCase evidenceCase : postResolutionEvidence)
        {
            for (Finding finding : evidenceCase.getFindings ())
            {
                for (VisualNode node : visualNetwork.getAllNodes ())
                {
                    if (node.getProbNode ().getVariable ().equals (finding.getVariable ()))
                    {
                        node.setPostResolutionFinding (true);
                    }
                }
            }
        }
        for (VisualNode node : visualNetwork.getAllNodes ())
        {
            node.setPreResolutionFinding (false);
        }
        for (Finding finding : preResolutionEvidence.getFindings ())
        {
            for (VisualNode node : visualNetwork.getAllNodes ())
            {
                if (node.getProbNode ().getVariable ().equals (finding.getVariable ()))
                {
                    node.setPreResolutionFinding (true);
                }
            }
        }
        // Update evidenceCasesCompilationState
        evidenceCasesCompilationState.clear ();
        for (int i = 0; i < postResolutionEvidence.size (); ++i)
        {
            evidenceCasesCompilationState.add (false);
        }
    }

    /**
     * This method returns true if propagation type currently set is automatic;
     * false if manual.
     * @return true if the current propagation type is automatic.
     */
    public boolean isAutomaticPropagation ()
    {
        return automaticPropagation;
    }

    /**
     * This method sets the current propagation type.
     * @param automaticPropagation new value of the propagation type.
     */
    public void setAutomaticPropagation (boolean automaticPropagation)
    {
        this.automaticPropagation = automaticPropagation;
    }

    /**
     * This method returns the propagation status: true if propagation should be
     * done right now; false otherwise.
     * @return true if propagation should be done right now.
     */
    public boolean isPropagationActive ()
    {
        return propagationActive;
    }

    /**
     * This method sets the propagation status.
     * @param propagationActive new value of the propagation status.
     */
    public void setPropagationActive (boolean propagationActive)
    {
        this.propagationActive = propagationActive;
        this.visualNetwork.setPropagationActive (propagationActive);
    }

    /**
     * This method returns the associated network panel.
     * @return the associated network panel.
     */
    public NetworkPanel getNetworkPanel ()
    {
        return networkPanel;
    }

    /**
     * This method changes the current expansion threshold.
     * @param expansionThreshold new value of the expansion threshold.
     */
    public void setExpansionThreshold (double expansionThreshold)
    {
        this.currentExpansionThreshold = expansionThreshold;
    }

    /**
     * This method returns the current expansion threshold.
     * @return the value of the current expansion threshold.
     */
    public double getExpansionThreshold ()
    {
        return currentExpansionThreshold;
    }

    /**
     * This method updates the expansion state (expanded/contracted) of the
     * nodes. It is used in transitions from edition to inference mode and vice
     * versa, and also when the user modifies the current expansion threshold in
     * the Inference tool bar
     * @param newWorkingMode new value of the working mode.
     */
    public void updateNodesExpansionState (int newWorkingMode)
    {
        if (newWorkingMode == NetworkPanel.EDITION_WORKING_MODE)
        {
            VisualNode visualNode = null;
            List<VisualNode> allNodes = visualNetwork.getAllNodes ();
            if (allNodes.size () > 0)
            {
                for (int i = 0; i < allNodes.size (); i++)
                {
                    visualNode = allNodes.get (i);
                    if (visualNode.isExpanded ())
                    {
                        visualNode.setExpanded (false);
                    }
                    repaint ();
                }
                repaint ();
            }
        }
        else if (newWorkingMode == NetworkPanel.INFERENCE_WORKING_MODE)
        {
            VisualNode visualNode = null;
            List<VisualNode> allNodes = visualNetwork.getAllNodes ();
            if (allNodes.size () > 0)
            {
                for (int i = 0; i < allNodes.size (); i++)
                {
                    visualNode = allNodes.get (i);
                    if (visualNode.getProbNode ().getRelevance () >= getExpansionThreshold ())
                    {
                        visualNode.setExpanded (true);
                    }
                    else
                    {
                        visualNode.setExpanded (false);
                    }
                    repaint ();
                }
            }
        }
    }

    /**
     * This method updates the value of each state for each node in the network
     * with the current individual probabilities.
     */
    public void updateIndividualProbabilities ()
    {
        // if some visualNode has a number of values different from the
        // number of evidence cases in memory, we need to recreate its
        // visual states and consider that the network has been changed.
        for (VisualNode visualNode : visualNetwork.getAllNodes ())
        {
            InnerBox innerBox = visualNode.getInnerBox ();
            VisualState visualState = null;
            if (innerBox instanceof FSVariableBox)
            {
                visualState = ((FSVariableBox) innerBox).getVisualState (0);
            }
            else if (innerBox instanceof ExpectedValueBox)
            {
                visualState = ((ExpectedValueBox) innerBox).getVisualState ();
            }
            if (visualState.getNumberOfValues () != postResolutionEvidence.size ())
            {
                if (innerBox instanceof FSVariableBox)
                {
                    ((FSVariableBox) innerBox).recreateVisualStates (postResolutionEvidence.size ());
                }
                else if (innerBox instanceof ExpectedValueBox)
                {
                    ((ExpectedValueBox) innerBox).recreateVisualState (postResolutionEvidence.size ());
                }
                networkChanged = true;
                for (int i = 0; i < postResolutionEvidence.size (); i++)
                {
                    evidenceCasesCompilationState.set (i, false);
                }
            }
        }
        if ((propagationActive)
            && (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE))
        {
            // if the network has been changed, propagation must be done in
            // each evidence case in memory. Otherwise, only propagation in
            // current case is needed.
            if (networkChanged)
            {
                for (int i = 0; i < postResolutionEvidence.size (); i++)
                {
                    doPropagation (getEvidenceCase (i), i);
                }
                updateNodesFindingState (postResolutionEvidence.get (currentCase));
                networkChanged = false;
            }
            else
            {
                if (evidenceCasesCompilationState.get (currentCase) == false)
                {
                    if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
                }
            }
        }
        else if (evidenceCasesCompilationState.get (currentCase) == false)
        {
            // Even if propagation mode is manual, a propagation should be
            // done the first time that inference mode is selected
            doPropagation (postResolutionEvidence.get (currentCase), currentCase);
        }
        updateAllVisualStates ("", currentCase);
        repaint ();
    }

    /**
     * This method removes all the findings established in the current evidence
     * case.
     */
    public void removeAllFindings ()
    {
        setPropagationActive (isAutomaticPropagation ());
        List<VisualNode> visualNodes = visualNetwork.getAllNodes ();
        for (int i = 0; i < visualNodes.size (); i++)
        {
            visualNodes.get (i).setPostResolutionFinding (false);
        }
        List<Finding> findings = postResolutionEvidence.get (currentCase).getFindings ();
        for (int i = 0; i < findings.size (); i++)
        {
            try
            {
                postResolutionEvidence.get (currentCase).removeFinding (findings.get (i).getVariable ());
            }
            catch (NoFindingException exc)
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               "ERROR\n"
                                                       + stringDatabase.getString ("ExceptionNoFinding.Text.Label")
                                                       + "\n\n" + exc.getMessage (),
                                               stringDatabase.getString ("ExceptionNoFinding.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
        if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
        networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        setSelectedAllNodes (false);
        networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsFindingsDependent (networkPanel);
    }

    /**
     * This method removes the findings that a node could have in all the
     * evidence cases in memory. It is invoked when a change takes place in
     * properties or probabilities of a the node
     * @param node the node in which to remove the findings.
     */
    public void removeNodeEvidenceInAllCases (ProbNode node)
    {
        for (int i = 0; i < postResolutionEvidence.size (); i++)
        {
            List<Finding> findings = postResolutionEvidence.get (i).getFindings ();
            for (int j = 0; j < findings.size (); j++)
            {
                try
                {
                    if (node.getVariable () == (findings.get (j).getVariable ()))
                    {
                        postResolutionEvidence.get (i).removeFinding (findings.get (j).getVariable ());
                        if (isAutomaticPropagation () && (inferenceAlgorithm != null))
                        {
                            if (!doPropagation (postResolutionEvidence.get (i), i)) setPropagationActive (false);
                        }
                        if (i == currentCase)
                        {
                            List<VisualNode> visualNodes = visualNetwork.getAllNodes ();
                            for (int k = 0; k < visualNodes.size (); k++)
                            {
                                if (visualNodes.get (k).getProbNode () == node)
                                {
                                    visualNodes.get (k).setPostResolutionFinding (false);// ...asaez....PENDIENTE........
                                }
                            }
                        }
                    }
                }
                catch (NoFindingException exc)
                {
                    JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                   "ERROR\n"
                                                           + stringDatabase.getString ("ExceptionNoFinding.Text.Label")
                                                           + "\n\n" + exc.getMessage (),
                                                   stringDatabase.getString ("ExceptionNoFinding.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        setSelectedAllNodes (false);
        networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsFindingsDependent (networkPanel);
        repaint ();
    }

    /**
     * This method returns true if there are any finding in the current evidence
     * case.
     * @return true if the current evidence case has at least one finding.
     */
    public boolean areThereFindingsInCase ()
    {
        boolean areFindings = false;
        List<Finding> findings = postResolutionEvidence.get (currentCase).getFindings ();
        if (findings != null)
        {
            if (findings.size () > 0)
            {
                areFindings = true;
            }
        }
        return areFindings;
    }

    /**
     * This method returns the number of the Evidence Case that is currently
     * selected
     * @param visualState the visual state in which the finding is going to be
     *            set.
     */
    public void setNewFinding (VisualNode visualNode, VisualState visualState)
    {
        boolean isInferenceMode = networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE;
        EvidenceCase evidenceCase = (isInferenceMode) ? postResolutionEvidence.get (currentCase)
                                                     : preResolutionEvidence;
        setPropagationActive (isAutomaticPropagation ());
        Variable variable = visualNode.getProbNode ().getVariable ();
        boolean nodeAlreadyHasFinding = evidenceCase.getFinding (variable) != null;
        int oldState = -1;
        if (nodeAlreadyHasFinding)
        {
            // There is already a finding in the node
            oldState = evidenceCase.getState (variable);
            if (oldState == visualState.getStateNumber ())
            {
                // The finding is in the same state, therefore, remove evidence
                try
                {
                    evidenceCase.removeFinding (variable);
                    if (isInferenceMode)
                    {
                        visualNode.setPostResolutionFinding (false);
                    }
                }
                catch (NoFindingException exc)
                {
                    JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                   "ERROR\n"
                                                           + stringDatabase.getString ("ExceptionNoFinding.Text.Label")
                                                           + "\n\n" + exc.getMessage (),
                                                   stringDatabase.getString ("ExceptionNoFinding.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                }
            }
            else
            {
                // There is a finding in another state. Remove old, add new
                try
                {
                    evidenceCase.removeFinding (variable);
                    Finding finding = new Finding (variable, visualState.getStateNumber ());
                    evidenceCase.addFinding (finding);
                    if (isInferenceMode)
                    {
                        visualNode.setPostResolutionFinding (true);
                    }
                    else
                    {
                        visualNode.setPreResolutionFinding (true);
                    }
                }
                catch (NoFindingException exc)
                {
                    JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                   "ERROR\n"
                                                           + stringDatabase.getString ("ExceptionNoFinding.Text.Label")
                                                           + "\n\n" + exc.getMessage (),
                                                   stringDatabase.getString ("ExceptionNoFinding.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                }
                catch (InvalidStateException exc)
                {
                    JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                   "ERROR\n"
                                                           + stringDatabase.getString ("ExceptionInvalidState.Text.Label")
                                                           + "\n\n" + exc.getMessage (),
                                                   stringDatabase.getString ("ExceptionInvalidState.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                }
                catch (IncompatibleEvidenceException exc)
                {
                    JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                   "ERROR\n"
                                                           + stringDatabase.getString ("ExceptionIncompatibleEvidence.Text.Label")
                                                           + "\n\n" + exc.getMessage (),
                                                   stringDatabase.getString ("ExceptionIncompatibleEvidence.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception exc)
                {
                    JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                   "ERROR" + "\n\n" + exc.getMessage (),
                                                   stringDatabase.getString ("ExceptionGeneric.Title.Label"),
                                                   JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else
        { // No finding previously in node, add
            Finding finding = new Finding (variable, visualState.getStateNumber ());
            try
            {
                evidenceCase.addFinding (finding);
                if (isInferenceMode)
                {
                    visualNode.setPostResolutionFinding (true);
                }
                else
                {
                    visualNode.setPreResolutionFinding (true);
                }
            }
            catch (InvalidStateException exc)
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               "ERROR\n"
                                                       + stringDatabase.getString ("ExceptionInvalidState.Text.Label")
                                                       + "\n\n" + exc.getMessage (),
                                               stringDatabase.getString ("ExceptionInvalidState.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
            }
            catch (IncompatibleEvidenceException exc)
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               "ERROR\n"
                                                       + stringDatabase.getString ("ExceptionIncompatibleEvidence.Text.Label")
                                                       + "\n\n" + exc.getMessage (),
                                               stringDatabase.getString ("ExceptionIncompatibleEvidence.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception exc)
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               "ERROR" + "\n\n" + exc.getMessage (),
                                               stringDatabase.getString ("ExceptionGeneric.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
        if (isInferenceMode)
        {
            evidenceCasesCompilationState.set (currentCase, false);
        }
        else
        {
            for (int i = 0; i < evidenceCasesCompilationState.size (); ++i)
            {
                evidenceCasesCompilationState.set (i, false);
            }
        }
        setSelectedAllNodes (false);
        networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
            && (isInferenceMode))
        {
            if (!doPropagation (evidenceCase, currentCase))
            // if propagation does not succeed, restore previous state
            {
                if (nodeAlreadyHasFinding)
                {
                    try
                    {
                        evidenceCase.removeFinding (variable);
                    }
                    catch (NoFindingException e)
                    {/* Not possible */
                    }
                    Finding finding = new Finding (variable, oldState);
                    try
                    {
                        evidenceCase.addFinding (finding);
                    }
                    catch (InvalidStateException e)
                    {/* Not possible */
                    }
                    catch (IncompatibleEvidenceException e)
                    {/* Not possible */
                    }
                }
                else
                {
                    try
                    {
                        evidenceCase.removeFinding (variable);
                    }
                    catch (NoFindingException e)
                    { /* Not possible */
                    }
                }
                if (isInferenceMode)
                {
                    visualNode.setPostResolutionFinding (nodeAlreadyHasFinding);
                }
                else
                {
                    visualNode.setPreResolutionFinding (nodeAlreadyHasFinding);
                }
            }
        }
        networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsFindingsDependent (networkPanel);
        networkPanel.getMainPanel ().getMainPanelMenuAssistant ().updateOptionsPropagationTypeDependent (networkPanel);// ..
        repaint ();
    }

    /**
     * Returns the inference algorithm assigned to the panel.
     * @return the inference algorithm assigned to the panel.
     */
    public InferenceAlgorithm getInferenceAlgorithm ()
    {
        return inferenceAlgorithm;
    }

    /**
     * Sets the inference algorithm assigned to the panel.
     * @param inferenceAlgorithm the inference Algorithm to be assigned to the
     *            panel.
     */
    public void setInferenceAlgorithm (InferenceAlgorithm inferenceAlgorithm)
    {
        this.inferenceAlgorithm = inferenceAlgorithm;
    }

    /**
     * This method does the propagation of the evidence in the network
     * @param evidenceCase the evidence case with which the propagation must be
     *            done.
     * @param caseNumber number of this evidence case.
     */
    public boolean doPropagation (EvidenceCase evidenceCase, int caseNumber)
    {
        HashMap<Variable, TablePotential> individualProbabilities = null;
        boolean propagationSucceded = false;
        try
        {
            long start = System.currentTimeMillis ();
            try
            {
                inferenceAlgorithm = inferenceManager.getDefaultInferenceAlgorithm (probNet);
                if (inferenceAlgorithm == null)
                {
                    throw new UnsupportedOperationException ();
                }
                inferenceAlgorithm.setPreResolutionEvidence (preResolutionEvidence);
                inferenceAlgorithm.setPostResolutionEvidence (evidenceCase);
                calculateMinAndMaxUtilityRanges ();
                individualProbabilities = inferenceAlgorithm.getProbsAndUtilities ();
            }
            catch (OutOfMemoryError e)
            {
                if (!approximateInferenceWarningGiven)
                {
                    JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                   stringDatabase.getString ("NotEnoughMemoryForExactInference.Text"),
                                                   stringDatabase.getString ("NotEnoughMemoryForExactInference.Title"),
                                                   JOptionPane.WARNING_MESSAGE);
                    approximateInferenceWarningGiven = true;
                }
                inferenceAlgorithm = inferenceManager.getDefaultApproximateAlgorithm (probNet);
                inferenceAlgorithm.setPostResolutionEvidence (evidenceCase);
                individualProbabilities = inferenceAlgorithm.getProbsAndUtilities ();
            }
            catch (NotEvaluableNetworkException e)
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this), e.getMessage ());
                return false;
            }
            long elapsedTimeMillis = System.currentTimeMillis () - start;
            System.out.println ("Inference took " + elapsedTimeMillis + " milliseconds.");
            updateNodesFindingState (evidenceCase);
            paintInferenceResults (caseNumber, individualProbabilities);
            propagationSucceded = true;
        }
        catch (IncompatibleEvidenceException e)
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this), "Incompatible evidence",
                                           "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace ();
        }
        catch (UnsupportedOperationException e)
        {
            JOptionPane.showMessageDialog (null,
                                           "ERROR\n"
                                                   + stringDatabase.getString ("NoPropagationCanBeDoneMessage1.Text.Label")
                                                   + "\n"
                                                   + stringDatabase.getString ("NoPropagationCanBeDoneMessage2.Text.Label")
                                                   + "\n\n" + probNet.getNetworkType (),
                                           stringDatabase.getString ("NoPropagationCanBeDoneMessage.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this), "Error during inference",
                                           "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace ();
        }
        evidenceCasesCompilationState.set (caseNumber, propagationSucceded);
        repaint ();
        return propagationSucceded;
    }

    // This commented method computes the exact ranges of the utility functions.
    // However, we are using an approximation in the method currently offered by
    // this class.
    /**
     * Calculates minUtilityRange and maxUtilityRange fields.
     */
    /*
     * private void () { TablePotential auxF; ArrayList<Variable>
     * utilityVariables = probNet .getVariables(NodeType.UTILITY); for (Variable
     * utility : utilityVariables) { auxF = probNet.getUtilityFunction(utility);
     * minUtilityRange.put(utility, Tools.min(auxF.values));
     * maxUtilityRange.put(utility, Tools.max(auxF.values)); } }
     */
    /**
     * Calculates minUtilityRange and maxUtilityRange fields. It is an
     * approximate implementation. The correct computation is given by a method
     * with the same name, but commented above.
     * @throws NonProjectablePotentialException
     */
    private void calculateMinAndMaxUtilityRanges ()
        throws NonProjectablePotentialException
    {
        List<Variable> utilityVariables = probNet.getVariables (NodeType.UTILITY);
        for (Variable utility : utilityVariables)
        {
            ProbNode probNode = probNet.getProbNode (utility);
            minUtilityRange.put (utility, probNode.getApproximateMinimumUtilityFunction ());
            maxUtilityRange.put (utility, probNode.getApproximateMaximumUtilityFunction ());
        }
    }

    /**
     * This method fills the visualStates with the proper values to be
     * represented after the evaluation of the evidence case
     * @param caseNumber number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each
     *            variable.
     */
    private void paintInferenceResults (int caseNumber,
                                        HashMap<Variable, TablePotential> individualProbabilities)
    {
        for (VisualNode visualNode : visualNetwork.getAllNodes ())
        {
            ProbNode probNode = visualNode.getProbNode ();
            Variable variable = probNode.getVariable ();
            switch (probNode.getNodeType ())
            {
                case CHANCE :
                case DECISION :
                    paintInferenceResultsChanceOrDecisionNode (caseNumber, individualProbabilities,
                                                               variable, visualNode);
                    break;
                case UTILITY :
                    paintInferenceResultsUtilityNode (caseNumber, individualProbabilities,
                                                      variable, visualNode);
                    break;
            }
        }
        repaint ();
    }

    /**
     * This method fills the visualStates of a utility node with the proper
     * values to be represented after the evaluation of the evidence case
     * @param caseNumber number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each
     *            variable.
     * @param variable
     * @param visualNode
     */
    private void paintInferenceResultsUtilityNode (int caseNumber,
                                                   HashMap<Variable, TablePotential> individualProbabilities,
                                                   Variable variable,
                                                   VisualNode visualNode)
    {
        if ((visualNode.getInnerBox ()) instanceof ExpectedValueBox)
        {
            // It is a utility node
            ExpectedValueBox innerBox = (ExpectedValueBox) visualNode.getInnerBox ();
            VisualState visualState = innerBox.getVisualState ();
            visualState.setStateValue (caseNumber, individualProbabilities.get (variable).values[0]);
            innerBox.setMinUtilityRange (minUtilityRange.get (variable));
            innerBox.setMaxUtilityRange (maxUtilityRange.get (variable));
        }
    }

    /**
     * This method fills the visualStates of a chance or decision node with the
     * proper values to be represented after the evaluation of the evidence case
     * @param caseNumber number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each
     *            variable.
     * @param variable
     * @param visualNode
     */
    private void paintInferenceResultsChanceOrDecisionNode (int caseNumber,
                                                            HashMap<Variable, TablePotential> individualProbabilities,
                                                            Variable variable,
                                                            VisualNode visualNode)
    {
        Potential potential = individualProbabilities.get (variable);
        if (potential.getPotentialType () == PotentialType.TABLE)
        {
            TablePotential tablePotential = (TablePotential) potential;
            if (tablePotential.getNumVariables () == 1)
            {
                double[] values = tablePotential.getValues ();
                if ((visualNode.getInnerBox ()) instanceof FSVariableBox)
                {
                    FSVariableBox innerBox = (FSVariableBox) visualNode.getInnerBox ();
                    for (int i = 0; i < innerBox.getNumStates (); i++)
                    {
                        VisualState visualState = innerBox.getVisualState (i);
                        visualState.setStateValue (caseNumber, values[i]);
                    }
                }
                // PROVISIONAL2: Currently the propagation
                // algorithm is returning a TablePotential
                // with 0 variables when the node has a Uniform
                // relation
            }
            else if (tablePotential.getNumVariables () == 0)
            {
                if ((visualNode.getInnerBox ()) instanceof FSVariableBox)
                {
                    FSVariableBox innerBox = (FSVariableBox) visualNode.getInnerBox ();
                    for (int i = 0; i < innerBox.getNumStates (); i++)
                    {
                        VisualState visualState = innerBox.getVisualState (i);
                        visualState.setStateValue (caseNumber, (1.0 / innerBox.getNumStates ()));
                    }
                }
                visualNode.setPostResolutionFinding (false);
                // END OF
                // PROVISIONAL2.............asaez...Comprobar si es innecesario
                // este Provisional2............
            }
            else
            {
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               "ERROR\n"
                                                       + "Table Potential of "
                                                       + variable.getName ()
                                                       + " has "
                                                       + tablePotential.getNumVariables ()
                                                       + " variables.\n It cannot be treated by now",
                                               "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method updates the "finding state" of each node
     * @param evidenceCase the evidence case with which the update must be done.
     */
    public void updateNodesFindingState (EvidenceCase evidenceCase)
    {
        for (VisualNode visualNode : visualNetwork.getAllNodes ())
        {
            visualNode.setPreResolutionFinding (false);
            visualNode.setPostResolutionFinding (false);
        }
        for (Finding finding : evidenceCase.getFindings ())
        {
            Variable variable = finding.getVariable ();
            for (VisualNode visualNode : visualNetwork.getAllNodes ())
            {
                if (variable.getName ().equals (visualNode.getProbNode ().getName ()))
                {
                    visualNode.setPostResolutionFinding (true);
                }
            }
        }
        for (Finding finding : preResolutionEvidence.getFindings ())
        {
            Variable variable = finding.getVariable ();
            for (VisualNode visualNode : visualNetwork.getAllNodes ())
            {
                if (variable.getName ().equals (visualNode.getProbNode ().getName ()))
                {
                    visualNode.setPreResolutionFinding (true);
                }
            }
        }
        repaint ();
    }

    public void temporalEvolution ()
    {
        VisualNode node = null;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes ();
        if (selectedNode.size () == 1)
        {
            node = selectedNode.get (0);
            new TraceTemporalEvolutionDialog (Utilities.getOwner (this), node.getProbNode (), preResolutionEvidence);
            setSelectedAllNodes (false);
            repaint ();
        }
    }

    /**
     * This method creates a new evidence case
     */
    public void createNewEvidenceCase ()
    {
        try
        {
            EvidenceCase newEvidenceCase = new EvidenceCase ();
            EvidenceCase currentEvidenceCase = getCurrentEvidenceCase ();
            List<Finding> currentFindings = currentEvidenceCase.getFindings ();
            for (int i = 0; i < currentFindings.size (); i++)
            {
                newEvidenceCase.addFinding (currentFindings.get (i));
            }
            addNewEvidenceCase (newEvidenceCase);
        }
        catch (InvalidStateException exc)
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                           "ERROR\n"
                                                   + stringDatabase.getString ("ExceptionInvalidState.Text.Label")
                                                   + "\n\n" + exc.getMessage (),
                                           stringDatabase.getString ("ExceptionInvalidState.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
        catch (IncompatibleEvidenceException exc)
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                           "ERROR\n"
                                                   + stringDatabase.getString ("ExceptionIncompatibleEvidence.Text.Label")
                                                   + "\n\n" + exc.getMessage (),
                                           stringDatabase.getString ("ExceptionIncompatibleEvidence.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                           "ERROR" + "\n\n" + exc.getMessage (),
                                           stringDatabase.getString ("ExceptionGeneric.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method adds a new evidence case
     */
    public void addNewEvidenceCase (EvidenceCase newEvidenceCase)
    {
        setPropagationActive (isAutomaticPropagation ());
        postResolutionEvidence.add (newEvidenceCase);
        currentCase = (postResolutionEvidence.size () - 1);
        evidenceCasesCompilationState.add (currentCase, false);
        updateAllVisualStates ("new", currentCase);
        networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        setSelectedAllNodes (false);
        if (isPropagationActive ()
            && networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE)
        {
            if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
        }
    }

    /**
     * This method makes the first evidence case to be the current
     */
    public void goToFirstEvidenceCase ()
    {
        currentCase = 0;
        updateAllVisualStates ("", currentCase);
        networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        setSelectedAllNodes (false);
        if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
            && (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE))
        {
            if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
        }
        else
        {
            updateNodesFindingState (postResolutionEvidence.get (currentCase));
        }
    }

    /**
     * This method makes the previous evidence case to be the current
     */
    public void goToPreviousEvidenceCase ()
    {
        if (currentCase > 0)
        {
            currentCase--;
            updateAllVisualStates ("", currentCase);
            networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
            setSelectedAllNodes (false);
            if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
                && (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE))
            {
                if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
            }
            else
            {
                updateNodesFindingState (postResolutionEvidence.get (currentCase));
            }
        }
        else
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                           "ERROR\n"
                                                   + stringDatabase.getString ("NoPreviousEvidenceCaseMessage.Text.Label"),
                                           stringDatabase.getString ("NoPreviousEvidenceCaseMessage.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method makes the next evidence case to be the current
     */
    public void goToNextEvidenceCase ()
    {
        if (currentCase < (postResolutionEvidence.size () - 1))
        {
            currentCase++;
            updateAllVisualStates ("", currentCase);
            networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
            setSelectedAllNodes (false);
            if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
                && (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE))
            {
                if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
            }
            else
            {
                updateNodesFindingState (postResolutionEvidence.get (currentCase));
            }
        }
        else
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                           "ERROR\n"
                                                   + stringDatabase.getString ("NoNextEvidenceCaseMessage.Text.Label"),
                                           stringDatabase.getString ("NoNextEvidenceCaseMessage.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method makes the last evidence case to be the current
     */
    public void goToLastEvidenceCase ()
    {
        currentCase = (postResolutionEvidence.size () - 1);
        updateAllVisualStates ("", currentCase);
        networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        setSelectedAllNodes (false);
        if ((propagationActive) && (evidenceCasesCompilationState.get (currentCase) == false)
            && (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE))
        {
            if (doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
        }
        else
        {
            updateNodesFindingState (postResolutionEvidence.get (currentCase));
        }
    }

    /**
     * This method clears out all the evidence cases. It returns to an 'initial
     * state' in which there is only an initial evidence case with no findings
     * (corresponding to prior probabilities)
     */
    public void clearOutAllEvidenceCases ()
    {
        setPropagationActive (isAutomaticPropagation ());
        postResolutionEvidence.clear ();
        evidenceCasesCompilationState.clear ();
        EvidenceCase newEvidenceCase = new EvidenceCase ();
        postResolutionEvidence.add (newEvidenceCase);
        currentCase = 0;
        evidenceCasesCompilationState.add (currentCase, false);
        updateAllVisualStates ("clear", currentCase);
        networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
        setSelectedAllNodes (false);
        if (!doPropagation (postResolutionEvidence.get (currentCase), currentCase)) setPropagationActive (false);
    }

    /**
     * This method updates all visual states of all visual nodes when it is
     * needed for a navigation operation among the existing evidence cases, a
     * creation of a new case or when all cases are cleared out.
     * @param option the specific operation to be done over the visual states.
     */
    public void updateAllVisualStates (String option, int caseNumber)
    {
        List<VisualNode> allVisualNodes = visualNetwork.getAllNodes ();
        for (VisualNode visualNode : allVisualNodes)
        {
            InnerBox innerBox = visualNode.getInnerBox ();
            VisualState visualState = null;
            for (int i = 0; i < innerBox.getNumStates (); i++)
            {
                if (innerBox instanceof FSVariableBox)
                {
                    visualState = ((FSVariableBox) innerBox).getVisualState (i);
                }
                else if (innerBox instanceof ExpectedValueBox)
                {
                    visualState = ((ExpectedValueBox) innerBox).getVisualState ();
                }
                if (option.equals ("new"))
                {
                    visualState.createNewStateValue ();
                }
                else if (option.equals ("clear"))
                {
                    visualState.clearAllStateValues ();
                }
                visualState.setCurrentStateValue (caseNumber);
            }
        }
        repaint ();
    }

    /**
     * This method does the propagation of the evidence for all the evidence
     * cases in memory.
     * @param mainPanelMenuAssistant the menu assistant associated to the main
     *            panel.
     */
    public void propagateEvidence (MainPanelMenuAssistant mainPanelMenuAssistant)
    {
        setPropagationActive (true);
        if (networkPanel.getWorkingMode () == NetworkPanel.INFERENCE_WORKING_MODE)
        {
            for (int i = 0; i < getNumberOfCases (); i++)
            {
                if (evidenceCasesCompilationState.get (i) == false)
                {
                    if (doPropagation (getEvidenceCase (i), i)) setPropagationActive (false);
                }
            }
            setSelectedAllNodes (false);
            updateAllVisualStates ("", currentCase);
            networkPanel.getMainPanel ().getInferenceToolBar ().setCurrentEvidenceCaseName (currentCase);
            updateNodesFindingState (postResolutionEvidence.get (currentCase));
        }
        mainPanelMenuAssistant.updateOptionsEvidenceCasesNavigation (networkPanel);
        mainPanelMenuAssistant.updateOptionsPropagationTypeDependent (networkPanel);
        mainPanelMenuAssistant.updateOptionsFindingsDependent (networkPanel);
    }

    /**
     * This method sets the inference options for this panel.
     */
    public void setInferenceOptions ()
    {
        InferenceOptionsDialog inferenceOptionsDialog = new InferenceOptionsDialog (
                                                                                    Utilities.getOwner (this),
                                                                                    this,
                                                                                    networkPanel.getMainPanel ().getInferenceToolBar ());
        inferenceOptionsDialog.setVisible (true);        
    }

    /**
     * Removes selected objects
     */
    public void removeSelectedObjects ()
    {
        RemoveSelectedEdit cutEdit = new RemoveSelectedEdit (visualNetwork);
        visualNetwork.setSelectedAllObjects (false);
        try
        {
            probNet.doEdit (cutEdit);
            propagationActive = isAutomaticPropagation ();
            networkChanged = true;
            repaint ();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog (Utilities.getOwner (this), e.getMessage (),
                                           stringDatabase.getString ("ErrorWindow.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /***
     * Initializes the link restriction potential of a link
     */
    public void enableLinkRestriction ()
    {
        List<VisualLink> links = visualNetwork.getSelectedLinks ();
        if (!links.isEmpty ())
        {
            Link link = links.get (0).getLink ();
            if (!link.hasRestrictions ())
            {
                link.initializesRestrictionsPotential ();
            }
            if (!requestLinkRestrictionValues (Utilities.getOwner (this), link))
            {
                probNet.getPNESupport ().undoAndDelete ();
            }
            link.resetRestrictionsPotential ();
            repaint ();
        }
    }

    /***
     * Resets the link restriction potential of a link
     */
    public void disableLinkRestriction ()
    {
        List<VisualLink> links = visualNetwork.getSelectedLinks ();
        if (!links.isEmpty ())
        {
            Link link = links.get (0).getLink ();
            link.setRestrictionsPotential (null);
            repaint ();
        }
    }

    /***
     * Initializes the revelation arc properties of a link
     */
    public void enableRevelationArc ()
    {
        List<VisualLink> links = visualNetwork.getSelectedLinks ();
        if (!links.isEmpty ())
        {
            Link link = links.get (0).getLink ();
            try
            {
                if (!requestRevelationArcValues (Utilities.getOwner (this), link))
                {
                    probNet.getPNESupport ().undoAndDelete ();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace ();
                JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                               e.getMessage (),
                                               stringDatabase.getString ("ErrorWindow.Title.Label"),
                                               JOptionPane.ERROR_MESSAGE);
            }
            repaint ();
        }
    }

    /**
     * Sets a new visualNetwork.
     * @param visualNetwork
     */
    public void setVisualNetwork (VisualNetwork visualNetwork)
    {
        this.visualNetwork = visualNetwork;
    }

    /**
     * Returns the visualNetwork.
     * @return the visualNetwork.
     */
    public VisualNetwork getVisualNetwork ()
    {
        return visualNetwork;
    }

    public void setProbNet (ProbNet probNet)
    {
        networkChanged = true;
        this.probNet = probNet;
        visualNetwork.setProbNet (probNet);
    }

    /**
     * Sets workingMode
     * @param newWorkingMode
     */
    public void setWorkingMode (int newWorkingMode)
    {
        visualNetwork.setWorkingMode (newWorkingMode);
        if (newWorkingMode == NetworkPanel.INFERENCE_WORKING_MODE)
        {
            editionMode = editionModeManager.getDefaultEditionMode ();
            setCursor (editionModeManager.getDefaultCursor ());
        }
    }

    // TODO OOPN start
    public void markSelectedAsInput ()
    {
        visualNetwork.markSelectedAsInput ();
        repaint ();
    }

    public void editClass ()
    {
        visualNetwork.editClass ();
    }

    public void setParameterArity (ParameterArity arity)
    {
        visualNetwork.setParameterArity (arity);
    }

    // TODO OOPN end
    protected double[] getBounds (Graphics2D graphics)
    {
        return visualNetwork.getNetworkBounds (graphics);
    }

    /**
     * If the dimensions of the network are greater than the dimensions of the
     * panel, changes the dimensions of the panel in order to accommodate the
     * whole network.
     */
    public void adjustPanelDimension ()
    {
        double[] bounds = getBounds ((Graphics2D) getGraphics ());
        Dimension newDimension = null;
        maxWidth = Math.min (maxWidth, bounds[1]);
        maxHeight = Math.min (maxHeight, bounds[3]);
        newDimension = new Dimension ((int) Math.round (getMaxWidth ()),
                                      (int) Math.round (getMaxHeight ()));
        setPreferredSize (newDimension);
        setSize (newDimension);
    }

    public void createNextSliceNode() {
        ProbNode selectedNode = visualNetwork.getSelectedNodes().get(0).getProbNode();
        Variable selectedVariable = selectedNode.getVariable();
        Variable newVariable = new Variable(selectedVariable);
        newVariable.setTimeSlice(selectedVariable.getTimeSlice() + 1);
        Point2D.Double position = new Point2D.Double(selectedNode.getNode().getCoordinateX() + 200,
                selectedNode.getNode().getCoordinateY());
        AddProbNodeEdit addProbNodeEdit = new AddProbNodeEdit (probNet, newVariable,
                selectedNode.getNodeType(), position);
        try
        {
            probNet.doEdit (addProbNodeEdit);
        }
        catch (Exception e1)
        {
            System.err.println (e1.toString ());
            e1.printStackTrace ();
            JOptionPane.showMessageDialog (this,
                                           e1.toString (),
                                           "Error creating node",
                                           JOptionPane.ERROR_MESSAGE);
        }
        adjustPanelDimension ();
        repaint ();
    }
}
