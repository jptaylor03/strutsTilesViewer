package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Utilities for miscellaneous things.
 */
public class MiscUtils {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");
	
	/**
	 * DocumentBuilderFactory instance.
	 */
	private static DocumentBuilderFactory dbf = null;
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Obtain a resource (from either inside a JAR or on the filesystem) and
	 * output its contents to a temporary file on the filesystem.
	 * 
	 * @param baseClass    Class to be used as a location reference point to help find the resource.
	 * @param resourceName String containing the name of the resource.
	 * @return String containing the (path and) name of the temporary file
	 *         which has just been created using the contents of the resource.
	 */
	public static String resourceToTempFile(Class baseClass, String resourceName) {
		String result = null;
		InputStream  inputStream  = null;
		OutputStream outputStream = null;
		try {
			inputStream = baseClass.getResourceAsStream(resourceName);
			String[] resource = new File(resourceName).getName().split("[.]");
			File file = File.createTempFile(resource[0], "." + (resource.length == 0?"tmp":resource[1]));
			outputStream = new FileOutputStream(file);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				outputStream.write(buf,0,len);
			}
			result = file.getAbsolutePath();
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return result;
	}
	
	/**
	 * Parse the specified xml file/resource into a {@link org.w3c.dom.Document} object. 
	 * 
	 * @param baseClass     Class to be used as a location reference point to help find the resource.
	 * @param xmlFileTarget String containing the name of the file/resource.
	 * @return Document object based on the contents of the target xml file.
	 */
	public static Document parseXmlFile(Class baseClass, String xmlFileTarget){
		Document result = null;
		
		// Obtain the factory
		if (dbf == null) {
			dbf = DocumentBuilderFactory.newInstance();
		}
		
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setEntityResolver(new MyEntityResolver(baseClass)); // [1.01]

			// Parse using builder to get DOM representation of the XML file
			URL url = baseClass.getResource(xmlFileTarget);
			if (url != null) {
				result = db.parse(url.openStream()); // Accessing JAR content
			} else {
				result = db.parse(xmlFileTarget);    // Accessing external content
			}
			logger.info("Parsed file: " + xmlFileTarget);
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(SAXException se) {
			se.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Find the specified resource by iterating through the pathMasks.
	 * <p>
	 *  NOTE: The first occurance of the resource will be used.  Therefore, the
	 *        iteration order is very important.  Because of this, the 'pathMasks'
	 *        value is used to control the iteration order.
	 * </p>
	 * 
	 * @param pathMasks    List containing masks of paths (to iterate).
	 * @param pathsByMask  Map  containing paths keyed by mask (to search).
	 * @param resourceName String identifying the name of the target resource.
	 * @return String containing the absolute path of the target resource.
	 */
	public static String findResource(List pathMasks, Map pathsByMask, String resourceName) {
		String result = null;
		if (pathMasks != null && pathsByMask != null && StringUtils.isNotEmpty(resourceName)) {
			for (Iterator it = pathMasks.iterator(); it.hasNext() && result == null;) {
				String mask  = (String)it.next();
				List   paths = (List)pathsByMask.get(mask);
				if (paths != null) {
					for (Iterator itSub = paths.iterator(); itSub.hasNext() && result == null;) {
						File path = (File)itSub.next();
						File resource = new File(path, resourceName); 
						if (resource.exists()) result = resource.getAbsolutePath();
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Determine whether the specified file/resource is missing.
	 * 
	 * @param baseClass    Class to be used as a location reference point to help find the resource.
	 * @param resourceName String containing the name of the resource.
	 * @return Boolean indicating whether the file/resource is missing.
	 */
    public static boolean confirmResourceMissing(Class baseClass, String resourceName) {
    	boolean result = false;
    	if (baseClass != null && StringUtils.isNotEmpty(resourceName)) {
    		// Does the resource exist as a file on the file-system?
    		File file = new File(resourceName);
    		if (!file.exists()) { // No
    			// Does the linkTarget exist as a resource (inside this apps JAR)?
    			URL url = baseClass.getResource(resourceName);
    			if (url == null) { // No
    				// Record the missing resource
    				MissingResources.addSource(resourceName);
    				// Set the return value
    				result = true;
    			}
    		}
    	}
    	return result;
    }
    
    /**
     * Obtain the specified file's 'lastModified' timestamp.
     * 
     * @param fileSystemObjectName String containing the name of the file.
     * @return Date containing the specified file's 'lastModified' timestamp (or <code>null</code> if not found).
     */
    public static Date getFileSystemObjectLastModified(String fileSystemObjectName) {
    	Date result = null;
    	if (fileSystemObjectName != null) {
    		File fileSystemObject = new File(fileSystemObjectName);
    		if (fileSystemObject.exists()) {
        		result = new Date(fileSystemObject.lastModified());
    		}
    	}
    	return result;
    }

}
