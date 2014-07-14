package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PreferenceHelper;

public class SquareChupaFragment extends AhFragment{


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = new PreferenceHelper(context);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_chupa, container, false);
		return view;
	}
}
