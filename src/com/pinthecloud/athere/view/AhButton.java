package com.pinthecloud.athere.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;

public class AhButton extends Button {

	public AhButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public AhButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public AhButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean performClick() {
		// TODO Auto-generated method stub
		boolean returnValue = false;
		try{
			returnValue = super.performClick();
		} catch (AhException ex) {
			ExceptionManager.fireException(ex);
		} catch (Exception ex) {
			ExceptionManager.fireException(ex);
		}
		return returnValue;
	}
}
