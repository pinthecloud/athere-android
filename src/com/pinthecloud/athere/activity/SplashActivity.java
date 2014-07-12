package com.pinthecloud.athere.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PreferenceHelper;

/**
 * @author hongkunyoo
 * 
 * First Page
 *
 */
public class SplashActivity extends AhActivity {

	private final int SPLASH_TIME = 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// Get device resolution and set it
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		AhGlobalVariable.DEVICE_WIDTH = displayMetrics.widthPixels;
		AhGlobalVariable.DEVICE_HEIGHT = displayMetrics.heightPixels;

		
//		Intent intent = new Intent();
//		intent.setClass(SplashActivity.this, HongkunTestAcitivity.class);
//		startActivity(intent);
		
		// Show splash image and move to next page
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				boolean isLoggedInUser = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY);
				boolean isLooggedInSquare = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);

				Intent intent = new Intent();
				if (!isLoggedInUser){
					// New User
					intent.setClass(SplashActivity.this, BasicProfileActivity.class);
				} else if(!isLooggedInSquare){
					// Already logged in
					intent.setClass(SplashActivity.this, SquareListActivity.class);
				} else{
					// Has entered a square
					intent.setClass(SplashActivity.this, SquareListActivity.class);
					//					intent.setClass(SplashActivity.this, SquareChatActivity.class);
				}
				startActivity(intent);
			}
		}, SPLASH_TIME);
	}
}
