package com.pinthecloud.athere;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pinthecloud.athere.model.AhMessage;

public class AhIntentService extends IntentService {
	
	public AhIntentService() {
		super("AhIntentService");
	}

	public AhIntentService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		AhMessage message = parseIntent(intent);
		
		if(isRunning(getBaseContext())){
		// if the app is running, add the message to the chat room.
			
			
		} else {
		// if the app is not running, send a notification
			
		}
		
	}
	
	private boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName())) 
                return true;                                  
        }

        return false;
    }
	
	private AhMessage parseIntent(Intent intent) {
		AhMessage message = new AhMessage();
		Bundle b = intent.getExtras();
		String jsonStr = b.getString("message");
		
		JSONObject jo = null;
		
		String type = "";
		String content = "";
		String sender = "";
		String senderId = "";
		String receiver = "";
		String receiverId = "";
		try {
			jo = new JSONObject(jsonStr);
		
			type = jo.getString("type");
			content = jo.getString("content");
			sender = jo.getString("sender");
			senderId = jo.getString("senderId");
			receiver = jo.getString("receiver");
			receiverId = jo.getString("receiverId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		message.setType(type);
		message.setContent(content);
		message.setSender(sender);
		message.setSenderId(senderId);
		message.setReceiver(receiver);
		message.setReceiverId(receiverId);
		
		return message;
	}
	
	private static boolean isRunning3(Context context) {


	    ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

	    List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

	    boolean isServiceFound = false;

	    for (int i = 0; i < services.size(); i++) {

	    	//Log.e("ERROR",services.get(i).service.getPackageName());
	    	Log.e("ERROR",services.get(i).service.getClassName());
	    }

	    return isServiceFound;
	}
	
	private void print(Context context){
		ActivityManager activityManager = (ActivityManager)context.getSystemService (Context.ACTIVITY_SERVICE); 
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE); 

		Log.e("ERROR","==================================");
		Log.e("ERROR","context.applicationName  : "+ context.getApplicationInfo().name);
		Log.e("ERROR","context.getPackageName() : "+ context.getPackageName());
		Log.e("ERROR","context.toString() : "+ context.toString());
		
		for (RunningTaskInfo task : tasks) {
			Log.e("ERROR","task : "+ task);
			Log.e("ERROR","task.baseActivity : "+ task.baseActivity);
			Log.e("ERROR","task.topActivity : "+ task.topActivity);
			Log.e("ERROR","task.describeContents() : "+ task.describeContents());
			Log.e("ERROR","task.description : "+ task.description);
			Log.e("ERROR","task.id : "+ task.id);
	    } 
	}
}
