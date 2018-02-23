package util;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import model.AbstractVO;
import model.ClassTypeInfo;
import model.SimpleTreeNode;
import model.StrutsActionFormBean;
import model.StrutsActionForward;
import model.StrutsActionMapping;
import model.TilesAttribute;
import model.TilesDefinition;
import model.ViewerConfig;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import concurrent.ThreadPoolFactory;

import util.classloading.MyLoadedClass;
import view.Viewer;


/**
 * Utilities for model manipulation.
 */
public class ModelUtils {

	private static final String PERCENTAGE_SIGN = "%";

	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");
	
	/**
	 * Class to use as the 'base' location when attempting to find/access resources.
	 */
	private static Class resourceBaseClass = Viewer.class;
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Create a <code>SimpleTreeNode</code> having 'viewer-about' information.
	 * 
	 * @param parent  SimpleTreeNode reference to the parent node.
	 * @param appInfo Object array containing information about the application.
	 * @return SimpleTreeNode containing the corresponding information.
	 */
	public static SimpleTreeNode createBranchViewerAbout(SimpleTreeNode parent, Object[] appInfo) {
		SimpleTreeNode va = new SimpleTreeNode("viewer-about", parent);
		va.add(new SimpleTreeNode("applicationName="         +appInfo[0], parent));
		va.add(new SimpleTreeNode("applicationVersion="  +"v"+appInfo[1], parent));
		va.add(new SimpleTreeNode("applicationBuildDate="    +appInfo[2], parent));
		va.add(new SimpleTreeNode("applicationAuthorContact="+appInfo[3], parent));
		
		return va;
	}
	
