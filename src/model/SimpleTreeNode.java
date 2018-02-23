package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.TreeNode;

import org.apache.commons.lang.StringUtils;


/***
 * Provides minimum structure to satisfy requirements of a (recursible) TreeNode.
 */
public class SimpleTreeNode implements TreeNode {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String   label      = null;
	private TreeNode parent     = null;
	private List     children   = null; // Only used if userObject == null, else userObject is interrogated for children 
	private Object   userObject = null;
	private String   category   = null;
	private String   linkTarget = null;
	private Object   info       = null; // Value to easily identify which Tree (from any node)
	private byte     depth      = -1;   // Depth w/in the Tree (root == 0)
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public SimpleTreeNode() {
		super();
	}
	public SimpleTreeNode(String label) {
		this();
		this.setLabel(label);
	}
	public SimpleTreeNode(String label, TreeNode parent) {
		this(label);
		this.setParent(parent);
	}
	public SimpleTreeNode(String label, TreeNode parent, Object userObject) {
		this(label, parent);
		this.setUserObject(userObject);
	}
	public SimpleTreeNode(String label, TreeNode parent, Object userObject, String category) {
		this(label, parent, userObject);
		this.setCategory(category);
	}
	public SimpleTreeNode(String label, TreeNode parent, Object userObject, String category, String linkTarget) {
		this(label, parent, userObject, category);
		this.setLinkTarget(linkTarget);
	}
	public SimpleTreeNode(String label, TreeNode parent, Object userObject, String category, String linkTarget, Object info) {
		this(label, parent, userObject, category, linkTarget);
		this.setInfo(info);
	}
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
//	public List getChildren() {
//		return children;
//	}
//	public void setChildren(List children) {
//		this.children = children;
//	}
	public Object getUserObject() {
		return userObject;
	}
	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLinkTarget() {
		return linkTarget;
	}
	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}
	public Object getInfo() {
		return info;
	}
	public void setInfo(Object info) {
		this.info = info;
	}
	public byte getDepth() {
		return depth;
	}
	public void setDepth(byte depth) {
		this.depth = depth;
	}

	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Obtain the child at the specified position from this <code>SimpleTreeNode</code>s list of children.
	 * 
	 * @param index Integer indicating the position w/in the list of the target child.
	 * @return TreeNode containing the corresponding child.
	 */
	public TreeNode getChildAt(int index) {
		TreeNode result = null;
		if        (userObject instanceof List) {
			List work = (List)userObject;
			result = (TreeNode)work.get(index);
		} else if (userObject instanceof Map) {
			Map work = (Map)userObject;
			result = (TreeNode)new ArrayList(work.values()).get(index);
		} else if (userObject instanceof Set) {
			Set work = (Set)userObject;
			result = (TreeNode)new ArrayList(work).get(index);
		} else if (userObject instanceof Object[]) {
			Object[] work = (Object[])userObject;
			result = (TreeNode)work[index];
		} else {
			if (children != null) {
				result = (TreeNode)children.get(index);
			}
		}
		return result;
	}

	/**
	 * Obtain a count of the children w/in this <code>SimpleTreeNode</code>.
	 * 
	 * @return Integer containing the child count.
	 */
	public int getChildCount() {
		int result = 0;
		if        (userObject instanceof List) {
			List work = (List)userObject;
			result = work.size();
		} else if (userObject instanceof Map) {
			Map work = (Map)userObject;
			result = work.size();
		} else if (userObject instanceof Set) {
			Set work = (Set)userObject;
			result = work.size();
		} else if (userObject instanceof Object[]) {
			Object[] work = (Object[])userObject;
			result = work.length;
		} else {
			if (children != null) {
				result = children.size();
			}
		}
		return result;
	}

	/**
	 * Obtain the parent of this <code>SimpleTreeNode</code>.
	 * 
	 * @return TreeNode containing a reference to the parent.
	 */
	public TreeNode getParent() {
		return parent;
	}
	
	/**
	 * Establish the parent of this <code>SimpleTreeNode</code>.
	 * <p>
	 *  NOTE: In addition to setting the 'parent', this method also replicates
	 *        the {@link #info} from parent to child, and then also calculates/sets
	 *        the {@link NodeInfo#setDepth(byte)} w/in the {@link #info}.
	 * </p>
	 * 
	 * @param parent TreeNode containing a reference to the parent.
	 */
	public void setParent(TreeNode parent) {
		this.parent = parent;
		this.setInfo(parent == null?null:((SimpleTreeNode)parent).getInfo());
		this.setDepth((byte)(parent == null?0 :((SimpleTreeNode)parent).getDepth() + 1));
	}

	/**
	 * Obtain the position of the specified child w/in this <code>SimpleTreeNode</code>s list of children.
	 * 
	 * @param child TreeNode containing a reference to the child.
	 * @return Integer indicating the position of the child w/in the list of children (or -1 if not found).
	 */
	public int getIndex(TreeNode child) {
		int result = -1;
		if        (userObject instanceof List) {
			List work = (List)userObject;
			result = work.indexOf(child);
		} else if (userObject instanceof Map) {
			Map work = (Map)userObject;
			result = new ArrayList(work.values()).indexOf(child);
		} else if (userObject instanceof Set) {
			Set work = (Set)userObject;
			result = new ArrayList(work).indexOf(child);
		} else if (userObject instanceof Object[]) {
			Object[] work = (Object[])userObject;
			List temp = Arrays.asList(work);
			result = temp.indexOf(child);
		} else {
			if (children != null) {
				result = children.indexOf(child);
			}
		}
		return result;
	}

	/**
	 * Obtain whether this <code>SimpleTreeNode</code> can contain children.
	 * <p>
	 *  NOTE: Current any/all <code>SimpleTreeNode</code> objects can have
	 *        children, therefore, this method always returns <code>true</code>.
	 * </p>
	 * 
	 * @return Boolean indicating whether this <code>SimpleTreeNode</code> may contain children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 * Obtain whether this <code>SimpleTreeNode</code> is a leaf node (with no children)
	 * as opposed to a branch node (which has 1 or more children).
	 * 
	 * @return Boolean indicating whether this <code>SimpleTreeBode</code> is a leaf.
	 */
	public boolean isLeaf() {
		boolean result = (this.getChildCount() == 0);
		return result;
	}

	/**
	 * Obtain an {@link java.util.Enumeration} of this <code>SimpleTreeNode</code>s
	 * children.
	 */
	public Enumeration children() {
		Enumeration result = null;
		if        (userObject instanceof List) {
			List work = (List)userObject;
			result = new IteratorEnumeration(work.iterator());
		} else if (userObject instanceof Map) {
			Map work = (Map)userObject;
			result = new IteratorEnumeration(work.values().iterator());
		} else if (userObject instanceof Set) {
			Set work = (Set)userObject;
			result = new IteratorEnumeration(work.iterator());
		} else if (userObject instanceof Object[]) {
			Object[] work = (Object[])userObject;
			List temp = Arrays.asList(work);
			result = new IteratorEnumeration(temp.iterator());
		} else {
			if (children != null) {
				result = new IteratorEnumeration(children.iterator());
			}
		}
		return result;
	}

	/**
	 * Append the specified child to this <code>SimpleTreeNode</code>s list of children.
	 * 
	 * @param child TreeNode containing a reference to the child.
	 * @see #insert(TreeNode, int)
	 */
	public void add(TreeNode child) {
		this.insert(child, this.getChildCount());
	}
	
	/**
	 * Append the specified child to this <code>SimpleTreeNode</code>s list of children
	 * only if the 'emptyCheck' value is not empty.
	 * 
	 * @param child      TreeNode containing a reference to the child.
	 * @param emptyCheck Object to perform an empty check against, and if empty the child is not added to the list.
	 * @see #insert(TreeNode, int, Object)
	 */
	public void add(TreeNode child, Object emptyCheck) {
		this.insert(child, this.getChildCount(), emptyCheck);
	}
	
	/**
	 * Insert the specified child into this <code>SimpleTreeNode</code>s list of children
	 * at the specified position.
	 * 
	 * @param child TreeNode containing a reference to the child.
	 * @param index Integer indicating the desired position to insert the child.
	 */
	public void insert(TreeNode child, int index) {
		((SimpleTreeNode)child).setParent(this);
		if        (userObject instanceof List) {
			List work = (List)userObject;
			work.add(index, child);
	/*
	 * NOTE: The 'insert' operation is not supported with Maps due to:
	 *       (1) lack of a specified key value
	 *       (2) lack of support for an index
	 */
	//	} else if (userObject instanceof Map) {
	//		Map work = (Map)userObject;
	//		work.values().add(child);
		} else if (userObject instanceof Set) {
			Set work = (Set)userObject;
			List temp = new ArrayList(work);
			temp.add(index, child);
			setUserObject(new HashSet(temp));
		} else if (userObject instanceof Object[]) {
			Object[] work = (Object[])userObject;
			List temp = Arrays.asList(work);
			temp.add(index, child);
			setUserObject(temp.toArray());
		} else {
			if (children == null) {
				children = new ArrayList();
			}
			children.add(index, child);
		}
	}
	
	/**
	 * Insert the specified child into this <code>SimpleTreeNode</code>s list of children
	 * at the specified position.
	 * 
	 * @param child      TreeNode containing a reference to the child.
	 * @param index      Integer indicating the desired position to insert the child.
	 * @param emptyCheck Object to perform an empty check against, and if empty the child is not added to the list.
	 * @see #insert(TreeNode, int)
	 * @see #isEmpty(Object)
	 */
	public void insert(TreeNode child, int index, Object emptyCheck) {
		if (!this.isEmpty(emptyCheck)) {
			this.insert(child, index);
		}
	}
	
	/**
	 * Remove the child at the specified position from this <code>SimpleTreeNode</code>s list of children.
	 * 
	 * @param index Integer indicating the position w/in the list of the target child.
	 */
	public void remove(int index) {
		if        (userObject instanceof List) {
			List work = (List)userObject;
			work.remove(index);
		} else if (userObject instanceof Map) {
			Map work = (Map)userObject;
			Object child = getChildAt(index);
			work.values().remove(child);
		} else if (userObject instanceof Set) {
			Set work = (Set)userObject;
			List temp = new ArrayList(work);
			temp.remove(index);
			setUserObject(new HashSet(temp));
		} else if (userObject instanceof Object[]) {
			Object[] work = (Object[])userObject;
			List temp = Arrays.asList(work);
			temp.remove(index);
			setUserObject(temp.toArray());
		} else {
			if (children != null) {
				children.remove(index);
			}
		}
	}
	
	/**
	 * Overrides the standard {@link Object#toString()} method to provide an
	 * appropriate value based on the specific contents of this class.
	 * 
	 * @return String containing an appropriate value specific to this class.
	 */
	public String toString() {
		String result = null;
		if (label != null) {
		//	NodeInfo nodeInfo = (NodeInfo)info;
		//	result = "[pi="+nodeInfo.getPanelIndex()+":gbi="+nodeInfo.getGroupByIndex()+":dep="+nodeInfo.getDepth()+":cat="+nodeInfo.getCategory()+"]["+depth+"] "+""+label;
			if (label.indexOf("${") >= 0 && label.indexOf("}") >= 0) {
				int size = getChildCount();
				String[][] searchReplaceTokens = {
					{ "[$][{]length[}]", ""+size }
				,	{ "[$][{]size[}]"  , ""+size }
				};
				for (int x = 0; x < searchReplaceTokens.length; x++) {
					label = label.replaceAll(searchReplaceTokens[x][0], searchReplaceTokens[x][1]);
				}
			}
			result = ""+label;
		} else {
			result = this.getClass().getName()+"@"+this.hashCode();
		}
		return result;
	}
	
	/**
	 * Overrides the standard {@link Object#hashCode()} method to provide an
	 * appropriate value based on the specific contents of this class.
	 * 
	 * @return Integer containing an appropriate value specific to this class.
	 */
	public int hashCode() {
		int result = Integer.MIN_VALUE;
		String hashKey = this.getLabel() /*+ ":" + this.getCategory() + ":" + this.getParent() + ":" + this.getUserObject()*/;
		result += hashKey.hashCode();
		return result;
	}
	
	/**
	 * Overrides the standard {@link Object#equals(Object)} method to provide an
	 * appropriate value based on the specific contents of this class.
	 * 
	 * @param other Object to compare against.
	 * @return Boolean containing an appropriate value specific to this class.
	 */
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof SimpleTreeNode) {
			SimpleTreeNode that = (SimpleTreeNode)other;
			result = this.hashCode() == that.hashCode();
		}
		return result;
	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Determine whether the specified object is 'empty'.
	 * <ul>
	 *  NOTE: The 'empty' status is object type-specific...
	 *  <li>Lists, Maps and Sets are 'empty' if their 'size' is zero.</li>
	 *  <li>Arrays               are 'empty' if their length is zero.</li>
	 *  <li>Strings              are 'empty' if they are either <code>null</code> or zero-length.</li>
	 *  <li>Objects (in general) are 'empty' if they are not <code>null</code></li>
	 * </ul>
	 * 
	 * @param emptyCheck Object to perform an empty check against.
	 * @return Boolean indicating whether the specified object is 'empty.
	 */
	private boolean isEmpty(Object emptyCheck) {
		boolean result = true;
		if (emptyCheck != null) {
			if      (emptyCheck instanceof List    ) result = ((List    )emptyCheck).size() == 0;
			else if (emptyCheck instanceof Map     ) result = ((Map     )emptyCheck).size() == 0;
			else if (emptyCheck instanceof Set     ) result = ((Set     )emptyCheck).size() == 0;
			else if (emptyCheck instanceof Object[]) result = ((Object[])emptyCheck).length == 0;
			else if (emptyCheck instanceof String  ) result = StringUtils.isEmpty(emptyCheck+"");
			else if (emptyCheck instanceof Object  ) result = false;
		}
		return result;
	}
	
}
