package com.example.fypv2.updatelocation;


import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.fypv2.AppContextHelper;
import com.example.fypv2.EnvAbstractData;
import com.example.fypv2.HttpTools;
import com.example.fypv2.MainFrame;
import com.example.fypv2.MainContext;

import android.os.AsyncTask;
import android.util.Log;

class UploadEngine extends AsyncTask<ArrayList<String>, Void, Void> {

	private String result;
	
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		
		MainContext context = AppContextHelper.getMainContext();

		context.getEnvAbsData().getDownloadManager().switchOff();
		
		if (MainFrame.getDataDrawer() != null) {
			context.getMainFrameStatus();
			MainFrame.getDataDrawer().cancelRefreshTimer();
		}		
		//clear input buffer
		
	}

	protected void onPostExecute() {
		// TODO Auto-generated method stub
		super.onPostExecute(null);
		
		MainContext context = AppContextHelper.getMainContext();
		
		try {
			if (result != null ) {
			
				if(result.contains("mb_UpdateStatus:1"))
					AppContextHelper.getMainContext().showToast("Location Successfully Updated");
			
				else {
					JSONObject root = new JSONObject(result);
					AppContextHelper.getMainContext().showToast("Update Failed: "+root.getString("message"));
				}
			}
			else
				AppContextHelper.getMainContext().showToast("Connection Error!");
		
			context.getEnvAbsData().getDownloadManager().switchOn();	
		}
		catch (Exception e) {
			Log.i("Error onPostExecute",e.toString());
		}
		
		
		
		
		
	}


	@Override
	protected Void doInBackground(ArrayList<String>... urlList) {
		ArrayList<String> url = urlList[0];
		
		for (int i = 0; i< url.size(); i++) {
			try {
				Log.i("Update Location","Submmiting Upload request to url:" + url.get(i));
				
				result = HttpTools.getPageContent(url.get(i),
						AppContextHelper.getMainContext().getContentResolver());	
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.i("Error UpdateEngine",e.toString());
			}		
		}
		return null;
	}


}
