package com.pinthecloud.athere.helper;

import java.net.MalformedURLException;
import java.util.List;

import android.content.Context;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.pinthecloud.athere.AhException;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.UserDBHelper;
import com.pinthecloud.athere.util.JsonConverter;

public class ServiceClient {

	/**
	 * Mobile Service Connection Information
	 */
	private MobileServiceClient mClient;
	private String APP_URL = "https://athere.azure-mobile.net/";
	private String APP_KEY = "AyHtUuHXEwDSTuuLvvSYZtVSQZxtnT17";

	/*
	 * Methods name
	 */
	private final String ENTER_SQUARE = "enter_square";
	private final String GET_NEAR_SQUARE = "get_near_square";
	private final String SEND_MESSAGE = "send_message";

	/*
	 * String
	 */
	private final String currentLatitude = "currentLatitude";
	private final String currentLongitude = "currentLongitude";
	private final String getSquareList = "getSquareList";
	private final String getSquareListSync = "getSquareListSync";
	private final String getSquareListAsync = "getSquareListAsync";

	/**
	 * Activity UI variables
	 */
	private Context context;
	private PreferenceHelper pref;
	
	/**
	 * Model tables
	 */
	private MobileServiceTable<User> userTable;
	private MobileServiceTable<Square> squareTable;
	private Object lock = new Object();
	
	
	public ServiceClient(Context context) throws MalformedURLException{
		this.context = context;
		try {
			this.mClient = new MobileServiceClient(
					APP_URL,
					APP_KEY,
					this.context);
		} catch (MalformedURLException e) {
			throw e;
		}

		this.userTable = mClient.getTable(User.class);
		this.squareTable = mClient.getTable(Square.class);

		this.pref = new PreferenceHelper(context);
	}


	public List<Square> getSquareListSync(Location loc) throws AhException {
		return this.getSquareListSync(loc.getLatitude(), loc.getLongitude());
	}


