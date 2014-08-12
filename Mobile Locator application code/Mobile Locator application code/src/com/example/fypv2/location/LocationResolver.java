package com.example.fypv2.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * This class will be used to start each location provider for 20 seconds
 * and they will then listen for locations. This class will check for updates for
 * the observer. 
 * Using this method: http://stackoverflow.com/questions/3145089/
 * @author A. Egal
 */
public class LocationResolver implements LocationListener{
	//mixare version
	private String provider;
	private LocationEngine locationEngine;
	private LocationManager lm;
	
	public LocationResolver(LocationManager lm, String provider, LocationEngine locationMgrImpl){
		this.lm = lm;
		this.provider = provider;
		this.locationEngine = locationMgrImpl;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		lm.removeUpdates(this);
		locationEngine.locationCallback(provider);
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
}
