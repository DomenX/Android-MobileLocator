/*
package com.example.fypv2;
import java.util.ArrayList;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.OverlayManager;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OSMapActivity extends Activity{
	
	private MapView OSMapView;
	private MainContext context;
	
	//Overlay
	private DirectedLocationOverlay dlocationOverlay;
	private TrafficOverlay trafficOverlay;
	@Override
	protected void onSetupContentView() {
		this.setContentView(R.layout.osmmapview);
		this.OSMapView = (MapView)findViewById(R.id.OsmMap);
		this.OSMapView.setTileSource();
		this.OSMapView.setMapListener(new AndRoadMapListener(this));

        final OverlayManager overlaymanager = this.OSMapView.getOverlayManager();

		// Add a new instance of our fancy Overlay-Class to the MapView. 

		this.dlocationOverlay = new DirectedLocationOverlay(this);
		this.dlocationOverlay.setLocation(context.getEnvAbsData().getLocationFinder().getCurrentLocation());

		this.mTrafficOverlay = new TrafficOverlay(this, new ArrayList<TrafficOverlayItem>(), new OnItemGestureListener<TrafficOverlayItem>(){
			@Override
			public boolean onItemSingleTapUp(final int index, final TrafficOverlayItem item) {
				if(index >= WhereAmIMap.this.mTrafficOverlay.getOverlayItems().size()) {
					throw new IllegalArgumentException();
				}

				final TrafficOverlayItem focusedItem = WhereAmIMap.this.mTrafficOverlay.getFocusedItem();
				if(!item.equals(focusedItem)){
					WhereAmIMap.this.mTrafficOverlay.setFocusedItem(item);
				}else{
					WhereAmIMap.this.mTrafficOverlay.unSetFocusedItem();
				}

				OSMapActivity.this.OSMapView.getController().animateTo(item.getPoint(), AnimationType.MIDDLEPEAKSPEED);

				return true;
			}

            @Override
            public boolean onItemLongPress(final int index, final TrafficOverlayItem item) {
                return true;
            }
		});
		this.mTrafficOverlay.setDrawnItemsLimit(50);
		this.mTrafficOverlay.setFocusItemsOnTap(false);

		this.mAASOverlay = new AreaOfInterestOverlay(this);
		this.mAASOverlay.setDrawnAreasLimit(10);
        this.mPOIOverlay = new CircleOverlay(this);
		this.mFFOverlay = new CircleOverlay(this);
		this.mFavoriteOverlay = new BitmapOverlay(this);
        this.mOsmBugOverlay = new CircleOverlay(this);
        this.mMapAnnotationsOverlay = new CircleOverlay(this);
		this.mAreaOfAvoidingsOverlay = new AreaOfInterestOverlay(this, this.mAvoidAreas);
        this.mFlagsOverlay = new BitmapOverlay(this);

		// SetNavPoints-Overlay. 
		this.mCrosshairOverlay = new OSMMapViewCrosshairOverlay(this, Color.BLACK, 2, 17);
		this.mCrosshairOverlay.setEnabled(false);
		this.mStartFlagItem = new BitmapItem(null, this, R.drawable.flag_start, null, new Point(18,47));
		this.mDestinationFlagItem = new BitmapItem(null, this, R.drawable.flag_destination, null, new Point(18,47));
        this.mFlagsOverlay.getBitmapItems().add(this.mStartFlagItem);
        this.mFlagsOverlay.getBitmapItems().add(this.mDestinationFlagItem);
		this.mNavPointsConnectionLineOverlay = new OSMMapViewSimpleLineOverlay(this);
		this.mNavPointsConnectionLineOverlay.setPaintNormal();
		this.mNavPointsConnectionLineOverlay.setEnabled(false);

		overlaymanager.add(this.mAASOverlay);
        overlaymanager.add(this.mPOIOverlay);
		overlaymanager.add(this.mFFOverlay);
		overlaymanager.add(this.mFavoriteOverlay);
        overlaymanager.add(this.mOsmBugOverlay);
        overlaymanager.add(this.mMapAnnotationsOverlay);
		overlaymanager.add(this.mAreaOfAvoidingsOverlay);
		overlaymanager.add(this.mTrafficOverlay);
		overlaymanager.add(this.mNavPointsConnectionLineOverlay);
		overlaymanager.add(this.mFlagsOverlay);
		overlaymanager.add(this.mMyLocationOverlay);
		overlaymanager.add(this.mCrosshairOverlay);

        mapAnnotationDB = new MapAnnotationsDBManager(this);
	}

	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);
        // Call this method to turn off hardware acceleration at the View level.
        // setHardwareAccelerationOff();
        return mMapView;
    }
}
*/