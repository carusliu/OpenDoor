package com.carusliu.opendoor.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.modle.Prize;

public class GridAdapter extends BaseAdapter{
	
	private ArrayList<Prize> prizeList;//数据
	private LayoutInflater inflater;
	
	
	public GridAdapter(Context context, ArrayList<Prize> prizeList) {
		super();
		this.prizeList = prizeList;
		inflater = LayoutInflater.from(context);
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
		ViewHolder holder = null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.grid_item, null);
			holder = new ViewHolder();
			//地址
			holder.prizePic = (ImageView) convertView.findViewById(R.id.prize_pic);
			//评分结果
			holder.prizeName = (TextView) convertView.findViewById(R.id.prize_name);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.prizeName.setText(prizeList.get(position).getName());
		
		holder.prizePic.setImageBitmap(prizeList.get(position).getSmallPic());
		
		return convertView;
	}
	
	class ViewHolder{
		public ImageView prizePic;
		public TextView prizeName;
	}

}
