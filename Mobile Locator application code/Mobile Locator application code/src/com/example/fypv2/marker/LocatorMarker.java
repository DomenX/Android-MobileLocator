package com.example.fypv2.marker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import org.mixare.lib.HtmlUnescape;
import org.mixare.lib.MixContextInterface;
import org.mixare.lib.MixStateInterface;
import org.mixare.lib.MixUtils;
import org.mixare.lib.gui.PaintScreen;
import org.mixare.lib.gui.TextObj;
import org.mixare.lib.marker.draw.DrawCommand;
import org.mixare.lib.marker.draw.DrawImage;
import org.mixare.lib.marker.draw.DrawTextBox;
import org.mixare.lib.marker.draw.ParcelableProperty;

import com.example.fypv2.AppContextHelper;
import com.example.fypv2.MainContext;
import com.example.fypv2.R;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.os.Parcel;
import android.util.Log;


public class LocatorMarker extends LocalMarker{

	public static final int MAX_OBJECTS=100;
	
	private int rectangleBackgroundColor = Color.WHITE;
	
	public Bitmap image;
	
	//overloading variable just to display the details of location
	private String title;
	private String imgUrl;
	private String desc;
	
	public LocatorMarker(String id, String title, double latitude, double longitude,
			double altitude, String desc, String addr_state,String city, String imgURL, int type, int color) {
		
		super(id, title, latitude, longitude, altitude, imgURL, type, color);
		this.title = title;
		this.imgUrl = imgURL;
		this.desc = desc;
		
		String[] address = AppContextHelper.getAppContext().getResources().getStringArray(R.array.address);
		image = this.getBitmapFromURL(address[0]+"/"+imgUrl);
		
	}

	@Override
	public void update(Location curGPSFix) {
		super.update(curGPSFix);
	}

	@Override
	public void draw(PaintScreen dw) {
		drawTitle(dw);

		try {

			if (image == null) {
				image = BitmapFactory.decodeResource(
						AppContextHelper.getAppContext().getResources(),R.drawable.image_not_found);
			}
			drawImage(dw);
			
			
			
			//drawTextBlock(dw);
		} catch (Exception e) {
			Log.v("exception",e.toString());
		}
		
		

		//drawCircle(dw);


		//drawImage(dw);
		//drawArrow(dw);
		//drawTextBlock(dw);
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
	
	public void drawImage(PaintScreen dw) {
		if (isVisible) {
		//dw.setStrokeWidth(dw.getHeight() / 100f);
		//dw.setFill(false);
		//dw.setColor(rectangleBackgroundColor);
		dw.paintBitmap(image, signMarker.x - (image.getWidth() / 2),
					signMarker.y - (image.getHeight() / 2));
			
		}
	}
	
	public void drawTitle(PaintScreen dw) {
		float maxHeight = Math.round(dw.getHeight() / 10f) + 1;
		
		//TODO: change textblock only when distance changes
		String textStr="";
		double d = distance;
		DecimalFormat df = new DecimalFormat("@#");
		String imageTitle = "";
		if (title.length() > 10){
			imageTitle = title.substring(0, 10) + "...";
		}else {
			imageTitle = title;
		}
		if(d<1000.0) {
			textStr = imageTitle + " ("+ df.format(d) + "m)";			
		}
		else {
			d=d/1000.0;
			textStr = imageTitle + " (" + df.format(d) + "km)";
		}
		textBlock = new TextObj(textStr, Math.round(maxHeight / 2f) + 1,
				250, dw, underline);
	
		if (isVisible) {
			//dw.setColor(DataSource.getColor(type));
			float currentAngle = MixUtils.getAngle(cMarker.x, cMarker.y, signMarker.x, signMarker.y);
			txtLab.prepare(textBlock);
			dw.setStrokeWidth(1f);
			dw.setFill(true);
			dw.paintObj(txtLab, signMarker.x - txtLab.getWidth()
					/ 2, signMarker.y + maxHeight, currentAngle + 90, 1);
		}
	}
	
	public void drawArrow(PaintScreen dw) {
		if (isVisible) {
			float currentAngle = MixUtils.getAngle(cMarker.x, cMarker.y, signMarker.x, signMarker.y);
			float maxHeight = Math.round(dw.getHeight() / 10f) + 1;

			//dw.setColor(DataSource.getColor(type));
			dw.setStrokeWidth(maxHeight / 10f);
			dw.setFill(false);
			
			Path arrow = new Path();
			float radius = maxHeight / 1.5f;
			float x=0;
			float y=0;
			arrow.moveTo(x-radius/3, y+radius);
			arrow.lineTo(x+radius/3, y+radius);
			arrow.lineTo(x+radius/3, y);
			arrow.lineTo(x+radius, y);
			arrow.lineTo(x, y-radius);
			arrow.lineTo(x-radius, y);
			arrow.lineTo(x-radius/3,y);
			arrow.close();
			dw.paintPath(arrow,cMarker.x,cMarker.y,radius*2,radius*2,currentAngle+90,1);			
		}
	}

	@Override
	public boolean fClick(float x, float y, MixContextInterface ctx, MixStateInterface state) {
		boolean evtHandled = false;
		MainContext mainContext = (MainContext) ctx;

		if (isClickValid(x, y)) {
			evtHandled = mainContext.DisplayLocationDetails(title, imgUrl, desc);
		}
		return evtHandled;
	}
	
	
	
	
	@Override
	public int getMaxObjects() {
		return MAX_OBJECTS;
	}


}

