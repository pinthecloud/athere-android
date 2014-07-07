package com.pinthecloud.athere.fragment;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareChatActivity;
import com.pinthecloud.athere.helper.FileUtil;
import com.pinthecloud.athere.helper.PreferenceHelper;

public class SquareProfileFragment extends AhFragment{

	private ImageView profileImage;
	private EditText nickNameEditText;
	private Button startButton;
	private RelativeLayout relativeLayout;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = new PreferenceHelper(context);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_profile, container, false);

		/*
		 * Find UI component
		 */
		profileImage = (ImageView) view.findViewById(R.id.square_profile_frag_image);
		nickNameEditText = (EditText)view.findViewById(R.id.square_profile_frag_nick_name_edit_text);
		startButton = (Button) view.findViewById(R.id.square_profile_frag_start_button);
		relativeLayout = (RelativeLayout) view.findViewById(R.id.square_profile_frag_layout);


		/*
		 * Set event on layout
		 */
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard(context, nickNameEditText);
			}
		});


		/*
		 * Set event on button
		 */
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Check nick name edit text and save setting
				 */
				String message = checkNickNameEditText(nickNameEditText);
				if(!message.equals("")){
					// Unproper nick name
					// Show warning toast for each situation
					Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else{
					// Proper nick name
					// Save this setting and go to next activity
					pref.putString(AhGlobalVariable.NICK_NAME_KEY, nickNameEditText.getText().toString());
					pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY, true);

					Intent intent = new Intent(context, SquareChatActivity.class);
					startActivity(intent);
					activity.finish();
				}

				
				/*
				 * Save profile image
				 */
				try {
					Bitmap profileImageBitmap = ((BitmapDrawable)profileImage.getDrawable()).getBitmap();
					FileUtil.saveImageToInternalStorage(context, profileImageBitmap);
				} catch (IOException e) {
					// Save profile image to Internal storage error
				}
			}
		});


		/*
		 * Set Edit Text
		 */
		nickNameEditText.setHint(getResources().getString(R.string.type_nick_name_hint));
		nickNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					hideKeyboard(context, nickNameEditText);
				}
			}
		});

		
		/*
		 * Set Profile Image
		 */
		profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				profileImageOnClick(profileImage);
			}
		});

		return view;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		profileImageOnActivityResult(profileImage, requestCode, resultCode, data);
	}
}
