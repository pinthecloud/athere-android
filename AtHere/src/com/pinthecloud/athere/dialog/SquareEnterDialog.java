package com.pinthecloud.athere.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.Square;

public class SquareEnterDialog extends AhDialogFragment{

	private TextView titleText;
	private TextView warningText;
	private EditText codeText;
	private ImageButton enterButton;

	private AhDialogCallback ahDialogCallback;
	private Square square;


	public SquareEnterDialog(Square square, AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.square = square;
		setStyle(STYLE_NO_TITLE, 0);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_square_enter, container, false);
		
		findComponent(view);
		setComponent();
		setEditText();
		setButtonEvent();
		
		return view;
	}


	private void findComponent(View view){
		titleText = (TextView) view.findViewById(R.id.square_enter_dialog_title);
		warningText = (TextView) view.findViewById(R.id.square_enter_dialog_code_warning_text);
		codeText = (EditText) view.findViewById(R.id.square_enter_dialog_code_text);
		enterButton = (ImageButton) view.findViewById(R.id.square_enter_dialog_enter_button);
	}


	private void setComponent(){
		titleText.setText(square.getName());
	}


	private void setEditText(){
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
	}


	private void setButtonEvent(){
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
	}
}
