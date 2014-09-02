package com.pinthecloud.athere.activity;

import io.fiverocks.android.FiveRocks;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChupaChatFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

public class ProfileSettingsActivity extends AhActivity{

	private MessageHelper messageHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_settings);
		FiveRocks.init(this, AhGlobalVariable.Five_ROCKS_APP_ID, AhGlobalVariable.Five_ROCKS_APP_KEY);


		/*
		 * Set helper
		 */
		messageHelper = app.getMessageHelper();


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		final ChupaChatFragment chupaChatFragment = new ChupaChatFragment();
		fragmentTransaction.add(R.id.chupa_chat_container, chupaChatFragment);
		fragmentTransaction.commit();


		/*
		 * Set Handler for forced logout
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
				if (message.getType().equals(AhMessage.TYPE.FORCED_LOGOUT.toString())) {
					String text = _this.getResources().getString(R.string.forced_logout_title);
					Toast toast = Toast.makeText(_this, text, Toast.LENGTH_LONG);
					toast.show();

					Intent intent = new Intent(ProfileSettingsActivity.this, SquareListActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}


	@Override
	protected void onStart() {
		super.onStart();
		FiveRocks.onActivityStart(this);
	}


	@Override
	protected void onStop() {
		super.onStop();
		FiveRocks.onActivityStop(this);
	}
}
