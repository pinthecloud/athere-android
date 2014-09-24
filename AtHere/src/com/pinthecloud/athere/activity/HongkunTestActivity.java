package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Button;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.fragment.HongkunTestFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;

public class HongkunTestActivity extends AhActivity {
	Button btn01;
	Button btn02;
	Button btn03;
	Button btn04;
	int count = 0;
	int b1Count = 0;
	int b2Count = 0;
	int b3Count = 0;
	int b4Count = 0;
	StringBuilder squareId = new StringBuilder();
	UserHelper userHelper;
	MessageHelper messageHelper;
	SquareHelper squareHelper;

	public static final String SENDER_ID = "838051405989";
	AhFragment hongFrag;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_hongkun_test);
		btn01 = (Button)findViewById(R.id.drawer_user_chupa_btn);
		btn02 = (Button)findViewById(R.id.button2);
		btn03 = (Button)findViewById(R.id.button3);
		btn04 = (Button)findViewById(R.id.button4);

		userHelper = app.getUserHelper();
		squareHelper = app.getSquareHelper();
		messageHelper = app.getMessageHelper();


		/*
		 * Set Fragment to container
		 */
		try{
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			//SquareListFragment squareListFragment = new SquareListFragment();
			hongFrag = new HongkunTestFragment();
			fragmentTransaction.add(R.id.hongkun_test_container, hongFrag);
			fragmentTransaction.commit();
		} catch(AhException ex){
			Log(this,"HERE : ",ex);
		}
		
	}
	
	
	/*
	public void addItem(View view) {

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
				btn01.setText(btn01.getText().toString() + (b1Count++));
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
				btn02.setText(btn02.getText().toString() + (b2Count++));
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
				btn03.setText(btn03.getText().toString() + (b3Count++));
			}
		});
	}


	public void addItem04(View view) {

		new Thread(new Runnable(){

			@Override
			public void run() {
				AhMessage message = new AhMessage();
				message.setContent("Exit Square");
				message.setSender(who);
				message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
				message.setReceiverId("326BFDF8-BC82-4C98-B3D8-56A70B29D53E");
				message.setType(AhMessage.MESSAGE_TYPE.EXIT_SQUARE);

				// Send message to server
				messageHelper.sendMessageSync(message);
				Log.e("ERROR","complete!");
			}

		}).start();

	}
	 */
}
