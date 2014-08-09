package com.pinthecloud.athere.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;

/**
 * 
 *  First Page
 *  Routes the page
 *  	to BasicProfileActivity if AhGlobalVariable.IS_LOGGED_IN_USER_KEY is false
 *  	to SquareListActivity   if AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY is false
 *  	to SquareActivity 		else
 */
public class SplashActivity extends AhActivity {

	private final int SPLASH_TIME = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
		/*
		 * Get device resolution and set it
		 */
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		AhGlobalVariable.DEVICE_WIDTH = displayMetrics.widthPixels;
		AhGlobalVariable.DEVICE_HEIGHT = displayMetrics.heightPixels;
		AhGlobalVariable.DEVICE_DPI = displayMetrics.densityDpi;
		AhGlobalVariable.DEVICE_DENSITY = displayMetrics.density;
		AhGlobalVariable.APP_NAME = getResources().getString(R.string.app_name);


		/*
		 * Exception for hongkun
		 */
		// Erase Later
		String httpAgent = "Dalvik/1.6.0 (Linux; U; Android 4.0.4; SHW-M250K Build/IMM76D)";
		String note = "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SHV-E250S Build/KOT49H)";
		if (httpAgent.equals(System.getProperty("http.agent")) || note.equals(System.getProperty("http.agent"))){
			new AlertDialog.Builder(this)
			.setTitle("Routing Dialog")
			.setMessage("Want to Go to HongkunTest?")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					startActivity(new Intent(SplashActivity.this, HongkunTestAcitivity.class)); 
					finish();
					return;
				}
			})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					/*
					 * Show splash image and move to next page
					 */
					new Handler().postDelayed(new Runnable(){

						@Override
						public void run() {
							/*
							 * Move to next activity by user status
							 */
							boolean isLoggedInUser = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY);
							boolean isLooggedInSquare = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);

							Intent intent = new Intent();
							if (!isLoggedInUser){
								// New User
								intent.setClass(SplashActivity.this, BasicProfileActivity.class);
							} else if(!isLooggedInSquare){
								// Already logged in
								//					intent.setClass(SplashActivity.this, BasicProfileActivity.class);
								intent.setClass(SplashActivity.this, SquareListActivity.class);
							} else{
								// Has entered a square
								//					intent.setClass(SplashActivity.this, BasicProfileActivity.class);
								//								intent.setClass(SplashActivity.this, SquareListActivity.class);
								intent.setClass(SplashActivity.this, SquareActivity.class);
							}
							startActivity(intent);
						}

					}, SPLASH_TIME);
					// Erase Later
				}
			})
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
		} else {
			new Handler().postDelayed(new Runnable(){

				@Override
				public void run() {
					/*
					 * Move to next activity by user status
					 */
					boolean isLoggedInUser = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY);
					boolean isLooggedInSquare = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);

					Intent intent = new Intent();
					if (!isLoggedInUser){
						// New User
						intent.setClass(SplashActivity.this, BasicProfileActivity.class);
					} else if(!isLooggedInSquare){
						// Already logged in
						//						intent.setClass(SplashActivity.this, BasicProfileActivity.class);
						intent.setClass(SplashActivity.this, SquareListActivity.class);
					} else{
						// Has entered a square
						//					intent.setClass(SplashActivity.this, BasicProfileActivity.class);
						//						intent.setClass(SplashActivity.this, SquareListActivity.class);
						intent.setClass(SplashActivity.this, SquareActivity.class);
					}
					startActivity(intent);
				}

			}, SPLASH_TIME);
		}
	}
}