	public List<Square> getSquareListSync(double latitude, double longitude) throws AhException {
		final AhCarrier<List<Square>> carrier = new AhCarrier<List<Square>>();
		JsonObject jo = new JsonObject();
		jo.addProperty(currentLatitude, latitude);
		jo.addProperty(currentLongitude, longitude);

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(GET_NEAR_SQUARE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if ( exception == null) {
					List<Square> list = JsonConverter.convertToSquareList(json.getAsJsonArray());
					if (list == null) throw new AhException(exception, getSquareList);
					carrier.load(list);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					throw new AhException(exception, getSquareListSync);
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();
	}


	public Square createSquareSync(String name, double latitude, double longitude) throws AhException {
		String whoMade = pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY);

		if (whoMade == PreferenceHelper.DEFAULT_STRING) {
			throw new AhException("createSquare NO Registration");
		}
		Square square = new Square();

		square.setName(name);
		square.setLatitude(latitude);
		square.setLongitude(longitude);
		square.setWhoMade(whoMade);

		return this.createSquareSync(square);
	}


	public Square createSquareSync(Square square) throws AhException {
		final AhCarrier<Square> carrier = new AhCarrier<Square>();

		squareTable.insert(square, new TableOperationCallback<Square>() {

			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {

				if (exception == null) {
					carrier.load(entity);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					throw new AhException(exception, "createSquareAsync");
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();
	}

	//	public void updateSquareAsync(Square square, final AhEntityCallback<Square> callback) throws AhException {
	//		squareTable.update(square, new TableOperationCallback<Square>() {
	//
	//			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {
	//				
	//				if (exception == null) {
	//					callback.onCompleted(entity);
	//				} else {
	//					throw new AhException(exception, "updateSquare");
	//				}
	//			}
	//		});
	//	}

	public boolean enterSquareSync(User user) throws AhException, InterruptedException {
		final AhCarrier<List<User>> carrier = new AhCarrier<List<User>>();

		JsonObject jo = user.toJson();

		Gson g = new Gson();
		JsonElement requestJson = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(ENTER_SQUARE, requestJson, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null){
					List<User> list = JsonConverter.convertToUserList(json);
					carrier.load(list);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					throw new AhException(exception, "enterSquareSync");
				}

			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw e;
			}
		}

		UserDBHelper userHelper = new UserDBHelper(context);
		userHelper.addAllUsers(carrier.getItem());

		return true;
	}


	public boolean exitSquareSync(String squareId) throws AhException {
		final AhCarrier<Boolean> carrier = new AhCarrier<Boolean>();

		userTable.delete(squareId, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception exception, ServiceFilterResponse arg1) {
				if (exception == null) {
					carrier.load(true);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					carrier.load(false);
					throw new AhException(exception, "exitSquareAsync");
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();

	}


	public boolean sendMessageSync(AhMessage message) throws AhException {

		final AhCarrier<Boolean> carrier = new AhCarrier<Boolean>();
		JsonObject jo = new JsonObject();
		jo.addProperty("type", message.getType());
		jo.addProperty("content", message.getContent());
		jo.addProperty("sender", message.getSender());
		jo.addProperty("senderId", message.getSenderId());
		jo.addProperty("receiver", message.getReceiver());
		jo.addProperty("receiverId", message.getReceiverId());

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(SEND_MESSAGE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null){
					carrier.load(true);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					throw new AhException(exception, "sendMessageSync");
				}

			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();
	}


	public boolean turnOnPush(){

		return false;
	}


	public boolean turnOffPush(){

		return false;
	}


	/**
	 *  Async Task Methods
	 * 
	 */
	public void getSquareListAsync(Location loc, final AhListCallback<Square> callback) throws AhException {
		JsonObject jo = new JsonObject();
		jo.addProperty(currentLatitude, loc.getLatitude());
		jo.addProperty(currentLongitude, loc.getLongitude());

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(GET_NEAR_SQUARE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					List<Square> list = JsonConverter.convertToSquareList(json.getAsJsonArray());
					if (list == null) throw new AhException(exception, getSquareList);
					callback.onCompleted(list, list.size());
				} else {
					throw new AhException(exception, getSquareListAsync);
				}
			}
		});
	}


	@Deprecated
	public String createSquareWithoutFuture() {
		final ServiceClient _this = this;
		final StringBuilder sb = new StringBuilder();

		final Object log = new Object();
		_this.createSquareAsync("squareName", 10.0, 10.0, new AhEntityCallback<Square>() {

			@Override
			public void onCompleted(Square entity) {
				sb.append(entity.getId());
				synchronized (log) {
					log.notify();
				}

			}
		});

		synchronized (log) {
			try {
				log.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}


	public void createSquareAsync(String name, double latitude, double longitude, final AhEntityCallback<Square> callback) throws AhException {
		String whoMade = pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY);

		if (whoMade == PreferenceHelper.DEFAULT_STRING) {
			throw new AhException("createSquare NO Registration");
		}

		Square square = new Square();
		square.setName(name);
		square.setLatitude(latitude);
		square.setLongitude(longitude);
		square.setWhoMade(whoMade);

		this.createSquareAsync(square, callback);
	}


	public void createSquareAsync(Square square, final AhEntityCallback<Square> callback) throws AhException {
		squareTable.insert(square, new TableOperationCallback<Square>() {

			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {

				if (exception == null) {
					callback.onCompleted(entity);
				} else {
					throw new AhException(exception, "createSquareAsync");
				}
			}
		});
	}


	public void enterSquareAsync(User user, final AhEntityCallback<Boolean> callback) throws AhException {
		userTable.insert(user, new TableOperationCallback<User>() {

			@Override
			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(true);
				} else {
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});
	}


	public void exitSquareAsync(String squareId, final AhEntityCallback<Boolean> callback) throws AhException {

		userTable.delete(squareId, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception exception, ServiceFilterResponse arg1) {
				if (exception == null) {
					callback.onCompleted(true);
				} else {
					throw new AhException(exception, "exitSquareAsync");
				}
			}
		});

	}


	public void sendMessageAsync(AhMessage message, final AhEntityCallback<AhMessage> callback) throws AhException {
		JsonObject jo = new JsonObject();
		jo.addProperty("type", message.getType());
		jo.addProperty("content", message.getContent());
		jo.addProperty("sender", message.getSender());
		jo.addProperty("senderId", message.getSenderId());
		jo.addProperty("receiver", message.getReceiver());
		jo.addProperty("receiverId", message.getReceiverId());

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(SEND_MESSAGE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				callback.onCompleted(null);
			}
		});
	}

	//	public void isAvailableNickName(User user, final AhEntityCallback<Boolean> callback) throws AhException {
	//	
	//	userTable.where().field("nickName").eq(val(true)).execute(new TableQueryCallback<User>() {
	//
	//		@Override
	//		public void onCompleted(List<User> result, int count, Exception exception, ServiceFilterResponse response) {
	//			// Succeed
	//			if (exception == null) {
	//				if (count > 0) callback.onCompleted(false);
	//				else callback.onCompleted(true);
	//			// failed
	//			} else {
	//				throw new AhException(exception, "isAvailableNickName");
	//			}
	//		}
	//	});
	//}
}
