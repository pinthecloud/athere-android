package com.pinthecloud.athere.helper;

import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class BItmapUtil {

	public static Bitmap resize(Context context, Uri imageUri, int reqWidth, int reqHeight) throws FileNotFoundException {
		Bitmap bitmapImage = null;

		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(imageUri), null, options);

			options.inSampleSize = calculateSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			bitmapImage = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(imageUri), null, options);

		} catch (FileNotFoundException e) {
			throw e;
		}

		return bitmapImage;
	}

	
	public static int calculateSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int size = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				size = Math.round((float) height / (float) reqHeight);
			} else {
				size = Math.round((float) width / (float) reqWidth);
			}
		}
		return size;
	}
}
