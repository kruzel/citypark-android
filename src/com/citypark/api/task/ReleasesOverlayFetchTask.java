package com.citypark.api.task;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.citypark.view.overlay.LiveGarageMarkers;
import com.citypark.view.overlay.LiveStreetLinesMarkers;
import com.citypark.view.overlay.LiveStreetReleasesMarkers;

public class ReleasesOverlayFetchTask extends AsyncTask<GeoPoint, Void, Void> {
	private OverlayListener listener;
	private LiveStreetReleasesMarkers releasesMarkers;
	private LiveGarageMarkers garageMarkers;
	private Boolean res = false;

	public ReleasesOverlayFetchTask(final MapView osmv, Context context, OverlayListener listener, LiveGarageMarkers garageMarkers, LiveStreetReleasesMarkers releasesMarkers) {
		super();
		this.listener = listener;
		
		this.releasesMarkers = releasesMarkers;
		this.garageMarkers = garageMarkers;
	}

	@Override
	protected Void doInBackground(GeoPoint... p) {
		res = false;
		if(!isCancelled())
			res = releasesMarkers.fetch(p[0]);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if(!isCancelled())
			listener.overlayFetchComplete(false,res,false);
		super.onPostExecute(result);
	}
}
