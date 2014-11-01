package com.pinthecloud.athere.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.GuideActivity;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
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
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_splash, container, false);

		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);


		/*
		 * Get unique android id
		 */
		if (!userHelper.hasMobileId()) {
			String androidId = Secure.getString(app.getContentResolver(), Secure.ANDROID_ID);
			userHelper.setMyMobileId(androidId);
		}


		/*
		 * If time is up, remove local preferences.
		 */
		if(squareHelper.isLoggedInSquare()){
			Time time = new Time();
			time.setToNow();
			String currentTime = time.format("%Y:%m:%d:%H");
			String[] currentArray = currentTime.split(":");
			int currentYear = Integer.parseInt(currentArray[0]);
			int currentMonth = Integer.parseInt(currentArray[1]);
			int currentDay = Integer.parseInt(currentArray[2]);
			int currentHour = Integer.parseInt(currentArray[3]);

			String lastLoggedInSquareTime = squareHelper.getTimeStampAtLoggedInSquare();
			String[] lastArray = lastLoggedInSquareTime.split(":");
			int lastYear = Integer.parseInt(lastArray[0]);
			int lastMonth = Integer.parseInt(lastArray[1]);
			int lastDay = Integer.parseInt(lastArray[2]);
			int lastHour = Integer.parseInt(lastArray[3]);

			int resetTime = squareHelper.getMySquareInfo().getResetTime();
			if(currentYear > lastYear || currentMonth > lastMonth || currentDay > lastDay + 1){
				app.removeMySquarePreference(thisFragment);
			} else if(currentDay > lastDay && lastHour < resetTime){
				app.removeMySquarePreference(thisFragment);
			} else if(currentDay > lastDay && currentHour >= resetTime){
				app.removeMySquarePreference(thisFragment);
			} else if(currentDay == lastDay && lastHour < resetTime && currentHour >= resetTime){
				app.removeMySquarePreference(thisFragment);
			}
		}

		
		//		isHongkunTest();
		runChupa();
		return view;
	}


	//	private void hongkunTest() {
	//
	//		String myGal2 = "Dalvik/1.6.0 (Linux; U; Android 4.0.4; SHW-M250K Build/IMM76D)";
	//		String httpAgent = System.getProperty("http.agent");
	//		if (!myGal2.equals(httpAgent)){
	//			return;
	//		}
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
	//	}


	private void runChupa() {
		AsyncChainer.asyncChain(thisFragment, new Chainable(){

			@Override
			public void doNext(final AhFragment frag) {
				if(!userHelper.hasRegistrationId()){
					if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
						userHelper.getRegistrationIdAsync(frag, new AhEntityCallback<String>(){

							@Override
							public void onCompleted(String registrationId) {
								userHelper.setMyRegistrationId(registrationId);
							}
						});
					}else{
						ExceptionManager.fireException(new AhException(frag, "getRegistrationIdAsync", AhException.TYPE.GCM_REGISTRATION_FAIL));
					}
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
							AhApplication.getInstance().removeMySquarePreference(thisFragment);
							AhApplication.getInstance().removeMyUserPreference(thisFragment);
							
							String message = getResources().getString(R.string.update_app_message);
							AhAlertDialog updateDialog = new AhAlertDialog(null, message, true, new AhDialogCallback() {

								@Override
								public void doPositiveThing(Bundle bundle) {
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AhGlobalVariable.GOOGLE_PLAY_APP_ID));
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
		if(thisFragment.isAdded()){
			Intent intent = new Intent();
			if (!userHelper.isLoggedInUser()){
				// New User
				intent.setClass(context, GuideActivity.class);
			} else if(!squareHelper.isLoggedInSquare()){
				// Already logged in
				intent.setClass(context, SquareListActivity.class);
			} else{
				// Has entered a square
				intent.setClass(context, SquareActivity.class);
			}
			startActivity(intent);
		}
	}


	@Override
	public void handleException(AhException ex) {
		if (ex.getType().equals(AhException.TYPE.GCM_REGISTRATION_FAIL)) {
			String message = getResources().getString(R.string.google_play_services_message);
			new AhAlertDialog(null, message, true, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse("market://details?id=" + AhGlobalVariable.GOOGLE_PLAY_SERVICE_APP_ID));
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
