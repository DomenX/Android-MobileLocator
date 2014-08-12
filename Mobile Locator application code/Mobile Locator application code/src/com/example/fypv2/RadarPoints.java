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


import org.mixare.lib.marker.Marker;
import org.mixare.lib.gui.PaintScreen;
import org.mixare.lib.gui.ScreenObj;

import com.example.fypv2.datasource.DataHandler;


import android.graphics.Color;

/** Takes care of the small radar in the top left corner and of its points
 * @author daniele
 *
 */
public class RadarPoints implements ScreenObj {
	//mixare version
	public DataDrawer view;											// The screen 
	public float range;												//The radar's range
	public static float RADIUS = 40;								// Radius in pixel on screen
	public static float originX = 0 , originY = 0;					//Position on screen
	public static int radarColor = Color.argb(100, 0, 0, 200);		//color
	
	public void paint(PaintScreen dw) {
		range = view.getDefaultRadius() * 1000;	//radius is in KM
		
		// Draw the radar
		dw.setFill(true);
		dw.setColor(radarColor);
		dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);

		/** put the markers in it */
		float scale = range / RADIUS;

		DataHandler jLayer = view.getDataHandler();

		for (int i = 0; i < jLayer.getMarkerCount(); i++) {
			Marker pm = jLayer.getMarker(i);
			float x = pm.getLocationVector().x / scale;
			float y = pm.getLocationVector().z / scale;

			if (pm.isActive() && (x * x + y * y < RADIUS * RADIUS)) {
				dw.setFill(true);
				
				// For OpenStreetMap the color is changing based on the URL
					dw.setColor(pm.getColour());
				
				dw.paintRect(x + RADIUS - 1, y + RADIUS - 1, 2, 2);
			}
		}
	}

	/** Width on screen */
	public float getWidth() {
		return RADIUS * 2;
	}

	/** Height on screen */
	public float getHeight() {
		return RADIUS * 2;
	}
}

