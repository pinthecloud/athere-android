package com.pinthecloud.athere;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;
import com.pinthecloud.athere.util.BitmapUtil;

public class AhIntentService extends IntentService {

	private AhApplication app;
	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;
	private UserDBHelper userDBHelper;
	private UserHelper userHelper;
	private PreferenceHelper pref;
	private Context _this;


	public AhIntentService() {
		this("AhIntentService");
	}

	public AhIntentService(String name) {
		super(name);
		app = AhApplication.getInstance();
		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userDBHelper = app.getUserDBHelper();
		userHelper = app.getUserHelper();
		pref = app.getPref();
		_this = this;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		/*
		 * Parsing the data from server
		 */
		AhMessage _message = null;
		String _userId = null; 
		try {
			_message = parseMessageIntent(intent);
			_userId = parseUserIdIntent(intent);
		} catch (JSONException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "Error while parsing Message Intent : " + e.getMessage());
			return;
		}
		Log.d(AhGlobalVariable.LOG_TAG,"Received Message Type : " + _message.getType());
		final AhMessage message = _message;
		final String userId = _userId;


		/*
		 * Process by message type
		 */
		new Thread(new Runnable(){
			public void run(){
				User user = null;
				if (AhMessage.TYPE.TALK.toString().equals(message.getType())) {
					messageDBHelper.addMessage(message);
				} else if (AhMessage.TYPE.SHOUTING.toString().equals(message.getType())) {
					// Do noghing
				} else if (AhMessage.TYPE.CHUPA.toString().equals(message.getType())) {
					messageDBHelper.addMessage(message);
					messageDBHelper.increaseBadgeNum(message.getChupaCommunId());
				} else if (AhMessage.TYPE.ENTER_SQUARE.toString().equals(message.getType())) {
					user = userHelper.getUserSync(null, userId);
					userDBHelper.addUser(user);
				} else if (AhMessage.TYPE.EXIT_SQUARE.toString().equals(message.getType())) {
					//userDBHelper.deleteUser(userId);
					//messageDBHelper.addMessage(message);
					userDBHelper.exitUser(userId);
				} else if (AhMessage.TYPE.UPDATE_USER_INFO.toString().equals(message.getType())) {
					user = userHelper.getUserSync(null, userId);
					userDBHelper.updateUser(user);
				}


				/*
				 * if the App is running
				 */
				if (isRunning(app)) {
					messageHelper.triggerMessageEvent(message);
					userHelper.triggerUserEvent(user);
					return;
				}


				/*
				 * if the Application is NOT Running
				 */
				if (AhMessage.TYPE.TALK.toString().equals(message.getType())){
					return; // do nothing
				} 

				String title = "";
				String content = "";
				Class<?> clazz = SquareActivity.class;
				Resources resources = _this.getResources();
				if (AhMessage.TYPE.CHUPA.toString().equals(message.getType())){
					title = message.getSender() +" " + resources.getString(R.string.send_chupa_notification_title);
					content = message.getContent();
					clazz = ChupaChatActivity.class;
				} else if (AhMessage.TYPE.SHOUTING.toString().equals(message.getType())){
					messageDBHelper.addMessage(message);
					title = message.getSender() + " " + resources.getString(R.string.shout_notification_title);
					content = message.getContent();
				} else if (AhMessage.TYPE.ENTER_SQUARE.toString().equals(message.getType())){
					messageDBHelper.addMessage(message);
					title = message.getSender() + " " + resources.getString(R.string.enter_square_message);
					content = message.getContent();
					if(!pref.getBoolean(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY)){
						return;
					}
				} else if (AhMessage.TYPE.EXIT_SQUARE.toString().equals(message.getType())){
					return;
				} 


				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(_this, clazz);
				if (AhMessage.TYPE.CHUPA.toString().equals(message.getType())){
					User chupaUser = userDBHelper.getUser(message.getSenderId());
					resultIntent.putExtra(AhGlobalVariable.USER_KEY, chupaUser);
					resultIntent.putExtra("gotoChupa", true);
				}

				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(_this);

				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(ChupaChatActivity.class);

				//				stackBuilder.addNextIntent(new Intent(_this, SquareActivity.class));

				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				//				stackBuilder.addNextIntentWithParentStack(resultIntent);

				PendingIntent resultPendingIntent =
						stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
				User sentUser = userDBHelper.getUser(message.getSenderId());
				Bitmap bm = null;
				if (sentUser == null){
					Log.e("ERROR","no sentUser error");
					bm = BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
				} else {
					bm = BitmapUtil.convertToBitmap(sentUser.getProfilePic());
				}


				/*
				 * Set Notification
				 */
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(_this)
				.setSmallIcon(R.drawable.launcher)
				.setLargeIcon(bm)
				.setContentTitle(title)
				.setContentText(content)
				.setAutoCancel(true);
				mBuilder.setContentIntent(resultPendingIntent);

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				// mId allows you to update the notification later on.
				mNotificationManager.notify(1, mBuilder.build());
				AudioManager audioManager = (AudioManager) _this.getSystemService(Context.AUDIO_SERVICE);
				if(AudioManager.RINGER_MODE_SILENT != audioManager.getRingerMode()){
					((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(800);
				}
			}
		}).start();
	}


	private boolean isRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks) {
			if (context.getPackageName().equalsIgnoreCase(task.topActivity.getPackageName())) 
				return true;                                  
		}
		return false;
	}


	private AhMessage parseMessageIntent(Intent intent) throws JSONException {
		AhMessage.Builder messageBuilder = new AhMessage.Builder();
		Bundle b = intent.getExtras();
		String jsonStr = b.getString("message");

		JSONObject jo = null;

		try {
			jo = new JSONObject(jsonStr);

			String type = jo.getString("type");
			String content = jo.getString("content");
			String sender = jo.getString("sender");
			String senderId = jo.getString("senderId");
			String receiver = jo.getString("receiver");
			String receiverId = jo.getString("receiverId");
			String timeStamp = jo.getString("timeStamp");
			String chupaCommunId = jo.getString("chupaCommunId");

			messageBuilder.setType(type)
			.setContent(content)
			.setSender(sender)
			.setSenderId(senderId)
			.setReceiver(receiver)
			.setReceiverId(receiverId)
			.setTimeStamp(timeStamp)
			.setChupaCommunId(chupaCommunId);

		} catch (JSONException e) {
			e.printStackTrace();
			throw e;
		}

		return messageBuilder.build();
	}


	private String parseUserIdIntent(Intent intent){
		return intent.getExtras().getString("userId");
	}


	//	private User parseUserIntent(Intent intent) throws JSONException {
	//		User user = new User();
	//		Bundle b = intent.getExtras();
	//		String jsonStr = b.getString("userData");
	//		Log.e("ERROR", "jsonStr : "+jsonStr);
	//		if (jsonStr == null) return null;
	//
	//		JSONObject jo = null;
	//
	//		try {
	//			jo = new JSONObject(jsonStr);
	//
	//			String id = jo.getString("id");
	//			String nickName = jo.getString("nickName");
	//			String profilePic = jo.getString("profilePic");
	//			String mobileId = jo.getString("mobileId");
	//			String registrationId = jo.getString("registrationId");
	//			boolean isMale = jo.getBoolean("isMale");
	//			int companyNum = jo.getInt("companyNum");
	//			int age = jo.getInt("age");
	//			String squareId = jo.getString("squareId");
	//
	//			user.setId(id);
	//			user.setNickName(nickName);
	//			user.setProfilePic(profilePic);
	//			user.setMobileId(mobileId);
	//			user.setRegistrationId(registrationId);
	//			user.setMale(isMale);
	//			user.setCompanyNum(companyNum);
	//			user.setAge(age);
	//			user.setSquareId(squareId);
	//		} catch (JSONException e) {
	//			e.printStackTrace();
	//			throw e;
	//		}
	//
	//		return user;
	//	}
	//
	//	private static boolean isRunning3(Context context) {
	//		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	//
	//		List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
	//
	//		boolean isServiceFound = false;
	//
	//		for (int i = 0; i < services.size(); i++) {
	//
	//			//Log.e("ERROR",services.get(i).service.getPackageName());
	//			Log.e("ERROR",services.get(i).service.getClassName());
	//		}
	//
	//		return isServiceFound;
	//	}
	//
	//	private void print(Context context){
	//		ActivityManager activityManager = (ActivityManager)context.getSystemService (Context.ACTIVITY_SERVICE); 
	//		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE); 
	//
	//		Log.e("ERROR","==================================");
	//		Log.e("ERROR","context.applicationName  : "+ context.getApplicationInfo().name);
	//		Log.e("ERROR","context.getPackageName() : "+ context.getPackageName());
	//		Log.e("ERROR","context.toString() : "+ context.toString());
	//
	//		for (RunningTaskInfo task : tasks) {
	//			Log.e("ERROR","task : "+ task);
	//			Log.e("ERROR","task.baseActivity : "+ task.baseActivity);
	//			Log.e("ERROR","task.topActivity : "+ task.topActivity);
	//			Log.e("ERROR","task.describeContents() : "+ task.describeContents());
	//			Log.e("ERROR","task.description : "+ task.description);
	//			Log.e("ERROR","task.id : "+ task.id);
	//		} 
	//	}
}