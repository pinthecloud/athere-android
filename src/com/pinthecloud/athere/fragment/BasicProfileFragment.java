package com.pinthecloud.athere.fragment;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;

public class BasicProfileFragment extends AhFragment{

	private final String DAY_SPINNER = "mDaySpinner";
	private final String MONTH_SPINNER = "mMonthSpinner";
	private final String DAY_CALENDAR = "mCalendarView";

	private RadioGroup genderRadioGroup;
	private DatePicker yearPicker; 
	private Button startButton;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basic_profile, container, false);

		/*
		 * Find UI component
		 */
		genderRadioGroup = (RadioGroup)view.findViewById(R.id.basic_profile_frag_radio_gender);
		yearPicker = (DatePicker)view.findViewById(R.id.basic_profile_frag_year_picker);
		startButton = (Button) view.findViewById(R.id.basic_profile_frag_start_button);


		/*
		 * Set year picker
		 */
		findAndHideField(yearPicker, DAY_SPINNER);
		findAndHideField(yearPicker, MONTH_SPINNER);
		findAndHideField(yearPicker, DAY_CALENDAR);


		/*
		 * Set Start Button
		 */
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, SquareListActivity.class);
				startActivity(intent);
				activity.finish();
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