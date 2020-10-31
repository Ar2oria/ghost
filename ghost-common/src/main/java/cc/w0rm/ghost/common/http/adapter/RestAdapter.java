package cc.w0rm.ghost.common.http.adapter;

import cc.w0rm.ghost.common.json.JsonUtil;
import cc.w0rm.ghost.common.util.Strings;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author : xuyang
 * @date : 2019-11-25 16:51
 */

public interface RestAdapter extends HttpAdapter {

    /**
     * get请求并自动反序列化成指定的对象
     *
     * @param url    请求url
     * @param header 请求头
     * @param type   待反序列化类型
     * @return http响应结果
     */
    default <T> T deserializeFromGet(String url, Map<String, String> header, Type type) {
        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("url is null");
        }
        String response = doGet(url, header);
        return JsonUtil.readValue(response, type);
    }


    /**
     * form形式的post请求并自动反序列化成指定的对象
     *
     * @param url    请求url
     * @param header 请求头
     * @param body   form请求内容，使用键值对方式存储
     * @param type   待反序列化类型
     * @return http响应结果
     */
    default <T> T deserializeFromForm(String url, Map<String, String> header, Map<String, Object> body, Type type) {
        String response = doPostByForm(url, header, body);
        return JsonUtil.readValue(response, type);
    }


    /**
     * json形式的post请求并自动反序列化成指定的对象
     *
     * @param url    请求url
     * @param header 请求头
     * @param body   json请求内容
     * @param type   待反序列化类型
     * @return http响应结果
     */
    default <T> T deserializeFromJson(String url, Map<String, String> header, Object body, Type type) {
        String response = doPostByJson(url, header, body);
        return JsonUtil.readValue(response, type);
    }

}
