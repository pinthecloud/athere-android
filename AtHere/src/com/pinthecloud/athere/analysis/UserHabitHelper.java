package com.pinthecloud.athere.analysis;

import io.userhabit.service.Userhabit;
import android.app.Activity;

import com.pinthecloud.athere.AhGlobalVariable;

public class UserHabitHelper {

	private final String USER_HABIT_KEY = "06374d75c6ade6b5d2f6365df933120e106e413e";


	public void activityStart(Activity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			Userhabit.activityStart(activity, USER_HABIT_KEY);
		}
	}


	public void activityStop(Activity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			Userhabit.activityStop(activity);
		}
	}
}
