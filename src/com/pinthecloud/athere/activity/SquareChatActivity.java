package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SquareChatFragment;

public class SquareChatActivity extends AhActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_chat);
		
		/*
		 * Set Fragment to container
		 */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        SquareChatFragment squareChatFragment = new SquareChatFragment();
        fragmentTransaction.add(R.id.square_chat_container, squareChatFragment);
        fragmentTransaction.commit();
	}
}
