package com.pinthecloud.athere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.model.Square;

public class SquareListAdapter extends ArrayAdapter<Square>{

	private enum TYPE{
		NORMAL,
		ADMIN
	}

	private Context context;
	private AhFragment frag;
	private LayoutInflater inflater;
	private CachedBlobStorageHelper blobStorageHelper;


	public SquareListAdapter(Context context, AhFragment frag) {
		super(context, 0);
		this.context = context;
		this.frag = frag;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.blobStorageHelper = AhApplication.getInstance().getBlobStorageHelper();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			if(type == TYPE.NORMAL.ordinal()){
				view = inflater.inflate(R.layout.row_square_list, parent, false);
			} else if(type == TYPE.ADMIN.ordinal()){
				view = inflater.inflate(R.layout.row_square_list, parent, false);
			} 
		}

		Square square = getItem(position);
		if (square != null) {
			/*
			 * Find UI component
			 */
			ImageView background = (ImageView)view.findViewById(R.id.row_square_list_background);
			TextView squareNameText = (TextView)view.findViewById(R.id.row_square_list_name);
			TextView distanceText = (TextView)view.findViewById(R.id.row_square_list_distance);


			/*
			 * Set UI component by type
			 */
			if(type == TYPE.ADMIN.ordinal()){
				blobStorageHelper.setImageViewAsync(frag, BlobStorageHelper.SQUARE_PROFILE, 
						square.getId(), 0, background, false);
				squareNameText.setTextColor(context.getResources().getColor(android.R.color.white));
			}


			/*
			 * Set common UI component
			 */
			squareNameText.setText(square.getName());
			int distance = square.getDistance();
			String unit = context.getResources().getString(R.string.meter);
			if((distance / 1000) >= 1){
				distance /= 1000;  // km
				unit = frag.getResources().getString(R.string.kilometer);	
			}
			distanceText.setText(distance + unit);
		}
		return view;
	}


	@Override
	public int getViewTypeCount() {
		return TYPE.values().length;
	}


	@Override
	public int getItemViewType(int position) {
		// Inflate different layout by user
		Square square = getItem(position);
		if(square.isAdmin()){
			return TYPE.ADMIN.ordinal();
		} else{
			return TYPE.NORMAL.ordinal();
		}
	}
}
