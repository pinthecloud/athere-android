package com.pinthecloud.athere;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
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

import com.google.android.gcm.GCMBaseIntentService;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhIdUser;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.FileUtil;

public class GCMIntentService extends GCMBaseIntentService {

	private AhApplication app;
	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;
	private UserDBHelper userDBHelper;
	private UserHelper userHelper;
	private PreferenceHelper pref;
	private Context _this;

	private AhMessage message = null;
	private String userId = null; 

	
//	public AhIntentService() {
//		this("AhIntentService");
//	}
//
//	
//	public AhIntentService(String name) {
//		super(name);
//		app = AhApplication.getInstance();
//		messageHelper = app.getMessageHelper();
//		messageDBHelper = app.getMessageDBHelper();
//		userDBHelper = app.getUserDBHelper();
//		userHelper = app.getUserHelper();
//		pref = app.getPref();
//		_this = this;
//	}
	
	public GCMIntentService(){
		this(AhGlobalVariable.GCM_SENDER_ID);
	}

	public GCMIntentService(String senderId){
		super(senderId);
		app = AhApplication.getInstance();
		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userDBHelper = app.getUserDBHelper();
		userHelper = app.getUserHelper();
		pref = app.getPref();
		_this = this;
	}
	

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.e("ERROR", "onError");
	}


	@Override
	protected void onMessage(Context arg0, Intent intent) {
		// TODO Auto-generated method stub
		Log.e("ERROR", "onMessage");
		_onHandleIntent(intent);
	}


	@Override
	protected void onRegistered(Context arg0, String registrationId) {
		// TODO Auto-generated method stub
		pref.putString(AhGlobalVariable.REGISTRATION_ID_KEY, registrationId);
		AhIdUser idUser = new AhIdUser();
	}


	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.e("ERROR", "onUnregistered");
		pref.removePref(AhGlobalVariable.REGISTRATION_ID_KEY);
	}
	

	
	public void _onHandleIntent(Intent intent) {
		/*
		 * Parsing the data from server
		 */
		String unRegisterd = intent.getStringExtra("unregistered");
		if (unRegisterd != null && unRegisterd.equals(AhGlobalVariable.GOOGLE_STORE_APP_ID))
			return;
		
		try {
			message = parseMessageIntent(intent);
			userId = parseUserIdIntent(intent);
		} catch (JSONException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "Error while parsing Message Intent : " + e.getMessage());
			return;
		}
		Log.e(AhGlobalVariable.LOG_TAG,"Received Message Type : " + message.getType());
		
		final AhMessage.TYPE type = AhMessage.TYPE.valueOf(message.getType());
		new AhThread(new Runnable() {

			@Override
			public void run() {
				if (AhMessage.TYPE.TALK.equals(type)) {
					TALK();
				} else if (AhMessage.TYPE.SHOUTING.equals(type)) {
					SHOUTING();
				} else if (AhMessage.TYPE.CHUPA.equals(type)) {
					CHUPA();
				} else if (AhMessage.TYPE.ENTER_SQUARE.equals(type)) {
					ENTER_SQUARE();
				} else if (AhMessage.TYPE.EXIT_SQUARE.equals(type)) {
					EXIT_SQUARE();
				} else if (AhMessage.TYPE.UPDATE_USER_INFO.equals(type)) {
					UPDATE_USER_INFO();
				} else if (AhMessage.TYPE.MESSAGE_READ.equals(type)) {
					MESSAGE_READ();
				} else if (AhMessage.TYPE.FORCED_LOGOUT.equals(type)) {
					FORCED_LOGOUT();
				} else if (AhMessage.TYPE.ADMIN_MESSAGE.equals(type)) {
					ADMIN_MESSAGE();
				}
			}
		}).start();
	}


	/**
	 *  Private Methods for Each Message TYPE (TALK, CHUPA, ENTER... etc)
	 * 
	 */
	private void TALK() {
		messageDBHelper.addMessage(message);
		if (isRunning(app)) {
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
		}
	}

	private void SHOUTING() {
		messageDBHelper.addMessage(message);
		if (isRunning(app)) {
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
		} else {
			alertNotification(AhMessage.TYPE.SHOUTING);
		}
	}

	private void CHUPA() {
		messageDBHelper.addMessage(message);
		messageDBHelper.increaseBadgeNum(message.getChupaCommunId());
		if (isRunning(app)) {
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
			if (!isChupaChatRunning(app)){
				alertNotification(AhMessage.TYPE.CHUPA);
			}
		} else {
			alertNotification(AhMessage.TYPE.CHUPA);
		}
	}

	private void ENTER_SQUARE() {
		userHelper.getUserAsync(null, userId, new AhEntityCallback<AhUser>() {

			@Override
			public void onCompleted(AhUser user) {

				userDBHelper.addUser(user);
				if (isRunning(app)) {
					String currentActivityName = getCurrentRunningActivityName(app);
					messageHelper.triggerMessageEvent(currentActivityName, message);
					userHelper.triggerUserEvent(user);
				} else {
					alertNotification(AhMessage.TYPE.ENTER_SQUARE);
				}
			}
		});
	}

	private void EXIT_SQUARE() {
		userDBHelper.exitUser(userId);
		AhUser user = userDBHelper.getUser(userId, true);
		if (isRunning(app)) {
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
			userHelper.triggerUserEvent(user);
		} 
	}

	private void UPDATE_USER_INFO() {
		userHelper.getUserAsync(null, userId, new AhEntityCallback<AhUser>() {

			@Override
			public void onCompleted(AhUser user) {
				userDBHelper.updateUser(user);
				if (isRunning(app)) {
					userHelper.triggerUserEvent(user);
				}
			}
		});
	}

	private void MESSAGE_READ() {
		throw new AhException("NOT IMPLEMENTED YET");
		//		messageDBHelper.updateMessage(message);
		//		if (isRunning(app)) {
		//			messageHelper.triggerMessageEvent(message);
		//		}
	}
	
	private void FORCED_LOGOUT() {
		AhApplication.getInstance().forcedLogoutAsync(null, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				
			}
		});
	}
	
	private void ADMIN_MESSAGE() {
		
	}


	/**
	 *  Method For alerting notification
	 */
	private void alertNotification(AhMessage.TYPE type){
		String title = "";
		String content = "";
		Class<?> clazz = SquareActivity.class;
		Resources resources = _this.getResources();
		if (AhMessage.TYPE.CHUPA.equals(type)){
			title = message.getSender() +" " + resources.getString(R.string.send_chupa_notification_title);
			content = message.getContent();
			clazz = ChupaChatActivity.class;
		} else if (AhMessage.TYPE.SHOUTING.equals(type)){
			title = message.getSender() + " " + resources.getString(R.string.shout_notification_title);
			content = message.getContent();
		} else if (AhMessage.TYPE.ENTER_SQUARE.equals(type)){
			title = message.getSender() + " " + resources.getString(R.string.enter_square_message);
			content = message.getContent();
			if(!pref.getBoolean(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY)){
				return;
			}
		} 

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(_this, clazz);

		/**
		 *  NEED TO BE FIXED!!
		 */
		if (AhMessage.TYPE.CHUPA.equals(type)){
			resultIntent.putExtra(AhGlobalVariable.USER_KEY, message.getSenderId());
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
		AhUser sentUser = userDBHelper.getUser(message.getSenderId());
		Bitmap bitmap = null;
		if (sentUser == null){
			Log.e("ERROR","no sentUser error");
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
		} else {
//			bitmap = BitmapUtil.convertToBitmap(sentUser.getProfilePic(), 0, 0);
			bitmap = FileUtil.getImageFromInternalStorage(app, sentUser.getProfilePic(), 0, 0);
		}


		/*
		 * Set Notification
		 */
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(_this)
		.setSmallIcon(R.drawable.launcher)
		.setLargeIcon(bitmap)
		.setContentTitle(title)
		.setContentText(content)
		.setAutoCancel(true);
		mBuilder.setContentIntent(resultPendingIntent);

		// Notify!
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());

		// For Vibration
		AudioManager audioManager = (AudioManager) _this.getSystemService(Context.AUDIO_SERVICE);
		if(AudioManager.RINGER_MODE_SILENT != audioManager.getRingerMode()){
			((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(800);
		}
	}


	/**
	 * 
	 * @param Application context
	 * @return true if the app is Running foreground
	 * 		   false if the app is turned OFF
	 */
	private boolean isRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks) {
			if (context.getPackageName().equalsIgnoreCase(task.topActivity.getPackageName())) 
				return true;                                  
		}
		return false;
	}
	
	private boolean isChupaChatRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks) {
			if (task.topActivity.getClassName().equals(ChupaChatActivity.class.getName())) {
				return true;
			}
		}
		return false;
	}
	
	private String getCurrentRunningActivityName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
		
		for (RunningTaskInfo task : tasks) {
			if (context.getPackageName().equalsIgnoreCase(task.topActivity.getPackageName())) {
				return task.topActivity.getClassName();
			}
		}
		return GCMIntentService.class.getName();
	}


	/**
	 * 
	 * @param intent given from the server
	 * @return AhMessage sent from the server
	 */
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
			.setStatus(AhMessage.STATUS.SENT)
			.setChupaCommunId(chupaCommunId);

		} catch (JSONException e) {
			e.printStackTrace();
			throw e;
		}

		return messageBuilder.build();
	}

	/**
	 * 
	 * @author hongkunyoo
	 * @param intent given for the server
	 * @return userId String related to the sent message
	 */
	private String parseUserIdIntent(Intent intent){
		return intent.getExtras().getString("userId");
	}
}
