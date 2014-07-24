package com.pinthecloud.athere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.interfaces.AhDialogCallback;

public class LocationConsentDialog extends DialogFragment {
	
	private AhDialogCallback ahDialogCallback;
	
	public LocationConsentDialog(AhDialogCallback ahDialogCallback) {
		super();
		this.ahDialogCallback = ahDialogCallback;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get String from resources
		Resources resources = getResources();
		String yes = resources.getString(R.string.yes);
		String no = resources.getString(R.string.no);
		String locConsent = resources.getString(R.string.location_consent);
		String locConsentMessage = resources.getString(R.string.location_consent_message);

		// Make Dialog
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(getActivity());
		altBuilder.setTitle(locConsent);
		altBuilder.setMessage(locConsentMessage);
		altBuilder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				ahDialogCallback.doPositiveThing();
			}
		});
		altBuilder.setNegativeButton(no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ahDialogCallback.doNegativeThing();
			}
		});
		AlertDialog alertDialog = altBuilder.create();
		return alertDialog;
	}
}
