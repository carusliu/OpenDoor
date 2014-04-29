package com.carusliu.opendoor.activity;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.R.layout;
import com.carusliu.opendoor.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RechargeActivity extends Activity implements OnClickListener{
	private TextView leftText, title, rightText;
	private Button rechargeBtn;
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
			
			title.setText("≥‰÷µ");
			leftText.setText("<∑µªÿ");

			rightText.setOnClickListener(this);
			leftText.setOnClickListener(this);
			rechargeBtn.setOnClickListener(this);
		}
	
	 @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_immediately_pay:
				
				break;

			}
		}
}
