package com.pinthecloud.athere.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.analysis.FiveRocksHelper;
import com.pinthecloud.athere.analysis.UserHabitHelper;

/**
 *  Base class for every activity.
 *  Provides AhApplication reference for subclasses.
 *  Every Activity is a container for each Fragment.
 *  Fragments do the real works.
 */
public class AhActivity extends Activity{

	protected AhApplication app;
	protected FiveRocksHelper fiveRocksHelper;
	protected UserHabitHelper userHabitHelper;

	protected AhActivity thisActivity;
	protected String simpleClassName;

	private boolean isDestroyed = false;


	public AhActivity(){
		thisActivity = this;
		app = AhApplication.getInstance();
		simpleClassName = thisActivity.getClass().getSimpleName();

		fiveRocksHelper = app.getFiveRocksHelper();
		userHabitHelper = app.getUserHabitHelper();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogSM(simpleClassName + " onCreate");
		fiveRocksHelper.initFiveRocks(thisActivity);
	}


	// Logging Method
	public void Log(AhActivity activity, Object... params){
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


	public void LogSM(String params){
		if(AhGlobalVariable.DEBUG_MODE){
			Log.d("Seungmin", params);
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
		LogSM(simpleClassName + " onStart");
		fiveRocksHelper.onActivityStart(thisActivity);
		userHabitHelper.activityStart(thisActivity);
	}


	@Override
	protected void onStop() {
		LogSM(simpleClassName + " onStop");
		super.onStop();
		fiveRocksHelper.onActivityStop(thisActivity);
		userHabitHelper.activityStop(thisActivity);
	}


	@Override
	protected void onDestroy() {
		LogSM(simpleClassName + " onDestroy");
		super.onDestroy();
		isDestroyed = true;
	}


	@SuppressLint("NewApi")
	@Override
	public boolean isDestroyed() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return isDestroyed;
		} else {
			return super.isDestroyed();
		}
	}
}
