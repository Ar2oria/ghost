package cc.w0rm.ghost.common.http;

import cc.w0rm.ghost.common.http.adapter.OKHttpAdapter;
import cc.w0rm.ghost.common.http.adapter.RestAdapter;
import cc.w0rm.ghost.common.http.sign.RestSign;
import cc.w0rm.ghost.common.http.sign.Signature;
import cc.w0rm.ghost.common.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : xuyang
 * @date : 2019-11-25 17:12
 */

public class RestHelper {
    private static final Logger log = LoggerFactory.getLogger(RestHelper.class);
    private static final RestAdapter DEFAULT_ADAPTER = new OKHttpAdapter();
    private static final Signature DEFAULT_SIGNATURE = new RestSign();
    private Map<String, String> header = new HashMap<>();
    private RestAdapter adapter;
    private Signature signature;

    private RestHelper() {
    }

    private RestHelper(RestAdapter adapter, Signature signature) {
        if (adapter == null) {
            throw new IllegalArgumentException("适配器不能为空！");
        } else {
            this.adapter = adapter;
        }

        if (signature == null) {
            throw new IllegalArgumentException("签名方式不能为空！");
        } else {
            this.signature = signature;
        }
    }

    /**
     * 获得rest适配器
     * @return 适配器
     */
    public RestAdapter getAdapter() {
        return adapter;
    }

