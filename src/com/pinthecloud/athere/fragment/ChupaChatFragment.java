package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareChatListAdapter;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhException;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class ChupaChatFragment extends AhFragment {
	
	private ListView messageListView;
	private EditText messageEditText;
	private ImageButton sendButton;
	
	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;
	private UserHelper userHelper;
	private UserDBHelper userDBHelper;
	
	private User user;
	
	private SquareChatListAdapter messageListAdapter;
	private ArrayList<AhMessage> messageList = new ArrayList<AhMessage>(); 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userHelper = app.getUserHelper();
		userDBHelper = app.getUserDBHelper();
		Intent intent = activity.getIntent();
		user = intent.getParcelableExtra(AhGlobalVariable.USER_KEY);
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
				messageBuilder.setContent(messageEditText.getText().toString())
				.setSender(pref.getString(AhGlobalVariable.NICK_NAME_KEY))
				.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
				.setReceiverId(user.getId())
				.setType(AhMessage.TYPE.CHUPA);
				
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

		/**
		 * See 
		 *   1) com.pinthecloud.athere.helper.MessageEventHelper class, which is the implementation of the needed structure 
		 *   2) com.pinthecloud.athere.AhIntentService class Line #47, which has the event time when to trigger
		 *  
		 * This method sets the MessageHandler received on app running
		 */
		messageHelper.setMessageHandler(AhMessage.TYPE.CHUPA, new AhEntityCallback<AhMessage>() {

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
		
		String chupaCommunId = new AhMessage.Builder()
					.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
					.setReceiverId((user.getId()))
					.build().getChupaCommunId();
		
		if(chupaCommunId == null || "".equals(chupaCommunId)) throw new AhException("No chupaCommunId");
		final List<AhMessage> chupas = messageDBHelper.getChupasByCommunId(chupaCommunId);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				for (AhMessage message : chupas) {
					messageList.add(message);
					messageListAdapter.notifyDataSetChanged();
					messageListView.setSelection(messageListView.getCount() - 1);
				}
			}
		});
		
		return view;
	}
}
