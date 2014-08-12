package com.pinthecloud.athere.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhException;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.AhActivity;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.util.ExceptionManager;
import com.pinthecloud.athere.util.ExceptionManager.ExceptionHandler;

/**
 *  Basic Fragment class for At here application
 *  Provides each instances that are needed in fragments
 * 
 */
public class AhFragment extends Fragment implements ExceptionHandler{

	protected AhApplication app;
	protected Context context;
	protected AhActivity activity;
	protected PreferenceHelper pref;
	protected AhFragment _thisFragment;
	
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
		_thisFragment = this;
		
		ExceptionManager.setHandler(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View view = null;
		
		try {
			view = onAhCreateView(inflater, container, savedInstanceState);
		} catch (AhException ex) {
			Log(this, ex);
			ex.printStackTrace();
		} catch (Exception ex) {
			Log(this, ex);
		}
		
		return view;
	}
	
	
	
	protected View onAhCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void Log(AhFragment fragment, Object... params){
		Log.e("ERROR", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.e("ERROR", "[ "+fragment.getClass().getName() + " ]");
		for(Object str : params) {
			if (str == null) {
				Log.e("ERROR", "null");
				continue;
			}
			Log.e("ERROR", str.toString());
		}
		Log.e("ERROR", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
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

	@Override
	public void handleException(AhException ex) {
		// TODO Auto-generated method stub
		Log.e("ERROR","AhFragment handler : " + ex);
	}

	@Override
	public void handleException(Exception ex) {
		// TODO Auto-generated method stub
		
	}
}
