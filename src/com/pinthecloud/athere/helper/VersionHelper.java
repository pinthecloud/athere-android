package com.pinthecloud.athere.helper;

import java.util.List;

import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AppVersion;
import com.pinthecloud.athere.util.AsyncChainer;

public class VersionHelper {

//	private static final String APP_VERSION_KEY = "APP_VERSION_KEY";
	private static final String GET_APP_VERSION = "get_app_version";

	private AhApplication app;
	private PreferenceHelper pref;
	private MobileServiceClient mClient;
	private Object lock;

	/**
	 * Model tables
	 */
	private MobileServiceTable<AppVersion> appVersionTable;


	public enum TYPE {
		MANDATORY,
		OPTIONAL
	}

	public VersionHelper() {
		app = AhApplication.getInstance();
		pref = app.getPref();
		mClient = app.getmClient();
		lock = app.getLock();
		appVersionTable = app.getAppVersionTable();
	}

	public AppVersion getServerAppVersionSync(final AhFragment frag) {
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return null;
		}

		final AhCarrier<AppVersion> carrier = new AhCarrier<AppVersion>();
		appVersionTable.select("").execute(new TableQueryCallback<AppVersion>() {

			@Override
			public void onCompleted(List<AppVersion> list, int count, Exception exception,
					ServiceFilterResponse response) {
				if(exception == null){
					if (list.size() == 1) {
						AppVersion appVersion = list.get(0);
						carrier.load(appVersion);
					} else {
						ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.SERVER_ERROR));
					}

					synchronized (lock) {
						lock.notify();
					}
				} else {
					carrier.load(null);
					ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();
	}

	public void getServerAppVersionAsync(final AhFragment frag, final AhEntityCallback<AppVersion> callback) {
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		appVersionTable.select("*").execute(new TableQueryCallback<AppVersion>() {

			@Override
			public void onCompleted(List<AppVersion> list, int count, Exception exception,
					ServiceFilterResponse response) {
				if(exception == null){
					if (list.size() == 1) {
						callback.onCompleted(list.get(0));
						AsyncChainer.notifyNext(frag);
					} else {
						ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.SERVER_ERROR));
					}
				} else {
					ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	public double getClientAppVersion() throws NameNotFoundException {
		String versionName = app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName;
		Log.d(AhGlobalVariable.LOG_TAG, versionName);
		return Double.parseDouble(versionName);
	}

}

// get Version from server;
// get Version from client;
// compare
// if (isOld)
//		show Dialog;
// else
//		keep do afterward thing;

//
//new AlertDialog.Builder(Test.this)
//.setIcon(R.drawable.icon)
//.setTitle("Update Available")
//.setMessage("An update for is available!\\n\\nOpen Android Market and see the details?")
//.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//        public void onClick(DialogInterface dialog, int whichButton) {
//                /* User clicked OK so do some stuff */
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:your.app.id"));
//                startActivity(intent);
//        }
//})
//.setNegativeButton("No", new DialogInterface.OnClickListener() {
//        public void onClick(DialogInterface dialog, int whichButton) {
//                /* User clicked Cancel */
//        }
//})
//.show();
//}