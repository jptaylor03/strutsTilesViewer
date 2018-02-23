package model;

/**
 * Container for maintaining the state of an application.
 */
public class AppState {
	
	/***************
	 * Constant(s) *
	 ***************/
	
	/**
	 * State indicating the application is currently starting up.
	 */
	public static final byte STARTUP  = 0;
	
	/**
	 * State indicating the application is currently active.
	 */
	public static final byte ACTIVE   = 1;
	
	/**
	 * State indicating the application is currently restarting.
	 */
	public static final byte RESTART  = 2;
	
	/**
	 * State indicating the application is currently shutting down.
	 */
	public static final byte SHUTDOWN = 3;
	
	/**
	 * State indicating the application is currently inactive.
	 */
	public static final byte INACTIVE = 4;
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private byte state = STARTUP;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public AppState() {}
	public AppState(byte state) {
		this.state = state;
	}
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public byte getState() {
		return state;
	}
	public void setState(byte state) {
		this.state = state;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Convenience method to compare the application's current state.
	 * 
	 * @param state Byte value to compare against the current application state.
	 * @return Boolean indicating whether the current application state matches the one specified.
	 */
	public boolean is(byte state) {
		return (this.state == state);
	}
	
}
