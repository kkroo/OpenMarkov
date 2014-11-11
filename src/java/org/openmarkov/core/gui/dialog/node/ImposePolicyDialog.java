package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.common.PolicyTypePanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanel;
import org.openmarkov.core.gui.dialog.common.TablePotentialPanel;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial")
public class ImposePolicyDialog extends OkCancelApplyUndoRedoHorizontalDialog {

    private ProbNode        probNode;
    private PolicyTypePanel pnlPolicyType;
    private PotentialPanel  potentialPanel;
    private boolean         readOnly;
    private ProbNode        dummyProbNode;

    public ImposePolicyDialog(Window owner, ProbNode probNode) {
        super(owner);
        this.probNode = probNode;
        this.readOnly = false;
        probNode.getProbNet().getPNESupport().openParenthesis();
        initialize();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        Rectangle bounds = owner.getBounds();
        int width = screenSize.width / 2;
        int height = screenSize.height / 2;

        // center point of the owner window
        int x = bounds.x / 2 - width / 2;
        int y = bounds.y / 2 - height / 2;
        this.setBounds(x, y, width, height);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(width, height / 2));
        setResizable(true);

        pack();
    }

    /**
     * This method configures the dialog box.
     */
    private void initialize() {

        setTitle(StringDatabase.getUniqueInstance().getString("ImposePolicydialog.Title.Label")
                + ": "
                + (probNode == null ? "" : probNode.getName()));

        configureComponentsPanel();
        pack();
    }

    /**
     * Sets up the panel where all components, except the buttons of the buttons
     * panel, will be appear.
     */
    private void configureComponentsPanel() {
        getComponentsPanel().setLayout(new BorderLayout(5, 5));
        // getComponentsPanel().setSize(294, 29);
        getComponentsPanel().setMaximumSize(new Dimension(180, 40));
        getComponentsPanel().add(getPoliticyTypePanel(), BorderLayout.NORTH);
        getComponentsPanel().add(getPotentialPanel(), BorderLayout.CENTER);

    }

    /**
     * @return PolicyTypePanel with three radio buttons with the types of
     *         policy: optimal, deterministic, or probabilistic
     */
    protected PolicyTypePanel getPoliticyTypePanel() {

        if (pnlPolicyType == null) {
            // pnlPolicyType = new PolicyTypePanel(this, probNode);
        }
        return pnlPolicyType;
    }

    /**
     * Gets the panel that matches the type of potential to be edited
     * 
     * @return the potential panel matching the potential edited.
     */
    private PotentialPanel getPotentialPanel() {

        if (potentialPanel == null) {
            // before creating TablePotentialPanel we have to set a
            // tablePotential to the decision node
            List<Variable> variables = new ArrayList<Variable>();
            // conditiones variable
            variables.add(probNode.getVariable());
            // adding variable parents
            List<ProbNode> probNodes = probNode.getProbNet().getProbNodes();
            for (ProbNode probNode : probNodes) {
                if (probNode.isParent(this.probNode)) {
                    variables.add(probNode.getVariable());
                }
            }
            try {
                // copy of the probNode
                this.dummyProbNode = new ProbNode(probNode);
                TablePotential policy = new TablePotential(variables, PotentialRole.POLICY);
                SetPotentialEdit setPotentialEdit = new SetPotentialEdit(dummyProbNode, policy);

                probNode.getProbNet().doEdit(setPotentialEdit);

            } catch (WrongCriterionException
                    | ConstraintViolationException
                    | CanNotDoEditException
                    | NonProjectablePotentialException
                    | DoEditException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            potentialPanel = new TablePotentialPanel(probNode);
            potentialPanel.setReadOnly(readOnly);
        }
        return potentialPanel;
    }

    /**
     * @return An integer indicating the button clicked by the user when closing
     *         this dialog
     */
    public int requestValues() {

        setVisible(true);
        return selectedButton;
    }

    public ProbNode getDummyProbNode() {
        return dummyProbNode;
    }

    /**
     * This method carries out the actions when the user presses the OK button
     * before hiding the dialog.
     * 
     * @return true if all the fields are correct.
     */
    @Override
    protected boolean doOkClickBeforeHide() {
        getPotentialPanel().close();
        probNode.getProbNet().getPNESupport().closeParenthesis();
        return true;
    }

    @Override
    protected void doCancelClickBeforeHide() {
        probNode.getProbNet().getPNESupport().closeParenthesis();
    }

}
