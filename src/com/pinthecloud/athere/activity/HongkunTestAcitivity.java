package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.ImageConverter;

public class HongkunTestAcitivity extends Activity {
	
	ImageView imageView;
	ImageView imageView2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hongkun_test_acitivity);
		
//		imageView.buildDrawingCache();
//		Bitmap img = imageView.getDrawingCache();
		
		BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
		Bitmap img = drawable.getBitmap();
		
		String imageStr = ImageConverter.convertToString(img);
		
		Bitmap afterImg = ImageConverter.convertToImage(imageStr);
		
		imageView2.setImageBitmap(afterImg);
	}
}
