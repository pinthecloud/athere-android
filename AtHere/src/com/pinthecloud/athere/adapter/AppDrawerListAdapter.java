package com.pinthecloud.athere.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.AhActivity;
import com.pinthecloud.athere.activity.SettingsActivity;
import com.pinthecloud.athere.analysis.GAHelper;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.AppDrawerListItem;

public class AppDrawerListAdapter extends RecyclerView.Adapter<AppDrawerListAdapter.ViewHolder> {

	private enum TYPE{
		SETTINGS,
		SHARE,
		QUESTION
	}

	private Context context;
	private AhActivity activity;
	private AhFragment frag;
	private AhUser user;
	private List<AppDrawerListItem> appDrawerList;
	private GAHelper gaHelper;


	public AppDrawerListAdapter(Context context, AhFragment frag, AhUser user, List<AppDrawerListItem> appDrawerList) {
		this.context = context;
		this.activity = (AhActivity)context;
		this.frag = frag;
		this.user = user;
		this.appDrawerList = appDrawerList;
		this.gaHelper = AhApplication.getInstance().getGAHelper();
	}


	public static class ViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView icon;
		public TextView title;

		public ViewHolder(View view) {
			super(view);
			this.view = view;
			this.icon = (ImageView)view.findViewById(R.id.row_app_drawer_list_icon);
			this.title = (TextView)view.findViewById(R.id.row_app_drawer_list_title);
		}
	}


	@Override
	public AppDrawerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_app_drawer_list, parent, false);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		AppDrawerListItem item = appDrawerList.get(position);
		holder.icon.setImageResource(item.getIconId());
		holder.title.setText(item.getTitle());
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = null;
				if(position == TYPE.QUESTION.ordinal()){
					gaHelper.sendEventGA(
							frag.getClass().getSimpleName(),
							"Question",
							"DrawerQuestion");

					intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:pinthecloud@gmail.com"));
					activity.startActivity(intent);
				} else if(position == TYPE.SHARE.ordinal()){
					gaHelper.sendEventGA(
							frag.getClass().getSimpleName(),
							"Share",
							"DrawerShare");

					intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getText(R.string.share_sentence));
					intent.setType("text/plain");
					activity.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.share_to)));
				} else if(position == TYPE.SETTINGS.ordinal()){
					gaHelper.sendEventGA(
							frag.getClass().getSimpleName(),
							"Settings",
							"DrawerSettings");

					intent = new Intent(context, SettingsActivity.class);
					intent.putExtra(AhGlobalVariable.USER_KEY, user);
					activity.startActivity(intent);
				}
			}
		});
	}


	@Override
	public int getItemCount() {
		return this.appDrawerList.size();
	}
}
