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

public class PersonalActivity extends HWActivity implements OnClickListener, OnItemClickListener{
	private TextView leftText, title, rightText;
	private View prizeItem, infoItem, pwdItem, prizeView, infoView, pwdView;
	private List<Prize> prizeList = new ArrayList<Prize>();
	private ListView awardlist;
	private BaseAdapter awardListAdapter;
	private ImageButton userPhoto;
	private TextView tvUserName, tvUserGender, tvUserPhone, tvUserEmail;
	private EditText etUserName, etUserGender, etUserPhone, etUserEmail, etOldPwd,etNewPwd,etConfirmPwd;
	private TextView modifyInfo;
	private ImageView imgPrize,imgInfo,imgPwd;
	private Button modifyInfoBtn, modifyPwdBtn;
	private int prizeFlag = 0;
	private int infoFlag = 0;
	private int pwdFlag = 0;
	private int modifyInfoFlag = 0;
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
		//etUserGender =(EditText)findViewById(R.id.et_user_gender);
		etUserPhone =(EditText)findViewById(R.id.et_user_phone);
		etUserEmail =(EditText)findViewById(R.id.et_user_email);
		
		etOldPwd =(EditText)findViewById(R.id.et_old_pwd);
		etNewPwd =(EditText)findViewById(R.id.et_new_pwd);
		etConfirmPwd =(EditText)findViewById(R.id.et_confirm_pwd);
		
		title.setText("��������");
		leftText.setText("<����");
		rightText.setText("ע��>");
		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		prizeItem.setOnClickListener(this);
		infoItem.setOnClickListener(this);
		pwdItem.setOnClickListener(this);
		modifyInfo.setOnClickListener(this);
	}
    
    public void modifyInfoRequest(){
    	String userName = etUserName.getText().toString().trim();
    	String gender = etUserGender.getText().toString().trim();
    	String userPhone = etUserPhone.getText().toString().trim();
    	String userEmail = etUserEmail.getText().toString().trim();
    	
    	if(userName.equals("")){
			Toast.makeText(this, "��������Ϊ��", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!userPhone.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){
			Toast.makeText(this, "����д��ȷ���ֻ���", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!userPhone.matches("^([a-z0-9A-Z]+[-|//.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?//.)+[a-zA-Z]{2,}$")){
			Toast.makeText(this, "����д��ȷ������", Toast.LENGTH_SHORT).show();
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
		tvUserGender.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_GENDER, ""));
		tvUserPhone.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_PHONE, ""));
		tvUserEmail.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_EMAIL, ""));
		
		etUserName.setVisibility(View.GONE);
		etUserGender.setVisibility(View.GONE);
		etUserPhone.setVisibility(View.GONE);
		etUserEmail.setVisibility(View.GONE);
		
    }
    public void showModifyView(){
    	
		etUserName.setVisibility(View.VISIBLE);
		etUserGender.setVisibility(View.VISIBLE);
		etUserPhone.setVisibility(View.VISIBLE);
		etUserEmail.setVisibility(View.VISIBLE);
		
		etUserName.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_NAME, ""));
		etUserGender.setText(SharedPreferencesHelper.getString(SharedPreferencesKey.USER_GENDER, ""));
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
			Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
			return;
		}
		if(newPwd.length() < 6 || newPwd.length() > 16){
			Toast.makeText(this, "����Ϊ����6-16λ�ַ�", Toast.LENGTH_SHORT).show();
			return;
		}
		if(confirmPwd.equals("")){
			Toast.makeText(this, "��ȷ������", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!confirmPwd.equals(newPwd)){
			Toast.makeText(this, "�������벻һ��", Toast.LENGTH_SHORT).show();
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
	    	
	        Toast.makeText(getApplicationContext(), "��¼�ɹ�", Toast.LENGTH_SHORT).show();
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
			//�����Ի�����ʾ
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
				modifyInfo.setText("���");
				showModifyView();
				modifyInfoFlag = 1;
			}else{
				modifyInfo.setText("�޸�");
				showInfoView();
				modifyInfoFlag = 0;
			}
			break;
		}
	}
    
    @Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		//�򿪶һ�����
		
	}
    
    private void showExitDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("����")
                .setMessage("��ȷ��Ҫ�˳���")
                .setPositiveButton("ȷ��",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                            	SharedPreferencesHelper.putString(SharedPreferencesKey.IS_LOGIN, "0");
                    			SharedPreferencesHelper.putString(SharedPreferencesKey.IS_FIRST_USE, "1");
                    			AppApplication.getInstance().applicationExit();
                    			finish();
                            }

                        })
                 .setNegativeButton("ȡ��",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create(); // �����Ի���
        alertDialog.show(); // ��ʾ�Ի���
    }
}
