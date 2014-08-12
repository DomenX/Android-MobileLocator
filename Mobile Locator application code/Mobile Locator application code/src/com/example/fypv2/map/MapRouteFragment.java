package com.example.fypv2.map;


import java.util.ArrayList;
import java.util.List;

import org.mixare.lib.marker.Marker;

import com.example.fypv2.DataDrawer;
import com.example.fypv2.MainContext;
import com.example.fypv2.MainFrame;
import com.example.fypv2.R;
import com.example.fypv2.R.drawable;
import com.example.fypv2.R.layout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MapRouteFragment extends Fragment {

	private static View view;
	private static GoogleMap map;
	
	private DataDrawer dataTool;
	private MainContext mainContext;
	
	private static List<Marker> markerList;
	private static Context thisContext;
	private static List<Overlay> mapOverlays;
	
	public static MapRouteFragment newInstance() {
		MapRouteFragment fragment = new MapRouteFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if (container == null) {
        return null;
		}	
		dataTool = MainFrame.getDataDrawer();
		setMainContext(dataTool.getContext());
		setMarkerList(dataTool.getDataHandler().getMarkerList());
		

		view = inflater.inflate(R.layout.maproute, container, false);
		setUpMapIfNeeded(); // For setting up the MapFragment
		return view;
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	        // Try to obtain the map from the SupportMapFragment.
	        //map = ((SupportMapFragment) MapRoute.fragmentManager.findFragmentById(R.id.map)).getMap();
	        // Check if we were successful in obtaining the map.
	        if (map != null)
	            setUpMap();
	    }
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	    // TODO Auto-generated method stub
	    if (map != null)
	        setUpMap();

	    if (map == null) {
	        // Try to obtain the map from the SupportMapFragment.
	        //map = ((SupportMapFragment) MapRoute.fragmentManager.findFragmentById(R.id.map)).getMap();
	        // Check if we were successful in obtaining the map.
	        if (map != null)
	            setUpMap();
	    }
	}	
	
	private void updataMyLocation() {
		Location location = getMainContext().getEnvAbsData().getLocationFinder().getCurrentLocation();	
		
		double latitude = location.getLatitude()*1E6;
		double longitude = location.getLongitude()*1E6;
		
		LatLng myLocation = new LatLng(latitude, longitude);
		
		map.addMarker(new MarkerOptions().position(myLocation).title("My Home").snippet("Home Address")
				 .icon(BitmapDescriptorFactory.fromResource(R.drawable.loc_icon)));
	
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);	
	}
	
	private void setUpMap() {
	    map.setMyLocationEnabled(true);
	    //updataMyLocation();

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