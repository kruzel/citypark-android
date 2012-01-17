package com.citypark.api.task;

public interface OverlayListener {

	/**
	 * Called when a overlay fetch complete.
	 * @param successCode
	 */
	public void overlayFetchComplete(final Boolean success);
	
}
