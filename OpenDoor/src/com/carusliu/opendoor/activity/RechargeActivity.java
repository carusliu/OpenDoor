package com.carusliu.opendoor.activity;

import java.util.HashMap;

import org.json.JSONObject;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.R.layout;
import com.carusliu.opendoor.R.menu;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RechargeActivity extends HWActivity implements OnClickListener {
	private TextView leftText, title, rightText;
	private Button rechargeBtn;
	private int money = 1;
	private View preView ;
	String TN;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);

		initView();
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
	}

	public void initView() {

		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		rechargeBtn = (Button) findViewById(R.id.btn_immediately_pay);
		
		MyOnClickListener listener = new MyOnClickListener();
		preView = findViewById(R.id.tx_money_1);
		preView.setOnClickListener(listener);
		findViewById(R.id.tx_money_2).setOnClickListener(listener);
		findViewById(R.id.tx_money_5).setOnClickListener(listener);
		findViewById(R.id.tx_money_8).setOnClickListener(listener);
		findViewById(R.id.tx_money_10).setOnClickListener(listener);
		findViewById(R.id.tx_money_20).setOnClickListener(listener);

		title.setText("充值");
		leftText.setText("<返回");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		rechargeBtn.setOnClickListener(this);
	}

	public void getOrderInfoReuquest() {

		HashMap<String, String> data = new HashMap<String, String>();
		String userId = SharedPreferencesHelper.getString(
				SharedPreferencesKey.USER_ID, "0");
	    data.put(SysConstants.USER_ID, userId);
		data.put(SysConstants.ORDER_AMOUNT, money+"");
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.ORDER_INFO_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}
	public void updateOrderReuquest() {
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put(SysConstants.TN, TN);
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.ORDER_INFO_URL, null,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}

	@Override
	public void parseResponse(NBRequest request) {
		// TODO Auto-generated method stub
		progressDialog.cancel();
		if (request.getCode().equals(SysConstants.ZERO)) {
			JSONObject jsonObject = request.getBodyJSONObject();

			TN = jsonObject.optString("tn");
			UPPayAssistEx.startPayByJAR(this, PayActivity.class, null, null,
					TN, "01");

		} else {
			Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_immediately_pay:
			Log.i("OpenDoor", ""+money);
			progressDialog.setMessage("正在生成订单");
			progressDialog.show();
			getOrderInfoReuquest();
			break;

		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		String str = data.getExtras().getString("pay_result");
		System.out.println(str);
		if (str.equals("success")) {
			updateOrderReuquest();
			Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT)
			.show();
		} else if (str.equalsIgnoreCase("fail")) {
			Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT)
			.show();
		} else if (str.equalsIgnoreCase("cancel")) {
			Toast.makeText(getApplicationContext(), "您已取消支付", Toast.LENGTH_SHORT)
			.show();
		}
	}
	
	public class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			preView.setBackgroundResource(R.drawable.charge_uncheck);
			v.setBackgroundResource(R.drawable.charge_check);
			preView = v;
			
			switch (v.getId()) {
			case R.id.tx_money_1:
				money = 1;
				break;
			case R.id.tx_money_2:
				money = 2;
				break;
			case R.id.tx_money_5:
				money = 5;
				break;
			case R.id.tx_money_8:
				money = 8;
				break;
			case R.id.tx_money_10:
				money = 10;
				break;
			case R.id.tx_money_20:
				money = 20;
				break;

			}
		}
		
	}
}
