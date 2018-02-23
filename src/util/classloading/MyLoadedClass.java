package util.classloading;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * Container for all information related to a successfully loaded class.
 */
public class MyLoadedClass {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String        className            = null;
	private Date          lastModified         = null;
	private Class         superClass           = null;
	private Class[]       interfaces           = null;
	private Class[]       declaredClasses      = null;
	private Constructor[] declaredConstructors = null;
	private Field[]       declaredFields       = null;
	private Method[]      declaredMethods      = null;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public MyLoadedClass() {}
	public MyLoadedClass(String className, Date lastModified,
			Class superClass, Class[] interfaces,
			Class[] declaredClasses, Constructor[] declaredConstructors,
			Field[] declaredFields, Method[] declaredMethods) {
		this.className            = className;
		this.lastModified         = lastModified;
		this.superClass           = superClass;
		this.interfaces           = interfaces;
		this.declaredClasses      = declaredClasses;
		this.declaredConstructors = declaredConstructors;
		this.declaredFields       = declaredFields;
		this.declaredMethods      = declaredMethods;
	}
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
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
	
}
