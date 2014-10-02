package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChupaChatFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

public class ChupaChatActivity extends AhActivity {

	private MessageHelper messageHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);


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
		fragmentTransaction.add(R.id.activity_container, chupaChatFragment);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		getMenuInflater().inflate(R.menu.chupa_chat, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
