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

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.AhApplication.TrackerName;
import com.pinthecloud.athere.adapter.SquareChatListAdapter;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SquareChatFragment extends AhFragment{

	private EditText messageEditText;
	private ImageButton sendButton;

	private ListView messageListView;
	private SquareChatListAdapter messageListAdapter;
	private String squareId;

	private List<AhMessage> talks;
	private AhMessage talk;
	
	Tracker t;
	
	public SquareChatFragment() {
		super();
	}

	public SquareChatFragment(String squareId) {
		super();
		this.squareId = squareId;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* 
		 * for google analytics
		 */
		GoogleAnalytics.getInstance(getActivity().getApplication()).newTracker("UA-53944359-1");

        if (t==null){
            t = ((AhApplication) getActivity().getApplication()).getTracker(
                    AhApplication.TrackerName.APP_TRACKER);

            t.setScreenName("SquareChatFragment");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
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
				refreshView(message.getId());
			}
		});
//		refreshView(true);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		Log.d(AhGlobalVariable.LOG_TAG, "SquareChatFragment onStart");
		refreshView(null);
		
		GoogleAnalytics.getInstance(getActivity().getApplication()).reportActivityStart(getActivity());
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		GoogleAnalytics.getInstance(getActivity().getApplication()).reportActivityStop(getActivity());
	}
	

	public void sendTalk(final AhMessage message){
		message.setStatus(AhMessage.STATUS.SENDING);
		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				messageListAdapter.add(message);
//				messageListAdapter.notifyDataSetChanged();
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
				/*
				 * Check Chat
				 */
				Tracker t = ((AhApplication) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
				t.send(new HitBuilders.EventBuilder()
				.setCategory("SquareChatFragment")
				.setAction("SendChat")
				.setLabel("Chat")
				.build());
				messageDBHelper.updateMessages(message);
//				messageListAdapter.notifyDataSetChanged();
//				messageListView.setSelection(messageListView.getCount() - 1);
				messageListAdapter.remove(message);
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
		Log.d(AhGlobalVariable.LOG_TAG, "SquareChatFragment refreshView");
		/*
		 * Set ENTER, EXIT, TALK messages
		 */
		if(messageDBHelper.isEmpty(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE, AhMessage.TYPE.TALK)) {
			String nickName = pref.getString(AhGlobalVariable.NICK_NAME_KEY);
			String enterMessage = getResources().getString(R.string.enter_square_message);
			String warningMessage = getResources().getString(R.string.behave_well_warning);
			AhMessage enterTalk = new AhMessage.Builder()
			.setContent(nickName + " " + enterMessage + "\n" + warningMessage)
			.setSender(nickName)
			.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
			.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY))
			.setType(AhMessage.TYPE.ENTER_SQUARE)
			.setStatus(AhMessage.STATUS.SENT)
			.setTimeStamp().build();
			messageDBHelper.addMessage(enterTalk);
		}
		
		if (id == null){
			talks = messageDBHelper.getAllMessages(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE, AhMessage.TYPE.TALK);
		} else {
			int _id = Integer.valueOf(id);
//			talk = messageDBHelper.getLastMessage(AhMessage.TYPE.ENTER_SQUARE, AhMessage.TYPE.EXIT_SQUARE, AhMessage.TYPE.TALK);
			talk = messageDBHelper.getMessage(_id);
		}
		
		/*
		 * Set message list view
		 */
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (id == null) {
					messageListAdapter.clear();
					messageListAdapter.addAll(talks);
				} else {
					messageListAdapter.add(talk);
				}
				
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
			messageDBHelper.updateMessages(exMessage);
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					messageListAdapter.notifyDataSetChanged();
					messageListView.setSelection(messageListView.getCount() - 1);
				}
			});
			
			return;
		}
		super.handleException(ex);
	}

}
