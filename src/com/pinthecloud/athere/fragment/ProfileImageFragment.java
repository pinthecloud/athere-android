package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.athere.R;

public class ProfileImageFragment extends AhFragment{

	private ImageView profileView; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chupa_chat, container, false);
		
		/*
		 * Set UI component
		 */
		profileView = (ImageView) view.findViewById(R.id.profile_image_frag_view);
		
		
		return view;
	}

}
