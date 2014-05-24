package com.carusliu.opendoor.activity;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.application.AppApplication;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.MD5Util;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class RegisterActivity extends HWActivity implements OnClickListener{
	private TextView leftText, title, rightText;
	private EditText etUserAccount, etPwd, etConfirmPwd, etUserName, etUserPhone, etUserEmail;
	private RadioGroup genderRadio;
	private String gender = "0";
	private Button registerBtn;
	private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
		initView();
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
    }

    public void initView() {
    	leftText = (TextView) findViewById(R.id.btn_left);
 		title = (TextView) findViewById(R.id.tv_center);
 		rightText = (TextView) findViewById(R.id.btn_right);
 		registerBtn = (Button) findViewById(R.id.btn_register);
 		etUserAccount = (EditText) findViewById(R.id.et_reg_account);
 		etPwd = (EditText) findViewById(R.id.et_reg_pwd);
 		etConfirmPwd = (EditText) findViewById(R.id.et_reg_pwd_confirm);
 		etUserName = (EditText) findViewById(R.id.et_reg_name);
 		genderRadio = (RadioGroup) findViewById(R.id.radio_gender);
 		etUserPhone = (EditText) findViewById(R.id.et_reg_phone);
 		etUserEmail = (EditText) findViewById(R.id.et_reg_email);
 		
		title.setText("注册");
		leftText.setText("<返回");
		rightText.setText("注册>");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		
		genderRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch(checkedId){
				case R.id.rb_male:
					gender = "0";
					break;
				case R.id.rb_female:
					gender = "1";
					break;
				}
			}
		});
		
	}
   
    public void registerRequest(){
    	String userAccount  = etUserAccount.getText().toString().trim();
    	String userPwd  = etPwd.getText().toString().trim();
    	String confirmPwd  = etConfirmPwd.getText().toString().trim();
    	String userName  = etUserName.getText().toString().trim();
    	
    	String userPhone  = etUserPhone.getText().toString().trim();
    	String userEmail  = etUserEmail.getText().toString().trim();

    	if(!userAccount.matches("^([\\w+\\.]\\w+@[\\w+\\.]+\\w+|1[35678]\\d{9})$")){
			Toast.makeText(this, "账号必须为正确的手机号和邮箱", Toast.LENGTH_SHORT).show();
			return;
		}
    	//检查输入参数
		if(userPwd.equals("")){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(userPwd.length() < 6 || userPwd.length() > 16){
			Toast.makeText(this, "密码为长度6-16位字符", Toast.LENGTH_SHORT).show();
			return;
		}
		if(confirmPwd.equals("")){
			Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!confirmPwd.equals(userPwd)){
			Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}
		if(userName.equals("")){
			Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!userPhone.matches("^(13|15|17|18)\\d{9}$")){
			Toast.makeText(this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!userEmail.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")){
			Toast.makeText(this, "请填写正确的邮箱", Toast.LENGTH_SHORT).show();
			return;
		}
    	HashMap<String, String> data = new HashMap<String, String>();
		data.put(SysConstants.USER_ACCOUNT, userAccount);
		data.put(SysConstants.USER_PASSWORD, MD5Util.md5(userPwd));
		data.put(SysConstants.USER_NAME, userName);
		data.put(SysConstants.USER_GENDER, gender);
		data.put(SysConstants.USER_PHONE, userPhone);
		data.put(SysConstants.USER_EMAIL, userEmail);
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.REGISTER_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
    }
    
    @Override
	public void parseResponse(NBRequest request) {
		// TODO Auto-generated method stub
    	System.out.println(request.getCode());
    	progressDialog.cancel();
    	if(request.getCode().equals(SysConstants.ZERO)){
	    	//JSONObject jsonObject = request.getBodyJSONObject();
	    	//System.out.println(jsonObject.toString());
	    	//SharedPreferencesHelper.putString(SharedPreferencesKey.IS_LOGIN, "1");
	    	
	    	//Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
			//startActivity(intent);
			Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
			//AppApplication.getInstance().applicationExit();
			finish();
    	}else{
    		Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
    	}
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
			progressDialog.setMessage("正在注册");
			progressDialog.show();
			registerRequest();
			break;
		}
	}
}
