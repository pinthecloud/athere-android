package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.helper.PreferenceHelper;

/**
 *  Base class for every activity.
 *  Provides AhApplication reference for subclasses.
 *  Every Activity is a container for each Fragment.
 *  Fragments do the real works.
 */
public class AhActivity extends Activity {

	protected AhApplication app;
	protected PreferenceHelper pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Set static value
		 */
		app = AhApplication.getInstance();
		pref = app.getPref();
	}
	
	// Logging Method
	protected void Log(String... params){
		for(String str : params)
			Log.e("ERROR", str);
	}
}
