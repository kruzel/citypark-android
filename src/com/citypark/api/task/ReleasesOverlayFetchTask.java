package com.citypark.api.task;

import android.content.Context;
import android.os.AsyncTask;

import com.citypark.view.overlay.LiveGarageMarkers;
import com.citypark.view.overlay.LiveStreetReleasesMarkers;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class ReleasesOverlayFetchTask extends AsyncTask<GeoPoint, Void, Void> {
	private OverlayListener listener;
	private LiveStreetReleasesMarkers releasesMarkers;
	private Boolean res = false;

	public ReleasesOverlayFetchTask(final MapView osmv, Context context, OverlayListener listener, LiveStreetReleasesMarkers releasesMarkers) {
		super();
		this.listener = listener;
		
		this.releasesMarkers = releasesMarkers;
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
