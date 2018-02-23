package util.classloading;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import model.AppState;
import model.ViewerConfig;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.MiscUtils;
import util.MissingResources;
import view.Viewer;


/**
 * Provide classloader-related caching.
 */
public class MyClassLoaderCache {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");
	
	/**
	 * Provides quick/easy reference to {@link Viewer#viewerConfig}.
	 */
	private static ViewerConfig vc = Viewer.getViewerConfig();
	
	/**
	 * Container for a map of successfully loaded classes.
	 * <p>
	 *  NOTE: Each map entry's key is the class name (a.k.a. the 'forName'),
	 *        while each map entry's value is the corresponding <code>MyLoadedClass</code>.
	 * </p>
	 */
	private static Map loadedClasses = new HashMap();
	
	/**
	 * Container for a set of missing classes.
	 * <p>
	 *  NOTE: Each set entry is the class name (a.k.a. the 'forName').
	 * </p>
	 */
	private static Set missingClasses = new HashSet();
	
	/**
	 * Instance of the MyURLClassLoader class.
	 * 
	 * @see MyURLClassLoader
	 */
	private static MyURLClassLoader classLoader = null;
	
	/**
	 * Instance of the URL array for containing the classpath.
	 */
	private static URL[] classPath = null;
	
	/**
	 * Instance of the (inner) DateComparator class.
	 * 
	 * @see DateComparator
	 */
	private static DateComparator dateComparator = new DateComparator();
	
	/**
	 * Instance of a map containing both {@link ViewerConfig#getTargetAppObjectCodePathMasks()}
	 * and {@link ViewerConfig#getTargetAppClasspathPathMasks()} for the purposes of (easily)
	 * finding/loading a (target) class.
	 */
	private static List combinedTargetAppObjectCodePathMasks = null;
	
	/**
	 * Instance of a map containing both ViewerConfig#getTargetAppObjectCodePathsByMask()
	 * and ViewerConfig#getTargetAppClasspathPathsByMask() for the purposes of (easily)
	 * finding/loading a (target) class.
	 */
	private static Map combinedTargetAppObjectCodePathsByMask = null;
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	/**
	 * Obtain the current classloader.
	 * <p>
	 *  NOTE: If the current classloader is currently empty, then a new one is allocated.
	 * </p>
	 * 
	 * @return MyURLClassLoader containing the current classloader.
	 */
    private static MyURLClassLoader getClassLoader() {
    	if (classLoader == null) {
			classLoader = new MyURLClassLoader(getClassPath(), MyClassLoaderCache.class.getClassLoader());
    	}
    	return classLoader;
    }
    
    /**
     * Obtain the current classpath.
	 * <p>
	 *  NOTE: If the current classpath is currently empty, then a new one is allocated.
	 * </p>
     *  
     * @return URL array containing the current classpath.
     */
	private static URL[] getClassPath() {
		if (classPath == null) {
			List classPathList = new ArrayList();
			addPathsToClassPath(vc.getTargetAppObjectCodePathsByMask(), classPathList);
			addPathsToClassPath(vc.getTargetAppClasspathPathsByMask(),  classPathList);
			logger.info("Classpath used by the embedded Class Loader: " + classPathList);
			classPath = (URL[])classPathList.toArray(new URL[classPathList.size()]);
		}
		return classPath;
	}
	
	/**
	 * Obtain the combinedTargetAppObjectCodePathMasks list.
	 * <p>
	 *  NOTE: If the list is currently empty, then a new one is allocated.
	 * </p>
	 * 
	 * @return List containing the resulting values.
	 */
	private static List getCombinedTargetAppObjectCodePathMasks() {
		if (combinedTargetAppObjectCodePathMasks == null) {
			List work = new ArrayList();
			work.addAll(vc.getTargetAppObjectCodePathMasks());
			work.addAll(vc.getTargetAppClasspathPathMasks() );
			combinedTargetAppObjectCodePathMasks = work;
		}
		return combinedTargetAppObjectCodePathMasks;
	}
	
