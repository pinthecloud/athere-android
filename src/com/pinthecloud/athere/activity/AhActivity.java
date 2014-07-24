package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.os.Bundle;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.helper.PreferenceHelper;

public class AhActivity extends Activity {

	protected PreferenceHelper pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Set static value
		 */
		AhApplication app = AhApplication.getInstance();
		pref = app.getPref();
	}
}
