package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import model.Filename;
import model.SimpleTreeNode;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import view.Viewer;


/**
 * Utilities for tree manipulation.
 * 
 * TODO[jpt] Cleanup this class (some of its methods can be refactored / combined / removed).
 */
public class TreeUtils {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");
	
	/**
	 * Map containing information about each supported 'nodeCategory'.
	 * <ul>
	 *  NOTE: The information for each 'nodeCategory' is an Object array containing...
	 *  <li>[0] == Icon data</li>
	 *  <li>[1] == Description</li>
	 * </ul>
	 * 
	 * @see #getNodeCategory(Object)
	 */
	private static Map nodeCategorys;
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	/**
	 * Obtain the 'nodeCategorys' map.
	 * <p>
	 *  NOTE: If the 'nodeCategorys' map is empty, then it is created.
	 * </p>
	 * 
	 * @return Map containing the 'nodeCategory' information.
	 */
	public static Map getNodeCategorys() {
		if (nodeCategorys == null) {
			nodeCategorys = new HashMap();
			nodeCategorys.put("attr"            , new Object[]{ createImageIcon("attribute-icon.jpg"       ), "Attribute" });
			nodeCategorys.put("td"              , new Object[]{ createImageIcon("apache-tiles-logo.jpg"    ), "Apache Tiles Definition" });
			nodeCategorys.put("td.alt"          , new Object[]{ createImageIcon("generic-tiles-icon.jpg"   ), "Tiles Definition" });
			nodeCategorys.put("webapp"          , new Object[]{ createImageIcon("webapp_obj.gif"           ), "Web application" });
			nodeCategorys.put("link"            , new Object[]{ createImageIcon("link_obj.gif"             ), "Link in a JSP file or an HTML file" });
			nodeCategorys.put("sam"             , new Object[]{ createImageIcon("actionmapping_obj.gif"    ), "Action Mapping defined in a Struts configuration file" });
			nodeCategorys.put("safb"            , new Object[]{ createImageIcon("formbean_obj.gif"         ), "Form-bean in a Struts configuration file" });
			nodeCategorys.put("classGS"         , new Object[]{ createImageIcon("dataformfield_getset.gif" ), "Form-bean field or a Dynaform field that has 'get' and 'set' methods" });
			nodeCategorys.put("classG"          , new Object[]{ createImageIcon("dataformfield_get.gif"    ), "Form-bean field that has a 'get' method only" });
			nodeCategorys.put("classS"          , new Object[]{ createImageIcon("dataformfield_set.gif"    ), "Form-bean field that has a 'set' method only" });
			nodeCategorys.put("sami"            , new Object[]{ createImageIcon("actionmapping_input.gif"  ), "Indicator that the 'input' attribute of the Action Mapping is set" });
			nodeCategorys.put("saf"             , new Object[]{ createImageIcon("forward_obj.gif"          ), "Forward in a Struts configuration file" });
			nodeCategorys.put("exception"       , new Object[]{ createImageIcon("exception_obj.gif"        ), "Indicator that the part in question has an exception" });
			nodeCategorys.put("gexh"            , new Object[]{ createImageIcon("globalexception_obj.gif"  ), "Exception handler defined in a Struts configuration file" });
			nodeCategorys.put("samsaf"          , new Object[]{ createImageIcon("actionmapping_forward.gif"), "Indicator that the Forward attribute of the Action Mapping is set" });
			nodeCategorys.put("module"          , new Object[]{ createImageIcon("module_obj.gif"           ), "(Struts 1.1) Module node" });
			nodeCategorys.put("modulet"         , new Object[]{ createImageIcon("module_transition.gif"    ), "(Struts 1.1) Module transition node" });

			nodeCategorys.put("debug"           , new Object[]{ createImageIcon("debug_obj.gif"            ), "Indicator that the part in question is for debugging" });
			nodeCategorys.put("info"            , new Object[]{ createImageIcon("info_obj.gif"             ), "Indicator that the part in question is informational" });
			nodeCategorys.put("warn"            , new Object[]{ createImageIcon("warning_obj.gif"          ), "Indicator that the part in question has a warning" });
			nodeCategorys.put("error"           , new Object[]{ createImageIcon("error_obj.gif"            ), "Indicator that the part in question has an error" });
			
			nodeCategorys.put("annotation"      , new Object[]{ createImageIcon("annotation_obj.jpg"       ), "Annotation" });
			nodeCategorys.put("class"           , new Object[]{ createImageIcon("class_obj.jpg"            ), "Class file" });
			nodeCategorys.put("enum"            , new Object[]{ createImageIcon("enum_obj.jpg"             ), "Enumeration" });
			nodeCategorys.put("interface"       , new Object[]{ createImageIcon("interface_obj.jpg"        ), "Interface file" });
			
			nodeCategorys.put("class.file"      , new Object[]{ createImageIcon("classf_obj.jpg"           ), "Class file" });
			nodeCategorys.put("exe"             , new Object[]{ createImageIcon("executable_obj.jpg"       ), "Executable file" });
			nodeCategorys.put("file"            , new Object[]{ createImageIcon("file_obj.gif"             ), "File" });
			nodeCategorys.put("folder"          , new Object[]{ createImageIcon("folder_obj.gif"           ), "Folder" });
			nodeCategorys.put("html"            , new Object[]{ createImageIcon("html_obj.jpg"             ), "HTML file" });
			nodeCategorys.put("jar"             , new Object[]{ createImageIcon("jar_obj.jpg"              ), "Java Archive file" });
			nodeCategorys.put("java"            , new Object[]{ createImageIcon("javaf_obj.jpg"            ), "Java file" });
			nodeCategorys.put("java.alt"        , new Object[]{ createImageIcon("java_obj.gif"             ), "Java file" });
			nodeCategorys.put("jsp"             , new Object[]{ createImageIcon("jsp_obj.gif"              ), "JSP file" });
			nodeCategorys.put("log"             , new Object[]{ createImageIcon("log_obj.gif"              ), "Log file" });
			nodeCategorys.put("out"             , new Object[]{ createImageIcon("console_obj.gif"          ), "Console output" });
			nodeCategorys.put("xml"             , new Object[]{ createImageIcon("xml_obj.jpg"              ), "XML file" });
			nodeCategorys.put("zip"             , new Object[]{ createImageIcon("zip_obj.gif"              ), "Zip Archive file" });
			
			nodeCategorys.put("field.default"   , new Object[]{ createImageIcon("field_default_obj.jpg"    ), "Default field (package visible)" });
			nodeCategorys.put("field.private"   , new Object[]{ createImageIcon("field_private_obj.jpg"    ), "Private field" });
			nodeCategorys.put("field.protected" , new Object[]{ createImageIcon("field_protected_obj.jpg"  ), "Protected field" });
			nodeCategorys.put("field.public"    , new Object[]{ createImageIcon("field_public_obj.jpg"     ), "Public field" });
			nodeCategorys.put("method.default"  , new Object[]{ createImageIcon("method_default_obj.jpg"   ), "Default method (package visible)" });
			nodeCategorys.put("method.private"  , new Object[]{ createImageIcon("method_private_obj.jpg"   ), "Private method" });
			nodeCategorys.put("method.protected", new Object[]{ createImageIcon("method_protected_obj.jpg" ), "Protected method" });
			nodeCategorys.put("method.public"   , new Object[]{ createImageIcon("method_public_obj.jpg"    ), "Public method" });
		}
		return nodeCategorys;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Convenience method used to obtain a String value from an XML node.
	 * 
	 * @param node          Node containing the data.
	 * @param attributeName String identifying the name of the value to obtain.
	 * @return String containing the corresponding XML node value.
	 * @see #getNodeAttributeStringValue(Node, String, String)
	 */
	public static String getNodeAttributeStringValue(Node node, String attributeName) {
		return getNodeAttributeStringValue(node, attributeName, "");
	}
	
	/**
	 * Obtain a String value from an XML node.
	 * 
	 * @param node          Node containing the data.
	 * @param attributeName String identifying the name of the value to obtain.
	 * @param defaultValue  String containing a default value to use if the value is empty.
	 * @return String containing the corresponding XML node value.
	 * @see #getNodeAttributeStringValue(Node, String)
	 */
	public static String getNodeAttributeStringValue(Node node, String attributeName, String defaultValue) {
		String temp = null;
		if (node != null && node.getAttributes() != null) {
			Node work = node.getAttributes().getNamedItem(attributeName);
			if (work != null) {
				temp = work.getNodeValue();
			}
		}
		if (temp == null && defaultValue != null) {
			temp = defaultValue;
		}
		return (temp == null?null:temp.trim());
	}
	
	/**
	 * Convenience method used to obtain a Boolean value from an XML node.
	 * 
	 * @param node          Node containing the data.
	 * @param attributeName String identifying the name of the value to obtain.
	 * @return Boolean containing the corresponding XML node value.
	 * @see #getNodeAttributeBooleanValue(Node, String, Boolean)
	 */
	public static Boolean getNodeAttributeBooleanValue(Node node, String attributeName) {
		return getNodeAttributeBooleanValue(node, attributeName, null);
	}
	
	/**
	 * Obtain a Boolean value from an XML node.
	 * 
	 * @param node          Node containing the data.
	 * @param attributeName String identifying the name of the value to obtain.
	 * @param defaultValue  Boolean containing a default value to use if the value is empty.
	 * @return Boolean containing the corresponding XML node value.
	 * @see #getNodeAttributeBooleanValue(Node, String)
	 */
	public static Boolean getNodeAttributeBooleanValue(Node node, String attributeName, Boolean defaultValue) {
		String temp = null;
		if (node != null && node.getAttributes() != null) {
			Node work = node.getAttributes().getNamedItem(attributeName);
			if (work != null) {
				temp = work.getNodeValue();
			}
		}
		if (temp == null && defaultValue != null) {
			temp = defaultValue.booleanValue() + "";
		}
		return (temp == null?null:new Boolean(temp));
	}
	
//	public static void addBranch(Object emptyCheck, SimpleTreeNode parent, SimpleTreeNode child) {
//		if (emptyCheck instanceof String) {
//			if (StringUtils.isNotEmpty(""+emptyCheck)) {
//				parent.add(child);
//			}
//		} else if (emptyCheck != null) {
//			parent.add(child);
//		}
//	}
    
//	public static SimpleTreeNode recreateBranch(SimpleTreeNode parent, String key) {
//		removeChild(parent, key);
//		SimpleTreeNode newChild = new SimpleTreeNode(new NodeInfo(key, null, null));
//		if (parent != null) parent.add(newChild);
//		return newChild;
//	}

//	public static void removeChild(SimpleTreeNode parent, String key) {
//		int childIndex = getChildIndex(parent, key);
//		if (childIndex >= 0) {
//			parent.remove(childIndex);
//		}
//	}

//	public static void replaceChild(SimpleTreeNode parent, String key, SimpleTreeNode newChild) {
//		int childIndex = getChildIndex(parent, key);
//		if (childIndex >= 0) {
//			parent.remove(childIndex);
//			parent.insert(newChild, childIndex);
//		} else {
//			parent.add(newChild);
//		}
//	}

//	public static int getChildIndex(SimpleTreeNode parent, String key) {
//		int result = -1;
//		if (parent != null && parent.children() != null && key != null) {
//			int x = 0;
//			for (Enumeration children = parent.children(); children.hasMoreElements();) {
//				SimpleTreeNode child = (SimpleTreeNode)children.nextElement();
//				if (key.equals(((SimpleTreeNode)child.getUserObject()).getLabel())) {
//					result = x;
//					break;
//				}
//				x++;
//			}
//		}
//		return result;
//	}

    /**
     * Obtain a TreePath for the specified node.
     * 
     * @param node TreeNode for which to find the path.
     * @return TreePath for the specified node.
     */
    public static TreePath getTreePath(TreeNode node) {
        List list = new ArrayList();

        // Add all nodes to list
        while (node != null) {
            list.add(node);
            node = node.getParent();
        }
        Collections.reverse(list);

        // Convert array of nodes to TreePath
        return new TreePath(list.toArray());
    }
    
    /**
     * Determine the 'nodeCategory' for the specified 'nodeObject'.
     * 
     * @param nodeObject Object containing a reference to the 'nodeObject' to evaluate.
     * @return String identifying the 'category' for the specified 'nodeObject'.
     */
	public static String getNodeCategory(Object nodeObject) {
		String result = null;
		SimpleTreeNode treeNode = getNodeInfo(nodeObject);
		if (treeNode != null) {
			result = treeNode.getCategory();
		}
		return result;
	}

	/**
	 * Obtain a StringBuffer representation of the 'base' node and all its children.
	 * 
	 * @param base SimpleTreeNode identifying the starting node from where to begin recursing.
	 * @param seed StringBuffer containing a starting value to use for the resulting value.
	 * @return StringBuffer containing the result of the recursion.
	 * @see #traverseTreeNodes(SimpleTreeNode, StringBuffer, byte)
	 */
	public static StringBuffer traverseTreeNodes(SimpleTreeNode base, StringBuffer seed) {
		return TreeUtils.traverseTreeNodes(base, seed, base.getDepth());
	}
	
	/**
	 * Determine the 'category' of the specified 'fileName' (either "folder" or "file").
	 * 
	 * @param fileName String containing the name of the file to evaluate.
	 * @see #getFileObjectCategory(File)
	 */
	public static String getFileObjectCategory(String fileName) {
		return getFileObjectCategory(new File(fileName));
	}

	/**
	 * Determine the 'category' of the specified 'file' (either "folder" or "file").
	 * 
	 * @param file File identifying the file to evaluate.
	 * @see #getFileObjectCategory(String)
	 */
	public static String getFileObjectCategory(File file) {
		String category = null;
		if (file != null) {
			if (file.isDirectory()) {
				category = "folder";
			} else if (file.isFile()) {
				category = "file";
				Filename fileName = new Filename(file.getAbsolutePath(), '/', '.');
				Object[] info = (Object[])getNodeCategorys().get(fileName.extension());
				if (info != null) category = fileName.extension();
			}
		}
		return category;
	}
	
	/**
	 * Determine whether 'lookForMe' already exists somewhere up the tree branch
	 * as an ancestor.
	 * <ul>
	 *  NOTE: A 'lookForMe' can be either...
	 *  <li>SimpleTreeNode              (SimpleTreeNode.getLabel()                 is compared)</li>
	 *  <li>Class                       (SimpleTreeNode.getUserObject().getClass() is compared)</li>
	 *  <li>SimpleTreeNode.userObject() (SimpleTreeNode.getUserObject()            is compared)</li>
	 * </ul>
	 * 
	 * @param lookForMe Object to look for as an ancestor.
	 * @param parent    SimpleTreeNode that is the parent of the specified nodeObject. 
	 * @return Boolean indicating whether 'lookForMe' was found as an ancestor.
	 */
	public static boolean existsAsAncestor(Object lookForMe, SimpleTreeNode parent) {
		boolean result = false;
		if (lookForMe != null) {
			SimpleTreeNode work = (SimpleTreeNode)(parent == null?null:parent.getParent());
			while (work != null && result == false) {
				if        (lookForMe instanceof SimpleTreeNode) {
					result = (((SimpleTreeNode)lookForMe).getLabel().equals(work.getLabel()));
				} else if (lookForMe instanceof Class && work.getUserObject() != null) {
					result = lookForMe.equals(work.getUserObject().getClass());
				} else if (lookForMe instanceof Object) {
					result = lookForMe.equals(work.getUserObject());
				}
				work = (SimpleTreeNode)work.getParent();
			}
		}
		return result;
	}
	
//	public static int getDepth(TreeNode node) {
//		int depth = 0;
//		TreeNode work = node;
//		while (work.getParent() != null) {
//			depth++;
//			work = work.getParent();
//		}
//		return depth;
//	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Obtain icon data for the specified 'imageName'.
	 * 
	 * @param imageName String identifying the name of the image to load.
	 * @return ImageIcon containing the icon data for the specified image.
	 */
	private static ImageIcon createImageIcon(String imageName) {
		java.net.URL imageURL = Viewer.class.getResource("resources/images/"+imageName);
		if (imageURL != null) {
			return new ImageIcon(imageURL);
		} else {
			logger.error("Couldn't find file: " + imageName);
			return null;
		}
	}
	
	/**
	 * Obtain 'nodeInfo' for the specified 'nodeObject'
	 * 
	 * @param nodeObject Object referencing the 'nodeObject' to evaluate.
	 * @return SimpleTreeNode containing the 'nodeInfo' for the specified 'nodeObject'.
	 */
	private static SimpleTreeNode getNodeInfo(Object nodeObject) {
		SimpleTreeNode result = null;
		if (nodeObject instanceof SimpleTreeNode) {
			result = (SimpleTreeNode)nodeObject;
//		} else if (nodeObject instanceof Object[]) {
//			result = null;
//		} else if (nodeObject instanceof List ||
//					nodeObject instanceof Map ||
//					nodeObject instanceof Set) {
//			if (nodeObject != null) {
//				List work = null;
//				if (       nodeObject instanceof List) {
//					work = (List)nodeObject;
//				} else if (nodeObject instanceof Map) {
//					work = new ArrayList(((Map)nodeObject).values());
//				} else if (nodeObject instanceof Set) {
//					work = new ArrayList((Set)nodeObject);
//				}
//				if (work != null && work.size() > 0) {
//					Object firstNodeObject = work.iterator().next();
//					result = TreeUtils.getNodeInfo(firstNodeObject);
//				}
//			}
//		} else if (nodeObject instanceof StrutsActionFormBean) {
//			result = ((StrutsActionFormBean)nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof StrutsActionForward ) {
//			result = ((StrutsActionForward )nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof StrutsActionMapping ) {
//			result = ((StrutsActionMapping )nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof TilesAttribute      ) {
//			result = ((TilesAttribute      )nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof TilesDefinition     ) {
//			result = ((TilesDefinition     )nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof ViewerConfig        ) {
//			result = ((ViewerConfig        )nodeObject).getNodeInfo();
//		} else {
//			logger.debug("nodeObject.class == " + nodeObject.getClass());
		}
		return result;
	}

	/**
	 * Convenience method used to obtain a StringBuffer representation of the 'base' node and all its children.
	 * 
	 * @param base SimpleTreeNode identifying the starting node from where to begin recursing.
	 * @param seed StringBuffer containing a starting value to use for the resulting value.
	 * @param offset Byte value (used internally) to determine the initial depth of the 'base' node.
	 * @return StringBuffer containing the result of the recursion.
	 * @see #traverseTreeNodes(SimpleTreeNode, StringBuffer)
	 */
	private static StringBuffer traverseTreeNodes(SimpleTreeNode base, StringBuffer seed, byte offset) {
		if (base != null) {
			if (seed == null) {
				seed = new StringBuffer();
			} else {
				seed.append("\n");
			}
			if (seed.length() > 0) {
				seed.append(StringUtils.repeat("\t", base.getDepth() - offset) + "+ ");
			}
			seed.append(base.getLabel());
			if (base.getChildCount() > 0) {
				for (Enumeration children = base.children(); children.hasMoreElements();) {
					SimpleTreeNode child = (SimpleTreeNode)children.nextElement();
					seed = traverseTreeNodes(child, seed, offset);
				}
			}
		}
		return seed;
	}
	
}
