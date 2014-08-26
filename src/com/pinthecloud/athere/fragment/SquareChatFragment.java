package com.pinthecloud.athere.fragment;

import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

public class SquareChatFragment extends AhFragment{

	private EditText messageEditText;
	private ImageButton sendButton;

	private ListView messageListView;
	private SquareChatListAdapter messageListAdapter;
//	private List<AhMessage> messageList = new ArrayList<AhMessage>();

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
				.setType(AhMessage.TYPE.TALK);
				AhMessage sendTalk = messageBuilder.build();
				sendTalk(sendTalk);
			}
		});
		sendButton.setEnabled(false);


		/*
		 * Set message list view
		 */
		messageListAdapter = new SquareChatListAdapter
				(context, _thisFragment);
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
				Log.d(AhGlobalVariable.LOG_TAG, "SquareChatFragment Message onComplete : " + message.getType() + " " + message.getContent());
				
				// Chupa & User Update Message can't go through here
				if (message.getType().equals(AhMessage.TYPE.CHUPA.toString())
						|| message.getType().equals(AhMessage.TYPE.UPDATE_USER_INFO.toString())){
					return;
				}
				refreshView();
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


	public void sendTalk(final AhMessage message){
		message.setStatus(AhMessage.STATUS.SENDING);
//		messageList.add(message);
		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				messageListAdapter.add(message);
				messageListAdapter.notifyDataSetChanged();
				messageListView.setSelection(messageListView.getCount() - 1);
				messageEditText.setText("");
			}
		});

		int id = messageDBHelper.addMessage(message);
		message.setId(String.valueOf(id));

		// Send message to server
		messageHelper.sendMessageAsync(_thisFragment, message, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				message.setStatus(AhMessage.STATUS.SENT);
				message.setTimeStamp();
				messageDBHelper.updateMessages(message);
				refreshView();
			}
		});
	}


	/**
	 * @author hongkunyoo
	 * notify this Method When this Fragment is on Resume
	 * so that the Message stored in MessageDBHelper can inflate to the view again
	 */
	private void refreshView(){
		Log.d(AhGlobalVariable.LOG_TAG, "SquareChatFragment refreshView");
		
		/*
		 * Set ENTER, EXIT, TALK messages
		 */
		if(messageDBHelper.isEmpty(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE, AhMessage.TYPE.TALK)) {
			String nickName = pref.getString(AhGlobalVariable.NICK_NAME_KEY);
			String enterMessage = getResources().getString(R.string.enter_square_message);
			AhMessage enterTalk = new AhMessage.Builder()
			.setContent(nickName + " " + enterMessage)
			.setSender(nickName)
			.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
			.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY))
			.setType(AhMessage.TYPE.ENTER_SQUARE)
			.setStatus(AhMessage.STATUS.SENT)
			.setTimeStamp().build();
			Log(_thisFragment, enterTalk);
			messageDBHelper.addMessage(enterTalk);
		}
		final List<AhMessage> talks = messageDBHelper.getAllMessages(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE, AhMessage.TYPE.TALK);
//		messageList.clear();
//		messageList.addAll(talks);
		

		/*
		 * Set message list view
		 */
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				messageListAdapter.clear();
				messageListAdapter.addAll(talks);
				messageListAdapter.notifyDataSetChanged();
				messageListView.setSelection(messageListView.getCount() - 1);
			}
		});
	}


	@Override
	public void handleException(AhException ex) {
		if(ex.getMethodName().equals("sendMessageAsync")){
			AhMessage exMessage = (AhMessage)ex.getParameter();
			exMessage.setStatus(AhMessage.STATUS.FAIL);
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					messageListAdapter.notifyDataSetChanged();
					messageListView.setSelection(messageListView.getCount() - 1);
				}
			});
			messageDBHelper.updateMessages(exMessage);
			return;
		}
		super.handleException(ex);
	}
	
}
