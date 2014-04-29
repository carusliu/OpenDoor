package com.carusliu.opendoor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.application.AppApplication;

public class RegisterActivity extends HWActivity implements OnClickListener{
	private TextView leftText, title, rightText;
	private Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
		initView();
    }

    public void initView() {
    	leftText = (TextView) findViewById(R.id.btn_left);
 		title = (TextView) findViewById(R.id.tv_center);
 		rightText = (TextView) findViewById(R.id.btn_right);
 		registerBtn = (Button) findViewById(R.id.btn_register);
		title.setText("×¢²á");
		leftText.setText("<·µ»Ø");
		rightText.setText("×¢²á>");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		
	}
   
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			break;
		case R.id.btn_register:
			Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
			startActivity(intent);
			AppApplication.getInstance().applicationExit();
			finish();
			break;
		}
	}
}
