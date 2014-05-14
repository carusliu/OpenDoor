package com.carusliu.opendoor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.sysconstants.SysConstants;

public class AboutActivity extends Activity {
	private TextView leftText, title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		initView();
	}

	public void initView() {
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		title.setText("关于");
		leftText.setText("<返回");


		leftText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		/*WebView wv_about = (WebView) findViewById(R.id.wv_about);
		WebSettings setting = wv_about.getSettings();
		setting = wv_about.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setBuiltInZoomControls(false);//不允许放大
		//setting.setLightTouchEnabled(true);
		setting.setSupportZoom(false);
		setting.setAllowFileAccess(true);
		//setting.setBlockNetworkImage(true);
		wv_about.loadUrl(SysConstants.ABOUT_URL);*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}
