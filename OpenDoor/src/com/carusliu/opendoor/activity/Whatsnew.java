package com.carusliu.opendoor.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.AsyncImageLoader;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;
import com.carusliu.opendoor.tool.AsyncImageLoader.ImageCallback;

public class Whatsnew extends HWActivity implements OnClickListener {
	
	private ViewPager mViewPager;	
	private ViewPager newPager, superPager;
	private ImageView mPage0;
	private ImageView mPage1;
	private ImageView mPage2;
	private ImageView newPrizeImage1,newPrizeImage2,newPrizeImage3,superPrizeImage1,superPrizeImage2,superPrizeImage3,newPrizeImageS1,newPrizeImageS2,newPrizeImageS3,superPrizeImageS1,superPrizeImageS2,superPrizeImageS3;
	private Button startBtn;
	private ArrayList<ImageView> newViewList, superViewList;
	private int newCurrentItem = 0; // 当前图片的索引号
	private int superCurrentItem = 0; // 当前图片的索引号
	private Timer newTimer, superTimer;
	private TimerTask newTask, superTask;
	private TextView leftText, title, rightText;
	private ProgressDialog progressDialog;
	private ArrayList<Prize> prizeList;
	private ArrayList<ImageView> largeImageList,smallImageList;
	AsyncImageLoader asyncImageLoader ;
	private int currIndex = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				newPager.setCurrentItem(newCurrentItem);// 切换当前显示的图片
				break;
			case 1:
				superPager.setCurrentItem(superCurrentItem);// 切换当前显示的图片
				break;
			}
		};
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatsnew_viewpager);
        
        prizeList = new ArrayList<Prize>();
        asyncImageLoader = new AsyncImageLoader(this);
        mViewPager = (ViewPager)findViewById(R.id.whatsnew_viewpager);        
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
       
        mPage0 = (ImageView)findViewById(R.id.page0);
        mPage1 = (ImageView)findViewById(R.id.page1);
        mPage2 = (ImageView)findViewById(R.id.page2);

        
      //将要分页显示的View装入数组中
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.whats1, null);
        View view2 = mLi.inflate(R.layout.whats2, null);
        View view3 = mLi.inflate(R.layout.whats3, null);
        
        newPager = (ViewPager)view1.findViewById(R.id.new_prize_pager);
        superPager = (ViewPager)view2.findViewById(R.id.super_prize_pager);
        
        newPrizeImageS1 = (ImageView)view1.findViewById(R.id.img_new_s_1);
        newPrizeImageS2 = (ImageView)view1.findViewById(R.id.img_new_s_2);
        newPrizeImageS3 = (ImageView)view1.findViewById(R.id.img_new_s_3);
        superPrizeImageS1 = (ImageView)view2.findViewById(R.id.img_super_s_1);
        superPrizeImageS2 = (ImageView)view2.findViewById(R.id.img_super_s_2);
        superPrizeImageS3 = (ImageView)view2.findViewById(R.id.img_super_s_3);
        
        smallImageList = new ArrayList<ImageView>();
        smallImageList.add(newPrizeImageS1);
        smallImageList.add(newPrizeImageS2);
        smallImageList.add(newPrizeImageS3);
        smallImageList.add(superPrizeImageS1);
        smallImageList.add(superPrizeImageS2);
        smallImageList.add(superPrizeImageS3);
        
		leftText = (TextView) view3.findViewById(R.id.btn_left);
		title = (TextView) view3.findViewById(R.id.tv_center);
		rightText = (TextView) view3.findViewById(R.id.btn_right);
		startBtn = (Button)view3.findViewById(R.id.startBtn);
		
		title.setText("芝麻开门");
		leftText.setText("<关于");
		
		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		startBtn.setOnClickListener(this);
        
        newPager.setOnPageChangeListener(new OnNewPageChangeListener());
        superPager.setOnPageChangeListener(new OnSuperPageChangeListener());
        
      //每个页面的view数据
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        
        newViewList = new ArrayList<ImageView>();
        newPrizeImage1 = new ImageView(this);
        newPrizeImage1.setImageResource(R.drawable.home_prize_ad);
        newPrizeImage1.setScaleType(ScaleType.CENTER_CROP);
        newViewList.add(newPrizeImage1);
        newPrizeImage2 = new ImageView(this);
        newPrizeImage2.setImageResource(R.drawable.new_l_2);
        newPrizeImage2.setScaleType(ScaleType.CENTER_CROP);
        newViewList.add(newPrizeImage2);
        newPrizeImage3 = new ImageView(this);
        newPrizeImage3.setImageResource(R.drawable.home_prize_ad);
        newPrizeImage3.setScaleType(ScaleType.CENTER_CROP);
        newViewList.add(newPrizeImage3);
        
        superViewList = new ArrayList<ImageView>();
        superPrizeImage1 = new ImageView(this);
        superPrizeImage1.setImageResource(R.drawable.home_prize_ad);
        superPrizeImage1.setScaleType(ScaleType.CENTER_CROP);
        superViewList.add(superPrizeImage1);
        superPrizeImage2 = new ImageView(this);
        superPrizeImage2.setImageResource(R.drawable.home_prize_ad);
        superPrizeImage2.setScaleType(ScaleType.CENTER_CROP);
        superViewList.add(superPrizeImage2);
        superPrizeImage3 = new ImageView(this);
        superPrizeImage3.setImageResource(R.drawable.home_prize_ad);
        superPrizeImage3.setScaleType(ScaleType.CENTER_CROP);
        superViewList.add(superPrizeImage3);
        
        largeImageList = new ArrayList<ImageView>();
        largeImageList.addAll(newViewList);
        largeImageList.addAll(superViewList);
        //填充ViewPager的数据适配器
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
		
		PagerAdapter mNewPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return newViewList.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(newViewList.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(newViewList.get(position));
				return newViewList.get(position);
			}
		};
		
		PagerAdapter mSuperPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return superViewList.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(superViewList.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(superViewList.get(position));
				return superViewList.get(position);
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
		newPager.setAdapter(mNewPagerAdapter);
		superPager.setAdapter(mSuperPagerAdapter);
		newPager.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 
				switch(event.getAction()){
				
					case MotionEvent.ACTION_DOWN:
						newTimer.cancel();
						break;
					case MotionEvent.ACTION_MOVE:
						newTimer.cancel();
						break;
					case MotionEvent.ACTION_UP:
						exacuteNewSwitchTask();
						break;
				}
				return false;
			}
		});
		superPager.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch(event.getAction()){
				
				case MotionEvent.ACTION_DOWN:
					superTimer.cancel();
					break;
				case MotionEvent.ACTION_MOVE:
					superTimer.cancel();
					break;
				case MotionEvent.ACTION_UP:
					exacuteSuperSwitchTask();
					break;
				}
				return false;
			}
		});
		exacuteNewSwitchTask();
		exacuteSuperSwitchTask();
		
		progressDialog = new ProgressDialog(this);
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
					//JSONObject prizeObj = prizeArray.optJSONObject(i);
					Prize prize = new Prize();
					/*prize.setId(prizeObj.optString(""));
					prize.setName(prizeObj.optString(""));
					prize.setInfo(prizeObj.optString(""));*/
					//prize.setSmallPic(SysConstants.SERVER+prizeObj.optString("awardImage"));
					prize.setSmallPic("http://i0.sinaimg.cn/home/2014/0509/U8843P30DT20140509085453.jpg");
					prizeList.add(prize);
				}
				//异步加载图片
			
				asyncImageLoader.loadBitmap(prizeList.get(0).getSmallPic(), new ImageCallback() {  
		            public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
		            	
		                smallImageList.get(0).setImageBitmap(imageDrawable);
		                largeImageList.get(0).setImageBitmap(imageDrawable);
		            }  
		        });
				asyncImageLoader.loadBitmap(prizeList.get(1).getSmallPic(), new ImageCallback() {  
					public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
						smallImageList.get(1).setImageBitmap(imageDrawable);
						largeImageList.get(1).setImageBitmap(imageDrawable);
					}  
				});
				asyncImageLoader.loadBitmap(prizeList.get(2).getSmallPic(), new ImageCallback() {  
					public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
						smallImageList.get(2).setImageBitmap(imageDrawable);
						largeImageList.get(2).setImageBitmap(imageDrawable);
					}  
				});
				asyncImageLoader.loadBitmap(prizeList.get(3).getSmallPic(), new ImageCallback() {  
					public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
						smallImageList.get(3).setImageBitmap(imageDrawable);
						largeImageList.get(3).setImageBitmap(imageDrawable);
					}  
				});
				asyncImageLoader.loadBitmap(prizeList.get(4).getSmallPic(), new ImageCallback() {  
					public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
						smallImageList.get(4).setImageBitmap(imageDrawable);
						largeImageList.get(4).setImageBitmap(imageDrawable);
					}  
				});
				asyncImageLoader.loadBitmap(prizeList.get(5).getSmallPic(), new ImageCallback() {  
					public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
						smallImageList.get(5).setImageBitmap(imageDrawable);
						largeImageList.get(5).setImageBitmap(imageDrawable);
					}  
				});
				
				progressDialog.dismiss();
			}
	}
    
    public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			System.out.println(arg0);
			switch (arg0) {
			case 0:				
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
				//exacuteNewSwitchTask();
				//superTimer.cancel();
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
				//exacuteSuperSwitchTask();
				//newTimer.cancel();
				break;
			case 2:
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;

			}
			currIndex = arg0;

		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
    
    public class OnNewPageChangeListener implements OnPageChangeListener {
    	@Override
    	public void onPageSelected(int arg0) {
    		switch (arg0) {
    		case 0:				
    			newPrizeImageS1.setBackgroundColor(getResources().getColor(R.color.hint_blue));
    			newPrizeImageS2.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			newPrizeImageS3.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			break;
    		case 1:
    			newPrizeImageS1.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			newPrizeImageS2.setBackgroundColor(getResources().getColor(R.color.hint_blue));
    			newPrizeImageS3.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			break;
    		case 2:
    			newPrizeImageS1.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			newPrizeImageS2.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			newPrizeImageS3.setBackgroundColor(getResources().getColor(R.color.hint_blue));
    			break;
    			
    		}
    		currIndex = arg0;
    		
    	}
    	@Override
    	public void onPageScrolled(int arg0, float arg1, int arg2) {
    	}
    	
    	@Override
    	public void onPageScrollStateChanged(int arg0) {
    	}
    }
    
    public class OnSuperPageChangeListener implements OnPageChangeListener {
    	@Override
    	public void onPageSelected(int arg0) {
    		switch (arg0) {
    		case 0:				
    			superPrizeImageS1.setBackgroundColor(getResources().getColor(R.color.hint_blue));
    			superPrizeImageS2.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			superPrizeImageS3.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			break;
    		case 1:
    			superPrizeImageS1.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			superPrizeImageS2.setBackgroundColor(getResources().getColor(R.color.hint_blue));
    			superPrizeImageS3.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			break;
    		case 2:
    			superPrizeImageS1.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			superPrizeImageS2.setBackgroundColor(getResources().getColor(R.color.bg_color));
    			superPrizeImageS3.setBackgroundColor(getResources().getColor(R.color.hint_blue));
    			break;
    			
    		}
    		currIndex = arg0;
    		
    	}
    	@Override
    	public void onPageScrolled(int arg0, float arg1, int arg2) {
    	}
    	
    	@Override
    	public void onPageScrollStateChanged(int arg0) {
    	}
    }
    
    public void exacuteNewSwitchTask(){
		newTimer = new Timer();
		newTask = new TimerTask(){
			@Override
			public void run() {
				synchronized (newPager) {
					newCurrentItem = (newCurrentItem + 1) % newViewList.size();
					handler.obtainMessage(0).sendToTarget(); // 通过Handler切换图片
				}
			}};
		newTimer.schedule(newTask, 0, 2000);
	}
    
    public void exacuteSuperSwitchTask(){
    	superTimer = new Timer();
    	superTask = new TimerTask(){
    		@Override
    		public void run() {
    			synchronized (superPager) {
    				superCurrentItem = (superCurrentItem + 1) % superViewList.size();
    				handler.obtainMessage(1).sendToTarget(); // 通过Handler切换图片
    			}
    		}};
    		superTimer.schedule(superTask, 0, 2000);
    }

    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btn_left:
			intent.setClass(Whatsnew.this, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_right:
			if (SharedPreferencesHelper.getString(SharedPreferencesKey.IS_LOGIN,
					"0").equals("0")) {
				
				intent.setClass(Whatsnew.this, Login.class);
			} else {
				intent.setClass(Whatsnew.this, PersonalActivity.class);
			}
			
			startActivity(intent);
			break;
		case R.id.startBtn:
			intent.setClass(Whatsnew.this, ShakeActivity.class);
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
    
}
