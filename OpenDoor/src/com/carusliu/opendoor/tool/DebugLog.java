package com.carusliu.opendoor.tool;

import android.util.Log;


public class DebugLog {
	
    private static String TAG = "car_cube";
//    private static boolean ENABLE_LOG = true;        // Release or Debug
//    public static final int LOG_LEVEL = Log.VERBOSE - 1;  // Debug
    private static boolean ENABLE_LOG = false;        // Release or Debug
    public static final int LOG_LEVEL = Log.ASSERT + 1;   // Release

    
    public static void loge(String msg) {
        log(msg, Log.ERROR);
    }
    
    public static void logd(String msg) {
        log(msg, Log.DEBUG);
    }
    
    public static void logw(String msg) {
        log(msg, Log.WARN);
    }
    
    public static void logi(String msg) {
        log(msg, Log.INFO);
    }
    
    public static void logv(String msg) {
        log(msg, Log.VERBOSE);
    }
    
    public static void loge(String tag, String msg) {
        log(tag, msg, Log.ERROR);
    }
    
    public static void logd(String tag, String msg) {
        log(tag, msg, Log.DEBUG);
    }
    
    public static void logw(String tag, String msg) {
        log(tag, msg, Log.WARN);
    }
    
    public static void logi(String tag, String msg) {
        log(tag, msg, Log.INFO);
    }
    
    public static void logv(String tag, String msg) {
        log(tag, msg, Log.VERBOSE);
    }
    
    private static void log(String msg, int level) {
    	log(TAG, msg, level);
    }
    
    private static void log(String tag, String msg, int level) {
    	if(ENABLE_LOG) {
	        if (level < LOG_LEVEL) {
	        	return;
	        }else {
	        	Log.println(level, tag, msg);
	        }
    	}
    }
}
