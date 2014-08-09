package com.pinthecloud.athere.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;
import com.pinthecloud.athere.util.BitmapUtil;

/**
 * 
 * @author hongkunyoo
 * Test Fragment for hongkunyoo's own experiments
 */

public class HongkunTestFragment extends AhFragment {


	Button[] btnArr;
	TextView messageText;
	int count = 0;
	int[] countArr;
	StringBuilder squareId = new StringBuilder();
	UserHelper userHelper;
	UserDBHelper userDBHelper;
	MessageHelper messageHelper;
	SquareHelper squareHelper;
	MessageDBHelper messageDB;
	private String who;
	private String content;
	String __id = "";

	public static final String SENDER_ID = "838051405989";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set Helper
		squareHelper = app.getSquareHelper();
		userHelper = app.getUserHelper();
		userDBHelper = app.getUserDBHelper();
		squareHelper = app.getSquareHelper();
		messageHelper = app.getMessageHelper();
		messageDB = app.getMessageDBHelper();
		who = "test user";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_hongkun_test, container, false);

		countArr = new int[6];
		btnArr = new Button[6];
		btnArr[0] = (Button)view.findViewById(R.id.drawer_user_chupa_btn);
		btnArr[1] = (Button)view.findViewById(R.id.button2);
		btnArr[2] = (Button)view.findViewById(R.id.button3);
		btnArr[3] = (Button)view.findViewById(R.id.button4);
		btnArr[4] = (Button)view.findViewById(R.id.button5);
		btnArr[5] = (Button)view.findViewById(R.id.button6);
		messageText = (TextView)view.findViewById(R.id.message_text);

		for(int i = 0 ; i < 6 ; i++){
			btnArr[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Button b = (Button)v;
					if (b.getId() == btnArr[0].getId()) {
						
						User user = User.addUserTest();
						__id = user.getId();
						userDBHelper.addUser(user);
						Log("add User id : ", __id);
					} else if (b.getId() == btnArr[1].getId()) {
						User user = userDBHelper.getUser(__id);
						Log("id : ",__id, "getUser : ", user);
						userDBHelper.exitUser(__id);
						Log("exitUser");
					} else if (b.getId() == btnArr[2].getId()) {
						User user = userDBHelper.getUser(__id);
						Log("getUser(id) : ",user);
					} else if (b.getId() == btnArr[3].getId()) {
						User user = userDBHelper.getUser(__id, true);
						Log("getUser(id, true) : ",user);
						
					} else if (b.getId() == btnArr[4].getId()) {
						User user = userDBHelper.getUser(__id, false);
						Log("getUser(id, false) : ",user);
					} else if (b.getId() == btnArr[5].getId()) {
						Log("isUserExit : ", userDBHelper.isUserExit(__id));
					}
					messageText.setText(b.getText());
				}
			});
		}


		return view;
	}

	//	public void addItem01(View view) {
	//		
	//		AhMessage message = new AhMessage();
	//		message.setContent("Enter Square");
	//		message.setSender(who);
	//		message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
	//		message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
	//		message.setType(AhMessage.MESSAGE_TYPE.ENTER_SQUARE);
	//		
	//		// Send message to server
	//		messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {
	//
	//			@Override
	//			public void onCompleted(AhMessage entity) {
	//				// TODO Auto-generated method stub
	//				btn01.setText("Enter" + (b1Count++));
	//				Log.e("ERROR","Enter succeed");
	//				messageText.setText("Enter succeed");
	//			}
	//		});
	//	}
	//	
	//	public void addItem02(View view) {
	//		
	//		AhMessage message = new AhMessage();
	//		message.setContent("Exit Square");
	//		message.setSender(who);
	//		message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
	//		message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
	//		message.setType(AhMessage.MESSAGE_TYPE.EXIT_SQUARE);
	//		
	//		// Send message to server
	//		messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {
	//
	//			@Override
	//			public void onCompleted(AhMessage entity) {
	//				// TODO Auto-generated method stub
	//				btn02.setText("Exit" + (b2Count++));
	//				Log.e("ERROR","Exit succeed");
	//				messageText.setText("Exit succeed");
	//			}
	//		});
	//	}
	//	
	//	public void addItem03(View view) {
	//		
	//		AhMessage message = new AhMessage();
	//		message.setContent("talk talk");
	//		message.setSender(who);
	//		message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
	//		message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
	//		message.setType(AhMessage.MESSAGE_TYPE.TALK);
	//		
	//		// Send message to server
	//		messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {
	//
	//			@Override
	//			public void onCompleted(AhMessage entity) {
	//				// TODO Auto-generated method stub
	//				btn03.setText("Talk" + (b3Count++));
	//				Log.e("ERROR","Talk succeed");
	//				messageText.setText("Talk succeed");
	//			}
	//		});
	//	}
	//	
	//	public void addItem04(View view) {
	//		new Thread(new Runnable(){
	//
	//			@Override
	//			public void run() {
	//				// TODO Auto-generated method stub
	//				AhMessage message = new AhMessage();
	//				message.setContent("Chupa");
	//				message.setSender(who);
	//				message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
	//				message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
	//				message.setType(AhMessage.MESSAGE_TYPE.CHUPA);
	//				
	//				// Send message to server
	//				messageHelper.sendMessageSync(message);
	//				activity.runOnUiThread(new Runnable() {
	//					
	//					@Override
	//					public void run() {
	//						// TODO Auto-generated method stub
	//						btn04.setText("Chupa" + (b4Count++));
	//						messageText.setText("Chupa succeed");
	//					}
	//				});
	//				Log.e("ERROR","Chupa succeed!");
	//				
	//			}
	//			
	//		}).start();
	//	}
	//	
	//	public void addItem05(View view) {
	//		new Thread(new Runnable(){
	//
	//			@Override
	//			public void run() {
	//				// TODO Auto-generated method stub
	//				AhMessage message = new AhMessage();
	//				message.setContent("Shout");
	//				message.setSender(who);
	//				message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
	//				message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
	//				message.setType(AhMessage.MESSAGE_TYPE.SHOUTING);
	//				
	//				// Send message to server
	//				messageHelper.sendMessageSync(message);
	//				Log.e("ERROR","Shout succeed!");
	//				activity.runOnUiThread(new Runnable() {
	//					
	//					@Override
	//					public void run() {
	//						// TODO Auto-generated method stub
	//						btn05.setText("Shout" + (b5Count++));
	//						messageText.setText("Shout succeed");
	//					}
	//				});
	//			}
	//			
	//		}).start();
	//	}
	//	
	//	public void addItem06(View view) {
	//		new Thread(new Runnable(){
	//
	//			@Override
	//			public void run() {
	//				// TODO Auto-generated method stub
	//				AhMessage message = new AhMessage();
	//				message.setContent("Update User");
	//				message.setSender(who);
	//				message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
	//				message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
	//				message.setType(AhMessage.MESSAGE_TYPE.SHOUTING);
	//				
	//				// Send message to server
	//				messageHelper.sendMessageSync(message);
	//				Log.e("ERROR","Update User succeed!");
	//				activity.runOnUiThread(new Runnable() {
	//					
	//					@Override
	//					public void run() {
	//						// TODO Auto-generated method stub
	//						btn06.setText("Update User" + (b6Count++));
	//						messageText.setText("Update User succeed");
	//					}
	//				});
	//			}
	//			
	//		}).start();
	//	}

}
