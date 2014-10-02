package com.pinthecloud.athere.activity;

import android.app.Activity;
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
public class AhActivity extends Activity {

	protected AhApplication app;
	protected FiveRocksHelper fiveRocksHelper;
	protected UserHabitHelper userHabitHelper;
	protected AhActivity thisActivity;


	public AhActivity(){
		thisActivity = this;
		app = AhApplication.getInstance();
		fiveRocksHelper = app.getFiveRocksHelper();
		userHabitHelper = app.getUserHabitHelper();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fiveRocksHelper.initFiveRocks(thisActivity);
	}


	@Override
	protected void onStart() {
		super.onStart();
		fiveRocksHelper.onActivityStart(thisActivity);
		userHabitHelper.activityStart(thisActivity);
	}


	@Override
	protected void onStop() {
		super.onStop();
		fiveRocksHelper.onActivityStop(thisActivity);
		userHabitHelper.activityStop(thisActivity);
	}


	public void Log(Activity activity, Object... params){
		if(AhGlobalVariable.DEBUG_MODE){
			Log.e("ERROR", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Log.e("ERROR", "[ "+activity.getClass().getName() + " ]");
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
}
