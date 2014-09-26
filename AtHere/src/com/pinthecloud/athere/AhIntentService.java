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
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.fragment.ChupaChatFragment;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;
import com.pinthecloud.athere.util.JsonConverter;

public class AhIntentService extends IntentService {

	private Context _this;
	private AhApplication app;

	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;
	private UserDBHelper userDBHelper;
	private UserHelper userHelper;
	private CachedBlobStorageHelper blobStorageHelper;

	private AhMessage message = null;
	private AhUser user = null; 

	public AhIntentService() {
		this("AhIntentService");
	}


	public AhIntentService(String name) {
		super(name);
		_this = this;
		app = AhApplication.getInstance();

		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userDBHelper = app.getUserDBHelper();
		userHelper = app.getUserHelper();
		blobStorageHelper = app.getBlobStorageHelper();
	}


	public void onHandleIntent(Intent intent) {
		/*
		 * Parsing the data from server
		 */
		String unRegisterd = intent.getStringExtra("unregistered");
		if (unRegisterd != null && unRegisterd.equals(AhGlobalVariable.GOOGLE_PLAY_APP_ID)) return;

		try {
			String messageStr = intent.getExtras().getString("message");
			message = parseMessageString(messageStr);
			user = parseUserString(messageStr);
		} catch (JSONException e) {
			return;
		}

		final AhMessage.TYPE type = AhMessage.TYPE.valueOf(message.getType());
		Log.e("ERROR", type.toString());
		new AhThread(new Runnable() {

			public void run() {
				if (AhMessage.TYPE.TALK.equals(type)) {
					TALK();
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
		int id = messageDBHelper.addMessage(message);
		message.setId(String.valueOf(id));

		boolean isChatEnable = userHelper.getMyUserInfo().isChatEnable();
		
		if (isRunning(app)) {
			// Is the Chupa App Running
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
			if (!isActivityRunning(app, SquareActivity.class) && isChatEnable){
				// Is the User is Not in SquareActivity
				alertNotification(AhMessage.TYPE.TALK);
			}
		} else if(isChatEnable){
			// if App Not Running
			alertNotification(AhMessage.TYPE.TALK);
		}
	}


	private void CHUPA() {
		int id = messageDBHelper.addMessage(message);
		message.setId(String.valueOf(id));
		messageDBHelper.increaseChupaBadgeNum(message.getChupaCommunId());

		if (isRunning(app)) {
			// Is the Chupa App Running
			String currentActivityName = getCurrentRunningActivityName(app);
			if (isActivityRunning(app, ChupaChatActivity.class)){
				// Is the User in ChupaActivity
				AhUser currentChupaUser = ChupaChatFragment.otherUser;
				if (currentChupaUser != null && currentChupaUser.getId().equals(message.getSenderId())) {
					// Is the currentUser talking is the same user from the server.
					messageHelper.triggerMessageEvent(currentActivityName, message);
				} else {
					// Or the server from the user is different from the current User talking
					alertNotification(AhMessage.TYPE.CHUPA);
				}
			} else {
				// Is the User is Not in ChupaActivity
				messageHelper.triggerMessageEvent(currentActivityName, message);
				alertNotification(AhMessage.TYPE.CHUPA);
			}
		} else {
			// if App Not Running
			alertNotification(AhMessage.TYPE.CHUPA);
		}
	}


	private void ENTER_SQUARE() {
		int id = messageDBHelper.addMessage(message);
		message.setId(String.valueOf(id));
		userDBHelper.addIfNotExistOrUpdate(user);

		if (isRunning(app)) {
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
			userHelper.triggerUserEvent(user);
		} else if(userHelper.getMyUserInfo().isChatEnable()){
			alertNotification(AhMessage.TYPE.ENTER_SQUARE);
		}
	}


	private void EXIT_SQUARE() {
		userDBHelper.exitUser(user.getId());
		AhUser _user = userDBHelper.getUser(user.getId(), true);
		if (isRunning(app)) {
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
			userHelper.triggerUserEvent(_user);
		}
	}


	private void UPDATE_USER_INFO() {
		userDBHelper.updateUser(user);
		FileUtil.clearFile(app, message.getSenderId());
		FileUtil.clearFile(app, message.getSenderId()+AhGlobalVariable.SMALL);
		blobStorageHelper.clearCache(message.getSenderId());
		blobStorageHelper.clearCache(message.getSenderId()+AhGlobalVariable.SMALL);

		if (isRunning(app)) {
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
			userHelper.triggerUserEvent(user);
		}
	}


	private void MESSAGE_READ() {
		throw new AhException("NOT IMPLEMENTED YET");
	}


	private void FORCED_LOGOUT() {
//		AhApplication.getInstance().forcedLogoutAsync(null, new AhEntityCallback<Boolean>() {
//
//			@Override
//			public void onCompleted(Boolean entity) {
//				if (isRunning(app)){
//					String currentActivityName = getCurrentRunningActivityName(app);
//					messageHelper.triggerMessageEvent(currentActivityName, message);
//				} else {
//					alertNotification(AhMessage.TYPE.FORCED_LOGOUT);
//				}
//			}
//		});
		app.removeSquarePreference(null);
		if (isRunning(app)){
			String currentActivityName = getCurrentRunningActivityName(app);
			messageHelper.triggerMessageEvent(currentActivityName, message);
		} else {
			alertNotification(AhMessage.TYPE.FORCED_LOGOUT);
		}
	}


	private void ADMIN_MESSAGE() {
		TALK();
	}


	/**
	 *  Method For alerting notification
	 */
	private void alertNotification(AhMessage.TYPE type){
		/*
		 * Creates an explicit intent for an Activity in your app
		 */
		Intent resultIntent = new Intent();
		String title = "";
		String content = "";
		Resources resources = _this.getResources();
		if (AhMessage.TYPE.TALK.equals(type)){
			Log.d("Seungmin", "asdf");
			title = message.getSender();
			content = message.getContent();
			resultIntent.setClass(_this, SquareActivity.class);
		} else if (AhMessage.TYPE.ENTER_SQUARE.equals(type)){
			title = message.getContent();
			String age = resources.getString(R.string.age);
			String person = resources.getString(R.string.person);
			String gender = resources.getString(R.string.male);
			if(!user.isMale()){
				gender = resources.getString(R.string.female);
			}
			content = gender + " " + user.getAge() + age + " " + user.getCompanyNum() + person;
			resultIntent.setClass(_this, SquareActivity.class);
		} else if (AhMessage.TYPE.CHUPA.equals(type)){
			title = message.getSender() +" " + resources.getString(R.string.send_chupa_notification_title);
			content = message.getContent();
			resultIntent.setClass(_this, ChupaChatActivity.class);
			resultIntent.putExtra(AhGlobalVariable.USER_KEY, message.getSenderId());
		} else if (AhMessage.TYPE.FORCED_LOGOUT.equals(type)){
			title = resources.getString(R.string.forced_logout_title);
			content = message.getContent();
			resultIntent.setClass(_this, SquareListActivity.class);
		}


		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(_this);

		// Adds the back stack for the Intent (but not the Intent itself)
		if (AhMessage.TYPE.CHUPA.equals(type)){
			stackBuilder.addParentStack(ChupaChatActivity.class);
		}

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);

		// Set intent and bitmap
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
		Bitmap bitmap = null;
		if(user != null){
			bitmap = FileUtil.getBitmapFromInternalStorage(app, user.getId()+AhGlobalVariable.SMALL);
		}else{
			bitmap = BitmapUtil.decodeInSampleSize(getResources(), R.drawable.launcher, BitmapUtil.SMALL_PIC_SIZE, BitmapUtil.SMALL_PIC_SIZE);
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
	private AhMessage parseMessageString(String message) throws JSONException {
		AhMessage.Builder messageBuilder = new AhMessage.Builder();
		//		Bundle b = intent.getExtras();
		JSONObject messageObj = new JSONObject(message);
		String jsonStr = messageObj.getString("message");
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
	private AhUser parseUserString(String message) throws JSONException {
		JSONObject messageObj = new JSONObject(message);
		String jsonStr = messageObj.getString("user");
		if (jsonStr == null || jsonStr.equals("null")) return null;

		AhUser _user = null;
		JsonObject jo = new JsonParser().parse(jsonStr).getAsJsonObject();
		
		_user = JsonConverter.convertToUser(jo);
		return _user;
	}
}
