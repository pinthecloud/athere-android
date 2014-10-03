package com.pinthecloud.athere.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.adapter.MemberGridAdapter;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhUser;

public class MemberFragment extends AhFragment{

	private PullToRefreshGridView pullToRefreshGridView;
	private GridView memberGridView;
	private MemberGridAdapter memberGridAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_member, container, false);


		/*
		 * Find UI component
		 */
		pullToRefreshGridView = (PullToRefreshGridView)view.findViewById(R.id.member_frag_grid);
		memberGridView = pullToRefreshGridView.getRefreshableView();
		registerForContextMenu(memberGridView);


		/*
		 * Set member grid view
		 */
		memberGridAdapter = new MemberGridAdapter(context, thisFragment);
		memberGridView.setAdapter(memberGridAdapter);
		memberGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"ViewOtherProfile",
						"Member");

				final AhUser user = memberGridAdapter.getItem(position);
				ProfileDialog profileDialog = new ProfileDialog(thisFragment, user, new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						gaHelper.sendEventGA(
								thisFragment.getClass().getSimpleName(),
								"SendChupa",
								"Member");

						Intent intent = new Intent(context, ChupaChatActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY, user);
						context.startActivity(intent);
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
						Intent intent = new Intent(context, ProfileImageActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY, user);
						context.startActivity(intent);
					}
				});
				profileDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});
		pullToRefreshGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {

			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				refreshView();
				refreshView.onRefreshComplete();
			}
		});

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		refreshView();
	}


	@Override 
	public void onSaveInstanceState(Bundle outState) {
		//first saving my state, so the bundle wont be empty.
		outState.putString("VIEWPAGER_BUG_FIX",  "VIEWPAGER_BUG_FIX");
		super.onSaveInstanceState(outState);
	}


	private void refreshView() {
		/*
		 * Set member grid
		 */
		final List<AhUser> userList = userDBHelper.getAllUsers(false);
		userList.add(0, userHelper.getMyUserInfo());
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				memberGridAdapter.clear();
				memberGridAdapter.addAll(userList);
			}
		});
	}
}
