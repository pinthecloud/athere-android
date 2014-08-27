package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ProfileImageFragment;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class ProfileImageActivity extends AhActivity{

	Tracker t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_image);

		/* 
		 * for google analytics
		 */
        GoogleAnalytics.getInstance(this).newTracker("UA-53944359-1");

        if (t==null){
            t = ((AhApplication) getApplication()).getTracker(
                    AhApplication.TrackerName.APP_TRACKER);

            t.setScreenName("ProfileImageActivity");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
		
		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		ProfileImageFragment profileImageFragment = new ProfileImageFragment();
		fragmentTransaction.add(R.id.profile_image_container, profileImageFragment);
		fragmentTransaction.commit();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
}

