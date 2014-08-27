package com.pinthecloud.athere.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.util.AsyncChainer;

public class BlobStorageHelper {

	
	private static final String storageConnectionString = 
			"DefaultEndpointsProtocol=http;AccountName=athere;AccountKey=ldhgydlWndSIl7XfiaAQ+sibsNtVZ1Psebba1RpBKxMbyFVYUCMvvuQir0Ty7f0+8TnNLfFKc9yFlYpP6ZSuQQ==";
	private static final String CONTAINER_NAME = "chupaprofile";
	protected CloudBlobClient blobClient;
	
	public BlobStorageHelper() {
		CloudStorageAccount account = null;
		try {
			account = CloudStorageAccount.parse(storageConnectionString);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			ExceptionManager.fireException(new AhException(null, "BlobStorageHelper", AhException.TYPE.BLOB_STORAGE_ERROR));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			ExceptionManager.fireException(new AhException(null, "BlobStorageHelper", AhException.TYPE.BLOB_STORAGE_ERROR));
		}

		// Create a blob service client
		blobClient = account.createCloudBlobClient();
	}
	
	public String uploadBitmapSync(final AhFragment frag, String id, Bitmap bitmap) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(CONTAINER_NAME);
			 blob = container.getBlockBlobReference(id);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			 blob.upload(new ByteArrayInputStream(baos.toByteArray()), baos.size());
			 baos.close();
		} catch (URISyntaxException e) {
			ExceptionManager.fireException(new AhException(frag, "uploadBitmapSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		} catch (StorageException e) {
			ExceptionManager.fireException(new AhException(frag, "uploadBitmapSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		} catch (IOException e) {
			ExceptionManager.fireException(new AhException(frag, "uploadBitmapSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		}
		
		return id;
	}
	
	public Bitmap downloadBitmapSync(final AhFragment frag, String id) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(CONTAINER_NAME);
			blob = container.getBlockBlobReference(id);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			blob.download(baos);
			return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
		} catch (URISyntaxException e) {
			e.printStackTrace();
//			ExceptionManager.fireException(new AhException(frag, "downloadBitmapSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		} catch (StorageException e) {
			e.printStackTrace();
//			ExceptionManager.fireException(new AhException(frag, "downloadBitmapSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		}
		Bitmap bm = null;
		if (frag != null) {
			bm = BitmapFactory.decodeResource(frag.getActivity().getResources(), com.pinthecloud.athere.R.drawable.launcher);
		}
		return bm;
	}
	
	public String downloadToFileSync(final AhFragment frag, String id, String path) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		
		try {
			container = blobClient.getContainerReference(CONTAINER_NAME);
			blob = container.getBlockBlobReference(id);
			blob.downloadToFile(frag.getActivity().getFilesDir() + "/" + path);
			return frag.getActivity().getFilesDir() + "/" + path;
		} catch (URISyntaxException e) {
			ExceptionManager.fireException(new AhException(frag, "downloadToFileSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		} catch (StorageException e) {
			ExceptionManager.fireException(new AhException(frag, "downloadToFileSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		} catch (IOException e) {
			ExceptionManager.fireException(new AhException(frag, "downloadToFileSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		}
		return null;
	}
	
	public boolean deleteBitmapSync(final AhFragment frag, String id) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		
		try {
			container = blobClient.getContainerReference(CONTAINER_NAME);
			blob = container.getBlockBlobReference(id);
			blob.delete();
			return true;
		} catch (URISyntaxException e) {
			ExceptionManager.fireException(new AhException(frag, "downloadToFileSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		} catch (StorageException e) {
			ExceptionManager.fireException(new AhException(frag, "downloadToFileSync", AhException.TYPE.BLOB_STORAGE_ERROR));
		}
		return false;
	}
	
	public void uploadBitmapAsync(final AhFragment frag, String id, final Bitmap bitmap, final AhEntityCallback<String> callback) {
		
		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String id = params[0];
				return uploadBitmapSync(frag, id, bitmap);
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (callback != null)
					callback.onCompleted(result);
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}
	
	public void downloadBitmapAsync(final AhFragment frag, String id, final AhEntityCallback<Bitmap> callback) {
		(new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				String id = params[0];
				return downloadBitmapSync(frag, id);
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
	
	public void downloadToFileAsync(final AhFragment frag, String id, final Context context, final String path, final AhEntityCallback<String> callback) {
		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String id = params[0];
				return downloadToFileSync(frag, id, path);
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				if (callback != null)
					callback.onCompleted(result);
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}
	
	public void deleteBitmapAsync(final AhFragment frag, String id, final AhEntityCallback<Boolean> callback) {
		(new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				String id = params[0];
				return deleteBitmapSync(frag, id);
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				if (callback != null)
					callback.onCompleted(result);
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}
}
