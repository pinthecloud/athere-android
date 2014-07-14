package com.pinthecloud.athere.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.Square;

public class SquareListAdapter extends ArrayAdapter<Square>{

	private Context context;
	private int layoutId;
	private ArrayList<Square> items;


	public SquareListAdapter(Context context, int layoutId, ArrayList<Square> items) {
		super(context, layoutId, items);
		this.context = context;
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

		Square square = items.get(position);
		if (square != null) {
			/*
			 * Find UI component
			 */
			TextView squareNameText = (TextView)view.findViewById(R.id.row_square_list_name);


			/*
			 * Set UI component
			 */
			squareNameText.setText(square.getName());
		}
		return view;
	}
}