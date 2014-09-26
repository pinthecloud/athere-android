package com.pinthecloud.athere.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhUser;


public class BasicProfileFragment extends AhFragment{

	private ProgressBar progressBar;
	private EditText nickNameEditText;
	private EditText birthYearEditText;
	private ImageButton startButton;

	private boolean isTypedNickName = false;
	private boolean isPickedProfileImage = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_basic_profile, container, false);		
		AhUser user = userHelper.getMyUserInfo();


		/*
		 * Find UI component
		 */
		progressBar = (ProgressBar) view.findViewById(R.id.basic_profile_frag_progress_bar);
		nickNameEditText = (EditText) view.findViewById(R.id.basic_profile_frag_nick_name_edit_text);
		birthYearEditText = (EditText) view.findViewById(R.id.basic_profile_frag_birth_gender_text);
		startButton = (ImageButton) view.findViewById(R.id.basic_profile_frag_start_button);


		/*
		 * Set nick name edit text
		 */
		nickNameEditText.setText(user.getNickName());
		nickNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					isTypedNickName = false;
				}else{
					isTypedNickName = true;
				}
				startButton.setEnabled(isCompleteButtonEnable());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});


		/*
		 * Set gender and birth year edit text
		 */
		boolean isMale = user.isMale();
		int age = user.getAge();
		String gender = getResources().getString(R.string.male);
		if(!isMale){
			gender = getResources().getString(R.string.female);
		}
		birthYearEditText.setText(age + " " + gender);


		/*
		 * Set Start Button
		 */
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String nickName = nickNameEditText.getText().toString().trim();
				nickNameEditText.setText(nickName);
				nickNameEditText.setSelection(nickName.length());


				/*
				 * Unproper nick name
				 * Show warning toast for each situation
				 */
				String message = app.checkNickName(nickName);
				if(!message.equals("")){
					Toast.makeText(context, message, Toast.LENGTH_LONG)
					.show();
					return;
				} 


				/*
				 * Proper nick name
				 * Show progress bar
				 * Disable complete button for preventing double click
				 * Save this setting and go to next activity
				 */
				progressBar.setVisibility(View.VISIBLE);
				startButton.setEnabled(false);

				userHelper.setMyChupaEnable(true)
				.setMyCompanyNum(0)
				.setMyNickName(nickName);

				AhUser user = userHelper.getMyUserInfo();
				userHelper.addUserAsync(thisFragment, user, new AhEntityCallback<AhUser>() {

					@Override
					public void onCompleted(AhUser entity) {
						progressBar.setVisibility(View.GONE);

						String gender = "Male";
						if(!entity.isMale()){
							gender = "Female";
						}
						gaHelper.sendEventGA(
								thisFragment.getClass().getSimpleName(),
								"CheckGender",
								gender);
						gaHelper.sendEventGA(
								thisFragment.getClass().getSimpleName(),
								"CheckAge",
								""+entity.getAge());

						userHelper.setLoggedInUser(true)
						.setMyId(entity.getId());

						Intent intent = new Intent(context, SquareListActivity.class);
						startActivity(intent);
						activity.finish();
					}
				});
			}
		});

		return view;
	}

	private boolean isCompleteButtonEnable(){
		return isTypedNickName && isPickedProfileImage;
	}
}