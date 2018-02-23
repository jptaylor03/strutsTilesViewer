package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTree;

import model.SimpleTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.TreeUtils;
import view.Viewer;


/**
 * Respond to keys being pressed/released/type in the Tree component(s)
 * on each tab of the Viewer.
 */
public class TreeKeyListener implements KeyListener {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("controller");
	
	/**
	 * Viewer instance.
	 */
	private Viewer viewer;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public TreeKeyListener() {}
	public TreeKeyListener(Viewer viewer) {
		this.viewer = viewer;
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public Viewer getViewer() {
		return this.viewer;
	}
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Respond to a 'keyPressed' event.
	 * <p>
	 *  NOTE: Currently nothing is done on 'keyPressed', instead,
	 *        a response is only generated on 'keyReleased'. 
	 * </p>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see #keyReleased(KeyEvent)
	 */
	public void keyPressed(KeyEvent event) {}
	
	/**
	 * Respond to a 'keyReleased' event.
	 * <ul>
	 *  NOTE: Upon each 'keyReleased' event, the pressed keys are evaluated...
	 *  <li>        [Control]+[C] == contents of selected node are copied to the clipboard.</li>
	 *  <li>[Shift]+[Control]+[C] == contents of selected node and all children are copied to the clipboard.</li>
	 * </ul>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see TreeUtils#traverseTreeNodes(SimpleTreeNode, StringBuffer)
	 */
	public void keyReleased(KeyEvent event) {
		if (KeyEvent.getKeyModifiersText(event.getModifiers()).indexOf("Ctrl") >= 0
				&& KeyEvent.getKeyText(event.getKeyCode()).equals("C")) {
			JTree source = (JTree)event.getSource();
			if (source.getLastSelectedPathComponent() instanceof SimpleTreeNode) {
				SimpleTreeNode selectedNode = (SimpleTreeNode)source.getLastSelectedPathComponent();
				String forClipboard = null;
				if (KeyEvent.getKeyModifiersText(event.getModifiers()).indexOf("Shift") >= 0
						&& selectedNode.getChildCount() > 0) {
					forClipboard = TreeUtils.traverseTreeNodes(selectedNode, null).toString();
				} else {
					forClipboard = selectedNode.getLabel();
				}
				viewer.setClipboardContents(forClipboard);
			}
		}
	}
	
	/**
	 * Respond to a 'keyTyped' event.
	 * <p>
	 *  NOTE: Currently nothing is done on 'keyTyped', instead,
	 *        a response is only generated on 'keyReleased'. 
	 * </p>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see #keyReleased(KeyEvent)
	 */
	public void keyTyped(KeyEvent event) {}
	
}
