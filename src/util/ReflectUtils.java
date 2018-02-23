package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.classloading.MyClassLoaderCache;
import util.classloading.MyLoadedClass;


/**
 * Utilities for reflectiong manipulation.
 */
public class ReflectUtils {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Release classloader-related resources.
	 * 
	 * @see MyClassLoaderCache#releaseResources();
	 */
	public static void releaseResources() {
		MyClassLoaderCache.releaseResources();
	}
	
	/**
	 * Obtain the <code>MyLoadedClass</code> for the specified class.
	 * 
	 * @param className String containing the (package and) name of the class.
	 * @return MyLoadedClass containing information about the specified Class.
	 */
	public static MyLoadedClass getMyLoadedClass(String className) {
		MyLoadedClass result = null;
		if (StringUtils.isNotEmpty(className)) {
			result = MyClassLoaderCache.forName(className);
			if (result != null) {
				Arrays.sort(result.getInterfaces()          , new MemberComparator());
				Arrays.sort(result.getDeclaredClasses()     , new MemberComparator());
				Arrays.sort(result.getDeclaredConstructors(), new MemberComparator());
				Arrays.sort(result.getDeclaredFields()      , new MemberComparator());
				Arrays.sort(result.getDeclaredMethods()     , new MemberComparator());
			}
		}
		return result;
	}
	
//	/**
//	 * Obtain the superClass for the specified class.
//	 * 
//	 * @param className String containing the (package and) name of the class.
//	 * @return Class containing the superClass of the specified Class.
//	 */
//	public static Class getSuperClass(String className) {
//		Class result = null;
//		MyLoadedClass myLoadedClass = getMyLoadedClass(className);
//		if (myLoadedClass != null) {
//			result = myLoadedClass.getSuperClass();
//		}
//		return result;
//	}
	
//	/**
//	 * Obtain the interfaces for the specified class.
//	 * 
//	 * @param className String containing the (package and) name of the class.
//	 * @return Array of Classes.
//	 */
//	public static Class[] getInterfaces(String className) {
//		Class[] result = new Class[0];
//		MyLoadedClass myLoadedClass = getMyLoadedClass(className);
//		if (myLoadedClass != null) {
//			result = myLoadedClass.getInterfaces();
//		}
//		return result;
//	}
	
//	/**
//	 * Obtain the declared classes for the specified class.
//	 * 
//	 * @param className String containing the (package and) name of the class.
//	 * @return Array of Classes.
//	 */
//	public static Class[] getDeclaredClasses(String className) {
//		Class[] result = new Class[0];
//		MyLoadedClass myLoadedClass = getMyLoadedClass(className);
//		if (myLoadedClass != null) {
//			result = myLoadedClass.getDeclaredClasses();
//		}
//		return result;
//	}
	
//	/**
//	 * Obtain the declared constructors for the specified class.
//	 * 
//	 * @param className String containing the (package and) name of the class.
//	 * @return Array of Constructors.
//	 */
//	public static Constructor[] getDeclaredConstructors(String className) {
//		Constructor[] result = new Constructor[0];
//		MyLoadedClass myLoadedClass = getMyLoadedClass(className);
//		if (myLoadedClass != null) {
//			result = myLoadedClass.getDeclaredConstructors();
//		}
//		return result;
//	}
	
//	/**
//	 * Obtain the declared fields for the specified class.
//	 * 
//	 * @param className String containing the (package and) name of the class.
//	 * @return Array of Fields.
//	 */
//	public static Field[] getDeclaredFields(String className) {
//		Field[] result = new Field[0];
//		MyLoadedClass myLoadedClass = getMyLoadedClass(className);
//		if (myLoadedClass != null) {
//			result = myLoadedClass.getDeclaredFields();
//		}
//		return result;
//	}
	
//	/**
//	 * Obtain the declared methods for the specified class.
//	 * 
//	 * @param className String containing the (package and) name of the class.
//	 * @return Array of Methods.
//	 */
//	public static Method[] getDeclaredMethods(String className) {
//		Method[] result = new Method[0];
//		MyLoadedClass myLoadedClass = getMyLoadedClass(className);
//		if (myLoadedClass != null) {
//			result = myLoadedClass.getDeclaredMethods();
//		}
//		return result;
//	}
	
