package concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import model.SimpleTreeNode;
import model.StrutsActionFormBean;
import model.StrutsActionMapping;
import model.TilesDefinition;
import model.ViewerConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.ModelUtils;
import view.Viewer;

/**
 * Container for a parse document task to be executed.
 */
public class ParseDocumentJob implements Callable<Map<String, Map<String, SimpleTreeNode>>> {

	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("view");
	
	/**
	 * Potential job status(es)...
	 * <ul>
	 *  <li>{@value #STATUS_WAITING}</li>
	 *  <li>{@value #STATUS_STARTING}</li>
	 *  <li>{@value #STATUS_RUNNING}</li>
	 *  <li>{@value #STATUS_FINISHED}</li>
	 *  <li>{@value #STATUS_ERRORED}</li>
	 * </ul>
	 */
	public static final String STATUS_WAITING  = "STATUS_WAITING";
	public static final String STATUS_STARTING = "STATUS_STARTING";
	public static final String STATUS_RUNNING  = "STATUS_RUNNING";
	public static final String STATUS_FINISHED = "STATUS_FINISHED";
	public static final String STATUS_ERRORED  = "STATUS_ERRORED";

	/**
	 * Primary constructor.
	 * 
	 * @param configFile String containing the name of this specific job.
	 */
	public ParseDocumentJob(String configFile, Document dom, String[] mapNames, String[] tagNames, SimpleTreeNode[] parents) {
		this.configFile = configFile;
		this.dom = dom;
		this.mapNames = mapNames;
		this.tagNames = tagNames;
		this.parents = parents;
	}
	
	/**
	 * Container for the Configuration File used by this job.
	 */
	private String configFile;
	public String getConfigFile() {
		return this.configFile;
	}
	
	/**
	 * Container for the Document Object Model used by this job.
	 */
	private Document dom;
	public Document getDom() {
		return this.dom;
	}
	
	/**
	 * Container for the Map Names used by this job.
	 */
	private String[] mapNames;
	public String[] getMapNames() {
		return this.mapNames;
	}
	
	/**
	 * Container for the Tag Names used by this job.
	 */
	private String[] tagNames;
	public String[] getTagNames() {
		return this.tagNames;
	}
	
	/**
	 * Container for the (parent) Simple Tree Nodes used by this job.
	 */
	private SimpleTreeNode[] parents;
	public SimpleTreeNode[] getParents() {
		return this.parents;
	}
	
	/**
	 * Container for the status of this job.
	 */
	private String jobStatus = ParseDocumentJob.STATUS_WAITING;
	public String getJobStatus() {
		return this.jobStatus;
	}
	
//	/**
//	 * Container for the result of this job.
//	 */
//	private Map jobResult;
//	public Map getJobResult() {
//		return this.jobResult;
//	}
	
	/**
	 * Payload for all Callable implementations.
	 * <p>
	 *  NOTE: This method is the entry-point for all Callables, and is therefore
	 *        the method that will be launched when the task is selected for
	 *        execution by the Thread Pool.
	 * </p>
	 */
	@Override
	public Map<String, Map<String, SimpleTreeNode>> call() throws Exception {
		// Default the job status to 
		jobStatus = ParseDocumentJob.STATUS_STARTING;
		logger.debug(configFile + "- Started.");
		Map<String, Map<String, SimpleTreeNode>> result = doWork();
		logger.debug(configFile + "- Finished.");
		return result;
	}
	
	/**
	 * The true payload of this class.
	 * <p>
	 *  NOTE: This method is called by the {@link #call()} method.
	 * </p>
	 * 
	 * @return Map containing the result.
	 */
	public Map<String, Map<String, SimpleTreeNode>> doWork() {
		// Update the job status
		jobStatus = ParseDocumentJob.STATUS_RUNNING;
		
		Map<String, Map<String, SimpleTreeNode>> result = new HashMap<String, Map<String, SimpleTreeNode>>();
		
		try {
			for (int x = 0; x < mapNames.length; x++) {
				String mapName = mapNames[x];
				String tagName = tagNames[x];
				SimpleTreeNode parent = parents[x];
				
				Map<String, SimpleTreeNode> work = new HashMap<String, SimpleTreeNode>();
				result.put(mapName, work);
				
				//get the root elememt
				Element domRoot = dom.getDocumentElement();
				
				if ("viewer-config".equals(tagName)) {
					//create the "viewer-config" branch and add it to results map
					work.put("viewer-config", ModelUtils.createBranchViewerConfig(parent, configFile, domRoot));
					
					//create the "viewer-about" branch add it to results map
					work.put("viewer-about", ModelUtils.createBranchViewerAbout(parent, Viewer.appInfo));
					
					//create the "viewer-debug" branch add it to results map
					work.put("viewer-debug", ModelUtils.createBranchViewerDebugInfo(parent));
					
					//set the (static) viewer-config (for quick/easy reference elsewhere)
					Viewer.viewerConfig = (ViewerConfig)((SimpleTreeNode)work.get("viewer-config")).getUserObject();
				} else {
					//get a nodelist of <tagName> elements
					NodeList domNodeList = domRoot.getElementsByTagName(tagName);
					if (domNodeList != null && domNodeList.getLength() > 0) {
						for (int i = 0; i < domNodeList.getLength(); i++) {
							//get the node
							Node domNode = (Node)domNodeList.item(i);
							if (domNode.getNodeType() == Node.ELEMENT_NODE) {
								//get the element
								Element domElement = (Element)domNode;
								
								//get the value object
								if ("action".equals(tagName)) {
									SimpleTreeNode sam = ModelUtils.createBranchStrutsActionMapping(parent, configFile, domElement);
									
									//add it to results map
									work.put(((StrutsActionMapping)sam.getUserObject()).getPath(), sam);
								} else
								if ("form-bean".equals(tagName)) {
									SimpleTreeNode safb = ModelUtils.createBranchStrutsActionFormBean(parent, configFile, domElement);
									
									//add it to results map
									work.put(((StrutsActionFormBean)safb.getUserObject()).getName(), safb);
								} else
								if ("definition".equals(tagName)) {
									SimpleTreeNode td = ModelUtils.createBranchTilesDefinition(parent, configFile, domElement);
									
									//add it to results map
									work.put(((TilesDefinition)td.getUserObject()).getName(), td);
								}
							}
						}
					}
				}
			}

			// Update the job status
			jobStatus = ParseDocumentJob.STATUS_FINISHED;
		} catch (Throwable t) {
			// Update the job status
			jobStatus = ParseDocumentJob.STATUS_ERRORED;
		}
		return result;
	}

}
