package com.pinthecloud.athere.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.activity.AhActivity;
import com.pinthecloud.athere.helper.PreferenceHelper;


public class AhFragment extends Fragment{

	protected AhApplication app;
	protected Context context;
	protected AhActivity activity;
	protected PreferenceHelper pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*
		 * Set static value
		 */
		app = AhApplication.getInstance();
		context = getActivity();
		activity = (AhActivity) context;
		pref = app.getPref();
	}
}
