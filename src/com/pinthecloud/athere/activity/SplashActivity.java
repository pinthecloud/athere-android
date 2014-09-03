package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SplashFragment;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;


/**
 * 
 *  First Page
 *  Routes the page
 *  	to BasicProfileActivity if AhGlobalVariable.IS_LOGGED_IN_USER_KEY is false
 *  	to SquareListActivity   if AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY is false
 *  	to SquareActivity 		else
 */
public class SplashActivity extends AhActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);


		/* 
		 * for google analytics
		 */
		//        GoogleAnalytics.getInstance(this).newTracker("UA-53944359-1");
		//
		//        if (t==null){
		//            t = ((AhApplication) getApplication()).getTracker(
		//                    AhApplication.TrackerName.APP_TRACKER);
		//
		//            t.setScreenName("SplashActivity");
		//            t.send(new HitBuilders.AppViewBuilder().build());
		//        }


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		SplashFragment splashFragment = new SplashFragment ();
		fragmentTransaction.add(R.id.splash_container, splashFragment);
		fragmentTransaction.commit();
	}
}
