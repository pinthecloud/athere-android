package com.pinthecloud.athere.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.util.AsyncChainer;

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


	public boolean sendMessageSync(final AhFragment frag, AhMessage message) throws AhException {

		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "sendMessageSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return false;
		}

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
					ExceptionManager.fireException(new AhException(frag, "sendMessageSync", AhException.TYPE.SERVER_ERROR));
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

	public void sendMessageAsync(final AhFragment frag, AhMessage message, final AhEntityCallback<AhMessage> callback) throws AhException {

		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "sendMessageAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

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

		//mClient.setContext(frag.getActivity());
		mClient.invokeApi(SEND_MESSAGE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception e,
					ServiceFilterResponse response) {
				if (e == null) {
					callback.onCompleted(null);
					AsyncChainer.notifyNext(frag);
				}
				else
					ExceptionManager.fireException(new AhException(frag, "sendMessageAsync", AhException.TYPE.SERVER_ERROR));
			}
		});

	}


	private Map<String, List<AhEntityCallback<AhMessage>>> mapList = new HashMap<String, List<AhEntityCallback<AhMessage>>>();

	private final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED_ON_AIR";
	//	public static final String MESSAGE_RECEIVED_WHILE_SLEEP = "MESSAGE_RECEIVED_WHILE_SLEEP";

//	public void setMessageHandler(AhMessage.TYPE type, AhEntityCallback<AhMessage> callback){
//		map.put(type.toString(), callback);
//		Log.d(AhGlobalVariable.LOG_TAG,"setMessageHandler :" + type.toString() + callback);
//	}

	public void setMessageHandler(AhEntityCallback<AhMessage> callback){
		List<AhEntityCallback<AhMessage>> list = mapList.get(MESSAGE_RECEIVED);
		if (list == null) {
			mapList.put(MESSAGE_RECEIVED, new ArrayList<AhEntityCallback<AhMessage>>());
			list = mapList.get(MESSAGE_RECEIVED);
		}
		list.add(callback);
	}

	public void triggerMessageEvent(AhMessage message){
		List<AhEntityCallback<AhMessage>> list = mapList.get(MESSAGE_RECEIVED);
		if(list != null) {
			for (AhEntityCallback<AhMessage> callback : list) {
				callback.onCompleted(message);
			}
		}
		else 
			Log.d(AhGlobalVariable.LOG_TAG,"[MessageHelper.triggerMessage] map.get(MESSAGE_RECEIVED); :" + message.getType());
	}
}
