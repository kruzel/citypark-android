/**
 * 
 */
package com.citypark.api.task;

/**
 * @author Ofer Kruzel
 *
 */
public interface LoginListener {
	/**
	 * Called when a login completes.
	 * @param session ID or null
	 */
	public void loginComplete(String sessionId);
	
	public void loginFailed();
}
