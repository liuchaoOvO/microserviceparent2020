package lc.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName HttpClientUtil
 * @Description httpclient get/post方式调用接口
 * @Author liuchaoOvO
 * @Date 2019/1/7
 * Version  1.0
 */
public class HttpClientUtil {
    private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String ENCODING = "UTF-8";

    private static final int CONNECTION_TIME_OUT = 3000;

    private static final int SO_TIME_OUT = 5000;

    private static final boolean STALE_CHECK_ENABLED = true;

    private static final boolean TCP_NO_DELAY = true;

    private static final int DEFAULT_MAX_CONNECTIONS_PER_HOST = 100;

    private static final int MAX_TOTAL_CONNECTIONS = 1000;

    private static final HttpConnectionManager connectionManager;

    public static final HttpClient client;

    static {
        HttpConnectionManagerParams params = loadHttpConfFromFile();

        connectionManager = new MultiThreadedHttpConnectionManager();

        connectionManager.setParams(params);

        client = new HttpClient(connectionManager);
    }

    private static HttpConnectionManagerParams loadHttpConfFromFile() {
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setConnectionTimeout(CONNECTION_TIME_OUT);
        params.setStaleCheckingEnabled(STALE_CHECK_ENABLED);
        params.setTcpNoDelay(TCP_NO_DELAY);
        params.setSoTimeout(SO_TIME_OUT);
        params.setDefaultMaxConnectionsPerHost(DEFAULT_MAX_CONNECTIONS_PER_HOST);
        params.setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);
        params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        return params;
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        String result = null;
        try {
            GetMethod getMethod = new GetMethod(url);
            client.executeMethod(getMethod);
            result = getMethod.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("httpclient get request url=" + url + ",exception=" + e);
        }
        return result;
    }

    public static String doPost(String url, String contentType, String content) throws Exception {
        PostMethod method = new PostMethod(url);
        method.addRequestHeader("Connection", "Keep-Alive");
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        try {
            //1、使用JSONObject
            System.out.println(content);
            //   String s="{'id':'100','username':'qw','password':'er'}";
            if (content.equalsIgnoreCase("\"\"")) {
                content = null;
            }
            JSONObject jsonObject = JSONObject.parseObject(content);
            if (jsonObject == null) jsonObject = new JSONObject();
            method.setRequestEntity(new StringRequestEntity(jsonObject.toString(), contentType, "utf-8"));
            method.addRequestHeader("Content-Type", contentType);

            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            byte[] ret = method.getResponseBody();
            if (ret == null)
                return null;
            return new String(ret, ENCODING);
        } finally {
            method.releaseConnection();
        }
    }

}

