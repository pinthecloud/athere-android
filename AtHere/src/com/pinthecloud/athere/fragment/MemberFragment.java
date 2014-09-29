package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.Square;

public class MemberFragment extends AhFragment{

	private Square square;


	public MemberFragment() {
		super();
	}


	public MemberFragment(Square square) {
		super();
		this.square = square;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_member, container, false);
		return view;
	}
}