	/**
	 * Obtain a value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: 'objectFieldName' may contain either a simple (single-level) reference,
	 *        like 'longValue' from a Long object, or it may also contain a complex
	 *        (nested) reference delimited using periods, like 'trim.toString' from
	 *        a String.  When this method encounters a nested reference, it simply
	 *        breaks the reference into multiple simple references and finds the
	 *        corresponding value for each until it's done.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Object containing the default value to use if the value is empty.
	 * @return Object containing the specified value from the object.
	 */
	public static Object getObjectValue(Object objectInstance, String objectFieldName, Object defaultValue) {
		Object result = defaultValue;
		try {
			if (objectInstance != null && !StringUtils.isEmpty(objectFieldName)) {
				String[] objectFieldNames = objectFieldName.split("\\.");
				Object object = objectInstance;
				for (int x = 0; x < objectFieldNames.length; x++) {
					Field field = object.getClass().getDeclaredField(objectFieldNames[x]);
					if (field != null) {
						field.setAccessible(true); // Bypass scope security
						Object work = field.get(object);
						if (x == objectFieldNames.length - 1) {
							result = work;
							if (result == null || "".equals(result)) {
								result = defaultValue;
							}
						} else {
							object = work;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.warn("WARN - ReflectUtils.getObjectValue(" + objectInstance + "[class=" + (objectInstance == null?"null":objectInstance.getClass().getName()) + "], '" + objectFieldName + "', '" + defaultValue + "') - " + ex.getMessage());
		}
		return result;
	}
	
	/**
	 * Convenience method used to obtain a Boolean value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Boolean containing the default value to use if the value is empty.
	 * @return Boolean containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static Boolean getObjectBoolean(Object objectInstance, String objectFieldName, Boolean defaultValue) {
		return (Boolean)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
	}
	
	/**
	 * Convenience method used to obtain a Byte value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Byte containing the default value to use if the value is empty.
	 * @param lessThanOneIsEmpty  Boolean indicating that a value < 1 is empty and triggers the defaultValue to be used.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectByte(Object objectInstance, String objectFieldName, Byte defaultValue, boolean lessThanOneIsEmpty) {
		String result = null;
		Byte work = (Byte)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
		if (lessThanOneIsEmpty && work != null && work.byteValue() < 1) {
			result = n2s(defaultValue);
		} else {
			result = n2s(work);
		}
		return result;
	}
	
	/**
	 * Convenience method used to obtain a Character value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Character containing the default value to use if the value is empty.
	 * @param lessThanOneIsEmpty  Boolean indicating that a value < 1 is empty and triggers the defaultValue to be used.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectCharacter(Object objectInstance, String objectFieldName, Character defaultValue, boolean lessThanOneIsEmpty) {
		String result = null;
		Character work = (Character)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
		if (lessThanOneIsEmpty && work != null && work.charValue() < 1) {
			result = c2s(defaultValue);
		} else {
			result = c2s(work);
		}
		return result;
	}
	
	/**
	 * Convenience method used to obtain a Short value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Short containing the default value to use if the value is empty.
	 * @param lessThanOneIsEmpty  Boolean indicating that a value < 1 is empty and triggers the defaultValue to be used.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectShort(Object objectInstance, String objectFieldName, Short defaultValue, boolean lessThanOneIsEmpty) {
		String result = null;
		Short work = (Short)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
		if (lessThanOneIsEmpty && work != null && work.shortValue() < 1) {
			result = n2s(defaultValue);
		} else {
			result = n2s(work);
		}
		return result;
	}
	
	/**
	 * Convenience method used to obtain a Integer value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Integer containing the default value to use if the value is empty.
	 * @param lessThanOneIsEmpty  Boolean indicating that a value < 1 is empty and triggers the defaultValue to be used.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectInteger(Object objectInstance, String objectFieldName, Integer defaultValue, boolean lessThanOneIsEmpty) {
		String result = null;
		Integer work = (Integer)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
		if (lessThanOneIsEmpty && work != null && work.intValue() < 1) {
			result = n2s(defaultValue);
		} else {
			result = n2s(work);
		}
		return result;
	}
	
	/**
	 * Convenience method used to obtain a Long value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Long containing the default value to use if the value is empty.
	 * @param lessThanOneIsEmpty  Boolean indicating that a value < 1 is empty and triggers the defaultValue to be used.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectLong(Object objectInstance, String objectFieldName, Long defaultValue, boolean lessThanOneIsEmpty) {
		String result = null;
		Long work = (Long)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
		if (lessThanOneIsEmpty && work != null && work.longValue() < 1) {
			result = n2s(defaultValue);
		} else {
			result = n2s(work);
		}
		return result;
	}
	
	/**
	 * Convenience method used to obtain a Float value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Float containing the default value to use if the value is empty.
	 * @param lessThanOneIsEmpty  Boolean indicating that a value < 1 is empty and triggers the defaultValue to be used.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectFloat(Object objectInstance, String objectFieldName, Float defaultValue, boolean lessThanOneIsEmpty) {
		String result = null;
		Float work = (Float)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
		if (lessThanOneIsEmpty && work != null && work.floatValue() < 1) {
			result = n2s(defaultValue);
		} else {
			result = n2s(work);
		}
		return result;
	}
	
	/**
	 * Convenience method used to obtain a Double value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Double containing the default value to use if the value is empty.
	 * @param lessThanOneIsEmpty  Boolean indicating that a value < 1 is empty and triggers the defaultValue to be used.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectDouble(Object objectInstance, String objectFieldName, Double defaultValue, boolean lessThanOneIsEmpty) {
		String result = null;
		Double work = (Double)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
		if (lessThanOneIsEmpty && work != null && work.doubleValue() < 1) {
			result = n2s(defaultValue);
		} else {
			result = n2s(work);
		}
		return result;
	}
	
	/**
	 * Convenience method used to obtain a Date value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectDate(Object, String, Object, String)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Date containing the default value to use if the value is empty.
	 * @return Date containing the specified value from the object.
	 * @see #getObjectDate(Object, String, Object, String)
	 */
	public static String getObjectDate(Object objectInstance, String objectFieldName, Date defaultValue) {
		return ReflectUtils.getObjectDate(objectInstance, objectFieldName, defaultValue, "MMMMM dd, yyyy");
	}
	
	/**
	 * Convenience method used to obtain a Date value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        Date containing the default value to use if the value is empty.
	 * @param outputValueFormat   String containing the (output) value format.
	 * @return Date containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectDate(Object objectInstance, String objectFieldName, Date defaultValue, String outputValueFormat) {
		return d2s((Date)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue), outputValueFormat);
	}
	
	/**
	 * Convenience method used to obtain a String value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        String containing the default value to use if the value is empty.
//	 * @param replaceSpecialChars Boolean indicating whether to replace special characters.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectString(Object objectInstance, String objectFieldName, String defaultValue/*, boolean replaceSpecialChars*/) {
		return getObjectString(objectInstance, objectFieldName, defaultValue/*, replaceSpecialChars*/, false);
	}
	
	/**
	 * Convenience method used to obtain a String value from an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #getObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param defaultValue        String containing the default value to use if the value is empty.
//	 * @param replaceSpecialChars Boolean indicating whether to replace special characters.
	 * @param convertEmptyToNull  Boolean indicating whether to convert empty values ("") to null.
	 * @return String containing the specified value from the object.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static String getObjectString(Object objectInstance, String objectFieldName, String defaultValue/*, boolean replaceSpecialChars*/, boolean convertEmptyToNull) {
		String result = (String)ReflectUtils.getObjectValue(objectInstance, objectFieldName, defaultValue);
		if (result != null) {
			// Format the result
			// ..Always trim the result
			result = result.trim();
//			// ..Search/replace special characters (when requested)
//			if (replaceSpecialChars) {
//				result = StringUtil.replaceSpecialChars(result, false);
//			}
			// ..Convert empty values to null (when specified)
			if (convertEmptyToNull && "".equals(result)) {
				result = null;
			}
		}
		return result;
	}
	
	/**
	 * Establish a value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: 'objectFieldName' may contain either a simple (single-level) reference,
	 *        like 'longValue' from a Long object, or it may also contain a complex
	 *        (nested) reference delimited using periods, like 'trim.toString' from
	 *        a String.  When this method encounters a nested reference, it simply
	 *        breaks the reference into multiple simple references and finds the
	 *        corresponding value for each until it's done.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         Object containing the value to establish in the object.
	 */
	public static void setObjectValue(Object objectInstance, String objectFieldName, Object objectValue) {
		try {
			if (objectInstance != null && !StringUtils.isEmpty(objectFieldName)) {
				String[] objectFieldNames = objectFieldName.split("\\.");
				Object object = objectInstance;
				for (int x = 0; x < objectFieldNames.length; x++) {
					Field field = object.getClass().getDeclaredField(objectFieldNames[x]);
					if (field != null) {
						field.setAccessible(true); // Bypass scope security
						Object work = field.get(object);
						if (x == objectFieldNames.length - 1) {
							field.set(object, objectValue);
						} else {
							object = work;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.warn("WARN - ReflectUtils.setObjectValue(" + objectInstance + "[class=" + (objectInstance == null?"null":objectInstance.getClass().getName()) + "], '" + objectFieldName + "', '" + objectValue + "') - " + ex.getMessage());
		}
	}
	
	/**
	 * Convenience method used to establish a Boolean value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectBoolean(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, objectValue);
	}
	
	/**
	 * Convenience method used to establish a Byte value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectByte(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, s2n(objectValue, Byte.class));
	}
	
	/**
	 * Convenience method used to establish a Character value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectCharacter(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, s2c(objectValue));
	}
	
	/**
	 * Convenience method used to establish a Short value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectShort(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, s2n(objectValue, Short.class));
	}
	
	/**
	 * Convenience method used to establish a Integer value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectInteger(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, s2n(objectValue, Integer.class));
	}
	
	/**
	 * Convenience method used to establish a Long value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectLong(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, s2n(objectValue, Long.class));
	}
	
	/**
	 * Convenience method used to establish a Float value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectFloat(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, s2n(objectValue, Float.class));
	}
	
	/**
	 * Convenience method used to establish a Double value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectDouble(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, s2n(objectValue, Double.class));
	}
	
	/**
	 * Convenience method used to establish a Date value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectDate(Object, String, Object, String)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @see #setObjectDate(Object, String, Object, String)
	 */
	public static void setObjectDate(Object objectInstance, String objectFieldName, String objectValue) {
		ReflectUtils.setObjectDate(objectInstance, objectFieldName, objectValue, "MMMMM dd, yyyy");
	}
	
	/**
	 * Convenience method used to establish a Date value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
	 * @param inputValueFormat    String containing the (input) value format.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectDate(Object objectInstance, String objectFieldName, String objectValue, String inputValueFormat) {
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, s2d(objectValue, inputValueFormat));
	}
	
	/**
	 * Convenience method used to establish a String value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
//	 * @param replaceSpecialChars Boolean indicating whether to replace special characters.
	 * @see #setObjectValue(Object, String, Object)
	 */
	public static void setObjectString(Object objectInstance, String objectFieldName, String objectValue/*, boolean replaceSpecialChars*/) {
		ReflectUtils.setObjectString(objectInstance, objectFieldName, objectValue/*, replaceSpecialChars*/, false);
	}
	
	/**
	 * Convenience method used to establish a String value in an Object
	 * (using Reflection).
	 * <p>
	 *  NOTE: This method defers most of its processing to
	 *        {@link #setObjectValue(Object, String, Object)}.
	 * </p>
	 * 
	 * @param objectInstance      Instance of the object.
	 * @param objectFieldName     String containing the name of the field in the object.
	 * @param objectValue         String containing the value to use in the object.
//	 * @param replaceSpecialChars Boolean indicating whether to replace special characters.
	 * @param convertEmptyToNull  Boolean indicating whether to convert empty values ("") to null.
	 * @see #getObjectValue(Object, String, Object)
	 */
	public static void setObjectString(Object objectInstance, String objectFieldName, String objectValue/*, boolean replaceSpecialChars*/, boolean convertEmptyToNull) {
		// Format the value
		// ..Always trim the result
		objectValue = StringUtils.defaultString(objectValue).trim();
//		// ..Search/replace special characters (when requested)
//		if (replaceSpecialChars) {
//			objectValue = StringUtil.replaceSpecialChars(objectValue, false);
//		}
		// ..Convert empty values to null (when specified)
		if (convertEmptyToNull && "".equals(objectValue)) {
			objectValue = null;
		}
		// Set the value
		ReflectUtils.setObjectValue(objectInstance, objectFieldName, objectValue);
	}
	
	
	/**
	 * Convenience method used to format a character in the standard format and convert
	 * any null results automatically to an empty string.
	 * 
	 * @param input Character containing the character value.
	 * @return String equivalent of the specified input character value.
	 * @see #d2s(Date)
	 * @see #n2s(Number)
	 */
	public static String c2s(Character input) {
		return (input == null)?"":input.charValue()+"";
	}
	
	/**
	 * Convenience method used to format a date in the standard format and convert
	 * any null results automatically to an empty string.
	 * <p>
	 *  NOTE: The format used for the resulting dates is always: MMMMM dd, yyyy
	 * </p>
	 * 
	 * @param input Date containing the date value.
	 * @return String equivalent of the specified input date value.
	 * @see #c2s(Character)
	 * @see #n2s(Number)
	 */
	public static String d2s(Date input) {
		return d2s(input, "MMMMM dd, yyyy");
	}
	
	/**
	 * Convenience method used to format a date in the specified format and convert
	 * any null results automatically to an empty string.
	 * 
	 * @param input Date containing the date value.
	 * @param format String containing the target format.
	 * @return String equivalent of the specified input date value.
	 * @see #c2s(Character)
	 * @see #n2s(Number)
	 */
	public static String d2s(Date input, String format) {
		return StringUtils.defaultString(DateUtils.fromDate(input, format));
	}
	
	/**
	 * Convenience method used to format a numeric in the standard format and convert
	 * any null results automatically to an empty string.
	 * <p>
	 *  NOTE: <code>Character</code> is not a subclass of <code>Number</code> and
	 *        is therefore not supported by this method.  Instead, refer to the
	 *        {@link #c2s(Character)} method.
	 * </p>
	 * 
	 * @param input Number containing the numeric value.
	 * @return String equivalent of the specified input numeric value.
	 * @see #c2s(Character)
	 * @see #d2s(Date)
	 */
	public static String n2s(Number input) {
		if (input == null) {
			return "";
		}
		if (input instanceof Byte ||
			input instanceof Short ||
			input instanceof Integer ||
			input instanceof Long) {
			return input.longValue()+"";
		}
		if (input instanceof Float ||
			input instanceof Double) {
			return input.doubleValue()+"";
		}
		return "";
	}
	
	/**
	 * Convenience method used to convert a string representation of a character into
	 * a character.
	 * 
	 * @param input String equivalent of the target character value.
	 * @return Character containing the specified character value.
	 * @see #s2d(Date)
	 * @see #s2n(Number)
	 */
	public static Character s2c(String input) {
		return (input == null)?null:new Character(input.charAt(0));
	}
	
	/**
	 * Convenience method used to convert a string representation of a date into
	 * a date.
	 * 
	 * @param input String equivalent of the target date value.
	 * @return Date containing the specified date value.
	 * @see #s2c(String)
	 * @see #s2n(String)
	 */
	public static Date s2d(String input) {
		return s2d(input, null);
	}
	
	/**
	 * Convenience method used to convert a string representation of a date into
	 * a date.
	 * <p>
	 *  NOTE: The default format for the input dates is always: MMMMM dd, yyyy
	 * </p>
	 * 
	 * @param input String equivalent of the target date value.
	 * @param format String containing the input format.
	 * @return Date containing the specified date value.
	 * @see #s2c(String)
	 * @see #s2n(String)
	 */
	public static Date s2d(String input, String format) {
		if (StringUtils.isEmpty(format)) {
			format = "MMMMM dd, yyyy";
		}
		return DateUtils.toDate(input, format);
	}
	
	/**
	 * Convenience method used to convert a string representation of a numeric into
	 * a numeric.
	 * <p>
	 *  NOTE: <code>Character</code> is not a subclass of <code>Number</code> and
	 *        is therefore not supported by this method.  Instead, refer to the
	 *        {@link #s2c(String)} method.
	 * </p>
	 * 
	 * @param input String equivalent of the input numeric value.
	 * @return Number containing the specified numeric value.
	 * @see #s2c(String)
	 * @see #s2d(String)
	 */
	public static Number s2n(String input) {
		return s2n(input, null);
	}
	
	/**
	 * Convenience method used to convert a string representation of a numeric into
	 * a numeric.
	 * <p>
	 *  NOTE: <code>Character</code> is not a subclass of <code>Number</code> and
	 *        is therefore not supported by this method.  Instead, refer to the
	 *        {@link #s2c(String)} method.
	 * </p>
	 * 
	 * @param input String equivalent of the input numeric value.
	 * @param clazz (Optional) Class to use for the resulting number (finds most appropriate if empty).
	 * @return Number containing the specified numeric value.
	 * @see #s2c(String)
	 * @see #s2d(String)
	 */
	public static Number s2n(String input, Class clazz) {
		Number result = null;
		if (StringUtils.isNumeric(input)) {
			double  work    = Double.parseDouble(input);
			boolean isWhole = (work % 1 != 0);
			if        (clazz == Byte.class    || ( isWhole && work >= Byte.MIN_VALUE    && work <= Byte.MAX_VALUE   )) {
				result = new Byte(input);
			} else if (clazz == Short.class   || ( isWhole && work >= Short.MIN_VALUE   && work <= Short.MAX_VALUE  )) {
				result = new Short(input);
			} else if (clazz == Integer.class || ( isWhole && work >= Integer.MIN_VALUE && work <= Integer.MAX_VALUE)) {
				result = new Integer(input);
			} else if (clazz == Long.class    || ( isWhole && work >= Long.MIN_VALUE    && work <= Long.MAX_VALUE   )) {
				result = new Integer(input);
			} else if (clazz == Float.class   || (!isWhole && work >= Float.MIN_VALUE   && work <= Float.MAX_VALUE  )) {
				result = new Float(input);
			} else if (clazz == Double.class  || (!isWhole && work >= Double.MIN_VALUE  && work <= Double.MAX_VALUE )) {
				result = new Double(input);
			}
		}
		return result;
	}
	
	/********************
	 * Helper class(es) *
	 ********************/
	
	/**
	 * Used to sort/compare two Reflection-based objects.
	 * <ul>
	 *  NOTE: Currently, the supported Reflection-based objects are...
	 *  <li>Class</li>
	 *  <li>Constructor</li>
	 *  <li>Method</li>
	 *  <li>Field</li>
	 * </ul>
	 */
	private static class MemberComparator implements Comparator {
		
		/**
		 * Compare two Reflection-related objects.
		 * 
		 * @param object1 Object containing the 1st object to compare.
		 * @param object2 Object containing the 2nd object to compare.
		 * @return Integer indicating the sort order of the objects.
		 */
		public int compare(Object object1, Object object2) {
			String sortValue1 = null;
			String sortValue2 = null;
			if (object1 instanceof Class) {
				sortValue1 = ((Class)object1).getPackage() + ":" + ((Class)object1).getName() + ":" + ((Class)object1).getModifiers();
				sortValue2 = ((Class)object2).getPackage() + ":" + ((Class)object2).getName() + ":" + ((Class)object2).getModifiers();
			} else
			if (object1 instanceof Constructor) {
				sortValue1 = ((Constructor)object1).getDeclaringClass() + ":" + ((Constructor)object1).getName() + ":" + ((Constructor)object1).getModifiers();
				sortValue2 = ((Constructor)object2).getDeclaringClass() + ":" + ((Constructor)object2).getName() + ":" + ((Constructor)object2).getModifiers();
			} else
			if (object1 instanceof Field) {
				sortValue1 = ((Field)object1).getName() + ":" + ((Field)object1).getModifiers() + ":" + ((Field)object1).getType();
				sortValue2 = ((Field)object2).getName() + ":" + ((Field)object2).getModifiers() + ":" + ((Field)object2).getType();
			} else
			if (object1 instanceof Method) {
				sortValue1 = ((Method)object1).getName() + ":" + ((Method)object1).getModifiers() + ":" + ((Method)object1).getReturnType();
				sortValue2 = ((Method)object2).getName() + ":" + ((Method)object2).getModifiers() + ":" + ((Method)object2).getReturnType();
			}
			return sortValue1.compareTo(sortValue2);
		}
	}

}
