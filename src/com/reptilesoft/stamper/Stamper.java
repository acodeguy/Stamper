package com.reptilesoft.stamper;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Stamper extends Activity implements OnClickListener {
	
	private final String TAG = "Stamper";
	public static Context context;
	Button btn_okay,
		btn_view_log_inbox,
		btn_pref;
	ImageButton btn_market_link;
	TextView tv_ver, tv_app_name;
	PackageInfo pi;
	PackageManager pm;
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = getApplicationContext();
        
        btn_okay = (Button)findViewById(R.id.btn_ok); // okay btn
        btn_okay.setOnClickListener(this);
        //btn_view_log_inbox.setOnClickListener(this);
        btn_pref=(Button)findViewById(R.id.btn_pref); // pref btn
        btn_pref.setOnClickListener(this);
        btn_market_link=(ImageButton)findViewById(R.id.btn_market_link); //view all published apps btn 
        btn_market_link.setOnClickListener(this);
        
        // admanager testing
        /*AdManager.setTestDevices( new String[] {                 
        	     AdManager.TEST_EMULATOR,             // Android emulator
        	     "E83D20734F72FB3108F104ABC0FFC738",  // My T-Mobile G1 Test Phone
        	     } );*/
        
        //TODO: get app details, ver # etc
        tv_ver=(TextView)findViewById(R.id.txt_app_ver);
        //tv_app_name=(TextView)findViewById(R.id.txt_app_title);
        /* get the packagemanager */
		pm = getPackageManager();
		try{
			pi=pm.getPackageInfo("com.reptilesoft.stamper",0);
			/* set the ver */
			tv_ver.setText("v"+pi.versionName);
		}catch(NameNotFoundException e){
			e.printStackTrace();
		}
		
		//runTest(0);
		// disable ok button for 5 seconds
		/*btn_okay.setEnabled(false);
		Timer t=new Timer();
		t.schedule(new TimerTask(){
			public void run() {
				if(com.reptilesoft.stamper.Constants.LOGGING)
					Log.i(TAG,"sleeping for a little bit...");
				btn_okay.setEnabled(true);
			}
		}, 5000);*/
		
    }
	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return super.onKeyDown(keyCode, event);
	    }
	    else {
	    	return true;
	    }
	}*/
    
	@Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	if(com.reptilesoft.stamper.Constants.LOGGING)
    		Log.i(TAG, "onDestroy()");
    	
    }

	public void onClick(View v) {
		
		switch(v.getId())
		{
		case R.id.btn_ok:
			finish();
			break;
		//case R.id.btn_view_inbox:
			/* view inbox */
			//Toast.makeText(this, "coming soon...", Toast.LENGTH_SHORT).show();
			//startActivity(new Intent(this,ViewLogInbox.class));
			//break;
		case R.id.btn_pref:
			startActivity(new Intent(this,Preferences.class));
			break;
		case R.id.btn_market_link:
			/* go to market to view all my apps */
			startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://search?q=pub:\"Reptile Soft\"")));
			break;
			default:
				break;
		}
	}
	
	public void runTest(int testValue) {
		
		String longString=Integer.toString(testValue);
		if(com.reptilesoft.stamper.Constants.LOGGING)
			Log.i(TAG,"longString("+longString.length()+"): "+longString);
		Toast.makeText(this, "longString("+longString.length()+"): "+longString, Toast.LENGTH_LONG);
	}
}