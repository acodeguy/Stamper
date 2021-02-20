/* 
 * 1. intercept message
 * 2. fix message in db
 * 3. update message ts
 */

package com.reptilesoft.stamper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver {
	
	private final String TAG = "SmsReceiver";
	public static long timestamp;
	public static String address;
	public static String body;
	public static String type; // message type, sms or mms
	public static int index=0;
	public static int body_len=0;
	Date smsc_dt;
	Time smsc_tm;
	//private DBObject dbo;
	SharedPreferences prefs;
	int fix_delay=0;
	public static boolean fix_on=false; // does user want app to fix ts?
	public static boolean body_timestamp_tx=false; // does the user want their message bodies stamped tx?
	public static boolean body_timestamp_rx=false; // does the user want their message bodies stamped with rx?
	Context appContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		appContext = context;
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG, "onReceive()");
		
		// get prefs
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		fix_on = prefs.getBoolean("fix_on", false);
		fix_delay = Integer.parseInt(prefs.getString("pref_delay", "3000"));
		body_timestamp_tx=prefs.getBoolean("body_timestamp_tx", true);
		body_timestamp_rx=prefs.getBoolean("body_timestamp_rx",true);
				
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"parsing message...");
		//---get the SMS message passed in---
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs;           
        if (bundle != null)
        {
        	if(com.reptilesoft.stamper.Constants.LOGGING)
        		Log.i(TAG,"bundle found, extracting...");
        	
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                smsc_dt = new Date(msgs[i].getTimestampMillis());
                smsc_tm = new Time(msgs[i].getTimestampMillis());
                
                /* set the timestamp long to new ts, and address too */
                timestamp = msgs[i].getTimestampMillis();
                address = msgs[i].getOriginatingAddress();
                body_len = msgs[i].getMessageBody().toString().length();                
            }
            
            // sleep first
            if(com.reptilesoft.stamper.Constants.LOGGING){
            	Log.i(TAG,"receiver sleeping for "+fix_delay+"ms");
    			Log.i(TAG,"continuing...");
            }
    		
            if(fix_on) // only if use wants fixing will the service start!
            {
            	Timer t = new Timer();
            	t.schedule(new TimerTask() {
            		public void run() {
            			appContext.startService(new Intent(appContext, StamperService.class));
            		}
            	}, fix_delay);
            }
        }
	}
}
