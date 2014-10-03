package com.pinthecloud.athere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pinthecloud.athere.interfaces.AhDialogCallback;


public class AhAlertListDialog extends AhDialogFragment{

	protected AhDialogCallback[] ahDialogCallbacks;
	private String title;
	private String[] list;


	public AhAlertListDialog(String title, String[] list, AhDialogCallback[] ahDialogCallbacks) {
		super();
		this.ahDialogCallbacks = ahDialogCallbacks;
		this.title = title;
		this.list = list;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Make Dialog
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(getActivity());
		if(title == null){
			setStyle(STYLE_NO_TITLE, 0);
		}else{
			altBuilder.setTitle(title);
		}
		altBuilder.setItems(list, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				for(int i=0 ; i<list.length ; i++){
					if(item == i){
						ahDialogCallbacks[i].doPositiveThing(null);
					}
				}
			}
		});
		AlertDialog alertDialog = altBuilder.create();
		//		alertDialog.setCanceledOnTouchOutside(true);
		return alertDialog;
	}
}
