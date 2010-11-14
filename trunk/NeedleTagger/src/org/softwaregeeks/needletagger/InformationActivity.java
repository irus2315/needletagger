package org.softwaregeeks.needletagger;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class InformationActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.information);
		
		new ExceptionReportDialog(this,"Stack : asdfasdf");
	}
}