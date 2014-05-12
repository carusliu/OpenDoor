package com.carusliu.opendoor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.carusliu.opendoor.R;


public class Appstart extends Activity {
	private ImageView iv_zmkm, iv_yyy, iv_play1, iv_play2, iv_play3, iv_play4;
	private ImageView playBtn;
	Animation anim_zmkm ;
	Animation anim_yyy ;
	Animation anim_play1;
	Animation anim_play2;
	Animation anim_play3;
	Animation anim_play4;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		
		iv_zmkm = (ImageView)findViewById(R.id.img_lead_zmkm);
		iv_yyy = (ImageView)findViewById(R.id.img_lead_yyy);
		iv_play1 = (ImageView)findViewById(R.id.img_lead_play1);
		iv_play2 = (ImageView)findViewById(R.id.img_lead_play2);
		iv_play3 = (ImageView)findViewById(R.id.img_lead_play3);
		iv_play4 = (ImageView)findViewById(R.id.img_lead_play4);
		playBtn = (ImageView)findViewById(R.id.btn_play);
		playBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iv_zmkm.startAnimation(anim_zmkm);
				iv_yyy.startAnimation(anim_yyy);
				iv_play1.startAnimation(anim_play1);
				iv_play2.startAnimation(anim_play2);
				iv_play3.startAnimation(anim_play3);
				iv_play4.startAnimation(anim_play4);
				playBtn.setVisibility(View.GONE);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// 判断是显示引导页还是主界面
					//if (SharedPreferencesHelper.getString(
					//	SharedPreferencesKey.IS_FIRST_USE, "1").equals("1")) {
					//		SharedPreferencesHelper.putString(
					//				SharedPreferencesKey.IS_FIRST_USE, "0");
							Intent intent = new Intent();
							intent.setClass(Appstart.this, Whatsnew.class);
							startActivity(intent);
					//	} else {

					//			Intent intent = new Intent(Appstart.this, MainActivity.class);
					//			startActivity(intent);
							//}
					//	}
						finish();
					}
				}, 2200);
			}
		});
		/*iv_play1.setOnClickListener(this);
		iv_play2.setOnClickListener(this);
		iv_play3.setOnClickListener(this);
		iv_play4.setOnClickListener(this);*/
		
		anim_zmkm = AnimationUtils.loadAnimation(this, R.anim.lead_zmkm);
		anim_yyy = AnimationUtils.loadAnimation(this, R.anim.lead_yyy);
		anim_play1 = AnimationUtils.loadAnimation(this, R.anim.play1);
		anim_play2 = AnimationUtils.loadAnimation(this, R.anim.play2);
		anim_play3 = AnimationUtils.loadAnimation(this, R.anim.play3);
		anim_play4 = AnimationUtils.loadAnimation(this, R.anim.play4);
		
	}
	
}