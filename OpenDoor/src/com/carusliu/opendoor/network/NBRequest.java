package com.carusliu.opendoor.network;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.carusliu.opendoor.activity.HWActivity;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.DebugLog;

/**
 * notice:
 * if we get a net error,the progressdialog in BizActivity will dismiss;
 * if we get a successful respond, we check it with {link {@link HWActivity#processError(NBRequest)} (default return true);
 * only when the processError method return true,we callback the {@link HWActivity#parseResponse(NBRequest)};
 * the default setting about the dialog in activity is when we get a successful respond, we dismiss the dialog, if u don't want so, u can use {@link #setDismissLoading(boolean)}
 *            by qinbi.
 */
public class NBRequest extends Object {

	private static final String MESSAGE = "message";
	private static final String CODE = "code";
	private static final String HAS_NEW_MYCARD = "is_new_mycard";//:0—�?无新名片,1—�?有新名片�?  
	private static final String PARAM_MYCARD = "mycard";

	HttpConnect m_httpConnect;
	protected String m_url;
	protected HashMap<?, ?> m_data;
	protected String m_method;
//	private String m_format;
	protected Handler m_handler;
	private JSONObject m_jsonObject;
	protected Object pThis;

	private int m_requestTag;// 区别两个相同的请求，例如请求验证�?
	private JSONObject mMycardJSObj = null;
	boolean mDismissLoading;// 是否在请求结束时关闭loading。默认关�?

	public NBRequest() {
		mDismissLoading = true;
	}

	public NBRequest(int requestTag) {
		this();
		this.m_requestTag = requestTag;
	}

	/**
	 * @param auto dismiss the dialog when we get a successful response(not a net error). true关闭 false不关�?
	 */
	public void setDismissLoading(boolean diss) {
		mDismissLoading = diss;
	}

	public String getMessage() {
		return getJSONObject() == null ? "" : getJSONObject().optString(MESSAGE);
	}

	public String getCode() {
		return getJSONObject() == null ? "" : getJSONObject().optString(CODE);
	}
	
	public String getUrl() {
		return m_url;
	}

	public void setUrl(String m_url) {
		this.m_url = m_url;
	}

	public int getRequestTag() {
		return m_requestTag;
	}

	public void setRequestTag(int m_requestTag) {
		this.m_requestTag = m_requestTag;
	}

	public String getError() {
		return m_httpConnect.getError();
	}
    public JSONObject getJSONObject() {
        if (m_jsonObject == null) {
            String body = getResponseBody();
            try {
                m_jsonObject = new JSONObject(body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return m_jsonObject;
    }
	public JSONObject getBodyJSONObject() {
		JSONObject jsonBody = new JSONObject();
		if (m_jsonObject != null) {
			String body = getResponseBody();
			Log.i("NBRequest response: ", "m_url: " + m_url + " body: " + body);

			try {
				jsonBody = new JSONObject(body).optJSONObject("body");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonBody;
	}

	public int getResponseCode() {
		return m_httpConnect.getResponseCode();
	}

	public void sendRequest(Handler handler, String url, HashMap<?, ?> data, String method, String format) {
	    sendRequest(handler, url, data, method, format, true);
	}
	
	public void sendRequest(Handler handler, String url, HashMap<?, ?> data, String method, String format,final boolean add) {
		this.m_handler = handler;
		this.m_url = url;
		this.m_data = data;
		this.m_method = method;
//		this.m_format = format;

		pThis = this;
		Thread thread = new Thread() {
			public void run() {
				try {
					m_httpConnect = new HttpConnect();
					m_httpConnect.openHttp(m_url, m_data, m_method,add);
					m_handler.sendMessage(m_handler.obtainMessage(SysConstants.RESPONSE_SUCCESS, pThis));
					if (mDismissLoading) {
						m_handler.sendMessage(m_handler.obtainMessage(SysConstants.DISSMISS, pThis));
					}
				} catch (Exception e) {
					m_handler.sendMessage(m_handler.obtainMessage(SysConstants.NET_ERROR, pThis));
					e.printStackTrace();
				} finally {
				}
			}
		};

		thread.start();
//		JSONObject jsonObject = new JSONObject(m_data);
		if(m_data!=null){
			DebugLog.logi("NBRequest request:", "url: " + url + " ,data "
					+ (new JSONObject(m_data)).toString());
		}
	}

	public String getResponseBody() {
		return m_httpConnect.getResponseBody() == null ? "" : m_httpConnect.getResponseBody();
	}

	public HashMap<?, ?> getM_data() {
		return m_data;
	}

	public void setM_data(HashMap<?, ?> m_data) {
		this.m_data = m_data;
	}
    
	/**
	 * 去掉返回数据中的 NULL
	 * 
	 * @param jsonObject
	 * @return
	 */
	public JSONObject convertJson(JSONObject jsonObject) {

		if (jsonObject != null) {
			Iterator keys = jsonObject.keys();
			while (keys.hasNext()) {
				Object next = keys.next();

				if (next != null) {
					String key = next.toString();
					Object optObject = jsonObject.opt(key);

					if (optObject != null && "null".equals(optObject.toString())) {
						try {
							jsonObject.put(key, "");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					if (optObject instanceof JSONObject) {
						convertJson((JSONObject) optObject);
					} else if (optObject instanceof JSONArray) {
						JSONArray optJSONArray = (JSONArray) optObject;
						int length = optJSONArray.length();
						for (int i = 0; i < length; i++) {
							JSONObject object = (JSONObject) optJSONArray.opt(i);
							convertJson(object);

						}

					}

				}

			}
		}

		return jsonObject;

	}
}
