package com.carusliu.opendoor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.application.AppApplication;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;

public class PersonalActivity extends Activity implements OnClickListener, OnItemClickListener{
	private TextView leftText, title, rightText;
	private List<Prize> prizeList = new ArrayList<Prize>();
	private ListView awardlist;
	private BaseAdapter awardListAdapter;
	private ImageButton userPhoto;
	private TextView accountName;
	private TextView integral;
	private TextView balance;
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
		accountName = (TextView) findViewById(R.id.tv_account_name);
		integral = (TextView) findViewById(R.id.tv_integral);
		balance = (TextView) findViewById(R.id.tv_balance);
		userPhoto = (ImageButton) findViewById(R.id.ib_personal_photo);
		
		title.setText("个人中心");
		leftText.setText("<返回");
		rightText.setText("注销>");
		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		userPhoto.setOnClickListener(this);
		
		//加载用户数据显示
		accountName.setText("username");
		integral.setText("integral");
		balance.setText("balance");
		//加载照片
		for(int i=0;i<6;i++){
			Prize prize = new Prize();
			prize.setId("");
			prize.setName("iPad Air");
			prize.setInfo("五一超低价");
			prize.setSmallPic(BitmapFactory.decodeResource(getResources(), R.drawable.my_wx));
			prizeList.add(prize);
		}
		//列表适配器
		awardListAdapter = new UserAwardListAdapter(this);
		//列表
		awardlist = (ListView) findViewById(R.id.lv_prize_content);
		awardlist.setAdapter(awardListAdapter);
		awardlist.setOnItemClickListener(this);
		//加载我的奖品列表
	}
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_info, menu);
        return true;
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
		}
	}
    
    
    @Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		//打开兑换界面
		
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
    private class UserAwardListAdapter extends BaseAdapter {

		private Context mContext;
		
		public UserAwardListAdapter(Context mContext){
			this.mContext = mContext;
		}
		
		@Override
		public int getCount() {
			return prizeList.size();
		}

		@Override
		public Object getItem(int index) {
			return prizeList.get(index);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView icon = null; //奖品图片
			TextView title = null; //奖品标题
			TextView desc = null; //奖品描述
			if(convertView == null || position < prizeList.size()){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.award_list_item, null);
				icon = (ImageView) convertView.findViewById(R.id.iv_item_image);
				title = (TextView) convertView.findViewById(R.id.tv_item_title);
				desc = (TextView) convertView.findViewById(R.id.tv_item_desc);
			}
			Prize prize = prizeList.get(position);

			//icon.setImageBitmap(BitmapFactory.decodeFile(DictEnum.domain + picture));
			title.setText(prize.getName());
			desc.setText(prize.getInfo());
			icon.setImageBitmap(prize.getSmallPic());
			return convertView;
		}
	}

}
