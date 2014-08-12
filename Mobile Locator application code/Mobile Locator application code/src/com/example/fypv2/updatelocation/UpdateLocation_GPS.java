package com.example.fypv2.updatelocation;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.fypv2.AppContextHelper;
import com.example.fypv2.MainFrame;
import com.example.fypv2.MainContext;
import com.example.fypv2.R;

public class UpdateLocation_GPS extends Activity {
	
    private EditText inputLatitude;
    private EditText inputLongitude;
    
    private Button UpdateGPSButton;
    private Button BackButton;
    private Button NextButton;
	
    private String latitude = null;
    private String longitude = null;
    
    private MainContext context;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.updatelocation_gps);
       
        //initialize graphic variable
        initGraphicElement();
        context = AppContextHelper.getMainContext();
        
        updateCurrentLocation();

    }
    
	public void updateCurrentLocation() {
	
		Location locationData = context.getEnvAbsData().getLocationFinder().getCurrentLocation();
		
		latitude = String.valueOf(locationData.getLatitude());
		longitude = String.valueOf(locationData.getLongitude());
			
		inputLatitude.setText(latitude);
		inputLongitude.setText(longitude);
	}

	private void initGraphicElement() {

        // Edit Text
        inputLatitude = (EditText) findViewById(R.id.inputLatitude);
        inputLongitude = (EditText) findViewById(R.id.inputLongitude);
		
        //button
        UpdateGPSButton = (Button) findViewById(R.id.UpdateGPSButton);
        UpdateGPSButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                updateCurrentLocation();
            }
        });
        
        BackButton = (Button) findViewById(R.id.BackButton);
        BackButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
            	finish();
            }
        });     

        NextButton = (Button) findViewById(R.id.NextButton);
        NextButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
 
            	if (latitude != null && longitude != null) {
            		Intent intent = new Intent(UpdateLocation_GPS.this,UpdateLocationInfo.class);
            		
            		intent.putExtra("latitude", latitude);
            		intent.putExtra("longitude", longitude);
            		
            		startActivity(intent);
            		finish();
            	}
            	else {
            		context.showToast(R.string.update_gps_please);
            	}
            }
        });
	}
}


