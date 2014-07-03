package com.pinthecloud.athere;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class AhGlobalMethod {
	public static void hideKeyboard(Context context, EditText editText) {
	    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
