package com.citypark;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.citypark.adapter.GarageDataAdapter;
import com.citypark.constants.CityParkConsts;
import com.citypark.dto.GarageData;
import com.citypark.service.GarageDetailsListFetchTask;
import com.citypark.service.GarageDetailsListListener;

public class GarageListActivity extends ListActivity implements GarageDetailsListListener {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<GarageData> m_garage = null;
	private GarageDataAdapter m_adapter;
	private GarageDetailsListFetchTask task;
	private double lat,lng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.garage_item_list_view);
		m_garage = new ArrayList<GarageData>();
		this.m_adapter = new GarageDataAdapter(this, R.layout.garage_item,
				m_garage);
		setListAdapter(this.m_adapter);
		
		lat = getIntent().getDoubleExtra(CityParkConsts.LATITUDE,0.0d);
		lng = getIntent().getDoubleExtra(CityParkConsts.LONGITUDE,0.0d);
		if(lat==0||lng==0) {			
			return;
		}

		String sessionId = ((CityParkApp)getApplicationContext()).getSessionId();
		if(sessionId == null) return;
		task = new GarageDetailsListFetchTask(this, this, sessionId, lat, lng);
		task.execute();
		/*m_ProgressDialog = ProgressDialog.show(GarageListActivity.this,
				"Please wait...", "Retrieving data ...", true);*/
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		try
		{ 
			super.onListItemClick(l, v, position, id);
			GarageData garageData = (GarageData)l.getItemAtPosition(position);
			Intent intent = new Intent(this,GarageDetailsActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			intent.putExtra(CityParkConsts.GARAGE_ID, garageData.getParkingId());
			this.startActivity(intent);
		}
		catch(Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void GarageDetailsFetchComplete(List<GarageData> gdList) {
		m_garage.addAll(gdList);
		m_adapter.notifyDataSetChanged();
		
	}
}