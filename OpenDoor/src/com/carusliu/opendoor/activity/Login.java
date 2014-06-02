package com.carusliu.opendoor.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
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
	private CheckBox rememberMe;
	private String from;
	private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
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
		rememberMe = (CheckBox)findViewById(R.id.chb_login_remember);
		findViewById(R.id.btn_clear).setOnClickListener(this);
		
		etName.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_ACCOUNT,""));
		etPwd.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_PWD,""));
		
		title.setText("登录");
		leftText.setText("<返回");
		//rightText.setText("注册>");

		leftText.setOnClickListener(this);
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
		data.put(SysConstants.USER_ACCOUNT, userName);
		data.put(SysConstants.USER_PASSWORD, MD5Util.md5(userPwd));
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.LOGIN_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
    }
    
    @Override
	public void parseResponse(NBRequest request) {
		// TODO Auto-generated method stub
    	//System.out.println(request.getCode());
    	progressDialog.cancel();
    	if(request.getCode().equals(SysConstants.ZERO)){
	    	JSONObject jsonObject = request.getBodyJSONObject();
	    	//System.out.println(jsonObject.toString());
	    	JSONObject userInfoObj = jsonObject.optJSONObject("userInfo");
	    	SharedPreferencesHelper.putString(SharedPreferencesKey.USER_ID, userInfoObj.optString("id"));
	    	SharedPreferencesHelper.putString(SharedPreferencesKey.USER_NAME, userInfoObj.optString("userName"));
	    	SharedPreferencesHelper.putString(SharedPreferencesKey.USER_GENDER, userInfoObj.optString("userSax"));
	    	SharedPreferencesHelper.putString(SharedPreferencesKey.USER_PHONE, userInfoObj.optString("userPhone"));
	    	SharedPreferencesHelper.putString(SharedPreferencesKey.USER_EMAIL, userInfoObj.optString("userEmail"));
	    	
	    	if(rememberMe.isChecked()){
	    		SharedPreferencesHelper.putString(SharedPreferencesKey.USER_ACCOUNT, userInfoObj.optString("userAccount"));
	    		SharedPreferencesHelper.putString(SharedPreferencesKey.USER_PWD, etPwd.getText().toString());
	    	}else{
	    		SharedPreferencesHelper.putString(SharedPreferencesKey.USER_ACCOUNT, "");
	    		SharedPreferencesHelper.putString(SharedPreferencesKey.USER_PWD, "");
	    	}
	    	
	    	SharedPreferencesHelper.putString(SharedPreferencesKey.IS_LOGIN, "1");
	    	
	    	if(!"PrizeDetail".equals(from)){
	    		Intent intent = new Intent();
		        intent.setClass(Login.this,PersonalActivity.class);
		        startActivity(intent);
	    	}
	        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
	        finish();
    	}else{
    		Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
    	}
	}
    
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_register:
			
			intent.setClass(Login.this,RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_login:
			progressDialog.setMessage("正在登录");
			progressDialog.show();
			loginRequest();
			break;
		case R.id.btn_clear:
			etName.setText("");
			etPwd.setText("");
			break;
		}
	}
}
