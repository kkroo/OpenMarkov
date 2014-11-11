/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openmarkov.core.exception.ExceptionUncertainValuesDialogEdition;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.BetaFunction;
import org.openmarkov.core.model.network.modelUncertainty.ComplementFamily;
import org.openmarkov.core.model.network.modelUncertainty.ComplementFunction;
import org.openmarkov.core.model.network.modelUncertainty.DirichletFamily;
import org.openmarkov.core.model.network.modelUncertainty.DirichletFunction;
import org.openmarkov.core.model.network.modelUncertainty.ExactFunction;
import org.openmarkov.core.model.network.modelUncertainty.FamilyDistribution;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionManager;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionType;
import org.openmarkov.core.model.network.modelUncertainty.RangeFunction;
import org.openmarkov.core.model.network.modelUncertainty.Tools;
import org.openmarkov.core.model.network.modelUncertainty.TriangularFunction;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.TablePotential;

public class UncertainValuesDialog extends OkCancelHorizontalDialog {

    private static final int STATE_COLUMN_INDEX        = 0;
    private static final int DISTRIBUTION_COLUMN_INDEX = 1;
    private static final int PARAMETERS_COLUMN_INDEX   = 2;
    private static final int NAME_COLUMN_INDEX         = 3;

    public class DistributionsTableListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            if (e.getColumn() == DISTRIBUTION_COLUMN_INDEX) {
                int selectedRow = distributionTable.getSelectedRow();
                String distributionType = distributionTableModel.getValueAt(selectedRow,
                        DISTRIBUTION_COLUMN_INDEX).toString();
                DistributionParameterDialog parameterDialog = new DistributionParameterDialog(getOwner(),
                        distributionType);
                if(!distributionTypes.get(selectedRow).equals(distributionType))
                {
                    parameterDialog.setVisible(true);
                    if (parameterDialog.getSelectedButton() == OK_BUTTON) {
                        StringBuilder parameterString = new StringBuilder();
                        for (double parameter : parameterDialog.getParameters()) {
                            parameterString.append(parameter);
                            parameterString.append(" ");
                        }
                        distributionTableModel.setValueAt(parameterString.toString(),
                                selectedRow,
                                PARAMETERS_COLUMN_INDEX);
                        distributionTypes.set(selectedRow, distributionType);
                    }else
                    {
                        distributionTableModel.setValueAt(distributionTypes.get(selectedRow),
                                selectedRow,
                                DISTRIBUTION_COLUMN_INDEX);
                    }
                }
            }
        }
    }

    public class DistributionsTableMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2
                    && distributionTable.getSelectedColumn() == PARAMETERS_COLUMN_INDEX) {
                int selectedRow = distributionTable.getSelectedRow();
                String distributionType = distributionTableModel.getValueAt(selectedRow,
                        DISTRIBUTION_COLUMN_INDEX).toString();
                String currentParameters = distributionTableModel.getValueAt(selectedRow,
                        PARAMETERS_COLUMN_INDEX).toString();
                double[] parameters = null;
                if (!currentParameters.isEmpty()) {
                    String[] parameterArray = currentParameters.split(" ");
                    parameters = new double[parameterArray.length];
                    for (int i = 0; i < parameters.length; ++i) {
                        parameters[i] = Double.parseDouble(parameterArray[i]);
                    }
                }
                DistributionParameterDialog parameterDialog = new DistributionParameterDialog(getOwner(),
                        distributionType,
                        parameters);
                parameterDialog.setVisible(true);
                if (parameterDialog.getSelectedButton() == OK_BUTTON) {
                    StringBuilder parameterString = new StringBuilder();
                    for (double parameter : parameterDialog.getParameters()) {
                        parameterString.append(parameter);
                        parameterString.append(" ");
                    }
                    distributionTableModel.setValueAt(parameterString.toString(),
                            selectedRow,
                            PARAMETERS_COLUMN_INDEX);
                }
            }
        }
    }

    public class DistributionTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 1L;

        public DistributionTableModel(Object[][] initialData, String[] columnNames) {
            super(initialData, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return (col == DISTRIBUTION_COLUMN_INDEX) || (col == NAME_COLUMN_INDEX);
        }
    }

    private static final long      serialVersionUID = 1L;
    // Components related to the distributions box
    private DistributionTableModel distributionTableModel;
    private JTable                 distributionTable;
    private JPanel                 distributionsPanel;
    private Variable               variable;
    private List<String>           distributionTypes;
    private boolean                isChanceVariable;

    // List of uncertain values
    private List<UncertainValue>   uncertainColumn;
    // List of doubles calculated from uncertainColum by taking the mean value
    private List<Double>           valuesColumn;
    // Base position for storing the array of uncertain values in the table
    // potential
    private int                    posBase;

    /**
     * @param owner
     * @param variable
     * @param configuration
     * @param uncertainValues
     * @throws WrongCriterionException
     * @wbp.parser.constructor
     */
    public UncertainValuesDialog(Window owner, EvidenceCase configuration, TablePotential potential)
            throws WrongCriterionException {
        super(owner);
        isChanceVariable = !(potential.isUtility());
        distributionTypes = new ArrayList<>();
        variable = isChanceVariable ? potential.getVariable(0) : potential.getUtilityVariable();
        setTitle(getConfigurationDescription(variable, isChanceVariable, configuration));
        posBase = getPositionBaseUncertainValue(potential, configuration);
        setResizable(true);
        JPanel componentsPanel = getComponentsPanel();
        // Panel of distributions
        distributionsPanel = new JPanel();
        fillDistributionsTableModel(variable, configuration, potential);
        distributionTable.getModel().addTableModelListener(new DistributionsTableListener());
        distributionTable.addMouseListener(new DistributionsTableMouseListener());
        distributionsPanel.setBorder(new TitledBorder("Distributions"));
        JScrollPane distributionsTablePane = new JScrollPane(distributionTable);
        distributionsPanel.add(distributionsTablePane);
        distributionsTablePane.setPreferredSize(new Dimension(300, 100));
        distributionsPanel.setPreferredSize(new Dimension(350, 150));
        componentsPanel.add(distributionsPanel);
        try {
            initialize();
        } catch (Throwable e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    stringDatabase.getString(e.getMessage()),
                    stringDatabase.getString(e.getMessage()),
                    JOptionPane.ERROR_MESSAGE);
        }

        Point parentLocation = owner.getLocation();
        Dimension parentSize = owner.getSize();
        int x = (int) (parentLocation.getX() + parentSize.getWidth() / 2 - getSize().getWidth() / 2);
        int y = (int) (parentLocation.getY() + parentSize.getHeight() / 2 - getSize().getHeight() / 2);
        setLocation(new Point(x, y));
    }

    public int requestUncertainValues() {
        setVisible(true);
        return this.selectedButton;
    }

    public List<Double> getValuesColumn() {
        return valuesColumn;
    }

    public List<UncertainValue> getUncertainColumn() {
        return uncertainColumn;
    }

    public int getPosBase() {
        return posBase;
    }

    private int getPositionBaseUncertainValue(TablePotential potential, EvidenceCase configuration) {
        int[] coordinates;
        int sizeCoordinates;
        int pos;
        int sizeEvi = configuration.getFindings().size();
        sizeCoordinates = sizeEvi + (isChanceVariable ? 1 : 0);
        coordinates = new int[sizeCoordinates];
        List<Variable> varsTable = potential.getVariables();
        int startLoop;
        if (isChanceVariable) {
            coordinates[0] = 0;
            startLoop = 1;
        } else {
            startLoop = 0;
        }
        for (int i = startLoop; i < sizeCoordinates; i++) {
            coordinates[i] = configuration.getFinding(varsTable.get(i)).getStateIndex();
        }
        pos = potential.getPosition(coordinates);
        return pos;
    }

    private void fillDistributionsTableModel(Variable variable,
            EvidenceCase configuration,
            TablePotential potential)
            throws WrongCriterionException {
        UncertainValue[] uncertainTable = potential.getUncertaintyTable();
        TablePotential projectedPotential = null;
        try {
            projectedPotential = potential.tableProject(configuration, null).get(0);
        } catch (NonProjectablePotentialException e) {
            e.printStackTrace();
        }
        UncertainValue[] projectedUncertainTable = projectedPotential.getUncertaintyTable();
        // Get the table of uncertain values
        if (!hasUncertainValues(projectedUncertainTable)) {
            // Case assign
            uncertainTable = createExactUncertainValuesFromDouble(projectedPotential);
        } else {
            // Case edit
            uncertainTable = projectedPotential.getUncertaintyTable();
        }
        // Fill the table for the dialog
        String[] columnNames = new String[] { "State", "Distribution", "Parameters", "Name" };
        List<String> allowedDistributionTypes = ProbDensFunctionManager.getUniqueInstance().getValidProbDensFunctions(isChanceVariable);
        State[] states = variable.getStates();
        int numStates = states.length;
        Object[][] initialData = new Object[numStates][columnNames.length];
        JComboBox<String> distributionTypesCombo = new JComboBox<String>();
        for (String allowedDistributionType : allowedDistributionTypes) {
            distributionTypesCombo.addItem(allowedDistributionType);
        }
        int lastPosStates = numStates - 1;
        for (int i = 0; i < numStates; i++) {
            UncertainValue uncertainValue = uncertainTable[i];
            ProbDensFunction probDensFunction = uncertainValue.getProbDensFunction();
            String distribution = probDensFunction.getClass().getAnnotation(ProbDensFunctionType.class).name();
            distributionTypes.add(distribution);
            int iPosInitialData = lastPosStates - i;
            initialData[iPosInitialData][STATE_COLUMN_INDEX] = states[i].getName();
            initialData[iPosInitialData][DISTRIBUTION_COLUMN_INDEX] = distribution;
            initialData[iPosInitialData][PARAMETERS_COLUMN_INDEX] = getString(probDensFunction.getParameters());
            initialData[iPosInitialData][NAME_COLUMN_INDEX] = uncertainValue.getName();
        }
        distributionTableModel = new DistributionTableModel(initialData, columnNames);
        distributionTable = new JTable(distributionTableModel);
        // Model for the column "Distribution"
        TableColumnModel columnModel = distributionTable.getColumnModel();
        TableColumn column = columnModel.getColumn(DISTRIBUTION_COLUMN_INDEX);
        column.setCellEditor(new DefaultCellEditor(distributionTypesCombo));
        columnModel.getColumn(0).setCellEditor(null);
    }

    private String getString(double[] parameters) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<parameters.length; ++i)
        {
            sb.append(parameters[i]);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * @param projectedPotential
     *            Table potential which has no uncertain values. Its values are
     *            used for creating the uncertain values
     * @return An array of uncertain values
     */
    private UncertainValue[] createExactUncertainValuesFromDouble(TablePotential projectedPotential) {
        double[] tableProjected = projectedPotential.getValues();
        UncertainValue[] uncertainTable = new UncertainValue[tableProjected.length];
        for (int i = 0; i < tableProjected.length; i++) {
            uncertainTable[i] = new UncertainValue(tableProjected[i]);
        }
        return uncertainTable;
    }

    public static boolean hasUncertainValues(UncertainValue[] auxUncertainTable) {
        boolean hasUncertainValues;
        if ((auxUncertainTable == null) || (auxUncertainTable.length == 0)) {
            hasUncertainValues = false;
        } else {
            hasUncertainValues = false;
            for (int i = 0; (i < auxUncertainTable.length) && !hasUncertainValues; i++) {
                hasUncertainValues = (auxUncertainTable[i] != null);
            }
        }
        return hasUncertainValues;
    }

    private String getConfigurationDescription(Variable variable,
            boolean isChanceVariable,
            EvidenceCase configuration) {
        StringBuilder sb = new StringBuilder();
        sb.append((isChanceVariable) ? "P" : "U");
        sb.append("(");
        sb.append(variable.getName());
        sb.append(" | ");
        List<Finding> findings = configuration.getFindings();
        for (Finding finding : findings) {
            sb.append(finding.getVariable().getName());
            sb.append(" = '");
            sb.append(finding.getState());
            sb.append("', ");
        }
        if (sb.charAt(sb.length() - 2) == ',') {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * This method initializes this instance.
     */
    private void initialize() {
        setName("UncertainValuesDialog");
        iconLoader = new IconLoader();
        configureButtonsPanel();
        setDefaultButton(getJButtonOK());
        quitIconsOfButtons();
        pack();
    }

    /**
     * This method carries out the actions when the user press the Ok button
     * before hide the dialog.
     * 
     * @return true if the dialog box can be closed.
     */
    protected boolean doOkClickBeforeHide() {
        List<UncertainValue> uncertainValues = readDataFromTable();
        boolean verify = verifyLocalConstraintsUncertainty(uncertainValues);
        if (verify) {
            if (isChanceVariable) {
                if (!verifyGlobalConstraintUncertainty(uncertainValues)) {
                    // System.out.println("Distribution "+typeDistrib+" does not verify the constraints associated to its domain.");
                    verify = false;
                }
            }
        }
        if (verify) {
            uncertainColumn = reverse(uncertainValues);
            valuesColumn = calculateReferenceValues();
        }
        return verify;
    };

    private List<UncertainValue> reverse(List<UncertainValue> list) {
        List<UncertainValue> rev = new ArrayList<UncertainValue>();
        for (int i = list.size() - 1; i >= 0; i--) {
            rev.add(list.get(i));
        }
        return rev;
    }

    private List<Double> calculateReferenceValues() {
        List<Integer> complementIndexes = new ArrayList<Integer>();
        List<Integer> dirichletIndexes = new ArrayList<Integer>();
        List<Integer> otherIndexes = new ArrayList<Integer>();
        double[] refValues = new double[uncertainColumn.size()];
        ComplementFamily comp = new ComplementFamily(uncertainColumn);
        DirichletFamily dir = new DirichletFamily(uncertainColumn);
        List<UncertainValue> otherUncertain = new ArrayList<UncertainValue>();
        for (int i = 0; i < uncertainColumn.size(); i++) {
            UncertainValue uncertainValue = uncertainColumn.get(i);
            if (uncertainValue.getProbDensFunction() instanceof ComplementFunction) {
                complementIndexes.add(i);
            } else if (uncertainValue.getProbDensFunction() instanceof DirichletFunction) {
                dirichletIndexes.add(i);
            } else {
                otherIndexes.add(i);
            }
        }
        otherUncertain = getElementsFromIndexes(uncertainColumn, otherIndexes);
        // Process other
        FamilyDistribution other = new FamilyDistribution(otherUncertain);
        double[] meanOther = other.getMean();
        placeInArray(refValues, otherIndexes, meanOther);
        // Process Dirichlet
        double[] meanDir = dir.getMean();
        placeInArray(refValues, dirichletIndexes, meanDir);
        // Process complements
        double massForComp = 1.0 - (Tools.sum(meanOther) + Tools.sum(meanDir));
        comp.setProbMass(massForComp);
        double[] meanComp = comp.getMean();
        placeInArray(refValues, complementIndexes, meanComp);
        List<Double> ref = new ArrayList<Double>();
        for (int i = 0; i < refValues.length; i++) {
            ref.add(refValues[i]);
        }
        return ref;
    }

    private static void placeInArray(double[] refValue, List<Integer> indexes, double[] x) {
        for (int i = 0; i < indexes.size(); i++) {
            refValue[indexes.get(i)] = x[i];
        }
    }

    private static List<UncertainValue> getElementsFromIndexes(List<UncertainValue> column,
            List<Integer> index) {
        List<UncertainValue> list = new ArrayList<UncertainValue>();
        for (Integer aux : index) {
            list.add(column.get(aux));
        }
        return list;
    }

    private boolean verifyLocalConstraintsUncertainty(List<UncertainValue> uncertainvalues) {
        boolean comply = true;
        // Verify individual constraints for each Uncertain Value
        for (int i = 0; i < uncertainvalues.size() && comply; i++) {
            UncertainValue uncertainValue = uncertainvalues.get(i);
            String distributionName = uncertainValue.getProbDensFunction().getClass().getAnnotation(ProbDensFunctionType.class).name();

            if (!uncertainValue.verifyParametersDomain(isChanceVariable)) {
                try {
                    String message = "Distribution "
                            + distributionName
                            + " does not comply with the constraints associated to its domain.";
                    throw new ExceptionUncertainValuesDialogEdition(message);
                } catch (ExceptionUncertainValuesDialogEdition e) {
                }
                comply = false;
            }

        }
        return comply;
    }

    private boolean verifyGlobalConstraintUncertainty(List<UncertainValue> uncertainValues) {
        FamilyDistribution family = new FamilyDistribution(uncertainValues);
        return (doVerifyRule1(family) && doVerifyRule2(family) && doVerifyRule3(family));
    }

    /*
     * If one of the distributions is Exact, Range, or Triangular, then: 
     * • all of the others must be either exact, or range, or triangular, or complement;
     * • at least one of the others must be Complement; 
     * • the sum of the maxima of all the distributions (different from Complement) 
     * cannot be greater than 1.
     */
    private boolean doVerifyRule1(FamilyDistribution family) {
        boolean verify = false;
        List<UncertainValue> exactRangeOrUncertain = null;
        List<Class<? extends ProbDensFunction>> rangeOrTriangTypes = new ArrayList<>();
        rangeOrTriangTypes.add(RangeFunction.class);
        rangeOrTriangTypes.add(TriangularFunction.class);
        List<UncertainValue> uncertainFamily = family.getFamily();
        List<UncertainValue> exactUncertain = getUncertainValuesOfClass(uncertainFamily,
                ExactFunction.class);
        int totalSizeFamily = uncertainFamily.size();
        List<UncertainValue> rangeOrTriangUncertain = getUncertainValuesOfClasses(uncertainFamily,
                rangeOrTriangTypes);
        int sizeRangeOrTriang = rangeOrTriangUncertain.size();
        int sizeExact = exactUncertain.size();
        if (sizeRangeOrTriang > 0 || thereAreExactValuesGreaterThanZero(exactUncertain)) {
            int numComplement = getUncertainValuesOfClass(uncertainFamily, ComplementFunction.class).size();
            exactRangeOrUncertain = new ArrayList<UncertainValue>(rangeOrTriangUncertain);
            exactRangeOrUncertain.addAll(exactUncertain);
            verify = ((numComplement > 0) && (sizeExact + sizeRangeOrTriang + numComplement == totalSizeFamily))
                    && (Tools.sum(new FamilyDistribution(exactRangeOrUncertain).getMaximum()) <= 1.0);
        } else {
            verify = true;
        }
        if (!verify) {
            try {
                String message = "Following rule has been broken. \n";
                message += "  If one of the distributions is Exact with v != 0, Range, or Triangular, then:\n";
                message += "    · all the others must be either Exact, Range, Triangular, or Complement;\n";
                message += "    · at least one of the others must be Complement;\n";
                message += "    · the sum of the maxima of all the distributions (different from Complement) cannot be greater than 1;";
                throw new ExceptionUncertainValuesDialogEdition(message);
            } catch (ExceptionUncertainValuesDialogEdition e) {
            }
        }
        return verify;
    }

    private static List<UncertainValue> getUncertainValuesOfClasses(List<UncertainValue> uncertainValues,
            List<Class<? extends ProbDensFunction>> classes) {
        List<UncertainValue> filtered = new ArrayList<UncertainValue>();
        for (UncertainValue aux : uncertainValues) {
            boolean isInClasses = false;
            for (int i = 0; (i < classes.size()) && !isInClasses; i++) {
                isInClasses = classes.get(i).isAssignableFrom(aux.getProbDensFunction().getClass());
            }
            if (isInClasses) {
                filtered.add(aux);
            }
        }
        return filtered;
    }

    private static boolean thereAreExactValuesGreaterThanZero(List<UncertainValue> arrayUncertain) {
        boolean thereAre = false;
        for (int i = 0; (i < arrayUncertain.size()) && !thereAre; i++) {
            UncertainValue aux = arrayUncertain.get(i);
            ProbDensFunction probDensityFunction = aux.getProbDensFunction();
            thereAre = (probDensityFunction instanceof ExactFunction)
                    && probDensityFunction.getMean() > 0;
        }
        return thereAre;
    }

    /**
     * @param uncertainValues
     * @param types
     * @return
     */
    private static int[] getIndexesUncertainValuesOfClasses(List<UncertainValue> uncertainValues,
            List<Class<? extends ProbDensFunction>> types) {
        List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < uncertainValues.size(); i++) {
            UncertainValue uncertainValue = uncertainValues.get(i);
            ProbDensFunction probDensFunction = uncertainValue.getProbDensFunction();
            boolean isInTypes = false;
            for (int j = 0; (j < types.size()) && !isInTypes; j++) {
                isInTypes = types.get(j).isAssignableFrom(probDensFunction.getClass());
            }
            if (isInTypes) {
                indexes.add(i);
            }
        }
        int numIndexesOfTypes = indexes.size();
        int[] intIndexes = new int[numIndexesOfTypes];
        for (int i = 0; i < numIndexesOfTypes; i++) {
            intIndexes[i] = indexes.get(i);
        }
        return intIndexes;
    }

    public static int[] getIndexesUncertainValuesOfClass(List<UncertainValue> uncertainValues,
            Class<? extends ProbDensFunction> functionClass) {
        List<Class<? extends ProbDensFunction>> classes = new ArrayList<>();
        classes.add(functionClass);
        return getIndexesUncertainValuesOfClasses(uncertainValues, classes);
    }

    private static List<UncertainValue> getUncertainValuesOfClass(List<UncertainValue> arrayUncertain,
            Class<? extends ProbDensFunction> type) {
        List<Class<? extends ProbDensFunction>> types = new ArrayList<>();
        types.add(type);
        return getUncertainValuesOfClasses(arrayUncertain, types);
    }

    @SuppressWarnings("unused")
    private boolean doVerifyRule4(FamilyDistribution family) {
        boolean verify;
        List<UncertainValue> uncertainFamily = family.getFamily();
        int totalSizeFamily = uncertainFamily.size();
        List<UncertainValue> compUncertain = getUncertainValuesOfClass(uncertainFamily,
                ComplementFunction.class);
        verify = (totalSizeFamily != compUncertain.size());
        if (!verify) {
            try {
                String message = "Rule 4 of the specification of sensitivity analysis in ProbModelXML has been violated. Please, check the distributions and its parameters.";
                throw new ExceptionUncertainValuesDialogEdition(message);
            } catch (ExceptionUncertainValuesDialogEdition e) {
            }
        }
        return verify;
    }

    private boolean doVerifyRule3(FamilyDistribution family) {
        int totalSizeFamily;
        boolean verify;
        List<UncertainValue> uncertainFamily = family.getFamily();
        totalSizeFamily = uncertainFamily.size();
        List<UncertainValue> dirUncertain = getUncertainValuesOfClass(uncertainFamily,
                DirichletFunction.class);
        int numDirichlet = dirUncertain.size();
        if (numDirichlet > 0) {
            if (numDirichlet > 1) {
                List<UncertainValue> exactUncertain = getUncertainValuesOfClass(uncertainFamily,
                        ExactFunction.class);
                int numExact = exactUncertain.size();
                verify = ((numExact + numDirichlet == totalSizeFamily) && areAllZero(new FamilyDistribution(exactUncertain).getMean()));
            } else {
                verify = false;
            }
        } else {
            verify = true;
        }
        if (!verify) {
            try {
                String message = "Following rule has been broken. \n";
                message += "  If one of the distributions is a Dirichlet, then:\n";
                message += "    · all the others must be Exact with v = 0 or Dirichlet\n";
                message += "    · at least one of the others must also be a Dirichlet.";
                throw new ExceptionUncertainValuesDialogEdition(message);
            } catch (ExceptionUncertainValuesDialogEdition e) {
            }
        }
        return verify;
    }

    /*
     * If one of the distributions is a Beta, then: 
     * • all the others must be Exact, with v = 0, or Complement; 
     * • at least one of the others must be Complement.
     */
    private boolean doVerifyRule2(FamilyDistribution family) {
        int totalSizeFamily;
        boolean verify;
        List<UncertainValue> uncertainFamily = family.getFamily();
        totalSizeFamily = uncertainFamily.size();
        List<UncertainValue> betaUncertain = getUncertainValuesOfClass(uncertainFamily,
                BetaFunction.class);
        int numBeta = betaUncertain.size();
        if (numBeta > 0) {
            if (numBeta == 1) {
                List<UncertainValue> exactUncertain = getUncertainValuesOfClass(uncertainFamily,
                        ExactFunction.class);
                List<UncertainValue> compUncertain = getUncertainValuesOfClass(uncertainFamily,
                        ComplementFunction.class);
                int numExact = exactUncertain.size();
                int numComp = compUncertain.size();
                verify = ((numExact + numComp + 1 == totalSizeFamily)
                        && areAllZero(new FamilyDistribution(exactUncertain).getMean()) && (numComp >= 1));
            } else {
                verify = false;
            }
        } else {
            verify = true;
        }
        if (!verify) {
            try {
                String message = "Following rule has been broken.\n";
                message += "  If one of the distributions is a Beta, then:\n";
                message += "    · all the others must be Exact with v = 0 or Complement\n";
                message += "    · at least one of the others must be Complement.";
                throw new ExceptionUncertainValuesDialogEdition(message);
            } catch (ExceptionUncertainValuesDialogEdition e) {
            }
        }
        return verify;
    }

    private boolean areAllZero(double[] x) {
        boolean allZero;
        allZero = true;
        for (int i = 0; (i < x.length) && allZero; i++) {
            allZero = x[i] == 0.0;
        }
        return allZero;
    }

    public boolean isChanceVariable() {
        return isChanceVariable;
    }

    private List<UncertainValue> readDataFromTable() {
        Vector<?> data = distributionTableModel.getDataVector();
        int numRows = data.size();
        List<UncertainValue> uncertainValues = new ArrayList<UncertainValue>();
        ProbDensFunctionManager distributionManager = ProbDensFunctionManager.getUniqueInstance();
        for (int i = 0; i < numRows; i++) {
            Vector<?> row = (Vector<?>) data.get(i);
            String distributionType = row.get(DISTRIBUTION_COLUMN_INDEX).toString();
            String[] parameters = row.get(PARAMETERS_COLUMN_INDEX).toString().split(" ");
            double[] parameterArray = new double[parameters.length];
            for (int j = 0; j < parameters.length; ++j) {
                parameterArray[j] = Double.parseDouble(parameters[j]);
            }
            String name = (String) row.get(NAME_COLUMN_INDEX);
            ProbDensFunction probDensFunction = distributionManager.newInstance(distributionType,
                    parameterArray);
            UncertainValue uncertainValue = new UncertainValue(probDensFunction, name);
            uncertainValues.add(uncertainValue);
        }
        return uncertainValues;
    }

    private void quitIconsOfButtons() {
        this.getJButtonOK().setIcon(null);
        this.getJButtonCancel().setIcon(null);
    }

    /**
     * Sets up the panel where the buttons of the buttons panel will be appear.
     */
    private void configureButtonsPanel() {
        addButtonToButtonsPanel(getJButtonOK());
        // addButtonToButtonsPanel(getJButtonRemove());
        addButtonToButtonsPanel(getJButtonCancel());
    }

    /**
     * This class is used for painting and coloring the table and the headers
     */
    @SuppressWarnings("unused")
    private class RendererConfigurationTable extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            setBackground((row == 1)? Color.gray : Color.white);
            return super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
        }
    }
}
