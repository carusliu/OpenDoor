package com.carusliu.opendoor.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class PrizeDetail extends Activity implements OnClickListener {
	
	 private TextView leftText, title, rightText;
	 private Button promote_rate;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prize_detail);
        
        initView();
        
    }

    public void initView() {
		
		//花几毛钱提高中奖率事件绑定
		promote_rate = (Button) findViewById(R.id.btn_promote_rate);
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		
		title.setText("奖品详情");
		leftText.setText("<返回");
		rightText.setText("分享>");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		promote_rate.setOnClickListener(this);
		
		//加载兑换内容
		WebView wv_about = (WebView) findViewById(R.id.wv_exchange_content);
		WebSettings setting = wv_about.getSettings();
		setting = wv_about.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setBuiltInZoomControls(false);//不允许放大
		//setting.setLightTouchEnabled(true);
		setting.setSupportZoom(false);
		setting.setAllowFileAccess(true);
		//setting.setBlockNetworkImage(true);
		//wv_about.loadUrl(DictEnum.exchange_url+"?uid="+uid+"&latitude="+session.getLatitude()+"&longitude="+session.getLongitude());
	}
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			Intent intent = new Intent(PrizeDetail.this, ShareActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_promote_rate:
			promoteRate();
			break;
		}
		
	}
    
    public void promoteRate(){
    	
    	String isLogin = SharedPreferencesHelper.getString(SharedPreferencesKey.IS_LOGIN,"0");
    	Float balance = SharedPreferencesHelper.getFloat(SharedPreferencesKey.BALANCE,100);
    	Float buyRateMinimumRequest = SharedPreferencesHelper.getFloat(SharedPreferencesKey.MINIMUM_REQUEST,120);
    	
		if(isLogin.equals("1")){
			//判断余额
			if(buyRateMinimumRequest > balance){
				//当前余额不够支付
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("余额不足");
				alert.setMessage("您的账户余额已不足，是否立即充值？");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "不，谢谢", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//关闭对话框
						dialog.cancel();
					}
				});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "是", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(PrizeDetail.this, RechargeActivity.class);
						startActivity(intent);
					}
				});
				//显示对话框
				alert.show();
			}else{
				//提示当前将消费金额
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("支付确认");
				alert.setMessage("将从您余额中扣除 "+buyRateMinimumRequest+" 元，是否继续？");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//关闭对话框
						dialog.cancel();
					}
				});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "好", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//支付
						
					}
				});
				//显示对话框
				alert.show();
			}
		}else{
			//提示登录
			Toast.makeText(this, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
		}
    }
}
