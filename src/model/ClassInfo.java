package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;


/**
 * Container for <code>Class</code> properties.
 * <p>
 *  NOTE: See the deprecation note explaining why this class is currently
 *        not being used.
 * </p>
 * 
 * @deprecated Enabling 'declaredClasses' causes the JAR files to be locked.  The
 *             best guess is that hard references back to the originating class are
 *             kept intact which prevents the JAR(s) from being unlocked.  However,
 *             this doesn't explain why we can do the exact same thing directly in
 *             both {@link StrutsActionFormBean} and {@link StrutsActionMapping} w/o
 *             suffering any of the locked-JAR consequences.  Therefore, until this
 *             problem is resolved we're not using this class.
 */
public class ClassInfo {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String        name                 = null;
//	private Class         superClass           = null;
//	private Class[]       interfaces           = null;
//	private Class[]       declaredClasses      = null;
	private Constructor[] declaredConstructors = null;
	private Field[]       declaredFields       = null;
	private Method[]      declaredMethods      = null;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public ClassInfo() {
		super();
	}
	public ClassInfo(String name) {
		this();
		this.setName(name);
	}
	public ClassInfo(String name,
//			Class superClass, Class[] interfaces, Class[] declaredClasses,
			Constructor[] declaredConstructors, Field[] declaredFields, Method[] declaredMethods) {
		this(name);
//		this.setSuperClass(          superClass          );
//		this.setInterfaces(          interfaces          );
//		this.setDeclaredClasses(     declaredClasses     );
		this.setDeclaredConstructors(declaredConstructors);
		this.setDeclaredFields(      declaredFields      );
		this.setDeclaredMethods(     declaredMethods     );
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
//	public Class getSuperClass() {
//		return superClass;
//	}
//	public void setSuperClass(Class superClass) {
//		this.superClass = superClass;
//	}
//	public Class[] getInterfaces() {
//		return interfaces;
//	}
//	public void setInterfaces(Class[] interfaces) {
//		this.interfaces = interfaces;
//	}
//	public Class[] getDeclaredClasses() {
//		return declaredClasses;
//	}
//	public void setDeclaredClasses(Class[] declaredClasses) {
//		this.declaredClasses = declaredClasses;
//	}
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
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Convenience method used to convert a {@link java.lang.Class} into a ClassInfo.
	 * 
	 * @param clazz Class to convert.
	 * @return ClassInfo representation of the specified {@link java.lang.Class}.
	 */
	public static ClassInfo classToClassInfo(Class clazz) {
		ClassInfo result = null;
		if (clazz != null) {
			result = new ClassInfo();
			/*
			 * NOTE: Cloning is used to prevent references back to the class
			 *       in order to prevent (JAR) file-locking.
			 */
			result.setName(                                     new String(clazz.getName()                ));
//			result.setSuperClass(                                          clazz.getSuperClass()           );
//			result.setInterfaces(          (Class[]      )ArrayUtils.clone(clazz.getInterfaces()          ));
//			result.setDeclaredClasses(     (Class[]      )ArrayUtils.clone(clazz.getDeclaredClasses()     ));
			result.setDeclaredConstructors((Constructor[])ArrayUtils.clone(clazz.getDeclaredConstructors()));
			result.setDeclaredFields(      (Field[]      )ArrayUtils.clone(clazz.getDeclaredFields()      ));
			result.setDeclaredMethods(     (Method[]     )ArrayUtils.clone(clazz.getDeclaredMethods()     ));
		}
		return result;
	}
	
}
