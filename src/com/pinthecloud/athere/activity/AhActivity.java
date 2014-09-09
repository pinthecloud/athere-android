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
	
	protected AhActivity _thisActivity;
	protected String simpleClassName;


	public AhActivity(){
		_thisActivity = this;
		app = AhApplication.getInstance();
		pref = app.getPref();
		fiveRocksHelper = app.getFiveRocksHelper();
		simpleClassName = _thisActivity.getClass().getSimpleName();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onCreate");
		fiveRocksHelper.initFiveRocks(_thisActivity);
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
		fiveRocksHelper.onActivityStart(_thisActivity);
	}


	@Override
	protected void onStop() {
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onStop");
		super.onStop();
		fiveRocksHelper.onActivityStop(_thisActivity);
	}
}
