/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.core.util;

public class ModelNetUse {
	
	private boolean useModelNet;
	private boolean useNodePositions;
	private boolean startFromModelNet;
	private boolean allowLinkAddition;
	private boolean allowLinkRemoval;
	private boolean allowLinkInversion;
	
	public ModelNetUse(boolean useModelNet, boolean useNodePositions,
						boolean startFromModelNet, boolean allowLinkAddition, 
						boolean allowLinkRemoval, boolean allowLinkInversion)
	{
		this.useModelNet = useModelNet;
		this.useNodePositions = useNodePositions;
		this.startFromModelNet = startFromModelNet;
		this.allowLinkAddition = allowLinkAddition;
		this.allowLinkRemoval = allowLinkRemoval;
		this.allowLinkInversion = allowLinkInversion;
		if ( !useNodePositions && !startFromModelNet )
		{
			this.useModelNet = false;
		}
	}
	
    public ModelNetUse()
   {
       this(false, false, false, false, false, false);
   }	

	/**
	 * @return the useModelNet
	 */
	public boolean isUseModelNet() {
		return useModelNet;
	}

	/**
	 * @param useModelNet the useModelNet to set
	 */
	public void setUseModelNet(boolean useModelNet) {
		this.useModelNet = useModelNet;
	}

	/**
	 * @return the useNodesModelNet
	 */
	public boolean isUseNodePositions() {
		return useNodePositions;
	}
	
	public boolean isStartFromModelNet() {
		return startFromModelNet;
	}

	/**
	 * @param useOnlyNodes the useNodesModelNet to set
	 */
	public void setOnlyUseNodePositions(boolean useOnlyNodePositions) {
		this.useNodePositions = useOnlyNodePositions;
	}

	/**
	 * @return the addLinkModelNet
	 */
	public boolean isLinkAdditionAllowed() {
		return allowLinkAddition;
	}

	/**
	 * @param addLinkModelNet the addLinkModelNet to set
	 */
	public void setLinkAdditionAllowed(boolean allowLinkAddition) {
		this.allowLinkAddition = allowLinkAddition;
	}

	/**
	 * @return the deleteLinksModelNet
	 */
	public boolean isLinkRemovalAllowed() {
		return allowLinkRemoval;
	}

	/**
	 * @param deleteLinksModelNet the deleteLinksModelNet to set
	 */
	public void setLinkRemovalAllowed(boolean allowLinkRemoval) {
		this.allowLinkRemoval = allowLinkRemoval;
	}

	/**
	 * @return the allowLinkInversion
	 */
	public boolean isLinkInversionAllowed() {
		return allowLinkInversion;
	}

	/**
	 * @param allowLinkInversion the allowLinkInversion to set
	 */
	public void setLinkInversionAllowed(boolean allowLinkInversion) {
		this.allowLinkInversion = allowLinkInversion;
	}
	
}
