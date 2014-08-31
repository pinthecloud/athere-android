package com.pinthecloud.athere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pinthecloud.athere.interfaces.AhDialogCallback;

public class AhAlertDialog extends AhDialogFragment{

	private AhDialogCallback ahDialogCallback;
	private String title;
	private String message;
	private String okMessage;
	private String cancelMessage;
	private boolean cancel;

	
	public AhAlertDialog(String title, String message, boolean cancel, AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.title = title;
		this.message = message;
		this.cancel = cancel;
	}

	
	public AhAlertDialog(String title, String message, String okMessage, String cancelMessage, boolean cancel, AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.title = title;
		this.message = message;
		this.okMessage = okMessage;
		this.cancelMessage = cancelMessage;
		this.cancel = cancel;
	}

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(okMessage == null){
			okMessage =  getResources().getString(android.R.string.ok);
		}
		if(cancelMessage == null){
			cancelMessage =  getResources().getString(android.R.string.no);	
		}

		// Make Dialog
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(activity);
		if(title == null){
			setStyle(STYLE_NO_TITLE, 0);
		}else{
			altBuilder.setTitle(title);
		}
		altBuilder.setMessage(message);
		altBuilder.setPositiveButton(okMessage, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				ahDialogCallback.doPositiveThing(null);
				dismiss();
			}
		});
		if(cancel){
			altBuilder.setNegativeButton(cancelMessage, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					ahDialogCallback.doNegativeThing(null);
					dismiss();
				}
			});
		}
		AlertDialog alertDialog = altBuilder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		return alertDialog;
	}
}
