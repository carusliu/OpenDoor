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
		//drawerSet ();//����  drawer����    �л� ��ť�ķ���
		
		mVibrator = (Vibrator)getApplication().getSystemService(VIBRATOR_SERVICE);
		//ImageView shakeImage = (ImageView)findViewById(R.id.shakeBg);
		anim = (AnimationDrawable)findViewById(R.id.shakeBg).getBackground();
		
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		title.setText("ҡһҡ");
		leftText.setText("<����");

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
				//Toast.makeText(getApplicationContext(), "��Ǹ����ʱû���ҵ���ͬһʱ��ҡһҡ���ˡ�\n����һ�ΰɣ�", Toast.LENGTH_SHORT).show();
				anim.start();  //��ʼ ҡһҡ���ƶ���
				mShakeListener.stop();
				startVibrato(); //��ʼ ��
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
						/*Toast.makeText(getApplicationContext(),
							     "δҡ���κν�Ʒ", 10).show();*/
						showPrizeDialog();
					    mVibrator.cancel();
					    anim.stop();
						mShakeListener.start();
					}
				}, 2000);
			}
		});
   }
	
	public void startAnim () {   //����ҡһҡ��������
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
	public void startVibrato(){		//������
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); //��һ�����������ǽ������飬 �ڶ����������ظ�������-1Ϊ���ظ�����-1���մ�pattern��ָ���±꿪ʼ�ظ�
	}
	
	public void shake_activity_back(View v) {     //������ ���ذ�ť
      	this.finish();
      }  
	public void linshi(View v) {     //������
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
                .setTitle("�н���")
                .setMessage("��ϲ��ҡ����һ����Ʒ��")
                .setPositiveButton("�鿴",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                            	Intent intent = new Intent();
                            	intent.setClass(ShakeActivity.this, PrizeDetail.class);
                            	startActivity(intent);
                            }

                        })
                 .setNegativeButton("ȡ��",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create(); // �����Ի���
        alertDialog.show(); // ��ʾ�Ի���
    }
	
	public Location getLocation(){
		LocationManager loctionManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//�߾���
		criteria.setAltitudeRequired(false);//��Ҫ�󺣰�
		criteria.setBearingRequired(false);//��Ҫ��λ
		criteria.setCostAllowed(true);//�����л���
		criteria.setPowerRequirement(Criteria.POWER_LOW);//�͹���
		//�ӿ��õ�λ���ṩ���У�ƥ�����ϱ�׼������ṩ��
		String provider = loctionManager.getBestProvider(criteria, true);
		Location location  = loctionManager.getLastKnownLocation(provider);
		
		return location;
	}
}