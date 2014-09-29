package com.pinthecloud.athere.deprecated;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.dialog.AhDialogFragment;
import com.pinthecloud.athere.interfaces.AhDialogCallback;

public class NumberPickerDialog extends AhDialogFragment {

	private AhDialogCallback ahDialogCallback;
	private NumberPicker yearPicker;
	private int min;
	private int max;
	private int defaultValue;


	public NumberPickerDialog(int min, int max, int defaultValue, AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.min = min;
		this.max = max;
		this.defaultValue = defaultValue;
		setStyle(STYLE_NO_TITLE, 0);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_number_picker, container, false);

		// Find UI Component
		yearPicker = (NumberPicker) view.findViewById(R.id.number_picker_dialog_picker);
		ImageButton setButton = (ImageButton) view.findViewById(R.id.number_picker_dialog_set_button);


		/*
		 * Set birth year picker
		 */
		yearPicker.setMinValue(min);
		yearPicker.setMaxValue(max);
		yearPicker.setValue(defaultValue);


		/*
		 * Set Button
		 */
		setButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int year = yearPicker.getValue();
				Bundle bundle = new Bundle();
				bundle.putInt(AhGlobalVariable.NUMBER_PICKER_VALUE_KEY, year);
				ahDialogCallback.doPositiveThing(bundle);
				dismiss();
			}
		});

		return view;
	}
}
