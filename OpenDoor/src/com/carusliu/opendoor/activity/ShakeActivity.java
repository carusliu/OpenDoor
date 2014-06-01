package com.carusliu.opendoor.activity;



import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.activity.ShakeListener.OnShakeListener;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.AsyncImageLoader.ImageCallback;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;


public class ShakeActivity extends HWActivity{
	
	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	private RelativeLayout mTitle;
	private TextView leftText, title;
	private SlidingDrawer mDrawer;
	private Button mDrawerBtn;
	private AnimationDrawable anim;
	private ProgressDialog progressDialog;
	private static final int CODE_BALANCE = 3;
	private static final int CODE_GET_AWARD = 4;
	private static final int CODE_GET_FREE_AWARD = 5;
	private int free = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.shake_activity);
		//drawerSet ();//����  drawer����    �л� ��ť�ķ���
		progressDialog = new ProgressDialog(ShakeActivity.this);
		progressDialog.setCanceledOnTouchOutside(false);
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
							if(isOnline()){
								
								progressDialog.setMessage("���ڻ�ȡ��Ʒ��Ϣ�����Ժ�...");
								progressDialog.show();
								if (SharedPreferencesHelper.getString(SharedPreferencesKey.IS_LOGIN,
										"0").equals("0")) {
									sendNormalPrizeRequest();
								} else {
									if(free==1){
										//�������ҡһҡ�ӿ�
										sendFreePrizeRequest();
									}else{
										getUserBalanceReuquest();
									}
								}
							}else{
								Toast.makeText(ShakeActivity.this, "���粻����", Toast.LENGTH_SHORT).show();
							}
						
					    mVibrator.cancel();
					    anim.stop();
						mShakeListener.start();
					}
				}, 2000);
			}
		});
   }
	public void sendPrizeRequest(){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put(SysConstants.USER_ID, SharedPreferencesHelper.getString(SharedPreferencesKey.USER_ID, ""));
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_GET_AWARD);
		nbRequest.sendRequest(m_handler, SysConstants.SHAKE_AWARD_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}
	
	public void sendFreePrizeRequest(){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put(SysConstants.USER_ID, SharedPreferencesHelper.getString(SharedPreferencesKey.USER_ID, ""));
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_GET_FREE_AWARD);
		nbRequest.sendRequest(m_handler, SysConstants.SHAKE_FREE_AWARD_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}
	
	public void sendNormalPrizeRequest(){
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_GET_AWARD);
		nbRequest.sendRequest(m_handler, SysConstants.SHAKE_NOMAL_AWARD_URL, null,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}
	
	public void getUserBalanceReuquest() {
		
		HashMap<String, String> data = new HashMap<String, String>();
		String userId = SharedPreferencesHelper.getString(
				SharedPreferencesKey.USER_ID, "0");
		data.put(SysConstants.USER_ID, userId);
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_BALANCE);
		nbRequest.sendRequest(m_handler, SysConstants.GET_USER_AMOUNT_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}

	@Override
	public void parseResponse(NBRequest request) {
			if (SysConstants.ZERO.equals(request.getCode())) {
				//�������ݸ��½���
				JSONObject jsonObject = request.getBodyJSONObject();
				System.out.println(jsonObject.toString());
				switch (request.getRequestTag()) {
				case CODE_BALANCE:
					double balance = jsonObject.optDouble("resultAmount");
					promoteRate(balance);
					break;

				case CODE_GET_AWARD:
					JSONObject prizeObj = jsonObject.optJSONArray("awardList").optJSONObject(0);
					Prize prize = new Prize();
					prize.setId(prizeObj.optString("id"));
					prize.setNumber(prizeObj.optString("awardNumber"));
					prize.setName(prizeObj.optString(""));
					prize.setInfo(prizeObj.optString("awardInfo"));
					prize.setAddress(prizeObj.optString("awardAddress"));
					prize.setCipher(prizeObj.optString("awardSecret"));
					prize.setProvider(prizeObj.optString("awardProvide"));
					prize.setStartDate(prizeObj.optString("awardStart")+"��"+prizeObj.optString("awardEnd"));
					prize.setSmallPic(SysConstants.SERVER+prizeObj.optString("awardImage"));
					prize.setPhone(prizeObj.optString("awardPhone"));
					
					progressDialog.dismiss();
					Intent intent = new Intent();
					intent.putExtra("prize", prize);
	            	intent.setClass(ShakeActivity.this, PrizeDetail.class);
	            	startActivityForResult(intent, 0);
					break;
				case CODE_GET_FREE_AWARD:
					String haveFree = jsonObject.optString("isFree");
					if(haveFree.equals("1")){
						progressDialog.setMessage("���ڻ�ȡ��Ʒ��Ϣ�����Ժ�...");
						progressDialog.show();
						sendPrizeRequest();
					}else{
						progressDialog.dismiss();
						AlertDialog alert = new AlertDialog.Builder(this).create();
						alert.setTitle("��ܰ��ʾ");
						alert.setMessage("����������ҡ�������Ѿ����꣬���������۳�0.2Ԫ�������Ƿ������");
						alert.setButton(AlertDialog.BUTTON_NEGATIVE, "ȡ��",
								new AlertDialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// �رնԻ���
										dialog.cancel();
									}
								});
						alert.setButton(AlertDialog.BUTTON_POSITIVE, "����",
								new AlertDialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										getUserBalanceReuquest();
									}
								});
						// ��ʾ�Ի���
						alert.show();
					}
					break;
				}
				
				
			}
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
	public boolean isOnline() {
  	    ConnectivityManager connMgr = (ConnectivityManager) 
  	            getSystemService(Context.CONNECTIVITY_SERVICE);
  	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
  	    return (networkInfo != null && networkInfo.isConnected());
  	} 
	
	public void promoteRate(double balance) {

			// �ж����
			if (0.2 > balance) {
				// ��ǰ����֧��
				progressDialog.cancel();
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("����");
				alert.setMessage("�����˻�����Ѳ��㣬�Ƿ�������ֵ��");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "����лл",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// �رնԻ���
								dialog.cancel();
							}
						});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "��",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(ShakeActivity.this,
										RechargeActivity.class);
								startActivity(intent);
							}
						});
				// ��ʾ�Ի���
				alert.show();
			} else {
				sendPrizeRequest();
			}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode){
		case 0:
			free = 1;
			break;
		//���¡�����ëǮ��������������
		case 1:
			free = 0;
			break;
		}
	}
}