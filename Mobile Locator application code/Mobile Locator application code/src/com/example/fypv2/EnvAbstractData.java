package com.example.fypv2;



import org.mixare.lib.render.Matrix;

import com.example.fypv2.datasource.DataSourceManager;
import com.example.fypv2.datasource.DataSourceManagerFactory;
import com.example.fypv2.download.DownloadManager;
import com.example.fypv2.download.DownloadManagerFactory;
import com.example.fypv2.location.LocationFinder;
import com.example.fypv2.location.LocationFinderFactory;

public class EnvAbstractData {

	private MainContext mainContext;
	
	private LocationFinder locationFinder;
	private DataSourceManager dataSourceManager;
	private DownloadManager downloadManager;

	
	public EnvAbstractData(MainContext mainContext) {
		this.mainContext = mainContext;
	}

	
	public LocationFinder getLocationFinder() {
		if (this.locationFinder == null) {
			locationFinder = LocationFinderFactory.makeLocationFinder(mainContext);
		}
		return locationFinder;
	}
	
	public DataSourceManager getDataSourceManager() {
		if (this.dataSourceManager == null) {
			dataSourceManager = DataSourceManagerFactory.makeDataSourceManager(mainContext);
		}
		return dataSourceManager;
	}
	
	public DownloadManager getDownloadManager() {
		if (this.downloadManager == null) {
			downloadManager = DownloadManagerFactory.makeDownloadManager(mainContext);
			getLocationFinder().setDownloadManager(downloadManager);
		}
		return downloadManager;
	}
	
}
