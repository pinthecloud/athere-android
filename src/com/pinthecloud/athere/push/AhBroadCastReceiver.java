package com.pinthecloud.athere.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;

public class AhBroadCastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(AhGlobalVariable.LOG_TAG, "AhBroadCastReceiver onReceive");

		String regId = intent.getExtras().getString("registration_id");
		if(regId != null && !regId.equals("")) {
			/*
			 * Save registration id
			 */
			AhApplication.getInstance().getPref().putString(AhGlobalVariable.REGISTRATION_ID_KEY, regId);
		} else{
			/*
			 * Get push and do intent service by push
			 */
			// Explicitly specify that GcmIntentService will handle the intent.
			ComponentName comp = new ComponentName(context.getPackageName(), AhIntentService.class.getName());

			// Start the service, keeping the device awake while it is launching.
			startWakefulService(context, (intent.setComponent(comp)));
			setResultCode(Activity.RESULT_OK);
		}
	}
}
