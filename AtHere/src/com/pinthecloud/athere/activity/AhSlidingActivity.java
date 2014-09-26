package com.pinthecloud.athere.activity;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.analysis.FiveRocksHelper;
import com.pinthecloud.athere.analysis.UserHabitHelper;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.fragment.AppDrawerFragment;

public class AhSlidingActivity extends SlidingActivity{

	protected AhApplication app;
	protected FiveRocksHelper fiveRocksHelper;
	protected UserHabitHelper userHabitHelper;

	protected AhSlidingActivity thisActivity;
	protected String simpleClassName;

	private boolean isDestroyed = false;


	public AhSlidingActivity(){
		thisActivity = this;
		app = AhApplication.getInstance();
		simpleClassName = thisActivity.getClass().getSimpleName();
		fiveRocksHelper = app.getFiveRocksHelper();
		userHabitHelper = app.getUserHabitHelper();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogSM(simpleClassName + " onCreate");
		fiveRocksHelper.initFiveRocks(thisActivity);

		
		/*
		 * Set App Drawer
		 */
		setBehindContentView(R.layout.frame_app_drawer);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		AppDrawerFragment appDrawerFragment = new AppDrawerFragment();
		fragmentTransaction.replace(R.id.app_drawer_container, appDrawerFragment);
		fragmentTransaction.commit();

		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.app_drawer_shadow_width);
		sm.setShadowDrawable(R.drawable.app_drawer_shadow);
		sm.setBehindOffsetRes(R.dimen.app_drawer_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
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


	public void Log(AhFragment fragment, Object... params){
		if(AhGlobalVariable.DEBUG_MODE){
			Log.e("ERROR", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			Log.e("ERROR", "[ "+fragment.getClass().getName() + " ]");
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
}
