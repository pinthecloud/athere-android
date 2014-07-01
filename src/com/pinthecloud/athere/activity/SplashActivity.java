package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PrefHelper;

/**
 * @author hongkunyoo
 * 
 * First Page
 *
 */
public class SplashActivity extends Activity {

	private final int SPLASH_TIME = 1500;
	private final String IS_NEW_USER_KEY = "isNewUserKey";
	
	private PrefHelper pref;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		pref = new PrefHelper(this);
			
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				String isNew = pref.getString(IS_NEW_USER_KEY);
				
				// already logged in
				if (isNew != null){
					Intent i = new Intent(SplashActivity.this, SquareListActivity.class);
					startActivity(i);
				}
				// new User
				else { 
					Intent i = new Intent(SplashActivity.this, ProfileActivity.class);
					startActivity(i);
				}
			}
			
		}, SPLASH_TIME);
	}
}
