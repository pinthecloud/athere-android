package com.pinthecloud.athere.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareChatListAdapter;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.model.AhMessage;

public class SquareChatFragment extends AhFragment{

	private ListView messageListView;
	private SquareChatListAdapter messageListSendAdapter;
	private EditText messageEditText;
	private Button sendButton;

	private ArrayList<AhMessage> messageList = new ArrayList<AhMessage>(); 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_chat, container, false);
		pref = new PreferenceHelper(context);
		
		/*
		 * Set UI component
		 */
		messageListView = (ListView) view.findViewById(R.id.square_chat_frag_list);
		messageEditText = (EditText) view.findViewById(R.id.square_chat_frag_message_text);
		sendButton = (Button) view.findViewById(R.id.square_chat_frag_send_button);


		/*
		 * Set message list view
		 */
		messageListSendAdapter = new SquareChatListAdapter
				(context, R.layout.row_square_chat_list_send, messageList);
		messageListView.setAdapter(messageListSendAdapter);
		messageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});


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
				AhMessage message = new AhMessage();
				message.setContent(messageEditText.getText().toString());
				message.setId(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
				messageList.add(message);
				messageListSendAdapter.notifyDataSetChanged();

				messageEditText.setText("");
			}
		});

		return view;
	}

}
