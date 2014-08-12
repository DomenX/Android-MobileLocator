/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file is part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package com.example.fypv2;

import static android.view.KeyEvent.KEYCODE_CAMERA;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.example.fypv2.datasource.DataHandler;
import com.example.fypv2.datasource.DataSource;
import com.example.fypv2.download.DownloadManager;
import com.example.fypv2.download.DownloadRequest;
import com.example.fypv2.download.DownloadResult;

import org.mixare.lib.MixUtils;
import org.mixare.lib.marker.Marker;
import org.mixare.lib.render.Camera;
import org.mixare.lib.gui.PaintScreen;
import org.mixare.lib.gui.ScreenLine;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;


public class DataDrawer {
	
	//init variable
	private int width, height;
	private Camera CamObj;
	private ScreenLine lrl = new ScreenLine();	//left radar line,initialize 0,0
	private ScreenLine rrl = new ScreenLine();	//right radar line
	private float rx = 10, ry = 20;
	
	private boolean isFrozen;
	private boolean isInit;
	
	private ActState state;
	private RadarPoints radarPoints = new RadarPoints();
	private MainContext mainContext;
	
	private float DefaultRadius = 20;
	private DataHandler dataHandler = new DataHandler();
	
	private Location position;
	
	private int retry;		//retry when there is an error
	private Timer refresh = null;
	private final long refreshDelay = 45 * 1000; // refresh every 45 seconds

	private float addX = 0, addY = 0;

	private ArrayList<UIEvent> uiArray = new ArrayList<UIEvent>();
	
	public DataDrawer(MainContext ctxt) {
		this.mainContext=ctxt;
	}
	
	public MainContext getContext() {
		return mainContext;
	}

	public DataHandler getDataHandler() {
		return dataHandler;
	}
	
	public boolean IsFrozen() {
		return isFrozen;
	}
	
	public boolean IsInited() {
		return isInit;
	}
	
	public void setisFrozen(boolean frozen) {
		this.isFrozen = frozen;
	}
	
	public float getDefaultRadius() {
		return DefaultRadius;
	}
	///////////////////////////////////////////////////
	public void doStart() {
		startActState();
		mainContext.getEnvAbsData().getLocationFinder().setLocationAtLastDownload(position);
	}
	
	public void refresh(){
		dataHandler = new DataHandler();
		startActState();
		//state.nextLStatus = ActState.NOT_STARTED;
	}
	
	private void startActState() {
		state =  new ActState();
	}
	///////////////////////////////////////////////////////////////
	public void RequestSingleData(String url) {
		DownloadRequest request = new DownloadRequest(new DataSource(
				"LAUNCHER", url, DataSource.TYPE.MIXARE,
				DataSource.DISPLAY.CIRCLE_MARKER, true));
		mainContext.getEnvAbsData().getDataSourceManager().setAllDataSourcesforLauncher(
				request.getSource());
		mainContext.getEnvAbsData().getDownloadManager().submitJob(request);
		state.nextLStatus = ActState.PROCESSING;
	}
	
