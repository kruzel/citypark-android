package com.citypark.service;

import com.citypark.parser.CityParkReportLocationParser;

import android.content.Context;
import android.os.AsyncTask;

public class ReportLocationTask extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	private String sessionId;
	private double latitude;
	private double longitude;

	public ReportLocationTask(Context context, String sessionId,
			double latitude, double longitude) {
		super();
		this.context = context;
		this.sessionId = sessionId;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	protected Void doInBackground(Void... params) {
		CityParkReportLocationParser parser = new CityParkReportLocationParser(context, sessionId, latitude, longitude);
		parser.parse();

		return null;
	}

}
