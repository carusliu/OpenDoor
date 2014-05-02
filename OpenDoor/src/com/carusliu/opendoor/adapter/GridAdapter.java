package com.carusliu.opendoor.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.tool.AsyncImageLoader;
import com.carusliu.opendoor.tool.AsyncImageLoader.ImageCallback;

public class GridAdapter extends BaseAdapter{
	
	private ArrayList<Prize> prizeList;//数据
	private GridView gridView;
	private LayoutInflater inflater;
	AsyncImageLoader asyncImageLoader ;
	
	public GridAdapter(Context context, ArrayList<Prize> prizeList, GridView listView) {
		super();
		this.prizeList = prizeList;
		this.gridView = listView;
		inflater = LayoutInflater.from(context);
		asyncImageLoader = new AsyncImageLoader(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return prizeList == null ? 0 : prizeList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return prizeList==null?null:prizeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewCache viewCache = null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.grid_item, null);
			viewCache = new ViewCache(convertView);
			//地址
			viewCache.prizePic = (ImageView) convertView.findViewById(R.id.prize_pic);
			//评分结果
			viewCache.prizeName = (TextView) convertView.findViewById(R.id.prize_name);
			
			convertView.setTag(viewCache);
		}else{
			viewCache = (ViewCache) convertView.getTag();
		}
		
		Prize prize = prizeList.get(position);
		viewCache.prizePic.setTag(prize.getSmallPic());
		viewCache.prizeName.setText(prize.getName());
		//异步加载图片
		Bitmap cachedImage = asyncImageLoader.loadBitmap(prize.getSmallPic(), new ImageCallback() {  
            public void imageLoaded(Bitmap imageDrawable, String imageUrl) {  
                ImageView imageViewByTag = (ImageView) gridView.findViewWithTag(imageUrl);  
                if (imageViewByTag != null) {  
                    imageViewByTag.setImageBitmap(imageDrawable);  
                }  
            }  
        });
		if (cachedImage == null) {  
			viewCache.prizePic.setImageResource(R.drawable.new_s_2);  
        }else{  
        	viewCache.prizePic.setImageBitmap(cachedImage);  
        }  
		return convertView;
	}
	
	class ViewCache{
		private View baseView;  
		public ImageView prizePic;
		public TextView prizeName;
		
		public ViewCache(View baseView) {
			super();
			this.baseView = baseView;
		}
		public ImageView getPrizePic() {
			return prizePic;
		}
		public void setPrizePic(ImageView prizePic) {
			this.prizePic = prizePic;
		}
		public TextView getPrizeName() {
			return prizeName;
		}
		public void setPrizeName(TextView prizeName) {
			this.prizeName = prizeName;
		}
		
	}

}
