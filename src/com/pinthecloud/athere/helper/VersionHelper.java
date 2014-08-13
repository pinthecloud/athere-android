package com.pinthecloud.athere.helper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;

public class VersionHelper {
	
	private static final String APP_VERSION_KEY = "APP_VERSION_KEY";
	private static final String GET_APP_VERSION = "get_app_version";
	private PreferenceHelper pref;
	private MobileServiceClient mClient;
	private Object lock;
	
	public VersionHelper() {
		AhApplication app = AhApplication.getInstance();
		pref = app.getPref();
		mClient = app.getmClient();
		lock = app.getLock();
	}
	
	public double getServerAppVersionSync(final AhFragment frag) {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return 0;
		}

		final AhCarrier<Double> carrier = new AhCarrier<Double>();
		
		mClient.invokeApi(GET_APP_VERSION, new ApiJsonOperationCallback() {
			
			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				if(exception == null){
					JsonObject jo = json.getAsJsonObject();
					if (jo != null) {
						JsonElement je = jo.get("version");
						if (je != null) {
							double version = je.getAsDouble();
							carrier.load(version);
						} else {
							ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.PARSING_ERROR));
						}
					} else {
						ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.PARSING_ERROR));
					}
					
					synchronized (lock) {
						lock.notify();
					}
				} else {
					carrier.load(0.0);
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
	
public void getServerAppVersionAsync(final AhFragment frag, final AhEntityCallback<Double> callback) {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}
		
		mClient.invokeApi(GET_APP_VERSION, new ApiJsonOperationCallback() {
			
			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				if(exception == null){
					JsonObject jo = json.getAsJsonObject();
					if (jo != null) {
						JsonElement je = jo.get("version");
						if (je != null) {
							double version = je.getAsDouble();
							callback.onCompleted(version);
						} else {
							ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.PARSING_ERROR));
						}
					} else {
						ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.PARSING_ERROR));
					}
				} else {
					ExceptionManager.fireException(new AhException(frag, "getServerAppVersionSync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}
	
	public double getClientAppVersion() {
		return AhGlobalVariable.CLIENT_APP_VERSION;
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