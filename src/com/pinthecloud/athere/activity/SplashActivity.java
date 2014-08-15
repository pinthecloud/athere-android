package com.pinthecloud.athere.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.AhThread;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.VersionHelper;
import com.pinthecloud.athere.model.AppVersion;

/**
 * 
 *  First Page
 *  Routes the page
 *  	to BasicProfileActivity if AhGlobalVariable.IS_LOGGED_IN_USER_KEY is false
 *  	to SquareListActivity   if AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY is false
 *  	to SquareActivity 		else
 */
public class SplashActivity extends AhActivity implements Runnable{

	private final int SPLASH_TIME = 300;
	private VersionHelper versionHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		versionHelper = app.getVersionHelper();
		
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


		//////////////////////////////////////////////////////////////
		// Erase Later (Exception for hongkun)
		//////////////////////////////////////////////////////////////
		if (!isHongkunTest()) {
			// Start Chupa Application
			new AhThread(this).start();
		}
		
		
	}
	
	
	
	public boolean isHongkunTest() {
		String myGal2 = "Dalvik/1.6.0 (Linux; U; Android 4.0.4; SHW-M250K Build/IMM76D)";
		String note = "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SHV-E250S Build/KOT49H)";
		String myGal3 = "Dalvik/1.6.0 (Linux; U; Android 4.3; SHW-M440S Build/JSS15J)";
		String httpAgent = System.getProperty("http.agent");
		if (!((myGal2.equals(httpAgent)			// hongkunyoo Galaxy 2 
				|| note.equals(httpAgent)) 		// Note 2
				|| myGal3.equals(httpAgent)))	// Galaxy 3
			return false;
		
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
					new AhThread(SplashActivity.this).start();
					return;
				}
			})
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
		return true;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		final AppVersion serverVer = versionHelper.getServerAppVersionSync(null);
		double clientVer = versionHelper.getClientAppVersion();
		if (serverVer.getVersion() > clientVer) {
			SplashActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					new AlertDialog.Builder(SplashActivity.this)
					.setTitle("Please Update your Application")
					.setMessage("You need to update your Application")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int whichButton) {
					                /* User clicked OK so do some stuff */
					                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?id="+AhGlobalVariable.GOOGLE_STORE_APP_ID));
					                SplashActivity.this.startActivity(intent);
					        }
					})
					.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int whichButton) {
					        	if (serverVer.getType().equals(AppVersion.TYPE.MANDATORY.toString())){
					        		android.os.Process.killProcess(android.os.Process.myPid());
				                    System.exit(1);
					        	} else {
					        		goToNextActivity();
					        	}
					        }
					})
					.show();
				}
			});
			return;
		}
		
		goToNextActivity();
		
	}
	
	public void goToNextActivity() {
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
			intent.setClass(SplashActivity.this, SquareListActivity.class);
		} else{
			// Has entered a square
			intent.setClass(SplashActivity.this, SquareActivity.class);
		}
		startActivity(intent);
	}
}
