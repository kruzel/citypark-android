package com.citypark;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.citypark.adapter.GarageDataAdapter;
import com.citypark.dto.GarageData;

public class GarageListActivity extends ListActivity {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<GarageData> m_garage = null;
	private GarageDataAdapter m_adapter;
	private Runnable viewGarages;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.garage_item_list_view);
		m_garage = new ArrayList<GarageData>();
		this.m_adapter = new GarageDataAdapter(this, R.layout.garage_item,
				m_garage);
		setListAdapter(this.m_adapter);

		viewGarages = new Runnable() {
			@Override
			public void run() {
				getOrders();
			}
		};
		Thread thread = new Thread(null, viewGarages, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(GarageListActivity.this,
				"Please wait...", "Retrieving data ...", true);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		try
		{//TODO: goto garage detailed activity 
			super.onListItemClick(l, v, position, id);
			GarageData garageData = (GarageData)l.getItemAtPosition(position);
			String strTextToDisplay = "Selected item is :"  +garageData.getName();
			Toast.makeText(this, strTextToDisplay, Toast.LENGTH_LONG).show();
		}
		catch(Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if (m_garage != null && m_garage.size() > 0) {
				m_adapter.notifyDataSetChanged();
				for (int i = 0; i < m_garage.size(); i++)
					m_adapter.add(m_garage.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private void getOrders() {
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
	}
}
