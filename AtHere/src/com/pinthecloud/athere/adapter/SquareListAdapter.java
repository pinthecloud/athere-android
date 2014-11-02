package com.pinthecloud.athere.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.model.Square;

public class SquareListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum TYPE{
		NORMAL,
		ADMIN
	}

	private AhFragment frag;
	private List<Square> squareList;
	private OnClickListener itemClickListener;
	private CachedBlobStorageHelper blobStorageHelper;


	public SquareListAdapter(AhFragment frag, List<Square> squareList, OnClickListener itemClickListener) {
		this.frag = frag;
		this.squareList = squareList;
		this.itemClickListener = itemClickListener;
		this.blobStorageHelper = AhApplication.getInstance().getBlobStorageHelper();
	}


	private static class AdminViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView squareNameText;
		public ImageView background;
		public ImageView lockImage;

		public AdminViewHolder(View view) {
			super(view);
			this.view = view;
			this.squareNameText = (TextView)view.findViewById(R.id.row_square_list_admin_name);
			this.background = (ImageView)view.findViewById(R.id.row_square_list_admin_background);
			this.lockImage = (ImageView)view.findViewById(R.id.row_square_list_admin_lock);
		}
	}


	private static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView squareNameText;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.squareNameText = (TextView)view.findViewById(R.id.row_square_list_normal_name);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		if(viewType == TYPE.ADMIN.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_square_list_admin, parent, false);
			viewHolder = new AdminViewHolder(view);
		}else if(viewType == TYPE.NORMAL.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_square_list_normal, parent, false);
			viewHolder = new NormalViewHolder(view);
		}
		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Square square = squareList.get(position);
		int viewType = getItemViewType(position);
		if(viewType == TYPE.ADMIN.ordinal()){
			AdminViewHolder adminViewHolder = (AdminViewHolder)holder;
			setAdminComponent(adminViewHolder, square);
		}else if(viewType == TYPE.NORMAL.ordinal()){
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNormalComponent(normalViewHolder, square);
		}
	}


	@Override
	public int getItemCount() {
		return this.squareList.size();
	}


	@Override
	public int getItemViewType(int position) {
		Square square = squareList.get(position);
		if(square.isAdmin()){
			return TYPE.ADMIN.ordinal();
		} else{
			return TYPE.NORMAL.ordinal();
		}
	}


	private void setNormalComponent(NormalViewHolder holder, Square square){
		holder.squareNameText.setText(square.getName());
		holder.view.setOnClickListener(itemClickListener);
	}


	private void setAdminComponent(AdminViewHolder holder, Square square){
		holder.squareNameText.setText(square.getName());
		holder.view.setOnClickListener(itemClickListener);

		blobStorageHelper.setImageViewAsync(frag, BlobStorageHelper.SQUARE_PROFILE, 
				square.getId(), R.drawable.ground_premium_pic_default, holder.background, false);
		if(!square.getCode().equals("")){
			holder.lockImage.setVisibility(View.VISIBLE);
		} else{
			holder.lockImage.setVisibility(View.GONE);
		}
	}
}
