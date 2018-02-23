package view;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Custom Log4j message appender which writes to both java swing component and sysOut/sysErr.
 * 
 * @author TAYLOJ10
 * @see log4j.properties
 */
public class CustomConsoleAppender extends AppenderSkeleton {

	/**
	 * Define level --> color mappings.
	 */
	private static Map<String, Color> LEVEL_COLORS = null;
	static {
		LEVEL_COLORS = new HashMap<String, Color>();
		LEVEL_COLORS.put(Level.FATAL.toString(), Color.MAGENTA);
		LEVEL_COLORS.put(Level.ERROR.toString(), Color.RED);
		LEVEL_COLORS.put(Level.WARN.toString() , Color.PINK);
		LEVEL_COLORS.put(Level.INFO.toString() , Color.BLACK);
		LEVEL_COLORS.put(Level.DEBUG.toString(), Color.GRAY);
		LEVEL_COLORS.put(Level.TRACE.toString(), Color.LIGHT_GRAY);
	}

	/**
	 * Cache level --> attribute sets.
	 */
	private static Map<String, AttributeSet> LEVEL_ATTRS = new HashMap<String, AttributeSet>();

	/**
	 * Container for the log4j.properties file.
	 */
	private static Properties LOG4J_PROPERTIES = null;
	private static Properties getLog4jProperties() {
		if (LOG4J_PROPERTIES == null) {
			final String LOG4J_PROPERTIES_FILE = "/log4j.properties";
			Properties properties = new Properties();
			InputStream inputStream = null;
			try {
				inputStream = CustomConsoleAppender.class.getResourceAsStream(LOG4J_PROPERTIES_FILE);
				properties.load(inputStream);
				LOG4J_PROPERTIES = properties;
			} catch (IOException e) {
				System.err.println("Error: Cannot load '" + LOG4J_PROPERTIES_FILE + "' file ");
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return LOG4J_PROPERTIES;
	}

	/**
	 * Container for the ConversionPattern (from the log4j.properties).
	 */
	private static String CONVERSION_PATTERN = null;
	private static String getConversionPattern() {
		if (CONVERSION_PATTERN == null) {
			CONVERSION_PATTERN = getLog4jProperties().getProperty("log4j.appender.console.layout.ConversionPattern");
		}
		return CONVERSION_PATTERN;
	}

	/**
	 * Container for the PatternLayout (based on the ConversionPattern).
	 */
	private static PatternLayout PATTERN_LAYOUT = null;
	private static PatternLayout getPatternLayout() {
		if (PATTERN_LAYOUT == null) {
			PATTERN_LAYOUT = new PatternLayout(getConversionPattern());
		}
		return PATTERN_LAYOUT;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (event.getLogger().isEnabledFor(event.getLevel())) {
			// Obtain attribute set for the current message
			AttributeSet attributeSet = null;
			if (!LEVEL_ATTRS.containsKey(event.getLevel().toString())) {
				// Determine level-specific color
				Color color = LEVEL_COLORS.get(event.getLevel().toString());
				if (color == null) {
					color = Color.BLACK;
				}

				// Determine (font) attributes for the current message
				StyleContext styleContext = StyleContext.getDefaultStyleContext();
		        attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
		        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontFamily, "Lucida Console");
		        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		        LEVEL_ATTRS.put(event.getLevel().toString(), attributeSet);
			}
			attributeSet = LEVEL_ATTRS.get(event.getLevel().toString());

			// Write message to the java swing component
			String message = getPatternLayout().format(event);
			JTextPane console = Viewer.getConsole();
			console.setCaretPosition(console.getDocument().getLength());
			//console.setCharacterAttributes(attributeSet, false);
			//console.replaceSelection(message);
			try {
				console.getDocument().insertString(console.getDocument().getLength(), message, attributeSet);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			// Write message to sysOut/sysErr
			if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
				System.err.append(message);
			} else {
				System.out.append(message);
			}
		}
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

}
