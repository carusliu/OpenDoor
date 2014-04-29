package com.carusliu.opendoor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;


public class Appstart extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN); //ȫ����ʾ
		// Toast.makeText(getApplicationContext(), "���ӣ��úñ��У�",
		// Toast.LENGTH_LONG).show();
		// overridePendingTransition(R.anim.hyperspace_in,
		// R.anim.hyperspace_out);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// �ж�����ʾ����ҳ����������
			if (SharedPreferencesHelper.getString(
				SharedPreferencesKey.IS_FIRST_USE, "1").equals("1")) {
					SharedPreferencesHelper.putString(
							SharedPreferencesKey.IS_FIRST_USE, "0");
					Intent intent = new Intent();
					intent.setClass(Appstart.this, Whatsnew.class);
					startActivity(intent);
				} else {
					//if (SharedPreferencesHelper.getString(
					//		SharedPreferencesKey.IS_LOGIN, "0").equals("0")) {
					//	SharedPreferencesHelper.putString(
					//			SharedPreferencesKey.IS_FIRST_USE, "1");
						Intent intent = new Intent(Appstart.this, MainActivity.class);
						startActivity(intent);
					//}
				}
				finish();
				//getApplicationContext().getSharedPreferences("opendoor", Context.MODE_PRIVATE).edit();
			}
		}, 1000);
	}
}