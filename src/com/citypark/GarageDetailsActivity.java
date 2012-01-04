package com.citypark;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.citypark.constants.CityParkConsts;
import com.citypark.parser.CityParkGaragesByIdParser;
import com.citypark.parser.CityParkGaragesByIdParser.GarageDetailes;

public class GarageDetailsActivity extends Activity {
	private int garageId;
	private TextView garageName;
	private TextView garageAddress;
	private TextView garagePaymentMthod;
	private ImageView criple;
	private ImageView garage;
	private ImageView nis;
	private ImageView noLimit;
	private ImageView roof;
	private ImageView underground;
	private ImageView withLock;
	private ImageView garageImage;
	
	private TextView fisrtHourMidWeek;
	private TextView firstHourWeekend;
	private TextView extraQuaterMidWeek;
	private TextView extraQuaterWeekend;
	private TextView allDayMidWeek;
	private TextView allDayWeekend;
	
	private TextView couponText;
	
	/** Application reference. **/
	protected CityParkApp app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.garage_detailes);
		
		app = (CityParkApp) getApplicationContext();
		if (app.getSessionId()==null)
			return;
		
		garageId = getIntent().getIntExtra(CityParkConsts.GARAGE_ID, 0);
		if(garageId==0) {
			//TODO show message
			//TODO try to get location
			return;
		}
		
		garageName = (TextView)findViewById(R.id.textViewName);
		garageAddress = (TextView)findViewById(R.id.textViewAddress);
		garagePaymentMthod = (TextView)findViewById(R.id.textViewPaymentMethod);
		//images
		criple = (ImageView)findViewById(R.id.imageViewCriple);
		garage = (ImageView)findViewById(R.id.imageViewGarage);
		nis = (ImageView)findViewById(R.id.imageViewNis);
		noLimit = (ImageView)findViewById(R.id.imageViewRoof);
		roof = (ImageView)findViewById(R.id.imageViewRoof);
		underground = (ImageView)findViewById(R.id.imageViewUnderground);
		withLock = (ImageView)findViewById(R.id.imageViewWithlock);
		garageImage = (ImageView)findViewById(R.id.imageViewImage);
		//prices table
		fisrtHourMidWeek = (TextView)findViewById(R.id.textViewFirstHourMidWeek);
		firstHourWeekend = (TextView)findViewById(R.id.textViewFirstHourWeekend);
		extraQuaterMidWeek = (TextView)findViewById(R.id.textViewExtraQuaterMidWeek);
		extraQuaterWeekend = (TextView)findViewById(R.id.textViewExtraQuaterWeekend);
		allDayMidWeek = (TextView)findViewById(R.id.textViewAllDayMidWeek);
		allDayWeekend = (TextView)findViewById(R.id.textViewAllDayWeekend);
		//coupon
		couponText = (TextView)findViewById(R.id.textViewCouponText);
		
		Thread t = new Thread() {
			@Override
			public void run() {
				super.run();
				CityParkGaragesByIdParser parser = new CityParkGaragesByIdParser(GarageDetailsActivity.this, app.getSessionId(), garageId);
				GarageDetailes garageDetails = parser.parse();
				
				garageName.setText(garageDetails.getName());
				garageAddress.setText(garageDetails.getStreetName() + " " + garageDetails.getHouseNumber() + ", " + garageDetails.getCity());
				garagePaymentMthod.setText("On Exit, Visa, Cash");
				//images
				if(garageDetails.getCriple())
					criple.setImageDrawable(getResources().getDrawable(R.drawable.criple));
				else
					criple.setImageDrawable(getResources().getDrawable(R.drawable.criple_g));
				
				if(garageDetails.getGarage())
					garage.setImageDrawable(getResources().getDrawable(R.drawable.garage));
				else
					garage.setImageDrawable(getResources().getDrawable(R.drawable.garage_g));
				
				if(garageDetails.getNoLimit())
					noLimit.setImageDrawable(getResources().getDrawable(R.drawable.nolimit));
				else
					noLimit.setImageDrawable(getResources().getDrawable(R.drawable.nolimit_g));
				
				if(garageDetails.getRoof())
					roof.setImageDrawable(getResources().getDrawable(R.drawable.roof));
				else
					roof.setImageDrawable(getResources().getDrawable(R.drawable.roof_g));

				if(garageDetails.getUnderground())
					underground.setImageDrawable(getResources().getDrawable(R.drawable.underground));
				else
					underground.setImageDrawable(getResources().getDrawable(R.drawable.underground_g));

				if(garageDetails.getWithLock())
					withLock.setImageDrawable(getResources().getDrawable(R.drawable.withlock));
				else
					withLock.setImageDrawable(getResources().getDrawable(R.drawable.withlock_g));

				//TODO via URL
				//garageImage 
				
				//prices table
				fisrtHourMidWeek.setText(Double.toString(garageDetails.getFirstHourPrice()));
				firstHourWeekend.setText("0");
				extraQuaterMidWeek.setText(Double.toString(garageDetails.getExtraQuarterPrice()));
				extraQuaterWeekend.setText("0");
				allDayMidWeek.setText(Double.toString(garageDetails.getAllDayPrice()));
				allDayWeekend.setText("0");
			
				//coupon
				couponText.setText(garageDetails.getCouponText());
			}
			
		};
	}

}
