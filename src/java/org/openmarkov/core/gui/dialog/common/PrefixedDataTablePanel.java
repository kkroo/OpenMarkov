/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.common;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;

/**
 * This class implements a key table with the following features:
 * <ul>
 * <li>Its elements aren't modifiable.</li>
 * <li>New elements can be added selecting them of a prefixed set.</li>
 * <li>An element of the prefixed set can be added only once.</li>
 * <li>The first column is treated as the rest of columns.</li>
 * <li>The information of a row (except the first column) can't be taken up or
 * down.</li>
 * <li>The rows can be removed.</li>
 * </ul>
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @author jlgozalo
 * @version 1.0 jlgozalo - change class modifier to public
 */
public class PrefixedDataTablePanel extends KeyTablePanel {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 2127072068749928448L;

	/**
	 * Prefixed data.
	 */
	private Object[][] prefixedData = null;

	/**
	 * Array that contains the prefixed data that is not in the table.
	 */
	private Object[][] absentData = null;

	/**
	 * String that appears in the titlebar of the dialog box showed to add new
	 * rows.
	 */
	private String titleToSelectRows;

	private ProbNode probNode;
	
	
	ArrayList<PNEdit> edits = new ArrayList<PNEdit>();

	/**
	 * This is the default constructor
	 * 
	 * @param newColumns
	 *            array of texts that appear in the header of the columns.
	 * @param newData
	 *            content of the cells (subset of prefixedData).
	 * @param newPrefixedData
	 *            content that can appears into the cells.
	 * @param newTitleToSelectRows
	 *            title of the window where the user can select new rows.
	 */
	public PrefixedDataTablePanel(ProbNode probNode, String[] newColumns, Object[][] newData,
									Object[][] newPrefixedData,
									String newTitleToSelectRows,
									boolean firstColumnHidden){

		super(newColumns, new Object[0][0], false, false);
		this.probNode = probNode; 
		prefixedData = newPrefixedData.clone();
		titleToSelectRows = newTitleToSelectRows;
		initialize();
		valuesTable.setFirstColumnHidden(firstColumnHidden);
		setData(newData);
	}

	/**
	 * Sets a new table model with new data.
	 * 
	 * @param newData
	 *            new data for the table.
	 */
	@Override
	public void setData(Object[][] newData) {

		data = newData.clone();
		tableModel = null;
		valuesTable.setModel(getTableModel());
		absentData = absentPrefixedData();
		setEnabledAddValue(absentData.length != 0);

	}
	private static Object[][] fillArrayWithNodes(List<Node> nodes) {

		int i, l;
		Object[][] result;
		l = nodes.size();
		result = new Object[l][2];
		for (i = 0; i < l; i++) {
			result[i][0] = "p_"+i; //internal name for the parent
			result[i][1] = ((ProbNode)nodes.get(i).getObject()).getName();
		}

		return result;
	}

	/**
	 * Invoked when the button 'add' is pressed.
	 */
	@Override
	protected void actionPerformedAddValue() {

		int newIndex = 0;
		int i = 0;
		int l = 0;
		Object[][] newData = null;

		newIndex = valuesTable.getRowCount();
		if (absentData == null){
			JOptionPane.showMessageDialog(
					Utilities.getOwner(this), "Ningún nodo disponible",
					stringDatabase.getString("ErrorWindow.Title.Label"),
					JOptionPane.INFORMATION_MESSAGE);
		}else{
			newData = requestNewData();
			if (newData != null) {
				l = newData.length;
				for (i = 0; i < l; i++) {
					String name =  (String) newData[i][1];
					for (PNEdit edit:edits){
						if (((AddLinkEdit)edit).getProbNode1().getName().equals(
								name)){
							try {
								probNode.getProbNet().getPNESupport().doEdit(
										(AddLinkEdit)edit);
								tableModel.insertRow(newIndex + i, newData[i]);
								edits.remove(edit);
								break;
							} catch (DoEditException
									| NonProjectablePotentialException
									| WrongCriterionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								JOptionPane.showMessageDialog(
										Utilities.getOwner(this), e.getMessage(),
										stringDatabase
										.getString("ErrorWindow.Title.Label"),
										JOptionPane.ERROR_MESSAGE);
							} 
						}
						
					}
				}
				valuesTable.getSelectionModel().setSelectionInterval(
						newIndex, newIndex);
				absentData = absentPrefixedData();
				setEnabledAddValue(absentData.length != 0);
			}
		}

	}

