package com.pinthecloud.athere.helper;

import java.io.IOException;
import java.util.List;

import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.interfaces.AhPairEntityCallback;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.SquareUser;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.JsonConverter;

public class UserHelper {

	private final String USER_ID_KEY = "USER_ID_KEY";
	private final String AH_ID_KEY = "AH_ID_KEY";
	private final String MOBILE_ID_KEY = "MOBILE_ID_KEY";
	private final String REGISTRATION_ID_KEY = "REGISTRATION_ID_KEY";
	private final String NICK_NAME_KEY = "NICK_NAME_KEY";
	private final String IS_MALE_KEY = "IS_MALE_KEY";
	private final String BIRTH_YEAR_KEY = "BIRTH_YEAR_KEY";
	private final String IS_CHAT_ENABLE_KEY = "IS_CHAT_ENABLE_KEY";
	private final String IS_CHUPA_ENABLE_KEY = "IS_CHUPA_ENABLE_KEY";
	private final String IS_LOGGED_IN_USER_KEY = "IS_LOGGED_IN_USER_KEY";

	private final String ENTER_SQUARE = "enter_square";
	private final String EXIT_SQUARE = "exit_square";

	private AhApplication app;
	private PreferenceHelper pref;

	private MobileServiceTable<AhUser> userTable;
	private MobileServiceTable<SquareUser> squareUserTable;
	private MobileServiceClient mClient;


