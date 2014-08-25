package com.pinthecloud.athere.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.FileUtil;

public class ProfileImageFragment extends AhFragment{

	private PreferenceHelper pref;
	private AhUser user;

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
		Log.d(AhGlobalVariable.LOG_TAG, "ProfileImageFragmenr onStart");

		Bitmap profileBitmap = null;
		if(user != null){
			// other user
			int w = profileImage.getWidth();
			int h = profileImage.getHeight();
			blobStorageHelper.getBitmapAsync(_thisFragment, user.getId(), w, h, new AhEntityCallback<Bitmap>() {
				
				@Override
				public void onCompleted(Bitmap entity) {
					profileImage.setImageBitmap(entity);
				}
			});
		}else{
			profileBitmap = FileUtil.getImageFromInternalStorage(context, AhGlobalVariable.PROFILE_PICTURE_NAME);
			profileImage.setImageBitmap(profileBitmap);
		}
		
	}


	@Override
	public void onStop() {
		Log.d(AhGlobalVariable.LOG_TAG, "ProfileImageFragmenr onResume");
		profileImage.setImageBitmap(null);
		super.onStop();
	}
}
