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

import com.pinthecloud.athere.AhGlobal;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.helper.PrefHelper;

public class ProfileFragment extends Fragment{

	private PrefHelper pref;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		
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
				// can't go to next activity
				
				// Remove blank of along side.
				String nickName = nickNameEditText.getText().toString().trim();
				nickNameEditText.setText(nickName);
				nickNameEditText.setSelection(nickName.length());
				
				// Set regular expression and check nick name
				String nickNameRegx = "^[a-zA-Z0-9가-힣_-]{2,15}$";
				String message = "";

				if(nickName.length() < 3){
					message = getResources().getString(R.string.min_nick_name_message);
				}
				else if(!nickName.matches(nickNameRegx)){
					message = getResources().getString(R.string.bad_nick_name_message);
				}
				else if(nickName.length() > 15){
					message = getResources().getString(R.string.max_nick_name_message);
				}

				if(!message.equals("")){
					Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				else{
					pref = new PrefHelper(getActivity());
					pref.putString(AhGlobal.NICK_NAME_KEY, nickName);
//					pref.putBoolean(AtHereGlobal.IS_LOGGED_IN_USER_KEY, true);
					
					Intent intent = new Intent(getActivity(), SquareListActivity.class);
					startActivity(intent);
					getActivity().finish();
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
