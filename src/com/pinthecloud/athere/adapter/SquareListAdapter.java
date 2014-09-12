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

	private Context context;
	private AhFragment frag;
	private CachedBlobStorageHelper blobStorageHelper;


	public SquareListAdapter(Context context, AhFragment frag) {
		super(context, 0);
		this.context = context;
		this.frag = frag;
		this.blobStorageHelper = AhApplication.getInstance().getBlobStorageHelper();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_square_list, parent, false);
		}

		Square square = getItem(position);
		if (square != null) {
			/*
			 * Find UI component
			 */
			ImageView background = (ImageView)view.findViewById(R.id.row_square_list_background);
			TextView squareNameText = (TextView)view.findViewById(R.id.row_square_list_name);
			TextView maleNumText = (TextView)view.findViewById(R.id.row_square_list_male_number);
			TextView femaleNumText = (TextView)view.findViewById(R.id.row_square_list_female_number);


			/*
			 * Set UI component
			 */
			if(square.isAdmin()){
				blobStorageHelper.setImageViewAsync(frag, BlobStorageHelper.SQUARE_PROFILE, 
						square.getWhoMade(), 0, background, false);	
			}
			squareNameText.setText(square.getName());
			maleNumText.setText(""+square.getMaleNum());
			femaleNumText.setText(""+square.getFemaleNum());
		}
		return view;
	}
}
