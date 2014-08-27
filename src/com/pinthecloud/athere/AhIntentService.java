package com.pinthecloud.athere;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.FileUtil;

public class AhIntentService extends IntentService {

	private Context _this;
	private AhApplication app;
	private PreferenceHelper pref;

	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;
	private UserDBHelper userDBHelper;
	private UserHelper userHelper;

	private AhMessage message = null;
	private String userId = null; 
	
	public AhIntentService() {
		this("AhIntentService");
	}


	public AhIntentService(String name) {
		super(name);
		_this = this;
		app = AhApplication.getInstance();
		pref = app.getPref();

		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userDBHelper = app.getUserDBHelper();
		userHelper = app.getUserHelper();
	}


	public void onHandleIntent(Intent intent) {
		messageDBHelper.open();
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
				messageDBHelper.close();
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
		
		// Is the Chupa App Running
		if (isRunning(app)) {
			String currentActivityName = getCurrentRunningActivityName(app);
			AhUser currentChupaUser = app.getCurrentChupaUser();
			// Is the User in ChupaActivity
			if (isActivityRunning(app, ChupaChatActivity.class)){
				// Is the currentUser talking is the same user from the server.
				if (currentChupaUser != null && currentChupaUser.getId().equals(message.getSenderId())) {
					messageHelper.triggerMessageEvent(currentActivityName, message);
				// Or the server from the user is different from the current User talking
				} else {
					alertNotification(AhMessage.TYPE.CHUPA);
				}
			// Is the User is Not in ChupaActivity
			} else {
				messageHelper.triggerMessageEvent(currentActivityName, message);
				alertNotification(AhMessage.TYPE.CHUPA);
			}
		// if App Not Running
		} else {
			alertNotification(AhMessage.TYPE.CHUPA);
		}
	}

	private void ENTER_SQUARE() {
		messageDBHelper.addMessage(message);
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
		messageDBHelper.addMessage(message);
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
		AhApplication.getInstance().forcedLogoutAsync(null, new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {

				if (isRunning(app)){
					String currentActivityName = getCurrentRunningActivityName(app);
					messageHelper.triggerMessageEvent(currentActivityName, message);
				} else {
					alertNotification(AhMessage.TYPE.FORCED_LOGOUT);
				}
			}
		});
	}

	private void ADMIN_MESSAGE() {
		throw new AhException("NOT IMPLEMENTED YET");
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
			if(!pref.getBoolean(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY)){
				return;
			}
			title = message.getSender() + " " + resources.getString(R.string.enter_square_message);
			content = message.getContent();
		} else if (AhMessage.TYPE.FORCED_LOGOUT.equals(type)){
			title = resources.getString(R.string.forced_logout_title);
			content = message.getContent();
			clazz = SquareListActivity.class;
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
		if (AhMessage.TYPE.CHUPA.equals(type))
			stackBuilder.addParentStack(ChupaChatActivity.class);


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
			bitmap = FileUtil.getImageFromInternalStorage(app, sentUser.getId());
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


	private boolean isActivityRunning(Context context, Class<?> clazz) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks) {
			if (task.topActivity.getClassName().equals(clazz.getName())) {
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
		return AhIntentService.class.getName();
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
			String chupaCommunId = jo.getString("chupaCommunId");

			messageBuilder.setType(type)
			.setContent(content)
			.setSender(sender)
			.setSenderId(senderId)
			.setReceiver(receiver)
			.setReceiverId(receiverId)
			.setTimeStamp()
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
