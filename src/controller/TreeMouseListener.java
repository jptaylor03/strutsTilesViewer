package controller;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import model.SimpleTreeNode;
import model.ViewerConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.MiscUtils;
import view.Viewer;


/**
 * Respond to mouse clicks on nodes w/in the Tree component(s)
 * on each tab of the Viewer.
 */
public class TreeMouseListener extends MouseAdapter {
	
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
	
	public TreeMouseListener() {}
	public TreeMouseListener(Viewer viewer) {
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
	 * Respond to a 'mousePressed' event.
	 * <ul>
	 *  NOTE: Upon each 'mousePressed' event, the number of clicks (and Shift key) are evaluated...
	 *  <li>        [Single-click] == Nothing happens.</li>
	 *  <li>        [Double-click] == Attempt to open the corresponding file/folder ONLY if the node is a leaf.</li>
	 *  <li>[Shift]+[Double-click] == Attempt to open the corresponding file/folder.</li>
	 * </ul>
	 * 
	 * @param event MouseEvent that has occurred on the component.
	 * @see Runtime#getRuntime()
	 */
	public void mousePressed(MouseEvent event) {
		JTree source = (JTree)event.getSource();
		int selectedRow = source.getRowForLocation(event.getX(), event.getY());
		if (selectedRow != -1) {
			TreePath selectedPath = source.getPathForLocation(event.getX(), event.getY());
			if (selectedPath.getLastPathComponent() instanceof SimpleTreeNode) {
				SimpleTreeNode treeNode = (SimpleTreeNode)selectedPath.getLastPathComponent();
				if (treeNode.isLeaf() || MouseEvent.getMouseModifiersText(event.getModifiers()).indexOf("Shift") >= 0) {
					switch (event.getClickCount()) {
						case 1: { // single-click
							break;
						}
						default: { // double-click
							if (StringUtils.isNotEmpty(treeNode.getLinkTarget())) {
								Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								try {
									ViewerConfig vc = Viewer.getViewerConfig();
									String linkTarget = treeNode.getLinkTarget();
									// Create temp file (for resources)
									if (!new File(linkTarget).exists()) {
										linkTarget = MiscUtils.resourceToTempFile(Viewer.class, linkTarget);
									}
									linkTarget = linkTarget.replaceAll("/", "\\\\");
									String command = null;
									if ("folder".equals(treeNode.getCategory())) {
										command = "explorer /root," + " " + "\"" + linkTarget + "\"";
									} else {
										command = "\"" + vc.getTextEditorExecutable() + "\"" + " " + "\""+ linkTarget + "\"";
									}
									logger.info("Attempting to open: " + linkTarget);
									Runtime.getRuntime().exec(command);
								//	Process p = Runtime.getRuntime().exec(command);
								//	p.waitFor(); // NOTE: If text editor not already open, waitFor() hangs indefinitely
								//	logger.debug("Exit value: " + p.exitValue());
								//	// Remove temp file (for resources)
								//	if (!linkTarget.equals(treeNode.getLinkTarget())) {
								//		new File(linkTarget).delete();
								//	}
								} catch (Throwable t) {
									logger.error(t);
								}
								Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}
							break;
						}
					}
				}
			}
		}
	}
	
}
