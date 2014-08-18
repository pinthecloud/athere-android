package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChupaChatFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;

public class ChupaChatActivity extends AhActivity {
	
	MessageHelper messageHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chupa_chat);

		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		final ChupaChatFragment chupaChatFragment = new ChupaChatFragment();
		fragmentTransaction.add(R.id.chupa_chat_container, chupaChatFragment);
		fragmentTransaction.commit();
		
		messageHelper = app.getMessageHelper();
		
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {
			
			@Override
			public void onCompleted(AhMessage message) {
				// TODO Auto-generated method stub
				messageHelper.triggerMessageEvent(chupaChatFragment, message);
			}
		});
	}
}
