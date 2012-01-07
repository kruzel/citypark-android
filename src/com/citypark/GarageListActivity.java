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

	/*private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
		//	p, RADIUS, context,app.getSessionId()
			final CityParkGaragesParser parser = new CityParkGaragesParser(getApplication(),
					"2270T387",
					32.089859d,
					34.771961d,
					5000);
			m_garage = new ArrayList<GarageData>();
			for (GaragePoint gp : parser.parse()) {
				GarageData gd = new GarageData();
				gd.setName(gp.getName());
				gd.setFirstHourPrice((int)gp.getPrice());
				m_garage.add(gd);
				
			}
			if (m_garage != null && m_garage.size() > 0) {
				m_adapter.notifyDataSetChanged();
				for (int i = 0; i < m_garage.size(); i++)
					m_adapter.add(m_garage.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};*/

	/*private void getGarageList() {
		try {
			m_garage = new ArrayList<GarageData>();
			GarageData o1 = new GarageData();
			o1.setName("SF services");
			o1.setFirstHourPrice(7);
			GarageData o2 = new GarageData();
			o2.setName("SF Advertisement");
			o2.setFirstHourPrice(4);
			m_garage.add(o1);
			m_garage.add(o2);
			Thread.sleep(5000);
			Log.i("ARRAY", "" + m_garage.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}*/

	@Override
	public void GarageDetailsFetchComplete(List<GarageData> gdList) {
		m_garage.addAll(gdList);
		m_adapter.notifyDataSetChanged();
		
	}
}