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
import com.carusliu.opendoor.application.AppApplication;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.MD5Util;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class RegisterActivity extends HWActivity implements OnClickListener{
	private TextView leftText, title, rightText;
	private EditText etUserName, etPwd, etConfirmPwd;
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
 		etUserName = (EditText) findViewById(R.id.et_reg_name);
 		etPwd = (EditText) findViewById(R.id.et_reg_pwd);
 		etConfirmPwd = (EditText) findViewById(R.id.et_reg_pwd_confirm);
 		
		title.setText("注册");
		leftText.setText("<返回");
		rightText.setText("注册>");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		
	}
   
    public void registerRequest(){
    	String userName  = etUserName.getText().toString().trim();
    	String userPwd  = etPwd.getText().toString().trim();
    	String confirmPwd  = etPwd.getText().toString().trim();
    	
    	//检查输入参数
    	if("".equals(userName)){
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!userName.matches("^([\\w+\\.]\\w+@[\\w+\\.]+\\w+|1[3568]\\d{9})$")){
			Toast.makeText(this, "用户名必须为邮箱或手机号", Toast.LENGTH_SHORT).show();
			return;
		}
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
		
    	HashMap<String, String> data = new HashMap<String, String>();
		data.put(SysConstants.USER_ID, userName);
		data.put(SysConstants.PASSWORD, MD5Util.md5(userPwd));
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.REGISTER_URL, data,
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
	    	
	    	Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
			startActivity(intent);
			Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
			AppApplication.getInstance().applicationExit();
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
		case R.id.btn_right:
			break;
		case R.id.btn_register:
			registerRequest();
			break;
		}
	}
}
