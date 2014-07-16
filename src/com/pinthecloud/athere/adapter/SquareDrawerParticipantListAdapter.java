package com.pinthecloud.athere.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pinthecloud.athere.model.User;

public class SquareDrawerParticipantListAdapter extends ArrayAdapter<User> {

	private Context context;
	private int layoutId;
	private ArrayList<User> items;


	public SquareDrawerParticipantListAdapter(Context context, 
			int layoutId, ArrayList<User> items) {
		super(context, layoutId, items);
		this.context = context;
		this.layoutId = layoutId;
		this.items = items;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(this.layoutId, parent, false);
		}

		User user = items.get(position);
		if (user != null) {
			// TODO Ser view
		}
		return view;
	}
}
