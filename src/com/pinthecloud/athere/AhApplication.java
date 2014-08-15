package com.pinthecloud.athere;

import java.net.MalformedURLException;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.helper.VersionHelper;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AppVersion;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

/*
 * 
 * Base Application class
 * This class is made for the instances that are needed globally.
 * The instances will be initialized at here and can be referenced in AhActivity and AhFragment
 *
 */
public class AhApplication extends Application{

	// Windows Azure Mobile Service Keys
	private final String APP_URL = "https://athere.azure-mobile.net/";
	private final String APP_KEY = "AyHtUuHXEwDSTuuLvvSYZtVSQZxtnT17";
	
	private static final String FORCED_LOGOUT = "forced_logout";

	// Application
	private static AhApplication app;
	private static PreferenceHelper pref;
	private static Object lock;

	// Mobile Service instances
	private static MobileServiceClient mClient;
	private static MobileServiceTable<User> userTable;
	private static MobileServiceTable<Square> squareTable;
	private MobileServiceTable<AppVersion> appVersionTable;

	// Helper
	private static UserHelper userHelper;
	private static SquareHelper squareHelper;
	private static MessageHelper messageHelper;
	private static VersionHelper versionHelper;

	// DB
	private static UserDBHelper userDBHelper;
	private static MessageDBHelper messageDBHelper;


	@Override
	public void onCreate() {
		super.onCreate();
		app = this;

		try {
			mClient = new MobileServiceClient(
					APP_URL,
					APP_KEY,
					this);
		} catch (MalformedURLException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "AhApplication onCreate : " + e.getMessage());
		}
		pref = new PreferenceHelper(this);
		lock = new Object();

		userDBHelper = new UserDBHelper(this);
		messageDBHelper = new MessageDBHelper(this);

		userTable = mClient.getTable(User.class);
		squareTable = mClient.getTable(Square.class);
		appVersionTable = mClient.getTable(AppVersion.class);

		userHelper = new UserHelper();
		squareHelper = new SquareHelper();
		messageHelper = new MessageHelper();
		versionHelper = new VersionHelper();

	}

	public static AhApplication getInstance(){
		return app;
	}
	public PreferenceHelper getPref() {
		return pref;
	}
	public Object getLock() {
		return lock;
	}
	public MobileServiceClient getmClient() {
		return mClient;
	}
	public MobileServiceTable<User> getUserTable() {
		return userTable;
	}
	public MobileServiceTable<Square> getSquareTable() {
		return squareTable;
	}
	public MobileServiceTable<AppVersion> getAppVersionTable() {
		return appVersionTable;
	}
	public UserDBHelper getUserDBHelper() {
		return userDBHelper;
	}
	public MessageDBHelper getMessageDBHelper() {
		return messageDBHelper;
	}
	public UserHelper getUserHelper() {
		return userHelper;
	}
	public SquareHelper getSquareHelper() {
		return squareHelper;
	}
	public MessageHelper getMessageHelper() {
		return messageHelper;
	}
	public VersionHelper getVersionHelper() {
		return versionHelper;
	}

	/*
	 * @return true, if the App is connected with Internet.
	 */
	public static boolean isOnline(){
		ConnectivityManager cm = 
				(ConnectivityManager)app.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
	
	
	// TODO : Don't Use NOW
	public static void forcedLogoutSync (final AhFragment frag) {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "sendMessageSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}
		
		JsonObject jo = new JsonObject();
		jo.addProperty("userId", pref.getString(AhGlobalVariable.USER_ID_KEY));
		jo.addProperty("registrationId", pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY));

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);
		
		String exitMessage = app.getResources().getString(R.string.exit_square_message);
		String nickName = pref.getString(AhGlobalVariable.NICK_NAME_KEY);
		AhMessage.Builder messageBuilder = new AhMessage.Builder();
		messageBuilder.setContent(nickName + " : " + exitMessage)
		.setSender(nickName)
		.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
		.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY))
		.setType(AhMessage.TYPE.EXIT_SQUARE);
		final AhMessage message = messageBuilder.build();
		
		
		mClient.invokeApi(FORCED_LOGOUT, json, new ApiJsonOperationCallback() {
			
			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				
				messageHelper.sendMessageSync(frag, message);
				
				pref.removePref(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);
				pref.removePref(AhGlobalVariable.USER_ID_KEY);
				pref.removePref(AhGlobalVariable.REGISTRATION_ID_KEY);
				pref.removePref(AhGlobalVariable.COMPANY_NUMBER_KEY);
				pref.removePref(AhGlobalVariable.SQUARE_ID_KEY);
				pref.removePref(AhGlobalVariable.SQUARE_NAME_KEY);
				pref.removePref(AhGlobalVariable.IS_CHUPA_ENABLE_KEY);
				pref.removePref(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY);
			}
		});
	}
}
