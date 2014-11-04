package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.ChatListAdapter;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;

public class ChatFragment extends AhFragment{

	private RecyclerView chatListView;
	private ChatListAdapter chatListAdapter;
	private RecyclerView.LayoutManager chatListLayoutManager;
	private List<AhMessage> chatList;

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

		findComponent(view);
		setEditText();
		setButtonEvent();
		setChatList();
		setMessageHandler();

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();

		NotificationManager mNotificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);

		if(messageDBHelper.isEmpty(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.TALK, AhMessage.TYPE.ADMIN_MESSAGE)) {
			showWelcomeChat();
		}
		updateChatList();
	}


	@Override 
	public void onSaveInstanceState(Bundle outState) {
		//first saving my state, so the bundle wont be empty.
		outState.putString("VIEWPAGER_BUG_FIX",  "VIEWPAGER_BUG_FIX");
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
					chatListAdapter.notifyDataSetChanged();
				}
			});
		}else{
			super.handleException(ex);	
		}
	}


	private void findComponent(View view){
		chatListView = (RecyclerView) view.findViewById(R.id.chat_frag_list);
		messageEditText = (EditText) view.findViewById(R.id.chat_frag_message_text);
		sendButton = (ImageButton) view.findViewById(R.id.chat_frag_send_button);
	}


	private void setEditText(){
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
	}


	private void setChatList(){
		chatListView.setHasFixedSize(true);

		chatListLayoutManager = new LinearLayoutManager(context);
		chatListView.setLayoutManager(chatListLayoutManager);

		chatList = new ArrayList<AhMessage>();
		chatListAdapter = new ChatListAdapter(context, thisFragment, chatList);
		chatListView.setAdapter(chatListAdapter);


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
	}


	private void setButtonEvent(){
		sendButton.setEnabled(false);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AhUser myUser = userHelper.getMyUserInfo();

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
	}


	private void setMessageHandler(){
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {
				// Chupa & Exit Square Message can't go through here
				if (message.getType().equals(AhMessage.TYPE.CHUPA.toString())
						|| message.getType().equals(AhMessage.TYPE.EXIT_SQUARE.toString())){
					return;
				}

				if(message.getType().equals((AhMessage.TYPE.UPDATE_USER_INFO.toString()))){
					updateChatList();
					return;
				}

				chatList.add(message);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						insertChat(message);
					}
				});
			}
		});
	}


	private void showWelcomeChat(){
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


	private void updateChatList(){
		chatList.clear();
		chatList.addAll(messageDBHelper.getAllMessages(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.TALK, AhMessage.TYPE.ADMIN_MESSAGE));
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chatListAdapter.notifyDataSetChanged();
				chatListView.scrollToPosition(chatList.size()-1);
			}
		});
	}


	public void sendChat(final AhMessage chat){
		chat.setStatus(AhMessage.STATUS.SENDING);
		chatList.add(chat);
		int id = messageDBHelper.addMessage(chat);
		chat.setId(String.valueOf(id));

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				messageEditText.setText("");
				insertChat(chat);
			}
		});

		messageHelper.sendMessageAsync(thisFragment, chat, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"SendChat",
						"Chat");

				chat.setStatus(AhMessage.STATUS.SENT);
				chat.setTimeStamp();
				messageDBHelper.updateMessages(chat);

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						chatListAdapter.notifyDataSetChanged();
					}
				});
			}
		});
	}


	private void insertChat(AhMessage chat){
		chatListAdapter.notifyItemInserted(chatList.indexOf(chat));
		chatListView.scrollToPosition(chatList.size()-1);
	}
}
