package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.analysis.FiveRocksHelper;
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
	protected FiveRocksHelper fiveRocksHelper;

	protected AhActivity thisActivity;
	protected String simpleClassName;


	public AhActivity(){
		thisActivity = this;
		app = AhApplication.getInstance();
		pref = app.getPref();
		fiveRocksHelper = app.getFiveRocksHelper();
		simpleClassName = thisActivity.getClass().getSimpleName();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogSM(simpleClassName + " onCreate");
		fiveRocksHelper.initFiveRocks(thisActivity);
	}


	// Logging Method
	protected void Log(AhActivity activity, Object... params){
		if(AhGlobalVariable.DEBUG_MODE){
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
	}


	protected void LogSM(String params){
		if(AhGlobalVariable.DEBUG_MODE){
			Log.d("Seungmin", params);
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
		LogSM(simpleClassName + " onStart");
		fiveRocksHelper.onActivityStart(thisActivity);
	}


	@Override
	protected void onStop() {
		LogSM(simpleClassName + " onStop");
		super.onStop();
		fiveRocksHelper.onActivityStop(thisActivity);
	}
}