	/**
	 * This method request the user to select one or more new elements to add.
	 * The new elements are the subset of the prefixed set that aren't in the
	 * array 'data'.
	 * 
	 * @return the elements that the user has selected or null if he/she has
	 *         selected nothing.
	 */
	private Object[][] requestNewData() {

		Object[][] possibleData = absentData;
		KeyListSelectionDialog dialog = null;
		dialog =
			new KeyListSelectionDialog(Utilities.getOwner(this), titleToSelectRows,
				possibleData, columns);

		return (dialog.requestSelectRows() == KeyListSelectionDialog.OK_BUTTON)
			? dialog.getSelectedRows() : null;

	}

	/**
	 * This method returns an array of arrays of strings whose elements are the
	 * prefixed ones that aren't in the array 'data'.
	 * 
	 * @return the prefixed data that aren't in the array 'data'.
	 */
	private Object[][] absentPrefixedData() {
	    List<ProbNode> probNodes = probNode.getProbNet().getProbNodes();
	    List<Node> nodes = new ArrayList<Node>();
		edits.clear();
		
		for (ProbNode pProbNode:probNodes){
			if (!probNode.getNode().getParents().contains(pProbNode.getNode()) && 
					pProbNode != probNode){
				
				//LinkEdit linkEdit = new LinkEdit(probNode.getProbNet(),pProbNode.getName(), probNode.getName(), true, true);
				AddLinkEdit linkEdit = new AddLinkEdit(probNode.getProbNet(),
						pProbNode.getVariable(), probNode.getVariable(), true);
				
				try {
					probNode.getProbNet().getPNESupport().announceEdit(linkEdit);
					edits.add(linkEdit);
					nodes.add(pProbNode.getNode());
				} catch(ConstraintViolationException ignore){
				} catch (CanNotDoEditException
						| NonProjectablePotentialException
						| WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, stringDatabase
							.getString( e.getMessage() ),
							stringDatabase.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} 
				
			}
			
		}
		return fillArrayWithNodes(nodes);
	}

	/**
	 * Invoked when the button 'remove' is pressed.
	 */
	@Override
	protected void actionPerformedRemoveValue() {

		int selectedRow = valuesTable.getSelectedRow();
		int rowCount = 0;
		
		String name = (String) valuesTable.getValueAt(selectedRow, 1);
		
		/*LinkEdit linkEdit;
		linkEdit = new LinkEdit(probNode.getProbNet(), name,
				probNode.getName(), true, 
				false);*/
		ProbNet probNet = probNode.getProbNet();
		RemoveLinkEdit linkEdit;
		try {
			linkEdit = new RemoveLinkEdit(probNet, probNet.getVariable(name),
					probNode.getVariable(), true);
			probNode.getProbNet().doEdit(linkEdit);
				
			tableModel.removeRow(selectedRow);
			rowCount = valuesTable.getRowCount();
			if ((rowCount > 0) && (selectedRow >= rowCount)) {
				valuesTable.getSelectionModel().setSelectionInterval(
					selectedRow - 1, selectedRow - 1);
			}
			absentData = absentPrefixedData();
			setEnabledAddValue(true);
			
		} catch (DoEditException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(
			Utilities.getOwner(this), e.getMessage(),
			stringDatabase.getString("ErrorWindow.Title.Label"),
			JOptionPane.ERROR_MESSAGE);
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
		} catch (CanNotDoEditException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
			Utilities.getOwner(this), e.getMessage(),
			stringDatabase.getString("ErrorWindow.Title.Label"),
			JOptionPane.ERROR_MESSAGE);
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase
					.getString( e.getMessage() ),
					stringDatabase.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase
					.getString( e.getMessage() ),
					stringDatabase.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (ProbNodeNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase
					.getString( e1.getMessage() ),
					stringDatabase.getString( e1.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}
		
		

	}

	// ESCA-JAVA0025:
	/**
	 * Invoked when the button 'up' is pressed.
	 */
	@Override
	protected void actionPerformedUpValue() {

	}

	// ESCA-JAVA0025:
	/**
	 * Invoked when the button 'down' is pressed.
	 */
	@Override
	protected void actionPerformedDownValue() {

	}
}
