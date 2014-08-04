package com.pinthecloud.athere.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareChupaListAdapter;
import com.pinthecloud.athere.model.Square;

public class SquareChupaListFragment extends AhFragment{

	private Square square;

	private SquareChupaListAdapter squareChupaListAdapter;
	private ListView squareChupaListView;

	private ArrayList<String> chupaIds = new ArrayList<String>();


	public SquareChupaListFragment(Square square) {
		super();
		this.square = square;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_chupa_list, container, false);

		/*
		 * Set UI component
		 */
		squareChupaListView = (ListView)view.findViewById(R.id.square_chupa_list_frag_list);

		/*
		 * Set square chupa list view
		 */
		squareChupaListAdapter = new SquareChupaListAdapter(context, R.layout.row_square_chupa_list, chupaIds);
		squareChupaListView.setAdapter(squareChupaListAdapter);
		squareChupaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		getChupas();

		return view;
	}


	/*
	 * Get square near from user
	 * Now it just gets all squares cause of location law. (lati and longi is 0)
	 */
	private void getChupas(){
	}
}
