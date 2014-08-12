package com.example.fypv2;

import java.util.List;


import org.mixare.lib.render.Matrix;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.PowerManager.WakeLock;
import android.widget.SeekBar;
import android.widget.TextView;

public class EnvSimpleData {
	
		private final MainContext mixContext;
		private float[] RTmp;
		private float[] Rot;
		private float[] I;
		private float[] grav;
		private float[] mag;
		private SensorManager sensorMgr;
		private List<Sensor> sensors;
		private Sensor sensorGrav;
		private Sensor sensorMag;
		private int rHistIdx;
		private Matrix tempR;
		private Matrix finalR;
		private Matrix smoothR;
		private Matrix[] histR;
		private Matrix m1;
		private Matrix m2;
		private Matrix m3;
		private Matrix m4;
		private SeekBar myZoomBar;
		private WakeLock mWakeLock;
		private int compassErrorDisplayed;
		private String zoomLevel;
		private int zoomProgress;
		private TextView searchNotificationTxt;
	
		private String searchString =   null;
		private String searchCategory = null;
		
		public EnvSimpleData(MainContext mixContext) {
			this.mixContext=mixContext;
			this.RTmp = new float[9];
			this.Rot = new float[9];
			this.I = new float[9];
			this.grav = new float[3];
			this.mag = new float[3];
			this.rHistIdx = 0;
			this.tempR = new Matrix();
			this.finalR = new Matrix();
			this.smoothR = new Matrix();
			this.histR = new Matrix[60];
			this.m1 = new Matrix();
			this.m2 = new Matrix();
			this.m3 = new Matrix();
			this.m4 = new Matrix();
			this.compassErrorDisplayed = 0;
		}

		/* ******* Getter and Setters ********** */
		public void resetSearch() {
			setSearchString(null);
			setSearchCategory(null);
		}
		
		
		public void setSearchString(String search){
				this.searchString = search;
		}
		
		public void setSearchCategory(String category) {
				this.searchCategory = category;

		}

		public String getSearcString() {
			return searchString;
		}
		
		public String getSearcCategory() {
			return searchCategory;
		}

		
		
		
		public MainContext getMyContext() {
			return mixContext;
		}

		public float[] getRTmp() {
			return RTmp;
		}

		public void setRTmp(float[] rTmp) {
			RTmp = rTmp;
		}

		public float[] getRot() {
			return Rot;
		}

		public void setRot(float[] rot) {
			Rot = rot;
		}

		public float[] getI() {
			return I;
		}

		public void setI(float[] i) {
			I = i;
		}

		public float[] getGrav() {
			return grav;
		}

		public void setGrav(float[] grav) {
			this.grav = grav;
		}

		public float[] getMag() {
			return mag;
		}

		public void setMag(float[] mag) {
			this.mag = mag;
		}

		public SensorManager getSensorMgr() {
			return sensorMgr;
		}

		public void setSensorMgr(SensorManager sensorMgr) {
			this.sensorMgr = sensorMgr;
		}

		public List<Sensor> getSensors() {
			return sensors;
		}

		public void setSensors(List<Sensor> sensors) {
			this.sensors = sensors;
		}

		public Sensor getSensorGrav() {
			return sensorGrav;
		}

		public void setSensorGrav(Sensor sensorGrav) {
			this.sensorGrav = sensorGrav;
		}

		public Sensor getSensorMag() {
			return sensorMag;
		}

		public void setSensorMag(Sensor sensorMag) {
			this.sensorMag = sensorMag;
		}

		public int getrHistIdx() {
			return rHistIdx;
		}

		public void setrHistIdx(int rHistIdx) {
			this.rHistIdx = rHistIdx;
		}

		public Matrix getTempR() {
			return tempR;
		}

		public void setTempR(Matrix tempR) {
			this.tempR = tempR;
		}

		public Matrix getFinalR() {
			return finalR;
		}

		public void setFinalR(Matrix finalR) {
			this.finalR = finalR;
		}

		public Matrix getSmoothR() {
			return smoothR;
		}

		public void setSmoothR(Matrix smoothR) {
			this.smoothR = smoothR;
		}

		public Matrix[] getHistR() {
			return histR;
		}

		public void setHistR(Matrix[] histR) {
			this.histR = histR;
		}

		public Matrix getM1() {
			return m1;
		}

		public void setM1(Matrix m1) {
			this.m1 = m1;
		}

		public Matrix getM2() {
			return m2;
		}

		public void setM2(Matrix m2) {
			this.m2 = m2;
		}

		public Matrix getM3() {
			return m3;
		}

		public void setM3(Matrix m3) {
			this.m3 = m3;
		}

		public Matrix getM4() {
			return m4;
		}

		public void setM4(Matrix m4) {
			this.m4 = m4;
		}

		public SeekBar getMyZoomBar() {
			return myZoomBar;
		}

		public void setMyZoomBar(SeekBar myZoomBar) {
			this.myZoomBar = myZoomBar;
		}

		public WakeLock getmWakeLock() {
			return mWakeLock;
		}

		public void setmWakeLock(WakeLock mWakeLock) {
			this.mWakeLock = mWakeLock;
		}

		public int getCompassErrorDisplayed() {
			return compassErrorDisplayed;
		}

		public void setCompassErrorDisplayed(int compassErrorDisplayed) {
			this.compassErrorDisplayed = compassErrorDisplayed;
		}

		public String getZoomLevel() {
			return zoomLevel;
		}

		public void setZoomLevel(String zoomLevel) {
			this.zoomLevel = zoomLevel;
		}

		public int getZoomProgress() {
			return zoomProgress;
		}

		public void setZoomProgress(int zoomProgress) {
			this.zoomProgress = zoomProgress;
		}

		public TextView getSearchNotificationTxt() {
			return searchNotificationTxt;
		}

		public void setSearchNotificationTxt(TextView searchNotificationTxt) {
			this.searchNotificationTxt = searchNotificationTxt;
		}

	}
