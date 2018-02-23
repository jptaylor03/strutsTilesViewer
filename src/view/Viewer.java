package view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.tree.TreeSelectionModel;

import model.AppState;
import model.NodeInfo;
import model.SimpleTreeModel;
import model.SimpleTreeNode;
import model.StrutsActionFormBean;
import model.StrutsActionForward;
import model.StrutsActionMapping;
import model.TilesDefinition;
import model.ViewerConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import util.MiscUtils;
import util.MissingResources;
import util.ModelUtils;
import util.ReflectUtils;
import util.TreeUtils;
import concurrent.ParseDocumentJob;
import concurrent.ThreadPoolFactory;
import controller.FilterByKeyListener;
import controller.GroupByActionListener;
import controller.RefreshButtonActionListener;
import controller.TreeKeyListener;
import controller.TreeMouseListener;


/**
 * Main class for the application.
 *
 * TODO[jpt] Convert from Swing Application to an Eclipse Plug-in
 */
public class Viewer extends JPanel implements ClipboardOwner {

	/***************
	 * Constant(s) *
	 ***************/

	/**
	 * Serializable.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Object array containing basic information about the application.
	 */
	public static final Object[] appInfo = {
		"Struts/Tiles Viewer"			// [0] == Application Name
	,	"2.04"							// [1] == Application Version
	,	"08/20/2015"					// [2] == Application Build Date
	,	"james.p.taylor@jfs.ohio.gov"	// [3] == Application Author Contact
	,	Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("view/resources/images/struts.png")) // [4] == Application Icon
	};

	/**
	 * Obtain formatted version of the application info.
	 *
	 * @return String containing application info formatted for display.
	 */
	public static String getAppInfoFormatted() {
		return	"Name: "	+ appInfo[0] + "\n" +
				"Version: "	+ appInfo[1] + "\n" +
				"Build: "	+ appInfo[2] + "\n" +
				"Author: "	+ appInfo[3] + "\n";
	}

	/*
	 * Panels
	 */

	// TODO[jpt] Convert all INFO constants from 'int' to 'byte'.
	// Panel Fields
	public static final int INFO_ID        = 0;
	public static final int INFO_NAME      = 1;
	public static final int INFO_ICON      = 2;
	public static final int INFO_ROOT      = 3;
	public static final int INFO_TREE      = 4;
	public static final int INFO_FILTER_BY = 5;
	public static final int INFO_GROUP_BY  = 6;
	// Panel Types
	public static final int INFO_ID_VIEWER_CONFIG  = 0;
	public static final int INFO_ID_VIEWER_CONSOLE = 1;
	public static final int INFO_ID_SAFB           = 2;
	public static final int INFO_ID_SAF            = 3;
	public static final int INFO_ID_SAM            = 4;
	public static final int INFO_ID_TD             = 5;
	// Panels
	// TODO[jpt] Add a Struts Global Exceptions tab
	private static final Object[][] PANEL_INFO = {
	//		  [0]id                   [1]name              [2]icon  [3]root [4]tree  [5]filterBy  [6]groupBy
/*[0] */	{ "viewerConfig"        , "Viewer Config"    , "xml"  , null   , null   , null       , null      } /*[0]*/
/*[1] */,	{ "viewerConsole"       , "Viewer Console"   , "out"  , null   , null   , null       , null      } /*[1]*/
/*[2] */,	{ "strutsActionForms"   , "Action Form Beans", "safb" , null   , null   , null       , null      } /*[2]*/
/*[3] */,	{ "strutsActionForwards", "Action Forwards"  , "saf"  , null   , null   , null       , null      } /*[3]*/
/*[4] */,	{ "strutsActionMapping" , "Action Mappings"  , "sam"  , null   , null   , null       , null      } /*[4]*/
/*[5] */,	{ "tilesDefinitions"    , "Tiles Definitions", "td"   , null   , null   , null       , null      } /*[5]*/
	//		  [0]id                   [1]name              [2]icon  [3]root [4]tree  [5]filterBy  [6]groupBy
	};

	/*
	 * Group By
	 */

	// TODO[jpt] Convert all GROUP_BY constants from 'int' to 'byte'.
	// Struts Action Form Beans
	public static final int GROUP_BY_SAFB_BY_NAME               = 0; // Default
	public static final int GROUP_BY_SAFB_BY_TYPE               = 1;
	// Struts Action Forwards
	public static final int GROUP_BY_SAF_BY_NAME                = 0;
	public static final int GROUP_BY_SAF_BY_PATH                = 1;
	public static final int GROUP_BY_SAF_BY_UNIQUE              = 2; // Default
	// Struts Action Mappings
	public static final int GROUP_BY_SAM_BY_SAF_NAME            = 0;
	public static final int GROUP_BY_SAM_BY_SAF_PATH            = 1;
	public static final int GROUP_BY_SAM_BY_NAME                = 2;
	public static final int GROUP_BY_SAM_BY_PATH                = 3; // Default
	public static final int GROUP_BY_SAM_BY_TYPE                = 4;
	// Tiles Definitions
	public static final int GROUP_BY_TD_BY_ATTR_PAGE_WORK       = 0;
	public static final int GROUP_BY_TD_BY_ATTR_PAGE_WORK_TITLE = 1;
	public static final int GROUP_BY_TD_BY_EXTENDS              = 2;
	public static final int GROUP_BY_TD_BY_NAME                 = 3; // Default

