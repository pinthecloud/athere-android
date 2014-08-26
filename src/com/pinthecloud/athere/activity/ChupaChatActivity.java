package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChupaChatFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders;

public class ChupaChatActivity extends AhActivity {
	
	MessageHelper messageHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chupa_chat);

		// for google analytics
		Tracker t = ((AhApplication) getApplication()).getTracker(AhApplication.TrackerName.APP_TRACKER);
		
		t.setScreenName("ChupaChatActivity");
		t.send(new HitBuilders.AppViewBuilder().build());
		
		
		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		final ChupaChatFragment chupaChatFragment = new ChupaChatFragment();
		fragmentTransaction.add(R.id.chupa_chat_container, chupaChatFragment);
		fragmentTransaction.commit();
		
		messageHelper = app.getMessageHelper();
		
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {
			
			@Override
			public void onCompleted(AhMessage message) {
				// TODO Auto-generated method stub
				if (message.getType().equals(AhMessage.TYPE.FORCED_LOGOUT.toString())) {
					String text = _this.getResources().getString(R.string.forced_logout_title);
					Toast toast = Toast.makeText(_this, text, Toast.LENGTH_LONG);
					toast.show();
					Intent intent = new Intent(ChupaChatActivity.this, SquareListActivity.class);
					startActivity(intent);
					finish();
					return;
				}
				messageHelper.triggerMessageEvent(chupaChatFragment, message);
			}
		});
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

}
