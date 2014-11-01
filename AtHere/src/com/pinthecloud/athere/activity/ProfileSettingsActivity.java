package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ProfileSettingsFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

public class ProfileSettingsActivity extends AhActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ProfileSettingsFragment fragment = new ProfileSettingsFragment();
		fragmentTransaction.add(R.id.activity_container, fragment);
		fragmentTransaction.commit();

		setMessageHandler();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void setMessageHandler(){
		MessageHelper messageHelper = app.getMessageHelper();
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
				if (message.getType().equals(AhMessage.TYPE.FORCED_LOGOUT.toString())) {
					String text = getResources().getString(R.string.forced_logout_title);
					Toast toast = Toast.makeText(thisActivity, text, Toast.LENGTH_LONG);
					toast.show();

					Intent intent = new Intent(ProfileSettingsActivity.this, SquareListActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}
}
