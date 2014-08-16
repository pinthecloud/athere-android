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
import com.pinthecloud.athere.sqlite.UserDBHelper;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class ProfileImageFragment extends AhFragment{

	private PreferenceHelper pref;
	private UserDBHelper userDBHelper;
	private User user;

	private ImageView profileImageView; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = app.getPref();
		userDBHelper = app.getUserDBHelper();

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
		profileImageView = (ImageView) view.findViewById(R.id.profile_image_frag_view);
		if(user != null){
			// other user
			profileImageView.setImageBitmap(BitmapUtil.convertToBitmap(user.getProfilePic()));
		}else{
			Bitmap profileBitmap = null;
			try {
				profileBitmap = FileUtil.getImageFromInternalStorage(context, AhGlobalVariable.PROFILE_PICTURE_NAME);
			} catch (FileNotFoundException e) {
				profileBitmap = BitmapFactory.decodeResource(app.getResources(), R.drawable.splash);
				Log.d(AhGlobalVariable.LOG_TAG, "Error of ProfileImageFragmet : " + e.getMessage());
			}
			profileImageView.setImageBitmap(profileBitmap);
		}

		return view;
	}

}
