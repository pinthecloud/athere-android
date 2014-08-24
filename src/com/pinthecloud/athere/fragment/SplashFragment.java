package com.pinthecloud.athere.fragment;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.BasicProfileActivity;
import com.pinthecloud.athere.activity.HongkunTestAcitivity;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.dialog.AhAlertDialog;
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
				AhApplication.getInstance().removeSquarePreference();
			} else if(currentDay > lastDay && lastHour < 12){
				AhApplication.getInstance().removeSquarePreference();
			} else if(currentDay > lastDay && currentHour >= 12){
				AhApplication.getInstance().removeSquarePreference();
			} else if(currentDay == lastDay && lastHour < 12 && currentHour >= 12){
				AhApplication.getInstance().removeSquarePreference();
			}
		}


		// Erase Later (Exception for hongkun)
		if (!isHongkunTest()) {
			// Start Chupa Application
			runChupa();
		}

		return view;
	}


	public boolean isHongkunTest() {
		String myGal2 = "Dalvik/1.6.0 (Linux; U; Android 4.0.4; SHW-M250K Build/IMM76D)";
		//		String note = "Dalvik/1.6.0 (Linux; U; Android 4.4.2; SHV-E250S Build/KOT49H)";
		String myGal3 = "Dalvik/1.6.0 (Linux; U; Android 4.3; SHW-M440S Build/JSS15J)";
		String httpAgent = System.getProperty("http.agent");
		if (!((myGal2.equals(httpAgent)			// hongkunyoo Galaxy 2 
				|| myGal3.equals(httpAgent))))	// Galaxy 3
			return false;

		new AlertDialog.Builder(context)
		.setTitle("Routing Dialog")
		.setMessage("Want to Go to HongkunTest?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				startActivity(new Intent(activity, HongkunTestAcitivity.class)); 
				activity.finish();
				return;
			}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				runChupa();
				return;
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
		return true;
	}


	public void runChupa() {
		AsyncChainer.asyncChain(_thisFragment, new Chainable(){

			@Override
			public void doNext(AhFragment frag) {
				if(pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY).equals(PreferenceHelper.DEFAULT_STRING)){
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
							Log.d(AhGlobalVariable.LOG_TAG, "Error of SplashActivity : " + e.getMessage());
							clientVer = 0.1;
						}
						if (serverVer.getVersion() > clientVer) {
							Resources resources = getResources();
							String title = resources.getString(R.string.update_app_title);
							String message = resources.getString(R.string.update_app_message);
							AhAlertDialog updateDialog = new AhAlertDialog(title, message, true, new AhDialogCallback() {

								@Override
								public void doPositiveThing(Bundle bundle) {
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:" + AhGlobalVariable.GOOGLE_STORE_APP_ID));
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
}
