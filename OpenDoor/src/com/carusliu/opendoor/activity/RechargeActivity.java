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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);

		initView();
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

		title.setText("≥‰÷µ");
		leftText.setText("<∑µªÿ");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		rechargeBtn.setOnClickListener(this);
	}

	public void getOrderInfoReuquest() {

		HashMap<String, String> data = new HashMap<String, String>();
		String userId = SharedPreferencesHelper.getString(
				SharedPreferencesKey.USER_ID, "0");
		// data.put(SysConstants.USER_ID, userId);
		// data.put(SysConstants.AWARD_ID, prize.getId());
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.ORDER_INFO_URL, null,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}

	@Override
	public void parseResponse(NBRequest request) {
		// TODO Auto-generated method stub
		
		if (request.getCode().equals(SysConstants.ZERO)) {
			JSONObject jsonObject = request.getBodyJSONObject();

			String TN = jsonObject.optString("tn");
			UPPayAssistEx.startPayByJAR(this, PayActivity.class, null, null,
					TN, "01");

		} else {
			Toast.makeText(getApplicationContext(), "«Î«Û ß∞‹", Toast.LENGTH_SHORT)
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
			getOrderInfoReuquest();
			break;

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
