package com.pinthecloud.athere.activity;

import java.net.MalformedURLException;

import android.app.Activity;
import android.os.Bundle;

import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.ServiceClient;
import com.pinthecloud.athere.helper.UserHelper;

public class AhActivity extends Activity {

	protected ServiceClient serviceClient;
	protected PreferenceHelper pref;
	protected UserHelper userHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			serviceClient = new ServiceClient(this);
		} catch (MalformedURLException e) {
		}
		pref = new PreferenceHelper(this);
		userHelper = new UserHelper(this);
	}
}
