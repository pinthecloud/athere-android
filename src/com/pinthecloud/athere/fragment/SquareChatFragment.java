package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareChatListAdapter;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.sqlite.MessageDBHelper;

public class SquareChatFragment extends AhFragment{

	private ListView messageListView;
	private SquareChatListAdapter messageListAdapter;
	private EditText messageEditText;
	private Button sendButton;

	private MessageDBHelper messageDBHelper;
	private MessageHelper messageHelper;

	private ArrayList<AhMessage> messageList = new ArrayList<AhMessage>(); 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageDBHelper = app.getMessageDBHelper();
		messageHelper = app.getMessageHelper();
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


		/*
		 * Set message list view
		 */
		messageListAdapter = new SquareChatListAdapter
				(context, R.layout.row_square_chat_list_send, messageList);
		messageListView.setAdapter(messageListAdapter);
		List<AhMessage> messagesFromBuffer = messageDBHelper.getAllMessages();
		for(int i=0 ; i<messagesFromBuffer.size() ; i++){
			messageList.add(messagesFromBuffer.get(i));
		}
		messageListAdapter.notifyDataSetChanged();


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
				//				message.setReceiverId("38A0D350-ABCA-4E9A-9249-4ACE9D571CE8");
				message.setStatus(AhMessage.SENDING);
				messageList.add(message);
				messageListAdapter.notifyDataSetChanged();
				messageEditText.setText("");

				// Send message to server
				//				messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {
				//
				//					@Override
				//					public void onCompleted(AhMessage entity) {
				//						Log.d(AhGlobalVariable.LOG_TAG, "sent message");
				//						message.setStatus(AhMessage.SENT);
				//						messageListAdapter.notifyDataSetChanged();
				//					}
				//				});
			}
		});


		/**
		 * See 
		 *   1) com.pinthecloud.athere.helper.MessageEventHelper class, which is the implementation of the needed structure 
		 *   2) com.pinthecloud.athere.AhIntentService class Line #47, which has the event time when to trigger
		 *  
		 * This method sets the MessageHandler received on app running
		 */
		messageDBHelper.setMessageHandler(new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
				messageList.add(message);
				messageListAdapter.notifyDataSetChanged();
			}
		});

		return view;
	}
}
