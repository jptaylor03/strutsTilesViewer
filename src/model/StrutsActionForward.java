package model;


/**
 * Container for a Struts Action Forward.
 */
public class StrutsActionForward extends AbstractVO {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String name;
	private String path;
	private Boolean redirect;
	private StrutsActionMapping actionMapping;
	private String className;
	private Boolean contextRelative;
	private String id;
	private TilesDefinition tilesDefinition;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public StrutsActionForward() {}
	public StrutsActionForward(String name, String path, Boolean redirect, StrutsActionMapping actionMapping) {
		this.name = name;
		this.path = path;
		this.redirect = redirect;
		this.actionMapping = actionMapping;
	}
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Boolean getRedirect() {
		return redirect;
	}
	public void setRedirect(Boolean redirect) {
		this.redirect = redirect;
	}
	public StrutsActionMapping getActionMapping() {
		return actionMapping;
	}
	public void setActionMapping(StrutsActionMapping actionMapping) {
		this.actionMapping = actionMapping;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Boolean getContextRelative() {
		return contextRelative;
	}
	public void setContextRelative(Boolean contextRelative) {
		this.contextRelative = contextRelative;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TilesDefinition getTilesDefinition() {
		return tilesDefinition;
	}
	public void setTilesDefinition(TilesDefinition tilesDefinition) {
		this.tilesDefinition = tilesDefinition;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Overrides the standard {@link Object#toString()} method to provide an
	 * appropriate value based on the specific contents of this class.
	 * 
	 * @return String containing an appropriate value specific to this class.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		if (this.actionMapping != null) {
			result.append(this.actionMapping.getPath() + "@");
		}
		result.append(this.getName());
		return result.toString();
	}
	
}
