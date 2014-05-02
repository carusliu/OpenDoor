package com.carusliu.opendoor.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager{

	public ChildViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ChildViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
      getParent().requestDisallowInterceptTouchEvent(true);  
          super.dispatchTouchEvent(ev);  
           
          return true;  
    }  
	
}