	/**
	 * Create a <code>SimpleTreeNode</code> having 'viewer-config' information.
	 * 
	 * @param parent           SimpleTreeNode reference to the parent node.
	 * @param configFileTarget String containing the (path and) name of the corresponding configuration file.
	 * @param domElement       Element object containing parsed data for the <code>ViewerConfig</code>.
	 * @return SimpleTreeNode containing the corresponding information.
	 */
	public static SimpleTreeNode createBranchViewerConfig(SimpleTreeNode parent, String configFileTarget, Element domElement) {
		ViewerConfig vc = new ViewerConfig();
		vc.setConfigFileTarget(configFileTarget);
		
		List targetAppConfigFileMasks = new ArrayList();
		vc.setTargetAppConfigFileMasks(targetAppConfigFileMasks);
		vc.setTargetAppConfigFilesByMask(null);

		List targetAppSourceCodePathMasks = new ArrayList();
		vc.setTargetAppSourceCodePathMasks(targetAppSourceCodePathMasks);
		vc.setTargetAppSourceCodePathsByMask(null);
		
		List targetAppObjectCodePathMasks = new ArrayList();
		vc.setTargetAppObjectCodePathMasks(targetAppObjectCodePathMasks);
		vc.setTargetAppObjectCodePathsByMask(null);
		
		List targetAppClasspathPathMasks = new ArrayList();
		vc.setTargetAppClasspathPathMasks(targetAppClasspathPathMasks);
		vc.setTargetAppClasspathPathsByMask(null);
		
		NodeList domChildren = domElement.getChildNodes();
		for (int x = 0; x < domChildren.getLength(); x++) {
			Node domChild = domChildren.item(x);
			short  domNodeType  = domChild.getNodeType();
			String domNodeName  = domChild.getNodeName();
			String domNodeValue = (domNodeType != Node.ELEMENT_NODE?null:domChild.getChildNodes().getLength() == 0?null:domChild.getFirstChild().getNodeValue());
			if (domNodeName  != null) domNodeName  = domNodeName.trim();
			if (domNodeValue != null) domNodeValue = domNodeValue.trim();
			if (domNodeType == Node.ELEMENT_NODE) {
				if ("target-app-config-file-base-path".equals(domNodeName)) {
					domNodeValue = normalizeTarget(domNodeValue);
					vc.setTargetAppConfigFileBasePath(domNodeValue);
					vc.setTargetAppConfigFileBasePathMissing(MiscUtils.confirmResourceMissing(resourceBaseClass, domNodeValue));
				} else
				if ("target-app-config-file-masks".equals(domNodeName)) {
					String[] work = domNodeValue.split(",");
					for (int y = 0; y < work.length; y++) {
						targetAppConfigFileMasks.add(work[y].trim());
					}
					vc.setTargetAppConfigFilesByMask(createFileRelatedMapFromList(vc.getTargetAppConfigFileBasePath(), vc.getTargetAppConfigFileMasks(), true));
				} else
				if ("target-app-source-code-base-path".equals(domNodeName)) {
					domNodeValue = normalizeTarget(domNodeValue);
					vc.setTargetAppSourceCodeBasePath(domNodeValue);
					vc.setTargetAppSourceCodeBasePathMissing(MiscUtils.confirmResourceMissing(resourceBaseClass, domNodeValue));
				} else
				if ("target-app-source-code-path-masks".equals(domNodeName)) {
					String[] work = domNodeValue.split(",");
					for (int y = 0; y < work.length; y++) {
						targetAppSourceCodePathMasks.add(work[y].trim());
					}
					vc.setTargetAppSourceCodePathsByMask(createFileRelatedMapFromList(vc.getTargetAppSourceCodeBasePath(), vc.getTargetAppSourceCodePathMasks(), false));
				} else
				if ("target-app-object-code-base-path".equals(domNodeName)) {
					domNodeValue = normalizeTarget(domNodeValue);
					vc.setTargetAppObjectCodeBasePath(domNodeValue);
					vc.setTargetAppObjectCodeBasePathMissing(MiscUtils.confirmResourceMissing(resourceBaseClass, domNodeValue));
				} else
				if ("target-app-object-code-path-masks".equals(domNodeName)) {
					String[] work = domNodeValue.split(",");
					for (int y = 0; y < work.length; y++) {
						targetAppObjectCodePathMasks.add(work[y].trim());
					}
					vc.setTargetAppObjectCodePathsByMask(createFileRelatedMapFromList(vc.getTargetAppObjectCodeBasePath(), vc.getTargetAppObjectCodePathMasks(), false));
				} else
				if ("target-app-classpath-base-path".equals(domNodeName)) {
					domNodeValue = normalizeTarget(domNodeValue);
					vc.setTargetAppClasspathBasePath(domNodeValue);
					vc.setTargetAppClasspathBasePathMissing(MiscUtils.confirmResourceMissing(resourceBaseClass, domNodeValue));
				} else
				if ("target-app-classpath-path-masks".equals(domNodeName)) {
					String[] work = domNodeValue.split(",");
					for (int y = 0; y < work.length; y++) {
						targetAppClasspathPathMasks.add(work[y].trim());
					}
					vc.setTargetAppClasspathPathsByMask(createFileRelatedMapFromList(vc.getTargetAppClasspathBasePath(), vc.getTargetAppClasspathPathMasks(), true));
				} else
				if ("target-app-base-package".equals(domNodeName)) {
					vc.setTargetAppBasePackage(domNodeValue);
					String samplePath = null;
					for (Iterator it = vc.getTargetAppSourceCodePathsByMask().keySet().iterator(); it.hasNext() && samplePath == null;) {
						String mask  = (String)it.next();
						List   paths = (List  )vc.getTargetAppSourceCodePathsByMask().get(mask);
						for (Iterator itSub = paths.iterator(); itSub.hasNext() && samplePath == null;) {
							File path = (File)itSub.next();
							samplePath = path.getAbsolutePath();
						}
					}
					vc.setTargetAppBasePackageMissing(MiscUtils.confirmResourceMissing(resourceBaseClass, samplePath + "/" + domNodeValue.replaceAll("[.]", "/")));
				} else
				if ("text-editor-executable".equals(domNodeName)) {
					domNodeValue = normalizeTarget(domNodeValue);
					vc.setTextEditorExecutable(domNodeValue);
					vc.setTextEditorExecutableMissing(MiscUtils.confirmResourceMissing(resourceBaseClass, domNodeValue));
				} else
				if ("recurse-to-show-siblings".equals(domNodeName)) {
					vc.setRecurseToShowSiblings(new Boolean(domNodeValue).booleanValue());
				} else
				if ("threadpool".equals(domNodeName)) {
					NodeList domGrandChildren = domChild.getChildNodes();
					for (int y = 0; y < domGrandChildren.getLength(); y++) {
						Node domGrandChild = domGrandChildren.item(y);
						domNodeType  = domGrandChild.getNodeType();
						domNodeName  = domGrandChild.getNodeName();
						domNodeValue = (domNodeType != Node.ELEMENT_NODE?null:domGrandChild.getChildNodes().getLength() == 0?null:domGrandChild.getFirstChild().getNodeValue());
						if (domNodeName  != null) domNodeName  = domNodeName.trim();
						if (domNodeValue != null) domNodeValue = domNodeValue.trim();
						if (domNodeType == Node.ELEMENT_NODE) {
							if ("poolCoreSize".equals(domNodeName)) {
								vc.setThreadPoolOptionPoolCoreSize(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolOptionPoolCoreSize()) && StringUtils.isNumeric(vc.getThreadPoolOptionPoolCoreSize())) {
									ThreadPoolFactory.setTHREADPOOL_OPTION_POOL_CORE_SIZE(Short.parseShort(vc.getThreadPoolOptionPoolCoreSize()));
								}
							} else
							if ("poolMaximumSize".equals(domNodeName)) {
								vc.setThreadPoolOptionPoolMaximumSize(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolOptionPoolMaximumSize()) && StringUtils.isNumeric(vc.getThreadPoolOptionPoolMaximumSize())) {
									ThreadPoolFactory.setTHREADPOOL_OPTION_POOL_MAXIMUM_SIZE(Short.parseShort(vc.getThreadPoolOptionPoolMaximumSize()));
								}
							} else
							if ("queueMaximumSize".equals(domNodeName)) {
								vc.setThreadPoolOptionQueueMaximumSize(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolOptionQueueMaximumSize()) && StringUtils.isNumeric(vc.getThreadPoolOptionQueueMaximumSize())) {
									ThreadPoolFactory.setTHREADPOOL_OPTION_QUEUE_MAXIMUM_SIZE(Short.parseShort(vc.getThreadPoolOptionQueueMaximumSize()));
								}
							} else
							if ("queueFairOrder".equals(domNodeName)) {
								vc.setThreadPoolOptionQueueFairOrder(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolOptionQueueFairOrder())) {
									ThreadPoolFactory.setTHREADPOOL_OPTION_QUEUE_FAIR_ORDER(Boolean.parseBoolean(vc.getThreadPoolOptionQueueFairOrder()));
								}
							} else
							if ("preStartCoreThreads".equals(domNodeName)) {
								vc.setThreadPoolOptionPreStartCoreThreads(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolOptionPreStartCoreThreads()) && StringUtils.isNumeric(vc.getThreadPoolOptionPreStartCoreThreads())) {
									ThreadPoolFactory.setTHREADPOOL_OPTION_PRESTART_CORE_THREADS(Byte.parseByte(vc.getThreadPoolOptionPreStartCoreThreads()));
								}
							} else
							if ("keepAliveTime".equals(domNodeName)) {
								vc.setThreadPoolOptionKeepAliveTime(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolOptionKeepAliveTime()) && StringUtils.isNumeric(vc.getThreadPoolOptionKeepAliveTime())) {
									ThreadPoolFactory.setTHREADPOOL_OPTION_KEEP_ALIVE_TIME(Integer.parseInt(vc.getThreadPoolOptionKeepAliveTime()));
								}
							} else
							if ("queueType".equals(domNodeName)) {
								vc.setThreadPoolOptionQueueType(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolOptionQueueType()) && StringUtils.isNumeric(vc.getThreadPoolOptionQueueType())) {
									ThreadPoolFactory.setTHREADPOOL_OPTION_QUEUE_TYPE(Byte.parseByte(vc.getThreadPoolOptionQueueType()));
								}
							} else
							if ("rejectionPolicy".equals(domNodeName)) {
								vc.setThreadPoolOptionRejectionPolicy(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolOptionRejectionPolicy()) && StringUtils.isNumeric(vc.getThreadPoolOptionRejectionPolicy())) {
									ThreadPoolFactory.setTHREADPOOL_OPTION_REJECTION_POLICY(Byte.parseByte(vc.getThreadPoolOptionRejectionPolicy()));
								}
							} else
							if ("shutdownTimeoutThreshold".equals(domNodeName)) {
								vc.setThreadPoolShutdownTimeoutThreshold(domNodeValue);
								if (StringUtils.isNotEmpty(vc.getThreadPoolShutdownTimeoutThreshold()) && StringUtils.isNumeric(vc.getThreadPoolShutdownTimeoutThreshold())) {
									ThreadPoolFactory.setTHREADPOOL_SHUTDOWN_TIMEOUT_THRESHOLD(Integer.parseInt(vc.getThreadPoolShutdownTimeoutThreshold()));
								}
							}
						}
					}
				}
			}
		}
		return new SimpleTreeNode("viewer-config", parent, vc);
	}
	
	/**
	 * Normalize the specified target.
	 * <p>
	 *  NOTE: This includes:
	 *  <ul>
	 *   <li>Substituting environment variable names with their corresponding value.</li>
	 *  </ul>
	 * </p>
	 * 
	 * @param path String containing the target to normalize.
	 * @return String containing the normalized target.
	 */
	private static String normalizeTarget(String path) {
		String result = new String(path);
		if (StringUtils.isNotEmpty(result)) {
			String envVar = null;
			int[] offsets = new int[2];
			while (result.contains(PERCENTAGE_SIGN)) {
				offsets[0] = result.indexOf(PERCENTAGE_SIGN);
				offsets[1] = result.indexOf(PERCENTAGE_SIGN, offsets[0] + 1);
				if (offsets[0] < offsets[1]) {
					envVar = result.substring(offsets[0] + 1, offsets[1]);
					result = result.replaceAll(PERCENTAGE_SIGN + envVar + PERCENTAGE_SIGN, System.getenv(envVar));
				} else {
					result = result.replaceAll(PERCENTAGE_SIGN, StringUtils.EMPTY);
				}
			}
		}
		return result;
	}
	
	/**
	 * Create a <code>SimpleTreeNode</code> having 'viewer-debug' information.
	 * 
	 * @param parent SimpleTreeNode reference to the parent node.
	 * @return SimpleTreeNode containing the corresponding information.
	 */
	public static SimpleTreeNode createBranchViewerDebugInfo(SimpleTreeNode parent) {
		SimpleTreeNode vi = new SimpleTreeNode("viewer-debug", parent);
		vi.add(new SimpleTreeNode("missingResources=${size}", parent, MissingResources.getMissingResources()));
		
		return vi;
	}
	
	/**
	 * Create a <code>SimpleTreeNode</code> having <code>StrutsActionFormBean</code> information.
	 * 
	 * @param parent           SimpleTreeNode reference to the parent node.
	 * @param configFileTarget String containing the (path and) name of the corresponding configuration file.
	 * @param domElement       Element object containing parsed data for the <code>StrutsActionFormBean</code>.
	 * @return SimpleTreeNode containing the corresponding information.
	 */
	public static SimpleTreeNode createBranchStrutsActionFormBean(SimpleTreeNode parent, String configFileTarget, Element domElement) {
		ViewerConfig vc = Viewer.getViewerConfig();
		StrutsActionFormBean safb = new StrutsActionFormBean();
		safb.setConfigFileTarget(        configFileTarget);
		safb.setConfigFileTargetMissing( MiscUtils.confirmResourceMissing(resourceBaseClass, configFileTarget));
		safb.setName(                    domElement.getAttribute("name"));
		safb.setType(                    domElement.getAttribute("type"));
		safb.setTypeTarget(              MiscUtils.findResource(vc.getTargetAppSourceCodePathMasks(), vc.getTargetAppSourceCodePathsByMask(), safb.getType().replaceAll("[.]", "/") + ".java"));
		safb.setTypeTargetMissing(       MiscUtils.confirmResourceMissing(resourceBaseClass, safb.getTypeTarget()));
		MyLoadedClass myLoadedClass = ReflectUtils.getMyLoadedClass(safb.getType());
		if (myLoadedClass != null) {
			safb.setSuperClass(          myLoadedClass.getSuperClass()          );
			safb.setInterfaces(          myLoadedClass.getInterfaces()          );
			safb.setDeclaredClasses(     myLoadedClass.getDeclaredClasses()     );
			safb.setDeclaredConstructors(myLoadedClass.getDeclaredConstructors());
			safb.setDeclaredFields(      myLoadedClass.getDeclaredFields()      );
			safb.setDeclaredMethods(     myLoadedClass.getDeclaredMethods()     );
		}
		safb.setClassName(               domElement.getAttribute("className"));
		safb.setId(                      domElement.getAttribute("id"));
		safb.setDynamic(                 TreeUtils.getNodeAttributeBooleanValue(domElement, "dynamic"));
		
		return new SimpleTreeNode(/*"name="+*/safb.getName(), parent, safb, safb.isTypeTargetMissing()?"warn":"safb", safb.getTypeTarget());
	}
	
	/**
	 * Create a <code>SimpleTreeNode</code> having <code>StrutsActionMapping</code> information.
	 * 
	 * @param parent           SimpleTreeNode reference to the parent node.
	 * @param configFileTarget String containing the (path and) name of the corresponding configuration file.
	 * @param domElement       Element object containing parsed data for the <code>StrutsActionMapping</code>.
	 * @return SimpleTreeNode containing the corresponding information.
	 */
	public static SimpleTreeNode createBranchStrutsActionMapping(SimpleTreeNode parent, String configFileTarget, Element domElement) {
		ViewerConfig vc = Viewer.getViewerConfig();
		StrutsActionMapping sam = new StrutsActionMapping();
		sam.setConfigFileTarget(           configFileTarget);
		sam.setConfigFileTargetMissing(    MiscUtils.confirmResourceMissing(resourceBaseClass, configFileTarget));
		sam.setName(                       domElement.getAttribute("name"));
		sam.setType(                       domElement.getAttribute("type"));
		sam.setTypeTarget(                 MiscUtils.findResource(vc.getTargetAppSourceCodePathMasks(), vc.getTargetAppSourceCodePathsByMask(), sam.getType().replaceAll("[.]", "/") + ".java"));
		sam.setTypeTargetMissing(          MiscUtils.confirmResourceMissing(resourceBaseClass, sam.getTypeTarget()));
		sam.setPath(                       domElement.getAttribute("path"));
		sam.setScope(                      domElement.getAttribute("scope"));
		sam.setParameter(                  domElement.getAttribute("parameter"));
		sam.setValidate(                   TreeUtils.getNodeAttributeBooleanValue(domElement, "validate"));
		Map safs = new TreeMap();
		sam.setActionForwards(safs);
		SimpleTreeNode result = new SimpleTreeNode(sam.getPath(), parent, sam, sam.isTypeTargetMissing()?"warn":"sam", sam.getTypeTarget());
		NodeList domNodeList = domElement.getElementsByTagName("forward");
		for (int x = 0; x < domNodeList.getLength(); x++) {
			Node node = domNodeList.item(x);
			StrutsActionForward saf = new StrutsActionForward();
			saf.setName(                   TreeUtils.getNodeAttributeStringValue( node, "name"           ));
			saf.setPath(                   TreeUtils.getNodeAttributeStringValue( node, "path"           ));
			saf.setRedirect(               TreeUtils.getNodeAttributeBooleanValue(node, "redirect"       ));
			saf.setActionMapping(          sam                                                            );
			saf.setClassName(              TreeUtils.getNodeAttributeStringValue( node, "className"      ));
			saf.setContextRelative(        TreeUtils.getNodeAttributeBooleanValue(node, "contextRelative"));
			saf.setId(                     TreeUtils.getNodeAttributeStringValue( node, "id"             ));
			saf.setConfigFileTarget(       configFileTarget                                               );
			saf.setConfigFileTargetMissing(sam.isConfigFileTargetMissing()                             );
			safs.put(saf.getName(), new SimpleTreeNode(sam.getPath()+"@"+saf.getName(), result, saf, "saf"));
		}
		MyLoadedClass myLoadedClass = ReflectUtils.getMyLoadedClass(sam.getType());
		if (myLoadedClass != null) {
			sam.setSuperClass(             myLoadedClass.getSuperClass()          );
			sam.setInterfaces(             myLoadedClass.getInterfaces()          );
			sam.setDeclaredClasses(        myLoadedClass.getDeclaredClasses()     );
			sam.setDeclaredConstructors(   myLoadedClass.getDeclaredConstructors());
			sam.setDeclaredFields(         myLoadedClass.getDeclaredFields()      );
			sam.setDeclaredMethods(        myLoadedClass.getDeclaredMethods()     );
		}
		sam.setAttribute(                  domElement.getAttribute("attribute"));
		sam.setClassName(                  domElement.getAttribute("className"));
		sam.setForward(                    domElement.getAttribute("forward"));
		sam.setId(                         domElement.getAttribute("id"));
		sam.setInclude(                    domElement.getAttribute("include"));
		sam.setInput(                      domElement.getAttribute("input"));
		sam.setPrefix(                     domElement.getAttribute("prefix"));
		sam.setRoles(                      domElement.getAttribute("roles"));
		sam.setSuffix(                     domElement.getAttribute("suffix"));
		sam.setUnknown(                    TreeUtils.getNodeAttributeBooleanValue(domElement, "unknown"));
		
		return result;
	}

	/**
	 * Create a <code>SimpleTreeNode</code> having <code>TilesDefinition</code> information.
	 * 
	 * @param parent           SimpleTreeNode reference to the parent node.
	 * @param configFileTarget String containing the (path and) name of the corresponding configuration file.
	 * @param domElement       Element object containing parsed data for the <code>TilesDefinition</code>.
	 * @return SimpleTreeNode containing the corresponding information.
	 */
	public static SimpleTreeNode createBranchTilesDefinition(SimpleTreeNode parent, String configFileTarget, Element domElement) {
		ViewerConfig vc = Viewer.getViewerConfig();
		TilesDefinition td = new TilesDefinition();
		td.setConfigFileTarget(       configFileTarget);
		td.setConfigFileTargetMissing(MiscUtils.confirmResourceMissing(resourceBaseClass, configFileTarget));
		td.setName(                   domElement.getAttribute("name"));
		td.setExtends(                domElement.getAttribute("extends"));
		td.setControllerClass(        domElement.getAttribute("controllerClass"));
		td.setControllerUrl(          domElement.getAttribute("controllerUrl"));
		td.setPage(                   domElement.getAttribute("page"));
		td.setPath(                   domElement.getAttribute("path"));
		td.setRole(                   domElement.getAttribute("role"));
		td.setTemplate(               domElement.getAttribute("template"));
		Map tas = new TreeMap();
		td.setAttributes(tas);
		SimpleTreeNode result = new SimpleTreeNode(td.getName(), parent, td, "td");
		NodeList domNodeList = domElement.getElementsByTagName("put");
		for (int x = 0; x < domNodeList.getLength(); x++) {
			Node domNode = domNodeList.item(x);
			TilesAttribute ta = new TilesAttribute();
			ta.setName(   TreeUtils.getNodeAttributeStringValue( domNode, "name"));
			ta.setValue(  TreeUtils.getNodeAttributeStringValue( domNode, "value"));
			ta.setContent(TreeUtils.getNodeAttributeStringValue( domNode, "content"));
			ta.setDirect( TreeUtils.getNodeAttributeBooleanValue(domNode, "direct"));
			ta.setType(   TreeUtils.getNodeAttributeStringValue( domNode, "type"));
			String category   = "ta";
			String linkTarget = null;
			if (TilesDefinition.ATTR_PAGE_WORK.equals(ta.getName())) {
				String pageWork = ta.getValue().trim();
				if (!pageWork.startsWith("/")) pageWork = "/" + pageWork;
				category   = "jsp";
				linkTarget = MiscUtils.findResource(vc.getTargetAppSourceCodePathMasks(), vc.getTargetAppSourceCodePathsByMask(), pageWork);
			}
			tas.put(ta.getName(), new SimpleTreeNode(ta.getName()+"="+ta.getValue(), result, ta, category, linkTarget));
		}
		// TODO[jpt] Evaluate whether following logic (and fields in TilesDefinition) is still needed
		SimpleTreeNode treeNode = (SimpleTreeNode)tas.get(TilesDefinition.ATTR_PAGE_WORK);
		if (treeNode != null) {
			TilesAttribute taPageWork = (TilesAttribute)treeNode.getUserObject();
			if (taPageWork != null && StringUtils.isNotEmpty(taPageWork.getValue())) {
				String pageWork = taPageWork.getValue().trim();
				if (!pageWork.startsWith("/")) pageWork = "/" + pageWork;
				td.setPageWorkTarget(       MiscUtils.findResource(vc.getTargetAppSourceCodePathMasks(), vc.getTargetAppSourceCodePathsByMask(), pageWork));
				td.setPageWorkTargetMissing(MiscUtils.confirmResourceMissing(resourceBaseClass, td.getPageWorkTarget()));
			}
		}
		
		return result;
	}
	
	/**
	 * Create/add a <code>SimpleTreeNode</code> branch having <code>Class</code> information
	 * for the specified <code>AbstractVO</code> to the specified parent node.
	 * 
	 * @param parent SimpleTreeNode reference to the parent node.
	 * @param avo    AbstractVO containing the parsed data for the information to be used.
	 * @see #createBranchClass(SimpleTreeNode, Class, Class[], Class[], Constructor[], Field[], Method[])
	 */
    public static void createBranchClassFromAbstractVO(SimpleTreeNode parent, AbstractVO avo) {
		if (parent != null && avo != null) {
			Class         superClass           = null;
			Class[]       interfaces           = null;
			Class[]       declaredClasses      = null;
			Constructor[] declaredConstructors = null;
			Field[]       declaredFields       = null;
			Method[]      declaredMethods      = null;
			if (avo instanceof StrutsActionFormBean) {
				StrutsActionFormBean safb = (StrutsActionFormBean)avo;
				superClass                        = safb.getSuperClass();
				interfaces                        = safb.getInterfaces();
				declaredClasses                   = safb.getDeclaredClasses();
				declaredConstructors              = safb.getDeclaredConstructors();
				declaredFields                    = safb.getDeclaredFields();
				declaredMethods                   = safb.getDeclaredMethods();
			} else /*if (avo instanceof StrutsActionMapping)*/ {
				StrutsActionMapping sam = (StrutsActionMapping)avo;
				superClass                      = sam.getSuperClass();
				interfaces                      = sam.getInterfaces();
				declaredClasses                 = sam.getDeclaredClasses();
				declaredConstructors            = sam.getDeclaredConstructors();
				declaredFields                  = sam.getDeclaredFields();
				declaredMethods                 = sam.getDeclaredMethods();
			}
			createBranchClass(parent, superClass, interfaces, declaredClasses, declaredConstructors, declaredFields, declaredMethods);
		}
    }
    
	/********************
	 * Helper method(s) *
	 ********************/
	
    /**
	 * Create/add a <code>SimpleTreeNode</code> branch having <code>Class</code> information
	 * based on the specified information to the specified parent node.
	 * 
	 * @param parent               SimpleTreeNode reference to the parent node.
     * @param superClass           Class                 containing a reference to the superClass.
     * @param interfaces           Array of Interfaces   containing all interfaces.
     * @param declaredClasses      Array of Classes      containing all (inner) classes.
     * @param declaredConstructors Array of Constructors containing all constructors.
     * @param declaredFields       Array of Fields       containing all fields.
     * @param declaredMethods      Array of Methods      containing all methods.
     */
    private static void createBranchClass(SimpleTreeNode parent,
    		Class superClass, Class[] interfaces,
    		Class[] declaredClasses, Constructor[] declaredConstructors,
    		Field[] declaredFields, Method[] declaredMethods) {
    	if (parent != null) {
    		/***********
    		 * Methods *
    		 ***********/
    		Set getters = new TreeSet();
    		Set setters = new TreeSet();
    		if (declaredMethods != null /*&& declaredMethods.length > 0*/) {
    			SimpleTreeNode declaredMethodsBranch = new SimpleTreeNode("Method(s)", parent);
    			parent.insert(declaredMethodsBranch, 0);
    			for (int x = 0; x < declaredMethods.length; x++) {
    				createBranchClassMethod(declaredMethodsBranch, null, declaredMethods[x], getters, setters);
    			}
    		}
    		/**********
    		 * Fields *
    		 **********/
    		if (declaredFields != null /*&& declaredFields.length > 0*/) {
    			SimpleTreeNode declaredFieldsBranch = new SimpleTreeNode("Field(s)", parent);
    			parent.insert(declaredFieldsBranch, 0); // Force to occur before methods
    			for (int x = 0; x < declaredFields.length; x++) {
    				createBranchClassField(declaredFieldsBranch, null, declaredFields[x], getters, setters);
    			}
    		}
    		/****************
    		 * Constructors *
    		 ****************/
    		if (declaredConstructors != null /*&& declaredConstructors.length > 0*/) {
    			SimpleTreeNode declaredConstructorsBranch = new SimpleTreeNode("Constructor(s)", parent);
    			parent.insert(declaredConstructorsBranch, 0);
    			for (int x = 0; x < declaredConstructors.length; x++) {
    				createBranchClassConstructor(declaredConstructorsBranch, null, declaredConstructors[x]);
    			}
    		}
    		/***********
    		 * Classes *
    		 ***********/
    		if (declaredClasses != null /*&& declaredClasses.length > 0*/) {
    			SimpleTreeNode declaredClassesBranch = new SimpleTreeNode("Class(es)", parent);
    			parent.insert(declaredClassesBranch, 0);
    			for (int x = 0; x < declaredClasses.length; x++) {
    				createBranchClassClass(declaredClassesBranch, null, declaredClasses[x]);
    			}
    		}
    		/**************
    		 * Interfaces *
    		 **************/
    		if (interfaces != null /*&& interfaces.length > 0*/) {
    			SimpleTreeNode interfacesBranch = new SimpleTreeNode("Interface(s)", parent);
    			parent.insert(interfacesBranch, 0);
    			for (int x = 0; x < interfaces.length; x++) {
    				createBranchClassClass(interfacesBranch, null, interfaces[x]);
    			}
    		}
    		/*******************
    		 * Additional Info *
    		 *******************/
    		if (superClass != null) {
    			SimpleTreeNode additionalInfoBranch = new SimpleTreeNode("Additional Info", parent);
    			parent.insert(additionalInfoBranch, 0);
    			createBranchClassClass(additionalInfoBranch, "superClass="+superClass.getName(), superClass);
    		//	additionalInfoBranch.add(new SimpleTreeNode("classDepth="+getClassDepth(superClass), additionalInfoBranch));
    		}
    		/***********************************
    		 * Remove any/all empty branch(es) *
    		 ***********************************/
    		for (int x = parent.getChildCount() - 1; x >= 0; x--) {
    			if (parent.getChildAt(x).getChildCount() == 0) {
    				parent.remove(x);
    			}
    		}
    	}
    }
    
