package com.pinthecloud.athere.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.activity.SquareProfileActivity;
import com.pinthecloud.athere.helper.SquareListAdapter;
import com.pinthecloud.athere.model.Square;

public class SquareListFragment extends AhFragment{

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

	private SquareListAdapter squareListAdapter;
	private ListView squareListView;

	private ArrayList<Square> squares = new ArrayList<Square>();


	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static SquareListFragment newInstance(int sectionNumber) {
		SquareListFragment fragment = new SquareListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_list, container, false);

		/*
		 * Set UI component
		 */
		squareListView = (ListView)view.findViewById(R.id.square_list_frag_list);

		
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
				startActivity(intent);
			}
		});

		return view;
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((SquareListActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}
}
