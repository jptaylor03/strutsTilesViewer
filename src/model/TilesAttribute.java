package model;

/**
 * Container for a Tiles Attribute.
 */
public class TilesAttribute extends AbstractVO {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String name;
	private String value;
	private String content;
	private Boolean direct;
	private String type;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public TilesAttribute() {}
	public TilesAttribute(String name, String value) {
		this.name = name;
		this.value = value;
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Boolean getDirect() {
		return direct;
	}
	public void setDirect(Boolean direct) {
		this.direct = direct;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
		return this.name;
	}
	
}
