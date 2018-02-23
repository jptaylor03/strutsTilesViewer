package model;


/**
 * Container for information related to the type of class.
 * <ul>
 *  NOTE: The corresponding info would contain...
 *  <li>Category    == (e.g. "array", "class", "interface", or "primitive")</li>
 *  <li>Overview    == (e.g. "[class] java.util.String", "[primitive] boolean", etc.) </li>
 *  <li>Link Target == (e.g. "D:/work/SACWIS/sacwis/code/webcustom/src/us/oh/state/odjfs/sacwis/administration/application/presentation/agency/AgencyContactValueObject.java", etc.)</li>
 * </ul>
 */
public class ClassTypeInfo {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String overview;
	private String category;
	private String linkTarget;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public ClassTypeInfo() {
		super();
	}
	public ClassTypeInfo(String overview, String category, String linkTarget) {
		this();
		this.setOverview(overview);
		this.setCategory(category);
		this.setLinkTarget(linkTarget);
	}
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLinkTarget() {
		return linkTarget;
	}
	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}

}
