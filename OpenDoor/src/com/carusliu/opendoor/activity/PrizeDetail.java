package com.carusliu.opendoor.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class PrizeDetail extends Activity implements OnClickListener {
	
	 private TextView leftText, title, rightText;
	 private ImageView promote_rate, shareBtn;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prize_detail);
        
        initView();
        
    }

    public void initView() {
		
		//����ëǮ����н����¼���
		promote_rate = (ImageView) findViewById(R.id.btn_promote_rate);
		shareBtn = (ImageView) findViewById(R.id.btn_share);
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		
		title.setText("�콱��");
		leftText.setText("<����");
		rightText.setText("ɾ��");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		promote_rate.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		
		
	}
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_share:
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
			//Toast.makeText(this, "����δ��¼�����ȵ�¼", Toast.LENGTH_SHORT).show();
			//�Ƚ���Ʒ��Ϣ������
			Intent intent = new Intent(PrizeDetail.this, Login.class);
			startActivity(intent);
		}
    }
}
