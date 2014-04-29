package com.carusliu.opendoor.network;

import org.json.JSONException;
import org.json.JSONObject;

import com.carusliu.opendoor.tool.DebugLog;


public class HWHttpConnect extends HttpConnect
{
	private static final String MESSAGE = "message";
	private static final String CODE = "code";
	
	public String getMessage() {
		JSONObject obj = getJSONObject();
		return (obj == null)  ? "" : obj.optString(MESSAGE);
	}

	public String getCode() {
		JSONObject obj = getJSONObject();
		return (obj == null)  ? "" : obj.optString(CODE);
	}
	
	public JSONObject getJSONObject()
	{
		String m_jsonStr = getResponseBody();
		JSONObject jsonData = null;
		try {
			DebugLog.logd(m_jsonStr);
			
			if(m_jsonStr != null) {
				jsonData = new JSONObject(m_jsonStr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonData;
	}
}
