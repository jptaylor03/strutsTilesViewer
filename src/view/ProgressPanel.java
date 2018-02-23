package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import org.apache.commons.lang.StringUtils;


/**
 * Provides basic JProgressBar functionality and is
 * self-contained w/in in its own JFrame and JPanel.
 */
public class ProgressPanel extends JPanel /*implements PropertyChangeListener*/ {

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
	 * Frame containing the Progress Bar.
	 */
	private JDialog dialog;
	
//	/**
//	 * Panel containing the Progress Bar.
//	 */
//	private ProgressPanel progressPanel;
	
	/**
	 * Progress Bar instance.
	 */
	private JProgressBar progressBar;
	
	/**
	 * Progress Console Panel instance.
	 */
	private JScrollPane consolePanel;
	
	/**
	 * Allows for incrementing the Progress Bars 'value' by a fractional amount.
	 */
	private float value;
	
	/**
	 * Standard amount to increment by.
	 */
	private float incrementValue = 1.0F;

	/******************
	 * Constructor(s) *
	 ******************/
	
	public ProgressPanel(String title, JTextPane console) {
		super(new BorderLayout());
		
		//Create and set up the window.
		dialog = new JDialog(new JFrame(), StringUtils.defaultIfEmpty(title, "Progress"));
	//	dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dialog.setVisible(false);
			}
		});

		dialog.setResizable(false);
	//	dialog.setUndecorated(true);
		dialog.setContentPane(this);

//		//Create and set up the content pane.
//		progressPanel = this;
//		progressPanel.setOpaque(true); //content panes must be opaque
//		progressPanel.setPreferredSize(new Dimension(200, 75));
//		//..Center the content pane
//		//....Get the size of the screen
//		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		//....Determine the new location of the window
//		int w = progressPanel.getSize().width;
//		int h = progressPanel.getSize().height;
//		int x = (dim.width-w)/2;
//		int y = (dim.height-h)/2;
//		//....Move the window
//		progressPanel.setLocation(x, y);
		
		//Create and set up the progress bar
		progressBar = new JProgressBar();
		progressBar.setValue(0);

		//Call setStringPainted now so that the progress bar height
		//stays the same whether or not the string is shown.
		progressBar.setStringPainted(true); 

		JPanel progressPanel = new JPanel(new FlowLayout());
		progressPanel.add(progressBar);
		
		consolePanel = new JScrollPane(Viewer.getConsole());
		consolePanel.setPreferredSize(new Dimension(1024, 768));
		consolePanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		consolePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		consolePanel.setWheelScrollingEnabled(false);
		
		add(progressPanel, BorderLayout.PAGE_START);
		add(consolePanel, BorderLayout.PAGE_END);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	}
	
	public ProgressPanel(String title, JTextPane console, int min, int max) {
		this(title, console);
		setMinimum(min);
		setMaximum(max);
	}
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public boolean isIndeterminate() {
		return progressBar.isIndeterminate();
	}
	public void setIndeterminate(boolean indeterminate) {
		progressBar.setIndeterminate(indeterminate);
	}
	
	public int getMinimum() {
		return progressBar.getMinimum();
	}
	public void setMinimum(int min) {
		progressBar.setMinimum(min);
	}
	
	public int getMaximum() {
		return progressBar.getMaximum();
	}
	public void setMaximum(int max) {
		progressBar.setMaximum(max);
	}
	
	public float getValue() {
		return this.value;
	}
	public void setValue(float value) {
		this.value = value;
		progressBar.setValue((int)value);
	}
	
	public float getIncrementValue() {
		return incrementValue;
	}
	public void setIncrementValue(float incrementValue) {
		this.incrementValue = incrementValue;
	}

	/********************
	 * Member method(s) *
	 ********************/
	
//	/**
//	 * Invoked when task's progress property changes.
//	 */
//	public void propertyChange(PropertyChangeEvent evt) {
//		int progress = Integer.parseInt(""+evt.getNewValue());
//		progressBar.setIndeterminate(false);
//		progressBar.setValue(progress);
//	}

	/**
	 * Create the GUI and show it. As with all GUI code, this must run
	 * on the event-dispatching thread.
	 */
	public void createAndShowGUI() {
		//Display the window.
		dialog.pack();
		dialog.setVisible(true);
	}
	
	public void increment() {
		this.increment(this.incrementValue);
	}
	
	public void increment(float value) {
		this.setValue(this.value += (float)value);
	}
	
	public void hideGUI() {
		//Hide the window.
		dialog.setVisible(false);
	}

}