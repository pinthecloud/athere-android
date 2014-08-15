package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.ChupaChatListAdapter;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;
import com.pinthecloud.athere.util.BitmapUtil;


public class ChupaChatFragment extends AhFragment {

	private ActionBar mActionBar;
	private EditText messageEditText;
	private ImageButton sendButton;

	private ImageView otherProfileImage;
	private TextView otherNickName;
	private ImageView otherGender;
	private TextView otherAge;
	private TextView otherCompanyNumber;

	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;
	private UserDBHelper userDBHelper;

	private User otherUser;
	private boolean isOtherUserExit = false;
	private boolean isTypedMessage = false;

	private ListView messageListView;
	private ChupaChatListAdapter messageListAdapter;
	private ArrayList<AhMessage> messageList = new ArrayList<AhMessage>(); 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(AhGlobalVariable.LOG_TAG, "ChupaChatFragment onCreate");

		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userDBHelper = app.getUserDBHelper();

		Intent intent = activity.getIntent();
		String userId = intent.getStringExtra(AhGlobalVariable.USER_KEY);
		otherUser = userDBHelper.getUser(userId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chupa_chat, container, false);

		/*
		 * Set UI component
		 */
		mActionBar = activity.getActionBar();
		otherProfileImage = (ImageView) view.findViewById(R.id.chupa_chat_frag_other_profile);
		otherNickName = (TextView) view.findViewById(R.id.chupa_chat_frag_other_nick_name);
		otherGender = (ImageView) view.findViewById(R.id.chupa_chat_frag_other_gender);
		otherAge = (TextView) view.findViewById(R.id.chupa_chat_frag_other_age);
		otherCompanyNumber = (TextView) view.findViewById(R.id.chupa_chat_frag_other_company_number);
		messageListView = (ListView) view.findViewById(R.id.chupa_chat_frag_list);
		messageEditText = (EditText) view.findViewById(R.id.chupa_chat_frag_message_text);
		sendButton = (ImageButton) view.findViewById(R.id.chupa_chat_frag_send_button);


		/*
		 * Set Action Bar
		 */
		mActionBar.setTitle(pref.getString(AhGlobalVariable.SQUARE_NAME_KEY));


		/*
		 * Set other bar
		 */
		Bitmap profile = BitmapUtil.cropRound(BitmapUtil.convertToBitmap(otherUser.getProfilePic()));
		otherProfileImage.setImageBitmap(profile);
		otherNickName.setText(otherUser.getNickName());
		otherAge.setText("" + otherUser.getAge());
		otherCompanyNumber.setText("" + otherUser.getCompanyNum());
		if(otherUser.isMale()){
			otherGender.setImageResource(R.drawable.chupa_gender_m);
		}else{
			otherGender.setImageResource(R.drawable.chupa_gender_w);
		}


		/*
		 * Set message list view
		 */
		messageListAdapter = new ChupaChatListAdapter
				(context, R.layout.row_square_chat_list_send, messageList);
		messageListView.setAdapter(messageListAdapter);

		/*
		 * Set sent and received chupas to list view 
		 */
		String chupaCommunId = AhMessage.buildChupaCommunId(pref.getString(AhGlobalVariable.USER_ID_KEY), otherUser.getId());

		if(chupaCommunId == null || "".equals(chupaCommunId))
			throw new AhException("No chupaCommunId");

		// Clear badge numbers displayed on chupa list
		messageDBHelper.clearBadgeNum(chupaCommunId);

		// Get every chupa by chupaCommunId
		final List<AhMessage> chupas = messageDBHelper.getChupasByCommunId(chupaCommunId);
		for (AhMessage message : chupas) {
			message.setStatus(AhMessage.SENT);
			messageList.add(message);
		}
		messageListAdapter.notifyDataSetChanged();
		messageListView.setSelection(messageListView.getCount() - 1);


		/*
		 * If other user exit, add exit message
		 */
		if (userDBHelper.isUserExit(otherUser.getId())){
			isOtherUserExit = true;

			String exitMessage = getResources().getString(R.string.exit_square_message);
			String nickName = otherUser.getNickName();
			AhMessage.Builder messageBuilder = new AhMessage.Builder();
			messageBuilder.setContent(nickName + " " + exitMessage)
			.setSender(nickName)
			.setSenderId(otherUser.getId())
			.setReceiverId(otherUser.getSquareId())
			.setType(AhMessage.TYPE.EXIT_SQUARE);
			AhMessage message = messageBuilder.build();

			messageList.add(message);
			messageListAdapter.notifyDataSetChanged();
			messageListView.setSelection(messageListView.getCount() - 1);
		}


		/*
		 * Set edit text
		 */
		messageEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String message = s.toString().trim();
				if(message.length() < 1){
					isTypedMessage = false;
				}else{
					isTypedMessage = true;
				}
				sendButton.setEnabled(isSenderButtonEnable());
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
		 * Set event on button
		 */
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Make message
				AhMessage.Builder messageBuilder = new AhMessage.Builder();
				messageBuilder.setContent(messageEditText.getText().toString())
				.setSender(pref.getString(AhGlobalVariable.NICK_NAME_KEY))
				.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
				.setReceiver(otherUser.getNickName())
				.setReceiverId(otherUser.getId())
				.setType(AhMessage.TYPE.CHUPA);
				final AhMessage message = messageBuilder.build();
				message.setStatus(AhMessage.SENDING);

				messageList.add(message);
				messageListAdapter.notifyDataSetChanged();
				messageListView.setSelection(messageListView.getCount() - 1);
				messageEditText.setText("");

				// Send message to server
				messageHelper.sendMessageAsync(_thisFragment,message, new AhEntityCallback<AhMessage>() {

					@Override
					public void onCompleted(AhMessage entity) {
						message.setStatus(AhMessage.SENT);
						messageListAdapter.notifyDataSetChanged();
						messageDBHelper.addMessage(message);
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
		messageHelper.setMessageHandler(new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {
				
				messageList.add(message);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						messageListAdapter.notifyDataSetChanged();
						messageListView.setSelection(messageListView.getCount() - 1);
						if(message.getType().equals(AhMessage.TYPE.EXIT_SQUARE.toString())){
							isOtherUserExit = true;
							sendButton.setEnabled(false);
						}
					}
				});
			}
		});

		return view;
	}


	private boolean isSenderButtonEnable(){
		return isTypedMessage && !isOtherUserExit;
	}
}
