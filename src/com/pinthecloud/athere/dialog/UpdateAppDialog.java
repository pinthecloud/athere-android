package com.pinthecloud.athere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.interfaces.AhDialogCallback;

public class UpdateAppDialog extends AhDialogFragment{

	public UpdateAppDialog(AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get String from resources
		Resources resources = getResources();
		String yes = resources.getString(R.string.yes);
		String no = resources.getString(R.string.no);
		String title = getResources().getString(R.string.update_app_title);
		String message = getResources().getString(R.string.update_app_message);

		// Make Dialog
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(getActivity());
		altBuilder.setTitle(title);
		altBuilder.setMessage(message);
		altBuilder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				ahDialogCallback.doPositiveThing(null);
			}
		});
		altBuilder.setNegativeButton(no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ahDialogCallback.doNegativeThing(null);
			}
		});
		AlertDialog alertDialog = altBuilder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		return alertDialog;
	}
}
