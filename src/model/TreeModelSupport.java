package model;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;


/**
 * This class takes care of the event listener lists required by TreeModel.
 * It also adds "fire" methods that call the methods in TreeModelListener.
 * 
 * @see AbstractTreeModel
 */
public class TreeModelSupport {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Container for a list of TreeModelListeners.
	 */
	private Vector listeners = new Vector();

	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Add the specified <code>TreeModelListener</code> to the list of listeners.
	 * 
	 * @param listener TreeModelListener to be added.
	 */
	public void addTreeModelListener(TreeModelListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.addElement(listener);
		}
	}

	/**
	 * Remove the specified <code>TreeModelListener</code> from the list of listeners.
	 * 
	 * @param listener TreeModelListener to be removed.
	 */
	public void removeTreeModelListener(TreeModelListener listener) {
		if (listener != null) {
			listeners.removeElement(listener);
		}
	}

	/**
	 * Fire the specified 'treeNodesChanged' <code>TreeModelEvent</code>
	 * to each listener in the list of listeners.
	 * 
	 * @param event TreeModelEvent to be fired.
	 */
	public void fireTreeNodesChanged(TreeModelEvent event) {
		Enumeration work = listeners.elements();
		while (work.hasMoreElements()) {
			TreeModelListener listener = (TreeModelListener)work.nextElement();
			listener.treeNodesChanged(event);
		}
	}

	/**
	 * Fire the specified 'treeNodesInserted' <code>TreeModelEvent</code>
	 * to each listener in the list of listeners.
	 * 
	 * @param event TreeModelEvent to be fired.
	 */
	public void fireTreeNodesInserted(TreeModelEvent event) {
		Enumeration work = listeners.elements();
		while (work.hasMoreElements()) {
			TreeModelListener listener = (TreeModelListener)work.nextElement();
			listener.treeNodesInserted(event);
		}
	}

	/**
	 * Fire the specified 'treeNodesRemoved' <code>TreeModelEvent</code>
	 * to each listener in the list of listeners.
	 * 
	 * @param event TreeModelEvent to be fired.
	 */
	public void fireTreeNodesRemoved(TreeModelEvent event) {
		Enumeration work = listeners.elements();
		while (work.hasMoreElements()) {
			TreeModelListener listener = (TreeModelListener)work.nextElement();
			listener.treeNodesRemoved(event);
		}
	}

	/**
	 * Fire the specified 'treeStructureChanged' <code>TreeModelEvent</code>
	 * to each listener in the list of listeners.
	 * 
	 * @param event TreeModelEvent to be fired.
	 */
	public void fireTreeStructureChanged(TreeModelEvent event) {
		Enumeration work = listeners.elements();
		while (work.hasMoreElements()) {
			TreeModelListener listener = (TreeModelListener)work.nextElement();
			listener.treeStructureChanged(event);
		}
	}
	
}
