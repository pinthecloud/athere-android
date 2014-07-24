package com.pinthecloud.athere.fragment;

import java.net.MalformedURLException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.ServiceClient;


public class AhFragment extends Fragment{

	protected ServiceClient serviceClient;
	protected Context context;
	protected Activity activity;
	protected PreferenceHelper pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		activity = (Activity) context;
		try {
			serviceClient = new ServiceClient(context);
		} catch (MalformedURLException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "AhFragment onCreate : " + e.getMessage());
		}
		pref = new PreferenceHelper(context);
	}
}
