package com.pinthecloud.athere;

import java.net.MalformedURLException;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.pinthecloud.athere.helper.LocationHelper;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

/**
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

	// Application
	private static AhApplication app;
	private static PreferenceHelper pref;
	private static Object lock;

	// Mobile Service instances
	private static MobileServiceClient mClient;
	private static MobileServiceTable<User> userTable;
	private static MobileServiceTable<Square> squareTable;

	// Helper
	private static UserHelper userHelper;
	private static SquareHelper squareHelper;
	private static MessageHelper messageHelper; 

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

		userTable = mClient.getTable(User.class);
		squareTable = mClient.getTable(Square.class);

		userHelper = new UserHelper();
		squareHelper = new SquareHelper();
		messageHelper = new MessageHelper();

		userDBHelper = new UserDBHelper(this);
		messageDBHelper = new MessageDBHelper(this);
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

	/**
	 * @return true, if the App is connected with Internet.
	 */
	public boolean isOnline(){
		ConnectivityManager cm = 
				(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
}
