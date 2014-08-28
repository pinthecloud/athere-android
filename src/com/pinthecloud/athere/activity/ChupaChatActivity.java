package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChupaChatFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

import io.fiverocks.android.FiveRocks;

public class ChupaChatActivity extends AhActivity {

	private MessageHelper messageHelper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chupa_chat);
	    FiveRocks.init(this, FiveRocks_AppId, FiveRocks_AppKey);
		
	    
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

		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
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
		super.onStart();
		FiveRocks.onActivityStart(this);
	}


	@Override
	protected void onStop() {
		super.onStop();
		FiveRocks.onActivityStop(this);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
