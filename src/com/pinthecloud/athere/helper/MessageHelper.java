package com.pinthecloud.athere.helper;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhException;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.util.ExceptionManager;

public class MessageHelper {

	private AhApplication app;
	private MobileServiceClient mClient;
	private Object lock;

	/*
	 * Methods name
	 */
	private final String SEND_MESSAGE = "send_message";


	public MessageHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.mClient = app.getmClient();
		this.lock = app.getLock();
	}


	public boolean sendMessageSync(AhMessage message) throws AhException {
		
		if (!AhApplication.isOnline()) throw new AhException(AhException.TYPE.INTERNET_NOT_CONNECTED);
		
		final AhCarrier<Boolean> carrier = new AhCarrier<Boolean>();

		JsonObject jo = new JsonObject();
		jo.addProperty("type", message.getType());
		jo.addProperty("content", message.getContent());
		jo.addProperty("sender", message.getSender());
		jo.addProperty("senderId", message.getSenderId());
		jo.addProperty("receiver", message.getReceiver());
		jo.addProperty("receiverId", message.getReceiverId());
		jo.addProperty("timeStamp", message.getTimeStamp());
		jo.addProperty("chupaCommunId", message.getChupaCommunId());

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(SEND_MESSAGE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if(exception == null){
					carrier.load(true);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					carrier.load(false);
					ExceptionManager.fireException(new AhException(exception, "sendMessageSync", AhException.TYPE.SERVER_ERROR));
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

	public void sendMessageAsync(AhMessage message, final AhEntityCallback<AhMessage> callback) throws AhException {
		
		if (!AhApplication.isOnline()) throw new AhException(AhException.TYPE.INTERNET_NOT_CONNECTED);
		
		JsonObject jo = new JsonObject();
		jo.addProperty("type", message.getType());
		jo.addProperty("content", message.getContent());
		jo.addProperty("sender", message.getSender());
		jo.addProperty("senderId", message.getSenderId());
		jo.addProperty("receiver", message.getReceiver());
		jo.addProperty("receiverId", message.getReceiverId());
		jo.addProperty("timeStamp", message.getTimeStamp());
		jo.addProperty("chupaCommunId", message.getChupaCommunId());

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);
		
		mClient.invokeApi(SEND_MESSAGE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null)
					callback.onCompleted(null);
				else
					ExceptionManager.fireException(new AhException(exception, "sendMessageAsync", AhException.TYPE.SERVER_ERROR));
			}
		});
		
	}


	private Map<String, AhEntityCallback<AhMessage>> map = new HashMap<String, AhEntityCallback<AhMessage>>();

	private final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED_ON_AIR";
//	public static final String MESSAGE_RECEIVED_WHILE_SLEEP = "MESSAGE_RECEIVED_WHILE_SLEEP";

	public void setMessageHandler(AhMessage.TYPE type, AhEntityCallback<AhMessage> callback){
		map.put(type.toString(), callback);
	}
	
	public void setMessageHandler(AhEntityCallback<AhMessage> callback){
		map.put(MESSAGE_RECEIVED, callback);
	}

	public void triggerMessageEvent(AhMessage message){
		AhEntityCallback<AhMessage> callback = map.get(message.getType());
		if(callback != null)
			callback.onCompleted(message);
		else 
			Log.d(AhGlobalVariable.LOG_TAG,"message.getType() :" + message.getType());
		
		callback = map.get(MESSAGE_RECEIVED);
		if(callback != null)
			callback.onCompleted(message);
		else 
			Log.d(AhGlobalVariable.LOG_TAG,"map.get(MESSAGE_RECEIVED); :" + message.getType());
	}
}
