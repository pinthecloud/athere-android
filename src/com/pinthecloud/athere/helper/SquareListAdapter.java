package com.pinthecloud.athere.helper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.Square;

public class SquareListAdapter extends ArrayAdapter<Square>{
	
	private Context context;
	private ArrayList<Square> items;

	
	public SquareListAdapter(Context context, int layoutId, ArrayList<Square> items) {
		super(context, layoutId, items);
		this.context = context;
		this.items = items;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_square_list, parent, false);
		}
		
		Square square = items.get(position);
		if (square != null) {
			// TODO Set view
		}
		return view;
	}
}
