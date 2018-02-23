package view;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import model.SimpleTreeNode;
import model.StrutsActionFormBean;
import model.StrutsActionForward;
import model.StrutsActionMapping;
import model.TilesDefinition;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.TreeUtils;


/**
 * Extends <code>DefaultTreeCellRenderer</code>
 * in order to provide custom behavior.
 */
public class TreeCellRenderer extends DefaultTreeCellRenderer {
	
	/***************
	 * Constant(s) *
	 ***************/
	
	/**
	 * Serializable.
	 */
	private static final long serialVersionUID = 1L;
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public TreeCellRenderer() {}

	/********************
	 * Member method(s) *
	 ********************/

	/**
	 * {@inheritDoc}
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		Object userObject = value;
		if (userObject instanceof SimpleTreeNode && ((SimpleTreeNode)userObject).getUserObject() != null) {
			userObject = ((SimpleTreeNode)userObject).getUserObject();
		}
		String nodeType = null;
		if (userObject instanceof StrutsActionFormBean) nodeType = "safb";
		if (userObject instanceof StrutsActionForward ) nodeType = "saf";
		if (userObject instanceof StrutsActionMapping ) nodeType = "sam";
		if (userObject instanceof TilesDefinition     ) nodeType = "td";
//		if (userObject instanceof TilesAttribute      ) nodeType = "ta";
		if (StringUtils.isEmpty(nodeType)) nodeType = TreeUtils.getNodeCategory(value);
		Object[] iconInfo = (Object[])TreeUtils.getNodeCategorys().get(nodeType);
		if (iconInfo == null) {
			if (leaf) {
				iconInfo = (Object[])TreeUtils.getNodeCategorys().get("attr");
			} else {
		//		iconInfo = (Object[])TreeUtils.getNodeCategorys().get("folder");
			}
		}
		if (iconInfo != null) {
			setIcon(       (ImageIcon)iconInfo[0]);
			setToolTipText((String   )iconInfo[1]);
		} else {
		//	setIcon(       null); //no icon
			setToolTipText(null); //no tool tip
		}
        return this;
	}

}
