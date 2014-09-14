package com.pinthecloud.athere.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.BasicProfileActivity;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.VersionHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AppVersion;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.AsyncChainer.Chainable;


public class SplashFragment extends AhFragment {

	private VersionHelper versionHelper;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		versionHelper = app.getVersionHelper();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_splash, container, false);
		
		
		/*
		 * Set notification removed when launched app 
		 */
		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
		

		/*
		 * Get device resolution and set it
		 */
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		AhGlobalVariable.DEVICE_WIDTH = displayMetrics.widthPixels;
		AhGlobalVariable.DEVICE_HEIGHT = displayMetrics.heightPixels;
		AhGlobalVariable.DEVICE_DPI = displayMetrics.densityDpi;
		AhGlobalVariable.DEVICE_DENSITY = displayMetrics.density;


		/*
		 * Get unique android id
		 */
		if(pref.getString(AhGlobalVariable.ANDROID_ID_KEY).equals(PreferenceHelper.DEFAULT_STRING)){
			String androidId = Secure.getString(app.getContentResolver(), Secure.ANDROID_ID);
			pref.putString(AhGlobalVariable.ANDROID_ID_KEY, androidId);
		}


		/*
		 * If time is up, remove local preferences.
		 */
		if(pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY)){
			Time time = new Time();
			time.setToNow();
			String currentTime = time.format("%Y:%m:%d:%H");
			String[] currentArray = currentTime.split(":");
			int currentYear = Integer.parseInt(currentArray[0]);
			int currentMonth = Integer.parseInt(currentArray[1]);
			int currentDay = Integer.parseInt(currentArray[2]);
			int currentHour = Integer.parseInt(currentArray[3]);

			String lastLoggedInSquareTime = pref.getString(AhGlobalVariable.TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY);
			String[] lastArray = lastLoggedInSquareTime.split(":");
			int lastYear = Integer.parseInt(lastArray[0]);
			int lastMonth = Integer.parseInt(lastArray[1]);
			int lastDay = Integer.parseInt(lastArray[2]);
			int lastHour = Integer.parseInt(lastArray[3]);

			if(currentYear > lastYear || currentMonth > lastMonth || currentDay > lastDay + 1){
				app.removeSquarePreference(thisFragment);
			} else if(currentDay > lastDay && lastHour < 12){
				app.removeSquarePreference(thisFragment);
			} else if(currentDay > lastDay && currentHour >= 12){
				app.removeSquarePreference(thisFragment);
			} else if(currentDay == lastDay && lastHour < 12 && currentHour >= 12){
				app.removeSquarePreference(thisFragment);
			}
		}


		/*
		 * Check whether it is first time launching app after update
		 */
		if (pref.getString(AhGlobalVariable.FIRST_UPDATE).equals(PreferenceHelper.DEFAULT_STRING)) {
			pref.removePref(AhGlobalVariable.REGISTRATION_ID_KEY);
			pref.putString(AhGlobalVariable.FIRST_UPDATE, AhGlobalVariable.FIRST_UPDATE);
		}


		/*
		 * Start Chupa Application
		 */
		// Erase Later (Exception for hongkun)
		//		if (AhGlobalVariable.DEBUG_MODE) {
		//			isHongkunTest();
		//			return view;
		//		}
		runChupa();
		return view;
	}


	//	private boolean isHongkunTest() {
	//		
	//		//		String myGal2 = "Dalvik/1.6.0 (Linux; U; Android 4.0.4; SHW-M250K Build/IMM76D)";
	//		//		//		String note = "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SHV-E250S Build/KOT49H)";
	//		//		//		String myGal3 = "Dalvik/1.6.0 (Linux; U; Android 4.3; SHW-M440S Build/JSS15J)";
	//		//		String myGal3 = "";
	//		//		String httpAgent = System.getProperty("http.agent");
	//		//		if (!((myGal2.equals(httpAgent)			// hongkunyoo Galaxy 2 
	//		//				|| myGal3.equals(httpAgent))))	// Galaxy 3
	//		//			return false;
	//
	//		new AlertDialog.Builder(context)
	//		.setTitle("Routing Dialog")
	//		.setMessage("Real or Test Server")
	//		.setPositiveButton("Real", new DialogInterface.OnClickListener() {
	//			public void onClick(DialogInterface dialog, int which) { 
	//				try {
	//					MobileServiceClient mClient = AhApplication.getInstance().getmClient();
	//					mClient = new MobileServiceClient(
	//							AhApplication.getInstance().APP_URL,
	//							AhApplication.getInstance().APP_KEY,
	//							app);
	//					app.setmClient(mClient);
	//				} catch (MalformedURLException e) {
	//					Log.d(AhGlobalVariable.LOG_TAG, "AhApplication onCreate : " + e.getMessage());
	//				}
	//				runChupa();
	//			}
	//		})
	//		.setNegativeButton("Test", new DialogInterface.OnClickListener() {
	//			public void onClick(DialogInterface dialog, int which) {
	//				runChupa();
	//			}
	//		})
	//		.setIcon(android.R.drawable.ic_dialog_alert)
	//		.show();
	//		return true;
	//	}


	private void runChupa() {
		AsyncChainer.asyncChain(thisFragment, new Chainable(){

			@Override
			public void doNext(final AhFragment frag) {
				if(pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY).equals(PreferenceHelper.DEFAULT_STRING)){
					int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
					if (errorCode != ConnectionResult.SUCCESS) {
						ExceptionManager.fireException(new AhException(frag, "getRegistrationIdAsync", AhException.TYPE.GCM_REGISTRATION_FAIL));
						return;
					}
					userHelper.getRegistrationIdAsync(frag, new AhEntityCallback<String>(){

						@Override
						public void onCompleted(String registrationId) {
							pref.putString(AhGlobalVariable.REGISTRATION_ID_KEY, registrationId);
						}
					});
				} else {
					AsyncChainer.notifyNext(frag);
				}
			}

		},new Chainable(){

			@Override
			public void doNext(AhFragment frag) {

				versionHelper.getServerAppVersionAsync(frag, new AhEntityCallback<AppVersion>() {

					@Override
					public void onCompleted(final AppVersion serverVer) {
						double clientVer;
						try {
							clientVer = versionHelper.getClientAppVersion();
						} catch (NameNotFoundException e) {
							clientVer = 0.11;
						}
						if (serverVer.getVersion() > clientVer) {
							String message = getResources().getString(R.string.update_app_message);
							AhAlertDialog updateDialog = new AhAlertDialog(null, message, true, new AhDialogCallback() {

								@Override
								public void doPositiveThing(Bundle bundle) {
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AhGlobalVariable.GOOGLE_STORE_APP_ID));
									startActivity(intent);
								}
								@Override
								public void doNegativeThing(Bundle bundle) {
									if (serverVer.getType().equals(AppVersion.TYPE.MANDATORY.toString())){
										activity.finish();
									} else {
										goToNextActivity();
									}
								}
							});
							updateDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
						}else{
							goToNextActivity();
						}
					}
				});
			}
		});
	}


	/*
	 * Move to next activity by user status
	 */
	public void goToNextActivity() {
		boolean isLoggedInUser = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY);
		boolean isLooggedInSquare = pref.getBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);

		Intent intent = new Intent();
		if (!isLoggedInUser){
			// New User
			intent.setClass(context, BasicProfileActivity.class);
		} else if(!isLooggedInSquare){
			// Already logged in
			intent.setClass(context, SquareListActivity.class);
		} else{
			// Has entered a square
			intent.setClass(context, SquareActivity.class);
		}
		startActivity(intent);
	}


	@Override
	public void handleException(AhException ex) {
		if (ex.getType().equals(AhException.TYPE.GCM_REGISTRATION_FAIL)) {
			String message = getResources().getString(R.string.google_play_services_message);
			String install = getResources().getString(R.string.install);
			new AhAlertDialog(null, message, install, null, true, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
					startActivity(intent);
					activity.finish();
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					activity.finish();
				}
			}).show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			return;
		}
		super.handleException(ex);
	}
}
