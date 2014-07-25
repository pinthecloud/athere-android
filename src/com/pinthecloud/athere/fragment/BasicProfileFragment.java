package com.pinthecloud.athere.fragment;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;

public class BasicProfileFragment extends AhFragment{

	private final int MIN_YEAR = 1950;
	private final int MAX_YEAR = 2000;
	private final int DEFAULT_YEAR = 1990;

	private EditText nickNameEditText;
	private ToggleButton maleButton;
	private ToggleButton femaleButton;
	private NumberPicker yearPicker; 
	private Button completeButton;

	private boolean isMale = true;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basic_profile, container, false);

		/*
		 * Find UI component
		 */
		nickNameEditText = (EditText) view.findViewById(R.id.basic_profile_frag_nick_name_edit_text);
		maleButton = (ToggleButton) view.findViewById(R.id.basic_profile_frag_male_button);
		femaleButton = (ToggleButton) view.findViewById(R.id.basic_profile_frag_female_button);
		yearPicker = (NumberPicker) view.findViewById(R.id.basic_profile_frag_year_picker);
		completeButton = (Button) view.findViewById(R.id.basic_profile_frag_start_button);


		/*
		 * Set nick name edit text
		 */
		nickNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					completeButton.setEnabled(false);
				}else{
					completeButton.setEnabled(true);
				}
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
		 * Set gender button
		 */
		maleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				boolean on = ((ToggleButton) view).isChecked();

				if(on){
					isMale = true;
					maleButton.setEnabled(false);
					femaleButton.setEnabled(true);
					femaleButton.setChecked(false);
				}
			}
		});
		femaleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				boolean on = ((ToggleButton) view).isChecked();

				if(on){
					isMale = false;
					maleButton.setEnabled(true);
					maleButton.setChecked(false);
					femaleButton.setEnabled(false);
				}
			}
		});


		/*
		 * Set birth year picker
		 */
		yearPicker.setMinValue(MIN_YEAR);
		yearPicker.setMaxValue(MAX_YEAR);
		yearPicker.setValue(DEFAULT_YEAR);


		/*
		 * Set Start Button
		 */
		completeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Save gender and birth year infomation to preference
				String message = checkNickNameEditText(nickNameEditText);
				if(!message.equals("")){
					// Unproper nick name
					// Show warning toast for each situation
					Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
					toast.show();
				} else{
					// Disable complete button for preventing double click
					completeButton.setEnabled(false);

					// Proper nick name
					// Save this setting and go to next activity
					int birthYear = yearPicker.getValue();
					Calendar c = Calendar.getInstance();
					int age = c.get(Calendar.YEAR) - (birthYear - 1);
					pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY, true);
					pref.putString(AhGlobalVariable.NICK_NAME_KEY, nickNameEditText.getText().toString());
					pref.putBoolean(AhGlobalVariable.IS_MALE_KEY, isMale);
					pref.putInt(AhGlobalVariable.BIRTH_YEAR_KEY, birthYear);
					pref.putInt(AhGlobalVariable.AGE_KEY, age);

					Intent intent = new Intent(context, SquareListActivity.class);
					startActivity(intent);
					activity.finish();
				}
			}
		});

		return view;
	}


	/*
	 * Check nick name EditText
	 */
	private String checkNickNameEditText(EditText editText){
		// Remove blank of along side.
		String nickName = editText.getText().toString().trim();
		editText.setText(nickName);
		editText.setSelection(nickName.length());

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
}