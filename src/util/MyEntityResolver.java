package util;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Allow for redirecting certain entities to copies on the local file-system.
 * 
 * @author TAYLOJ10
 * @version Added [1.01]
 */
public class MyEntityResolver implements EntityResolver {
	
	/**
	 * Container for overrides to the 'systemId'.
	 */
	private Map systemIds = new HashMap();
	
	/**
	 * Container for class used to locate resources.
	 */
	private Class baseClass = null;
	
	/**
	 * Default constructor.
	 */
	public MyEntityResolver(Class baseClass) {
		this.baseClass = baseClass;
		
		// Add entries to override specific 'systemId' values
		systemIds.put("http://jakarta.apache.org/struts/dtds/struts-config_1_1.dtd", "resources/struts-config_1_1.dtd");
		systemIds.put("http://jakarta.apache.org/struts/dtds/tiles-config.dtd"     , "resources/tiles-config.dtd"     );
	}
	
	/**
	 * Implementation of the interface method.
	 * 
	 * @param publicId String containing the 'publicId' of the entity.
	 * @param systemId String containing the 'systemId' of the entity.
	 * @return InputSource for the entity.
	 */
	public InputSource resolveEntity (String publicId, String systemId) {
		// attempt to find/return custom input source
		if (systemIds.containsKey(systemId)) {
			// Return local copy of the file
            return new InputSource(baseClass.getResource((String)systemIds.get(systemId)).toExternalForm());
		}
		// use the default behaviour
		return null;
	}

}
