package com.pinthecloud.athere.analysis;

import io.userhabit.service.Userhabit;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.activity.AhActivity;

public class UserHabitHelper {

	private final String USER_HABIT_KEY = "2028c9a3c48fe760421cd5354e64fc3336b1d447";


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
