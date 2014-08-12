package com.example.fypv2;

import android.app.Application;
import android.content.Context;

/*
 * Store information about the maincontext and also the application context
 */


public class AppContextHelper extends Application{
	
	private static MainContext mainContext;
	private static Context context;
	
    public void onCreate(){
        super.onCreate();
       context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
    
	public static MainContext getMainContext() {
		return mainContext;
	}
	
	public static void setMainContext(MainContext ctx) {
		mainContext = ctx;
	}

}
