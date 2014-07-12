package com.pinthecloud.athere.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.ServiceClient;


public class AhFragment extends Fragment{

	protected ServiceClient serviceClient;
	protected Context context;
	protected Activity activity;
	protected PreferenceHelper pref;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		activity = (Activity) context;
		serviceClient = ((AhApplication) activity.getApplication()).getServiceClient();
	}


	protected void hideKeyboard(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
