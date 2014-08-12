package com.example.fypv2;



import static android.hardware.SensorManager.SENSOR_DELAY_GAME;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.mixare.lib.gui.PaintScreen;
import org.mixare.lib.marker.Marker;
import org.mixare.lib.render.Matrix;

import ru.biovamp.widget.CircleLayout;

import com.example.fypv2.camera.CameraCompatibility;
import com.example.fypv2.camera.CameraScreen;
import com.example.fypv2.datasource.DataHandler;
import com.example.fypv2.datasource.DataSourceList;
import com.example.fypv2.map.MapRoute;
import com.example.fypv2.map.PathGoogleMapActivity;
import com.example.fypv2.updatelocation.UpdateLocation_GPS;


public class MainFrame extends Activity implements SensorEventListener, OnTouchListener {

	public static final String TAG ="MobileLocator";
	public static final String PREFS_NAME = "MyPrefsFileForMenuItems";
	
    private final String[] viewmode = {"AR Mode", "Map Mode", "List Mode"};
	private final int MENU_REQUEST_CODE =1;
	private final int SHARE_REQUEST_CODE =2;
	private final int MAP_REQUEST_CODE =3;
	
	private MainContext mainContext = new MainContext(this);;
	private EnvSimpleData SimData;
	private EnvAbstractData AbsData;
	
	private CameraScreen CamScreen;
	private AugmentedScreen AugScreen;
	
	private static PaintScreen PaintTool;		//tool for painting the screen
	private static DataDrawer DataTool;			//tool for generates data into screen
	
	private boolean IsInited = false;
	

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try
        {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        
			SimData = mainContext.getEnvSimData();
			AbsData = mainContext.getEnvAbsData();
			
			final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			SimData.setmWakeLock(pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag"));
			
			InitDataSource();
			InitLocation();
			InitDownloadManager();			    	//initialize thread to start download data
			
			InitCamScreen();	
			InitARScreen();
        	//initializeZoomBar();	
			
			if (!IsInited) {
				//getMixViewData().setMixContext(new MixContext(this));
				//getMixViewData().getMixContext().setDownloadManager(new DownloadManager(mixViewData.getMixContext()));
				setPaintScreen(new PaintScreen());
				setDataDrawer(new DataDrawer(getMainContext()));

				/* set the radius in data view to the last selected by the user */
				//setZoomLevel();
				IsInited = true;
			}
			
			Intent menu = new Intent(MainFrame.this, MainActivity.class);
			startActivityForResult(menu, MENU_REQUEST_CODE);
			
		} catch (Exception ex) {
				//doError(ex);
		}
    }
	
	protected void onPause() {
		super.onPause();

		try {
			this.SimData.getmWakeLock().release();

			try {
				SimData.getSensorMgr().unregisterListener(this,
						SimData.getSensorGrav());
				SimData.getSensorMgr().unregisterListener(this,
						SimData.getSensorMag());
				SimData.setSensorMgr(null);
				
				//AbsData.getLocationFinder().switchOff();
				//AbsData.getDownloadManager().switchOff();

				if (getDataDrawer() != null) {
					getDataDrawer().cancelRefreshTimer();
				}
			} catch (Exception ignore) {
			}
			/*
			if (fError) {
				finish();
			}*/
		} catch (Exception ex) {
			//doError(ex);
		}
	}
    

