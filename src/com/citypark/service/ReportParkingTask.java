package com.citypark.service;

import android.content.Context;
import android.os.AsyncTask;

import com.citypark.parser.CityParkReportParkingParser;

public class ReportParkingTask extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	private String sessionId;
	private double latitude;
	private double longitude;

	public ReportParkingTask(Context context, String sessionId,
			double latitude, double longitude) {
		super();
		this.context = context;
		this.sessionId = sessionId;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	protected Void doInBackground(Void... params) {
		CityParkReportParkingParser parser = new CityParkReportParkingParser(context, sessionId, latitude, longitude);
		parser.parse();

		return null;
	}

}
