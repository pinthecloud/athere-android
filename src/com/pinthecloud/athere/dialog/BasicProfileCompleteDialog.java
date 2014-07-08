package com.pinthecloud.athere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.BasicProfileActivity;

public class BasicProfileCompleteDialog extends DialogFragment{

	private static final String GENDER_KEY = "GENDER_KEY";
	private static final String BIRTH_YEAR_KEY = "BIRTH_YEAR_KEY";


	public static BasicProfileCompleteDialog newInstance(int gender, int birthYear) {
		BasicProfileCompleteDialog frag = new BasicProfileCompleteDialog();
		Bundle args = new Bundle();
		args.putInt(GENDER_KEY, gender);
		args.putInt(BIRTH_YEAR_KEY, birthYear);
		frag.setArguments(args);
		return frag;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get parameters
		int gender = getArguments().getInt(GENDER_KEY);
		int birthYear = getArguments().getInt(BIRTH_YEAR_KEY);

		Resources resources = getResources();
		String profile = resources.getString(R.string.profile);
		String genderString = resources.getString(R.string.gender);
		String male = resources.getString(R.string.male);
		String female = resources.getString(R.string.female);
		String genderDetailString = null;
		if(gender == AhGlobalVariable.MALE){
			genderDetailString = male;
		}else{
			genderDetailString = female;	
		}
		String yes = resources.getString(R.string.yes);
		String no = resources.getString(R.string.no);
		String birthYearString = resources.getString(R.string.birth_year);
		String completeMessage = resources.getString(R.string.basic_profile_complete_message);

		// Show dialog for confirming to delete profile image
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(getActivity());
		altBuilder.setTitle(profile);
		altBuilder.setMessage(completeMessage + "\n\n" + genderString + " : "
				+ genderDetailString + "\n" + birthYearString + " : " + birthYear);

		altBuilder.setPositiveButton(yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((BasicProfileActivity)getActivity()).doPositiveClick();
			}
		});
		altBuilder.setNegativeButton(no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog alertDialog = altBuilder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		return alertDialog;
	}
}
