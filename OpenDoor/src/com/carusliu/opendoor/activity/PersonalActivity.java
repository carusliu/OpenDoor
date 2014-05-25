package com.carusliu.opendoor.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.carusliu.opendoor.R;
import com.carusliu.opendoor.adapter.GridAdapter;
import com.carusliu.opendoor.application.AppApplication;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.AsyncImageLoader;
import com.carusliu.opendoor.tool.MD5Util;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;
import com.carusliu.opendoor.tool.AsyncImageLoader.ImageCallback;

public class PersonalActivity extends HWActivity implements OnClickListener{
	private TextView leftText, title, rightText;
	private View prizeItem, infoItem, pwdItem, prizeView, infoView, pwdView;
	private GridView prizeGridView;
	private ArrayList<Prize> prizeList;//数据
	private GridAdapter prizeListAdapter;
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
	private static final int CODE_MODIFY_INFO = 0;
	private static final int CODE_MODIFY_PWD = 1;
	private static final int CODE_GET_AWARD = 2;
	private static final int CODE_DELETE = 3;
	private ProgressDialog progressDialog;
	private View dialogView;
	private ImageView dialogImg;
	AsyncImageLoader asyncImageLoader ;
	private int prizePosition;
	AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        
        initView();
        asyncImageLoader = new AsyncImageLoader(this);
        progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		getUserAwardRequest();
		progressDialog.setMessage("正在获取奖品信息");
		progressDialog.show();
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
		
		dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_prize_detail, null);
		dialogImg = (ImageView)dialogView.findViewById(R.id.dialog_prize_pic);
		prizeGridView = (GridView) findViewById(R.id.grid_user_prize);
		prizeList = new ArrayList<Prize>();
		
		alert = new AlertDialog.Builder(PersonalActivity.this).create();
		alert.setTitle("奖品详情");
		alert.setView(dialogView);
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// 关闭对话框
						dialog.cancel();
						
					}
				});
		alert.setButton(AlertDialog.BUTTON_POSITIVE, "删除",
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						deleteAwardReuquest();
					}
				});
		prizeGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				prizePosition = position;
				asyncImageLoader.loadBitmap(prizeList.get(position).getSmallPic(), new ImageCallback() {  
					public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
						dialogImg.setImageBitmap(imageDrawable);
					}  
				});
				// 显示对话框
				alert.show();
			}
		});
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
    
    public void deleteAwardReuquest() {

		HashMap<String, String> data = new HashMap<String, String>();
		String userId = SharedPreferencesHelper.getString(
				SharedPreferencesKey.USER_ID, "0");
		data.put(SysConstants.USER_ID, userId);
		data.put(SysConstants.AWARD_ID, prizeList.get(prizePosition).getId());
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_DELETE);
		nbRequest.sendRequest(m_handler, SysConstants.DELETE_AWARD_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}
    
    public void modifyInfoRequest(){
    	String userName = etUserName.getText().toString().trim();
    	String userPhone = etUserPhone.getText().toString().trim();
    	String userEmail = etUserEmail.getText().toString().trim();
    	
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
    	String userId = SharedPreferencesHelper.getString(SharedPreferencesKey.USER_ID,
				"0");
    	data.put(SysConstants.USER_ID, userId);
    	data.put(SysConstants.USER_NAME, userName);
    	data.put(SysConstants.USER_GENDER, gender);
    	data.put(SysConstants.USER_PHONE, userPhone);
    	data.put(SysConstants.USER_EMAIL, userEmail);
    	
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_MODIFY_INFO);
		nbRequest.sendRequest(m_handler, SysConstants.MODIFY_INFO_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
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
    	String userAccount = SharedPreferencesHelper.getString(SharedPreferencesKey.USER_ACCOUNT,
				"0");
    	data.put(SysConstants.USER_ACCOUNT, userAccount);
    	data.put(SysConstants.USER_PASSWORD, MD5Util.md5(oldPwd));
    	data.put(SysConstants.NEW_PASSWORD, MD5Util.md5(newPwd));
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_MODIFY_PWD);
		nbRequest.sendRequest(m_handler, SysConstants.MODIFY_PWD_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
    }
    
    public void getUserAwardRequest() {
		
		HashMap<String, String> data = new HashMap<String, String>();
		String userId = SharedPreferencesHelper.getString(
				SharedPreferencesKey.USER_ID, "0");
		data.put(SysConstants.USER_ID, userId);
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_GET_AWARD);
		nbRequest.sendRequest(m_handler, SysConstants.GET_USER_AWARD_URL, data,
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
    
    

    
    @Override
	public void parseResponse(NBRequest request) {
		// TODO Auto-generated method stub
    	progressDialog.cancel();
    	if(request.getCode().equals(SysConstants.ZERO)){
	    	/*JSONObject jsonObject = request.getBodyJSONObject();
	    	System.out.println(jsonObject.toString());*/
	    	//SharedPreferencesHelper.putString(SharedPreferencesKey.IS_LOGIN, "1");
	        /*Intent intent = new Intent();
	        intent.setClass(Login.this,MainActivity.class);
	        startActivity(intent);*/
	    	//1.更改后的信息存起来
	    	//2.更新信息界面
    		switch(request.getRequestTag()){
    		case CODE_MODIFY_INFO:
    			SharedPreferencesHelper.putString(SharedPreferencesKey.USER_NAME, etUserName.getText().toString());
    	    	SharedPreferencesHelper.putString(SharedPreferencesKey.USER_GENDER, gender);
    	    	SharedPreferencesHelper.putString(SharedPreferencesKey.USER_PHONE, etUserPhone.getText().toString());
    	    	SharedPreferencesHelper.putString(SharedPreferencesKey.USER_EMAIL, etUserEmail.getText().toString());
    			Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
    			break;
    			
    		case CODE_MODIFY_PWD:
    			SharedPreferencesHelper.putString(SharedPreferencesKey.USER_PWD, "");
    			Toast.makeText(getApplicationContext(), "密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();
    			Intent intent = new Intent();
    	        intent.setClass(PersonalActivity.this,Login.class);
    	        startActivity(intent);
    	        finish();
    			break;
    			
    		case CODE_GET_AWARD:
    			JSONObject jsonObject = request.getBodyJSONObject();
    			JSONArray prizeArray = jsonObject.optJSONArray("userAward");
				for(int i=0;i<prizeArray.length();i++){
					JSONObject prizeObj = prizeArray.optJSONObject(i);
					Prize prize = new Prize();
					prize.setId(prizeObj.optString("id"));
					prize.setNumber(prizeObj.optString("awardNumber"));
					prize.setName(prizeObj.optString(""));
					prize.setInfo(prizeObj.optString("awardInfo"));
					prize.setAddress(prizeObj.optString("awardAddress"));
					prize.setCipher(prizeObj.optString("awardSecret"));
					prize.setProvider(prizeObj.optString("awardProvide"));
					prize.setStartDate(prizeObj.optString("awardStart")+"至"+prizeObj.optString("awardEnd"));
					prize.setSmallPic(SysConstants.SERVER+prizeObj.optString("awardImage"));
					prize.setPhone(prizeObj.optString("awardPhone"));
					prizeList.add(prize);
				}
				prizeListAdapter = new GridAdapter(PersonalActivity.this, prizeList,prizeGridView);
				prizeGridView.setAdapter(prizeListAdapter);
    			break;
    			
    		case CODE_DELETE:
    			prizeList.remove(prizePosition);
    			prizeListAdapter.notifyDataSetChanged();
    			break;
    		}
	        
    	}else{
    		Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
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
			progressDialog.setMessage("正在修改");
			progressDialog.show();
			modifyInfoRequest();
			
			break;
		case R.id.btn_modify_pwd:
			progressDialog.setMessage("正在修改");
			progressDialog.show();
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
