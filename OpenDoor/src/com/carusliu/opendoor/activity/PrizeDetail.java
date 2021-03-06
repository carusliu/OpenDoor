package com.carusliu.opendoor.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.AsyncImageLoader;
import com.carusliu.opendoor.tool.AsyncImageLoader.ImageCallback;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class PrizeDetail extends HWActivity implements OnClickListener {

	private TextView leftText, title, rightText;
	private TextView tvPrizeUse, tvPrizeInfo, tvPrizeId, tvPrizeAddress,
			tvPrizePhone, tvPrizeCipher, tvPrizeProvider;
	private ImageView promote_rate, shareBtn, prizePic;
	private Prize prize;
	AsyncImageLoader asyncImageLoader;
	private static final int CODE_BALANCE = 3;
	private static final int CODE_DELETE = 4;
	private ProgressDialog progressDialog;
	private ArrayList<String> phoneNums;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prize_detail);

		Intent intent = getIntent();
		if (intent != null) {
			prize = (Prize) intent.getSerializableExtra("prize");
		}

		if (prize == null) {
			prize = new Prize();
		}
		asyncImageLoader = new AsyncImageLoader(this);
		initView();
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		
		phoneNums = new ArrayList<String>();
	}

	public void initView() {

		// 花几毛钱提高中奖率事件绑定
		promote_rate = (ImageView) findViewById(R.id.btn_promote_rate);
		shareBtn = (ImageView) findViewById(R.id.btn_share);
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		prizePic = (ImageView) findViewById(R.id.prize_pic);
		if (prize != null) {
			((TextView) findViewById(R.id.tx_prize_use)).setText("抵用现金附送物品");
			((TextView) findViewById(R.id.prize_info)).setText(prize.getInfo());
			((TextView) findViewById(R.id.prize_id)).setText(prize.getNumber());
			((TextView) findViewById(R.id.prize_address)).setText(prize
					.getAddress());
			((TextView) findViewById(R.id.prize_phone)).setText(prize
					.getPhone());
			((TextView) findViewById(R.id.prize_cipher)).setText(prize
					.getCipher());
			((TextView) findViewById(R.id.prize_date)).setText(prize
					.getStartDate());
			((TextView) findViewById(R.id.tx_prize_provider)).setText(prize
					.getProvider());
		}
		title.setText("领奖啦");
		leftText.setText("<返回");
		rightText.setText("删除");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		promote_rate.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		asyncImageLoader.loadBitmap(prize.getSmallPic(), new ImageCallback() {
			public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
				prizePic.setImageBitmap(imageDrawable);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			progressDialog.setMessage("正删除");
			progressDialog.show();
			deleteAwardReuquest();
			break;
		case R.id.btn_share:
			readContactsTask();
			break;
		case R.id.btn_promote_rate:
			progressDialog.setMessage("正在查询余额");
			progressDialog.show();
			getUserBalanceReuquest();
			break;
		}

	}

	public void deleteAwardReuquest() {

		HashMap<String, String> data = new HashMap<String, String>();
		String userId = SharedPreferencesHelper.getString(
				SharedPreferencesKey.USER_ID, "0");
		data.put(SysConstants.USER_ID, userId);
		data.put(SysConstants.AWARD_ID, prize.getId());
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_DELETE);
		nbRequest.sendRequest(m_handler, SysConstants.DELETE_AWARD_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}
	public void getUserBalanceReuquest() {
		
		HashMap<String, String> data = new HashMap<String, String>();
		String userId = SharedPreferencesHelper.getString(
				SharedPreferencesKey.USER_ID, "0");
		data.put(SysConstants.USER_ID, userId);
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_BALANCE);
		nbRequest.sendRequest(m_handler, SysConstants.GET_USER_AMOUNT_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}

	@Override
	public void parseResponse(NBRequest request) {
		// TODO Auto-generated method stub
		progressDialog.cancel();
		if (request.getCode().equals(SysConstants.ZERO)) {
			JSONObject jsonObject = request.getBodyJSONObject();
			System.out.println(jsonObject);
			switch (request.getRequestTag()) {
			case CODE_BALANCE:
				
				double balance = jsonObject.optDouble("resultAmount");
				promoteRate(balance);
				break;

			case CODE_DELETE:
				Toast.makeText(getApplicationContext(), "刪除成功",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			}

		} else {
			Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT)
					.show();
		}
	}


	public void promoteRate(double balance) {

		String isLogin = SharedPreferencesHelper.getString(
				SharedPreferencesKey.IS_LOGIN, "0");
		if (isLogin.equals("1")) {
			// 判断余额
			if (0.2 > balance) {
				// 当前余额不够支付
				progressDialog.cancel();
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("余额不足");
				alert.setMessage("您的账户余额已不足，是否立即充值？");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "不，谢谢",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 关闭对话框
								dialog.cancel();
							}
						});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "是",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(PrizeDetail.this,
										RechargeActivity.class);
								startActivity(intent);
							}
						});
				// 显示对话框
				alert.show();
			} else {
				/*// 提示当前将消费金额
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("支付确认");
				alert.setMessage("将从您余额中扣除 0.2元，是否继续？");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 关闭对话框
								dialog.cancel();
							}
						});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "好",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 支付
								Intent intent = new Intent(PrizeDetail.this, ShakeActivity.class);
								startActivity(intent);
							}
						});
				// 显示对话框
				alert.show();*/
				Intent intent = new Intent(PrizeDetail.this, ShakeActivity.class);
				intent.putExtra("from", "PrizeDetail");
				setResult(1, intent);
				finish();
			}
		} else {
			// 提示登录
			// Toast.makeText(this, "您尚未登录，请先登录", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(PrizeDetail.this, Login.class);
			intent.putExtra("from", "PrizeDetail");
			startActivity(intent);
		}
	}
	
	public void readContactsTask(){
    	new AsyncTask<Integer, Integer, String>() {

			@Override
			protected String doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				readContacts();
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				progressDialog.cancel();
				sendMSG();
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				progressDialog.setTitle("读取联系人");
				progressDialog.setMessage("正在读取通讯录，请稍后。。。");
				//progressDialog.setProgressStyle(progressDialog.)
				progressDialog.show();
				
			}

		}.execute(null,null,null);
    }
    
    public void readContacts(){
		Cursor cursor = this.getBaseContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,   
                null, null, null, null);  
       int contactIdIndex = 0;  
       int nameIndex = 0;  
         
       if(cursor.getCount() > 0) {  
           contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);  
           nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);  
       }  
       while(cursor.moveToNext()) {  
           String contactId = cursor.getString(contactIdIndex);  
           String name = cursor.getString(nameIndex);  
          // Log.i(TAG, contactId);  
           //Log.i(TAG, name);  
             
            
            //查找该联系人的phone信息 
              
           Cursor phones = this.getBaseContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
                   null,   
                   ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,   
                   null, null);  
           int phoneIndex = 0;  
           if(phones.getCount() > 0) {  
               phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);  
           }  
           while(phones.moveToNext()) {  
               String phoneNumber = phones.getString(phoneIndex);
               phoneNums.add(phoneNumber);
               //Log.i(TAG, phoneNumber);  
           }  
             
            
           /* //查找该联系人的email信息 
              
           Cursor emails = this.getBaseContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,   
                   null,   
                   ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId,   
                   null, null);  
           int emailIndex = 0;  
           if(emails.getCount() > 0) {  
               emailIndex = emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);  
           }  
           while(phones.moveToNext()) {  
               String email = emails.getString(emailIndex);  
               Log.i(TAG, email);  
           }*/
           
           phones.close();
           //emails.close();
       }
       cursor.close();
	}
    
    public void sendMSG(){
    	int num = new Random().nextInt(phoneNums.size());
    	
    	Uri smsToUri = Uri.parse("smsto:"+phoneNums.get(num));  
    	  
    	Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);  
    	  
    	intent.putExtra("sms_body", "这个应用碉堡了，各类大奖等你拿！");  
    	  
    	startActivity(intent);  
    }

}
