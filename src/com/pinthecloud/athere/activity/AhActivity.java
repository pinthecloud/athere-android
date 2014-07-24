package com.pinthecloud.athere.activity;

import java.net.MalformedURLException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.ServiceClient;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class AhActivity extends Activity {

	protected ServiceClient serviceClient;
	protected PreferenceHelper pref;
	protected UserDBHelper userHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			serviceClient = new ServiceClient(this);
		} catch (MalformedURLException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "AhActivity onCreate : " + e.getMessage());
		}
		pref = new PreferenceHelper(this);
		userHelper = new UserDBHelper(this);
	}
}
