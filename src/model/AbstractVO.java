package model;

/**
 * Container for attributes common to many model objects.
 */
public abstract class AbstractVO {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String configFileTarget;
	private boolean configFileTargetMissing;

	/******************
	 * Constructor(s) *
	 ******************/
	
	public AbstractVO() {}
	public AbstractVO(String configFileTarget) {
		this.configFileTarget = configFileTarget;
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public String getConfigFileTarget() {
		return configFileTarget;
	}
	public void setConfigFileTarget(String configFileTarget) {
		this.configFileTarget = configFileTarget;
	}
	public boolean isConfigFileTargetMissing() {
		return configFileTargetMissing;
	}
	public void setConfigFileTargetMissing(boolean configFileTargetMissing) {
		this.configFileTargetMissing = configFileTargetMissing;
	}

}
