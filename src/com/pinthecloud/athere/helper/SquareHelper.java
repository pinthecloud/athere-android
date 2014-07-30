package com.pinthecloud.athere.helper;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhException;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.util.JsonConverter;

public class SquareHelper {

	private AhApplication app;
	private MobileServiceClient mClient;
	private PreferenceHelper pref;
	private Object lock;

	/**
	 * Model tables
	 */
	private MobileServiceTable<Square> squareTable;

	/*
	 * Methods name
	 */
	private final String GET_NEAR_SQUARE = "get_near_square";

	/*
	 * String
	 */
	private final String currentLatitude = "currentLatitude";
	private final String currentLongitude = "currentLongitude";


	public SquareHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.mClient = app.getmClient();
		this.pref = app.getPref();
		this.lock = app.getLock();
		this.squareTable = app.getSquareTable();
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
					if (list == null) throw new AhException(exception, "getSquareList");
					carrier.load(list);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					throw new AhException(exception, "getSquareListSync");
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

		if (whoMade.equals(PreferenceHelper.DEFAULT_STRING)) {
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


	/*
	 *  Async Task Methods
	 */
	public void getSquareListAsync(double latitude, double longitude, final AhListCallback<Square> callback) throws AhException {
		JsonObject jo = new JsonObject();
		jo.addProperty(currentLatitude, latitude);
		jo.addProperty(currentLongitude, longitude);

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(GET_NEAR_SQUARE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					List<Square> list = JsonConverter.convertToSquareList(json.getAsJsonArray());
					if (list == null) throw new AhException(exception, "getSquareList");
					callback.onCompleted(list, list.size());
				} else {
					throw new AhException(exception, "getSquareListAsync");
				}
			}
		});
	}


	public void createSquareAsync(String name, double latitude, double longitude, final AhEntityCallback<Square> callback) throws AhException {
		String whoMade = pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY);

		if (whoMade.equals(PreferenceHelper.DEFAULT_STRING)) {
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

	public Square getSquare(){
		Square square = new Square();
		square.setId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
		square.setName(pref.getString(AhGlobalVariable.SQUARE_NAME_KEY));
		return square;
	}
}
