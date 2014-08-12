package com.example.fypv2;


import com.example.fypv2.updatelocation.UpdateLocationInfo;
import com.example.fypv2.updatelocation.UpdateLocation_GPS;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;



public class MainActivity extends Activity {
	
	
	private ImageButton b1_search;
	private ImageButton b2_category;
	private	ImageButton b3_viewMode;
	private ImageButton b4_displayPath;
	private ImageButton b5_updateLocation;
	private ImageButton b6_exit;
	
	private int execution = -1;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.menu);
		InitGraphicElement();
		
		this.findViewById(R.id.ibtn_menu_search).requestFocus();

		

		
	}

	private void InitGraphicElement() {
		b1_search = (ImageButton) findViewById(R.id.ibtn_menu_search);
		b2_category = (ImageButton) findViewById(R.id.ibtn_menu_category);
		b3_viewMode = (ImageButton) findViewById(R.id.ibtn_menu_view_mode);
		b4_displayPath = (ImageButton) findViewById(R.id.ibtn_menu_display_path);
		b5_updateLocation = (ImageButton) findViewById(R.id.ibtn_menu_update_location);
		b6_exit = (ImageButton) findViewById(R.id.ibtn_menu_exit);
		
		b1_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	MainActivity.this.execution = 1;
                finish();
            }
        });
		
		b2_category.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	MainActivity.this.execution = 2;
                finish();
            }
        });
		
		b3_viewMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	MainActivity.this.execution = 3;
                finish();
            }
        });
		b4_displayPath.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	MainActivity.this.execution = 4;
                finish();
            }
        });
		b5_updateLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	MainActivity.this.execution = 5;
                finish();
            }
        });
		b6_exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	MainActivity.this.execution = 6;
                finish();
            }
        });
        
	}
	
	@Override
	public void finish() {
	  // Prepare data intent 
	  Intent myIntent = new Intent();
	  
	  switch (execution) {
	  	case 1:
	  	  myIntent.putExtra("search", true);
	  	  break;
	  	case 2:
		  myIntent.putExtra("category", true);
		  break;
	  	case 3:
		  myIntent.putExtra("viewMode", true);
		  break;
	  	case 4:
		  myIntent.putExtra("displayPath", true);
		  break;
	  	case 5:
		  myIntent.putExtra("updateLocation", true);
		  break;
	  	case 6:
		  myIntent.putExtra("exit", true);
		  break;
		default:
			break;
	  }

	  // Activity finished ok, return the data
	  setResult(RESULT_OK, myIntent);
	  super.finish();
	} 
}