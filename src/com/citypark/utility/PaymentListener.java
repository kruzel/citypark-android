package com.citypark.utility;

public interface PaymentListener {

	/**
	 * Called when a payment operation completes.
	 * @param success true or false
	 */
	public void PaymentComplete(Boolean success);
	
}
