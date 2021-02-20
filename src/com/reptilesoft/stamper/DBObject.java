package com.reptilesoft.stamper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBObject extends SQLiteOpenHelper {

	/* variables */
	private final String TAG="DBObject";
	private final static String DB_NAME="stamper.db";
	private final String TBL_NAME="inbox";
	private final static int DB_VERSION=2;
	
	public DBObject(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"created db object");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"onCreate()");
		db.execSQL("create table if not exists "+TBL_NAME+"(msg_id integer primary key autoincrement," +
				"body text," +
				"address text," +
				"timestamp real," +
				"received real);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"upgrading db");
		
		db.execSQL("drop table if exists "+TBL_NAME+";");
		
		onCreate(db);
	}
		
	public void writeSMStoDB(String body, String address, long timestamp) {
		/* write an incoming sms to logging db */
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"writing sms...");
		
		SQLiteDatabase db=this.getWritableDatabase();
		try{
			db.execSQL("insert into "+TBL_NAME+"(body,address,[timestamp]) values('"+body+"','"+address+"',"+timestamp+");");
		}catch(Exception e){
			Log.e(TAG,"insert failed: "+e.toString());
			e.printStackTrace();
		}
		db.close();//release
	}
	
	public Cursor returnLog() {
		/* return all msgs in log via cursor */
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"returnLog()");
		
		Cursor c;
		SQLiteDatabase db=this.getReadableDatabase();
		
		c=db.rawQuery("select * from "+TBL_NAME+" order by [timestamp] desc", null);

		db.close();
		
		Log.i(TAG,"returning cursor, count="+c.getCount());
		return c;
	}
	
	public Cursor getMessageDetail(int msg_id) {
		/* get the details from OUR db */
		
		Cursor m_cursor;
		SQLiteDatabase db=this.getReadableDatabase();
		m_cursor=db.rawQuery("select * from "+TBL_NAME+" where msg_id="+msg_id, null);
		db.close();
		
		return m_cursor;
	}
	
	public void clearLog() {
		/* delete all entries on the table */
		
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"clearing log");
		
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("delete from "+TBL_NAME+";");
		db.close();
	}
}