	public UserHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.pref = PreferenceHelper.getInstance();
		this.mClient = app.getmClient();
		this.userTable = mClient.getTable(AhUser.class);
		this.squareUserTable = mClient.getTable(SquareUser.class);
	}

	public boolean isLoggedInUser() {
		return pref.getBoolean(IS_LOGGED_IN_USER_KEY);
	}
	public UserHelper setLoggedInUser(boolean loggedIn) {
		pref.putBoolean(IS_LOGGED_IN_USER_KEY, loggedIn);
		return this;
	}
	public boolean isChatEnable() {
		return pref.getBoolean(IS_CHAT_ENABLE_KEY);
	}
	public UserHelper setChatEnable(boolean isChatEnable) {
		pref.putBoolean(IS_CHAT_ENABLE_KEY, isChatEnable);
		return this;
	}
	public UserHelper setMyAhId(String ahId) {
		pref.putString(AH_ID_KEY, ahId);
		return this;
	}
	public UserHelper setMyId(String id) {
		pref.putString(USER_ID_KEY, id);
		return this;
	}
	public boolean hasMobileId() {
		return !pref.getString(MOBILE_ID_KEY).equals(PreferenceHelper.DEFAULT_STRING);
	}
	public UserHelper setMyMobileId(String id) {
		pref.putString(MOBILE_ID_KEY, id);
		return this;
	}
	public boolean hasRegistrationId() {
		return !pref.getString(REGISTRATION_ID_KEY).equals(PreferenceHelper.DEFAULT_STRING);
	}
	public UserHelper setMyRegistrationId(String id) {
		pref.putString(REGISTRATION_ID_KEY, id);
		return this;
	}
	public UserHelper setMyMale(boolean isMale) {
		pref.putBoolean(IS_MALE_KEY, isMale);
		return this;
	}
	public UserHelper setMyBirthYear(int birthYear) {
		pref.putInt(BIRTH_YEAR_KEY, birthYear);
		return this;
	}
	public UserHelper setMyNickName(String nickName) {
		pref.putString(NICK_NAME_KEY, nickName);
		return this;
	}public UserHelper setMyChupaEnable(boolean isChupaEnable) {
		pref.putBoolean(IS_CHUPA_ENABLE_KEY, isChupaEnable);
		return this;
	}
	
	public void addUserAsync(final AhFragment frag, AhUser user, final AhEntityCallback<AhUser> callback) throws AhException {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "addUserAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		userTable.insert(user, new TableOperationCallback<AhUser>() {

			@Override
			public void onCompleted(AhUser entity, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity);
					AsyncChainer.notifyNext(frag);
				} else {
					if (response.getContent().contains(AhException.TYPE.DUPLICATED_NICK_NAME.toString())) { 
						ExceptionManager.fireException(new AhException(frag, "addUserAsync", AhException.TYPE.DUPLICATED_NICK_NAME));
					} else {
						ExceptionManager.fireException(new AhException(frag, "addUserAsync", AhException.TYPE.SERVER_ERROR));
					}
				}
			}
		});
	}

	public void addSquareUserAsync(final AhFragment frag, SquareUser user, final AhEntityCallback<SquareUser> callback) throws AhException {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "addSquareUserAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		squareUserTable.insert(user, new TableOperationCallback<SquareUser>() {

			@Override
			public void onCompleted(SquareUser entity, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					if (callback != null)
						callback.onCompleted(entity);
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "addSquareUserAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	//	public void addIdUserAsync(final AhFragment frag, AhIdUser user, final AhEntityCallback<AhIdUser> callback) {
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "addAhIdUser", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return;
	//		}
	//
	//		idUserTable.insert(user, new TableOperationCallback<AhIdUser>() {
	//
	//			@Override
	//			public void onCompleted(AhIdUser _user, Exception exception,
	//					ServiceFilterResponse response) {
	//				if (exception == null) {
	//					callback.onCompleted(_user);
	//					AsyncChainer.notifyNext(frag);
	//				} else {
	//					ExceptionManager.fireException(new AhException(frag, "addAhIdUser", AhException.TYPE.SERVER_ERROR));
	//				}
	//			}
	//		});
	//	}

	public void enterSquareAsync(final AhFragment frag, AhUser user, String squareId, boolean isPreview, final AhPairEntityCallback<String, List<AhUser>> callback) throws AhException {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "enterSquareAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		JsonObject json = user.toJson();
		json.addProperty("squareId", squareId);
		json.addProperty("isPreview", isPreview);

		mClient.invokeApi(ENTER_SQUARE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					String userId = JsonConverter.convertToUserId(_json);
					List<AhUser> list = JsonConverter.convertToUserList(_json);
					callback.onCompleted(userId, list);
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "enterSquareAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	//	public void deleteUserAsync(final AhFragment frag, String userId, final AhEntityCallback<Boolean> callback) throws AhException {
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "exitSquareAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return;
	//		}
	//
	//		userTable.delete(userId, new TableDeleteCallback() {
	//
	//			@Override
	//			public void onCompleted(Exception e, ServiceFilterResponse response) {
	//				if (e == null) {
	//					callback.onCompleted(true);
	//					AsyncChainer.notifyNext(frag);
	//				} else {
	//					ExceptionManager.fireException(new AhException(frag, "exitSquareAsync", AhException.TYPE.SERVER_ERROR));
	//				}
	//			}
	//		});
	//	}

	public void exitSquareAsync(final AhFragment frag, AhUser user, final AhEntityCallback<Boolean> callback) throws AhException {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "exitSquareAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		user.setRegistrationId("");
		user.setProfilePic("");
		JsonElement json = user.toJson();

		mClient.invokeApi(EXIT_SQUARE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(true);
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "exitSquareAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	public void getUserListAsync(final AhFragment frag, String squareId, final AhListCallback<AhUser> callback){
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getUserListAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		userTable.where().field("squareId").eq(squareId).execute(new TableQueryCallback<AhUser>() {

			@Override
			public void onCompleted(List<AhUser> result, int count, Exception exception,
					ServiceFilterResponse reponse) {
				if (exception == null) {
					callback.onCompleted(result, count);
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "getUserListAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	public void getUserAsync(final AhFragment frag, String id, final AhEntityCallback<AhUser> callback) {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getUserAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		userTable.where().field("id").eq(id).execute(new TableQueryCallback<AhUser>() {

			@Override
			public void onCompleted(List<AhUser> result, int count, Exception exception,
					ServiceFilterResponse reponse) {
				if (exception == null && result.size() == 1) {
					callback.onCompleted(result.get(0));
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "getUserAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	public void updateUserAsync(final AhFragment frag, AhUser user, final AhEntityCallback<AhUser> callback){

		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "updateUserAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		userTable.update(user, new TableOperationCallback<AhUser>() {

			@Override
			public void onCompleted(AhUser entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity);
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "updateUserAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	public void updateMyUserAsync(final AhFragment frag, AhEntityCallback<AhUser> callback){
		AhUser user = this.getMyUserInfo();
		this.updateUserAsync(frag, user, callback);
	}

	public AhUser getAdminUser(String id) {
		AhUser user = new AhUser();
		user.setId(id);
		user.setAhId(AhGlobalVariable.APP_NAME);
		user.setMobileId("");
		user.setRegistrationId("");
		user.setMale(true);
		user.setBirthYear(1900);
		user.setNickName(app.getResources().getString(R.string.admin));
		user.setChupaEnable(true);
		return user;
	}

	public AhUser getMyUserInfo() {
		String id = pref.getString(USER_ID_KEY);
		if(id.equals(PreferenceHelper.DEFAULT_STRING)){
			id = null;
		}
		String ahId = pref.getString(AH_ID_KEY);
		String mobileId = pref.getString(MOBILE_ID_KEY);
		String registrationId = pref.getString(REGISTRATION_ID_KEY);
		boolean isMale = pref.getBoolean(IS_MALE_KEY);
		int birthYear = pref.getInt(BIRTH_YEAR_KEY);
		String nickName = pref.getString(NICK_NAME_KEY);
		boolean isChupaEnable = pref.getBoolean(IS_CHUPA_ENABLE_KEY);

		AhUser user = new AhUser();
		user.setId(id);
		user.setAhId(ahId);
		user.setMobileId(mobileId);
		user.setRegistrationId(registrationId);
		user.setMale(isMale);
		user.setBirthYear(birthYear);
		user.setNickName(nickName);
		user.setChupaEnable(isChupaEnable);
		return user;
	}

	public void removeMySquareUserInfo() {
		pref.removePref(IS_CHAT_ENABLE_KEY);
		pref.removePref(IS_CHUPA_ENABLE_KEY);
	}

	public void removeMyUserInfo() {
		pref.removePref(AH_ID_KEY);
		pref.removePref(IS_MALE_KEY);
		pref.removePref(BIRTH_YEAR_KEY);
		pref.removePref(NICK_NAME_KEY);
		pref.removePref(IS_CHAT_ENABLE_KEY);
		pref.removePref(IS_CHUPA_ENABLE_KEY);
		pref.removePref(IS_LOGGED_IN_USER_KEY);
	}

	public void getRegistrationIdAsync(final AhFragment frag, final AhEntityCallback<String> callback) {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getRegistrationIdAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		(new AsyncTask<GoogleCloudMessaging, Void, String>() {

			@Override
			protected String doInBackground(GoogleCloudMessaging... params) {
				GoogleCloudMessaging gcm = params[0];
				try {
					return gcm.register(AhGlobalVariable.GCM_SENDER_ID);
				} catch (IOException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (result != null) {
					callback.onCompleted(result);
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "getRegistrationIdAsync", AhException.TYPE.GCM_REGISTRATION_FAIL));
				}
			}
		}).execute(GoogleCloudMessaging.getInstance(frag.getActivity()));
	}

	private AhEntityCallback<AhUser> _callback;
	public void setUserHandler(AhEntityCallback<AhUser> callback){
		_callback = callback;
	}
	public void triggerUserEvent(AhUser user){
		if(_callback != null){
			_callback.onCompleted(user);
		}
	}


	/*
	 * Sync Method
	 * NOT USING
	 */

	//	public boolean _exitSquareSync(final AhFragment frag, String userId) throws AhException {
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "exitSquareSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return false;
	//		}
	//
	//		final AhCarrier<Boolean> carrier = new AhCarrier<Boolean>();
	//
	//		userTable.delete(userId, new TableDeleteCallback() {
	//
	//			@Override
	//			public void onCompleted(Exception exception, ServiceFilterResponse arg1) {
	//				if (exception == null) {
	//					carrier.load(true);
	//					synchronized (lock) {
	//						lock.notify();
	//					}
	//				} else {
	//					ExceptionManager.fireException(new AhException(frag, "exitSquareSync", AhException.TYPE.SERVER_ERROR));
	//				}
	//			}
	//		});
	//
	//		synchronized (lock) {
	//			try {
	//				lock.wait();
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		return carrier.getItem();
	//	}
	//	
	//	public String _enterSquareSync(final AhFragment frag, AhUser user) throws AhException {
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "enterSquareSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return null;
	//		}
	//
	//		final AhCarrier<String> carrier = new AhCarrier<String>();
	//
	//		userTable.insert(user, new TableOperationCallback<AhUser>() {
	//
	//			@Override
	//			public void onCompleted(AhUser entity, Exception e, ServiceFilterResponse response) {
	//				if (e == null) {
	//					carrier.load(entity.getId());
	//					synchronized (lock) {
	//						lock.notify();
	//					}
	//				} else {
	//					ExceptionManager.fireException(new AhException(frag, "enterSquareSync", AhException.TYPE.SERVER_ERROR));
	//				}
	//			}
	//		});
	//
	//		synchronized (lock) {
	//			try {
	//				lock.wait();
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		return carrier.getItem();
	//	}
	//	
	//	
	//	public List<AhUser> _getUserListSync(final AhFragment frag, String squareId){
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "getUserListSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return null;
	//		}
	//
	//		final AhCarrier<List<AhUser>> carrier = new AhCarrier<List<AhUser>>();
	//
	//		userTable.where().field("squareId").eq(squareId).execute(new TableQueryCallback<AhUser>() {
	//
	//			@Override
	//			public void onCompleted(List<AhUser> result, int count, Exception exception,
	//					ServiceFilterResponse reponse) {
	//				if (exception == null) {
	//					carrier.load(result);
	//					synchronized (lock) {
	//						lock.notify();
	//					}
	//				} else {
	//					ExceptionManager.fireException(new AhException(frag, "getUserListSync", AhException.TYPE.SERVER_ERROR));
	//				}
	//			}
	//		});
	//
	//		synchronized (lock) {
	//			try {
	//				lock.wait();
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		return carrier.getItem();
	//	}
	//	
	//	public AhUser _getUserSync(final AhFragment frag, String id) {
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "getUserSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return null;
	//		}
	//
	//		if (id == null) if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "getUserSync", AhException.TYPE.NO_USER_ID));
	//			return null;
	//		}
	//		final AhCarrier<AhUser> carrier = new AhCarrier<AhUser>();
	//
	//		userTable.where().field("id").eq(id).execute(new TableQueryCallback<AhUser>() {
	//
	//			@Override
	//			public void onCompleted(List<AhUser> result, int count, Exception exception,
	//					ServiceFilterResponse reponse) {
	//				if (exception == null && result.size() == 1) {
	//					carrier.load(result.get(0));
	//					synchronized (lock) {
	//						lock.notify();
	//					}
	//				} else {
	//					ExceptionManager.fireException(new AhException(frag, "getUserSync", AhException.TYPE.SERVER_ERROR));
	//				}
	//			}
	//		});
	//
	//		synchronized (lock) {
	//			try {
	//				lock.wait();
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		return carrier.getItem();
	//	}
	//	
	//	public boolean _unRegisterGcmSync(final AhFragment frag) {
	//
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "UnRegistrationIdSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return false;
	//		}
	//		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(app);
	//
	//		try {
	//			gcm.unregister();
	//		} catch (IOException e) {
	//			ExceptionManager.fireException(new AhException(frag, "UnRegistrationIdSync", AhException.TYPE.GCM_REGISTRATION_FAIL));
	//			return false;
	//		}
	//		return true;
	//	}
}
