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
		//drawerSet ();//设置  drawer监听    切换 按钮的方向
		progressDialog = new ProgressDialog(ShakeActivity.this);
		progressDialog.setCanceledOnTouchOutside(false);
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
							if(isOnline()){
								
								progressDialog.setMessage("正在获取奖品信息，请稍后...");
								progressDialog.show();
								if (SharedPreferencesHelper.getString(SharedPreferencesKey.IS_LOGIN,
										"0").equals("0")) {
									sendNormalPrizeRequest();
								} else {
									if(free==1){
										//调用免费摇一摇接口
										sendFreePrizeRequest();
									}else{
										getUserBalanceReuquest();
									}
								}
							}else{
								Toast.makeText(ShakeActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
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
				//解析数据更新界面
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
					prize.setStartDate(prizeObj.optString("awardStart")+"至"+prizeObj.optString("awardEnd"));
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
						progressDialog.setMessage("正在获取奖品信息，请稍后...");
						progressDialog.show();
						sendPrizeRequest();
					}else{
						progressDialog.dismiss();
						AlertDialog alert = new AlertDialog.Builder(this).create();
						alert.setTitle("温馨提示");
						alert.setMessage("您今天的免费摇奖机会已经用完，点击继续会扣除0.2元，请问是否继续？");
						alert.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
								new AlertDialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 关闭对话框
										dialog.cancel();
									}
								});
						alert.setButton(AlertDialog.BUTTON_POSITIVE, "继续",
								new AlertDialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										getUserBalanceReuquest();
									}
								});
						// 显示对话框
						alert.show();
					}
					break;
				}
				
				
			}
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
	public boolean isOnline() {
  	    ConnectivityManager connMgr = (ConnectivityManager) 
  	            getSystemService(Context.CONNECTIVITY_SERVICE);
  	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
  	    return (networkInfo != null && networkInfo.isConnected());
  	} 
	
	public void promoteRate(double balance) {

			// 判断余额
			if (0.2 > balance) {
				// 当前余额不够支付
				progressDialog.cancel();
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("余额不足");
				alert.setMessage("您的账户余额已不足，是否立即充值？");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "不，谢谢",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 关闭对话框
								dialog.cancel();
							}
						});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "是",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(ShakeActivity.this,
										RechargeActivity.class);
								startActivity(intent);
							}
						});
				// 显示对话框
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
		//按下“花两毛钱换换手气”返回
		case 1:
			free = 0;
			break;
		}
	}
}