package com.pinthecloud.athere;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.pinthecloud.athere.activity.SplashActivity;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;

public class AhIntentService extends IntentService {
	
	AtomicInteger i = new AtomicInteger();
	
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
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.ic_launcher)
					//.setLargeIcon(ImageConverter.convertToImage(message.get));
			        .setContentTitle(message.getSender())
			        .setAutoCancel(true)
			        .setContentText(message.getContent() +" : "+i.get());
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(this, SplashActivity.class);

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(SplashActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(i.getAndIncrement(), mBuilder.build());
			Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(800);
			
			UserHelper userHelper = new UserHelper(this);
			
			List<User> userList = userHelper.getAllUsers();
			for(User user : userList){
				Log.e("ERROR",user.toString());
			}
		}
		
	}
	
	private boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.topActivity.getPackageName())) 
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
