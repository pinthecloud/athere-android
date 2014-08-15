package com.pinthecloud.athere.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.util.BitmapUtil;

public class ProfileDialog extends AhDialogFragment{

	private User user;
	private ImageView profileImage;
	private ImageView genderImage;
	private TextView nickNameText;
	private TextView ageText;
	private TextView companyNumberText;
	private Button sendChupaButton;


	public ProfileDialog(User user, AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.user = user;
		setStyle(STYLE_NO_TITLE, 0);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_profile, container, false);

		/*
		 * Find UI component
		 */
		profileImage = (ImageView) view.findViewById(R.id.profile_dialog_profile);
		genderImage = (ImageView) view.findViewById(R.id.profile_dialog_gender);
		nickNameText = (TextView) view.findViewById(R.id.profile_dialog_nick_name);
		ageText = (TextView) view.findViewById(R.id.profile_dialog_age);
		companyNumberText = (TextView) view.findViewById(R.id.profile_dialog_company_number);
		sendChupaButton = (Button) view.findViewById(R.id.profile_dialog_send_button);


		/*
		 * Set UI Component
		 */
		if(user.isMale()){
			genderImage.setImageResource(R.drawable.profile_gender_m);
		}else{
			genderImage.setImageResource(R.drawable.profile_gender_w);
		}
		genderImage.bringToFront();
		nickNameText.setText(user.getNickName());
		ageText.setText("" + user.getAge());
		companyNumberText.setText("" + user.getCompanyNum());


		/*
		 * Set profile image
		 */
		final Bitmap profileBitmap = BitmapUtil.convertToBitmap(user.getProfilePic());
		profileImage.setImageBitmap(profileBitmap);
		profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		/*
		 * Set eventon button
		 */
		sendChupaButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ahDialogCallback.doPositiveThing(null);
				dismiss();
			}
		});

		return view;
	}
}
