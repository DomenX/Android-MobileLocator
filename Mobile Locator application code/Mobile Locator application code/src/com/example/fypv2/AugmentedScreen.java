package com.example.fypv2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import com.example.fypv2.MainFrame;

import org.mixare.lib.gui.PaintScreen;


public class AugmentedScreen extends View{
	
	MainFrame mainFrame;
	Paint zoomPaint = new Paint();
	
	
	public AugmentedScreen(Context context) {
		super(context);
		mainFrame = (MainFrame) context;
		/*
		try {
			app = (MixView) context;

			app.killOnError();
		} catch (Exception ex) {
			app.doError(ex);
		}*/
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		try {
			MainFrame.getPaintScreen().setWidth(canvas.getWidth());
			MainFrame.getPaintScreen().setHeight(canvas.getHeight());
			MainFrame.getPaintScreen().setCanvas(canvas);

			//initialize radar line point
			if (!MainFrame.getDataDrawer().IsInited()) {
				MainFrame.getDataDrawer().init(MainFrame.getPaintScreen().getWidth(),
						MainFrame.getPaintScreen().getHeight());
			}

			if (mainFrame.isZoombarVisible()) {
				zoomPaint.setColor(Color.WHITE);
				zoomPaint.setTextSize(14);
				String startKM = "80km";
				String endKM = "0km";

				/*
				 * if(MixListView.getDataSource().equals("Twitter")){ startKM =
				 * "1km"; }
				 * */
				 
				canvas.drawText(startKM, canvas.getWidth() / 100 * 4,
						canvas.getHeight() / 100 * 85, zoomPaint);
				canvas.drawText(endKM, canvas.getWidth() / 100 * 99 + 25,
						canvas.getHeight() / 100 * 85, zoomPaint);

				int height = canvas.getHeight() / 100 * 85;
				int zoomProgress = mainFrame.getMainContext().getEnvSimData().getZoomProgress();
				if (zoomProgress > 92 || zoomProgress < 6) {
					height = canvas.getHeight() / 100 * 80;
				}
				canvas.drawText(mainFrame.getMainContext().getEnvSimData().getZoomLevel(), (canvas.getWidth()) / 100
						* zoomProgress + 20, height, zoomPaint);
			}
			
			MainFrame.getDataDrawer().draw(MainFrame.getPaintScreen());
			
		} catch (Exception ex) {
			
		}
	}//end ondraw
}
