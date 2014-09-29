package com.pinthecloud.athere.fragment;

import uk.co.senab.photoview.PhotoViewAttacher;
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
	private PhotoViewAttacher mAttacher;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		/*
		 * Get user id from previous activity
		 */
		Intent intent = activity.getIntent();
		user = intent.getParcelableExtra(AhGlobalVariable.USER_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_profile_image, container, false);


		/*
		 * Set UI component
		 */
		profileImage = (ImageView) view.findViewById(R.id.profile_image_frag_view);
		mAttacher = new PhotoViewAttacher(profileImage);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		blobStorageHelper.setImageViewAsync(thisFragment, BlobStorageHelper.USER_PROFILE, 
				user.getId(), 0, profileImage, true);
		mAttacher.update();
	}


	@Override
	public void onStop() {
		profileImage.setImageBitmap(null);
		super.onStop();
	}
}
