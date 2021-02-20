package com.reptilesoft.stamper;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		
		// add the pref xml
		addPreferencesFromResource(R.xml.preferences);
	}
}
