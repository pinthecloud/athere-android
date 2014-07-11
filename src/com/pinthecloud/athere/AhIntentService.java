package com.pinthecloud.athere;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pinthecloud.athere.model.AhMessage;

public class AhIntentService extends IntentService {

	public AhIntentService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
		Log.e("ERROR", messageType);
		
		AhMessage message = parseIntent(intent);
		
		if (message.getType().equals("")) return;
		
		Log.e("ERROR", message.toString());
		 
	}
	
	private AhMessage parseIntent(Intent intent) {
		AhMessage message = new AhMessage();
		Bundle b = intent.getExtras();
		String type = b.getString("type");
		String content = b.getString("content");
		String sender = b.getString("sender");
		String senderId = b.getString("senderId");
		String receiver = b.getString("receiver");
		String receiverId = b.getString("receiverId");
		
		message.setType(type);
		message.setContent(content);
		message.setSender(sender);
		message.setSenderId(senderId);
		message.setReceiver(receiver);
		message.setReceiverId(receiverId);
		
		return message;
	}
}
