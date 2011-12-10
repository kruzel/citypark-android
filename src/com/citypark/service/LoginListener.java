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
	 * @param msg Response code
	 * @param route Route computed or null
	 */
	public void loginComplete(String sessionId);
}
