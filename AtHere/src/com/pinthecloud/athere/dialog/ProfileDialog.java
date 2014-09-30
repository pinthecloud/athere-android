package com.pinthecloud.athere.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhUser;

public class ProfileDialog extends AhDialogFragment{

	private AhDialogCallback ahDialogCallback;
	private AhApplication app;
	private AhFragment frag;
	private AhUser user;

	private ImageView profileImage;
	private ImageView genderImage;
	private TextView nickNameText;
	private TextView ageText;
	private TextView companyNumberText;
	private Button sendChupaButton;

	private SquareHelper squareHelper;
	private UserHelper userHelper;
	private CachedBlobStorageHelper blobStorageHelper;


	public ProfileDialog(AhFragment frag, AhUser user, AhDialogCallback ahDialogCallback) {
		super();
		this.frag = frag;
		this.user = user;
		this.ahDialogCallback = ahDialogCallback;
		this.app = AhApplication.getInstance();
		this.squareHelper = app.getSquareHelper();
		this.userHelper = app.getUserHelper();
		this.blobStorageHelper = app.getBlobStorageHelper();
		setStyle(STYLE_NO_TITLE, 0);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_profile, container, false);


		/*
		 * Find UI component
		 */
		profileImage = (ImageView) view.findViewById(R.id.profile_dialog_profile);
		genderImage = (ImageView) view.findViewById(R.id.profile_dialog_gender);
		nickNameText = (TextView) view.findViewById(R.id.profile_dialog_nick_name);
		ageText = (TextView) view.findViewById(R.id.profile_dialog_age);
		companyNumberText = (TextView) view.findViewById(R.id.profile_dialog_company_number);
		sendChupaButton = (Button) view.findViewById(R.id.profile_dialog_send_button);


		/*
		 * Set UI Component
		 */
		if(user.isMale()){
			genderImage.setImageResource(R.drawable.profile_gender_m);
			companyNumberText.setTextColor(getResources().getColor(R.color.blue));
		}else{
			genderImage.setImageResource(R.drawable.profile_gender_w);
			companyNumberText.setTextColor(getResources().getColor(R.color.red_dark));
		}
		nickNameText.setText(user.getNickName());
		ageText.setText("" + user.getAge());
		companyNumberText.setText("" + user.getCompanyNum());


		/*
		 * Set event on chupa button
		 */
		sendChupaButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ahDialogCallback.doPositiveThing(null);
				dismiss();
			}
		});
		if(user.getId().equals(userHelper.getMyUserInfo().getId()) || squareHelper.isPreview()){
			sendChupaButton.setVisibility(View.GONE);
		}


		/*
		 * Set profile image
		 */
		profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ahDialogCallback.doNegativeThing(null);
			}
		});

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		blobStorageHelper.setImageViewAsync(frag, BlobStorageHelper.USER_PROFILE, 
				user.getId(), R.drawable.dialog_profile_default, profileImage, true);
	}


	@Override
	public void onStop() {
		profileImage.setImageBitmap(null);
		super.onStop();
	}
}
