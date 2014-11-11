/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

/**
 * Input dialog for cost effectiveness purposes used to introduce relevant
 * information such as cycle length, number of cycles, introduce findings to
 * numerical variables within the network, cost and effectiveness discount
 * rate...
 * 
 * @author myebra
 */
public class CostEffectivenessDialog extends OkCancelHorizontalDialog implements ItemListener,
        FocusListener {
    private static final long       serialVersionUID       = 1L;
    private JLabel                  costDiscountLabel;
    private JLabel                  effectivenessDiscountLabel;
    private JTextField              costDiscountTextField;
    private JTextField              effectivenessDiscountTextField;
    private Double                  costDiscount;
    private Double                  effectivenessDiscount;
    private Integer                 simulationsNumber;
    private JLabel                  numSlicesLabel;
    private JTextField              numSlicesTextField;
    private Integer                 numSlices;
    private JRadioButton            beginningOfCycleButton;
    private JRadioButton            endOfCycleButton;
    private JRadioButton            halfCycleButton;
    private ButtonGroup             transitionsButtonGroup;
    private JPanel                  transitionsPanel;
    private JRadioButton            instantButton;
    private JRadioButton            cumulativeButton;
    private ButtonGroup             instantOrCumulativeButtonGroup;
    private JPanel                  instantOrCumulativePanel;
    private boolean                 isCumulative           = false;
    private JPanel                  numSlicesPanel;
    private JLabel                  numSimulationsLabel;
    private Integer                 numSimulations;
    private JTextField              numSimulationsTextField;
    private Map<Variable, Double>   initialValues;
    private Map<String, JTextField> initialValueComponents = new HashMap<>();

    /**
     * Creates a CostEffectivenessDialog for expansion only
     * 
     * @param owner
     *            The parent of the dialog
     */
    public CostEffectivenessDialog(Window owner) {
        super(owner);
        // setMinimumSize(new Dimension(250 , 150));
        BorderLayout layout = new BorderLayout(5, 5);
        getComponentsPanel().setLayout(layout);
        getComponentsPanel().add(getNumSlicesPanel(), BorderLayout.NORTH);
        setResizable(false);
        pack();
        Point parentLocation = owner.getLocation();
        Dimension parentSize = owner.getSize();
        int x = (int) (parentLocation.getX() + parentSize.getWidth() / 2 - getSize().getWidth() / 2);
        int y = (int) (parentLocation.getY() + parentSize.getHeight() / 2 - getSize().getHeight() / 2);
        setLocation(new Point(x, y));
        repaint();
    }

    /**
     * Creates a CostEffectivenessDialog for temporal evolution
     * 
     * @param owner
     *            The parent of the dialog
     * @param b
     */
    public CostEffectivenessDialog(Window owner, ProbNet probNet, boolean sensitivityAnalysis,
            boolean isTemporalEvolution) {
        super(owner);
        this.initialValues = new HashMap<>();
        List<ProbNode> temporalNodes = CostEffectivenessAnalysis.getShiftingTemporalNodes(probNet);
        for (ProbNode numericalTemporalNode : temporalNodes) {
            initialValues.put(numericalTemporalNode.getVariable(),
                    numericalTemporalNode.getVariable().getPartitionedInterval().getMin());
        }
        initialize(sensitivityAnalysis, isTemporalEvolution);
        setResizable(false);
        setTitle(probNet.getName(), isTemporalEvolution);
        pack();
        Point parentLocation = owner.getLocation();
        Dimension parentSize = owner.getSize();
        int x = (int) (parentLocation.getX() + parentSize.getWidth() / 2 - getSize().getWidth() / 2);
        int y = (int) (parentLocation.getY() + parentSize.getHeight() / 2 - getSize().getHeight() / 2);
        setLocation(new Point(x, y));
        repaint();
    }

    private void initialize(boolean sensitivityAnalysis, boolean isTemporalEvolution) {
        setMinimumSize(new Dimension(250, 150));
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel otherPanel = new JPanel();
        otherPanel.setLayout(new BorderLayout());
        JPanel slicesPanel = new JPanel();
        slicesPanel.add(getJLabelNumSlices());
        slicesPanel.add(getNumSlicesTextField());
        slicesPanel.setBorder(new TitledBorder("Time horizon"));
        slicesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        otherPanel.add(slicesPanel, BorderLayout.NORTH);
        otherPanel.add(getTransitionsPanel(), BorderLayout.CENTER);
        JPanel discountTitlePanel = new JPanel();
        discountTitlePanel.setBorder(new TitledBorder("Discounts"));
        JPanel discountsPanel = new JPanel();
        discountsPanel.setBorder(new EmptyBorder(5, 5, 2, 2));
        discountsPanel.setLayout(new GridLayout(2, 4, 5, 5));
        discountsPanel.add(getCostDiscountLabel());
        discountsPanel.add(getCostDiscountTextField());
        discountsPanel.add(new JLabel("%"));
        discountsPanel.add(getEffectivenessDiscountLabel());
        discountsPanel.add(getEffectivenessDiscountTextField());
        discountsPanel.add(new JLabel("%"));
        discountTitlePanel.add(discountsPanel);
        otherPanel.add(discountTitlePanel, BorderLayout.SOUTH);
        panel.add(otherPanel, BorderLayout.NORTH);
        if(!initialValues.isEmpty())
        {
	        JPanel initialValuesPanel = new JPanel();
	        initialValuesPanel.setBorder(new TitledBorder("Initial Values"));
	        initialValuesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	        for (Variable numericTemporalVariable : initialValues.keySet()) {
	            JPanel initialValuePanel = new JPanel();
	            JLabel label = new JLabel(numericTemporalVariable.getName());
	            JTextField textField = new JTextField(10);
	            textField.setName(numericTemporalVariable.getName());
	            textField.setText("" + initialValues.get(numericTemporalVariable));
	            textField.addFocusListener(this);
	            initialValuePanel.add(label);
	            initialValuePanel.add(textField);
	            initialValueComponents.put(numericTemporalVariable.getName(), textField);
	            initialValuePanel.add(new JLabel(stringDatabase.getString("CostEffectiveness.Cycles")));
	            initialValuesPanel.add(initialValuePanel);
	        }
	        panel.add(initialValuesPanel, BorderLayout.CENTER);
        }
        getComponentsPanel().setLayout(new BorderLayout(20, 0));
        getComponentsPanel().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getComponentsPanel().add(panel, BorderLayout.NORTH);
        if (isTemporalEvolution) {
            getComponentsPanel().add(new JPanel());
            getComponentsPanel().add(getJPanelInstantOrAccumulative(), BorderLayout.SOUTH);
        }
        if (sensitivityAnalysis) {
            numSimulations = 1000;
            JPanel numSimulationsPanel = new JPanel();
            numSimulationsPanel.setBorder(new TitledBorder("Simulation"));
            numSimulationsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            numSimulationsTextField = new JTextField(10);
            numSimulationsTextField.addFocusListener(this);
            numSimulationsTextField.setName("numSimulationsTextField");
            numSimulationsTextField.setText(numSimulations + "");
            numSimulationsPanel.add(getSimulationsNumberLabel());
            numSimulationsPanel.add(numSimulationsTextField);
            panel.add(numSimulationsPanel, BorderLayout.SOUTH);
        }
        pack();
        repaint();
    }

    private JPanel getNumSlicesPanel() {
        if (numSlicesPanel == null) {
            numSlicesPanel = new JPanel();
            numSlicesPanel.setLayout(new GridLayout(1, 2, 10, 10));
            numSlicesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            numSlicesPanel.add(getJLabelNumSlices());
            numSlicesPanel.add(getNumSlicesTextField());
        }
        return numSlicesPanel;
    }

    private JLabel getJLabelNumSlices() {
        if (numSlicesLabel == null) {
            numSlicesLabel = new JLabel(stringDatabase.getString("CostEffectiveness.NumberOfCycles"));
        }
        return numSlicesLabel;
    }

    private JTextField getNumSlicesTextField() {
        if (numSlicesTextField == null) {
            numSlices = 20;
            numSlicesTextField = new JTextField();
            numSlicesTextField.setText("" + numSlices);
            numSlicesTextField.setColumns(10);
            numSlicesTextField.setName("numSlicesTextField");
            numSlicesTextField.addFocusListener(this);
        }
        return numSlicesTextField;
    }

    private JLabel getSimulationsNumberLabel() {
        if (numSimulationsLabel == null) {
            numSimulationsLabel = new JLabel(stringDatabase.getString("CostEffectiveness.NumberOfSimulations"));
        }
        return numSimulationsLabel;
    }

    private JLabel getCostDiscountLabel() {
        if (costDiscountLabel == null) {
            costDiscountLabel = new JLabel(stringDatabase.getString("CostEffectiveness.Cost"));
        }
        return costDiscountLabel;
    }

    private JLabel getEffectivenessDiscountLabel() {
        if (effectivenessDiscountLabel == null) {
            effectivenessDiscountLabel = new JLabel(stringDatabase.getString("CostEffectiveness.Effectiveness"));
        }
        return effectivenessDiscountLabel;
    }

    private JTextField getCostDiscountTextField() {
        if (costDiscountTextField == null) {
            costDiscountTextField = new JTextField("0.0");
            costDiscountTextField.setColumns(10);
        }
        return costDiscountTextField;
    }

    private JTextField getEffectivenessDiscountTextField() {
        if (effectivenessDiscountTextField == null) {
            effectivenessDiscountTextField = new JTextField("0.0");
            effectivenessDiscountTextField.setColumns(10);
        }
        return effectivenessDiscountTextField;
    }

    private JRadioButton getInstantValuesButton() {
        if (instantButton == null) {
            instantButton = new JRadioButton(stringDatabase.getString("CostEffectiveness.TemporalEvolution.InstantValues"),
                    true);
            instantButton.addItemListener(this);
        }
        return instantButton;
    }

    private JRadioButton getCumulativeValuesButton() {
        if (cumulativeButton == null) {
            cumulativeButton = new JRadioButton(stringDatabase.getString("CostEffectiveness.TemporalEvolution.CumulativeValues"),
                    false);
            cumulativeButton.addItemListener(this);
        }
        return cumulativeButton;
    }

    private void initInstantOrCumulativeButtonGroup() {
        instantOrCumulativeButtonGroup = new ButtonGroup();
        instantOrCumulativeButtonGroup.add(getInstantValuesButton());
        instantOrCumulativeButtonGroup.add(getCumulativeValuesButton());
    }

    /**
     * @return the panel with the two buttons
     */
    private JPanel getJPanelInstantOrAccumulative() {
        if (instantOrCumulativePanel == null) {
            instantOrCumulativePanel = new JPanel();
            instantOrCumulativePanel.setLayout(new GridLayout(2, 1));
            instantOrCumulativePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                    stringDatabase.getString("CostEffectiveness.TemporalEvolution.ValueType")));
            instantOrCumulativePanel.setName("instantOrAccumulativePanel");
            initInstantOrCumulativeButtonGroup();
            instantOrCumulativePanel.add(getInstantValuesButton());
            instantOrCumulativePanel.add(getCumulativeValuesButton());
        }
        return instantOrCumulativePanel;
    }

    private JRadioButton getBeginningOfCycleButton() {
        if (beginningOfCycleButton == null) {
            beginningOfCycleButton = new JRadioButton(stringDatabase.getString("CostEffectiveness.BeginningOfCycle"),
                    true);
            beginningOfCycleButton.addItemListener(this);
        }
        return beginningOfCycleButton;
    }

    private JRadioButton getEndOfCycleButton() {
        if (endOfCycleButton == null) {
            endOfCycleButton = new JRadioButton(stringDatabase.getString("CostEffectiveness.EndOfCycle"),
                    true);
            endOfCycleButton.addItemListener(this);
        }
        return endOfCycleButton;
    }

    private JRadioButton getHalfCycleButton() {
        if (halfCycleButton == null) {
            halfCycleButton = new JRadioButton(stringDatabase.getString("CostEffectiveness.HalfCycle"),
                    true);
            halfCycleButton.addItemListener(this);
        }
        return halfCycleButton;
    }

    private void initTransitionsButtonGroup() {
        transitionsButtonGroup = new ButtonGroup();
        transitionsButtonGroup.add(getBeginningOfCycleButton());
        transitionsButtonGroup.add(getHalfCycleButton());
        transitionsButtonGroup.add(getEndOfCycleButton());
    }

    /**
     * @return the panel with the transition buttons
     */
    private JPanel getTransitionsPanel() {
        if (transitionsPanel == null) {
            transitionsPanel = new JPanel();
            transitionsPanel.setLayout(new GridLayout(3, 1));
            transitionsPanel.setBorder(new TitledBorder("Transitions"));
            transitionsPanel.setName("transitionsPanel");
            initTransitionsButtonGroup();
            transitionsPanel.add(getBeginningOfCycleButton());
            transitionsPanel.add(getHalfCycleButton());
            transitionsPanel.add(getEndOfCycleButton());
        }
        return transitionsPanel;
    }

    public int requestData() {
        setVisible(true);
        return selectedButton;
    }

    @Override
    protected boolean doOkClickBeforeHide() {
        boolean allValid = checkTextFieldValidity(getNumSlicesTextField());
        numSlices = Integer.valueOf(getNumSlicesTextField().getText());
        allValid &= checkInitialValuesValidity();
        if (allValid) {
            numSlices = Integer.valueOf(getNumSlicesTextField().getText());
            if (getCostDiscountTextField() != null) {
                costDiscount = Double.valueOf(getCostDiscountTextField().getText());
            }
            if (getEffectivenessDiscountTextField() != null) {
                effectivenessDiscount = Double.valueOf(getEffectivenessDiscountTextField().getText());
            }
            if (numSimulationsTextField != null) {
                simulationsNumber = Integer.valueOf(numSimulationsTextField.getText());
            }
        }
        return allValid;
    }

    public double getCostDiscount() {
        return costDiscount;
    }

    public double getEffectivenessDiscount() {
        return effectivenessDiscount;
    }

    public int getNumSlices() {
        return numSlices;
    }

    public TransitionTime getTransitionTime() {
        TransitionTime transitionTime = TransitionTime.BEGINNING;
        if (halfCycleButton != null && halfCycleButton.isSelected()) {
            transitionTime = TransitionTime.HALF;
        }
        if (endOfCycleButton != null && endOfCycleButton.isSelected()) {
            transitionTime = TransitionTime.END;
        }
        return transitionTime;
    }

    public int getSimulationsNumber() {
        return simulationsNumber;
    }

    public boolean isCumulative() {
        return this.isCumulative;
    }

    public Map<Variable, Double> getInitialValues() {
        return initialValues;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem().equals(getInstantValuesButton())) {
            this.isCumulative = false;
        }
        if (e.getItem().equals(getCumulativeValuesButton())) {
            this.isCumulative = true;
        }
    }

    private void setTitle(String netName, boolean isTemporalEvolution) {
        String title = stringDatabase.getString(((isTemporalEvolution) ? "CostEffectiveness.TemporalEvolution"
                : "CostEffectiveness.Analysis")
                + ".Label");
        super.setTitle(title + " - " + FilenameUtils.getBaseName(netName));
    }

    private boolean checkInitialValuesValidity() {
        boolean allValid = true;
        if (initialValueComponents != null) {
            for (JTextField numericTemporalField : initialValueComponents.values()) {
                allValid &= checkTextFieldValidity(numericTemporalField);
            }
        }
        return allValid;
    }

    private boolean checkTextFieldValidity(JTextField sourceTextField) {
        boolean valid = true;
        if (initialValueComponents.containsKey(sourceTextField.getName())
                || sourceTextField.equals(getNumSlicesTextField())) {
            boolean numSlicesDefined = getNumSlicesTextField().getText() != null;
            int numSlices = (numSlicesDefined) ? Integer.valueOf(getNumSlicesTextField().getText())
                    : -1;
            if (initialValues != null) {
                for (Variable numericTemporalVariable : initialValues.keySet()) {
                    PartitionedInterval interval = numericTemporalVariable.getPartitionedInterval();
                    double numericValue = Double.parseDouble(initialValueComponents.get(numericTemporalVariable.getName()).getText());
                    double timeHorizon = numericValue + numSlices;
                    if (numSlicesDefined) {
                        if ((!interval.isRightClosed() && timeHorizon >= interval.getMax())
                                || timeHorizon > interval.getMax()) {
                            JOptionPane.showMessageDialog(this.getParent(),
                                    numericTemporalVariable.getBaseName()
                                            + " "
                                            + stringDatabase.getString("CostEffectiveness.ExceedsTimeHorizon"));
                            valid = false;
                        }
                    }
                    if ((!interval.isLeftClosed() && numericValue <= interval.getMin())
                            || numericValue < interval.getMin()) {
                        JOptionPane.showMessageDialog(this.getParent(),
                                numericTemporalVariable.getBaseName()
                                        + " "
                                        + stringDatabase.getString("CostEffectiveness.VariableTooLow"));
                        valid = false;
                    }
                    if (valid) {
                        initialValues.put(numericTemporalVariable, numericValue);
                    }
                    initialValueComponents.get(numericTemporalVariable.getName()).setText(""
                            + numericValue);
                }
            }
            if (valid) {
                this.numSlices = numSlices;
            }
            getNumSlicesTextField().setText("" + this.numSlices);
        }
        if (sourceTextField.equals(numSimulationsTextField)) {
            try {
                int newValue = Integer.parseInt(numSimulationsTextField.getText());
                this.numSimulations = newValue;
            } catch (NumberFormatException e) {
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Ignore
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() instanceof JTextField && ((JTextField) e.getSource()).getName() != null) {
            JTextField sourceTextField = (JTextField) e.getSource();
            checkTextFieldValidity(sourceTextField);
        }
    }

    public Integer getNumSimulations() {
        return numSimulations;
    }
}
