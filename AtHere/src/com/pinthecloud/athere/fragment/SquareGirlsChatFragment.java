package com.pinthecloud.athere.fragment;

import com.pinthecloud.athere.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SquareGirlsChatFragment extends AhFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_girls_chat, container, false);
		return view;
	}
}