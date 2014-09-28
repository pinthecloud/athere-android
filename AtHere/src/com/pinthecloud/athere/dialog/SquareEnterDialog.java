package com.pinthecloud.athere.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.Square;

public class SquareEnterDialog extends AhDialogFragment{

	public static final String SHOW_CODE = "SHOW_CODE"; 
	private boolean showCode;

	private TextView warningText;
	private EditText codeText;
	private ImageButton enterButton;
	private Button previewButton;

	private AhDialogCallback ahDialogCallback;
	private Square square;


	public SquareEnterDialog(Square square, AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.square = square;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(square.getName());
		View view = inflater.inflate(R.layout.dialog_square_enter, container, false);
		showCode = getArguments().getBoolean(SHOW_CODE);

		
		/*
		 * Find UI component
		 */
		warningText = (TextView) view.findViewById(R.id.square_enter_dialog_code_warning_text);
		codeText = (EditText) view.findViewById(R.id.square_enter_dialog_code_text);
		enterButton = (ImageButton) view.findViewById(R.id.square_enter_dialog_enter_button);
		previewButton = (Button) view.findViewById(R.id.square_enter_dialog_preview_button);


		/*
		 * Set UI
		 */
		if(!showCode){
			warningText.setVisibility(View.GONE);
			codeText.setVisibility(View.GONE);
			enterButton.setVisibility(View.GONE);
		}


		/*
		 * Set code edit text
		 */
		codeText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String code = s.toString().trim();
				if(code.length() < 1){
					enterButton.setEnabled(false);
				}else{
					enterButton.setEnabled(true);
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
		 * Set button event
		 */
		enterButton.setEnabled(false);
		enterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String code = codeText.getText().toString().trim();
				if(code.equals(square.getCode())){
					ahDialogCallback.doPositiveThing(null);
					dismiss();
				}else{
					codeText.setText(code);
					codeText.setSelection(code.length());
					warningText.setText(getResources().getString(R.string.bad_entry_code_message));
				}
			}
		});
		previewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ahDialogCallback.doNegativeThing(null);
			}
		});

		return view;
	}
}
