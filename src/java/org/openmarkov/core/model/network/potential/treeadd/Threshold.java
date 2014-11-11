package org.openmarkov.core.model.network.potential.treeadd;

/**
 * A threshold is defined by a float value and a boolean that indicates if the
 * value delimits a closed or an open interval on the left and on the right
 * side. There are two possibilities )[, ](
 * 
 * It is used by TreeADDBranch when its topVariable is a numeric variable
 * 
 * @author myebra
 * 
 */
public class Threshold {
	
	private double limit;
	private boolean belongsToLeft; // if true --> ](; if false --> )[; 
	
	
	public Threshold(double limit, boolean belongsToLeft){
		this.limit = limit;
		this.belongsToLeft = belongsToLeft;
		
	}
	
	public double getLimit() {
		return this.limit;
	}
	
	public boolean belongsToLeft() {
		return belongsToLeft;
	}
	public void setBelongsToLeft (boolean belongsToLeft) {
		this.belongsToLeft = belongsToLeft;
	}
		
	/**@return true if the value is above the limit value of the threshold object
	 * @param value to check
	 * **/
	public boolean isBelow(double value){
	    return value > this.limit || (value == this.limit && !belongsToLeft);
	}
	
	/**@return true if the value is below the limit value of the threshold object
	 * @param value to check
	 * **/
	public boolean isAbove(double value){
	    return value < this.limit || (value == this.limit && belongsToLeft);
	}
	
	public boolean equals (Threshold threshold) {
	    return limit == threshold.getLimit() && belongsToLeft == threshold.belongsToLeft();
	}
	
	public Threshold copy() {
		return new Threshold(this.limit, this.belongsToLeft);
	}

}
