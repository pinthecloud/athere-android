package com.pinthecloud.athere.fragment;

import java.io.FileNotFoundException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class ProfileImageFragment extends AhFragment{

	private PreferenceHelper pref;
	private User user;

	private ImageView profileImage; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = app.getPref();

		Intent intent = activity.getIntent();
		String userId = intent.getStringExtra(AhGlobalVariable.USER_KEY);

		// If it is other user, get the user in DB
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
		Log.d(AhGlobalVariable.LOG_TAG, "ProfileImageFragmenr onResume");

		Bitmap profileBitmap = null;
		if(user != null){
			// other user
			int w = profileImage.getWidth();
			int h = profileImage.getHeight();
			profileBitmap = BitmapUtil.convertToBitmap(user.getProfilePic(), w, h);
		}else{
			try {
				profileBitmap = FileUtil.getImageFromInternalStorage(context, AhGlobalVariable.PROFILE_PICTURE_NAME);
			} catch (FileNotFoundException e) {
				profileBitmap = BitmapFactory.decodeResource(app.getResources(), R.drawable.splash);
				Log.d(AhGlobalVariable.LOG_TAG, "Error of ProfileImageFragmet : " + e.getMessage());
			}
		}
		profileImage.setImageBitmap(profileBitmap);
	}


	@Override
	public void onStop() {
		Log.d(AhGlobalVariable.LOG_TAG, "ProfileImageFragmenr onResume");
		profileImage.setImageBitmap(null);
		super.onStop();
	}
}
