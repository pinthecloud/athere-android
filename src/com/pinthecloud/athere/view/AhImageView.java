package com.pinthecloud.athere.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class AhImageView extends ImageView{

	public AhImageView(Context context) {
		super(context);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public AhImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public AhImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}
}
