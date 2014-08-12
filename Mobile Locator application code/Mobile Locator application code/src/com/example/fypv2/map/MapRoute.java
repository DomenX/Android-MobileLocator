package com.example.fypv2.map;


import java.util.List;

import org.mixare.lib.marker.Marker;

import com.example.fypv2.DataDrawer;
import com.example.fypv2.MainContext;
import com.example.fypv2.MainFrame;
import com.example.fypv2.R;
import com.example.fypv2.R.id;
import com.example.fypv2.R.layout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class MapRoute extends FragmentActivity {

		private GoogleMap map;
		
		private DataDrawer dataTool;
		private MainContext mainContext;
		
		private static List<Marker> markerList;
		private static Context thisContext;
		private static List<Overlay> mapOverlays;
		
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.maproute);
	        
			dataTool = MainFrame.getDataDrawer();
			setMainContext(dataTool.getContext());
			setMarkerList(dataTool.getDataHandler().getMarkerList());
	        
	        
	        setUpMapIfNeeded();
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	        setUpMapIfNeeded();
	    }

	    /**
	     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	     * installed) and the map has not already been instantiated.. This will ensure that we only ever
	     * call {@link #setUpMap()} once when {@link #mMap} is not null.
	     * <p>
	     * If it isn't installed {@link SupportMapFragment} (and
	     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
	     * install/update the Google Play services APK on their device.
	     * <p>
	     * A user can return to this FragmentActivity after following the prompt and correctly
	     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	     * have been completely destroyed during this process (it is likely that it would only be
	     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	     * method in {@link #onResume()} to guarantee that it will be called.
	     */
	    private void setUpMapIfNeeded() {
	        // Do a null check to confirm that we have not already instantiated the map.
	        if (map == null) {
	            // Try to obtain the map from the SupportMapFragment.
	            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	            // Check if we were successful in obtaining the map.
	            if (map != null) {
	                setUpMap();
	            }
	        }
	    }

	    /**
	     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	     * just add a marker near Africa.
	     * <p>
	     * This should only be called once and when we are sure that {@link #mMap} is not null.
	     */
	    private void setUpMap() {
	    	//Location location = getMainContext().getEnvAbsData().getLocationFinder().getCurrentLocation();	
			
			double latitude = 5.3546718;		//location.getLatitude();
			double longitude = 100.3010370;		//location.getLongitude();
			
			LatLng myLocation = new LatLng(latitude, longitude);
	        map.getUiSettings().setZoomControlsEnabled(false);

	        // Show Sydney
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10));

			
			map.addMarker(new MarkerOptions().position(myLocation).title("My Home").snippet("Home Address"));
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

		public List<Overlay> getMapOverlayList(){
			return getMapOverlays();
		}
		
		private static List<Overlay> getMapOverlays() {
			return mapOverlays;
		}

		public void setMapContext(Context context){
			thisContext= context;
		}

		public Context getMapContext(){
			return thisContext;
		}

}