	@Override
	protected void onResume() {
		super.onResume();
		
		try {
			this.SimData.getmWakeLock().acquire();

			//killOnError();
			getMainContext().resumeMainFrame(this);

			getDataDrawer().refresh();
			getDataDrawer().doStart();
			getDataDrawer().clearEvents();

			AbsData.getDataSourceManager().refreshDataSources();
			//AbsData.getLocationFinder().switchOn();
			//AbsData.getDownloadManager().switchOn();
			
			float angleX, angleY;

			int marker_orientation = -90;

			int rotation = CameraCompatibility.getRotation(this);
			
			// display text from left to right and keep it horizontal
			angleX = (float) Math.toRadians(marker_orientation);
			SimData.getM1().set(1f, 0f, 0f, 0f,
					(float) FloatMath.cos(angleX),
					(float) -FloatMath.sin(angleX), 0f,
					(float) FloatMath.sin(angleX),
					(float) FloatMath.cos(angleX));
			angleX = (float) Math.toRadians(marker_orientation);
			angleY = (float) Math.toRadians(marker_orientation);
			if (rotation == 1) {
				SimData.getM2().set(1f, 0f, 0f, 0f,
						(float) FloatMath.cos(angleX),
						(float) -FloatMath.sin(angleX), 0f,
						(float) FloatMath.sin(angleX),
						(float) FloatMath.cos(angleX));
				SimData.getM3().set((float) FloatMath.cos(angleY), 0f,
						(float) FloatMath.sin(angleY), 0f, 1f, 0f,
						(float) -FloatMath.sin(angleY), 0f,
						(float) FloatMath.cos(angleY));
			} else {
				SimData.getM2().set((float) FloatMath.cos(angleX), 0f,
						(float) FloatMath.sin(angleX), 0f, 1f, 0f,
						(float) -FloatMath.sin(angleX), 0f,
						(float) FloatMath.cos(angleX));
				SimData.getM3().set(1f, 0f, 0f, 0f,
						(float) FloatMath.cos(angleY),
						(float) -FloatMath.sin(angleY), 0f,
						(float) FloatMath.sin(angleY),
						(float) FloatMath.cos(angleY));

			}

			SimData.getM4().toIdentity();

			for (int i = 0; i < SimData.getHistR().length; i++) {
				SimData.getHistR()[i] = new Matrix();
			}

			SimData.setSensorMgr((SensorManager) getSystemService(SENSOR_SERVICE));

			SimData.setSensors(SimData.getSensorMgr().getSensorList(Sensor.TYPE_ACCELEROMETER));
			if (SimData.getSensors().size() > 0) {
				SimData.setSensorGrav(SimData.getSensors().get(0));
			}

			SimData.setSensors(SimData.getSensorMgr().getSensorList(Sensor.TYPE_MAGNETIC_FIELD));
			if (SimData.getSensors().size() > 0) {
				SimData.setSensorMag(SimData.getSensors().get(0));
			}
			
			SimData.getSensorMgr().registerListener(this,SimData.getSensorGrav(), SENSOR_DELAY_GAME);
			SimData.getSensorMgr().registerListener(this,SimData.getSensorMag(), SENSOR_DELAY_GAME);

			try {
				GeomagneticField gmf = AbsData.getLocationFinder().getGeomagneticField(); 
				angleY = (float) Math.toRadians(-gmf.getDeclination());
				SimData.getM4().set((float) FloatMath.cos(angleY), 0f,
						(float) FloatMath.sin(angleY), 0f, 1f, 0f,
						(float) -FloatMath.sin(angleY), 0f,
						(float) FloatMath.cos(angleY));
			} catch (Exception ex) {
				Log.d("mixare", "GPS Initialize Error", ex);
			}

			//SimData.getMixContext().getDownloadManager().switchOn();
			//SimData.getMixContext().getLocationFinder().switchOn();
		} catch (Exception ex) {
			//doError(ex);
			try {
				if (SimData.getSensorMgr() != null) {
					SimData.getSensorMgr().unregisterListener(this,
							SimData.getSensorGrav());
					SimData.getSensorMgr().unregisterListener(this,
							SimData.getSensorMag());
					SimData.setSensorMgr(null);
				}

				if (AbsData != null) {
					AbsData.getLocationFinder().switchOff();
					AbsData.getDownloadManager().switchOff();
				}
			} catch (Exception ignore) {
			}
		}

		Log.d("-------------------------------------------", "resume");
		if (getDataDrawer().IsFrozen() && SimData.getSearchNotificationTxt() == null) {
			SimData.setSearchNotificationTxt(new TextView(this));
			SimData.getSearchNotificationTxt().setWidth(
					getPaintScreen().getWidth());
			SimData.getSearchNotificationTxt().setPadding(10, 2, 0, 0);
			SimData.getSearchNotificationTxt().setText(
					getString(R.string.search_active_1) + " "
							+ DataSourceList.getDataSourcesStringList()
							+ getString(R.string.search_active_2));
			;
			SimData.getSearchNotificationTxt().setBackgroundColor(
					Color.DKGRAY);
			SimData.getSearchNotificationTxt().setTextColor(Color.WHITE);
			SimData.getSearchNotificationTxt().setOnTouchListener(this);
			
			addContentView(SimData.getSearchNotificationTxt(),
					new LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));
		} else if (!getDataDrawer().IsFrozen()
				&& SimData.getSearchNotificationTxt() != null) {
			SimData.getSearchNotificationTxt().setVisibility(View.GONE);
			SimData.setSearchNotificationTxt(null);
		}
	}   
    
    private void InitDataSource() {
		AbsData.getDataSourceManager().refreshDataSources();

		if (!AbsData.getDataSourceManager().isAtLeastOneDatasourceSelected()) {
			Matrix x= new Matrix();
			x.toIdentity();
			mainContext.updateSmoothRotation(x);
		}
    	
	}      
    
    private void InitLocation() {
    	AbsData.getLocationFinder().switchOn();
    	AbsData.getLocationFinder().findLocation();
    	
	} 
    
    private void InitDownloadManager() {
    	AbsData.getDownloadManager().switchOn();
	}  
    
	private void InitCamScreen()	{
        if (CamScreen == null) {
        	CamScreen = new CameraScreen(this);	
        }   		
        setContentView(CamScreen);
    }
	
    private void InitARScreen() {
    	if (AugScreen == null) {
    			AugScreen = new AugmentedScreen(this);
    	}
    	addContentView(AugScreen, new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	
	public static void setPaintScreen(PaintScreen PSPara) {
		MainFrame.PaintTool = PSPara;
	}
	
	public static PaintScreen getPaintScreen() {
	    return PaintTool;
	}
	
	public static void setDataDrawer(DataDrawer DTPara) {
		MainFrame.DataTool = DTPara;
	}
 
	public static DataDrawer getDataDrawer() {
	    return DataTool;
	}
	
	public MainContext getMainContext() {
		return mainContext;
	}
	
	public boolean isZoombarVisible() {
		return SimData.getMyZoomBar() != null
				&& SimData.getMyZoomBar().getVisibility() == View.VISIBLE;
	}
	
	public void refresh(){
		DataTool.refresh();
	}
    /*
	private void initializeZoomBar() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		FrameLayout frameLayout = createZoomBar(settings);
	
		addContentView(frameLayout, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM));
	}
	
	private FrameLayout createZoomBar(SharedPreferences settings) {
		getEnvData().setMyZoomBar(new SeekBar(this));
		getEnvData().getMyZoomBar().setMax(100);
		getEnvData().getMyZoomBar()
				.setProgress(settings.getInt("zoomLevel", 65));
		//getEnvData().getMyZoomBar().setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);
		getEnvData().getMyZoomBar().setVisibility(View.INVISIBLE);

		FrameLayout frameLayout = new FrameLayout(this);

		frameLayout.setMinimumWidth(3000);
		frameLayout.addView(getEnvData().getMyZoomBar());
		frameLayout.setPadding(10, 0, 10, 10);
		return frameLayout;
	}
	*/

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
				&& accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE
				&& SimData.getCompassErrorDisplayed() == 0) {
			for (int i = 0; i < 2; i++) {
				Toast.makeText(getMainContext(),
						"Compass data unreliable. Please recalibrate compass.",
						Toast.LENGTH_LONG).show();
			}
			SimData.setCompassErrorDisplayed(SimData
					.getCompassErrorDisplayed() + 1);
		}
	}


	@Override
	public void onSensorChanged(SensorEvent evt) {
		try {

			if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				SimData.getGrav()[0] = evt.values[0];
				SimData.getGrav()[1] = evt.values[1];
				SimData.getGrav()[2] = evt.values[2];

				AugScreen.postInvalidate();
			} else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				SimData.getMag()[0] = evt.values[0];
				SimData.getMag()[1] = evt.values[1];
				SimData.getMag()[2] = evt.values[2];

				AugScreen.postInvalidate();
			}

			SensorManager.getRotationMatrix(SimData.getRTmp(),
					SimData.getI(), SimData.getGrav(),
					SimData.getMag());

			int rotation = CameraCompatibility.getRotation(this);

			if (rotation == 1) {
				SensorManager.remapCoordinateSystem(SimData.getRTmp(),
						SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z,
						SimData.getRot());
			} else {
				SensorManager.remapCoordinateSystem(SimData.getRTmp(),
						SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z,
						SimData.getRot());
			}
			SimData.getTempR().set(SimData.getRot()[0],
					SimData.getRot()[1], SimData.getRot()[2],
					SimData.getRot()[3], SimData.getRot()[4],
					SimData.getRot()[5], SimData.getRot()[6],
					SimData.getRot()[7], SimData.getRot()[8]);

				SimData.getFinalR().toIdentity();
				SimData.getFinalR().prod(SimData.getM4());
				SimData.getFinalR().prod(SimData.getM1());
				SimData.getFinalR().prod(SimData.getTempR());
				SimData.getFinalR().prod(SimData.getM3());
				SimData.getFinalR().prod(SimData.getM2());
				SimData.getFinalR().invert();

				SimData.getHistR()[SimData.getrHistIdx()].set(SimData
						.getFinalR());
				SimData.setrHistIdx(SimData.getrHistIdx() + 1);
				if (SimData.getrHistIdx() >= SimData.getHistR().length)
					SimData.setrHistIdx(0);

				SimData.getSmoothR().set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
				for (int i = 0; i < SimData.getHistR().length; i++) {
					SimData.getSmoothR().add(SimData.getHistR()[i]);
				}
				SimData.getSmoothR().mult(
						1 / (float) SimData.getHistR().length);

				mainContext.updateSmoothRotation(SimData.getSmoothR());
		} catch (Exception ex) {
			Log.v("eror sensor changed",ex.toString());
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		try {

			float xPress = me.getX();
			float yPress = me.getY();
			if (me.getAction() == MotionEvent.ACTION_UP) {
				getDataDrawer().clickEvent(xPress, yPress);
			}//TODO add gesture events (low)

			return true;
		} catch (Exception ex) {
			// doError(ex);
			ex.printStackTrace();
			return super.onTouchEvent(me);
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int base = Menu.FIRST;
		/* define the first */
		MenuItem item1 = menu.add(base, base, base,
				getString(R.string.menu_item_1));
		MenuItem item2 = menu.add(base, base + 1, base + 1,
				getString(R.string.menu_item_2));

		
		/* assign icons to the menu items */
		item1.setIcon(android.R.drawable.ic_menu_edit);	
		item2.setIcon(android.R.drawable.ic_menu_rotate);	
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case 1:{
			Intent menu = new Intent(MainFrame.this, MainActivity.class);
			startActivityForResult(menu, MENU_REQUEST_CODE);
			break;
		}

		case 2:{
			Menu_ResetAll();
			refresh();
			break;
		}
		}
		return true;
	}

	protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == MENU_REQUEST_CODE) {
			if (data.hasExtra("search") && data.getBooleanExtra("search", false) == true)
			     Menu_Search();
			else if (data.hasExtra("category") && data.getBooleanExtra("category", false) == true)
			     Menu_Category();
			else if (data.hasExtra("viewMode") && data.getBooleanExtra("viewMode", false) == true)
			     Menu_ViewMode();
			else if (data.hasExtra("displayPath") && data.getBooleanExtra("displayPath", false) == true)
			     Menu_DisplayPath();
			else if (data.hasExtra("updateLocation") && data.getBooleanExtra("updateLocation", false) == true)
			     Menu_UpdateLocation();
			else if (data.hasExtra("exit") && data.getBooleanExtra("exit", false) == true)
			     Menu_Exit();	
		 }	
	}
	
	public void Menu_Search(){
		LayoutInflater li = LayoutInflater.from(mainContext);
		View promptsView = li.inflate(R.layout.searchview, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainContext);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText keyword = (EditText) promptsView
				.findViewById(R.id.inputSearch);
		final Spinner category = (Spinner) promptsView
				.findViewById(R.id.inputCategory);
		
		String[] array_spinner = mainContext.getResources().getStringArray(R.array.locationcategory);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.locationcategory, R.layout.spinner_item);
		//new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_spinner);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		category.setAdapter(adapter);
       
		// set dialog message
		alertDialogBuilder
			.setCancelable(true)
			.setPositiveButton("Search",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				
			    // gettext return empty string if not value is inserted 
				String searchString = keyword.getText().toString();
				String searchCategory = category.getSelectedItem().toString();
				SearchHelper(searchString, searchCategory);
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	private void SearchHelper(String search, String category) {
		if (search != null && category !=null ) {
			if (!getDataDrawer().IsFrozen()) {
				getDataDrawer().setisFrozen(true);
				getDataDrawer().refresh();
				getDataDrawer().doStart();
				getDataDrawer().clearEvents();
				
				SimData.setSearchString(search);
				SimData.setSearchCategory(category);

				AbsData.getDataSourceManager().refreshDataSources();
				getDataDrawer().setisFrozen(false);
				mainContext.showToast("Please wait for server to retrieve data!");
			}
		}
	}
	
	private void Menu_Category() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(mainContext);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Category: ");
        
        final String[] category = mainContext.getResources().getStringArray(R.array.locationcategory);
        builderSingle.setItems(category, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
        		SearchHelper("",category[which]);	//empty string used to prevent overloading
        	}
       	});
        builderSingle.show();
     }

    private void Menu_ViewMode() {
		AlertDialog.Builder builderSingle2 = new AlertDialog.Builder(mainContext);
        builderSingle2.setIcon(R.drawable.ic_launcher);
        builderSingle2.setTitle("Select View Mode: ");
        

        builderSingle2.setItems(viewmode, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
        		ViewModeHelper(viewmode[which]);	//empty string used to prevent overloading
        	}
       	});
        builderSingle2.show();

	}
    
    private void ViewModeHelper(String mode) {
    	if (mode.equals("AR Mode"))
        	//doing nothing, the default implementation already do all its works!
       		return;	
    	else if (mode.equals("Map Mode")) {
        	Intent MapIntent =  new Intent(MainFrame.this, PathGoogleMapActivity.class);
        	startActivityForResult(MapIntent,MAP_REQUEST_CODE);
        	
    		//CircleLayout layout = CreateMapLayout();
    		//addContentView(layout, new CircleLayout.LayoutParams( 100, 100));
    		//LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
    	}
    	else if (mode.equals("List Mode")) {
    		ListModeHelper(false);
    	
    	}
    }
    
	private void Menu_DisplayPath() {
		ListModeHelper(true);
	}
	
	private void ListModeHelper(final boolean routeRequest) {
		
		String[] temp;
		
		if (DataTool.IsFrozen())
			return;
		int retry=0;
		while (retry <=3) {
			retry++;
			final DataHandler jLayer = DataTool.getDataHandler();
			
			if (jLayer.getMarkerCount() >0) {
				//add all marker
				temp = new String[jLayer.getMarkerCount()];
				for (int i = 0; i < jLayer.getMarkerCount(); i++) {
					Marker ma = jLayer.getMarker(i);
					if(ma.isActive()) {
						temp[i] = ma.getTitle();
					}
				}
				
				final String [] markerName = temp;
				
				AlertDialog.Builder builderSingle2 = new AlertDialog.Builder(mainContext);
		        builderSingle2.setIcon(R.drawable.ic_launcher);
		        builderSingle2.setTitle("Available Location: ");
		        
		        //display marker
		        builderSingle2.setItems(markerName, new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int which) {
		        		if (routeRequest == true) {
		        			
		        			// get the latitude and longitude
		        			for (int i = 0; i < jLayer.getMarkerCount(); i++) {
		        				Marker ma = jLayer.getMarker(i);
		        				if(ma.isActive() && ma.getTitle().equals(markerName[which])) {
		                        	Intent MapIntent =  new Intent(MainFrame.this, PathGoogleMapActivity.class);
		                        	MapIntent.putExtra("searchMarker", markerName[which]);
		                        	MapIntent.putExtra("latitude", ma.getLatitude());
		                        	MapIntent.putExtra("longitude", ma.getLongitude());
		                        	startActivityForResult(MapIntent,MAP_REQUEST_CODE);
		                        	break;
		        				}
		        			}

		        		}
		        	}
		       	});
		        builderSingle2.show();
				break;
			}	
		}	
	}

	private CircleLayout CreateMapLayout() {

		CircleLayout layout = new CircleLayout(mainContext);
		layout.setLayoutMode(CircleLayout.LAYOUT_PIE);		
		layout.setBackgroundColor(Color.WHITE);

		//layout.addView(new MapView(mainContext, 256, mResourceProxy));
		//CircleLayout.setMinimumWidth(3000);
		//frameLayout.addView(getMixViewData().getMyZoomBar());

		layout.setPadding(10, 0, 10, 10);
		return layout;
	}
	
	
	private void Menu_UpdateLocation() {
		AppContextHelper.setMainContext(getMainContext());
		
		Intent shareIntent = new Intent(MainFrame.this, UpdateLocation_GPS.class);
		startActivityForResult(shareIntent, SHARE_REQUEST_CODE);	
	}
	
	private void Menu_Exit() {
		finish();		
	}
	
	
	public void Menu_ResetAll() {
		if (!getDataDrawer().IsFrozen()) {
			getDataDrawer().setisFrozen(true);
			SimData.resetSearch();
			
			getDataDrawer().refresh();
			getDataDrawer().doStart();
			getDataDrawer().clearEvents();
			
			AbsData.getDataSourceManager().refreshDataSources();
			getDataDrawer().setisFrozen(false);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		getDataDrawer().setisFrozen(false);
		if (SimData.getSearchNotificationTxt() != null) {
			SimData.getSearchNotificationTxt().setVisibility(View.GONE);
			SimData.setSearchNotificationTxt(null);
		}
		return false;
	}



	
	
	
		

	

}



