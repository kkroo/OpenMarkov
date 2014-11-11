/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network;

/**
 * Defines a set of intervals
 * 
 * @author fjdiez
 * @author manuel
 * @since OpenMarkov 1.0
 * @version 1.0 fjdiez, manuel
 * @version 1.1 jlgozalo add auxiliar conversion functions
 * @version 1.2 jlgozalo add method equals()
 * @invariant belongsToLeftSide.length = limits.length
 */
public class PartitionedInterval implements Cloneable {

	// Attributes
	/** This numbers delimits the set of subintervals */
	protected double[] limits;
	
    protected boolean[] belongsToLeftSide;
	
	/** @frozen */
    protected int numSubintervals;
	
    // Constructors
	/**
	 * @argCondition limits.size() == belongsToLeftSide.size()
	 * @argCondition limits[i] <= limits[i+1]
	 * @argCondition if limits[i] == limits[i+1] then belongsToLeftSide[i] =
	 *               false and belongsToLeftSide[i+1] = true
	 */
	public PartitionedInterval(double[] limits, boolean[] belongsToLeftSide) {
		this.limits = limits.clone();
		this.belongsToLeftSide = belongsToLeftSide.clone();
		numSubintervals = limits.length - 1;
	}
	
	/** Creates a <code>PartitionedInterval</code> with only one Subinterval */
	public PartitionedInterval(boolean leftClosed, double min,
                    double max, boolean rightClosed) {
		limits = new double[]{min,max};
		belongsToLeftSide = new boolean[]{leftClosed,rightClosed};
		numSubintervals = 1;
	}

	/** Creates a <code>PartitionedInterval</code> from an Object[][] table */
	public PartitionedInterval(Object [][] values) {
		int numSubIntervals = 0;
		double limits[] = null;
		boolean belongsToLeftSide[] = null;
		int i = 0;
		numSubIntervals = values.length;
		limits = new double[numSubIntervals + 1];
		belongsToLeftSide = new boolean[numSubIntervals + 1];
		if (numSubIntervals > 1) {
			try {// id-name-symbol-value-separator-value-symbol
				for (i = 0; i < numSubIntervals ; i++) {
					belongsToLeftSide[i] = (values[i][2] == "[" ? true : false);
					limits[i] = ((Double) values[i][3]).doubleValue();
				}
				limits[i] = ((Double) values[i-1][5]).doubleValue(); 
				belongsToLeftSide[i] = (values[i-1][6] == "]" ? true : false);
				
			} catch (NumberFormatException ex) {
				// TODO set the actions to capture this exception if happens
			}
			this.limits= limits.clone();
			this.belongsToLeftSide = belongsToLeftSide.clone();
			numSubintervals = limits.length - 1;
		} else {
			boolean leftClosed = (values[0][2] == "[" ? false : true);
			boolean rightClosed = (values[0][6] == "]" ? true : false);
			double min = ((Double) values[0][3]).doubleValue();
			double max = ((Double) values[0][5]).doubleValue();
			this.limits = new double[]{min,max};
			this.belongsToLeftSide = new boolean[]{leftClosed,rightClosed};
			this.numSubintervals = 1;

		}
	}
	
    // Methods
    /**
	 * @return true if the value is included between the outside limits
	 * @consultation
	 */
    public boolean contains(double number) {
        return (((limits[0] < number) && (number < limits[limits.length-1])) || 
        	((number == limits[0]) && !belongsToLeftSide[0]) ||
			((number == limits[limits.length-1]) && 
                    belongsToLeftSide[limits.length-1]));
    }

