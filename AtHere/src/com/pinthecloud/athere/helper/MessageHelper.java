package com.pinthecloud.athere.helper;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.activity.AhActivity;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.util.AsyncChainer;

public class MessageHelper {

	private AhApplication app;
	private MobileServiceClient mClient;

	/*
	 * Methods name
	 */
	private final String SEND_MESSAGE = "send_message";


	public MessageHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.mClient = app.getmClient();
	}


	public void sendMessageAsync(final AhFragment frag, final AhMessage message, final AhEntityCallback<AhMessage> callback) throws AhException {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "sendMessageAsync", AhException.TYPE.INTERNET_NOT_CONNECTED, message));
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

		mClient.invokeApi(SEND_MESSAGE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception e,
					ServiceFilterResponse response) {
				if (e == null) {
					callback.onCompleted(null);
					AsyncChainer.notifyNext(frag);
				}
				else
					ExceptionManager.fireException(new AhException(frag, "sendMessageAsync", AhException.TYPE.SERVER_ERROR, message));
			}
		});

	}
	

	/**
	 *  ===[The Message Triggering Propagation Mechanism]===
	 *  When the message pushed by server comes,
	 *  
	 *  The message triggers the Activity currently on Top (whether [SquareActivity] or [ChupaChatActivity])
	 * 	if [SquareActivity]
	 * 		it  triggers [SquareTabFragment]
	 * 		and [SquareTabFragment] triggers both [SquareChatFragment] & [SquareChupaListFragment]
	 * 
	 *  if [ChupaChatActivity]
	 *  	it triggers [ChupaChatFragment]
	 *  
	 *  
	 *  ===[The Way to Program in Each Handler]===
	 *  The handlers SHOULD NOT MODIFY any Database contents.
	 *  The Database contents will be modified before handlers. (Precisely, in [AhIntentService])
	 *  The handlers should only refresh the related Views. (typically, refreshView() method)
	 * 
	 */

	private Map<String, AhEntityCallback<AhMessage>> map = new HashMap<String, AhEntityCallback<AhMessage>>();
	public void setMessageHandler(AhActivity activity, AhEntityCallback<AhMessage> callback){
		map.put(activity.getClass().getName(), callback);
	}
	public void setMessageHandler(AhFragment frag, AhEntityCallback<AhMessage> callback){
		map.put(frag.getClass().getName(), callback);
	}
	public void triggerMessageEvent(AhFragment frag, AhMessage message){
		this.triggerMessageEvent(frag.getClass().getName(), message);
	}
	public void triggerMessageEvent(String key, AhMessage message){
		AhEntityCallback<AhMessage> callback = map.get(key);
		if(callback != null) {
			callback.onCompleted(message);
		}
	}


	/*
	 * Sync Method
	 * NOT USING
	 */

	//	public boolean _sendMessageSync(final AhFragment frag, AhMessage message) throws AhException {
	//
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "sendMessageSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return false;
	//		}
	//
	//		final AhCarrier<Boolean> carrier = new AhCarrier<Boolean>();
	//
	//		JsonObject jo = new JsonObject();
	//		jo.addProperty("type", message.getType());
	//		jo.addProperty("content", message.getContent());
	//		jo.addProperty("sender", message.getSender());
	//		jo.addProperty("senderId", message.getSenderId());
	//		jo.addProperty("receiver", message.getReceiver());
	//		jo.addProperty("receiverId", message.getReceiverId());
	//		jo.addProperty("timeStamp", message.getTimeStamp());
	//		jo.addProperty("chupaCommunId", message.getChupaCommunId());
	//
	//		Gson g = new Gson();
	//		JsonElement json = g.fromJson(jo, JsonElement.class);
	//
	//		mClient.invokeApi(SEND_MESSAGE, json, new ApiJsonOperationCallback() {
	//
	//			@Override
	//			public void onCompleted(JsonElement json, Exception exception,
	//					ServiceFilterResponse response) {
	//				if(exception == null){
	//					carrier.load(true);
	//					synchronized (lock) {
	//						lock.notify();
	//					}
	//				} else {
	//					carrier.load(false);
	//					ExceptionManager.fireException(new AhException(frag, "sendMessageSync", AhException.TYPE.SERVER_ERROR));
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
	//		return carrier.getItem();
	//	}
}
