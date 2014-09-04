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
	protected AhActivity _thisActivity;
	protected String simpleClassName;


	public AhActivity(){
		_thisActivity = this;
		app = AhApplication.getInstance();
		pref = app.getPref();
		simpleClassName = _thisActivity.getClass().getSimpleName();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onCreate");

		
		/*
		 * Set FiveRocks
		 */
		FiveRocks.setDebugEnabled(true);
		FiveRocks.init(_thisActivity, AhGlobalVariable.Five_ROCKS_APP_ID, AhGlobalVariable.Five_ROCKS_APP_KEY);
		// FiveRocks.setUserCohortVariable(1, "DemoUser");
	}


	// Logging Method
	protected void Log(AhActivity activity, Object... params){
		Log.e("ERROR", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.e("ERROR", simpleClassName);
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
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onStart");
		FiveRocks.onActivityStart(_thisActivity);
	}


	@Override
	protected void onStop() {
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onStop");
		super.onStop();
		FiveRocks.onActivityStop(_thisActivity);
	}
}
