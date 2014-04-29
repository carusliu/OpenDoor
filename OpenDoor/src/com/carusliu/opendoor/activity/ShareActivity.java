package com.carusliu.opendoor.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carusliu.opendoor.R;

public class ShareActivity extends Activity implements OnClickListener{
	private TextView leftText, title, rightText;
	private Button shareBtn;
	private EditText shareContent;
	private ProgressDialog progressDialog;
	 private static final String TAG = "read:";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		initView();
	}

	public void initView() {

        leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		shareBtn = (Button) findViewById(R.id.btn_share);
		shareContent = (EditText) findViewById(R.id.et_content);
		
		title.setText("分享");
		leftText.setText("<返回");
		//rightText.setText("注册>");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		shareBtn.setOnClickListener(this);

	}
	
	public void sendMSG(){
    	Uri smsToUri = Uri.parse("smsto:");  
    	  
    	Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);  
    	  
    	intent.putExtra("sms_body", shareContent.getText().toString());  
    	  
    	startActivity(intent);  
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_share:
			readContactsTask();
			break;
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
				progressDialog = new ProgressDialog(ShareActivity.this);
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
           Log.i(TAG, contactId);  
           Log.i(TAG, name);  
             
            
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
               Log.i(TAG, phoneNumber);  
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
    
}