	public void init(int widthInit, int heightInit) {
		
		try {
			startActState();
			
			width = widthInit;
			height = heightInit;

			CamObj = new Camera(width, height, true);
			CamObj.setViewAngle(Camera.DEFAULT_VIEW_ANGLE);
			
			lrl.set(0,-RadarPoints.RADIUS ); //set 0,40 
			lrl.rotate(Camera.DEFAULT_VIEW_ANGLE / 2); //45/2 
			lrl.add(rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS);	//50,60
			
			rrl.set(0, -RadarPoints.RADIUS);	//0,40
			rrl.rotate(-Camera.DEFAULT_VIEW_ANGLE / 2);
			rrl.add(rx + RadarPoints.RADIUS, ry + RadarPoints.RADIUS);	//50,60
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		isFrozen = false;
		isInit = true;
	}


	public void draw(PaintScreen dw) {
		
		mainContext.getRM(CamObj.transform);
		position = mainContext.getEnvAbsData().getLocationFinder().getCurrentLocation();
		
		state.calcPitchBearing(CamObj.transform);

		// Load Layer
		if (state.nextLStatus == ActState.NOT_STARTED && !IsFrozen()) {
			
			double lat = position.getLatitude();
			double lon = position.getLongitude();
			double alt = position.getAltitude();
			state.nextLStatus = ActState.PROCESSING;
			
			if (mainContext.getEnvSimData().getSearcCategory()!= null || mainContext.getEnvSimData().getSearcString()!= null) {
				//search function
				String searchString = mainContext.getEnvSimData().getSearcString();
				String searchCategory = mainContext.getEnvSimData().getSearcCategory();
				Log.i(searchString, searchCategory);
				
				mainContext.getEnvAbsData().getDataSourceManager()
				.requestSearchDataFromAllActiveDataSource
				(searchString, searchCategory, lat, lon, alt, DefaultRadius);

			} else {
				mainContext.getEnvAbsData().getDataSourceManager()
				.requestDataFromAllActiveDataSource(lat, lon, alt, DefaultRadius);
			}
			
			//changeState
			if (state.nextLStatus == ActState.NOT_STARTED)
					state.nextLStatus = ActState.DONE;

		} else if (state.nextLStatus == ActState.PROCESSING) {
				DownloadManager downloader = mainContext.getEnvAbsData().getDownloadManager();
				DownloadResult dlResult;

				while ((dlResult = downloader.getNextResult()) != null) {
					if (dlResult.isError() && retry < 3) {
						retry++;
						mainContext.getEnvAbsData().getDownloadManager().submitJob(
									dlResult.getErrorRequest());
							// Notification
							// Toast.makeText(mixContext, dRes.errorMsg,
							// Toast.LENGTH_SHORT).show();
					}
					//download engine processrequest
					
					if(!dlResult.isError()) {

						if(dlResult.getMarkers() != null){
							//jLayer = (DataHandler) dRes.obj;
							Log.i(MainFrame.TAG,"Adding Markers");
							mainContext.showToast(dlResult.getMarkers().size() + " Location found nearby!");
							dataHandler.addMarkers(dlResult.getMarkers());
							dataHandler.onLocationChanged(position);
							// Notification
							mainContext.showToast(
									mainContext.getResources().getString(R.string.download_received) 
									+ " " 
									+ dlResult.getDataSource().getName());						
						}
						else {
							mainContext.showToast("No location found nearby!!!");
						}
					}
				}//end while

				if (downloader.isDone()) {
					retry = 0;
					state.nextLStatus = ActState.DONE;

					if (refresh == null) { // start the refresh timer if it is null
						refresh = new Timer(false);
						Date date = new Date(System.currentTimeMillis()
									+ refreshDelay);
						refresh.schedule(new TimerTask() {

							@Override
							public void run() {
								callRefreshToast();
								refresh();
							}
						}, date, refreshDelay);
					}
				}//end if downloader.isDone
			}//end else if

			// Update markers
			dataHandler.updateActivationStatus(mainContext);
			for (int i = dataHandler.getMarkerCount() - 1; i >= 0; i--) {

				Marker ma = dataHandler.getMarker(i);
				// if (ma.isActive() && (ma.getDistance() / 1000f < radius || ma
				// instanceof NavigationMarker || ma instanceof SocialMarker)) {

				if (ma.isActive() && (ma.getDistance() / 1000f < DefaultRadius)) {
						// To increase performance don't recalculate position vector
						// for every marker on every draw call, instead do this only
						// after onLocationChanged and after downloading new marker
						// if (!frozen)
						// ma.update(curFix);
						if (!isFrozen)
							ma.calcPaint(CamObj, addX, addY);
						ma.draw(dw);
					}
				}

				// Draw Radar
				drawRadar(dw);

				// Get next event
				UIEvent evt = null;
				synchronized (uiArray) {
					if (uiArray.size() > 0) {
						evt = uiArray.get(0);
						uiArray.remove(0);
					}
				}
				if (evt != null) {
					switch (evt.type) {
					case UIEvent.KEY:
						handleKeyEvent((KeyEvent) evt);
						break;
					case UIEvent.CLICK:
						handleClickEvent((ClickEvent) evt);
						break;
					}
				}
				state.nextLStatus = ActState.PROCESSING;

	}
	
	/**
	 * Handles drawing radar and direction.
	 * @param PaintScreen screen that radar will be drawn to
	 */
	private void drawRadar(PaintScreen dw) {
		String dirTxt = "";
		int bearing = (int) state.getCurBearing();
		int range = (int) (state.getCurBearing() / (360f / 16f));
		// TODO: get strings from the values xml file
		if (range == 15 || range == 0)
			dirTxt = getContext().getString(R.string.N);
		else if (range == 1 || range == 2)
			dirTxt = getContext().getString(R.string.NE);
		else if (range == 3 || range == 4)
			dirTxt = getContext().getString(R.string.E);
		else if (range == 5 || range == 6)
			dirTxt = getContext().getString(R.string.SE);
		else if (range == 7 || range == 8)
			dirTxt = getContext().getString(R.string.S);
		else if (range == 9 || range == 10)
			dirTxt = getContext().getString(R.string.SW);
		else if (range == 11 || range == 12)
			dirTxt = getContext().getString(R.string.W);
		else if (range == 13 || range == 14)
			dirTxt = getContext().getString(R.string.NW);

		
		radarPoints.view = this;
		dw.paintObj(radarPoints, rx, ry, -state.getCurBearing(), 1);
		dw.setFill(false);
		dw.setColor(Color.argb(150, 0, 0, 220));
		dw.paintLine(lrl.x, lrl.y, rx + RadarPoints.RADIUS, ry
				+ RadarPoints.RADIUS);
		dw.paintLine(rrl.x, rrl.y, rx + RadarPoints.RADIUS, ry
				+ RadarPoints.RADIUS);
		dw.setColor(Color.rgb(255, 255, 255));
		dw.setFontSize(12);

		radarText(dw, MixUtils.formatDist(DefaultRadius * 1000), rx
				+ RadarPoints.RADIUS, ry + RadarPoints.RADIUS * 2 - 10, false);
		radarText(dw, "" + bearing + ((char) 176) + " " + dirTxt, rx
				+ RadarPoints.RADIUS, ry - 5, true);
	}
	
	private void radarText(PaintScreen dw, String txt, float x, float y, boolean bg) {
		float padw = 4, padh = 2;
		float w = dw.getTextWidth(txt) + padw * 2;
		float h = dw.getTextAsc() + dw.getTextDesc() + padh * 2;
		if (bg) {
			dw.setColor(Color.rgb(0, 0, 0));
			dw.setFill(true);
			dw.paintRect(x - w / 2, y - h / 2, w, h);
			dw.setColor(Color.rgb(255, 255, 255));
			dw.setFill(false);
			dw.paintRect(x - w / 2, y - h / 2, w, h);
		}
		dw.paintText(padw + x - w / 2, padh + dw.getTextAsc() + y - h / 2, txt,
				false);
	}
	
	private void callRefreshToast(){
		mainContext.getMainFrameStatus().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(
						mainContext,mainContext.getResources().getString(R.string.refreshing),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void cancelRefreshTimer() {
		if (refresh != null) {
			refresh.cancel();
		}
	}

	private void handleKeyEvent(KeyEvent evt) {
		/** Adjust marker position with keypad */
		final float CONST = 10f;
		switch (evt.keyCode) {
		case KEYCODE_DPAD_LEFT:
			addX -= CONST;
			break;
		case KEYCODE_DPAD_RIGHT:
			addX += CONST;
			break;
		case KEYCODE_DPAD_DOWN:
			addY += CONST;
			break;
		case KEYCODE_DPAD_UP:
			addY -= CONST;
			break;
		case KEYCODE_DPAD_CENTER:
			isFrozen = !isFrozen;
			break;
		case KEYCODE_CAMERA:
			isFrozen = !isFrozen;
			break; // freeze the overlay with the camera button
		default: //if key is set, then ignore event
				break;
		}
	}

	private boolean handleClickEvent(ClickEvent evt) {
		boolean evtHandled = false;

		// Handle event
		if (state.nextLStatus == ActState.DONE) {
			// the following will traverse the markers in ascending order (by
			// distance) the first marker that
			// matches triggers the event.
			//TODO handle collection of markers. (what if user wants the one at the back)
			for (int i = 0; i < dataHandler.getMarkerCount() && !evtHandled; i++) {
				Marker pm = dataHandler.getMarker(i);
				evtHandled = pm.fClick(evt.x, evt.y, mainContext, state);
			}
		}
		return evtHandled;
	}

	public void clickEvent(float x, float y) {
		synchronized (uiArray) {
			uiArray.add(new ClickEvent(x, y));
		}
	}

	public void keyEvent(int keyCode) {
		synchronized (uiArray) {
			uiArray.add(new KeyEvent(keyCode));
		}
	}
	
	public void clearEvents() {
		synchronized (uiArray) {
			uiArray.clear();
		}
	}
	
}