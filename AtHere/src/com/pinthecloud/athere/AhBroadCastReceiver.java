package com.pinthecloud.athere;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AhBroadCastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String regId = intent.getExtras().getString("registration_id");
		if(regId != null && !regId.equals("")) {
			// Save registration id
			AhApplication.getInstance().getUserHelper().setMyRegistrationId(regId);
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
