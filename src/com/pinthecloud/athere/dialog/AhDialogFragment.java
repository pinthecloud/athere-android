package com.pinthecloud.athere.dialog;

import com.pinthecloud.athere.interfaces.AhDialogCallback;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

public class AhDialogFragment extends DialogFragment{

	protected AhDialogCallback ahDialogCallback;
	private boolean isShowing = false;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
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
