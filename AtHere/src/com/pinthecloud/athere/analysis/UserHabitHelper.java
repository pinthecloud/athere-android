package com.pinthecloud.athere.analysis;

import io.userhabit.service.Userhabit;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.activity.AhActivity;

public class UserHabitHelper {

	private final String USER_HABIT_KEY = "06374d75c6ade6b5d2f6365df933120e106e413e";


	public void activityStart(AhActivity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			Userhabit.activityStart(activity, USER_HABIT_KEY);
		}
	}


	public void activityStop(AhActivity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			Userhabit.activityStop(activity);
		}
	}
}
