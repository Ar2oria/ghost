package cc.w0rm.ghost.common.http;

import java.util.Map;

public interface RequestProperties {
    /**
     * 获取请求url
     * @return 请求url
     */
    String getUrl();

    /**
     * 设置url
     * @param url url
     */
    void setUrl(String url);

    /**
     * 获取请求参数
     * @return 请求参数
     */
    Map<String, Object> getArgs();

    /**
     * 设置请求参数
     * @param args 请求参数
     */
    void setArgs(Map<String, Object> args);

    /**
     * 获取请求密钥
     * @return 密钥
     */
    String getAppSecret();

    /**
     * 设置请求密钥
     * @param appSecret 密钥
     */
    void setAppSecret(String appSecret);

}
