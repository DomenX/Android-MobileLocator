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


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.mixare.lib.MixContextInterface;
import org.mixare.lib.render.Matrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * API for other class to access environment data
 */
public class MainContext extends ContextWrapper implements MixContextInterface {

	private MainFrame mainFrame;
	private EnvSimpleData SimData;	//class contain all variable for the DotMap
	private EnvAbstractData AbsData;
	
	private Matrix rotationM = new Matrix();
	

	
	public MainContext(Context base) {
		super(base);
		mainFrame = (MainFrame) base;

	}

	@Override
	public void loadMixViewWebPage(String url) throws Exception {
		//AbsData.getWebContentManager().loadWebPage(url, getMainFrameStatus());
	}
	
	public void getRM(Matrix dest) {
		synchronized (rotationM) {
			dest.set(rotationM);
		}
	}
	
	public void updateSmoothRotation(Matrix smoothR) {
		synchronized (rotationM) {
			rotationM.set(smoothR);
		}
	}

	private void setMainFrameStatus (MainFrame mainActivity) {
		synchronized (mainFrame) {
			this.mainFrame = mainActivity;
		}
	}
	
	public MainFrame getMainFrameStatus() {
		synchronized (mainFrame) {
			return this.mainFrame;
		}
	}
	
    public EnvSimpleData getEnvSimData() {
		if (SimData == null){
			// TODO: VERY inportant, only one!
			SimData = new EnvSimpleData(this);
		}
		return SimData;
	}
    
    public EnvAbstractData getEnvAbsData() {
		if (AbsData == null){
			// TODO: VERY inportant, only one!
			AbsData = new EnvAbstractData(this);
		}
		return AbsData;
	}

	public ContentResolver getContentResolver() {
		ContentResolver out = super.getContentResolver();
		if (super.getContentResolver() == null) {
			out = getMainFrameStatus().getContentResolver();
		}
		return out;
	}
   
	public boolean DisplayLocationDetails(String name, String imgDirectory, String desc) {

		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.locationdetails, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final TextView titleText = (TextView) promptsView
				.findViewById(R.id.locationtitle);
		final ImageView imageView = (ImageView) promptsView
				.findViewById(R.id.locationimage);
		final TextView descText = (TextView) promptsView
				.findViewById(R.id.locationdesc);
		
		String[] address = this.getResources().getStringArray(R.array.address);
		
		Bitmap image = this.getBitmapFromURL(address[0]+"/"+imgDirectory);

		titleText.setText(name);
		if (image!=null)
			imageView.setImageBitmap(image);

		descText.setText(desc);
		
		// set dialog message
		alertDialogBuilder
			.setCancelable(true)
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog, and change it width
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		
		return true;
		
	}
	
    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
 
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }       
    }

	
	public void resumeMainFrame (MainFrame mainFrame) {
		setMainFrameStatus(mainFrame);
	}
    
    
	public void showToast(final String string){
	       Toast.makeText(this,string,Toast.LENGTH_LONG).show();
	}
	
	public void showToast(int RidOfString) {
		 Toast.makeText(this,this.getString(RidOfString),Toast.LENGTH_LONG).show();
	}

}
