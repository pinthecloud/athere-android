package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareProfileActivity;
import com.pinthecloud.athere.adapter.SquareListAdapter;
import com.pinthecloud.athere.helper.LocationHelper;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.Square;

public class SquareListFragment extends AhFragment{

	private SquareListAdapter squareListAdapter;
	private ListView squareListView;
	private ProgressBar mProgressBar;

	private ArrayList<Square> squares = new ArrayList<Square>();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_list, container, false);

		/*
		 * Set UI component
		 */
		squareListView = (ListView)view.findViewById(R.id.square_list_frag_list);
		mProgressBar = (ProgressBar)view.findViewById(R.id.square_list_frag_progress_bar);


		/*
		 * Set temp square items
		 */
		for(int i=0 ; i<3 ; i++){
			Square square = new Square();
			square.setName("Example" + i);
			squares.add(square);	
		}


		/*
		 * Set square list view
		 */
		squareListAdapter = new SquareListAdapter(context, R.layout.row_square_list, squares);
		squareListView.setAdapter(squareListAdapter);
		squareListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(context, SquareProfileActivity.class);
				intent.putExtra(AhGlobalVariable.SQUARE_KEY, squares.get(position));
				startActivity(intent);
			}
		});
		getNearSquare();

		return view;
	}


	private void getNearSquare(){
		mProgressBar.setVisibility(View.VISIBLE);

		LocationHelper mLocationHelper = new LocationHelper(context);
		Location loc = mLocationHelper.getLocation();
		serviceClient.getSquareListAsync(loc, new AhListCallback<Square>() {

			@Override
			public void onCompleted(List<Square> list, int count) {
				squares.addAll(list);
				squareListAdapter.notifyDataSetChanged();
				mProgressBar.setVisibility(View.GONE);
			}
		});
	}
}
