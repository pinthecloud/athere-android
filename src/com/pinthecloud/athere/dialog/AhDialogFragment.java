package com.pinthecloud.athere.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pinthecloud.athere.activity.AhActivity;

public class AhDialogFragment extends DialogFragment{

	protected Context context;
	protected AhActivity activity;
	private boolean isShowing = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = getActivity();
		activity = (AhActivity) context;
		super.onCreate(savedInstanceState);
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
}
