package com.carusliu.opendoor.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.carusliu.opendoor.R;
import com.carusliu.opendoor.application.AppApplication;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class PersonalActivity extends HWActivity implements OnClickListener{
	private TextView leftText, title, rightText;
	private View prizeItem, infoItem, pwdItem, prizeView, infoView, pwdView;
	private List<Prize> prizeList = new ArrayList<Prize>();
	private ListView awardlist;
	private BaseAdapter awardListAdapter;
	private ImageButton userPhoto;
	private TextView tvUserName, tvUserGender, tvUserPhone, tvUserEmail;
	private EditText etUserName,  etUserPhone, etUserEmail, etOldPwd,etNewPwd,etConfirmPwd;
	private TextView etUserGender,modifyInfo;
	private ImageView imgPrize,imgInfo,imgPwd;
	private Button modifyInfoBtn, modifyPwdBtn;
	private int prizeFlag = 0;
	private int infoFlag = 0;
	private int pwdFlag = 0;
	private int modifyInfoFlag = 0;
	private String gender = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        
        initView();
    }

    public void initView() {
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		prizeItem = (View) findViewById(R.id.rv_person_prize_item);
		infoItem = (View) findViewById(R.id.rv_person_info_item);
		pwdItem = (View) findViewById(R.id.rv_person_pwd_item);
		prizeView = (View) findViewById(R.id.rv_person_prize);
		infoView = (View) findViewById(R.id.rv_person_info);
		pwdView = (View) findViewById(R.id.rv_person_pwd);
		imgPrize = (ImageView) findViewById(R.id.prize_open);
		imgInfo = (ImageView) findViewById(R.id.info_open);
		imgPwd = (ImageView) findViewById(R.id.pwd_open);
		modifyInfo = (TextView)findViewById(R.id.tv_modify_info);
		
		tvUserName = (TextView)findViewById(R.id.user_name);
		tvUserGender = (TextView)findViewById(R.id.user_gender);
		tvUserPhone = (TextView)findViewById(R.id.user_phone);
		tvUserEmail = (TextView)findViewById(R.id.user_email);
		etUserName =(EditText)findViewById(R.id.et_user_name);
		etUserGender =(TextView)findViewById(R.id.et_user_gender);
		etUserPhone =(EditText)findViewById(R.id.et_user_phone);
		etUserEmail =(EditText)findViewById(R.id.et_user_email);
		modifyInfoBtn = (Button)findViewById(R.id.btn_modify_info);
		
		etOldPwd =(EditText)findViewById(R.id.et_old_pwd);
		etNewPwd =(EditText)findViewById(R.id.et_new_pwd);
		etConfirmPwd =(EditText)findViewById(R.id.et_confirm_pwd);
		modifyPwdBtn = (Button)findViewById(R.id.btn_modify_pwd);
		
		//1.初始化个人信息
		tvUserName.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_NAME, ""));
		setGender();
		tvUserPhone.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_PHONE, ""));
		tvUserEmail.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_EMAIL, ""));
		title.setText("个人中心");
		leftText.setText("<返回");
		rightText.setText("注销>");
		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		prizeItem.setOnClickListener(this);
		infoItem.setOnClickListener(this);
		pwdItem.setOnClickListener(this);
		modifyInfo.setOnClickListener(this);
		modifyInfoBtn.setOnClickListener(this);
		modifyPwdBtn.setOnClickListener(this);
		etUserGender.setOnClickListener(this);
	}
    
    public void modifyInfoRequest(){
    	String userName = etUserName.getText().toString().trim();
    	String userPhone = etUserPhone.getText().toString().trim();
    	String userEmail = etUserEmail.getText().toString().trim();
    	
    	if(userName.equals("")){
			Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!userPhone.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){
			Toast.makeText(this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!userPhone.matches("^([a-z0-9A-Z]+[-|//.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?//.)+[a-zA-Z]{2,}$")){
			Toast.makeText(this, "请填写正确的邮箱", Toast.LENGTH_SHORT).show();
			return;
		}
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	String userId = SharedPreferencesHelper.getString(SharedPreferencesKey.USER_ID,
				"0");
    	data.put(SysConstants.USER_ID, userId);
    	data.put(SysConstants.USER_NAME, userName);
    	data.put(SysConstants.USER_GENDER, gender);
    	data.put(SysConstants.USER_PHONE, userPhone);
    	data.put(SysConstants.USER_EMAIL, userEmail);
    	
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.MODIFY_PWD_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
    }
    
    public void showInfoView(){
    	tvUserName.setVisibility(View.VISIBLE);
		tvUserGender.setVisibility(View.VISIBLE);
		tvUserPhone.setVisibility(View.VISIBLE);
		tvUserEmail.setVisibility(View.VISIBLE);
		
		tvUserName.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_NAME, ""));
		setGender();
		tvUserPhone.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_PHONE, ""));
		tvUserEmail.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_EMAIL, ""));
		
		etUserName.setVisibility(View.GONE);
		etUserGender.setVisibility(View.GONE);
		etUserPhone.setVisibility(View.GONE);
		etUserEmail.setVisibility(View.GONE);
		modifyInfoBtn.setVisibility(View.GONE);
    }
    public void showModifyView(){
    	
		etUserName.setVisibility(View.VISIBLE);
		etUserGender.setVisibility(View.VISIBLE);
		etUserPhone.setVisibility(View.VISIBLE);
		etUserEmail.setVisibility(View.VISIBLE);
		modifyInfoBtn.setVisibility(View.VISIBLE);
		
		etUserName.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_NAME, ""));
		setGender();
		etUserPhone.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_PHONE, ""));
		etUserEmail.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_EMAIL, ""));
		
		tvUserName.setVisibility(View.GONE);
		tvUserGender.setVisibility(View.GONE);
		tvUserPhone.setVisibility(View.GONE);
		tvUserEmail.setVisibility(View.GONE);
    }
    
    
    public void modifyPwdRequest(){
    	String oldPwd = etOldPwd.getText().toString().trim();
    	String newPwd = etNewPwd.getText().toString().trim();
    	String confirmPwd = etConfirmPwd.getText().toString().trim();
    	
    	if(oldPwd.equals("")){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(newPwd.length() < 6 || newPwd.length() > 16){
			Toast.makeText(this, "密码为长度6-16位字符", Toast.LENGTH_SHORT).show();
			return;
		}
		if(confirmPwd.equals("")){
			Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!confirmPwd.equals(newPwd)){
			Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	String userId = SharedPreferencesHelper.getString(SharedPreferencesKey.USER_ID,
				"0");
    	data.put(SysConstants.USER_ID, userId);
    	data.put(SysConstants.OLD_PASSWORD, oldPwd);
    	data.put(SysConstants.USER_PASSWORD, newPwd);
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.MODIFY_PWD_URL, data,
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
	    	//1.更改后的信息存起来
	    	//2.更新信息界面
	        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
	        finish();
    	}
	}
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			//弹出对话框提示
			showExitDialog();
			break;
		case R.id.rv_person_prize_item:
			if(prizeFlag==0){
				prizeView.setVisibility(View.VISIBLE);
				imgPrize.setBackgroundDrawable(getResources().getDrawable(R.drawable.close));
				prizeFlag = 1;
			}else{
				prizeView.setVisibility(View.GONE);
				imgPrize.setBackgroundDrawable(getResources().getDrawable(R.drawable.open));
				prizeFlag = 0;
			}
			break;
		case R.id.rv_person_info_item:
			
			if(infoFlag==0){
				infoView.setVisibility(View.VISIBLE);
				modifyInfo.setVisibility(View.VISIBLE);
				imgInfo.setBackgroundDrawable(getResources().getDrawable(R.drawable.close));
				infoFlag = 1;
			}else{
				infoView.setVisibility(View.GONE);
				modifyInfo.setVisibility(View.GONE);
				imgInfo.setBackgroundDrawable(getResources().getDrawable(R.drawable.open));
				infoFlag = 0;
			}
			break;
		case R.id.rv_person_pwd_item:
			if(pwdFlag==0){
				pwdView.setVisibility(View.VISIBLE);
				imgPwd.setBackgroundDrawable(getResources().getDrawable(R.drawable.close));
				pwdFlag = 1;
			}else{
				pwdView.setVisibility(View.GONE);
				imgPwd.setBackgroundDrawable(getResources().getDrawable(R.drawable.open));
				pwdFlag = 0;
			}
			break;
		case R.id.tv_modify_info:
			if(modifyInfoFlag==0){
				modifyInfo.setText("完成");
				showModifyView();
				modifyInfoFlag = 1;
			}else{
				modifyInfo.setText("修改");
				showInfoView();
				modifyInfoFlag = 0;
			}
			break;
		case R.id.btn_modify_info:
			modifyInfoRequest();
			
			break;
		case R.id.btn_modify_pwd:
			modifyPwdRequest();
			
			break;
		case R.id.et_user_gender:
			showGenderDialog();
			
			break;
		}
	}
    
    private void showExitDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("您确定要退出吗？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                            	SharedPreferencesHelper.putString(SharedPreferencesKey.IS_LOGIN, "0");
                    			SharedPreferencesHelper.putString(SharedPreferencesKey.IS_FIRST_USE, "1");
                    			AppApplication.getInstance().applicationExit();
                    			finish();
                            }

                        })
                 .setNegativeButton("取消",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }
    
    private void showGenderDialog(){
    	int sex_item = 0;
    	if("1".equals(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_GENDER, "0")));{
    		sex_item = 1;
    	}
	    
		final String[] mList = { "男", "女" };
		
		AlertDialog.Builder sinChosDia = new AlertDialog.Builder(
				PersonalActivity.this);
		sinChosDia.setTitle("选择性别");
		sinChosDia.setSingleChoiceItems(mList, sex_item,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							gender = "0";
							etUserGender.setText("男");
						}else{
							etUserGender.setText("女");
							gender = "1";
						}
					}
				});
		sinChosDia.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
					}
				});
		sinChosDia.create().show();
    }
    private void setGender(){
    String genderStr = SharedPreferencesHelper.getString(SharedPreferencesKey.USER_GENDER, "0");
	if("0".equals(genderStr)){
		tvUserGender.setText("男");
		etUserGender.setText("男");
	}else{
		tvUserGender.setText("女");
		etUserGender.setText("女");
	}
    }
}
