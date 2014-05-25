package com.carusliu.opendoor.sysconstants;

import com.carusliu.opendoor.tool.Tool;

public class SysConstants {

	//HTTP
	public static final String SERVER_NAME = "bswlkj.gotoip55.com"; // test
	public static final String SERVER = "http://" + SERVER_NAME + "/";
	public static final String UTF_8 = "UTF-8";
	public static final int CONNECT_TIMEOUT = 60 * 1000;
	public static final int READ_TIMEOUT = 60 * 1000;
	public static final String USER_AGENT_KEY = "User-Agent";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String COOKIE = "Cookie";
	public static final String ACCEPT = "Accept";
	public static final String ACCEPT_VALUE = "*/*;version=";
	public static final String GZIP_DEFLATE = "gzip,deflate";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final String GZIP = "gzip";
	public static final String CONNECT_METHOD_GET = "GET";
	public static final String CONNECT_METHOD_POST = "POST";
	public static final String FORMAT_JSON = "JSON";
	public static int ZIP_BUFFER_SIZE = 1024;
	public static final String ZERO = "0";
	public static final String API_VERSION = "1.0.0";
	public static final String USER_AGENT = Tool.getUserAgent();
	public static final int SUCCESS_CODE = 200;
	public static final String APP_KEY = "secret_appkey";
	public static final String TICKET_KEY = "secret_ticket";
	public static final String CARCUBE_USERID = "secret_userid";
	public static final String VIRTUAL_TOKEN = "secret_virtoken";
	public static final String RESP_SUCCESS = "0";
	public static final String RESP_SYSTEM_ERROR = "-1";
	// handlerMessage
	// public static final int DIALOG_DISMISS = 0;
	public static final int TOAST = 1;
	public static final int NET_ERROR = TOAST + 1;
	public static final int RESPONSE_SUCCESS = NET_ERROR + 1;
	public static final int LOAD = RESPONSE_SUCCESS + 1;
	public static final int SYNCLOAD = LOAD + 1;
	public static final int DISSMISS = SYNCLOAD + 1;
	
	//接口
	/**获取奖品URL*/
	public static final String TODAY_AWARDS_URL = "award/getNearAward";
	
	public static final String SHAKE_AWARD_URL = "award/getMotionAward";
	public static final String SHAKE_NOMAL_AWARD_URL = "award/getNomalMotionAward";
	
	public static final String DELETE_AWARD_URL = "user/deleteAward";
	public static final String MODIFY_PWD_URL = "user/userModifyPassword";
	public static final String MODIFY_INFO_URL = "user/userModify";
	/**用户登录URL*/
	public static final String LOGIN_URL = "user/login";
	/**用户注册URL*/
	public static final String REGISTER_URL = "user/userAdd";
	public static final String ORDER_INFO_URL = "order/getAndroidOrderInfo";
	public static final String UPDATE_ORDER_URL = "order/updateOrderStatus";
	public static final String GET_USER_AWARD_URL = "award/getUserAward";
	public static final String GET_USER_AMOUNT_URL = "user/getUserAmount";

	//请求参数
	
	public static final String USER_ID = "userId";
	public static final String USER_ACCOUNT = "userAccount";
	public static final String USER_PASSWORD = "userPassword";//0代表车保镖，1代表车立方
	public static final String NEW_PASSWORD = "newUserPassword";//0代表车保镖，1代表车立方
	public static final String USER_NAME = "userName";
	public static final String USER_GENDER = "userGender";
	public static final String USER_PHONE = "userPhone";
	public static final String USER_EMAIL = "userEmail";
	public static final String LANTITUDE = "lat1";
	public static final String LONGITUDE = "lng1";
	public static final String AWARD_ID = "awardId";
	public static final String ORDER_AMOUNT = "orderAmount";
	public static final String TN = "tn";
	

}
