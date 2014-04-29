package com.carusliu.opendoor.activity;
import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.application.AppApplication;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.DebugLog;
import com.carusliu.opendoor.tool.Tool;


public class HWActivity extends Activity {

	protected Handler m_handler;

    private ProgressDialog m_progressDialog = null;

	private AlertDialog m_alertdialog;

	private boolean m_destroyed = false;
	
	public Handler getHandler() {
        return m_handler;
    }

    public void setHandler(Handler m_handler) {
        this.m_handler = m_handler;
    }
    
	@Override
	protected void onStart() {
		super.onStart();
//		R2ExceptionHandler.init(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		m_destroyed = true;
		DebugLog.logd("..onDestroy: " + this.getClass().getName());
		
		dismissAlertDialog();
		dismissProgressDialog();
		AppApplication.getInstance().delActivity(this);
	}
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugLog.logd("....onCreate: " + this.getClass().getName());
		AppApplication.getInstance().addActivity(this);
		
		m_handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {

				case SysConstants.RESPONSE_SUCCESS:
					NBRequest request = (NBRequest) msg.obj;
					if (request != null) {
						DebugLog.logi("BizActivity--->url: " + request.getUrl() + ",m_requestTag: " + request.getRequestTag());
						
						/*
						 * Token 杩囨�?
						 * */
						String code = request.getCode();
						boolean m_error = processError(request);
						if (m_error) {
							parseResponse(request);
						}
					}
					// dismissProgressDialog();
					break;
				case SysConstants.NET_ERROR:
					try {
						showProgressDialog(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					dismissProgressDialog();
					break;
				case SysConstants.LOAD:
					showLoading(true);
					break;
				case SysConstants.SYNCLOAD:
					showLoading(false);
					break;
				case SysConstants.DISSMISS:
					dismissProgressDialog();
					break;
				default:
					break;
				}
			}
		};
	}

	/*
	 * Token 杩囨湡锛岃烦杞埌Login
	 * */
	private void gotoLogin() {
	}
	
	public void alertDialog(int msgId) {
		alertDialog(msgId, R.string.ok);
	}

	public void alertDialog(String msgStr) {
		alertDialog(msgStr, getString(R.string.ok));
	}

	public void alertDialog(int msgId, int positiveButtonStrId) {
		alertDialog(getString(msgId), getString(positiveButtonStrId));
	}

	public void alertDialog(String msgStr, String positiveButtonStr) {

		if (m_alertdialog != null && m_alertdialog.isShowing()) {
			return;
		}
		m_alertdialog = new AlertDialog.Builder(this).setMessage(msgStr).setPositiveButton(positiveButtonStr, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				//actFinish(); //点击确定后结束当前activity
			}
		}).create();

		if (!this.isFinishing()) {
			m_alertdialog.show();
		}
	}
	
	public void dismissAlertDialog(){
		if (m_alertdialog != null && m_alertdialog.isShowing()) {
			m_alertdialog.dismiss();
		}
	}

	public void showProgressDialog(int strId) {
		showProgressDialog(getString(strId));
	}

	public void showProgressDialog(String str) {
		if (m_progressDialog != null && m_progressDialog.isShowing()) {
			m_progressDialog.setMessage(str);
			return;
		}
		m_progressDialog = null;
		m_progressDialog = ProgressDialog.show(this, "", str, true, true);
		// m_progressDialog = ProgressDialog.show(this, "", str, true, false);
		// //.setCancelable(false)4.0绯荤粺瑙︽懜灞忓箷浠讳綍浣嶇疆閮戒細cancel鎺夊脊鍑烘
	}
	
	/**
	 * 鍙敤浜庡脊鍑轰竴涓笉鍙痗ancel鐨刾rogressdialog
	 * @param strId
	 * @param cancelable 
	 */
	public void showProgressDialog(int strId, boolean cancelable) {
		showProgressDialog(getString(strId), cancelable);
	}

	public void showProgressDialog(String str, boolean cancelable) {
		if (m_progressDialog != null && m_progressDialog.isShowing()) {
//			m_progressDialog.setMessage(str);
//			return;
			m_progressDialog.dismiss();
		}
		m_progressDialog = null;
		m_progressDialog = ProgressDialog.show(this, "", str, true, cancelable);
		// m_progressDialog = ProgressDialog.show(this, "", str, true, false);
		// //.setCancelable(false)4.0绯荤粺瑙︽懜灞忓箷浠讳綍浣嶇疆閮戒細cancel鎺夊脊鍑烘
	}

	public void dismissProgressDialog() {
		if (m_progressDialog != null && m_progressDialog.isShowing()) {
			m_progressDialog.dismiss();
		}
		m_progressDialog = null;
	}

	public void showLoading(boolean cancelable) {
		showProgressDialog(R.string.netLoading, cancelable);
	}

	public void showLoading() {
		showProgressDialog(R.string.netLoading);
	}
	
	/**
	 * create a netloading dialog, if u want to do this in the main thread, u
	 * may use {@link #showLoading()}
	 */
	public void showNetLoading() {
//		showProgressDialog(R.string.netLoading);
		m_handler.sendEmptyMessage(SysConstants.LOAD);
	}
	
	public void showSyncNetLoading() {
		m_handler.sendEmptyMessage(SysConstants.SYNCLOAD);
	}
	
	public void dismissNetLoading() {
		m_handler.sendEmptyMessage(SysConstants.DISSMISS);
	}
	
	public void showToast(int strId) {
		Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show();
	}

	public void showToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public boolean checkInternet() {
		if (!Tool.hasInternet(this)) {
			alertDialog(R.string.networkUnavailable);
			dismissProgressDialog();
			return false;
		}
		return true;
	}

	public void parseResponse(NBRequest request) {

	}

	// Success for true
	public boolean processError(NBRequest request) {
		return true;
	}

	public boolean processCommonError(NBRequest request) {

		if (request.getError() != null)// 杩炴帴寮傚父
		{
			alertDialog(R.string.networkUnavailable);
			return false;
		}

		int status = request.getResponseCode();
		DebugLog.logd("status=" + status + " err:" + request.getError());

		if (HttpStatus.SC_OK != status)// 缃戠粶璇锋眰閿欒�?
		{
			alertDialog(getString(R.string.networkError));
			return false;
		}

		String code = request.getCode();
		DebugLog.logd("url : " + request.getUrl() + "code=" + code);

		if (SysConstants.RESP_SYSTEM_ERROR.equals(code))// 绯荤粺寮傚父
		{
			alertDialog(request.getMessage());
			return false;
		}

		return true;
	}

	public boolean processGeneralError(NBRequest request) {

		boolean commonError = processCommonError(request);
		if (commonError) {
			String code = request.getCode();
			if (SysConstants.RESP_SUCCESS.equals(code)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSuccess(NBRequest request) {

		String code = request.getCode();
		if (SysConstants.RESP_SUCCESS.endsWith(code)) {
			return true;
		}
		return false;
	}
	
	public boolean isActDestroyed() {
		return m_destroyed;
	}
	
	public void setActDestroyed() {
		m_destroyed = true;
	}
	
	public void actFinish() {
		m_destroyed = true;
		finish();
	}
	public void showListView(SimpleAdapter adpter){
		
	}
}
