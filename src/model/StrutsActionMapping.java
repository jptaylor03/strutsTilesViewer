package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * Container for a Struts Action Mapping.
 */
public class StrutsActionMapping extends AbstractVO {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String name;
	private String type;
	private String typeTarget;
	private boolean typeTargetMissing;
	private String path;
	private String scope;
	private String parameter;
	private Boolean validate;
	private Map actionForwards;
	private Class superClass;
	private Class[] interfaces;
	private Class[] declaredClasses;
	private Constructor[] declaredConstructors;
	private Field[] declaredFields;
	private Method[] declaredMethods;
	private String attribute;
	private String className;
	private String forward;
	private String id;
	private String include;
	private String input;
	private String prefix;
	private String roles;
	private String suffix;
	private Boolean unknown;
	private StrutsActionFormBean formBean;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public StrutsActionMapping() {
		super();
	}
	public StrutsActionMapping(String name, String type, String path, String scope, String parameter, Boolean validate, Map actionForwards) {
		this();
		this.setName(name);
		this.setType(type);
		this.setPath(path);
		this.setScope(scope);
		this.setParameter(parameter);
		this.setValidate(validate);
		this.setActionForwards(actionForwards);
	}
	public StrutsActionMapping(String name, String type, String path, String scope, String parameter, Boolean validate, Map actionForwards,
			Class superClass, Class[] interfaces,
			Class[] declaredClasses, Constructor[] declaredConstructors,
			Field[] declaredFields, Method[] declaredMethods) {
		this(name, type, path, scope, parameter, validate, actionForwards);
		this.setSuperClass(superClass);
		this.setInterfaces(interfaces);
		this.setDeclaredClasses(declaredClasses);
		this.setDeclaredConstructors(declaredConstructors);
		this.setDeclaredFields(declaredFields);
		this.setDeclaredMethods(declaredMethods);
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeTarget() {
		return typeTarget;
	}
	public void setTypeTarget(String typeTarget) {
		this.typeTarget = typeTarget;
	}
	public boolean isTypeTargetMissing() {
		return typeTargetMissing;
	}
	public void setTypeTargetMissing(boolean typeTargetMissing) {
		this.typeTargetMissing = typeTargetMissing;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public Boolean getValidate() {
		return validate;
	}
	public void setValidate(Boolean validate) {
		this.validate = validate;
	}
	public Map getActionForwards() {
		return actionForwards;
	}
	public void setActionForwards(Map actionForwards) {
		this.actionForwards = actionForwards;
	}
	public Class getSuperClass() {
		return superClass;
	}
	public void setSuperClass(Class superClass) {
		this.superClass = superClass;
	}
	public Class[] getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(Class[] interfaces) {
		this.interfaces = interfaces;
	}
	public Class[] getDeclaredClasses() {
		return declaredClasses;
	}
	public void setDeclaredClasses(Class[] declaredClasses) {
		this.declaredClasses = declaredClasses;
	}
	public Constructor[] getDeclaredConstructors() {
		return declaredConstructors;
	}
	public void setDeclaredConstructors(Constructor[] declaredConstructors) {
		this.declaredConstructors = declaredConstructors;
	}
	public Field[] getDeclaredFields() {
		return declaredFields;
	}
	public void setDeclaredFields(Field[] declaredFields) {
		this.declaredFields = declaredFields;
	}
	public Method[] getDeclaredMethods() {
		return declaredMethods;
	}
	public void setDeclaredMethods(Method[] declaredMethods) {
		this.declaredMethods = declaredMethods;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getForward() {
		return forward;
	}
	public void setForward(String forward) {
		this.forward = forward;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInclude() {
		return include;
	}
	public void setInclude(String include) {
		this.include = include;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public Boolean getUnknown() {
		return unknown;
	}
	public void setUnknown(Boolean unknown) {
		this.unknown = unknown;
	}
	public StrutsActionFormBean getFormBean() {
		return formBean;
	}
	public void setFormBean(StrutsActionFormBean formBean) {
		this.formBean = formBean;
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
		return this.path;
	}
	
}
