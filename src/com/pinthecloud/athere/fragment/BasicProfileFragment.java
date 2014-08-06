package com.pinthecloud.athere.fragment;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.dialog.NumberPickerDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;

public class BasicProfileFragment extends AhFragment{

	private EditText nickNameEditText;
	private EditText birthYearEditText;
	private NumberPickerDialog yearPickerDialog;

	private ImageButton maleButton;
	private ImageButton femaleButton;

	private ImageButton completeButton;

	private boolean isMale = true;
	private boolean isTypedNickName = false;
	private boolean isPickedBirthYear = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basic_profile, container, false);

		/*
		 * Find UI component
		 */
		nickNameEditText = (EditText) view.findViewById(R.id.basic_profile_frag_nick_name_edit_text);
		birthYearEditText = (EditText) view.findViewById(R.id.basic_profile_frag_year_text);
		maleButton = (ImageButton) view.findViewById(R.id.basic_profile_frag_male_button);
		femaleButton = (ImageButton) view.findViewById(R.id.basic_profile_frag_female_button);
		completeButton = (ImageButton) view.findViewById(R.id.basic_profile_frag_start_button);


		/*
		 * Set nick name edit text
		 */
		nickNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					isTypedNickName = false;
				}else{
					isTypedNickName = true;
				}
				completeButton.setEnabled(isCompleteButtonEnable());
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
		 * Set birth year edit text and year picker dialog
		 */
		String title = getResources().getString(R.string.birth_of_year);
		yearPickerDialog = new NumberPickerDialog(title, 1950, 2000, 1990, new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Set edit text to birth year use picked
				int birthYear = bundle.getInt(AhGlobalVariable.NUMBER_PICKER_VALUE_KEY);
				birthYearEditText.setText("" + birthYear);
			}

			@Override
			public void doNegativeThing(Bundle bundle) {
				// do nothing				
			}
		});
		birthYearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String birthYear = s.toString().trim();
				if(birthYear.length() < 1){
					isPickedBirthYear = false;
				}else{
					isPickedBirthYear = true;
				}
				completeButton.setEnabled(isCompleteButtonEnable());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		birthYearEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				yearPickerDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});
		birthYearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					yearPickerDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
				}
			}
		});
		birthYearEditText.setInputType(InputType.TYPE_NULL);


		/*
		 * Set gender button
		 */
		maleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				isMale = true;
				maleButton.setEnabled(false);
				femaleButton.setEnabled(true);
			}
		});
		femaleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				isMale = false;
				maleButton.setEnabled(true);
				femaleButton.setEnabled(false);
			}
		});
		maleButton.setEnabled(false);


		/*
		 * Set Start Button
		 */
		completeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Remove blank of along side.
				String nickName = nickNameEditText.getText().toString().trim();
				nickNameEditText.setText(nickName);
				nickNameEditText.setSelection(nickName.length());

				// Save gender and birth year infomation to preference
				String message = checkNickName(nickName);
				if(!message.equals("")){
					// Unproper nick name
					// Show warning toast for each situation
					Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
					toast.show();
				} else{
					// Proper nick name
					// Disable complete button for preventing double click
					completeButton.setEnabled(false);

					// Save this setting and go to next activity
					int birthYear = Integer.parseInt(birthYearEditText.getText().toString());
					Calendar c = Calendar.getInstance();
					int age = c.get(Calendar.YEAR) - (birthYear - 1);
					pref.putInt(AhGlobalVariable.BIRTH_YEAR_KEY, birthYear);
					pref.putInt(AhGlobalVariable.AGE_KEY, age);
					
					pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY, true);
					pref.putBoolean(AhGlobalVariable.IS_MALE_KEY, isMale);
					pref.putString(AhGlobalVariable.NICK_NAME_KEY, nickNameEditText.getText().toString());

					Intent intent = new Intent(context, SquareListActivity.class);
					startActivity(intent);
					activity.finish();
				}
			}
		});
		completeButton.setEnabled(false);

		return view;
	}


	/*
	 * Check nick name EditText
	 */
	private String checkNickName(String nickName){
		Log.d(AhGlobalVariable.LOG_TAG, "BasicProfileFragment checkNickNameEditText");

		// Set regular expression for checking nick name
		String nickNameRegx = "^[a-zA-Z0-9가-힣_-]{2,15}$";
		String message = "";

		/*
		 * Check logic whether this nick name is valid or not
		 * If user doesn't type in proper nick name,
		 * can't go to next activity
		 */
		// Check length of nick name
		if(nickName.length() < 2){
			message = getResources().getString(R.string.min_nick_name_message);
		} else if(!nickName.matches(nickNameRegx)){
			message = getResources().getString(R.string.bad_nick_name_message);
		} else if(nickName.length() > 15){
			message = getResources().getString(R.string.max_nick_name_message);
		}
		return message;
	}


	private boolean isCompleteButtonEnable(){
		return isTypedNickName && isPickedBirthYear;
	}
}