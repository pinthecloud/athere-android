package com.pinthecloud.athere.adapter;

import java.util.List;

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
	private List<Square> items;


	public SquareListAdapter(Context context, int layoutId, List<Square> items) {
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
			TextView maleNumText = (TextView)view.findViewById(R.id.row_square_list_male_number);
			TextView femaleNumText = (TextView)view.findViewById(R.id.row_square_list_female_number);

			
			/*
			 * Set UI component
			 */
			squareNameText.setText(square.getName());
			maleNumText.setText(""+square.getMaleNum());
			femaleNumText.setText(""+square.getFemaleNum());
		}
		return view;
	}
}
