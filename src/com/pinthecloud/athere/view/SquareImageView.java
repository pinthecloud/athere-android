package com.pinthecloud.athere.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

	public SquareImageView(Context context) {
		super(context);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		setMeasuredDimension(width, width);
	}
}
