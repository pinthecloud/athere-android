package com.pinthecloud.athere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.AppDrawerListItem;

public class AppDrawerListAdapter extends ArrayAdapter<AppDrawerListItem> {

	private Context context;


	public AppDrawerListAdapter(Context context) {
		super(context, 0);
		this.context = context;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_app_drawer_list, parent, false);
		}

		final AppDrawerListItem item = this.getItem(position);
		if (item != null) {
			/*
			 * Find UI Component
			 */
			TextView title = (TextView)view.findViewById(R.id.row_app_drawer_list_title);


			/*
			 * Set UI
			 */
			title.setCompoundDrawablesWithIntrinsicBounds(item.getIconId(), 0,0,0);
			title.setText(item.getTitle());
		}
		return view;
	}
}
