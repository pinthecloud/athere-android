package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PrefHelper;

/**
 * @author hongkunyoo
 * 
 * First Page
 *
 */
public class SplashActivity extends AhActivity {

	private final int SPLASH_TIME = 500;
	private PrefHelper pref;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		pref = new PrefHelper(this);
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				Intent intent = new Intent();
				boolean isLoggedIn = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY);

				if (!isLoggedIn){
					// New User
					intent.setClass(SplashActivity.this, ProfileActivity.class);
				} else { 
					// Already logged in
					intent.setClass(SplashActivity.this, SquareListActivity.class);
				}
				startActivity(intent);
				finish();
			}
		}, SPLASH_TIME);
	}
}
