package com.pinthecloud.athere.dialog;

import java.util.Calendar;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;

public class NumberPickerDialog extends DialogFragment {

	private final int MIN_YEAR = 1950;
	private final int MAX_YEAR = 2000;
	private final int DEFAULT_YEAR = 1990;

	private AhDialogCallback ahDialogCallback;
	private AhApplication app;
	private PreferenceHelper pref;
	
	private NumberPicker yearPicker;


	public NumberPickerDialog(AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.app = AhApplication.getInstance();
		this.pref = app.getPref();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(getResources().getString(R.string.birth_of_year));
		View view = inflater.inflate(R.layout.dialog_nimber_picker, container, false);

		// Find UI Component
		yearPicker = (NumberPicker) view.findViewById(R.id.number_picker_dialog_picker);
		ImageButton setButton = (ImageButton) view.findViewById(R.id.number_picker_dialog_set_button);


		/*
		 * Set birth year picker
		 */
		yearPicker.setMinValue(MIN_YEAR);
		yearPicker.setMaxValue(MAX_YEAR);
		yearPicker.setValue(DEFAULT_YEAR);


		/*
		 * Set Button
		 */
		setButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Save year setting
				int year = yearPicker.getValue();
				Calendar c = Calendar.getInstance();
				int age = c.get(Calendar.YEAR) - (year - 1);
				pref.putInt(AhGlobalVariable.BIRTH_YEAR_KEY, year);
				pref.putInt(AhGlobalVariable.AGE_KEY, age);
				
				ahDialogCallback.doPositiveThing();
				dismiss();
			}
		});

		return view;
	}
}
