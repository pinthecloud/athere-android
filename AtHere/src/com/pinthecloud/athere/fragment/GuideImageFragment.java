package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.athere.R;

public class GuideImageFragment extends AhFragment{

	private int[] guideImageIds;
	private int position;
	private ImageView guideImage;


	public GuideImageFragment() {
		super();
	}

	public GuideImageFragment(int[] guideImageIds, int position) {
		super();
		this.guideImageIds = guideImageIds;
		this.position = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_guide_image, container, false);
		guideImage = (ImageView)view.findViewById(R.id.guide_image_frag_image);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		guideImage.setImageResource(guideImageIds[position]);
	}

	@Override
	public void onStop() {
		guideImage.setImageBitmap(null);
		super.onStop();
	}


	@Override 
	public void onSaveInstanceState(Bundle outState) {
		//first saving my state, so the bundle wont be empty.
		outState.putString("VIEWPAGER_BUG",  "VIEWPAGER_FIX");
		super.onSaveInstanceState(outState);
	}
}