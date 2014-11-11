/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.action.SetPotentialVariablesEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.CommentHTMLScrollPane;
import org.openmarkov.core.gui.dialog.common.ICIPotentialsTablePanel;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.common.PanelResizeEvent;
import org.openmarkov.core.gui.dialog.common.PanelResizeEventListener;
import org.openmarkov.core.gui.dialog.common.PolicyTypePanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanelManager;
import org.openmarkov.core.gui.dialog.common.ProbabilityTablePanel;
import org.openmarkov.core.gui.dialog.common.TablePotentialPanel;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialTypeManager;

/**
 * Dialog box to edit all type of potentials ( TablePotential and TreeADDs ). If
 * the potential is a utility role or uniform type, then no Values panel is
 * displayed. If potential is TreeADDpotential, then graphic edition panel is
 * showed.
 * @author mpalacios
 * @author jmendoza
 * @author ibermejo
 * @version 1.0
 * @version 1.2 jlgozalo - set class to use independent panels;
 */
public class PotentialEditDialog extends OkCancelApplyUndoRedoHorizontalDialog
    implements
        ActionListener, PanelResizeEventListener
{
    /**
     * 
     */
    private static final long    serialVersionUID                = -7344555059488539825L;
    /**
     * The JComboBox object that shows all the potentials types
     */
    private JComboBox<String>    potentialTypeComboBox;
    /**
     * The node edited
     */
    private ProbNode             probNode;
    /**
     * The panel that contains all the common option to potentials
     */
    private JPanel               potentialTypePanel;
    private PolicyTypePanel      pnlPolicyType;
    /**
     * Label for relation type
     */
    private JLabel               lblPotentialType;
    /**
     * Relation Type Manager
     */
    private RelationPotentialTypeManager relationTypeManager;
    /**
     * Panel of the graphic editor
     */
    private PotentialPanel       potentialPanel;
    /**
     * The builder object that contains UncertaintyContextualMenu
     */
    // unused private ContextualMenuFactory contextualMenuFactory;
    /**
     * Option deselected in the jComboboxRelationType
     */
    private int                  optionPreviouslySelected        = 0;
    private String               previouslySelectedPotentialType = "";
    /**
     * If true, values inside the dialog will not be editable
     */
    private boolean              readOnly;
    private JButton              reorderVariablesButton;
    
    private CommentHTMLScrollPane commentPane;
    

    /**
     * Creates the dialog.
     */
    public PotentialEditDialog (Window owner,
                                ProbNode probNode,
                                boolean newElement,
                                boolean readOnly)
    {
        super (owner);
        this.probNode = probNode;
        this.readOnly = readOnly;
        // TODO create PNESupport
        probNode.getProbNet ().getPNESupport ().openParenthesis ();
        initialize ();
        List<Potential> potentials = probNode.getPotentials();
        if (!potentials.isEmpty()
                && potentials.get(0).getComment() != null
                && !potentials.get(0).getComment().isEmpty())        {
            commentPane.setCommentHTMLTextPaneText(potentials.get(0).getComment());
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit ();
        Dimension screenSize = toolkit.getScreenSize ();
        Rectangle bounds = owner.getBounds ();
        int width = screenSize.width / 2;
        int height = screenSize.height / 2;
        // center point of the owner window
        int x = bounds.x / 2 - width / 2;
        int y = bounds.y / 2 - height / 2;
        this.setBounds (x, y, width, height);
        setLocationRelativeTo (null);
        setMinimumSize (new Dimension (width, height / 2));
        setResizable (true);
        pack ();
    }

    /**
     * Constructor
     */
    public PotentialEditDialog (Window owner, ProbNode probNode, boolean newElement)
    {
        this (owner, probNode, newElement, false);
    }

    /**
     * This method configures the dialog box.
     */
    private void initialize ()
    {
        relationTypeManager = new RelationPotentialTypeManager ();
        // Set default title
        setTitle ("NodePotentialDialog.Title.Label");
        configureComponentsPanel ();
        pack ();
    }

    /**
     * Sets up the panel where all components, except the buttons of the buttons
     * panel, will be appear.
     */
    private void configureComponentsPanel ()
    {
        getComponentsPanel ().setLayout (new BorderLayout (5, 5));
        // getComponentsPanel().setSize(294, 29);
        getComponentsPanel ().setMaximumSize (new Dimension (180, 40));
        getComponentsPanel ().add (getPotentialTypePanel (), BorderLayout.NORTH);
        getComponentsPanel ().add (getPotentialPanel (), BorderLayout.CENTER);
        if (((probNode.getPotentials ().get (0).getVariables ().size () > 1 && probNode.getPotentials ().get (0).getPotentialRole () == PotentialRole.UTILITY) || (probNode.getPotentials ().get (0).getVariables ().size () - 1 > 1 && probNode.getPotentials ().get (0).getPotentialRole () == PotentialRole.CONDITIONAL_PROBABILITY))
            && getPotentialPanel () instanceof ProbabilityTablePanel)
        {
            getReorderVariablesButton ().setVisible (true);
            getReorderVariablesButton ().setEnabled (true);
        }
        else
        {
            getReorderVariablesButton ().setVisible (false);
            getReorderVariablesButton ().setEnabled (false);
        }
        getComponentsPanel ().add(getCommentPane(),BorderLayout.SOUTH);

    }

    /**
     * @return label for the type of relations or policy
     */
    protected JLabel getPotentialTypeJLabel ()
    {
        if (lblPotentialType == null)
        {
            lblPotentialType = new JLabel ();
            lblPotentialType.setName ("jLabelRelationType");
            lblPotentialType.setText ("a Label");
            lblPotentialType.setText (stringDatabase.getString ("NodeProbsValuesTablePanel.jLabelRelationType.Text"));
        }
        return lblPotentialType;
    }

    /**
     * @return ComboBox with the types of families of relation to be used
     */
    protected JComboBox<String> getPotentialTypeJCombobox ()
    {
        if (potentialTypeComboBox == null)
        {
            List<String> filteredPotentialNames = relationTypeManager.getFilteredPotentials (probNode);
            Collections.sort (filteredPotentialNames);
            potentialTypeComboBox = new JComboBox<> (
                                                     (String[]) filteredPotentialNames.toArray (new String[0]));
            potentialTypeComboBox.setSelectedItem (probNode.getPotentials ().get (0).getClass ().getAnnotation (RelationPotentialType.class).name ());
            potentialTypeComboBox.setBorder (new LineBorder (
                                                             UIManager.getColor ("List.dropLineColor"),
                                                             1, false));
            potentialTypeComboBox.setName ("jComboBoxRelationType");
            potentialTypeComboBox.addActionListener (new java.awt.event.ActionListener ()
                {
                    public void actionPerformed (java.awt.event.ActionEvent evt)
                    {
                        potentialTypeChanged (evt);
                    }
                });
            potentialTypeComboBox.setEnabled (!readOnly);
        }
        return potentialTypeComboBox;
    }

    /**
     * Enables or disables the potential type combo box
     * @param enable
     */
    public void setEnabledPotentialTypeCombobox (boolean enable)
    {
        getPotentialTypeJCombobox ().setEnabled (enable);
    }

    /**
     * Gets the panel that matches the type of potential to be edited
     * @return the potential panel matching the potential edited.
     */
    private PotentialPanel getPotentialPanel ()
    {
        if (potentialPanel == null)
        {
            String potentialName = (String) potentialTypeComboBox.getSelectedItem ();
            String potentialFamily = relationTypeManager.getPotentialsFamily (potentialName);
            potentialPanel = PotentialPanelManager.getInstance ().getPotentialPanel (potentialName,
                                                                                     potentialFamily,
                                                                                     probNode);
            potentialPanel.setReadOnly (readOnly);
            potentialPanel.suscribePanelResizeEventListener(this);
        }
        return potentialPanel;
    }

    @Override
    public void setTitle (String title)
    {
        String nodeName = (probNode == null) ? "" : probNode.getName ();
        super.setTitle (stringDatabase.getString (title) + ": " + nodeName);
    }

    /**
     * @return An integer indicating the button clicked by the user when closing
     *         this dialog
     */
    public int requestValues ()
    {
        // Shows the potentials' options table
        if (probNode.getNodeType () == NodeType.DECISION
              && probNode.getPolicyType () == PolicyType.OPTIMAL 
              && (probNode.getPotentials ().isEmpty () || !probNode.getPotentials ().get (0).isUtility ())  
              && readOnly)
        {
            setEnabledDecisionOptions (true);
        }
        else
        {
            showFields (probNode);
        }
        setVisible (true);
        return selectedButton;
    }

    /**
     * This method fills the content of the fields from a ProbNode object. In
     * this method, when Elvira will be discontinued, the code for
     * discriminating discrete and discretized variables must be eliminated
     * @param probNode object from where load the information.
     */
    // TODO Remove all this
    private void showFields (ProbNode probNode)
    {
        // The element order in PotentialType object are same that
        // JComboBoxRelationType
        previouslySelectedPotentialType = probNode.getPotentials ().get (0).getClass ().getAnnotation (RelationPotentialType.class).name ();
        getPotentialTypeJCombobox ().setSelectedItem (previouslySelectedPotentialType);
        updatePotentialPanel ();
        // Elvira do not distinguish between DISCRETE and DISCRETIZED
        // so here we will see if there are intervals in the states
        if (Util.hasLimitBracketSymbols (probNode.getVariable ().getStates ())
            && (probNode.getVariable ().getVariableType () == VariableType.FINITE_STATES))
        {
            // really DISCRETIZED, so change the value of the VariableType
            probNode.getVariable ().setVariableType (VariableType.DISCRETIZED);
        }
        // set the nodeProperties variable in this dialog and panels
        this.probNode = probNode;
        // *******
        getPotentialPanel ().setData (probNode);
    }

    /**
     * @return The panel that indicates the type of the table (and perhaps the
     *         type of policy (optimal or imposed))
     */
    protected JPanel getPotentialTypePanel ()
    {
        if (potentialTypePanel == null)
        {
            potentialTypePanel = new JPanel ();
            // jPanelRelationTableType.setBorder( new LineBorder( UIManager
            // .getColor( "List.dropLineColor" ), 1, false ) );
            potentialTypePanel.setLayout (new FlowLayout ());
            potentialTypePanel.setSize (294, 29);
            potentialTypePanel.setName ("potentialTypePanel");
            potentialTypePanel.add (getPotentialTypeJLabel ());
            potentialTypePanel.add (getPotentialTypeJCombobox ());
            potentialTypePanel.add (getReorderVariablesButton ());
            // potentialTypePanel.add( getPoliticyTypePanel() );
            // /getPoliticyTypePanel().setVisible(false);
            // getPotentialPanel().setEnabled(false);
        }
        return potentialTypePanel;
    }

    /**
     * @return The panel that indicates the type of the table (and perhaps the
     *         type of policy (optimal or imposed))
     */
    protected JButton getReorderVariablesButton ()
    {
        if (reorderVariablesButton == null)
        {
            reorderVariablesButton = new JButton (
                                                  stringDatabase.getString ("PotentialEditDialog.ReorderVariables.Text"));
            reorderVariablesButton.setName ("reorderVariablesButton");
            // reorderVariablesButton.setVisible(false);
            reorderVariablesButton.addActionListener (this);
        }
        return reorderVariablesButton;
    }
    
    /**
     * This method initializes getCommentPane
     * 
     * @return a new comment HTML scroll pane.
     */
    private CommentHTMLScrollPane getCommentPane() {

        if (commentPane == null) {
            commentPane = new CommentHTMLScrollPane();
            commentPane.setName("commentPane");
            commentPane.setPreferredSize(new Dimension(10, 30));
        }
        return commentPane;
    }  

    /**
     * @return PolicyTypePanel with three radio buttons with the types of
     *         policy: optimal, deterministic, or probabilistic
     */
    protected PolicyTypePanel getPoliticyTypePanel ()
    {
        if (pnlPolicyType == null)
        {
            pnlPolicyType = new PolicyTypePanel (this, probNode);
        }
        return pnlPolicyType;
    }

    protected void potentialTypeChanged (ActionEvent evt)
    {
        String potentialType = (String) potentialTypeComboBox.getSelectedItem ();
        if (!previouslySelectedPotentialType.equals (potentialType))
        {
            SetPotentialEdit setPotentialEdit = new SetPotentialEdit (probNode, potentialType);
            try
            {
                probNode.getProbNet ().doEdit (setPotentialEdit);
            }
            catch (ConstraintViolationException e1)
            {
                JOptionPane.showMessageDialog (this,
                                               stringDatabase.getString (e1.getMessage ()),
                                               stringDatabase.getString ("ConstraintViolationException"),
                                               JOptionPane.ERROR_MESSAGE);
                revertPotentialTypeChange ();
                potentialTypeComboBox.requestFocus ();
            }
            catch (Exception e1)
            {
                e1.printStackTrace ();
            }
            updatePotentialPanel ();
            previouslySelectedPotentialType = potentialType;
            optionPreviouslySelected = potentialTypeComboBox.getSelectedIndex ();
        }
    }

    /**
     * This method carries out the actions when the user presses the OK button
     * before hiding the dialog.
     * @return true if all the fields are correct.
     */
    @Override
    protected boolean doOkClickBeforeHide ()
    {
        if (getPotentialPanel () instanceof TablePotentialPanel)
        {
            ((TablePotentialPanel) getPotentialPanel ()).getValuesTable ().stopCellEditing ();
        }
        if (getPotentialPanel () instanceof ICIPotentialsTablePanel)
        {
            ((ICIPotentialsTablePanel) getPotentialPanel ()).getICIValuesTable ().stopCellEditing ();
        }
        getPotentialPanel ().saveChanges ();
        if(commentPane.isChanged())
        {
            probNode.getPotentials().get(0).setComment(commentPane.getCommentText());
        }
        probNode.getProbNet ().getPNESupport ().closeParenthesis ();
        return true;
    }

    @Override
    protected void doCancelClickBeforeHide ()
    {
        getPotentialPanel ().close ();
        probNode.getProbNet ().getPNESupport ().closeParenthesis ();
    }

    /**
     * Update potential panel
     */
    public void updatePotentialPanel ()
    {
        getComponentsPanel ().remove (getPotentialPanel ());
        potentialPanel.close ();
        potentialPanel = null;
        if (((probNode.getPotentials ().get (0).getVariables ().size () > 1 && probNode.getPotentials ().get (0).getPotentialRole () == PotentialRole.UTILITY) || (probNode.getPotentials ().get (0).getVariables ().size () - 1 > 1 && probNode.getPotentials ().get (0).getPotentialRole () == PotentialRole.CONDITIONAL_PROBABILITY))
            && getPotentialPanel () instanceof ProbabilityTablePanel)
        {
            getReorderVariablesButton ().setVisible (true);
            getReorderVariablesButton ().setEnabled (true);
        }
        else
        {
            getReorderVariablesButton ().setVisible (false);
            getReorderVariablesButton ().setEnabled (false);
        }
        getComponentsPanel ().add (getPotentialPanel (), BorderLayout.CENTER);
        getComponentsPanel ().updateUI ();
        getComponentsPanel ().repaint ();
        this.repaint ();
        this.pack ();
    }

    /**
     * Shows and activated the options related to decision policy
     * @param show
     */
    private void setEnabledDecisionOptions (boolean show)
    {
        if (show)
        {
            switch (probNode.getPolicyType ())
            {
                case OPTIMAL :
                    getPotentialTypeJCombobox ().setEnabled (false);
                    break;
                case DETERMINISTIC :
                    getPotentialTypeJCombobox ().setEnabled (false);
                    break;
                case PROBABILISTIC :
                    Potential potential = probNode.getPotentials ().get (0);
                    switch (potential.getPotentialType ())
                    {
                        case UNIFORM :
                        case TABLE :
                            getPotentialTypeJCombobox ().setSelectedIndex (potential.getPotentialType ().getType ());
                            // getJComboBoxRelationType().setEnabled(false);
                            break;
                    // TODO definir el comportamiento para los demás tipos de
                    // potenciales
                    }
                    break;
            }
        }
        getPoliticyTypePanel ().setEnabledDecisionOptions (show);
    }

    public void revertPotentialTypeChange ()
    {
        getPotentialTypeJCombobox ().setSelectedIndex (optionPreviouslySelected);
    }

    /**
     * @return the readOnly
     */
    public boolean isReadOnly ()
    {
        return readOnly;
    }

    @Override
    public void actionPerformed (ActionEvent e)
    {
        if (e.getSource ().equals (reorderVariablesButton))
        {
            actionPerformedReorderVariables ();
        }
    }

    protected void actionPerformedReorderVariables ()
    {
        ReorderVariablesDialog reorderVariablesDialog = new ReorderVariablesDialog (this, probNode);
        if (reorderVariablesDialog.requestValues () == NodePropertiesDialog.OK_BUTTON)
        {
            List<Variable> newVariables = reorderVariablesDialog.getReorderVariablesPanel ().getVariables ();
            
            if (getPotentialPanel () instanceof TablePotentialPanel)
            {
                // if (probNode.getPotentials().get(0) instanceof
                // TablePotential) {
                Potential potential = DiscretePotentialOperations.reorder ((TablePotential) probNode.getPotentials ().get (0),
                                                                           newVariables);
                SetPotentialEdit potentialEdit = new SetPotentialEdit (probNode, potential);
                try
                {
                    probNode.getProbNet ().doEdit (potentialEdit);
                }
                catch (DoEditException | ConstraintViolationException | CanNotDoEditException
                        | NonProjectablePotentialException | WrongCriterionException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace ();
                }
                updatePotentialPanel ();
            }
            else if (getPotentialPanel () instanceof ICIPotentialsTablePanel)
            {
                SetPotentialVariablesEdit setPotentialVariables = new SetPotentialVariablesEdit (
                                                                                                 probNode,
                                                                                                 newVariables);
                try
                {
                    probNode.getProbNet ().doEdit (setPotentialVariables);
                }
                catch (DoEditException | ConstraintViolationException | CanNotDoEditException
                        | NonProjectablePotentialException | WrongCriterionException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace ();
                }
                updatePotentialPanel ();
            }
        }
    }
    
    @Override
    public void panelSizeChanged(PanelResizeEvent event) {
        pack();
        repaint();
    }

}
