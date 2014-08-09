package com.pinthecloud.athere.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.Square;

public class SquareCodeDialog extends DialogFragment{

	private AhDialogCallback ahDialogCallback;
	private Square square;

	private EditText codeText;
	private ImageButton enterButton;

	public SquareCodeDialog(Square square, AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.square = square;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(square.getName());
		View view = inflater.inflate(R.layout.dialog_square_code, container, false);

		/*
		 * Find UI component
		 */
		codeText = (EditText) view.findViewById(R.id.square_code_dialog_code_edit_text);
		enterButton = (ImageButton) view.findViewById(R.id.square_code_dialog_enter_button);


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
		 * Set enter button event
		 */
		enterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String code = codeText.getText().toString().trim();
				Bundle bundle = new Bundle();
				bundle.putString(AhGlobalVariable.CODE_VALUE_KEY, code);
				ahDialogCallback.doPositiveThing(bundle);
			}
		});
		enterButton.setEnabled(false);

		return view;
	}
}
