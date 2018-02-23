package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Container for a Tiles Definition.
 */
public class TilesDefinition extends AbstractVO {
	
	/***************
	 * Constant(s) *
	 ***************/
	
	// TODO[jpt] Tiles variable names ('page.work' and 'page.work.title') s/b configurable parameters
	public static final String ATTR_PAGE_WORK       = "page.work";
	public static final String ATTR_PAGE_WORK_TITLE = "page.work.title";

	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String name;
	private String extendsDefinition;
	private Map attributes;
	private List actionForwards;
	private String  pageWorkTarget;
	private boolean pageWorkTargetMissing;
	private String controllerClass;
	private String controllerUrl;
	private String page;
	private String path;
	private String role;
	private String template;

	/******************
	 * Constructor(s) *
	 ******************/
	
	public TilesDefinition() {}
	public TilesDefinition(String name, String extendsDefinition, Map attributes, List actionForwards) {
		this.name = name;
		this.extendsDefinition = extendsDefinition;
		this.attributes = attributes;
		this.actionForwards = actionForwards;
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExtends() {
		return extendsDefinition;
	}
	public void setExtends(String extendsDefinition) {
		this.extendsDefinition = extendsDefinition;
	}
	public Map getAttributes() {
		return attributes;
	}
	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}
	public List getActionForwards() {
		return actionForwards;
	}
	public void setActionForwards(List actionForwards) {
		this.actionForwards = actionForwards;
	}
	public String getPageWorkTarget() {
		return pageWorkTarget;
	}
	public void setPageWorkTarget(String pageWorkTarget) {
		this.pageWorkTarget = pageWorkTarget;
	}
	public boolean isPageWorkTargetMissing() {
		return pageWorkTargetMissing;
	}
	public void setPageWorkTargetMissing(boolean pageWorkTargetMissing) {
		this.pageWorkTargetMissing = pageWorkTargetMissing;
	}
	public String getExtendsDefinition() {
		return extendsDefinition;
	}
	public void setExtendsDefinition(String extendsDefinition) {
		this.extendsDefinition = extendsDefinition;
	}
	public String getControllerClass() {
		return controllerClass;
	}
	public void setControllerClass(String controllerClass) {
		this.controllerClass = controllerClass;
	}
	public String getControllerUrl() {
		return controllerUrl;
	}
	public void setControllerUrl(String controllerUrl) {
		this.controllerUrl = controllerUrl;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Overrides the standard {@link Object#toString()} method to provide an
	 * appropriate value based on the specific contents of this class.
	 * 
	 * @return String containing an appropriate value specific to this class.
	 */
	public String toString() {
		return this.name;
	}
	
	/**
	 * Convenience method used to get a specific attribute.
	 * 
	 * @param key String identifying the key to get.
	 * @return SimpleTreeNode containing the value corresponding to the specified key.
	 */
	public SimpleTreeNode getAttribute(String key) {
		SimpleTreeNode result = null;
		if (this.getAttributes() != null) {
			result = (SimpleTreeNode)this.getAttributes().get(key);
		}
		return result;
	}
	
	/**
	 * Convenience method used to set a specific attribute.
	 * 
	 * @param key   String identifying the key to set.
	 * @param value SimpleTreeNode identifying the value to set.
	 */
	public void setAttribute(String key, SimpleTreeNode value) {
		if (this.getAttributes() == null) {
			this.setAttributes(new HashMap());
		}
		this.getAttributes().put(key, value);
	}
	
	/**
	 * Convenience method used to remove a specific attribute.
	 * 
	 * @param key String identifying the key to remove.
	 * @return SimpleTreeNode containing the value corresponding to the specified key.
	 */
	public SimpleTreeNode removeAttribute(String key) {
		SimpleTreeNode result = null;
		if (this.getAttributes() != null) {
			result = (SimpleTreeNode)this.getAttributes().remove(key);
		}
		return result;
	}
	
	/**
	 * Convenience method used to get a specific attribute's "name".
	 * 
	 * @param key String identifying the key to get.
	 * @return String containing the value corresponding to the specified key's "name".
	 */
	public String getAttributeName(String key) {
		String result = null;
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) result = work.getName();
		return result;
	}
	
	/**
	 * Convenience method used to set a specific attribute's "name".
	 * 
	 * @param key   String identifying the key to set.
	 * @param value String identifying the value to set the attribute's "name".
	 */
	public void setAttributeName(String key, String value) {
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) work.setName(value);
	}
	
	/**
	 * Convenience method used to get a specific attribute's "value".
	 * 
	 * @param key String identifying the key to get.
	 * @return String containing the value corresponding to the specified key's "value".
	 */
	public String getAttributeValue(String key) {
		String result = null;
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) result = work.getValue();
		return result;
	}
	
	/**
	 * Convenience method used to set a specific attribute's "value".
	 * 
	 * @param key   String identifying the key to set.
	 * @param value String identifying the value to set the attribute's "value".
	 */
	public void setAttributeValue(String key, String value) {
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) work.setValue(value);
	}
	
	/**
	 * Convenience method used to get a specific attribute's "content".
	 * 
	 * @param key String identifying the key to get.
	 * @return String containing the value corresponding to the specified key's "content".
	 */
	public String getAttributeContent(String key) {
		String result = null;
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) result = work.getContent();
		return result;
	}
	
	/**
	 * Convenience method used to set a specific attribute's "content".
	 * 
	 * @param key   String identifying the key to set.
	 * @param value String identifying the value to set the attribute's "content".
	 */
	public void setAttributeContent(String key, String value) {
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) work.setContent(value);
	}
	
	/**
	 * Convenience method used to get a specific attribute's "direct".
	 * 
	 * @param key String identifying the key to get.
	 * @return Boolean containing the value corresponding to the specified key's "direct".
	 */
	public Boolean getAttributeDirect(String key) {
		Boolean result = null;
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) result = work.getDirect();
		return result;
	}
	
	/**
	 * Convenience method used to set a specific attribute's "direct".
	 * 
	 * @param key   String identifying the key to set.
	 * @param value Boolean identifying the value to set the attribute's "direct".
	 */
	public void setAttributeDirect(String key, Boolean value) {
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) work.setDirect(value);
	}
	
	/**
	 * Convenience method used to get a specific attribute's "type".
	 * 
	 * @param key String identifying the key to get.
	 * @return String containing the value corresponding to the specified key's "type".
	 */
	public String getAttributeType(String key) {
		String result = null;
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) result = work.getType();
		return result;
	}
	
	/**
	 * Convenience method used to set a specific attribute's "type".
	 * 
	 * @param key   String identifying the key to set.
	 * @param value String identifying the value to set the attribute's "type".
	 */
	public void setAttributeType(String key, String value) {
		TilesAttribute work = this.getSimpleTreeNodeAttribute(key);
		if (work != null) work.setType(value);
	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Convenience method used to get a TilesAttribute from the SimpleTreeNode with the specified key.
	 * 
	 * @param key String identifying the key to get.
	 * @return TilesAttribute from the SimpleTreeNode with the specified key.
	 */
	private TilesAttribute getSimpleTreeNodeAttribute(String key) {
		TilesAttribute result = null;
		SimpleTreeNode treeNode = (SimpleTreeNode)this.getAttribute(key);
		if (treeNode != null) {
			result = (TilesAttribute)treeNode.getUserObject();
		}
		return result;
	}
	
}
