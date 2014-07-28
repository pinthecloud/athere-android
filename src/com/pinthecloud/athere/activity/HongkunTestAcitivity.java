package com.pinthecloud.athere.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;

public class HongkunTestAcitivity extends AhActivity {
	Button btn;
	int count = 0;
	StringBuilder squareId = new StringBuilder();

	public static final String SENDER_ID = "838051405989";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hongkun_test_acitivity);
		btn = (Button)findViewById(R.id.button1);

		//		(new AsyncTask<Context, Void, String>(){
		//
		//			@Override
		//			protected String doInBackground(Context... arg0) {
		//				// TODO Auto-generated method stub
		//
		//				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(arg0[0]);
		//				String registrationId = "";
		//				try {
		//					registrationId = gcm.register(SENDER_ID);
		//				} catch (IOException e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
		//				return registrationId;
		//			}
		//
		//			@Override
		//			protected void onPostExecute(String result) {
		//				super.onPostExecute(result);
		//				//				serviceClient.setProfile("BobNick", true, 1989, result);
		//				Log.e("ERROR","succeed : " + result);
		//			}
		//
		//		}).execute(this);
	}

	public void addItem(View view) {
		final User user = new User();

		user.setNickName("TestNick");
		user.setProfilePic("pic");
		user.setMale(true);
		user.setCompanyNum(3);
		user.setAge(21);
		user.setMobileId("Android");
		user.setRegistrationId("registID");
		user.setSquareId("38A0D350-ABCA-4E9A-9249-4ACE9D571CE8");


		MobileServiceClient mClient = AhApplication.getInstance().getmClient();

		mClient.invokeApi("enter_square", new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				Log.e("ERROR","DONE");
			}
		});

		//		AhApplication.getInstance().getUserHelper().enterSquareAsync(user, new AhEntityCallback<Boolean>() {
		//
		//			@Override
		//			public void onCompleted(Boolean entity) {
		//				// TODO Auto-generated method stub
		//				Log.e("ERROR","OK");
		//			}
		//			
		//		});

		//		new Thread(new Runnable(){
		//
		//			@Override
		//			public void run() {
		//				// TODO Auto-generated method stub
		//				AhApplication.getInstance().getUserHelper().enterSquareSync(user);
		//			}
		//			
		//		}).start();


		boolean var = true;
		if(var) return;

		new Thread(new Runnable() {

			@Override
			public void run() {
				//String result = serviceClient.createSquareWithoutFuture();
				String result = "38A0D350-ABCA-4E9A-9249-4ACE9D571CE8";
				Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_splash);
				//				serviceClient.enterSquareSync(result, img, 3, "Android");

				AhMessage message = new AhMessage();

				//message.setType(MESSAGE_TYPE.SQUARE);
				message.setContent("message contents");
				message.setSender("bobNick");
				//				message.setSenderId(pref.getRegistrationId());
				message.setReceiver("receiver name");
				message.setReceiverId(result);
				Log.e("ERROR", message.getReceiverId());
				//				boolean re = serviceClient.sendMessageSync(message);
				//				Log.e("ERROR","result : "+re);
			}
		}).start();
	}
}
