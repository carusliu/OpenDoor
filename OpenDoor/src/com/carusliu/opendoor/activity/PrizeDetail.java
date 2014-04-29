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
		
		//����ëǮ����н����¼���
		promote_rate = (Button) findViewById(R.id.btn_promote_rate);
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		
		title.setText("��Ʒ����");
		leftText.setText("<����");
		rightText.setText("����>");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		promote_rate.setOnClickListener(this);
		
		//���ضһ�����
		WebView wv_about = (WebView) findViewById(R.id.wv_exchange_content);
		WebSettings setting = wv_about.getSettings();
		setting = wv_about.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setBuiltInZoomControls(false);//������Ŵ�
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
			//�ж����
			if(buyRateMinimumRequest > balance){
				//��ǰ����֧��
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("����");
				alert.setMessage("�����˻�����Ѳ��㣬�Ƿ�������ֵ��");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "����лл", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//�رնԻ���
						dialog.cancel();
					}
				});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "��", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(PrizeDetail.this, RechargeActivity.class);
						startActivity(intent);
					}
				});
				//��ʾ�Ի���
				alert.show();
			}else{
				//��ʾ��ǰ�����ѽ��
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("֧��ȷ��");
				alert.setMessage("����������п۳� "+buyRateMinimumRequest+" Ԫ���Ƿ������");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "ȡ��", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//�رնԻ���
						dialog.cancel();
					}
				});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "��", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//֧��
						
					}
				});
				//��ʾ�Ի���
				alert.show();
			}
		}else{
			//��ʾ��¼
			Toast.makeText(this, "����δ��¼�����ȵ�¼", Toast.LENGTH_SHORT).show();
		}
    }
}
