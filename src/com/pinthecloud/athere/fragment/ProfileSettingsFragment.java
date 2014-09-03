package com.pinthecloud.athere.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.AhUser;

public class ProfileSettingsFragment extends AhFragment{

	private AhUser user;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get parameter from previous activity intent
		Intent intent = activity.getIntent();
		user = intent.getParcelableExtra(AhGlobalVariable.USER_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(app).reportActivityStart(activity);
	}


	@Override
	public void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(app).reportActivityStart(activity);
	}
}
