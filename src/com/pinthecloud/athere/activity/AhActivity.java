package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.os.Bundle;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.manager.ServiceClient;

public class AhActivity extends Activity {
	
	protected ServiceClient serviceClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_ah);
		serviceClient = ((AhApplication) getApplication()).geterviceClient();
	}
	
	
}