//	/**
//	 * Determine the class depth (how many superclass ancestors exist above it).
//	 *  
//	 * @param clazz Class to evaluate.
//	 * @return Byte indicating the number of superclass levels that exist above the specified class.
//	 */
//	private static byte getClassDepth(Class clazz) {
//		byte result = 0;
//		Class work = clazz;
//		while (work != null) {
//			result++;
//			work = work.getSuperclass();
//		}
//		return result;
//	}

	/**
	 * Create/add a branch for the specified Method to the specified parent branch.
	 * 
	 * @param parent  SimpleTreeNode referencing the parent of the new branch.
	 * @param label   String containing a specific value for the label (when empty 'name' is used).
	 * @param method  Method containing information for the new branch.
	 * @param getters Set of getters which can be updated here if the 'method' is a getter method.
	 * @param setters Set of setters which can be updated here if the 'method' is a setter method.
	 */
    private static void createBranchClassMethod(SimpleTreeNode parent, String label, Method method, Set getters, Set setters) {
		if (parent != null && method != null) {
			String name = method.getName();
			// Update set of getters/setters accordingly
			boolean isGetterSetter = false;
			int offset = 3;
			if ((name.startsWith("get") || name.startsWith("is")) &&
					method.getParameterTypes().length == 0 &&
					method.getReturnType() != null) {
				if (name.startsWith("is")) offset = 2;
				getters.add(name.substring(offset).toLowerCase());
				isGetterSetter = true;
			} else
			if (name.startsWith("set") &&
					method.getParameterTypes().length == 1 &&
					method.getReturnType() == Void.TYPE) {
				setters.add(name.substring(offset).toLowerCase());
				isGetterSetter = true;
			}
			// Skip getters/setters in the method list
			if (!isGetterSetter) {
				ClassTypeInfo classTypeInfo = getClassTypeInfo(method.getReturnType(), true, true);
				String formattedName = method.getName().substring(method.getName().lastIndexOf('.') + 1) + "(" + getClassTypeArrayAsString(method.getParameterTypes(), false, false) + ") : " + classTypeInfo.getOverview();
				// Add branch for this method
				SimpleTreeNode declaredMethodBranch = new SimpleTreeNode(label != null?label:formattedName, parent, null, getScopeBasedCategory(false, Modifier.toString(method.getModifiers())));
				parent.add(declaredMethodBranch);
				// Add info branches
//				declaredMethodBranch.add(new SimpleTreeNode("isAccessible="   + declaredMethod.isAccessible()                   , declaredMethodBranch));
//				declaredMethodBranch.add(new SimpleTreeNode("declaringClass=" + declaredMethod.getDeclaringClass().getName()    , declaredMethodBranch));
				declaredMethodBranch.add(new SimpleTreeNode("modifiers="      + Modifier.toString(method.getModifiers()), declaredMethodBranch));
				declaredMethodBranch.add(new SimpleTreeNode("name="           + formattedName                                   , declaredMethodBranch));
				createBranchClassTypeArray(method.getParameterTypes(), true, true, declaredMethodBranch, "parameterTypes=" + getClassTypeArrayAsString(method.getParameterTypes(), false, false));
				createBranchClassTypeArray(method.getExceptionTypes(), true, true, declaredMethodBranch, "exceptionTypes=" + getClassTypeArrayAsString(method.getExceptionTypes(), false, false));
				declaredMethodBranch.add(new SimpleTreeNode("returnType=" + classTypeInfo.getOverview(), declaredMethodBranch, null, classTypeInfo.getCategory(), classTypeInfo.getLinkTarget()));
			}
		}
    }
    
	/**
	 * Create/add a branch for the specified Field to the specified parent branch.
	 * 
	 * @param parent  SimpleTreeNode referencing the parent of the new branch.
	 * @param label   String containing a specific value for the label (when empty 'name' is used).
	 * @param field   Field containing information for the new branch.
	 * @param getters Set of getters which is used to determine if the 'field' contains a getter method.
	 * @param setters Set of setters which is used to determine if the 'field' contains a setter method.
	 */
    private static void createBranchClassField(SimpleTreeNode parent, String label, Field field, Set getters, Set setters) {
    	if (parent != null && field != null) {
			String name = field.getName();
			// Determine whether this field has getter/setter
			boolean hasGetter = getters.contains(name.toLowerCase());
			boolean hasSetter = setters.contains(name.toLowerCase());
			String  category = null;
			if (hasGetter && hasSetter) category = "classGS";
			else if (hasGetter) category = "classG";
			else if (hasSetter) category = "classS";
			// Add branch for this field
			ClassTypeInfo classTypeInfo = getClassTypeInfo(field.getType(), true, true);
			SimpleTreeNode fieldBranch = new SimpleTreeNode(label != null?label:field.getName() + " : " + classTypeInfo.getOverview(), parent, null, getScopeBasedCategory(true, Modifier.toString(field.getModifiers())));
			parent.add(fieldBranch);
			// Add info branches
//			fieldBranch.add(new SimpleTreeNode("isAccessible="   + field.isAccessible()                   , parent));
//			fieldBranch.add(new SimpleTreeNode("declaringClass=" + field.getDeclaringClass().getName()    , parent));
			fieldBranch.add(new SimpleTreeNode("name="           + name                                   , parent, null, category));
			fieldBranch.add(new SimpleTreeNode("modifiers="      + Modifier.toString(field.getModifiers()), parent));
			fieldBranch.add(new SimpleTreeNode("type=" + classTypeInfo.getOverview(), parent, null, classTypeInfo.getCategory(), classTypeInfo.getLinkTarget()));
    	}
    }
    
	/**
	 * Create/add a branch for the specified Constructor to the specified parent branch.
	 * 
	 * @param parent      SimpleTreeNode referencing the parent of the new branch.
	 * @param label       String containing a specific value for the label (when empty 'name' is used).
	 * @param constructor Constructor containing information for the new branch.
	 */
    private static void createBranchClassConstructor(SimpleTreeNode parent, String label, Constructor constructor) {
    	if (parent != null && constructor != null) {
			// Skip constructors w/o names
			if (StringUtils.isNotEmpty(constructor.getName())) {
				String formattedName = constructor.getName().substring(constructor.getName().lastIndexOf('.') + 1) + "(" + getClassTypeArrayAsString(constructor.getParameterTypes(), false, false) + ")";
				// Add branch for this method
				SimpleTreeNode constructorBranch = new SimpleTreeNode(label != null?label:formattedName, parent, null, getScopeBasedCategory(false, Modifier.toString(constructor.getModifiers())));
				parent.add(constructorBranch);
				// Add info branches
//				constructorBranch.add(new SimpleTreeNode("isArray="        + constructor.isAccessible()                   , constructorBranch));
//				constructorBranch.add(new SimpleTreeNode("declaringClass=" + constructor.getDeclaringClass().getName()    , constructorBranch));
				constructorBranch.add(new SimpleTreeNode("modifiers="      + Modifier.toString(constructor.getModifiers()), constructorBranch));
				constructorBranch.add(new SimpleTreeNode("name="           + formattedName                                , constructorBranch));
				createBranchClassTypeArray(constructor.getParameterTypes(), true, true, constructorBranch, "parameterTypes=" + getClassTypeArrayAsString(constructor.getParameterTypes(), false, false));
				createBranchClassTypeArray(constructor.getExceptionTypes(), true, true, constructorBranch, "exceptionTypes=" + getClassTypeArrayAsString(constructor.getExceptionTypes(), false, false));
			}
    	}
    }
    
	/**
	 * Create/add a branch for the specified Class to the specified parent branch.
	 * 
	 * @param parent SimpleTreeNode referencing the parent of the new branch.
	 * @param label  String containing a specific value for the label (when empty 'name' is used).
	 * @param clazz  Class containing information for the new branch.
	 */
	private static void createBranchClassClass(SimpleTreeNode parent, String label, Class clazz) {
		if (parent != null && clazz != null) {
			// Skip classes w/o names
			if (StringUtils.isNotEmpty(clazz.getName())) {
				ClassTypeInfo classTypeInfo = getClassTypeInfo(clazz, true, true);
				String        className     = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
				String        classPackage  = null;
				if (clazz.getPackage() != null) classPackage = clazz.getPackage().getName().substring(clazz.getPackage().getName().lastIndexOf(' ') + 1);
				// Add branch for this method
				SimpleTreeNode classBranch = new SimpleTreeNode(label != null?label:clazz.getName(), parent, null, classTypeInfo.getCategory(), classTypeInfo.getLinkTarget());
				parent.add(classBranch);
				// Add info branches
//				classBranch.add(new SimpleTreeNode("isArray="        + clazz.isArray()                        , classBranch));
//				classBranch.add(new SimpleTreeNode("isPrimitive="    + clazz.isPrimitive()                    , classBranch));
//				classBranch.add(new SimpleTreeNode("declaringClass=" + clazz.getDeclaringClass().getName()    , classBranch));
				classBranch.add(new SimpleTreeNode("modifiers="      + Modifier.toString(clazz.getModifiers()), classBranch));
				classBranch.add(new SimpleTreeNode("name="           + className                              , classBranch));
				classBranch.add(new SimpleTreeNode("package="        + classPackage                           , classBranch));
				classBranch.add(new SimpleTreeNode("isInterface="    + clazz.isInterface()                    , classBranch));
				createBranchClassTypeArray(clazz.getInterfaces(), true, true, classBranch, "interfaces=" + getClassTypeArrayAsString(  clazz.getInterfaces(), false, false));
				createBranchObjectArray(   clazz.getSigners()               , classBranch, "signers="    + getObjectArrayAsString(clazz.getSigners()                 ));
				classBranch.add(new SimpleTreeNode("type=" + classTypeInfo.getOverview(), parent, null, classTypeInfo.getCategory(), classTypeInfo.getLinkTarget()));
//				if (clazz.getSuperclass() != null && !TreeUtils.existsAsAncestor(new SimpleTreeNode(clazz.getSuperclass().getName()), parent)) { // End recursion
//					createBranchClassProperties(classBranch, clazz.getSuperclass(), clazz.getInterfaces(), clazz.getDeclaredClasses(), clazz.getDeclaredConstructors(), clazz.getDeclaredFields(), clazz.getDeclaredMethods());
//				}
			}
		}
	}
	
	/**
	 * Convert a types array into a <code>SimpleTreeNode</code> of nicely formatted values.
	 * 
	 * @param typeArray    Class array of types (parameters, exceptions, interfaces, etc).
	 * @param objectTypes  Boolean indicating whether to include object types (primitive, class, interface).
	 * @param packageNames Boolean indicating whether to include package names.
	 * @param parent       SimpleTreeNode reference to the parent of the new immediate child node.
	 * @param childLabel   String containing the label to use for the immediate child node.
	 * @see #getTypeAsString(Class, boolean, boolean)
	 * @see #getTypeLinkTarget(Class)
	 */
	private static void createBranchClassTypeArray(Class[] typeArray, boolean objectTypes, boolean packageNames, SimpleTreeNode parent, String childLabel) {
		if (typeArray != null && typeArray.length > 0 && parent != null) {
	    	SimpleTreeNode typesBranch = new SimpleTreeNode(childLabel, parent, null, "attr");
	    	parent.add(typesBranch);
			for (int x = 0; x < typeArray.length; x++) {
				ClassTypeInfo classTypeInfo = getClassTypeInfo(typeArray[x], objectTypes, packageNames);
				typesBranch.add(new SimpleTreeNode(classTypeInfo.getOverview(), parent, null, classTypeInfo.getCategory(), classTypeInfo.getLinkTarget()));
			//	createBranchClass(typesBranch, typeArray[x]);
			//	createBranchClassProperties(typesBranch, typeArray[x].getSuperclass(), typeArray[x].getInterfaces(), typeArray[x].getDeclaredClasses(), typeArray[x].getDeclaredConstructors(), typeArray[x].getDeclaredFields(), typeArray[x].getDeclaredMethods());
			}
		}
	}

	/**
	 * Convert an object array into a <code>SimpleTreeNode</code> of nicely formatted values.
	 * 
	 * @param objectArray  Object array of generic values.
	 * @param parent       SimpleTreeNode reference to the parent of the new immediate child node.
	 * @param childLabel   String containing the label to use for the immediate child node.
	 * @see #createBranchClassTypeArray(Class[], boolean, boolean, SimpleTreeNode, String)
	 */
	private static void createBranchObjectArray(Object[] objectArray, SimpleTreeNode parent, String childLabel) {
		if (objectArray != null && objectArray.length > 0 && parent != null) {
	    	SimpleTreeNode objectsBranch = new SimpleTreeNode(childLabel, parent, objectArray);
	    	parent.add(objectsBranch);
		}
	}
	
	/**
	 * Obtain info pertaining to the specified type.
	 * 
	 * @param type         Class for the type.
	 * @param objectTypes  Boolean indicating whether to include object types (primitive, array, class, interface).
	 * @param packageNames Boolean indicating whether to include package names.
	 * @return String array containing the information described above.
	 */
	private static ClassTypeInfo getClassTypeInfo(Class type, boolean objectTypes, boolean packageNames) {
		ClassTypeInfo result = new ClassTypeInfo();
		if (type != null) {
			final String BRACKET_L = "[";
			final String BRACKET_R = "]";
			final String SPACE     = " ";
		//	final String DOLLAR    = "$";
			final String PERIOD    = ".";
			final String CLASS     = "class";
			final String PRIMITIVE = "primitive";
			final String ARRAY     = "array";
			final String INTERFACE = "interface";
			final String VOID      = "void";
			
			String objectType  = null;
			String objectClass = null;
			
			/************
			 * Overview *
			 ************/
			StringBuffer overview = new StringBuffer();
			if (type == Void.TYPE) {
				if (objectTypes) {
					overview.append(BRACKET_L + VOID + BRACKET_R);
				}
			} else {
				objectType  = type.isPrimitive()?PRIMITIVE:type.isArray()?ARRAY:type.isInterface()?INTERFACE:CLASS; 
				objectClass = type.getName();
				// Work-around for Object arrays
				if (type.isArray()) {
					final String ARRAY_PREFIX = "[L";
					final String ARRAY_SUFFIX = ";";
					int firstPrefix = objectClass.indexOf(ARRAY_PREFIX);
					if (firstPrefix >= 0) {
						int firstSuffix = objectClass.indexOf(ARRAY_SUFFIX, firstPrefix);
						if (firstSuffix >= 0) {
							objectClass = objectClass.substring(firstPrefix + ARRAY_PREFIX.length(), firstSuffix);
						}
					}
				}
				if (!packageNames) {
					int lastSeparator = -1; //objectClass.lastIndexOf(DOLLAR);
					if (lastSeparator < 0) lastSeparator = objectClass.lastIndexOf(PERIOD);
					objectClass = objectClass.substring(lastSeparator + 1);
				}
				if (objectTypes) {
					overview.append(BRACKET_L + objectType + BRACKET_R + SPACE);
				}
				overview.append(objectClass);
			}
			result.setOverview(overview.toString());
			
			/************
			 * Category *
			 ************/
    		result.setCategory(objectType);
			
    		/***************
    		 * Link Target *
    		 ***************/
    		String linkTarget = null;
    		if (type != Void.TYPE && !type.isPrimitive() && objectClass != null) {
   				final ViewerConfig vc = Viewer.getViewerConfig();
   				if (objectClass.startsWith(vc.getTargetAppBasePackage())) {
   					linkTarget = MiscUtils.findResource(vc.getTargetAppSourceCodePathMasks(), vc.getTargetAppSourceCodePathsByMask(), objectClass.replaceAll("[.]", "/") + ".java");
   					if (MiscUtils.getFileSystemObjectLastModified(linkTarget) == null) {
   						linkTarget = null;
   					}
    			}
    		}
    		result.setLinkTarget(linkTarget);
		}
		return result;
	}
	