    /**
     * 设置rest适配器
     * @param adapter 适配器
     */
    public void setAdapter(RestAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 获取请求签名
     * @return 签名方法类
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * 设置请求方法
     * @param signature 签名方法
     */
    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    /**
     * 使用默认的属性生成一个RestHelper
     * @return RestHelper
     */
    public static RestHelper create() {
        return new RestHelper(DEFAULT_ADAPTER, DEFAULT_SIGNATURE);
    }

    /**
     * 指定rest适配器和签名方法去创建RestHelper
     * @param restAdapter rest适配器
     * @param signature 签名方法类
     * @return RestHelper
     */
    public static RestHelper create(RestAdapter restAdapter, Signature signature) {
        return new RestHelper(restAdapter, signature);
    }

    /**
     * get请求，无签名，返回http请求内容
     * @param url url链接
     * @return http请求内容
     */
    public String doGet(String url) {
        return adapter.doGet(url, header);
    }

    /**
     * post请求，无签名，返回http请求内容
     * @param url url链接
     * @param body form请求体
     * @return http请求内容
     */
    public String doPostByForm(String url, Map<String, Object> body) {
        return adapter.doPostByForm(url, header, body);
    }

    /**
     * json请求，无签名，返回http请求内容
     * @param url url链接
     * @param body json请求体
     * @return http请求内容
     */
    public String doPostByJson(String url, Object body) {
        return adapter.doPostByJson(url, header, body);
    }

    /**
     * get请求，无签名，根据类型自动反序列化成对象
     * @param url url链接
     * @param type 反序列化类型
     * @return http响应对象
     */
    public <T> T deserializeFromGet(String url, Type type) {
        return adapter.deserializeFromGet(url, header, type);
    }

    /**
     * form请求，无签名，根据类型自动反序列化成对象
     * @param url url链接
     * @param body form内容，使用键值对存储
     * @param type 反序列化类型
     * @return http响应对象
     */
    public <T> T deserializeFromForm(String url, Map<String, Object> body, Type type) {
        if (Objects.isNull(body)) {
            body = Collections.emptyMap();
        }
        return adapter.deserializeFromForm(url, header, body, type);
    }

    /**
     * json请求，无签名，根据类型自动反序列化成对象
     * @param url url链接
     * @param body json内容，使用键值对存储
     * @param type 反序列化类型
     * @return http响应对象
     */
    public <T> T deserializeFromJson(String url, Object body, Type type) {
        if (Objects.isNull(body)) {
            body = "{}";
        }
        return adapter.deserializeFromJson(url, header, body, type);
    }

    /**
     *  get请求，有签名
     * @param requestProperties 请求参数
     * @return http响应字符串
     */
    public String doGet(RequestProperties requestProperties) {
        String url = signature.signatureGet(requestProperties);
        return adapter.doGet(url, header);
    }

    /**
     *  json形式的post请求，有签名
     * @param requestProperties 请求参数
     * @param body json内容
     * @return http响应字符串
     */
    public String doPostByJson(RequestProperties requestProperties, Object body) {
        Object signedBody = signature.signatureJson(requestProperties, body);
        return doPostByJson(requestProperties.getUrl(), signedBody);
    }

    /**
     *  form形式的post请求，有签名
     * @param requestProperties 请求参数
     * @param body form表单内容
     * @return http响应字符串
     */
    public String doPostByForm(RequestProperties requestProperties, Map<String, Object> body) {
        Map<String, Object> signedBody = signature.signatureForm(requestProperties, body);
        return doPostByForm(requestProperties.getUrl(), signedBody);
    }

    /**
     * get请求，有签名，根据类型自动反序列化成对象
     * @param requestProperties 请求参数
     * @param type 反序列化类型
     * @return http响应对象
     */
    public <T> T deserializeFromGet(RequestProperties requestProperties, Type type) {
        String url = signature.signatureGet(requestProperties);
        return deserializeFromGet(url, type);
    }

    /**
     * json形式的post请求，有签名，根据类型自动反序列化成对象
     * @param requestProperties 请求参数
     * @param body json内容
     * @param type 反序列化类型
     * @return http响应对象
     */
    public <T> T deserializeFromJson(RequestProperties requestProperties, Object body, Type type) {
        Object signedBody = signature.signatureJson(requestProperties, body);
        return deserializeFromJson(requestProperties.getUrl(), signedBody, type);
    }

    /**
     * form形式的post请求，有签名，根据类型自动反序列化成对象
     * @param requestProperties 请求参数
     * @param body form表单内容
     * @param type 反序列化类型
     * @return http响应对象
     */
    public <T> T deserializeFromForm(RequestProperties requestProperties, Map<String, Object> body, Type type) {
        Map<String, Object> signedBody = signature.signatureForm(requestProperties, body);
        return deserializeFromForm(requestProperties.getUrl(), signedBody, type);
    }

    /**
     * 设置请求头
     * @param header 请求头
     * @return 链式调用
     */
    public RestHelper setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    /**
     * 添加请求头，如果key值相同，保留原有val，添加新val
     * @param key key
     * @param val val
     * @return 链式调用
     */
    public RestHelper addHeader(String key, String val) {
        String v1 = header.get(key);
        if (Strings.isBlank(v1)) {
            header.put(key, val);
        } else {
            header.put(key, v1 + ";" + val);
        }
        return this;
    }

    /**
     * 获取指定key值的val，如果没有key值则返回null
     * @param key header的key
     * @return 链式调用
     */
    public String getHeader(String key) {
        return header.get(key);
    }

    /**
     * 设置cookie，已存在的cookie会被覆盖
     * @param val cookie的值
     * @return 链式调用
     */
    public RestHelper setCookie(String val) {
        this.header.put("Cookie", val);
        return this;
    }

    /**
     * 添加cookie，保留原有val，添加新val
     * @param val cookie值
     * @return 链式调用
     */
    public RestHelper addCookie(String val) {
        return addHeader("Cookie", val);
    }

    /**
     * 返回cookie内容
     * @return 链式调用
     */
    public String getCookie() {
        return getHeader("Cookie");
    }

    /**
     * 清空请求头
     * @return 链式调用
     */
    public RestHelper clearHeader() {
        header.clear();
        return this;
    }

    /**
     * 清空cookie
     * @return 链式调用
     */
    public RestHelper clearCookie() {
        header.put("Cookie", "");
        return this;
    }

    /**
     * http utf8 encode
     * @param url 待转码的字符
     * @return 转码后的字符
     */
    public static String encodeUrl(String url) {
        String encode;
        try {
            encode = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("object:{}使用url编码失败，e:{}", url, e);
            throw new IllegalArgumentException("使用url编码失败！");
        }
        return encode;
    }

}
