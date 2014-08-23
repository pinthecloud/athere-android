package com.pinthecloud.athere.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.AhActivity;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhUser;

public class SquareDrawerParticipantListAdapter extends ArrayAdapter<AhUser> {

	private Context context;
	private AhFragment frag;
	private int layoutId;
	private List<AhUser> items;
	private CachedBlobStorageHelper blobStorageHelper;

	public SquareDrawerParticipantListAdapter(Context context, AhFragment fragment, int layoutId, List<AhUser> items) {
		super(context, layoutId, items);
		this.context = context;
		this.frag = fragment;
		this.layoutId = layoutId;
		this.items = items;
		this.blobStorageHelper = AhApplication.getInstance().getBlobStorageHelper();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(this.layoutId, parent, false);
		}

		final AhUser user = items.get(position);
		if (user != null) {
			/*
			 * Find UI Component
			 */
			final ImageView profileImage = (ImageView)view.findViewById(R.id.drawer_user_pro_pic);
			TextView nickName = (TextView)view.findViewById(R.id.drawer_user_nick_name);
			ImageView gender = (ImageView)view.findViewById(R.id.drawer_user_gender);
			ImageButton chupaButton = (ImageButton)view.findViewById(R.id.drawer_user_chupa_btn);
			TextView companyNumber = (TextView) view.findViewById(R.id.drawer_user_company_num);

			/*
			 * Set UI component
			 */
			nickName.setText(user.getNickName());
			companyNumber.setText("" + user.getCompanyNum());
			Resources resources = context.getResources();
			if(user.isMale()){
				gender.setImageResource(R.drawable.profile_gender_m);
				companyNumber.setTextColor(resources.getColor(R.color.blue));
			}else{
				gender.setImageResource(R.drawable.profile_gender_w);
				companyNumber.setTextColor(resources.getColor(R.color.dark_red));
			}
			final int w = profileImage.getWidth();
			final int h = profileImage.getHeight();
//			Bitmap profileBitmap = FileUtil.getImageFromInternalStorage(context, user.getProfilePic(), w, h);
//			profileImage.setImageBitmap(profileBitmap);
			blobStorageHelper.getBitmapAsync(frag, user.getId(), w,h, new AhEntityCallback<Bitmap>() {

				@Override
				public void onCompleted(final Bitmap entity) {
					// TODO Auto-generated method stub
					AhActivity activity = (AhActivity)context;
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							profileImage.setImageBitmap(entity);
						}
					});
					
				}
			});
//			blobStorageHelper.downloadBitmapAsync(frag, user.getId(), new AhEntityCallback<Bitmap>() {
//
//				@Override
//				public void onCompleted(final Bitmap entity) {
//					// TODO Auto-generated method stub
//					Log.e("ERROR", "bitmap : " + entity);
//					AhActivity activity = (AhActivity)context;
//					activity.runOnUiThread(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							profileImage.setImageBitmap(entity);
//						}
//					});
//				}
//			});


			/*
			 * Set event on button
			 */
			chupaButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ChupaChatActivity.class);
					intent.putExtra(AhGlobalVariable.USER_KEY, user.getId());
					context.startActivity(intent);
				}
			});
		}
		return view;
	}
}
