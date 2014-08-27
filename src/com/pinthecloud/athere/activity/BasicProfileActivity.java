package com.pinthecloud.athere.activity;

import io.fiverocks.android.FiveRocks;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.BasicProfileFragment;

//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;

public class BasicProfileActivity extends AhActivity{

//	private Tracker t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic_profile);
	    FiveRocks.init(this, FiveRocks_AppId, FiveRocks_AppKey);
		
	    
		/* 
		 * for google analytics
		 */
//        GoogleAnalytics.getInstance(this).newTracker("UA-53944359-1");
//
//        if (t==null){
//            t = ((AhApplication) getApplication()).getTracker(
//                    AhApplication.TrackerName.APP_TRACKER);
//
//            t.setScreenName("BasicProfileActivity");
//            t.send(new HitBuilders.AppViewBuilder().build());
//        }
		
	    
		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		BasicProfileFragment basicProfileFragment = new BasicProfileFragment();
		fragmentTransaction.add(R.id.basic_profile_container, basicProfileFragment);
		fragmentTransaction.commit();
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
