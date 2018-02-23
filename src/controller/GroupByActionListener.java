package controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import view.Viewer;


/**
 * Respond to combo-box selection changes in the "Group By" component(s)
 * on each tab of the Viewer.
 */
public class GroupByActionListener implements ActionListener {

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
	
	public GroupByActionListener() {}
	public GroupByActionListener(Viewer viewer) {
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
	 * Respond to an 'actionPerformed' event related to a combo-box selection change.
	 * <p>
	 *  NOTE: Upon each 'actionPerformed' a call to {@link Viewer#groupBy(int, int)}
	 *        is triggered to automatically re-group the corresponding tabs output.
	 * </p>
	 * 
	 * @param event ActionEvent that has occurred on the component.
	 * @see Viewer#groupBy(int, int)
	 */
	public void actionPerformed(ActionEvent event) {
		Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		JComboBox source = (JComboBox)event.getSource();
		int x = Integer.parseInt(source.getName());
		int y = ((JComboBox)Viewer.getPanelInfoElement(x, Viewer.INFO_GROUP_BY)).getSelectedIndex();
		viewer.groupBy(x, y);
		Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
