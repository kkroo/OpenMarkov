package org.openmarkov.core.action;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNode;
/**
 * 
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class TimeSliceEdit extends SimplePNEdit{
	/**
	 * The last time slice before the edition
	 */
	private int lastTimeSlice;
	/**
	 * The new time slice after the edition
	 */
	private int newTimeSlice;
	/**
	 * The edited node
	 */
	private ProbNode probNode = null;
	/**
	 * the last base name of the temporal variable
	 */
	private String lastBaseName;
	/**
	 * The last variable name
	 */
	private String lastName; 
	
/**
 * 
 * @param probNode
 * @param timeSlice
 */
	public TimeSliceEdit(ProbNode probNode, int timeSlice) {
		super(probNode.getProbNet());
		this.lastTimeSlice = probNode.getVariable().getTimeSlice();
		this.newTimeSlice = timeSlice;
		this.lastBaseName =  probNode.getVariable().getBaseName();
		this.lastName =  probNode.getVariable().getName();
		this.probNode = probNode;
		
	}

	@Override
	public void doEdit() throws DoEditException {
		//onlyTemporal && not only atemporal
		probNode.getVariable().setTimeSlice(newTimeSlice);
		if (newTimeSlice == Integer.MIN_VALUE && lastTimeSlice != Integer.MIN_VALUE && lastBaseName != null) {
			probNode.getVariable().setBaseName(null);
			int beginSlicePart = lastName.lastIndexOf('[') - 1;
			String newName = null;
			if (beginSlicePart > 0) {
				newName = lastName.substring(0, beginSlicePart);
			}
			probNode.getVariable().setName(newName);
		}
		//not only temporaL && not only atemporal but also set name and base name
		if (lastTimeSlice == Integer.MIN_VALUE) {
			probNode.getVariable().setBaseName(lastBaseName);	
			probNode.getVariable().setName(lastName+ " " + "["+ String.valueOf(newTimeSlice)+"]");
		}
	}
	@Override
	public void undo() {
		super.undo();
		//onlyTemporal
		probNode.getVariable().setTimeSlice(lastTimeSlice);
		//not only temporaL && not only atemporal but also set name and base name
		if (lastTimeSlice == Integer.MIN_VALUE) {
			probNode.getVariable().setBaseName(lastBaseName);
			probNode.getVariable().setName(lastName);
		}
	}

}
