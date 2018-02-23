package model;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import util.ModelUtils;
import util.TreeUtils;
import view.Viewer;


/***
 * Provides minimum behavior to satisfy requirements of a (recursible) TreeModel.
 * 
 * @see AbstractTreeModel
 */
public class SimpleTreeModel extends AbstractTreeModel {
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public SimpleTreeModel() {
		super();
	}
	public SimpleTreeModel(Object root) {
		super(root);
	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Convenience method used to convert the specified 'nodeObject'
	 * into a <code>List</code> of children.
	 * 
	 * @param nodeObject Object to be converted.
	 * @return List containing the children for the specified 'nodeObject'.
	 * @see #convertObjectToList(Object, SimpleTreeNode)
	 */
	List convertObjectToList(Object nodeObject) {
		return convertObjectToList(nodeObject, null);
	}
	
	/**
	 * Convenience method used (via recursion) to convert the specified 'nodeObject'
	 * into a <code>List</code> of children.
	 * 
	 * @param nodeObject Object to be converted.
	 * @param parent     SimpleTreeNode reference to the 'nodeObject's parent.
	 * @return List containing the children for the specified 'nodeObject'.
	 * @see #convertObjectToList(Object)
	 */
	private List convertObjectToList(Object nodeObject, SimpleTreeNode parent) {
	//	final NodeInfo info  = (NodeInfo)(parent == null?null:(NodeInfo)parent.getInfo());
	//	final byte     depth = (byte    )(parent == null?0   :parent.getDepth());
		List result = null;
		if (nodeObject instanceof SimpleTreeNode) {
			/******************
			 * SimpleTreeNode *
			 ******************/
			SimpleTreeNode treeNode = (SimpleTreeNode)nodeObject;
			if (treeNode.children() != null) {
				result = Collections.list(treeNode.children());
			} else {
				result = convertObjectToList(treeNode.getUserObject(), (SimpleTreeNode)nodeObject);
			}
	//	} else if (nodeObject instanceof List) {
	//		result = (List)nodeObject;
	//	} else if (nodeObject instanceof Map) {
	//		result = new ArrayList(((Map)nodeObject).values());
	//	} else if (nodeObject instanceof Set) {
	//		result = new ArrayList(((Set)nodeObject).values());
		} else if (nodeObject instanceof StrutsActionForward) {
			/***********************
			 * StrutsActionForward *
			 ***********************/
			if (TreeUtils.existsAsAncestor(nodeObject, parent)) return result; // End recursion
		//	final boolean isPrimary = (info.getPanelIndex() == Viewer.INFO_ID_SAF);
		//	final byte    keyDepth  = (byte)((isPrimary?0:1)+(info.getGroupByIndex() == Viewer.GROUP_BY_SAF_BY_UNIQUE?1:2));
		//	if ((isPrimary && depth > 2) || (!isPrimary && depth > keyDepth)) return result;
		//	final String  qualifier = (depth <= keyDepth?"unique=":"");
			StrutsActionForward saf = (StrutsActionForward)nodeObject;
			parent.add(new SimpleTreeNode("name="            +saf.getName()                          ), saf.getName()            );
		  if (saf.getTilesDefinition() != null) {
			parent.add(new SimpleTreeNode("path="+saf.getPath(), null, saf.getTilesDefinition(), "td"), saf.getTilesDefinition());
		  } else {
			parent.add(new SimpleTreeNode("path="+saf.getPath()                                      ), saf.getPath());
		  }
			parent.add(new SimpleTreeNode("redirect="        +saf.getRedirect()                      ), saf.getRedirect()        );
			parent.add(new SimpleTreeNode("className="       +saf.getClassName()                     ), saf.getClassName()       );
			parent.add(new SimpleTreeNode("contextRelative=" +saf.getContextRelative()               ), saf.getContextRelative() );
			parent.add(new SimpleTreeNode("id="              +saf.getId()                            ), saf.getId()              );
			parent.add(new SimpleTreeNode("source="          +saf.getConfigFileTarget(), null, null, saf.isConfigFileTargetMissing()?"warn":"xml", saf.getConfigFileTarget()), saf.getConfigFileTarget());
		  if (saf.getActionMapping() != null) {
			StrutsActionMapping sam = saf.getActionMapping();
			parent.add(new SimpleTreeNode("path="+saf.getActionMapping().getPath(), null, sam, sam.isTypeTargetMissing()?"warn":"sam"), sam);
		  }
		} else if (nodeObject instanceof StrutsActionFormBean) {
			/************************
			 * StrutsActionFormBean *
			 ************************/
			if (TreeUtils.existsAsAncestor(nodeObject, parent)) return result; // End recursion
		//	final boolean isPrimary = (info.getPanelIndex() == Viewer.INFO_ID_SAFB);
		//	final byte    keyDepth  = (byte)((isPrimary?0:1)+(info.getGroupByIndex() == Viewer.GROUP_BY_SAFB_BY_NAME?1:2));
		//	if ((isPrimary && depth > 2) || (!isPrimary && depth > keyDepth)) return result; 
		//	final String  qualifier = (depth <= keyDepth?"name=":"");
		//	final String  qualifier = getNodeDisplayInfo(info.getPanelIndex(), info.getGroupByIndex(), depth, "safb").isDisplayQualifier()?"name=":"";
			StrutsActionFormBean safb = (StrutsActionFormBean)nodeObject;
			parent.add(new SimpleTreeNode("name="            +safb.getName()                               ), safb.getName()            );
			SimpleTreeNode classBranch = new SimpleTreeNode("type="+safb.getType(), null, null, "class", safb.getTypeTarget());
			parent.add(classBranch, safb.getType());
			ModelUtils.createBranchClassFromAbstractVO(classBranch, safb);
			parent.add(new SimpleTreeNode("className="       +safb.getClassName()                          ), safb.getClassName()       );
			parent.add(new SimpleTreeNode("dynamic="         +safb.getDynamic()                            ), safb.getDynamic()         );
			parent.add(new SimpleTreeNode("id="              +safb.getId()                                 ), safb.getId()              );
			parent.add(new SimpleTreeNode("source="          +safb.getConfigFileTarget(), null, null, safb.isConfigFileTargetMissing()?"warn":"xml", safb.getConfigFileTarget()), safb.getConfigFileTarget());
		  if (safb.getActionMappings() != null && (Viewer.viewerConfig.isRecurseToShowSiblings() || !TreeUtils.existsAsAncestor(StrutsActionMapping.class, parent))) {
			SimpleTreeNode sams = new SimpleTreeNode("Action Mapping(s)");
			parent.add(sams);
			for (Iterator it = safb.getActionMappings().iterator(); it.hasNext();) {
				StrutsActionMapping sam = (StrutsActionMapping)it.next();
				sams.add(new SimpleTreeNode("path="+sam.getPath(), sams, sam, sam.isTypeTargetMissing()?"warn":"sam"));
			}
		  }
		} else if (nodeObject instanceof StrutsActionMapping) {
			/***********************
			 * StrutsActionMapping *
			 ***********************/
			if (TreeUtils.existsAsAncestor(nodeObject, parent)) return result; // End recursion
		//	final boolean isPrimary = (info.getPanelIndex() == Viewer.INFO_ID_SAM);
		//	final byte    keyDepth  = (byte)((isPrimary?0:1)+(info.getGroupByIndex() == Viewer.GROUP_BY_SAM_BY_PATH?1:2));
		//	if ((isPrimary && depth > 2) || (!isPrimary && depth > keyDepth)) return result; 
		//	final String  qualifier = (depth <= keyDepth?"path=":"");
		//	final String  qualifier = getNodeDisplayInfo(info.getPanelIndex(), info.getGroupByIndex(), depth, "sam").isDisplayQualifier()?"path=":"";
			StrutsActionMapping sam = (StrutsActionMapping)nodeObject;
			parent.add(new SimpleTreeNode("path="            +sam.getPath()                                 ), sam.getPath()            );
			SimpleTreeNode classBranch = new SimpleTreeNode("type="+sam.getType(), null, null, "class", sam.getTypeTarget());
			parent.add(classBranch, sam.getType());
			ModelUtils.createBranchClassFromAbstractVO(classBranch, sam);
		  if (sam.getFormBean() != null) {
			StrutsActionFormBean safb = sam.getFormBean();
			parent.add(new SimpleTreeNode("name="            +sam.getName(), null, safb, safb.isTypeTargetMissing()?"warn":"safb", safb.getTypeTarget()));
		  }
			parent.add(new SimpleTreeNode("scope="           +sam.getScope()                                ), sam.getScope()           );
			parent.add(new SimpleTreeNode("parameter="       +sam.getParameter()                            ), sam.getParameter()       );
			parent.add(new SimpleTreeNode("validate="        +sam.getValidate()                             ), sam.getValidate()        );
			parent.add(new SimpleTreeNode("attribute="       +sam.getAttribute()                            ), sam.getAttribute()       );
			parent.add(new SimpleTreeNode("className="       +sam.getClassName()                            ), sam.getClassName()       );
			parent.add(new SimpleTreeNode("forward="         +sam.getForward()                              ), sam.getForward()         );
			parent.add(new SimpleTreeNode("id="              +sam.getId()                                   ), sam.getId()              );
			parent.add(new SimpleTreeNode("include="         +sam.getInclude()                              ), sam.getInclude()         );
			parent.add(new SimpleTreeNode("input="           +sam.getInput()                                ), sam.getInput()           );
			parent.add(new SimpleTreeNode("prefix="          +sam.getPrefix()                               ), sam.getPrefix()          );
			parent.add(new SimpleTreeNode("roles="           +sam.getRoles()                                ), sam.getRoles()           );
			parent.add(new SimpleTreeNode("suffix="          +sam.getSuffix()                               ), sam.getSuffix()          );
			parent.add(new SimpleTreeNode("unknown="         +sam.getUnknown()                              ), sam.getUnknown()         );
			parent.add(new SimpleTreeNode("source="          +sam.getConfigFileTarget(), null, null, sam.isConfigFileTargetMissing()?"warn":"xml", sam.getConfigFileTarget()), sam.getConfigFileTarget());
		  if (sam.getActionForwards() != null && (Viewer.viewerConfig.isRecurseToShowSiblings() || !TreeUtils.existsAsAncestor(StrutsActionForward.class, parent))) {
			SimpleTreeNode safs = new SimpleTreeNode("Action Forward(s)");
			parent.add(safs);
			for (Iterator it = sam.getActionForwards().keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				StrutsActionForward saf = (StrutsActionForward)((SimpleTreeNode)sam.getActionForwards().get(key)).getUserObject();
				safs.add(new SimpleTreeNode("unique="+sam.getPath()+"@"+saf.getName(), safs, saf, "saf"));
			}
		  }
		} else if (nodeObject instanceof TilesDefinition) {
			/*******************
			 * TilesDefinition *
			 *******************/
			if (TreeUtils.existsAsAncestor(nodeObject, parent)) return result; // End recursion
		//	final boolean isPrimary = (info.getPanelIndex() == Viewer.INFO_ID_TD);
		//	final byte    keyDepth  = (byte)((isPrimary?0:1)+(info.getGroupByIndex() == Viewer.GROUP_BY_TD_BY_NAME?1:2));
		//	if ((isPrimary && depth > 2) || (!isPrimary && depth > keyDepth)) return result; 
		//	final String  qualifier = (depth <= keyDepth?"name=":"");
			TilesDefinition td = (TilesDefinition)nodeObject;
			parent.add(new SimpleTreeNode("name="             +td.getName()                               ), td.getName()             );
			parent.add(new SimpleTreeNode("extends="          +td.getExtends()                            ), td.getExtends()          );
			parent.add(new SimpleTreeNode("extendsDefinition="+td.getExtendsDefinition()                  ), td.getExtendsDefinition());
			parent.add(new SimpleTreeNode("controllerClass="  +td.getControllerClass()                    ), td.getControllerClass()  );
			parent.add(new SimpleTreeNode("controllerUrl="    +td.getControllerUrl()                      ), td.getControllerUrl()    );
			parent.add(new SimpleTreeNode("page="             +td.getPage()                               ), td.getPage()             );
			parent.add(new SimpleTreeNode("path="             +td.getPath()                               ), td.getPath()             );
			parent.add(new SimpleTreeNode("role="             +td.getRole()                               ), td.getRole()             );
			parent.add(new SimpleTreeNode("template="         +td.getTemplate()                           ), td.getTemplate()         );
			parent.add(new SimpleTreeNode("source="           +td.getConfigFileTarget(), null, null, td.isConfigFileTargetMissing()?"warn":"xml", td.getConfigFileTarget()), td.getConfigFileTarget());
		  if (td.getAttributes() != null && td.getAttributes().size() > 0) {	
			SimpleTreeNode tas = new SimpleTreeNode("Tiles Attribute(s)");
			parent.add(tas);
			for (Iterator it = td.getAttributes().keySet().iterator(); it.hasNext();) {
				Object key = it.next();
				SimpleTreeNode treeNode = (SimpleTreeNode)td.getAttributes().get(key);
				tas.add(treeNode);
			}
		  }
		  if (td.getActionForwards() != null && (Viewer.viewerConfig.isRecurseToShowSiblings() || !TreeUtils.existsAsAncestor(StrutsActionForward.class, parent))) {
			SimpleTreeNode safs = new SimpleTreeNode("Action Forward(s)");
			parent.add(safs);
			for (Iterator it = td.getActionForwards().iterator(); it.hasNext();) {
				StrutsActionForward saf = (StrutsActionForward)it.next();
				safs.add(new SimpleTreeNode("unique="+saf.getActionMapping().getPath()+"@"+saf.getName(), null, saf, "saf"));
			}
		  }
		} else if (nodeObject instanceof ViewerConfig) {
			/****************
			 * ViewerConfig *
			 ****************/
			ViewerConfig vc = (ViewerConfig)nodeObject;
			
			// Config files
			SimpleTreeNode configFileBasePathNode = new SimpleTreeNode("Configuration File(s)");
			parent.add(configFileBasePathNode);
			configFileBasePathNode.add(new SimpleTreeNode("targetAppConfigFileBasePath="+vc.getTargetAppConfigFileBasePath(), null, null, vc.isTargetAppConfigFileBasePathMissing()?"warn":"folder", vc.getTargetAppConfigFileBasePath()), vc.getTargetAppConfigFileBasePath());
			SimpleTreeNode configFileMasksNode = new SimpleTreeNode("Mask(s)");
			configFileBasePathNode.add(configFileMasksNode);
			for (Iterator it = vc.getTargetAppConfigFileMasks().iterator(); it.hasNext();) {
				String configFileMask = (String)it.next();
				SimpleTreeNode configFileMaskNode = new SimpleTreeNode(configFileMask);
				configFileMasksNode.add(configFileMaskNode);
				if (vc.getTargetAppConfigFilesByMask().containsKey(configFileMask)) {
					for (Iterator itSub = ((List)vc.getTargetAppConfigFilesByMask().get(configFileMask)).iterator(); itSub.hasNext();) {
						File configFile = (File)itSub.next();
						SimpleTreeNode configFileNode = new SimpleTreeNode(configFile.getPath(), null, null, TreeUtils.getFileObjectCategory(configFile), configFile.getAbsolutePath());
						configFileMaskNode.add(configFileNode);
					}
				}
			}
			
			// Source code paths
			SimpleTreeNode sourceCodeBasePathNode = new SimpleTreeNode("Source Code Path(s)");
			parent.add(sourceCodeBasePathNode);
			sourceCodeBasePathNode.add(new SimpleTreeNode("targetAppSourceCodeBasePath="+vc.getTargetAppSourceCodeBasePath(), null, null, vc.isTargetAppSourceCodeBasePathMissing()?"warn":"folder", vc.getTargetAppSourceCodeBasePath()), vc.getTargetAppSourceCodeBasePath());
			SimpleTreeNode sourceCodeMasksNode = new SimpleTreeNode("Mask(s)");
			sourceCodeBasePathNode.add(sourceCodeMasksNode);
			for (Iterator it = vc.getTargetAppSourceCodePathMasks().iterator(); it.hasNext();) {
				String sourceCodeMask = (String)it.next();
				SimpleTreeNode sourceCodeMaskNode = new SimpleTreeNode(sourceCodeMask);
				sourceCodeMasksNode.add(sourceCodeMaskNode);
				if (vc.getTargetAppSourceCodePathsByMask().containsKey(sourceCodeMask)) {
					for (Iterator itSub = ((List)vc.getTargetAppSourceCodePathsByMask().get(sourceCodeMask)).iterator(); itSub.hasNext();) {
						File sourceCode = (File)itSub.next();
						SimpleTreeNode sourceCodeNode = new SimpleTreeNode(sourceCode.getPath(), null, null, TreeUtils.getFileObjectCategory(sourceCode), sourceCode.getAbsolutePath());
						sourceCodeMaskNode.add(sourceCodeNode);
					}
				}
			}
			
			// Object code paths
			SimpleTreeNode objectCodeBasePathNode = new SimpleTreeNode("Object Code Path(s)");
			parent.add(objectCodeBasePathNode);
			objectCodeBasePathNode.add(new SimpleTreeNode("targetAppObjectCodeBasePath="+vc.getTargetAppObjectCodeBasePath(), null, null, vc.isTargetAppObjectCodeBasePathMissing()?"warn":"folder", vc.getTargetAppObjectCodeBasePath()), vc.getTargetAppObjectCodeBasePath());
			SimpleTreeNode objectCodeMasksNode = new SimpleTreeNode("Mask(s)");
			objectCodeBasePathNode.add(objectCodeMasksNode);
			for (Iterator it = vc.getTargetAppObjectCodePathMasks().iterator(); it.hasNext();) {
				String objectCodeMask = (String)it.next();
				SimpleTreeNode objectCodeMaskNode = new SimpleTreeNode(objectCodeMask);
				objectCodeMasksNode.add(objectCodeMaskNode);
				if (vc.getTargetAppObjectCodePathsByMask().containsKey(objectCodeMask)) {
					for (Iterator itSub = ((List)vc.getTargetAppObjectCodePathsByMask().get(objectCodeMask)).iterator(); itSub.hasNext();) {
						File objectCode = (File)itSub.next();
						SimpleTreeNode objectCodeNode = new SimpleTreeNode(objectCode.getPath(), null, null, TreeUtils.getFileObjectCategory(objectCode), objectCode.getAbsolutePath());
						objectCodeMaskNode.add(objectCodeNode);
					}
				}
			}
			
			// Class paths
			SimpleTreeNode classPathsNode = new SimpleTreeNode("Class Path Entry(s)");
			parent.add(classPathsNode);
			classPathsNode.add(new SimpleTreeNode("targetAppClasspathBasePath="+vc.getTargetAppClasspathBasePath(), null, null, vc.isTargetAppClasspathBasePathMissing()?"warn":"folder", vc.getTargetAppClasspathBasePath()), vc.getTargetAppClasspathBasePath());
			SimpleTreeNode classPathMasksNode = new SimpleTreeNode("Mask(s)");
			classPathsNode.add(classPathMasksNode);
			for (Iterator it = vc.getTargetAppClasspathPathMasks().iterator(); it.hasNext();) {
				String classPathMask = (String)it.next();
				SimpleTreeNode classPathMaskNode = new SimpleTreeNode(classPathMask);
				classPathMasksNode.add(classPathMaskNode);
				if (vc.getTargetAppClasspathPathsByMask().containsKey(classPathMask)) {
					for (Iterator itSub = ((List)vc.getTargetAppClasspathPathsByMask().get(classPathMask)).iterator(); itSub.hasNext();) {
						File classPath = (File)itSub.next();
						SimpleTreeNode classPathNode = new SimpleTreeNode(classPath.getPath(), null, null, TreeUtils.getFileObjectCategory(classPath), classPath.getAbsolutePath());
						classPathMaskNode.add(classPathNode);
					}
				}
			}
			
			// Miscellaneous
			parent.add(new SimpleTreeNode("targetAppBasePackage=" +vc.getTargetAppBasePackage(), null, null, vc.isTargetAppBasePackageMissing()?"warn":"folder", vc.getTargetAppSourceCodeBasePath() + "/src/" + vc.getTargetAppBasePackage().replaceAll("[.]", "/")), vc.getTargetAppBasePackage());
			parent.add(new SimpleTreeNode("textEditorExecutable=" +vc.getTextEditorExecutable(), null, null, vc.isTextEditorExecutableMissing()?"warn":"exe"   , vc.getTextEditorExecutable()                                                                       ), vc.getTextEditorExecutable());
			parent.add(new SimpleTreeNode("recurseToShowSiblings="+vc.isRecurseToShowSiblings()));
			parent.add(new SimpleTreeNode("source="               +vc.getConfigFileTarget()    , null, null, vc.isConfigFileTargetMissing()    ?"warn":"xml"   , vc.getConfigFileTarget()                                                                           ), vc.getConfigFileTarget()    );
			
			// ThreadPool
			SimpleTreeNode threadPoolNode = new SimpleTreeNode("Thread Pool");
			parent.add(threadPoolNode);
			threadPoolNode.add(new SimpleTreeNode("poolCoreSize="+vc.getThreadPoolOptionPoolCoreSize()));
			threadPoolNode.add(new SimpleTreeNode("poolMaximumSize="+vc.getThreadPoolOptionPoolMaximumSize()));
			threadPoolNode.add(new SimpleTreeNode("queueMaximumSize="+vc.getThreadPoolOptionQueueMaximumSize()));
			threadPoolNode.add(new SimpleTreeNode("queueFairOrder="+vc.getThreadPoolOptionQueueFairOrder()));
			threadPoolNode.add(new SimpleTreeNode("preStartCoreThreads="+vc.getThreadPoolOptionPreStartCoreThreads()));
			threadPoolNode.add(new SimpleTreeNode("keepAliveTime="+vc.getThreadPoolOptionKeepAliveTime()));
			threadPoolNode.add(new SimpleTreeNode("queueType="+vc.getThreadPoolOptionQueueType()));
			threadPoolNode.add(new SimpleTreeNode("rejectionPolicy="+vc.getThreadPoolOptionRejectionPolicy()));
			threadPoolNode.add(new SimpleTreeNode("shutdownTimeoutThreshold="+vc.getThreadPoolShutdownTimeoutThreshold()));
		}
		return result;
	}
	
}
