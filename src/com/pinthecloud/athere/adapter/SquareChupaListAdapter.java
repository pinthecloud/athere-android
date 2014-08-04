package com.pinthecloud.athere.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SquareChupaListAdapter extends ArrayAdapter<String> {

	// TODO change ahmessage to chupa

	private Context context;
	private int layoutId;
	private ArrayList<String> items;


	public SquareChupaListAdapter(Context context, int layoutId, ArrayList<String> items) {
		super(context, layoutId, items);
		this.layoutId = layoutId;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(this.layoutId, parent, false);
		}

		String chupaId = items.get(position);
		if (chupaId != null) {

		}
		return view;
	}
}
