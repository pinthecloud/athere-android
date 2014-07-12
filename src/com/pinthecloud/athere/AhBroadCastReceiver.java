package com.pinthecloud.athere;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AhBroadCastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Bundle extra = intent.getExtras();
		
		//Log.e("ERROR", "message : "+extra.getString("message"));
		
		 // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                AhIntentService.class.getName());
        
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
//		Intent newIntent = new Intent(context, AhIntentService.class);
//		
//		newIntent.putExtra("bundle", extra);
//
//
//		context.startService(newIntent);
		
		
		//context.startService(service);
		
	}

}
