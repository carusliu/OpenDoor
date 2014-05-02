package com.carusliu.opendoor.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.MD5Util;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class Login extends HWActivity implements OnClickListener{
	private EditText etName; // 帐号编辑框
	private EditText etPwd; // 密码编辑框
	private TextView leftText, title, rightText;
	private Button loginBtn, registerBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        initView();
    }
    public void initView() {

        leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		etName = (EditText) findViewById(R.id.et_login_name);
		etPwd = (EditText) findViewById(R.id.et_login_pass);
		loginBtn = (Button) findViewById(R.id.btn_login);
		registerBtn = (Button)findViewById(R.id.btn_register);
		
		title.setText("登录");
		leftText.setText("<返回");
		rightText.setText("注册>");

		rightText.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
	}


    public void loginRequest(){
    	String userName  = etName.getText().toString().trim();
    	String userPwd  = etPwd.getText().toString().trim();
    	
    	if(userName.equals("")){
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!userName.toString().matches("^([\\w+\\.]\\w+@[\\w+\\.]+\\w+|1[3568]\\d{9})$")){
			Toast.makeText(this, "用户名必须为邮箱或手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(userPwd.equals("")){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
    	
    	HashMap<String, String> data = new HashMap<String, String>();
		data.put(SysConstants.USER_ID, userName);
		data.put(SysConstants.PASSWORD, MD5Util.md5(userPwd));
		data.put(SysConstants.PARAM_APP_ID, SysConstants.APP_ID);
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.REQUEST_LOGIN, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
    }
    
    @Override
	public void parseResponse(NBRequest request) {
		// TODO Auto-generated method stub
    	System.out.println(request.getCode());
    	if(request.getCode().equals(SysConstants.ZERO)){
	    	JSONObject jsonObject = request.getBodyJSONObject();
	    	System.out.println(jsonObject.toString());
	    	SharedPreferencesHelper.putString(SharedPreferencesKey.IS_LOGIN, "1");
	        /*Intent intent = new Intent();
	        intent.setClass(Login.this,MainActivity.class);
	        startActivity(intent);*/
	    	
	        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
	        finish();
    	}
	}
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_register:
			Intent intent = new Intent();
			intent.setClass(Login.this,RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_login:
			loginRequest();
			break;
		}
	}
}
