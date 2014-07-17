package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pinthecloud.athere.helper.LocationHelper;

public class CurrentLocationActivity extends Activity {

	private Button btnShowLocation;
	private LocationHelper gps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 setContentView(R.layout.main);

		// show location button click event
//		btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
		btnShowLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {        
				// create class object
				gps = new LocationHelper(CurrentLocationActivity.this);

				// check if GPS enabled     
				if(gps.canGetLocation()){

					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();

					// \n is for new line
					Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
				}else{
					// can't get location
					// GPS or Network is not enabled
					// Ask user to enable GPS/network in settings
					gps.showSettingsAlert();
				}

			}
		});
	}
}