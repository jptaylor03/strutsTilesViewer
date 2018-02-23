package unlockedJars;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import model.AppState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import view.Viewer;


/**
 * Test that JAR files are unlocked after Viewer configuration has been loaded
 * AND reloaded.
 * <p>
 *  NOTE: JAR files will, of course, be locked during the loading (and reloading)
 *        of the Viewer configuration.  However, when finished, the JAR files
 *        should then return to an unlocked state.
 * </p>
 * <p>
 *  <font color="red">
 *  WARNING: 'Run' mode always seems to fail (with a 'file must be locked' failure),
 *           however, 'Debug' mode seems to work properly.
 *  </font>
 * </p>
 */
public class UnlockedJarsTest extends TestCase {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("test");
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Load the Struts Tiles Viewer application (using a small(er) viewer-config.xml),
	 * attempt to rename a JAR file from the classpath, then 'reload configuration',
	 * and finally attempt (again) to rename a JAR file from the classpath.
	 * 
	 * @throws Throwable
	 */
	public void testJarsRemainUnlocked() throws Throwable {
		logger.debug("unlockedJarsTest BEGIN");
		
		// Determine absolute path of 'viewer-config.xml' override file
		String vcOverrideName = System.getProperty("user.dir") + "/bin/unlockedJars/resources/viewer-config.xml";
		File vcOverride = new File(vcOverrideName);
		assertTrue("Missing viewer config XML override file (" + vcOverrideName + ").", vcOverride.exists());
		
		// Startup the application
		Viewer.appState.setState(AppState.STARTUP);
		Viewer.createAndShowGUI(new String[]{ "configurationFileTarget="+vcOverride.getAbsolutePath() });
		
		// Pick a JAR for confirming whether all JARs remain unlocked
		String sourceJarName = null;
		Map classPathsByMask = Viewer.viewerConfig.getTargetAppClasspathPathsByMask();
		if (classPathsByMask != null) {
			Iterator it = classPathsByMask.keySet().iterator();
			if (it.hasNext()) {
				String classPathMask = (String)it.next();
				List classPaths = (List)classPathsByMask.get(classPathMask);
				if (classPaths != null && classPaths.size() > 0) {
					sourceJarName = ((File)classPaths.get(0)).getAbsolutePath();
				}
			}
		}
		assertNotNull("No source JAR found to use.", sourceJarName);
		File sourceJar = new File(sourceJarName);
		assertTrue("Source JAR (" + sourceJarName + ") does not exist.", sourceJar.exists());
		String targetJarName = sourceJarName.replaceFirst("[.]jar", "!.jar");
		assertNotSame("Source and Target JAR names are the same!?", sourceJarName, targetJarName);
		File targetJar = new File(targetJarName);
		assertFalse("Target JAR (" + targetJarName + ") already exists!", targetJar.exists());
		
		// Confirm that our target JAR is unlocked
//		Thread.sleep(3000);
		boolean renamed = sourceJar.renameTo(targetJar);
		assertTrue("Rename failed, source JAR (" + sourceJarName + ") must be locked.", renamed);
		// ..Reset
		boolean reset = targetJar.renameTo(sourceJar);
		assertTrue("Reset failed, target JAR (" + targetJarName + ") must be locked.", reset);
		
		// Reload the configuration
		Viewer.appState.setState(AppState.RESTART);
		Viewer.createAndShowGUI(new String[0]);
		
		// Confirm that our target JAR is unlocked (still)
//		Thread.sleep(3000);
		renamed = sourceJar.renameTo(targetJar);
		assertTrue("Rename failed, source JAR (" + sourceJarName + ") must be locked.", renamed);
		// ..Reset
		reset = targetJar.renameTo(sourceJar);
		assertTrue("Reset failed, target JAR (" + targetJarName + ") must be locked.", reset);
		
//		// Shutdown the application
//		Viewer.appState.setState(AppState.SHUTDOWN);
		
		logger.debug("unlockedJarsTest END");
	}
	
}
