/*
 * Copyleft 2012 - Peer internet solutions 
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
package com.example.fypv2.datasource;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.fypv2.MainContext;
import com.example.fypv2.download.DownloadRequest;


class DataSourceEngine implements DataSourceManager {
	//mixare version
	private final ConcurrentLinkedQueue<DataSource> allDataSources=new ConcurrentLinkedQueue<DataSource>(); 
	
	private final MainContext mainContext;

	public DataSourceEngine(MainContext mainContext) {
		this.mainContext = mainContext;
	}

	@Override
	public boolean isAtLeastOneDatasourceSelected() {
		boolean atLeastOneDatasourceSelected = false;
		for (DataSource ds : this.allDataSources) {
			if (ds.getEnabled())
				atLeastOneDatasourceSelected = true;
		}
		return atLeastOneDatasourceSelected;
	}



	public void setAllDataSourcesforLauncher(DataSource datasource) {
		this.allDataSources.clear(); // TODO WHY? CLEAN ALL
		this.allDataSources.add(datasource);
	}

	public void refreshDataSources() {
		this.allDataSources.clear();

		DataSourceStorage.getInstance(mainContext).fillDefaultDataSources();

		int size = DataSourceStorage.getInstance().getSize();

		// copy the value from shared preference to adapter
		for (int i = 0; i < size; i++) {
			String fields[] = DataSourceStorage.getInstance().getFields(i);
			this.allDataSources.add(new DataSource(fields[0], fields[1],
					fields[2], fields[3], fields[4]));
		}
	}

	public void requestDataFromAllActiveDataSource(double lat, double lon,
			double alt, float radius) {
		for (DataSource ds : allDataSources) {
			/*
			 * when type is OpenStreetMap iterate the URL list and for selected
			 * URL send data request
			 */
			if (ds.getEnabled()) {
				requestData(ds, lat, lon, alt, radius, Locale.getDefault()
						.getLanguage());
			}
		}

	}
	
	public void requestSearchDataFromAllActiveDataSource(String search , String category , 
			double lat , double lon, double alt , float radius) {

		for (DataSource ds : allDataSources) {
			if (ds.getEnabled()) {
				
				String overallParams =  
						ds.createRequestParams(lat, lon, alt, radius, Locale.getDefault().getLanguage())
						+ ds.createSearchRequestParams(search , category);
				
				DownloadRequest request = new DownloadRequest(ds,overallParams);
				mainContext.getEnvAbsData().getDownloadManager().submitJob(request);
			}

		}
	}
	
	
	private void requestData(DataSource datasource, double lat, double lon,
			double alt, float radius, String locale) {
			DownloadRequest request = new DownloadRequest(datasource,
					datasource.createRequestParams(lat, lon, alt, radius, locale));
			mainContext.getEnvAbsData().getDownloadManager().submitJob(request);

	}
}
