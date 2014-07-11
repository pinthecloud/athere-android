package com.pinthecloud.athere.activity;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.pinthecloud.athere.AhEntityCallback;
import com.pinthecloud.athere.AhException;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhMessage.MESSAGE_TYPE;
import com.pinthecloud.athere.model.ToDoItem;

public class HongkunTestAcitivity extends AhActivity {
	Button btn;
	int count = 0;
	StringBuilder squareId = new StringBuilder();
	
	private MobileServiceTable<ToDoItem> mToDoTable;
	private MobileServiceClient mClient;
	
	public static final String SENDER_ID = "838051405989";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hongkun_test_acitivity);
		btn = (Button)findViewById(R.id.button1);
		
		mClient = serviceClient.getClient();
		mToDoTable = mClient.getTable(ToDoItem.class);
		
		//NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
		
		(new AsyncTask<Context, Void, String>(){

			@Override
			protected String doInBackground(Context... arg0) {
				// TODO Auto-generated method stub
				
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(arg0[0]);
				String registrationId = "";
				try {
					registrationId = gcm.register(SENDER_ID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return registrationId;
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				serviceClient.setProfile("BobNick", true, 1989, result);
				Log.e("ERROR","succeed : " + result);
			}
			
		}).execute(this);
		
		
		
	}
	
	public void addItem(View view) {
		
//		new AsyncTask<Void, Void, String>(){
//
//			@Override
//			protected String doInBackground(Void... params) {
//				// TODO Auto-generated method stub
//				
//				return serviceClient.createSquareWithoutFuture();
//			}
//			
//			@Override
//			protected void onPostExecute(String result) {
//				// TODO Auto-generated method stub
//				super.onPostExecute(result);
//				
//				Log.e("ERROR", "onPost Result : " + result);
//			}
//			
//		}.execute();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = serviceClient.createSquareWithoutFuture();
				
				squareId.append(result);
				Log.e("ERROR", "squareId : "+result);
			}
		}).start();
			
	}
	
	public void addItem02(View view) {
		 
		Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.splash_logo);
		serviceClient.enterSquareAsync(squareId.toString(), img, 3, "mobileId", new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				// TODO Auto-generated method stub
				Log.e("ERROR", "addItem02 OK : " + entity);
				
			}
		});
	}
	
	public void addItem03(View view) {
		 
		AhMessage message = new AhMessage();
		
		message.setType(MESSAGE_TYPE.SQUARE);
		message.setContent("message contents");
		message.setSender("bobNick");
		message.setSenderId(pref.getRegistrationId());
		message.setReceiver("receiver name");
		message.setReceiverId(squareId.toString());
		
		try{
			serviceClient.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {
				
				@Override
				public void onCompleted(AhMessage entity) {
					// TODO Auto-generated method stub
					Log.e("ERROR","addItem03 OK : ");
				}
			});
		} catch (AhException ex) {
			ex.printStackTrace();
		}
		
	}
}
