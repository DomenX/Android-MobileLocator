package com.example.fypv2.updatelocation;

import java.util.ArrayList;

import com.example.fypv2.AppContextHelper;
import com.example.fypv2.EnvAbstractData;
import com.example.fypv2.MainFrame;
import com.example.fypv2.MainContext;
import com.example.fypv2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class UpdateLocationInfo extends Activity  {
	
		private String array_spinner[];
		
		private String latitude = null;
		private String longitude =null;
		private String name = null;
		private String type = null;
		private String desc = null;
		private String city = null;
		private String state = null;
		private String image = null;
		
	    private EditText inputName;
	    private EditText inputDesc;
	    
	    private Spinner inputType;
	    
	    private Button updateLocation;
	    private Button resetInfo;

		
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.updatelocation_info);
	        
	        Intent myIntent = getIntent();
	        latitude = myIntent.getStringExtra("latitude");
	        longitude= myIntent.getStringExtra("longitude");   
	        
	        //initialize graphic variable
	        initGraphicElement();

	    }
	    
		private void initGraphicElement() {

	        // Edit Text
	        inputName = (EditText) findViewById(R.id.inputName);
	        inputDesc = (EditText) findViewById(R.id.inputDesc);
			
			//spinner for input location type
	        inputType = (Spinner) findViewById(R.id.inputSpinnerType);
	         
	        array_spinner = AppContextHelper.getAppContext().getResources().getStringArray(R.array.locationcategory);
	        
	        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.locationcategory, R.layout.spinner_item);
		    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        inputType.setAdapter(adapter);
	        
	        //button
	        updateLocation = (Button) findViewById(R.id.UpdateButon);
	        updateLocation.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	getPartyStarted();
					AppContextHelper.getMainContext().showToast(R.string.update_location);
	            	finish();
	            	
	            }
	        });
	        
	        resetInfo = (Button) findViewById(R.id.ResetButton);
	        resetInfo.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	resetAll();
	            }
	        });     
	    }

		private void getPartyStarted() {
			name = inputName.getText().toString();
			desc = inputDesc.getText().toString();
			type = inputType.getSelectedItem().toString();

			city = "";
			state = "";
			image = "";
			if (latitude !=null && longitude != null && name != null && desc != null && type != null) {
				String[] url = getSourceURL();
				ArrayList <String> fullurl = new ArrayList();
				
				for ( int i =0; i< url.length; i++) {
					//add parameter to the URL
					fullurl.add(constructURLParams(url[i]));			
				}
				new UploadEngine().execute(fullurl);
			}
		}

		private String[] getSourceURL() {
			String[] UploadDatasources = AppContextHelper.getAppContext().getResources().
										getStringArray(R.array.uploaddatasources);
			return UploadDatasources;
		}

		private String constructURLParams(String url) {

			return url + "?name=" + name + "&type=" + type + "&latitude=" + latitude + 
					"&longitude=" + longitude + "&desc=" + desc + "&city=" + city + "&state=" + state + 
					"&image=" + image;
		}

		private void resetAll() {
			// TODO Auto-generated method stub
			inputName.setText("");
			inputDesc.setText("");
			inputType.setSelected(false);
		}
		
}