    /**
	 * @param number.
	 *            <code>double</code>
	 * @return The number of subinterval where is located the number (0, 1, ...)
	 *         or -1 if it is outside
	 * @consultation
	 */
	public int indexOfSubinterval(double number) {
		for (int i = 0; i < limits.length - 1; i++) {
			if (((limits[i] < number) && (number < limits[i+1])) || 
	        		((number == limits[i]) && !belongsToLeftSide[i]) ||
					((number == limits[i+1]) && belongsToLeftSide[i+1])) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * This method remove the index-th subinterval. 
	 * @param index.
	 *            <code>int</code>
	 * @consultation
	 */
	public void removeSubinterval(int index) {
		double[] newLimits = new double [limits.length - 1];
		boolean[] newBelongsToLeftSide = new boolean [limits.length - 1];
		for (int i = 0; i < newLimits.length ; i++) {
			if (i <= index){
				newLimits [i] = limits [i];
				newBelongsToLeftSide [i] = belongsToLeftSide [i];
			}else{
				newLimits [i] = limits [i+1];
				newBelongsToLeftSide [i] = belongsToLeftSide [i+1];
			}
		}
		
		limits = newLimits.clone();
		belongsToLeftSide = newBelongsToLeftSide.clone();
		
	}

	/**
	 * @consultation
	 * @return numSubintervals. <code>int</code>
	 */
	public int getNumSubintervals() {
		return numSubintervals = limits.length -1;
	}

	/**
	 * @return limits. <code>double[]</code>
	 * @consultation
	 */
	public double[] getLimits() {
		return limits;
	}
	/**
	 * @return limit. <code>double</code>
	 * @consultation
	 */
	public double getLimit(int index) {
		return limits[index];
	}
	/**
	 * @return belongsToLeftSide. <code>boolean[]</code>
	 * @consultation
	 */
	public boolean[] getBelongsToLeftSide() {
		return belongsToLeftSide;
	}
	/**
	 * 
	 * @param index <code>int</code>
	 * @return  belongsToLeftSide. <code>boolean</code>
	 */
	public boolean getBelongsToLeftSide(int index) {
		return belongsToLeftSide [index];
	}
	/**
	 * 
	 * @param index <code>int</code>
	 * @return  belongsTo. <code>String</code>
	 */
	public String getBelongsTo(int index) {
		if (belongsToLeftSide [index]== true)
			return "left"; 
		else
			return "right";
	}
    /**
	 * @return min. <code>
     * @consultation
	 */
    public double getMin() {
        return limits[0];
    }    

    /**
	 * @return max
	 * @consultation
	 */
    public double getMax() {
        return limits[getNumSubintervals()];
    }

    /**
	 * @return leftClosed
	 * @consultation
	 */
    public boolean isLeftClosed() {
        return !belongsToLeftSide[0];
    }

    /**
	 * @return rightClosed
	 * @consultation
	 */
    public boolean isRightClosed(){
        return belongsToLeftSide[getNumSubintervals()];
    }

    /**
	 * @argCondition newLimit < limit[indexOfLimit+1] && newLimit >
	 *               limit[indexOfLimit-1]
	 * @argCondition if limit[indexOfLimit-1] = newLimit then
	 *               belongsToLeftSide[indexOfLimit] = true &&
	 *               belongsToLeftSide[indexOfLimit-1] = false
	 * @argCondition if limit[indexOfLimit+1] = newLimit then
	 *               belongsToLeftSide[indexOfLimit] = false &&
	 *               belongsToLeftSide[indexOfLimit+1] = true
	 * @param indexOfLimit
	 * @param newLimit
	 * @param newBelongsToLeftSide
	 */
	public void changeLimit(int indexOfLimit, double newLimit, 
            boolean newBelongsToLeftSide) {
		limits[indexOfLimit] = newLimit;
		belongsToLeftSide[indexOfLimit] = newBelongsToLeftSide;
	}

	/**
	 * Convert a PartitionedInterval in an array of arrays of objects with the
	 * same elements.
	 * 
	 * @return an array of arrays of objects that has the same elements.
	 */
	public Object[][] convertToTableFormat() {

		Object[][] data;
		int i = 0;
		int numIntervals = 0;
		int numColumns = 6; // name-symbol-value-separator-value-symbol
		double[] limits;
		boolean[] belongsToLeftSide;

		numIntervals = getNumSubintervals();
		limits = getLimits();
		belongsToLeftSide = getBelongsToLeftSide();
		data = new Object[numIntervals][numColumns];
		for (i = 0; i < numIntervals; i++) {
			//for (i = numIntervals-1; i <=0; i--) {
			data[i][0] = ""; // name
			data[i][1] = (belongsToLeftSide[i] ? "(" : "["); // low interval
																// symbol
			data[i][2] = limits[i]; // low interval value
			data[i][3] = ","; // separator ","
			data[i][4] = limits[i + 1]; // high interval value
			data[i][5] = (belongsToLeftSide[i + 1] ? "]" : ")"); // high
																	// interval
																	// symbol
		}
		return data;

	}

	/**
	 * print a readable format of the Partitioned Interval
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Partitioned Interval ");
		buffer.append("\n");
		buffer.append("  > numSubIntervals = " + getNumSubintervals());
		buffer.append("\n");
		for (int i=0; i< getNumSubintervals(); i++){
			buffer.append("   > interval[" + i + "]=");
			buffer.append(!belongsToLeftSide[i]?"[":"(");
			buffer.append(limits[i]);
			buffer.append(",");
			buffer.append(limits[i+1]);
			buffer.append(!belongsToLeftSide[i+1]?")":"]");
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one. The graph
	 * which the node belongs to is not compared.
	 * 
	 * @param obj
	 *            object to compare with this one. It must be a NodeProperties
	 *            instance.
	 */
	@Override
	public boolean equals(Object obj) {

		PartitionedInterval otherInterval;

		boolean result = true;
		if (obj instanceof PartitionedInterval) {
			otherInterval = (PartitionedInterval) obj;
			if ((numSubintervals==otherInterval.numSubintervals) 
				&& (this.belongsToLeftSide.length 
								== otherInterval.belongsToLeftSide.length )
				&& (this.limits.length == otherInterval.limits.length)) {
				for (int i=0; result & (i<this.belongsToLeftSide.length);i++) {
					result = (this.belongsToLeftSide[i] 
					          == otherInterval.belongsToLeftSide[i]);
				}
				for (int i=0; result & (i<this.limits.length);i++) {
						result = (this.limits[i] 
						          == otherInterval.limits[i]);
				}
			} else {
				result = false;
			}
		} else {
			result = false;
		}
		return result;
	}
	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
