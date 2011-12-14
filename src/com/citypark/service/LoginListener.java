/**
 * 
 */
package com.citypark.service;

import android.content.Context;

import com.citypark.utility.route.Route;

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
}
