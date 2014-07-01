package com.pinthecloud.athere.fragment;

import java.util.Random;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;

public class ProfileFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile, container,
				false);
		
		/*
		 * Find UI component
		 */
		Button startButton = (Button)view.findViewById(R.id.profile_frag_start_button);
		final EditText nickNameEditText = (EditText)view.findViewById(R.id.profile_frag_nick_name_text);
		
		
        /*
         * Set event on button
         */
        startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// If user doesn't type in proper nick name,
				// can't go to next activity.
				String nickName = nickNameEditText.getText().toString().trim();
				nickNameEditText.setText(nickName);
		        nickNameEditText.setSelection(nickName.length());
		        
				if(nickName.equals("")){
					String message = getResources().getString(R.string.no_nick_name_message);
					Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				else{
					Intent intent = new Intent(getActivity(), SquareListActivity.class);
					startActivity(intent);
				}
			}
		});
        
        
        /*
         * Set Edit Text
         */
        String[] nickNameArray = getResources().getStringArray(R.array.nick_name_string_array);
        Random random = new Random();
        String nickName = nickNameArray[random.nextInt(nickNameArray.length)];
        nickNameEditText.setText(nickName);
        nickNameEditText.setSelection(nickName.length());
        
		return view;
	}

}
