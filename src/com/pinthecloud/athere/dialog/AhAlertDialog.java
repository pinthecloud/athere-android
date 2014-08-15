package com.pinthecloud.athere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import com.pinthecloud.athere.interfaces.AhDialogCallback;

public class AhAlertDialog extends AhDialogFragment{

	private String title;
	private String message;
	private boolean cancel;

	public AhAlertDialog(String title, String message, boolean cancel, AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
		this.title = title;
		this.message = message;
		this.cancel = cancel;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get String from resources
		Resources resources = getResources();
		String ok = resources.getString(android.R.string.ok);
		String no = resources.getString(android.R.string.no);

		// Make Dialog
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(getActivity());
		altBuilder.setTitle(title);
		altBuilder.setMessage(message);
		altBuilder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				ahDialogCallback.doPositiveThing(null);
			}
		});
		if(cancel){
			altBuilder.setNegativeButton(no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					ahDialogCallback.doNegativeThing(null);
				}
			});
		}
		AlertDialog alertDialog = altBuilder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		return alertDialog;
	}
}