//	/**
//	 * Obtain a link target for the specified class type.
//	 * 
//	 * @param type Class for the type.
//	 * @return String containing the link target for the specified type (or <code>null</code> when not applicable).
//	 */
//	private static String getClassTypeLinkTarget(Class type) {
//		String result = null;
//		if (type != null && type != Void.TYPE && !type.isPrimitive()) {
//			String[] typeInfo = type.toString().split(" ");
//			if (typeInfo.length >= 2) {
//				ViewerConfig vc = Viewer.getViewerConfig();
//				if (typeInfo[1].startsWith(vc.getTargetAppBasePackage())) {
//					result = vc.getTargetAppSourceCodeBasePath() + "/src/" + typeInfo[1].replaceAll("[.]", "/") + ".java";
//					if (MiscUtils.getFileSystemObjectLastModified(result) == null) {
//						result = null;
//					}
//				}
//			}
//		}
//		return result;
//	}

//	/**
//	 * Obtain the base category (primitive) for the specified class type.
//	 * 
//	 * @param type Class for the type.
//	 * @return String containing the category for the specified type (or <code>null</code> when not applicable).
//	 */
//	private static String getClassTypeCategory(Class type) {
//		String result = null;
//		if (type != null) {
//			String work = getClassTypeAsString(type, true, false);
//			if (work != null) {
//				String[] temp = work.split(" ");
//				result = temp[0].substring(1, temp[0].length() - 1);
//			}
//		}
//		return result;
//	}
    
