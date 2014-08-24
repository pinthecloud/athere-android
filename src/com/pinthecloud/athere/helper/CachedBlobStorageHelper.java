package com.pinthecloud.athere.helper;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.FileUtil;

public class CachedBlobStorageHelper extends BlobStorageHelper {
	
	public Bitmap getBitmapSync(final AhFragment frag, String id) {
		synchronized(frag) {
			Bitmap bm = FileUtil.getImageFromInternalStorage(frag.getActivity(), id);
			if (bm != null) return bm;
			
			bm = this.downloadBitmapSync(frag, id);
			if (bm == null) return null;
			FileUtil.saveImageToInternalStorage(frag.getActivity(), bm, id);
			return bm;
		}
	}
	
	public Bitmap getBitmapSync(final AhFragment frag, String id, int w, int h) {
		synchronized(frag) {
			Bitmap bm = FileUtil.getImageFromInternalStorage(frag.getActivity(), id, w, h);
			if (bm != null) return bm;
			
			bm = this.downloadBitmapSync(frag, id);
			if (bm == null) return null;
			FileUtil.saveImageToInternalStorage(frag.getActivity(), bm, id);
			return bm;
		}
	}
	
	
	public void getBitmapAsync(final AhFragment frag, String id, final AhEntityCallback<Bitmap> callback) {
		(new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				String id = params[0];
				return getBitmapSync(frag, id);
			}
			
			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (callback != null)
					callback.onCompleted(result);
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}
	
	public void getBitmapAsync(final AhFragment frag, String id, final int w, final int h, final AhEntityCallback<Bitmap> callback) {
		(new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				String id = params[0];
				return getBitmapSync(frag, id, w, h);
			}
			
			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (callback != null)
					callback.onCompleted(result);
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}
}
