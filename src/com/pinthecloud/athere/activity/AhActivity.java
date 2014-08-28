package com.pinthecloud.athere.activity;

import io.fiverocks.android.FiveRocks;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.helper.PreferenceHelper;

/**
 *  Base class for every activity.
 *  Provides AhApplication reference for subclasses.
 *  Every Activity is a container for each Fragment.
 *  Fragments do the real works.
 */
public class AhActivity extends Activity{

	protected AhApplication app;
	protected PreferenceHelper pref;
	protected AhActivity _this;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		/*
		 * Set FiveRocks
		 */
		FiveRocks.setDebugEnabled(true);
		FiveRocks.init(this, AhGlobalVariable.Five_ROCKS_APP_ID, AhGlobalVariable.Five_ROCKS_APP_KEY);
		// FiveRocks.setUserCohortVariable(1, "DemoUser");
		
		
		/*
		 * Set static value
		 */
		app = AhApplication.getInstance();
		pref = app.getPref();
		_this = this;
	}


	// Logging Method
	protected void Log(AhActivity activity, Object... params){
		Log.e("ERROR", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.e("ERROR", activity.getClass().getName());
		for(Object str : params) {
			if (str == null) {
				Log.e("ERROR", "null");
				continue;
			}
			Log.e("ERROR", str.toString());
		}
		Log.e("ERROR", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}


	@Override
	protected void onStart() {
		super.onStart();
		FiveRocks.onActivityStart(this);	
	}


	@Override
	protected void onStop() {
		super.onStop();
		FiveRocks.onActivityStop(this);
	}
}
