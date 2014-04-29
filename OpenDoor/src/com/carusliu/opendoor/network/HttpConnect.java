
package com.carusliu.opendoor.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParamBean;

import com.carusliu.opendoor.application.AppApplication;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.DebugLog;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;

public class HttpConnect {
    private final static int DEFAULT_TIMEOUT = SysConstants.CONNECT_TIMEOUT;

    private static final int RETRY_NUM = 2;

    private static final String QUESTION_MARK = "?";

    private static final String EMPTY = "";

    private static final String EQUAL_SIGN = "=";

    private static final String AND_SIGN = "&";

    private static final String NET_REEOR = "message is content fail";

    public static List<HttpConnect> s_httpConnect = new ArrayList<HttpConnect>();

    private String m_format;

    private int m_getStatusCode;

    private String m_Body;

    private String m_error = null;

    // always verify the host - do not check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            DebugLog.logd("Approving certificate for " + hostname);
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws java.security.cert.CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }

            }
        };

        // Install the all-trusting trust manager
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        DebugLog.logd("trustAllHosts");
        trustAllHosts();
    }

    public HttpConnect() {
    }

    /**
     * 发生网络断开等错误返�?
     * 
     * @return
     */
    public String getError() {
        return m_error;
    }

    /**
     * 得到返回的Body
     * 
     * @return
     */
    public String getResponseBody() {
        return m_Body;
    }

    /**
     * 得到返回的状态码
     * 
     * @return
     */
    public int getResponseCode() {
        return m_getStatusCode;
    }

    public String getFormat() {
        return m_format;
    }

    public void setFormat(String format) {
        this.m_format = format;
    }

    public void openHttp(String url, HashMap<?, ?> content, String method, int timeOut,boolean add) throws UnsupportedEncodingException {
        String requestUrl = url;
        // boolean isNeedTimeOut = false;

        if (SysConstants.CONNECT_METHOD_GET.equals(method)) {
            requestUrl = requestUrl + QUESTION_MARK + encodeUrl(content);
            // need to implement get method!!!
            sendGetRequestByUC(requestUrl, content, method, timeOut,add);
        } else {
            // m_httpProxy = new HttpProxy(requestUrl, content, isNeedTimeOut);
            sendPostRequestByUC(requestUrl, content, method, timeOut,add);
        }

    }
    
    private void sendGetRequestByUC(String requestUrl, HashMap<?, ?> content, String method,
            int timeOut){
        sendGetRequestByUC(requestUrl, content, method, timeOut, true);
    }

    private void sendGetRequestByUC(String requestUrl, HashMap<?, ?> content, String method,
            int timeOut,boolean add) {

        DebugLog.logi("------------- sendGetRequestByUC ------------");
        DebugLog.logi(SysConstants.SERVER + requestUrl);
        System.out.println(SysConstants.SERVER + requestUrl);
        for (int i = 0; i < RETRY_NUM; i++) {
            try {
                URL url;
                if(add){
                    url = new URL(SysConstants.SERVER + requestUrl);
                }else{
                    url = new URL(requestUrl);
                }

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setRequestMethod(method);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                //connection.setDoOutput(true);  4.0以上系统会导致使用POST方法，报�?
                connection.connect();// 连接

                InputStream inputStream = (InputStream)connection.getInputStream();

                // String encoding =
                // connection.getRequestProperty(SysConstants.CONTENT_ENCODING);
                // if (encoding != null
                // && encoding.toLowerCase()
                // .indexOf(SysConstants.GZIP) > -1) {
                // inputStream = new GZIPInputStream(inputStream);
                // }
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();

                m_getStatusCode = connection.getResponseCode();
                m_Body = sb.toString();
                connection.disconnect();// 断开连接
                DebugLog.logd("+++++++++++++ Request:" + SysConstants.SERVER + requestUrl
                        + "+++++++++++++");
                DebugLog.logd(m_Body);
                DebugLog.logd("+++++++++++++ Request Receive End +++++++++++++");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                m_error = NET_REEOR;
                DebugLog.loge("Request err:" + e.getMessage());
                e.printStackTrace();
                // Log2File.write2LogFile(fLogos, e.getMessage());
            } finally {
            }

            if (m_error == null) {
                // Log2File.closeLogFile(fLogos);
                return;
            }
            DebugLog.logi("HttpConnect times: ", i + "");
            // Log2File.closeLogFile(fLogos);
        }

    }
    
    public void openHttp(String url, HashMap<?, ?> content, String method) throws UnsupportedEncodingException {
        openHttp(url, content, method, true);
    }

    public void openHttp(String url, HashMap<?, ?> content, String method,boolean add) throws UnsupportedEncodingException {
        openHttp(url, content, method, DEFAULT_TIMEOUT,add);
    }

    private HttpClient getHttpClient() {
        HttpClient client = CustomSSLSocketFactory.getHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), SysConstants.CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(client.getParams(), SysConstants.READ_TIMEOUT);
        HttpProtocolParamBean protocol = new HttpProtocolParamBean(client.getParams());
        protocol.setUseExpectContinue(false);
        return client;
    }

    private StringBuffer getFormString(HashMap<?, ?> params) {
        StringBuffer stringBuffer = new StringBuffer("");
        Iterator<?> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (null != value) {
                stringBuffer = stringBuffer.append(key.toString() + "=").append(
                        value.toString() + "&");
                DebugLog.logi(key.toString() + "=\"" + value.toString() + "\"");
            } else {
                stringBuffer = stringBuffer.append(key.toString() + "=").append("&");
                DebugLog.logi(key.toString() + "= null");
            }
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);

        return stringBuffer;
    }

    private List<NameValuePair> getForm(HashMap<?, ?> params) {

        List<NameValuePair> form = new ArrayList<NameValuePair>();

        // form.add(new BasicNameValuePair(SysConstants.TOKEN, mToken));
        Iterator<?> iter = params.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (val == null) {
                form.add(new BasicNameValuePair(key.toString(), null));

                DebugLog.logi(key.toString() + "=null");
            } else {
                form.add(new BasicNameValuePair(key.toString(), val.toString()));

                DebugLog.logi(key.toString() + "=\"" + val.toString() + "\"");
            }

        }

        return form;
    }
    
    private void sendPostRequestByUC(String requestUrl, HashMap<?, ?> content, String method,
            int timeOut){
        sendPostRequestByUC(requestUrl, content, method, timeOut, true);
    }

    private void sendPostRequestByUC(String requestUrl, HashMap<?, ?> content, String method,
            int timeOut,boolean add) {

        DebugLog.logi("------------- sendPostRequestByUC ------------");
        DebugLog.logi(SysConstants.SERVER + requestUrl);
        // FileOutputStream fLogos = Log2File.openLogFile(requestUrl);
        // Log2File.write2LogFile(fLogos, SysConstants.SERVER + requestUrl);
        // Log2File.printLogTime(fLogos);
        // Log2File.write2LogFile(fLogos, content);

        // server address need read from xml files;
        for (int i = 0; i < RETRY_NUM; i++) {
            try {
                URL url;
                if(add){
                    url = new URL(SysConstants.SERVER + requestUrl);
                }else{
                    url = new URL(requestUrl);
                }
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod(method);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty(SysConstants.USER_AGENT_KEY, SysConstants.USER_AGENT);
                connection.setRequestProperty(SysConstants.ACCEPT_ENCODING,
                        SysConstants.GZIP_DEFLATE);
                connection.addRequestProperty(SysConstants.COOKIE, SysConstants.APP_KEY + "="
                        + AppApplication.getKey());
                connection.addRequestProperty(
                        SysConstants.COOKIE,
                        SysConstants.TICKET_KEY
                                + "="
                                + SharedPreferencesHelper.getSharedPreferences().getString(
                                        SysConstants.TICKET_KEY, ""));
                connection.addRequestProperty(
                        SysConstants.COOKIE,
                        SysConstants.CARCUBE_USERID
                                + "="
                                + SharedPreferencesHelper.getSharedPreferences().getString(
                                        SysConstants.USER_ID, ""));
                connection.addRequestProperty(
                        SysConstants.COOKIE,
                        SysConstants.VIRTUAL_TOKEN
                                + "="
                                + SharedPreferencesHelper.getSharedPreferences().getString(
                                        SysConstants.VIRTUAL_TOKEN, ""));
                connection.addRequestProperty(SysConstants.ACCEPT, SysConstants.ACCEPT_VALUE
                        + SysConstants.API_VERSION);
                connection.setConnectTimeout(timeOut);
                // connection.setReadTimeout(timeoutMillis);
                // Log
                DebugLog.logi(SysConstants.USER_AGENT_KEY + "=" + SysConstants.USER_AGENT);
                DebugLog.logi(SysConstants.ACCEPT_ENCODING + "=" + SysConstants.GZIP_DEFLATE);
                DebugLog.logi(SysConstants.COOKIE + "=" + SysConstants.APP_KEY + "="
                        + AppApplication.getKey());
                DebugLog.logi(SysConstants.COOKIE
                        + "="
                        + SysConstants.TICKET_KEY
                        + "="
                        + SharedPreferencesHelper.getSharedPreferences().getString(
                                SysConstants.TICKET_KEY, ""));
                DebugLog.logi(SysConstants.COOKIE
                        + "="
                        + SysConstants.VIRTUAL_TOKEN
                        + "="
                        + SharedPreferencesHelper.getSharedPreferences().getString(
                                SysConstants.VIRTUAL_TOKEN, ""));
                DebugLog.logi(SysConstants.ACCEPT + "=" + SysConstants.ACCEPT_VALUE
                        + SysConstants.API_VERSION);
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded�?
                // connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                DebugLog.logi("+++++++++++++ sendPostRequest start ++++++++++++++");
                connection.connect();

                // 上传数据
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                        connection.getOutputStream(), SysConstants.UTF_8));
                out.write(getFormString(content).toString());// 要post的数据，多个�?符号分割
                out.flush();
                out.close();

                // 读取post之后的返回�?
                InputStream inputStream = (InputStream)connection.getInputStream();

                String encoding = connection.getRequestProperty(SysConstants.CONTENT_ENCODING);
                if (encoding != null && encoding.toLowerCase().indexOf(SysConstants.GZIP) > -1) {
                    inputStream = new GZIPInputStream(inputStream);
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();

                m_getStatusCode = connection.getResponseCode();
                m_Body = sb.toString();
                connection.disconnect();// 断开连接
                DebugLog.logd("+++++++++++++ Request:" + SysConstants.SERVER + requestUrl
                        + "+++++++++++++");
                DebugLog.logd(m_Body);
                DebugLog.logd("+++++++++++++ Request Receive End +++++++++++++");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                m_error = NET_REEOR;
                DebugLog.loge("Request err:" + e.getMessage());
                e.printStackTrace();
                // Log2File.write2LogFile(fLogos, e.getMessage());
            } finally {
            }

            if (m_error == null) {
                // Log2File.closeLogFile(fLogos);
                return;
            }
            DebugLog.logi("HttpConnect times: ", i + "");
            // Log2File.closeLogFile(fLogos);
        }
    }

    /**
     * 获取文件�?
     * 
     * @param conn
     * @return String
     */
    private String getFileName(HttpURLConnection conn) {
        String filename = null;
        String mine = conn.getHeaderField("Content-Disposition");
        if (mine != null) {
            int pos = mine.indexOf("filename=");
            if (pos >= 0) {
                // 如果文件名带引号
                if (mine.charAt(mine.length() - 1) == '"') {
                    return mine.substring(pos + 10, mine.length() - 1);
                }
                return mine.substring(pos + 9, mine.length());
            }
        }

        filename = UUID.randomUUID() + ".csv";// 默认取一个文件名
        return filename;
    }

    private void writeToFile(InputStream inputStream, String outFilePath) {
        File file = new File(outFilePath);
        byte[] buf = new byte[1024];
        if (!file.exists() || file.length() == 0) {
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            // Read response into a buffered stream
            int readBytes = 0;
            while ((readBytes = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, readBytes);
            }

            inputStream.close();
            fos.close();
            DebugLog.logd("download file save to :" + outFilePath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * return save file full path. *
     */
    public String sendDownLoadRequest(String requestUrl, HashMap<?, ?> content, int timeOut,
            String outFileDir) {

        DebugLog.logi("------------- sendDownLoadRequest ------------");
        DebugLog.logi(SysConstants.SERVER + requestUrl);
        // FileOutputStream fLogos = Log2File.openLogFile(requestUrl);
        // Log2File.write2LogFile(fLogos, SysConstants.SERVER + requestUrl);
        // Log2File.printLogTime(fLogos);
        // Log2File.write2LogFile(fLogos, content);

        String filePath = "";
        // server address need read from xml files;
        for (int i = 0; i < 2; i++) {
            try {
                URL url;
                url = new URL(SysConstants.SERVER + requestUrl);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty(SysConstants.USER_AGENT_KEY, SysConstants.USER_AGENT);
                connection.setRequestProperty(SysConstants.ACCEPT_ENCODING,
                        SysConstants.GZIP_DEFLATE);
                connection.addRequestProperty(SysConstants.COOKIE, SysConstants.APP_KEY + "="
                        + AppApplication.getKey());
                connection.addRequestProperty(
                        SysConstants.COOKIE,
                        SysConstants.TICKET_KEY
                                + "="
                                + SharedPreferencesHelper.getSharedPreferences().getString(
                                        SysConstants.TICKET_KEY, ""));
                connection.addRequestProperty(
                        SysConstants.COOKIE,
                        SysConstants.CARCUBE_USERID
                                + "="
                                + SharedPreferencesHelper.getSharedPreferences().getString(
                                        SysConstants.USER_ID, ""));
                connection.addRequestProperty(
                        SysConstants.COOKIE,
                        SysConstants.VIRTUAL_TOKEN
                                + "="
                                + SharedPreferencesHelper.getSharedPreferences().getString(
                                        SysConstants.VIRTUAL_TOKEN, ""));
                connection.addRequestProperty(SysConstants.ACCEPT, SysConstants.ACCEPT_VALUE
                        + SysConstants.API_VERSION);
                connection.setConnectTimeout(timeOut);
                // connection.setReadTimeout(timeoutMillis);

                // Log
                DebugLog.logi(SysConstants.USER_AGENT_KEY + "=" + SysConstants.USER_AGENT);
                DebugLog.logi(SysConstants.ACCEPT_ENCODING + "=" + SysConstants.GZIP_DEFLATE);
                DebugLog.logi(SysConstants.COOKIE + "=" + SysConstants.APP_KEY + "="
                        + AppApplication.getKey());
                DebugLog.logi(SysConstants.COOKIE
                        + "="
                        + SysConstants.TICKET_KEY
                        + "="
                        + SharedPreferencesHelper.getSharedPreferences().getString(
                                SysConstants.TICKET_KEY, ""));
                DebugLog.logi(SysConstants.COOKIE
                        + "="
                        + SysConstants.VIRTUAL_TOKEN
                        + "="
                        + SharedPreferencesHelper.getSharedPreferences().getString(
                                SysConstants.VIRTUAL_TOKEN, ""));
                DebugLog.logi(SysConstants.ACCEPT + "=" + SysConstants.ACCEPT_VALUE
                        + SysConstants.API_VERSION);
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded�?
                // connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                DebugLog.logi("+++++++++++++ sendPostRequest start ++++++++++++++");
                connection.connect();

                // 上传数据
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                        connection.getOutputStream(), SysConstants.UTF_8));
                out.write(getFormString(content).toString());// 要post的数据，多个�?符号分割
                out.flush();
                out.close();

                // 读取post之后的返回�?
                InputStream inputStream = (InputStream)connection.getInputStream();
                String fileName = getFileName(connection);
                filePath = outFileDir + fileName;
                writeToFile(inputStream, filePath);

                connection.disconnect();// 断开连接
                DebugLog.logd("+++++++++++++ Request:" + SysConstants.SERVER + requestUrl
                        + "+++++++++++++" + connection.getResponseCode());

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                DebugLog.loge("Request err:" + e.getMessage());
                // Log2File.write2LogFile(fLogos, e.getMessage());
                e.printStackTrace();
            } finally {
            }

            DebugLog.logi("HttpConnect times: ", i + "");
            // Log2File.closeLogFile(fLogos);
            return filePath;
        }
        return filePath;
    }

    private static String encodeUrl(HashMap<?, ?> parameters) throws UnsupportedEncodingException {
        if (parameters == null) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        Iterator<?> iter = parameters.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();

            // sb.append(AND_SIGN);
            // sb.append(URLEncoder.encode(key.toString() + EQUAL_SIGN +
            // val.toString()));
            //if(!(key.toString().equals(SysConstants.ERRORCODES))){
            if(key!=null&&val!=null){
	        sb.append(URLEncoder.encode(key.toString(), SysConstants.UTF_8) + EQUAL_SIGN
	                    + URLEncoder.encode(val.toString(), SysConstants.UTF_8) + AND_SIGN);
	        }
	            
            //}
        }
        return sb.toString();//.replace("%2C", ",");
    }
}
