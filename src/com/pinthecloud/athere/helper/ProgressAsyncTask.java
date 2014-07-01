package com.pinthecloud.athere.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class ProgressAsyncTask <Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	ProgressDialog progressDialog;
	Context context;
	String message = "Please wait...";
	
	public ProgressAsyncTask(Context context){
		this.context = context;
	}
	
	public ProgressAsyncTask(Context context, ProgressDialog progressDialog){
		this.context = context;
		this.progressDialog = progressDialog;
	}
	
	public ProgressAsyncTask(Context context, String message){
		this.context = context;
		this.message = message;
	}
	
	@Override
	protected void onPreExecute() {
		if (context == null) return;
		
		if (progressDialog == null)
			progressDialog = ProgressDialog.show(context, "In progress", this.message, true);
		else{
			progressDialog = new ProgressDialog(context);
			progressDialog.show();
		}
	};
	
	@Override
	protected void onPostExecute(Result result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (context != null)
			progressDialog.dismiss();
	}
	
}
