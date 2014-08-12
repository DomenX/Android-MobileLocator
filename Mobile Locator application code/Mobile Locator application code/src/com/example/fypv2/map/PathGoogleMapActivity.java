package com.example.fypv2.map;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.mixare.lib.marker.Marker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.example.fypv2.DataDrawer;
import com.example.fypv2.MainContext;
import com.example.fypv2.MainFrame;
import com.example.fypv2.R;
import com.example.fypv2.datasource.DataSourceList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class PathGoogleMapActivity extends FragmentActivity {
 
		private static List<Marker> markerList;
		
		private GoogleMap googleMap;
		private DataDrawer dataTool;
		private MainContext mainContext;
		
	    private UiSettings mUiSettings;
	    
		private static Context thisContext;
		public static List<Marker> originalMarkerList;
		
		private Intent myIntent;

		 @Override
		 protected void onCreate(Bundle savedInstanceState) {
			 super.onCreate(savedInstanceState);
		   
			 myIntent = getIntent();
		
			 dataTool = MainFrame.getDataDrawer();
			 setMainContext(dataTool.getContext());

			 setMapContext(this);
			 
			 setContentView(R.layout.maproute);
		     setUpMapIfNeeded();
		 }	
		 

		@Override
		 protected void onResume() {
		      super.onResume();
		      setUpMapIfNeeded();
	
	
		 }
		 
		private void setUpMapIfNeeded() {
			// Do a null check to confirm that we have not already instantiated the map.
			if (googleMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
			                    .getMap();
			// Check if we were successful in obtaining the map.
			if (googleMap != null) {
			setupMapSetting();
			setUpMap();
			}
			}
		}
		        
		private void setupMapSetting() {
	   		// Keep the UI Settings state in sync with the checkboxes.
		    mUiSettings = googleMap.getUiSettings();
	   		mUiSettings.setZoomControlsEnabled(true);
	   		mUiSettings.setCompassEnabled(true);
	   		mUiSettings.setMyLocationButtonEnabled(true);
	   		mUiSettings.setScrollGesturesEnabled(true);
	   		mUiSettings.setZoomGesturesEnabled(true);
	   		mUiSettings.setTiltGesturesEnabled(true);
	   		mUiSettings.setRotateGesturesEnabled(true);
		}
	
		private void setUpMap() {
			setCurrentLocation();
			
			myIntent = getIntent();
			
			if (myIntent.hasExtra("searchMarker") && myIntent.hasExtra("latitude") && myIntent.hasExtra("longitude")) {
				 addDestMarker(myIntent.getStringExtra("searchMarker"), 
						 myIntent.getDoubleExtra("latitude", -999), 
						 myIntent.getDoubleExtra("longitude", -999));
				displayPath();
			}
			else
				addMarker();

		}
			 
		private void addDestMarker(String name, double latitude, double longitude) {
			LatLng markerLocation = new LatLng(latitude, longitude);
			googleMap.addMarker(new MarkerOptions().position(markerLocation).title(name)
					. icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));	
		}


		private void setCurrentLocation() {
			Location location = getMainContext().getEnvAbsData().getLocationFinder().getCurrentLocation();
	
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			LatLng myLocation = new LatLng(5.3546718, 100.3010370);
			//LatLng myLocation = new LatLng(latitude, longitude);
					
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
			googleMap.addMarker(new MarkerOptions().position(myLocation).title("You ARE Here!!")
							. icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}
		
		 private void displayPath() {
			 setMarkerList(dataTool.getDataHandler().getMarkerList());
			 String url = getMapsApiDirectionsUrl( myIntent.getStringExtra("searchMarker"),
					 		myIntent.getDoubleExtra("latitude", -999), 
					 		myIntent.getDoubleExtra("longitude", -999));
			 
			  ReadTask downloadTask = new ReadTask();
			  downloadTask.execute(url);	 
		}

		private void addMarker() {
			 setMarkerList(dataTool.getDataHandler().getMarkerList());
			 
			for(Marker marker:markerList) {
				if(marker.isActive()) {
					LatLng markerLocation = new LatLng(marker.getLatitude(), marker.getLongitude());

					googleMap.addMarker(new MarkerOptions().position(markerLocation).title(marker.getTitle())
							. icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
				}
			}
		}
		
		private String getMapsApiDirectionsUrl(String name, double latitude, double longitude) {
			Location location = getMainContext().getEnvAbsData().getLocationFinder().getCurrentLocation();
			/*
		    String waypoints = "waypoints=optimize:true|"
			        + latitude + "," + longitude
			        + "|" + "|" + location.getLatitude() + ","
			        + location.getLongitude();
			 */
		    String waypoints = "waypoints=optimize:true|"
			        + latitude + "," + longitude
			        + "|" + "|" + 5.3546718 + ","
			        + 100.3010370;
			    String sensor = "sensor=false";
			    String params = waypoints + "&" + sensor;
			    String output = "json";
			    String url = "https://maps.googleapis.com/maps/api/directions/"
			        + output + "?" + params;
			    return url;
		}
		 
	    public void onClearMap() {
	        if (googleMap ==null) {
	            return;
	        }
	        googleMap.clear();
	    }
		
		private MainContext getMainContext() {
			return mainContext;
		}
	
	
		private void setMainContext(MainContext Context) {
			this.mainContext = Context;
		}
		
		public void setMarkerList(List<Marker> maList){

			markerList = maList;
		}
	
		public DataDrawer getDataDrawer(){
			return dataTool;
		}
	
		public void setMapContext(Context context){
			thisContext= context;
		}
	
		public Context getMapContext(){
			return thisContext;
		}
 
 private class ReadTask extends AsyncTask<String, Void, String> {
   @Override
   protected String doInBackground(String... url) {
     String data = "";
     try {
       HttpConnection http = new HttpConnection();
       data = http.readUrl(url[0]);
     } catch (Exception e) {
       Log.d("Background Task", e.toString());
     }
     return data;
   }

   @Override
   protected void onPostExecute(String result) {
     super.onPostExecute(result);
     new ParserTask().execute(result);
   }
 }

 private class ParserTask extends
     AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

   @Override
   protected List<List<HashMap<String, String>>> doInBackground(
       String... jsonData) {

     JSONObject jObject;
     List<List<HashMap<String, String>>> routes = null;

     try {
       jObject = new JSONObject(jsonData[0]);
       PathJSONParser parser = new PathJSONParser();
       routes = parser.parse(jObject);
     } catch (Exception e) {
       e.printStackTrace();
     }
     return routes;
   }

   @Override
   protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
     ArrayList<LatLng> points = null;
     PolylineOptions polyLineOptions = null;

     // traversing through routes
     for (int i = 0; i < routes.size(); i++) {
       points = new ArrayList<LatLng>();
       polyLineOptions = new PolylineOptions();
       List<HashMap<String, String>> path = routes.get(i);

       for (int j = 0; j < path.size(); j++) {
         HashMap<String, String> point = path.get(j);

         double lat = Double.parseDouble(point.get("lat"));
         double lng = Double.parseDouble(point.get("lng"));
         LatLng position = new LatLng(lat, lng);

         points.add(position);
       }

       polyLineOptions.addAll(points);
       polyLineOptions.width(2);
       polyLineOptions.color(Color.BLUE);
     }

     googleMap.addPolyline(polyLineOptions);
   }
 }
}