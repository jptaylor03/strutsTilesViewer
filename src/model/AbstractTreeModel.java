package model;

import java.util.List;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/***
 * Base class for minimum behavior to satisfy requirements of a (recursible) TreeModel.
 * 
 * @see TreeModel
 * @see TreeModelSupport
 */
public abstract class AbstractTreeModel extends TreeModelSupport implements TreeModel {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	Object root = null;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public AbstractTreeModel() {}
	public AbstractTreeModel(Object root) {
		this.root = root;
	}
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public Object getRoot() {
		return root;
	}
	public void setRoot(Object root) {
		this.root = root;
	}

	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Obtain the child at the specified position from the specified parents list of children.
	 * 
	 * @param parent Object containing a reference to the parent.
	 * @param index  Integer indicating the position w/in the list of the target child.
	 * @return Object containing the corresponding child.
	 */
	public Object getChild(Object parent, int index) {
		Object result = null;
		List work = convertObjectToList(parent);
		if (work != null) {
			result = work.get(index);
		}
		return result;
	}

	/**
	 * Obtain a count of the children w/in the specified parent.
	 * 
	 * @return Integer containing the child count.
	 */
	public int getChildCount(Object parent) {
		int result = 0;
		List work = convertObjectToList(parent);
		if (work != null) {
			result = work.size();
		}
		return result;
	}

	/**
	 * Obtain whether the specified node is a leaf node (with no children)
	 * as opposed to a branch node (which has 1 or more children).
	 * 
	 * @return Boolean indicating whether the specified node is a leaf.
	 */
	public boolean isLeaf(Object node) {
		return this.getChildCount(node) == 0;
	}

	/**
	 * Messaged when the user has altered the value for the item identified by <code>path</code> to <code>newValue</code>.
	 * <p>
	 *  NOTE: If <code>newValue</code> signifies a truly new value the model should post a <code>treeNodesChanged</code> event.
	 * </p>
	 * 
	 * @param path     TreePath to the node that the user has altered.
	 * @param newValue Object containing the new value from the <code>TreeCellEditor</code>.
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		// Currently nothing is needed here
	}

	/**
	 * Obtain the position of the specified child w/in the specified parents list of children.
	 * 
	 * @param parent Object containing a reference to the parent.
	 * @param child  Object containing a reference to the child.
	 * @return Integer indicating the position of the child w/in the list of children (or -1 if not found).
	 */
	public int getIndexOfChild(Object parent, Object child) {
		int result = -1;
		List work = convertObjectToList(parent);
		if (work != null) {
			result = work.indexOf(child);
		}
		return result;
	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Convenience method used to convert the specified 'nodeObject'
	 * into a <code>List</code> of children.
	 * 
	 * @param nodeObject Object to be converted.
	 * @return List containing the children for the specified 'nodeObject'.
	 */
	abstract List convertObjectToList(Object nodeObject);
	
}
