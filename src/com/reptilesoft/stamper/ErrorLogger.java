package com.reptilesoft.stamper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

public class ErrorLogger {
	
	final String TAG="ErrorLogger";

	public void write_log(String error_message) {
		try {
		    File root = Environment.getExternalStorageDirectory();
		    if (root.canWrite()){
		        File logfile = new File(root, "stamperlog/" + new Date(SystemClock.currentThreadTimeMillis()) + "_" + new Time(SystemClock.currentThreadTimeMillis()).toString().replace(":", "") + ".txt");
		        FileWriter logwriter = new FileWriter(logfile);
		        BufferedWriter out = new BufferedWriter(logwriter);
		        out.write(error_message);
		        out.close();
		    }
		} catch (IOException e) {
		    Log.e(TAG, "Could not write file " + e.getMessage());
		}
	}
}