//	/**
//	 * Convert a type into a nicely formatted value.
//	 * 
//	 * @param type         Class for the type.
//	 * @param objectTypes  Boolean indicating whether to include object types (primitive, array, class, interface).
//	 * @param packageNames Boolean indicating whether to include package names.
//	 * @return String containing a nicely formatted representation of the type.
//	 */
//	private static String getClassTypeAsString(Class type, boolean objectTypes, boolean packageNames) {
//		StringBuffer result = new StringBuffer();
//		if (type != null) {
//			final String BRACKET_L = "[";
//			final String BRACKET_R = "]";
//			final String SPACE     = " ";
//		//	final String DOLLAR    = "$";
//			final String PERIOD    = ".";
//			final String CLASS     = "class";
//			final String PRIMITIVE = "primitive";
//			final String ARRAY     = "array";
//			final String INTERFACE = "interface";
//			final String VOID      = "void";
//			if (type == Void.TYPE) {
//				if (objectTypes) {
//					result.append(BRACKET_L + VOID + BRACKET_R);
//				}
//			} else {
//				String objectType  = type.isPrimitive()?PRIMITIVE:type.isArray()?ARRAY:type.isInterface()?INTERFACE:CLASS;
//				String objectClass = type.getName();
//				// Work-around for Object arrays
//				if (type.isArray()) {
//					final String ARRAY_PREFIX = "[L";
//					final String ARRAY_SUFFIX = ";";
//					int firstPrefix = objectClass.indexOf(ARRAY_PREFIX);
//					if (firstPrefix >= 0) {
//						int firstSuffix = objectClass.indexOf(ARRAY_SUFFIX, firstPrefix);
//						if (firstSuffix >= 0) {
//							objectClass = objectClass.substring(firstPrefix + ARRAY_PREFIX.length(), firstSuffix);
//						}
//					}
//				}
//				if (!packageNames) {
//					int lastSeparator = -1; //objectClass.lastIndexOf(DOLLAR);
//					if (lastSeparator < 0) lastSeparator = objectClass.lastIndexOf(PERIOD);
//					objectClass = objectClass.substring(lastSeparator + 1);
//				}
//				if (objectTypes) {
//					result.append(BRACKET_L + objectType + BRACKET_R + SPACE);
//				}
//				result.append(objectClass);
//			}
//		}
//		return result.toString();
//	}
    
	/**
	 * Convert a class type array into a (comma-delimited) list of nicely formatted values.
	 * 
	 * @param typeArray    Class array of types (parameters, exceptions, interfaces, etc).
	 * @param objectTypes  Boolean indicating whether to include object types (primitive, class, interface).
	 * @param packageNames Boolean indicating whether to include package names.
	 * @return String containing a nicely formatted representation of the types.
	 * @see #getTypeAsString(Class, boolean, boolean)
	 */
	private static String getClassTypeArrayAsString(Class[] typeArray, boolean objectTypes, boolean packageNames) {
		StringBuffer result = new StringBuffer();
		if (typeArray != null && typeArray.length > 0) {
			final String SPACE = " ";
			final String COMMA = ",";
			for (int x = 0; x < typeArray.length; x++) {
				if (x > 0) result.append(COMMA + SPACE);
				result.append(getClassTypeInfo(typeArray[x], objectTypes, packageNames).getOverview());
			}
		}
    	return result.toString();
	}
	
	/**
	 * Convert an object array into a (comma-delimited) list of nicely formatted values.
	 * 
	 * @param objectArray Object array to convert.
	 * @return String containing a nicely formatted representation of the objects.
	 */
	private static String getObjectArrayAsString(Object[] objectArray) {
		String result = null;
		if (objectArray != null) {
			result = ArrayUtils.toString(objectArray);
			if (result != null && result.length() >= 2) {
				result = result.substring(1, result.length() - 1);
			}
		}
		return result;
	}

	/**
	 * Helper method which reads the file system to create a 'targetMap' (keyed by mask)
	 * with all files that match each mask contained in the 'sourceList' (List).
	 * 
	 * @param sourceBasePath String identifying the base path to use.
	 * @param sourceList     List containing the values to iterate over.
	 * @param onlyFiles      Boolean indicating whether to only include files (vs only include directories).
	 * @return Map keyed by each item in 'sourceList' and containing an ArrayList of all matching files. 
	 */
