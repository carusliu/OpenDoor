package com.carusliu.opendoor.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.carusliu.opendoor.R;

public class LoadActivity extends Activity {
	private ViewPager mViewPager;	
	private GifView gif;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);
		
		mViewPager = (ViewPager)findViewById(R.id.load_viewpager);
		
		LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.load1, null);
        View view2 = mLi.inflate(R.layout.load2, null);
		
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int SCREEN_WIDTH = display.getWidth(); //deprecated Use getSize(Point) instead.
		int SCREEN_HEIGHT = display.getHeight(); //deprecated Use getSize(Point) instead.
		gif = (GifView)view1.findViewById(R.id.gif_bg);
		gif.setGifImage(R.drawable.load1);

		gif.setShowDimension(SCREEN_WIDTH, SCREEN_HEIGHT);

		gif.setGifImageType(GifImageType.COVER);
		
		view2.findViewById(R.id.button_go).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoadActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load, menu);
		return true;
	}

}
