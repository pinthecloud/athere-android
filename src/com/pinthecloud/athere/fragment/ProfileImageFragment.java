package com.pinthecloud.athere.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.model.AhUser;

public class ProfileImageFragment extends AhFragment{

	private AhUser user;
	private ImageView profileImage; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		/*
		 * Get user id from previous activity
		 */
		Intent intent = activity.getIntent();
		String userId = intent.getStringExtra(AhGlobalVariable.USER_KEY);
		if(!userId.equals(pref.getString(AhGlobalVariable.USER_ID_KEY))){
			user = userDBHelper.getUser(userId);
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_image, container, false);


		/*
		 * Set UI component
		 */
		profileImage = (ImageView) view.findViewById(R.id.profile_image_frag_view);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		String id = AhGlobalVariable.MY_PROFILE_PICTURE;
		if(user != null){
			id = user.getId();
		}
		blobStorageHelper.setImageViewAsync(thisFragment, BlobStorageHelper.USER_PROFILE, 
				id, 0, profileImage, true);
	}


	@Override
	public void onStop() {
		profileImage.setImageBitmap(null);
		super.onStop();
	}
}
