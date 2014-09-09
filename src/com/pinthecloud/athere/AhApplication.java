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
import com.pinthecloud.athere.analysis.FiveRocksHelper;
import com.pinthecloud.athere.analysis.GAHelper;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.helper.VersionHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhIdUser;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.AppVersion;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.AsyncChainer.Chainable;
import com.pinthecloud.athere.util.FileUtil;


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
	private final String APP_TEST_URL = "https://atheresub.azure-mobile.net/";
	private final String APP_TEST_KEY = "MRKovlGEFQRPXGTVMFaZCBkeBwQSQA92";

	// Method Name
	private final String FORCED_LOGOUT = "forced_logout";

	// Application
	private static AhApplication app;
	private static PreferenceHelper pref;

	// Mobile Service instances
	private static MobileServiceClient mClient;
	private static MobileServiceTable<AhUser> userTable;
	private static MobileServiceTable<AhIdUser> idUserTable;
	private static MobileServiceTable<Square> squareTable;
	private static MobileServiceTable<AppVersion> appVersionTable;

	// Helper
	private static UserHelper userHelper;
	private static SquareHelper squareHelper;
	private static MessageHelper messageHelper;
	private static VersionHelper versionHelper;
	private static CachedBlobStorageHelper blobStorageHelper;
	private static GAHelper gaHelper;
	private static FiveRocksHelper fiveRocksHelper;

	// DB
	private static UserDBHelper userDBHelper;
	private static MessageDBHelper messageDBHelper;


	@Override
	public void onCreate() {
		super.onCreate();

		String AZURE_URL;
		String AZURE_KEY;
		if (AhGlobalVariable.DEBUG_MODE) {
			AZURE_URL = APP_TEST_URL;
			AZURE_KEY = APP_TEST_KEY;
		} else {
			AZURE_URL = APP_URL;
			AZURE_KEY = APP_KEY;
		}

		app = this;
		try {
			mClient = new MobileServiceClient(
					AZURE_URL,
					AZURE_KEY,
					this);
		} catch (MalformedURLException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "AhApplication onCreate : " + e.getMessage());
		}
		pref = new PreferenceHelper(this);

		userDBHelper = new UserDBHelper(this);
		messageDBHelper = new MessageDBHelper(this);

		userTable = mClient.getTable(AhUser.class);
		idUserTable = mClient.getTable(AhIdUser.class);
		squareTable = mClient.getTable(Square.class);
		appVersionTable = mClient.getTable(AppVersion.class);

		userHelper = new UserHelper();
		squareHelper = new SquareHelper();
		messageHelper = new MessageHelper();
		versionHelper = new VersionHelper();
		blobStorageHelper = new CachedBlobStorageHelper();
		gaHelper = new GAHelper();
		fiveRocksHelper = new FiveRocksHelper();
	}

	public static AhApplication getInstance(){
		return app;
	}
	public PreferenceHelper getPref() {
		return pref;
	}
	public MobileServiceClient getmClient() {
		return mClient;
	}
	public void setmClient(MobileServiceClient client) {
		mClient = client;
	}
	public MobileServiceTable<AhUser> getUserTable() {
		return userTable;
	}
	public MobileServiceTable<AhIdUser> getIdUserTable() {
		return idUserTable;
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
	public CachedBlobStorageHelper getBlobStorageHelper() {
		return blobStorageHelper;
	}
	public GAHelper getGAHelper() {
		return gaHelper;
	}
	public FiveRocksHelper getFiveRocksHelper() {
		return fiveRocksHelper;
	}
	

	/*
	 * @return true, if the App is connected with Internet.
	 */
	public boolean isOnline(){
		ConnectivityManager cm = 
				(ConnectivityManager)app.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}


	public void forcedLogoutAsync (final AhFragment frag, final AhEntityCallback<Boolean> callback) {
		if (!isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "forcedLogoutAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty("userId", pref.getString(AhGlobalVariable.USER_ID_KEY));
		jo.addProperty("ahIdUserKey", pref.getString(AhGlobalVariable.AH_ID_USER_KEY));
		jo.addProperty("isMale", pref.getBoolean(AhGlobalVariable.IS_MALE_KEY));
		jo.addProperty("squareId", pref.getString(AhGlobalVariable.SQUARE_ID_KEY));

		Gson g = new Gson();
		final JsonElement json = g.fromJson(jo, JsonElement.class);

		String exitMessage = app.getResources().getString(R.string.exit_square_message);
		String nickName = pref.getString(AhGlobalVariable.NICK_NAME_KEY);
		AhMessage.Builder messageBuilder = new AhMessage.Builder();
		messageBuilder.setContent(nickName + " : " + exitMessage)
		.setSender(nickName)
		.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
		.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY))
		.setType(AhMessage.TYPE.EXIT_SQUARE);
		final AhMessage message = messageBuilder.build();

		AsyncChainer.asyncChain(frag, new Chainable(){

			@Override
			public void doNext(AhFragment frag) {
				messageHelper.sendMessageAsync(frag, message, new AhEntityCallback<AhMessage>() {

					@Override
					public void onCompleted(AhMessage entity) {

					}
				});
			}

		}, new Chainable() {

			@Override
			public void doNext(final AhFragment frag) {
				mClient.invokeApi(FORCED_LOGOUT, json, new ApiJsonOperationCallback() {

					@Override
					public void onCompleted(JsonElement json, Exception exception,
							ServiceFilterResponse response) {
						removeSquarePreference(frag);
						callback.onCompleted(true);
					}
				});
			}
		});

	}

	public void removeSquarePreference(AhFragment frag){
		userDBHelper.deleteAllUsers();
		messageDBHelper.deleteAllMessages();
		messageDBHelper.cleareAllBadgeNum();
		FileUtil.clearAllFiles(app);
		blobStorageHelper.clearCache();
		blobStorageHelper.deleteBitmapAsync(frag, pref.getString(AhGlobalVariable.USER_ID_KEY), null);

		pref.removePref(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);
		pref.removePref(AhGlobalVariable.TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY);
		pref.removePref(AhGlobalVariable.IS_CHUPA_ENABLE_KEY);
		pref.removePref(AhGlobalVariable.IS_CHAT_ENABLE_KEY);
		pref.removePref(AhGlobalVariable.SQUARE_EXIT_TAB_KEY);
		pref.removePref(AhGlobalVariable.COMPANY_NUMBER_KEY);
		pref.removePref(AhGlobalVariable.USER_ID_KEY);
		pref.removePref(AhGlobalVariable.SQUARE_ID_KEY);
		pref.removePref(AhGlobalVariable.SQUARE_NAME_KEY);
	}
	
	
	/*
	 * Check nick name EditText
	 */
	public String checkNickName(String nickName){
		Log.d(AhGlobalVariable.LOG_TAG, "CheckNickNameEditText");

		// Set regular expression for checking nick name
		String nickNameRegx = "^[a-zA-Z0-9가-힣_-]{2,15}$";
		String message = "";

		/*
		 * Check logic whether this nick name is valid or not
		 * If user doesn't type in proper nick name,
		 * can't go to next activity
		 */
		// Check length of nick name
		if(nickName.length() < 2){
			message = getResources().getString(R.string.min_nick_name_message);
		} else if(!nickName.matches(nickNameRegx)){
			message = getResources().getString(R.string.bad_nick_name_message);
		} else if(nickName.length() > 10){
			message = getResources().getString(R.string.max_nick_name_message);
		}
		return message;
	}
}
