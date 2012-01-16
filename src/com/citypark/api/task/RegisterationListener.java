package com.citypark.api.task;

public interface RegisterationListener {
	/**
	 * Called when a registration completes.
	 * @param successCode
	 */
	public void RegistrationComplete(final String successCode);

}
