package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.adapter.SquareListAdapter;
import com.pinthecloud.athere.dialog.SquareEnterDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.interfaces.AhPairEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.AsyncChainer.Chainable;



public class SquareListFragment extends AhFragment {

	private ProgressBar progressBar;
	private RecyclerView squareListView;
	private SquareListAdapter squareListAdapter;
	private RecyclerView.LayoutManager squareListLayoutManager;
	private List<Square> squareList;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_square_list, container, false);

		findComponent(view);
		setSquareList();
		updateSquareList();

		return view;
	}


	private void findComponent(View view){
		progressBar = (ProgressBar)view.findViewById(R.id.square_list_frag_progress_bar);
		squareListView = (RecyclerView)view.findViewById(R.id.square_list_frag_list);
	}


	private void setSquareList(){
		squareListView.setHasFixedSize(true);

		squareListLayoutManager = new LinearLayoutManager(context);
		squareListView.setLayoutManager(squareListLayoutManager);

		squareList = new ArrayList<Square>();
		squareListAdapter = new SquareListAdapter(thisFragment, squareList, new SquareListViewItemClickListener());
		squareListView.setAdapter(squareListAdapter);
	}


	private void updateSquareList(){
		squareHelper.getAllSquareList(thisFragment, new AhListCallback<Square>() {

			@Override
			public void onCompleted(List<Square> list, int count) {
				progressBar.setVisibility(View.GONE);

				Collections.sort(list, new Comparator<Square>(){

					@Override
					public int compare(Square lhs, Square rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}
				});
				Collections.sort(list, new Comparator<Square>(){

					@Override
					public int compare(Square lhs, Square rhs) {
						return lhs.isAdmin() == rhs.isAdmin() ? 0 : 
							!lhs.isAdmin() ? 1 : -1;
					}
				});

				squareList.clear();
				squareList.addAll(list);
				squareListAdapter.notifyDataSetChanged();
			}
		});
	}


	private void enterSquare(final Square square){
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();
		squareListView.setEnabled(false);

		AsyncChainer.asyncChain(thisFragment, new Chainable(){

			@Override
			public void doNext(AhFragment frag) {
				final AhUser user = userHelper.getMyUserInfo();
				userHelper.enterSquareAsync(frag, user, square.getId(), new AhPairEntityCallback<String, List<AhUser>>() {

					@Override
					public void onCompleted(String userId, List<AhUser> list) {
						userDBHelper.addAllUsers(list);
					}
				});
			}
		}, new Chainable() {

			@Override
			public void doNext(AhFragment frag) {
				String enterMessage = getResources().getString(R.string.enter_square_message);
				String greetingMessage = getResources().getString(R.string.greeting_sentence);
				AhUser user = userHelper.getMyUserInfo();
				AhMessage message = new AhMessage.Builder()
				.setContent(" " + enterMessage + "\n" + greetingMessage)
				.setSender(user.getNickName())
				.setSenderId(user.getId())
				.setReceiverId(square.getId())
				.setType(AhMessage.TYPE.ENTER_SQUARE).build();
				messageHelper.sendMessageAsync(frag, message, null);
			}
		}, new Chainable(){

			@Override
			public void doNext(AhFragment frag) {
				progressBar.setVisibility(View.GONE);
				squareListView.setEnabled(true);
				
				Time time = new Time();
				time.setToNow();

				squareHelper.setMySquareId(square.getId())
				.setMySquareName(square.getName())
				.setMySquareResetTime(square.getResetTime())
				.setSquareExitTab(SquareTabFragment.CHAT_TAB)
				.setLoggedInSquare(true)
				.setTimeStampAtLoggedInSquare(time.format("%Y:%m:%d:%H"))
				.setReview(true);

				Intent intent = new Intent();
				intent.setClass(context, SquareActivity.class);
				startActivity(intent);
				activity.finish();
			}
		});
	}


	private class SquareListViewItemClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			int position = squareListView.getChildPosition(v);
			final Square square = squareList.get(position);
			if(square.getCode().equals("")){
				enterSquare(square);
			} else{
				SquareEnterDialog enterDialog = new SquareEnterDialog(square, new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						enterSquare(square);
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
						// Do nothing
					}
				});
				enterDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		}
	}
}