	/**
	 * Obtain the combinedTargetAppObjectCodePathsByMask map.
	 * <p>
	 *  NOTE: If the map is currently empty, then a new one is allocated.
	 * </p>
	 * 
	 * @return Map containing the resulting values.
	 */
	private static Map getCombinedTargetAppObjectCodePathsByMask() {
		if (combinedTargetAppObjectCodePathsByMask == null) {
			Map work = new TreeMap();
			work.putAll(vc.getTargetAppObjectCodePathsByMask());
			work.putAll(vc.getTargetAppClasspathPathsByMask() );
			combinedTargetAppObjectCodePathsByMask = work;
		}
		return combinedTargetAppObjectCodePathsByMask;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Obtain information about class having the specified (package and) name.
	 * 
	 * @param className String containing the name of the (package and) class to lookup.
	 * @return MyLoadedClass containing the relevant information about the specified class.
	 */
	public static MyLoadedClass forName(String className) {
		MyLoadedClass result = null;
		if (StringUtils.isNotEmpty(className) && !missingClasses.contains(className)) {
			result = (MyLoadedClass)loadedClasses.get(className);
			
			String classTarget  = MiscUtils.findResource(getCombinedTargetAppObjectCodePathMasks(), getCombinedTargetAppObjectCodePathsByMask(), className.replace('.', '/') + ".class");
			Date   lastModified = MiscUtils.getFileSystemObjectLastModified(classTarget);
			if (result == null || dateComparator.compare(result.getLastModified(), lastModified) != 0) {
				try {
//					MyURLClassLoader myURLClassLoader = getClassLoader();
//					if (Viewer.appState.is(AppState.RESTART) && result != null && dateComparator.compare(result.getLastModified(), lastModified) != 0) {
//						myURLClassLoader = new MyURLClassLoader(getClassPath(), MyClassLoaderCache.class.getClassLoader());
//					}
					MyURLClassLoader myURLClassLoader = null;
					if (Viewer.appState.is(AppState.STARTUP)) {
						myURLClassLoader = getClassLoader();
					} else
					if (Viewer.appState.is(AppState.RESTART)) {
						if (result != null && dateComparator.compare(result.getLastModified(), lastModified) != 0) {
							myURLClassLoader = new MyURLClassLoader(getClassPath(), MyClassLoaderCache.class.getClassLoader());
						}
					}
					if (myURLClassLoader != null) {
						Class clazz = Class.forName(className, true, myURLClassLoader);
						// NOTE: Cloning is used to prevent references back to the class
						Class[]        declaredClasses      = (Class      [])ArrayUtils.clone(clazz.getDeclaredClasses()     );
						Constructor[]  declaredConstructors = (Constructor[])ArrayUtils.clone(clazz.getDeclaredConstructors());
						Field[]        declaredFields       = (Field      [])ArrayUtils.clone(clazz.getDeclaredFields()      );
						Method[]       declaredMethods      = (Method     [])ArrayUtils.clone(clazz.getDeclaredMethods()     );
						Class[]        interfaces           = (Class      [])ArrayUtils.clone(clazz.getInterfaces()          );
						result = new MyLoadedClass(className, lastModified, clazz.getSuperclass(), interfaces, declaredClasses, declaredConstructors, declaredFields, declaredMethods);
						loadedClasses.put(className, result);
					}
				} catch (Throwable t) {
					logger.warn(t);
					// Record the missing resource
					MissingResources.addObject(className);
					if (!missingClasses.contains(className)) missingClasses.add(className);
					if (loadedClasses.containsKey(className)) loadedClasses.remove(className);
				}
			}
		}
		return result;
	}
	
    /**
     * Release necessary resources after all class loading has been completed.
     * <p>
     *  NOTE: Refer to {@link MyURLClassLoader#disableUrlConnectionCaching(java.net.URLConnection)}
     *        for an explanation of how JAR files <i>were</i> being kept open
     *        (and therefore locked) by the class loading process.  However, the
     *        problem was corrected by overridding both <code>java.net.URLClassLoader</code>
     *        (resulting in {@link util.classloading.MyURLClassLoader}) as well as
     *        <code>sun.misc.URLClassPath</code> (resulting in {@link util.classloading.MyURLClassPath}).
     * </p>
     */
	public static void releaseResources() {
		/*
		 * While releasing the classLoader itself sounds like a reasonable
		 * thing to do, in practice it proved to be problematic.  If the
		 * classLoader is released, then any subsequent "reloading" of the
		 * configuration results in <code>java.lang.OutOfMemoryError</code>s.
		 * Therefore, we keep the classLoader in memory and subsequent "reloads"
		 * work perfectly.
		 */
	//	classLoader = null;
	//	classLoader = MyURLClassLoader.newInstance(getClassPath());
	//	classLoader.reset();
		missingClasses.clear();
	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Add the paths w/in the specified map to the specified list.
	 * 
	 * @param masksToPath        Map keyed by a mask each containing a list of paths.
	 * @param resultingClassPath List containing the updated classpath.
	 */
	private static void addPathsToClassPath(Map masksToPaths, List resultingClassPath) {
		if (resultingClassPath != null && masksToPaths != null) {
			try {
				for (Iterator it = masksToPaths.keySet().iterator(); it.hasNext();) {
					String mask = (String)it.next();
					List paths = (List)masksToPaths.get(mask);
					for (Iterator itSub = paths.iterator(); itSub.hasNext();) {
		    			File path = (File)itSub.next();
		    			if (path.exists()) {
			    			String prefix = "";
			    			String suffix = "/";
			    			if (path.isFile()) {
			    				prefix = "jar:";
			    				suffix = "!/";
			    			}
		    				resultingClassPath.add(new URL(prefix + "file:/" + path.getAbsolutePath() + suffix));
		    			}
					}
				}
			} catch (Throwable t) {
				logger.error(t);
			}
		}
	}
	
	/********************
	 * Helper class(es) *
	 ********************/
	
	/**
	 * Used to sort/compare two {@link java.util.Date}s
	 * while allowing both/either to be <code>null</code>.
	 */
	private static class DateComparator implements Comparator {
		
		/**
		 * Compare two {@link java.util.Date}s while allowing both/either to be <code>null</code>.
		 * 
		 * @param object1 Object containing the 1st date to compare.
		 * @param object2 Object containing the 2nd date to compare.
		 * @return Integer indicating the sort order of the dates.
		 */
		public int compare(Object object1, Object object2) {
			int result = 0;
			if (object1 != null && object2 != null) {
				result = ((Date)object1).compareTo((Date)object2);
			} else if (object1 == null && object2 != null) {
				result = 1;
			} else if (object1 != null && object2 == null) {
				result = -1;
			} else /*object1 == null && object2 == null*/ {
				result = 0;
			}
			return result;
		}
	}

}
