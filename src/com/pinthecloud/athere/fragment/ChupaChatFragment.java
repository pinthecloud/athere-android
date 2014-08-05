package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareChatListAdapter;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhMessage.Builder;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class ChupaChatFragment extends AhFragment {
	
	ListView messageListView;
	EditText messageEditText;
	ImageButton sendButton;
	
	
	MessageHelper messageHelper;
	MessageDBHelper messageDBHelper;
	UserHelper userHelper;
	UserDBHelper userDBHelper;
	
	
	private SquareChatListAdapter messageListAdapter;
	private ArrayList<AhMessage> messageList = new ArrayList<AhMessage>(); 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userHelper = app.getUserHelper();
		userDBHelper = app.getUserDBHelper();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chupa_chat, container, false);
		
		/*
		 * Set UI component
		 */
		messageListView = (ListView) view.findViewById(R.id.chupa_chat_frag_list);
		messageEditText = (EditText) view.findViewById(R.id.chupa_chat_frag_message_text);
		sendButton = (ImageButton) view.findViewById(R.id.chupa_chat_frag_send_button);
		//tempBackButton = (Button) view.findViewById(R.id.chupa_chat_frag_option_button);


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
				AhMessage.Builder messageBuilder = new AhMessage.Builder();
				messageBuilder.setContent(messageEditText.getText().toString());
				messageBuilder.setSender(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
				messageBuilder.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
				messageBuilder.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
				messageBuilder.setType(AhMessage.MESSAGE_TYPE.CHUPA);
				
				final AhMessage message = messageBuilder.build();
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
		sendButton.setEnabled(false);


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

				//				List<String> userIdList = userInfoFetchBuffer.popAllUsersId();
				//				
				//				for(String id : userIdList) {
				//					UserDBHelper. userHelper.getUserSync(id);
				//				}

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

		if (!messageDBHelper.isEmpty()) {
			final List<AhMessage> messageListFromBuffer = messageDBHelper.popAllMessages();
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					for (AhMessage message : messageListFromBuffer) {
						messageList.add(message);
						messageListAdapter.notifyDataSetChanged();
						messageListView.setSelection(messageListView.getCount() - 1);
					}
				}
			});
		}
		
		return view;
	}


}
