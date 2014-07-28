package com.pinthecloud.athere.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareProfileActivity;
import com.pinthecloud.athere.adapter.SquareChatListAdapter;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class SquareChatFragment extends AhFragment{

	private ListView messageListView;
	private SquareChatListAdapter messageListAdapter;
	private EditText messageEditText;
	private Button sendButton;
	
	private Button tempBackButton;
	
	private UserDBHelper userDBHelper;
	private UserHelper userHelper;
	private MessageDBHelper messageDBHelper;
	private MessageHelper messageHelper;

	private ArrayList<AhMessage> messageList = new ArrayList<AhMessage>(); 

	private Square square;


	public SquareChatFragment(Square square) {
		super();
		this.square = square;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//messageDBHelper = app.getMessageDBHelper();
		messageHelper = app.getMessageHelper();
		userHelper = app.getUserHelper();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_chat, container, false);


		/*
		 * Set UI component
		 */
		messageListView = (ListView) view.findViewById(R.id.square_chat_frag_list);
		messageEditText = (EditText) view.findViewById(R.id.square_chat_frag_message_text);
		sendButton = (Button) view.findViewById(R.id.square_chat_frag_send_button);
		tempBackButton = (Button) view.findViewById(R.id.square_chat_frag_option_button);
		

		/*
		 * Set message list view
		 */
		messageListAdapter = new SquareChatListAdapter
				(context, R.layout.row_square_chat_list_send, messageList);
		messageListView.setAdapter(messageListAdapter);


		/*
		 * Set edit text
		 */
		messageEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String message = s.toString().trim();
				if(message.length() < 1){
					sendButton.setEnabled(false);
				}else{
					sendButton.setEnabled(true);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});


		/*
		 * Set button
		 */
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Make message and send it
				final AhMessage message = new AhMessage();
				message.setContent(messageEditText.getText().toString());
				message.setSender(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
				message.setSenderId(pref.getString(AhGlobalVariable.UNIQUE_ID_KEY));
				message.setReceiverId(square.getId());
				message.setType(AhMessage.MESSAGE_TYPE.TALK);
				message.setStatus(AhMessage.SENDING);
				messageList.add(message);
				messageListAdapter.notifyDataSetChanged();
				messageListView.setSelection(messageListView.getCount() - 1);
				messageEditText.setText("");

				// Send message to server
				messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {

					@Override
					public void onCompleted(AhMessage entity) {
						message.setStatus(AhMessage.SENT);
						messageListAdapter.notifyDataSetChanged();
					}
				});
			}
		});
		
		
		/**
		 * temp Button for getting out of the chat Room
		 */
		tempBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Make message and send it
				final AhMessage message = new AhMessage();
				message.setContent(messageEditText.getText().toString());
				message.setSender(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
				message.setSenderId(pref.getString(AhGlobalVariable.UNIQUE_ID_KEY));
				message.setReceiverId(square.getId());
				message.setType(AhMessage.MESSAGE_TYPE.EXIT_SQUARE);
				message.setStatus(AhMessage.SENDING);
				
				userHelper.exitSquareAsync(square.getId(), new AhEntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						// TODO Auto-generated method stub
						Log.e("ERROR","exit Square Succeed");
						
						messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {

							@Override
							public void onCompleted(AhMessage entity) {
								message.setStatus(AhMessage.SENT);
								
								Intent i = new Intent(context, SquareProfileActivity.class);
								startActivity(i);
							}
						});
					}
				});
			}
		});
		///////////////////////////////////////////////////////////////////////////////////

		/**
		 * See 
		 *   1) com.pinthecloud.athere.helper.MessageEventHelper class, which is the implementation of the needed structure 
		 *   2) com.pinthecloud.athere.AhIntentService class Line #47, which has the event time when to trigger
		 *  
		 * This method sets the MessageHandler received on app running
		 */
		messageHelper.setMessageHandler(new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {
				
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						messageList.add(message);
						messageListAdapter.notifyDataSetChanged();
						messageListView.setSelection(messageListView.getCount() - 1);
					}
				});
			}
		});

		return view;
	}
}
