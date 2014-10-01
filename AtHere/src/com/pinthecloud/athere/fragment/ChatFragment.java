package com.pinthecloud.athere.fragment;

import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.ChatListAdapter;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;

public class ChatFragment extends AhFragment{

	private ListView messageListView;
	private ChatListAdapter messageListAdapter;
	private List<AhMessage> chats;
	private AhMessage chat;

	private LinearLayout inputbarLayout;
	private EditText messageEditText;
	private ImageButton sendButton;

	private Square square;


	public ChatFragment() {
		super();
	}


	public ChatFragment(Square square) {
		super();
		this.square = square;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_chat, container, false);


		/*
		 * Set UI component
		 */
		inputbarLayout = (LinearLayout) view.findViewById(R.id.chat_frag_inputbar_layout);
		messageEditText = (EditText) view.findViewById(R.id.chat_frag_message_text);
		sendButton = (ImageButton) view.findViewById(R.id.chat_frag_send_button);
		messageListView = (ListView) view.findViewById(R.id.chat_frag_list);


		/*
		 * If Preview, hide input bar.
		 */
		if(squareHelper.isPreview()){
			inputbarLayout.setVisibility(View.GONE);
		} 
		
		
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
				AhUser myUser = userHelper.getMyUserInfo();
				// Make message and send it
				AhMessage.Builder messageBuilder = new AhMessage.Builder();
				messageBuilder.setContent(messageEditText.getText().toString())
				.setSender(myUser.getNickName())
				.setSenderId(myUser.getId())
				.setReceiverId(square.getId())
				.setType(AhMessage.TYPE.TALK);
				AhMessage sendChat = messageBuilder.build();
				sendChat(sendChat);
			}
		});
		sendButton.setEnabled(false);


		/*
		 * Set message list view
		 */
		messageListAdapter = new ChatListAdapter
				(context, thisFragment);
		messageListView.setAdapter(messageListAdapter);


		//		messageListView.setOnScrollListener(new OnScrollListener() {
		//			public void onScroll(AbsListView view, int firstVisibleItem,
		//					int visibleItemCount, int totalItemCount) {
		//				if (firstVisibleItem == 1) {
		//					// TODO : Insert messageListView.add(0, messages);
		//					offset++;
		//					final List<AhMessage> talks = messageDBHelper.getAllMessagesByFifties(offset, AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE, AhMessage.TYPE.TALK);
		//					messageList.clear();
		//					messageList.addAll(0, talks);
		//					messageListAdapter.notifyDataSetChanged();
		//					messageListView.setSelection(messageListView.getCount() - 1);
		//				}
		//			}
		//			public void onScrollStateChanged(AbsListView view, int scrollState) {
		//			}
		//		});


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
				// Chupa & Exit Square Message can't go through here
				if (message.getType().equals(AhMessage.TYPE.CHUPA.toString())
						|| message.getType().equals(AhMessage.TYPE.EXIT_SQUARE.toString())){
					return;
				}

				if(message.getType().equals((AhMessage.TYPE.UPDATE_USER_INFO.toString()))){
					refreshView(null);
					return;
				}

				refreshView(message.getId());
			}
		});
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		NotificationManager mNotificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
		refreshView(null);
	}


	@Override 
	public void onSaveInstanceState(Bundle outState) {
		//first saving my state, so the bundle wont be empty.
		outState.putString("VIEWPAGER_BUG",  "VIEWPAGER_FIX");
		super.onSaveInstanceState(outState);
	}


	@Override
	public void handleException(AhException ex) {
		if(ex.getMethodName().equals("sendMessageAsync")){
			AhMessage exMessage = (AhMessage)ex.getParameter();
			exMessage.setStatus(AhMessage.STATUS.FAIL);
			messageDBHelper.updateMessages(exMessage);
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					messageListAdapter.notifyDataSetChanged();
				}
			});
		}else{
			super.handleException(ex);	
		}
	}


	public void sendChat(final AhMessage message){
		message.setStatus(AhMessage.STATUS.SENDING);

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				messageListAdapter.add(message);
				messageListView.setSelection(messageListView.getCount() - 1);
				messageEditText.setText("");
			}
		});

		int id = messageDBHelper.addMessage(message);
		message.setId(String.valueOf(id));

		// Send message to server
		messageHelper.sendMessageAsync(thisFragment, message, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"SendChat",
						"Chat");

				message.setStatus(AhMessage.STATUS.SENT);
				message.setTimeStamp();
				messageDBHelper.updateMessages(message);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						messageListAdapter.remove(message);
						messageListAdapter.notifyDataSetChanged();
					}
				});
				refreshView(message.getId());
			}
		});
	}


	/**
	 * @author hongkunyoo
	 * notify this Method When this Fragment is on Resume
	 * so that the Message stored in MessageDBHelper can inflate to the view again
	 */
	private void refreshView(final String id){
		/*
		 * Set ENTER, EXIT, CHAT messages
		 */
		if(!squareHelper.isPreview() && messageDBHelper.isEmpty(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.TALK, AhMessage.TYPE.ADMIN_MESSAGE)) {
			AhUser myUser = userHelper.getMyUserInfo();
			String nickName = myUser.getNickName();
			String enterMessage = getResources().getString(R.string.enter_square_message);
			String greetingMessage = getResources().getString(R.string.greeting_sentence);
			AhMessage enterChat = new AhMessage.Builder()
			.setContent(" " + enterMessage + "\n" + greetingMessage)
			.setSender(nickName)
			.setSenderId(myUser.getId())
			.setReceiverId(squareHelper.getMySquareInfo().getId())
			.setType(AhMessage.TYPE.ENTER_SQUARE)
			.setStatus(AhMessage.STATUS.SENT)
			.setTimeStamp().build();
			messageDBHelper.addMessage(enterChat);
		}
		if (id == null){
			chats = messageDBHelper.getAllMessages(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.TALK, AhMessage.TYPE.ADMIN_MESSAGE);
		} else {
			chat = messageDBHelper.getMessage(Integer.parseInt(id));
		}


		/*
		 * Set message list view
		 */
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (id == null) {
					messageListAdapter.clear();
					messageListAdapter.addAll(chats);
				} else {
					messageListAdapter.add(chat);
				}
				messageListView.setSelection(messageListView.getCount() - 1);
			}
		});
	}
}
