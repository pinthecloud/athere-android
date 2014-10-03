package com.pinthecloud.athere.analysis;

import com.flurry.android.FlurryAgent;
import android.app.Activity;

import com.pinthecloud.athere.AhGlobalVariable;

public class FlurryHelper {

	private final String FLURRY_KEY = "3YCMVJT5RBCBZDH53PR2";
	
	public void onStartSession(Activity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			FlurryAgent.onStartSession(activity,FLURRY_KEY);
		}
	}
	 
	public void onEndSession(Activity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			FlurryAgent.onEndSession(activity);
		}
	}
}
