package controller;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import view.Viewer;


/**
 * Respond to keys being pressed/released/typed in the "Filter By" component(s)
 * on each tab of the Viewer.
 */
public class FilterByKeyListener implements KeyListener {
	
	/***************
	 * Constant(s) *
	 ***************/
	
	private static final char[] CHARACTERS_TO_SUPPRESS = { '(', ')', '{', '}', '[', ']', '<', '>' };
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("controller");
	
	/**
	 * Viewer instance.
	 */
	private Viewer viewer;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public FilterByKeyListener() {}
	public FilterByKeyListener(Viewer viewer) {
		this.viewer = viewer;
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public Viewer getViewer() {
		return this.viewer;
	}
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Respond to a 'keyPressed' event.
	 * <p>
	 *  NOTE: Aside from potential character suppression,
	 *        currently nothing is done on 'keyPressed', instead,
	 *        a response is only generated on 'keyReleased'. 
	 * </p>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see #keyReleased(KeyEvent)
	 */
	public void keyPressed(KeyEvent event) {
		checkToSuppressCharacter(event);
	}
	
	/**
	 * Respond to a 'keyReleased' event.
	 * <p>
	 *  NOTE: Upon each 'keyReleased' a call to {@link Viewer#groupBy(int, int)}
	 *        is triggered to automatically re-filter the corresponding tabs output.
	 * </p>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see Viewer#groupBy(int, int)
	 */
	public void keyReleased(KeyEvent event) {
		if (!checkToSuppressCharacter(event)) {
			// (Allow character and) Proceed with processing
			Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JTextField source = (JTextField)event.getSource();
			int x = Integer.parseInt(source.getName());
			int y = ((JComboBox)Viewer.getPanelInfoElement(x, Viewer.INFO_GROUP_BY)).getSelectedIndex();
			viewer.groupBy(x, y);
			Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	/**
	 * Respond to a 'keyTyped' event.
	 * <p>
	 *  NOTE: Aside from potential character suppression,
	 *        currently nothing is done on 'keyTyped', instead,
	 *        a response is only generated on 'keyReleased'. 
	 * </p>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see #keyReleased(KeyEvent)
	 */
	public void keyTyped(KeyEvent event) {
		checkToSuppressCharacter(event);
	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Determine whether to suppress the current character.
	 * 
	 * @param event KeyEvent to evaluate.
	 * @return Boolean indicating whether the character is being suppressed.
	 */
	private boolean checkToSuppressCharacter(KeyEvent event) {
		if (ArrayUtils.contains(CHARACTERS_TO_SUPPRESS, event.getKeyChar())) {
			// Suppress certain characters
			event.consume();
		}
		return event.isConsumed();
	}
}
