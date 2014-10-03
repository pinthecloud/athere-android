package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChupaChatFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;

public class ChupaChatActivity extends AhActivity {

	private ProgressBar progressBar;
	private ChupaChatFragment chupaChatFragment;
	private MessageHelper messageHelper;
	private UserHelper userHelper;
	private AhUser user;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);


		/*
		 * Set UI and helper and user
		 */
		progressBar = (ProgressBar) findViewById(R.id.activity_progress_bar);
		messageHelper = app.getMessageHelper();
		userHelper = app.getUserHelper();
		user = userHelper.getMyUserInfo();


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		chupaChatFragment = new ChupaChatFragment();
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
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.chupa_chat, menu);
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem menuItem = menu.findItem(R.id.chupa_chat_menu_notification);
		if(user.isChupaEnable()){
			menuItem.setIcon(R.drawable.actionbar_red_chupa_alarm_on_btn);
		}else{
			menuItem.setIcon(R.drawable.actionbar_red_chupa_alarm_off_btn);
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			// Respond to the action bar's Up/Home button
			onBackPressed();
			break;
		case R.id.chupa_chat_menu_notification:
			// Handle chupa alarm
			progressBar.setVisibility(View.VISIBLE);
			progressBar.bringToFront();

			if(user.isChupaEnable()){
				user.setChupaEnable(false);
			}else{
				user.setChupaEnable(true);
			}
			userHelper.updateUserAsync(chupaChatFragment, user, new AhEntityCallback<AhUser>() {

				@Override
				public void onCompleted(AhUser entity) {
					progressBar.setVisibility(View.GONE);
					userHelper.setMyChupaEnable(entity.isChupaEnable());
					user = userHelper.getMyUserInfo();
					invalidateOptionsMenu();
				}
			});
			break;
		}
		return true;
	}
}
