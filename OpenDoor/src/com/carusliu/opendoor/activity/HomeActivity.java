package com.carusliu.opendoor.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.R.drawable;
import com.carusliu.opendoor.R.id;
import com.carusliu.opendoor.R.layout;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.AsyncImageLoader;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;
import com.carusliu.opendoor.tool.AsyncImageLoader.ImageCallback;

public class HomeActivity extends HWActivity implements OnClickListener {
	private ViewPager mViewPager;	
	private ActivityManager mActivityManager = null ; 
	private ViewGroup superContainer, newContainer;
	private HorizontalScrollView superScroll, newScroll;
	private TextView leftText, title, rightText;
	private Button startBtn;
	private ProgressDialog progressDialog;
	AsyncImageLoader asyncImageLoader ;
	public static final String TAG = "KUWO";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mViewPager = (ViewPager)findViewById(R.id.home_viewpager);
		mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);  
		asyncImageLoader = new AsyncImageLoader(this);
		
		LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.home1, null);
        View view2 = mLi.inflate(R.layout.whats3, null);
        
        leftText = (TextView) view2.findViewById(R.id.btn_left);
		title = (TextView) view2.findViewById(R.id.tv_center);
		rightText = (TextView) view2.findViewById(R.id.btn_right);
		startBtn = (Button)view2.findViewById(R.id.startBtn);
		
		title.setText("芝麻开门");
		//leftText.setText("<关于");
		
		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		startBtn.setOnClickListener(this);
        
		newContainer = (ViewGroup)view1.findViewById(R.id.view_new);
		newScroll = (HorizontalScrollView)view1.findViewById(R.id.scroll_new);
        superContainer = (ViewGroup)view1.findViewById(R.id.view_super);
		superScroll = (HorizontalScrollView)view1.findViewById(R.id.scroll_super);
		/*for(int i=0;i<6;i++){
			
			ImageView imageView = new ImageView(HomeActivity.this);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(dip2px(100),dip2px(80));
			param.leftMargin = dip2px(5);
			param.rightMargin = dip2px(5);
			imageView.setImageResource(R.drawable.new_s_1);
			View scrollItem = LayoutInflater.from(this).inflate(R.layout.scroll_item, null);
			newContainer.addView(scrollItem);
			
		}
		for(int i=0;i<6;i++){
			View scrollItem = LayoutInflater.from(this).inflate(R.layout.scroll_item, null);
			superContainer.addView(scrollItem);
			
		}*/
        
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        
        PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("正在获取奖品信息，请稍后...");
		if(isOnline()){
			progressDialog.show();
			sendPrizeRequest();
		}else{
			Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void sendPrizeRequest(){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put(SysConstants.LANTITUDE, "140");
		data.put(SysConstants.LONGITUDE, "39");
		NBRequest nbRequest = new NBRequest();
		
		nbRequest.sendRequest(m_handler, SysConstants.TODAY_AWARDS_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}
	
	@Override
	public void parseResponse(NBRequest request) {
			if (SysConstants.ZERO.equals(request.getCode())) {
				//解析数据更新界面
				JSONObject jsonObject = request.getBodyJSONObject();
				JSONArray prizeArray = jsonObject.optJSONArray("awardList");
				for(int i=0;i<6;i++){
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
					//prize.setSmallPic("http://i0.sinaimg.cn/home/2014/0509/U8843P30DT20140509085453.jpg");
					//异步加载图片
					View scrollItem = LayoutInflater.from(this).inflate(R.layout.scroll_item, null);
					final ImageView img = (ImageView)scrollItem.findViewById(R.id.iv_item_image);
					TextView title = (TextView)scrollItem.findViewById(R.id.tv_item_title);
					title.setText(prize.getInfo());
					
					asyncImageLoader.loadBitmap(prize.getSmallPic(), new ImageCallback() {  
			            public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
			            	img.setImageBitmap(imageDrawable);
			            }  
			        });
					
					if("0".equals(prizeObj.optString("awardType"))){
						newContainer.addView(scrollItem);
					}else{
						superContainer.addView(scrollItem);
					}
					
				}

				progressDialog.dismiss();
				performScroll();
			}
	}

	public void performScroll(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(superContainer.getRight()-superScroll.getScrollX()-superScroll.getWidth()==0){
					superScroll.smoothScrollTo(0, 0);
				}else{
					superScroll.smoothScrollBy(dip2px(110), 0);
				}
				if(newContainer.getRight()-newScroll.getScrollX()-newScroll.getWidth()==0){
					newScroll.smoothScrollTo(0, 0);
				}else{
					newScroll.smoothScrollBy(dip2px(110), 0);
				}
				
			}}, 0, 2000);
	}
	
	public int dip2px(float dpValue) {  
        final float scale = HomeActivity.this.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btn_left:
			intent.setClass(HomeActivity.this, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_right:
			if (SharedPreferencesHelper.getString(SharedPreferencesKey.IS_LOGIN,
					"0").equals("0")) {
				intent.putExtra("from","Whatsnew");
				intent.setClass(HomeActivity.this, Login.class);
			} else {
				intent.setClass(HomeActivity.this, PersonalActivity.class);
			}
			//intent.setClass(Whatsnew.this, PersonalActivity.class);
			startActivity(intent);
			break;
		case R.id.startBtn:
			intent.setClass(HomeActivity.this, ShakeActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	//判断是否有网络连接
  	public boolean isOnline() {
  	    ConnectivityManager connMgr = (ConnectivityManager) 
  	            getSystemService(Context.CONNECTIVITY_SERVICE);
  	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
  	    return (networkInfo != null && networkInfo.isConnected());
  	} 
  	
  	public Location getLocation(){
		LocationManager loctionManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
		criteria.setAltitudeRequired(false);//不要求海拔
		criteria.setBearingRequired(false);//不要求方位
		criteria.setCostAllowed(true);//允许有花费
		criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
		//从可用的位置提供器中，匹配以上标准的最佳提供器
		String provider = loctionManager.getBestProvider(criteria, true);
		Location location  = loctionManager.getLastKnownLocation(provider);
		
		return location;
	}
}
