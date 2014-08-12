package com.example.fypv2.dataprocessor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mixare.lib.HtmlUnescape;
import org.mixare.lib.marker.Marker;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import com.example.fypv2.MainFrame;
import com.example.fypv2.datasource.DataHandler;
import com.example.fypv2.datasource.DataSource;
import com.example.fypv2.marker.LocatorMarker;

public class MbLocatorDataProcessor extends DataHandler implements DataProcessor{

	public static final int MAX_JSON_OBJECTS = 1000;
	
	@Override
	public String[] getUrlMatch() {
		String[] str = {"dlLocation.php"};
		return str;
	}

	@Override
	public String[] getDataMatch() {
		String[] str = {"mb_lction"};
		return str;
	}

	@Override
	public boolean matchesRequiredType(String type) {
		if(type.equals(DataSource.TYPE.MOBILE_LOCATOR.name())){
			return true;
		}
		return false;
	}
	
	@Override
	public List<Marker> load(String rawData, int taskId, int colour) throws JSONException {
	
        List<Marker> markers = new ArrayList<Marker>();
        JSONObject root = convertToJSON(rawData);  
		JSONArray dataArray = root.getJSONArray("location");
		int top = Math.min(MAX_JSON_OBJECTS, dataArray.length());

		for (int i = 0; i < top; i++) {
			JSONObject jo = dataArray.getJSONObject(i);
			String[] attribute = { "mb_lction_id" , "mb_lction_name" , "mb_lction_lat" , "mb_lction_long" ,
									"mb_lction_type" , "mb_lction_elevation" , "mb_lction_desc" ,
									"mb_lction_addr_state" , "mb_lction_city" , "mb_lction_img"};
			int true_value  = 1;
			int false_value = 0;
			int AllowEntered = true_value;

			LocatorMarker marker = null;
            
			for (String temp: attribute) {
				if (jo.has(temp))
					AllowEntered *= true_value;
				else
					AllowEntered *= false_value;
			}
			
			if (AllowEntered == true_value) {
				Log.v(MainFrame.TAG, "processing MbLocator JSON object");

				marker = new LocatorMarker(
							jo.getString("mb_lction_id"),
							HtmlUnescape.unescapeHTML(jo.getString("mb_lction_name"), 0), 
							jo.getDouble("mb_lction_lat"), 
							jo.getDouble("mb_lction_long"), 
							jo.getDouble("mb_lction_elevation"),
							jo.getString("mb_lction_desc"),
							jo.getString("mb_lction_addr_state"),
							jo.getString("mb_lction_city"),
							jo.getString("mb_lction_img"),
							//"http://"+jo.getString("wikipediaUrl"), 
							taskId, colour);
				Log.v(MainFrame.TAG, "processing MbLocator JSON object");
                markers.add(marker);
			/*
			 * 	image = BitmapFactory.decodeResource(
                 		ContextHelper.getMainContext().getResources(),
                        R.drawable.lction_marker);
				if (jo.getString("object_type").equals("information")) {
		                   image = 
		            } else if (jo.getString("object_type").equals("question")) {
		              image = getBitmapFromURL(jo.getString("object_url"));
	           } else if (jo.getString("object_type").equals("image")) {
		             image = getBitmapFromURL(jo.getString("object_url"));
		       }*/
			}
		}
		
		if (markers.size() > 0)
			return markers;
		else
			return null;
	}		

	private JSONObject convertToJSON(String rawData){
		try {
			return new JSONObject(rawData);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
    
}
		
		
		
