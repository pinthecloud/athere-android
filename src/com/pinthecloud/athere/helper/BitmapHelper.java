package com.pinthecloud.athere.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.pinthecloud.athere.AhGlobalVariable;

public class BitmapHelper {

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


	public static int getImageOrientation(File imageFile) throws IOException{
		Log.d(AhGlobalVariable.LOG_TAG, "BitmapHelper getImageOrientation");
		
		try {
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			default:
				return 0;
			}
		} catch (IOException e) {
			throw e;
		}
	}


	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}


	public static Bitmap crop(Bitmap bitmap, int rotationDegree, int width, int height) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int xOffset = 0;
		int yOffset = 0;
		if(rotationDegree == AhGlobalVariable.ANGLE_180){
			xOffset = bitmapWidth - width;
		} else if(rotationDegree == AhGlobalVariable.ANGLE_270){
			yOffset = bitmapHeight - height;
		}
		return Bitmap.createBitmap(bitmap, xOffset, yOffset, width, height);
	}
}
