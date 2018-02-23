package controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.AppState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import view.Viewer;


/**
 * Respond to button pressing of the "Reload Configuration" component
 * on the Configuration tab of the Viewer.
 */
public class RefreshButtonActionListener implements ActionListener {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("controller");
	
//	/**
//	 * Viewer instance.
//	 */
//	private Viewer viewer;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public RefreshButtonActionListener() {}
//	public RefreshButtonActionListener(Viewer viewer) {
//		this.viewer = viewer;
//	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
//	public Viewer getViewer() {
//		return this.viewer;
//	}
//	public void setViewer(Viewer viewer) {
//		this.viewer = viewer;
//	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Respond to an 'actionPerformed' event related to a command button click.
	 * <p>
	 *  NOTE: Upon each 'actionPerformed' a call is made to set the {@link Viewer#appState}.
	 *        The {@link Viewer#main(String[])} method is designed to loop indefinitely
	 *        and respond to {@link Viewer#appState} changes.  Therefore, to respond to a
	 *        Reload Configuration button pressed event, this method only needs to update
	 *        the {@link Viewer#appState} to {@link AppState#RESTART}.
	 * </p>
	 * 
	 * @param event ActionEvent that has occurred on the component.
	 * @see Viewer#appState
	 */
	public void actionPerformed(final ActionEvent event) {
		Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Viewer.appState.setState(AppState.RESTART);
		Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