//	/*
//	 * Branch Label
//	 */
//
//	private static final int BRANCH_LABEL_STATIC_TEXT   = 0;
//	private static final int BRANCH_LABEL_KEY_AND_VALUE = 1;
//	private static final int BRANCH_LABEL_VALUE_ONLY    = 2;
//	private static final int BRANCH_LABEL_CUSTOM        = 3;

	/**********************
	 * Member variable(s) *
	 **********************/

	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("view");

	/**
	 * AppState instance for maintaining the state of the application.
	 */
	public static final AppState appState = new AppState();

	/**
	 * Map of command-line arguments (keyed by argument name).
	 */
	private static final Map<String, String> commandLineArgs = new HashMap<String, String>();

	/**
	 * ViewerFrame instance containing a reference to the frame used by this panel.
	 */
	public  static ViewerFrame frame = new ViewerFrame();

	/**
	 * Viewer instance of this class.
	 */
	private static Viewer viewer = null;

	/**
	 * ProgressPanel instance.
	 */
	private static ProgressPanel progressPanel = null;

	/**
	 * Console (output) instance.
	 */
	private static JTextPane console = null;
	public static JTextPane getConsole() {
		if (console == null) {
			// Create the console output area and associate it with SysOut/SysErr
			console = new JTextPane(); //JTextArea(44, 88)
			//console.setPreferredSize(new Dimension(1024, 768));
			//console.setMinimumSize(new Dimension(800, 600));
			console.setEditable(false);
			//PrintStream stream = new PrintStream(new CustomOutputStream(console));
			//System.setOut(stream);
			//System.setErr(stream);
		}
		return console;
	}

	/**
	 * Name of the primary configuration file for the application.
	 */
	private static String viewerConfigFileName = "viewer-config.xml";

	/**
	 * Path (and name) of the primary configuration file for the application.
	 */
	private static String viewerConfigFileTarget = "resources/" + viewerConfigFileName;

	/**
	 * Container for the (parsed) xml configuration from the "viewer-config.xml" file.
	 */
	public static ViewerConfig viewerConfig = null;

	/*
	 * Special Indices
	 */

	/**
	 * Map of Struts Action Mappings keyed by Struts Action Forward Name.
	 *
	 * @see #parseSpecifiedFiles()
	 * @see #groupBy(int, int)
	 */
	private static final Map<String, SimpleTreeNode> samBySafName = new TreeMap<String, SimpleTreeNode>();

	/**
	 * Map of Struts Action Mappings keyed by Struts Action Forward Path.
	 *
	 * @see #parseSpecifiedFiles()
	 * @see #groupBy(int, int)
	 */
	private static final Map<String, SimpleTreeNode> samBySafPath = new TreeMap<String, SimpleTreeNode>();

	/******************
	 * Constructor(s) *
	 ******************/

	public Viewer() {
		super(new BorderLayout());
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/

	public static ViewerConfig getViewerConfig() {
		return viewerConfig;
	}
//	public static void setViewerConfig(ViewerConfig viewerConfig) {
//		Viewer.viewerConfig = viewerConfig;
//	}

	public static Object[][] getPanelInfo() {
		return PANEL_INFO;
	}
//	public static void setPanelInfo(Object[][] panelInfo) {
//		PANEL_INFO = panelInfo;
//	}

	public static Object getPanelInfoElement(int infoField) {
		return PANEL_INFO[infoField];
	}
	public static void setPanelInfoElement(Object[] element, int infoField) {
		PANEL_INFO[infoField] = element;
	}

	public static Object getPanelInfoElement(int infoField, int infoType) {
		return PANEL_INFO[infoField][infoType];
	}
	public static void setPanelInfoElement(Object element, int infoField, int infoType) {
		PANEL_INFO[infoField][infoType] = element;
	}

	/********************
	 * Member method(s) *
	 ********************/

	/**
	 * Primary entry-point into this class (and the application).
	 *
	 * @param args String array containing any/all command-line arguments.
	 */
	public static void main(final String[] args) {
//		// Schedule a job for the event-dispatching thread:
//		// creating and showing this application's GUI.
//		javax.swing.SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
				while (!appState.is(AppState.SHUTDOWN)) {
					if (appState.is(AppState.STARTUP) || appState.is(AppState.RESTART)) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						createAndShowGUI(args);
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						appState.setState(AppState.ACTIVE);
					}
					try {
						Thread.sleep(1000);
					} catch (Throwable t) {
						logger.error(t);
					}
				}
				appState.setState(AppState.INACTIVE);
//			}
//		});
	}

	/**
	 * Create the GUI and show it.
	 * <p>
	 *  NOTE: For thread safety, this method should be invoked
	 *        from the event-dispatching thread.
	 * </p>
	 *
	 * @param args String array containing any/all command-line arguments.
	 */
	public static void createAndShowGUI(final String[] args) {
		// Create and set up the window
		if (StringUtils.isEmpty(frame.getTitle())) {
			frame.setTitle(appInfo[0]+"");
			frame.setVisible(false);
		//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					frame.setVisible(false);
					// Perform any other operations you might need before exit.
					System.exit(0);
				}
			});
		}

		// Begin creating and setting up the content pane
		if (viewer == null) {
			viewer = new Viewer();
			viewer.setOpaque(true); // Content pane(s) must be opaque
		}

		// Initialize the Progress Bar
		/*
		 * Determine the total # of Progress Bar tasks
		 * [0] == parseCommandLineOptions()
		 * [1] == loadConfiguration()
		 * [2] == parseSpecifiedFiles()/main
		 * [3] == parseSpecifiedFiles()/alternateIndices
		 * [4] == prepareViewer()
		 * [5] == releaseResources()
		 * [6] == logMissingResources()
		 */
		int[] progressTasks = new int[7];
		// [0] == parseCommandLineOptions()
		progressTasks[0] = 1;
		// [1] == loadConfiguration()
		progressTasks[1] = 1;
		// [2] == parseSpecifiedFiles()/main
		progressTasks[2] = 0; // Must wait until configuration has been loaded
    	// [3] == parseSpecifiedFiles()/alternateIndices
    	progressTasks[3] = 1;
		// [4] == prepareViewer()
		progressTasks[4] = 1;
		// [5] == releaseResources()
		progressTasks[5] = 1;
		// [6] == logMissingResources()
		progressTasks[6] = 1;
		int progressTotalTasks = 0;
		for (int x = 0; x < progressTasks.length; x++) {
			progressTotalTasks += progressTasks[x];
		}

		// Create/show the Progress Bar
		progressPanel = new ProgressPanel("Loading...", Viewer.getConsole(), 0, progressTotalTasks);
		progressPanel.setIndeterminate(true);
		progressPanel.createAndShowGUI();

		// Calculate the increment amount based on the total # of Progress Bar tasks
	//	progressPanel.setIncrementValue((float)100 / (float)progressTotalTasks);
		progressPanel.setIncrementValue(1.0F);

		logger.debug("progressTotalTasks: "                + progressTotalTasks);
		logger.debug("progressPanel.getIncrementValue(): " + progressPanel.getIncrementValue());
		logger.debug("progressPanel.getMinimum(): "        + progressPanel.getMinimum());
		logger.debug("progressPanel.getMaximum(): "        + progressPanel.getMaximum());

		// Finish creating and setting up the content pane
		viewer.parseCommandLineArguments(args);	progressPanel.increment(progressTasks[0]);
		viewer.parseViewerConfiguration();		progressPanel.increment(progressTasks[1]);
		progressPanel.setMaximum(progressTotalTasks += progressTasks[2] = getCountOfSpecifiedFiles());
		progressPanel.setIndeterminate(false);
		viewer.parseSpecifiedFiles();			progressPanel.increment(progressTasks[3]); // NOTE: [2] incremented w/in parseSpecifiedFiles()
		viewer.prepareViewer();					progressPanel.increment(progressTasks[4]);
		ReflectUtils.releaseResources();		progressPanel.increment(progressTasks[5]);
		MissingResources.logMissingResources();	progressPanel.increment(progressTasks[6]);

		// TODO[jpt] Determine why Progress Panel's Progress Bar requires percentages (even after setting min/max)
		logger.debug("progressPanel.getValue(): " + progressPanel.getValue());
		logger.debug("progressPanel.getMinimum(): " + progressPanel.getMinimum());
		logger.debug("progressPanel.getMaximum(): " + progressPanel.getMaximum());

		// Hide the Progress Bar
		progressPanel.hideGUI();

		frame.setContentPane(viewer);
		frame.pack();
		frame.setVisible(true);
		//frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setIconImage((Image)appInfo[4]);
	}

	/**
	 * [Re]Group the data contents of the tree
	 * on the specified 'panelIndex' by the specified 'groupByIndex'.
	 *
	 * @param panelIndex   Integer identifying the panel containing the tree to update.
	 * @param groupByIndex Integer identifying the type of group by to perform.
	 */
    @SuppressWarnings("unchecked")
	public void groupBy(int panelIndex, int groupByIndex) {
    	final String MISSING_GROUP_BY_KEY = "(undefined)";
    	String                      groupByKey     = null;
    	List<Object>                groupByList    = null;
    	Map<String, SimpleTreeNode> groupByDefault = (Map<String, SimpleTreeNode>)((SimpleTreeNode)PANEL_INFO[panelIndex][INFO_ROOT]).getUserObject();
    	Map<String, SimpleTreeNode> groupByResult  = new TreeMap<String, SimpleTreeNode>();
    	SimpleTreeNode              groupByNode[]  = { null, null };

		// Create new root node based on the old one
		SimpleTreeNode oldRoot = (SimpleTreeNode)PANEL_INFO[panelIndex][INFO_ROOT];
		SimpleTreeNode newRoot = new SimpleTreeNode("root", null, groupByResult, null, null, oldRoot.getInfo());

		switch (panelIndex) {
			case INFO_ID_VIEWER_CONFIG:
				// The default "aggregate" for this panel
				groupByResult.putAll(groupByDefault);
				break;
			case INFO_ID_SAFB:
				switch (groupByIndex) {
					case GROUP_BY_SAFB_BY_NAME: {
						// The default "aggregate" for this panel
						groupByResult.putAll(groupByDefault);
						break;
					}
					case GROUP_BY_SAFB_BY_TYPE: {
						for (Iterator<String> it = groupByDefault.keySet().iterator(); it.hasNext();) {
							String               key   = (String)it.next();
							StrutsActionFormBean value = (StrutsActionFormBean)((SimpleTreeNode)groupByDefault.get(key)).getUserObject();
							groupByKey     = StringUtils.defaultString(value.getType(), MISSING_GROUP_BY_KEY);
							groupByNode[0] = (SimpleTreeNode)groupByResult.get(groupByKey);
							if (groupByNode[0] == null) {
								groupByNode[0] = new SimpleTreeNode(groupByKey, newRoot, null);
							}
							groupByList = (ArrayList<Object>)groupByNode[0].getUserObject();
							if (groupByList == null) {
								groupByList = new ArrayList<Object>();
							}
							groupByNode[1] = new SimpleTreeNode(value.getName(), groupByNode[0], value, "safb");
							if (!groupByList.contains(groupByNode[1])) {
								groupByList.add(groupByNode[1]);
							}
							groupByNode[0].setUserObject(groupByList);
							groupByResult.put(groupByKey, groupByNode[0]);
						}
						break;
					}
				}
				break;
			case INFO_ID_SAF:
				switch (groupByIndex) {
					case GROUP_BY_SAF_BY_NAME:
						groupByResult.putAll(samBySafName);
						break;
					case GROUP_BY_SAF_BY_PATH:
						groupByResult.putAll(samBySafPath);
						break;
					case GROUP_BY_SAF_BY_UNIQUE:
						// The default "aggregate" for this panel
						groupByResult.putAll(groupByDefault);
						break;
				}
				break;
			case INFO_ID_SAM:
				switch (groupByIndex) {
					case GROUP_BY_SAM_BY_SAF_NAME:
					case GROUP_BY_SAM_BY_SAF_PATH: {
						for (Iterator<String> it = groupByDefault.keySet().iterator(); it.hasNext();) {
							String key = (String)it.next();
							StrutsActionMapping value = (StrutsActionMapping)((SimpleTreeNode)groupByDefault.get(key)).getUserObject();
							Map actionForwards = value.getActionForwards();
							for (Iterator itSub = actionForwards.values().iterator(); itSub.hasNext();) {
								StrutsActionForward saf = (StrutsActionForward)((SimpleTreeNode)itSub.next()).getUserObject();
								switch (groupByIndex) {
									case GROUP_BY_SAM_BY_SAF_NAME:
										groupByKey = StringUtils.defaultString(saf.getName(), MISSING_GROUP_BY_KEY);
										break;
									case GROUP_BY_SAM_BY_SAF_PATH:
										groupByKey = StringUtils.defaultString(saf.getPath(), MISSING_GROUP_BY_KEY);
										break;
								}
								groupByNode[0] = (SimpleTreeNode)groupByResult.get(groupByKey);
								if (groupByNode[0] == null) {
									groupByNode[0] = new SimpleTreeNode(groupByKey, newRoot, null);
								}
								groupByList = (ArrayList)groupByNode[0].getUserObject();
								if (groupByList == null) {
									groupByList = new ArrayList();
								}
								groupByNode[1] = new SimpleTreeNode(value.getPath(), groupByNode[0], value, "sam");
								if (!groupByList.contains(groupByNode[1])) {
									groupByList.add(groupByNode[1]);
								}
								groupByNode[0].setUserObject(groupByList);
								groupByResult.put(groupByKey, groupByNode[0]);
							}
						}
						break;
					}
					case GROUP_BY_SAM_BY_NAME:
					case GROUP_BY_SAM_BY_TYPE: {
						for (Iterator it = groupByDefault.keySet().iterator(); it.hasNext();) {
							String key = (String)it.next();
							StrutsActionMapping value = (StrutsActionMapping)((SimpleTreeNode)groupByDefault.get(key)).getUserObject();
							switch (groupByIndex) {
								case GROUP_BY_SAM_BY_NAME:
									groupByKey = StringUtils.defaultString(value.getName(), MISSING_GROUP_BY_KEY);
									break;
								case GROUP_BY_SAM_BY_TYPE:
									groupByKey = StringUtils.defaultString(value.getType(), MISSING_GROUP_BY_KEY);
									break;
							}
							groupByNode[0] = (SimpleTreeNode)groupByResult.get(groupByKey);
							if (groupByNode[0] == null) {
								groupByNode[0] = new SimpleTreeNode(groupByKey, newRoot, null);
							}
							groupByList = (ArrayList)groupByNode[0].getUserObject();
							if (groupByList == null) {
								groupByList = new ArrayList();
							}
							groupByNode[1] = new SimpleTreeNode(value.getPath(), groupByNode[0], value, "sam");
							if (!groupByList.contains(groupByNode[1])) {
								groupByList.add(groupByNode[1]);
							}
							groupByNode[0].setUserObject(groupByList);
							groupByResult.put(groupByKey, groupByNode[0]);
						}
						break;
					}
					case GROUP_BY_SAM_BY_PATH: {
						// The default "aggregate" for this panel
						groupByResult.putAll(groupByDefault);
						break;
					}
				}
				break;
			case INFO_ID_TD:
				switch (groupByIndex) {
					case GROUP_BY_TD_BY_ATTR_PAGE_WORK:
					case GROUP_BY_TD_BY_ATTR_PAGE_WORK_TITLE:
					case GROUP_BY_TD_BY_EXTENDS: {
						for (Iterator it = groupByDefault.keySet().iterator(); it.hasNext();) {
							String          key       = (String)it.next();
							TilesDefinition value     = (TilesDefinition)((SimpleTreeNode)groupByDefault.get(key)).getUserObject();
							String          attrValue = null;
							switch (groupByIndex) {
								case GROUP_BY_TD_BY_ATTR_PAGE_WORK:
									attrValue  = value.getAttributeValue(TilesDefinition.ATTR_PAGE_WORK);
									groupByKey = StringUtils.defaultString(attrValue, MISSING_GROUP_BY_KEY);
									break;
								case GROUP_BY_TD_BY_ATTR_PAGE_WORK_TITLE:
									attrValue  = value.getAttributeValue(TilesDefinition.ATTR_PAGE_WORK_TITLE);
									groupByKey = StringUtils.defaultString(attrValue, MISSING_GROUP_BY_KEY);
									break;
								case GROUP_BY_TD_BY_EXTENDS:
									groupByKey = StringUtils.defaultString((String)value.getExtends(), MISSING_GROUP_BY_KEY);
									break;
							}
							groupByNode[0] = (SimpleTreeNode)groupByResult.get(groupByKey);
							if (groupByNode[0] == null) {
								groupByNode[0] = new SimpleTreeNode(groupByKey, newRoot, null);
							}
							groupByList = (ArrayList)groupByNode[0].getUserObject();
							if (groupByList == null) {
								groupByList = new ArrayList();
							}
							groupByNode[1] = new SimpleTreeNode(value.getName(), groupByNode[0], value, "td");
							if (!groupByList.contains(groupByNode[1])) {
								groupByList.add(groupByNode[1]);
							}
							groupByNode[0].setUserObject(groupByList);
							groupByResult.put(groupByKey, groupByNode[0]);
						}
						break;
					}
					case GROUP_BY_TD_BY_NAME: {
						// The default "aggregate" for this panel
						groupByResult.putAll(groupByDefault);
						break;
					}
				}
				break;
		}

		this.filterByRegex(groupByResult, ((JTextField)PANEL_INFO[panelIndex][INFO_FILTER_BY]).getText());

		// Update the appropriate Tree Model and force a refresh
		JTree tree = (JTree)PANEL_INFO[panelIndex][INFO_TREE];
		tree.setModel(new SimpleTreeModel(newRoot));
		tree.invalidate();
    }

