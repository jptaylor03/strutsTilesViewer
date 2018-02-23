package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


/**
 * Container for a Struts Action Form Bean.
 */
public class StrutsActionFormBean extends AbstractVO {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String name;
	private String type;
	private String typeTarget;
	private boolean typeTargetMissing;
	private List actionMappings;
	private Class superClass;
	private Class[] interfaces;
	private Class[] declaredClasses;
	private Constructor[] declaredConstructors;
	private Field[] declaredFields;
	private Method[] declaredMethods;
	private String className;
	private Boolean dynamic;
	private String id;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public StrutsActionFormBean() {
		super();
	}
	public StrutsActionFormBean(String name, String type, List actionMappings) {
		this();
		this.setName(name);
		this.setType(type);
		this.setActionMappings(actionMappings);
	}
	public StrutsActionFormBean(String name, String type, List actionMappings,
			Class superClass, Class[] interfaces,
			Class[] declaredClasses, Constructor[] declaredConstructors,
			Field[] declaredFields, Method[] declaredMethods) {
		this(name, type, actionMappings);
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
	public List getActionMappings() {
		return actionMappings;
	}
	public void setActionMappings(List actionMappings) {
		this.actionMappings = actionMappings;
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
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Boolean getDynamic() {
		return dynamic;
	}
	public void setDynamic(Boolean dynamic) {
		this.dynamic = dynamic;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
}
