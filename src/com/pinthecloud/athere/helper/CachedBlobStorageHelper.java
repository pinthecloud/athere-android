package com.pinthecloud.athere.helper;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class CachedBlobStorageHelper extends BlobStorageHelper {

	private AhApplication app;
	private LruCache<String, Bitmap> mMemoryCache;


	public CachedBlobStorageHelper() {
		super();
		this.app = AhApplication.getInstance();

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};
	}


	//	public Bitmap getBitmapSync(final AhFragment frag, String id, int placeHolderId, int w, int h) {
	//		Bitmap bitmap = FileUtil.getImageFromInternalStorage(frag.getActivity(), id);
	//		if (bitmap != null) return bitmap;
	//
	//		bitmap = this.downloadBitmapSync(frag, id);
	//		if (bitmap == null) return null;
	//
	//		FileUtil.saveImageToInternalStorage(frag.getActivity(), bitmap, id);
	//		return bitmap;
	//	}


	//	public Bitmap getBitmapSync(final AhFragment frag, String id) {
	//		Bitmap bm = null;
	//
	//		bm = this.downloadBitmapSync(null, id);
	//		if (bm == null) return null;
	//		Bitmap smallBm = BitmapUtil.decodeInSampleSize(bm, BitmapUtil.SMALL_PIC_SIZE, BitmapUtil.SMALL_PIC_SIZE);
	//		FileUtil.saveImageToInternalStorage(frag.getActivity(), bm, id);
	//		FileUtil.saveImageToInternalStorage(frag.getActivity(), smallBm, id + BitmapUtil.SMALL_PIC_SIZE);
	//		return bm;
	//	}

	public void setImageViewAsync(AhFragment frag, String id, int placeHolderId, ImageView imageView, boolean isSmall) {
		if (cancelPotentialWork(id, imageView)) {
			String origId = id;
			if (isSmall){
				id = id + BitmapUtil.SMALL_PIC_SIZE;
			}

			// Check from cache
			Bitmap bitmap = getBitmapFromMemCache(id);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				return;
			}

			// Check from Disk
			bitmap = FileUtil.getImageFromInternalStorage(frag.getActivity(), id);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				addBitmapToMemoryCache(id, bitmap);
				return;
			}

			// Get from the server
			Bitmap mPlaceHolderBitmap = null;
			if(placeHolderId != 0){
				int w = imageView.getWidth();
				int h = imageView.getHeight();
				mPlaceHolderBitmap = BitmapUtil.decodeInSampleSize(frag.getResources(), placeHolderId, w, h);	
			}

			BitmapWorkerTask task = new BitmapWorkerTask(frag, imageView, isSmall);
			AsyncDrawable asyncDrawable = new AsyncDrawable(frag.getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(origId);
		}
	}


	private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private AhFragment frag;
		private WeakReference<ImageView> imageViewReference;
		private String id = null;
		private Boolean isSmall;

		public BitmapWorkerTask(AhFragment frag, ImageView imageView, Boolean isSmall) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			this.frag = frag;
			this.imageViewReference = new WeakReference<ImageView>(imageView);
			this.isSmall = isSmall;
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			this.id = params[0];

			Bitmap bitmap = downloadBitmapSync(frag, id);
			if (bitmap == null) return null;

			Bitmap bigBitmap = BitmapUtil.decodeInSampleSize(bitmap, BitmapUtil.BIG_PIC_SIZE, BitmapUtil.BIG_PIC_SIZE);
			Bitmap smallBitmap = BitmapUtil.decodeInSampleSize(bitmap, BitmapUtil.SMALL_PIC_SIZE, BitmapUtil.SMALL_PIC_SIZE);

			FileUtil.saveImageToInternalStorage(app, bigBitmap, id);
			FileUtil.saveImageToInternalStorage(app, smallBitmap, id+BitmapUtil.SMALL_PIC_SIZE);

			addBitmapToMemoryCache(id, bigBitmap);
			addBitmapToMemoryCache(id+BitmapUtil.SMALL_PIC_SIZE, smallBitmap);

			if (!this.isSmall) {
				return bigBitmap;
			} else{
				return smallBitmap;
			}
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
			AsyncChainer.notifyNext(frag);
		}
	}


	private class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}


	private boolean cancelPotentialWork(String id, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			String bitmapData = bitmapWorkerTask.id;
			// If bitmapData is not yet set or it differs from the new data
			if (bitmapData == null || !bitmapData.equals(id)) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}


	private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}


	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (key == null || bitmap == null) return;
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}


	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}


	public void clearCache() {
		mMemoryCache.evictAll();
	}


	//	public void getBitmapAsync(final AhFragment frag, String id, final AhEntityCallback<Bitmap> callback) {
	//		Bitmap bitmap = getBitmapFromMemCache(id);
	//		if (bitmap != null) {
	//			callback.onCompleted(bitmap);
	//		} else {
	//			(new AsyncTask<String, Void, Bitmap>() {
	//
	//				@Override
	//				protected Bitmap doInBackground(String... params) {
	//					String id = params[0];
	//					Bitmap bitmap = getBitmapSync(frag, id);
	//					addBitmapToMemoryCache(id, bitmap);
	//					return bitmap;
	//				}
	//
	//				@Override
	//				protected void onPostExecute(Bitmap result) {
	//					super.onPostExecute(result);
	//					if (callback != null)
	//						callback.onCompleted(result);
	//					AsyncChainer.notifyNext(frag);
	//				}
	//			}).execute(id);
	//		}
	//	}


	//	public void getBitmapAsync(final AhFragment frag, String id, final int w, final int h, final AhEntityCallback<Bitmap> callback) {
	//		(new AsyncTask<String, Void, Bitmap>() {
	//
	//			@Override
	//			protected Bitmap doInBackground(String... params) {
	//				String id = params[0];
	//				return getBitmapSync(frag, id, w, h);
	//			}
	//
	//			@Override
	//			protected void onPostExecute(Bitmap result) {
	//				super.onPostExecute(result);
	//				if (callback != null)
	//					callback.onCompleted(result);
	//				AsyncChainer.notifyNext(frag);
	//			}
	//		}).execute(id);
	//	}
}
