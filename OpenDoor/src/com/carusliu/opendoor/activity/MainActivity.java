package com.carusliu.opendoor.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.adapter.GridAdapter;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.AsyncImageLoader;
import com.carusliu.opendoor.tool.ObjectCacheUtils;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class MainActivity extends HWActivity implements OnClickListener {

	private TextView leftText, title, rightText;
	private Button shakeBtn;
	private GridView superGridView, hotGridView;
	private ArrayList<Prize> superPrizeList, hotPrizeList;//数据
	private GridAdapter superListAdapter, hotListAdapter;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}

	public void initView() {
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		shakeBtn = (Button) findViewById(R.id.btn_shake);
		superGridView = (GridView) findViewById(R.id.grid_super_prize);
		hotGridView = (GridView) findViewById(R.id.grid_hot_prize);
		
		title.setText("芝麻开门");
		leftText.setText("<关于");
		if (SharedPreferencesHelper.getString(SharedPreferencesKey.IS_LOGIN,
				"0").equals("0")) {
			
			rightText.setText("登录>");
		} else {
			rightText.setText("个人>");
		}
		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		shakeBtn.setOnClickListener(this);
		
		//先显示本地图片
		superPrizeList = new ArrayList<Prize>();
		hotPrizeList = new ArrayList<Prize>();
		//Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.my_wx);
		//for(int i=0;i<10;i++){
			Prize prize = new Prize();
			prize.setId("");
			prize.setName("iPad Air");
			prize.setInfo("");
			prize.setSmallPic("http://p.bagtree.com/big/1170458/1170458-10_01.jpg");
			superPrizeList.add(prize);
		//}
		//ObjectCacheUtils.saveObjCache(superPrizeList);
		//superPrizeList = ObjectCacheUtils.readObjCache();
		/*for(int i=0;i<15;i++){
			Prize prize = new Prize();
			prize.setId("");
			prize.setName("iPad Air");
			prize.setInfo("");
			//prize.setSmallPic(bitmap);
			hotPrizeList.add(prize);
		}*/
		//
		superListAdapter = new GridAdapter(MainActivity.this, superPrizeList,superGridView);
		hotListAdapter = new GridAdapter(MainActivity.this, superPrizeList,hotGridView);
		superGridView.setAdapter(superListAdapter);
		hotGridView.setAdapter(hotListAdapter);
		
		superGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, PrizeDetail.class);
				startActivity(intent);
			}
		});
		//发送请求
		//sendPrizeRequest();
	}

	public void sendPrizeRequest(){
		HashMap<String, String> data = new HashMap<String, String>();
		//data.put(SysConstants.USER_ID, "");
		NBRequest nbRequest = new NBRequest();
		nbRequest.sendRequest(m_handler, SysConstants.REQUEST_LOGIN, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}

	@Override
	public void parseResponse(NBRequest request) {
			if (SysConstants.ZERO.equals(request.getCode())) {
				
				//解析数据更新界面
				JSONObject jsonObject = request.getBodyJSONObject();
				JSONArray prizeArray = jsonObject.optJSONArray("");
				for(int i=0;i<prizeArray.length();i++){
					JSONObject prizeObj = prizeArray.optJSONObject(i);
					Prize prize = new Prize();
					prize.setId(prizeObj.optString(""));
					prize.setName(prizeObj.optString(""));
					prize.setInfo(prizeObj.optString(""));
					prize.setSmallPic(prizeObj.optString(""));
					superPrizeList.add(prize);
				}
				
			}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btn_left:
			intent.setClass(MainActivity.this, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_right:
			if (SharedPreferencesHelper.getString(SharedPreferencesKey.IS_LOGIN,
					"0").equals("0")) {
				
				intent.setClass(MainActivity.this, Login.class);
			} else {
				intent.setClass(MainActivity.this, PersonalActivity.class);
			}
			
			startActivity(intent);
			break;
		case R.id.btn_shake:
			intent.setClass(MainActivity.this, ShakeActivity.class);
			startActivity(intent);
			break;
		}
	}

}
