package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.os.Bundle;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.helper.PrefHelper;
import com.pinthecloud.athere.helper.ServiceClient;

public class AhActivity extends Activity {
	
	protected ServiceClient serviceClient;
	protected PrefHelper pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_ah);
		//serviceClient = ((AhApplication) getApplication()).geterviceClient();
		serviceClient = new ServiceClient(this);
		pref = new PrefHelper(this);
	}
	
	
}
