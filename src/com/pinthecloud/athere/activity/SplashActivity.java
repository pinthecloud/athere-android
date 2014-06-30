package com.pinthecloud.athere.activity;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PrefHelper;
import com.pinthecloud.athere.model.POJOTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends Activity {

	private static final int SPLASH_TIME = 1500;
	private PrefHelper pref;
	public String isNewUserKey = "isNewUserKey";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		pref = new PrefHelper(this);
			
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String isNew = pref.getString(isNewUserKey);
				
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
