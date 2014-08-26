package com.pinthecloud.athere.fragment;

import java.util.List;

import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.adapter.ChupaChatListAdapter;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;

public class ChupaChatFragment extends AhFragment {

	private ActionBar mActionBar;
	private EditText messageEditText;
	private ImageButton sendButton;

	private ImageView otherProfileImage;
	private TextView otherNickName;
	private ImageView otherGender;
	private TextView otherAge;
	private TextView otherCompanyNumber;

	private AhUser otherUser;
	private boolean isOtherUserExit = false;
	private boolean isTypedMessage = false;

	private ListView messageListView;
	private ChupaChatListAdapter messageListAdapter;
	
	private List<AhMessage> chupas;
	private AhMessage chupa;
//	private ArrayList<AhMessage> messageList = new ArrayList<AhMessage>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = activity.getIntent();
		String userId = intent.getStringExtra(AhGlobalVariable.USER_KEY);
		otherUser = userDBHelper.getUser(userId, true);
		if (otherUser == null) {
			throw new AhException("No User Error");
		}
		app.setCurrentChupaUser(otherUser);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chupa_chat, container,
				false);

		// Remove Notification when the user enters the Chupa chat room.
		NotificationManager mNotificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);

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
		 * Set message listview
		 */
		messageListAdapter = new ChupaChatListAdapter(context, this);
		messageListView.setAdapter(messageListAdapter);


		/*
		 * Set other user bar
		 */
		otherProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProfileDialog profileDialog = new ProfileDialog(_thisFragment, otherUser,
						new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						Intent intent = new Intent(context,
								ChupaChatActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY,
								otherUser.getId());
						context.startActivity(intent);
						activity.finish();
					}

					@Override
					public void doNegativeThing(Bundle bundle) {
						Intent intent = new Intent(context,
								ProfileImageActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY,
								otherUser.getId());
						context.startActivity(intent);
					}
				});
				profileDialog.show(getFragmentManager(),
						AhGlobalVariable.DIALOG_KEY);
			}
		});
		otherNickName.setText(otherUser.getNickName());
		otherAge.setText("" + otherUser.getAge());
		otherCompanyNumber.setText("" + otherUser.getCompanyNum());
		Resources resources = getResources();
		if (otherUser.isMale()) {
			otherGender.setImageResource(R.drawable.profile_gender_m);
			otherCompanyNumber.setTextColor(resources.getColor(R.color.blue));
		} else {
			otherGender.setImageResource(R.drawable.profile_gender_w);
			otherCompanyNumber.setTextColor(resources
					.getColor(R.color.dark_red));
		}

		/*
		 * Set edit text
		 */
		messageEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String message = s.toString().trim();
				if (message.length() < 1) {
					isTypedMessage = false;
				} else {
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
				.setType(AhMessage.TYPE.CHUPA)
				.setStatus(AhMessage.STATUS.SENDING);
				final AhMessage sendChupa = messageBuilder.build();
				sendChupa(sendChupa);
			}
		});
		sendButton.setEnabled(false);

		/**
		 * See 1) com.pinthecloud.athere.helper.MessageEventHelper class, which
		 * is the implementation of the needed structure 2)
		 * com.pinthecloud.athere.AhIntentService class Line #47, which has the
		 * event time when to trigger
		 * 
		 * This method sets the MessageHandler received on app running
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {

				// Only Chupa & Exit Message can go through
				if (!(message.getType().equals(
						AhMessage.TYPE.CHUPA.toString()) || message
						.getType().equals(
								AhMessage.TYPE.EXIT_SQUARE.toString())))
					return;

				// If Exit Message, Check if it's related Exit (Don't go
				// through other User Exit message)
				if (message.getType().equals(
						AhMessage.TYPE.EXIT_SQUARE.toString())) {
					if (!otherUser.getId()
							.equals(message.getSenderId()))
						return;
				}

				refreshView(message.getChupaCommunId(), false);
			}
		});

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		String chupaCommunId = AhMessage.buildChupaCommunId(pref.getString(AhGlobalVariable.USER_ID_KEY), otherUser.getId());
		refreshView(chupaCommunId, true);
		blobStorageHelper.setImageViewAsync(_thisFragment, otherUser.getId(), otherProfileImage);
	}

	@Override
	public void onStop() {
		otherProfileImage.setImageBitmap(null);
		super.onStop();
	}


	public void sendChupa(final AhMessage message){
		message.setStatus(AhMessage.STATUS.SENDING);
//		messageList.add(message);
		//messageListAdapter.add(message);
//		messageListAdapter.notifyDataSetChanged();
		messageListView.setSelection(messageListView.getCount() - 1);
		messageEditText.setText("");

		int id = messageDBHelper.addMessage(message);
		message.setId("" + id);

		// Send message to server
		messageHelper.sendMessageAsync(_thisFragment, message, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				message.setStatus(AhMessage.STATUS.SENT);
				message.setTimeStamp();
				messageDBHelper.updateMessages(message);
				refreshView(message.getChupaCommunId(), false);
			}
		});
	}


	/*
	 * Set sent and received chupas to list view
	 */
	private void refreshView(String chupaCommunId, final boolean refreshAll) {
		if (chupaCommunId == null || chupaCommunId.equals(""))
			throw new AhException("No chupaCommunId");

		/*
		 * Get every chupa by chupaCommunId
		 */
		if (refreshAll) {
			chupas = messageDBHelper
					.getChupasByCommunId(chupaCommunId);
		} else {
			chupa = messageDBHelper.getLastChupaByCommunId(chupaCommunId);
		}
		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (refreshAll) {
					messageListAdapter.clear();
					messageListAdapter.addAll(chupas);
				} else {
					messageListAdapter.add(chupa);
				}
				
			}
		});
		

		/*
		 * If other user exit, add exit message
		 */
		if (userDBHelper.isUserExit(otherUser.getId())) {
			isOtherUserExit = true;
			sendButton.setEnabled(false);

			String exitMessage = getResources().getString(
					R.string.exit_square_message);
			String nickName = otherUser.getNickName();
			AhMessage.Builder messageBuilder = new AhMessage.Builder();
			messageBuilder.setContent(nickName + " " + exitMessage)
			.setSender(nickName)
			.setSenderId(otherUser.getId())
			.setReceiverId(otherUser.getSquareId())
			.setType(AhMessage.TYPE.EXIT_SQUARE)
			.setStatus(AhMessage.STATUS.SENT)
			.setTimeStamp();
			AhMessage message = messageBuilder.build();
			messageListAdapter.add(message);
		}

		/*
		 * Clear badge numbers displayed on chupa list
		 */
		messageDBHelper.clearBadgeNum(chupaCommunId);

		/*
		 * Set message listview
		 */
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
//				messageListAdapter.notifyDataSetChanged();
				messageListView.setSelection(messageListView.getCount() - 1);
			}
		});
	}

	private boolean isSenderButtonEnable() {
		return isTypedMessage && !isOtherUserExit;
	}


	@Override
	public void handleException(AhException ex) {
		if(ex.getMethodName().equals("sendMessageAsync")){
			AhMessage exMessage = (AhMessage)ex.getParameter();
			exMessage.setStatus(AhMessage.STATUS.FAIL);
//			messageListAdapter.notifyDataSetChanged();
			messageListView.setSelection(messageListView.getCount() - 1);
			messageDBHelper.updateMessages(exMessage);
			return;
		}
		super.handleException(ex);
	}
}
