package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.Square;

public class SquareChupaFragment extends AhFragment{

	private Square square;


	public SquareChupaFragment(Square square) {
		super();
		this.square = square;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_chupa, container, false);
		return view;
	}
}
