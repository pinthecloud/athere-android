package com.pinthecloud.athere.fragment;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareProfileActivity;
import com.pinthecloud.athere.adapter.SquareListAdapter;
import com.pinthecloud.athere.dialog.SquareCodeDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.Square;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders;

public class SquareListFragment extends AhFragment{

	private ActionBar mActionBar;
	private ProgressBar mProgressBar;

	private ListView squareListView;
	private SquareListAdapter squareListAdapter;

	private Tracker t;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		/* 
		 * for google analytics
		 */
		GoogleAnalytics.getInstance(app).newTracker(AhGlobalVariable.GA_TRACKER_KEY);
        if (t==null){
            t = app.getTracker(AhApplication.TrackerName.APP_TRACKER);
            t.setScreenName("SquareListFragment");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_list, container, false);

		/*
		 * Set UI component
		 */
		mActionBar = activity.getActionBar();
		squareListView = (ListView)view.findViewById(R.id.square_list_frag_list);
		mProgressBar = (ProgressBar)view.findViewById(R.id.square_list_frag_progress_bar);


		/*
		 * Set Action Bar
		 */
		mActionBar.setTitle(pref.getString(AhGlobalVariable.NICK_NAME_KEY));


		/*
		 * Set square list view
		 */
		squareListAdapter = new SquareListAdapter(context, R.layout.row_square_list);
		squareListView.setAdapter(squareListAdapter);
		squareListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Square square = squareListAdapter.getItem(position);
				if(square.getCode().equals("")){
					Intent intent = new Intent(context, SquareProfileActivity.class);
					intent.putExtra(AhGlobalVariable.SQUARE_KEY, square);
					startActivity(intent);
				} else{
					SquareCodeDialog codeDialog = new SquareCodeDialog(square, new AhDialogCallback() {

						@Override
						public void doPositiveThing(Bundle bundle) {
							String code = bundle.getString(AhGlobalVariable.CODE_VALUE_KEY);
							if(code.equals(square.getCode())){
								Intent intent = new Intent(context, SquareProfileActivity.class);
								intent.putExtra(AhGlobalVariable.SQUARE_KEY, square);
								startActivity(intent);
							}else{
								String message = getResources().getString(R.string.bad_square_code_message);
								Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
								toast.show();
							}
						}
						@Override
						public void doNegativeThing(Bundle bundle) {
							// do nothing		
						}
					});
					codeDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
				}
			}
		});
		getNearSquares();

		return view;
	}


	/*
	 * Get square near from user
	 * Now it just gets all squares cause of location law. (lati and longi is 0)
	 */
	private void getNearSquares(){
		mProgressBar.setVisibility(View.VISIBLE);
		squareHelper.getSquareListAsync(_thisFragment, 0, 0, new AhListCallback<Square>() {

			@Override
			public void onCompleted(List<Square> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				squareListAdapter.clear();
				squareListAdapter.addAll(list);
			}
		});
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(app).reportActivityStart(activity);
	}
	
	
	@Override
	public void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(app).reportActivityStop(activity);
	}
}
