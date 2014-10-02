package com.pinthecloud.athere.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.analysis.GAHelper;
import com.pinthecloud.athere.fragment.AhFragment;

public class AhDialogFragment extends DialogFragment {

	protected AhDialogFragment thisFragment;
	protected Context context;
	protected Activity activity;
	protected GAHelper gaHelper;
	protected String simpleClassName;
	private boolean isShowing = false;


	public AhDialogFragment() {
		super();
		this.thisFragment = this;
		this.gaHelper = AhApplication.getInstance().getGAHelper();
		this.simpleClassName =  thisFragment.getClass().getSimpleName();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = getActivity();
		activity = (Activity) context;
		super.onCreate(savedInstanceState);
		gaHelper.sendScreenGA(simpleClassName);
	}


	@Override
	public void onStart() {
		super.onStart();
		gaHelper.reportActivityStart(activity);
	}


	@Override
	public void onStop() {
		super.onStop();
		gaHelper.reportActivityStop(activity);
	}


	@Override
	public void show(FragmentManager manager, String tag) {
		if(isShowing){
			return;
		}else{
			super.show(manager, tag);
			isShowing = true;
		}
	}


	@Override
	public void onDismiss(DialogInterface dialog) {
		isShowing = false;
		super.onDismiss(dialog);
	}


	public boolean isShowing(){
		return isShowing;
	}


	public void Log(AhFragment fragment, Object... params){
		if(AhGlobalVariable.DEBUG_MODE){
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
	}
}
