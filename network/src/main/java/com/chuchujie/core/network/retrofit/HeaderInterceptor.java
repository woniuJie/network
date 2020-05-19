package com.chuchujie.core.network.retrofit;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Version;

/**
 * 统一为所有请求添加公共Header
 * Created by wangjing on 2017/6/28.
 */
public class HeaderInterceptor implements Interceptor {

    /**
     * 在所有请求中增加app_session_id，由静态变量的方式生成，保证一次app启动只会生成一个id，透传到服务器各个系统
     * 中，形成一个全链路跟踪的标识符，服务器可跟踪到此用户的所有行为
     */
    public static final String SESSION_ID = UUID.randomUUID().toString().replace("-", "");

    private static final String APP_SESSION_ID = "asi";

    private boolean insertUserAgent = true;

    private boolean insertSessionId = true;

    private static final String HEADER_USER_AGENT = "User-Agent";

    public HeaderInterceptor() {
    }

    public HeaderInterceptor(boolean insertUserAgent, boolean insertSessionId) {
        this.insertUserAgent = insertUserAgent;
        this.insertSessionId = insertSessionId;
    }

    public boolean isInsertUserAgent() {
        return insertUserAgent;
    }

    public void setInsertUserAgent(boolean insertUserAgent) {
        this.insertUserAgent = insertUserAgent;
    }

    public boolean isInsertSessionId() {
        return insertSessionId;
    }

    public void setInsertSessionId(boolean insertSessionId) {
        this.insertSessionId = insertSessionId;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder newBuilder = request.newBuilder();

        if (insertUserAgent) {
            newBuilder.addHeader(HEADER_USER_AGENT, getUserAgentInfo());
        }

        if (insertSessionId) {
            newBuilder.addHeader(APP_SESSION_ID, SESSION_ID);
        }

        return chain.proceed(newBuilder.build());
    }

    /**
     * 构建默认的User-Agent信息
     * 获取UserAgent的信息, 处理okhttp的请求头的value不能传非法字符的问题, 特别是中文字符
     *
     * @return
     */
    public static String getUserAgentInfo() {
        String deviceInfo = System.getProperty("http.agent") + " " + Version.userAgent();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0, length = deviceInfo.length(); i < length; i++) {
            char c = deviceInfo.charAt(i);
            if ((c <= '\u001f' && c != '\u0009' /* htab */) || c >= '\u007f') {
                // 转为unicode码
                stringBuffer.append(String.format("\\u%04x", (int) c));
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }

}
