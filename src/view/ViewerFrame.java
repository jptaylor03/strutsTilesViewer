package view;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import model.AppState;


/**
 * Extends <code>JFrame</code> and implements <code>WindowListener</code>
 * in order to provide custom behavior.
 */
public class ViewerFrame extends JFrame implements WindowListener {

	/***************
	 * Constant(s) *
	 ***************/
	
	/**
	 * Serializable.
	 */
	private static final long serialVersionUID = 1L;
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("view");
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/*
	 * WindowListener-specific method(s)
	 */
	
	public void windowOpened(WindowEvent e) {
		logger.info("windowOpened()");
	}

	public void windowClosing(WindowEvent e) {
		logger.info("windowClosing()");
		// TODO[jpt] Determine why this event (or any Window event in this class) is never triggered
		if (this.isDisplayable()) {
			this.dispose();
		}
		Viewer.appState.setState(AppState.SHUTDOWN);
	}

	public void windowClosed(WindowEvent e) {
		logger.info("windowClosed()");
	}

	public void windowIconified(WindowEvent e) {
		logger.info("windowIconified()");
	}

	public void windowDeiconified(WindowEvent e) {
		logger.info("windowDeiconified()");
	}

	public void windowActivated(WindowEvent e) {
		logger.info("windowActivated()");
	}

	public void windowDeactivated(WindowEvent e) {
		logger.info("windowDeactivated()");
	}
	
}
