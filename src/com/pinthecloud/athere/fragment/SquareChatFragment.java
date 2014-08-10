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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareChatListAdapter;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.sqlite.MessageDBHelper;

public class SquareChatFragment extends AhFragment{

	private EditText messageEditText;
	private ImageButton sendButton;

	private ListView messageListView;
	private SquareChatListAdapter messageListAdapter;
	private List<AhMessage> messageList = new ArrayList<AhMessage>();

	private Square square;

	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;


	public SquareChatFragment(Square square) {
		super();
		this.square = square;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
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
		sendButton = (ImageButton) view.findViewById(R.id.square_chat_frag_send_button);


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
				.setReceiverId(square.getId())
				.setType(AhMessage.TYPE.TALK);

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


		/*
		 * Set message list view
		 */
		messageListAdapter = new SquareChatListAdapter
				(context, this, R.layout.row_square_chat_list_send, messageList);
		messageListView.setAdapter(messageListAdapter);


		/**
		 * See 
		 *   1) com.pinthecloud.athere.helper.MessageEventHelper class, which is the implementation of the needed structure 
		 *   2) com.pinthecloud.athere.AhIntentService class Line #47, which has the event time when to trigger
		 *  
		 * This method sets the MessageHandler received on app running
		 */
		messageHelper.setMessageHandler(AhMessage.TYPE.TALK, new AhEntityCallback<AhMessage>() {

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

		if (!messageDBHelper.isEmpty(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE)) {
			final List<AhMessage> messageListFromBuffer = messageDBHelper.popAllMessages(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE);
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
