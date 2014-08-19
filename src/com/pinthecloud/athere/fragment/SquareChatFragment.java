package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareChatListAdapter;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

public class SquareChatFragment extends AhFragment{

	private EditText messageEditText;
	private ImageButton sendButton;

	private ListView messageListView;
	private SquareChatListAdapter messageListAdapter;
	private List<AhMessage> messageList = new ArrayList<AhMessage>();

	private String squareId;


	public SquareChatFragment(String squareId) {
		super();
		this.squareId = squareId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				.setReceiverId(squareId)
				.setType(AhMessage.TYPE.TALK)
				.setStatus(AhMessage.STATUS.SENDING);
				final AhMessage message = messageBuilder.build();

				messageList.add(message);
				messageListAdapter.notifyDataSetChanged();
				messageListView.setSelection(messageListView.getCount() - 1);
				messageEditText.setText("");
				final int id = messageDBHelper.addMessage(message);

				// Send message to server
				messageHelper.sendMessageAsync(_thisFragment, message, new AhEntityCallback<AhMessage>() {

					@Override
					public void onCompleted(AhMessage entity) {
						message.setStatus(AhMessage.STATUS.SENT);
						messageListAdapter.notifyDataSetChanged();
						messageDBHelper.updateMessages(id, message);
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
		messageListView.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 1) {
					// TODO : Insert messageListView.add(0, messages);
				}
			}
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
		});

		/**
		 * See 
		 *   1) com.pinthecloud.athere.helper.MessageEventHelper class, which is the implementation of the needed structure 
		 *   2) com.pinthecloud.athere.AhIntentService class Line #47, which has the event time when to trigger
		 *  
		 * This method sets the MessageHandler received on app running
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {

				// Chupa & User Update Message can't go through here
				if (message.getType().equals(AhMessage.TYPE.CHUPA.toString())
						||message.getType().equals(AhMessage.TYPE.UPDATE_USER_INFO.toString())) return;
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						refreshView();
					}
				});
			}
		});

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		Log.d(AhGlobalVariable.LOG_TAG, "SquareChatFragment onStart");
		refreshView();
	}


	/**
	 * @author hongkunyoo
	 * notify this Method When this Fragment is on Resume
	 * so that the Message stored in MessageDBHelper can inflate to the view again
	 */
	private void refreshView(){
		if (!messageDBHelper.isEmpty(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE, AhMessage.TYPE.TALK)) {
			final List<AhMessage> talks = messageDBHelper.getAllMessages(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE, AhMessage.TYPE.TALK);
			messageList.clear();
			messageList.addAll(talks);
			messageListAdapter.notifyDataSetChanged();
			messageListView.setSelection(messageListView.getCount() - 1);
		}
	}


	//	@Override
	//	public void handleException(AhException ex) {
	//		super.handleException(ex);
	//		Log(_thisFragment, "in SquareChatFrag : " + ex.toString());
	//		if (message != null) {
	//			message.setStatus(AhMessage.FAIL);
	//			messageListAdapter.notifyDataSetChanged();
	//		}
	//	}
}
