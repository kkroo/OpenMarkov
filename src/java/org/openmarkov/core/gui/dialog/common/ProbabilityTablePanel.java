/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;

/**
 * This class implements a potentials table with the following features:
 * <ul>
 * <li>Its elements, except the first column, are modifiable.</li>
 * <li>New elements can be added, creating a new key row with empty data.</li>
 * <li>The key data (first column) consist of a key string following of the
 * index of the row and it is used for internal purposes only.</li>
 * <li>The key data is hidden.</li>
 * <li>The information of a row (except the first column) can not be taken up or
 * down.</li>
 * <li>The rows can not be removed.</li>
 * <li>The first editable row is the one that has the values of the potentials.</li>
 * <li>The rows between 0 and the first editable row are ocuppied by the values
 * of the states of the parents of the variable.</li>
 * <li>The header of columns is hidden.</li>
 * </ul>
 * This class is based upon KeyTablePanel without buttons
 * 
 * @author jlgozalo
 * @author maryebra
 * @version 1.0 jlgozalo
 */
public abstract class ProbabilityTablePanel extends PotentialPanel implements ActionListener {

    /**
     * Static field for serializable class.
     */
    private static final long       serialVersionUID                      = 6257314234781632512L;
    /**
     * Name of the columns of the table.
     */
    protected String[]              columns                               = null;

    /**
     * Data of the cells.
     */
    protected Object[][]            data                                  = null;
    /**
     * number of positions in this table
     */
    protected int                   position                              = -1;
    /**
     * list of variables that are shown in this table
     */
    protected List<Variable>        variables                             = null;

    /**
     * list of potentials for the variable
     */
    protected List<Potential>       listPotentials                        = null;

    /**
     * first editable row (only for temporal storage)
     */
    protected int                   firstEditableRow                      = -1;
    /**
     * last editable row (only for temporal storage)
     */
    protected int                   lastEditableRow                       = -1;

    /**
     * base index for coordinates in the table
     */
    private int                     baseIndexForCoordinates               = -1;

    /**
     * Icon loader.
     */
    protected IconLoader            iconLoader                            = null;

    /**
     * Properties for options to display in the table
     */
    protected boolean               showAllParameters                     = true;
    protected boolean               showProbabilitiesValues               = true;
    protected boolean               showTPCvalues                         = true;
    protected boolean               showNetValues                         = true;

    // private ProbNode probNode;
    /**
     * The contextualMenu that appears when there is a click on the valuesTable
     * Object
     */
    protected ContextualMenuFactory contextualMenuFactory;

    protected EvidenceCase          evidenceCase;
    /**
     * index of the column selected in valuesTable
     */
    protected int                   selectedColumn                        = -1;

    private JLabel                  jLabelNodeRelationComment;

    protected StringDatabase        stringDatabase                        = StringDatabase.getUniqueInstance();

    /**
     * this is a default constructor with no construction parameters
     * 
     * @wbp.parser.constructor
     */
    public ProbabilityTablePanel() {

        this(new String[] { "id", "states", "values" },
                new Object[][] { new Object[] { 0, null, 0 } }); // default init
    }

    /**
     * This is the default constructor
     * 
     * @param newColumns
     *            array of texts that appear in the header of the columns.
     * @param newData
     *            content of the cells.
     */
    public ProbabilityTablePanel(String[] newColumns, Object[][] newData) {

        iconLoader = new IconLoader();
        columns = newColumns.clone();
        data = newData.clone();
        repaint();
    }

    /**
     * @return label for the node relation comment
     */
    protected JLabel getJLabelNodeRelationComment() {

        if (jLabelNodeRelationComment == null) {
            jLabelNodeRelationComment = new JLabel();
            jLabelNodeRelationComment.setName("jLabelNodeRelationComment");
            jLabelNodeRelationComment.setText("a Label");
            jLabelNodeRelationComment.setText(stringDatabase.getString("NodeProbsValuesTablePanel.jLabelNodeRelationComment.Text"));
        }
        return jLabelNodeRelationComment;
    }

    /**
     * @return the showAllParameters
     */
    public boolean isShowAllParameters() {

        return showAllParameters;
    }

    /**
     * @return the showProbabilitiesValues
     */
    public boolean isShowProbabilitiesValues() {

        return showProbabilitiesValues;
    }

    /**
     * @return the showTPCvalues
     */
    public boolean isShowTPCvalues() {

        return showTPCvalues;
    }

    /**
     * @return the showNetValues
     */
    public boolean isShowNetValues() {

        return showNetValues;
    }

    /**
     * @param showNetValues
     *            the showNetValues to set
     */
    public void setShowNetValues(boolean showNetValues) {

        this.showNetValues = showNetValues;
        if (isShowNetValues()) {
            // show Net values
        } else {
            // show compound values
        }
    }

    /**
     * sets the first row for edition
     * 
     * @param firstRow
     *            - the first row that is available for edition
     */
    protected void setFirstEditableRow(int firstEditableRow) {

        this.firstEditableRow = firstEditableRow;

    }

    /**
     * gets the first row on edition
     * 
     * @return first row for edition
     */
    protected int getFirstEditableRow() {

        return this.firstEditableRow;
    }

    /**
     * @return the lastEditableRow
     */
    protected int getLastEditableRow() {

        return lastEditableRow;
    }

    /**
     * @param lastEditableRow
     *            the lastEditableRow to set
     */
    protected void setLastEditableRow(int lastEditableRow) {

        this.lastEditableRow = lastEditableRow;
    }

    /**
     * @return the position
     */
    protected int getPosition() {

        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    protected void setPosition(int position) {

        this.position = position;
    }

    /**
     * @return the variables
     */
    protected List<Variable> getVariables() {

        return variables;
    }

    /**
     * @param variables
     *            the variables to set
     */

    /**
     * @param listPotentials
     *            the listPotentials to set
     */
    public void setListPotentials(ArrayList<Potential> listPotentials) {

        this.listPotentials = listPotentials;
    }

    /**
     * Set the Base index for the coordinates in the table related to the
     * Potential of the variable of this node
     * 
     * @param value
     *            - the new base index for coordinates in the table
     */
    protected void setBaseIndexForCoordinates(int value) {
        this.baseIndexForCoordinates = value;
    }

    public void addParent(Variable parent) {

    }

    public void deleteParent(Variable parent) {

    }

    public void addState(String state) {

    }

    public void deleteState(String state) {

    }

    // @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    public Object[][] getData() {
        return this.data;
    }

}
