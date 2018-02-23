package util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Provides functionality for maintaining a set of missing resources.
 * 
 * TODO[jpt] Merge this class back into Viewer.
 */
public class MissingResources {

	/***************
	 * Constant(s) *
	 ***************/
	
	public static final String MISSING_RESOURCE_OBJECT = "[OBJECT]";
	public static final String MISSING_RESOURCE_SOURCE = "[SOURCE]";
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");
	
	/**
	 * Contains the set of missing resources.
	 */
	private static Set missingResources = new TreeSet();
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
    public static Set getMissingResources() {
    	return missingResources;
    }
    public static void setMissingResources(Set missingResources) {
    	MissingResources.missingResources = missingResources;
    }

	/********************
	 * Member method(s) *
	 ********************/
	
    /**
     * Add a missing resource to the set of missing resources.
     * 
     * @param type Integer indicating the resource type (0 == Source, 1 == Object)
     * @param resourceName String indicating the name of the missing resource.
     */
    public static void add(int type, String resourceName) {
    	String typeDesc = null;
    	switch (type) {
    		case 0:
    			typeDesc = MISSING_RESOURCE_SOURCE;
    			break;
    		case 1:
    			typeDesc = MISSING_RESOURCE_OBJECT;
    			break;
    	}
    	missingResources.add(typeDesc + " " + resourceName);
    }
    
    /**
     * Add a missing (source) resource to the set of missing resources.
     * 
     * @param resourceName String indicating the name of the missing resource.
     */
    public static void addSource(String resourceName) {
    	add(0, resourceName);
    }
    
    /**
     * Add a missing (object) resource to the set of missing resources.
     * 
     * @param resourceName String indicating the name of the missing resource.
     */
    public static void addObject(String resourceName) {
    	add(1, resourceName);
    }
    
    /**
     * Output the set of missing resources to the logger.
     */
    public static void logMissingResources() {
		if (!missingResources.isEmpty()) {
			logger.warn("Missing files/resources (totalling " + missingResources.size() + "): START");
			for (Iterator it = missingResources.iterator(); it.hasNext();) {
				logger.warn("+ " + it.next());
			}
			logger.warn("Missing files/resources (totalling " + missingResources.size() + "): END");
		}
    }

}
