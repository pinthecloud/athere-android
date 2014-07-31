package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.sqlite.UserInfoFetchBuffer;

public class HongkunTestFragment extends AhFragment {

	Button btn01;
	Button btn02;
	Button btn03;
	Button btn04;
	Button btn05;
	Button btn06;
	TextView messageText;
	int count = 0;
	int b1Count = 0;
	int b2Count = 0;
	int b3Count = 0;
	int b4Count = 0;
	int b5Count = 0;
	int b6Count = 0;
	StringBuilder squareId = new StringBuilder();
	UserHelper userHelper;
	MessageHelper messageHelper;
	SquareHelper squareHelper;
	
	private String who;
	private String content;

	public static final String SENDER_ID = "838051405989";
	UserInfoFetchBuffer buffer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set Helper
		squareHelper = app.getSquareHelper();
		userHelper = app.getUserHelper();
		squareHelper = app.getSquareHelper();
		messageHelper = app.getMessageHelper();
		
		who = "test user";
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_hongkun_test, container, false);
		
		btn01 = (Button)view.findViewById(R.id.button1);
		btn02 = (Button)view.findViewById(R.id.button2);
		btn03 = (Button)view.findViewById(R.id.button3);
		btn04 = (Button)view.findViewById(R.id.button4);
		btn05 = (Button)view.findViewById(R.id.button5);
		btn06 = (Button)view.findViewById(R.id.button6);
		messageText = (TextView)view.findViewById(R.id.message_text);
		
		btn01.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addItem01(v);
			}
		});
		
		btn02.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addItem02(v);
			}
		});

		btn03.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addItem03(v);
			}
		});
		
		btn04.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addItem04(v);
			}
		});
		
		btn05.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addItem05(v);
			}
		});
		
		btn06.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addItem06(v);
			}
		});
		
		return view;
	}
	
	public void addItem01(View view) {
		
		AhMessage message = new AhMessage();
		message.setContent("Enter Square");
		message.setSender(who);
		message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
		message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
		message.setType(AhMessage.MESSAGE_TYPE.ENTER_SQUARE);
		
		// Send message to server
		messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				// TODO Auto-generated method stub
				btn01.setText("Enter" + (b1Count++));
				Log.e("ERROR","Enter succeed");
				messageText.setText("Enter succeed");
			}
		});
	}
	
	public void addItem02(View view) {
		
		AhMessage message = new AhMessage();
		message.setContent("Exit Square");
		message.setSender(who);
		message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
		message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
		message.setType(AhMessage.MESSAGE_TYPE.EXIT_SQUARE);
		
		// Send message to server
		messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				// TODO Auto-generated method stub
				btn02.setText("Exit" + (b2Count++));
				Log.e("ERROR","Exit succeed");
				messageText.setText("Exit succeed");
			}
		});
	}
	
	public void addItem03(View view) {
		
		AhMessage message = new AhMessage();
		message.setContent("talk talk");
		message.setSender(who);
		message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
		message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
		message.setType(AhMessage.MESSAGE_TYPE.TALK);
		
		// Send message to server
		messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				// TODO Auto-generated method stub
				btn03.setText("Talk" + (b3Count++));
				Log.e("ERROR","Talk succeed");
				messageText.setText("Talk succeed");
			}
		});
	}
	
	public void addItem04(View view) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AhMessage message = new AhMessage();
				message.setContent("Chupa");
				message.setSender(who);
				message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
				message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
				message.setType(AhMessage.MESSAGE_TYPE.CHUPA);
				
				// Send message to server
				messageHelper.sendMessageSync(message);
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						btn04.setText("Chupa" + (b4Count++));
						messageText.setText("Chupa succeed");
					}
				});
				Log.e("ERROR","Chupa succeed!");
				
			}
			
		}).start();
	}
	
	public void addItem05(View view) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AhMessage message = new AhMessage();
				message.setContent("Shout");
				message.setSender(who);
				message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
				message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
				message.setType(AhMessage.MESSAGE_TYPE.SHOUTING);
				
				// Send message to server
				messageHelper.sendMessageSync(message);
				Log.e("ERROR","Shout succeed!");
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						btn05.setText("Shout" + (b5Count++));
						messageText.setText("Shout succeed");
					}
				});
			}
			
		}).start();
	}
	
	public void addItem06(View view) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AhMessage message = new AhMessage();
				message.setContent("Update User");
				message.setSender(who);
				message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
				message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
				message.setType(AhMessage.MESSAGE_TYPE.SHOUTING);
				
				// Send message to server
				messageHelper.sendMessageSync(message);
				Log.e("ERROR","Update User succeed!");
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						btn06.setText("Update User" + (b6Count++));
						messageText.setText("Update User succeed");
					}
				});
			}
			
		}).start();
	}
}
