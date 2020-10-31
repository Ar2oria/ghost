package cc.w0rm.ghost.common.http;

import java.util.Map;

public class MyRequestProperties implements RequestProperties {
   private   String url;
   private Map<String, Object> args;
   private String appSecret;

    public MyRequestProperties() {
    }

    /**
     * 获取请求url
     * @return 请求url
     */
    @Override
    public String getUrl() {
        return url;
    }
    /**
     * 设置url
     * @param url url
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * 获取请求参数
     * @return 请求参数
     */
    @Override
    public Map<String, Object> getArgs() {
        return args;
    }
    /**
     * 设置请求参数
     * @param args 请求参数
     */
    @Override
    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }
    /**
     * 获取请求密钥
     * @return 密钥
     */
    @Override
    public String getAppSecret() {
        return appSecret;
    }
    /**
     * 设置请求密钥
     * @param appSecret 密钥
     */
    @Override
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
