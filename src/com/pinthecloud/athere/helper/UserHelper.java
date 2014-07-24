package com.pinthecloud.athere.helper;

import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class UserHelper {

	private Context context;
	private PreferenceHelper pref;


	public UserHelper(Context context) {
		super();
		this.context = context;
		this.pref = new PreferenceHelper(context);
	}

	public User getUser() {
		Bitmap pictureBitmap = null;
		try {
			pictureBitmap = FileUtil.getImageFromInternalStorage
					(context, AhGlobalVariable.PROFILE_PICTURE_CIRCLE_NAME);
		} catch (FileNotFoundException e) {
			pictureBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_default_image);
			pictureBitmap = BitmapUtil.cropRound(pictureBitmap);
			Log.d(AhGlobalVariable.LOG_TAG, "SquareProfileFragment enterSquare : " + e.getMessage());
		}
		String profilePic = BitmapUtil.convertToString(pictureBitmap);

		User user = new User();
		user.setNickName(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
		user.setProfilePic(profilePic);
		user.setSquareId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
		user.setMale(pref.getBoolean(AhGlobalVariable.IS_MALE_KEY));
		user.setAge(pref.getInt(AhGlobalVariable.AGE_KEY));
		user.setRegistrationId(pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY));
		user.setCompanyNum(pref.getInt(AhGlobalVariable.COMPANY_NUMBER_KEY));
		return user;
	}
}
