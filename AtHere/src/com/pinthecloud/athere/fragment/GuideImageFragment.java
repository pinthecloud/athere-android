package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.pinthecloud.athere.R;

public class GuideImageFragment extends AhFragment{

	private int[] guideImageIds;
	private int position;

	private ImageView guideImage;
	private Button loginButton;


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
		View view = inflater.inflate(R.layout.fragment_guide_image, container, false);
		
		/*
		 * Find UI component
		 */
		guideImage = (ImageView)view.findViewById(R.id.guide_image_frag_image);
		loginButton = (Button)view.findViewById(R.id.guide_image_frag_login_button);
		
		
		/*
		 * Set login
		 */
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO login
			}
		});
		if(position >= guideImageIds.length-1){
			loginButton.setVisibility(View.VISIBLE);
		}else{
			loginButton.setVisibility(View.GONE);
		}
		
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
}