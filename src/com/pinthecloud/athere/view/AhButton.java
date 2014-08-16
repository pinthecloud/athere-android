package com.pinthecloud.athere.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;

public class AhButton extends Button {

	public AhButton(Context context) {
		super(context);
	}
	
	public AhButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AhButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean performClick() {
		boolean returnValue = false;
		try{
			returnValue = super.performClick();
		} catch (AhException ex) {
			ExceptionManager.fireException(ex);
		} catch (Exception ex) {
		}
		return returnValue;
	}
}
