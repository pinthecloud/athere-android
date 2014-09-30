package com.pinthecloud.athere;

import java.net.MalformedURLException;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.pinthecloud.athere.analysis.FiveRocksHelper;
import com.pinthecloud.athere.analysis.GAHelper;
import com.pinthecloud.athere.analysis.UserHabitHelper;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.helper.VersionHelper;
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

	// Application
	private static AhApplication app;

	// Mobile Service instances
	private static MobileServiceClient mClient;

	// Helper
	private static UserHelper userHelper;
	private static SquareHelper squareHelper;
	private static MessageHelper messageHelper;
	private static VersionHelper versionHelper;
	private static CachedBlobStorageHelper blobStorageHelper;

	// Analysis
	private static GAHelper gaHelper;
	private static FiveRocksHelper fiveRocksHelper;
	private static UserHabitHelper userHabitHelper;

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
			// Do nothing
		}

		userDBHelper = new UserDBHelper(this);
		messageDBHelper = new MessageDBHelper(this);

		userHelper = new UserHelper();
		squareHelper = new SquareHelper();
		messageHelper = new MessageHelper();
		versionHelper = new VersionHelper();
		blobStorageHelper = new CachedBlobStorageHelper();

		gaHelper = new GAHelper();
		fiveRocksHelper = new FiveRocksHelper();
		userHabitHelper = new UserHabitHelper();
	}

	public static AhApplication getInstance(){
		return app;
	}
	public MobileServiceClient getmClient() {
		return mClient;
	}
	public void setmClient(MobileServiceClient client) {
		mClient = client;
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
	public UserHabitHelper getUserHabitHelper() {
		return userHabitHelper;
	}


	/*
	 * @return true, if the App is connected with Internet.
	 */
	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager)app.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}


	/*
	 * Remove preference about square user entered.
	 */
	public void removeMySquarePreference(AhFragment frag){
		// Remove other users and messages.
		userDBHelper.deleteAllUsers();
		messageDBHelper.deleteAllMessages();
		messageDBHelper.clearAllChupaBadgeNum();

		// Remove others' profile image.
		String userId = userHelper.getMyUserInfo().getId();
		FileUtil.clearAllFilesExceptSomeFiles(app, new String[]{ userId, userId+AhGlobalVariable.SMALL });
		blobStorageHelper.clearAllCache();

		// Remove my preference
		userHelper.removeMySquareUserInfo();
		squareHelper.removeMySquareInfo();
	}


	/*
	 * Remove preference about me.
	 */
	public void removeMyUserPreference(AhFragment frag){
		// Remove my profile image.
		String userId = userHelper.getMyUserInfo().getId();
		blobStorageHelper.deleteBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, userId, null);
		blobStorageHelper.deleteBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, userId+AhGlobalVariable.SMALL, null);
		FileUtil.clearAllFilesExceptSomeFiles(app, null);
		blobStorageHelper.clearAllCache();

		// Remove my preference
		userHelper.removeMyUserInfo();
	}



	/*
	 * Check nick name EditText
	 */
	public String checkNickName(String nickName){
		// Set regular expression for checking nick name
		String nickNameRegx = "^[a-zA-Z0-9가-힣_-]{2,10}$";
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


	//	public void forcedLogoutAsync (final AhFragment frag, final AhEntityCallback<Boolean> callback) {
	//		if (!isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "forcedLogoutAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return;
	//		}
	//
	//		JsonObject jo = new JsonObject();
	//		jo.addProperty("userId", pref.getString(AhGlobalVariable.USER_ID_KEY));
	//		jo.addProperty("ahIdUserKey", pref.getString(AhGlobalVariable.AH_ID_USER_KEY));
	//		jo.addProperty("isMale", pref.getBoolean(AhGlobalVariable.IS_MALE_KEY));
	//		jo.addProperty("squareId", pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
	//
	//		Gson g = new Gson();
	//		final JsonElement json = g.fromJson(jo, JsonElement.class);
	//
	//		String exitMessage = app.getResources().getString(R.string.exit_square_message);
	//		String nickName = pref.getString(AhGlobalVariable.NICK_NAME_KEY);
	//		AhMessage.Builder messageBuilder = new AhMessage.Builder();
	//		messageBuilder.setContent(nickName + " : " + exitMessage)
	//		.setSender(nickName)
	//		.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
	//		.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY))
	//		.setType(AhMessage.TYPE.EXIT_SQUARE);
	//		final AhMessage message = messageBuilder.build();
	//
	//		AsyncChainer.asyncChain(frag, new Chainable(){
	//
	//			@Override
	//			public void doNext(AhFragment frag) {
	//				messageHelper.sendMessageAsync(frag, message, new AhEntityCallback<AhMessage>() {
	//
	//					@Override
	//					public void onCompleted(AhMessage entity) {
	//
	//					}
	//				});
	//			}
	//
	//		}, new Chainable() {
	//
	//			@Override
	//			public void doNext(final AhFragment frag) {
	//				mClient.invokeApi(FORCED_LOGOUT, json, new ApiJsonOperationCallback() {
	//
	//					@Override
	//					public void onCompleted(JsonElement json, Exception exception,
	//							ServiceFilterResponse response) {
	//						removeSquarePreference(frag);
	//						callback.onCompleted(true);
	//					}
	//				});
	//			}
	//		});
	//	}
}
