package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.os.Bundle;

import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.ServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class AhActivity extends Activity {
	
	protected ServiceClient serviceClient;
	protected PreferenceHelper pref;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		serviceClient = new ServiceClient(this);
		pref = new PreferenceHelper(this);
	}
}
