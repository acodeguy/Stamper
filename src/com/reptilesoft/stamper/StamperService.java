package com.reptilesoft.stamper;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class StamperService extends Service {
	
	public static Context context;
	public static Context srv_context;
	private final static String TAG = "StamperService";
	//SharedPreferences prefs;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"onCreate()");
	}
	
	@Override
	public void onStart(Intent i, int id) {
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG, "service onStart()");
				
		context = com.reptilesoft.stamper.Stamper.context;
				
		fix_timestamp(com.reptilesoft.stamper.SmsReceiver.timestamp,
				com.reptilesoft.stamper.SmsReceiver.address);
		
		this.stopSelf();
	}
	
	public void fix_timestamp(long timestamp, String address) {
		/*
		 * 1. return all new messages
		 */
		Date dt_msg=new Date(timestamp);
		Time tm_msg=new Time(timestamp);
		//Date dt_now=new Date(SystemClock.currentThreadTimeMillis()); 
		Time tm_now=new Time(System.currentTimeMillis());
		int body_len=0;
		
		
		Cursor cursor_sms=getContentResolver().query(Uri.parse("content://sms/inbox/"),null,"address=\""+address+"\"",null,null);
		if(cursor_sms!=null)
		{
			if(cursor_sms.getCount()>0)
			{
				if(com.reptilesoft.stamper.Constants.LOGGING)
					Log.i(TAG,"cursor count: "+cursor_sms.getCount());
				
				cursor_sms.moveToFirst();
				int thread_id=cursor_sms.getInt(cursor_sms.getColumnIndexOrThrow("thread_id"));
				int msg_id=cursor_sms.getInt(cursor_sms.getColumnIndexOrThrow("_id"));
				String body=cursor_sms.getString(cursor_sms.getColumnIndexOrThrow("body"));
				body_len=body.length();
				
				// if the body length matches the body length of the received message, continue...
				if(body_len==com.reptilesoft.stamper.SmsReceiver.body_len){
					
					if(com.reptilesoft.stamper.Constants.LOGGING)
						Log.i(TAG,"thread_id/msg_id: "+thread_id+"/"+msg_id);
					
					//update it
					ContentValues v=new ContentValues();
					//Date dt= new Date(1262307661);
					//timestamp=1262307661;
					v.put("date", timestamp);
					// adding rx/tx?
					if(com.reptilesoft.stamper.SmsReceiver.body_timestamp_tx==true && com.reptilesoft.stamper.SmsReceiver.body_timestamp_rx==true)
						v.put("body", body+" (tx: "+tm_msg.getHours()+":"+formatTime(tm_msg.getMinutes())+" / rx: "+tm_now.getHours()+":"+formatTime(tm_now.getMinutes())+")");
					// adding just tx only?	
					if(com.reptilesoft.stamper.SmsReceiver.body_timestamp_tx==true && com.reptilesoft.stamper.SmsReceiver.body_timestamp_rx==false)
						//v.put("body", body+" (tx: "+tm_msg.getHours()+":"+formatTime(tm_msg.getMinutes())+")");
						v.put("body", body+" (tx: "+tm_msg.getHours()+":"+formatTime(tm_msg.getMinutes())+")");
					// adding rx only?
					if(com.reptilesoft.stamper.SmsReceiver.body_timestamp_tx==false && com.reptilesoft.stamper.SmsReceiver.body_timestamp_rx==true)
						v.put("body", body+" (rx: "+tm_now.getHours()+":"+formatTime(tm_now.getMinutes())+")");
					// update it
					
					Log.i("","Updating date to "+timestamp);
					try{
						//getContentResolver().update(Uri.parse("content://sms/inbox/"), v, "thread_id="+thread_id+" and body='"+formatMessageBody(body)+"' and read=0", null);
						getContentResolver().update(Uri.parse("content://sms/inbox/"), v, "thread_id="+thread_id+" and _id="+msg_id, null);
					}catch(Exception e){
						if(com.reptilesoft.stamper.Constants.LOGGING){
								Log.e(TAG,e.toString());
								e.printStackTrace();
							}
					}
					v.clear();
				}
				
				if(com.reptilesoft.stamper.Constants.LOGGING)
					Log.i(TAG,"finished fix!");
				}
		}
		cursor_sms.close();
	}
	
	public static String formatTime(int minutes) {
	    
		Log.i(TAG,"minutes length: "+Integer.toString(minutes).length());
		
		if(Integer.toString(minutes).length()==1) // single digit, due to being >0 but <10 minutes past hour
			{
				//Log.i(TAG,"returning minutes: "+"0"+Integer.toString(minutes));
				return "0"+Integer.toString(minutes);
			}
		if(Integer.toString(minutes).length()==2) // double digits, do not modify at all
			{
				//Log.i(TAG,"returning minutes: "+Integer.toString(minutes));
				return Integer.toString(minutes); 
			}
		else {
				//Log.i(TAG,"returning minutes: 00");
				return "00"; // on the hour, return two zeros
			}
	}
	
}
