package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ProfileImageFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;


public class ProfileImageActivity extends AhActivity{

	private MessageHelper messageHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);

		Log(thisActivity, "here");

		/*
		 * Set helper
		 */
		messageHelper = app.getMessageHelper();


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ProfileImageFragment profileImageFragment = new ProfileImageFragment();
		fragmentTransaction.add(R.id.activity_container, profileImageFragment);
		fragmentTransaction.commit();


		/*
		 * Set Handler for forced logout
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
				if (message.getType().equals(AhMessage.TYPE.FORCED_LOGOUT.toString())) {
					String text = getResources().getString(R.string.forced_logout_title);
					Toast toast = Toast.makeText(thisActivity, text, Toast.LENGTH_LONG);
					toast.show();

					Intent intent = new Intent(ProfileImageActivity.this, SquareListActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}
}

