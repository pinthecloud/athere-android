package com.pinthecloud.athere.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.AhActivity;
import com.pinthecloud.athere.helper.PreferenceHelper;

/**
 *  Basic Fragment class for At here application
 *  Provides each instances that are needed in fragments
 * 
 */
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
	
	protected void Log(Object... params){
		for(Object str : params) {
			if (str == null) {
				Log.e("ERROR", "null");
				continue;
			}
			Log.e("ERROR", str.toString());
		}
	}
	
	protected void Log(String... params){
		for(String str : params) {
			if (str == null) {
				Log.e("ERROR", "null");
				continue;
			}
			Log.e("ERROR", str);
		}
	}
	
	
	/*
	 * Check nick name EditText
	 */
	protected String checkNickName(String nickName){
		Log.d(AhGlobalVariable.LOG_TAG, "CheckNickNameEditText");

		// Set regular expression for checking nick name
		String nickNameRegx = "^[a-zA-Z0-9가-힣_-]{2,15}$";
		String message = "";

		/*
		 * Check logic whether this nick name is valid or not
		 * If user doesn't type in proper nick name,
		 * can't go to next activity
		 */
		// Check length of nick name
		if(nickName.length() < 2){
			message = getResources().getString(R.string.min_nick_name_message);
		} else if(!nickName.matches(nickNameRegx)){
			message = getResources().getString(R.string.bad_nick_name_message);
		} else if(nickName.length() > 15){
			message = getResources().getString(R.string.max_nick_name_message);
		}
		return message;
	}
}
