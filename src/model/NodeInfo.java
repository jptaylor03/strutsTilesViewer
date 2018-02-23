package model;

/**
 * Container for storing additional information about each node w/in a tree.
 * <p>
 *  NOTE: Some information is static throughout the entire tree (like panelIndex,
 *        groupByIndex, and category) and is simply replicated down from parent
 *        to child so that it can be easily found from any node.  While other
 *        information is node-specific (like depth), and is calculated/set
 *        dynamically at the time the child is being added under the parent.
 * </p>
 */
public class NodeInfo {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private byte   panelIndex   = -1;
	private byte   groupByIndex = -1;
	private byte   depth        = -1;
	private String category     = null;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public NodeInfo() {}
	public NodeInfo(int panelIndex, int groupByIndex, int depth) {
		this.panelIndex   = (byte)panelIndex;
		this.groupByIndex = (byte)groupByIndex;
		this.depth        = (byte)depth;
	}
	public NodeInfo(int panelIndex, int groupByIndex, int depth, String category) {
		this(panelIndex, groupByIndex, depth);
		this.category = category;
	}
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public byte getPanelIndex() {
		return panelIndex;
	}
	public void setPanelIndex(byte panelIndex) {
		this.panelIndex = panelIndex;
	}
	public byte getGroupByIndex() {
		return groupByIndex;
	}
	public void setGroupByIndex(byte groupByIndex) {
		this.groupByIndex = groupByIndex;
	}
	public byte getDepth() {
		return depth;
	}
	public void setDepth(byte depth) {
		this.depth = depth;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

}
