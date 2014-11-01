package com.pinthecloud.athere.dialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.FileUtil;

public class ProfileDialog extends AhDialogFragment{

	private AhFragment frag;
	private AhUser user;
	private AhDialogCallback ahDialogCallback;

	private ImageView profileImage;
	private TextView nickNameText;
	private TextView ageGenderText;
	private RelativeLayout sendChupaLayout;

	private SquareHelper squareHelper;
	private UserHelper userHelper;
	private CachedBlobStorageHelper blobStorageHelper;


	public ProfileDialog(AhFragment frag, AhUser user, AhDialogCallback ahDialogCallback) {
		super();
		this.frag = frag;
		this.user = user;
		this.ahDialogCallback = ahDialogCallback;
		
		AhApplication app = AhApplication.getInstance();
		this.squareHelper = app.getSquareHelper();
		this.userHelper = app.getUserHelper();
		this.blobStorageHelper = app.getBlobStorageHelper();
		setStyle(STYLE_NO_TITLE, 0);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_profile, container, false);
		findComponent(view);
		setComponent();
		setClickEvent();
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		Bitmap placeHolder = FileUtil.getBitmapFromInternalStorage(context, user.getId()+AhGlobalVariable.SMALL);
		blobStorageHelper.setImageViewAsync(frag, BlobStorageHelper.USER_PROFILE, 
				user.getId(), placeHolder, profileImage, true);
	}


	@Override
	public void onStop() {
		profileImage.setImageBitmap(null);
		super.onStop();
	}


	private void findComponent(View view){
		profileImage = (ImageView) view.findViewById(R.id.profile_dialog_profile_image);
		nickNameText = (TextView) view.findViewById(R.id.profile_dialog_nick_name);
		ageGenderText = (TextView) view.findViewById(R.id.profile_dialog_age_gender);
		sendChupaLayout = (RelativeLayout) view.findViewById(R.id.profile_dialog_send_chupa_layout);
	}


	private void setComponent(){
		nickNameText.setText(user.getNickName());
		ageGenderText.setText("" + user.getAge());
		if(user.isMale()){
			ageGenderText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.general_gender_m, 0);
		}else{
			ageGenderText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.general_gender_w, 0);
		}
		if(userHelper.isMyUser(user) || squareHelper.isPreview()){
			sendChupaLayout.setVisibility(View.GONE);
		}
	}
	
	
	private void setClickEvent(){
		sendChupaLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ahDialogCallback.doPositiveThing(null);
				dismiss();
			}
		});
		profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ahDialogCallback.doNegativeThing(null);
			}
		});
	}
}
