package com.pinthecloud.athere.fragment;

import java.lang.reflect.Field;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ToggleButton;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.dialog.BasicProfileCompleteDialog;
import com.pinthecloud.athere.helper.PreferenceHelper;

public class BasicProfileFragment extends AhFragment{

	private final String DAY_SPINNER = "mDaySpinner";
	private final String MONTH_SPINNER = "mMonthSpinner";
	private final String DAY_CALENDAR = "mCalendarView";

	private ToggleButton maleButton;
	private ToggleButton femaleButton;
	private DatePicker yearPicker; 
	private Button completeButton;
	
	private int gender = 1;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		pref = new PreferenceHelper(context);
		View view = inflater.inflate(R.layout.fragment_basic_profile, container, false);

		/*
		 * Find UI component
		 */
		maleButton = (ToggleButton) view.findViewById(R.id.basic_profile_frag_male_button);
		femaleButton = (ToggleButton) view.findViewById(R.id.basic_profile_frag_female_button);
		yearPicker = (DatePicker)view.findViewById(R.id.basic_profile_frag_year_picker);
		completeButton = (Button) view.findViewById(R.id.basic_profile_frag_start_button);

		
		/*
		 * Set gender button
		 */
		maleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				boolean on = ((ToggleButton) view).isChecked();
				
				if(on){
					gender = 1;
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
					gender = 2;
					maleButton.setEnabled(true);
					maleButton.setChecked(false);
					femaleButton.setEnabled(false);
				}
			}
		});
		
		
		/*
		 * Set birth year picker
		 */
		findAndHideField(yearPicker, DAY_SPINNER);
		findAndHideField(yearPicker, MONTH_SPINNER);
		findAndHideField(yearPicker, DAY_CALENDAR);


		/*
		 * Set Start Button
		 */
		completeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Save gender and birth year infomation to preference
				int birthYear = yearPicker.getYear();
				pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY, true);
				pref.putInt(AhGlobalVariable.GENDER_KEY, gender);
				pref.putInt(AhGlobalVariable.BIRTH_YEAR_KEY, birthYear);

				
				// Show confirming dialog
				DialogFragment dialogFragment = BasicProfileCompleteDialog.newInstance(gender, birthYear);
				dialogFragment.show(getFragmentManager(), AhGlobalVariable.DIALOG);
			}
		});

		return view;
	}


	/*
	 * find a member field by given name in date picker and hide it 
	 */
	private void findAndHideField(DatePicker datePicker, String name) {
		try {
			Field field = DatePicker.class.getDeclaredField(name);
			field.setAccessible(true);
			View fieldInstance = (View) field.get(datePicker);
			fieldInstance.setVisibility(View.GONE);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}