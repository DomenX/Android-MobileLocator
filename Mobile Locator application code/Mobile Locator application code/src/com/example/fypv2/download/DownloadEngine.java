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
package com.example.fypv2.download;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.mixare.lib.marker.Marker;

import com.example.fypv2.HttpTools;
import com.example.fypv2.MainFrame;
import com.example.fypv2.MainContext;
import com.example.fypv2.dataprocessor.DataConvertor;

import android.util.Log;


class DownloadEngine implements Runnable, DownloadManager {
//mixare version
	private boolean stop = false;
	private MainContext ctx;
	private DownloadManagerState state = DownloadManagerState.Confused;
	private LinkedBlockingDeque<ManagedDownloadRequest> todoList = new LinkedBlockingDeque<ManagedDownloadRequest>();
	private ConcurrentHashMap<String, DownloadResult> doneList = new ConcurrentHashMap<String, DownloadResult>();
	private Executor executor = Executors.newSingleThreadExecutor();
	

	public DownloadEngine(MainContext ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("Mix Context IS NULL");
		}
		this.ctx = ctx;
		state=DownloadManagerState.OffLine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mixare.mgr.downloader.DownloadManager#run()
	 */
	

	public void run() {
		ManagedDownloadRequest mRequest;
		DownloadResult result;
		stop = false;
		while (!stop) {
			state=DownloadManagerState.OnLine;
			// Wait for proceed
			while (!stop) {
				try {
					mRequest = todoList.take();
					state=DownloadManagerState.Downloading;
					result = processRequest(mRequest);
				} catch (InterruptedException e) {
					result = new DownloadResult();
					result.setError(e, null);
				}
				doneList.put(result.getIdOfDownloadRequest(), result);
				state=DownloadManagerState.OnLine;
			}
		}
		state=DownloadManagerState.OffLine;
	}

	private DownloadResult processRequest(ManagedDownloadRequest mRequest) {
		DownloadRequest request = mRequest.getOriginalRequest();
		final DownloadResult result = new DownloadResult();
		try {
			if (request == null) {
				throw new Exception("Request is null");
			}
			
			if (!request.getSource().isWellFormed()) {
				throw new Exception("Datasource in not WellFormed");
			}
			
			String pageContent = HttpTools.getPageContent(request,
					ctx.getContentResolver());
		
			if (pageContent != null) {
				// try loading Marker data
				
				List<Marker> markers = DataConvertor.getInstance().load(
						request.getSource().getUrl(), pageContent,
						request.getSource());
				result.setAccomplish(mRequest.getUniqueKey(), markers,
						request.getSource());
			}
			
		} catch (Exception ex) {
			result.setError(ex, request);
			Log.w(MainFrame.TAG, "ERROR ON DOWNLOAD REQUEST", ex);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mixare.mgr.downloader.DownloadManager#purgeLists()
	 */
	public synchronized void resetActivity() {
		todoList.clear();
		doneList.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mixare.mgr.downloader.DownloadManager#submitJob(org.mixare.mgr.downloader
	 * .DownloadRequest)
	 */
	public String submitJob(DownloadRequest job) {
		String jobId = null;
		if (job != null && job.getSource().isWellFormed()) {
			ManagedDownloadRequest mJob;
			if (!todoList.contains(job)) {
				mJob = new ManagedDownloadRequest(job);
				todoList.add(mJob);
				Log.i(MainFrame.TAG, "Submitted " + job.toString());
				jobId = mJob.getUniqueKey();
			}
		}
		return jobId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mixare.mgr.downloader.DownloadManager#getReqResult(java.lang.String)
	 */
	public DownloadResult getReqResult(String jobId) {
		DownloadResult result = doneList.get(jobId);
		doneList.remove(jobId);
		return result;
	}

	/*	
	 * (non-Javadoc)
	 * 
	 * @see org.mixare.mgr.downloader.DownloadManager#getNextResult()
	 */
	public synchronized DownloadResult getNextResult() {
		DownloadResult result = null;
		if (!doneList.isEmpty()) {
			String nextId = doneList.keySet().iterator().next();
			result = doneList.get(nextId);
			doneList.remove(nextId);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mixare.mgr.downloader.DownloadManager#isDone()
	 */
	public Boolean isDone() {
		return todoList.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mixare.mgr.downloader.DownloadManager#goOnline()
	 */
	public void switchOn() {
		if (DownloadManagerState.OffLine.equals(getState())){
		    executor.execute(this);
		    Log.i("Download Engine: ","Online!");
		}else{
		    Log.i("Download Engine: ","Offline!");
		}
	}

	public void switchOff() {
		stop=true;
	}

	@Override
	public DownloadManagerState getState() {
		return state;
	}

	

}