//	private static Map createFileRelatedMapFromList(String sourceBasePath, List sourceList, boolean onlyFiles) {
//		Map work = new TreeMap();
//		File dir = StringUtils.isEmpty(sourceBasePath)?null:new File(sourceBasePath);
//    	for (Iterator it = sourceList.iterator(); it.hasNext();) {
//    		String mask = (String)it.next();
//    		File   subDir  = dir;
//    		String subMask = mask.replaceAll("\\\\", "/");
//    		int lastSlash = mask.lastIndexOf("/");
//    		if (lastSlash > 0) {
//    			if (dir == null || mask.indexOf(":") >= 0) {
//    				subDir = new File(                              mask.substring(0, lastSlash + 1));
//    			} else {
//    				subDir = new File(dir.getAbsolutePath() + "/" + mask.substring(0, lastSlash + 1));
//    			}
//    			subMask = mask.substring(lastSlash + 1);
//    		}
//    		FileFilter fileFilter = new RegexFileFilter(subMask);
//    		File[]     files      = subDir.listFiles(fileFilter);
//    		Collection result     = new ArrayList();
//    		if (files != null) {
//    			for (int x = 0; x < files.length; x++) {
//    				File file = files[x];
//    				if ((onlyFiles && file.isFile()) ||
//    					(!onlyFiles && file.isDirectory())) {
//    					result.add(file);
//    				}
//    			}
//    		}
//    		work.put(mask, result);
//    	}
//    	return work;
//	}
	private static Map<String, Collection<File>> createFileRelatedMapFromList(String sourceBasePath, List<String> sourceList, boolean onlyFiles) {
		Map<String, Collection<File>> result = new TreeMap<String, Collection<File>>();
    	for (String mask : sourceList) {
    		createFileRelatedMapFromListUsingRecursion(sourceBasePath, StringUtils.EMPTY, mask, 0, onlyFiles, result);
    	}
    	return result;
	}
	
	private static void createFileRelatedMapFromListUsingRecursion(String basePath, String relPath, String mask, int maskIndex, boolean onlyFiles, Map<String, Collection<File>> result) {
		if (StringUtils.isNotEmpty(basePath)) {
			int        maskIndexEnd  = mask.indexOf("/", maskIndex + 1) < 0?mask.length():mask.indexOf("/", maskIndex + 1);
			File       file          = new File(basePath + relPath);
			FileFilter fileFilter    = new RegexFileFilter(mask.substring(maskIndex + (mask.charAt(maskIndex) == '/'?1:0), maskIndexEnd));
			File[]     matchingFiles = file.listFiles(fileFilter);
			if (matchingFiles != null && matchingFiles.length > 0) {
				for (File matchingFile : matchingFiles) {
					if (maskIndexEnd == mask.length()) {
						if ((onlyFiles && matchingFile.isFile()) || (!onlyFiles && matchingFile.isDirectory())) {
							Collection<File> work = result.get(mask);
							if (work == null) {
								work = new ArrayList<File>();
							}
							work.add(matchingFile);
							result.put(mask, work);
						}
					} else if (matchingFile.isDirectory()) {
						createFileRelatedMapFromListUsingRecursion(basePath, relPath + "/" + matchingFile.getName(), mask, maskIndexEnd, onlyFiles, result);
					}
				}
			}
		}
	}
	
    /**
     * Determine the appropriate category for the field/method based on its modifierText.
     * 
     * @param isField      Boolean indicating whether it's a field (vs method).
     * @param modifierText String containing the field/method modifiers.
     * @return String containing the corresponding category.
     */
    private static String getScopeBasedCategory(boolean isField, String modifierText) {
    	StringBuffer category = new StringBuffer();
    	if (modifierText != null) {
    		category.append(isField?"field":"method");
    		category.append(".");
    		if      (modifierText.indexOf("private"  ) >= 0) category.append("private"  );
    		else if (modifierText.indexOf("protected") >= 0) category.append("protected");
    		else if (modifierText.indexOf("public"   ) >= 0) category.append("public"   );
    		else                                             category.append("default"  );
    	}
    	return category.toString();
    }
    
}
