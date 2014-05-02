package com.carusliu.opendoor.activity;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.activity.ShakeListener.OnShakeListener;


public class ShakeActivity extends Activity{
	
	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	private RelativeLayout mTitle;
	private TextView leftText, title;
	private SlidingDrawer mDrawer;
	private Button mDrawerBtn;
	private AnimationDrawable anim;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.shake_activity);
		//drawerSet ();//设置  drawer监听    切换 按钮的方向
		
		mVibrator = (Vibrator)getApplication().getSystemService(VIBRATOR_SERVICE);
		//ImageView shakeImage = (ImageView)findViewById(R.id.shakeBg);
		anim = (AnimationDrawable)findViewById(R.id.shakeBg).getBackground();
		
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		title.setText("摇一摇");
		leftText.setText("<返回");

		leftText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				//Toast.makeText(getApplicationContext(), "抱歉，暂时没有找到在同一时刻摇一摇的人。\n再试一次吧！", Toast.LENGTH_SHORT).show();
				anim.start();  //开始 摇一摇手掌动画
				mShakeListener.stop();
				startVibrato(); //开始 震动
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						/*Toast.makeText(getApplicationContext(),
							     "未摇中任何奖品", 10).show();*/
						showPrizeDialog();
					    mVibrator.cancel();
					    anim.stop();
						mShakeListener.start();
					}
				}, 2000);
			}
		});
   }
	
	public void startAnim () {   //定义摇一摇动画动画
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);
		
		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);	
	}
	public void startVibrato(){		//定义震动
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); //第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}
	
	public void shake_activity_back(View v) {     //标题栏 返回按钮
      	this.finish();
      }  
	public void linshi(View v) {     //标题栏
		startAnim();
      }  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mShakeListener != null) {
			mShakeListener.start();
		}
	}
	
	private void showPrizeDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("中奖啦")
                .setMessage("恭喜您摇中了一个奖品！")
                .setPositiveButton("查看",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                            	Intent intent = new Intent();
                            	intent.setClass(ShakeActivity.this, PrizeDetail.class);
                            	startActivity(intent);
                            }

                        })
                 .setNegativeButton("取消",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }
	
	public Location getLocation(){
		LocationManager loctionManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
		criteria.setAltitudeRequired(false);//不要求海拔
		criteria.setBearingRequired(false);//不要求方位
		criteria.setCostAllowed(true);//允许有花费
		criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
		//从可用的位置提供器中，匹配以上标准的最佳提供器
		String provider = loctionManager.getBestProvider(criteria, true);
		Location location  = loctionManager.getLastKnownLocation(provider);
		
		return location;
	}
}