//	public void createTree(int panelIndex, int groupByIndex, String regex, Map map) {
//		Pattern regexPattern = Pattern.compile(".*" + StringUtils.defaultString(regex) + ".*");
//		SimpleTreeNode root = new SimpleTreeNode(new NodeInfo("root", null, null));
//
//		JTree tree = (JTree)PANEL_INFO[panelIndex][INFO_TREE];
//		tree.removeAll();
//		tree.setModel(null);
//		System.gc();
//
//		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
//			String key   = (String)it.next();
//			Object value = map.get(key);
//			// Skip this key if it doesn't match the pattern
//			if (!regexPattern.matcher(key).matches()) {
//				continue;
//			}
//			switch (panelIndex) {
//			case INFO_ID_VIEWER_CONFIG: {
//				// "viewer-config" branch
//				ViewerConfig vc = (ViewerConfig)value;
//				SimpleTreeNode configBranch = new SimpleTreeNode(new NodeInfo(key, null, null));
//				root.add(configBranch);
//    			configBranch.add(new SimpleTreeNode(new NodeInfo("targetAppConfigFilePath="+vc.getTargetAppConfigFilePath(), vc.isTargetAppConfigFilePathMissing()?"warn":"folder", vc.getTargetAppConfigFilePath())));
//    			SimpleTreeNode targetAppConfigFileMasksBranchHeader = new SimpleTreeNode(new NodeInfo("Configuration File Mask(s)", null, null));
//    			configBranch.add(targetAppConfigFileMasksBranchHeader);
//    			for (Iterator itTargetAppConfigFileMasks = vc.getTargetAppConfigFileMasks().iterator(); itTargetAppConfigFileMasks.hasNext();) {
//    				String targetAppConfigFileMask = (String)itTargetAppConfigFileMasks.next();
//    				SimpleTreeNode targetAppConfigFileMaskBranch = new SimpleTreeNode(new NodeInfo(targetAppConfigFileMask, null, null));
//    				targetAppConfigFileMasksBranchHeader.add(targetAppConfigFileMaskBranch);
//    				List targetAppConfigFiles = (List)((Map)vc.getTargetAppConfigFilesByMask()).get(targetAppConfigFileMask);
//    				if (targetAppConfigFiles.size() > 0) {
//    					for (Iterator itTargetAppConfigFiles = targetAppConfigFiles.iterator(); itTargetAppConfigFiles.hasNext();) {
//    						File targetAppConfigFile = (File)itTargetAppConfigFiles.next();
//    						targetAppConfigFileMaskBranch.add(new SimpleTreeNode(new NodeInfo(targetAppConfigFile.getName(), "xml", targetAppConfigFile.getAbsolutePath())));
//    					}
//    				}
//    			}
//    			configBranch.add(new SimpleTreeNode(new NodeInfo("targetAppSourceCodeBasePath="+vc.getTargetAppSourceCodeBasePath(), vc.isTargetAppSourceCodeBasePathMissing()?"warn":"folder", vc.getTargetAppSourceCodeBasePath())));
//    			configBranch.add(new SimpleTreeNode(new NodeInfo("targetAppObjectCodeBasePath="+vc.getTargetAppObjectCodeBasePath(), vc.isTargetAppObjectCodeBasePathMissing()?"warn":"folder", vc.getTargetAppObjectCodeBasePath())));
//
//    			configBranch.add(new SimpleTreeNode(new NodeInfo("targetAppObjectCodeClassPathBase="+vc.getTargetAppObjectCodeClassPathBase(), vc.isTargetAppObjectCodeClassPathBaseMissing()?"warn":"folder", vc.getTargetAppObjectCodeClassPathBase())));
//    			SimpleTreeNode targetAppObjectCodeClassPathMasksBranchHeader = new SimpleTreeNode(new NodeInfo("Class Path Mask(s)", null, null));
//    			configBranch.add(targetAppObjectCodeClassPathMasksBranchHeader);
//    			for (Iterator itTargetAppObjectCodeClassPathMasks = vc.getTargetAppObjectCodeClassPathMasks().iterator(); itTargetAppObjectCodeClassPathMasks.hasNext();) {
//    				String targetAppObjectCodeClassPathMask = (String)itTargetAppObjectCodeClassPathMasks.next();
//    				SimpleTreeNode targetAppObjectCodeClassPathMaskBranch = new SimpleTreeNode(new NodeInfo(targetAppObjectCodeClassPathMask, null, null));
//    				targetAppObjectCodeClassPathMasksBranchHeader.add(targetAppObjectCodeClassPathMaskBranch);
//    				List targetAppObjectCodeClassPaths = (List)((Map)vc.getTargetAppObjectCodeClassPathsByMask()).get(targetAppObjectCodeClassPathMask);
//    				if (targetAppObjectCodeClassPaths.size() > 0) {
//    					for (Iterator itTargetAppObjectCodeClassPaths = targetAppObjectCodeClassPaths.iterator(); itTargetAppObjectCodeClassPaths.hasNext();) {
//    						File targetAppObjectCodeClassPath = (File)itTargetAppObjectCodeClassPaths.next();
//    						targetAppObjectCodeClassPathMaskBranch.add(new SimpleTreeNode(new NodeInfo(targetAppObjectCodeClassPath.getName(), null, targetAppObjectCodeClassPath.getAbsolutePath())));
//    					}
//    				}
//    			}
//
//    			configBranch.add(new SimpleTreeNode(new NodeInfo("targetAppBasePackage="+vc.getTargetAppBasePackage(), vc.isTargetAppBasePackageMissing()?"warn":"folder", vc.getTargetAppSourceCodeBasePath() + "/src/" + vc.getTargetAppBasePackage().replaceAll("[.]", "/"))));
//    			configBranch.add(new SimpleTreeNode(new NodeInfo("textEditorExecutable="+vc.getTextEditorExecutable(), null, null)));
//    			configBranch.add(new SimpleTreeNode(new NodeInfo("source="+vc.getConfigFileTarget(), "xml", vc.getConfigFileTarget())));
//
//    			// "viewer-info" branch
//    			SimpleTreeNode infoBranch = new SimpleTreeNode(new NodeInfo("viewer-info", null, null));
//    			root.add(infoBranch);
//    			infoBranch.add(new SimpleTreeNode(new NodeInfo("applicationName="+appInfo[0], null, null)));
//    			infoBranch.add(new SimpleTreeNode(new NodeInfo("applicationVersion=v"+appInfo[1], null, null)));
//    			infoBranch.add(new SimpleTreeNode(new NodeInfo("applicationBuildDate="+appInfo[2], null, null)));
//    			infoBranch.add(new SimpleTreeNode(new NodeInfo("applicationAuthorContact="+appInfo[3], null, null)));
//    			break;
//    		}
//    		case INFO_ID_SAFB: {
//    			switch (groupByIndex) {
//    			case GROUP_BY_SAFB_BY_NAME: {
//    				StrutsActionFormBean safb = (StrutsActionFormBean)value;
//    				SimpleTreeNode safbBranch = this.createBranchSafb(root, key, BRANCH_LABEL_VALUE_ONLY);
//    				List sams = safb.getActionMappings();
//    				if (sams != null && sams.size() > 0) {
//    					SimpleTreeNode samBranchHeader = new SimpleTreeNode(new NodeInfo("Action Mapping(s)", null, null));
//    					safbBranch.add(samBranchHeader);
//    					for (Iterator itSams = sams.iterator(); itSams.hasNext();) {
//    						String samKey = (String)itSams.next();
//    						SimpleTreeNode samBranch = this.createBranchSam(samBranchHeader, samKey, BRANCH_LABEL_VALUE_ONLY);
//    						StrutsActionMapping sam = (StrutsActionMapping)((Map)PANEL_INFO[INFO_ID_SAM][INFO_MAP]).get(samKey);
//    						Map safs = sam.getActionForwards();
//    						if (safs != null && safs.size() > 0) {
//    							SimpleTreeNode safBranchHeader = new SimpleTreeNode(new NodeInfo("Action Forward(s)", null, null));
//    							samBranch.add(safBranchHeader);
//    							for (Iterator itSafs = sam.getActionForwards().values().iterator(); itSafs.hasNext();) {
//    								StrutsActionForward saf = (StrutsActionForward)itSafs.next();
//    								SimpleTreeNode safBranch = this.createBranchSaf(safBranchHeader, sam.getPath() + "@" + saf.getName(), BRANCH_LABEL_VALUE_ONLY);
//    								this.createBranchTd(safBranch, saf.getPath(), BRANCH_LABEL_CUSTOM, "path="+saf.getPath());
//    							}
//    						}
//    					}
//    				}
//    				break;
//    			}
//    			case GROUP_BY_SAFB_BY_TYPE: {
//    				List safbs = (ArrayList)value;
//    				SimpleTreeNode safbBranch = new SimpleTreeNode(new NodeInfo(key, null, null));
//    				root.add(safbBranch);
//    				for (Iterator itSafbs = safbs.iterator(); itSafbs.hasNext();) {
//    					StrutsActionFormBean safb = (StrutsActionFormBean)itSafbs.next();
//    					this.createBranchSafb(safbBranch, safb.getName(), BRANCH_LABEL_VALUE_ONLY);
//    					List sams = safb.getActionMappings();
//    					if (sams != null && sams.size() > 0) {
//    						SimpleTreeNode samBranchHeader = new SimpleTreeNode(new NodeInfo("Action Mapping(s)", null, null));
//    						safbBranch.add(samBranchHeader);
//    						for (Iterator itSams = sams.iterator(); itSams.hasNext();) {
//    							String samKey = (String)itSams.next();
//    							SimpleTreeNode samBranch = this.createBranchSam(samBranchHeader, samKey, BRANCH_LABEL_VALUE_ONLY);
//    							StrutsActionMapping sam = (StrutsActionMapping)((Map)PANEL_INFO[INFO_ID_SAM][INFO_MAP]).get(samKey);
//    							Map safs = sam.getActionForwards();
//    							if (safs != null && safs.size() > 0) {
//    								SimpleTreeNode safBranchHeader = new SimpleTreeNode(new NodeInfo("Action Forward(s)", null, null));
//    								samBranch.add(safBranchHeader);
//    								for (Iterator itSafs = sam.getActionForwards().values().iterator(); itSafs.hasNext();) {
//    									StrutsActionForward saf = (StrutsActionForward)itSafs.next();
//    									SimpleTreeNode safBranch = this.createBranchSaf(safBranchHeader, sam.getPath() + "@" + saf.getName(), BRANCH_LABEL_VALUE_ONLY);
//    									this.createBranchTd(safBranch, saf.getPath(), BRANCH_LABEL_CUSTOM, "path="+saf.getPath());
//    								}
//    							}
//    						}
//    					}
//    				}
//    				break;
//    			}
//    			}
//    			break;
//    		}
//    		case INFO_ID_SAF: {
//    			switch (groupByIndex) {
//    			case GROUP_BY_SAF_BY_NAME:
//    			case GROUP_BY_SAF_BY_PATH: {
//    				SimpleTreeNode safBranchHeader = new SimpleTreeNode(new NodeInfo(key, null, null));
//    				root.add(safBranchHeader);
//    				List safs = (ArrayList)value;
//    				if (safs != null && safs.size() > 0) {
//    					for (Iterator itSafs = safs.iterator(); itSafs.hasNext();) {
//    						String unique = (String)itSafs.next();
//    						//	String samKey = unique.split("@")[0].trim();
//    						//	String safKey = unique.split("@")[1].trim();
//    						StrutsActionForward saf = (StrutsActionForward)((Map)PANEL_INFO[INFO_ID_SAF][INFO_MAP]).get(unique);
//    						SimpleTreeNode safBranch = this.createBranchSaf(safBranchHeader, unique, BRANCH_LABEL_VALUE_ONLY);
//    						StrutsActionMapping sam = saf.getActionMapping();
//    						if (sam != null) {
//    							SimpleTreeNode samBranch = this.createBranchSam(safBranch, sam.getPath(), BRANCH_LABEL_KEY_AND_VALUE);
//    							this.createBranchSafb(samBranch, sam.getName(), BRANCH_LABEL_KEY_AND_VALUE);
//    						}
//    						this.createBranchTd(safBranch, saf.getPath(), BRANCH_LABEL_CUSTOM, "path="+saf.getPath());
//    					}
//    				}
//    				break;
//    			}
//    			case GROUP_BY_SAF_BY_UNIQUE: {
//    				StrutsActionForward saf = (StrutsActionForward)value;
//    				SimpleTreeNode safBranch = this.createBranchSaf(root, key, BRANCH_LABEL_VALUE_ONLY);
//    				StrutsActionMapping sam = saf.getActionMapping();
//    				if (sam != null) {
//    					SimpleTreeNode samBranch = this.createBranchSam(safBranch, sam.getPath(), BRANCH_LABEL_KEY_AND_VALUE);
//    					this.createBranchSafb(samBranch, sam.getName(), BRANCH_LABEL_KEY_AND_VALUE);
//    				}
//    				this.createBranchTd(safBranch, saf.getPath(), BRANCH_LABEL_CUSTOM, "path="+saf.getPath());
//    				break;
//    			}
//    			}
//    			break;
//    		}
//    		case INFO_ID_SAM: {
//    			switch (groupByIndex) {
//    			case GROUP_BY_SAM_BY_SAF_NAME:
//    			case GROUP_BY_SAM_BY_SAF_PATH:
//    			case GROUP_BY_SAM_BY_NAME:
//    			case GROUP_BY_SAM_BY_TYPE: {
//    				List sams = (ArrayList)value;
//    				SimpleTreeNode samBranchHeader = new SimpleTreeNode(new NodeInfo(key, null, null));
//    				root.add(samBranchHeader);
//    				for (Iterator itSams = sams.iterator(); itSams.hasNext();) {
//    					StrutsActionMapping sam = (StrutsActionMapping)itSams.next();
//    					SimpleTreeNode samBranch = this.createBranchSam(samBranchHeader, sam.getPath(), BRANCH_LABEL_VALUE_ONLY);
//    					if (StringUtils.isNotEmpty(sam.getName())) {
//    						this.createBranchSafb(samBranch, sam.getName(), BRANCH_LABEL_KEY_AND_VALUE);
//    					}
//    					Map safs = sam.getActionForwards();
//    					if (safs != null && safs.size() > 0) {
//    						SimpleTreeNode safBranchHeader = new SimpleTreeNode(new NodeInfo("Action Forward(s)", null, null));
//    						samBranch.add(safBranchHeader);
//    						for (Iterator itSafs = sam.getActionForwards().values().iterator(); itSafs.hasNext();) {
//    							StrutsActionForward saf = (StrutsActionForward)itSafs.next();
//    							SimpleTreeNode safBranch = this.createBranchSaf(safBranchHeader, sam.getPath() + "@" + saf.getName(), BRANCH_LABEL_VALUE_ONLY);
//    							this.createBranchTd(safBranch, saf.getPath(), BRANCH_LABEL_CUSTOM, "path="+saf.getPath());
//    						}
//    					}
//    				}
//    				break;
//    			}
//    			case GROUP_BY_SAM_BY_PATH: {
//    				StrutsActionMapping sam = (StrutsActionMapping)value;
//    				SimpleTreeNode samBranch = this.createBranchSam(root, key, BRANCH_LABEL_VALUE_ONLY);
//    				if (StringUtils.isNotEmpty(sam.getName())) {
//    					this.createBranchSafb(samBranch, sam.getName(), BRANCH_LABEL_KEY_AND_VALUE);
//    				}
//    				Map safs = sam.getActionForwards();
//    				if (safs != null && safs.size() > 0) {
//    					SimpleTreeNode safBranchHeader = new SimpleTreeNode(new NodeInfo("Action Forward(s)", null, null));
//    					samBranch.add(safBranchHeader);
//    					for (Iterator itSafs = sam.getActionForwards().values().iterator(); itSafs.hasNext();) {
//    						StrutsActionForward saf = (StrutsActionForward)itSafs.next();
//    						SimpleTreeNode safBranch = this.createBranchSaf(safBranchHeader, sam.getPath() + "@" + saf.getName(), BRANCH_LABEL_VALUE_ONLY);
//    						this.createBranchTd(safBranch, saf.getPath(), BRANCH_LABEL_CUSTOM, "path="+saf.getPath());
//    					}
//    				}
//    				break;
//    			}
//    			}
//    			break;
//    		}
//    		case INFO_ID_TD: {
//    			switch (groupByIndex) {
//    			case GROUP_BY_TD_BY_NAME: {
//    				TilesDefinition td = (TilesDefinition)value;
//    				SimpleTreeNode tdBranch = this.createBranchTd(root, key, BRANCH_LABEL_VALUE_ONLY);
//    				List safs = td.getActionForwards();
//    				if (safs != null && safs.size() > 0) {
//    					SimpleTreeNode safBranchHeader = new SimpleTreeNode(new NodeInfo("Action Forward(s)", null, null));
//    					tdBranch.add(safBranchHeader);
//    					for (Iterator itSafs = safs.iterator(); itSafs.hasNext();) {
//    						String unique = (String)itSafs.next();
//    						String samKey = unique.split("@")[0].trim();
//    						//	String safKey = unique.split("@")[1].trim();
//    						SimpleTreeNode safBranch = this.createBranchSaf(safBranchHeader, unique, BRANCH_LABEL_VALUE_ONLY);
//    						StrutsActionMapping sam = (StrutsActionMapping)((Map)PANEL_INFO[INFO_ID_SAM][INFO_MAP]).get(samKey);
//    						SimpleTreeNode samBranch = this.createBranchSam(safBranch, samKey, BRANCH_LABEL_VALUE_ONLY);
//    						this.createBranchSafb(samBranch, sam.getName(), BRANCH_LABEL_KEY_AND_VALUE);
//    					}
//    				}
//    				break;
//    			}
//    			case GROUP_BY_TD_BY_ATTR_PAGE_WORK:
//    			case GROUP_BY_TD_BY_ATTR_PAGE_WORK_TITLE:
//    			case GROUP_BY_TD_BY_EXTENDS: {
//    				List tds = (ArrayList)value;
//    				SimpleTreeNode tdBranchHeader = new SimpleTreeNode(new NodeInfo(key, null, null));
//    				root.add(tdBranchHeader);
//    				for (Iterator itTds = tds.iterator(); itTds.hasNext();) {
//    					TilesDefinition td = (TilesDefinition)itTds.next();
//    					SimpleTreeNode tdBranch = this.createBranchTd(tdBranchHeader, td.getName(), BRANCH_LABEL_VALUE_ONLY);
//    					List safs = td.getActionForwards();
//    					if (safs != null && safs.size() > 0) {
//    						SimpleTreeNode safBranchHeader = new SimpleTreeNode(new NodeInfo("Action Forward(s)", null, null));
//    						tdBranch.add(safBranchHeader);
//    						for (Iterator itSafds = safs.iterator(); itSafds.hasNext();) {
//    							String unique = (String)itSafds.next();
//    							String samKey = unique.split("@")[0].trim();
//    							//	String safKey = unique.split("@")[1].trim();
//    							SimpleTreeNode safBranch = this.createBranchSaf(safBranchHeader, unique, BRANCH_LABEL_VALUE_ONLY);
//    							StrutsActionMapping sam = (StrutsActionMapping)((Map)PANEL_INFO[INFO_ID_SAM][INFO_MAP]).get(samKey);
//    							SimpleTreeNode samBranch = this.createBranchSam(safBranch, samKey, BRANCH_LABEL_VALUE_ONLY);
//    							this.createBranchSafb(samBranch, sam.getName(), BRANCH_LABEL_KEY_AND_VALUE);
//    						}
//    					}
//    				}
//    				break;
//    			}
//    			}
//    			break;
//    		}
//    		}
//    	}
//    	tree.setModel(new SimpleTreeModel(root));
//	}

	/*
	 * ClipboardOwner-specific method(s)
	 */

	/**
	 * Empty implementation of the ClipboardOwner interface.
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// Do nothing
	}

	/**
	 * Place a String on the clipboard, and make this class the
	 * owner of the Clipboard's contents.
	 */
	public void setClipboardContents(String newContents) {
		StringSelection stringSelection = new StringSelection(newContents);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/********************
	 * Helper method(s) *
	 ********************/

	/**
	 * Creates the GUI shown inside the frame's content pane.
	 */
	private void prepareViewer() {
		logger.info("Preparing viewer: START");
		JTabbedPane tabbedPane = new JTabbedPane();
		for (int panelIndex = 0; panelIndex < PANEL_INFO.length; panelIndex++) {
			if (PANEL_INFO[panelIndex][INFO_ID] != null) {
				if (Viewer.appState.is(AppState.STARTUP)) {
					tabbedPane.addTab(""+PANEL_INFO[panelIndex][INFO_NAME]
				                     ,(ImageIcon)((Object[])TreeUtils.getNodeCategorys().get(PANEL_INFO[panelIndex][INFO_ICON]))[0]
				                     ,this.createTabSpecificSubPanel(panelIndex), ""+PANEL_INFO[panelIndex][INFO_NAME]
				                     );
				} else /*if (Viewer.appState.is(AppState.RESTART))*/ {
					JComboBox groupBy = (JComboBox)PANEL_INFO[panelIndex][INFO_GROUP_BY];
					groupBy(panelIndex, groupBy.getSelectedIndex());
				}
			}
		}
		// Only do the following things once (on startup)
		if (Viewer.appState.is(AppState.STARTUP)) {
			this.add(tabbedPane, BorderLayout.CENTER);
			this.setPreferredSize(new Dimension(1024, 768));
		}
		logger.info("Preparing viewer: END");
	}

//	private void createTrees() {
//		logger.info("Creating Trees: START");
//    	for (int x = 0; x < PANEL_INFO.length; x++) {
//			if (PANEL_INFO[x][INFO_ID] != null) {
//    			this.createTree(x, ((JComboBox)PANEL_INFO[x][INFO_GROUP_BY]).getSelectedIndex(), null, (Map)PANEL_INFO[x][INFO_MAP]);
//    		}
//    	}
//		logger.info("Creating Trees: END");
//	}

//	private void addTabToTabbedPane(JTabbedPane tabbedPane, int panelIndex) {
//		Object[] panelInfo = PANEL_INFO[panelIndex];
//		tabbedPane.addTab(""+panelInfo[INFO_NAME], (ImageIcon)((Object[])TreeUtils.getNodeTypeMap().get(panelInfo[INFO_ICON]))[0], this.createViewerPanel(panelIndex), ""+panelInfo[INFO_NAME]);
//	}

	/**
	 * Obtain a map of parsed data based on the specified 'dom' and
	 * filtered by the specified 'tagName'.
	 *
	 * @param configFile String identifying the name of the xml file which was the source of the data.
	 * @param dom        Document containing the parsed xml data from the xml file.
	 * @param mapNames   String array containing the names of the maps into which to store the data.
	 * @param tagNames   String array containing the names of xml tags to focus on (ignoring all other nodes).
	 * @param parents    SimpleTreeNode array identifying the parents to associate with all resulting data elements.
	 * @return Map containing the corresponding data elements keyed accordingly.
	 */
	@SuppressWarnings("unchecked")
	private Future<Map<String, Map<String, SimpleTreeNode>>> parseDocument(String configFile, Document dom, String[] mapNames, String[] tagNames, SimpleTreeNode[] parents) {
//		Map result = new TreeMap();
//
//		//get the root elememt
//		Element domRoot = dom.getDocumentElement();
//
//		if ("viewer-config".equals(tagName)) {
//			//create the "viewer-config" branch and add it to results map
//			result.put("viewer-config", ModelUtils.createBranchViewerConfig(parent, configFile, domRoot));
//
//			//create the "viewer-about" branch add it to results map
//			result.put("viewer-about", ModelUtils.createBranchViewerAbout(parent, appInfo));
//
//			//create the "viewer-debug" branch add it to results map
//			result.put("viewer-debug", ModelUtils.createBranchViewerDebugInfo(parent));
//
//			//set the (static) viewer-config (for quick/easy reference elsewhere)
//			viewerConfig = (ViewerConfig)((SimpleTreeNode)result.get("viewer-config")).getUserObject();
//		} else {
//			//get a nodelist of <tagName> elements
//			NodeList domNodeList = domRoot.getElementsByTagName(tagName);
//			if (domNodeList != null && domNodeList.getLength() > 0) {
//				for (int i = 0; i < domNodeList.getLength(); i++) {
//					//get the node
//					Node domNode = (Node)domNodeList.item(i);
//					if (domNode.getNodeType() == Node.ELEMENT_NODE) {
//						//get the element
//						Element domElement = (Element)domNode;
//
//						//get the value object
//						if ("action".equals(tagName)) {
//							SimpleTreeNode sam = ModelUtils.createBranchStrutsActionMapping(parent, configFile, domElement);
//
//							//add it to results map
//							result.put(((StrutsActionMapping)sam.getUserObject()).getPath(), sam);
//						} else
//						if ("form-bean".equals(tagName)) {
//							SimpleTreeNode safb = ModelUtils.createBranchStrutsActionFormBean(parent, configFile, domElement);
//
//							//add it to results map
//							result.put(((StrutsActionFormBean)safb.getUserObject()).getName(), safb);
//						} else
//						if ("definition".equals(tagName)) {
//							SimpleTreeNode td = ModelUtils.createBranchTilesDefinition(parent, configFile, domElement);
//
//							//add it to results map
//							result.put(((TilesDefinition)td.getUserObject()).getName(), td);
//						}
//					}
//				}
//			}
//		}
		Future<Map<String, Map<String, SimpleTreeNode>>> result = null;
//		try {
			result /*Future<Map> future*/ = ThreadPoolFactory.getInstance().getThreadPool().submit(new ParseDocumentJob(configFile, dom, mapNames, tagNames, parents));
//			while (!future.isDone()) {
//				logger.debug("Blocking until job is done");
//				Thread.currentThread().sleep(2000);
//			}
//			result = future.get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return result;
	}

	/**
	 * Create a tab-specific sub panel (one for each tab of the main Viewer panel).
	 * <ul>
	 *  NOTE: Currently, there are 6 tabs.  While the 1st and 2nd tabs are different, the
	 *        other 4 tabs are virtually identical to each other...
	 *  <li>[0] == Viewer Config     tab == A "Reload" button instead of "Filter By" and "Group By" controls</li>
	 *  <li>[1] == Viewer Console    tab == A display of console output (System.out and System.err)</li>
	 *  <li>[2] == Action Form Beans tab == A standard "Filter By" control and a "Group By" control w/ type-specific options</li>
	 *  <li>[3] == Action Forwards   tab == A standard "Filter By" control and a "Group By" control w/ type-specific options</li>
	 *  <li>[4] == Action Mappings   tab == A standard "Filter By" control and a "Group By" control w/ type-specific options</li>
	 *  <li>[5] == Tiles Definitions tab == A standard "Filter By" control and a "Group By" control w/ type-specific options</li>
	 * </ul>
	 *
	 * @param panelIndex Integer identifying the target tab for which this panel is destined.
	 * @return JPanel containing the newly created panel.
	 */
	private JPanel createTabSpecificSubPanel(int panelIndex) {
		JButton refreshButton = new JButton("Reload Configuration");
		refreshButton.setToolTipText("Refresh/reload contents from disk");
		refreshButton.setName(""+panelIndex);
		refreshButton.addActionListener(new RefreshButtonActionListener());

		JLabel     filterByLabel = new JLabel("Filtered By: ");
		JTextField filterBy      = new JTextField();
		filterBy.setColumns(30);
		filterBy.setName(""+panelIndex);
		filterBy.addKeyListener(new FilterByKeyListener(this));
		// ..Store a reference to the filter's handle (for easy reference w/in Listeners)
		PANEL_INFO[panelIndex][INFO_FILTER_BY] = filterBy;

		String[] groupByOptions = null;
		int groupByDefault = -1;
		switch (panelIndex) {
			case INFO_ID_VIEWER_CONFIG:
				groupByOptions = new String[0];
				break;
			case INFO_ID_VIEWER_CONSOLE:
				// no op
				break;
			case INFO_ID_SAFB:
				groupByOptions = new String[]{ "Form Bean Name", "Form Bean Type", };
				groupByDefault = GROUP_BY_SAFB_BY_NAME;
				break;
			case INFO_ID_SAF:
				groupByOptions = new String[]{ "Forward Name", "Forward Path", "Mapping Path & Forward Name" };
				groupByDefault = GROUP_BY_SAF_BY_UNIQUE;
				break;
			case INFO_ID_SAM:
				groupByOptions = new String[]{ "Forward Name", "Forward Path", "Form Bean Name", "Mapping Path", "Mapping Type" };
				groupByDefault = GROUP_BY_SAM_BY_PATH;
				break;
			case INFO_ID_TD:
				// TODO[jpt] Refactor all "JSP" references to something generic (perhaps Rendered Output File or Resulting Web Page)
				groupByOptions = new String[]{ "JSP File Name", "JSP Page Title", "Tiles Definition Parent", "Tiles Definition Name" };
				groupByDefault = GROUP_BY_TD_BY_NAME;
				break;
		}
		JLabel    groupByLabel = null;
		JComboBox groupBy      = null;
		if (groupByOptions != null) {
			groupByLabel = new JLabel("Grouped By: ");
			groupBy      = new JComboBox(groupByOptions);
			groupBy.setSelectedIndex(groupByDefault);
			groupBy.setName(""+panelIndex);
			groupBy.addActionListener(new GroupByActionListener(this));
			// ..Store a reference to the comboBox's handle (for easy reference w/in Listeners)
			PANEL_INFO[panelIndex][INFO_GROUP_BY] = groupBy;
		}
		JPanel controlPanel = new JPanel(new FlowLayout());
		switch (panelIndex) {
			case INFO_ID_VIEWER_CONFIG:
				controlPanel.add(refreshButton);
				break;
			case INFO_ID_VIEWER_CONSOLE:
				final short[] PADDING = { 5, 35 }; // [0] == Horizontal, [1] == Vertical
				JScrollPane consolePanel = new JScrollPane(Viewer.getConsole());
				consolePanel.setPreferredSize(new Dimension(1024 - PADDING[0], 768 - PADDING[1]));
				consolePanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				consolePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				consolePanel.setWheelScrollingEnabled(true);
				controlPanel.add(consolePanel);
				break;
			default:
				controlPanel.add(filterByLabel);
				controlPanel.add(filterBy);
				controlPanel.add(groupByLabel);
				controlPanel.add(groupBy);
				break;
		}

		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(controlPanel); //, BorderLayout.PAGE_START

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(box, BorderLayout.PAGE_START);

		// Add the tree and its panel to the main panel
		if (groupByOptions != null) {
			if ((JTree)PANEL_INFO[panelIndex][INFO_TREE] == null) {
				JTree tree = new JTree(new SimpleTreeModel(PANEL_INFO[panelIndex][INFO_ROOT]));
				tree.setCellRenderer(new TreeCellRenderer());
				tree.addKeyListener(new TreeKeyListener(this));
				tree.addMouseListener(new TreeMouseListener(this));
				tree.setRootVisible(false);
				tree.setShowsRootHandles(true);
				tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				tree.expandPath(TreeUtils.getTreePath(new SimpleTreeNode()));
				ToolTipManager.sharedInstance().registerComponent(tree);
				PANEL_INFO[panelIndex][INFO_TREE] = tree;
			}
			JScrollPane scrollPanel = new JScrollPane((JTree)PANEL_INFO[panelIndex][INFO_TREE]);
			panel.add(scrollPanel, BorderLayout.CENTER);
		}

		return panel;
	}

	/**
	 * Parse the command-line arguments into the 'commandLineArgs' map.
	 *
	 * @param args String array containing any/all command-line arguments.
	 */
    private void parseCommandLineArguments(String args[]) {
		logger.info("Parsing command-line arguments: START");
    	if (args != null) {
    		for (int x = 0; x < args.length; x++) {
    			String[] keyValue = args[x].split("=");
    			commandLineArgs.put(keyValue[0], keyValue[1]);
    		}
    		logger.debug("Command-line arguments: " + Arrays.toString(args));
    	}
		logger.info("Parsing command-line arguments: END");
    }

    /**
     * Parse the "viewer-config.xml" file.
     */
    private void parseViewerConfiguration() {
		logger.info("Parsing viewer configuration: START");
    	// Override configuration file name (when necessary)
    	if (commandLineArgs.containsKey("configurationFileTarget")) {
    		viewerConfigFileTarget = (String)commandLineArgs.get("configurationFileTarget");
    	} else if (MiscUtils.getFileSystemObjectLastModified(viewerConfigFileName) != null) {
    		viewerConfigFileTarget = viewerConfigFileName;
    	}

    	// Parse the XML file into a document object
    	Document domViewerConfig = MiscUtils.parseXmlFile(this.getClass(), viewerConfigFileTarget);

    	SimpleTreeNode vc = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_VIEWER_CONFIG, -1, -1, null));

    	List<Future<Map<String, Map<String, SimpleTreeNode>>>> futureResults = new ArrayList<Future<Map<String, Map<String, SimpleTreeNode>>>>();

    	futureResults.add(this.parseDocument(viewerConfigFileTarget, domViewerConfig, new String[]{ "vc" }, new String[]{ "viewer-config" }, new SimpleTreeNode[]{ vc }));

    	for (Future<Map<String, Map<String, SimpleTreeNode>>> futureResult : futureResults) {
    		while (!futureResult.isDone()) {
    			try {
					Thread.currentThread().sleep(1000);
					logger.debug("Blocking viewerConfig:" + futureResult + " until done.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
			try {
				for (Map.Entry<String, Map<String, SimpleTreeNode>> result : futureResult.get().entrySet()) {
					((Map)vc.getUserObject()).putAll(result.getValue());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
    	}

    	// Reset current thread pool so that any configuration changes are applied
    	ThreadPoolFactory.getInstance().resetThreadPool();

    	PANEL_INFO[INFO_ID_VIEWER_CONFIG][INFO_ROOT] = vc;
		logger.info("Parsing viewer configuration: END");
    }

    /**
     * Parse all configuration files that have been specified in the "viewer-config.xml" file.
     * <p>
     *  NOTE: After parsing all of the configuration files, the sorting/grouping-related
     *        maps are then created.
     * </p>
     */
    private void parseSpecifiedFiles() {
		logger.info("Parsing specified files: START");

		SimpleTreeNode treeNode = null;

    	// Create the 4 main maps (keyed by an actual primary key)
		SimpleTreeNode safbByName  = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_SAFB, GROUP_BY_SAFB_BY_NAME , -1, "safb"));
		SimpleTreeNode safByUnique = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_SAF , GROUP_BY_SAF_BY_UNIQUE, -1, "saf" ));
		SimpleTreeNode samByPath   = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_SAM , GROUP_BY_SAM_BY_PATH  , -1, "sam" ));
		SimpleTreeNode tdByName    = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_TD  , GROUP_BY_TD_BY_NAME   , -1, "td"  ));

		List<Future<Map<String, Map<String, SimpleTreeNode>>>> futureResults = new ArrayList<Future<Map<String, Map<String, SimpleTreeNode>>>>();

    	for (Iterator it = Viewer.getViewerConfig().getTargetAppConfigFilesByMask().keySet().iterator(); it.hasNext();) {
    		String targetAppConfigFileMask = (String)it.next();
    		List targetAppConfigFiles = (List)Viewer.getViewerConfig().getTargetAppConfigFilesByMask().get(targetAppConfigFileMask);
    		for (Iterator itSub = targetAppConfigFiles.iterator(); itSub.hasNext();) {
    			File configFile = (File)itSub.next();
    			String configFileTarget = configFile.getAbsolutePath();
    			Document workDocument = MiscUtils.parseXmlFile(this.getClass(), configFileTarget);
    			if (configFile.getName().startsWith("struts-config")) {
    				futureResults.add(this.parseDocument(configFileTarget, workDocument, new String[]{ "safbByName", "samByPath" }, new String[]{ "form-bean", "action" }, new SimpleTreeNode[]{ safbByName, samByPath }));
    			} else { // "tiles-defs"
    				futureResults.add(this.parseDocument(configFileTarget, workDocument, new String[]{ "tdByName" }, new String[]{ "definition" }, new SimpleTreeNode[]{ tdByName } ));
    			}
    		}
    	}

    	for (Future<Map<String, Map<String, SimpleTreeNode>>> futureResult : futureResults) {
    		while (!futureResult.isDone()) {
    			try {
					Thread.currentThread().sleep(1000);
					logger.debug("Blocking viewerConfig:" + futureResult + " until done.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
			try {
				for (Map.Entry<String, Map<String, SimpleTreeNode>> result : futureResult.get().entrySet()) {
					if ("safbByName".equals(result.getKey())) {
						((Map)safbByName.getUserObject()).putAll(result.getValue());
	    			} else if ("samByPath".equals(result.getKey())) {
	    				((Map) samByPath.getUserObject()).putAll(result.getValue());
	    			} else if ("tdByName".equals(result.getKey())) {
	    				((Map)  tdByName.getUserObject()).putAll(result.getValue());
	    			}
	    			// Update the Progress Bar
					if (!"safbByName".equals(result.getKey())) {
						// NOTE: There's a safbByName for each samByPath (don't double-increment)
		    			progressPanel.increment();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
    	}

//    	// Tell threadpool we're done submitting threads
//    	ThreadPoolFactory.getInstance().getThreadPool().shutdown();
//    	// Wait for all threads to finish
//    	try {
//			while (!ThreadPoolFactory.getInstance().getThreadPool().awaitTermination(1, TimeUnit.SECONDS)) {
//			  logger.debug("Awaiting completion of threads.");
//			}
//		} catch (InterruptedException e) {
//			logger.error("ThreadPoolFactory.shutdown was interrupted - " + e.getMessage(), e);
//		}

    	/************************************************************
    	 * Create/Populate Maps/Collections based on (Map)samByPath *
    	 * + (Map )safByUnique                                      *
    	 * + (Map )samsBySafPath                                    *
    	 * + (Map )samsBySafName                                    *
    	 * + (List)safbByName.getActionMappings()                   *
    	 * + (List)tdByName.getActionForwards()                     *
    	 * + (Map )classMemberBranches                              *
    	 ************************************************************/
    	for (Iterator it = ((Map)samByPath.getUserObject()).keySet().iterator(); it.hasNext();) {
    		String samKey = (String)it.next();
    		StrutsActionMapping sam = (StrutsActionMapping)((SimpleTreeNode)((Map)samByPath.getUserObject()).get(samKey)).getUserObject();
       		// ..safByUnique, samsBySafName, samsBySafPath, tdByName.getActionForwards()
       		for (Iterator itSub = sam.getActionForwards().keySet().iterator(); itSub.hasNext();) {
       			String safKey = (String)itSub.next();
     			treeNode = (SimpleTreeNode)sam.getActionForwards().get(safKey);
     			if (treeNode == null) continue;
     			StrutsActionForward saf = (StrutsActionForward)treeNode.getUserObject();
   				// ..safByUnique
				String uniqueKey = sam.getPath() + "@" + saf.getName();
				SimpleTreeNode uniqueValue = new SimpleTreeNode(uniqueKey, safByUnique, saf, "saf");
   				((Map)safByUnique.getUserObject()).put(uniqueKey, uniqueValue);
   				// ..samsBySafName
   				SimpleTreeNode samsBySafNameNode = (SimpleTreeNode)samBySafName.get(saf.getName());
   				if (samsBySafNameNode == null) {
   					samsBySafNameNode = new SimpleTreeNode(saf.getName());
   				}
   				List samsBySafNameList = (List)samsBySafNameNode.getUserObject();
   				if (samsBySafNameList == null) {
   					samsBySafNameList = new ArrayList();
   				}
   				if (!samsBySafNameList.contains(uniqueValue)) {
   					samsBySafNameList.add(uniqueValue);
      			} else {
      				logger.debug("[samsBySafName] samsBySafName.contains(" + uniqueKey + ")");
   				}
   				samsBySafNameNode.setUserObject(samsBySafNameList);
   				samBySafName.put(saf.getName(), samsBySafNameNode);
   				// ..samsBySafPath
   				SimpleTreeNode samsBySafPathNode = (SimpleTreeNode)samBySafPath.get(saf.getPath());
   				if (samsBySafPathNode == null) {
   					samsBySafPathNode = new SimpleTreeNode(saf.getPath());
   				}
   				List samsBySafPathList = (List)samsBySafPathNode.getUserObject();
   				if (samsBySafPathList == null) {
   					samsBySafPathList = new ArrayList();
   				}
   				if (!samsBySafPathList.contains(uniqueValue)) {
   					samsBySafPathList.add(uniqueValue);
      			} else {
      				logger.debug("[samsBySafPath] samsBySafPath.contains(" + uniqueKey + ")");
   				}
   				samsBySafPathNode.setUserObject(samsBySafPathList);
   				samBySafPath.put(saf.getPath(), samsBySafPathNode);
         		// ..tdByName.getActionForwards()
 				treeNode = (SimpleTreeNode)((Map)tdByName.getUserObject()).get(saf.getPath());
 				if (treeNode == null) continue;
 				TilesDefinition td = (TilesDefinition)treeNode.getUserObject();
   				if (td != null) {
   					List safs = td.getActionForwards();
   					if (safs == null) {
   						safs = new ArrayList();
   					}
   					if (!safs.contains(saf)) {
   						safs.add(saf);
          			} else {
          				logger.debug("[tdByName] safs.contains(" + uniqueKey + ")");
   					}
   					td.setActionForwards(safs);
       				/*****************
       				 * Add td to saf *
       				 *****************/
					saf.setTilesDefinition(td);
         		}
       		}
     		// ..safbByName.getActionMappings()
     		if (StringUtils.isNotEmpty(sam.getName())) {
     			treeNode = (SimpleTreeNode)((Map)safbByName.getUserObject()).get(sam.getName());
     			if (treeNode == null) continue;
     			StrutsActionFormBean safb = (StrutsActionFormBean)treeNode.getUserObject();
      			if (safb != null) {
      				List sams = safb.getActionMappings();
      				if (sams == null) {
      					sams = new ArrayList();
      				}
      				//if (!sams.contains(samKey)) {
      				if (!sams.contains(sam)) {
      					//sams.add(samKey);
      					sams.add(sam);
          			} else {
          				logger.debug("[safbByName] sams.contains(" + samKey + ")");
      				}
      				safb.setActionMappings(sams);
       				/*******************
       				 * Add safb to sam *
       				 *******************/
					sam.setFormBean(safb);
      			}
      		}
      		// ..classMemberBranches
      		ModelUtils.createBranchClassFromAbstractVO(null, sam);
      		if (StringUtils.isNotEmpty(sam.getName())) {
    			treeNode = (SimpleTreeNode)((Map)safbByName.getUserObject()).get(sam.getName());
    			if (treeNode == null) continue;
    			StrutsActionFormBean safb = (StrutsActionFormBean)treeNode.getUserObject();
      			if (safb != null) {
      				ModelUtils.createBranchClassFromAbstractVO(null, safb);
      			}
      		}
  		}

		// Update the Progress Bar
		progressPanel.increment();

    	/***********************************************
    	 * Type-specific structure for each type of VO *
    	 *	+ Struts Tiles Viewer (Configuration)      *
    	 *		- (n/a)                                *
x    	 *	+ Action Form Bean...                      *
x    	 *		- Action Mapping(s)                    *
x    	 *			- Action Forward(s)                *
x    	 *				- Tiles Definition             *
-    	 *					- Tiles Attribute(s)       *
x    	 *	+ Action Forward...                        *
x    	 *		- Tiles Definition                     *
-    	 *			- Tiles Attribute(s)               *
x    	 *		- Action Mapping                       *
x    	 *			- Action Form Bean                 *
x    	 *	+ Action Mapping...                        *
x    	 *		- Action Form Bean                     *
x    	 *		- Action Forward(s)                    *
x    	 *			- Tiles Definition                 *
-    	 *				- Tiles Attribute(s)           *
-    	 *	+ Tiles Definition...                      *
-    	 *		- Tiles Attribute(s)                   *
x    	 *		- Action Forward(s)                    *
x    	 *			- Action Mapping                   *
x    	 *				- Action Form Bean             *
    	 ***********************************************/

    	// Update the 4 main maps (keyed by an actual primary key)
		PANEL_INFO[INFO_ID_SAFB][INFO_ROOT] = safbByName;
		PANEL_INFO[INFO_ID_SAF ][INFO_ROOT] = safByUnique;
		PANEL_INFO[INFO_ID_SAM ][INFO_ROOT] = samByPath;
		PANEL_INFO[INFO_ID_TD  ][INFO_ROOT] = tdByName;

		logger.info("Parsing specified files: END");
    }

    /**
     * Remove entries from the specified map that don't have a key that matches
     * the specified regex pattern.
     *
     * @param map   Map onto which to apply the filter.
     * @param regex String containing the value with which to filter.
     */
	private void filterByRegex(Map map, String regex) {
		Pattern regexPattern = Pattern.compile(".*" + StringUtils.defaultString(regex) + ".*");
		Set work = new HashSet(map.keySet());
		for (Iterator it = work.iterator(); it.hasNext();) {
			String key = (String)it.next();
			// Remove this key if it doesn't match the pattern
			if (!regexPattern.matcher(key).matches()) {
				map.remove(key);
			}
		}
	}

	/**
	 * Obtain a count of files that will be processed by {@link #parseSpecifiedFiles()}.
	 * <p>
	 *  NOTE: This count is then used to help determine the total count of Progress Bar-related tasks.
	 * </p>
	 *
	 * @return Integer containing the total number of files to be processed by {@link #parseSpecifiedFiles()}.
	 */
	private static int getCountOfSpecifiedFiles() {
		int result = 0;
    	for (Iterator it = Viewer.getViewerConfig().getTargetAppConfigFilesByMask().keySet().iterator(); it.hasNext();) {
    		String targetAppConfigFileMask = (String)it.next();
    		List targetAppConfigFiles = (List)Viewer.getViewerConfig().getTargetAppConfigFilesByMask().get(targetAppConfigFileMask);
   			result += targetAppConfigFiles.size();
    	}
    	return result;
	}

//	private SimpleTreeNode createBranchSafb(SimpleTreeNode parentBranch, String key, int branchLabelType) {
//		return this.createBranchSafb(parentBranch, key, branchLabelType, null);
//	}
//	private SimpleTreeNode createBranchSafb(SimpleTreeNode parentBranch, String key, int branchLabelType, String customLabel) {
//		SimpleTreeNode branch = null;
//		Map safbMap = (Map)PANEL_INFO[INFO_ID_SAFB][INFO_MAP];
//		if (safbMap != null && safbMap.containsKey(key)) {
//			StrutsActionFormBean safb = (StrutsActionFormBean)safbMap.get(key);
//			switch (branchLabelType) {
//				case BRANCH_LABEL_STATIC_TEXT:
//					branch = new SimpleTreeNode(new NodeInfo("Action Form Bean", "safb", null));
//					break;
//				case BRANCH_LABEL_KEY_AND_VALUE:
//					branch = new SimpleTreeNode(new NodeInfo("name="+safb.getName(), "safb", null));
//					break;
//				case BRANCH_LABEL_VALUE_ONLY:
//					branch = new SimpleTreeNode(new NodeInfo(key, "safb", null));
//					break;
//				case BRANCH_LABEL_CUSTOM:
//					branch = new SimpleTreeNode(new NodeInfo(customLabel, "safb", null));
//					break;
//			}
//			TreeUtils.replaceChild(parentBranch, "name="+safb.getName(), branch);
//			TreeUtils.addBranch(safb.getName()            , branch, new SimpleTreeNode(new NodeInfo("name="+safb.getName(), null, null)));
//			this.createBranchClass(branch, safb);
//			TreeUtils.addBranch(safb.getClassName()       , branch, new SimpleTreeNode(new NodeInfo("className="+safb.getClassName(), null, null)));
//			TreeUtils.addBranch(safb.getDynamic()         , branch, new SimpleTreeNode(new NodeInfo("dynamic="+safb.getDynamic(), null, null)));
//			TreeUtils.addBranch(safb.getId()              , branch, new SimpleTreeNode(new NodeInfo("id="+safb.getId(), null, null)));
//			TreeUtils.addBranch(safb.getConfigFileTarget(), branch, new SimpleTreeNode(new NodeInfo("source="+safb.getConfigFileTarget(), safb.isConfigFileTargetMissing()?"warn":"xml", safb.getConfigFileTarget())));
//		}
//		return branch;
//	}

//	private SimpleTreeNode createBranchSaf(SimpleTreeNode parentBranch, String key, int branchLabelType) {
//		return this.createBranchSaf(parentBranch, key, branchLabelType, null);
//	}
//	private SimpleTreeNode createBranchSaf(SimpleTreeNode parentBranch, String key, int branchLabelType, String customLabel) {
//		SimpleTreeNode branch = null;
//		Map safMap  = (Map)PANEL_INFO[INFO_ID_SAF][INFO_MAP];
//		if (safMap != null && safMap.containsKey(key)) {
//			StrutsActionForward saf = (StrutsActionForward)safMap.get(key);
//			switch (branchLabelType) {
//				case BRANCH_LABEL_STATIC_TEXT:
//					branch = new SimpleTreeNode(new NodeInfo("Action Forward", "saf", null));
//					break;
//				case BRANCH_LABEL_KEY_AND_VALUE:
//					branch = new SimpleTreeNode(new NodeInfo("path="+saf.getPath(), "saf", null));
//					break;
//				case BRANCH_LABEL_VALUE_ONLY:
//					branch = new SimpleTreeNode(new NodeInfo(key, "saf", null));
//					break;
//				case BRANCH_LABEL_CUSTOM:
//					branch = new SimpleTreeNode(new NodeInfo(customLabel, "saf", null));
//					break;
//			}
//			TreeUtils.replaceChild(parentBranch, "path="+saf.getPath(), branch);
//			TreeUtils.addBranch(saf.getName()            , branch, new SimpleTreeNode(new NodeInfo("name="+saf.getName(), null, null)));
//			TreeUtils.addBranch(saf.getPath()            , branch, new SimpleTreeNode(new NodeInfo("path="+saf.getPath(), null, null)));
//			TreeUtils.addBranch(saf.getRedirect()        , branch, new SimpleTreeNode(new NodeInfo("redirect="+saf.getRedirect(), null, null)));
//			TreeUtils.addBranch(saf.getClassName()       , branch, new SimpleTreeNode(new NodeInfo("className="+saf.getClassName(), null, null)));
//			TreeUtils.addBranch(saf.getContextRelative() , branch, new SimpleTreeNode(new NodeInfo("contextRelative="+saf.getContextRelative(), null, null)));
//			TreeUtils.addBranch(saf.getId()              , branch, new SimpleTreeNode(new NodeInfo("id="+saf.getId(), null, null)));
//			TreeUtils.addBranch(saf.getConfigFileTarget(), branch, new SimpleTreeNode(new NodeInfo("source="+saf.getConfigFileTarget(), saf.isConfigFileTargetMissing()?"warn":"xml", saf.getConfigFileTarget())));
//		}
//		return branch;
//	}

//	private SimpleTreeNode createBranchSam(SimpleTreeNode parentBranch, String key, int branchLabelType) {
//		return this.createBranchSam(parentBranch, key, branchLabelType, null);
//	}
//	private SimpleTreeNode createBranchSam(SimpleTreeNode parentBranch, String key, int branchLabelType, String customLabel) {
//		SimpleTreeNode branch = null;
//		Map samMap = (Map)PANEL_INFO[INFO_ID_SAM][INFO_MAP];
//		if (samMap != null && samMap.containsKey(key)) {
//			StrutsActionMapping sam = (StrutsActionMapping)samMap.get(key);
//			switch (branchLabelType) {
//				case BRANCH_LABEL_STATIC_TEXT:
//					branch = new SimpleTreeNode(new NodeInfo("Action Mapping", "sam", null));
//					break;
//				case BRANCH_LABEL_KEY_AND_VALUE:
//					branch = new SimpleTreeNode(new NodeInfo("path="+sam.getPath(), "sam", null));
//					break;
//				case BRANCH_LABEL_VALUE_ONLY:
//					branch = new SimpleTreeNode(new NodeInfo(key, "sam", null));
//					break;
//				case BRANCH_LABEL_CUSTOM:
//					branch = new SimpleTreeNode(new NodeInfo(customLabel, "sam", null));
//					break;
//			}
//			TreeUtils.replaceChild(parentBranch, "path="+sam.getPath(), branch);
//			TreeUtils.addBranch(sam.getPath()            , branch, new SimpleTreeNode(new NodeInfo("path="+sam.getPath(), null, null)));
//			this.createBranchClass(branch, sam);
//			TreeUtils.addBranch(sam.getName()            , branch, new SimpleTreeNode(new NodeInfo("name="+sam.getName(), null, null)));
//			TreeUtils.addBranch(sam.getScope()           , branch, new SimpleTreeNode(new NodeInfo("scope="+sam.getScope(), null, null)));
//			TreeUtils.addBranch(sam.getParameter()       , branch, new SimpleTreeNode(new NodeInfo("parameter="+sam.getParameter(), null, null)));
//			TreeUtils.addBranch(sam.getValidate()        , branch, new SimpleTreeNode(new NodeInfo("validate="+sam.getValidate(), null, null)));
//			TreeUtils.addBranch(sam.getAttribute()       , branch, new SimpleTreeNode(new NodeInfo("attribute="+sam.getAttribute(), null, null)));
//			TreeUtils.addBranch(sam.getClassName()       , branch, new SimpleTreeNode(new NodeInfo("className="+sam.getClassName(), null, null)));
//			TreeUtils.addBranch(sam.getForward()         , branch, new SimpleTreeNode(new NodeInfo("forward="+sam.getForward(), null, null)));
//			TreeUtils.addBranch(sam.getId()              , branch, new SimpleTreeNode(new NodeInfo("id="+sam.getId(), null, null)));
//			TreeUtils.addBranch(sam.getInclude()         , branch, new SimpleTreeNode(new NodeInfo("include="+sam.getInclude(), null, null)));
//			TreeUtils.addBranch(sam.getInput()           , branch, new SimpleTreeNode(new NodeInfo("input="+sam.getInput(), null, null)));
//			TreeUtils.addBranch(sam.getPrefix()          , branch, new SimpleTreeNode(new NodeInfo("prefix="+sam.getPrefix(), null, null)));
//			TreeUtils.addBranch(sam.getRoles()           , branch, new SimpleTreeNode(new NodeInfo("roles="+sam.getRoles(), null, null)));
//			TreeUtils.addBranch(sam.getSuffix()          , branch, new SimpleTreeNode(new NodeInfo("suffix="+sam.getSuffix(), null, null)));
//			TreeUtils.addBranch(sam.getUnknown()         , branch, new SimpleTreeNode(new NodeInfo("unknown="+sam.getUnknown(), null, null)));
//			TreeUtils.addBranch(sam.getConfigFileTarget(), branch, new SimpleTreeNode(new NodeInfo("source="+sam.getConfigFileTarget(), sam.isConfigFileTargetMissing()?"warn":"xml", sam.getConfigFileTarget())));
//		}
//		return branch;
//	}

//	private SimpleTreeNode createBranchTd(SimpleTreeNode parentBranch, String key, int branchLabelType) {
//		return this.createBranchTd(parentBranch, key, branchLabelType, null);
//	}
//	private SimpleTreeNode createBranchTd(SimpleTreeNode parentBranch, String key, int branchLabelType, String customLabel) {
//		SimpleTreeNode branch = null;
//		Map tdMap = (Map)PANEL_INFO[INFO_ID_TD][INFO_MAP];
//		if (tdMap != null && tdMap.containsKey(key)) {
//			TilesDefinition td = (TilesDefinition)tdMap.get(key);
//			switch (branchLabelType) {
//				case BRANCH_LABEL_STATIC_TEXT:
//					branch = new SimpleTreeNode(new NodeInfo("Tiles Definition", "td", null));
//					break;
//				case BRANCH_LABEL_KEY_AND_VALUE:
//					branch = new SimpleTreeNode(new NodeInfo("name="+td.getName(), "td", null));
//					break;
//				case BRANCH_LABEL_VALUE_ONLY:
//					branch = new SimpleTreeNode(new NodeInfo(key, "td", null));
//					break;
//				case BRANCH_LABEL_CUSTOM:
//					branch = new SimpleTreeNode(new NodeInfo(customLabel, "td", null));
//					break;
//			}
//			TreeUtils.replaceChild(parentBranch, "path="+td.getName(), branch); // Intentionally changed from "name="
//			TreeUtils.addBranch(td.getName()           , branch, new SimpleTreeNode(new NodeInfo("name="+td.getName(), null, null)));
//			TreeUtils.addBranch(td.getExtends()        , branch, new SimpleTreeNode(new NodeInfo("extends="+td.getExtends(), null, null)));
//			TreeUtils.addBranch(td.getControllerClass(), branch, new SimpleTreeNode(new NodeInfo("controllerClass="+td.getControllerClass(), null, null)));
//			TreeUtils.addBranch(td.getControllerUrl()  , branch, new SimpleTreeNode(new NodeInfo("controllerUrl="+td.getControllerUrl(), null, null)));
//			TreeUtils.addBranch(td.getPage()           , branch, new SimpleTreeNode(new NodeInfo("page="+td.getPage(), null, null)));
//			TreeUtils.addBranch(td.getPath()           , branch, new SimpleTreeNode(new NodeInfo("path="+td.getPath(), null, null)));
//			TreeUtils.addBranch(td.getRole()           , branch, new SimpleTreeNode(new NodeInfo("role="+td.getRole(), null, null)));
//			TreeUtils.addBranch(td.getTemplate()       , branch, new SimpleTreeNode(new NodeInfo("template="+td.getTemplate(), null, null)));
//			if (td.getAttributes().size() > 0) {
//				SimpleTreeNode tdAttrBranch = new SimpleTreeNode(new NodeInfo("Attributes", null, null));
//				branch.add(tdAttrBranch);
//				for (Iterator it = td.getAttributes().keySet().iterator(); it.hasNext();) {
//					String         attrKey    = (String)it.next();
//					TilesAttribute attrObject = td.getAttribute(attrKey);
//					SimpleTreeNode tdAttrLeaf = null;
//					if (TilesDefinition.ATTR_PAGE_WORK.equals(attrKey)) {
//						tdAttrLeaf = new SimpleTreeNode(new NodeInfo(attrKey+"="+attrObject.getValue(), td.isPageWorkTargetMissing()?"warn":"jsp", td.getPageWorkTarget()));
//					} else {
//						tdAttrLeaf = new SimpleTreeNode(new NodeInfo(attrKey+"="+attrObject.getValue(), null, null));
//					}
//					TreeUtils.addBranch(attrObject.getValue()  , tdAttrBranch, tdAttrLeaf);
//					TreeUtils.addBranch(attrObject.getName()   , tdAttrLeaf, new SimpleTreeNode(new NodeInfo("name="+attrObject.getName(), null, null)));
//					TreeUtils.addBranch(attrObject.getValue()  , tdAttrLeaf, new SimpleTreeNode(new NodeInfo("value="+attrObject.getValue(), null, null)));
//					TreeUtils.addBranch(attrObject.getContent(), tdAttrLeaf, new SimpleTreeNode(new NodeInfo("content="+attrObject.getContent(), null, null)));
//					TreeUtils.addBranch(attrObject.getDirect() , tdAttrLeaf, new SimpleTreeNode(new NodeInfo("direct="+attrObject.getDirect(), null, null)));
//					TreeUtils.addBranch(attrObject.getType()   , tdAttrLeaf, new SimpleTreeNode(new NodeInfo("type="+attrObject.getType(), null, null)));
//				}
//			}
//			TreeUtils.addBranch(td.getConfigFileTarget(), branch, new SimpleTreeNode(new NodeInfo("source="+td.getConfigFileTarget(), "xml", td.getConfigFileTarget())));
//		}
//		return branch;
//	}

//	private SimpleTreeNode getBranchClass(SimpleTreeNode parentBranch, AbstractVO avo) {
//		SimpleTreeNode branch = null;
//		if (avo != null) {
//			String key = null;
//			if (avo instanceof StrutsActionForm) {
//				StrutsActionFormBean safb = (StrutsActionFormBean)avo;
//				key = "[safb]" + safb.getName();
//			} else {
//				StrutsActionMapping sam = (StrutsActionMapping)avo;
//				key = "[sam]" + sam.getPath();
//			}
//			branch = (SimpleTreeNode)classMemberBranches.get(key);
//			if (branch != null) {
//				if (parentBranch != null) parentBranch.add(branch);
//			}
//		}
//		return branch;
//	}

}
