package com.carusliu.opendoor.sysconstants;

import com.carusliu.opendoor.tool.Tool;

public class SysConstants {

	//HTTP
	public static final String SERVER_NAME = "api.obd.camp4app.com"; // test
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
	public static final String REQUEST_LOGIN = "user/login";
	
	public static final String DOMAIN = "http://121.199.56.169:8080/zhima";
	
	public static final String IMAGE_CACHE = DOMAIN + "/images/";
	/**关于URL*/
	public static final String ABOUT_URL = DOMAIN + "/interface/about";
	/**奖品兑换URL*/
	public static final String EXCHANGE_URL = DOMAIN + "/interface/award_exchange";
	/**奖品详情URL*/
	public static final String AWARD_INFO_URL = DOMAIN + "/interface/award_info";
	/**用户获奖列表URL*/
	public static final String USER_AWARDS_URL = DOMAIN + "/interface/user_awards";
	/**更多奖品URL*/
	public static final String AWARD_LIST_URL = DOMAIN + "/interface/award_list";
	/**今日大奖URL*/
	public static final String TODAY_AWARDS_URL = DOMAIN + "/interface/today_awards";
	/**用户登录URL*/
	public static final String LOGIN_URL = DOMAIN + "/interface/login";
	/**用户注册URL*/
	public static final String REGISTER_URL = DOMAIN + "/interface/register";
	/**用户信息URL*/
	public static final String USER_INFO_URL = DOMAIN + "/interface/user_info";
	/**立即充值URL*/
	public static final String RECHARGE_URL = DOMAIN + "/interface/recharge";
	/**花几毛钱提升中奖率URL*/
	public static final String PROMOTE_RATE_URL = DOMAIN + "/interface/promte_rate";


	//请求参数
	public static final String USER_ID = "userid";
	public static final String APP_ID = "1";//0代表车保镖，1代表车立方
	public static final String PASSWORD = "password";
	public static final String PARAM_APP_ID = "appID";

}
