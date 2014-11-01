package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.fragment.ChatFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;

public class SquarePreviewActivity extends AhActivity {

	private ProgressBar progressBar;
	private AhFragment contentFragment;

	private MessageHelper messageHelper;
	private UserHelper userHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		
		setHelper();
		findComponent();
		
		Square square = getIntent().getParcelableExtra(AhGlobalVariable.SQUARE_KEY);
		setActionBar(square);
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		contentFragment = new ChatFragment(square);	
		fragmentTransaction.add(R.id.activity_container, contentFragment);
		fragmentTransaction.commit();
		
		setMessageHandler();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			exitSquare();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackPressed() {
		exitSquare();
	}


	private void findComponent(){
		progressBar = (ProgressBar) findViewById(R.id.activity_progress_bar);
	}
	
	
	private void setHelper(){
		messageHelper = app.getMessageHelper();
		userHelper = app.getUserHelper();
	}
	
	
	private void setMessageHandler(){
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
				messageHelper.triggerMessageEvent(contentFragment, message);
			}
		});
	}
	
	
	private void setActionBar(Square square){
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(square.getName());
	}
	
	
	private void exitSquare() {
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		AhUser user = userHelper.getMyUserInfo();
		userHelper.exitSquareAsync(contentFragment, user, new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean result) {
				app.removeMySquarePreference(contentFragment);
				finish();
			}
		});
	}
}
