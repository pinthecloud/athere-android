package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.helper.PreferenceHelper;

import io.fiverocks.android.FiveRocks;	

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

	// for FiveRocks analytics
	public static final String FiveRocks_AppId = "53f9e732333a3895de000001";
	public static final String FiveRocks_AppKey = "Mx8kZ2BxcZKXZPBz5UV8";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    FiveRocks.setDebugEnabled(true);
	    
		// Set FiveRocks AppId, AppKey
		FiveRocks.init(this, FiveRocks_AppId, FiveRocks_AppKey);
		FiveRocks.setUserCohortVariable(1, "DemoUser");
		
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
	  FiveRocks.onActivityStop(this);
	  super.onStop();
	}
	
	
}
