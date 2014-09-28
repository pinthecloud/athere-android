package com.pinthecloud.athere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.NotificationDrawerListItem;

public class NotificationDrawerListAdapter extends ArrayAdapter<NotificationDrawerListItem> {

	private Context context;


	public NotificationDrawerListAdapter(Context context) {
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

		final NotificationDrawerListItem item = this.getItem(position);
		if (item != null) {
			/*
			 * Find UI Component
			 */
			ImageView iconImage = (ImageView)view.findViewById(R.id.row_notification_drawer_list_icon);
			TextView title = (TextView)view.findViewById(R.id.row_notification_drawer_list_title);
			TextView content = (TextView)view.findViewById(R.id.row_notification_drawer_list_content);
			TextView time = (TextView) view.findViewById(R.id.row_notification_drawer_list_time);


			/*
			 * Set UI
			 */
			iconImage.setImageResource(item.getIconId());
			title.setText(item.getTitle());
			content.setText(item.getContent());
			time.setText(item.getTime());
		}
		return view;
	}
}
