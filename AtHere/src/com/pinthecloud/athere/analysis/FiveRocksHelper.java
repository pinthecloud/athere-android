package com.pinthecloud.athere.analysis;

import io.fiverocks.android.FiveRocks;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.activity.AhActivity;

public class FiveRocksHelper {

	private final String Five_ROCKS_APP_ID = "53f9e732333a3895de000001";
	private final String Five_ROCKS_APP_KEY = "Mx8kZ2BxcZKXZPBz5UV8";


	public void initFiveRocks(AhActivity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			FiveRocks.setDebugEnabled(true);
			FiveRocks.init(activity, Five_ROCKS_APP_ID, Five_ROCKS_APP_KEY);
			// FiveRocks.setUserCohortVariable(1, "DemoUser");	
		}
	}


	public void onActivityStart(AhActivity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			FiveRocks.onActivityStart(activity);
		}
	}


	public void onActivityStop(AhActivity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			FiveRocks.onActivityStop(activity);
		}
	}
}
