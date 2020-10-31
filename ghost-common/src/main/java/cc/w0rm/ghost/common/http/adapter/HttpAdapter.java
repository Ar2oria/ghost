package cc.w0rm.ghost.common.http.adapter;

import java.util.Map;

/**
 * @author : xuyang
 * @date : 2019-11-25 16:19
 */

public interface HttpAdapter {

    /**
     * get方式请求
     * @param url 请求链接
     * @param header 请求头
     * @return http响应内容
     */
    String doGet(String url, Map<String, String> header);

    /**
     * form方式的post请求
     * @param url 请求链接
     * @param header 请求头
     * @param body form请求内容，使用键值对方式存放
     * @return http响应内容
     */
    String doPostByForm(String url, Map<String, String> header, Map<String, Object> body);

    /**
     * json方式的post请求
     * @param url 请求链接
     * @param header 请求头
     * @param body json请求内容
     * @return http响应内容
     */
    String doPostByJson(String url, Map<String, String> header, Object body);

}
